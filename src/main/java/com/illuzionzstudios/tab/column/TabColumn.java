package com.illuzionzstudios.tab.column;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.illuzionzstudios.core.util.Logger;
import com.illuzionzstudios.scheduler.MinecraftScheduler;
import com.illuzionzstudios.scheduler.sync.Async;
import com.illuzionzstudios.scheduler.sync.Rate;
import com.illuzionzstudios.scheduler.util.PresetCooldown;
import com.illuzionzstudios.tab.CustomTab;
import com.illuzionzstudios.tab.controller.TabController;
import com.illuzionzstudios.tab.settings.Settings;
import com.illuzionzstudios.tab.text.AnimatedText;
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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a column of the tab
 */
public abstract class TabColumn implements Listener {

    /**
     * The player the tab is shown to
     */
    protected Player player;

    /**
     * List of registered tabs
     */
    public static List<Class<? extends TabColumn>> registered = new ArrayList<>();

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
    private List<String> elements;

    /**
     * List of header elements
     */
    private List<DynamicText> header = new ArrayList<>();

    /**
     * List of footer elements
     */
    private List<DynamicText> footer = new ArrayList<>();

    // REFRESH COOLDOWNS

    /**
     * Delay between updating header and footer
     */
    private final PresetCooldown headerFooterCooldown;

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
        headerFooterCooldown = new PresetCooldown(Settings.HEADER_FOOTER_REFRESH.getInt());
        elementCooldown = new PresetCooldown(Settings.TAB_REFRESH.getInt());
        pageScrollCooldown = new PresetCooldown(Settings.PAGE_SCROLL_COOLDOWN.getInt());

        MinecraftScheduler.get().registerSynchronizationService(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, CustomTab.getInstance());

        FrameText header = new FrameText(20,
                "&c&lTab Header",
                "&4&lT&c&lab Header",
                "&c&lT&4&la&c&lb Header",
                "&c&lTa&4&lb &c&lHeader");

        this.header.add(new FrameText(-1, ""));
        this.header.add(header);
        this.header.add(new FrameText(-1, ""));

        FrameText footer = new FrameText(20,
                "&c&lTab Footer",
                "&4&lT&c&lab Footer",
                "&c&lT&4&la&c&lb Footer",
                "&c&lTa&4&lb &c&lFooter");

        this.footer.add(new FrameText(-1,""));
        this.footer.add(footer);
        this.footer.add(new FrameText(-1,""));

        // Start timers
        headerFooterCooldown.go();
        elementCooldown.go();
        pageScrollCooldown.go();
    }

    /**
     * Called when column is destroyed or no longer being displayed
     */
    public void disable() {
        MinecraftScheduler.get().dismissSynchronizationService(this);
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
    public static void register(Class<? extends TabColumn> column) {
        registered.add(column);
    }

    /**
     * Refreshes the column of social
     */
    protected abstract void render(List<String> elements);

    /**
     * @return Returns the title of column
     */
    public abstract String getTitle();

    @Async(rate = Rate.TICK)
    public void renderHeaderFooter() {
        // If not ready, don't render
        if (player == null || !headerFooterCooldown.isReady()) {
            return;
        }
        headerFooterCooldown.reset();
        headerFooterCooldown.go();

        TabController API = TabController.INSTANCE;

        // Build the header/footer text
        StringBuilder headerText = new StringBuilder();
        StringBuilder footerText = new StringBuilder();

        // Update text
        header.forEach(header -> {
            header.changeText();

            headerText.append(header.getVisibleText()).append("\n");
        });

        footer.forEach(footer -> {
            footer.changeText();

            footerText.append(footer.getVisibleText()).append("\n");
        });

        // Set the text in the tab
        API.setHeaderFooter(headerText.toString(), footerText.toString(), player);
    }

    /*
     * Render all text elements in tab
     */
    @Async(rate = Rate.TICK)
    public void render() {
        // Don't render if timer not ready
        if (player == null || !elementCooldown.isReady()) {
            return;
        }
        elementCooldown.reset();
        elementCooldown.go();

        TabController API = TabController.INSTANCE;

        List<String> elements = new ArrayList<>();

        if (this.elements == null) {
            render(elements);
        } else {
            elements = this.elements;
        }

        List<String> sub = new ArrayList<>
                (elements.subList(Math.max(0, Math.min(cursor, elements.size())),
                        Math.min(elements.size(), cursor + 17)));

        sub.add(0, ChatColor.translateAlternateColorCodes('&', getTitle()));
        sub.add(1, " ");

        double size = (elements.size() + 2 + Math.floor((elements.size() / 20)));

        boolean pageInfo = false;

        if (size >= Settings.PAGE_ELEMENTS.getInt()) {
            // Calculate page length //
            double pageDelta = ((double) (cursor + 3) / Settings.PAGE_ELEMENTS.getInt() + 1) + 1;
            int page = (int) (pageDelta < 2 ? Math.floor(pageDelta) : Math.ceil(pageDelta));
            int max = (int) Math.ceil((size + (2 * elements.size() / Settings.PAGE_ELEMENTS.getInt() + 1)) / Settings.PAGE_ELEMENTS.getInt() + 1);

            sub.add("&7" + Math.max(1, page) + "&8/&7" + Math.max(1, max) + "");
            this.elements = elements;
            pageInfo = true;
        }

        for (int i = 1; i <= Settings.PAGE_ELEMENTS.getInt() + 1; i++) {
            boolean blank = (i - 1) >= sub.size();

            // Send update packet //
            String text = ChatColor.translateAlternateColorCodes('&', blank ? "" : sub.get(i - 1));
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
            cursor++;

        }

        if (pageScrollCooldown.isReady()) {
            // If page display at bottom
            if (pageInfo) {
                cursor -= 3;
            }

            // Check if cursor is greater than applicable
            // number of pages
            if (cursor >= (size - (3 * elements.size() / Settings.PAGE_ELEMENTS.getInt() + 1))) {
                // Reset to page 1
                this.elements = null;
                cursor = 0;
            }

            pageScrollCooldown.reset();
            pageScrollCooldown.go();
        }

    }


}
