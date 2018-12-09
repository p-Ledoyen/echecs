package echecs;

public class Couple {
    private Movement first;
    private int second;

    public Couple(Movement first, int second) {
        this.first = first;
        this.second = second;
    }

    public Movement getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }
}