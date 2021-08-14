package com.illuzionzstudios.tab.components.loader;

import com.illuzionzstudios.mist.config.ConfigSection;
import com.illuzionzstudios.tab.components.column.list.ListType;
import com.illuzionzstudios.tab.components.column.list.SortType;
import com.illuzionzstudios.tab.components.text.DynamicText;
import com.illuzionzstudios.tab.components.text.FrameText;
import com.illuzionzstudios.tab.components.text.ScrollableText;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created instance of a list loader.
 * What this class does is when instantiated, it
 * loads all information from a configuration section
 * then stores it.
 */
public class ListLoader implements Loader {

    /**
     * Display slot of the list
     */
    @Getter
    private int slot;

    /**
     * The title of the list
     */
    @Getter
    private DynamicText title;

    /**
     * Type of list
     */
    @Getter
    private ListType type;

    /**
     * How to sort the list
     */
    @Getter
    private SortType sorter;

    /**
     * Variable (passed as string) to sort if
     * a variable sorter is chosen.
     */
    @Getter
    private String sortVariable;

    /**
     * The formatting for each element
     */
    @Getter
    @Setter
    private DynamicText elementText;

    /**
     * @param section Config section we're loading
     */
    public ListLoader(ConfigSection section) {
        // Load slot
        slot = section.getInt("Slot");

        // Load title
        List<String> titleFrames = section.getStringList("Title.Animations");
        title = new FrameText(section.getInt("Title.Interval"), titleFrames);

        // Add to elements
        List<String> frames = section.getStringList("Text.Animations");
        elementText = section.getBoolean("Text.Scroll.Enabled") ?
                new ScrollableText(section.getInt("Text.Scroll.Interval"), frames.get(0)) :
                new FrameText(section.getInt("Text.Interval"), frames);

        type = ListType.valueOf(section.getString("Type").toUpperCase());
        sorter = SortType.valueOf(section.getString("Sorter").toUpperCase());
    }

}
