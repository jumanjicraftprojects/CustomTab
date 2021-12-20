package com.illuzionzstudios.tab.group

import com.illuzionzstudios.mist.Logger
import com.illuzionzstudios.mist.config.YamlConfig
import com.illuzionzstudios.mist.controller.PluginController
import com.illuzionzstudios.mist.plugin.SpigotPlugin
import com.illuzionzstudios.mist.util.PlayerUtil
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

object GroupController: PluginController {

    /**
     * All loaded group instances
     */
    val groups: MutableMap<String, Group> = HashMap()

    /**
     * A cache of assigned groups so we don't constantly have to do check for groups
     */
    val groupCache: MutableMap<UUID, Group> = HashMap()

    override fun initialize(plugin: SpigotPlugin) {
        YamlConfig.loadInternalYaml(plugin, "", "groups.yml")

        // Load groups
        GroupLoader().`object`.forEach {
            Logger.info("Loading group `" + it.id + "`")
            groups[it.id] = it
        }
    }

    override fun stop(plugin: SpigotPlugin) {
        groups.clear()
        groupCache.clear()
    }

    /**
     * Gets the highest group the player has
     *
     * @param player The player to check
     */
    fun getGroup(player: Player): Group? {
        // Find in cache first
//        if (groupCache.contains(player.uniqueId)) return groupCache[player.uniqueId]

        var highest: Group? = null
        for (group in groups.values) {
            // Has permission for group
            if (PlayerUtil.hasPerm(player, group.permission, true) || group.permission.trim().equals("", true)) {
                if ((highest?.weight ?: 0) < group.weight) {
                    highest = group
                }
            }
        }

//        groupCache[player.uniqueId] = highest ?: groups.values.first()
        return highest
    }
}