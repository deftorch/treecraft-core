package com.treecraft.core.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface IDetectionHeuristic {
    HeuristicResult evaluate(BlockState state, @Nullable BlockGetter level, @Nullable BlockPos pos);
}