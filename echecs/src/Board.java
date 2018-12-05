import java.util.HashMap;

public class Board {

    // Positions of all pieces
    private long blackKingPosition;
    private long blackKnightPosition;
    private long blackPawnPosition;
    private long blackQueenPosition;
    private long blackBishopPosition;
    private long blackRookPosition;
    private long whiteKingPosition;
    private long whiteKnightPosition;
    private long whitePawnPosition;
    private long whiteQueenPosition;
    private long whiteBishopPosition;
    private long whiteRookPosition;

    // Legal movements of all pieces
    private HashMap<Long, Long> kingMovements;
    private HashMap<Long, Long> knightMovements;
    private HashMap<Long, Long> blackPawnMovements;
    private HashMap<Long, Long> whitePawnMovements;
    private HashMap<Long, Long> queenMovements;
    private HashMap<Long, Long> bishopMovements;
    private HashMap<Long, Long> rookMovements;

    // Cells threatened by a pawn
    private HashMap<Long, Long> blackPawnThreatened;
    private HashMap<Long, Long> whitePawnThreatened;

    public Board() {
        // Initilization of the positions
        //      - black pieces
        this.blackKingPosition = pow2(60);
        this.blackKnightPosition = pow2(57) | pow2(62);
        this.blackPawnPosition = pow2(48)
                | pow2(49)
                | pow2(50)
                | pow2(51)
                | pow2(52)
                | pow2(53)
                | pow2(54)
                | pow2(55);
        this.blackQueenPosition = pow2(59);
        this.blackBishopPosition = pow2(58) | pow2(61);
        this.blackRookPosition = pow2(56) | pow2(63);

        //      - white pieces
        this.whiteKingPosition = 16;
        this.whiteKnightPosition = 66;
        this.whitePawnPosition = 65280;
        this.whiteQueenPosition = 8;
        this.whiteBishopPosition = 36;
        this.whiteRookPosition = 129;

        // Initialization of all possible deplacements
        this.kingMovements = new HashMap<>();
        this.knightMovements = new HashMap<>();
        this.whitePawnMovements = new HashMap<>();
        this.blackPawnMovements = new HashMap<>();
        this.queenMovements = new HashMap<>();
        this.rookMovements = new HashMap<>();
        this.bishopMovements = new HashMap<>();

        //      - king
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            if (i % 8 != 0) {
                dep |= pow2(i - 1);
                if (i < 56)
                    dep |= pow2(i + 7);
                if (i > 7)
                    dep |= pow2(i - 9);
            }
            if (i > 7)
                dep |= pow2(i - 8);
            if (i % 8 != 7) {
                dep |= pow2(i + 1);
                if (i < 56)
                    dep |= pow2(i + 9);
                if (i > 7)
                    dep |= pow2(i - 7);
            }
            if (i < 56)
                dep |= pow2(i + 8);

            kingMovements.put(pow2(i), dep);
        }

        //      - knight
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            if (i % 8 > 1) {
                if (i < 56)
                    dep |= pow2(i + 6);
                if (i > 7)
                    dep |= pow2(i - 10);
            }
            if (i % 8 < 6) {
                if (i > 7)
                    dep |= pow2(i - 6);
                if (i < 56)
                    dep |= pow2(i + 10);
            }

            if (i % 8 > 0) {
                if (i < 48)
                    dep |= pow2(i + 15);
                if (i > 8)
                    dep |= pow2(i - 17);
            }

            if (i % 8 < 7) {
                if (i > 8)
                    dep |= pow2(i - 15);
                if (i < 48)
                    dep |= pow2(i + 17);
            }

            knightMovements.put(pow2(i), dep);
        }
        //      - white pawn
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            if (i > 7 && i < 16)
                dep |= pow2(i + 16);
            if (i < 56)
                dep |= pow2(i + 8);

            whitePawnMovements.put(pow2(i), dep);
        }
        //      - black pawn
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            if (i > 47 && i < 56)
                dep |= pow2(i - 16);
            if (i > 7)
                dep |= pow2(i - 8);

            blackPawnMovements.put(pow2(i), dep);
        }
        //      - rook
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            for (int j = i % 8; j < 64; j += 8)
                if (j != i)
                    dep |= pow2(j);

            for (int j = i / 8 * 8; j < i / 8 * 8 + 8; j++)
                if (j != i)
                    dep |= pow2(j);
            rookMovements.put(pow2(i), dep);
        }
        //      - bishop
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            if (i % 8 != 7 && i < 56)
                for (int j = i + 9; j < 64 && j % 8 != 0; j += 9)
                    dep |= pow2(j);

            if (i % 8 != 0 && i < 56)
                for (int j = i + 7; j < 64 && j % 8 != 7; j += 7)
                    dep |= pow2(j);

            if (i % 8 != 7 && i > 7)
                for (int j = i - 7; j > 0 && j % 8 != 0; j -= 7)
                    dep |= pow2(j);

            if (i % 8 != 0 && i > 7)
                for (int j = i - 9; j > 0 && j % 8 != 7; j -= 9)
                    dep |= pow2(j);

            bishopMovements.put(pow2(i), dep);
        }
        //      - queen (rook + bishop)
        for (Long key : rookMovements.keySet())
            queenMovements.put(key, rookMovements.get(key) | bishopMovements.get(key));

        // Initialization of cells threatened
        this.blackPawnThreatened = new HashMap<>();
        this.whitePawnThreatened = new HashMap<>();
        //      - black pawn
        for (int i = 0; i < 64; i++) {
            long cells = 0;
            if (i > 7) {
                if (i % 8 != 0)
                    cells |= pow2(i - 9);
                if (i % 8 != 7)
                    cells |= pow2(i - 7);
            }
            blackPawnThreatened.put(pow2(i), cells);
        }
        //      - white pawn
        for (int i = 0; i < 64; i++) {
            long cells = 0;
            if (i < 56) {
                if (i % 8 != 0)
                    cells |= pow2(i + 7);
                if (i % 8 != 7)
                    cells |= pow2(i + 9);
            }
            whitePawnThreatened.put(pow2(i), cells);
        }

    }

    public void afficherLong(long l) {
        String chiffre = Long.toBinaryString(l);
        String binaire = chiffre;
        for (int i = 0; i < 64 - chiffre.length(); i++)
            binaire = "0" + binaire;

        for (int i = 0; i < 64; i += 8) {
            String ligne = binaire.substring(i, i + 8);
            for (int j = 7; j >= 0; j--)
                System.out.print(ligne.charAt(j));
            System.out.println();
        }
    }

    private long pow2(int pow) {
        if (pow == 63)
            return (long) -Math.pow(2, 63);
        else
            return (long) Math.pow(2, pow);
    }
}
