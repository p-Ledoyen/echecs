package echecs.pieces;

import echecs.Color;
import echecs.Library;

import java.util.HashMap;

public class Bishop extends Piece {

    public Bishop(int pow, Color color) {
        this.color = color;
        this.value = 3;

        this.position = Library.pow2(pow);

        this.movements = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            if (i % 8 != 7 && i < 56)
                for (int j = i + 9; j < 64 && j % 8 != 0; j += 9)
                    dep |= Library.pow2(j);

            if (i % 8 != 0 && i < 56)
                for (int j = i + 7; j < 64 && j % 8 != 7; j += 7)
                    dep |= Library.pow2(j);

            if (i % 8 != 7 && i > 7)
                for (int j = i - 7; j > 0 && j % 8 != 0; j -= 7)
                    dep |= Library.pow2(j);

            if (i % 8 != 0 && i > 7)
                for (int j = i - 9; j > 0 && j % 8 != 7; j -= 9)
                    dep |= Library.pow2(j);

            this.movements.put(Library.pow2(i), dep);
        }
    }

    @Override
    public long getThreatened(long occupiedCells) {
        long res = 0;
        int i = Library.log2(this.position);
        while (i > 8 && i % 8 != 0) {
            i -= 9;
            if ((Library.pow2(i) & occupiedCells) == 0)
                res |= Library.pow2(i);
            else
                break;
        }
        i = Library.log2(this.position);
        while (i > 8 && i % 8 != 7) {
            i -= 7;
            if ((Library.pow2(i) & occupiedCells) == 0)
                res |= Library.pow2(i);
            else
                break;
        }
        i = Library.log2(this.position);
        while (i < 56 && i % 8 != 0) {
            i += 7;
            if ((Library.pow2(i) & occupiedCells) == 0)
                res |= Library.pow2(i);
            else
                break;
        }
        i = Library.log2(this.position);
        while (i < 56 && i % 8 != 7) {
            i += 9;
            if ((Library.pow2(i) & occupiedCells) == 0)
                res |= Library.pow2(i);
            else
                break;
        }
        return res;
    }

    @Override
    public long legalMovements(long myPieces, long adversePieces) {
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
