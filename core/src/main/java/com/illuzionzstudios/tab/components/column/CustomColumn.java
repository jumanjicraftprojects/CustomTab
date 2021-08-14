package com.illuzionzstudios.tab.components.column;

import com.illuzionzstudios.tab.components.text.DynamicText;
import com.illuzionzstudios.tab.components.loader.ColumnLoader;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This is a tab column loaded from a
 * configuration file.
 */
public class CustomColumn extends TabColumn {

    /**
     * Our loader for options from config
     */
    private ColumnLoader loader;

    /**
     * Create a new custom column. Loader
     * is parsed in when creating instance
     *
     * @param player Player showing tab
     * @param loader Our loader to display data
     */
    public CustomColumn(Player player, ColumnLoader loader) {
        super(player, loader.getSlot());
        this.loader = loader;
    }

    @Override
    protected void render(List<DynamicText> elements) {
        elements.addAll(loader.getElements());
    }

    @Override
    public DynamicText getTitle() {
        return loader.getTitle();
    }
}
