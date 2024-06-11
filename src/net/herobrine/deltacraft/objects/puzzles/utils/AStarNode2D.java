package net.herobrine.deltacraft.objects.puzzles.utils;


import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class AStarNode2D implements Comparable<AStarNode2D> {

    private final int x, y;
    private int g, h;
    private int f;
    private AStarNode2D parent;
    private final Vector vector;

    public AStarNode2D(int x, int y) {
        this.x = x;
        this.y = y;

        this.vector = new Vector(x, y, 0);
    }

    public AStarNode2D(Vector vector) {
        this.x = (int) vector.getX();
        this.y = (int) vector.getY();
        vector.setZ(0);

        this.vector = vector;
    }

    public Vector toVector() {

        return vector;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AStarNode2D{");
        sb.append("x=").append(x);
        sb.append(", y=").append(y);
        sb.append(", g=").append(g);
        sb.append(", h=").append(h);
        sb.append(", f=").append(f);
        sb.append('}');
        return sb.toString();
    }

    public int dist(AStarNode2D node) {
        return (int) (NumberConversions.square(node.x - x) + NumberConversions.square(node.y - y));
    }

    public int calcF() {
        f = g + h;
        return f;
    }

    public List<AStarNode2D> getNeighbors(boolean diagonals) {
        List<AStarNode2D> neighbors = new ArrayList<>();

        neighbors.add(new AStarNode2D(x + 1, y));
        neighbors.add(new AStarNode2D(x - 1, y));
        neighbors.add(new AStarNode2D(x, y + 1));
        neighbors.add(new AStarNode2D(x, y - 1));

        if(diagonals){
            neighbors.add(new AStarNode2D(x + 1, y + 1));
            neighbors.add(new AStarNode2D(x - 1, y - 1));
            neighbors.add(new AStarNode2D(x - 1, y + 1));
            neighbors.add(new AStarNode2D(x + 1, y - 1));
        }

        return neighbors;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof AStarNode2D)) return false;
        AStarNode2D o  = (AStarNode2D) obj;
        return o.x == x && o.y == y;

    }

    @Override
    public int compareTo(AStarNode2D o) {
        if (f > o.f) {
            return 1;
        } else if (f < o.f)
            return -1;

        return 0;
    }

    public static AStarNode2D toNode(int slot, int columnRows){
        return new AStarNode2D(slot / columnRows, slot % columnRows);
    }

    public int fromExcel() {
        return (vector.getBlockX() * 9) + vector.getBlockY();
    }

    public int getX() {return x;}
    public int getY() {return y;}
    public int getG() {return g;}
    public int getH() {return h;}
    public void setG(int g) {this.g = g;}
    public void setH(int h) {this.h = h;}
    public int getF() {return f;}
    public AStarNode2D getParent() {return parent;}
    public void setParent(AStarNode2D parent) {this.parent = parent;}
}

