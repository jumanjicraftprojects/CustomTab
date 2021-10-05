package com.illuzionzstudios.tab.controller;

import com.illuzionzstudios.mist.Logger;
import com.illuzionzstudios.mist.config.ConfigSection;
import com.illuzionzstudios.mist.config.PluginSettings;
import com.illuzionzstudios.mist.config.YamlConfig;
import com.illuzionzstudios.mist.controller.PluginController;
import com.illuzionzstudios.mist.plugin.SpigotPlugin;
import com.illuzionzstudios.tab.CustomTab;
import com.illuzionzstudios.tab.components.loader.GroupLoader;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 * Control player tab groups
 */
public enum GroupController implements PluginController<SpigotPlugin> {
    INSTANCE;

    /**
     * All loaded groups
     */
    public HashMap<String, GroupLoader> groups = new HashMap<>();

    @Override
    public void initialize(SpigotPlugin plugin) {
        // Load groups
        YamlConfig config = PluginSettings.SETTINGS_FILE;

        // Loop through and create a loader for each section
        for (ConfigSection section : config.getSections("Tab.Groups")) {
            // Notify the column has been loaded
            Logger.info("Loaded tab group '" + section.getName() + "'");
            this.groups.put(section.getName().toLowerCase(), new GroupLoader(section));
        }
    }

    @Override
    public void stop(SpigotPlugin plugin) {
        this.groups.clear();
    }

    /**
     * Gets the highest group the player has
     *
     * @param player The player to check
     */
    public GroupLoader getGroup(Player player) {
        GroupLoader highest = null;

        // Weird permissions deop while doing check
        boolean wasOp = false;
        if (player.isOp()) {
            wasOp = true;
            player.setOp(false);
        }

        for (GroupLoader group : groups.values()) {
            // Has permission for group
            if (player.hasPermission(group.getPermission()) || group.getPermission().trim().equalsIgnoreCase("")) {
                int compare = Integer.compare(highest == null ? 0 : highest.getWeight(), group.getWeight());
                if (compare < 0) {
                    highest = group;
                }
            }
        }

        if (wasOp) {
            player.setOp(true);
        }

        return highest;
    }
}
