package com.treecraft.core.detection.heuristics;

import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.test.util.MockBlockState;
import net.minecraft.tags.BlockTags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TagHeuristicTest {

    private TagHeuristic heuristic;
    private MockBlockState mockBlockState;

    @BeforeEach
    void setUp() {
        com.treecraft.core.test.util.TestBootstrap.init();
        heuristic = new TagHeuristic();
        mockBlockState = new MockBlockState();
    }

    @Test
    void testLogsTag_ShouldDetectTrunk() {
        when(mockBlockState.get().is(BlockTags.LOGS)).thenReturn(true);
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.TRUNK, result.getType());
        assertEquals(0.9f, result.getConfidence());
    }

    @Test
    void testLeavesTag_ShouldDetectLeaves() {
        when(mockBlockState.get().is(BlockTags.LEAVES)).thenReturn(true);
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.LEAVES, result.getType());
        assertEquals(0.9f, result.getConfidence());
    }

    @Test
    void testMultipleTags_ShouldUseHighestConfidence() {
        when(mockBlockState.get().is(BlockTags.LOGS)).thenReturn(true);
        when(mockBlockState.get().is(BlockTags.LEAVES)).thenReturn(true);
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.TRUNK, result.getType());
    }

    @Test
    void testNoTags_ShouldReturnUnknown() {
        when(mockBlockState.get().is(BlockTags.LOGS)).thenReturn(false);
        when(mockBlockState.get().is(BlockTags.LEAVES)).thenReturn(false);
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.UNKNOWN, result.getType());
    }
}
