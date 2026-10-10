package fangdjupet.game;

import fangdjupet.contract.AttackStrategy;
import fangdjupet.contract.Combatant;

import java.util.List;
import java.util.Scanner;

/**
 * Huvudmenyn. Ansvarar bara för att visa valen och läsa in användarens val.
 * Själva spellogiken ligger i Game, som menyn anropar.
 */
public class Menu {
    private final Scanner scanner;
    private final Game game;
    private boolean running;

    public Menu(Scanner scanner, Game game) {
        this.scanner = scanner;
        this.game = game;
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
        System.out.println("=== Fångdjupet ===");
        System.out.println("1. Starta nytt äventyr");
        System.out.println("2. Fortsätt sparat äventyr");
        System.out.println("3. Visa hjältens utrustning");
        System.out.println("4. Strid mot nästa fiende");
        System.out.println("0. Avsluta");
        System.out.print("Välj: ");
    }

    private void handleChoice(String choice) {
        switch (choice) {
            case "1" -> startNewAdventure();
            case "2" -> continueSavedAdventure();
            case "3" -> showEquipment();
            case "4" -> fightNextEnemy();
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
        notImplemented("Fortsätt sparat äventyr");
    }

    private void showEquipment() {
        if (requireActiveAdventure()) {
            notImplemented("Visa hjältens utrustning");
        }
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
    }

    private void printOutcome() {
        Combatant hero = game.getHero().orElseThrow();
        if (!hero.isAlive()) {
            System.out.println("Du förlorade. Fångdjupet tog ännu en hjälte.");
        } else if (game.isOver()) {
            System.out.println("Seger! " + hero.getName() + " har besegrat alla fiender.");
        } else {
            System.out.println(hero.getName() + " har " + hero.getHealth() + "/" + hero.getMaxHealth()
                    + " i hälsa. Fiender kvar: " + game.getRemainingEnemyCount());
        }
    }

    private void quit() {
        System.out.println("Tack för att du spelade Fångdjupet!");
        running = false;
    }

    /** Skriver ut ett meddelande och returnerar false om inget äventyr pågår. */
    private boolean requireActiveAdventure() {
        if (!game.hasActiveAdventure()) {
            System.out.println("Inget äventyr pågår. Starta ett nytt eller fortsätt ett sparat.");
            return false;
        }
        return true;
    }

    private void notImplemented(String feature) {
        System.out.println(feature + " är inte implementerat ännu.");
    }
}
