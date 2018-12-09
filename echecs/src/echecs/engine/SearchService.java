package echecs.engine;

import echecs.Color;
import echecs.Couple;
import echecs.Movement;
import echecs.State;

public class SearchService {

    private Evaluator evaluator;
    private Color color;
    private int maxDepth;

    public SearchService(Evaluator evaluator, int maxDepth) {
        this.evaluator = evaluator;
        this.maxDepth = maxDepth;
        this.color = Color.WHITE;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Movement miniMaxDecision(State state) {
        return maxValue(state).getFirst();
    }

    private Couple maxValue(State state) {
        if (state.getDepth() == this.maxDepth)
            //return this.evaluator.evaluate(state.getBoard(),1);
            return new Couple(state.getMovement(), this.evaluator.evaluate(state.getBoard()));
        Couple res = new Couple(null, -10000);
        for (State s : state.successors()) {
            //  v = Math.max(v, minValue(s));
            Couple tmp = minValue(s);
            if (tmp.getSecond() > res.getSecond())
                res = tmp;
        }
        return res;
    }

    private Couple minValue(State state) {
        if (state.getDepth() == this.maxDepth)
            //return this.evaluator.evaluate(state.getBoard(),1);
            return new Couple(state.getMovement(), this.evaluator.evaluate(state.getBoard()));
        Couple res = new Couple(null, 10000);
        for (State s : state.successors()) {
            //  v = Math.max(v, minValue(s));
            Couple tmp = maxValue(s);
            if (tmp.getSecond() < res.getSecond())
                res = new Couple(s.getMovement(), tmp.getSecond());
        }
        return res;
    }
}
