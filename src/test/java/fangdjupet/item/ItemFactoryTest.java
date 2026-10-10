package fangdjupet.item;

import fangdjupet.contract.Item;
import fangdjupet.contract.ItemType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemFactoryTest {

    @Test
    void createReturnsRightClassForEachType() {
        assertInstanceOf(Weapon.class, ItemFactory.create(ItemType.WEAPON, 1));
        assertInstanceOf(Armor.class, ItemFactory.create(ItemType.ARMOR, 1));
        assertInstanceOf(Potion.class, ItemFactory.create(ItemType.POTION, 1));
    }

    @Test
    void createdItemHasRequestedLevelAndType() {
        Item item = ItemFactory.create(ItemType.ARMOR, 3);

        assertEquals(3, item.getLevel());
        assertEquals(ItemType.ARMOR, item.getType());
    }

    @Test
    void weaponBonusGrowsWithLevel() {
        assertEquals(2, ItemFactory.createWeapon(1).getAttackBonus());
        assertEquals(6, ItemFactory.createWeapon(3).getAttackBonus());
    }

    @Test
    void potionHealsMoreAtHigherLevel() {
        assertEquals(10, ItemFactory.createPotion(1).getHealAmount());
        assertEquals(30, ItemFactory.createPotion(3).getHealAmount());
    }

    @Test
    void levelAboveNameListGetsHighestName() {
        assertEquals("Runsvärd", ItemFactory.createWeapon(10).getName());
    }

    @Test
    void levelBelowOneThrows() {
        assertThrows(IllegalArgumentException.class, () -> ItemFactory.createPotion(0));
    }
}
