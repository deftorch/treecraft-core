package com.treecraft.core.test.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MockLevel {
    private final Level level;

    public MockLevel() {
        this.level = Mockito.mock(Level.class);
    }

    public Level get() {
        return level;
    }

    public MockLevel withBlock(BlockPos pos, BlockState state) {
        when(level.getBlockState(pos)).thenReturn(state);
        return this;
    }
}
