package com.deco2800.potatoes.managers;

import com.deco2800.potatoes.entities.AbstractEntity;
import com.deco2800.potatoes.util.Box3D;
import com.deco2800.potatoes.util.Path;
import com.deco2800.potatoes.worlds.AbstractWorld;
import org.lwjgl.Sys;

import java.util.*;



/**
 * Object to manage the creation and allocation of paths for enemies to follow.
 */
public class PathManager extends Manager {
    /* The PathManager stores a minimum spanning tree, representing all the internode connections that can be used to
     * create paths. This is represented as a hashmap in memory, where the keys are nodes, and the values are the
     * parent nodes of the keys within the minimum spanning tree.
     *
     * This tree is centered around the node storing the player's location for now, though in future that will be
     * expanded so that multiple different goals can be set.
     */
    private Map<Box3D, Box3D> spanningTree;

    public PathManager() {
        spanningTree = new HashMap<>();
    }

    private Set<Box3D> nodes = new HashSet<>();
    private Map<DoubleBox3D, Float> edges = new HashMap<>();

    private AbstractWorld world;

    private static float nodeOffset = (float) 0.5;

    /**
     * Populates the internal graph representation of the path manager, based on the initial world state.
     * Should be run after loading the map
     */
    public void initialiseGraph() {
        this.nodes = new HashSet<>();
        this.edges = new HashMap<>();

        //add points in corners of map
        nodes.add(new Box3D(0 + this.nodeOffset, 0 + this.nodeOffset, //left
                0, 0, 0, 0));
        nodes.add(new Box3D(this.world.getWidth() - this.nodeOffset, 0 + this.nodeOffset, //top
                0, 0, 0, 0));
        nodes.add(new Box3D(0 + this.nodeOffset, this.world.getLength() - this.nodeOffset, //bottom
                0, 0, 0, 0));
        nodes.add(new Box3D(this.world.getWidth() - this.nodeOffset, this.world.getLength() - this.nodeOffset, //right
                0, 0, 0, 0));

        //loop through entities, put nodes off of corners
        for (AbstractEntity e : world.getEntities().values()) {
            if (e.isStaticCollideable()) {
                nodes.add(new Box3D(e.getPosX() - this.nodeOffset, e.getPosY() - this.nodeOffset, //left
                        0, 0, 0, 0));
                nodes.add(new Box3D(e.getPosX() + e.getXLength() + this.nodeOffset, e.getPosY() - this.nodeOffset, //top
                        0, 0, 0, 0));
                nodes.add(new Box3D(e.getPosX() - this.nodeOffset, e.getPosY() + e.getYLength() + this.nodeOffset, //bottom
                        0, 0, 0, 0));
                nodes.add(new Box3D(e.getPosX() + e.getXLength() + this.nodeOffset, e.getPosY() + e.getYLength() + this.nodeOffset, //right
                        0, 0, 0, 0));
            }
        }


        //potentially make random nodes here

        //loop through all nodes and all entities, removing any nodes that intersect with the entity
        Set<Box3D> removedNodes = new HashSet<>();
        for (Box3D node : this.nodes) {
            for (AbstractEntity e : world.getEntities().values()) {
                if (e.isStaticCollideable() && e.getBox3D().overlaps(node)) {
                    removedNodes.add(node);
                }
            }
        }

        for (Box3D node : removedNodes) {
            this.nodes.remove(node);
        }


        //loop through every combination of 2 nodes & every entity check if the edge between the two nodes is valid
        boolean doesCollide;
        float dist;
        for (Box3D node1 : this.nodes) {
            for (Box3D node2 : this.nodes) {
                if (node1 == node2) { break; }
                doesCollide = false;
                for (AbstractEntity e : world.getEntities().values()) {
                    if (e.isStaticCollideable() &&
                            e.getBox3D().doesIntersectLine(node1.getX(),node1.getY(),0,node2.getX(), node2.getY(),0)) {
                        doesCollide = true;
                        break;
                    }
                }
                if(!doesCollide) {
                    dist = node1.distance(node2);
                    this.edges.put(new DoubleBox3D(node1, node2), dist);
                    this.edges.put(new DoubleBox3D(node2, node1), dist);
                }
            }
        }

        // build the minimum spanning tree from the graph - and set the spanningTree variable.
    }

    /**
     * Takes as inputs a representation of the graph of internode connections that can be used to create paths.
     * Calculates the minimum spanning tree of the graph, and then stores this internally so it can be used to generate
     * new paths for enemies.
     *
     * @param start The initial vertex within the graph where the search starts, and where each generated path will end.
     * @param vertices The vertices of the graph of internode connections. This set is effectively destroyed by the
     * time this function has completed.
     * @param edges The edges of the graph of internode connections.
     */
    private void optimiseGraph(Box3D start, Set<Box3D> vertices, Map<DoubleBox3D, Float> edges) {
        List<Box3D> workQueue = new ArrayList<>();
        Map<Box3D, Float> distances = new HashMap<>();

        spanningTree.clear();
        workQueue.add(start);
        distances.put(start, new Float(0));

        while (workQueue.size() > 0) {
            // find the closest thing within the work queue
            Box3D current = workQueue.get(0);
            for (Box3D other : workQueue) {
                if (distances.get(other) < distances.get(current)) {
                    current = other;
                }
            }
            workQueue.remove(current);

            // TODO make this more efficient by improving the way we store the graph
            for (Box3D other : vertices) {
                if (other.equals(current)) {
                    continue;
                }

                DoubleBox3D pair = new DoubleBox3D(current, other);

                if (!edges.containsKey(pair)) {
                    continue;
                }

                Float distance = edges.get(pair);

                if (!distances.containsKey(other) || distances.get(other) > distance) {
                    spanningTree.put(other, current);
                    distances.put(other, distance);
                }

                if (!workQueue.contains(other)) {
                    workQueue.add(other);
                }
            }

        }



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
        ArrayDeque<Box3D> nodes = new ArrayDeque<>();
        if (spanningTree.size() == 0) {
            nodes.add(start);
            nodes.add(goal);
        } else {
            nodes.add(start);
            Box3D closest = null;
            for (Box3D other : spanningTree.keySet()) {
                if (closest == null || closest.distance(start) > other.distance(start)) {
                    closest = other;
                }
            }
            do {
                nodes.add(closest);
                closest = spanningTree.get(closest);
            } while (closest != null);
        }
        return new Path(nodes);
    }

    /**
     * Sets the world state so that it can be accessed when generating paths
     *
     * @param world the current state of the world
     */
    public void setWorld(AbstractWorld world) {
        this.world = world;
    }

    /**
     * An unordered pair of two 3D boxes. Used as the keys in the mapping from edges to weights in the interal graph
     * representation. Designed so that DoubleBox3D(A, B) will compare as equal to DoubleBox3D(B, A).
     */
    private class DoubleBox3D {

        // The two points in the pair. The names are used to represent the order in which they were passed as arguments
        // to the constructor.
        private Box3D first;
        private Box3D second;

        /**
         * Creates a new DoubleBox3D with two given points.
         *
         * @param first One of the points in the pair.
         * @param second The other point in the pair.
         */
        public DoubleBox3D(Box3D first, Box3D second) {
            this.first = new Box3D(first);
            this.second = new Box3D(second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second) ^ Objects.hash(second, first);
        }

        /**
         * Gets the first Box3D of this pair.
         *
         * @return Returns a copy of the first Box3D.
         */
        public Box3D getFirst() {
            return new Box3D(this.first);
        }

        /**
         * Gets the second Box3D of this pair.
         *
         * @return Returns a copy of the second Box3D.
         */
        public Box3D getSecond() {
            return new Box3D(this.second);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof DoubleBox3D)) {
                return false;
            }

            DoubleBox3D that = (DoubleBox3D) o;

            return (this.getFirst().equals(that.getFirst()) && this.getSecond().equals(that.getSecond())) ||
                    (this.getFirst().equals(that.getSecond()) && this.getSecond().equals(that.getFirst()));
        }
    }
}
