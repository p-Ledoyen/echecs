package echecs;

import echecs.engine.Board;

import java.util.ArrayList;
import java.util.List;

public class State {
    private Board board;
    private Color color;
    private int depth;
    private State parent;
    private Movement movement;

    public State(Board board, int depth, State parent, Movement movement, Color color) {
        this.board = board;
        this.depth = depth;
        this.parent = parent;
        this.movement = movement;
        this.color = color;
    }

    public Board getBoard() {
        return board;
    }

    public int getDepth() {
        return depth;
    }

    public State getParent() {
        return parent;
    }

    public Movement getMovement() {
        return movement;
    }

    public List<State> successors() {
        List<State> res = new ArrayList<>();
        for (Movement m : this.board.allLegalDeplacements(this.color)) {
            board.makeMovement(m);
            res.add(new State(board.copy(),
                    this.depth + 1,
                    this,
                    m,
                    Color.other(color)));
            board.cancelMovement(m);
        }
        return res;
    }
}
