package fangdjupet.game;

import fangdjupet.combat.Enemy;
import fangdjupet.combat.Hero;
import fangdjupet.contract.Item;

import java.util.List;
import java.util.Objects;

/**
 * Det som läses in från en sparfil: hjälten, fienderna som återstår och ryggsäcken.
 */
public record SavedAdventure(Hero hero, List<Enemy> enemies, List<Item> backpack) {
    public SavedAdventure {
        Objects.requireNonNull(hero, "hero får inte vara null");
        enemies = List.copyOf(enemies);
        backpack = List.copyOf(backpack);
    }
}
