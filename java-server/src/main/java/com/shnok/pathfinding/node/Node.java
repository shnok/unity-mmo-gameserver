package com.shnok.pathfinding.node;

import com.shnok.pathfinding.PathFinding;

public class Node {
    private final NodeLoc _loc;
    private Node[] _neighbors;
    private Node _parent;
    private short _cost;

    public Node(NodeLoc Loc) {
        _loc = Loc;
    }

    public void attacheNeighbors() {
        if (_loc == null) {
            _neighbors = null;
        } else {
            _neighbors = PathFinding.getInstance().readNeighbors(_loc.getX(), _loc.getY(), _loc.getZ());
        }
    }

    public Node[] getNeighbors() {
        return _neighbors;
    }

    public Node getParent() {
        return _parent;
    }

    public void setParent(Node p) {
        _parent = p;
    }

    public NodeLoc getLoc() {
        return _loc;
    }

    public short getCost() {
        return _cost;
    }

    public void setCost(int cost) {
        _cost = (short) cost;
    }

    @Override
    public int hashCode() {
        final int prime = 1117;
        int result = 1;
        result = (prime * result) + _loc.getX();
        result = (prime * result) + _loc.getY();
        result = (prime * result) + _loc.getZ();
        return result;
    }

    @Override
    public boolean equals(Object arg0) {
        if (!(arg0 instanceof Node)) {
            return false;
        }
        Node n = (Node) arg0;
        // Check if x,y,z are the same
        return (_loc.getX() == n.getLoc().getX()) && (_loc.getY() == n.getLoc().getY()) && (_loc.getZ() == n.getLoc().getZ());
    }
}