package fangdjupet.item;

import fangdjupet.contract.Equippable;
import fangdjupet.contract.ItemType;

public class Weapon extends AbstractItem implements Equippable {
    private final int attackBonus;

    public Weapon(String name, int level, int attackBonus) {
        super(name, level);
        this.attackBonus = requireNotNegative(attackBonus, "attackBonus");
    }

    @Override
    public ItemType getType() {
        return ItemType.WEAPON;
    }

    @Override
    public int getAttackBonus() {
        return attackBonus;
    }

    @Override
    public int getDefenseBonus() {
        return 0;
    }
}
