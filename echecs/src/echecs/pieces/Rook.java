package echecs.pieces;

import echecs.Color;
import echecs.Constant;
import echecs.Library;

public class Rook extends Piece {

    public Rook(int pow, Color color) {
        this.color = color;
        this.value = Constant.RookValue;
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
        while (i < 56) {
            i += 8;
            if ((Library.pow2(i) & myPieces) == 0)
                res |= Library.pow2(i);
            else
                break;
            if ((Library.pow2(i) & adversePieces) > 0)
                break;
        }
        i = Library.log2(position);
        while (i > 8) {
            i -= 8;
            if ((Library.pow2(i) & myPieces) == 0)
                res |= Library.pow2(i);
            else
                break;
            if ((Library.pow2(i) & adversePieces) > 0)
                break;
        }
        i = Library.log2(position);
        while (i % 8 != 0) {
            i--;
            if ((Library.pow2(i) & myPieces) == 0)
                res |= Library.pow2(i);
            else
                break;
            if ((Library.pow2(i) & adversePieces) > 0)
                break;
        }
        i = Library.log2(position);
        while (i % 8 != 7) {
            i++;
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
