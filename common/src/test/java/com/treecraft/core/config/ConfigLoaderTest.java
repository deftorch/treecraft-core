package com.treecraft.core.config;

import com.treecraft.core.test.platform.MockPlatformHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigLoaderTest {

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("treecraft_config_test");
        MockPlatformHelper.setConfigDir(tempDir);

        // Reset CoreConfig to defaults before each test
        CoreConfig.enableAutoDetection = true;
        CoreConfig.minConfidenceThreshold = 0.5f;
        CoreConfig.maxTreeSize = 10000;
        CoreConfig.enableConquestSupport = true;
        CoreConfig.enableDynamicTreesSupport = true;
        CoreConfig.enableVanillaDetection = true;
        CoreConfig.detectionCacheSize = 1000;
        CoreConfig.asyncDetection = true;
        CoreConfig.maxDetectionThreads = 2;
        CoreConfig.debugMode = false;
        CoreConfig.logDetections = false;
    }

    @AfterEach
    void tearDown() throws IOException {
        MockPlatformHelper.reset();

        // Cleanup temp dir
        if (tempDir != null && Files.exists(tempDir)) {
            // Simple recursive delete
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
    void testLoadDefaultConfig_ShouldCreateFile() {
        ConfigLoader.load();

        Path configFile = tempDir.resolve("treecraft/core.json");
        assertTrue(Files.exists(configFile), "Config file should be created");

        // Verify defaults are still set
        assertTrue(CoreConfig.enableAutoDetection);
        assertEquals(0.5f, CoreConfig.minConfidenceThreshold);
    }

    @Test
    void testLoadExistingConfig_ShouldUpdateValues() throws IOException {
        // Create a config file with custom values
        Path configDir = tempDir.resolve("treecraft");
        Files.createDirectories(configDir);
        Path configFile = configDir.resolve("core.json");

        String json = "{\n" +
            "  \"enableAutoDetection\": false,\n" +
            "  \"minConfidenceThreshold\": 0.8,\n" +
            "  \"maxTreeSize\": 5000\n" +
            "}";

        Files.writeString(configFile, json);

        ConfigLoader.load();

        // Verify values are updated
        assertFalse(CoreConfig.enableAutoDetection, "Auto detection should be false");
        assertEquals(0.8f, CoreConfig.minConfidenceThreshold, 0.001f);
        assertEquals(5000, CoreConfig.maxTreeSize);

        // Verify missing values remain default (or rather, verify they are handled if not present in JSON)
        // Since we wrote partial JSON, Gson might set missing fields to default Java values (false/0/null)
        // if the DTO initializes them. Let's check DTO.
        // CoreConfigDTO initializes fields with defaults. Gson overwrites only present fields.
        // So default fields should be preserved.
        assertTrue(CoreConfig.enableConquestSupport);
    }
}
