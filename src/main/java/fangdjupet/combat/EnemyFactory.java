package fangdjupet.combat;

import java.util.Objects;

/**
 * Factory-mönstret: skapar fiender utifrån typ och nivå.
 * Varje nivå över 1 ger mer hälsa, anfall och försvar.
 */
public final class EnemyFactory {
    static final int HEALTH_PER_LEVEL = 5;
    static final int ATTACK_PER_LEVEL = 2;
    static final int DEFENSE_PER_LEVEL = 1;

    private EnemyFactory() {
    }

    public static Enemy create(EnemyType type, int level) {
        Objects.requireNonNull(type, "type får inte vara null");
        if (level < 1) {
            throw new IllegalArgumentException("level måste vara minst 1");
        }
        int extraLevels = level - 1;
        return new Enemy(
                type.getDisplayName(),
                level,
                type.getBaseHealth() + HEALTH_PER_LEVEL * extraLevels,
                type.getBaseAttack() + ATTACK_PER_LEVEL * extraLevels,
                type.getBaseDefense() + DEFENSE_PER_LEVEL * extraLevels,
                new MeleeAttack()
        );
    }
}
