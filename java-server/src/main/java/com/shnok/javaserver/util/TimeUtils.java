package com.shnok.javaserver.util;

public class TimeUtils {
    // Get the current in game time in hour
    public static float ticksToHour(long gameTicks, int tickDurationMs, int dayDurationMinutes) {
        float ticksPerDay = (float)dayDurationMinutes * 60 * 1000 / tickDurationMs;
        float currentHours = gameTicks / ticksPerDay * 24 % 24;

        return currentHours;
    }
}
