package fangdjupet.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsoleStyleTest {

    @Test
    void highHealthIsGreen() {
        assertEquals(ConsoleColor.GREEN, ConsoleStyle.healthColor(40, 40));
        assertEquals(ConsoleColor.GREEN, ConsoleStyle.healthColor(25, 40));
    }

    @Test
    void mediumHealthIsYellow() {
        assertEquals(ConsoleColor.YELLOW, ConsoleStyle.healthColor(24, 40));
        assertEquals(ConsoleColor.YELLOW, ConsoleStyle.healthColor(13, 40));
    }

    @Test
    void lowHealthIsRed() {
        assertEquals(ConsoleColor.RED, ConsoleStyle.healthColor(12, 40));
        assertEquals(ConsoleColor.RED, ConsoleStyle.healthColor(0, 40));
    }

    @Test
    void fullHealthGivesFullBar() {
        assertEquals("[██████████]", ConsoleStyle.healthBar(40, 40));
    }

    @Test
    void zeroHealthGivesEmptyBar() {
        assertEquals("[░░░░░░░░░░]", ConsoleStyle.healthBar(0, 40));
    }

    @Test
    void anyHealthLeftShowsAtLeastOneBlock() {
        assertEquals("[█░░░░░░░░░]", ConsoleStyle.healthBar(1, 40));
    }
}
