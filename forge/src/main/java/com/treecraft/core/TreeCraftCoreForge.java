package com.treecraft.core;

import com.treecraft.core.config.CoreConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

@Mod(Constants.MOD_ID)
public class TreeCraftCoreForge {

    public static final CommonConfig COMMON_CONFIG;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = commonSpecPair.getRight();
        COMMON_CONFIG = commonSpecPair.getLeft();
    }

    public TreeCraftCoreForge() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);

        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        TreeCraftCore.init();
    }

    @SubscribeEvent
    public void onModConfigEvent(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == COMMON_SPEC) {
            updateCommonConfig();
        }
    }

    private void updateCommonConfig() {
        CoreConfig.enableAutoDetection = COMMON_CONFIG.enableAutoDetection.get();
        CoreConfig.minConfidenceThreshold = COMMON_CONFIG.minConfidenceThreshold.get().floatValue();
        CoreConfig.maxTreeSize = COMMON_CONFIG.maxTreeSize.get();
        CoreConfig.enableConquestSupport = COMMON_CONFIG.enableConquestSupport.get();
        CoreConfig.enableDynamicTreesSupport = COMMON_CONFIG.enableDynamicTreesSupport.get();
        CoreConfig.enableVanillaDetection = COMMON_CONFIG.enableVanillaDetection.get();
        CoreConfig.detectionCacheSize = COMMON_CONFIG.detectionCacheSize.get();
        CoreConfig.asyncDetection = COMMON_CONFIG.asyncDetection.get();
        CoreConfig.maxDetectionThreads = COMMON_CONFIG.maxDetectionThreads.get();
        CoreConfig.debugMode = COMMON_CONFIG.debugMode.get();
        CoreConfig.logDetections = COMMON_CONFIG.logDetections.get();
    }

    public static class CommonConfig {
        public final ForgeConfigSpec.BooleanValue enableAutoDetection;
        public final ForgeConfigSpec.DoubleValue minConfidenceThreshold;
        public final ForgeConfigSpec.IntValue maxTreeSize;

        public final ForgeConfigSpec.BooleanValue enableConquestSupport;
        public final ForgeConfigSpec.BooleanValue enableDynamicTreesSupport;
        public final ForgeConfigSpec.BooleanValue enableVanillaDetection;

        public final ForgeConfigSpec.IntValue detectionCacheSize;
        public final ForgeConfigSpec.BooleanValue asyncDetection;
        public final ForgeConfigSpec.IntValue maxDetectionThreads;

        public final ForgeConfigSpec.BooleanValue debugMode;
        public final ForgeConfigSpec.BooleanValue logDetections;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            enableAutoDetection = builder.define("enableAutoDetection", true);
            minConfidenceThreshold = builder.defineInRange("minConfidenceThreshold", 0.5, 0.0, 1.0);
            maxTreeSize = builder.defineInRange("maxTreeSize", 10000, 1, 100000);
            builder.pop();

            builder.push("compatibility");
            enableConquestSupport = builder.define("enableConquestSupport", true);
            enableDynamicTreesSupport = builder.define("enableDynamicTreesSupport", true);
            enableVanillaDetection = builder.define("enableVanillaDetection", true);
            builder.pop();

            builder.push("performance");
            detectionCacheSize = builder.defineInRange("detectionCacheSize", 1000, 100, 10000);
            asyncDetection = builder.define("asyncDetection", true);
            maxDetectionThreads = builder.defineInRange("maxDetectionThreads", 2, 1, 8);
            builder.pop();

            builder.push("debug");
            debugMode = builder.define("debugMode", false);
            logDetections = builder.define("logDetections", false);
            builder.pop();
        }
    }
}