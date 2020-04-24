/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab;

import com.illuzionzstudios.config.Config;
import com.illuzionzstudios.core.plugin.IlluzionzPlugin;
import com.illuzionzstudios.scheduler.bukkit.BukkitScheduler;
import com.illuzionzstudios.tab.bukkit.membrane.Membrane;
import com.illuzionzstudios.tab.components.column.*;
import com.illuzionzstudios.tab.controller.TabController;
import com.illuzionzstudios.tab.listener.TabRegisterListener;
import com.illuzionzstudios.tab.settings.Settings;

import java.util.List;

/**
 * Main class instance
 */
public class CustomTab extends IlluzionzPlugin {

    private static CustomTab INSTANCE;

    public static CustomTab getInstance() {
        return INSTANCE;
    }

    @Override
    public void onPluginLoad() {
        INSTANCE = this;
    }

    @Override
    public void onPluginEnable() {
        // Load all settings and language
        Settings.loadSettings();
        this.setLocale(Settings.LANGUGE_MODE.getString(), false);

        new BukkitScheduler(this).initialize();

        // Controllers
        TabController.INSTANCE.initialize(this);
        Membrane.INSTANCE.initialize(this);

        // Listeners
        new TabRegisterListener(this);

        // Register tabs
        TabColumn.register("online", OnlineColumn.class);
        TabColumn.register("friends", FriendColumn.class);
        TabColumn.register("trainer", TrainerColumn.class);
        TabColumn.register("version", VersionColumn.class);

        // Metrics
        int pluginId = 7282;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onPluginDisable() {

    }

    @Override
    public void onConfigReload() {
        // Load all settings and language
        Settings.loadSettings();
        this.setLocale(Settings.LANGUGE_MODE.getString(), true);
    }

    @Override
    public List<Config> getExtraConfig() {
        return null;
    }

    @Override
    public String getPluginName() {
        return "CustomTab";
    }

    @Override
    public String getPluginVersion() {
        return "1.0 BETA";
    }
}
