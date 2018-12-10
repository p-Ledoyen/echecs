package echecs.pieces;

import echecs.Color;

import java.util.HashMap;

public abstract class Piece implements Cloneable {

    protected Long position;
    protected HashMap<Long, Long> movements;
    protected HashMap<Long, Long> threatened;
    protected Color color;
    protected int value;
    protected boolean alive;

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

    public boolean getAlive() {
        return this.alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public abstract long specializedThreatenedCells(long occupiedCells);

    public abstract long specializedLegalMovements(long myPieces, long adversePieces);

    public long threatenedCells(long occupiedCells) {
        if (this.alive)
            return this.specializedThreatenedCells(occupiedCells);
        else
            return 0;
    }

    public long legalMovements(long myPieces, long adversePieces) {
        if (this.alive)
            return this.specializedLegalMovements(myPieces, adversePieces);
        else
            return 0;
    }

    public Piece copy() {
        try {
            return (Piece) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
