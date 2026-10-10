package fangdjupet;

import fangdjupet.game.EventLog;
import fangdjupet.game.Game;
import fangdjupet.game.Menu;
import fangdjupet.game.SaveManager;

import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    private static final Path SAVE_FILE = Path.of("fangdjupet.save");

    public static void main(String[] args) {
        Game game = new Game();
        game.addListener(new EventLog());

        try (Scanner scanner = new Scanner(System.in)) {
            Menu menu = new Menu(scanner, game, new SaveManager(), SAVE_FILE);
            menu.run();
        }
    }
}
