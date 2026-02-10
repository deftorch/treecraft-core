package com.treecraft.core.compatibility;

import com.treecraft.core.Constants;
import com.treecraft.core.api.BlockStyle;
import com.treecraft.core.registry.StyleRegistry;

import java.util.ArrayList;
import java.util.List;

public class ModCompatibility {
    private static final List<ModAdapter> ADAPTERS = new ArrayList<>();

    public static void registerAdapters() {
        registerAdapter(new VanillaAdapter());
        // Add other adapters here when implemented
        // registerAdapter(new ConquestAdapter());
        // registerAdapter(new DynamicTreesAdapter());
    }

    public static void loadCompatibility() {
        Constants.LOG.info("Loading mod compatibility...");

        for (ModAdapter adapter : ADAPTERS) {
            try {
                if (adapter.isModLoaded()) {
                    Constants.LOG.info("Loading adapter for: {}", adapter.getModId());
                    adapter.registerBlocks();

                    List<BlockStyle> styles = adapter.createStyles();
                    if (styles != null) {
                        for (BlockStyle style : styles) {
                            StyleRegistry.register(style);
                        }
                    }
                }
            } catch (Exception e) {
                Constants.LOG.error("Failed to load adapter for " + adapter.getModId(), e);
            }
        }
    }

    public static void registerAdapter(ModAdapter adapter) {
        ADAPTERS.add(adapter);
    }
}