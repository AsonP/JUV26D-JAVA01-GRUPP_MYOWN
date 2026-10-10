package fangdjupet.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MagicAttackTest {
    private final MagicAttack magic = new MagicAttack();
    private final MeleeAttack melee = new MeleeAttack();

    @Test
    void magicIgnoresDefense() {
        Hero hero = new Hero("Hjälten", 30, 12, 2, magic);
        Enemy golem = new Enemy("Golemen", 1, 20, 5, 10, melee);

        assertEquals(9, magic.calculateDamage(hero, golem));
    }

    @Test
    void damageIsRoundedDown() {
        Hero hero = new Hero("Hjälten", 30, 10, 2, magic);
        Enemy rat = new Enemy("Råttan", 1, 10, 1, 0, melee);

        assertEquals(7, magic.calculateDamage(hero, rat));
    }

    @Test
    void damageIsAtLeastMinimum() {
        Hero hero = new Hero("Hjälten", 30, 1, 2, magic);
        Enemy rat = new Enemy("Råttan", 1, 10, 1, 0, melee);

        assertEquals(DamageRules.MINIMUM_DAMAGE, magic.calculateDamage(hero, rat));
    }
}
