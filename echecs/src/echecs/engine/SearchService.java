package echecs.engine;

import echecs.Color;
import echecs.Couple;
import echecs.Movement;

public class SearchService {

    private Evaluator evaluator;
    private int maxDepth;
    private Board board;
    private Color myColor;

    public SearchService(Evaluator evaluator, int maxDepth, Color myColor, Board board) {
        this.evaluator = evaluator;
        this.maxDepth = maxDepth;
        this.myColor = myColor;
        this.board = board;
    }

    public void setMyColor(Color myColor) {
        this.myColor = myColor;
    }

    public Movement miniMaxDecision() {
        return maxValue(0, -10000, 10000).getFirst();
    }

    private Couple maxValue(int depth, int alpha, int beta) {
        if (depth == this.maxDepth)
            return new Couple(null, this.evaluator.evaluate(this.board));
        Couple res = new Couple(null, -10000);
        // for (State s : state.successors()) {
        for (Movement m : this.board.allLegalDeplacements(this.myColor)) {
            board.makeMovement(m);
            Couple tmp = minValue(depth + 1, alpha, beta);
            this.board.cancelMovement(m);
            if (tmp.getSecond() > res.getSecond())
                res = new Couple(m, tmp.getSecond());
            if (res.getSecond() >= beta)
                return res;
            alpha = Math.max(alpha, res.getSecond());
        }
        return res;
    }

    private Couple minValue(int depth, int alpha, int beta) {
        if (depth == this.maxDepth)
            return new Couple(null, this.evaluator.evaluate(this.board));
        Couple res = new Couple(null, 10000);
        // for (State s : state.successors()) {
        for (Movement m : this.board.allLegalDeplacements(Color.other(myColor))) {
            this.board.makeMovement(m);
            Couple tmp = maxValue(depth + 1, alpha, beta);
            this.board.cancelMovement(m);
            if (tmp.getSecond() < res.getSecond())
                res = new Couple(m, tmp.getSecond());
            if (res.getSecond() <= alpha)
                return res;
            beta = Math.min(beta, res.getSecond());
        }
        return res;
    }
}
