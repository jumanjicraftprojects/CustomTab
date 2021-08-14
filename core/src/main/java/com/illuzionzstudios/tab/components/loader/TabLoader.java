package com.illuzionzstudios.tab.components.loader;

import com.illuzionzstudios.mist.config.ConfigSection;
import com.illuzionzstudios.tab.components.text.DynamicText;
import com.illuzionzstudios.tab.components.text.FrameText;
import com.illuzionzstudios.tab.components.text.ScrollableText;
import com.illuzionzstudios.tab.controller.TabController;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created instance of a tab loader.
 * What this class does is when instantiated, it
 * loads all information from a configuration section
 * then stores it.
 */
public class TabLoader implements Loader {

    /**
     * Permission needed to view tab
     */
    @Getter
    private String permission;

    /**
     * Weight of the tab
     */
    @Getter
    private int weight;

    /**
     * The columns this tab displays.
     * These are loaded last so we can store the objects
     */
    @Getter
    private List<Loader> columns = new ArrayList<>();

    /**
     * List of header elements
     */
    @Getter
    private List<DynamicText> header = new ArrayList<>();

    /**
     * List of footer elements
     */
    @Getter
    private List<DynamicText> footer = new ArrayList<>();

    public TabLoader(ConfigSection section) {
        permission = section.getString("Permission");
        weight = section.getInt("Weight");

        // Loop through and search for loaded column loaders
        for (String column : section.getStringList("Columns")) {
            Loader loadedColumn = TabController.INSTANCE.getLoaders().get(column.toLowerCase());
            if (loadedColumn != null) {
                this.columns.add(loadedColumn);
            }
        }

        // Load header elements
        for (ConfigSection text : section.getSections("Header.Text")) {
            // Add to elements
            List<String> frames = text.getStringList("Animations");
            DynamicText element = text.getBoolean("Scroll.Enabled") ?
                    new ScrollableText(text.getInt("Scroll.Interval"), frames.get(0)) :
                    new FrameText(text.getInt("Interval"), frames);
            header.add(element);
        }

        // Load footer elements
        for (ConfigSection text : section.getSections("Footer.Text")) {
            // Add to elements
            List<String> frames = text.getStringList("Animations");
            DynamicText element = text.getBoolean("Scroll.Enabled") ?
                    new ScrollableText(text.getInt("Scroll.Interval"), frames.get(0)) :
                    new FrameText(text.getInt("Interval"), frames);
            footer.add(element);
        }
    }

}
