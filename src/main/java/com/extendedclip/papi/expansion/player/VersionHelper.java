package com.extendedclip.papi.expansion.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

/**
 * @author Matt (<a href="https://github.com/ipsk">@ipsk</a>)
 */
public final class VersionHelper {

    private static final int VERSION = ServerVersion.encode(Bukkit.getBukkitVersion());

    /**
     * @see Damageable#getAbsorptionAmount()
     */
    public static final boolean HAS_ABSORPTION_METHODS = VERSION >= 1_015_000;

    /**
     * @see Player#getPing()
     */
    public static final boolean IS_1_17_OR_NEWER = VERSION >= 1_017_000;

    /**
     * @see Player#getLocale()
     */
    public static final boolean IS_1_20_2_OR_NEWER = VERSION >= 1_020_002;

    /**
     * @see Player#getLocale()
     */
    public static final boolean IS_1_20_4_OR_NEWER = VERSION >= 1_020_004;

    public static final boolean IS_1_21_OR_NEWER = VERSION >= 1_021_000;

    public static final boolean IS_1_21_3_OR_NEWER = VERSION >= 1_021_003;

    private VersionHelper() { }
}
