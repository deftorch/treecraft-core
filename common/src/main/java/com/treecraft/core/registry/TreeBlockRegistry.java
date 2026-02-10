package com.treecraft.core.registry;

import com.treecraft.core.Constants;
import com.treecraft.core.api.TreeComponentType;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TreeBlockRegistry {
    private static final Map<Block, TreeComponentType> REGISTRY = new ConcurrentHashMap<>();

    public static void initialize() {
        Constants.LOG.info("Initializing Tree Block Registry...");
    }

    public static void register(Block block, TreeComponentType type) {
        REGISTRY.put(block, type);
    }

    public static TreeComponentType getType(Block block) {
        return REGISTRY.getOrDefault(block, TreeComponentType.UNKNOWN);
    }

    public static boolean isTreeBlock(Block block) {
        return REGISTRY.containsKey(block);
    }

    public static Map<Block, TreeComponentType> getRegisteredBlocks() {
        return Collections.unmodifiableMap(REGISTRY);
    }
}