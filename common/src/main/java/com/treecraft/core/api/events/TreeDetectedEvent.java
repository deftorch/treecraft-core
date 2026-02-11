package com.treecraft.core.api.events;

import com.treecraft.core.api.TreeComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TreeDetectedEvent {
    private final BlockState block;
    private final BlockPos pos;
    private final TreeComponentType detectedType;
    private final float confidence;

    public TreeDetectedEvent(BlockState block, BlockPos pos, TreeComponentType detectedType, float confidence) {
        this.block = block;
        this.pos = pos;
        this.detectedType = detectedType;
        this.confidence = confidence;
    }

    public BlockState getBlock() { return block; }
    public BlockPos getPos() { return pos; }
    public TreeComponentType getDetectedType() { return detectedType; }
    public float getConfidence() { return confidence; }
}