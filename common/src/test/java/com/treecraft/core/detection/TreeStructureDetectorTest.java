package com.treecraft.core.detection;

import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.IDetectionHeuristic;
import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.test.util.MockBlockPos;
import com.treecraft.core.test.util.MockBlockState;
import com.treecraft.core.test.util.MockLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class TreeStructureDetectorTest {

    private MockLevel mockLevel;
    private MockBlockState trunkBlock;
    private MockBlockState leavesBlock;
    private MockBlockState branchBlock;

    @Mock
    private IDetectionHeuristic mockHeuristic;

    @BeforeEach
    void setUp() {
        com.treecraft.core.test.util.TestBootstrap.init();
        MockitoAnnotations.openMocks(this);

        mockLevel = new MockLevel();
        trunkBlock = new MockBlockState().withSound(net.minecraft.world.level.block.SoundType.WOOD);
        leavesBlock = new MockBlockState().withSound(net.minecraft.world.level.block.SoundType.GRASS);
        branchBlock = new MockBlockState().withSound(net.minecraft.world.level.block.SoundType.WOOD);

        // Reset detector and register mock heuristic
        TreeBlockDetector.getInstance().resetForTest();
        TreeBlockDetector.getInstance().registerHeuristic(mockHeuristic);

        // Setup heuristic behavior
        when(mockHeuristic.evaluate(eq(trunkBlock.get()), any(), any()))
            .thenReturn(new HeuristicResult(TreeComponentType.TRUNK, 1.0f));
        when(mockHeuristic.evaluate(eq(leavesBlock.get()), any(), any()))
            .thenReturn(new HeuristicResult(TreeComponentType.LEAVES, 1.0f));
        when(mockHeuristic.evaluate(eq(branchBlock.get()), any(), any()))
            .thenReturn(new HeuristicResult(TreeComponentType.BRANCH, 1.0f));

        // For any other block (including AIR), return UNKNOWN
        // We use a broader matcher for the block argument to catch AIR or others
        when(mockHeuristic.evaluate(any(), any(), any()))
            .thenAnswer(invocation -> {
                Object arg = invocation.getArgument(0);
                if (arg == trunkBlock.get()) return new HeuristicResult(TreeComponentType.TRUNK, 1.0f);
                if (arg == leavesBlock.get()) return new HeuristicResult(TreeComponentType.LEAVES, 1.0f);
                if (arg == branchBlock.get()) return new HeuristicResult(TreeComponentType.BRANCH, 1.0f);
                return new HeuristicResult(TreeComponentType.UNKNOWN, 0.0f);
            });
    }

    @AfterEach
    void tearDown() {
        TreeBlockDetector.getInstance().resetForTest();
    }

    @Test
    void testSimpleTreeDetection() {
        BlockPos basePos = MockBlockPos.at(0, 64, 0);

        // Build simple tree:
        // L L L
        // L T L
        //   T

        mockLevel.withBlock(basePos, trunkBlock.get());
        mockLevel.withBlock(basePos.above(), trunkBlock.get());

        // Leaves around top trunk
        BlockPos topTrunk = basePos.above();
        mockLevel.withBlock(topTrunk.north(), leavesBlock.get());
        mockLevel.withBlock(topTrunk.south(), leavesBlock.get());
        mockLevel.withBlock(topTrunk.east(), leavesBlock.get());
        mockLevel.withBlock(topTrunk.west(), leavesBlock.get());
        mockLevel.withBlock(topTrunk.above(), leavesBlock.get());

        TreeStructure tree = TreeStructureDetector.detectTree(basePos, mockLevel.get());

        assertTrue(tree.hasTrunk());
        assertTrue(tree.hasLeaves());
        assertEquals(2, tree.getComponentsOfType(TreeComponentType.TRUNK).size());
        assertEquals(5, tree.getComponentsOfType(TreeComponentType.LEAVES).size());
        assertEquals(7, tree.getTotalBlocks());
    }

    @Test
    void testBranchingTreeDetection() {
        BlockPos basePos = MockBlockPos.at(10, 64, 10);

        // Trunk
        mockLevel.withBlock(basePos, trunkBlock.get());
        mockLevel.withBlock(basePos.above(), trunkBlock.get());

        // Branch to East
        BlockPos branchStart = basePos.above().east();
        mockLevel.withBlock(branchStart, branchBlock.get());

        // Leaves on branch
        mockLevel.withBlock(branchStart.east(), leavesBlock.get());

        TreeStructure tree = TreeStructureDetector.detectTree(basePos, mockLevel.get());

        assertTrue(tree.hasTrunk());
        assertTrue(tree.hasBranches());
        assertTrue(tree.hasLeaves());
        assertEquals(2, tree.getComponentsOfType(TreeComponentType.TRUNK).size());
        assertEquals(1, tree.getComponentsOfType(TreeComponentType.BRANCH).size());
        assertEquals(1, tree.getComponentsOfType(TreeComponentType.LEAVES).size());
    }

    @Test
    void testDisconnectedBlocks_ShouldNotBeIncluded() {
        BlockPos basePos = MockBlockPos.at(0, 64, 0);

        // Trunk
        mockLevel.withBlock(basePos, trunkBlock.get());

        // Disconnected leaf (too far)
        BlockPos farLeaf = basePos.above().above().above();
        mockLevel.withBlock(farLeaf, leavesBlock.get());

        TreeStructure tree = TreeStructureDetector.detectTree(basePos, mockLevel.get());

        assertTrue(tree.hasTrunk());
        assertEquals(1, tree.getTotalBlocks());
        // Far leaf should not be included as it's not connected
    }

    @Test
    void testIsValidTree() {
        BlockPos basePos = MockBlockPos.at(0, 64, 0);

        // Valid Tree
        // L L L
        // L T L
        //   T
        mockLevel.withBlock(basePos, trunkBlock.get());
        mockLevel.withBlock(basePos.above(), trunkBlock.get());
        BlockPos topTrunk = basePos.above();
        mockLevel.withBlock(topTrunk.north(), leavesBlock.get());
        mockLevel.withBlock(topTrunk.south(), leavesBlock.get());
        mockLevel.withBlock(topTrunk.east(), leavesBlock.get());
        mockLevel.withBlock(topTrunk.west(), leavesBlock.get());
        mockLevel.withBlock(topTrunk.above(), leavesBlock.get());

        TreeStructure tree = TreeStructureDetector.detectTree(basePos, mockLevel.get());
        assertTrue(TreeStructureDetector.isValidTree(tree));

        // Invalid Tree (No Leaves)
        mockLevel = new MockLevel();
        mockLevel.withBlock(basePos, trunkBlock.get());
        mockLevel.withBlock(basePos.above(), trunkBlock.get());

        tree = TreeStructureDetector.detectTree(basePos, mockLevel.get());
        assertFalse(TreeStructureDetector.isValidTree(tree)); // Should fail because no leaves
    }

    @Test
    void testEstimateSpecies() {
        BlockPos basePos = MockBlockPos.at(0, 64, 0);

        // Tall thin tree (Spruce-like)
        // T
        // T
        // T
        // T
        // L
        mockLevel.withBlock(basePos, trunkBlock.get());
        mockLevel.withBlock(basePos.above(), trunkBlock.get());
        mockLevel.withBlock(basePos.above(2), trunkBlock.get());
        mockLevel.withBlock(basePos.above(3), trunkBlock.get());
        mockLevel.withBlock(basePos.above(4), leavesBlock.get()); // Just one leaf at top

        TreeStructure tree = TreeStructureDetector.detectTree(basePos, mockLevel.get());
        // Height 5, Width 1. Aspect Ratio 5. Leaf Density 1/5 = 0.2.
        // Aspect > 3.0 (5 > 3), LeafDensity < 0.4 (0.2 < 0.4) -> Spruce-like

        assertEquals("spruce_like", TreeStructureDetector.estimateSpecies(tree));

        // Tall bush (Bush-like) - high density but tall-ish
        // L
        // L
        // T
        mockLevel = new MockLevel();
        mockLevel.withBlock(basePos, trunkBlock.get()); // Base trunk
        mockLevel.withBlock(basePos.above(), leavesBlock.get());
        mockLevel.withBlock(basePos.above(2), leavesBlock.get());

        tree = TreeStructureDetector.detectTree(basePos, mockLevel.get());
        // Height 3. Width 1. Aspect 3.0.
        // Blocks 3. Leaves 2. Density 0.66.
        // Aspect 3.0. Not > 3.0.
        // Not > 2.0 (density fail).
        // Not < 1.5.
        // Height 3 < 8. Density 0.66 > 0.5. -> bush_like.

        assertEquals("bush_like", TreeStructureDetector.estimateSpecies(tree));
    }
}
