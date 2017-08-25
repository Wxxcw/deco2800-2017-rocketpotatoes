package com.deco2800.potatoes.managers;

import com.deco2800.potatoes.util.Box3D;
import com.deco2800.potatoes.util.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to manage the creation and allocation of paths for enemies to follow.
 */
public class PathManager extends Manager {


    /**
     * Updates the internal graph representation of the path manager, based on world state.
     */
    private void updateGraph() {

    }

    /**
     * Allocates a path to a given entity. Not guaranteed to be the optimal path, but at the time it is created it will
     * have no collisions. Paths cannot be shared by multiple entities.
     *
     * @param start The starting point of the entity - where the path is going to begin.
     * @param goal The goal of the entity - where the path is going to end.
     * @return The path object itself, which can then be followed.
     */
    public Path generatePath(Box3D start, Box3D goal) {
        List<Box3D> nodes = new ArrayList<>();
        nodes.add(start);
        nodes.add(goal);
        return new Path(nodes);
    }
}
