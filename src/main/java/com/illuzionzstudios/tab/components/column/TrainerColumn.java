package com.illuzionzstudios.tab.components.column;

import org.bukkit.entity.Player;

import java.util.List;

public class TrainerColumn extends TabColumn {

    public TrainerColumn(Player player) {
        super(player, 1);
    }

    public void render(List<String> elements) {
        elements.add("&7Player information displayed here");
    }

    @Override
    public String getTitle() {
        return "&e&lPlayer Info";
    }

}