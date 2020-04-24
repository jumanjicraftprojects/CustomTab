package com.illuzionzstudios.tab.components.column;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.illuzionzstudios.core.util.Logger;
import com.illuzionzstudios.scheduler.util.PresetCooldown;
import com.illuzionzstudios.tab.CustomTab;
import com.illuzionzstudios.tab.controller.TabController;
import com.illuzionzstudios.tab.settings.Settings;
import com.illuzionzstudios.tab.text.DynamicText;
import com.illuzionzstudios.tab.text.FrameText;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

/**
 * Represents a column of the tab
 */
public abstract class TabColumn implements Listener {

    /**
     * The player the tab is shown to
     */
    protected Player player;

    /**
     * List of registered tabs. Sorted by tab column name
     */
    public static Map<String, Class<? extends TabColumn>> registered = new HashMap<>();

    /**
     * Cached icon skins
     */
    protected Table<Integer, Integer, UUID> avatarCache = HashBasedTable.create();

    /**
     * The number of the tab display column
     */
    @Getter
    protected final int columnNumber;

    /**
     * Cursor between pages
     */
    private int cursor = 0;

    /**
     * Text elements on the column
     */
    private List<DynamicText> elements = new ArrayList<>();

    /**
     * Delay between updating tab elements
     */
    private final PresetCooldown elementCooldown;

    /**
     * Delay between scrolling sub pages
     */
    private final PresetCooldown pageScrollCooldown;

    public TabColumn(Player player, int columnNumber) {
        this.player = player;
        this.columnNumber = columnNumber;

        // Set refresh cooldowns
        elementCooldown = new PresetCooldown(Settings.TAB_REFRESH.getInt());
        pageScrollCooldown = new PresetCooldown(Settings.PAGE_SCROLL_COOLDOWN.getInt());

        Bukkit.getServer().getPluginManager().registerEvents(this, CustomTab.getInstance());

        // Start timers
        elementCooldown.go();
        pageScrollCooldown.go();
    }

    /**
     * Called when column is destroyed or no longer being displayed
     */
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlay(PlayerJoinEvent event) {
        if (!event.getPlayer().equals(this.player)) {
            return;
        }

        this.render();
    }

    /**
     * @param column Register a tab column
     */
    public static void register(String name, Class<? extends TabColumn> column) {
        registered.put(name, column);
    }

    /**
     * Refreshes the column of social
     */
    protected abstract void render(List<DynamicText> elements);

    /**
     * @return Returns the title of column
     */
    public abstract DynamicText getTitle();

    /*
     * Render all text elements in tab
     */
    public void render() {
        // Don't render if timer not ready
        if (player == null || !elementCooldown.isReady()) {
            return;
        }
        elementCooldown.reset();
        elementCooldown.go();

        TabController API = TabController.INSTANCE;

        // Render only if empty
        if (elements.isEmpty())
        render(elements);

        List<DynamicText> sub = new ArrayList<>
                (elements.subList(Math.max(0, Math.min(cursor, elements.size())),
                        Math.min(elements.size(), cursor + Settings.PAGE_ELEMENTS.getInt() - 2)));

        sub.add(0, getTitle());
        sub.add(1, new FrameText(-1, " "));

        double size = (elements.size() + 2 + Math.floor((elements.size() / Settings.PAGE_ELEMENTS.getInt() + 1)));

        boolean pageInfo = false;

        if (size >= Settings.PAGE_ELEMENTS.getInt()) {
            // Calculate page length //
            double pageDelta = ((double) (cursor + 3) / Settings.PAGE_ELEMENTS.getInt() + 1) + 1;
            int page = (int) (pageDelta < 2 ? Math.floor(pageDelta) : Math.ceil(pageDelta));
            int max = (int) Math.ceil((size + (2 * elements.size() / Settings.PAGE_ELEMENTS.getInt() + 1)) / Settings.PAGE_ELEMENTS.getInt() + 1);
            sub.add(new FrameText(-1, "&7" + Math.max(1, page) + "&8/&7" + Math.max(1, max) + ""));
            pageInfo = true;
        }

        // For elements in the sub tab
        for (int i = 1; i <= Settings.PAGE_ELEMENTS.getInt() + 1; i++) {
            boolean blank = (i - 1) >= sub.size();

            // Send update packet //
            String text = ChatColor.translateAlternateColorCodes('&', blank ? "" : sub.get(i - 1).getVisibleText());
            String[] textArray = text.split(" ");

            // Get player to see if to display player skin icon
            String playerName = ChatColor.stripColor(textArray.length == 0 ? "" : textArray[textArray.length - 1]);
            Player tabPlayer = Bukkit.getPlayer(playerName);

            // Check all elements with text
            if ((i - 1) < sub.size()) {
                if (tabPlayer != null && !playerName.trim().equalsIgnoreCase("")) {
                    // Set the avatar for that slot
                    if (!avatarCache.contains(columnNumber, i) || !avatarCache.get(columnNumber, i).equals(tabPlayer.getUniqueId())) {
                        API.setAvatar(columnNumber, i, tabPlayer, this.player);
                        avatarCache.put(columnNumber, i, tabPlayer.getUniqueId());
                    }
                }

                // Set text in that slot as our final text
                API.setText(columnNumber, i, text, this.player);
            } else {
                // Otherwise text not defined so set blank
                API.setText(columnNumber, i, "", this.player);

                // Make sure avatar is blank
                if (avatarCache.contains(columnNumber, i)) {
                    avatarCache.remove(columnNumber, i);
                    API.hideAvatar(columnNumber, i, this.player);
                }
            }

            // Go to next page if applicable
            if (pageInfo)
            cursor++;
        }

        // Update text
        elements.forEach(DynamicText::changeText);

        if (pageScrollCooldown.isReady()) {
            // If page display at bottom
            if (pageInfo) {
                cursor -= 3;
            }

            // Check if cursor is greater than applicable
            // number of pages
            if (cursor >= (size - (3 * elements.size() / Settings.PAGE_ELEMENTS.getInt() + 1))) {
                // Reset to page 1
                this.elements.clear();
                cursor = 0;
            }

            pageScrollCooldown.reset();
            pageScrollCooldown.go();
        }
    }


}
