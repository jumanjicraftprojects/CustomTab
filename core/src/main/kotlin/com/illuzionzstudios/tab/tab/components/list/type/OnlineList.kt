package com.illuzionzstudios.tab.tab.components.list.type

import com.illuzionzstudios.mist.config.locale.MistString
import com.illuzionzstudios.mist.util.TextUtil
import com.illuzionzstudios.tab.CustomTab
import com.illuzionzstudios.tab.group.Group
import com.illuzionzstudios.tab.group.GroupController
import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.model.FrameText
import com.illuzionzstudios.tab.settings.Locale
import com.illuzionzstudios.tab.skin.SkinController
import com.illuzionzstudios.tab.tab.TabController
import com.illuzionzstudios.tab.tab.TabInstance
import com.illuzionzstudios.tab.tab.components.list.SortType
import com.illuzionzstudios.tab.tab.components.list.TabList
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.floor

/**
 * A list that displays online players
 */
class OnlineList(id: String) : TabList(id) {

    /**
     * Player's on the tab
     */
    private val players: MutableList<TabPlayer?> = ArrayList()

    override fun render(slot: Int, player: Player?, displayTitles: Boolean): MutableList<DynamicText> {
        val list: MutableList<DynamicText> = ArrayList()

        // Add players to cache to display
        if (players.size != Bukkit.getOnlinePlayers().size) {
            players.clear()
            Bukkit.getOnlinePlayers().forEach { p: Player? ->
                // Detect vanished players
                if (!isVanished(player!!, p!!)) players.add(TabPlayer(p))
            }
        }

        try {
            Collections.sort(players)
        } catch (ignored: Exception) {
        }

        // For every player to display the tab
        for (i in players.indices) {
            val tabPlayer = players[i] ?: return ArrayList()

            // Process player name and skin
            val listElement: DynamicText? = elementText
            var text: String? = listElement?.getVisibleText()
            if (tabPlayer.group != null) {
                // Group formatting
                text = MistString.of(text)?.toString(
                    "group_format",
                    tabPlayer.group.tabDisplay.getVisibleText()
                ).toString()
            }
            if (CustomTab.instance!!.papiEnabled) // Process PAPI
                text = PlaceholderAPI.setPlaceholders(tabPlayer.tabPlayer, text)

            // Place
            val n = if (displayTitles) 3 else 1

            val tabInstance: TabInstance = TabController.displayedTabs[player?.uniqueId]!!

            // Check if not set at all
            // Set the avatar for that slot
            if (!tabInstance.avatarCache.contains(slot, i + n) || !tabInstance.avatarCache[slot, i + n].equals(tabPlayer.tabPlayer.uniqueId)) {
                SkinController.setDefaultAvatar(slot, i + n, player)
                TabController.setAvatar(slot, i + n, tabPlayer.tabPlayer, player)
                tabInstance.avatarCache.put(slot, i + n, tabPlayer.tabPlayer.uniqueId)
            }

            // Set text
            list.add(FrameText(-1, text ?: ""))
            elementText?.changeText()
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

        /**
         * The group of the player
         */
        val group: Group? = GroupController.getGroup(tabPlayer)

        // The greater the number, the higher priority
        // PAPI Here
        private val weight: Int
            get() {
                // Make sure has group
                if (group == null) return 0

                var weight = 0
                when (sorter) {
                    SortType.WEIGHT -> weight += group.weight
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