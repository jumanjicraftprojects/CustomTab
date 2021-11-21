package com.illuzionzstudios.tab.tab.components.item

import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.skin.CachedSkin
import com.illuzionzstudios.tab.tab.Ping

/**
 * This represents an item in the tab. It has data such as the skin, dynamic
 * text, ping etc. It also has a respective position on the tab
 */
interface TabItem {

    /**
     * Defines the text element to be display. Represented
     * as [DynamicText] as can be animated
     */
    fun getText(): DynamicText

    /**
     * Gets the skin avatar to be represented by this entry
     */
    fun getSkin(): CachedSkin?

    /**
     * Represents the ping value of this element
     */
    fun getPing(): Ping

}