package echecs.engine;

import echecs.Color;
import echecs.Couple;
import echecs.Movement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

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
        //X  System.out.println("info string : " + prevision.get(prevision.size()-1).getFirst());
        prevision = threadCreation();
        //  prevision = maxValue(board, 0, -10000, 10000);
        //return prevision;
        return prevision.get(prevision.size() - 1).getFirst().toString();
    }

    private List<Couple> threadCreation() {
        List<Couple> res1 = new ArrayList<>();
        List<Couple> res2 = new ArrayList<>();
        List<Couple> res3 = new ArrayList<>();
        List<Couple> res4 = new ArrayList<>();

        Board b1 = board.copy();
        Board b2 = board.copy();
        Board b3 = board.copy();
        Board b4 = board.copy();

        List<Movement> legalMovements = board.allLegalDeplacements(myColor);
        int length = legalMovements.size();
        Movement nextMovement = null;
        if (prevision != null)
            nextMovement = prevision.get(prevision.size() - 1).getFirst();

        List<Movement> m1 = new ArrayList<>();
        if (prevision != null && legalMovements.contains(nextMovement))
            m1.add(nextMovement);
        m1.addAll(b1.allLegalDeplacements(myColor).subList(0, length / 4));

        List<Movement> m2 = new ArrayList<>();
        if (prevision != null && legalMovements.contains(nextMovement))
            m2.add(nextMovement);
        m2.addAll(b2.allLegalDeplacements(myColor).subList(length / 4, 2 * length / 4));

        List<Movement> m3 = new ArrayList<>();
        if (prevision != null && legalMovements.contains(nextMovement))
            m3.add(nextMovement);
        m3.addAll(b3.allLegalDeplacements(myColor).subList(2 * length / 4, 3 * length / 4));

        List<Movement> m4 = new ArrayList<>();
        if (prevision != null && legalMovements.contains(nextMovement))
            m4.add(nextMovement);
        m4.addAll(b4.allLegalDeplacements(myColor).subList(3 * length / 4, length));

        MaxThread thread1 = new MaxThread(b1, m1);
        MaxThread thread2 = new MaxThread(b2, m2);
        MaxThread thread3 = new MaxThread(b3, m3);
        MaxThread thread4 = new MaxThread(b4, m4);

        FutureTask ft1 = new FutureTask<>(thread1);
        FutureTask ft2 = new FutureTask<>(thread2);
        FutureTask ft3 = new FutureTask<>(thread3);
        FutureTask ft4 = new FutureTask<>(thread4);
        new Thread(ft1, "th1").start();
        new Thread(ft2, "th2").start();
        new Thread(ft3, "th3").start();
        new Thread(ft4, "th4").start();

        try {
            res1 = (List<Couple>) ft1.get();
            res2 = (List<Couple>) ft2.get();
            res3 = (List<Couple>) ft3.get();
            res4 = (List<Couple>) ft4.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int best = Math.max(res1.get(res1.size() - 1).getSecond(),
                Math.max(res2.get(res2.size() - 1).getSecond(),
                        Math.max(res3.get(res3.size() - 1).getSecond(),
                                res4.get(res4.size() - 1).getSecond())));

        if (best == res1.get(res1.size() - 1).getSecond())
            return res1;
        if (best == res2.get(res2.size() - 1).getSecond())
            return res2;
        if (best == res3.get(res3.size() - 1).getSecond())
            return res3;
        if (best == res4.get(res4.size() - 1).getSecond())
            return res4;

        return null;

    }

    private List<Couple> maxValue(Board board, int depth, int alpha, int beta, Movement bestMoveFinded) {
        if (depth == this.maxDepth) {
            List<Couple> res = new ArrayList<>();
            res.add(new Couple(null, this.evaluator.evaluate(board)));
            return res;
        }

        List<Couple> res = new ArrayList<>();
        res.add(new Couple(null, -10000));
        List<Movement> movements = board.allLegalDeplacements(Color.other(myColor));
        if (bestMoveFinded != null)
            for (Movement m : movements)
                if (m.toString().equals(bestMoveFinded.toString())) {
                    movements.remove(m);
                    movements.add(0, bestMoveFinded);
                    break;
                }
        for (Movement m : movements) {
            board.makeMovement(m);
            if (!board.isMate(Color.other(myColor))) {
                List<Couple> tmp = minValue(board, depth + 1, alpha, beta, res.size() == 1 ? null : res.get(res.size() - 2).getFirst());
                board.cancelMovement(m);

                if (tmp.get(tmp.size() - 1).getSecond() > res.get(res.size() - 1).getSecond()) {
                    res = tmp;
                    res.add(new Couple(m, tmp.get(tmp.size() - 1).getSecond()));
                }
                if (res.get(res.size() - 1).getSecond() >= beta)
                    return res;
                alpha = Math.max(alpha, res.get(res.size() - 1).getSecond());
            } else {
                board.cancelMovement(m);
            }
        }
        return res;

    }

    private List<Couple> minValue(Board board, int depth, int alpha, int beta, Movement bestMoveFinded) {
        if (depth == this.maxDepth) {
            List<Couple> res = new ArrayList<>();
            res.add(new Couple(null, this.evaluator.evaluate(board)));
            return res;
        }
        List<Couple> res = new ArrayList<>();
        res.add(new Couple(null, 10000));
        List<Movement> movements = board.allLegalDeplacements(Color.other(myColor));
        if (bestMoveFinded != null)
            for (Movement m : movements)
                if (m.toString().equals(bestMoveFinded.toString())) {
                    movements.remove(m);
                    movements.add(0, bestMoveFinded);
                    break;
                }
        for (Movement m : movements) {
            board.makeMovement(m);
            if (!board.isMate(myColor)) {
                List<Couple> tmp = maxValue(board, depth + 1, alpha, beta, res.size() == 1 ? null : res.get(res.size() - 2).getFirst());
                board.cancelMovement(m);

                if (tmp.get(tmp.size() - 1).getSecond() < res.get(res.size() - 1).getSecond()) {
                    res = tmp;
                    res.add(new Couple(m, tmp.get(tmp.size() - 1).getSecond()));
                }
                if (res.get(res.size() - 1).getSecond() <= alpha)
                    return res;
                beta = Math.min(beta, res.get(res.size() - 1).getSecond());
            } else {
                board.cancelMovement(m);
            }
        }
        return res;
    }

    class MaxThread implements Callable<List<Couple>> {
        private Board board;
        private List<Movement> movements;

        public MaxThread(Board board, List<Movement> movements) {
            this.board = board;
            this.movements = movements;
        }

        @Override
        public List<Couple> call() {
            List<Couple> res = new ArrayList<>();
            res.add(new Couple(null, -10000));

            int alpha = -1000;
            int beta = 10000;

            for (Movement m : movements) {
                board.makeMovement(m);

                if (!board.isMate(myColor)) {
                    List<Couple> tmp = minValue(board, 1, alpha, beta, res.size() == 1 ? null : res.get(res.size() - 2).getFirst());
                    this.board.cancelMovement(m);

                    if (tmp.get(tmp.size() - 1).getSecond() > res.get(res.size() - 1).getSecond()) {
                        res = tmp;
                        res.add(new Couple(m, tmp.get(tmp.size() - 1).getSecond()));
                    }
                    if (res.get(res.size() - 1).getSecond() >= beta)
                        return res;
                    alpha = Math.max(alpha, res.get(res.size() - 1).getSecond());
                } else {
                    board.cancelMovement(m);
                }
            }
            return res;
        }
    }
}
