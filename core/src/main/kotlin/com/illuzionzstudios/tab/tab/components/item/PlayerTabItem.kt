package com.illuzionzstudios.tab.tab.components.item

import com.illuzionzstudios.tab.skin.SkinController
import org.bukkit.entity.Player

/**
 * Represents a tab item for a player
 */
class PlayerTabItem(
    val player: Player,
    val item: TabItem
) : TextTabItem(item.getText(), SkinController.getSkinFromPlayer(player.uniqueId), item.getPing(), item.getFilter(), item.isCenter()) {

}