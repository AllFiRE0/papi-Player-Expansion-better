package com.extendedclip.papi.expansion.player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class PlayerTracker implements Listener {

    private static final PlayerTracker INSTANCE = new PlayerTracker();

    private final Map<UUID, PlayerState> states = new ConcurrentHashMap<>();

    private BukkitTask task;
    private int cleanupTicks;
    private boolean started;
    private PlaceholderExpansion expansion;
    private int inactiveTicks;

    private PlayerTracker() {
    }

    public static PlayerTracker get() {
        return INSTANCE;
    }

    public PlayerState state(final Player player) {
        return this.states.computeIfAbsent(player.getUniqueId(), key -> new PlayerState());
    }

    public synchronized void start(final Plugin plugin, final PlaceholderExpansion owner) {
        this.expansion = owner;

        if (this.started) {
            return;
        }
        this.started = true;

        try {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        } catch (final Throwable throwable) {
            plugin.getLogger().log(Level.WARNING, "Could not register the Player-Expansion listeners", throwable);
        }

        if (this.task == null || this.task.isCancelled()) {
            this.task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
        }
    }

    private synchronized void shutdown() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        try {
            HandlerList.unregisterAll(this);
        } catch (final Throwable ignored) {
        }
        this.states.clear();
        this.started = false;
        this.inactiveTicks = 0;
    }

    private void tick() {
        if (this.expansion != null && !this.expansion.isRegistered()) {
            if (++this.inactiveTicks > 100) {
                this.shutdown();
                return;
            }
        } else {
            this.inactiveTicks = 0;
        }

        final long now = System.currentTimeMillis();

        for (final Player player : Bukkit.getOnlinePlayers()) {
            final PlayerState state = this.state(player);
            state.lastSeen = now;

            final boolean sprinting = player.isSprinting();
            final boolean sneaking = player.isSneaking() && player.getPose() == Pose.SNEAKING;
            final boolean sleeping = player.isSleeping();
            final boolean elytra = player.isGliding();
            final boolean flying = player.isFlying() && !elytra;
            final boolean inBed = PlayerUtil.isInBed(player);
            final boolean laying = (player.getPose() == Pose.SLEEPING && !inBed) || GsitHook.isLaying(player);
            final boolean crawling = (player.getPose() == Pose.SWIMMING && !player.isInWater())
                    || GsitHook.isCrawling(player);
            final boolean sitting = PlayerUtil.isSitting(player) || GsitHook.isSitting(player);

            state.sprinting = sprinting;
            state.sneaking = sneaking;
            state.sleeping = sleeping;
            state.flying = flying;
            state.elytra = elytra;
            state.bed = inBed;
            state.lay = laying;
            state.crawling = crawling;
            state.sitting = sitting;

            state.sprintStart = update(state.sprintStart, now, sprinting);
            state.sneakStart = update(state.sneakStart, now, sneaking);
            state.sleepStart = update(state.sleepStart, now, sleeping);
            state.flyStart = update(state.flyStart, now, flying);
            state.elytraStart = update(state.elytraStart, now, elytra);
            state.bedStart = update(state.bedStart, now, inBed);
            state.layStart = update(state.layStart, now, laying);
            state.crawlStart = update(state.crawlStart, now, crawling);
            state.sitStart = update(state.sitStart, now, sitting);

            final Location location = player.getLocation();
            boolean moved = false;
            if (state.hasPosition) {
                final double dx = location.getX() - state.lastX;
                final double dz = location.getZ() - state.lastZ;
                final double distanceSquared = dx * dx + dz * dz;
                moved = distanceSquared > 0.0025D && distanceSquared < 4.0D;
            }
            final boolean walking = moved
                    && !sprinting && !sneaking && !flying && !elytra
                    && !sitting && !sleeping && !laying && !crawling
                    && !player.isInsideVehicle() && !player.isDead()
                    && player.isOnGround() && player.getPose() == Pose.STANDING;

            state.walking = walking;
            state.walkStart = update(state.walkStart, now, walking);

            state.lastX = location.getX();
            state.lastY = location.getY();
            state.lastZ = location.getZ();
            state.hasPosition = true;

            final int level = player.getLevel();
            if (state.knownLevel == Integer.MIN_VALUE) {
                state.knownLevel = level;
            } else {
                if (level > state.knownLevel) {
                    state.lastLevel = level;
                }
                state.knownLevel = level;
            }
        }

        if (++this.cleanupTicks >= 600) {
            this.cleanupTicks = 0;
            this.states.entrySet().removeIf(entry -> Bukkit.getPlayer(entry.getKey()) == null
                    && now - entry.getValue().lastSeen > 86_400_000L);
        }
    }

    private static long update(final long start, final long now, final boolean active) {
        if (!active) {
            return -1L;
        }
        return start < 0L ? now : start;
    }

    private static Player resolvePlayer(final Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }
        if (entity instanceof Projectile) {
            final ProjectileSource shooter = ((Projectile) entity).getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }
        if (entity instanceof TNTPrimed) {
            final Entity source = ((TNTPrimed) entity).getSource();
            if (source instanceof Player) {
                return (Player) source;
            }
        }
        if (entity instanceof AreaEffectCloud) {
            final ProjectileSource source = ((AreaEffectCloud) entity).getSource();
            if (source instanceof Player) {
                return (Player) source;
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player victim = (Player) event.getEntity();
        final Player damager = resolvePlayer(event.getDamager());
        if (damager == null || damager.getUniqueId().equals(victim.getUniqueId())) {
            return;
        }

        final long now = System.currentTimeMillis();

        final PlayerState victimState = this.state(victim);
        victimState.lastDamagePlayer = damager.getName();
        victimState.lastCombatName = damager.getName();
        victimState.lastCombatTime = now;

        final PlayerState damagerState = this.state(damager);
        damagerState.lastCombatName = victim.getName();
        damagerState.lastCombatTime = now;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final PlayerState state = this.states.get(event.getPlayer().getUniqueId());
        if (state == null) {
            return;
        }
        state.lastSeen = System.currentTimeMillis();
        state.sprintStart = -1L;
        state.sneakStart = -1L;
        state.sleepStart = -1L;
        state.flyStart = -1L;
        state.elytraStart = -1L;
        state.bedStart = -1L;
        state.layStart = -1L;
        state.crawlStart = -1L;
        state.sitStart = -1L;
        state.walkStart = -1L;
        state.sprinting = false;
        state.sneaking = false;
        state.sleeping = false;
        state.flying = false;
        state.elytra = false;
        state.bed = false;
        state.lay = false;
        state.crawling = false;
        state.sitting = false;
        state.walking = false;
        state.hasPosition = false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player victim = event.getEntity();
        final Player killer = victim.getKiller();
        final long now = System.currentTimeMillis();

        final PlayerState victimState = this.state(victim);
        victimState.lastDeathTime = now;
        victimState.lastDeathName = deathCause(victim, killer);

        if (killer != null && !killer.getUniqueId().equals(victim.getUniqueId())) {
            final PlayerState killerState = this.state(killer);
            killerState.lastKillName = victim.getName();
            killerState.lastKillTime = now;
        }
    }

    private static String deathCause(final Player victim, final Player killer) {
        if (killer != null) {
            return killer.getName();
        }

        final EntityDamageEvent lastDamage = victim.getLastDamageCause();
        if (!(lastDamage instanceof EntityDamageByEntityEvent)) {
            return "";
        }

        final Entity damager = ((EntityDamageByEntityEvent) lastDamage).getDamager();
        final Player player = resolvePlayer(damager);
        return player == null ? damager.getType().name() : player.getName();
    }

    public static final class PlayerState {

        volatile boolean sprinting;
        volatile boolean sneaking;
        volatile boolean sleeping;
        volatile boolean flying;
        volatile boolean elytra;
        volatile boolean bed;
        volatile boolean lay;
        volatile boolean crawling;
        volatile boolean sitting;
        volatile boolean walking;

        volatile long sprintStart = -1L;
        volatile long sneakStart = -1L;
        volatile long sleepStart = -1L;
        volatile long flyStart = -1L;
        volatile long elytraStart = -1L;
        volatile long bedStart = -1L;
        volatile long layStart = -1L;
        volatile long crawlStart = -1L;
        volatile long sitStart = -1L;
        volatile long walkStart = -1L;

        volatile String lastDamagePlayer = "";
        volatile String lastDeathName = "";
        volatile long lastDeathTime;
        volatile String lastCombatName = "";
        volatile long lastCombatTime;
        volatile String lastKillName = "";
        volatile long lastKillTime;

        volatile int lastLevel = -1;
        volatile int knownLevel = Integer.MIN_VALUE;

        volatile long lastSeen;

        double lastX;
        double lastY;
        double lastZ;
        boolean hasPosition;

        public long sprintingSeconds() {
            return seconds(this.sprintStart);
        }

        public long sneakingSeconds() {
            return seconds(this.sneakStart);
        }

        public long sleepingSeconds() {
            return seconds(this.sleepStart);
        }

        public long flyingSeconds() {
            return seconds(this.flyStart);
        }

        public long elytraSeconds() {
            return seconds(this.elytraStart);
        }

        public long bedSeconds() {
            return seconds(this.bedStart);
        }

        public long laySeconds() {
            return seconds(this.layStart);
        }

        public long crawlingSeconds() {
            return seconds(this.crawlStart);
        }

        public long sittingSeconds() {
            return seconds(this.sitStart);
        }

        public long walkingSeconds() {
            return seconds(this.walkStart);
        }

        public long lastDeathSeconds() {
            return since(this.lastDeathTime);
        }

        public long lastCombatSeconds() {
            return since(this.lastCombatTime);
        }

        public long lastKillSeconds() {
            return since(this.lastKillTime);
        }

        private static long seconds(final long start) {
            if (start < 0L) {
                return 0L;
            }
            return Math.max(0L, (System.currentTimeMillis() - start) / 1000L);
        }

        private static long since(final long time) {
            if (time <= 0L) {
                return 0L;
            }
            return Math.max(0L, (System.currentTimeMillis() - time) / 1000L);
        }
    }
}
