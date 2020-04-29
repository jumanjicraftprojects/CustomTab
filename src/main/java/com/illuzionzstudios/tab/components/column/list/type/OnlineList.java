package com.illuzionzstudios.tab.components.column.list.type;

import com.illuzionzstudios.tab.components.column.TabColumn;
import com.illuzionzstudios.tab.components.column.list.ListType;
import com.illuzionzstudios.tab.components.loader.GroupLoader;
import com.illuzionzstudios.tab.components.loader.ListLoader;
import com.illuzionzstudios.tab.components.text.DynamicText;
import com.illuzionzstudios.tab.controller.GroupController;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

            loader.getElementText().clearPlaceholders();
            // Get animation display
            // Clone so we don't update actual object
            DynamicText text = loader.getElementText();

            // Format group formatting
            if (tabPlayer.getGroup() != null) {
                text.placehold("group_format", tabPlayer.getGroup().getElementText().getVisibleText());
            }

            elements.add(text);
        });
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
                    weight += Integer.parseInt(loader.getSortVariable());
                    break;
                case DISTANCE:
                    weight -= tabPlayer.getLocation().distance(player.getLocation());
                    break;
            }

            return weight;
        }

        @Override
        public int compareTo(TabPlayer o) {
            return Integer.compare(getWeight(), o.getWeight());
        }
    }

    @Override
    public DynamicText getTitle() {
        return loader.getTitle();
    }
}
