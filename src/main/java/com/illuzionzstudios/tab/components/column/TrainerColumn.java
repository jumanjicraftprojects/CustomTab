package com.illuzionzstudios.tab.components.column;

import com.illuzionzstudios.tab.text.DynamicText;
import com.illuzionzstudios.tab.text.FrameText;
import org.bukkit.entity.Player;

import java.util.List;

public class TrainerColumn extends TabColumn {

    public TrainerColumn(Player player) {
        super(player, 1);
    }

    public void render(List<DynamicText> elements) {
        elements.add(new FrameText(10, "&fPlayer information displayed here", "&f&lPlayer information displayed here"));
    }

    @Override
    public DynamicText getTitle() {
        return new FrameText(-1, "&e&lPlayer Info");
    }

}