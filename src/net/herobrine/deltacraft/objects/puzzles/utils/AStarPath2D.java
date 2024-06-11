package net.herobrine.deltacraft.objects.puzzles.utils;

import net.herobrine.deltacraft.objects.puzzles.utils.AStarNode2D;

import java.util.*;

public class AStarPath2D {

    public AStarPath2D(AStarNode2D start, AStarNode2D end) {
        this.start = start;
        this.end = end;
    }
    private final AStarNode2D start, end;
    private boolean diagonals;
    private final HashSet<AStarNode2D> closed = new HashSet<>();
    private final PriorityQueue<AStarNode2D> open = new PriorityQueue<>(Comparator.comparingInt(AStarNode2D::getF));

    public List<Integer> find() {
        start.setG(start.dist(end));
        open.add(start);

        while (!open.isEmpty()) {
            AStarNode2D current = open.poll();

            if (current.equals(end)) {
                closed.clear();
                open.clear();
                return reconstruct(start, current);
            }

            closed.add(current);

            List<AStarNode2D> neighbors = getCollisionNeighbors(current);

            for (AStarNode2D neighbor : neighbors) {
                int tempScore = current.getG() + start.dist(current);

                if (!open.contains(neighbor) || tempScore < neighbor.getG()) {
                    if (!diagonals)
                        neighbor.setG(tempScore);
                    neighbor.setH(neighbor.dist(end));
                    neighbor.calcF();
                    neighbor.setParent(current);

                    open.add(neighbor);
                }
            }

        }
        return new ArrayList<>();
    }

    private List<Integer> reconstruct(AStarNode2D start, AStarNode2D goal) {
        List<Integer> path = new ArrayList<>();

        AStarNode2D current = new AStarNode2D(goal.getX(), goal.getY());
        current.setParent(goal.getParent());
        current.setG(goal.getG());
        current.setH(goal.getH());

        while (!current.equals(start)) {
            path.add(0, current.fromExcel());
            current = current.getParent();
        }

        return path;
    }

    private List<AStarNode2D> getCollisionNeighbors(AStarNode2D current) {
        List<AStarNode2D> nodes = current.getNeighbors(diagonals);
        List<AStarNode2D> formatted = new ArrayList<>();

        for (AStarNode2D node : nodes) {
            if (closed.contains(node)) continue;
            node.setG(node.dist(current));
            node.setH(node.dist(end));
            node.calcF();

            formatted.add(node);
        }
        return formatted;
    }
    public AStarNode2D getStart() {return start;}
    public AStarNode2D getEnd() {return end;}
    public void setDiagonals(boolean diagonals) {this.diagonals = diagonals;}
}
