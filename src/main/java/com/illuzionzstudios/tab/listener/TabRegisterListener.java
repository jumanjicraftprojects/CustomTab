/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.listener;

import com.illuzionzstudios.tab.components.Tab;
import com.illuzionzstudios.tab.components.column.*;
import com.illuzionzstudios.tab.components.column.list.type.OnlineList;
import com.illuzionzstudios.tab.components.loader.ListLoader;
import com.illuzionzstudios.tab.controller.TabController;
import com.illuzionzstudios.tab.components.loader.ColumnLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;

/**
 * Simply used to register tabs on join
 */
public class TabRegisterListener implements Listener {

    public TabRegisterListener(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Tab tabList = new Tab(event.getPlayer(), TabController.INSTANCE.getTab(event.getPlayer()));

        tabList.getLoader().getColumns().forEach(loader -> {
            try {
                // Let's try load based on loader
                if (loader instanceof ColumnLoader) {
                    Constructor<?> ctor = CustomColumn.class.getConstructor(Player.class, ColumnLoader.class);
                    CustomColumn column = (CustomColumn) ctor.newInstance(event.getPlayer(), loader);

                    // Display to player
                    tabList.displayColumn(column.getColumnNumber(), column);
                } else if (loader instanceof ListLoader) {
                    // Constructor for our column class
                    Constructor<?> ctor = null;
                    // Column to display
                    TabColumn column = null;

                    // We must do all checks here for type
                    // of list, sorters etc
                    switch (((ListLoader) loader).getType()) {
                        case ONLINE_PLAYERS:
                            ctor = OnlineList.class.getConstructor(Player.class, ListLoader.class);
                            column = (OnlineList) ctor.newInstance(event.getPlayer(), loader);
                            break;
                    }

                    if (ctor != null && column != null) {
                        // Display to player
                        tabList.displayColumn(column.getColumnNumber(), column);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        TabController.INSTANCE.displayTab(event.getPlayer(), tabList);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        // Handle unregistering tabs
        TabController.INSTANCE.clearTabs(event.getPlayer());
    }

}
