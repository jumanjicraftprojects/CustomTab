package com.illuzionzstudios.tab.tab.components.item

import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.model.FrameText
import com.illuzionzstudios.tab.skin.CachedSkin
import com.illuzionzstudios.tab.tab.Ping
import org.bukkit.entity.Player
import java.util.function.Predicate

/**
 * A tab item that represents some text. Usually the base item in each slot
 */
open class TextTabItem(
    private var text0: DynamicText,
    private val skin0: CachedSkin?,
    private val ping0: Ping,
    private val filter0: Predicate<Player>,
    private val isCenter0: Boolean
) : TabItem {

    constructor(vararg frames: String) : this(-1, *frames)
    constructor(interval: Int, vararg frames: String) : this(
        FrameText(interval, *frames),
        null,
        Ping.FIVE,
        Predicate<Player> { true },
        false
    )

    constructor(skin: CachedSkin?, interval: Int, vararg frames: String) : this(
        FrameText(interval, *frames),
        skin,
        Ping.FIVE,
        Predicate<Player> { true },
        false
    )

    constructor(skin: CachedSkin?, ping: Ping, interval: Int, vararg frames: String) : this(
        FrameText(
            interval,
            *frames
        ), skin, ping, Predicate<Player> { true }, false
    )

    constructor(center: Boolean, skin: CachedSkin?, ping: Ping, interval: Int, vararg frames: String) : this(
        FrameText(
            interval,
            *frames
        ), skin, ping, Predicate<Player> { true }, center
    )

    constructor(
        center: Boolean,
        skin: CachedSkin?,
        ping: Ping,
        interval: Int,
        filter: Predicate<Player>,
        vararg frames: String
    ) : this(FrameText(interval, *frames), skin, ping, filter, center)

    override fun getText(): DynamicText = text0
    override fun getSkin(): CachedSkin? = skin0
    override fun getPing(): Ping = ping0
    override fun getFilter(): Predicate<Player> = filter0
    override fun isCenter(): Boolean = isCenter0
}