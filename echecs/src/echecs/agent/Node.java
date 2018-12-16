package echecs.agent;

public class Node {
    private Movement movement;
    private int evaluation;

    public Node(Movement movement, int evaluation) {
        this.movement = movement;
        this.evaluation = evaluation;
    }

    public Movement getMovement() {
        return movement;
    }

    public int getEvaluation() {
        return evaluation;
    }

    @Override
    public String toString() {
        return this.movement + "  " + this.getEvaluation();
    }
}