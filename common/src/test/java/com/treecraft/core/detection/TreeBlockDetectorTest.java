package com.treecraft.core.detection;

import com.treecraft.core.api.HeuristicResult;
import com.treecraft.core.api.IDetectionHeuristic;
import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.api.events.TreeCraftEvents;
import com.treecraft.core.api.events.TreeDetectedEvent;
import com.treecraft.core.registry.TreeBlockRegistry;
import com.treecraft.core.test.util.MockBlockState;
import com.treecraft.core.test.util.MockBlockPos;
import com.treecraft.core.test.util.MockLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TreeBlockDetectorTest {

    private TreeBlockDetector detector;
    private MockBlockState mockBlockState;
    private MockLevel mockLevel;
    private BlockPos blockPos;
    private MockedStatic<TreeCraftEvents> mockEvents;
    private MockedStatic<TreeBlockRegistry> mockRegistry;

    @BeforeEach
    void setUp() {
        com.treecraft.core.test.util.TestBootstrap.init();
        detector = TreeBlockDetector.getInstance();
        detector.resetForTest();
        mockBlockState = new MockBlockState();
        mockLevel = new MockLevel();
        blockPos = MockBlockPos.at(0, 0, 0);

        mockEvents = mockStatic(TreeCraftEvents.class, withSettings().lenient());
        mockRegistry = mockStatic(TreeBlockRegistry.class, withSettings().lenient());
    }

    @AfterEach
    void tearDown() {
        mockEvents.close();
        mockRegistry.close();
    }

    @Test
    void testSingletonInstance_ShouldReturnSameInstance() {
        TreeBlockDetector instance1 = TreeBlockDetector.getInstance();
        TreeBlockDetector instance2 = TreeBlockDetector.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testHeuristicRegistration_ShouldAddToList() {
        IDetectionHeuristic heuristic = mock(IDetectionHeuristic.class);
        detector.registerHeuristic(heuristic);

        when(heuristic.evaluate(any(), any(), any())).thenReturn(new HeuristicResult(TreeComponentType.TRUNK, 0.9f));

        TreeComponentType type = detector.detectBlockType(mockBlockState.get());
        assertEquals(TreeComponentType.TRUNK, type);
        verify(heuristic).evaluate(any(), any(), any());
    }

    @Test
    void testMultiHeuristicScoring_ShouldAggregateScores() {
        IDetectionHeuristic heuristic1 = mock(IDetectionHeuristic.class);
        IDetectionHeuristic heuristic2 = mock(IDetectionHeuristic.class);
        IDetectionHeuristic heuristic3 = mock(IDetectionHeuristic.class);

        when(heuristic1.evaluate(any(), any(), any())).thenReturn(new HeuristicResult(TreeComponentType.TRUNK, 0.3f));
        when(heuristic2.evaluate(any(), any(), any())).thenReturn(new HeuristicResult(TreeComponentType.TRUNK, 0.3f));
        when(heuristic3.evaluate(any(), any(), any())).thenReturn(new HeuristicResult(TreeComponentType.LEAVES, 0.5f));

        detector.registerHeuristic(heuristic1);
        detector.registerHeuristic(heuristic2);
        detector.registerHeuristic(heuristic3);

        TreeComponentType type = detector.detectBlockType(mockBlockState.get());
        assertEquals(TreeComponentType.TRUNK, type); // 0.6 > 0.5
    }

    @Test
    void testConfidenceThreshold_ShouldFilterLowScores() {
        IDetectionHeuristic heuristic = mock(IDetectionHeuristic.class);
        when(heuristic.evaluate(any(), any(), any())).thenReturn(new HeuristicResult(TreeComponentType.TRUNK, 0.4f));
        detector.registerHeuristic(heuristic);

        TreeComponentType type = detector.detectBlockType(mockBlockState.get());
        assertEquals(TreeComponentType.UNKNOWN, type);
    }

    @Test
    void testCaching_ShouldReturnCachedResult() {
        IDetectionHeuristic heuristic = mock(IDetectionHeuristic.class);
        when(heuristic.evaluate(any(), any(), any())).thenReturn(new HeuristicResult(TreeComponentType.TRUNK, 0.9f));
        detector.registerHeuristic(heuristic);

        // First call
        detector.detectBlockType(mockBlockState.get());
        // Second call
        detector.detectBlockType(mockBlockState.get());

        // Should be called only once due to caching (detectBlockType calls detect with level=null, which uses cache)
        verify(heuristic, times(1)).evaluate(any(), any(), any());
    }

    /*
    @Test
    void testCacheExpiration_ShouldRecompute() {
        // Hard to test without controlling ticker/clock.
    }
    */

    @Test
    void testRegistryOverride_ShouldUseRegistry() {
        mockRegistry.when(() -> TreeBlockRegistry.isTreeBlock(any())).thenReturn(true);
        mockRegistry.when(() -> TreeBlockRegistry.getType(any())).thenReturn(TreeComponentType.ROOT);

        TreeComponentType type = detector.detectBlockType(mockBlockState.get());
        assertEquals(TreeComponentType.ROOT, type);
    }

    @Test
    void testEventEmission_ShouldFireOnDetection() {
        IDetectionHeuristic heuristic = mock(IDetectionHeuristic.class);
        when(heuristic.evaluate(any(), any(), any())).thenReturn(new HeuristicResult(TreeComponentType.TRUNK, 0.9f));
        detector.registerHeuristic(heuristic);

        // detectBlockType passes null level/pos, so no event.
        // We must call detect(block, level, pos)
        detector.detect(mockBlockState.get(), mockLevel.get(), blockPos);

        mockEvents.verify(() -> TreeCraftEvents.post(any(TreeDetectedEvent.class)));
    }

    @Test
    void testExceptionHandling_ShouldContinueOnHeuristicError() {
        IDetectionHeuristic heuristic1 = mock(IDetectionHeuristic.class);
        IDetectionHeuristic heuristic2 = mock(IDetectionHeuristic.class);

        when(heuristic1.evaluate(any(), any(), any())).thenThrow(new RuntimeException("Oops"));
        when(heuristic2.evaluate(any(), any(), any())).thenReturn(new HeuristicResult(TreeComponentType.TRUNK, 0.9f));

        detector.registerHeuristic(heuristic1);
        detector.registerHeuristic(heuristic2);

        TreeComponentType type = detector.detectBlockType(mockBlockState.get());
        assertEquals(TreeComponentType.TRUNK, type);
    }
}
