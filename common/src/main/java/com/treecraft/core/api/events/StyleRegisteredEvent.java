package com.treecraft.core.api.events;

import com.treecraft.core.api.BlockStyle;

public class StyleRegisteredEvent {
    private final BlockStyle style;

    public StyleRegisteredEvent(BlockStyle style) {
        this.style = style;
    }

    public BlockStyle getStyle() { return style; }
}