package com.treecraft.core.detection.heuristics;

import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.IDetectionHeuristic;
import com.treecraft.core.api.TreeComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class MaterialHeuristic implements IDetectionHeuristic {
    @Override
    public HeuristicResult evaluate(BlockState block, @Nullable BlockGetter level, @Nullable BlockPos pos) {
        SoundType sound = block.getSoundType();

        if (sound == SoundType.WOOD || sound == SoundType.NETHER_WOOD || sound == SoundType.BAMBOO_WOOD) {
            return new HeuristicResult(TreeComponentType.TRUNK, 0.4f);
        }

        if (sound == SoundType.GRASS || sound == SoundType.VINE || sound == SoundType.CHERRY_LEAVES || sound == SoundType.AZALEA_LEAVES) {
            return new HeuristicResult(TreeComponentType.LEAVES, 0.4f);
        }

        return new HeuristicResult(TreeComponentType.UNKNOWN, 0.0f);
    }
}