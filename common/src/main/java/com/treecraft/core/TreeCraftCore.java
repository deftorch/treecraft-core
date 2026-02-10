package com.treecraft.core;

import com.treecraft.core.detection.TreeBlockDetector;
import com.treecraft.core.registry.StyleRegistry;
import com.treecraft.core.registry.TreeBlockRegistry;

public class TreeCraftCore {

    public static void init() {
        Constants.LOG.info("Initializing TreeCraft Core...");

        TreeBlockDetector.initialize();
        StyleRegistry.initialize();
        TreeBlockRegistry.initialize();

        Constants.LOG.info("TreeCraft Core initialized successfully!");
    }
}