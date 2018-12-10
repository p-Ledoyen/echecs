package echecs;

import echecs.pieces.Piece;

public class Movement {

    private Piece piece;
    private long initialPosition;
    private long finalPosition;

    public Movement(Piece piece, long initialPosition, long finalPosition) {
        this.piece = piece;
        this.initialPosition = initialPosition;
        this.finalPosition = finalPosition;
    }

    public Piece getPiece() {
        return piece;
    }

    public long getInitialPosition() {
        return initialPosition;
    }

    public long getFinalPosition() {
        return finalPosition;
    }

    @Override
    public String toString() {
        return Library.getCase(initialPosition) + "" + Library.getCase(finalPosition);
    }
}
