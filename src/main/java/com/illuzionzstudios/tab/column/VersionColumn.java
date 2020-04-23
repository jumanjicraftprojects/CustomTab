package com.illuzionzstudios.tab.column;

import org.bukkit.entity.Player;

import java.util.List;

public class VersionColumn extends TabColumn {

    public VersionColumn(Player player) {
        super(player, 4);
    }

    public void render(List<String> elements) {
        elements.add("Still in development");
    }

    @Override
    public String getTitle() {
        return "&6&lPatch Notes";
    }

}