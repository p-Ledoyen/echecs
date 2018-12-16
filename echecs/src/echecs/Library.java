package echecs;

import java.util.ArrayList;
import java.util.List;

public abstract class Library {

    /**
     * Turn a integer (the position on the board) into a bitboard.
     *
     * @param pow The integer that represent yje position on the board (a1 = 0, h8 = 63)
     * @return A bitboard with a one at the right position
     */
    public static long pow2(int pow) {
        if (pow == 63)
            return (long) -Math.pow(2, 63);
        else
            return (long) Math.pow(2, pow);
    }

    /**
     * Trun a bitboard into a integer
     *
     * @param bitboard The bitboard to turn
     * @return An integer that represent yje position on the board (a1 = 0, h8 = 63)
     */
    public static int log2(long bitboard) {
        if (bitboard < 0)
            return 63;
        else
            return (int) (Math.log(bitboard) / Math.log(2));
    }

    /**
     * Extract all cells ocuupied in a bitboard.
     *
     * @param bitboard The bitboard
     * @return A list of bitbaord with only one cell occupied.
     */
    public static List<Long> extract(long bitboard) {
        List<Long> res = new ArrayList<>();
        String binary = Long.toBinaryString(bitboard);
        int pow = 0;
        for (int i = binary.length() - 1; i >= 0; i--) {
            if (binary.charAt(i) == '1')
                res.add(Library.pow2(pow));
            pow++;
        }
        return res;
    }

    /**
     * Print a bitboard.
     *
     * @param bitboard The bitboard
     */
    public static void printLong(long bitboard) {
        String tmp = Long.toBinaryString(bitboard);
        String binary = tmp;
        for (int i = 0; i < 64 - tmp.length(); i++)
            binary = "0" + binary;

        for (int i = 0; i < 64; i += 8) {
            String line = binary.substring(i, i + 8);
            for (int j = 7; j >= 0; j--)
                System.out.print(line.charAt(j));
            System.out.println();
        }
    }

    /**
     * Get a cell (eg. e4) from a bitboard with only one cell occupied.
     *
     * @param bitboard The bitboard
     * @return A string that represent the cell
     */
    public static String getCell(long bitboard) {
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        int pow = Library.log2(bitboard);
        int row = pow / 8 + 1;
        int column = pow % 8;
        return letters[column] + row;
    }

    /**
     * Get a bitboard with only one cell occupied from a cell
     *
     * @param letter The column of the cell on the board
     * @param row    The row of the cell on the board
     * @return A bitboard with only of cell occupied
     */
    public static long getCell(char letter, char row) {
        int column;
        switch (letter) {
            case 'a':
                column = 0;
                break;
            case 'b':
                column = 1;
                break;
            case 'c':
                column = 2;
                break;
            case 'd':
                column = 3;
                break;
            case 'e':
                column = 4;
                break;
            case 'f':
                column = 5;
                break;
            case 'g':
                column = 6;
                break;
            case 'h':
                column = 7;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return pow2((Integer.parseInt("" + row) - 1) * 8 + column);
    }

    /**
     * Get number of 1 in a binary number.
     *
     * @param l The binary number.
     * @return Number of 1 in the binary number
     */
    public static int extractNumber1(long l) {
        return Long.toBinaryString(l).replace("0", "").length();
    }
}
