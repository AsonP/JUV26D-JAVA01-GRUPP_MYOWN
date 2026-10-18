package fangdjupet.game;

import fangdjupet.contract.Combatant;
import fangdjupet.contract.GameEventType;

/**
 * Samlar allt som har med färg och utseende i konsolen att göra, så att det finns på ett ställe.
 * Färgerna stängs av om miljövariabeln NO_COLOR är satt, enligt den vanliga konventionen.
 */
public final class ConsoleStyle {
    static final int BAR_WIDTH = 10;
    private static final boolean COLORS_ENABLED = System.getenv("NO_COLOR") == null;

    private ConsoleStyle() {
    }

    public static String color(String text, ConsoleColor color) {
        return COLORS_ENABLED ? color.code() + text + ConsoleColor.RESET : text;
    }

    /** Färgad hälsostapel med siffror, t.ex. [███████░░░] 28/40. */
    public static String health(Combatant combatant) {
        int health = combatant.getHealth();
        int maxHealth = combatant.getMaxHealth();
        String text = healthBar(health, maxHealth) + " " + health + "/" + maxHealth;
        return color(text, healthColor(health, maxHealth));
    }

    /** Grönt över 60 procent, gult över 30 procent, annars rött. */
    static ConsoleColor healthColor(int health, int maxHealth) {
        int percent = health * 100 / maxHealth;
        if (percent > 60) {
            return ConsoleColor.GREEN;
        }
        if (percent > 30) {
            return ConsoleColor.YELLOW;
        }
        return ConsoleColor.RED;
    }

    /** Stapel utan färg. Avrundas uppåt, så att en levande karaktär alltid har minst ett fyllt fält. */
    static String healthBar(int health, int maxHealth) {
        int filled = (health * BAR_WIDTH + maxHealth - 1) / maxHealth;
        return "[" + "█".repeat(filled) + "░".repeat(BAR_WIDTH - filled) + "]";
    }

    static ConsoleColor eventColor(GameEventType type) {
        return switch (type) {
            case ENEMY_DEFEATED, ITEM_USED -> ConsoleColor.GREEN;
            case ITEM_FOUND -> ConsoleColor.YELLOW;
            case HERO_DEFEATED -> ConsoleColor.RED;
            case BATTLE_STARTED, ATTACK, GAME_SAVED, GAME_LOADED -> ConsoleColor.CYAN;
        };
    }

    public static String heading(String text) {
        return color("=== " + text + " ===", ConsoleColor.BOLD);
    }
}
