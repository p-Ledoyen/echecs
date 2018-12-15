package echecs.pieces;

import echecs.Color;
import echecs.Constant;
import echecs.Library;

import java.util.HashMap;

public class WhitePawn extends Piece {

    private HashMap<Long, Long> threatened;

    public WhitePawn(int pow, Color color) {
        this.color = color;
        this.value = Constant.PawnValue;
        this.alive = true;

        this.position = Library.pow2(pow);

        this.threatened = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            long cells = 0;
            if (i < 56) {
                if (i % 8 != 0)
                    cells |= Library.pow2(i + 7);
                if (i % 8 != 7)
                    cells |= Library.pow2(i + 9);
            }
            this.threatened.put(Library.pow2(i), cells);
        }
    }

    @Override
    public long specializedThreatenedCells(long myPieces, long adversePieces) {
        return this.threatened.get(this.position) & ~myPieces;
    }

    @Override
    public long specializedLegalMovements(long myPieces, long adversePieces) {
        long res = 0;
        int powPosition = Library.log2(this.position);
        res |= this.threatened.get(this.position) & adversePieces;
        if (powPosition / 8 == 1 && (Library.pow2(powPosition + 16) & ~myPieces & ~adversePieces) != 0 && (Library.pow2(powPosition + 8) & ~myPieces & ~adversePieces) != 0)
            res |= Library.pow2(powPosition + 16);
        if ((Library.pow2(powPosition + 8) & ~myPieces & ~adversePieces) != 0)
            res |= Library.pow2(powPosition + 8);
        return res;
    }
}
