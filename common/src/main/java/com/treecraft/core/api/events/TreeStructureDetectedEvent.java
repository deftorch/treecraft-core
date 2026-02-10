package com.treecraft.core.api.events;

import com.treecraft.core.detection.TreeStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TreeStructureDetectedEvent {
    private final TreeStructure structure;
    private final Level level;
    private final BlockPos startPos;

    public TreeStructureDetectedEvent(TreeStructure structure, Level level, BlockPos startPos) {
        this.structure = structure;
        this.level = level;
        this.startPos = startPos;
    }

    public TreeStructure getStructure() { return structure; }
    public Level getLevel() { return level; }
    public BlockPos getStartPos() { return startPos; }
}