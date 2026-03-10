package com.treecraft.core.registry;

import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.test.util.TestBootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeBlockRegistryTest {

    @BeforeAll
    static void init() {
        TestBootstrap.init();
    }

    @Test
    void testRegisterAndRetrieve() {
        Block mockBlock = Mockito.mock(Block.class);
        TreeBlockRegistry.register(mockBlock, TreeComponentType.TRUNK);

        assertTrue(TreeBlockRegistry.isTreeBlock(mockBlock));
        assertEquals(TreeComponentType.TRUNK, TreeBlockRegistry.getType(mockBlock));
    }

    @Test
    void testUnknownBlock() {
        Block mockBlock = Mockito.mock(Block.class);

        assertFalse(TreeBlockRegistry.isTreeBlock(mockBlock));
        assertEquals(TreeComponentType.UNKNOWN, TreeBlockRegistry.getType(mockBlock));
    }

    @Test
    void testRegisterOverride() {
        Block mockBlock = Mockito.mock(Block.class);
        TreeBlockRegistry.register(mockBlock, TreeComponentType.TRUNK);
        assertEquals(TreeComponentType.TRUNK, TreeBlockRegistry.getType(mockBlock));

        // Override
        TreeBlockRegistry.register(mockBlock, TreeComponentType.BRANCH);
        assertEquals(TreeComponentType.BRANCH, TreeBlockRegistry.getType(mockBlock));
    }

    @Test
    void testVanillaBlock() {
        // Since we are mocking everything, let's verify integration with real blocks if we can.
        // Blocks.OAK_LOG is available via TestBootstrap.
        // But we shouldn't modify global registry state if other tests rely on it being clean.
        // However, we can register it and verify.

        TreeBlockRegistry.register(Blocks.OAK_LOG, TreeComponentType.TRUNK);
        assertTrue(TreeBlockRegistry.isTreeBlock(Blocks.OAK_LOG));
        assertEquals(TreeComponentType.TRUNK, TreeBlockRegistry.getType(Blocks.OAK_LOG));
    }
}
