import java.util.Comparator;
import java.util.PriorityQueue;

public class Engine {

    Board board;
    Evaluator evaluator;

    public Engine() {
        this.board = new Board();
        this.evaluator = new Evaluator();

        PriorityQueue<Couple> scores = new PriorityQueue<>(Comparator.comparingInt(Couple::getSecond));

        for (Movement m : board.allLegalDeplacements(Color.WHITE)) {
            board.makeMovement(m);
            scores.add(new Couple(m, evaluator.evaluate(board, Color.WHITE)));
            board.cancelMovement(m);
        }

        for (Couple c : scores) {
            System.out.println(c.getFirst() + "=" + c.getSecond());
        }

    }

    public void run(){
        //Start input thread
        Link input = new Link();
        Thread inputThread = new Thread(input);
        inputThread.start();
        while(true);
        //Stop the input thread
        //input.stopRunning();

    }
}
