package com.shnok.javaserver.pathfinding.node;

import com.shnok.javaserver.model.Point3D;
import lombok.Data;

@Data
public class Node implements Comparable<Node> {
    private Point3D nodeIndex;
    private Point3D worldPosition;
    private Point3D center;
    private float nodeSize;
    private float cost;
    private Node parentNode;

    public Node(Point3D nodeIndex, Point3D worldPosition, float nodeSize) {
        this.nodeIndex = nodeIndex;
        this.worldPosition = worldPosition;
        this.nodeSize = nodeSize;
        this.center = new Point3D (
                worldPosition.getX() + nodeSize / 2f,
                worldPosition.getY(),
                worldPosition.getZ() + nodeSize / 2f);
    }

    public Node(Node original) {
        nodeIndex = original.nodeIndex;
        worldPosition = original.worldPosition;
        nodeSize = original.nodeSize;
        center = original.center;
    }

    @Override
    public int hashCode() {
        return worldPosition.hashCode();
    }

    @Override
    public boolean equals(Object arg0) {
        if (!(arg0 instanceof Node)) {
            return false;
        }
        Node n = (Node) arg0;
        // Check if x,y,z are the same
        return (worldPosition.getX() == n.getWorldPosition().getX()) &&
                (worldPosition.getY() == n.getWorldPosition().getY()) &&
                (worldPosition.getZ() == n.getWorldPosition().getZ());
    }

    @Override
    public int compareTo(Node o) {
        return Float.compare(o.nodeIndex.getY(), this.nodeIndex.getY());
    }
}