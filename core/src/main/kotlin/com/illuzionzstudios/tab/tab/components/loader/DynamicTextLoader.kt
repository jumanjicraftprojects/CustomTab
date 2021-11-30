package com.illuzionzstudios.tab.tab.components.loader

import com.illuzionzstudios.mist.config.ConfigSection
import com.illuzionzstudios.mist.config.serialization.loader.type.YamlSectionLoader
import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.model.FrameText

/**
 * For loading straight dynamic text from a section
 */
class DynamicTextLoader(section: ConfigSection?): YamlSectionLoader<DynamicText>(section) {

    override fun loadObject(file: ConfigSection?): DynamicText {
        val animations: List<String> = file?.getStringList("animations") ?: ArrayList()
        val interval: Int = file?.getInt("interval") ?: -1

        return FrameText(interval, *animations.toTypedArray())
    }

    override fun save(): Boolean {
        TODO("Not yet implemented")
    }
}