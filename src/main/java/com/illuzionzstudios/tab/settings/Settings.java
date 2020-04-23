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
