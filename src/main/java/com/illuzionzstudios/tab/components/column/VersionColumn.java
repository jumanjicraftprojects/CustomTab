package com.illuzionzstudios.tab.components.column;

import com.illuzionzstudios.tab.text.DynamicText;
import com.illuzionzstudios.tab.text.FrameText;
import org.bukkit.entity.Player;

import java.util.List;

public class VersionColumn extends TabColumn {

    public VersionColumn(Player player) {
        super(player, 4);
    }

    public void render(List<DynamicText> elements) {
        elements.add(new FrameText(20, "&fStill in development", "&f&lStill in development"));
    }

    @Override
    public DynamicText getTitle() {
        return new FrameText(-1, "&6&lPatch Notes");
    }

}