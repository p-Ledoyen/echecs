package echecs.engine;


import echecs.Color;
import echecs.Movement;

public class Engine {

    private Board board;
    private Evaluator evaluator;
    private SearchService searchService;
    private Link input;
    private Thread inputThread;
    private Thread searchThread;
    private String adverseBestMove;

    public Engine() {
        this.board = new Board();
        this.evaluator = new Evaluator();

        this.input = new Link();
        this.inputThread = new Thread(input);
        this.searchService = new SearchService(evaluator, Color.WHITE, this.board);
        this.searchThread = new Thread(searchService);
    }

    /**
     * Start the engine.
     */
    public void run() {
        //Start input thread

        this.inputThread.start();
        //    this.searchThread.start();

        while (processGuiMessages(50)) {

        }
        //Stop the input thread
        input.stopRunning();

    }

    /**
     * Wait the next message and process it.
     *
     * @param wait The time maximum to wait the next message.
     * @return True a message is read different to 'quit'
     */
    private boolean processGuiMessages(int wait) {
        if (this.input.isInputReady()) {
            String input = this.input.getNextInput();
            if (input.split(" ")[0].equals("quit")) return false;
            else {
                process(input);
                return true;
            }
        }

        if (wait > 0) {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * Process a message receive by arena and answer to it
     *
     * @param input The message received
     */
    private void process(String input) {

        System.out.println("info string " + input);
        String[] command = input.split(" ");

        switch (command[0]) {
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
                this.board.initialization();
                break;

            case "position":
                if (command.length == 2) {
                    System.out.println("info string Echecs dit : ma couleur est BLANC");
                    searchService.setMyColor(Color.WHITE);
                    evaluator.setColor(Color.WHITE);
                } else if (command.length == 4) {
                    System.out.println("info string Echecs dit : ma couleur est NOIR");
                    searchService.setMyColor(Color.BLACK);
                    evaluator.setColor(Color.BLACK);
                    board.makeMovement(new Movement(command[command.length - 1]));
                } else if (command[command.length - 1].matches("([a-h][1-8]){2}") /*&& !command[command.length - 1].equals(adverseBestMove)*/) {
                    /*searchThread.interrupt();
                    board.cancelMovement(new Movement(adverseBestMove));*/
                    board.makeMovement(new Movement(command[command.length - 1]));
                   /* System.out.println("info string  loupe ... ");
                    searchService.threadCreation();*/
                }

                break;

            case "go":
                //Quels que soient les paramètres, on autorise seulement 2sec de recherche.

                if (!searchService.isActive())
                    searchThread.start();
                else
                    searchService.threadCreation();

                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("info string kill ");

                searchThread.interrupt();
                String bestmove = this.searchService.getBestMove();
                System.out.println("info string " + searchService.getPrevision());
                System.out.println("info string  best move " + bestmove);
                board.makeMovement(new Movement(bestmove));
                System.out.println("bestmove " + bestmove);


                adverseBestMove = this.searchService.getAdverseBestMove();
                System.out.println("info string adverse best move " + adverseBestMove);
                /*board.makeMovement(new Movement(adverseBestMove));
                searchService.threadCreation();*/


                break;

            case "stop":
                break;

            case "ponderhit":
                break;

            default:
                break;
        }


    }
}
