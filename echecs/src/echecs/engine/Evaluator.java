package echecs.engine;

import echecs.Color;
import echecs.Library;
import echecs.pieces.Piece;

public class Evaluator {

    public static final int QueenValue = 9;
    public static final int KnightValue = 3;
    public static final int RookValue = 5;
    public static final int BishopValue = 3;
    public static final int PawnValue = 1;

    public int evaluate(Board board, Color color) {
        int evaluation = 0;
        // Value of echecs.pieces
        for (Piece p : board.getPieces())
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
   /*     List<Long> blackRooks = echecs.Library.extract(board.getBlackRookPosition());
        List<Long> whiteRooks = echecs.Library.extract(board.getWhiteRookPosition());

        if (echecs.Library.log2(blackRooks.get(0)) % 8 == echecs.Library.log2(blackRooks.get(1)) % 8)
            evaluation += 5;

        if (echecs.Library.log2(whiteRooks.get(0)) % 8 == echecs.Library.log2(whiteRooks.get(1)) % 8)
            evaluation -= 5;

        // pawn as close as possible than the adversial raw
        for (long l : echecs.Library.extract(board.getBlackPawnPosition()))
            evaluation += (6 - echecs.Library.log2(l) / 8);

        for (long l : echecs.Library.extract(board.getWhitePawnPosition()))
            evaluation -= (echecs.Library.log2(l) / 8 - 1);*/

        // Malus
        //      - Distance to castle
        int distanceToCastle = 0;
        long occupiedCells = board.getOccupiedCells();
        if (board.isCastlingPossible(0, Color.WHITE)) {
            if ((occupiedCells & Library.pow2(5)) > 0)
                distanceToCastle -= 1;
            if ((occupiedCells & Library.pow2(6)) > 0)
                distanceToCastle -= 1;
        }
        if (board.isCastlingPossible(1, Color.WHITE)) {
            if ((occupiedCells & Library.pow2(1)) > 0)
                distanceToCastle -= 1;
            if ((occupiedCells & Library.pow2(2)) > 0)
                distanceToCastle -= 1;
            if ((occupiedCells & Library.pow2(3)) > 0)
                distanceToCastle -= 1;
        }

        if (board.isCastlingPossible(0, Color.BLACK)) {
            if ((occupiedCells & Library.pow2(61)) > 0)
                distanceToCastle -= 1;
            if ((occupiedCells & Library.pow2(62)) > 0)
                distanceToCastle -= 1;
        }
        if (board.isCastlingPossible(1, Color.BLACK)) {
            if ((occupiedCells & Library.pow2(57)) > 0)
                distanceToCastle -= 1;
            if ((occupiedCells & Library.pow2(58)) > 0)
                distanceToCastle -= 1;
            if ((occupiedCells & Library.pow2(59)) > 0)
                distanceToCastle -= 1;
        }

        if (color == Color.BLACK)
            evaluation += distanceToCastle;
        else
            evaluation -= distanceToCastle;

        // tropisme (roi protégé par une pièce elle même menacée

        // pions doublé ou triplés

        // pions isolés

        // pions meme ligne

        return evaluation;
    }
}
