package com.illuzionzstudios.tab.components.loader;

import com.illuzionzstudios.mist.config.ConfigSection;
import com.illuzionzstudios.tab.components.text.DynamicText;
import com.illuzionzstudios.tab.components.text.FrameText;
import com.illuzionzstudios.tab.components.text.ScrollableText;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created instance of a column loader.
 * What this class does is when instantiated, it
 * loads all information from a configuration section
 * then stores it.
 */
public class ColumnLoader implements Loader {

    /**
     * Display slot of the column
     */
    @Getter
    private int slot;

    /**
     * The title of the column
     */
    @Getter
    private DynamicText title;

    /**
     * The tab's elements
     */
    @Getter
    private List<DynamicText> elements = new ArrayList<>();

    /**
     * @param section Config section we're loading
     */
    public ColumnLoader(ConfigSection section) {
        // Load slot
        slot = section.getInt("Slot");

        // Load title
        List<String> titleFrames = section.getStringList("Title.Animations");
        title = new FrameText(section.getInt("Title.Interval"), titleFrames);

        // Load text elements
        for (ConfigSection text : section.getSections("Text")) {
            // Add to elements
            List<String> frames = text.getStringList("Animations");
            DynamicText element = text.getBoolean("Scroll.Enabled") ?
                    new ScrollableText(text.getInt("Scroll.Interval"), frames.get(0)) :
                    new FrameText(text.getInt("Interval"), frames);
            elements.add(element);
        }
    }

}
