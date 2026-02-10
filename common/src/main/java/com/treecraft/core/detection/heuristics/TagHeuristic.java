package com.treecraft.core.detection.heuristics;

import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.IDetectionHeuristic;
import com.treecraft.core.api.TreeComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TagHeuristic implements IDetectionHeuristic {
    @Override
    public HeuristicResult evaluate(BlockState block, @Nullable BlockGetter level, @Nullable BlockPos pos) {
        if (block.is(BlockTags.LOGS)) {
            return new HeuristicResult(TreeComponentType.TRUNK, 0.9f);
        }
        if (block.is(BlockTags.LEAVES)) {
            return new HeuristicResult(TreeComponentType.LEAVES, 0.9f);
        }
        if (block.is(BlockTags.SAPLINGS)) {
             return new HeuristicResult(TreeComponentType.UNKNOWN, 0.0f);
        }

        return new HeuristicResult(TreeComponentType.UNKNOWN, 0.0f);
    }
}