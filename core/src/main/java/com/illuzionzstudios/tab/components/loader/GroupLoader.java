package com.illuzionzstudios.tab.components.loader;

import com.illuzionzstudios.mist.config.ConfigSection;
import com.illuzionzstudios.tab.components.text.DynamicText;
import com.illuzionzstudios.tab.components.text.FrameText;
import com.illuzionzstudios.tab.components.text.ScrollableText;
import lombok.Getter;

import java.util.List;

/**
 * Created instance of a group loader.
 * What this class does is when instantiated, it
 * loads all information from a configuration section
 * then stores it.
 */
@Getter
public class GroupLoader implements Loader {

    /**
     * Permission required for group
     */
    public String permission;

    /**
     * Weight of the group
     */
    public int weight;

    /**
     * The formatting for each element
     */
    private DynamicText elementText;

    public GroupLoader(ConfigSection section) {
        permission = section.getString("Permission");
        weight = section.getInt("Weight");

        // Add to elements
        List<String> frames = section.getStringList("Display.Animations");
        elementText = section.getBoolean("Display.Scroll.Enabled") ?
                new ScrollableText(section.getInt("Display.Scroll.Interval"), frames.get(0)) :
                new FrameText(section.getInt("Display.Interval"), frames);
    }

}
