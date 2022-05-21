package com.illuzionzstudios.tab

import com.illuzionzstudios.tab.api.CustomTabAPI
import com.illuzionzstudios.tab.tab.TabController
import com.illuzionzstudios.tab.tab.components.Tab
import com.illuzionzstudios.tab.tab.components.column.TabColumn
import com.illuzionzstudios.tab.tab.instance.TabColumnInstance
import org.bukkit.entity.Player

class CustomTabImpl : CustomTabAPI {

    override fun setTab(player: Player, tab: Tab) {
        TabController.displayTab(player, tab)
    }

    override fun setColumn(player: Player, slot: Int, column: TabColumn) {
        TabController.getDisplayedTab(player)?.columns?.set(slot,
            TabColumnInstance(player, TabController.getDisplayedTab(player)!!, column)
        )
    }

    override fun registerTab(tab: Tab) {
        TabController.tabs[tab.id] = tab
    }
}