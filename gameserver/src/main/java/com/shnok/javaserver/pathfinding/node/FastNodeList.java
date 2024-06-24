package com.shnok.javaserver.pathfinding.node;

public class FastNodeList {
    private final Node[] _list;
    private int _size;

    public FastNodeList(int size) {
        _list = new Node[size];
    }

    public void add(Node n) {
        _list[_size] = n;
        _size++;
    }

    public boolean contains(Node n) {
        for (int i = 0; i < _size; i++) {
            if (_list[i].equals(n)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsRev(Node n) {
        for (int i = _size - 1; i >= 0; i--) {
            if (_list[i].equals(n)) {
                return true;
            }
        }
        return false;
    }
}
