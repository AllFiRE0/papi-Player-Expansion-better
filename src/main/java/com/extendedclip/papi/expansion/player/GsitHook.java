package com.extendedclip.papi.expansion.player;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

final class GsitHook {

    private static final Method IS_SITTING;
    private static final Method GET_POSE;
    private static final Method IS_CRAWLING;
    private static final Method GET_POSE_TYPE;

    static {
        Method isSitting = null;
        Method getPose = null;
        Method isCrawling = null;
        Method getPoseType = null;

        try {
            final Class<?> api = Class.forName("dev.geco.gsit.api.GSitAPI");
            isSitting = api.getMethod("isEntitySitting", LivingEntity.class);
            getPose = api.getMethod("getPoseByPlayer", Player.class);
            isCrawling = api.getMethod("isPlayerCrawling", Player.class);
            getPoseType = Class.forName("dev.geco.gsit.model.Pose").getMethod("getPoseType");
        } catch (final Throwable ignored) {
            isSitting = null;
            getPose = null;
            isCrawling = null;
            getPoseType = null;
        }

        IS_SITTING = isSitting;
        GET_POSE = getPose;
        IS_CRAWLING = isCrawling;
        GET_POSE_TYPE = getPoseType;
    }

    private GsitHook() {
    }

    static boolean isAvailable() {
        return IS_SITTING != null && GET_POSE != null && IS_CRAWLING != null && GET_POSE_TYPE != null;
    }

    static boolean isSitting(final Player player) {
        return invokeBoolean(IS_SITTING, player);
    }

    static boolean isCrawling(final Player player) {
        return invokeBoolean(IS_CRAWLING, player);
    }

    static boolean isLaying(final Player player) {
        if (GET_POSE == null || GET_POSE_TYPE == null) {
            return false;
        }

        try {
            final Object pose = GET_POSE.invoke(null, player);
            if (pose == null) {
                return false;
            }

            final Object poseType = GET_POSE_TYPE.invoke(pose);
            if (!(poseType instanceof Enum)) {
                return false;
            }

            final String name = ((Enum<?>) poseType).name();
            return "LAY".equals(name) || "BELLYFLOP".equals(name);
        } catch (final Throwable ignored) {
            return false;
        }
    }

    private static boolean invokeBoolean(final Method method, final Player player) {
        if (method == null) {
            return false;
        }

        try {
            final Object result = method.invoke(null, player);
            return result instanceof Boolean && (Boolean) result;
        } catch (final Throwable ignored) {
            return false;
        }
    }
}
