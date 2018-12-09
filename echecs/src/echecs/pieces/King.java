package echecs.pieces;

import echecs.Color;
import echecs.Library;

import java.util.HashMap;

public class King extends Piece {

    public King(int pow, Color color) {
        this.color = color;
        this.value = 0;

        this.position = Library.pow2(pow);

        this.movements = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            if (i % 8 != 0) {
                dep |= Library.pow2(i - 1);
                if (i < 56)
                    dep |= Library.pow2(i + 7);
                if (i > 7)
                    dep |= Library.pow2(i - 9);
            }
            if (i > 7)
                dep |= Library.pow2(i - 8);
            if (i % 8 != 7) {
                dep |= Library.pow2(i + 1);
                if (i < 56)
                    dep |= Library.pow2(i + 9);
                if (i > 7)
                    dep |= Library.pow2(i - 7);
            }
            if (i < 56)
                dep |= Library.pow2(i + 8);

            this.movements.put(Library.pow2(i), dep);
        }
    }

    @Override
    public long getThreatened(long occupiedCells) {
        return this.movements.get(this.position);
    }

    @Override
    public long legalMovements(long myPieces, long adversePieces) {
        return this.movements.get(this.position) & ~myPieces;
    }
}
