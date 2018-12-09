package echecs.pieces;

import echecs.Color;

import java.util.HashMap;

public abstract class Piece {

    protected Long position;
    protected HashMap<Long, Long> movements;
    protected HashMap<Long, Long> threatened;
    protected Color color;
    protected int value;

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public HashMap<Long, Long> getMovements() {
        return movements;
    }

    public Color getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }

    public abstract long getThreatened(long occupiedCells);

    public abstract long legalMovements(long myPieces, long adversePieces);
}
