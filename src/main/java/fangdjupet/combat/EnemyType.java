package fangdjupet.combat;

/**
 * Fiendetyper med grundvärden på nivå 1 och hur mycket de växer per nivå.
 */
public enum EnemyType {
    RAT("Råttan", 10, 4, 0),
    GOBLIN("Goblinen", 13, 4, 1),
    TROLL("Trollet", 14, 5, 2);

    private final String displayName;
    private final int baseHealth;
    private final int baseAttack;
    private final int baseDefense;

    EnemyType(String displayName, int baseHealth, int baseAttack, int baseDefense) {
        this.displayName = displayName;
        this.baseHealth = baseHealth;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
    }

    public String getDisplayName() {
        return displayName;
    }

    int getBaseHealth() {
        return baseHealth;
    }

    int getBaseAttack() {
        return baseAttack;
    }

    int getBaseDefense() {
        return baseDefense;
    }
}
