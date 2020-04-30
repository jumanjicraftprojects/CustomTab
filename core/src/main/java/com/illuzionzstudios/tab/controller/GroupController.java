/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.controller;

import com.illuzionzstudios.config.Config;
import com.illuzionzstudios.config.ConfigSection;
import com.illuzionzstudios.core.bukkit.controller.BukkitController;
import com.illuzionzstudios.core.util.Logger;
import com.illuzionzstudios.tab.CustomTab;
import com.illuzionzstudios.tab.components.loader.GroupLoader;
import com.illuzionzstudios.tab.components.loader.ListLoader;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 * Control player tab groups
 */
public enum GroupController implements BukkitController<Plugin> {
    INSTANCE;

    /**
     * All loaded groups
     */
    public HashMap<String, GroupLoader> groups = new HashMap<>();

    @Override
    public void initialize(Plugin plugin) {
        // Load groups
        Config config = CustomTab.getInstance().getCoreConfig();

        // Loop through and create a loader for each section
        for (ConfigSection section : config.getSections("Tab.Groups")) {
            // Notify the column has been loaded
            Logger.info("Loaded tab group '" + section.getName() + "'");
            this.groups.put(section.getName().toLowerCase(), new GroupLoader(section));
        }
    }

    @Override
    public void stop(Plugin plugin) {
        this.groups.clear();
    }

    /**
     * Gets the highest group the player has
     *
     * @param player The player to check
     */
    public GroupLoader getGroup(Player player) {
        GroupLoader highest = null;

        for (GroupLoader group : groups.values()) {
            // Has permission for group
            if (player.hasPermission(group.getPermission()) || group.getPermission().trim().equalsIgnoreCase("")) {
                int compare = Integer.compare(highest == null ? 0 : highest.getWeight(), group.getWeight());
                if (compare < 0) {
                    highest = group;
                }
            }
        }

        return highest;
    }
}
