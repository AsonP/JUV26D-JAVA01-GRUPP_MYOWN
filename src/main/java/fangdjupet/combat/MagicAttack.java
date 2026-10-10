package fangdjupet.combat;

import fangdjupet.contract.AttackStrategy;
import fangdjupet.contract.Combatant;

/**
 * Magi: går rakt igenom rustningen men är svagare.
 * Gör tre fjärdedelar av anfallet i skada, oavsett försvar.
 */
public class MagicAttack implements AttackStrategy {

    @Override
    public String getName() {
        return "Magi";
    }

    @Override
    public int calculateDamage(Combatant attacker, Combatant defender) {
        return DamageRules.atLeastMinimum(attacker.getAttack() * 3 / 4);
    }
}
