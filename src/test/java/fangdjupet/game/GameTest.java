package fangdjupet.game;

import fangdjupet.combat.Enemy;
import fangdjupet.combat.Hero;
import fangdjupet.combat.MeleeAttack;
import fangdjupet.contract.GameEvent;
import fangdjupet.contract.GameEventType;
import fangdjupet.item.ItemFactory;
import fangdjupet.item.Potion;
import fangdjupet.item.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {
    private final MeleeAttack melee = new MeleeAttack();
    private Game game;
    private List<GameEvent> events;

    /** En Random som alltid väljer första föremålstypen, dvs. vapen. */
    private static final Random ALWAYS_WEAPON = new Random() {
        @Override
        public int nextInt(int bound) {
            return 0;
        }
    };

    @BeforeEach
    void setUp() {
        game = new Game(ALWAYS_WEAPON);
        events = new ArrayList<>();
        game.addListener(events::add);
    }

    private Hero strongHero() {
        return new Hero("Hjälten", 30, 100, 0, melee);
    }

    private Enemy weakEnemy(String name, int level) {
        return new Enemy(name, level, 5, 1, 0, melee);
    }

    private Enemy weakEnemy(String name) {
        return weakEnemy(name, 1);
    }

    @Test
    void noAdventureIsActiveFromStart() {
        assertFalse(game.hasActiveAdventure());
        assertFalse(game.isOver());
        assertTrue(game.getHero().isEmpty());
    }

    @Test
    void startNewAdventureSetsHeroAndEnemies() {
        game.startNewAdventure(strongHero(), List.of(weakEnemy("Råttan"), weakEnemy("Fladdermusen")));

        assertTrue(game.hasActiveAdventure());
        assertEquals(2, game.getRemainingEnemyCount());
        assertEquals("Råttan", game.getCurrentEnemy().orElseThrow().getName());
    }

    @Test
    void defeatedEnemyIsRemovedAndEventIsPublished() {
        game.startNewAdventure(strongHero(), List.of(weakEnemy("Råttan"), weakEnemy("Fladdermusen")));

        game.fightRound();

        assertEquals(1, game.getRemainingEnemyCount());
        assertEquals("Fladdermusen", game.getCurrentEnemy().orElseThrow().getName());
        assertTrue(events.stream().anyMatch(e -> e.type() == GameEventType.ENEMY_DEFEATED));
    }

    @Test
    void gameIsOverWhenAllEnemiesAreDefeated() {
        game.startNewAdventure(strongHero(), List.of(weakEnemy("Råttan")));

        game.fightRound();

        assertTrue(game.isOver());
    }

    @Test
    void fightRoundWithoutAdventureThrows() {
        assertThrows(IllegalStateException.class, () -> game.fightRound());
    }

    @Test
    void defeatedEnemyLeavesLoot() {
        Hero hero = strongHero();
        game.startNewAdventure(hero, List.of(weakEnemy("Råttan")));

        game.fightRound();

        assertEquals(1, hero.getEquipment().size());
        assertTrue(events.stream().anyMatch(e -> e.type() == GameEventType.ITEM_FOUND));
    }

    @Test
    void betterWeaponReplacesWorseAndOldGoesToBackpack() {
        Hero hero = strongHero();
        Weapon oldWeapon = ItemFactory.createWeapon(1);
        hero.equip(oldWeapon);
        game.startNewAdventure(hero, List.of(weakEnemy("Trollet", 3)));

        game.fightRound();

        assertEquals(1, hero.getEquipment().size());
        assertEquals(3, hero.getEquipment().get(0).getLevel());
        assertEquals(List.of(oldWeapon), game.getInventory().getItems());
    }

    @Test
    void drinkingPotionHealsHeroAndRemovesPotion() {
        Hero hero = strongHero();
        game.startNewAdventure(hero, List.of(weakEnemy("Råttan")));
        hero.takeDamage(15);
        game.getInventory().add(new Potion("Läkedryck", 1, 10));

        assertTrue(game.drinkPotion().isPresent());

        assertEquals(25, hero.getHealth());
        assertTrue(game.getInventory().isEmpty());
    }

    @Test
    void drinkingWithoutPotionDoesNothing() {
        game.startNewAdventure(strongHero(), List.of(weakEnemy("Råttan")));

        assertTrue(game.drinkPotion().isEmpty());
    }
}
