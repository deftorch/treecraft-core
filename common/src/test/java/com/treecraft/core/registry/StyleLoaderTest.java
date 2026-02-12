package com.treecraft.core.registry;

import com.treecraft.core.api.BlockStyle;
import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.test.platform.MockPlatformHelper;
import com.treecraft.core.test.util.TestBootstrap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StyleLoaderTest {

    private Path tempDir;

    @BeforeAll
    static void init() {
        TestBootstrap.init();
    }

    @BeforeEach
    void setUp() throws IOException {
        StyleRegistry.clear();
        tempDir = Files.createTempDirectory("treecraft_style_test");
        MockPlatformHelper.setConfigDir(tempDir);
    }

    @AfterEach
    void tearDown() throws IOException {
        MockPlatformHelper.reset();
        if (tempDir != null && Files.exists(tempDir)) {
             Files.walk(tempDir)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        }
    }

    @Test
    void testLoadStyles_FromJson() throws IOException {
        Path stylesDir = tempDir.resolve("treecraft/styles");
        Files.createDirectories(stylesDir);
        Path styleFile = stylesDir.resolve("test_style.json");

        String json = "{\n" +
                "  \"id\": \"treecraft:test_style_json\",\n" +
                "  \"display_name\": \"JSON Style\",\n" +
                "  \"palette\": {\n" +
                "    \"trunk\": [\"minecraft:oak_log\"],\n" +
                "    \"leaves\": [\"minecraft:oak_leaves\"]\n" +
                "  }\n" +
                "}";

        Files.writeString(styleFile, json);

        StyleLoader.loadStyles();

        ResourceLocation id = new ResourceLocation("treecraft", "test_style_json");
        assertTrue(StyleRegistry.hasStyle(id));

        Optional<BlockStyle> style = StyleRegistry.getStyle(id);
        assertTrue(style.isPresent());
        assertEquals("JSON Style", style.get().getDisplayName());

        // Verify palette loaded correctly
        // Note: BuiltInRegistries should resolve minecraft:oak_log to Blocks.OAK_LOG
        assertTrue(style.get().getBlockPalette().containsKey(TreeComponentType.TRUNK));
        assertEquals(Blocks.OAK_LOG.defaultBlockState(), style.get().getBlockPalette().get(TreeComponentType.TRUNK).get(0));
    }
}
