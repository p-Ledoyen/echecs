package echecs.engine;

import echecs.Color;
import echecs.Constant;
import echecs.EvaluationMovement;
import echecs.Movement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SearchService implements Runnable {

    private Evaluator evaluator;
    private int maxDepth;
    private volatile Board board;
    private Color myColor;
    private List<EvaluationMovement> prevision;
    private List<Thread> runnables;
    private String bestMove;
    private String adverseBestMove;
    private boolean active;

    public SearchService(Evaluator evaluator, Color myColor, Board board) {
        this.active = false;
        this.evaluator = evaluator;
        this.myColor = myColor;
        this.board = board;
        this.runnables = new ArrayList<>();
        for (int i = 0; i < Constant.THREADS_NB; i++) {
            runnables.add(new Thread());
        }
    }

    public void setMyColor(Color myColor) {
        this.myColor = myColor;
    }

    public String getBestMove() {
        return bestMove;
    }

    public String getAdverseBestMove() {
        return adverseBestMove;
    }

    public boolean isActive() {
        return active;
    }

    public List<EvaluationMovement> getPrevision() {
        return prevision;
    }

    @Override
    public void run() {
        this.active = true;
        threadCreation();
    }

    /**
     * Create threads to search the best move.
     */

    public void threadCreation() {
        maxDepth = 1;
        if (this.prevision != null) {
            if (this.prevision.size() > 1)
                this.prevision.remove(this.prevision.size() - 1);
            if (this.prevision.size() > 1)
                this.prevision.remove(this.prevision.size() - 1);
        }

        // Init a barrier to wait all thread ending
        CyclicBarrier cb = new CyclicBarrier(Constant.THREADS_NB + 1);

        List<List<EvaluationMovement>> res = new ArrayList<>();
        List<Board> boards = new ArrayList<>();
        List<Movement> legalMovements = this.board.allLegalDeplacements(myColor);

        int length = legalMovements.size();
        Movement nextMovement = null;
        if (prevision != null && this.prevision.size() > 1)
            nextMovement = prevision.get(prevision.size() - 1).getMovement();

        List<List<Movement>> movements = new ArrayList<>();

        for (int i = 0; i < Constant.THREADS_NB; i++) {
            //init boards
            boards.add(this.board.copy());
            //init movements
            List<Movement> m = new ArrayList<>();
            if (nextMovement != null && legalMovements.contains(nextMovement))
                m.add(nextMovement);
            m.addAll(legalMovements.subList((i * length) / Constant.THREADS_NB, ((i + 1) * length) / Constant.THREADS_NB));
            movements.add(m);
            //init res
            res.add(new ArrayList<>());

            this.runnables.get(i).set(boards.get(i), movements.get(i), cb);
        }

        // Start all threads
        for (Thread t : runnables)
            new java.lang.Thread(t).start();

        long time = System.currentTimeMillis();
        while (System.currentTimeMillis() - time < 3000) {
            maxDepth++;
            try {
                cb.await();
            } catch (InterruptedException e) {
                return;
            } catch (BrokenBarrierException e) {
                return;
            }

            for (int i = 0; i < Constant.THREADS_NB; i++) {
                res.set(i, runnables.get(i).getRes());
            }

            // get the best result of all threads
            int best = res.get(0).get(res.get(0).size() - 1).getEvaluation();
            int bestIndex = 0;
            for (int i = 1; i < Constant.THREADS_NB; i++) {
                if (best < res.get(i).get(res.get(i).size() - 1).getEvaluation()) {
                    bestIndex = i;
                    best = res.get(i).get(res.get(i).size() - 1).getEvaluation();
                }
            }
            prevision = res.get(bestIndex);
            bestMove = prevision.get(prevision.size() - 1).getMovement().toString();
            adverseBestMove = prevision.get(prevision.size() - 2).getMovement().toString();


        }
    }

    /**
     * A thread that search on a part of tree search.
     */
    class Thread implements Runnable {
        private Board board;
        private List<Movement> movements;
        private List<EvaluationMovement> res;
        private CyclicBarrier cb;
        private int maxDepth;

        /**
         * Get the result finded by the thread.
         *
         * @return The list of the next moves
         */
        public List<EvaluationMovement> getRes() {
            return this.res;
        }

        /**
         * Initiliaze the thread.
         *
         * @param board     The board with the last moves.
         * @param movements The first movements in the tree (depth 1)
         */
        public void set(Board board, List<Movement> movements, CyclicBarrier cb) {
            this.board = board;
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
            while (System.currentTimeMillis() - time < 3000) {
                for (Movement m : movements) {
                    board.makeMovement(m);

                    if (!board.isMate(myColor)) {
                        List<EvaluationMovement> tmp = minValue(board, 1, alpha, beta, this.res.size() < 2 ? null : this.res.get(this.res.size() - 2).getMovement());
                        this.board.cancelMovement(m);

                        if (tmp.size() == 0) {
                            // check mate
                            this.res.add(new EvaluationMovement(m, 10000000));
                            cb.reset();
                            return;
                        }

                        if (this.res.size() == 0 || tmp.get(tmp.size() - 1).getEvaluation() > this.res.get(this.res.size() - 1).getEvaluation()) {
                            this.res = tmp;
                            this.res.add(new EvaluationMovement(m, tmp.get(tmp.size() - 1).getEvaluation()));
                        }
                        if (this.res.get(this.res.size() - 1).getEvaluation() >= beta) {
                            break;
                        }
                        alpha = Math.max(alpha, this.res.get(this.res.size() - 1).getEvaluation());
                    } else {
                        board.cancelMovement(m);
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
        private List<EvaluationMovement> maxValue(Board board, int depth, int alpha, int beta, Movement bestMoveFinded) {
            if (depth >= this.maxDepth) {
                List<EvaluationMovement> res = new ArrayList<>();
                res.add(new EvaluationMovement(null, evaluator.evaluate(board, myColor)));
                return res;
            }

            List<EvaluationMovement> res = new ArrayList<>();
            List<Movement> movements = board.allLegalDeplacements(myColor);
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
                    List<EvaluationMovement> tmp = minValue(board, depth + 1, alpha, beta, res.size() < 2 ? null : res.get(res.size() - 2).getMovement());
                    board.cancelMovement(m);

                    if (tmp.size() == 0) {
                        // check mate
                        res.add(new EvaluationMovement(m, 10000000));
                        cb.reset();
                        return res;
                    }

                    if (res.size() == 0 || tmp.get(tmp.size() - 1).getEvaluation() > res.get(res.size() - 1).getEvaluation()) {
                        res = tmp;
                        res.add(new EvaluationMovement(m, tmp.get(tmp.size() - 1).getEvaluation()));
                    }

                    if (res.get(res.size() - 1).getEvaluation() >= beta)
                        return res;
                    alpha = Math.max(alpha, res.get(res.size() - 1).getEvaluation());
                } else {
                    board.cancelMovement(m);
                }
            }
            return res;

        }

        /**
         * Minimax function, min phase.
         */
        private List<EvaluationMovement> minValue(Board board, int depth, int alpha, int beta, Movement bestMoveFinded) {
            if (depth >= this.maxDepth) {
                List<EvaluationMovement> res = new ArrayList<>();
                res.add(new EvaluationMovement(null, evaluator.evaluate(board, Color.other(myColor))));
                return res;
            }

            List<EvaluationMovement> res = new ArrayList<>();
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
                    List<EvaluationMovement> tmp = maxValue(board, depth + 1, alpha, beta, res.size() < 2 ? null : res.get(res.size() - 2).getMovement());
                    board.cancelMovement(m);

                    if (tmp.size() == 0) {
                        // check mate
                        res.add(new EvaluationMovement(m, -10000000));
                        cb.reset();
                        return res;
                    }
                    try {
                        if (res.size() == 0 || tmp.get(tmp.size() - 1).getEvaluation() < res.get(res.size() - 1).getEvaluation()) {
                            res = tmp;
                            res.add(new EvaluationMovement(m, tmp.get(tmp.size() - 1).getEvaluation()));
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    if (res.get(res.size() - 1).getEvaluation() <= alpha)
                        return res;
                    beta = Math.min(beta, res.get(res.size() - 1).getEvaluation());
                } else {
                    board.cancelMovement(m);
                }
            }
            return res;
        }
    }
}
