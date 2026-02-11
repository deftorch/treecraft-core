package com.treecraft.core.api;

public class StyleMetadata {
    private final String colorScheme;
    private final String textureStyle;
    private final boolean seasonalVariants;

    public StyleMetadata(String colorScheme, String textureStyle, boolean seasonalVariants) {
        this.colorScheme = colorScheme;
        this.textureStyle = textureStyle;
        this.seasonalVariants = seasonalVariants;
    }

    public String getColorScheme() { return colorScheme; }
    public String getTextureStyle() { return textureStyle; }
    public boolean hasSeasonalVariants() { return seasonalVariants; }
}