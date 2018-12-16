package echecs.agent;

import echecs.Color;
import echecs.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SearchService implements Runnable {

    private Color myColor;
    private List<Node> previsions;
    private List<threadResearcher> threadResearchers;
    private String intention;
    private Board belief;

    public SearchService(PerformanceMeasure performanceMeasure, Color myColor, Board board) {
        this.belief = board;
        this.myColor = myColor;
        this.threadResearchers = new ArrayList<>();
        for (int i = 0; i < Constant.THREADS_NB; i++) {
            threadResearchers.add(new threadResearcher(myColor, performanceMeasure));
        }
    }

    public void setMyColor(Color myColor) {
        this.myColor = myColor;
    }

    public String getIntention() {
        return intention;
    }

    @Override
    public void run() {
        startResearch();
    }

    /**
     * Create threads to search the best move.
     */

    public void startResearch() {
        if (this.previsions != null) {
            if (this.previsions.size() > 1)
                this.previsions.remove(this.previsions.size() - 1);
            if (this.previsions.size() > 1)
                this.previsions.remove(this.previsions.size() - 1);
        }

        // Init a barrier to wait all thread ending
        CyclicBarrier cb = new CyclicBarrier(Constant.THREADS_NB + 1);

        List<List<Node>> res = new ArrayList<>();
        List<Movement> legalMovements = this.belief.allLegalMovements(myColor);

        int length = legalMovements.size();
        Movement nextMovement = null;
        if (previsions != null && this.previsions.size() > 1)
            nextMovement = previsions.get(previsions.size() - 1).getMovement();

        List<List<Movement>> movements = new ArrayList<>();
        for (int i = 0; i < Constant.THREADS_NB; i++) {
            //init movements
            List<Movement> m = new ArrayList<>();
            if (nextMovement != null && legalMovements.contains(nextMovement))
                m.add(nextMovement);
            m.addAll(legalMovements.subList((i * length) / Constant.THREADS_NB, ((i + 1) * length) / Constant.THREADS_NB));
            movements.add(m);
            //init res
            res.add(new ArrayList<>());

            this.threadResearchers.get(i).set(movements.get(i), cb, belief.copy());
        }

        // Start all threads
        for (threadResearcher miniagent : threadResearchers)
            new java.lang.Thread(miniagent).start();

        long time = System.currentTimeMillis();
        while (System.currentTimeMillis() - time < 20000) {
            try {

                System.out.println("info string  wait");
                cb.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                return;
            }

            for (int i = 0; i < Constant.THREADS_NB; i++) {
                res.set(i, threadResearchers.get(i).getRes());
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
            previsions = res.get(bestIndex);
            intention = previsions.get(previsions.size() - 1).getMovement().toString();
        }
    }


}
