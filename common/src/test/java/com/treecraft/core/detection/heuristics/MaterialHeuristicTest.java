package com.treecraft.core.detection.heuristics;

import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.test.util.MockBlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

class MaterialHeuristicTest {

    private MaterialHeuristic heuristic;
    private MockBlockState mockBlockState;

    @BeforeEach
    void setUp() {
        com.treecraft.core.test.util.TestBootstrap.init();
        heuristic = new MaterialHeuristic();
        mockBlockState = new MockBlockState();
    }

    @Test
    void testWoodMaterial_ShouldDetectTrunk() {
        mockBlockState.withSound(SoundType.WOOD);
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.TRUNK, result.getType());
        assertEquals(0.4f, result.getConfidence());
    }

    @Test
    void testLeavesMaterial_ShouldDetectLeaves() {
        mockBlockState.withSound(SoundType.GRASS);
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.LEAVES, result.getType());
        assertEquals(0.4f, result.getConfidence());
    }

    @Test
    void testNonTreeMaterial_ShouldReturnUnknown() {
        mockBlockState.withSound(SoundType.STONE);
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.UNKNOWN, result.getType());
    }

    @Test
    void testConfidenceLevels() {
        mockBlockState.withSound(SoundType.WOOD);
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(0.4f, result.getConfidence());
    }

    /*
    @Test
    void testHardness_ShouldDistinguishTrunkFromPlanks() {
        // This test requires MaterialHeuristic to check hardness, which it currently doesn't.
        // Planks and Logs both have SoundType.WOOD.
        // If we want to implement this, we need to mock hardness (destroySpeed).

        mockBlockState.withSound(SoundType.WOOD);
        // Assuming planks have different hardness? Actually they are often similar (2.0 vs 2.0).
        // Maybe logs are harder? Oak log is 2.0, Oak planks are 2.0.
        // So hardness might not be enough.
        // But let's assume the requirement implies checking something.
    }
    */
}
