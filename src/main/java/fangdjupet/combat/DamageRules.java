package fangdjupet.combat;

/**
 * Gemensamma regler för skadeberäkning, så att de inte upprepas i varje strategi.
 */
final class DamageRules {
    static final int MINIMUM_DAMAGE = 1;

    private DamageRules() {
    }

    /** Ser till att ett anfall alltid gör minst MINIMUM_DAMAGE i skada. */
    static int atLeastMinimum(int damage) {
        return Math.max(MINIMUM_DAMAGE, damage);
    }
}