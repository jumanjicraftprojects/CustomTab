package com.illuzionzstudios.tab.components.column.list.type;

import com.illuzionzstudios.tab.CustomTab;
import com.illuzionzstudios.tab.components.column.TabColumn;
import com.illuzionzstudios.tab.components.column.list.ListType;
import com.illuzionzstudios.tab.components.loader.GroupLoader;
import com.illuzionzstudios.tab.components.loader.ListLoader;
import com.illuzionzstudios.tab.components.text.DynamicText;
import com.illuzionzstudios.tab.controller.GroupController;
import com.illuzionzstudios.tab.settings.Settings;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tab list for {@link ListType#ONLINE_PLAYERS}
 */
public class OnlineList extends TabColumn {

    /**
     * Our loader for data
     */
    private final ListLoader loader;

    /**
     * Player's on the tab
     */
    @Getter
    private final List<TabPlayer> players = new ArrayList<>();

    public OnlineList(Player player, ListLoader loader) {
        super(player, loader.getSlot());

        this.loader = loader;
    }

    @Override
    public void render(List<DynamicText> elements) {
        // Add players to cache to display
        if (!((players.size() == Bukkit.getOnlinePlayers().size()) && (players.containsAll(Bukkit.getOnlinePlayers()) && Bukkit.getOnlinePlayers().containsAll(players)))) {
            players.clear();
            Bukkit.getOnlinePlayers().forEach(p -> {
                // Detect vanished players
                if (!isVanished(player, p))
                players.add(new TabPlayer(p));
            });
        }

        try {
            Collections.sort(players);
        } catch (Exception ignored) {
        }

        // For every player to display in the tab
        this.players.forEach(tabPlayer -> {
            if (tabPlayer.getTabPlayer() == null) {
                return;
            }

            elements.add(loader.getElementText());
            // Update text
            loader.getElementText().changeText();
        });
    }

    /**
     * See if player is vanished to another
     *
     * @param player Viewer
     * @param other Checks on
     */
    private boolean isVanished(Player player, Player other) {
        boolean vanished = false;

        // Only if to hide from all
        if (Settings.TAB_VANISH.getBoolean()) {
            for (MetadataValue meta : other.getMetadata("vanished")) {
                if (meta.asBoolean()) vanished = true;
            }
        } else {
            vanished = !player.canSee(other);
        }

        return vanished;
    }

    public class TabPlayer implements Comparable<TabPlayer> {

        /**
         * Player associated
         */
        @Getter
        private final Player tabPlayer;

        /**
         * The group of the player
         */
        @Getter
        private final GroupLoader group;

        public TabPlayer(Player player) {
            this.tabPlayer = player;

            group = GroupController.INSTANCE.getGroup(player);
        }

        private int getWeight() {

            int weight = 0;

            // Sort list
            switch (loader.getSorter()) {
                case WEIGHT:
                    weight += group.getWeight();
                    break;
                case STRING_VARIABLE:
                    break;
                case NUMBER_VARIABLE:
                    // The greater the number, the higher priority
                    // PAPI Here
                    String toParse = loader.getSortVariable();
                    if (CustomTab.isPapiEnabled()) {
                        toParse = PlaceholderAPI.setPlaceholders(tabPlayer, toParse);
                    }
                    weight += Integer.parseInt(toParse);
                    break;
                case DISTANCE:
                    weight -= tabPlayer.getLocation().distance(player.getLocation());
                    break;
            }

            return weight;
        }

        @Override
        public int compareTo(TabPlayer o) {
            return Integer.compare(o.getWeight(), getWeight());
        }
    }

    @Override
    public DynamicText getTitle() {
        return loader.getTitle();
    }
}
