package com.extendedclip.papi.expansion.player;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.logging.Level;

/*
 *
 * Player-Expansion
 * Copyright (C) 2018 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
public final class PlayerUtil {

    public static final int ticksAtMidnight = 18000;
    public static final int ticksPerDay = 24000;
    public static final int ticksPerHour = 1000;
    public static final double ticksPerMinute = 1000d / 60d;
    private static final SimpleDateFormat twentyFour = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    private static final SimpleDateFormat twelve = new SimpleDateFormat("h:mm aa", Locale.ENGLISH);
    private static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

    private PlayerUtil() { }

    private static final Function<Player, Integer> PLAYER_GET_PING = new Function<Player, Integer>() {

        private Field ping;
        private Method getHandle;

        @Override
        public Integer apply(final Player player) {
            // 1.17 added Player#getPing, and we can use that directly
            if (VersionHelper.IS_1_17_OR_NEWER) {
                return player.getPing();
            }

            // For versions before 1.17 we need to use reflection
            if (ping == null) {
                try {
                    cacheReflection(player);
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException ex) {
                    ex.printStackTrace();
                }
            }

            try {
                return ping.getInt(getHandle.invoke(player));
            } catch (final IllegalAccessException | InvocationTargetException ex) {
                PlaceholderAPIPlugin.getInstance()
                                .getLogger()
                                .log(Level.SEVERE, "Could not get the ping of " + player.getName() + ", using -1 as fallback value", ex);
            }
            return -1;
        }


        private void cacheReflection(final Player player) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            getHandle = player.getClass().getDeclaredMethod("getHandle");
            getHandle.setAccessible(true);

            final Object entityPlayer = getHandle.invoke(player);
            ping = entityPlayer.getClass().getDeclaredField("ping");
            ping.setAccessible(true);
        }
    };

    public static int getPing(final Player player) {
        return PLAYER_GET_PING.apply(player);
    }

    public static String getLocale(final Player player) {
        final String locale = player.getLocale();
        return locale == null || locale.isEmpty() ? "en_US" : locale;
    }

    public static String format12(long ticks) {
        try {
            return twelve.format(twentyFour.parse(ticksToTime(ticks)));
        } catch (ParseException e) {
            return ticksToTime(ticks);
        }
    }

    public static String format24(long ticks) {
        return ticksToTime(ticks);
    }

    private static String ticksToTime(long ticks) {
        ticks = ticks - ticksAtMidnight + ticksPerDay;
        long hours = ticks / ticksPerHour;
        ticks -= hours * ticksPerHour;
        long mins = (long) Math.floor(ticks / ticksPerMinute);
        if (hours >= 24) {
            hours = hours - 24;
        }
        return (hours < 10 ? "0" + hours : hours) + ":" + (mins < 10 ? "0" + mins : mins);
    }

    public static BlockFace getDirection(Player player) {
        return radial[Math.round(player.getLocation().getYaw() / 45f) & 0x7].getOppositeFace();
    }

    public static String getXZDirection(Player player) {
        double rotation = player.getLocation().getYaw();
        if (rotation < 0.0D) {
            rotation += 360.0D;
        }

        if (Math.abs(rotation) <= 45 || Math.abs(rotation - 360) <= 45) {
            return "+Z";
        } else if (Math.abs(rotation - 90) <= 45) {
            return "-X";
        } else if (Math.abs(rotation - 180) <= 45) {
            return "-Z";
        } else if (Math.abs(rotation - 270) <= 45) {
            return "+X";
        }

        return "";
    }

    public static ItemStack itemInHand(Player p) {
        try {
            return p.getInventory().getItemInMainHand();
        } catch (NoSuchMethodError e) {
            return p.getInventory().getItemInHand();
        }
    }

    public static int durability(ItemStack item) {
        if (item == null) {
            return 0;
        }

        final ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            final Damageable damageable = (Damageable) meta;
            final int max = damageable.hasMaxDamage()
                    ? damageable.getMaxDamage()
                    : item.getType().getMaxDurability();
            return Math.max(0, max - damageable.getDamage());
        }

        return Math.max(0, item.getType().getMaxDurability() - item.getDurability());
    }

    public static int getEmptySlots(Player p) {
        int slots = 0;
        PlayerInventory inv = p.getInventory();
        for (ItemStack is : inv.getContents()) {
            if (is == null) slots++;
        }

        if (!Bukkit.getBukkitVersion().contains("1.7") && !Bukkit.getBukkitVersion().contains("1.8")) {
            if (inv.getItemInOffHand() == null || inv.getItemInOffHand().getType() == Material.AIR) slots--;
            if (inv.getHelmet() == null) slots--;
            if (inv.getChestplate() == null) slots--;
            if (inv.getLeggings() == null) slots--;
            if (inv.getBoots() == null) slots--;
        }
        return slots;
    }

    private static int getExperienceAtLevel(int level) {
        if (level <= 15) {
            return (level << 1) + 7;
        }
        if (level <= 30) {
            return (level * 5) - 38;
        }
        return (level * 9) - 158;
    }

    public static int getTotalExperience(Player player) {
        int experience = Math.round(getExperienceAtLevel(player.getLevel()) * player.getExp());
        int currentLevel = player.getLevel();
        while (currentLevel > 0) {
            currentLevel--;
            experience += getExperienceAtLevel(currentLevel);
        }
        if (experience < 0) {
            experience = 0;
        }
        return experience;
    }

    public static String getBiome(Player p) {
        if (VersionHelper.IS_1_21_3_OR_NEWER) {
            return p.getLocation().getBlock().getBiome().name();
        }

        return String.valueOf(p.getLocation().getBlock().getBiome());
    }

    public static String getCapitalizedBiome(Player p) {
        String[] biomeWords = getBiome(p).split("_");
        for (int i = 0; i < biomeWords.length; i++) {
            biomeWords[i] = biomeWords[i].substring(0, 1).toUpperCase() + biomeWords[i].substring(1).toLowerCase();
        }
        return String.join(" ", biomeWords);
    }

    public static double getMaxHealth(final Player player) {
        final AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute != null) {
            return attribute.getValue();
        }
        return player.getMaxHealth();
    }

    public static Attribute getAttribute(final String name) {
        if (name == null) {
            return null;
        }

        String normalized = name.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        for (final String prefix : new String[] { "minecraft:", "generic.", "generic_", "player.", "player_" }) {
            if (normalized.startsWith(prefix)) {
                normalized = normalized.substring(prefix.length());
            }
        }
        if (normalized.isEmpty()) {
            return null;
        }

        final NamespacedKey key = NamespacedKey.minecraft(normalized);
        try {
            final Attribute attribute = Registry.ATTRIBUTE.get(key);
            if (attribute != null) {
                return attribute;
            }
        } catch (final Throwable ignored) {
        }

        try {
            return Attribute.valueOf(normalized.toUpperCase(Locale.ROOT));
        } catch (final Throwable ignored) {
            return null;
        }
    }

    public static String getAttributeValue(final Player player, final String name) {
        final Attribute attribute = getAttribute(name);
        if (attribute == null) {
            return "";
        }

        final AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return "0";
        }

        return String.valueOf(instance.getValue());
    }

    public static String getElytraSpeed(final Player player) {
        final Vector velocity = player.getVelocity();
        final double speed = Math.sqrt(velocity.getX() * velocity.getX()
                + velocity.getY() * velocity.getY()
                + velocity.getZ() * velocity.getZ()) * 20.0D;
        return String.format(Locale.ENGLISH, "%.2f", speed);
    }

    public static boolean isInBed(final Player player) {
        if (!player.isSleeping() && player.getPose() != Pose.SLEEPING) {
            return false;
        }

        final Location location = player.getLocation();
        return isBed(location.getBlock()) || isBed(location.clone().subtract(0, 1, 0).getBlock());
    }

    private static boolean isBed(final Block block) {
        return block.getBlockData() instanceof Bed;
    }

    public static boolean isSitting(final Player player) {
        if (player.getPose() == Pose.SITTING) {
            return true;
        }

        if (player.isInsideVehicle()) {
            final Entity vehicle = player.getVehicle();
            if (vehicle != null) {
                final String type = vehicle.getType().name();
                return type.contains("CUSHION") || type.contains("SEAT") || type.equals("ARMOR_STAND");
            }
        }

        return false;
    }

    public static PotionEffectType getPotionEffectType(final String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        final String normalized = name.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        final NamespacedKey key = normalized.contains(":")
                ? NamespacedKey.fromString(normalized)
                : NamespacedKey.minecraft(normalized);
        if (key != null) {
            try {
                final PotionEffectType type = Registry.EFFECT.get(key);
                if (type != null) {
                    return type;
                }
            } catch (final Throwable ignored) {
            }
        }

        try {
            return PotionEffectType.getByName(name.trim().toUpperCase(Locale.ROOT).replace(' ', '_'));
        } catch (final Throwable ignored) {
            return null;
        }
    }

    public static int getEnchantmentLevel(final ItemStack item, final String name) {
        if (item == null || name == null || name.isEmpty()) {
            return 0;
        }

        final String normalized = name.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        Enchantment enchantment = null;

        final NamespacedKey key = normalized.contains(":")
                ? NamespacedKey.fromString(normalized)
                : NamespacedKey.minecraft(normalized);
        if (key != null) {
            try {
                enchantment = Enchantment.getByKey(key);
            } catch (final Throwable ignored) {
            }
        }

        if (enchantment == null) {
            try {
                enchantment = Enchantment.getByName(name.trim().toUpperCase(Locale.ROOT).replace(' ', '_'));
            } catch (final Throwable ignored) {
                return 0;
            }
        }

        return enchantment == null ? 0 : item.getEnchantmentLevel(enchantment);
    }

    public static String formatDuration(final long totalSeconds, final String format) {
        if (format == null || format.isEmpty()) {
            return "0";
        }

        final long seconds = Math.max(0L, totalSeconds);
        final long days = seconds / 86400L;
        final long hours = (seconds % 86400L) / 3600L;
        final long minutes = (seconds % 3600L) / 60L;
        final long secs = seconds % 60L;

        final List<Object> parts = new ArrayList<>();
        int tokenCount = 0;
        char highest = 0;

        for (int i = 0; i < format.length(); ) {
            final char current = format.charAt(i);
            final int start = i;

            if (Character.isLetter(current)) {
                while (i < format.length() && Character.isLetter(format.charAt(i))) {
                    i++;
                }
                final String run = format.substring(start, i);

                if (run.length() <= 2 && isDurationRun(run)) {
                    final char first = Character.toUpperCase(run.charAt(0));
                    if (run.length() == 1) {
                        parts.add(new DurationToken(first, false));
                        tokenCount++;
                    } else if (first == Character.toUpperCase(run.charAt(1))) {
                        parts.add(new DurationToken(first, true));
                        tokenCount++;
                    } else {
                        parts.add(new DurationToken(first, false));
                        parts.add(new DurationToken(Character.toUpperCase(run.charAt(1)), false));
                        tokenCount += 2;
                    }

                    if (highest == 0 || order(first) > order(highest)) {
                        highest = first;
                    }
                    final char second = Character.toUpperCase(run.charAt(run.length() - 1));
                    if (order(second) > order(highest)) {
                        highest = second;
                    }
                } else {
                    parts.add(run);
                }
            } else {
                while (i < format.length() && !Character.isLetter(format.charAt(i))) {
                    i++;
                }
                parts.add(format.substring(start, i));
            }
        }

        if (tokenCount == 0) {
            return format;
        }

        if (tokenCount == 1) {
            for (final Object part : parts) {
                if (part instanceof DurationToken) {
                    return String.valueOf(total(((DurationToken) part).unit, days, seconds));
                }
            }
        }

        final StringBuilder builder = new StringBuilder(format.length() + 4);
        for (final Object part : parts) {
            if (!(part instanceof DurationToken)) {
                builder.append(part);
                continue;
            }

            final DurationToken token = (DurationToken) part;
            final long value = token.unit == highest
                    ? total(token.unit, days, seconds)
                    : component(token.unit, days, hours, minutes, secs);

            if (token.padded && value < 10) {
                builder.append('0');
            }
            builder.append(value);
        }

        return builder.toString();
    }

    private static long total(final char unit, final long days, final long seconds) {
        switch (unit) {
            case 'D':
                return days;
            case 'H':
                return seconds / 3600L;
            case 'M':
                return seconds / 60L;
            default:
                return seconds;
        }
    }

    private static long component(final char unit, final long days, final long hours,
                                  final long minutes, final long secs) {
        switch (unit) {
            case 'D':
                return days;
            case 'H':
                return hours;
            case 'M':
                return minutes;
            default:
                return secs;
        }
    }

    private static int order(final char unit) {
        switch (unit) {
            case 'D':
                return 3;
            case 'H':
                return 2;
            case 'M':
                return 1;
            default:
                return 0;
        }
    }

    private static boolean isDurationRun(final String run) {
        for (int i = 0; i < run.length(); i++) {
            final char upper = Character.toUpperCase(run.charAt(i));
            if (upper != 'D' && upper != 'H' && upper != 'M' && upper != 'S') {
                return false;
            }
        }
        return true;
    }

    private static final class DurationToken {
        private final char unit;
        private final boolean padded;

        private DurationToken(final char unit, final boolean padded) {
            this.unit = unit;
            this.padded = padded;
        }
    }
}
