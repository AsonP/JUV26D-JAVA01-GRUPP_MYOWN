package fangdjupet.game;

import fangdjupet.combat.Hero;
import fangdjupet.contract.AttackStrategy;
import fangdjupet.contract.Combatant;
import fangdjupet.contract.Equippable;
import fangdjupet.contract.Item;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Huvudmenyn. Ansvarar bara för att visa valen och läsa in användarens val.
 * Själva spellogiken ligger i Game, som menyn anropar.
 */
public class Menu {
    private final Scanner scanner;
    private final Game game;
    private final SaveManager saveManager;
    private final Path saveFile;
    private boolean running;

    public Menu(Scanner scanner, Game game, SaveManager saveManager, Path saveFile) {
        this.scanner = scanner;
        this.game = game;
        this.saveManager = saveManager;
        this.saveFile = saveFile;
    }

    public void run() {
        running = true;
        while (running) {
            printMenu();
            if (!scanner.hasNextLine()) {
                break;
            }
            handleChoice(scanner.nextLine().trim());
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println(ConsoleStyle.heading("Fångdjupet"));
        System.out.println("1. Starta nytt äventyr");
        System.out.println("2. Fortsätt sparat äventyr");
        System.out.println("3. Visa hjältens utrustning");
        System.out.println("4. Strid mot nästa fiende");
        System.out.println("5. Drick läkedryck");
        System.out.println("0. Avsluta");
        System.out.print("Välj: ");
    }

    private void handleChoice(String choice) {
        switch (choice) {
            case "1" -> startNewAdventure();
            case "2" -> continueSavedAdventure();
            case "3" -> showEquipment();
            case "4" -> fightNextEnemy();
            case "5" -> drinkPotion();
            case "0" -> quit();
            default -> System.out.println("Ogiltigt val, försök igen.");
        }
    }

    private void startNewAdventure() {
        System.out.print("Vad heter din hjälte? ");
        String name = readLine();
        AttackStrategy attackStyle = chooseAttackStyle();
        game.startNewAdventure(AdventureSetup.createHero(name, attackStyle), AdventureSetup.createEnemies());

        Combatant hero = game.getHero().orElseThrow();
        System.out.println(hero.getName() + " kliver ner i Fångdjupet med " + attackStyle.getName().toLowerCase()
                + ". " + game.getRemainingEnemyCount() + " fiender väntar.");
        autoSave();
    }

    private AttackStrategy chooseAttackStyle() {
        List<AttackStrategy> styles = AdventureSetup.availableAttackStyles();
        System.out.println("Välj anfallsstil:");
        for (int i = 0; i < styles.size(); i++) {
            System.out.println((i + 1) + ". " + styles.get(i).getName());
        }
        System.out.print("Välj: ");

        try {
            int choice = Integer.parseInt(readLine().trim());
            if (choice >= 1 && choice <= styles.size()) {
                return styles.get(choice - 1);
            }
        } catch (NumberFormatException e) {
            // Ogiltig inmatning hanteras nedan
        }
        System.out.println("Ogiltigt val, du får " + styles.get(0).getName().toLowerCase() + ".");
        return styles.get(0);
    }

    private String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : "";
    }

    private void continueSavedAdventure() {
        try {
            game.restore(saveManager.load(saveFile));
        } catch (SaveGameException e) {
            System.out.println(e.getMessage() + ".");
            return;
        }
        printHeroStatus();
    }

    private void showEquipment() {
        if (!requireActiveAdventure()) {
            return;
        }
        Hero hero = game.getHero().orElseThrow();

        System.out.println();
        System.out.println(ConsoleStyle.heading(hero.getName()));
        System.out.println("Hälsa: " + ConsoleStyle.health(hero));
        System.out.println("Anfall: " + hero.getAttack() + "   Försvar: " + hero.getDefense());
        System.out.println("Anfallsstil: " + hero.getAttackStrategy().getName());
        System.out.println("Utrustning:");

        List<Equippable> equipment = hero.getEquipment();
        if (equipment.isEmpty()) {
            System.out.println("  (ingen utrustning ännu)");
        }
        for (Equippable item : equipment) {
            System.out.println("  " + item + "  anfall +" + item.getAttackBonus()
                    + ", försvar +" + item.getDefenseBonus());
        }

        System.out.println("Ryggsäck:");
        List<Item> backpack = game.getInventory().getItems();
        if (backpack.isEmpty()) {
            System.out.println("  (tom)");
        }
        for (Item item : backpack) {
            System.out.println("  " + item);
        }
    }

    private void drinkPotion() {
        if (!requireActiveAdventure()) {
            return;
        }
        if (game.drinkPotion().isEmpty()) {
            System.out.println("Du har ingen läkedryck att dricka.");
            return;
        }
        autoSave();
    }

    private void fightNextEnemy() {
        if (!requireActiveAdventure()) {
            return;
        }
        if (game.isOver()) {
            System.out.println("Äventyret är slut. Starta ett nytt för att spela igen.");
            return;
        }

        Combatant enemy = game.getCurrentEnemy().orElseThrow();
        System.out.println("Du möter " + enemy.getName() + "!");
        game.fightRound();
        printOutcome();
        autoSave();
    }

    private void printOutcome() {
        Combatant hero = game.getHero().orElseThrow();
        if (!hero.isAlive()) {
            System.out.println(ConsoleStyle.color("Du förlorade. Fångdjupet tog ännu en hjälte.", ConsoleColor.RED));
        } else if (game.isOver()) {
            System.out.println(ConsoleStyle.color("Seger! " + hero.getName() + " har besegrat alla fiender.",
                    ConsoleColor.GREEN));
        } else {
            printHeroStatus();
        }
    }

    private void printHeroStatus() {
        Hero hero = game.getHero().orElseThrow();
        System.out.println(hero.getName() + " " + ConsoleStyle.health(hero)
                + "   Fiender kvar: " + game.getRemainingEnemyCount());
    }

    private void quit() {
        if (game.hasActiveAdventure() && !game.isOver()) {
            autoSave();
            System.out.println("Ditt äventyr är sparat. Välj 2 nästa gång för att fortsätta.");
        }
        System.out.println("Tack för att du spelade Fångdjupet!");
        running = false;
    }

    /** Sparar ett pågående äventyr, eller tar bort sparfilen om äventyret är slut. */
    private void autoSave() {
        try {
            if (game.isOver()) {
                saveManager.delete(saveFile);
            } else {
                saveManager.save(game, saveFile);
            }
        } catch (SaveGameException e) {
            System.out.println("Varning: " + e.getMessage());
        }
    }

    /** Skriver ut ett meddelande och returnerar false om inget äventyr pågår. */
    private boolean requireActiveAdventure() {
        if (!game.hasActiveAdventure()) {
            System.out.println("Inget äventyr pågår. Starta ett nytt eller fortsätt ett sparat.");
            return false;
        }
        return true;
    }
}
