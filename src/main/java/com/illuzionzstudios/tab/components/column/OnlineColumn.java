package com.illuzionzstudios.tab.components.column;

import com.illuzionzstudios.tab.text.DynamicText;
import com.illuzionzstudios.tab.text.FrameText;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class OnlineColumn extends TabColumn {

    public OnlineColumn(Player player) {
        super(player, 2);
    }

    private final List<TabPlayer> players = new ArrayList<>();

    @Override
    public void render(List<DynamicText> elements) {
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

            elements.add(new FrameText(-1, text));
        });
    }

    public class TabPlayer implements Comparable<TabPlayer> {

        private final Player tabPlayer;

        public TabPlayer(Player player) {
            this.tabPlayer = player;
        }

        private int getWeight() {
            int weight = 0;

            weight += tabPlayer.getLocation().distance(player.getLocation());

            return weight;
        }

        @Override
        public int compareTo(TabPlayer o) {
            return Integer.compare(getWeight(), o.getWeight());
        }
    }

    @Override
    public DynamicText getTitle() {
        return new FrameText(-1, "&a&lOnline &2Test");
    }
}
