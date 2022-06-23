package com.illuzionzstudios.tab.settings

import com.illuzionzstudios.mist.config.locale.PluginLocale
import com.illuzionzstudios.mist.plugin.SpigotPlugin

class Locale(plugin: SpigotPlugin): PluginLocale(plugin) {

    companion object {
        var TAB_PAGE_TEXT = STARTUP_GROUP.create("tab.page.text", "&7{current_page}&8/&7{max_page}")
    }

}