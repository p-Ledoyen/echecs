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

    // true if the castling isn't possible anymore (rook movement or king movement)
    private boolean noMovementSmallCastling;
    private boolean noMovementBigCastling;

    public Board() {
        this.noMovementBigCastling = true;
        this.noMovementSmallCastling = true;
        this.piecesEaten = new Stack<>();

        // Initilization of the positions
        //      - black echecs.pieces
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

        //      - white echecs.pieces
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

    /////////////////////////
    // GETTERS AND SETTERS //
    /////////////////////////

    public List<Piece> getPieces() {
        return pieces;
    }

    public long getOccupiedCells() {
        long res = 0;
        for (Piece p : this.pieces)
            if (p.getAlive())
                res |= p.getPosition();
        return res;
    }

    public long attack(Piece piece) {
        return piece.threatenedCells(this.getOccupiedCells());
    }

    public long getThreatenedCells(Color color) {
        long res = 0;
        for (Piece p : this.pieces)
            if (p.getColor() == color && p.getAlive())
                res |= this.attack(p);
        return res;
    }

    /**
     * @param type 0 for the "small castling" and 1 for the "big castling"
     * @return
     */
    public boolean isCastlingPossible(int type, Color color) {
        long occupiedCells = this.getOccupiedCells();

        boolean noPieces = false;
        if (color == Color.WHITE && type == 0)
            noPieces = (occupiedCells & 32 & 64) == 0;
        else if (color == Color.WHITE && type == 1)
            noPieces = (occupiedCells & 2 & 4 & 8) == 0;
        else if (color == Color.BLACK && type == 0)
            noPieces = (occupiedCells & Library.pow2(57) & Library.pow2(58)) == 0;
        else if (color == Color.BLACK && type == 1)
            noPieces = (occupiedCells & Library.pow2(60) & Library.pow2(61) & Library.pow2(62)) == 0;

        if (type == 0)
            return noMovementSmallCastling & noPieces;
        else if (type == 1)
            return noMovementBigCastling & noPieces;
        else
            throw new IllegalArgumentException();
    }

    public boolean isMate(Color color) {
        boolean res = false;
        // get te adverse king position
        long kingAdversePosition = -1;
        for (Piece p : this.pieces)
            if (p.getColor() == color && (p instanceof King))
                kingAdversePosition = p.getPosition();
        if (kingAdversePosition == -1)
            throw new RuntimeException("there is no adverse king");

        return (this.getThreatenedCells(color) & kingAdversePosition) > 0;
    }

    public long legalDeplacements(Piece piece) {
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

    public List<Movement> allLegalDeplacements(Color color) {
        List<Movement> res = new ArrayList<>();
        for (Piece p : this.pieces)
            if (color == p.getColor() && p.getAlive()) {
                List<Long> finalPositions = Library.extract(this.legalDeplacements(p));
                for (long l : finalPositions) {
                    res.add(new Movement(p, p.getPosition(), l));
                }
            }

        return res;
    }

    public void makeMovement(Movement movement) {
        boolean eaten = false;
        for (Piece p : this.pieces)
            if (p.getPosition() == movement.getFinalPosition() && p.getAlive() && p != movement.getPiece()) {
                p.setAlive(false);
                this.piecesEaten.add(p);
                eaten = true;
                break;
            }
        if (!eaten)
            this.piecesEaten.push(null);
        movement.getPiece().setPosition(movement.getFinalPosition());

    }

    public void cancelMovement(Movement movement) {
        Piece p = piecesEaten.pop();
        if (p != null)
            p.setAlive(true);
        movement.getPiece().setPosition(movement.getInitialPosition());
    }

    public int numberPieces(long l) {
        return Long.toBinaryString(l).replace("0", "").length();
    }

    public void afficher() {
        Library.afficherLong(this.getOccupiedCells());
    }

    public Board copy() {
        Board copy = new Board();
        copy.pieces.clear();
        for (Piece p : pieces) {
            copy.pieces.add((Piece) p.copy());
        }
        return copy;
    }
}
