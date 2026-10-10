package fangdjupet.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeleeAttackTest {
    private final MeleeAttack melee = new MeleeAttack();

    @Test
    void damageIsAttackMinusDefense() {
        Hero hero = new Hero("Hjälten", 30, 10, 2, melee);
        Enemy troll = new Enemy("Trollet", 1, 20, 5, 3, melee);

        assertEquals(7, melee.calculateDamage(hero, troll));
    }

    @Test
    void damageIsAtLeastMinimumWhenDefenseIsHigher() {
        Hero hero = new Hero("Hjälten", 30, 2, 2, melee);
        Enemy golem = new Enemy("Golemen", 1, 20, 5, 10, melee);

        assertEquals(DamageRules.MINIMUM_DAMAGE, melee.calculateDamage(hero, golem));
    }

    @Test
    void attackReducesTargetHealth() {
        Hero hero = new Hero("Hjälten", 30, 10, 2, melee);
        Enemy troll = new Enemy("Trollet", 1, 20, 5, 3, melee);

        int damage = hero.attack(troll);

        assertEquals(20 - damage, troll.getHealth());
    }

    @Test
    void healthNeverGoesBelowZero() {
        Hero hero = new Hero("Hjälten", 30, 50, 2, melee);
        Enemy rat = new Enemy("Råttan", 1, 5, 1, 0, melee);

        hero.attack(rat);

        assertEquals(0, rat.getHealth());
    }
}
