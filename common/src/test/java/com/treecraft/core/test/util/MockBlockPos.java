package com.treecraft.core.test.util;

import net.minecraft.core.BlockPos;

public class MockBlockPos {
    public static BlockPos at(int x, int y, int z) {
        // BlockPos is a simple data class, we can instantiate it directly
        // providing the environment is set up correctly.
        // If direct instantiation fails due to static initializers, we might need to mock it.
        return new BlockPos(x, y, z);
    }
}
