package com.deco2800.potatoes.managers;

import com.deco2800.potatoes.entities.AbstractEntity;
import com.deco2800.potatoes.util.Box3D;
import com.deco2800.potatoes.util.Line;
import com.deco2800.potatoes.util.MinimumSpanningTree;
import com.deco2800.potatoes.util.MinimumSpanningTree.Vertex;
import com.deco2800.potatoes.util.Path;
import com.deco2800.potatoes.worlds.AbstractWorld;



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
    private MinimumSpanningTree treeMaker;
    private ArrayList<Box3D> nodes = new ArrayList<>();
    private ArrayList<Line> obstacle = new ArrayList<>();
    private Map<DoubleBox3D, Float> edges = new HashMap<>();
    private Box3D lastPlayerPosition;
    private int numberOfRandomNodes = 40;


    /**
     *
     */
    public PathManager() {
        spanningTree = new HashMap<>();
        nodes = new ArrayList<>();
        edges = new HashMap<>();
    }



    private static float nodeOffset = (float) 0.5;

    /**
     * Populates the internal graph representation of the path manager, based on the initial world state.
     * Should be run after loading the map
     */
    public void initialise(Box3D player) {
        this.lastPlayerPosition = player;
        nodes.clear();
        edges.clear();

        AbstractWorld world = GameManager.get().getWorld();

        nodes.add(player);

        // Loop through entities, make lines going through center to nodeOffset for
        // top and bottom, left and right.
        for (AbstractEntity e : world.getEntities().values()) {
            if (e.isStaticCollideable()) {
                obstacle.add(
                        new Line(
                                (e.getPosX() - this.nodeOffset),
                                 e.getPosY(),
                                (e.getPosX() + this.nodeOffset),
                                 e.getPosY()
                        ));
                obstacle.add(
                        new Line(
                                e.getPosX(),
                                (e.getPosY() - this.nodeOffset),
                                e.getPosX(),
                                (e.getPosY() + this.nodeOffset)
                        ));
            }
        }

        //potentially make random nodes here
        for (int i = 0; i < numberOfRandomNodes; i++) {
            nodes.add(new Box3D(
                    (float) (Math.random() * world.getWidth()),      // x coordinate
                    (float) (Math.random() * world.getLength()),     // y coordinate
                    0,
                    1,
                    1,
                    1
            ));
        }

        //loop through all nodes and all entities, removing any nodes that intersect with the entity
        Set<Box3D> removedNodes = new HashSet<>();
        for (Box3D node : this.nodes) {
            for (AbstractEntity entity : world.getEntities().values()) {
                if (entity.isStaticCollideable() && entity.getBox3D().overlaps(node)) {
                    removedNodes.add(node);
                }
            }
        }

        for (Box3D node : removedNodes) {
            this.nodes.remove(node);
        }

        // create a new mini spanning tree
        treeMaker = new MinimumSpanningTree(nodes.size());
        // Add the nodes to the vertexList.
        for (int i = 0; i < nodes.size(); i++) {
            treeMaker.addVertex(nodes.get(i), i);
        }
    }

    /**
     * To be run on every game tick, the pathManager needs to know the location of the player
     * It set the path of sqirrels on the map that have run into a wall //TODO
     *
     * @param player coordinates of the player
     */
    public void onTick(Box3D player) {
        // AbstractWorld world = GameManager.get().getWorld();

        // //if player hasn't moved since last tick, can skip this
        // if (!player.equals(lastPlayerPosition)) {
        //     lastPlayerPosition = player;

        //     //populates directNode, nodes which have a direct line of sight
        //     boolean doesCollide;
        //     for (Box3D node : this.nodes) {
        //         doesCollide = false;
        //         for (AbstractEntity entity : world.getEntities().values()) {
        //             if (entity.isStaticCollideable() &&
        //                     entity.getBox3D().doesIntersectLine(node.getX(), node.getY(), 0, player.getX(), player.getY(), 0)) {
        //                 doesCollide = true;
        //                 break;
        //             }
        //         }
        //         if (!doesCollide) {
        //             this.directNode.put(node, true);
        //         }
        //     }
        // }

    }

    /**
     * Takes as inputs a representation of the graph of internode connections that can be used to create paths.
     * Calculates the minimum spanning tree of the graph, and then stores this internally so it can be used to generate
     * new paths for enemies.
     *
     * @param start The initial vertex within the graph where the search starts, and where each generated path will end.
     * @param vertices The vertices of the graph of internode connections.
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
            vertices.remove(current);
            workQueue.remove(current);

            // TODO make this more efficient by improving the way we store the graph
            for (Box3D other : vertices) {

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
        ArrayDeque<Box3D> path = new ArrayDeque<>();
        Vertex startVertex;
        Vertex goalVertex;
        Box3D next;

        if (spanningTree.size() == 0 || !goal.equals(lastPlayerPosition)) {
            initialise(goal);
        }
        // Find the closest Vertices to the start and goal points.
        startVertex = treeMaker.findClosest(start);
        goalVertex = treeMaker.findClosest(goal);
        // build the minimum spanning tree from the graph - and set the spanningTree variable
        spanningTree = treeMaker.createTree(goalVertex);
        // Add the starting point to the path.
        path.add(startVertex.getEntry());
        // If the spanning tree has only two entries
        // return a new path with the start and end point.
        if (spanningTree.size() < 2) {
            path.add(goalVertex.getEntry());
            return new Path(path);
        }
        // Add extra path points as needed.
        // Set next as the value returned from start as
        // the key to spanningTree.
        next = spanningTree.get(startVertex.getEntry());
        while (!(next.equals(goalVertex.getEntry()))) {
            path.add(next);
            next = spanningTree.get(next);
        }
        return new Path(path);
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
