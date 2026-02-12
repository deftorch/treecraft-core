package com.treecraft.core.detection;

import com.treecraft.core.api.TreeComponentType;
import com.treecraft.core.api.events.TreeCraftEvents;
import com.treecraft.core.api.events.TreeStructureDetectedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class TreeStructureDetector {

    public static TreeStructure detectTree(BlockPos startPos, Level level) {
        BlockPos basePos = findTreeBase(startPos, level);

        TreeStructure tree = new TreeStructure(basePos);

        Queue<BlockPos> toProcess = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        toProcess.add(basePos);
        visited.add(basePos);

        int blocksProcessed = 0;
        int maxBlocks = com.treecraft.core.config.CoreConfig.maxTreeSize;

        while (!toProcess.isEmpty() && blocksProcessed < maxBlocks) {
            BlockPos pos = toProcess.poll();
            blocksProcessed++;

            BlockState blockState = level.getBlockState(pos);
            TreeComponentType type = TreeBlockDetector.getInstance().detect(blockState, level, pos);

            if (type != TreeComponentType.UNKNOWN) {
                tree.addComponent(pos, blockState, type);
                addConnectedBlocks(pos, type, toProcess, visited);
            }
        }

        if (tree.isReasonableSize()) {
            TreeCraftEvents.post(new TreeStructureDetectedEvent(tree, level, startPos));
        }

        return tree;
    }

    private static void addConnectedBlocks(
        BlockPos pos,
        TreeComponentType type,
        Queue<BlockPos> queue,
        Set<BlockPos> visited
    ) {
        Direction[] directions = switch (type) {
            case TRUNK -> new Direction[]{
                Direction.UP, Direction.DOWN,
                Direction.NORTH, Direction.SOUTH,
                Direction.EAST, Direction.WEST
            };
            case BRANCH -> Direction.values();
            case LEAVES -> Direction.values();
            case ROOT -> new Direction[]{
                Direction.DOWN,
                Direction.NORTH, Direction.SOUTH,
                Direction.EAST, Direction.WEST
            };
            default -> new Direction[0];
        };

        for (Direction dir : directions) {
            BlockPos neighbor = pos.relative(dir);
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                queue.add(neighbor);
            }
        }
    }

    public static BlockPos findTreeBase(BlockPos startPos, Level level) {
        BlockPos current = startPos;
        BlockPos lowest = startPos;

        for (int i = 0; i < 256; i++) {
            BlockPos below = current.below();
            if (below.getY() < level.getMinBuildHeight()) break;

            BlockState state = level.getBlockState(below);

            TreeComponentType type = TreeBlockDetector.getInstance().detect(state, level, below);

            if (type == TreeComponentType.TRUNK || type == TreeComponentType.ROOT) {
                lowest = below;
                current = below;
            } else {
                break;
            }
        }

        return lowest;
    }

    /**
     * Validate if structure is a valid tree
     */
    public static boolean isValidTree(TreeStructure structure) {
        // Minimum requirements
        if (!structure.hasTrunk()) {
            return false;
        }

        if (!structure.hasLeaves()) {
            return false;
        }

        // Size check
        if (!structure.isReasonableSize()) {
            return false;
        }

        // Structure check - trunk should be continuous
        if (!hasContinuousTrunk(structure)) {
            return false;
        }

        // Leaves should be above/around trunk
        if (!leavesPositionedCorrectly(structure)) {
            return false;
        }

        return true;
    }

    private static boolean hasContinuousTrunk(TreeStructure structure) {
        Set<BlockPos> trunkBlocks = structure.getComponentsOfType(TreeComponentType.TRUNK);

        if (trunkBlocks.isEmpty()) {
            return false;
        }

        // Find lowest trunk block
        BlockPos lowest = trunkBlocks.stream()
            .min(Comparator.comparingInt(BlockPos::getY))
            .orElseThrow();

        // Check vertical continuity
        Set<BlockPos> reachable = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(lowest);
        reachable.add(lowest);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();

            // Check adjacent trunk blocks
            for (Direction dir : Direction.values()) {
                BlockPos next = pos.relative(dir);
                if (trunkBlocks.contains(next) && !reachable.contains(next)) {
                    reachable.add(next);
                    queue.add(next);
                }
            }
        }

        // At least 80% of trunk should be reachable
        return reachable.size() >= trunkBlocks.size() * 0.8;
    }

    private static boolean leavesPositionedCorrectly(TreeStructure structure) {
        Set<BlockPos> leaves = structure.getComponentsOfType(TreeComponentType.LEAVES);
        Set<BlockPos> trunk = structure.getComponentsOfType(TreeComponentType.TRUNK);

        if (leaves.isEmpty() || trunk.isEmpty()) {
            return false;
        }

        // Get average trunk height
        int avgTrunkY = trunk.stream()
            .mapToInt(BlockPos::getY)
            .sum() / trunk.size();

        // Get average leaves height
        int avgLeavesY = leaves.stream()
            .mapToInt(BlockPos::getY)
            .sum() / leaves.size();

        // Leaves should generally be at or above trunk level
        return avgLeavesY >= avgTrunkY - 2;
    }

    /**
     * Estimate tree species from structure
     */
    public static String estimateSpecies(TreeStructure structure) {
        // Analyze structure characteristics
        int height = structure.getHeight();
        int width = structure.getWidth();
        float aspectRatio = (float) height / (width == 0 ? 1 : width);

        Set<BlockPos> trunk = structure.getComponentsOfType(TreeComponentType.TRUNK);
        Set<BlockPos> leaves = structure.getComponentsOfType(TreeComponentType.LEAVES);

        float leafDensity = (float) leaves.size() / structure.getTotalBlocks();

        // Simple classification based on characteristics
        if (aspectRatio > 3.0 && leafDensity < 0.4) {
            return "spruce_like";
        } else if (aspectRatio > 2.0 && leafDensity < 0.5) {
            return "pine_like";
        } else if (aspectRatio < 1.5 && leafDensity > 0.6) {
            return "oak_like";
        } else if (height < 8 && leafDensity > 0.5) {
            return "bush_like";
        } else {
            return "generic_tree";
        }
    }
}
