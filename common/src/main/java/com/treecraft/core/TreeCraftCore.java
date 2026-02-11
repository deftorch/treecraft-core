package com.treecraft.core;

import com.treecraft.core.compatibility.ModCompatibility;
import com.treecraft.core.config.ConfigLoader;
import com.treecraft.core.detection.TreeBlockDetector;
import com.treecraft.core.registry.StyleLoader;
import com.treecraft.core.registry.StyleRegistry;
import com.treecraft.core.registry.TreeBlockRegistry;

public class TreeCraftCore {

    public static void init() {
        Constants.LOG.info("Initializing TreeCraft Core...");

        ConfigLoader.load();
        TreeBlockDetector.initialize();
        StyleRegistry.initialize();
        TreeBlockRegistry.initialize();

        // Load compatibility
        ModCompatibility.registerAdapters();
        ModCompatibility.loadCompatibility();

        // Load styles after registries are initialized
        StyleLoader.loadStyles();

        Constants.LOG.info("TreeCraft Core initialized successfully!");
    }
}