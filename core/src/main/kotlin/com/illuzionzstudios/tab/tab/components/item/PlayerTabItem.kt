package com.illuzionzstudios.tab.tab.components.item

import com.illuzionzstudios.tab.tab.TabController
import org.bukkit.entity.Player

/**
 * Represents a tab item for a player
 */
class PlayerTabItem(
    val player: Player,
    val item: TabItem
) : TextTabItem(item.getText(), TabController.getSkinFromPlayer(player.uniqueId), item.getPing(), item.getFilter(), item.isCenter()) {

}