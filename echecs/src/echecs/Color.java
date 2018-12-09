package echecs;

public enum Color {
    BLACK,
    WHITE;

    public static Color other(Color color) {
        if (color == WHITE)
            return BLACK;
        else
            return WHITE;
    }

}
