package com.illuzionzstudios.tab.model

import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown

/**
 * An instance of dynamic text that can constantly be changing
 */
interface DynamicText: Cloneable {

    /**
     * @return Default text to display
     */
    fun getOriginalText(): String?

    /**
     * @return Current frame or visible text
     */
    fun getVisibleText(): String?

    /**
     * @return Attempt to change text to next frame
     */
    fun changeText(): String?

    /**
     * @return Interval between updates
     */
    fun getInterval(): PresetCooldown?

    /**
     * @param frames Update the text's frames
     */
    fun setFrames(frames: List<String>)

    /**
     * @return Get the frames of the text
     */
    fun getFrames(): List<String>?

    fun copy(): DynamicText

}