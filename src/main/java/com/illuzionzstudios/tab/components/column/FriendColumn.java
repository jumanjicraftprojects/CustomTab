package com.illuzionzstudios.tab.components.column;

import com.illuzionzstudios.tab.text.DynamicText;
import com.illuzionzstudios.tab.text.FrameText;
import org.bukkit.entity.Player;

import java.util.List;

public class FriendColumn extends TabColumn {

    public FriendColumn(Player player) {
        super(player, 3);
    }

    public void render(List<DynamicText> elements) {
        elements.add(new FrameText(20, "&fFaction information", "&f&lFaction information"));
    }

    @Override
    public DynamicText getTitle() {
        return new FrameText(-1, "&2&lFaction");
    }

}