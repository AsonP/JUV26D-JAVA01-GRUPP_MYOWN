package fangdjupet.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RangedAttackTest {
    private final RangedAttack ranged = new RangedAttack();
    private final MeleeAttack melee = new MeleeAttack();

    @Test
    void onlyHalfOfDefenseCounts() {
        Hero hero = new Hero("Hjälten", 30, 10, 2, ranged);
        Enemy troll = new Enemy("Trollet", 1, 20, 5, 6, melee);

        assertEquals(7, ranged.calculateDamage(hero, troll));
    }

    @Test
    void halfDefenseIsRoundedDown() {
        Hero hero = new Hero("Hjälten", 30, 10, 2, ranged);
        Enemy goblin = new Enemy("Goblinen", 1, 20, 5, 5, melee);

        assertEquals(8, ranged.calculateDamage(hero, goblin));
    }

    @Test
    void damageIsAtLeastMinimum() {
        Hero hero = new Hero("Hjälten", 30, 2, 2, ranged);
        Enemy golem = new Enemy("Golemen", 1, 20, 5, 10, melee);

        assertEquals(DamageRules.MINIMUM_DAMAGE, ranged.calculateDamage(hero, golem));
    }
}
