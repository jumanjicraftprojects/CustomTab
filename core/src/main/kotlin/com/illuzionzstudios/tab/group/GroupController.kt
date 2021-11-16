package com.illuzionzstudios.tab.group

import com.illuzionzstudios.mist.controller.PluginController
import com.illuzionzstudios.mist.plugin.SpigotPlugin
import org.bukkit.entity.Player

object GroupController: PluginController {

    /**
     * All loaded group instances
     */
    val groups: Map<String, Group> = HashMap()

    override fun initialize(plugin: SpigotPlugin) {
        // Load groups
    }

    override fun stop(plugin: SpigotPlugin) {

    }

    /**
     * Gets the highest group the player has
     *
     * @param player The player to check
     */
    fun getGroup(player: Player): Group? {
        var highest: Group? = null

        // Weird permissions deop while doing check
        var wasOp = false
        if (player.isOp) {
            wasOp = true
            player.isOp = false
        }

        for (group in groups.values) {
            // Has permission for group
            if (player.hasPermission(group.permission) || group.permission.trim().equals("", true)) {
                val compare = (highest?.weight ?: 0).compareTo(group.weight)
                if (compare < 0) {
                    highest = group
                }
            }
        }

        if (wasOp) {
            player.isOp = true
        }

        return highest
    }
}