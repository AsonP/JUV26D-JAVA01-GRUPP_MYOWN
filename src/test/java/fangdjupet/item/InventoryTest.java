package fangdjupet.item;

import fangdjupet.contract.Item;
import fangdjupet.contract.ItemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryTest {
    private Inventory<Item> inventory;
    private Weapon sword;
    private Armor shield;
    private Potion smallPotion;
    private Potion bigPotion;

    @BeforeEach
    void setUp() {
        inventory = new Inventory<>();
        sword = new Weapon("Järnsvärd", 2, 4);
        shield = new Armor("Träsköld", 1, 2);
        smallPotion = new Potion("Liten läkedryck", 1, 10);
        bigPotion = new Potion("Stor läkedryck", 3, 30);
    }

    @Test
    void newInventoryIsEmpty() {
        assertTrue(inventory.isEmpty());
        assertEquals(0, inventory.size());
    }

    @Test
    void addedItemsAreCounted() {
        inventory.add(sword);
        inventory.add(shield);

        assertEquals(2, inventory.size());
        assertFalse(inventory.isEmpty());
    }

    @Test
    void removeReturnsTrueOnlyForExistingItem() {
        inventory.add(sword);

        assertTrue(inventory.remove(sword));
        assertFalse(inventory.remove(sword));
        assertTrue(inventory.isEmpty());
    }

    @Test
    void findByTypeReturnsOnlyThatType() {
        inventory.add(sword);
        inventory.add(smallPotion);
        inventory.add(bigPotion);

        assertEquals(List.of(smallPotion, bigPotion), inventory.findByType(ItemType.POTION));
    }

    @Test
    void filterUsesGivenCondition() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(bigPotion);

        assertEquals(List.of(sword, bigPotion), inventory.filter(item -> item.getLevel() >= 2));
    }

    @Test
    void findAllReturnsItemsWithCorrectClass() {
        inventory.add(sword);
        inventory.add(smallPotion);

        List<Potion> potions = inventory.findAll(Potion.class);

        assertEquals(1, potions.size());
        assertEquals(10, potions.get(0).getHealAmount());
    }

    @Test
    void itemListCannotBeChangedFromOutside() {
        inventory.add(sword);

        assertThrows(UnsupportedOperationException.class, () -> inventory.getItems().clear());
    }

    @Test
    void addingNullThrows() {
        assertThrows(NullPointerException.class, () -> inventory.add(null));
    }

    @Test
    void itemLevelMustBeAtLeastOne() {
        assertThrows(IllegalArgumentException.class, () -> new Weapon("Pinne", 0, 1));
    }
}
