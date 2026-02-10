package com.treecraft.core.api;

public class HeuristicResult {
    private final TreeComponentType type;
    private final float confidence;

    public HeuristicResult(TreeComponentType type, float confidence) {
        this.type = type;
        this.confidence = Math.max(0.0f, Math.min(1.0f, confidence));
    }

    public TreeComponentType getType() {
        return type;
    }

    public float getConfidence() {
        return confidence;
    }
}