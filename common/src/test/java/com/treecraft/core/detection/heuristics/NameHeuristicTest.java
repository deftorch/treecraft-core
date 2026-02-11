package com.treecraft.core.detection.heuristics;

import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.test.util.MockBlockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameHeuristicTest {

    private NameHeuristic heuristic;
    private MockBlockState mockBlockState;

    @BeforeEach
    void setUp() {
        com.treecraft.core.test.util.TestBootstrap.init();
        mockBlockState = new MockBlockState();
    }

    private void setupHeuristic(String blockId) {
        heuristic = new NameHeuristic() {
            @Override
            protected ResourceLocation getBlockKey(Block block) {
                return new ResourceLocation(blockId);
            }
        };
    }

    @Test
    void testLogPattern_ShouldDetectTrunk() {
        setupHeuristic("minecraft:oak_log");
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.TRUNK, result.getType());
        assertEquals(0.8f, result.getConfidence());
    }

    @Test
    void testTrunkPattern_ShouldDetectTrunk() {
        setupHeuristic("mod:mystic_trunk");
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.TRUNK, result.getType());
    }

    @Test
    void testWoodPattern_ShouldDetectTrunk() {
        setupHeuristic("mod:dark_wood");
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.TRUNK, result.getType());
    }

    @Test
    void testBranchPattern_ShouldDetectBranch() {
        setupHeuristic("mod:small_branch");
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.BRANCH, result.getType());
    }

    @Test
    void testLeavesPattern_ShouldDetectLeaves() {
        setupHeuristic("minecraft:oak_leaves");
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.LEAVES, result.getType());
        assertEquals(0.8f, result.getConfidence());
    }

    @Test
    void testRootPattern_ShouldDetectRoot() {
        setupHeuristic("mod:mangrove_root");
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.ROOT, result.getType());
    }

    @Test
    void testUnknownBlock_ShouldReturnUnknown() {
        setupHeuristic("minecraft:stone");
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.UNKNOWN, result.getType());
        assertEquals(0.0f, result.getConfidence());
    }

    @Test
    void testCaseInsensitive_ShouldMatch() {
        // We need to mock ResourceLocation because real one enforces lowercase
        ResourceLocation mockLocation = org.mockito.Mockito.mock(ResourceLocation.class);
        org.mockito.Mockito.when(mockLocation.toString()).thenReturn("MOD:OAK_LOG");

        heuristic = new NameHeuristic() {
            @Override
            protected ResourceLocation getBlockKey(Block block) {
                return mockLocation;
            }
        };

        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(TreeComponentType.TRUNK, result.getType());
    }

    @Test
    void testConfidenceLevel_ShouldBe80Percent() {
        setupHeuristic("minecraft:oak_log");
        HeuristicResult result = heuristic.evaluate(mockBlockState.get(), null, null);
        assertEquals(0.8f, result.getConfidence());
    }
}
