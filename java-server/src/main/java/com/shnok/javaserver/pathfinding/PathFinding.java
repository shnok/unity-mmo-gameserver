package com.shnok.javaserver.pathfinding;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.pathfinding.node.FastNodeList;
import com.shnok.javaserver.pathfinding.node.Node;
import com.shnok.javaserver.util.VectorUtils;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;
import java.util.List;

@Log4j2
public class PathFinding {
    private static PathFinding instance;

    public static PathFinding getInstance() {
        if (instance == null) {
            instance = new PathFinding();
        }
        return instance;
    }

    public List<Point3D> findPath(Point3D from, Point3D to) {
        return findPath(from, to, 0);
    }

    public List<Point3D> findPath(Point3D from, Point3D to, float stopAtRange) {
        try {
            String startZone = Geodata.getInstance().getCurrentZone(from);
            Node start = Geodata.getInstance().getNodeAt(from, startZone);
            if(Config.PRINT_PATHFINDER_LOGS) {
                log.debug("Startnode: {}", start.getWorldPosition());
            }
            String endZone = Geodata.getInstance().getCurrentZone(to);
            Node end = Geodata.getInstance().getNodeAt(to, endZone);
            if(Config.PRINT_PATHFINDER_LOGS) {
                log.debug("EndNode: {}", end.getWorldPosition());
            }
            return searchByClosest(start, end, stopAtRange);

        } catch (Exception e) {
            if(Config.PRINT_PATHFINDER_LOGS) {
                log.debug("{}.", e.getMessage());
            }
            return null;
        }
    }

    public List<Point3D> searchByClosest(Node start, Node end, float stopAtRange) {
        // List of Visited Nodes
        FastNodeList visitedNodes = new FastNodeList(550);

        // List of Nodes to Visit
        LinkedList<Node> nodesToVisit = new LinkedList<>();
        nodesToVisit.add(start);

        boolean added;

        int i = 0;
        int maxIterations = 1500;

        while (i++ < maxIterations) {
            Node node;

            // Get and remove node from the nodesToVisit list
            try {
                node = nodesToVisit.removeFirst();
            } catch (Exception e) {
                // No Path found
                if(Config.PRINT_PATHFINDER_LOGS) {
                    log.debug("No path found - {} to {}.", start.getCenter(), end.getCenter());
                }
                return null;
            }

            // Current node is the destination node
            // Path was found
            if (node.equals(end)) {
                if(Config.PRINT_PATHFINDER_LOGS) {
                    log.debug("Found path - {} to {} after {} iteration(s).", start.getCenter(), end.getCenter(), i);
                }
                return constructPath(node);
            }

            //log.debug("Visiting node: {}", node.getNodeIndex());
            //log.debug("Distance to destination: {}", VectorUtils.calcDistance(node.getCenter(), end.getCenter()));

            // Add node to visited nodes
            visitedNodes.add(node);

            Node[] neighbors = findNodeNeighbors(node.getCenter());
            if (neighbors == null) {
                continue;
            }

            // Iterate through node's neighbors
            for (Node neighbor : neighbors) {
                if (neighbor == null) {
                    continue;
                }

                // check if neighbor was visited and needs to be visited
                if (!visitedNodes.containsRev(neighbor) && !nodesToVisit.contains(neighbor)) {

                    // Calculate neighbor node cost
                    neighbor.setParentNode(node);

                    neighbor.setCost(VectorUtils.calcDistance(neighbor.getCenter(), end.getCenter()));

                    // insert neighbor into the nodes to visit based on its cost
                    added = false;
                    for (int index = 0; index < nodesToVisit.size(); index++) {
                        /*if (i < 10) {
                            log.debug("Parent id: {} - Neighbor id: {}", neighbor.getParentNode().getNodeIndex(), neighbor.getNodeIndex());
                            log.debug("Neighbor node cost: {} - Node to visit cost: {}",
                                    neighbor.getCost(), nodesToVisit.get(index).getCost());
                        }*/

                        if (neighbor.getCost() < nodesToVisit.get(index).getCost()) {
                            nodesToVisit.add(index, neighbor);
                            added = true;
                            break;
                        }
                    }

                    // add neighbor at the end of the nodes to visit list
                    if (!added) {
                        nodesToVisit.addLast(neighbor);
                    }
                }
            }
        }

        // No Path found
        if(Config.PRINT_PATHFINDER_LOGS) {
            log.debug("No path found (max iterations reached)");
        }
        return null;
    }

    public Node[] findNodeNeighbors(Point3D nodePos) {
        Node[] returnList = new Node[8];
        short i = 0;

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0)
                    continue;

                try {
                    Point3D neighborPos = new Point3D(nodePos.getX() + x * Config.GEODATA_NODE_SIZE,
                            nodePos.getY(),
                            nodePos.getZ() + z * Config.GEODATA_NODE_SIZE);
                    String mapId = Geodata.getInstance().getCurrentZone(neighborPos);

                    Node node = null;
                    try {
                        node = Geodata.getInstance().getNodeAt(neighborPos, mapId);
                    } catch(Exception e) {}

                    if (node == null) {
                        neighborPos.setY(nodePos.getY() + Config.GEODATA_NODE_SIZE);
                        try {
                            node = Geodata.getInstance().getNodeAt(neighborPos, mapId);
                        } catch(Exception e) {}
                    }

                    if (node == null) {
                        neighborPos.setY(nodePos.getY() - Config.GEODATA_NODE_SIZE);
                        try {
                            node = Geodata.getInstance().getNodeAt(neighborPos, mapId);
                        } catch(Exception e) {}
                    }

                    if(node != null) {
                        returnList[i++] = node;
                    }
                } catch (Exception e) {
                    if(Config.PRINT_PATHFINDER_LOGS) {
                        log.debug("Node neighbor could not be found.");
                    }
                }
            }
        }

        //log.debug("Node neighbors: {}", i);
        return returnList;
    }

    public List<Point3D> constructPath(Node node) {
        if(Config.PATHFINDER_SIMPLIFY_PATH) {
            return constructPathSimplified(node);
        } else {
            return constructPathFull(node);
        }
    }

    public List<Point3D> constructPathSimplified(Node node) {
        LinkedList<Point3D> path = new LinkedList<>();
        float prevX = -1000;
        float prevY = -1000;
        float dirX;
        float dirY;
        while (node.getParentNode() != null) {
            // only add a new route point if moving direction changes
            dirX = node.getCenter().getX() - node.getParentNode().getCenter().getX();
            dirY = node.getCenter().getZ() - node.getParentNode().getCenter().getZ();
            if ((dirX != prevX) || (dirY != prevY)) {
                prevX = dirX;
                prevY = dirY;
                path.addFirst(node.getCenter());
            }
            node = node.getParentNode();
        }
        return path;
    }

    public List<Point3D> constructPathFull(Node node) {
        LinkedList<Point3D> path = new LinkedList<>();
        while (node.getParentNode() != null) {
            path.addFirst(node.getCenter());
            node = node.getParentNode();
        }
        return path;
    }
}
