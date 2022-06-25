package com.shnok.pathfinding;

import com.shnok.pathfinding.node.FastNodeList;
import com.shnok.pathfinding.node.Node;
import com.shnok.pathfinding.node.NodeLoc;
import com.shnok.pathfinding.node.NodeType;

import java.util.LinkedList;
import java.util.List;

public class PathFinding {
    private static PathFinding _instance;

    public static PathFinding getInstance() {
        if (_instance == null) {
            _instance = new PathFinding();
        }
        return _instance;
    }

    public List<NodeLoc> findPath(int x, int y, int z, int tx, int ty, int tz) {
        Node start = readNode(x, y, z);
        //System.out.println("Start: " + start.getLoc().toString());
        Node end = readNode(tx, ty, tz);
        //System.out.println("End: " + end.getLoc().toString());

        if ((start == null) || (end == null)) {
            return null;
        }
        if (start == end) {
            return null;
        }
        return searchByClosest(start, end);
    }

    public List<NodeLoc> searchByClosest(Node start, Node end) {
        // List of Visited Nodes
        FastNodeList visited = new FastNodeList(550);

        // List of Nodes to Visit
        LinkedList<Node> to_visit = new LinkedList<>();
        to_visit.add(start);
        int targetx = end.getLoc().getX();
        int targetz = end.getLoc().getZ();
        int dx, dz;
        boolean added;
        int i = 0;
        while (i < 550) {
            Node node;
            try {
                node = to_visit.removeFirst();
            } catch (Exception e) {
                // No Path found
                System.out.println("No path found - " + start.getLoc() + " - " + end.getLoc());
                return null;
            }
            if (node.equals(end)) {
                return constructPath(node);
            }

            i++;
            visited.add(node);
            node.attacheNeighbors();
            Node[] neighbors = node.getNeighbors();
            if (neighbors == null) {
                continue;
            }
            for (Node n : neighbors) {
                if (n == null) {
                    continue;
                }
                if (!visited.containsRev(n) && !to_visit.contains(n)) {

                    added = false;
                    n.setParent(node);
                    dx = targetx - n.getLoc().getX();
                    dz = targetz - n.getLoc().getZ();
                    n.setCost((dx * dx) + (dz * dz));
                    for (int index = 0; index < to_visit.size(); index++) {
                        if (to_visit.get(index).getCost() > n.getCost()) {
                            //System.out.println(n.getLoc().toString());
                            to_visit.add(index, n);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        to_visit.addLast(n);
                    }
                }
            }
        }
        // No Path found
        return null;
    }

    private Node readNode(int nodeX, int nodeY, int nodeZ) {
        //System.out.println("Read node: " + nodeX + "," + nodeY + "," + nodeZ);
        NodeType type = Geodata.getInstance().getNodeType(nodeX, nodeY, nodeZ);
        if (type == NodeType.UNWALKABLE) {
            return null;
        }

        return new Node(new NodeLoc(nodeX, nodeY, nodeZ));
    }


    public Node[] readNeighbors(int nodeX, int nodeY, int nodeZ) {
        Node[] returnList = new Node[8];

        short i = 0;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0)
                    continue;

                Node node = readNode(nodeX + x, nodeY, nodeZ + z);
                if (node == null)
                    node = readNode(nodeX + x, nodeY - 1, nodeZ + z);
                if (node == null)
                    node = readNode(nodeX + x, nodeY + 1, nodeZ + z);

                returnList[i] = node;
                i++;
            }

        }

        return returnList;
    }

    public List<NodeLoc> constructPath(Node node) {
        //return constructPathSimplified(node);
        return constructPathFull(node);
    }

    public List<NodeLoc> constructPathSimplified(Node node) {
        LinkedList<NodeLoc> path = new LinkedList<>();
        int prevX = -1000;
        int prevY = -1000;
        int dirX;
        int dirY;
        while (node.getParent() != null) {
            // only add a new route point if moving direction changes
            dirX = node.getLoc().getX() - node.getParent().getLoc().getX();
            dirY = node.getLoc().getY() - node.getParent().getLoc().getY();
            if ((dirX != prevX) || (dirY != prevY)) {
                prevX = dirX;
                prevY = dirY;
                path.addFirst(node.getLoc());
            }
            node = node.getParent();
        }
        return path;
    }

    public List<NodeLoc> constructPathFull(Node node) {
        LinkedList<NodeLoc> path = new LinkedList<>();
        while (node.getParent() != null) {
            path.addFirst(node.getLoc());
            node = node.getParent();
        }
        return path;
    }
}
