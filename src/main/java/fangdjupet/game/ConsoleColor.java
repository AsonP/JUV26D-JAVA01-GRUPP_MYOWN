package fangdjupet.game;

/**
 * ANSI-färgkoder (American National Standards Institute) som de flesta terminaler förstår.
 */
public enum ConsoleColor {
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    CYAN("\u001B[36m"),
    BOLD("\u001B[1m");

    static final String RESET = "\u001B[0m";

    private final String code;

    ConsoleColor(String code) {
        this.code = code;
    }

    String code() {
        return code;
    }
}
