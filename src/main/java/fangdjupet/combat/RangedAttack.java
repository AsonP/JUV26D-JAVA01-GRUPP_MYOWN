package fangdjupet.combat;

import fangdjupet.contract.AttackStrategy;
import fangdjupet.contract.Combatant;

/**
 * Avståndsanfall: pilar tränger delvis igenom rustningen.
 * Hela anfallet minus halva försvaret.
 */
public class RangedAttack implements AttackStrategy {

    @Override
    public String getName() {
        return "Avståndsanfall";
    }

    @Override
    public int calculateDamage(Combatant attacker, Combatant defender) {
        return DamageRules.atLeastMinimum(attacker.getAttack() - defender.getDefense() / 2);
    }
}
