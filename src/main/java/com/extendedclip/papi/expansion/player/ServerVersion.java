package com.extendedclip.papi.expansion.player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ServerVersion {

    private static final Pattern VERSION = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?");

    private ServerVersion() { }

    static int encode(final String bukkitVersion) {
        final Matcher matcher = VERSION.matcher(bukkitVersion);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Could not retrieve server version from '" + bukkitVersion + "'!");
        }

        final int major = parseInt(matcher.group(1));
        final int minor = parseInt(matcher.group(2));
        final int patch = matcher.group(3) == null ? 0 : parseInt(matcher.group(3));
        return major * 1_000_000 + minor * 1_000 + patch;
    }

    private static int parseInt(final String value) {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException exception) {
            return 0;
        }
    }
}
