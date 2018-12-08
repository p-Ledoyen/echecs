import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    // true if the castling isn't possible anymore (rook movement or king movement)
    private boolean noMovementSmallCastling;
    private boolean noMovementBigCastling;

    public Board() {
        // Initilization of the positions
        //      - black pieces
        this.blackKingPosition = Library.pow2(60);
        this.blackKnightPosition = Library.pow2(57) | Library.pow2(62);
        this.blackPawnPosition = Library.pow2(48)
                | Library.pow2(49)
                | Library.pow2(50)
                | Library.pow2(51)
                | Library.pow2(52)
                | Library.pow2(53)
                | Library.pow2(54)
                | Library.pow2(55);
        this.blackQueenPosition = Library.pow2(59);
        this.blackBishopPosition = Library.pow2(58) | Library.pow2(61);
        this.blackRookPosition = Library.pow2(56) | Library.pow2(63);

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
                dep |= Library.pow2(i - 1);
                if (i < 56)
                    dep |= Library.pow2(i + 7);
                if (i > 7)
                    dep |= Library.pow2(i - 9);
            }
            if (i > 7)
                dep |= Library.pow2(i - 8);
            if (i % 8 != 7) {
                dep |= Library.pow2(i + 1);
                if (i < 56)
                    dep |= Library.pow2(i + 9);
                if (i > 7)
                    dep |= Library.pow2(i - 7);
            }
            if (i < 56)
                dep |= Library.pow2(i + 8);

            kingMovements.put(Library.pow2(i), dep);
        }

        //      - knight
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            if (i % 8 > 1) {
                if (i < 56)
                    dep |= Library.pow2(i + 6);
                if (i > 7)
                    dep |= Library.pow2(i - 10);
            }
            if (i % 8 < 6) {
                if (i > 7)
                    dep |= Library.pow2(i - 6);
                if (i < 56)
                    dep |= Library.pow2(i + 10);
            }

            if (i % 8 > 0) {
                if (i < 48)
                    dep |= Library.pow2(i + 15);
                if (i > 8)
                    dep |= Library.pow2(i - 17);
            }

            if (i % 8 < 7) {
                if (i > 8)
                    dep |= Library.pow2(i - 15);
                if (i < 48)
                    dep |= Library.pow2(i + 17);
            }

            knightMovements.put(Library.pow2(i), dep);
        }
        //      - white pawn
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            if (i > 7 && i < 16)
                dep |= Library.pow2(i + 16);
            if (i < 56)
                dep |= Library.pow2(i + 8);

            whitePawnMovements.put(Library.pow2(i), dep);
        }
        //      - black pawn
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            if (i > 47 && i < 56)
                dep |= Library.pow2(i - 16);
            if (i > 7)
                dep |= Library.pow2(i - 8);

            blackPawnMovements.put(Library.pow2(i), dep);
        }
        //      - rook
        for (int i = 0; i < 64; i++) {
            long dep = 0;
            for (int j = i % 8; j < 64; j += 8)
                if (j != i)
                    dep |= Library.pow2(j);

            for (int j = i / 8 * 8; j < i / 8 * 8 + 8; j++)
                if (j != i)
                    dep |= Library.pow2(j);
            rookMovements.put(Library.pow2(i), dep);
        }
        //      - bishop
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

            bishopMovements.put(Library.pow2(i), dep);
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
                    cells |= Library.pow2(i - 9);
                if (i % 8 != 7)
                    cells |= Library.pow2(i - 7);
            }
            blackPawnThreatened.put(Library.pow2(i), cells);
        }
        //      - white pawn
        for (int i = 0; i < 64; i++) {
            long cells = 0;
            if (i < 56) {
                if (i % 8 != 0)
                    cells |= Library.pow2(i + 7);
                if (i % 8 != 7)
                    cells |= Library.pow2(i + 9);
            }
            whitePawnThreatened.put(Library.pow2(i), cells);
        }
    }


    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    /***************
     ** Positions **
     ***************/
    public long getBlackKingPosition() {
        return blackKingPosition;
    }

    public long getBlackKnightPosition() {
        return blackKnightPosition;
    }

    public long getBlackPawnPosition() {
        return blackPawnPosition;
    }

    public long getBlackQueenPosition() {
        return blackQueenPosition;
    }

    public long getBlackBishopPosition() {
        return blackBishopPosition;
    }

    public long getBlackRookPosition() {
        return blackRookPosition;
    }

    public long getWhiteKingPosition() {
        return whiteKingPosition;
    }

    public long getWhiteKnightPosition() {
        return whiteKnightPosition;
    }

    public long getWhitePawnPosition() {
        return whitePawnPosition;
    }

    public long getWhiteQueenPosition() {
        return whiteQueenPosition;
    }

    public long getWhiteBishopPosition() {
        return whiteBishopPosition;
    }

    public long getWhiteRookPosition() {
        return whiteRookPosition;
    }

    /***************
     ** Movements **
     ***************/

    public HashMap<Long, Long> getKingMovements() {
        return kingMovements;
    }

    public HashMap<Long, Long> getKnightMovements() {
        return knightMovements;
    }

    public HashMap<Long, Long> getBlackPawnMovements() {
        return blackPawnMovements;
    }

    public HashMap<Long, Long> getWhitePawnMovements() {
        return whitePawnMovements;
    }

    public HashMap<Long, Long> getQueenMovements() {
        return queenMovements;
    }

    public HashMap<Long, Long> getBishopMovements() {
        return bishopMovements;
    }

    public HashMap<Long, Long> getRookMovements() {
        return rookMovements;
    }

    public long getOccupiedCells() {
        return this.blackKingPosition
                | this.whiteKingPosition
                | this.blackKnightPosition
                | this.whiteKnightPosition
                | this.blackQueenPosition
                | this.whiteQueenPosition
                | this.blackPawnPosition
                | this.whitePawnPosition
                | this.blackRookPosition
                | this.whiteRookPosition
                | this.blackBishopPosition
                | this.whiteBishopPosition;
    }

    public long getThreatenedCells(String color) {
        if (color.equals(Constante.WHITE))
            return this.kingMovements.get(this.whiteKingPosition)
                    | this.queenMovements.get(this.whiteQueenPosition)
                    | this.knightMovements.get(this.whiteKnightPosition)
                    | this.rookMovements.get(this.whiteRookPosition)
                    | this.bishopMovements.get(this.whiteBishopPosition)
                    | this.whitePawnThreatened.get(this.whitePawnPosition);
        else if (color.equals(Constante.BLACK))
            return this.kingMovements.get(this.whiteKingPosition)
                    | this.queenMovements.get(this.blackQueenPosition)
                    | this.knightMovements.get(this.blackKnightPosition)
                    | this.rookMovements.get(this.blackRookPosition)
                    | this.bishopMovements.get(this.blackBishopPosition)
                    | this.blackPawnThreatened.get(this.blackPawnPosition);
        else
            throw new IllegalArgumentException();
    }

    /**
     * @param type 0 for the "small castling" and 1 for the "big castling"
     * @return
     */
    public boolean isCastlingPossible(int type, String color) {
        long occupiedCells = this.getOccupiedCells();

        boolean noPieces = false;
        if (color.equals(Constante.WHITE) && type == 0)
            noPieces = (occupiedCells & 32 & 64) == 0;
        else if (color.equals(Constante.WHITE) && type == 1)
            noPieces = (occupiedCells & 2 & 4 & 8) == 0;
        else if (color.equals(Constante.BLACK) && type == 0)
            noPieces = (occupiedCells & Library.pow2(57) & Library.pow2(58)) == 0;
        else if (color.equals(Constante.BLACK) && type == 1)
            noPieces = (occupiedCells & Library.pow2(60) & Library.pow2(61) & Library.pow2(62)) == 0;

        if (type == 0)
            return noMovementSmallCastling & noPieces;
        else if (type == 1)
            return noMovementBigCastling & noPieces;
        else
            throw new IllegalArgumentException();
    }

    public boolean isMate(String color) {
        boolean res = false;
        long threatenedCells = 0;
        if (color.equals(Constante.WHITE)) {
            for (long l : Library.extract(blackRookPosition))
                threatenedCells |= legalDeplacements(l, "rook");
            for (long l : Library.extract(blackKnightPosition))
                threatenedCells |= legalDeplacements(l, "knight");
            for (long l : Library.extract(blackBishopPosition))
                threatenedCells |= legalDeplacements(l, "bishop");
            for (long l : Library.extract(blackQueenPosition))
                threatenedCells |= legalDeplacements(l, "queen");
            for (long l : Library.extract(blackPawnPosition))
                threatenedCells |= legalDeplacements(l, "black_pawn");
            return (threatenedCells & whiteKingPosition) > 0;
        } else if (color.equals(Constante.BLACK)) {
            for (long l : Library.extract(whiteRookPosition))
                threatenedCells |= legalDeplacements(l, "rook");
            for (long l : Library.extract(whiteKnightPosition))
                threatenedCells |= legalDeplacements(l, "knight");
            for (long l : Library.extract(whiteBishopPosition))
                threatenedCells |= legalDeplacements(l, "bishop");
            for (long l : Library.extract(whiteQueenPosition))
                threatenedCells |= legalDeplacements(l, "queen");
            for (long l : Library.extract(whitePawnPosition))
                threatenedCells |= legalDeplacements(l, "white_pawn");
            return (threatenedCells & blackKingPosition) > 0;
        } else throw new IllegalArgumentException();
    }

    public long legalDeplacements(Long position, String type) {
        long positionsOccupees = this.getOccupiedCells();

        long rookMovements = 0;
        long bishopMovements = 0;
        if (type.equals("king"))
            return this.kingMovements.get(position) & ~positionsOccupees;
        else if (type.equals("knight"))
            return this.knightMovements.get(position) & ~positionsOccupees;
        else if (type.equals("white_pawn"))
            return this.whitePawnMovements.get(position) & ~positionsOccupees;
        else if (type.equals("black_pawn"))
            return this.blackPawnMovements.get(position) & ~positionsOccupees;
        else if (type.equals("rook") || type.equals("queen")) {
            for (int i = (int) (Math.log(position) / Math.log(2)) + 8; i < 64; i += 8)
                if ((Library.pow2(i) & positionsOccupees) == 0)
                    rookMovements |= Library.pow2(i);
                else
                    break;
            for (int i = (int) (Math.log(position) / Math.log(2)) - 8; i > 0; i -= 8)
                if ((Library.pow2(i) & positionsOccupees) == 0)
                    rookMovements |= Library.pow2(i);
                else
                    break;
            for (int i = (int) (Math.log(position) / Math.log(2)) - 1; i % 8 != 0; i--)
                if ((Library.pow2(i) & positionsOccupees) == 0)
                    rookMovements |= Library.pow2(i);
                else
                    break;
            for (int i = (int) (Math.log(position) / Math.log(2)) + 1; i % 8 != 7; i++)
                if ((Library.pow2(i) & positionsOccupees) == 0)
                    rookMovements |= Library.pow2(i);
                else
                    break;
        }
        if (type.equals("bishop") || type.equals("queen")) {
            for (int i = (int) (Math.log(position) / Math.log(2)) - 9; i > 0 && i % 8 != 7; i -= 9)
                if ((Library.pow2(i) & positionsOccupees) == 0)
                    bishopMovements |= Library.pow2(i);
                else
                    break;
            for (int i = (int) (Math.log(position) / Math.log(2)) - 7; i > 0 && i % 8 != 0; i -= 7)
                if ((Library.pow2(i) & positionsOccupees) == 0)
                    bishopMovements |= Library.pow2(i);
                else
                    break;
            for (int i = (int) (Math.log(position) / Math.log(2)) + 7; i < 64 && i % 8 != 7; i += 7)
                if ((Library.pow2(i) & positionsOccupees) == 0)
                    bishopMovements |= Library.pow2(i);
                else
                    break;
            for (int i = (int) (Math.log(position) / Math.log(2)) + 9; i < 64 && i % 8 != 0; i += 9)
                if ((Library.pow2(i) & positionsOccupees) == 0)
                    bishopMovements |= Library.pow2(i);
                else
                    break;
        }

        switch (type) {
            case "queen":
                return rookMovements | bishopMovements;
            case "rook":
                return rookMovements;
            case "bishop":
                return bishopMovements;
        }

        throw new IllegalArgumentException();
    }

    public HashMap<Long, Couple> allLegalDeplacements(String color) {
        HashMap<Long, Couple> res = new HashMap<>();
        if (color.equals(Constante.BLACK)) {
            res.put(this.blackKingPosition, new Couple("king", this.legalDeplacements(blackKingPosition, "king")));
            for (long l : Library.extract(this.blackQueenPosition))
                res.put(l, new Couple("queen", this.legalDeplacements(l, "queen")));
            for (long l : Library.extract(this.blackRookPosition))
                res.put(l, new Couple("rook", this.legalDeplacements(l, "rook")));
            for (long l : Library.extract(this.blackBishopPosition))
                res.put(l, new Couple("bishop", this.legalDeplacements(l, "bishop")));
            for (long l : Library.extract(this.blackKnightPosition))
                res.put(l, new Couple("knight", this.legalDeplacements(l, "knight")));
            for (long l : Library.extract(this.blackPawnPosition))
                res.put(l, new Couple("pawn", this.legalDeplacements(l, "black_pawn")));
        } else if (color.equals(Constante.WHITE)) {
            res.put(this.whiteKingPosition, new Couple("king", this.legalDeplacements(blackKingPosition, "king")));
            for (long l : Library.extract(this.whiteQueenPosition))
                res.put(l, new Couple("queen", this.legalDeplacements(l, "queen")));
            for (long l : Library.extract(this.whiteRookPosition))
                res.put(l, new Couple("rook", this.legalDeplacements(l, "rook")));
            for (long l : Library.extract(this.whiteBishopPosition))
                res.put(l, new Couple("bishop", this.legalDeplacements(l, "bishop")));
            for (long l : Library.extract(this.whiteKnightPosition))
                res.put(l, new Couple("knight", this.legalDeplacements(l, "knight")));
            for (long l : Library.extract(this.whitePawnPosition))
                res.put(l, new Couple("pawn", this.legalDeplacements(l, "white_pawn")));
        } else {
            throw new IllegalArgumentException();
        }

        List<Long> toDelete = new ArrayList<>();
        for (long position : res.keySet())
            if (res.get(position).getSecond() == 0)
                toDelete.add(position);

        for (long l : toDelete)
            res.remove(l);

        return res;
    }

    public void makeDeplacement(long initialPosition, String kind, long newPosition, String color) {
        switch (kind) {
            case "king":
                if (color.equals(Constante.BLACK))
                    this.blackKingPosition = newPosition;
                else if (color.equals(Constante.WHITE))
                    this.whiteKingPosition = newPosition;
                else
                    throw new IllegalArgumentException();
                break;
            case "knight":
                if (color.equals(Constante.BLACK))
                    this.blackKnightPosition -= initialPosition - newPosition;
                else if (color.equals(Constante.WHITE))
                    this.whiteKnightPosition -= initialPosition - newPosition;
                else
                    throw new IllegalArgumentException();
                break;
            case "queen":
                if (color.equals(Constante.BLACK))
                    this.blackQueenPosition -= initialPosition - newPosition;
                else if (color.equals(Constante.WHITE))
                    this.whiteQueenPosition -= initialPosition - newPosition;
                else
                    throw new IllegalArgumentException();
                break;
            case "rook":
                if (color.equals(Constante.BLACK))
                    this.blackRookPosition -= initialPosition - newPosition;
                else if (color.equals(Constante.WHITE))
                    this.whiteRookPosition -= initialPosition - newPosition;
                else
                    throw new IllegalArgumentException();
                break;
            case "bishop":
                if (color.equals(Constante.BLACK))
                    this.blackBishopPosition -= initialPosition - newPosition;
                else if (color.equals(Constante.WHITE))
                    this.whiteBishopPosition -= initialPosition - newPosition;
                else
                    throw new IllegalArgumentException();
                break;
            case "pawn":
                if (color.equals(Constante.BLACK)) {
                    this.blackPawnPosition -= initialPosition - newPosition;
                } else if (color.equals(Constante.WHITE))
                    this.whitePawnPosition -= initialPosition - newPosition;
                else
                    throw new IllegalArgumentException();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public int numberPieces(long l) {
        return Long.toBinaryString(l).replace("0", "").length();
    }

    public void afficher() {
        Library.afficherLong(this.getOccupiedCells());
    }

}
