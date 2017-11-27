package ekotech.network;

import ekotech.Vector;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private Node parent;
    private List<Node> childs;
    private long id;
    private Vector vector;

    public Node(int id){
        this.id = id;
        this.childs = new ArrayList<>();
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChilds() {
        return childs;
    }

    public void addChild(Node child) {
        child.setParent(this);
        this.childs.add(child);
    }

    public void removeChild(Node child){
        child.setParent(null);
        this.childs.remove(child);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Vector getVector() {
        return vector;
    }

    public void setVector(Vector vector) {
        this.vector = vector;
    }


}
