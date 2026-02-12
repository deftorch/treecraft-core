package com.treecraft.core.registry;

import com.treecraft.core.api.BlockStyle;
import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.test.util.TestBootstrap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StyleRegistryTest {

    @BeforeAll
    static void init() {
        TestBootstrap.init();
    }

    @BeforeEach
    void setUp() {
        StyleRegistry.clear();
    }

    @Test
    void testRegisterStyle() {
        ResourceLocation id = new ResourceLocation("treecraft", "test_style");
        BlockStyle style = new BlockStyle.Builder()
                .id(id)
                .displayName("Test Style")
                .trunk(Blocks.OAK_LOG)
                .leaves(Blocks.OAK_LEAVES)
                .build();

        StyleRegistry.register(style);

        assertTrue(StyleRegistry.hasStyle(id));
        assertEquals(1, StyleRegistry.getStyleCount());
    }

    @Test
    void testGetStyle() {
        ResourceLocation id = new ResourceLocation("treecraft", "test_style");
        BlockStyle style = new BlockStyle.Builder()
                .id(id)
                .displayName("Test Style")
                .trunk(Blocks.OAK_LOG)
                .leaves(Blocks.OAK_LEAVES)
                .build();

        StyleRegistry.register(style);

        Optional<BlockStyle> retrieved = StyleRegistry.getStyle(id);
        assertTrue(retrieved.isPresent());
        assertEquals("Test Style", retrieved.get().getDisplayName());
    }

    @Test
    void testGetStylesByMod() {
        ResourceLocation id1 = new ResourceLocation("mod_a", "style1");
        ResourceLocation id2 = new ResourceLocation("mod_a", "style2");
        ResourceLocation id3 = new ResourceLocation("mod_b", "style3");

        StyleRegistry.register(new BlockStyle.Builder().id(id1).displayName("S1").trunk(Blocks.STONE).build());
        StyleRegistry.register(new BlockStyle.Builder().id(id2).displayName("S2").trunk(Blocks.STONE).build());
        StyleRegistry.register(new BlockStyle.Builder().id(id3).displayName("S3").trunk(Blocks.STONE).build());

        assertEquals(2, StyleRegistry.getStylesByMod("mod_a").size());
        assertEquals(1, StyleRegistry.getStylesByMod("mod_b").size());
        assertEquals(0, StyleRegistry.getStylesByMod("mod_c").size());
    }
}
