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

import com.illuzionzstudios.command.CommandManager;
import com.illuzionzstudios.config.Config;
import com.illuzionzstudios.core.plugin.IlluzionzPlugin;
import com.illuzionzstudios.scheduler.bukkit.BukkitScheduler;
import com.illuzionzstudios.tab.bukkit.membrane.Membrane;
import com.illuzionzstudios.tab.command.CustomTabCommand;
import com.illuzionzstudios.tab.components.column.*;
import com.illuzionzstudios.tab.controller.GroupController;
import com.illuzionzstudios.tab.controller.TabController;
import com.illuzionzstudios.tab.listener.TabRegisterListener;
import com.illuzionzstudios.tab.settings.Settings;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.List;

/**
 * Main class instance
 */
public class CustomTab extends IlluzionzPlugin {

    private static CustomTab INSTANCE;

    public static CustomTab getInstance() {
        return INSTANCE;
    }

    // Plugin hooks
    @Getter
    public static boolean papiEnabled;

    @Override
    public void onPluginLoad() {
        INSTANCE = this;
    }

    @Override
    public void onPluginEnable() {
        // Load all settings and language
        saveResource("config.yml", false);
        Settings.loadSettings();
        this.setLocale(Settings.LANGUAGE_MODE.getString(), false);

        loadCommands();

        new BukkitScheduler(this).initialize();

        // Load plugin hooks
        papiEnabled = Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        // Controllers
        TabController.INSTANCE.initialize(this);
        GroupController.INSTANCE.initialize(this);
        Membrane.INSTANCE.initialize(this);

        // Listeners
        new TabRegisterListener(this);

        // Metrics
        int pluginId = 7282;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onPluginDisable() {
        GroupController.INSTANCE.stop(this);
        TabController.INSTANCE.stop(this);
    }

    @Override
    public void onConfigReload() {
        saveResource("config.yml", false);
        // Load all settings and language
        Settings.loadSettings();
        this.setLocale(Settings.LANGUAGE_MODE.getString(), true);

        // Reload tab
        TabController.INSTANCE.stop(this);
        TabController.INSTANCE.initialize(this);
        TabController.INSTANCE.reloadSlots();

        // Reload groups
        GroupController.INSTANCE.stop(this);
        GroupController.INSTANCE.initialize(this);

        // Reshow all tabs
        Bukkit.getOnlinePlayers().forEach(TabController.INSTANCE::showTab);
    }

    /**
     * Register all plugin commands
     */
    private void loadCommands() {
        new CommandManager(this).initialize(this);

        CommandManager.get().register(new CustomTabCommand(this));
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
