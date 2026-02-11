package com.treecraft.core.detection.heuristics;

import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.IDetectionHeuristic;
import com.treecraft.core.api.TreeComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

public class PropertiesHeuristic implements IDetectionHeuristic {
    @Override
    public HeuristicResult evaluate(BlockState block, @Nullable BlockGetter level, @Nullable BlockPos pos) {
        // Check block properties
        if (block.hasProperty(BlockStateProperties.AXIS)) {
            // Log-like blocks have axis property
            return new HeuristicResult(TreeComponentType.TRUNK, 0.7f);
        }

        if (block.hasProperty(BlockStateProperties.DISTANCE)) {
            // Leaves have distance property in vanilla
            return new HeuristicResult(TreeComponentType.LEAVES, 0.8f);
        }

        return new HeuristicResult(TreeComponentType.UNKNOWN, 0.0f);
    }
}
