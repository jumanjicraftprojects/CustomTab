package com.illuzionzstudios.tab.tab.components.loader

import com.illuzionzstudios.mist.config.ConfigSection
import com.illuzionzstudios.mist.config.YamlConfig
import com.illuzionzstudios.mist.config.serialization.loader.YamlFileLoader
import com.illuzionzstudios.mist.requirement.PlayerRequirementLoader
import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown
import com.illuzionzstudios.tab.tab.components.item.BlankTabItem
import com.illuzionzstudios.tab.tab.components.list.ListType
import com.illuzionzstudios.tab.tab.components.list.SortType
import com.illuzionzstudios.tab.tab.components.list.TabList
import com.illuzionzstudios.tab.tab.components.list.type.OnlineList

/**
 * Load a tab list
 */
class TabListLoader(directory: String, fileName: String) : YamlFileLoader<TabList<*>>(directory, fileName) {

    override fun loadYamlObject(file: YamlConfig?): TabList<*> {
        // Determine which type of list based on type
        val listType = ListType.valueOf(file?.getString("type")?.uppercase() ?: "ONLINE_PLAYERS")
        val list: TabList<*> = TabList.getListFromType(listType, file?.getString("name") ?: "default")
            ?: OnlineList(file?.getString("name") ?: "default")

        list.pageEnabled = file?.getBoolean("page.enabled") ?: true
        list.maxPages = file?.getInt("page.max", 5) ?: 5
        list.pageItem = TabItemLoader(file?.getConfigurationSection("page.text")).`object`

        list.pageElements = file?.getInt("page.elements") ?: 20
        list.pageInterval = file?.getInt("config.interval") ?: 100

        list.title = TabItemLoader(file?.getConfigurationSection("title")!!).`object`
        list.sorter = SortType.valueOf(file.getString("sorter")?.uppercase() ?: "WEIGHT")
        list.sortVariable = file.getString("sort-variable") ?: ""
        list.elementText = TabItemLoader(file.getConfigurationSection("text")!!).`object`

        // Filters if online list
        if (list is OnlineList && file.getConfigurationSection("requirement") != null)
            list.filter = PlayerRequirementLoader(file.getConfigurationSection("requirement")!!).`object`

        return list
    }

    override fun saveYaml() {
        TODO("Not yet implemented")
    }
}