package echecs.engine;

import echecs.Color;
import echecs.Constant;
import echecs.EvaluationMovement;
import echecs.Movement;

import java.util.ArrayList;
import java.util.List;

public class SearchService implements Runnable {

    private Evaluator evaluator;
    private int maxDepth;
    private Board board;
    private Color myColor;
    private List<EvaluationMovement> prevision;
    private List<Thread> runnables;
    private List<java.lang.Thread> threads;
    private String bestMove;
    private String adverseBestMove;

    public SearchService(Evaluator evaluator, Color myColor, Board board) {
        this.evaluator = evaluator;
        this.maxDepth = 1;
        this.myColor = myColor;
        this.board = board;
        this.runnables = new ArrayList<>();
        this.threads = new ArrayList<>();
        for (int i = 0; i < Constant.THREADS_NB; i++) {
            runnables.add(new Thread());
            threads.add(new java.lang.Thread(runnables.get(i)));
            threads.get(i).start();
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

    @Override
    public void run() {
        if (this.prevision != null) {
            if (this.prevision.size() > 1)
                this.prevision.remove(this.prevision.size() - 1);
            if (this.prevision.size() > 1)
                this.prevision.remove(this.prevision.size() - 1);
        }
        threadCreation();
    }

    /**
     * Create threads to search the best move.
     */
    private void threadCreation() {
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
        }

        for (int i = 0; i < Constant.THREADS_NB; i++)
            this.runnables.get(i).set(boards.get(i), movements.get(i));

        long time = System.currentTimeMillis();
        maxDepth = 2;
        while (System.currentTimeMillis() - time < 50000) {
            System.out.println("info string " + maxDepth);

            for (Thread runnable : runnables)
                runnable.startWithMax();


            for (int i = 0; i < Constant.THREADS_NB; i++) {
                while (!runnables.get(i).isResReady()) {
                }
                res.set(i, runnables.get(i).getRes());
            }

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

            maxDepth++;
        }
    }

    /**
     * Minimax function, max phase.
     */
    private List<EvaluationMovement> maxValue(Board board, int depth, int alpha, int beta, Movement bestMoveFinded) {
        if (depth == this.maxDepth) {
            List<EvaluationMovement> res = new ArrayList<>();
            res.add(new EvaluationMovement(null, this.evaluator.evaluate(board)));
            return res;
        }

        List<EvaluationMovement> res = new ArrayList<>();
        res.add(new EvaluationMovement(null, -10000));
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
                List<EvaluationMovement> tmp = minValue(board, depth + 1, alpha, beta, res.size() == 1 ? null : res.get(res.size() - 2).getMovement());
                board.cancelMovement(m);

                if (tmp.get(tmp.size() - 1).getEvaluation() > res.get(res.size() - 1).getEvaluation()) {
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
        if (depth == this.maxDepth) {
            List<EvaluationMovement> res = new ArrayList<>();
            res.add(new EvaluationMovement(null, this.evaluator.evaluate(board)));
            return res;
        }
        List<EvaluationMovement> res = new ArrayList<>();
        res.add(new EvaluationMovement(null, 10000));
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
                List<EvaluationMovement> tmp = maxValue(board, depth + 1, alpha, beta, res.size() == 1 ? null : res.get(res.size() - 2).getMovement());
                board.cancelMovement(m);

                if (tmp.get(tmp.size() - 1).getEvaluation() < res.get(res.size() - 1).getEvaluation()) {
                    res = tmp;
                    res.add(new EvaluationMovement(m, tmp.get(tmp.size() - 1).getEvaluation()));
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

    /**
     * A thread that search on a part of tree search.
     */
    class Thread implements Runnable {
        private Board board;
        private List<Movement> movements;
        private List<EvaluationMovement> res;
        private boolean resReady;

        @Override
        public void run() {
            res = new ArrayList<>();
            while (true) ;
        }

        /**
         * Start researches.
         */
        public void startWithMax() {
            resReady = false;
            res.clear();
            this.res.add(new EvaluationMovement(null, -10000));

            int alpha = -1000;
            int beta = 10000;

            for (Movement m : movements) {
                board.makeMovement(m);

                if (!board.isMate(myColor)) {
                    List<EvaluationMovement> tmp = minValue(board, 1, alpha, beta, this.res.size() == 1 ? null : this.res.get(this.res.size() - 2).getMovement());
                    this.board.cancelMovement(m);

                    if (tmp.get(tmp.size() - 1).getEvaluation() > this.res.get(this.res.size() - 1).getEvaluation()) {
                        this.res = tmp;
                        this.res.add(new EvaluationMovement(m, tmp.get(tmp.size() - 1).getEvaluation()));
                    }
                    if (this.res.get(this.res.size() - 1).getEvaluation() >= beta) {
                        resReady = true;
                        return;
                    }
                    alpha = Math.max(alpha, this.res.get(this.res.size() - 1).getEvaluation());
                } else {
                    board.cancelMovement(m);
                }
            }
            resReady = true;
            return;
        }

        /**
         * Check if the thread have finiched his reserches.
         *
         * @return True if the thread ins't active
         */
        public boolean isResReady() {
            return resReady;
        }

        /**
         * Get the result finded by the thread.
         *
         * @return The list of the next moves
         */
        public List<EvaluationMovement> getRes() {
            return this.res;
        }

        /**
         * Initiliaer the thread.
         * @param board The board with the last moves.
         * @param movements The first movements in the tree (depth 1)
         */
        public void set(Board board, List<Movement> movements) {
            this.board = board;
            this.movements = movements;
        }
    }
}
