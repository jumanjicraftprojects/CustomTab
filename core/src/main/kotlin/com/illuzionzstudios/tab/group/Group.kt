package com.illuzionzstudios.tab.group

import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.tab.components.item.TabItem

/**
 * A group that a player can belong to
 */
data class Group(val id: String, val permission: String, val weight: Int, val tabDisplay: TabItem, val tagDisplay: List<DynamicText>)