package com.treecraft.core.test.platform;

import com.treecraft.core.platform.services.IPlatformHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MockPlatformHelper implements IPlatformHelper {
    private static Path configDir;

    public static void setConfigDir(Path dir) {
        configDir = dir;
    }

    public static void reset() {
        configDir = null;
    }

    @Override
    public String getPlatformName() {
        return "Test";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return false;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return true;
    }

    @Override
    public Path getConfigDirectory() {
        if (configDir == null) {
            try {
                configDir = Files.createTempDirectory("treecraft_test_config");
                configDir.toFile().deleteOnExit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return configDir;
    }
}
