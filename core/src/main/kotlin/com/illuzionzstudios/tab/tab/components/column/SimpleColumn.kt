package com.illuzionzstudios.tab.tab.components.column

import com.illuzionzstudios.tab.model.DynamicText
import org.bukkit.entity.Player

/**
 * A simple column that just renders all elements for a player
 */
class SimpleColumn(id: String, var elementstoRender: MutableList<DynamicText> = ArrayList()) : TabColumn(id) {

    override fun render(slot: Int, player: Player?, displayTitles: Boolean): MutableList<DynamicText> {
        val list: MutableList<DynamicText> = ArrayList()
        list.addAll(this.elementstoRender)
        return list
    }
}