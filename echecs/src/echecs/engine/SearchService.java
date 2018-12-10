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

    private int alpha;
    private int beta;

    public SearchService(Evaluator evaluator, int maxDepth, Color myColor, Board board) {
        this.evaluator = evaluator;
        this.maxDepth = maxDepth;
        this.myColor = myColor;
        this.board = board;
        this.alpha = -100000;
        this.beta = 100000;
    }

    public void setMyColor(Color myColor) {
        this.myColor = myColor;
    }

    public List<Couple> miniMaxDecision() {
     /*   if (prevision != null) {
            prevision.remove(prevision.size() - 1);
            prevision.remove(prevision.size() - 1);
        }

        int depth = 0;
        List<Couple> res = new ArrayList<>();

        // prevision to initialize alpha and beta
        if (this.prevision != null) {
            for (int i = this.prevision.size() - 1; i >= 0; i--) {
                Movement m = prevision.get(i).getFirst();
                if (m != null) {
                    board.makeMovement(m);
                    depth++;
                }
            }

            if (depth%2==1)
                res = minValue(depth);
            else
                res = maxValue(depth);

            for (int i = 0; i < this.prevision.size(); i++) {
                Movement m = prevision.get(i).getFirst();
                if (m != null) {
                    board.cancelMovement(m);
                    res.add(new Couple(m, alpha));
                }
            }
        }

*/

        this.prevision = maxValue(0);
        return prevision;
    }

    private List<Couple> maxValue(int depth) {
        if (depth == this.maxDepth) {
            List<Couple> res = new ArrayList<>();
            res.add(new Couple(null, this.evaluator.evaluate(this.board)));
            return res;
        }
        List<Couple> res = new ArrayList<>();
        res.add(new Couple(null, -10000));


        // all movements
        for (Movement m : this.board.allLegalDeplacements(this.myColor)) {
            board.makeMovement(m);
            List<Couple> tmp = minValue(depth + 1);
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

    private List<Couple> minValue(int depth) {
        if (depth == this.maxDepth) {
            List<Couple> res = new ArrayList<>();
            res.add(new Couple(null, this.evaluator.evaluate(this.board)));
            return res;
        }
        List<Couple> res = new ArrayList<>();
        res.add(new Couple(null, 10000));
        // for (State s : state.successors()) {
        for (Movement m : this.board.allLegalDeplacements(Color.other(myColor))) {
            this.board.makeMovement(m);
            List<Couple> tmp = maxValue(depth + 1);
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
