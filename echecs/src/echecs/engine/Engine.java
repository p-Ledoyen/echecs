package echecs.engine;


import echecs.Constant;

public class Engine {

    Board board;
    Evaluator evaluator;
    SearchService searchService;

    public Engine() {
        this.board = new Board();
        this.evaluator = new Evaluator();
        this.searchService = new SearchService(evaluator, Constant.MAX_DEPTH);
    }


    public void run() {
        //Start input thread
        Link input = new Link();
        Thread inputThread = new Thread(input);
        inputThread.start();
        while (true) ;
        //Stop the input thread
        //input.stopRunning();

    }
}
