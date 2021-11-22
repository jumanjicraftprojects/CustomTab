package com.illuzionzstudios.tab.tab.components.list.type

import com.illuzionzstudios.mist.Logger
import com.illuzionzstudios.mist.config.locale.MistString
import com.illuzionzstudios.tab.CustomTab
import com.illuzionzstudios.tab.group.Group
import com.illuzionzstudios.tab.group.GroupController
import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.model.FrameText
import com.illuzionzstudios.tab.skin.SkinController
import com.illuzionzstudios.tab.tab.TabController
import com.illuzionzstudios.tab.tab.components.item.TabItem
import com.illuzionzstudios.tab.tab.components.item.TextTabItem
import com.illuzionzstudios.tab.tab.instance.TabInstance
import com.illuzionzstudios.tab.tab.components.list.SortType
import com.illuzionzstudios.tab.tab.components.list.TabList
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList

/**
 * A list that displays online players
 */
class OnlineList(id: String) : TabList(id) {

    /**
     * Player's on the tab
     */
    private val players: MutableList<TabPlayer?> = ArrayList()

    override fun render(slot: Int, player: Player?, displayTitles: Boolean): MutableList<TabItem> {
        val list: MutableList<TabItem> = ArrayList()

        // Add players to cache to display
//        if (players.size != Bukkit.getOnlinePlayers().size) {
            players.clear()
            Bukkit.getOnlinePlayers().forEach { p: Player? ->
                // Detect vanished players
                if (!isVanished(player!!, p!!)) players.add(TabPlayer(p))
            }
//        }

        try {
            Collections.sort(players)
        } catch (ignored: Exception) {
        }

        // For every player to display the tab
        for (i in players.indices) {
            if (players.isEmpty()) return list
            val tabPlayer = players[i] ?: return ArrayList()

            // Process player name and skin
            var listElement: TabItem = elementText
            if (GroupController.getGroup(tabPlayer.tabPlayer) != null) {
                val groupFormat = GroupController.getGroup(tabPlayer.tabPlayer)?.tabDisplay
                listElement = groupFormat!!
            }

            // Place
            val n = if (displayTitles) 3 else 1

            val tabInstance: TabInstance = TabController.displayedTabs[player?.uniqueId]!!

            // Check if not set at all
            // Set the avatar for that slot
            if (!tabInstance.avatarCache.contains(slot, i + n) || !tabInstance.avatarCache[slot, i + n].equals(tabPlayer.tabPlayer.uniqueId)) {
                SkinController.setAvatar(slot, i + n, tabPlayer.tabPlayer, player)
                tabInstance.avatarCache.put(slot, i + n, tabPlayer.tabPlayer.uniqueId)
            }

            // Set text
            list.add(listElement)
        }

        return list
    }

    /**
     * See if player is vanished to another
     *
     * @param player Viewer
     * @param other Checks on
     */
    private fun isVanished(player: Player, other: Player): Boolean {
        return !player.canSee(other)
    }

    inner class TabPlayer(
        /**
         * Player associated
         */
        val tabPlayer: Player
    ) : Comparable<TabPlayer?> {

        // The greater the number, the higher priority
        // PAPI Here
        private val weight: Int
            get() {
                // Make sure has group
                if (GroupController.getGroup(tabPlayer) == null) return 0

                var weight = 0
                when (sorter) {
                    SortType.WEIGHT -> weight += GroupController.getGroup(tabPlayer)?.weight ?: 0
                    SortType.STRING_VARIABLE -> {
                    }
                    SortType.NUMBER_VARIABLE -> {
                        // The greater the number, the higher priority
                        // PAPI Here
                        var toParse: String = sortVariable
                        if (CustomTab.instance!!.papiEnabled) toParse =
                            PlaceholderAPI.setPlaceholders(tabPlayer, toParse)
                        weight += toParse.toInt()
                    }
                    SortType.DISTANCE -> weight -= tabPlayer.location.distance(tabPlayer.location).toInt()
                }
                return weight
            }

        override fun compareTo(other: TabPlayer?): Int {
            return (other?.weight!!).compareTo(weight)
        }
    }
}