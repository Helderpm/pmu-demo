package com.pmu2.exec.util;

/**
 * Utility class for mathematical operations.
 */
public final class MathUtil {
    
    private MathUtil() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Clamps a value between a minimum and maximum range.
     * 
     * @param value the value to clamp
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     * @return the clamped value
     * @throws IllegalArgumentException if min > max
     */
    public static int clamp(int value, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min cannot be greater than max");
        }
        return Math.clamp(value, min, max);
    }
    
}

