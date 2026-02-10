package com.treecraft.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.treecraft.core.config.CoreConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class TreeCraftCoreFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOG.info("Initializing TreeCraft Core (Fabric)...");

        loadConfig();

        TreeCraftCore.init();
    }

    private void loadConfig() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configFile = configDir.resolve("treecraft_core.json");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (configFile.toFile().exists()) {
            try (FileReader reader = new FileReader(configFile.toFile())) {
                ConfigData data = gson.fromJson(reader, ConfigData.class);
                if (data != null) {
                    applyConfig(data);
                }
            } catch (IOException e) {
                Constants.LOG.error("Failed to load config", e);
            }
        } else {
            // Create default config
            ConfigData data = new ConfigData();
            try (FileWriter writer = new FileWriter(configFile.toFile())) {
                gson.toJson(data, writer);
            } catch (IOException e) {
                Constants.LOG.error("Failed to save default config", e);
            }
        }
    }

    private void applyConfig(ConfigData data) {
        CoreConfig.enableAutoDetection = data.enableAutoDetection;
        CoreConfig.minConfidenceThreshold = data.minConfidenceThreshold;
        CoreConfig.maxTreeSize = data.maxTreeSize;
        CoreConfig.enableConquestSupport = data.enableConquestSupport;
        CoreConfig.enableDynamicTreesSupport = data.enableDynamicTreesSupport;
        CoreConfig.enableVanillaDetection = data.enableVanillaDetection;
        CoreConfig.detectionCacheSize = data.detectionCacheSize;
        CoreConfig.asyncDetection = data.asyncDetection;
        CoreConfig.maxDetectionThreads = data.maxDetectionThreads;
        CoreConfig.debugMode = data.debugMode;
        CoreConfig.logDetections = data.logDetections;
    }

    private static class ConfigData {
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