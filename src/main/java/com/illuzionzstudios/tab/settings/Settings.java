/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.settings;

import com.illuzionzstudios.config.Config;
import com.illuzionzstudios.config.ConfigSetting;
import com.illuzionzstudios.tab.CustomTab;

/**
 * This is the main settings class relating to config.yml
 */
public class Settings {

    static final Config config = CustomTab.getInstance().getCoreConfig();

    public static final ConfigSetting HEADER_FOOTER_REFRESH = new ConfigSetting(config, "Refresh.Header Footer", 5,
            "This is the interval (in ticks) between updating",
            "the tab header and footer.");

    public static final ConfigSetting TAB_REFRESH = new ConfigSetting(config, "Refresh.Tab", 5,
            "This is the interval (in ticks) between updating",
            "the tab elements and sections.");

    public static final ConfigSetting PAGE_ELEMENTS = new ConfigSetting(config, "Page.Elements", 20,
            "This is the amount of elements per column.",
            "This includes title and pagination text. Maximum value is 20");

    public static final ConfigSetting PAGE_SCROLL_COOLDOWN = new ConfigSetting(config, "Page.Refresh", 100,
            "This is the interval (in ticks) between scrolling",
            "through tab pages. Recommended to set to a slower speed so",
            "the pages don't fly through fast. This will be limited by",
            "the update speed of the tab.");

    public static final ConfigSetting TAB_COLUMNS = new ConfigSetting(config, "Tab.Columns", 4,
            "Amount of columns to display. This be set here so we always",
            "display that many columns, but just change the content.");

    public static final ConfigSetting TAB_WIDTH = new ConfigSetting(config, "Tab.Width", 70,
            "The amount of characters per column. This is the limit for",
            "text displayed per column before it cuts off.",
            "minimum width for each tab column, meaning it will always be that length");

    public static final ConfigSetting TAB_TITLES = new ConfigSetting(config, "Tab.Display Titles", true,
            "Whether each tab column will have a title, defined for each column.",
            "If false, only text elements will be displayed");

    public static final ConfigSetting TAB_PAGE_TEXT = new ConfigSetting(config, "Tab.Page Text", "&7%current_page%&8/&7%max_page%",
            "This is the text displayed at the bottom of each tab column",
            "when there are multiple pages.");

    public static final ConfigSetting LANGUAGE_MODE = new ConfigSetting(config, "System.Language Mode", "en_US",
            "The language file to use for the plugin",
            "More language files (if available) can be found in the plugins locale folder.");

    /**
     * Load settings from file into server
     */
    public static void loadSettings() {
        config.load();
        config.setAutoremove(false);

        config.saveChanges();
    }
}
