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

import com.illuzionzstudios.mist.Logger;
import com.illuzionzstudios.mist.config.PluginSettings;
import com.illuzionzstudios.mist.config.locale.Locale;
import com.illuzionzstudios.mist.plugin.SpigotPlugin;
import com.illuzionzstudios.mist.scheduler.MinecraftScheduler;
import com.illuzionzstudios.tab.bukkit.membrane.Membrane;
import com.illuzionzstudios.tab.command.CustomTabCommand;
import com.illuzionzstudios.tab.controller.GroupController;
import com.illuzionzstudios.tab.controller.TabController;
import com.illuzionzstudios.tab.listener.TabRegisterListener;
import com.illuzionzstudios.tab.settings.Settings;
import com.illuzionzstudios.tab.settings.TabLocale;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * Main class instance
 */
public class CustomTab extends SpigotPlugin {

    /**
     * Singleton instance of our {@link SpigotPlugin}
     */
    private static volatile CustomTab INSTANCE;

    /**
     * Return our instance of the {@link SpigotPlugin}
     *
     * Should be overridden in your own {@link SpigotPlugin} class
     * as a way to implement your own methods per plugin
     *
     * @return This instance of the plugin
     */
    public static CustomTab getInstance() {
        // Assign if null
        if (INSTANCE == null) {
            INSTANCE = JavaPlugin.getPlugin(CustomTab.class);

            Objects.requireNonNull(INSTANCE, "Cannot create instance of plugin. Did you reload?");
        }

        return INSTANCE;
    }

    /**
     * Check if PAPI is enabled
     */
    @Getter
    public static boolean papiEnabled;

    @Override
    public void onPluginLoad() {
    }

    @Override
    public void onPluginPreEnable() {

    }

    @Override
    public void onPluginEnable() {
        // Load plugin hooks
        papiEnabled = Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;

        if (papiEnabled) {
            Logger.info("Hooked into PlaceholderAPI");
        }

        // Controllers
        TabController.INSTANCE.initialize(this);
        GroupController.INSTANCE.initialize(this);
        Membrane.INSTANCE.initialize(this);

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
    public void onPluginPreReload() {
    }

    @Override
    public void onPluginReload() {
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

    @Override
    public void onReloadablesStart() {
        registerMainCommand(new CustomTabCommand(), "tab", "customtab");

        // Listeners
        registerListener(TabController.INSTANCE);
        registerListener(new TabRegisterListener());
    }

    @Override
    public PluginSettings getPluginSettings() {
        return new Settings(this);
    }

    @Override
    public Locale getPluginLocale() {
        return new TabLocale(this);
    }

    @Override
    public int getPluginId() {
        return 78200;
    }
}
