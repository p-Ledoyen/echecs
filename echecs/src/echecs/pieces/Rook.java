package echecs.pieces;

import echecs.Color;
import echecs.Library;

import java.util.HashMap;

public class Rook extends Piece {

    public Rook(int pow, Color color) {
        this.color = color;
        this.value = 5;

        this.position = Library.pow2(pow);

        this.movements = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            for (int j = i % 8; j < 64; j += 8)
                if (j != i)
                    dep |= Library.pow2(j);

            for (int j = i / 8 * 8; j < i / 8 * 8 + 8; j++)
                if (j != i)
                    dep |= Library.pow2(j);
            this.movements.put(Library.pow2(i), dep);
        }
    }

    @Override
    public long getThreatened(long occupiedCells) {
        long res = 0;
        int i = Library.log2(this.position);
        while (i < 56) {
            i += 8;
            if ((Library.pow2(i) & occupiedCells) == 0)
                res |= Library.pow2(i);
            else
                break;
        }
        i = Library.log2(this.position);
        while (i > 8) {
            i -= 8;
            if ((Library.pow2(i) & occupiedCells) == 0)
                res |= Library.pow2(i);
            else
                break;
        }
        i = Library.log2(this.position);
        while (i % 8 != 0) {
            i--;
            if ((Library.pow2(i) & occupiedCells) == 0)
                res |= Library.pow2(i);
            else
                break;
        }
        i = Library.log2(this.position);
        while (i % 8 != 7) {
            i++;
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
