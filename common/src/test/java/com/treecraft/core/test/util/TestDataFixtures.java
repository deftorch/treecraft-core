package com.treecraft.core.test.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class TestDataFixtures {

    public static BlockState createBlockState(SoundType sound) {
        return new MockBlockState()
                .withSound(sound)
                .get();
    }

    public static BlockState createLog() {
        return createBlockState(SoundType.WOOD);
    }

    public static BlockState createLeaves() {
        return createBlockState(SoundType.GRASS);
    }
}
