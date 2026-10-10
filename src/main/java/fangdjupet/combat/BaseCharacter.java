package fangdjupet.combat;

import fangdjupet.contract.AttackStrategy;
import fangdjupet.contract.Combatant;

import java.util.Objects;

/**
 * Gemensam grund för hjältar och fiender, så att hälsa och anfall
 * bara behöver skrivas en gång.
 */
public abstract class BaseCharacter implements Combatant {
    private final String name;
    private final int maxHealth;
    private final int baseAttack;
    private final int baseDefense;
    private int health;
    private AttackStrategy attackStrategy;

    protected BaseCharacter(String name, int maxHealth, int baseAttack, int baseDefense,
                            AttackStrategy attackStrategy) {
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("maxHealth måste vara större än 0");
        }
        this.name = Objects.requireNonNull(name, "name får inte vara null");
        this.maxHealth = maxHealth;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.health = maxHealth;
        this.attackStrategy = Objects.requireNonNull(attackStrategy, "attackStrategy får inte vara null");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public int getAttack() {
        return baseAttack;
    }

    @Override
    public int getDefense() {
        return baseDefense;
    }

    /** Anfall utan bonusar från utrustning. */
    public int getBaseAttack() {
        return baseAttack;
    }

    /** Försvar utan bonusar från utrustning. */
    public int getBaseDefense() {
        return baseDefense;
    }

    @Override
    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount får inte vara negativt");
        }
        health = Math.max(0, health - amount);
    }

    /** Ökar hälsan, dock aldrig över maxHealth. */
    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount får inte vara negativt");
        }
        health = Math.min(maxHealth, health + amount);
    }

    /** Anfaller målet med nuvarande strategi och returnerar skadan som gjordes. */
    @Override
    public int attack(Combatant target) {
        int damage = attackStrategy.calculateDamage(this, target);
        target.takeDamage(damage);
        return damage;
    }

    public AttackStrategy getAttackStrategy() {
        return attackStrategy;
    }

    public void setAttackStrategy(AttackStrategy attackStrategy) {
        this.attackStrategy = Objects.requireNonNull(attackStrategy, "attackStrategy får inte vara null");
    }
}
