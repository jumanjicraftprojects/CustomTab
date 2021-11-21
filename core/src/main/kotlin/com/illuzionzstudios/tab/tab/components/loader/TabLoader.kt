package com.illuzionzstudios.tab.tab.components.loader

import com.illuzionzstudios.mist.Logger
import com.illuzionzstudios.mist.config.YamlConfig
import com.illuzionzstudios.mist.config.serialization.loader.YamlFileLoader
import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.tab.TabController
import com.illuzionzstudios.tab.tab.components.Tab
import com.illuzionzstudios.tab.tab.components.column.TabColumn

/**
 * This is the object that loads the tab from our config. If we want
 * to edit the tab instance we edit this and re-save and load our tab object
 * to then update in game
 */
class TabLoader(directory: String, fileName: String): YamlFileLoader<Tab>(directory, fileName) {

    override fun loadYamlObject(file: YamlConfig?): Tab {
        val tab = Tab(file?.getString("name") ?: "default")

        tab.permission = file?.getString("permission") ?: ""
        tab.weight = file?.getInt("weight") ?: 1

        tab.displayTitles = file?.getBoolean("columns.display-titles", true) ?: true
        tab.elementWidth = file?.getInt("columns.width") ?: 50

        // Load columns into tab
        // COLUMNS MUST BE LOADED FIRST
        file?.getConfigurationSection("columns.list")?.getKeys(false)?.forEach {
            // Try columns then list
            tab.columns[it.toInt()] = TabController.columns[file.getString("columns.list.$it")] ?: TabController.lists[file.getString("columns.list.$it")]!!
        }

        val headerText: MutableList<DynamicText> = ArrayList()
        file?.getSections("header.text")?.forEach {
            headerText.add(DynamicTextLoader(it!!).`object`)
        }
        tab.header = headerText

        val footerText: MutableList<DynamicText> = ArrayList()
        file?.getSections("footer.text")?.forEach {
            footerText.add(DynamicTextLoader(it!!).`object`)
        }
        tab.footer = footerText

        return tab
    }

    override fun saveYaml() {
        TODO()
    }
}