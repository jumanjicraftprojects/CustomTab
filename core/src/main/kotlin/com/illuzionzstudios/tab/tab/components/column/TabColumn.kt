package com.illuzionzstudios.tab.tab.components.column

import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown
import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.tab.components.item.TabItem
import org.bukkit.entity.Player

/**
 * An instance of a tab column
 */
abstract class TabColumn(
    /**
     * ID of this tab
     */
    val id: String
) {

    // ----------------------------------------
    // Page options
    // ----------------------------------------

    /**
     * If pagination is enabled
     */
    var pageEnabled: Boolean = true

    /**
     * The height of each column
     */
    var pageElements: Int = 20

    /**
     * Item to display for pagination
     */
    var pageItem: TabItem? = null

    /**
     * The amount of ticks between changing through pages
     */
    var pageInterval: Int = 100

    /**
     * The title for this column
     */
    var title: TabItem? = null

    /**
     * Custom implementation to add [TabItem]
     */
    abstract fun render(slot: Int, player: Player?, displayTitles: Boolean): MutableList<TabItem>

}