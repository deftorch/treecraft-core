package com.treecraft.core.config;

public class CoreConfig {
    // Detection
    public static boolean enableAutoDetection = true;
    public static float minConfidenceThreshold = 0.5f;
    public static int maxTreeSize = 10000;

    // Compatibility
    public static boolean enableConquestSupport = true;
    public static boolean enableDynamicTreesSupport = true;
    public static boolean enableVanillaDetection = true;

    // Performance
    public static int detectionCacheSize = 1000;
    public static boolean asyncDetection = true;
    public static int maxDetectionThreads = 2;

    // Debug
    public static boolean debugMode = false;
    public static boolean logDetections = false;
}