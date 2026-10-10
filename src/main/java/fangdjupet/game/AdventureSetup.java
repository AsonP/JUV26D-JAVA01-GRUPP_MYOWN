package fangdjupet.game;

import fangdjupet.combat.Enemy;
import fangdjupet.combat.EnemyFactory;
import fangdjupet.combat.EnemyType;
import fangdjupet.combat.Hero;
import fangdjupet.combat.MagicAttack;
import fangdjupet.combat.MeleeAttack;
import fangdjupet.combat.RangedAttack;
import fangdjupet.contract.AttackStrategy;

import java.util.List;

/**
 * Skapar hjälte och fiender för ett nytt äventyr.
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
        return List.of(
                EnemyFactory.create(EnemyType.RAT, 1),
                EnemyFactory.create(EnemyType.GOBLIN, 2),
                EnemyFactory.create(EnemyType.TROLL, 3)
        );
    }
}
