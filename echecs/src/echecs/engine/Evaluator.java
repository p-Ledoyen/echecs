package echecs.engine;

import echecs.Color;
import echecs.Library;
import echecs.pieces.BlackPawn;
import echecs.pieces.Piece;
import echecs.pieces.Rook;
import echecs.pieces.WhitePawn;

import java.util.ArrayList;
import java.util.List;

public class Evaluator {

    public Color color = Color.WHITE;

    public void setColor(Color color) {
        this.color = color;
    }

    public int evaluate(Board board) {
        int evaluation = 0;
        // Value of echecs.pieces
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
                evaluation += board.numberPieces(board.legalDeplacements(p));
            else
                evaluation -= board.numberPieces(board.legalDeplacements(p));

        //      - control center
        long blackThreatenedCells = board.getThreatenedCells(Color.BLACK);
        long whiteThreatenedCells = board.getThreatenedCells(Color.WHITE);

        int controlCenter = 0;
        if ((blackThreatenedCells & Library.pow2(35)) > 0)
            controlCenter += 1;
        if ((blackThreatenedCells & Library.pow2(36)) > 0)
            controlCenter += 1;
        if ((blackThreatenedCells & Library.pow2(27)) > 0)
            controlCenter += 3;
        if ((blackThreatenedCells & Library.pow2(28)) > 0)
            controlCenter += 3;
        if ((whiteThreatenedCells & Library.pow2(35)) > 0)
            controlCenter -= 3;
        if ((whiteThreatenedCells & Library.pow2(36)) > 0)
            controlCenter -= 3;
        if ((whiteThreatenedCells & Library.pow2(27)) > 0)
            controlCenter -= 1;
        if ((whiteThreatenedCells & Library.pow2(28)) > 0)
            controlCenter -= 1;

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
                        evaluation += 20;
                    else
                        blackRooks.add(Library.log2((p.getPosition()) % 8));

                } else {
                    if (whiteRooks.contains(Library.log2(p.getPosition()) % 8))
                        // two rooks on the same column
                        evaluation -= 20;
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
            evaluation += pawnPosition;
        else
            evaluation -= pawnPosition;

        // Malus
        // tropisme (roi protégé par une pièce elle même menacée)


        return evaluation;
    }
}
