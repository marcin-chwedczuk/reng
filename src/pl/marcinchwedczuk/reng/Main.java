package pl.marcinchwedczuk.reng;

import pl.marcinchwedczuk.reng.parser.RParseException;
import pl.marcinchwedczuk.reng.parser.RParser;

import java.io.Console;

public class Main {
    // Run from terminal, IntelliJ does not support System.console().
    // java -cp ./out/production/reng/ pl.marcinchwedczuk.reng.Main
    public static void main(String[] args) {
        Console con = System.console();
        if (con == null) {
            System.err.println("System.console() returned null.");
            System.exit(1);
        }

        con.printf("Press Ctrl+D to exit.%n");
        con.flush();

        RAst regex = readRegex(con);
        if (regex == null) return;

        String line;
        while ((line = con.readLine("INPUT? ")) != null) {
            Match m = BacktrackingMatcher.match(line, regex);
            if (m.hasMatch) {
                con.printf("MATCH %s at position %d%n", m.matched(), m.start);
            }
            else {
                con.printf("NO MATCH FOUND%n");
            }
            con.flush();
        }
    }

    private static RAst readRegex(Console con) {
        RAst regex = null;

        do {
            String regexString = con.readLine("REGEX? ");
            if (regexString == null) return null;

            try {
                regex = RParser.parse(regexString);
            }
            catch (RParseException e) {
                // Error pointer must be aligned with the input
                //         'REGEX? '
                con.printf("     | %s%n", getErrorPointer(e.column));
                con.printf("ERROR: %s%n", e.getMessage());
            }
        } while (regex == null);

        return regex;
    }

    private static String getErrorPointer(int column) {
        StringBuilder pointer = new StringBuilder();

        for (int i = 0; i < column; i++) {
            pointer.append('-');
        }
        pointer.append('^');

        return pointer.toString();
    }
}
