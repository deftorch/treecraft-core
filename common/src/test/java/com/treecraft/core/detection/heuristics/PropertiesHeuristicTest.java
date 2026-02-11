package com.treecraft.core.detection.heuristics;

import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.test.util.MockBlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesHeuristicTest {

    private PropertiesHeuristic heuristic;
    private MockBlockState mockBlockState;

    @BeforeEach
    void setUp() {
        com.treecraft.core.test.util.TestBootstrap.init();
        heuristic = new PropertiesHeuristic();
        mockBlockState = new MockBlockState();
    }

    @Test
    void testAxisProperty_ShouldDetectTrunk() {
        mockBlockState.withProperty(BlockStateProperties.AXIS);
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.TRUNK, result.getType());
        assertEquals(0.7f, result.getConfidence());
    }

    @Test
    void testDistanceProperty_ShouldDetectLeaves() {
        mockBlockState.withProperty(BlockStateProperties.DISTANCE);
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.LEAVES, result.getType());
        assertEquals(0.8f, result.getConfidence());
    }

    @Test
    void testNoProperties_ShouldReturnUnknown() {
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.UNKNOWN, result.getType());
        assertEquals(0.0f, result.getConfidence());
    }
}
