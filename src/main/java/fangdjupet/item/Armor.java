package fangdjupet.item;

import fangdjupet.contract.Equippable;
import fangdjupet.contract.ItemType;

public class Armor extends AbstractItem implements Equippable {
    private final int defenseBonus;

    public Armor(String name, int level, int defenseBonus) {
        super(name, level);
        this.defenseBonus = requireNotNegative(defenseBonus, "defenseBonus");
    }

    @Override
    public ItemType getType() {
        return ItemType.ARMOR;
    }

    @Override
    public int getAttackBonus() {
        return 0;
    }

    @Override
    public int getDefenseBonus() {
        return defenseBonus;
    }
}
