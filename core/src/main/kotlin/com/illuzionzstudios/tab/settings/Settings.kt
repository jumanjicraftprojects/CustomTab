package com.illuzionzstudios.tab.settings

import com.illuzionzstudios.mist.config.ConfigSettings
import com.illuzionzstudios.mist.config.PluginSettings
import com.illuzionzstudios.mist.config.YamlConfig
import com.illuzionzstudios.mist.plugin.SpigotPlugin

class Settings(plugin: SpigotPlugin): PluginSettings(plugin) {

    override fun loadSettings() {
    }

    companion object {

        /**
         * The locale type to use, for instance
         * "en_US"
         */
        var DEFAULT_TAB = GENERAL_GROUP.create(
            "tab.default", "default",
            "The default tab to show players if",
            "they don't meet any requirements"
        )
    }
}