package com.treecraft.core.api;

import com.treecraft.core.detection.TreeStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ITreeBlockDetector {
    TreeComponentType detectBlockType(BlockState block);
    TreeStructure detectTree(BlockPos pos, Level level);
    void registerHeuristic(IDetectionHeuristic heuristic);
}