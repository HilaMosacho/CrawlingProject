import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryData {

    private String name;
    private int index;
    private List<ItemData> items;
    private Map<String, ItemData> map;

    /**
     * Constructor
     *
     * @precondition items are sorted in decreasing score order
     */
    public CategoryData(String name, List<ItemData> items) {
        this.name = name;
        this.items = items;
        index = 0;
        map = new HashMap<String, ItemData>();

        for (ItemData item : items) {
            map.put(item.getName(), item);
        }
    }

    /**
     * @return next category item, null if doesn't exist
     */
    public ItemData next() {
        if (index >= items.size())
            return null;

        return items.get(index++);
    }

    /**
     * Finds item data according to its name
     *
     * @param name item's name
     * @return item data if found, null otherwise
     */
    public ItemData find(String name) {
        return map.get(name);
    }
}
