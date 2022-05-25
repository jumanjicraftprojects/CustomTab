package com.illuzionzstudios.tab.tab.components.list.type

import com.illuzionzstudios.tab.group.GroupController
import com.illuzionzstudios.tab.tab.TabController
import com.illuzionzstudios.tab.tab.components.item.PlayerTabItem
import com.illuzionzstudios.tab.tab.components.item.TabItem
import com.illuzionzstudios.tab.tab.components.list.TabList
import com.illuzionzstudios.tab.tab.components.list.TabPlayer
import org.bukkit.entity.Player
import java.util.*

/**
 * A list that displays online players
 */
class OnlineList(id: String) : TabList<Player>(id) {

    override fun render(slot: Int, player: Player?, displayTitles: Boolean): MutableList<TabItem> {
        try {
            val list: MutableList<TabItem> = ArrayList()

            // Get all tab players
            val players: MutableList<TabPlayer> = TabController.getPlayers(this, slot, pageElements - (if (displayTitles) 2 else 0) - (if (pageEnabled) 1 else 0))

            // Filter players
            players.filter {
                if (isVanished(player!!, it.tabPlayer)) return@filter false
                if (!filter.test(it.tabPlayer)) return@filter false
                return@filter true
            }

            if (players.isEmpty()) return list

            // For every player to display the tab
            for (i in players.indices) {
                val tabPlayer = players[i] ?: return ArrayList()

                // Process player name and skin
                val listElement: TabItem = GroupController.getGroup(tabPlayer.tabPlayer)?.tabDisplay ?: elementText

                // Add element, handles text and skin
                list.add(PlayerTabItem(tabPlayer.tabPlayer, listElement))
            }

            return list
        } catch (ignored: Exception) {
            // If any error occurred will be written over
        }

        return ArrayList()
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

}