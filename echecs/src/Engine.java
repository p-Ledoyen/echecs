import java.util.Scanner;


public class Engine {

    Board board = new Board();
    Evaluator evaluator = new Evaluator();


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
