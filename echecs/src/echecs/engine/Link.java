package echecs.engine;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Link implements Runnable {
    private volatile boolean stillRunning;
    private Scanner stdin;
    private volatile Queue<String> commands;

    public Link() {
        this.stillRunning = true;
        this.stdin = new Scanner(System.in);
        this.commands = new ConcurrentLinkedDeque<>();
    }

    @Override
    public void run() {

        while (this.stillRunning) {
            String input = this.stdin.nextLine();
            List<String> tokens = new ArrayList<String>();

            for (String token : input.split("[ \t]+"))
                tokens.add(token);

            ListIterator<String> iterator = tokens.listIterator();

            String command = null;
            String next, next2 = null;

            while (iterator.hasNext()) {
                next = iterator.next();
                switch (next) {
                    case "uci":
                    case "isready":
                    case "ucinewgame":
                    case "stop":
                    case "ponderhit":
                    case "quit":
                        command = next;
                        break;
                    case "debug":
                        if (iterator.hasNext()) {
                            next2 = iterator.next();
                            if (next2.equals("on") || next2.equals("off"))
                                command = next + " " + next2;
                        }
                        break;
                    case "register":
                        if (iterator.hasNext()) {
                            next2 = iterator.next();
                            if (next2.equals("later"))
                                command = next + " " + next2;
                            else if (next2.equals("name") && iterator.hasNext()) {
                                // register name ...
                                command = next + " " + next2;
                                while (iterator.hasNext()) {
                                    next = iterator.next();
                                    if (next.equals("code") && iterator.hasNext()) {
                                        command += " " + next + " " + iterator.next();
                                        break;
                                    } else if (!next.equals("code")) command += " " + next;
                                }
                            }
                        }

                        break;

                    case "go":
                        command = next;
                        while (iterator.hasNext()) {
                            next2 = iterator.next();
                            switch (next2) {
                                case "ponder":
                                case "winc":
                                case "binc":
                                case "infinite":
                                    command += " " + next2;
                                    break;
                                case "wtime":
                                case "btime":
                                case "movestogo":
                                case "depth":
                                case "nodes":
                                case "mate":
                                case "movetime":
                                    if (iterator.hasNext())
                                        command += " " + next2 + " " + iterator.next();
                                    break;
                                case "searchmoves":
                                    if (iterator.hasNext()) {
                                        command += " " + next2;
                                        while (iterator.hasNext()) {
                                            next2 = iterator.next();
                                            if (next2.matches("([a-h][1-8]){2}"))
                                                command += " " + next2;
                                            else {
                                                iterator.previous();
                                                break;
                                            }

                                        }
                                    }
                                    break;
                                default:

                            }
                        }
                        break;

                    case "position":
                        command = next;
                        while(iterator.hasNext())
                            command += " "+ iterator.next();
                        break;
                    default:
                }
                if (command != null) break;
            }

            if (command != null) {
                this.commands.add(command);
            }
        }
    }

    public boolean isInputReady() {
        return (this.commands.size() != 0);
    }

    public String getNextInput() {
        return this.commands.remove();
    }

    public void stopRunning() {
        this.stillRunning = false;
    }
}
