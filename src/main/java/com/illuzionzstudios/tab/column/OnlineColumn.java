package com.illuzionzstudios.tab.column;

import com.illuzionzstudios.tab.controller.TabController;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
public class OnlineColumn extends TabColumn {

    public OnlineColumn(Player player) {
        super(player, 2);
    }

    private final List<TabPlayer> players = new ArrayList<>();

    @Override
    public void render(List<String> elements) {
        // Add players to cache to display
        if (!((players.size() == Bukkit.getOnlinePlayers().size()) && (players.containsAll(Bukkit.getOnlinePlayers()) && Bukkit.getOnlinePlayers().containsAll(players)))) {
            players.clear();
            Bukkit.getOnlinePlayers().forEach(p -> {
                players.add(new TabPlayer(player));
            });
        }

        try {
            Collections.sort(players);
        } catch (Exception ignored) {
        }

        players.forEach(p -> {
            if (p.tabPlayer == null) {
                return;
            }

            String text = player.getDisplayName();

            elements.add(text);
        });
    }

    public class TabPlayer implements Comparable<TabPlayer> {

        private final Player tabPlayer;

        public TabPlayer(Player player) {
            this.tabPlayer = player;
        }

        private int getWeight() {
            int weight = 0;

            if (tabPlayer.equals(player)) {
                weight -= 99999;
            }

            weight += tabPlayer.getLocation().distance(player.getLocation());

            return weight;
        }

        @Override
        public int compareTo(TabPlayer o) {
            return Integer.compare(getWeight(), o.getWeight());
        }
    }

    @Override
    public String getTitle() {
        return "&a&lOnline&2 &2TEST";
    }
}
