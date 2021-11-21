package com.illuzionzstudios.tab.tab.components.column

import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.tab.components.item.TabItem
import org.bukkit.entity.Player

/**
 * A simple column that just renders all elements for a player
 */
class SimpleColumn(id: String, var elementstoRender: MutableList<TabItem> = ArrayList()) : TabColumn(id) {

    override fun render(slot: Int, player: Player?, displayTitles: Boolean): MutableList<TabItem> {
        val list: MutableList<TabItem> = ArrayList()
        list.addAll(this.elementstoRender)
        return list
    }
}