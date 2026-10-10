package fangdjupet.game;

import fangdjupet.combat.Enemy;
import fangdjupet.combat.Hero;
import fangdjupet.combat.MagicAttack;
import fangdjupet.combat.MeleeAttack;
import fangdjupet.combat.RangedAttack;
import fangdjupet.contract.AttackStrategy;

import java.util.List;

/**
 * Skapar hjälte och fiender för ett nytt äventyr.
 * Tillfällig lösning: fienderna ska senare skapas av en EnemyFactory i föremålsmodulen.
 */
public final class AdventureSetup {
    private static final String DEFAULT_HERO_NAME = "Hjälten";
    private static final List<AttackStrategy> ATTACK_STYLES =
            List.of(new MeleeAttack(), new RangedAttack(), new MagicAttack());

    private AdventureSetup() {
    }

    /** Anfallsstilarna spelaren kan välja mellan. Strategierna saknar tillstånd och kan delas. */
    public static List<AttackStrategy> availableAttackStyles() {
        return ATTACK_STYLES;
    }

    public static Hero createHero(String name, AttackStrategy attackStyle) {
        String heroName = (name == null || name.isBlank()) ? DEFAULT_HERO_NAME : name.trim();
        return new Hero(heroName, 40, 8, 3, attackStyle);
    }

    public static List<Enemy> createEnemies() {
        MeleeAttack melee = new MeleeAttack();
        return List.of(
                new Enemy("Råttan", 1, 10, 4, 0, melee),
                new Enemy("Goblinen", 2, 18, 6, 2, melee),
                new Enemy("Trollet", 3, 24, 9, 4, melee)
        );
    }
}
