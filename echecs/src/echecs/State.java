package echecs;

import echecs.engine.Board;

import java.util.ArrayList;
import java.util.List;

public class State {
    private int depth;
    private State parent;
    private Movement movement;

    public State(int depth, State parent, Movement movement) {
        this.depth = depth;
        this.parent = parent;
        this.movement = movement;
    }


}
