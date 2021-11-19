package com.illuzionzstudios.tab.tab.components.item

import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.model.FrameText
import com.illuzionzstudios.tab.skin.CachedSkin
import com.illuzionzstudios.tab.skin.SkinController
import com.illuzionzstudios.tab.tab.Ping

/**
 * A tab item that represents some text. Usually the base item in each slot
 */
open class TextTabItem(
    private val text0: DynamicText,
    private val skin0: CachedSkin,
    private val ping0: Ping
): TabItem {

    constructor(vararg frames: String) : this(-1, *frames)
    constructor(interval: Int, vararg frames: String) : this(FrameText(interval, *frames), SkinController.UNKNOWN_SKIN, Ping.FIVE)
    constructor(skin: CachedSkin, interval: Int, vararg frames: String) : this(FrameText(interval, *frames), skin, Ping.FIVE)
    constructor(skin: CachedSkin, ping: Ping, interval: Int, vararg frames: String) : this(FrameText(interval, *frames), skin, ping)

    override fun getText(): DynamicText = text0
    override fun getSkin(): CachedSkin = skin0
    override fun getPing(): Ping = ping0
}