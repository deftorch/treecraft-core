# TreeCraft Core - Implementation Examples

## Complete Code Examples for Core Components

---

## 1. Main Mod Class

```java
package com.treecraft.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TreeCraftCore.MOD_ID)
public class TreeCraftCore {
    public static final String MOD_ID = "treecraft_core";
    public static final Logger LOGGER = LogManager.getLogger();
    
    private static TreeCraftCore instance;
    private TreeCraftAPI api;
    
    public TreeCraftCore() {
        instance = this;
        
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::setup);
        
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void setup(FMLCommonSetupEvent event) {
        LOGGER.info("TreeCraft Core initializing...");
        
        // Initialize core systems
        event.enqueueWork(() -> {
            // Load configuration
            CoreConfig.load();
            
            // Initialize detection system
            TreeBlockDetector.initialize();
            
            // Initialize registries
            TreeBlockRegistry.initialize();
            StyleRegistry.initialize();
            
            // Load mod adapters
            ModCompatibility.registerAdapters();
            ModCompatibility.loadCompatibility();
            
            // Load styles from config
            StyleLoader.loadStyles();
            
            // Initialize API
            api = new TreeCraftAPI();
            
            LOGGER.info("TreeCraft Core initialized successfully!");
        });
    }
    
    public static TreeCraftCore getInstance() {
        return instance;
    }
    
    public TreeCraftAPI getAPI() {
        return api;
    }
}
```

---

## 2. Tree Block Detector Implementation

```java
package com.treecraft.core.detection;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.config.CoreConfig;
import com.treecraft.core.detection.heuristics.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TreeBlockDetector {
    private static final List<IDetectionHeuristic> HEURISTICS = new ArrayList<>();
    private static Cache<BlockState, DetectionResult> cache;
    
    public static void initialize() {
        // Register default heuristics in order of reliability
        registerHeuristic(new MaterialHeuristic());      // Most reliable
        registerHeuristic(new PropertiesHeuristic());
        registerHeuristic(new NameHeuristic());
        registerHeuristic(new ConnectionHeuristic());    // Context-dependent
        registerHeuristic(new TagHeuristic());
        
        // Initialize cache
        cache = CacheBuilder.newBuilder()
            .maximumSize(CoreConfig.detectionCacheSize)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();
    }
    
    /**
     * Detect the component type of a block
     */
    public static TreeComponentType detect(BlockState block) {
        // Check cache first
        DetectionResult cached = cache.getIfPresent(block);
        if (cached != null) {
            return cached.type;
        }
        
        // Run detection
        DetectionResult result = detectUncached(block);
        
        // Cache result
        cache.put(block, result);
        
        return result.type;
    }
    
    /**
     * Detect with confidence score
     */
    public static DetectionResult detectWithConfidence(BlockState block) {
        DetectionResult cached = cache.getIfPresent(block);
        if (cached != null) {
            return cached;
        }
        
        DetectionResult result = detectUncached(block);
        cache.put(block, result);
        
        return result;
    }
    
    private static DetectionResult detectUncached(BlockState block) {
        Map<TreeComponentType, Float> scores = new EnumMap<>(TreeComponentType.class);
        
        // Evaluate all heuristics
        for (IDetectionHeuristic heuristic : HEURISTICS) {
            try {
                HeuristicResult result = heuristic.evaluate(block);
                
                if (result.getType() != TreeComponentType.UNKNOWN) {
                    scores.merge(
                        result.getType(), 
                        result.getConfidence(), 
                        Float::sum
                    );
                }
            } catch (Exception e) {
                TreeCraftCore.LOGGER.warn(
                    "Heuristic {} failed for block {}", 
                    heuristic.getClass().getSimpleName(),
                    block.getBlock().getRegistryName(),
                    e
                );
            }
        }
        
        // Find highest scoring type
        Optional<Map.Entry<TreeComponentType, Float>> best = scores.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue());
        
        if (best.isPresent() && best.get().getValue() >= CoreConfig.minConfidenceThreshold) {
            return new DetectionResult(
                best.get().getKey(),
                best.get().getValue()
            );
        }
        
        return new DetectionResult(TreeComponentType.UNKNOWN, 0.0f);
    }
    
    /**
     * Register a custom heuristic
     */
    public static void registerHeuristic(IDetectionHeuristic heuristic) {
        HEURISTICS.add(heuristic);
    }
    
    /**
     * Clear detection cache
     */
    public static void clearCache() {
        cache.invalidateAll();
    }
    
    /**
     * Detection result with confidence score
     */
    public static class DetectionResult {
        public final TreeComponentType type;
        public final float confidence;
        
        public DetectionResult(TreeComponentType type, float confidence) {
            this.type = type;
            this.confidence = confidence;
        }
    }
}
```

---

## 3. Tree Structure Detector

```java
package com.treecraft.core.detection;

import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.config.CoreConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class TreeStructureDetector {
    
    /**
     * Detect entire tree structure from a starting position
     */
    public static TreeStructure detectTree(BlockPos startPos, Level level) {
        TreeStructure tree = new TreeStructure(startPos);
        
        Queue<BlockPos> toProcess = new LinkedList<>();
        Set<BlockPos> processed = new HashSet<>();
        
        toProcess.add(startPos);
        
        int blocksProcessed = 0;
        int maxBlocks = CoreConfig.maxTreeSize;
        
        while (!toProcess.isEmpty() && blocksProcessed < maxBlocks) {
            BlockPos pos = toProcess.poll();
            
            if (processed.contains(pos)) {
                continue;
            }
            
            processed.add(pos);
            blocksProcessed++;
            
            BlockState blockState = level.getBlockState(pos);
            TreeComponentType type = TreeBlockDetector.detect(blockState);
            
            if (type != TreeComponentType.UNKNOWN) {
                tree.addComponent(pos, blockState, type);
                
                // Add connected blocks to queue
                addConnectedBlocks(pos, type, toProcess, processed);
            }
        }
        
        return tree;
    }
    
    private static void addConnectedBlocks(
        BlockPos pos, 
        TreeComponentType type,
        Queue<BlockPos> queue,
        Set<BlockPos> processed
    ) {
        // Different connection patterns for different components
        Direction[] directions = switch (type) {
            case TRUNK -> new Direction[]{
                Direction.UP, Direction.DOWN,
                Direction.NORTH, Direction.SOUTH,
                Direction.EAST, Direction.WEST
            };
            case BRANCH -> Direction.values(); // All directions
            case LEAVES -> Direction.values(); // All directions
            case ROOT -> new Direction[]{
                Direction.DOWN,
                Direction.NORTH, Direction.SOUTH,
                Direction.EAST, Direction.WEST
            };
            default -> new Direction[0];
        };
        
        for (Direction dir : directions) {
            BlockPos neighbor = pos.relative(dir);
            if (!processed.contains(neighbor)) {
                queue.add(neighbor);
            }
        }
    }
    
    /**
     * Find the base of a tree (lowest trunk block)
     */
    public static BlockPos findTreeBase(BlockPos startPos, Level level) {
        BlockPos current = startPos;
        BlockPos lowest = startPos;
        
        // Search downward for trunk blocks
        for (int i = 0; i < 256; i++) {
            BlockPos below = current.below();
            BlockState state = level.getBlockState(below);
            
            TreeComponentType type = TreeBlockDetector.detect(state);
            
            if (type == TreeComponentType.TRUNK || type == TreeComponentType.ROOT) {
                lowest = below;
                current = below;
            } else {
                break;
            }
        }
        
        return lowest;
    }
    
    /**
     * Validate if structure is a valid tree
     */
    public static boolean isValidTree(TreeStructure structure) {
        // Minimum requirements
        if (!structure.hasTrunk()) {
            return false;
        }
        
        if (!structure.hasLeaves()) {
            return false;
        }
        
        // Size check
        if (!structure.isReasonableSize()) {
            return false;
        }
        
        // Structure check - trunk should be continuous
        if (!hasContinuousTrunk(structure)) {
            return false;
        }
        
        // Leaves should be above/around trunk
        if (!leavesPositionedCorrectly(structure)) {
            return false;
        }
        
        return true;
    }
    
    private static boolean hasContinuousTrunk(TreeStructure structure) {
        Set<BlockPos> trunkBlocks = structure.getComponentsOfType(TreeComponentType.TRUNK);
        
        if (trunkBlocks.isEmpty()) {
            return false;
        }
        
        // Find lowest trunk block
        BlockPos lowest = trunkBlocks.stream()
            .min(Comparator.comparingInt(BlockPos::getY))
            .orElseThrow();
        
        // Check vertical continuity
        Set<BlockPos> reachable = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(lowest);
        reachable.add(lowest);
        
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            
            // Check adjacent trunk blocks
            for (Direction dir : Direction.values()) {
                BlockPos next = pos.relative(dir);
                if (trunkBlocks.contains(next) && !reachable.contains(next)) {
                    reachable.add(next);
                    queue.add(next);
                }
            }
        }
        
        // At least 80% of trunk should be reachable
        return reachable.size() >= trunkBlocks.size() * 0.8;
    }
    
    private static boolean leavesPositionedCorrectly(TreeStructure structure) {
        Set<BlockPos> leaves = structure.getComponentsOfType(TreeComponentType.LEAVES);
        Set<BlockPos> trunk = structure.getComponentsOfType(TreeComponentType.TRUNK);
        
        if (leaves.isEmpty() || trunk.isEmpty()) {
            return false;
        }
        
        // Get average trunk height
        int avgTrunkY = trunk.stream()
            .mapToInt(BlockPos::getY)
            .sum() / trunk.size();
        
        // Get average leaves height
        int avgLeavesY = leaves.stream()
            .mapToInt(BlockPos::getY)
            .sum() / leaves.size();
        
        // Leaves should generally be at or above trunk level
        return avgLeavesY >= avgTrunkY - 2;
    }
    
    /**
     * Estimate tree species from structure
     */
    public static String estimateSpecies(TreeStructure structure) {
        // Analyze structure characteristics
        int height = structure.getHeight();
        int width = structure.getWidth();
        float aspectRatio = (float) height / width;
        
        Set<BlockPos> trunk = structure.getComponentsOfType(TreeComponentType.TRUNK);
        Set<BlockPos> leaves = structure.getComponentsOfType(TreeComponentType.LEAVES);
        
        float leafDensity = (float) leaves.size() / structure.getTotalBlocks();
        
        // Simple classification based on characteristics
        if (aspectRatio > 3.0 && leafDensity < 0.4) {
            return "spruce_like";
        } else if (aspectRatio > 2.0 && leafDensity < 0.5) {
            return "pine_like";
        } else if (aspectRatio < 1.5 && leafDensity > 0.6) {
            return "oak_like";
        } else if (height < 8 && leafDensity > 0.5) {
            return "bush_like";
        } else {
            return "generic_tree";
        }
    }
}
```

---

## 4. Tree Structure Data Class

```java
package com.treecraft.core.detection;

import com.treecraft.core.api.TreeComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.*;

public class TreeStructure {
    private final BlockPos basePos;
    private final Map<TreeComponentType, Map<BlockPos, BlockState>> components;
    private AABB boundingBox;
    private final long detectedTime;
    
    public TreeStructure(BlockPos basePos) {
        this.basePos = basePos;
        this.components = new EnumMap<>(TreeComponentType.class);
        this.detectedTime = System.currentTimeMillis();
        updateBoundingBox();
    }
    
    /**
     * Add a component to the tree
     */
    public void addComponent(BlockPos pos, BlockState state, TreeComponentType type) {
        components.computeIfAbsent(type, k -> new HashMap<>())
                  .put(pos, state);
        updateBoundingBox();
    }
    
    /**
     * Get all positions of a specific component type
     */
    public Set<BlockPos> getComponentsOfType(TreeComponentType type) {
        Map<BlockPos, BlockState> typeMap = components.get(type);
        return typeMap != null ? typeMap.keySet() : Collections.emptySet();
    }
    
    /**
     * Get block state at position
     */
    public Optional<BlockState> getBlockAt(BlockPos pos) {
        for (Map<BlockPos, BlockState> typeMap : components.values()) {
            BlockState state = typeMap.get(pos);
            if (state != null) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
    
    /**
     * Check if tree has trunk components
     */
    public boolean hasTrunk() {
        return components.containsKey(TreeComponentType.TRUNK) &&
               !components.get(TreeComponentType.TRUNK).isEmpty();
    }
    
    /**
     * Check if tree has leaves
     */
    public boolean hasLeaves() {
        return components.containsKey(TreeComponentType.LEAVES) &&
               !components.get(TreeComponentType.LEAVES).isEmpty();
    }
    
    /**
     * Check if tree has branches
     */
    public boolean hasBranches() {
        return components.containsKey(TreeComponentType.BRANCH) &&
               !components.get(TreeComponentType.BRANCH).isEmpty();
    }
    
    /**
     * Check if tree has roots
     */
    public boolean hasRoots() {
        return components.containsKey(TreeComponentType.ROOT) &&
               !components.get(TreeComponentType.ROOT).isEmpty();
    }
    
    /**
     * Get total block count
     */
    public int getTotalBlocks() {
        return components.values().stream()
            .mapToInt(Map::size)
            .sum();
    }
    
    /**
     * Get component type distribution
     */
    public Map<TreeComponentType, Integer> getComponentDistribution() {
        Map<TreeComponentType, Integer> distribution = new EnumMap<>(TreeComponentType.class);
        components.forEach((type, blocks) -> 
            distribution.put(type, blocks.size())
        );
        return distribution;
    }
    
    /**
     * Check if size is reasonable
     */
    public boolean isReasonableSize() {
        int total = getTotalBlocks();
        return total >= 5 && total <= 10000;
    }
    
    /**
     * Get tree height
     */
    public int getHeight() {
        if (boundingBox == null) return 0;
        return (int) (boundingBox.maxY - boundingBox.minY);
    }
    
    /**
     * Get tree width (average of X and Z dimensions)
     */
    public int getWidth() {
        if (boundingBox == null) return 0;
        int xWidth = (int) (boundingBox.maxX - boundingBox.minX);
        int zWidth = (int) (boundingBox.maxZ - boundingBox.minZ);
        return (xWidth + zWidth) / 2;
    }
    
    /**
     * Get bounding box
     */
    public AABB getBoundingBox() {
        return boundingBox;
    }
    
    /**
     * Get base position
     */
    public BlockPos getBasePos() {
        return basePos;
    }
    
    /**
     * Get detection timestamp
     */
    public long getDetectedTime() {
        return detectedTime;
    }
    
    /**
     * Update bounding box based on current components
     */
    private void updateBoundingBox() {
        if (components.isEmpty()) {
            boundingBox = null;
            return;
        }
        
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        
        for (Map<BlockPos, BlockState> typeMap : components.values()) {
            for (BlockPos pos : typeMap.keySet()) {
                minX = Math.min(minX, pos.getX());
                minY = Math.min(minY, pos.getY());
                minZ = Math.min(minZ, pos.getZ());
                maxX = Math.max(maxX, pos.getX());
                maxY = Math.max(maxY, pos.getY());
                maxZ = Math.max(maxZ, pos.getZ());
            }
        }
        
        boundingBox = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
    }
    
    /**
     * Get all block positions in tree
     */
    public Set<BlockPos> getAllPositions() {
        Set<BlockPos> allPositions = new HashSet<>();
        components.values().forEach(map -> allPositions.addAll(map.keySet()));
        return allPositions;
    }
    
    /**
     * Check if position is part of tree
     */
    public boolean containsPosition(BlockPos pos) {
        return components.values().stream()
            .anyMatch(map -> map.containsKey(pos));
    }
    
    /**
     * Get component type at position
     */
    public Optional<TreeComponentType> getComponentTypeAt(BlockPos pos) {
        for (Map.Entry<TreeComponentType, Map<BlockPos, BlockState>> entry : components.entrySet()) {
            if (entry.getValue().containsKey(pos)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }
    
    @Override
    public String toString() {
        return String.format(
            "TreeStructure{base=%s, blocks=%d, height=%d, width=%d, components=%s}",
            basePos,
            getTotalBlocks(),
            getHeight(),
            getWidth(),
            getComponentDistribution()
        );
    }
}
```

---

## 5. Style Registry Implementation

```java
package com.treecraft.core.registry;

import com.treecraft.core.TreeCraftCore;
import com.treecraft.core.api.BlockStyle;
import com.treecraft.core.events.StyleRegisteredEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StyleRegistry {
    private static final Map<ResourceLocation, BlockStyle> STYLES = new ConcurrentHashMap<>();
    private static final Map<String, Set<ResourceLocation>> STYLES_BY_MOD = new ConcurrentHashMap<>();
    
    public static void initialize() {
        TreeCraftCore.LOGGER.info("Initializing Style Registry...");
    }
    
    /**
     * Register a new block style
     */
    public static void register(BlockStyle style) {
        ResourceLocation id = style.getId();
        
        if (STYLES.containsKey(id)) {
            TreeCraftCore.LOGGER.warn("Style {} already registered, overwriting", id);
        }
        
        STYLES.put(id, style);
        
        // Index by mod
        String modId = id.getNamespace();
        STYLES_BY_MOD.computeIfAbsent(modId, k -> new HashSet<>()).add(id);
        
        // Fire event
        MinecraftForge.EVENT_BUS.post(new StyleRegisteredEvent(style));
        
        TreeCraftCore.LOGGER.debug("Registered style: {}", id);
    }
    
    /**
     * Get style by ID
     */
    public static Optional<BlockStyle> getStyle(ResourceLocation id) {
        return Optional.ofNullable(STYLES.get(id));
    }
    
    /**
     * Get all registered styles
     */
    public static Collection<BlockStyle> getAllStyles() {
        return Collections.unmodifiableCollection(STYLES.values());
    }
    
    /**
     * Get styles from a specific mod
     */
    public static List<BlockStyle> getStylesByMod(String modId) {
        Set<ResourceLocation> styleIds = STYLES_BY_MOD.get(modId);
        if (styleIds == null) {
            return Collections.emptyList();
        }
        
        return styleIds.stream()
            .map(STYLES::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    /**
     * Find styles matching a predicate
     */
    public static List<BlockStyle> findStyles(Predicate<BlockStyle> predicate) {
        return STYLES.values().stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    /**
     * Check if style exists
     */
    public static boolean hasStyle(ResourceLocation id) {
        return STYLES.containsKey(id);
    }
    
    /**
     * Get number of registered styles
     */
    public static int getStyleCount() {
        return STYLES.size();
    }
    
    /**
     * Clear all registered styles
     */
    public static void clear() {
        STYLES.clear();
        STYLES_BY_MOD.clear();
        TreeCraftCore.LOGGER.info("Style registry cleared");
    }
    
    /**
     * Get all style IDs
     */
    public static Set<ResourceLocation> getAllStyleIds() {
        return Collections.unmodifiableSet(STYLES.keySet());
    }
}
```

---

## 6. Configuration System

```java
package com.treecraft.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class CoreConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    // Detection Settings
    public static final ForgeConfigSpec.BooleanValue ENABLE_AUTO_DETECTION;
    public static final ForgeConfigSpec.DoubleValue MIN_CONFIDENCE_THRESHOLD;
    public static final ForgeConfigSpec.IntValue MAX_TREE_SIZE;
    
    // Compatibility Settings
    public static final ForgeConfigSpec.BooleanValue ENABLE_CONQUEST_SUPPORT;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DYNAMIC_TREES_SUPPORT;
    public static final ForgeConfigSpec.BooleanValue ENABLE_VANILLA_DETECTION;
    
    // Performance Settings
    public static final ForgeConfigSpec.IntValue DETECTION_CACHE_SIZE;
    public static final ForgeConfigSpec.BooleanValue ASYNC_DETECTION;
    public static final ForgeConfigSpec.IntValue MAX_DETECTION_THREADS;
    
    // Debug Settings
    public static final ForgeConfigSpec.BooleanValue DEBUG_MODE;
    public static final ForgeConfigSpec.BooleanValue LOG_DETECTIONS;
    
    static {
        BUILDER.comment("TreeCraft Core Configuration").push("core");
        
        // Detection
        BUILDER.comment("Block Detection Settings").push("detection");
        ENABLE_AUTO_DETECTION = BUILDER
            .comment("Enable automatic tree block detection")
            .define("enableAutoDetection", true);
        
        MIN_CONFIDENCE_THRESHOLD = BUILDER
            .comment("Minimum confidence threshold for detection (0.0-1.0)")
            .defineInRange("minConfidenceThreshold", 0.5, 0.0, 1.0);
        
        MAX_TREE_SIZE = BUILDER
            .comment("Maximum number of blocks in a tree structure")
            .defineInRange("maxTreeSize", 10000, 1, 100000);
        BUILDER.pop();
        
        // Compatibility
        BUILDER.comment("Mod Compatibility Settings").push("compatibility");
        ENABLE_CONQUEST_SUPPORT = BUILDER
            .comment("Enable Conquest Reforged compatibility")
            .define("enableConquestSupport", true);
        
        ENABLE_DYNAMIC_TREES_SUPPORT = BUILDER
            .comment("Enable Dynamic Trees compatibility")
            .define("enableDynamicTreesSupport", true);
        
        ENABLE_VANILLA_DETECTION = BUILDER
            .comment("Enable vanilla tree detection")
            .define("enableVanillaDetection", true);
        BUILDER.pop();
        
        // Performance
        BUILDER.comment("Performance Settings").push("performance");
        DETECTION_CACHE_SIZE = BUILDER
            .comment("Size of detection result cache")
            .defineInRange("detectionCacheSize", 1000, 100, 10000);
        
        ASYNC_DETECTION = BUILDER
            .comment("Enable asynchronous tree detection")
            .define("asyncDetection", true);
        
        MAX_DETECTION_THREADS = BUILDER
            .comment("Maximum threads for async detection")
            .defineInRange("maxDetectionThreads", 2, 1, 8);
        BUILDER.pop();
        
        // Debug
        BUILDER.comment("Debug Settings").push("debug");
        DEBUG_MODE = BUILDER
            .comment("Enable debug mode")
            .define("debugMode", false);
        
        LOG_DETECTIONS = BUILDER
            .comment("Log all block detections")
            .define("logDetections", false);
        BUILDER.pop();
        
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
    
    public static void load() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
    
    // Convenience getters
    public static boolean enableAutoDetection = true;
    public static float minConfidenceThreshold = 0.5f;
    public static int maxTreeSize = 10000;
    public static boolean enableConquestSupport = true;
    public static boolean enableDynamicTreesSupport = true;
    public static int detectionCacheSize = 1000;
    public static boolean asyncDetection = true;
    public static boolean debugMode = false;
}
```

---

## 7. Example Usage from Other Mods

```java
// Example: Using TreeCraft Core API in another mod

public class MyTreeMod {
    
    public void onModInit() {
        // Get TreeCraft API
        TreeCraftAPI api = TreeCraftCore.getInstance().getAPI();
        
        // Example 1: Detect a block type
        BlockState oakLog = Blocks.OAK_LOG.defaultBlockState();
        TreeComponentType type = api.getDetector().detectBlockType(oakLog);
        System.out.println("Oak log detected as: " + type);
        
        // Example 2: Detect entire tree structure
        Level level = /* get level */;
        BlockPos pos = new BlockPos(0, 64, 0);
        TreeStructure tree = api.getDetector().detectTree(pos, level);
        System.out.println("Detected tree: " + tree);
        
        // Example 3: Register custom style
        BlockStyle myStyle = new BlockStyle.Builder()
            .id(new ResourceLocation("mymod", "custom_oak"))
            .displayName("My Custom Oak")
            .trunk(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG)
            .leaves(Blocks.OAK_LEAVES)
            .build();
        
        api.getStyleRegistry().registerStyle(myStyle);
        
        // Example 4: Listen for detection events
        MinecraftForge.EVENT_BUS.addListener(this::onTreeDetected);
    }
    
    private void onTreeDetected(TreeStructureDetectedEvent event) {
        TreeStructure tree = event.getStructure();
        System.out.println("Tree detected with " + tree.getTotalBlocks() + " blocks");
    }
}
```

This implementation provides a solid foundation for TreeCraft Core!
