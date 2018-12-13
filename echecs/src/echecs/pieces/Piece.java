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

    /**
     * Get the cells threatened by a piece.
     *
     * @param occupiedCells The cells ocuupied by other pieces.
     * @return All cells threatened by 'this' (on a bitboard).
     */
    public abstract long specializedThreatenedCells(long occupiedCells);

    /**
     * Get all movements that a piece can make.
     * @param myPieces The cells occupied by the pieces of the player
     * @param adversePieces The cells occupied by the pieces of the other player
     * @return All movements that the piece can make (on a bitboard)
     */
    public abstract long specializedLegalMovements(long myPieces, long adversePieces);

    /**
     * Get the cells threatened by a generic piece.
     * @param occupiedCells The cells ocuupied by other pieces.
     * @return All cells threatened by 'this' (on a bitboard).
     */
    public long threatenedCells(long occupiedCells) {
        if (this.alive)
            return this.specializedThreatenedCells(occupiedCells);
        else
            return 0;
    }

    /**
     * Get all movements that a generic piece can make.
     * @param myPieces The cells occupied by the pieces of the player
     * @param adversePieces The cells occupied by the pieces of the other player
     * @return All movements that the piece can make (on a bitboard)
     */
    public long legalMovements(long myPieces, long adversePieces) {
        if (this.alive)
            return this.specializedLegalMovements(myPieces, adversePieces);
        else
            return 0;
    }

    /**
     * Copy a piece.
     * @return A nex piece similar to this.
     */
    public Piece copy() {
        try {
            return (Piece) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
