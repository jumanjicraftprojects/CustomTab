package com.illuzionzstudios.tab.tab.components.column

import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown
import com.illuzionzstudios.tab.model.DynamicText
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
     * The height of each column
     */
    var pageElements: Int = 20

    /**
     * The amount of ticks between changing through pages
     */
    var pageInterval: PresetCooldown = PresetCooldown(100)

    /**
     * The title for this column
     */
    var title: DynamicText? = null

    /**
     * Custom implementation to add elements
     */
    abstract fun render(slot: Int, player: Player?, displayTitles: Boolean): MutableList<DynamicText>

}