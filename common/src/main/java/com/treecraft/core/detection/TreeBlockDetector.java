package com.treecraft.core.detection;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.IDetectionHeuristic;
import com.treecraft.core.api.ITreeBlockDetector;
import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.detection.heuristics.ConnectionHeuristic;
import com.treecraft.core.detection.heuristics.MaterialHeuristic;
import com.treecraft.core.detection.heuristics.NameHeuristic;
import com.treecraft.core.detection.heuristics.PropertiesHeuristic;
import com.treecraft.core.detection.heuristics.TagHeuristic;
import com.treecraft.core.api.events.TreeDetectedEvent;
import com.treecraft.core.api.events.TreeCraftEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TreeBlockDetector implements ITreeBlockDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger("TreeBlockDetector");
    private static final TreeBlockDetector INSTANCE = new TreeBlockDetector();
    private final List<IDetectionHeuristic> heuristics = new ArrayList<>();
    private Cache<BlockState, DetectionResult> cache;

    private TreeBlockDetector() {
        // Register default heuristics
        registerHeuristic(new TagHeuristic());
        registerHeuristic(new MaterialHeuristic());
        registerHeuristic(new NameHeuristic());
        registerHeuristic(new PropertiesHeuristic());
        registerHeuristic(new ConnectionHeuristic());

        cache = CacheBuilder.newBuilder()
            .maximumSize(com.treecraft.core.config.CoreConfig.detectionCacheSize)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();
    }

    public static TreeBlockDetector getInstance() {
        return INSTANCE;
    }

    public static void initialize() {
        // Just to trigger class loading
        getInstance();
    }

    @Override
    public void registerHeuristic(IDetectionHeuristic heuristic) {
        heuristics.add(heuristic);
    }

    @Override
    public TreeComponentType detectBlockType(BlockState block) {
        return detect(block, null, null);
    }

    public TreeComponentType detect(BlockState block, Level level, BlockPos pos) {
        // Check registry first (manual override)
        if (com.treecraft.core.registry.TreeBlockRegistry.isTreeBlock(block.getBlock())) {
            return com.treecraft.core.registry.TreeBlockRegistry.getType(block.getBlock());
        }

        if (level == null) {
            DetectionResult cached = cache.getIfPresent(block);
            if (cached != null) {
                return cached.type;
            }
        }

        DetectionResult result = detectUncached(block, level, pos);

        if (level == null) {
            cache.put(block, result);
        } else if (pos != null && result.type != TreeComponentType.UNKNOWN) {
            TreeCraftEvents.post(new TreeDetectedEvent(block, pos, result.type, result.confidence));
        }

        return result.type;
    }

    @Override
    public TreeStructure detectTree(BlockPos pos, Level level) {
        return TreeStructureDetector.detectTree(pos, level);
    }

    private DetectionResult detectUncached(BlockState block, Level level, BlockPos pos) {
        Map<TreeComponentType, Float> scores = new EnumMap<>(TreeComponentType.class);

        for (IDetectionHeuristic heuristic : heuristics) {
            try {
                HeuristicResult result = heuristic.evaluate(block, level, pos);

                if (result.getType() != TreeComponentType.UNKNOWN) {
                    scores.merge(result.getType(), result.getConfidence(), Float::sum);
                }
            } catch (Exception e) {
                LOGGER.error("Error in heuristic evaluation", e);
            }
        }

        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .filter(e -> e.getValue() >= 0.5f)
            .map(e -> new DetectionResult(e.getKey(), e.getValue()))
            .orElse(new DetectionResult(TreeComponentType.UNKNOWN, 0.0f));
    }

    public static class DetectionResult {
        public final TreeComponentType type;
        public final float confidence;

        public DetectionResult(TreeComponentType type, float confidence) {
            this.type = type;
            this.confidence = confidence;
        }
    }

    public void resetForTest() {
        heuristics.clear();
        // Reset cache to default configuration
        this.cache = CacheBuilder.newBuilder()
            .maximumSize(com.treecraft.core.config.CoreConfig.detectionCacheSize)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();
    }

    // Visible for testing
    public void setCacheTickerForTest(com.google.common.base.Ticker ticker) {
        this.cache = CacheBuilder.newBuilder()
            .maximumSize(com.treecraft.core.config.CoreConfig.detectionCacheSize)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .ticker(ticker)
            .build();
    }
}