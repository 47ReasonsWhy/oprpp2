package hr.fer.oprpp2.util;

import java.util.List;

/**
 * Generates random font color.
 */
public class RandomFontColorChooser {
    private static final List<String> colors = List.of(
        "red", "green", "blue", "yellow", "cyan", "magenta", "orange", "pink", "gray"
    );

    public static String choose() {
        return colors.get((int) (Math.random() * colors.size()));
    }
}
