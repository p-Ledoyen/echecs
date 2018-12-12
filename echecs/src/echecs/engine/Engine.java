package echecs.engine;


import echecs.Color;
import echecs.Constant;
import echecs.Movement;

public class Engine {

    private Board board;
    private Evaluator evaluator;
    private SearchService searchService;
    private Link input;
    private TimeThread time;
    private Thread inputThread, timeThread, searchThread;


    public Engine() {
        this.board = new Board();
        this.evaluator = new Evaluator();

        this.input = new Link();
        this.inputThread = new Thread(input);
        this.time = new TimeThread();
        this.timeThread = new Thread(time);
        this.searchService = new SearchService(evaluator, Color.WHITE,this.board);
        this.searchThread = new Thread(searchService);
    }


    public void run() {
        //Start input thread

        this.inputThread.start();
        this.searchThread.start();

        while (processGuiMessages(50)){

        }
        //Stop the input thread
        input.stopRunning();

    }


    private boolean processGuiMessages(int wait){
        if (this.input.isInputReady()){
            String input = this.input.getNextInput();
            if (input.split(" ")[0].equals("quit")) return false;
            else{
                process(input);
                return true;
            }
        }

        if (wait>0) {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void process(String input) {

        String[] command = input.split(" ");

        System.out.println("info string Arena dit : "+input);
        switch(command[0]){
            case "uci":
                System.out.println("id author Aurélie et Paul");
                System.out.println("uciok");
                break;

            case "debug":
                break;

            case "isready":
                System.out.println("readyok");
                break;

            case "setoption":
                //TODO
                break;

            case "register":
                break;

            case "ucinewgame":
                this.board.clear();
                break;

            case "position":
                if (command.length == 2) {
                        System.out.println("info string Echecs dit : ma couleur est BLANC");
                        searchService.setMyColor(Color.WHITE);
                        evaluator.setColor(Color.WHITE);
                }
                else if (command.length == 4) {
                        System.out.println("info string Echecs dit : ma couleur est NOIR");
                        searchService.setMyColor(Color.BLACK);
                        evaluator.setColor(Color.BLACK);
                }
                if(command[command.length-1].matches("([a-h][1-8]){2}"))
                    board.makeMovement(new Movement(command[command.length-1]));
                break;

            case "go":
                //Quels que soient les paramètres, on autorise seulement 2sec de recherche.

                System.out.println("info string Lancement de la recherche");

                searchService.launchSearch();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("info string Recherche terminée");



                String bestmove=this.searchService.getBestMove();
                System.out.println("info string Echecs dit :  "+bestmove);
                System.out.println("info string"+board.toString());
                board.makeMovement(new Movement(bestmove));
                System.out.println("bestmove "+bestmove);
                break;

            case "stop":
                System.out.println("info string STOOOOOOOOOP");
                break;

            case "ponderhit":
                break;

            default:
                break;
        }


    }


    class TimeThread implements Runnable {

        @Override
        public void run() {
            System.out.println("info string Lancement de TimeThread");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }
}
