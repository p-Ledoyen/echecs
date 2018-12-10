package echecs.pieces;

import echecs.Color;
import echecs.Constant;
import echecs.Library;

import java.util.HashMap;

public class BlackPawn extends Piece {

    public BlackPawn(int pow, Color color) {
        this.color = color;
        this.value = Constant.PawnValue;
        this.alive = true;

        this.position = Library.pow2(pow);

        this.movements = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            if (i > 47 && i < 56)
                dep |= Library.pow2(i - 16);
            if (i > 7)
                dep |= Library.pow2(i - 8);

            this.movements.put(Library.pow2(i), dep);
        }

        this.threatened = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            long cells = 0;
            if (i > 7) {
                if (i % 8 != 0)
                    cells |= Library.pow2(i - 9);
                if (i % 8 != 7)
                    cells |= Library.pow2(i - 7);
            }
            this.threatened.put(Library.pow2(i), cells);
        }
    }

    @Override
    public long specializedThreatenedCells(long occupiedCells) {
        return this.threatened.get(this.position);
    }

    @Override
    public long specializedLegalMovements(long myPieces, long adversePieces) {
        return ((this.threatened.get(this.position) & adversePieces)
                | (this.movements.get(this.position) & ~myPieces & ~adversePieces));
    }
}