package com.illuzionzstudios.tab.tab.components

import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.tab.components.column.TabColumn
import org.bukkit.entity.Player
import java.util.function.Predicate

/**
 * A tab data object that contains info about the tab and options
 * to display to players. One tab instance is created per player. This is
 * because the information on the tab is usually unique to the player
 * in terms of placeholders etc
 */
open class Tab(
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
    var requirement: Predicate<Player> = Predicate<Player> { true }

    /**
     * The weight of the tab. Default to high
     * if creating custom object and not loading from file.
     */
    var weight: Int = 9999

    // ----------------------------------------
    // Column options
    // ----------------------------------------

    /**
     * If to display titles for each column
     */
    var displayTitles: Boolean = false

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

    /**
     * Set the column for this tab in a slot
     */
    protected fun setColumn(slot: Int, column: TabColumn) {
        columns[slot] = column
    }

}