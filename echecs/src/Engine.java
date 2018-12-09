public class Engine {

    Board board;
    Evaluator evaluator;

    public Engine() {
        this.board = new Board();
        this.evaluator = new Evaluator();
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
