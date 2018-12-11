package echecs.engine;

import echecs.Color;
import echecs.Couple;
import echecs.Movement;

import java.util.ArrayList;
import java.util.List;

public class SearchService {

    private Evaluator evaluator;
    private int maxDepth;
    private Board board;
    private Color myColor;
    private List<Couple> prevision;

    public SearchService(Evaluator evaluator, int maxDepth, Color myColor, Board board) {
        this.evaluator = evaluator;
        this.maxDepth = maxDepth;
        this.myColor = myColor;
        this.board = board;
    }

    public void setMyColor(Color myColor) {
        this.myColor = myColor;
    }

    public String miniMaxDecision() {
        if (prevision != null) {
            prevision.remove(prevision.size() - 1);
            prevision.remove(prevision.size() - 1);
        }
        prevision = maxValue(0, -10000, 10000);
        return prevision.get(this.prevision.size() - 1).getFirst().toString();
    }

    private List<Couple> maxValue(int depth, int alpha, int beta) {
        if (depth == this.maxDepth) {
            List<Couple> res = new ArrayList<>();
            res.add(new Couple(null, this.evaluator.evaluate(this.board)));
            return res;
        }
        List<Couple> res = new ArrayList<>();
        res.add(new Couple(null, -10000));

        List<Movement> movements = new ArrayList<>();
        if (depth == 0) {
            if (prevision != null)
                movements.add(prevision.get(prevision.size() - 1).getFirst());
        }
        movements.addAll(board.allLegalDeplacements(myColor));
        for (Movement m : movements) {
            board.makeMovement(m);
            List<Couple> tmp = minValue(depth + 1, alpha, beta);
            this.board.cancelMovement(m);
            if (tmp.get(tmp.size() - 1).getSecond() > res.get(res.size() - 1).getSecond()) {
                res = tmp;
                res.add(new Couple(m, tmp.get(tmp.size() - 1).getSecond()));
            }
            if (res.get(res.size() - 1).getSecond() >= beta)
                return res;
            alpha = Math.max(alpha, res.get(res.size() - 1).getSecond());
        }
        return res;
    }

    private List<Couple> minValue(int depth, int alpha, int beta) {
        if (depth == this.maxDepth) {
            List<Couple> res = new ArrayList<>();
            res.add(new Couple(null, this.evaluator.evaluate(this.board)));
            return res;
        }
        List<Couple> res = new ArrayList<>();
        res.add(new Couple(null, 10000));
        for (Movement m : this.board.allLegalDeplacements(Color.other(myColor))) {
            this.board.makeMovement(m);
            List<Couple> tmp = maxValue(depth + 1, alpha, beta);
            this.board.cancelMovement(m);
            if (tmp.get(tmp.size() - 1).getSecond() < res.get(res.size() - 1).getSecond()) {
                res = tmp;
                res.add(new Couple(m, tmp.get(tmp.size() - 1).getSecond()));
            }
            if (res.get(res.size() - 1).getSecond() <= alpha)
                return res;
            beta = Math.min(beta, res.get(res.size() - 1).getSecond());
        }
        return res;
    }
}
