package com.illuzionzstudios.tab.tab.components.loader

import com.illuzionzstudios.mist.Logger
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
        val center: Boolean = file?.getBoolean("center") ?: false

        val customSkin: Boolean = (file?.contains("skin.value", true) == true && file.contains("skin.signature", true))
                && (file.getString("skin.value")?.trim()?.isNotBlank() == true && file.getString("skin.signature")?.trim()?.isNotBlank() == true)

        // Load skin element
        var skin: CachedSkin? = SkinController.getSkin(file?.getString("skin.name"))
        if (customSkin) {
            skin = CachedSkin(
                RandomStringUtils.randomAlphabetic(12),
                file?.getString("skin.value") ?: "",
                file?.getString("skin.signature") ?: ""
            )
        }

        return TextTabItem(DynamicTextLoader(file!!).`object`, skin, ping, center)
    }

    override fun save(): Boolean {
        TODO("Not yet implemented")
    }
}