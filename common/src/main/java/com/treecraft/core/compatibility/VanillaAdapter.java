package com.treecraft.core.compatibility;

import com.treecraft.core.api.BlockStyle;
import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.registry.TreeBlockRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class VanillaAdapter implements ModAdapter {
    @Override
    public String getModId() {
        return "minecraft";
    }

    @Override
    public boolean isModLoaded() {
        return true;
    }

    @Override
    public void registerBlocks() {
        // Oak
        register(Blocks.OAK_LOG, TreeComponentType.TRUNK);
        register(Blocks.OAK_WOOD, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_OAK_LOG, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_OAK_WOOD, TreeComponentType.TRUNK);
        register(Blocks.OAK_LEAVES, TreeComponentType.LEAVES);

        // Spruce
        register(Blocks.SPRUCE_LOG, TreeComponentType.TRUNK);
        register(Blocks.SPRUCE_WOOD, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_SPRUCE_LOG, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_SPRUCE_WOOD, TreeComponentType.TRUNK);
        register(Blocks.SPRUCE_LEAVES, TreeComponentType.LEAVES);

        // Birch
        register(Blocks.BIRCH_LOG, TreeComponentType.TRUNK);
        register(Blocks.BIRCH_WOOD, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_BIRCH_LOG, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_BIRCH_WOOD, TreeComponentType.TRUNK);
        register(Blocks.BIRCH_LEAVES, TreeComponentType.LEAVES);

        // Jungle
        register(Blocks.JUNGLE_LOG, TreeComponentType.TRUNK);
        register(Blocks.JUNGLE_WOOD, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_JUNGLE_LOG, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_JUNGLE_WOOD, TreeComponentType.TRUNK);
        register(Blocks.JUNGLE_LEAVES, TreeComponentType.LEAVES);

        // Acacia
        register(Blocks.ACACIA_LOG, TreeComponentType.TRUNK);
        register(Blocks.ACACIA_WOOD, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_ACACIA_LOG, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_ACACIA_WOOD, TreeComponentType.TRUNK);
        register(Blocks.ACACIA_LEAVES, TreeComponentType.LEAVES);

        // Dark Oak
        register(Blocks.DARK_OAK_LOG, TreeComponentType.TRUNK);
        register(Blocks.DARK_OAK_WOOD, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_DARK_OAK_LOG, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_DARK_OAK_WOOD, TreeComponentType.TRUNK);
        register(Blocks.DARK_OAK_LEAVES, TreeComponentType.LEAVES);

        // Mangrove
        register(Blocks.MANGROVE_LOG, TreeComponentType.TRUNK);
        register(Blocks.MANGROVE_WOOD, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_MANGROVE_LOG, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_MANGROVE_WOOD, TreeComponentType.TRUNK);
        register(Blocks.MANGROVE_ROOTS, TreeComponentType.ROOT);
        register(Blocks.MUDDY_MANGROVE_ROOTS, TreeComponentType.ROOT);
        register(Blocks.MANGROVE_LEAVES, TreeComponentType.LEAVES);

        // Cherry
        register(Blocks.CHERRY_LOG, TreeComponentType.TRUNK);
        register(Blocks.CHERRY_WOOD, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_CHERRY_LOG, TreeComponentType.TRUNK);
        register(Blocks.STRIPPED_CHERRY_WOOD, TreeComponentType.TRUNK);
        register(Blocks.CHERRY_LEAVES, TreeComponentType.LEAVES);
    }

    private void register(Block block, TreeComponentType type) {
        TreeBlockRegistry.register(block, type);
    }

    @Override
    public List<BlockStyle> createStyles() {
        List<BlockStyle> styles = new ArrayList<>();

        styles.add(createStyle("vanilla_oak", "Oak",
            Blocks.OAK_LOG, Blocks.OAK_LEAVES));

        styles.add(createStyle("vanilla_spruce", "Spruce",
            Blocks.SPRUCE_LOG, Blocks.SPRUCE_LEAVES));

        styles.add(createStyle("vanilla_birch", "Birch",
            Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES));

        styles.add(createStyle("vanilla_jungle", "Jungle",
            Blocks.JUNGLE_LOG, Blocks.JUNGLE_LEAVES));

        styles.add(createStyle("vanilla_acacia", "Acacia",
            Blocks.ACACIA_LOG, Blocks.ACACIA_LEAVES));

        styles.add(createStyle("vanilla_dark_oak", "Dark Oak",
            Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_LEAVES));

        styles.add(createStyle("vanilla_mangrove", "Mangrove",
            Blocks.MANGROVE_LOG, Blocks.MANGROVE_LEAVES, Blocks.MANGROVE_ROOTS));

        styles.add(createStyle("vanilla_cherry", "Cherry",
            Blocks.CHERRY_LOG, Blocks.CHERRY_LEAVES));

        return styles;
    }

    private BlockStyle createStyle(String name, String displayName, Block log, Block leaves) {
        return new BlockStyle.Builder()
            .id(new ResourceLocation("treecraft", name))
            .displayName(displayName)
            .trunk(log)
            .leaves(leaves)
            .build();
    }

    private BlockStyle createStyle(String name, String displayName, Block log, Block leaves, Block roots) {
        return new BlockStyle.Builder()
            .id(new ResourceLocation("treecraft", name))
            .displayName(displayName)
            .trunk(log)
            .leaves(leaves)
            .roots(roots)
            .build();
    }
}