package com.treecraft.core.test.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MockLevel {
    private final Level level;
    private final Map<BlockPos, BlockState> blocks = new HashMap<>();

    public MockLevel() {
        this.level = Mockito.mock(Level.class);
        // Use thenAnswer to dynamically return block state from the map
        when(level.getBlockState(any(BlockPos.class))).thenAnswer(invocation -> {
            BlockPos pos = invocation.getArgument(0);
            // Default to AIR if not set
            return blocks.getOrDefault(pos, Blocks.AIR.defaultBlockState());
        });
    }

    public Level get() {
        return level;
    }

    public MockLevel withBlock(BlockPos pos, BlockState state) {
        // Since BlockPos implements equals/hashCode correctly, this works
        // Note: Make sure pos used here and in getBlockState are effectively equal (same coordinates)
        blocks.put(pos.immutable(), state);
        return this;
    }
}
