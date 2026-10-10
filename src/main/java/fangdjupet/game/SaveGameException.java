package fangdjupet.game;

/**
 * Kastas när ett äventyr inte kan sparas eller läsas in.
 */
public class SaveGameException extends Exception {
    public SaveGameException(String message) {
        super(message);
    }

    public SaveGameException(String message, Throwable cause) {
        super(message, cause);
    }
}
