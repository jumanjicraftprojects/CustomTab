package com.illuzionzstudios.tab.tab.components.item

import org.bukkit.entity.Player

/**
 * Represents a tab item for a player
 */
class PlayerTabItem(
    val player: Player,
    val item: TabItem
) : TextTabItem(item.getText(), item.getSkin(), item.getPing(), item.getFilter(), item.isCenter()) {

}