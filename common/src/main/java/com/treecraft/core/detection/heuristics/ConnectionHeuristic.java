package com.treecraft.core.detection.heuristics;

import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.IDetectionHeuristic;
import com.treecraft.core.api.TreeComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ConnectionHeuristic implements IDetectionHeuristic {
    @Override
    public HeuristicResult evaluate(BlockState block, @Nullable BlockGetter level, @Nullable BlockPos pos) {
        if (level == null || pos == null) {
            return new HeuristicResult(TreeComponentType.UNKNOWN, 0.0f);
        }

        int verticalConnections = 0;
        int horizontalConnections = 0;

        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighbor);

            if (neighborState.is(block.getBlock())) {
                if (dir.getAxis().isVertical()) {
                    verticalConnections++;
                } else {
                    horizontalConnections++;
                }
            }
        }

        if (verticalConnections > horizontalConnections) {
            return new HeuristicResult(TreeComponentType.TRUNK, 0.6f);
        }

        if (horizontalConnections > 0) {
            return new HeuristicResult(TreeComponentType.BRANCH, 0.5f);
        }

        return new HeuristicResult(TreeComponentType.UNKNOWN, 0.0f);
    }
}