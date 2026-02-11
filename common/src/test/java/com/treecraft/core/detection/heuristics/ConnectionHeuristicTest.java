package com.treecraft.core.detection.heuristics;

import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.test.util.MockBlockPos;
import com.treecraft.core.test.util.MockBlockState;
import com.treecraft.core.test.util.MockLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ConnectionHeuristicTest {

    private ConnectionHeuristic heuristic;
    private MockLevel mockLevel;
    private BlockPos centerPos;
    private MockBlockState centerBlock;

    @BeforeEach
    void setUp() {
        com.treecraft.core.test.util.TestBootstrap.init();
        heuristic = new ConnectionHeuristic();
        mockLevel = new MockLevel();
        centerPos = MockBlockPos.at(0, 64, 0);
        centerBlock = new MockBlockState();

        // Ensure the mock state claims to be the mock block
        when(centerBlock.get().is(centerBlock.getBlock())).thenReturn(true);

        // Setup center block in level
        mockLevel.withBlock(centerPos, centerBlock.get());
    }

    @Test
    void testVerticalConnection_ShouldDetectTrunk() {
        // Add block above
        BlockPos above = centerPos.above();
        mockLevel.withBlock(above, centerBlock.get());

        HeuristicResult result = heuristic.evaluate(centerBlock.get(), mockLevel.get(), centerPos);
        assertEquals(TreeComponentType.TRUNK, result.getType());
        assertEquals(0.6f, result.getConfidence());
    }

    @Test
    void testHorizontalConnection_ShouldDetectBranch() {
        // Add block to east
        BlockPos east = centerPos.east();
        mockLevel.withBlock(east, centerBlock.get());

        HeuristicResult result = heuristic.evaluate(centerBlock.get(), mockLevel.get(), centerPos);
        assertEquals(TreeComponentType.BRANCH, result.getType());
        assertEquals(0.5f, result.getConfidence());
    }

    @Test
    void testVerticalAndHorizontal_ShouldPrioritizeTrunk() {
        // Add block above, below (2 vertical) and east (1 horizontal)
        mockLevel.withBlock(centerPos.above(), centerBlock.get());
        mockLevel.withBlock(centerPos.below(), centerBlock.get());
        mockLevel.withBlock(centerPos.east(), centerBlock.get());

        HeuristicResult result = heuristic.evaluate(centerBlock.get(), mockLevel.get(), centerPos);
        assertEquals(TreeComponentType.TRUNK, result.getType());
    }

    @Test
    void testNoConnections_ShouldReturnUnknown() {
        // Only center block exists
        HeuristicResult result = heuristic.evaluate(centerBlock.get(), mockLevel.get(), centerPos);
        assertEquals(TreeComponentType.UNKNOWN, result.getType());
    }

    @Test
    void testDifferentBlockConnection_ShouldIgnored() {
        // Add block above but different type (using standard AIR or STONE, which won't match the mock block)
        BlockPos above = centerPos.above();
        mockLevel.withBlock(above, Blocks.STONE.defaultBlockState());

        HeuristicResult result = heuristic.evaluate(centerBlock.get(), mockLevel.get(), centerPos);
        assertEquals(TreeComponentType.UNKNOWN, result.getType());
    }
}
