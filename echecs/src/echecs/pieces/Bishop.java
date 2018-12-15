package echecs.pieces;

import echecs.Color;
import echecs.Constant;
import echecs.Library;

public class Bishop extends Piece {

    public Bishop(int pow, Color color) {
        this.color = color;
        this.value = Constant.BishopValue;
        this.alive = true;

        this.position = Library.pow2(pow);
    }

    @Override
    public long specializedThreatenedCells(long myPieces, long adversePiece) {
        return this.specializedLegalMovements(myPieces, adversePiece);
    }

    @Override
    public long specializedLegalMovements(long myPieces, long adversePieces) {
        long res = 0;
        int i = Library.log2(position);
        while (i > 8 && i % 8 != 0) {
            i -= 9;
            if ((Library.pow2(i) & myPieces) == 0)
                res |= Library.pow2(i);
            else
                break;
            if ((Library.pow2(i) & adversePieces) > 0)
                break;
        }
        i = Library.log2(position);
        while (i > 8 && i % 8 != 7) {
            i -= 7;
            if ((Library.pow2(i) & myPieces) == 0)
                res |= Library.pow2(i);
            else
                break;
            if ((Library.pow2(i) & adversePieces) > 0)
                break;
        }
        i = Library.log2(position);
        while (i < 56 && i % 8 != 0) {
            i += 7;
            if ((Library.pow2(i) & myPieces) == 0)
                res |= Library.pow2(i);
            else
                break;
            if ((Library.pow2(i) & adversePieces) > 0)
                break;
        }
        i = Library.log2(position);
        while (i < 56 && i % 8 != 7) {
            i += 9;
            if ((Library.pow2(i) & myPieces) == 0)
                res |= Library.pow2(i);
            else
                break;
            if ((Library.pow2(i) & adversePieces) > 0)
                break;
        }

        return res;
    }
}
