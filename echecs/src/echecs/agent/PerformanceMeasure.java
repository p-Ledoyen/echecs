package echecs.agent;

import echecs.Color;
import echecs.Library;
import echecs.pieces.BlackPawn;
import echecs.pieces.Piece;
import echecs.pieces.Rook;
import echecs.pieces.WhitePawn;

import java.util.ArrayList;
import java.util.List;

public class PerformanceMeasure {

    public Color color;

    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Evaluate a board.
     *
     * @param board The board to evaluate
     * @return The evaluation of the board
     */
    public int evaluate(Board board, Color nextPlayer) {
        int evaluation = 0;
        // Value of pieces
        for (Piece p : board.getPieces())
            if (p.getAlive())
                if (p.getColor() == color)
                    evaluation += p.getValue();
                else
                    evaluation -= p.getValue();

        // Bonus
        //      - freedom degree
        for (Piece p : board.getPieces())
            if (p.getColor() == color)
                evaluation += Library.extractNumber1(board.legalMovements(p));
            else
                evaluation -= Library.extractNumber1(board.legalMovements(p));

        //      - control center
        long blackThreatenedCells = board.getThreatenedCells(Color.BLACK);
        long whiteThreatenedCells = board.getThreatenedCells(Color.WHITE);

        int controlCenter = 0;
        if ((blackThreatenedCells & Library.pow2(35)) > 0)
            controlCenter += 10;
        if ((blackThreatenedCells & Library.pow2(36)) > 0)
            controlCenter += 10;
        if ((blackThreatenedCells & Library.pow2(27)) > 0)
            controlCenter += 30;
        if ((blackThreatenedCells & Library.pow2(28)) > 0)
            controlCenter += 30;
        if ((whiteThreatenedCells & Library.pow2(35)) > 0)
            controlCenter -= 30;
        if ((whiteThreatenedCells & Library.pow2(36)) > 0)
            controlCenter -= 30;
        if ((whiteThreatenedCells & Library.pow2(27)) > 0)
            controlCenter -= 10;
        if ((whiteThreatenedCells & Library.pow2(28)) > 0)
            controlCenter -= 10;

        if (color == Color.BLACK)
            evaluation += controlCenter;
        else
            evaluation -= controlCenter;

        // rooks on the same column
        List<Integer> blackRooks = new ArrayList<>();
        List<Integer> whiteRooks = new ArrayList<>();
        for (Piece p : board.getPieces())
            if (p instanceof Rook)
                if (p.getColor() == color) {
                    if (blackRooks.contains(Library.log2(p.getPosition()) % 8))
                        // two rooks on the same column
                        evaluation += 50;
                    else
                        blackRooks.add(Library.log2((p.getPosition()) % 8));

                } else {
                    if (whiteRooks.contains(Library.log2(p.getPosition()) % 8))
                        // two rooks on the same column
                        evaluation -= 50;
                    else
                        whiteRooks.add((Library.log2(p.getPosition()) % 8));
                }

        // pawn as close as possible than the adversial raw
        int pawnPosition = 0;
        for (Piece p : board.getPieces())
            if (p instanceof BlackPawn)
                pawnPosition += (6 - Library.log2(p.getPosition()) / 8);
            else if (p instanceof WhitePawn)
                pawnPosition -= (Library.log2(p.getPosition()) / 8 - 1);

        if (color == Color.BLACK)
            evaluation += pawnPosition * 4;
        else
            evaluation -= pawnPosition * 4;

        // Mate bonus / malus
        if (board.isMate(color))
            evaluation -= 1000;

        if (board.isMate(Color.other(color)))
            evaluation += 50;

        // bonus : defend my threatened pieces
        for (Piece p : board.getPieces()) {
            if (p.getColor() == color) {
                if ((board.getProtectedCells(color) & p.getPosition()) > 0)
                    evaluation += 5;
                else if ((board.getThreatenedCells(Color.other(color)) & p.getPosition()) > 0 &&
                        nextPlayer == Color.other(color))
                    evaluation -= p.getValue() / 2;
            } else {
                if ((board.getThreatenedCells(color) & p.getPosition()) > 0 &&
                        (board.getProtectedCells(Color.other(color)) & p.getPosition()) == 0 &&
                        nextPlayer == color)
                    evaluation += p.getValue();
            }
        }

        return evaluation;
    }
}
