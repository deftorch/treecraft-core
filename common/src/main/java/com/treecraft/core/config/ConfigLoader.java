package com.treecraft.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.treecraft.core.Constants;
import com.treecraft.core.platform.Services;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        Path configDir = Services.PLATFORM.getConfigDirectory();
        if (configDir == null) {
            Constants.LOG.error("Config directory is null, skipping config load");
            return;
        }

        Path configFile = configDir.resolve("treecraft/core.json");

        if (Files.exists(configFile)) {
            loadConfig(configFile);
        } else {
            saveDefaultConfig(configFile);
        }
    }

    private static void loadConfig(Path file) {
        try (Reader reader = Files.newBufferedReader(file)) {
            CoreConfigDTO dto = GSON.fromJson(reader, CoreConfigDTO.class);
            if (dto != null) {
                applyConfig(dto);
            }
        } catch (IOException e) {
            Constants.LOG.error("Failed to load config: " + file, e);
        }
    }

    private static void saveDefaultConfig(Path file) {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(file)) {
                CoreConfigDTO dto = new CoreConfigDTO();
                // Since DTO has default values initialized, this saves defaults
                GSON.toJson(dto, writer);
            }
        } catch (IOException e) {
            Constants.LOG.error("Failed to save default config: " + file, e);
        }
    }

    private static void applyConfig(CoreConfigDTO dto) {
        CoreConfig.enableAutoDetection = dto.enableAutoDetection;
        CoreConfig.minConfidenceThreshold = dto.minConfidenceThreshold;
        CoreConfig.maxTreeSize = dto.maxTreeSize;
        CoreConfig.enableConquestSupport = dto.enableConquestSupport;
        CoreConfig.enableDynamicTreesSupport = dto.enableDynamicTreesSupport;
        CoreConfig.enableVanillaDetection = dto.enableVanillaDetection;
        CoreConfig.detectionCacheSize = dto.detectionCacheSize;
        CoreConfig.asyncDetection = dto.asyncDetection;
        CoreConfig.maxDetectionThreads = dto.maxDetectionThreads;
        CoreConfig.debugMode = dto.debugMode;
        CoreConfig.logDetections = dto.logDetections;
    }

    private static class CoreConfigDTO {
        boolean enableAutoDetection = true;
        float minConfidenceThreshold = 0.5f;
        int maxTreeSize = 10000;

        boolean enableConquestSupport = true;
        boolean enableDynamicTreesSupport = true;
        boolean enableVanillaDetection = true;

        int detectionCacheSize = 1000;
        boolean asyncDetection = true;
        int maxDetectionThreads = 2;

        boolean debugMode = false;
        boolean logDetections = false;
    }
}
