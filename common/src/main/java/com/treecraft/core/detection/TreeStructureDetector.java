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
}