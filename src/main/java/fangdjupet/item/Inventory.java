package fangdjupet.item;

import fangdjupet.contract.Item;
import fangdjupet.contract.ItemType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Ett generiskt inventarium. T avgör vilka föremål som får läggas i det,
 * t.ex. Inventory<Item> för allt eller Inventory<Potion> för bara drycker.
 */
public class Inventory<T extends Item> {
    private final List<T> items = new ArrayList<>();

    public void add(T item) {
        items.add(Objects.requireNonNull(item, "item får inte vara null"));
    }

    public boolean remove(T item) {
        return items.remove(item);
    }

    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<T> filter(Predicate<? super T> condition) {
        return items.stream().filter(condition).toList();
    }

    public List<T> findByType(ItemType type) {
        return filter(item -> item.getType() == type);
    }

    /** Hämtar alla föremål av en viss klass, med rätt typ direkt, t.ex. findAll(Potion.class). */
    public <U extends T> List<U> findAll(Class<U> itemClass) {
        return items.stream()
                .filter(itemClass::isInstance)
                .map(itemClass::cast)
                .toList();
    }
}
