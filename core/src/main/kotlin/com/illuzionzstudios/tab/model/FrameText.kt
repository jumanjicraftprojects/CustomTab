package com.illuzionzstudios.tab.model

import com.google.common.collect.Iterators
import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown

/**
 * Text that consists of multiple frames to cycle through
 */
class FrameText(interval: Int, frames: List<String?>?): DynamicText {

    constructor(interval: Int, vararg frames: String) : this(interval, listOf(*frames))

    /**
     * Frames to go through
     */
    private var frames: List<String?>? = null

    /**
     * Iterator for frames
     */
    private var cycle: Iterator<String?>? = null

    /**
     * Current displayed frame
     */
    private var visibleText: String? = null

    /**
     * Interval in ticks between frame updates
     */
    private var interval: PresetCooldown? = null

    init {
        this.frames = frames
        // Create frame cycle
        cycle = Iterators.cycle(this.frames)
        this.interval = PresetCooldown(interval)
    }

    override fun getOriginalText(): String? {
        // Get first element
        return frames!![0]
    }

    override fun getVisibleText(): String? {
        if (visibleText == null) {
            visibleText = getOriginalText()
            changeText()
        }

        return visibleText
    }

    override fun changeText(): String? {
        // Check changing cooldown
        if (!getInterval()!!.isReady || cycle == null) return visibleText
        getInterval()!!.reset()
        getInterval()!!.go()

        visibleText = if (cycle?.hasNext() == true) cycle?.next() ?: getOriginalText() else getOriginalText()
        return visibleText
    }

    override fun getInterval(): PresetCooldown? = interval

    override fun setFrames(frames: List<String?>) {
        this.frames = frames

        // Create frame cycle again
        cycle = Iterators.cycle(getFrames())
        if ((cycle as MutableIterator<String?>?)?.hasNext()!!) visibleText = (cycle as MutableIterator<String?>?)?.next()
    }

    override fun getFrames(): List<String?>? = frames
}