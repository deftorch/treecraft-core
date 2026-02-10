package com.treecraft.core.detection.heuristics;

import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.IDetectionHeuristic;
import com.treecraft.core.api.TreeComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.regex.Pattern;

public class NameHeuristic implements IDetectionHeuristic {
    private static final Map<Pattern, TreeComponentType> PATTERNS = Map.of(
        Pattern.compile(".*log.*|.*trunk.*|.*wood.*"), TreeComponentType.TRUNK,
        Pattern.compile(".*branch.*|.*twig.*"), TreeComponentType.BRANCH,
        Pattern.compile(".*leaves.*|.*foliage.*"), TreeComponentType.LEAVES,
        Pattern.compile(".*root.*"), TreeComponentType.ROOT
    );

    @Override
    public HeuristicResult evaluate(BlockState block, @Nullable BlockGetter level, @Nullable BlockPos pos) {
        String id = getBlockKey(block.getBlock()).toString().toLowerCase();

        for (Map.Entry<Pattern, TreeComponentType> entry : PATTERNS.entrySet()) {
            if (entry.getKey().matcher(id).matches()) {
                return new HeuristicResult(entry.getValue(), 0.8f);
            }
        }

        return new HeuristicResult(TreeComponentType.UNKNOWN, 0.0f);
    }

    protected net.minecraft.resources.ResourceLocation getBlockKey(net.minecraft.world.level.block.Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
}