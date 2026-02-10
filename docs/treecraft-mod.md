# 🌲 **TreeCraft Ecosystem: Mod Summary**

## **Complete Module Overview**

---

## **📦 Module 1: TreeCraft Core** ⭐ REQUIRED

### **Deskripsi:**
Library dasar yang mendeteksi dan mengkategorikan block pohon dari mod manapun.

### **Fitur Utama:**
```
✅ Block Detection System
├─ Auto-detect tree blocks dari ANY mod
├─ Kategorisasi otomatis: trunk, branch, leaves, roots
├─ Support Conquest Reforged (15,000+ blocks)
├─ Support Dynamic Trees
└─ Support vanilla & custom mods

✅ Style Registry
├─ Manage visual styles (block palettes)
├─ JSON-based configuration
├─ Mix & match blocks from different mods
└─ API untuk mod lain

✅ Mod Compatibility Layer
├─ Conquest Reforged integration
├─ Dynamic Trees compatibility
├─ Terralith/Biomes O' Plenty support
└─ Extensible untuk mod baru
```

### **Size:** ~100KB  
### **Dependencies:** None  
### **API:** Public (other mods can use)

### **Use Cases:**
- ✅ Sendirian: Classify existing trees
- ✅ Dengan Procedural: Provide block palettes untuk generation
- ✅ Dengan Physics: Identify tree blocks untuk falling system
- ✅ Dengan mod lain: Foundation library

---

## **📦 Module 2: TreeCraft Life** ⭐ RECOMMENDED

### **Deskripsi:**
Sistem lifecycle lengkap - pohon tumbuh, berubah musiman, sakit, dan mati secara natural.

### **Fitur Utama:**
```
🌱 PROGRESSIVE GROWTH
├─ 6 Stages: Seed → Sapling → Young → Adolescent → Mature → Ancient
├─ Real-time smooth growth (visible progression)
├─ Faktor: Light, water, soil, season
├─ Time to mature: 30 minutes - 10 hours (configurable)
└─ Bonemeal acceleration

🍂 SEASONAL SYSTEM
├─ Spring: Bright green, flowers, fast growth
├─ Summer: Dark green, fruit, normal growth
├─ Autumn: Orange/red/yellow, leaves fall, slow growth
├─ Winter: Dormant, deciduous lose leaves
└─ Gradual color transitions

🏥 HEALTH & DISEASE
├─ 4 Disease Types:
│   ├─ Fungal Infection (brown spots)
│   ├─ Pest Infestation (bark beetles)
│   ├─ Root Rot (dark roots, wilting)
│   └─ Leaf Blight (discolored leaves)
├─ Disease spreads to nearby trees
├─ Treatment items (medicine, fungicide)
└─ Visual symptoms progression

⏳ AGING & DEATH
├─ Trees age naturally (20-80 hours lifespan)
├─ Ancient trees (100+ hours, special bonuses)
├─ Death causes: old age, disease, lightning, player
├─ Decay process (1 hour to decompose)
└─ Enriches soil after death

🌍 ENVIRONMENTAL RESPONSE
├─ Weather: Rain speeds growth, storms damage branches
├─ Soil: Nutrients affect growth rate (0.3x - 2.0x)
├─ Crowding: Too many trees = slow growth
└─ Biome-specific behaviors
```

### **Size:** ~400KB  
### **Dependencies:** TreeCraft Core (required)  
### **Performance:** <5% FPS impact

### **Configuration:**
```json
{
  "growth_rates": {
    "base_rate": 0.0001,
    "time_to_mature_hours": 3.0,
    "bonemeal_boost": 0.25
  },
  "seasons": {
    "enabled": true,
    "cycle_days": 28,
    "deciduous_lose_leaves_winter": true
  },
  "disease": {
    "enabled": true,
    "spread_chance": 0.1,
    "infection_rate": 0.0001
  },
  "aging": {
    "natural_death": true,
    "max_lifespan_hours": 40
  }
}
```

---

## **📦 Module 3: TreeCraft Physics** ⭐ RECOMMENDED

### **Deskripsi:**
Sistem physics realistis - pohon jatuh, ranting patah, daun gugur dengan physics entities.

### **Fitur Utama:**
```
🪓 TREE FALLING SYSTEM
├─ Triggers:
│   ├─ Foundation broken (player chop base)
│   ├─ Storm damage (high winds)
│   ├─ Lightning strike
│   ├─ Disease death
│   └─ Old age collapse
├─ Realistic animation (90° rotation, 3 seconds)
├─ Direction: Player hit direction + center of mass + slope
├─ Damage entities underneath (crush damage)
└─ Interactive after landing (chop individual blocks)

🎯 LOCAL GRID SYSTEM
├─ Preserves blocky aesthetic (no smoothing)
├─ All blocks maintain appearance
├─ Conquest models preserved
├─ Tile entities preserved (if tree has chests/signs)
└─ Rotates as entity, blocks stay aligned

🌿 BRANCH BREAKING
├─ Causes:
│   ├─ Weather (storm winds)
│   ├─ Snow weight (winter accumulation)
│   ├─ Age (old branches weaken)
│   ├─ Disease (rot weakens structure)
│   └─ Player damage (axe hits)
├─ Branches fall as physics entities
├─ Create fallen branch decorations
└─ Can be collected as items

🍃 LEAF FALLING
├─ Individual leaf entities
├─ Gentle floating motion (wind sway)
├─ Slow descent (0.02 blocks/tick)
├─ Creates leaf piles on ground
├─ Piles decompose → enrich soil
└─ Collectable with shears

🔍 UNIVERSAL TREE DETECTION
├─ Works with ANY tree type:
│   ├─ TreeCraft generated trees
│   ├─ Conquest Reforged trees
│   ├─ Dynamic Trees
│   ├─ Vanilla trees
│   └─ Player-built custom trees
└─ Preserves ALL block data
```

### **Size:** ~300KB  
### **Dependencies:** TreeCraft Core (optional but recommended)  
### **Performance:** ~10% FPS impact during falling (LOD optimized)

### **Technical Details:**
```java
// Fallen tree entity
- Local grid: 3D array of blocks
- Rotation: Quaternion (smooth 360° rotation)
- Collision: AABB per-block or simplified
- LOD Levels:
  ├─ FULL (<16 blocks): All blocks, full physics
  ├─ MEDIUM (16-32): Simplified collision
  ├─ LOW (32-64): Bounding box only
  └─ MINIMAL (>64): Single entity
```

---

## **📦 Module 4: TreeCraft Procedural** 🔸 OPTIONAL

### **Deskripsi:**
Engine untuk generate pohon secara algorithmic dengan customization penuh.

### **Fitur Utama:**
```
🧬 GENERATION ALGORITHMS
├─ L-System (grammar-based)
│   ├─ Parametric rules
│   ├─ Stochastic variations
│   └─ 3D turtle graphics
├─ Space Colonization (attraction points)
│   ├─ Realistic branching
│   ├─ Natural crown shape
│   └─ Environmental response
├─ Meristem Growth (biological simulation)
│   ├─ Hormone-based
│   ├─ Growth competition
│   └─ Very realistic
└─ Hybrid (combination of above)

🌳 SPECIES SYSTEM
├─ 50+ pre-made species
├─ JSON-based definitions
├─ 81+ parameters per species:
│   ├─ Trunk: height, thickness, taper
│   ├─ Branches: angle, density, recursion
│   ├─ Leaves: radius, density, shape
│   ├─ Roots: depth, spread
│   └─ Special: flowers, fruit, thorns
└─ Import/Export species files

🎨 CUSTOMIZATION TOOLS
├─ Tree Designer GUI (in-game editor)
│   ├─ Visual preview (live update)
│   ├─ Parameter sliders (10 main params)
│   ├─ Advanced mode (all 81 params)
│   └─ Export to JSON
├─ Woodland Staff (copy/paste trees)
├─ Blueprint System (shareable tree codes)
└─ Random seed variations

🌍 WORLDGEN INTEGRATION
├─ Replace vanilla tree generation
├─ Biome-specific species
├─ Density & spacing controls
├─ Structure sets (tree groves)
└─ Compatible with terrain mods
```

### **Size:** ~500KB  
### **Dependencies:** TreeCraft Core (required)  
### **Use Cases:**
- ✅ Replace vanilla boring trees
- ✅ Create custom forests
- ✅ Design unique trees for builds
- ✅ Share tree designs with community

### **Example Species Definition:**
```json
{
  "species": {
    "id": "treecraft:ancient_oak",
    "display_name": "Ancient Oak",
    "style": "treecraft:conquest_oak",
    "generation": {
      "algorithm": "hybrid",
      "parameters": {
        "trunk_height": [20, 30],
        "trunk_thickness": 2.5,
        "branch_angle": [25, 45],
        "branch_density": 0.7,
        "branch_recursion": 4,
        "leaf_radius": 8.0,
        "leaf_density": 0.8,
        "root_depth": 5,
        "root_spread": 10
      }
    },
    "worldgen": {
      "biomes": ["minecraft:forest", "minecraft:plains"],
      "rarity": 0.05,
      "min_spacing": 30
    }
  }
}
```

---

## **📦 Module 5: TreeCraft Survival** 🔸 OPTIONAL

### **Deskripsi:**
Gameplay mechanics - progression, tools, processing, economy.

### **Fitur Utama:**
```
⚔️ PROGRESSION SYSTEM
├─ 5 Tiers:
│   ├─ Novice: Basic planting (5 species)
│   ├─ Apprentice: Size control (15 species)
│   ├─ Expert: Customization (30 species)
│   ├─ Master: Full features (all species)
│   └─ Legendary: Mythical trees
├─ Unlock by achievements
├─ Species discovery system
└─ Forester's Journal tracking

🛠️ TOOLS & ITEMS
├─ Pruning Shears (early)
│   ├─ Control sapling size
│   ├─ Trim branches
│   └─ Harvest leaves efficiently
├─ Forester's Journal (tracking)
│   ├─ View tree stats
│   ├─ Record species
│   └─ Disease diagnosis
├─ Woodland Staff (advanced)
│   ├─ Copy/paste trees
│   ├─ Quick harvest
│   ├─ Growth boost
│   └─ Cure disease
├─ Tapping Kit (resource)
│   ├─ Tap mature trees
│   ├─ Collect resin/sap
│   └─ Don't overharvest
└─ Medicine & Catalysts

⚙️ SIZE-BASED HARVESTING
├─ Sapling: HAND (breakable)
├─ Young: ANY AXE
├─ Adolescent: IRON+ AXE
├─ Mature: DIAMOND+ AXE
├─ Ancient: NETHERITE AXE or Staff
└─ Wrong tool = very slow + no drops

📦 UNIFIED DROP SYSTEM
├─ All tree blocks → Wood Pieces
├─ Shape-agnostic harvesting
├─ Drops based on SIZE not shape:
│   ├─ Sapling: 0-2 wood pieces
│   ├─ Young: 10-20 wood pieces
│   ├─ Adolescent: 40-80 wood pieces
│   ├─ Mature: 100-200 wood pieces
│   └─ Ancient: 300-500 + special items
└─ Fortune enchantment affects yield

🏗️ PROCESSING STATIONS
├─ Carpenter's Bench (basic)
│   ├─ Wood pieces → Shaped blocks
│   ├─ Example: 4 pieces → 1 log
│   ├─ Example: 2 pieces → 1 branch
│   └─ Manual crafting
├─ Sawmill (advanced)
│   ├─ More efficient (+bonus)
│   ├─ Example: 2 pieces → 5 planks (+1 bonus)
│   ├─ Automated processing
│   ├─ Byproducts (sawdust)
│   └─ Requires power (water wheel/redstone)
└─ Lumber Yard (industrial)
    ├─ Process entire fallen trees
    ├─ Bulk operations
    ├─ Multiple outputs simultaneously
    └─ 5x5x3 multiblock

💰 ECONOMY FEATURES
├─ Villager trading (forester profession)
├─ Tree value based on:
│   ├─ Size (ancient = valuable)
│   ├─ Rarity (rare species = expensive)
│   └─ Quality (healthy = better)
├─ Sell wood pieces, saplings, products
└─ Buy rare species saplings
```

### **Size:** ~400KB  
### **Dependencies:** 
- TreeCraft Core (required)
- TreeCraft Procedural (required)
- TreeCraft Life (optional, enhanced features)

### **Crafting Examples:**
```
CARPENTER'S BENCH RECIPES:

Wood Pieces → Blocks:
├─ 4 Oak Wood Pieces → 1 Oak Log
├─ 3 Oak Wood Pieces → 1 Thick Branch
├─ 2 Oak Wood Pieces → 1 Medium Branch
├─ 1 Oak Wood Piece → 2 Thin Branches
└─ 1 Oak Wood Piece → 4 Sticks

SAWMILL RECIPES (Efficient):

├─ 2 Oak Wood Pieces → 5 Oak Planks + 1 Sawdust
├─ 1 Oak Log → 6 Oak Planks + 2 Sawdust
└─ 1 Thick Branch → 12 Sticks + 1 Sawdust

BYPRODUCT USES:

Sawdust:
├─ Compost (3 sawdust → 1 compost)
├─ Paper (9 sawdust → 3 paper)
├─ Fuel (burns 10 seconds)
└─ Building (9 sawdust → 1 sawdust block)
```

---

## **🎯 Recommended Module Combinations**

### **⭐ Minimum (Vanilla Enhancement):**
```
TreeCraft Core + TreeCraft Life
= Living trees with seasonal changes
```

### **⭐⭐ Recommended (Best Experience):**
```
TreeCraft Core + TreeCraft Life + TreeCraft Physics
= Complete lifecycle + realistic physics
```

### **⭐⭐⭐ Full Experience:**
```
All 5 Modules
= Complete tree ecosystem with customization
```

### **🎨 Builder's Pack:**
```
TreeCraft Core + TreeCraft Procedural + TreeCraft Survival
= Custom trees without physics complexity
```

### **🔬 Realism Pack:**
```
TreeCraft Core + TreeCraft Life + TreeCraft Physics
= Maximum realism focus
```

---

## **📊 Feature Comparison Matrix**

| Feature | Core | Life | Physics | Procedural | Survival |
|---------|------|------|---------|------------|----------|
| **Block Detection** | ✅ | - | - | - | - |
| **Style Registry** | ✅ | - | - | - | - |
| **Progressive Growth** | - | ✅ | - | - | - |
| **Seasonal Changes** | - | ✅ | - | - | - |
| **Disease System** | - | ✅ | - | - | ✅ (cure) |
| **Aging & Death** | - | ✅ | - | - | - |
| **Tree Falling** | - | - | ✅ | - | - |
| **Branch Breaking** | - | - | ✅ | - | - |
| **Leaf Physics** | - | - | ✅ | - | - |
| **Algorithms** | - | - | - | ✅ | - |
| **Species System** | - | - | - | ✅ | ✅ (unlock) |
| **Designer GUI** | - | - | - | ✅ | - |
| **Worldgen** | - | - | - | ✅ | - |
| **Tools** | - | - | - | - | ✅ |
| **Progression** | - | - | - | - | ✅ |
| **Processing** | - | - | - | - | ✅ |
| **Economy** | - | - | - | - | ✅ |

---

## **💾 Download Sizes & Performance**

| Module | File Size | RAM Usage | FPS Impact |
|--------|-----------|-----------|------------|
| Core | ~100KB | <10MB | <1% |
| Life | ~400KB | ~50MB | ~5% |
| Physics | ~300KB | ~100MB (peak) | ~10% (during falling) |
| Procedural | ~500KB | ~30MB | <2% |
| Survival | ~400KB | ~20MB | <1% |
| **TOTAL** | **~1.7MB** | **~210MB** | **~15%** |

**Performance Notes:**
- Core: Minimal impact (pure library)
- Life: Ticks only loaded chunks
- Physics: LOD system optimizes distant trees
- Procedural: Generation cached
- Survival: GUI-only overhead

---

## **🔌 Mod Compatibility**

### **✅ Fully Compatible:**
- Conquest Reforged (first-class support)
- Dynamic Trees (can coexist)
- Biomes O' Plenty
- Terralith
- Oh The Biomes You'll Go
- Farmer's Delight
- Create (sawmill alternative)

### **🔸 Partial Compatible:**
- Serene Seasons (seasonal override)
- Weather2 (enhanced storms)
- Botania (tree magic compatibility)

### **❌ Incompatible:**
- None known (designed for universal compatibility)

---

## **📋 Installation Guide**

### **Step 1: Choose Your Modules**

**Beginner:**
```
Download: Core + Life
Install to mods folder
Play!
```

**Intermediate:**
```
Download: Core + Life + Physics
Config: Adjust growth rates
Play!
```

**Advanced:**
```
Download: All modules
Config: Customize everything
Optional: Add Conquest Reforged
Play!
```

### **Step 2: Configuration**

```
minecraft/config/treecraft/
├── core.json (block detection rules)
├── life.json (growth rates, seasons)
├── physics.json (falling behavior)
├── procedural.json (algorithms, species)
└── survival.json (progression, drops)
```

### **Step 3: Resource Packs (Optional)**

```
Enhanced textures:
├── Leaf variations
├─ Bark detail
├─ Seasonal textures
└─ Ancient tree special textures
```

---

## **🎮 Quick Start Guide**

### **First 5 Minutes:**
1. **Plant a sapling** - Right-click with sapling
2. **Watch it grow** - Takes 30 mins - 3 hours
3. **Observe seasons** - Colors change
4. **Harvest with axe** - Size-appropriate tool

### **First Hour:**
1. **Build Carpenter's Bench**
2. **Process wood pieces** into logs/planks
3. **Unlock Forester's Journal**
4. **Discover new species**

### **First Day:**
1. **Plant diverse forest**
2. **Witness tree falling** (chop base)
3. **Cure first disease**
4. **Reach Expert tier**

---

## **❓ FAQ**

**Q: Do I need all modules?**
A: No! Core + Life is minimum. Add others as desired.

**Q: Performance impact?**
A: 5-15% FPS drop with all modules. Configurable.

**Q: Works with existing worlds?**
A: Yes! Trees generate in new chunks.

**Q: Compatible with Conquest Reforged?**
A: YES! First-class support, auto-detects all CR blocks.

**Q: Can I disable features?**
A: Yes, extensive config options.

**Q: Multiplayer compatible?**
A: Yes, fully synced.

**Q: Can I contribute?**
A: Yes! Open source, API available.

---

## **🚀 Future Modules (Planned)**

### **TreeCraft Ecology** (Planned)
- Wildlife spawning in trees
- Pollination mechanics
- Seed dispersal
- Tree-animal interactions

### **TreeCraft Magic** (Planned)
- Magical tree variants
- Mana production
- Enchanted wood
- Tree spirits

### **TreeCraft Tech** (Planned)
- Automated tree farms
- Industrial processing
- Genetic modification
- Cloning system

---

**Total: 5 Core Modules + 3 Planned = Complete Ecosystem** 🌲