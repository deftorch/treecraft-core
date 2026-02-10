package com.treecraft.core.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.treecraft.core.Constants;
import com.treecraft.core.api.BlockStyle;
import com.treecraft.core.api.StyleMetadata;
import com.treecraft.core.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class StyleLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void loadStyles() {
        Path configDir = Services.PLATFORM.getConfigDirectory();
        if (configDir == null) {
            Constants.LOG.error("Config directory is null!");
            return;
        }

        Path stylesDir = configDir.resolve("treecraft/styles");

        if (!Files.exists(stylesDir)) {
            try {
                Files.createDirectories(stylesDir);
            } catch (IOException e) {
                Constants.LOG.error("Failed to create styles directory", e);
                return;
            }
        }

        try (Stream<Path> paths = Files.walk(stylesDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(p -> loadStyleFile(p));
        } catch (IOException e) {
            Constants.LOG.error("Failed to load styles", e);
        }
    }

    private static void loadStyleFile(Path file) {
        try (Reader reader = Files.newBufferedReader(file)) {
            StyleDTO dto = GSON.fromJson(reader, StyleDTO.class);
            if (dto != null && dto.id != null) {
                try {
                    BlockStyle style = parseStyle(dto);
                    StyleRegistry.register(style);
                } catch (Exception e) {
                    Constants.LOG.error("Failed to parse style content: " + file, e);
                }
            }
        } catch (Exception e) {
            Constants.LOG.error("Failed to read style file: " + file, e);
        }
    }

    private static BlockStyle parseStyle(StyleDTO dto) {
        ResourceLocation id = new ResourceLocation(dto.id);
        BlockStyle.Builder builder = new BlockStyle.Builder()
                .id(id)
                .displayName(dto.display_name);

        if (dto.palette != null) {
            if (dto.palette.trunk != null) {
                builder.trunk(parseBlocks(dto.palette.trunk));
            }
            if (dto.palette.branch != null) {
                builder.branch(parseBlocks(dto.palette.branch));
            }
            if (dto.palette.leaves != null) {
                builder.leaves(parseBlocks(dto.palette.leaves));
            }
            if (dto.palette.roots != null) {
                builder.roots(parseBlocks(dto.palette.roots));
            }
        }

        if (dto.metadata != null) {
            builder.metadata(new StyleMetadata(
                    dto.metadata.color_scheme,
                    dto.metadata.texture_style,
                    dto.metadata.seasonal_variants
            ));
        }

        return builder.build();
    }

    private static Block[] parseBlocks(List<String> blockIds) {
        return blockIds.stream()
                .map(ResourceLocation::new)
                .map(BuiltInRegistries.BLOCK::get)
                .filter(Objects::nonNull)
                .toArray(Block[]::new);
    }

    private static class StyleDTO {
        String id;
        String display_name;
        PaletteDTO palette;
        MetadataDTO metadata;
    }

    private static class PaletteDTO {
        List<String> trunk;
        List<String> branch;
        List<String> leaves;
        List<String> roots;
    }

    private static class MetadataDTO {
        String color_scheme;
        String texture_style;
        boolean seasonal_variants;
    }
}