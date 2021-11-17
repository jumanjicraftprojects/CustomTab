package com.illuzionzstudios.tab.tab.components

import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown
import com.illuzionzstudios.tab.CustomTab
import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.tab.Ping
import com.illuzionzstudios.tab.tab.TabController
import com.illuzionzstudios.tab.tab.components.column.TabColumn
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player

/**
 * A tab data object that contains info about the tab and options
 * to display to players. One tab instance is created per player. This is
 * because the information on the tab is usually unique to the player
 * in terms of placeholders etc
 */
class Tab(
    /**
     * ID of this tab
     */
    val id: String
) {

    // ----------------------------------------
    // General options
    // ----------------------------------------

    /**
     * Permission needed to view this tab
     */
    var permission: String = ""

    /**
     * The weight of the tab
     */
    var weight: Int = 1

    // ----------------------------------------
    // Column options
    // ----------------------------------------

    /**
     * If to display titles for each column
     */
    var displayTitles: Boolean = true

    /**
     * The minimum width for each text element on the tab
     */
    var elementWidth: Int = 50

    /**
     * A map of the tab columns to the slot they're in
     */
    var columns: MutableMap<Int, TabColumn> = HashMap()

    // ----------------------------------------
    // Header and footer
    // ----------------------------------------

    var header: List<DynamicText> = ArrayList()
    var footer: List<DynamicText> = ArrayList()

}