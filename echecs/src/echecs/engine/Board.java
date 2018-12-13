package echecs.engine;

import echecs.Color;
import echecs.Library;
import echecs.Movement;
import echecs.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Board implements Cloneable {

    private List<Piece> pieces;
    private Stack<Piece> piecesEaten;
    private Stack<Piece> promotion;

    public Board() {
        initialization();
    }

    public void initialization() {
        this.piecesEaten = new Stack<>();
        this.promotion = new Stack<>();

        // Initilization of the positions
        //      - black pieces
        this.pieces = new ArrayList<>();
        this.pieces.add(new King(60, Color.BLACK));
        this.pieces.add(new Queen(59, Color.BLACK));
        this.pieces.add(new Bishop(58, Color.BLACK));
        this.pieces.add(new Bishop(61, Color.BLACK));
        this.pieces.add(new Knight(57, Color.BLACK));
        this.pieces.add(new Knight(62, Color.BLACK));
        this.pieces.add(new Rook(56, Color.BLACK));
        this.pieces.add(new Rook(63, Color.BLACK));
        this.pieces.add(new BlackPawn(48, Color.BLACK));
        this.pieces.add(new BlackPawn(49, Color.BLACK));
        this.pieces.add(new BlackPawn(50, Color.BLACK));
        this.pieces.add(new BlackPawn(51, Color.BLACK));
        this.pieces.add(new BlackPawn(52, Color.BLACK));
        this.pieces.add(new BlackPawn(53, Color.BLACK));
        this.pieces.add(new BlackPawn(54, Color.BLACK));
        this.pieces.add(new BlackPawn(55, Color.BLACK));

        //      - white pieces
        this.pieces.add(new King(4, Color.WHITE));
        this.pieces.add(new Queen(3, Color.WHITE));
        this.pieces.add(new Bishop(5, Color.WHITE));
        this.pieces.add(new Bishop(2, Color.WHITE));
        this.pieces.add(new Knight(6, Color.WHITE));
        this.pieces.add(new Knight(1, Color.WHITE));
        this.pieces.add(new Rook(7, Color.WHITE));
        this.pieces.add(new Rook(0, Color.WHITE));
        this.pieces.add(new WhitePawn(8, Color.WHITE));
        this.pieces.add(new WhitePawn(9, Color.WHITE));
        this.pieces.add(new WhitePawn(10, Color.WHITE));
        this.pieces.add(new WhitePawn(11, Color.WHITE));
        this.pieces.add(new WhitePawn(12, Color.WHITE));
        this.pieces.add(new WhitePawn(13, Color.WHITE));
        this.pieces.add(new WhitePawn(14, Color.WHITE));
        this.pieces.add(new WhitePawn(15, Color.WHITE));
    }

    ////////////
    // Pieces //
    ////////////

    /**
     * Get the list of all pieces.
     *
     * @return The list of pieces
     */
    public List<Piece> getPieces() {
        return pieces;
    }

    ///////////
    // CELLS //
    ///////////

    /**
     * Get the cells occupied by a piece.
     * @return The cells on a bitboard of all cells occupied.
     */
    public long getOccupiedCells() {
        long res = 0;
        for (Piece p : this.pieces)
            if (p.getAlive())
                res |= p.getPosition();
        return res;
    }

    /**
     * Get the cells occupied by pieces if a certain color.
     * @param color The color we search the pieces
     * @return All the cells (in a bitboard) occupied by a piece of the color 'color'
     */
    public long piecesCells(Color color) {
        long res = 0;
        for (Piece p : this.pieces)
            if (p.getAlive() && p.getColor() == color)
                res |= p.getPosition();
        return res;
    }

    /**
     * Get the cells threatened by a piece.
     *
     * @param piece The piece we are looking for cells it can attack
     * @return The cells threatened by the piece (on a bitboard)
     */
    public long attack(Piece piece) {
        return piece.threatenedCells(this.piecesCells(piece.getColor()));
    }

    /**
     * Get all the cells threatened by a certain color.
     * @param color The color of the player
     * @return All cells threaten by a piece of the player 'color' (on a bitboard)
     */
    public long getThreatenedCells(Color color) {
        long res = 0;
        for (Piece p : this.pieces)
            if (p.getColor() == color && p.getAlive())
                res |= this.attack(p);
        return res;
    }

    //////////
    // MATE //
    //////////

    /**
     * Check if the kink of a color is mate.
     * @param color The color of the king
     * @return True if the king is in danger
     */
    public boolean isMate(Color color) {
        long kingAdversePosition = -1;
        for (Piece p : this.pieces)
            if (p.getColor() == color && (p instanceof King)) {
                kingAdversePosition = p.getPosition();
                break;
            }
        if (kingAdversePosition == -1)
            throw new RuntimeException("there is no adverse king");

        return (this.getThreatenedCells(Color.other(color)) & kingAdversePosition) > 0;
    }

    ///////////////
    // MOVEMENTS //
    ///////////////

    /**
     * All legal movements of a piece.
     *
     * @param piece The piece we are looking for movements.
     * @return All movements that 'piece' can make
     */
    public long legalMovements(Piece piece) {
        long myPieces = 0;
        long adversePieces = 0;

        Color color = piece.getColor();
        for (Piece p : this.pieces)
            if (p.getColor() == color && p.getAlive())
                myPieces |= p.getPosition();
            else if (p.getColor() != color && p.getAlive())
                adversePieces |= p.getPosition();

        return piece.legalMovements(myPieces, adversePieces);
    }

    /**
     * All movements a player can make.
     * @param color The color of the player
     * @return All movements which can be played by the player 'color'
     */
    public List<Movement> allLegalDeplacements(Color color) {
        List<Movement> res = new ArrayList<>();
        for (int i = 0; i < pieces.size(); i++) {
            Piece p = pieces.get(i);
            if (color == p.getColor() && p.getAlive()) {
                List<Long> finalPositions = Library.extract(this.legalMovements(p));
                for (long l : finalPositions) {
                    res.add(new Movement(p.getPosition(), l));
                }
            }
        }
        return res;
    }

    /**
     * Make a movment on the board.
     * @param movement The movement to make
     */
    public void makeMovement(Movement movement) {
        boolean eaten = false;
        for (Piece p : this.pieces)
            if (p.getPosition() == movement.getFinalPosition() && p.getAlive()) {
                p.setAlive(false);
                this.piecesEaten.add(p);
                eaten = true;
                break;
            }
        if (!eaten)
            this.piecesEaten.push(null);

        boolean promotion = false;
        for (int i = 0; i < pieces.size(); i++) {
            Piece p = pieces.get(i);
            if (p.getPosition() == movement.getInitialPosition() && p.getAlive()) {
                p.setPosition(movement.getFinalPosition());
                if (p instanceof BlackPawn && Library.log2(p.getPosition()) / 8 == 0) {
                    pieces.set(i, new Queen(Library.log2(p.getPosition()), Color.BLACK));
                    this.promotion.push(p);
                    promotion = true;
                } else if (p instanceof WhitePawn && Library.log2(p.getPosition()) / 8 == 7) {
                    pieces.set(i, new Queen(Library.log2(p.getPosition()), Color.WHITE));
                    this.promotion.push(pieces.get(i));
                    promotion = true;
                }
                break;
            }
        }

        if (!promotion)
            this.promotion.push(null);

    }

    /**
     * Cancel a movement.
     * @param movement The movment to cancel
     */
    public void cancelMovement(Movement movement) {
        Piece p = this.promotion.pop();

        for (int i = 0; i < pieces.size(); i++) {
            Piece p1 = pieces.get(i);
            if (p1.getPosition() == movement.getFinalPosition() && p1.getAlive()) {
                if (p != null)
                    // a pawn has been promoted
                    if (p.getColor() == Color.WHITE)
                        pieces.set(i, new WhitePawn(Library.log2(movement.getInitialPosition()), Color.WHITE));
                    else
                        pieces.set(i, new BlackPawn(Library.log2(movement.getInitialPosition()), Color.BLACK));
                else
                    p1.setPosition(movement.getInitialPosition());
                break;
            }
        }

        p = piecesEaten.pop();
        if (p != null)
            p.setAlive(true);

    }

    /**
     * Get number of 1 on a bitboard.
     *
     * @param bitboard The bitboard.
     * @return
     */
    public int number(long bitboard) {
        return Long.toBinaryString(bitboard).replace("0", "").length();
    }

    /**
     * Copy a board (to give to differents threads).
     * @return A new board with the same dipositions of pieces than 'this'
     */
    public Board copy() {
        Board copy = new Board();
        copy.pieces.clear();
        for (Piece p : pieces) {
            copy.pieces.add(p.copy());
        }
        return copy;
    }

    @Override
    public String toString() {
        String res = "";
        String chiffre = Long.toBinaryString(this.getOccupiedCells());
        String binaire = chiffre;
        for (int i = 0; i < 64 - chiffre.length(); i++)
            binaire = "0" + binaire;

        for (int i = 0; i < 64; i += 8) {
            String ligne = binaire.substring(i, i + 8);
            for (int j = 7; j >= 0; j--)
                res += ligne.charAt(j);
            //res += "\n";
            res += " ";
        }
        return res;
    }
}
