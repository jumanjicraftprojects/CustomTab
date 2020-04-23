package com.illuzionzstudios.tab.column;

import com.illuzionzstudios.tab.controller.TabController;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class OnlineColumn extends TabColumn {

    public OnlineColumn(Player player) {
        super(player, 2);
    }

    private List<TabPlayer> players = new ArrayList<>();

    @Override
    public void render(List<String> elements) {
        try {
            Collections.sort(players);
        } catch (Exception ignored) {

        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            players.add(new TabPlayer(player));
        });

        players.forEach(p -> {
            if (p.tabPlayer == null) {
                return;
            }

            String text = player.getDisplayName();

            elements.add(text);
        });
    }

    public class TabPlayer implements Comparable<TabPlayer> {

        private Player tabPlayer;

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
