package fangdjupet.combat;

import fangdjupet.contract.AttackStrategy;
import fangdjupet.contract.Combatant;

/**
 * Närstrid: hela anfallet minus hela försvaret.
 */
public class MeleeAttack implements AttackStrategy {

    @Override
    public String getName() {
        return "Närstrid";
    }

    @Override
    public int calculateDamage(Combatant attacker, Combatant defender) {
        return DamageRules.atLeastMinimum(attacker.getAttack() - defender.getDefense());
    }
}
