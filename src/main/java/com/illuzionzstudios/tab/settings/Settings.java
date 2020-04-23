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

    public static final ConfigSetting HEADER_FOOTER_REFRESH = new ConfigSetting(config, "Refresh.Header Footer", 20,
            "This is the interval (in ticks) between updating",
            "the tab header and footer.");

    public static final ConfigSetting TAB_REFRESH = new ConfigSetting(config, "Refresh.Tab", 100,
            "This is the interval (in ticks) between updating",
            "the tab elements and sections.");

    public static final ConfigSetting PAGE_ELEMENTS = new ConfigSetting(config, "Page.Elements", 19,
            "This is the amount of elements to display per page.",
            "Total slots is this value + 1 because of the pagination text.");

    public static final ConfigSetting PAGE_SCROLL_COOLDOWN = new ConfigSetting(config, "Page.Refresh", 100,
            "This is the interval (in ticks) between scrolling",
            "through tab pages. Recommended to set to a slower speed so",
            "the pages don't fly through fast.");

    public static final ConfigSetting LANGUGE_MODE = new ConfigSetting(config, "System.Language Mode", "en_US",
            "The language file to use for the plugin",
            "More language files (if available) can be found in the plugins locale folder.");

    /**
     * Load settings from file into server
     */
    public static void loadSettings() {
        config.load();
        config.setAutoremove(true);

        config.saveChanges();
    }
}
