package com.illuzionzstudios.tab.components.column;

import org.bukkit.entity.Player;

import java.util.List;

public class FriendColumn extends TabColumn {

    public FriendColumn(Player player) {
        super(player, 3);
    }

    public void render(List<String> elements) {
        elements.add("&7Faction: None");
        elements.add("&7Power: 0/0");
        elements.add("&7Online Members: 2/7");
    }

    @Override
    public String getTitle() {
        return "&2&lFaction";
    }

}