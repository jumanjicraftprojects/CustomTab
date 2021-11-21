package com.illuzionzstudios.tab.tab.components.loader

import com.illuzionzstudios.mist.config.ConfigSection
import com.illuzionzstudios.mist.config.serialization.loader.type.YamlSectionLoader
import com.illuzionzstudios.tab.skin.CachedSkin
import com.illuzionzstudios.tab.skin.SkinController
import com.illuzionzstudios.tab.tab.Ping
import com.illuzionzstudios.tab.tab.components.item.TabItem
import com.illuzionzstudios.tab.tab.components.item.TextTabItem
import org.apache.commons.lang.RandomStringUtils

/**
 * Responsible for loading a tab item from a config section
 */
class TabItemLoader(section: ConfigSection): YamlSectionLoader<TabItem>(section) {

    override fun loadObject(file: ConfigSection?): TabItem {
        val ping: Ping = Ping.valueOf(file?.getString("ping", "FIVE")?.uppercase() ?: "FIVE")
        // Load skin element
        val skin: CachedSkin = if ((file?.getString("name") ?: "").trim().isEmpty())
            CachedSkin(RandomStringUtils.randomAlphabetic(12), file?.getString("value") ?: "", file?.getString("signature") ?: "") else
                SkinController.UNKNOWN_SKIN

        return TextTabItem(DynamicTextLoader(file!!).`object`, skin, ping,)
    }

    override fun save(): Boolean {
        TODO("Not yet implemented")
    }
}