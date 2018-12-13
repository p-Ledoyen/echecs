package echecs.engine;

import echecs.Color;
import echecs.Constant;
import echecs.Couple;
import echecs.Movement;

import java.util.ArrayList;
import java.util.List;

public class SearchService implements Runnable {

    private Evaluator evaluator;
    private int maxDepth;
    private Board board;
    private Color myColor;
    private List<Couple> prevision;
    private List<MaxThread> runnables;
    private List<Thread> threads;

    public SearchService(Evaluator evaluator, Color myColor, Board board) {
        this.evaluator = evaluator;
        this.maxDepth = 1;
        this.myColor = myColor;
        this.board = board;
        this.runnables = new ArrayList<>();
        this.threads = new ArrayList<>();
        for (int i = 0; i < Constant.THREADS_NB; i++) {
            runnables.add(new MaxThread());
            threads.add(new Thread(runnables.get(i)));
            threads.get(i).start();
        }


    }

    public void setMyColor(Color myColor) {
        this.myColor = myColor;
    }

    public String getBestMove() {
        String s = prevision.get(prevision.size() - 1).getFirst().toString();

        System.out.println("info string contenu de prevision : " + s);
        return prevision.get(prevision.size() - 1).getFirst().toString();
    }

    @Override
    public void run() {
        System.out.println("info string Lancement de SearchService");
        if (this.prevision != null) {
            if (this.prevision.size() > 1)
                this.prevision.remove(this.prevision.size() - 1);
            if (this.prevision.size() > 1)
                this.prevision.remove(this.prevision.size() - 1);
            if (this.prevision.size() > 1)
                System.out.println("info string Prévision avant init : " + this.prevision.get(this.prevision.size() - 1).getFirst().toString());
        }

        threadCreation();
    }

    private void threadCreation() {
        List<List<Couple>> res = new ArrayList<>();
        List<Board> boards = new ArrayList<>();
        List<Movement> legalMovements = this.board.allLegalDeplacements(myColor);

        int length = legalMovements.size();
        Movement nextMovement = null;
        if (prevision != null && this.prevision.size() > 1)
            nextMovement = prevision.get(prevision.size() - 1).getFirst();


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
        maxDepth = 1;
        while (true) {
            System.out.println("info string " + maxDepth);

            for (MaxThread runnable : runnables)
                runnable.startWithMax();


            for (int i = 0; i < Constant.THREADS_NB; i++) {
                while (!runnables.get(i).isResReady()) {
                }
                res.set(i, runnables.get(i).getRes());
            }

            int best = res.get(0).get(res.get(0).size() - 1).getSecond();
            int bestIndex = 0;
            for (int i = 1; i < Constant.THREADS_NB; i++) {
                if (best < res.get(i).get(res.get(i).size() - 1).getSecond()) {
                    bestIndex = i;
                    best = res.get(i).get(res.get(i).size() - 1).getSecond();
                }
            }
            System.out.println("info string taille res : " + res.get(0).size() + " bestindex : " + bestIndex);
            prevision = res.get(bestIndex);
            maxDepth++;
        }

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

    class MaxThread implements Runnable {
        private Board board;
        private List<Movement> movements;
        private List<Couple> res;
        private boolean resReady;

        @Override
        public void run() {
            System.out.println("info string Lancement d'un thread de calcul");

            res = new ArrayList<>();
            while (true) ;

        }

        public void startWithMax() {
            resReady = false;
            res.clear();
            this.res.add(new Couple(null, -10000));

            int alpha = -1000;
            int beta = 10000;

            for (Movement m : movements) {
                board.makeMovement(m);

                if (!board.isMate(myColor)) {
                    List<Couple> tmp = minValue(board, 1, alpha, beta, this.res.size() == 1 ? null : this.res.get(this.res.size() - 2).getFirst());
                    this.board.cancelMovement(m);

                    if (tmp.get(tmp.size() - 1).getSecond() > this.res.get(this.res.size() - 1).getSecond()) {
                        this.res = tmp;
                        this.res.add(new Couple(m, tmp.get(tmp.size() - 1).getSecond()));
                    }
                    if (this.res.get(this.res.size() - 1).getSecond() >= beta) {
                        resReady = true;
                        return;
                    }
                    alpha = Math.max(alpha, this.res.get(this.res.size() - 1).getSecond());
                } else {
                    board.cancelMovement(m);
                }
            }
            resReady = true;
            return;
        }

        public boolean isResReady() {
            return resReady;
        }

        public List<Couple> getRes() {
            return this.res;
        }

        public void set(Board board, List<Movement> movements) {
            this.board = board;
            this.movements = movements;
        }


    }


}
