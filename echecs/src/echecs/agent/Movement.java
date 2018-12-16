package echecs.agent;

import echecs.Library;

public class Movement {

    private long initialPosition;
    private long finalPosition;

    public Movement(long initialPosition, long finalPosition) {
        this.initialPosition = initialPosition;
        this.finalPosition = finalPosition;
    }

    public Movement(String movement) {
        this.initialPosition = Library.getCell(movement.charAt(0), movement.charAt(1));

        this.finalPosition = Library.getCell(movement.charAt(2), movement.charAt(3));
    }

    public long getInitialPosition() {
        return initialPosition;
    }

    public long getFinalPosition() {
        return finalPosition;
    }

    @Override
    public String toString() {
        return Library.getCell(initialPosition) + "" + Library.getCell(finalPosition);
    }
}
