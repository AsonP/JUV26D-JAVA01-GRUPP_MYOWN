package fangdjupet.item;

import fangdjupet.contract.Item;

import java.util.Objects;

/**
 * Gemensam grund för alla föremål: namn och nivå.
 */
public abstract class AbstractItem implements Item {
    private final String name;
    private final int level;

    protected AbstractItem(String name, int level) {
        if (level < 1) {
            throw new IllegalArgumentException("level måste vara minst 1");
        }
        this.name = Objects.requireNonNull(name, "name får inte vara null");
        this.level = level;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return name + " (nivå " + level + ")";
    }

    protected static int requireNotNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " får inte vara negativt");
        }
        return value;
    }
}
