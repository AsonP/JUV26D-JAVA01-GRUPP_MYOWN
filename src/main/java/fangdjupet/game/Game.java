package fangdjupet.game;

import fangdjupet.combat.Battle;
import fangdjupet.combat.Enemy;
import fangdjupet.combat.Hero;
import fangdjupet.contract.Equippable;
import fangdjupet.contract.GameEvent;
import fangdjupet.contract.GameEventListener;
import fangdjupet.contract.GameEventType;
import fangdjupet.contract.Item;
import fangdjupet.contract.ItemType;
import fangdjupet.item.Inventory;
import fangdjupet.item.ItemFactory;
import fangdjupet.item.Potion;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * Håller ordning på en pågående runda: hjälten, kön av fiender och ryggsäcken.
 * Är också "subject" i Observer-mönstret: lyssnare får veta när något händer.
 */
public class Game {
    private final List<GameEventListener> listeners = new ArrayList<>();
    private final Deque<Enemy> enemies = new ArrayDeque<>();
    private final Battle battle = new Battle(this::publish);
    private final Random random;
    private Inventory<Item> inventory = new Inventory<>();
    private Hero hero;

    public Game() {
        this(new Random());
    }

    /** Med en egen Random kan testerna styra vilka föremål som hittas. */
    public Game(Random random) {
        this.random = Objects.requireNonNull(random, "random får inte vara null");
    }

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

    public void startNewAdventure(Hero hero, List<? extends Enemy> enemies) {
        this.hero = Objects.requireNonNull(hero, "hero får inte vara null");
        this.enemies.clear();
        this.enemies.addAll(Objects.requireNonNull(enemies, "enemies får inte vara null"));
        this.inventory = new Inventory<>();
    }

    public boolean hasActiveAdventure() {
        return hero != null;
    }

    public Optional<Hero> getHero() {
        return Optional.ofNullable(hero);
    }

    public Optional<Enemy> getCurrentEnemy() {
        return Optional.ofNullable(enemies.peekFirst());
    }

    public int getRemainingEnemyCount() {
        return enemies.size();
    }

    /** Fienderna som återstår, i tur och ordning. */
    public List<Enemy> getEnemies() {
        return List.copyOf(enemies);
    }

    /** Fortsätter ett sparat äventyr. */
    public void restore(SavedAdventure saved) {
        Objects.requireNonNull(saved, "saved får inte vara null");
        startNewAdventure(saved.hero(), saved.enemies());
        saved.backpack().forEach(inventory::add);
        publish(new GameEvent(GameEventType.GAME_LOADED,
                hero.getName() + " fortsätter äventyret i Fångdjupet"));
    }

    public Inventory<Item> getInventory() {
        return inventory;
    }

    /** Kör en stridsrunda mot nuvarande fiende. */
    public void fightRound() {
        if (!hasActiveAdventure() || isOver()) {
            throw new IllegalStateException("Inget pågående äventyr att strida i");
        }
        battle.fightRound(hero, enemies.peekFirst());
        removeDefeatedEnemy();
    }

    /** Dricker den första läkedrycken i ryggsäcken. Returnerar drycken, eller tomt om det inte fanns någon. */
    public Optional<Potion> drinkPotion() {
        if (!hasActiveAdventure() || !hero.isAlive()) {
            return Optional.empty();
        }
        Optional<Potion> potion = inventory.findAll(Potion.class).stream().findFirst();
        potion.ifPresent(p -> {
            inventory.remove(p);
            hero.heal(p.getHealAmount());
            publish(new GameEvent(GameEventType.ITEM_USED,
                    hero.getName() + " dricker " + p.getName() + " och har nu "
                            + hero.getHealth() + "/" + hero.getMaxHealth() + " i hälsa"));
        });
        return potion;
    }

    public boolean isOver() {
        return hasActiveAdventure() && (!hero.isAlive() || enemies.isEmpty());
    }

    private void removeDefeatedEnemy() {
        Enemy enemy = enemies.peekFirst();
        if (enemy != null && !enemy.isAlive()) {
            enemies.removeFirst();
            publish(new GameEvent(GameEventType.ENEMY_DEFEATED,
                    hero.getName() + " besegrade " + enemy.getName()));
            handleLoot(createLoot(enemy.getLevel()));
        }
    }

    private Item createLoot(int level) {
        ItemType[] types = ItemType.values();
        return ItemFactory.create(types[random.nextInt(types.length)], level);
    }

    private void handleLoot(Item item) {
        publish(new GameEvent(GameEventType.ITEM_FOUND, hero.getName() + " hittade " + item));
        if (item instanceof Equippable equippable) {
            equipIfBetter(equippable);
        } else {
            inventory.add(item);
        }
    }

    /** Byter till det nya föremålet om det är bättre än det hjälten bär av samma typ. */
    private void equipIfBetter(Equippable newItem) {
        Optional<Equippable> current = hero.getEquipment().stream()
                .filter(item -> item.getType() == newItem.getType())
                .findFirst();

        if (current.isEmpty()) {
            hero.equip(newItem);
        } else if (power(newItem) > power(current.get())) {
            hero.unequip(current.get());
            hero.equip(newItem);
            inventory.add(current.get());
        } else {
            inventory.add(newItem);
        }
    }

    private static int power(Equippable item) {
        return item.getAttackBonus() + item.getDefenseBonus();
    }
}
