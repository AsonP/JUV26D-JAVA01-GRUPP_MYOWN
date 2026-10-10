package fangdjupet.game;

import fangdjupet.combat.Battle;
import fangdjupet.combat.Hero;
import fangdjupet.contract.Combatant;
import fangdjupet.contract.GameEvent;
import fangdjupet.contract.GameEventListener;
import fangdjupet.contract.GameEventType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Håller ordning på en pågående runda: hjälten och kön av fiender.
 * Är också "subject" i Observer-mönstret: lyssnare får veta när något händer.
 */
public class Game {
    private final List<GameEventListener> listeners = new ArrayList<>();
    private final Deque<Combatant> enemies = new ArrayDeque<>();
    private final Battle battle = new Battle(this::publish);
    private Hero hero;

    public void addListener(GameEventListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener får inte vara null"));
    }

    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    public void publish(GameEvent event) {
        for (GameEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public void startNewAdventure(Hero hero, List<? extends Combatant> enemies) {
        this.hero = Objects.requireNonNull(hero, "hero får inte vara null");
        this.enemies.clear();
        this.enemies.addAll(Objects.requireNonNull(enemies, "enemies får inte vara null"));
    }

    public boolean hasActiveAdventure() {
        return hero != null;
    }

    public Optional<Hero> getHero() {
        return Optional.ofNullable(hero);
    }

    public Optional<Combatant> getCurrentEnemy() {
        return Optional.ofNullable(enemies.peekFirst());
    }

    public int getRemainingEnemyCount() {
        return enemies.size();
    }

    /** Kör en stridsrunda mot nuvarande fiende. */
    public void fightRound() {
        if (!hasActiveAdventure() || isOver()) {
            throw new IllegalStateException("Inget pågående äventyr att strida i");
        }
        battle.fightRound(hero, enemies.peekFirst());
        removeDefeatedEnemy();
    }

    /** Tar bort nuvarande fiende ur kön om den är besegrad. */
    public void removeDefeatedEnemy() {
        Combatant enemy = enemies.peekFirst();
        if (enemy != null && !enemy.isAlive()) {
            enemies.removeFirst();
            publish(new GameEvent(GameEventType.ENEMY_DEFEATED,
                    hero.getName() + " besegrade " + enemy.getName()));
        }
    }

    public boolean isOver() {
        return hasActiveAdventure() && (!hero.isAlive() || enemies.isEmpty());
    }
}
