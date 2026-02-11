package com.treecraft.core.test.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class MockBlockState {
    private final BlockState state;
    private final Block block;

    public MockBlockState() {
        this.state = Mockito.mock(BlockState.class);
        this.block = Mockito.mock(Block.class);
        when(state.getBlock()).thenReturn(block);
    }

    public BlockState get() {
        return state;
    }

    public Block getBlock() {
        return block;
    }

    public MockBlockState withSound(SoundType sound) {
        when(state.getSoundType()).thenReturn(sound);
        return this;
    }

    public MockBlockState withProperty(net.minecraft.world.level.block.state.properties.Property<?> property) {
        when(state.hasProperty(property)).thenReturn(true);
        return this;
    }
}
