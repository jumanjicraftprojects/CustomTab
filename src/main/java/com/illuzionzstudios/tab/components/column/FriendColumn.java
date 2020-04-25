package com.illuzionzstudios.tab.components.column;

import com.illuzionzstudios.tab.components.text.DynamicText;
import com.illuzionzstudios.tab.components.text.FrameText;
import com.illuzionzstudios.tab.components.text.ScrollableText;
import com.illuzionzstudios.tab.settings.Settings;
import org.bukkit.entity.Player;

import java.util.List;

public class FriendColumn extends TabColumn {

    public FriendColumn(Player player) {
        super(player, 3);
    }

    public void render(List<DynamicText> elements) {
        elements.add(new FrameText(20, "&fFaction information", "&f&lFaction information"));
        elements.add(new ScrollableText(5, "This is now scrolling text that scrolls across", Settings.TAB_WIDTH.getInt() / 3));
    }

    @Override
    public DynamicText getTitle() {
        return new FrameText(-1, "&2&lFaction");
    }

}