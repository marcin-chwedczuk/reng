package pl.marcinchwedczuk.reng;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PuzzleTest {

    private boolean isValidSolution(char[][] board) {
        // Check rows
        for (int row = 0; row < 3; row++) {
            // Check board[row] contains unique characters
            Set<Character> seen = new HashSet<>();
            for (int col = 0; col < 3; col++) {
                if (!seen.add(board[row][col])) {
                    // We spot repeated character
                    return false;
                }
            }
        }

        // Check columns
        for (int col = 0; col < 3; col++) {
            // Check board[][col] contains unique characters
            Set<Character> seen = new HashSet<>();
            for (int row = 0; row < 3; row++) {
                if (!seen.add(board[row][col])) {
                    // We spot repeated character
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isTaken(char[][] board, int position) {
        int row = position / 3;
        int col = position % 3;

        return (board[row][col] != '?');
    }

    private void putShape(char[][] board, int position, char shape) {
        int row = position / 3;
        int col = position % 3;

        board[row][col] = shape;
    }

    // Board positions:
    // [0 | 1 | 2]
    // [3 | 4 | 5]
    // [6 | 7 | 8]
    // Notice that rowIndex = currentPosition / 3
    // and         colIndex = currentPosition % 3
    private boolean solve(char[][] board, int currentPosition) {
        if (currentPosition == 9) {
            return isValidSolution(board);
        }

        if (isTaken(board, currentPosition)) {
            return solve(board, currentPosition+1);
        }

        for (char shape: new char[] { 'C', 'H', 'R' }) {
            putShape(board, currentPosition, shape);

            if (solve(board, currentPosition + 1)) {
                return true;
            }

            // Clear position
            putShape(board, currentPosition, '?');
        }

        return false;
    }

    @Test
    public void puzzle_example() {
        char[][] board = {
            { 'C', '?', 'R' },
            { 'H', '?', '?' },
            { '?', '?', 'H' },
        };

        solve(board, 0);

        char[][] expected = {
            { 'C', 'H', 'R' },
            { 'H', 'R', 'C' },
            { 'R', 'C', 'H' },
        };

        assertEqual(expected, board);
    }

    private void assertEqual(char[][] expected, char[][] actual) {
        String expectedStr = toString(expected);
        String actualStr = toString(actual);
        assertEquals(expectedStr, actualStr);
    }

    private String toString(char[][] board) {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < 3; row++) {
            sb.append('[');
            for (int col = 0; col < 3; col++) {
                sb.append(board[row][col]);
                if (col < 2) sb.append(',');
            }
            sb.append(']').append('\n');
        }

        return sb.toString();
    }
}
