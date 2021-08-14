package com.illuzionzstudios.tab.settings;

import com.illuzionzstudios.mist.config.ConfigSetting;
import com.illuzionzstudios.mist.config.ConfigSettings;
import com.illuzionzstudios.mist.config.PluginSettings;
import com.illuzionzstudios.mist.plugin.SpigotPlugin;

/**
 * This is the main settings class relating to config.yml
 */
public class Settings extends PluginSettings {

    /**
     * Tab refresh settings
     */
    public static final ConfigSettings REFRESH_GROUP = new ConfigSettings("refresh");

    /**
     * Tab pages settings
     */
    public static final ConfigSettings PAGE_GROUP = new ConfigSettings("page");

    /**
     * Actual tab settings
     */
    public static final ConfigSettings TAB_GROUP = new ConfigSettings("tab");

    /**
     * The interval (in ticks) between refreshing the header and footer
     */
    public static ConfigSetting HEADER_FOOTER_REFRESH = REFRESH_GROUP.create("Refresh.Header Footer", 5,
            "This is the interval (in ticks) between updating",
            "the tab header and footer.");

    /**
     * The interval (in ticks) between updating the actual tab elements
     */
    public static ConfigSetting TAB_REFRESH = REFRESH_GROUP.create("Refresh.Tab", 5,
            "This is the interval (in ticks) between updating",
            "the tab elements and sections.");

    public static ConfigSetting PAGE_ELEMENTS = PAGE_GROUP.create("Page.Elements", 20,
            "This is the amount of elements per column.",
            "This includes title and pagination text. Maximum value is 20");

    public static ConfigSetting PAGE_SCROLL_COOLDOWN = PAGE_GROUP.create("Page.Refresh", 100,
            "This is the interval (in ticks) between scrolling",
            "through tab pages. Recommended to set to a slower speed so",
            "the pages don't fly through fast. This will be limited by",
            "the update speed of the tab.");

    public static ConfigSetting TAB_VANISH = TAB_GROUP.create("Tab.Hide Vanished From All", false,
            "If true, all vanished players will be hidden from everyone",
            "in the tab. If false, only players vanished to certain players are hidden.",
            "E.g, Normal player doesn't see vanished player on tab but admin sees",
            "that player in tab.");

    public static ConfigSetting TAB_COLUMNS = TAB_GROUP.create("Tab.Columns", 4,
            "Amount of columns to display. This be set here so we always",
            "display that many columns, but just change the content.");

    public static ConfigSetting TAB_WIDTH = TAB_GROUP.create("Tab.Width", 70,
            "The amount of characters per column. This is the limit for",
            "text displayed per column before it cuts off.",
            "minimum width for each tab column, meaning it will always be that length");

    public static ConfigSetting TAB_TITLES = TAB_GROUP.create("Tab.Display Titles", true,
            "Whether each tab column will have a title, defined for each column.",
            "If false, only text elements will be displayed");

    public static ConfigSetting TAB_PAGE_TEXT = TAB_GROUP.create("Tab.Page Text", "&7%current_page%&8/&7%max_page%",
            "This is the text displayed at the bottom of each tab column",
            "when there are multiple pages.");

    public static ConfigSetting TAB_DEFAULT = TAB_GROUP.create("Tab.Default Tab", "default",
            "The default tab to show the player if",
            "they don't meet any conditions");

    public Settings(SpigotPlugin plugin) {
        super(plugin);
    }

    /**
     * Load settings from file into server
     */
    @Override
    public void loadSettings() {
        // Config information
        SETTINGS_FILE.setHeader("Controls information on tabs to display. Here you",
                "can configure different tab columns to display and",
                "all animations for them. You can also make lists that",
                "loop over a group (eg players) and formats placeholders",
                "for each player. You can also then construct tabs with the",
                "different columns and display them separately to different",
                "players. This way you could have a player tab and a staff tab.",
                " ",
                "Any queries join our discord at https://discord.gg/DbJXzWq");
    }
}
