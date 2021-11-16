package com.illuzionzstudios.tab.group

import com.illuzionzstudios.tab.model.DynamicText

/**
 * A group that a player can belong to
 */
data class Group(val permission: String, val weight: Int, val tabDisplay: DynamicText, val tagDisplay: DynamicText)