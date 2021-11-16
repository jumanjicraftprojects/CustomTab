package com.illuzionzstudios.tab.tab.components.loader

import com.illuzionzstudios.mist.config.serialization.loader.YamlFileLoader
import com.illuzionzstudios.tab.tab.components.Tab

/**
 * This is the object that loads the tab from our config. If we want
 * to edit the tab instance we edit this and re-save and load our tab object
 * to then update in game
 */
class TabLoader(directory: String, fileName: String): YamlFileLoader<Tab>(directory, fileName) {

    override fun loadYamlObject(): Tab {
        val tab: Tab = Tab(config?.getString("name")!!)



        return tab
    }

    override fun saveYaml() {

    }
}