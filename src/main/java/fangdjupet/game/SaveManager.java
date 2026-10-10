package fangdjupet.game;

import fangdjupet.combat.Enemy;
import fangdjupet.combat.Hero;
import fangdjupet.combat.MeleeAttack;
import fangdjupet.contract.AttackStrategy;
import fangdjupet.contract.Equippable;
import fangdjupet.contract.Item;
import fangdjupet.contract.ItemType;
import fangdjupet.item.ItemFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Sparar och läser in ett pågående äventyr som en textfil, en rad per sak:
 * <pre>
 * HERO;Peter;40;8;3;33;Magi        namn, maxhälsa, grundanfall, grundförsvar, hälsa, anfallsstil
 * EQUIP;WEAPON;2                   utrustning som hjälten bär: typ, nivå
 * ITEM;POTION;1                    föremål i ryggsäcken: typ, nivå
 * ENEMY;Goblinen;2;18;6;2;18       namn, nivå, maxhälsa, anfall, försvar, hälsa
 * </pre>
 */
public class SaveManager {
    private static final String SEPARATOR = ";";

    public void save(Game game, Path file) throws SaveGameException {
        Hero hero = game.getHero()
                .orElseThrow(() -> new SaveGameException("Det finns inget äventyr att spara"));

        List<String> lines = new ArrayList<>();
        lines.add(join("HERO", clean(hero.getName()), hero.getMaxHealth(), hero.getBaseAttack(),
                hero.getBaseDefense(), hero.getHealth(), hero.getAttackStrategy().getName()));
        for (Equippable item : hero.getEquipment()) {
            lines.add(join("EQUIP", item.getType(), item.getLevel()));
        }
        for (Item item : game.getInventory().getItems()) {
            lines.add(join("ITEM", item.getType(), item.getLevel()));
        }
        for (Enemy enemy : game.getEnemies()) {
            lines.add(join("ENEMY", clean(enemy.getName()), enemy.getLevel(), enemy.getMaxHealth(),
                    enemy.getBaseAttack(), enemy.getBaseDefense(), enemy.getHealth()));
        }

        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SaveGameException("Kunde inte spara till " + file, e);
        }
    }

    public SavedAdventure load(Path file) throws SaveGameException {
        List<String> lines = readLines(file);
        Hero hero = null;
        List<Enemy> enemies = new ArrayList<>();
        List<Item> backpack = new ArrayList<>();

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split(SEPARATOR, -1);
            try {
                switch (parts[0]) {
                    case "HERO" -> hero = parseHero(parts);
                    case "EQUIP" -> requireHero(hero).equip(parseEquippable(parts));
                    case "ITEM" -> backpack.add(parseItem(parts));
                    case "ENEMY" -> enemies.add(parseEnemy(parts));
                    default -> throw new IllegalArgumentException("okänd radtyp");
                }
            } catch (RuntimeException e) {
                throw new SaveGameException("Felaktig rad i sparfilen: " + line, e);
            }
        }

        if (hero == null) {
            throw new SaveGameException("Sparfilen saknar hjälte");
        }
        return new SavedAdventure(hero, enemies, backpack);
    }

    private List<String> readLines(Path file) throws SaveGameException {
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new SaveGameException("Det finns inget sparat äventyr", e);
        } catch (IOException e) {
            throw new SaveGameException("Kunde inte läsa " + file, e);
        }
    }

    private Hero parseHero(String[] parts) {
        requireLength(parts, 7);
        Hero hero = new Hero(parts[1], toInt(parts[2]), toInt(parts[3]), toInt(parts[4]),
                findAttackStyle(parts[6]));
        restoreHealth(hero.getMaxHealth(), toInt(parts[5]), hero::takeDamage);
        return hero;
    }

    private Equippable parseEquippable(String[] parts) {
        if (parseItem(parts) instanceof Equippable equippable) {
            return equippable;
        }
        throw new IllegalArgumentException("föremålet kan inte bäras");
    }

    private Item parseItem(String[] parts) {
        requireLength(parts, 3);
        return ItemFactory.create(ItemType.valueOf(parts[1]), toInt(parts[2]));
    }

    private Enemy parseEnemy(String[] parts) {
        requireLength(parts, 7);
        Enemy enemy = new Enemy(parts[1], toInt(parts[2]), toInt(parts[3]), toInt(parts[4]),
                toInt(parts[5]), new MeleeAttack());
        restoreHealth(enemy.getMaxHealth(), toInt(parts[6]), enemy::takeDamage);
        return enemy;
    }

    private static AttackStrategy findAttackStyle(String name) {
        return AdventureSetup.availableAttackStyles().stream()
                .filter(style -> style.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("okänd anfallsstil: " + name));
    }

    /** En nyskapad karaktär har full hälsa, så skadan som tagits räknas fram och dras av. */
    private static void restoreHealth(int maxHealth, int health, IntConsumer takeDamage) {
        if (health < 0 || health > maxHealth) {
            throw new IllegalArgumentException("ogiltig hälsa: " + health);
        }
        takeDamage.accept(maxHealth - health);
    }

    private static Hero requireHero(Hero hero) {
        if (hero == null) {
            throw new IllegalStateException("utrustning före hjälten");
        }
        return hero;
    }

    private static void requireLength(String[] parts, int expected) {
        if (parts.length != expected) {
            throw new IllegalArgumentException("fel antal fält");
        }
    }

    private static int toInt(String value) {
        return Integer.parseInt(value.trim());
    }

    /** Semikolon i ett namn skulle förstöra radformatet. */
    private static String clean(String name) {
        return name.replace(SEPARATOR, ",");
    }

    private static String join(Object... values) {
        List<String> parts = new ArrayList<>();
        for (Object value : values) {
            parts.add(String.valueOf(value));
        }
        return String.join(SEPARATOR, parts);
    }
}
