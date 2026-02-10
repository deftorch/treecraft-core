# 🌲 TreeCraft Core - Core Architecture Design Guide

## 📋 Table of Contents
1. [Overview](#overview)
2. [Core Architecture](#core-architecture)
3. [Block Detection System](#block-detection-system)
4. [Style Registry System](#style-registry-system)
5. [Mod Compatibility Layer](#mod-compatibility-layer)
6. [API Design](#api-design)
7. [Data Structures](#data-structures)
8. [Implementation Roadmap](#implementation-roadmap)

---

## 1. Overview

### Purpose
TreeCraft Core adalah library dasar yang mendeteksi dan mengkategorikan tree blocks dari mod manapun, menyediakan foundation untuk semua modul TreeCraft lainnya.

### Key Requirements
- ✅ **Universal Detection**: Auto-detect tree blocks dari ANY mod
- ✅ **Smart Categorization**: Klasifikasi otomatis (trunk, branch, leaves, roots)
- ✅ **Lightweight**: Target <100KB, minimal dependencies
- ✅ **Extensible**: Public API untuk mod integration
- ✅ **Compatible**: Support Conquest Reforged, Dynamic Trees, vanilla

---

## 2. Core Architecture

### High-Level Structure

```
TreeCraft Core
├── Detection Engine
│   ├── Block Analyzer
│   ├── Pattern Matcher
│   └── ML-based Classifier (optional)
│
├── Registry System
│   ├── Block Registry
│   ├── Style Registry
│   └── Species Registry
│
├── Compatibility Layer
│   ├── Mod Adapters
│   ├── Block Mapping
│   └── API Bridges
│
└── API Layer
    ├── Public API
    ├── Events System
    └── Data Providers
```

### Package Structure

```java
com.treecraft.core/
├── api/                          // Public API
│   ├── ITreeBlockDetector.java
│   ├── IStyleRegistry.java
│   ├── ITreeComponent.java
│   └── events/
│       ├── TreeDetectedEvent.java
│       └── StyleRegisteredEvent.java
│
├── detection/                    // Block Detection
│   ├── BlockAnalyzer.java
│   ├── TreeStructureDetector.java
│   ├── PatternMatcher.java
│   └── heuristics/
│       ├── NameHeuristic.java
│       ├── ConnectionHeuristic.java
│       └── MaterialHeuristic.java
│
├── registry/                     // Registry System
│   ├── TreeBlockRegistry.java
│   ├── StyleRegistry.java
│   ├── SpeciesRegistry.java
│   └── data/
│       ├── TreeComponent.java
│       ├── BlockStyle.java
│       └── TreeSpecies.java
│
├── compatibility/                // Mod Compatibility
│   ├── ModAdapter.java
│   ├── ConquestAdapter.java
│   ├── DynamicTreesAdapter.java
│   └── VanillaAdapter.java
│
├── config/                       // Configuration
│   ├── CoreConfig.java
│   └── DetectionConfig.java
│
└── util/                         // Utilities
    ├── BlockUtils.java
    ├── NBTUtils.java
    └── GeometryUtils.java
```

---

## 3. Block Detection System

### 3.1 Detection Pipeline

```
Block Input → Multiple Heuristics → Confidence Scoring → Classification
```

### 3.2 Multi-Heuristic Detection

```java
public class TreeBlockDetector {
    private List<IDetectionHeuristic> heuristics;
    
    public TreeComponentType detect(BlockState block) {
        Map<TreeComponentType, Float> scores = new HashMap<>();
        
        // Evaluate all heuristics
        for (IDetectionHeuristic heuristic : heuristics) {
            HeuristicResult result = heuristic.evaluate(block);
            scores.merge(result.type, result.confidence, Float::sum);
        }
        
        // Return highest confidence type
        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(TreeComponentType.UNKNOWN);
    }
}
```

### 3.3 Detection Heuristics

#### A. Name-Based Heuristic
```java
public class NameHeuristic implements IDetectionHeuristic {
    private static final Map<Pattern, TreeComponentType> PATTERNS = Map.of(
        Pattern.compile(".*log.*|.*trunk.*|.*wood.*"), TreeComponentType.TRUNK,
        Pattern.compile(".*branch.*|.*twig.*"), TreeComponentType.BRANCH,
        Pattern.compile(".*leaves.*|.*foliage.*"), TreeComponentType.LEAVES,
        Pattern.compile(".*root.*"), TreeComponentType.ROOT
    );
    
    @Override
    public HeuristicResult evaluate(BlockState block) {
        String id = block.getBlock().getRegistryName().toString().toLowerCase();
        
        for (Map.Entry<Pattern, TreeComponentType> entry : PATTERNS.entrySet()) {
            if (entry.getKey().matcher(id).matches()) {
                return new HeuristicResult(entry.getValue(), 0.8f);
            }
        }
        
        return new HeuristicResult(TreeComponentType.UNKNOWN, 0.0f);
    }
}
```

#### B. Material-Based Heuristic
```java
public class MaterialHeuristic implements IDetectionHeuristic {
    @Override
    public HeuristicResult evaluate(BlockState block) {
        Material material = block.getMaterial();
        
        if (material == Material.WOOD) {
            // Check hardness to distinguish trunk from planks
            float hardness = block.getDestroySpeed(null, null);
            if (hardness > 1.5f) {
                return new HeuristicResult(TreeComponentType.TRUNK, 0.7f);
            }
        }
        
        if (material == Material.LEAVES) {
            return new HeuristicResult(TreeComponentType.LEAVES, 0.9f);
        }
        
        return new HeuristicResult(TreeComponentType.UNKNOWN, 0.0f);
    }
}
```

#### C. Connection-Based Heuristic
```java
public class ConnectionHeuristic implements IDetectionHeuristic {
    @Override
    public HeuristicResult evaluate(BlockState block) {
        // Analyze neighboring blocks
        // Trunks typically connect vertically
        // Branches connect in various directions
        // Leaves cluster around branches
        
        BlockPos pos = /* current position */;
        int verticalConnections = countVerticalConnections(pos);
        int horizontalConnections = countHorizontalConnections(pos);
        
        if (verticalConnections > horizontalConnections) {
            return new HeuristicResult(TreeComponentType.TRUNK, 0.6f);
        } else if (horizontalConnections > 0) {
            return new HeuristicResult(TreeComponentType.BRANCH, 0.5f);
        }
        
        return new HeuristicResult(TreeComponentType.UNKNOWN, 0.0f);
    }
}
```

#### D. NBT/Properties Heuristic
```java
public class PropertiesHeuristic implements IDetectionHeuristic {
    @Override
    public HeuristicResult evaluate(BlockState block) {
        // Check block properties
        if (block.hasProperty(BlockStateProperties.AXIS)) {
            // Log-like blocks have axis property
            return new HeuristicResult(TreeComponentType.TRUNK, 0.7f);
        }
        
        if (block.hasProperty(BlockStateProperties.DISTANCE)) {
            // Leaves have distance property in vanilla
            return new HeuristicResult(TreeComponentType.LEAVES, 0.8f);
        }
        
        return new HeuristicResult(TreeComponentType.UNKNOWN, 0.0f);
    }
}
```

### 3.4 Tree Structure Detection

```java
public class TreeStructureDetector {
    /**
     * Detects entire tree structure from a starting block
     */
    public TreeStructure detectTree(BlockPos startPos, Level level) {
        TreeStructure tree = new TreeStructure();
        Queue<BlockPos> toProcess = new LinkedList<>();
        Set<BlockPos> processed = new HashSet<>();
        
        toProcess.add(startPos);
        
        while (!toProcess.isEmpty()) {
            BlockPos pos = toProcess.poll();
            if (processed.contains(pos)) continue;
            
            BlockState block = level.getBlockState(pos);
            TreeComponentType type = TreeBlockDetector.detect(block);
            
            if (type != TreeComponentType.UNKNOWN) {
                tree.addComponent(pos, block, type);
                processed.add(pos);
                
                // Add connected blocks
                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = pos.relative(dir);
                    if (!processed.contains(neighbor)) {
                        toProcess.add(neighbor);
                    }
                }
            }
        }
        
        return tree;
    }
    
    /**
     * Validates if detected structure is actually a tree
     */
    public boolean isValidTree(TreeStructure structure) {
        // Validation rules:
        // 1. Has trunk components
        // 2. Trunk connects from ground upward
        // 3. Has leaves above/around trunk
        // 4. Reasonable size (not too big/small)
        
        return structure.hasTrunk() 
            && structure.hasContinuousVerticalTrunk()
            && structure.hasLeaves()
            && structure.isReasonableSize();
    }
}
```

---

## 4. Style Registry System

### 4.1 Block Style Definition

```java
public class BlockStyle {
    private final ResourceLocation id;
    private final String displayName;
    private final Map<TreeComponentType, List<BlockState>> blockPalette;
    private final StyleMetadata metadata;
    
    public static class Builder {
        public Builder trunk(Block... blocks);
        public Builder branch(Block... blocks);
        public Builder leaves(Block... blocks);
        public Builder roots(Block... blocks);
        public Builder metadata(StyleMetadata meta);
        public BlockStyle build();
    }
}
```

### 4.2 Style Registry

```java
public class StyleRegistry {
    private static final Map<ResourceLocation, BlockStyle> STYLES = new HashMap<>();
    
    /**
     * Register a new style
     */
    public static void register(BlockStyle style) {
        STYLES.put(style.getId(), style);
        MinecraftForge.EVENT_BUS.post(new StyleRegisteredEvent(style));
    }
    
    /**
     * Get style by ID
     */
    public static Optional<BlockStyle> getStyle(ResourceLocation id) {
        return Optional.ofNullable(STYLES.get(id));
    }
    
    /**
     * Auto-create style from detected blocks
     */
    public static BlockStyle createStyleFromTree(TreeStructure tree) {
        BlockStyle.Builder builder = new BlockStyle.Builder();
        
        // Group blocks by component type
        tree.getComponents().forEach((type, blocks) -> {
            switch (type) {
                case TRUNK -> builder.trunk(blocks.toArray(new Block[0]));
                case BRANCH -> builder.branch(blocks.toArray(new Block[0]));
                case LEAVES -> builder.leaves(blocks.toArray(new Block[0]));
                case ROOT -> builder.roots(blocks.toArray(new Block[0]));
            }
        });
        
        return builder.build();
    }
}
```

### 4.3 JSON-Based Style Configuration

```json
{
  "style": {
    "id": "treecraft:oak_style_conquest",
    "display_name": "Conquest Reforged Oak",
    "source_mod": "conquest_reforged",
    "palette": {
      "trunk": [
        "conquest:oak_log_small",
        "conquest:oak_log_medium",
        "conquest:oak_log_large"
      ],
      "branch": [
        "conquest:oak_branch_straight",
        "conquest:oak_branch_angled",
        "conquest:oak_branch_curved"
      ],
      "leaves": [
        "conquest:oak_leaves_full",
        "conquest:oak_leaves_partial"
      ],
      "roots": [
        "conquest:oak_root_exposed",
        "conquest:oak_root_buried"
      ]
    },
    "metadata": {
      "color_scheme": "warm_brown",
      "texture_style": "detailed",
      "seasonal_variants": true
    }
  }
}
```

### 4.4 Style Loader

```java
public class StyleLoader {
    public static void loadStyles() {
        // Load from config directory
        Path stylesDir = FMLPaths.CONFIGDIR.get().resolve("treecraft/styles");
        
        try (Stream<Path> paths = Files.walk(stylesDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".json"))
                 .forEach(StyleLoader::loadStyleFile);
        } catch (IOException e) {
            TreeCraft.LOGGER.error("Failed to load styles", e);
        }
    }
    
    private static void loadStyleFile(Path file) {
        try {
            String json = Files.readString(file);
            BlockStyle style = parseStyle(json);
            StyleRegistry.register(style);
        } catch (Exception e) {
            TreeCraft.LOGGER.error("Failed to parse style: " + file, e);
        }
    }
}
```

---

## 5. Mod Compatibility Layer

### 5.1 Adapter Pattern

```java
public interface ModAdapter {
    /**
     * Get mod ID this adapter supports
     */
    String getModId();
    
    /**
     * Check if mod is loaded
     */
    boolean isModLoaded();
    
    /**
     * Register blocks from this mod
     */
    void registerBlocks();
    
    /**
     * Create styles from this mod's blocks
     */
    List<BlockStyle> createStyles();
}
```

### 5.2 Conquest Reforged Adapter

```java
public class ConquestAdapter implements ModAdapter {
    private static final String MOD_ID = "conquest";
    
    @Override
    public void registerBlocks() {
        // Conquest has 15,000+ blocks, use filtering
        ForgeRegistries.BLOCKS.getEntries().stream()
            .filter(entry -> entry.getKey().location().getNamespace().equals(MOD_ID))
            .map(Map.Entry::getValue)
            .filter(this::isTreeBlock)
            .forEach(this::registerTreeBlock);
    }
    
    private boolean isTreeBlock(Block block) {
        String path = block.getRegistryName().getPath();
        return path.contains("log") 
            || path.contains("wood")
            || path.contains("branch")
            || path.contains("leaves")
            || path.contains("root");
    }
    
    private void registerTreeBlock(Block block) {
        TreeComponentType type = detectConquestBlockType(block);
        TreeBlockRegistry.register(block, type);
    }
    
    @Override
    public List<BlockStyle> createStyles() {
        // Group Conquest blocks by wood type
        Map<String, List<Block>> blocksByWoodType = new HashMap<>();
        
        TreeBlockRegistry.getRegisteredBlocks().forEach((block, type) -> {
            if (block.getRegistryName().getNamespace().equals(MOD_ID)) {
                String woodType = extractWoodType(block);
                blocksByWoodType.computeIfAbsent(woodType, k -> new ArrayList<>())
                                .add(block);
            }
        });
        
        // Create style for each wood type
        return blocksByWoodType.entrySet().stream()
            .map(entry -> createStyleForWoodType(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }
}
```

### 5.3 Dynamic Trees Adapter

```java
public class DynamicTreesAdapter implements ModAdapter {
    private static final String MOD_ID = "dynamictrees";
    
    @Override
    public void registerBlocks() {
        // Dynamic Trees has special blocks that grow
        // Need to detect both static and dynamic variants
        
        // Register branch blocks
        ForgeRegistries.BLOCKS.getEntries().stream()
            .filter(e -> e.getKey().location().getNamespace().equals(MOD_ID))
            .filter(e -> e.getValue() instanceof BranchBlock)
            .forEach(e -> TreeBlockRegistry.register(
                e.getValue(), 
                TreeComponentType.BRANCH
            ));
        
        // Register dynamic leaves
        ForgeRegistries.BLOCKS.getEntries().stream()
            .filter(e -> e.getKey().location().getNamespace().equals(MOD_ID))
            .filter(e -> e.getValue() instanceof DynamicLeavesBlock)
            .forEach(e -> TreeBlockRegistry.register(
                e.getValue(), 
                TreeComponentType.LEAVES
            ));
    }
}
```

### 5.4 Vanilla Adapter

```java
public class VanillaAdapter implements ModAdapter {
    @Override
    public void registerBlocks() {
        // Register all vanilla log variants
        registerBlock(Blocks.OAK_LOG, TreeComponentType.TRUNK);
        registerBlock(Blocks.SPRUCE_LOG, TreeComponentType.TRUNK);
        registerBlock(Blocks.BIRCH_LOG, TreeComponentType.TRUNK);
        // ... etc
        
        // Register all vanilla leaves
        registerBlock(Blocks.OAK_LEAVES, TreeComponentType.LEAVES);
        registerBlock(Blocks.SPRUCE_LEAVES, TreeComponentType.LEAVES);
        // ... etc
    }
    
    @Override
    public List<BlockStyle> createStyles() {
        List<BlockStyle> styles = new ArrayList<>();
        
        // Oak style
        styles.add(new BlockStyle.Builder()
            .id(new ResourceLocation("treecraft", "vanilla_oak"))
            .trunk(Blocks.OAK_LOG)
            .leaves(Blocks.OAK_LEAVES)
            .build());
        
        // Spruce style
        styles.add(new BlockStyle.Builder()
            .id(new ResourceLocation("treecraft", "vanilla_spruce"))
            .trunk(Blocks.SPRUCE_LOG)
            .leaves(Blocks.SPRUCE_LEAVES)
            .build());
        
        // ... etc for all vanilla wood types
        
        return styles;
    }
}
```

---

## 6. API Design

### 6.1 Public API Interfaces

```java
/**
 * Main API entry point
 */
public interface ITreeCraftAPI {
    /**
     * Get tree block detector
     */
    ITreeBlockDetector getDetector();
    
    /**
     * Get style registry
     */
    IStyleRegistry getStyleRegistry();
    
    /**
     * Get compatibility manager
     */
    ICompatibilityManager getCompatibilityManager();
}

/**
 * Block detection API
 */
public interface ITreeBlockDetector {
    /**
     * Detect component type of a block
     */
    TreeComponentType detectBlockType(BlockState block);
    
    /**
     * Detect entire tree structure
     */
    TreeStructure detectTree(BlockPos pos, Level level);
    
    /**
     * Register custom detection heuristic
     */
    void registerHeuristic(IDetectionHeuristic heuristic);
}

/**
 * Style registry API
 */
public interface IStyleRegistry {
    /**
     * Register a new style
     */
    void registerStyle(BlockStyle style);
    
    /**
     * Get style by ID
     */
    Optional<BlockStyle> getStyle(ResourceLocation id);
    
    /**
     * Get all registered styles
     */
    Collection<BlockStyle> getAllStyles();
    
    /**
     * Find styles matching criteria
     */
    List<BlockStyle> findStyles(Predicate<BlockStyle> predicate);
}
```

### 6.2 Event System

```java
/**
 * Fired when a tree block is detected
 */
public class TreeBlockDetectedEvent extends Event {
    private final BlockState block;
    private final BlockPos pos;
    private TreeComponentType detectedType;
    
    // Allow other mods to override detection
    public void setDetectedType(TreeComponentType type) {
        this.detectedType = type;
    }
}

/**
 * Fired when a tree structure is detected
 */
public class TreeStructureDetectedEvent extends Event {
    private final TreeStructure structure;
    private final Level level;
    
    // Allow modification before registration
    public TreeStructure getStructure() {
        return structure;
    }
}

/**
 * Fired when a style is registered
 */
public class StyleRegisteredEvent extends Event {
    private final BlockStyle style;
    
    public BlockStyle getStyle() {
        return style;
    }
}
```

### 6.3 Data Provider API

```java
/**
 * Provides tree data to other systems
 */
public interface ITreeDataProvider {
    /**
     * Get all blocks of a specific component type
     */
    List<Block> getBlocksOfType(TreeComponentType type);
    
    /**
     * Get compatible blocks for a style
     */
    List<Block> getStyleBlocks(ResourceLocation styleId, TreeComponentType type);
    
    /**
     * Check if block is part of any tree
     */
    boolean isTreeBlock(Block block);
    
    /**
     * Get metadata for a tree block
     */
    Optional<TreeBlockMetadata> getMetadata(Block block);
}
```

---

## 7. Data Structures

### 7.1 Core Classes

```java
/**
 * Represents a tree component type
 */
public enum TreeComponentType {
    TRUNK,
    BRANCH,
    LEAVES,
    ROOT,
    UNKNOWN;
}

/**
 * Represents a detected tree structure
 */
public class TreeStructure {
    private final Map<TreeComponentType, Set<BlockPos>> components;
    private final BoundingBox bounds;
    private final BlockPos basePos;
    
    public void addComponent(BlockPos pos, BlockState block, TreeComponentType type) {
        components.computeIfAbsent(type, k -> new HashSet<>()).add(pos);
    }
    
    public Set<BlockPos> getComponentsOfType(TreeComponentType type) {
        return components.getOrDefault(type, Collections.emptySet());
    }
    
    public boolean hasTrunk() {
        return !components.getOrDefault(TreeComponentType.TRUNK, Collections.emptySet()).isEmpty();
    }
    
    public int getTotalBlocks() {
        return components.values().stream()
            .mapToInt(Set::size)
            .sum();
    }
    
    public boolean isReasonableSize() {
        int total = getTotalBlocks();
        return total >= 5 && total <= 10000;
    }
}

/**
 * Result from a detection heuristic
 */
public class HeuristicResult {
    private final TreeComponentType type;
    private final float confidence; // 0.0 to 1.0
    
    public HeuristicResult(TreeComponentType type, float confidence) {
        this.type = type;
        this.confidence = Math.max(0.0f, Math.min(1.0f, confidence));
    }
}

/**
 * Metadata for tree blocks
 */
public class TreeBlockMetadata {
    private final TreeComponentType componentType;
    private final String sourceMod;
    private final Map<String, Object> customData;
    
    // Properties like hardness, flammability, etc.
}
```

### 7.2 Configuration Classes

```java
public class CoreConfig {
    // Detection settings
    @ConfigValue
    public static boolean enableAutoDetection = true;
    
    @ConfigValue
    public static float minConfidenceThreshold = 0.5f;
    
    @ConfigValue
    public static int maxTreeSize = 10000;
    
    @ConfigValue
    public static boolean enableConquestSupport = true;
    
    @ConfigValue
    public static boolean enableDynamicTreesSupport = true;
    
    // Performance settings
    @ConfigValue
    public static int detectionCacheSize = 1000;
    
    @ConfigValue
    public static boolean asyncDetection = true;
}
```

---

## 8. Implementation Roadmap

### Phase 1: Foundation (Week 1-2)
```
✅ Setup project structure
✅ Implement basic detection heuristics
✅ Create block registry
✅ Basic configuration system
```

### Phase 2: Detection Engine (Week 3-4)
```
✅ Implement multi-heuristic system
✅ Tree structure detection
✅ Validation system
✅ Caching layer
```

### Phase 3: Style System (Week 5-6)
```
✅ Style registry implementation
✅ JSON loader
✅ Style builder API
✅ Auto-style creation
```

### Phase 4: Compatibility (Week 7-8)
```
✅ Mod adapter framework
✅ Vanilla adapter
✅ Conquest Reforged adapter
✅ Dynamic Trees adapter
```

### Phase 5: API & Polish (Week 9-10)
```
✅ Public API finalization
✅ Event system
✅ Documentation
✅ Testing & optimization
```

### Phase 6: Integration Testing (Week 11-12)
```
✅ Test with Conquest Reforged
✅ Test with Dynamic Trees
✅ Test with terrain mods
✅ Performance profiling
✅ Bug fixes
```

---

## 9. Performance Considerations

### 9.1 Caching Strategy

```java
public class DetectionCache {
    private final LoadingCache<BlockState, TreeComponentType> blockCache;
    private final LoadingCache<BlockPos, TreeStructure> structureCache;
    
    public DetectionCache() {
        this.blockCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public TreeComponentType load(BlockState block) {
                    return TreeBlockDetector.detect(block);
                }
            });
    }
}
```

### 9.2 Async Detection

```java
public class AsyncTreeDetector {
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    
    public CompletableFuture<TreeStructure> detectTreeAsync(BlockPos pos, Level level) {
        return CompletableFuture.supplyAsync(
            () -> TreeStructureDetector.detectTree(pos, level),
            executor
        );
    }
}
```

### 9.3 Memory Optimization

```java
// Use flyweight pattern for common component types
public class ComponentTypePool {
    private static final Map<String, TreeComponentType> POOL = new HashMap<>();
    
    public static TreeComponentType get(String key) {
        return POOL.computeIfAbsent(key, k -> TreeComponentType.valueOf(k));
    }
}
```

---

## 10. Testing Strategy

### 10.1 Unit Tests

```java
@Test
public void testNameHeuristic() {
    BlockState oakLog = Blocks.OAK_LOG.defaultBlockState();
    HeuristicResult result = new NameHeuristic().evaluate(oakLog);
    
    assertEquals(TreeComponentType.TRUNK, result.getType());
    assertTrue(result.getConfidence() > 0.5f);
}

@Test
public void testTreeStructureDetection() {
    // Create test world with tree
    Level level = createTestLevel();
    placeTestTree(level, new BlockPos(0, 64, 0));
    
    TreeStructure tree = TreeStructureDetector.detectTree(
        new BlockPos(0, 64, 0), 
        level
    );
    
    assertTrue(tree.hasTrunk());
    assertTrue(tree.hasLeaves());
    assertTrue(tree.isValidTree());
}
```

### 10.2 Integration Tests

```java
@Test
public void testConquestIntegration() {
    // Assumes Conquest mod is loaded
    ConquestAdapter adapter = new ConquestAdapter();
    adapter.registerBlocks();
    
    List<BlockStyle> styles = adapter.createStyles();
    assertFalse(styles.isEmpty());
    
    // Verify oak style exists
    Optional<BlockStyle> oakStyle = styles.stream()
        .filter(s -> s.getId().getPath().contains("oak"))
        .findFirst();
    
    assertTrue(oakStyle.isPresent());
}
```

---

## 11. Documentation Requirements

### 11.1 API Documentation
- Javadoc for all public classes and methods
- Usage examples for common scenarios
- Integration guide for other mod developers

### 11.2 User Documentation
- Configuration guide
- Supported mods list
- Troubleshooting section

### 11.3 Developer Documentation
- Architecture overview
- Extension points
- Performance best practices

---

## 12. Success Metrics

### Core Functionality
- ✅ Detect 99%+ of vanilla tree blocks
- ✅ Detect 95%+ of Conquest Reforged tree blocks
- ✅ <1% false positive rate
- ✅ <100KB mod size

### Performance
- ✅ <1ms per block detection (cached)
- ✅ <50ms per tree structure detection
- ✅ <10MB memory usage

### Compatibility
- ✅ Works with Forge 1.18+
- ✅ No conflicts with major mods
- ✅ Stable API for other mods

---

## 13. Future Enhancements

### Machine Learning Integration
- Train ML model on tree block patterns
- Improve detection accuracy
- Auto-learn from user corrections

### Advanced Features
- Block variant detection (stripped, weathered, etc.)
- Tree age estimation from structure
- Species identification from block combinations

### Optimization
- Parallel processing for large forests
- GPU acceleration for detection
- Incremental detection updates
