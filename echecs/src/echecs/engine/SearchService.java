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
        return maxValue(state, -10000, 10000).getFirst();
    }

    private Couple maxValue(State state, int alpha, int beta) {
        if (state.getDepth() == this.maxDepth)
            return new Couple(state.getMovement(), this.evaluator.evaluate(state.getBoard()));
        Couple res = new Couple(null, -10000);
        for (State s : state.successors()) {
            //  v = Math.max(v, minValue(s));
            Couple tmp = minValue(s, alpha, beta);
            if (tmp.getSecond() > res.getSecond())
                res = new Couple(s.getMovement(), tmp.getSecond());
            if (res.getSecond() >= beta)
                return res;
            alpha = Math.max(alpha, res.getSecond());
        }
        return res;
    }

    private Couple minValue(State state, int alpha, int beta) {
        if (state.getDepth() == this.maxDepth)
            return new Couple(state.getMovement(), this.evaluator.evaluate(state.getBoard()));
        Couple res = new Couple(null, 10000);
        for (State s : state.successors()) {
            //  v = Math.max(v, minValue(s));
            Couple tmp = maxValue(s, alpha, beta);
            if (tmp.getSecond() < res.getSecond())
                res = new Couple(s.getMovement(), tmp.getSecond());
            if (res.getSecond() <= alpha)
                return res;
            beta = Math.min(beta, res.getSecond());
        }
        return res;
    }
}
