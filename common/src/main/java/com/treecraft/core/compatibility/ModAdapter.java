package com.treecraft.core.compatibility;

import com.treecraft.core.api.BlockStyle;
import java.util.List;

public interface ModAdapter {
    /**
     * Get mod ID this adapter supports
     */
    String getModId();

    /**
     * Check if mod is loaded
     */
    boolean isModLoaded();

    /**
     * Register blocks from this mod
     */
    void registerBlocks();

    /**
     * Create styles from this mod's blocks
     */
    List<BlockStyle> createStyles();
}