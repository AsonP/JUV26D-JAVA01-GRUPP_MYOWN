package fangdjupet.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnemyFactoryTest {

    @Test
    void levelOneUsesBaseValues() {
        Enemy rat = EnemyFactory.create(EnemyType.RAT, 1);

        assertEquals("Råttan", rat.getName());
        assertEquals(1, rat.getLevel());
        assertEquals(10, rat.getMaxHealth());
        assertEquals(4, rat.getAttack());
        assertEquals(0, rat.getDefense());
    }

    @Test
    void higherLevelGivesStrongerEnemy() {
        Enemy troll = EnemyFactory.create(EnemyType.TROLL, 3);

        assertEquals(14 + 2 * EnemyFactory.HEALTH_PER_LEVEL, troll.getMaxHealth());
        assertEquals(5 + 2 * EnemyFactory.ATTACK_PER_LEVEL, troll.getAttack());
        assertEquals(2 + 2 * EnemyFactory.DEFENSE_PER_LEVEL, troll.getDefense());
    }

    @Test
    void newEnemyHasFullHealth() {
        Enemy goblin = EnemyFactory.create(EnemyType.GOBLIN, 2);

        assertEquals(goblin.getMaxHealth(), goblin.getHealth());
    }

    @Test
    void levelBelowOneThrows() {
        assertThrows(IllegalArgumentException.class, () -> EnemyFactory.create(EnemyType.RAT, 0));
    }
}
