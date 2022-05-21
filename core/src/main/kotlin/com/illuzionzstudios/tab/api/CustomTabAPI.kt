package com.illuzionzstudios.tab.api

import com.illuzionzstudios.tab.tab.components.Tab
import com.illuzionzstudios.tab.tab.components.column.TabColumn
import org.bukkit.entity.Player

/**
 * API for interacting with the tab.
 */
interface CustomTabAPI {

    /**
     * Set the currently displaying tab for the player
     */
    fun setTab(player: Player, tab: Tab)

    /**
     * Set a column for the player in current displayed tab
     */
    fun setColumn(player: Player, slot: Int, column: TabColumn)

    /**
     * Register a custom tab in the plugin. This way it
     * can be assigned to player on join if conditions are met.
     */
    fun registerTab(tab: Tab)

}