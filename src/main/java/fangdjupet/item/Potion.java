package fangdjupet.item;

import fangdjupet.contract.ItemType;

/**
 * En dryck som återställer hälsa. Dryckens jobb är bara att veta hur mycket;
 * själva läkningen görs av den som dricker den.
 */
public class Potion extends AbstractItem {
    private final int healAmount;

    public Potion(String name, int level, int healAmount) {
        super(name, level);
        this.healAmount = requireNotNegative(healAmount, "healAmount");
    }

    @Override
    public ItemType getType() {
        return ItemType.POTION;
    }

    public int getHealAmount() {
        return healAmount;
    }
}
