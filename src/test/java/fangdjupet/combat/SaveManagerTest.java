package fangdjupet.game;

import fangdjupet.combat.Enemy;
import fangdjupet.combat.EnemyFactory;
import fangdjupet.combat.EnemyType;
import fangdjupet.combat.Hero;
import fangdjupet.combat.MagicAttack;
import fangdjupet.item.ItemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SaveManagerTest {
    @TempDir
    Path tempDir;

    private final SaveManager saveManager = new SaveManager();
    private Path saveFile;
    private Game game;
    private Hero hero;

    @BeforeEach
    void setUp() {
        saveFile = tempDir.resolve("fangdjupet.save");
        hero = new Hero("Peter", 40, 8, 3, new MagicAttack());
        game = new Game();
        game.startNewAdventure(hero, List.of(
                EnemyFactory.create(EnemyType.GOBLIN, 2),
                EnemyFactory.create(EnemyType.TROLL, 3)));
    }

    @Test
    void heroIsRestoredWithHealthStyleAndEquipment() throws SaveGameException {
        hero.takeDamage(7);
        hero.equip(ItemFactory.createWeapon(2));

        saveManager.save(game, saveFile);
        Hero loaded = saveManager.load(saveFile).hero();

        assertEquals("Peter", loaded.getName());
        assertEquals(33, loaded.getHealth());
        assertEquals(40, loaded.getMaxHealth());
        assertEquals(8 + 4, loaded.getAttack());
        assertEquals("Magi", loaded.getAttackStrategy().getName());
        assertEquals(1, loaded.getEquipment().size());
    }

    @Test
    void remainingEnemiesAreRestoredInOrderWithHealth() throws SaveGameException {
        game.getEnemies().get(0).takeDamage(5);

        saveManager.save(game, saveFile);
        List<Enemy> loaded = saveManager.load(saveFile).enemies();

        assertEquals(2, loaded.size());
        assertEquals("Goblinen", loaded.get(0).getName());
        assertEquals(13, loaded.get(0).getHealth());
        assertEquals("Trollet", loaded.get(1).getName());
        assertEquals(3, loaded.get(1).getLevel());
    }

    @Test
    void backpackIsRestored() throws SaveGameException {
        game.getInventory().add(ItemFactory.createPotion(1));
        game.getInventory().add(ItemFactory.createArmor(2));

        saveManager.save(game, saveFile);
        SavedAdventure loaded = saveManager.load(saveFile);

        assertEquals(2, loaded.backpack().size());
        assertEquals("Liten läkedryck", loaded.backpack().get(0).getName());
    }

    @Test
    void restoredAdventureCanBeContinued() throws SaveGameException {
        saveManager.save(game, saveFile);

        Game newGame = new Game();
        newGame.restore(saveManager.load(saveFile));

        assertEquals(2, newGame.getRemainingEnemyCount());
        assertEquals("Peter", newGame.getHero().orElseThrow().getName());
    }

    @Test
    void loadingMissingFileThrows() {
        assertThrows(SaveGameException.class, () -> saveManager.load(tempDir.resolve("finns-inte.save")));
    }

    @Test
    void loadingCorruptFileThrows() throws IOException {
        Files.writeString(saveFile, "HERO;Peter;inte-ett-tal");

        assertThrows(SaveGameException.class, () -> saveManager.load(saveFile));
    }

    @Test
    void savingWithoutAdventureThrows() {
        assertThrows(SaveGameException.class, () -> saveManager.save(new Game(), saveFile));
    }
}
