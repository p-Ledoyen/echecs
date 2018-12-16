package echecs.agent;


import echecs.Color;

public class Engine {

    private PerformanceMeasure performanceMeasure;
    private SearchService searchService;
    private Link input;
    private Thread inputThread;
    private Board board;

    public Engine() {
        board = new Board();
        this.performanceMeasure = new PerformanceMeasure();

        this.input = new Link();
        this.inputThread = new Thread(input);
        this.searchService = new SearchService(performanceMeasure, Color.WHITE, board);
    }

    /**
     * Start the engine.
     */
    public void run() {
        //Start input thread

        this.inputThread.start();
        //    this.searchThread.start();

        while (processGuiMessages()) {
        }
        //Stop the input thread
        input.stopRunning();

    }

    /**
     * Wait the next message and process it.
     *
     * @return True a message is read different to 'quit'
     */
    private boolean processGuiMessages() {
        if (this.input.isInputReady()) {
            String input = this.input.getNextInput();
            if (input.split(" ")[0].equals("quit"))
                return false;
            else {
                process(input);
                return true;
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
                    searchService.setMyColor(Color.WHITE);
                    performanceMeasure.setColor(Color.WHITE);
                } else if (command.length == 4) {
                    searchService.setMyColor(Color.BLACK);
                    performanceMeasure.setColor(Color.BLACK);
                    board.makeMovement(new Movement(command[command.length - 1]));
                } else if (command[command.length - 1].matches("([a-h][1-8]){2}")) {
                    board.makeMovement(new Movement(command[command.length - 1]));
                }

                break;

            case "go":
                //Quels que soient les paramètres, on autorise seulement 2sec de recherche.

                Thread t = new Thread(this.searchService);
                t.start();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                t.stop();
                String bestmove = this.searchService.getIntention();
                System.out.println("info string " + bestmove);
                board.makeMovement(new Movement(bestmove));
                System.out.println("bestmove " + bestmove);



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
