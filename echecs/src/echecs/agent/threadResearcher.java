package echecs.agent;


import echecs.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * A thread that search on a part of tree search.
 */
public class threadResearcher implements Runnable {
    /*private Board board;*/
    private List<Movement> movements;
    private List<Node> res;
    private CyclicBarrier cb;
    private int maxDepth;
    private Color myColor;
    private PerformanceMeasure performanceMeasure;
    private Board belief;

    public threadResearcher(Color myColor, PerformanceMeasure performanceMeasure) {
        this.myColor = myColor;
        this.performanceMeasure = performanceMeasure;
    }

    /**
     * Get the result finded by the thread.
     *
     * @return The list of the next moves
     */
    public List<Node> getRes() {
        return this.res;
    }

    /**
     * Initiliaze the thread.
     *
     * @param movements The first movements in the tree (depth 1)
     */
    public void set(List<Movement> movements, CyclicBarrier cb, Board board) {
        this.belief = board;
        this.movements = movements;
        this.cb = cb;
    }

    @Override
    public void run() {
        res = new ArrayList<>();
        res.clear();
        this.maxDepth = 2;

        int alpha = -1000;
        int beta = 10000;

        long time = System.currentTimeMillis();
        while (System.currentTimeMillis() - time < 20000) {
            for (Movement m : movements) {
                belief.makeMovement(m);

                if (!belief.isMate(myColor)) {
                    List<Node> tmp = minValue(1, alpha, beta, this.res.size() < 2 ? null : this.res.get(this.res.size() - 2).getMovement());
                    this.belief.cancelMovement(m);

                    if (tmp.size() == 0) {
                        // check mate
                        this.res.add(new Node(m, 10000000));
                        cb.reset();
                        System.out.println("mate " + m);
                        break;
                    }

                    if (this.res.size() == 0 || tmp.get(tmp.size() - 1).getEvaluation() > this.res.get(this.res.size() - 1).getEvaluation()) {
                        this.res = tmp;
                        this.res.add(new Node(m, tmp.get(tmp.size() - 1).getEvaluation()));
                    }
                    if (this.res.get(this.res.size() - 1).getEvaluation() >= beta)
                        break;
                    alpha = Math.max(alpha, this.res.get(this.res.size() - 1).getEvaluation());
                } else {
                    this.belief.cancelMovement(m);
                }
            }
            try {
                cb.await();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                return;
            }
            maxDepth++;
        }
    }

    /**
     * Minimax function, max phase.
     */
    private List<Node> maxValue(int depth, int alpha, int beta, Movement bestMoveFinded) {
        if (depth >= this.maxDepth) {
            List<Node> res = new ArrayList<>();
            res.add(new Node(null, performanceMeasure.evaluate(belief, myColor)));
            return res;
        }

        List<Node> res = new ArrayList<>();
        List<Movement> movements = this.belief.allLegalMovements(myColor);
        if (bestMoveFinded != null)
            for (Movement m : movements)
                if (m.toString().equals(bestMoveFinded.toString())) {
                    movements.remove(m);
                    movements.add(0, bestMoveFinded);
                    break;
                }
        for (Movement m : movements) {
            this.belief.makeMovement(m);
            if (!this.belief.isMate(myColor)) {
                List<Node> tmp = minValue(depth + 1, alpha, beta, res.size() < 2 ? null : res.get(res.size() - 2).getMovement());
                this.belief.cancelMovement(m);

                if (tmp.size() == 0) {
                    // check mate
                    res.add(new Node(m, 10000000));
                    cb.reset();
                    return res;
                }

                if (res.size() == 0 || tmp.get(tmp.size() - 1).getEvaluation() > res.get(res.size() - 1).getEvaluation()) {
                    res = tmp;
                    res.add(new Node(m, tmp.get(tmp.size() - 1).getEvaluation()));
                }

                if (res.get(res.size() - 1).getEvaluation() >= beta)
                    return res;
                alpha = Math.max(alpha, res.get(res.size() - 1).getEvaluation());
            } else {
                this.belief.cancelMovement(m);
            }
        }
        return res;

    }

    /**
     * Minimax function, min phase.
     */
    private List<Node> minValue(int depth, int alpha, int beta, Movement bestMoveFinded) {
        if (depth >= this.maxDepth) {
            List<Node> res = new ArrayList<>();
            res.add(new Node(null, performanceMeasure.evaluate(belief, Color.other(myColor))));
            return res;
        }

        List<Node> res = new ArrayList<>();
        List<Movement> movements = this.belief.allLegalMovements(Color.other(myColor));
        if (bestMoveFinded != null)
            for (Movement m : movements)
                if (m.toString().equals(bestMoveFinded.toString())) {
                    movements.remove(m);
                    movements.add(0, bestMoveFinded);
                    break;
                }
        for (Movement m : movements) {
            this.belief.makeMovement(m);
            if (!this.belief.isMate(Color.other(myColor))) {
                List<Node> tmp = maxValue(depth + 1, alpha, beta, res.size() < 2 ? null : res.get(res.size() - 2).getMovement());
                this.belief.cancelMovement(m);

                if (tmp.size() == 0) {
                    // check mate
                    res.add(new Node(m, -10000000));
                    cb.reset();
                    return res;
                }
                try {
                    if (res.size() == 0 || tmp.get(tmp.size() - 1).getEvaluation() < res.get(res.size() - 1).getEvaluation()) {
                        res = tmp;
                        res.add(new Node(m, tmp.get(tmp.size() - 1).getEvaluation()));
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                if (res.get(res.size() - 1).getEvaluation() <= alpha)
                    return res;
                beta = Math.min(beta, res.get(res.size() - 1).getEvaluation());
            } else {
                this.belief.cancelMovement(m);
            }
        }
        return res;
    }
}