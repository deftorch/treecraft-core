package com.treecraft.core.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class BlockStyle {
    private final ResourceLocation id;
    private final String displayName;
    private final Map<TreeComponentType, List<BlockState>> blockPalette;
    private final StyleMetadata metadata;

    private BlockStyle(ResourceLocation id, String displayName, Map<TreeComponentType, List<BlockState>> blockPalette, StyleMetadata metadata) {
        this.id = id;
        this.displayName = displayName;
        this.blockPalette = blockPalette;
        this.metadata = metadata;
    }

    public ResourceLocation getId() { return id; }
    public String getDisplayName() { return displayName; }
    public Map<TreeComponentType, List<BlockState>> getBlockPalette() { return blockPalette; }
    public StyleMetadata getMetadata() { return metadata; }

    public static class Builder {
        private ResourceLocation id;
        private String displayName;
        private final Map<TreeComponentType, List<BlockState>> blockPalette = new EnumMap<>(TreeComponentType.class);
        private StyleMetadata metadata;

        public Builder id(ResourceLocation id) {
            this.id = id;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder trunk(Block... blocks) {
            addBlocks(TreeComponentType.TRUNK, blocks);
            return this;
        }

        public Builder branch(Block... blocks) {
            addBlocks(TreeComponentType.BRANCH, blocks);
            return this;
        }

        public Builder leaves(Block... blocks) {
            addBlocks(TreeComponentType.LEAVES, blocks);
            return this;
        }

        public Builder roots(Block... blocks) {
            addBlocks(TreeComponentType.ROOT, blocks);
            return this;
        }

        public Builder metadata(StyleMetadata meta) {
            this.metadata = meta;
            return this;
        }

        private void addBlocks(TreeComponentType type, Block... blocks) {
            List<BlockState> states = blockPalette.computeIfAbsent(type, k -> new ArrayList<>());
            for (Block block : blocks) {
                states.add(block.defaultBlockState());
            }
        }

        public BlockStyle build() {
            if (id == null) {
                throw new IllegalStateException("ID is required for BlockStyle");
            }
            return new BlockStyle(id, displayName, blockPalette, metadata);
        }
    }
}