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