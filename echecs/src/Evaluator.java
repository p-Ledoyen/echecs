import java.util.List;

public class Evaluator {

    public static final int QueenValue = 9;
    public static final int KnightValue = 3;
    public static final int RookValue = 5;
    public static final int BishopValue = 3;
    public static final int PawnValue = 1;

    public int evaluate(Board board, String color) {
        int evaluation = 0;
        // Value of pieces
        evaluation += board.numberPieces(board.getBlackQueenPosition()) * QueenValue;
        evaluation += board.numberPieces(board.getBlackKnightPosition()) * KnightValue;
        evaluation += board.numberPieces(board.getBlackRookPosition()) * RookValue;
        evaluation += board.numberPieces(board.getBlackBishopPosition()) * BishopValue;
        evaluation += board.numberPieces(board.getBlackPawnPosition()) * PawnValue;

        evaluation -= board.numberPieces(board.getWhiteQueenPosition()) * QueenValue;
        evaluation -= board.numberPieces(board.getWhiteKnightPosition()) * KnightValue;
        evaluation -= board.numberPieces(board.getWhiteRookPosition()) * RookValue;
        evaluation -= board.numberPieces(board.getWhiteBishopPosition()) * BishopValue;
        evaluation -= board.numberPieces(board.getWhitePawnPosition()) * PawnValue;

        // Bonus
        //      - freedom degree
        evaluation += board.numberPieces(board.legalDeplacements(board.getBlackKingPosition(), "king", Constante.BLACK));
        for (long l : Library.extract(board.getBlackKnightPosition()))
            evaluation += board.numberPieces(board.legalDeplacements(l, "knight", Constante.BLACK));
        for (long l : Library.extract(board.getBlackQueenPosition()))
            evaluation += board.numberPieces(board.legalDeplacements(l, "queen", Constante.BLACK));
        for (long l : Library.extract(board.getBlackRookPosition()))
            evaluation += board.numberPieces(board.legalDeplacements(l, "rook", Constante.BLACK));
        for (long l : Library.extract(board.getBlackBishopPosition()))
            evaluation += board.numberPieces(board.legalDeplacements(l, "bishop", Constante.BLACK));
        for (long l : Library.extract(board.getBlackPawnPosition()))
            evaluation += board.numberPieces(board.legalDeplacements(l, "black_pawn", Constante.BLACK));

        evaluation -= board.numberPieces(board.legalDeplacements(board.getWhiteKingPosition(), "king", Constante.WHITE));
        for (long l : Library.extract(board.getWhiteKnightPosition()))
            evaluation -= board.numberPieces(board.legalDeplacements(l, "knight", Constante.WHITE));
        for (long l : Library.extract(board.getWhiteQueenPosition()))
            evaluation -= board.numberPieces(board.legalDeplacements(l, "queen", Constante.WHITE));
        for (long l : Library.extract(board.getWhiteRookPosition()))
            evaluation -= board.numberPieces(board.legalDeplacements(l, "rook", Constante.WHITE));
        for (long l : Library.extract(board.getWhiteBishopPosition()))
            evaluation -= board.numberPieces(board.legalDeplacements(l, "bishop", Constante.WHITE));
        for (long l : Library.extract(board.getWhitePawnPosition()))
            evaluation -= board.numberPieces(board.legalDeplacements(l, "white_pawn", Constante.WHITE));

        //      - control center
        long blackThreatenedCells = board.getThreatenedCells("black");
        long whiteThreatenedCells = board.getThreatenedCells("white");

        if ((blackThreatenedCells & Library.pow2(35)) > 0)
            evaluation += 1;
        if ((blackThreatenedCells & Library.pow2(36)) > 0)
            evaluation += 1;
        if ((blackThreatenedCells & Library.pow2(27)) > 0)
            evaluation += 3;
        if ((blackThreatenedCells & Library.pow2(28)) > 0)
            evaluation += 3;
        if ((whiteThreatenedCells & Library.pow2(35)) > 0)
            evaluation -= 3;
        if ((whiteThreatenedCells & Library.pow2(36)) > 0)
            evaluation -= 3;
        if ((whiteThreatenedCells & Library.pow2(27)) > 0)
            evaluation -= 1;
        if ((whiteThreatenedCells & Library.pow2(28)) > 0)
            evaluation -= 1;

        // rooks on the same column
        List<Long> blackRooks = Library.extract(board.getBlackRookPosition());
        List<Long> whiteRooks = Library.extract(board.getWhiteRookPosition());

        if (Library.log2(blackRooks.get(0)) % 8 == Library.log2(blackRooks.get(1)) % 8)
            evaluation += 5;

        if (Library.log2(whiteRooks.get(0)) % 8 == Library.log2(whiteRooks.get(1)) % 8)
            evaluation -= 5;

        // pawn as close as possible than the adversial raw
        for (long l : Library.extract(board.getBlackPawnPosition()))
            evaluation += (6 - Library.log2(l) / 8);

        for (long l : Library.extract(board.getWhitePawnPosition()))
            evaluation -= (Library.log2(l) / 8 - 1);

        // Malus
        //      - DistanceToCastle
        long occupiedCells = board.getOccupiedCells();
        if (board.isCastlingPossible(0, "white")) {
            if ((occupiedCells & Library.pow2(5)) > 0)
                evaluation -= 1;
            if ((occupiedCells & Library.pow2(6)) > 0)
                evaluation -= 1;
        }
        if (board.isCastlingPossible(1, "white")) {
            if ((occupiedCells & Library.pow2(1)) > 0)
                evaluation -= 1;
            if ((occupiedCells & Library.pow2(2)) > 0)
                evaluation -= 1;
            if ((occupiedCells & Library.pow2(3)) > 0)
                evaluation -= 1;
        }

        if (board.isCastlingPossible(0, "black")) {
            if ((occupiedCells & Library.pow2(61)) > 0)
                evaluation -= 1;
            if ((occupiedCells & Library.pow2(62)) > 0)
                evaluation -= 1;
        }
        if (board.isCastlingPossible(1, "black")) {
            if ((occupiedCells & Library.pow2(57)) > 0)
                evaluation -= 1;
            if ((occupiedCells & Library.pow2(58)) > 0)
                evaluation -= 1;
            if ((occupiedCells & Library.pow2(59)) > 0)
                evaluation -= 1;
        }

        // tropisme (roi protégé par une pièce elle même menacée

        // pions doublé ou triplés

        // pions isolés

        // pions meme ligne

        if (color.equals("black"))
            return evaluation;
        else if (color.equals("white"))
            return -evaluation;
        else
            throw new IllegalArgumentException();
    }
}
