package com.illuzionzstudios.tab.column;

import org.bukkit.entity.Player;

import java.util.List;

public class FriendColumn extends TabColumn {

    public FriendColumn(Player player) {
        super(player, 3);
    }

    public void render(List<String> elements) {
        elements.add("&fHey! You seem pretty");
        elements.add("&flonely..");
        elements.add("");
        elements.add("Type &a/addfriend ");
        elements.add("&fto add a friend!");
    }

    @Override
    public String getTitle() {
        return "&2&lFriends";
    }

}