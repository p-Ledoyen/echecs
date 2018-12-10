package echecs.pieces;

import echecs.Color;
import echecs.Constant;
import echecs.Library;

import java.util.HashMap;

public class Knight extends Piece {

    public Knight(int pow, Color color) {
        this.color = color;
        this.value = Constant.KnightValue;
        this.alive = true;

        this.position = Library.pow2(pow);

        this.movements = new HashMap<Long, Long>();
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            if (i % 8 > 1) {
                if (i < 56)
                    dep |= Library.pow2(i + 6);
                if (i > 7)
                    dep |= Library.pow2(i - 10);
            }
            if (i % 8 < 6) {
                if (i > 7)
                    dep |= Library.pow2(i - 6);
                if (i < 56)
                    dep |= Library.pow2(i + 10);
            }

            if (i % 8 > 0) {
                if (i < 48)
                    dep |= Library.pow2(i + 15);
                if (i > 8)
                    dep |= Library.pow2(i - 17);
            }

            if (i % 8 < 7) {
                if (i > 8)
                    dep |= Library.pow2(i - 15);
                if (i < 48)
                    dep |= Library.pow2(i + 17);
            }

            this.movements.put(Library.pow2(i), dep);
        }
    }

    @Override
    public long specializedThreatenedCells(long occupiedCells) {
        return this.movements.get(this.position);
    }

    @Override
    public long specializedLegalMovements(long myPieces, long adversePieces) {
        return this.movements.get(this.position) & ~myPieces;
    }
}
