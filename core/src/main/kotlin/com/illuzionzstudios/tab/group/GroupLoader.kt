package com.illuzionzstudios.tab.group

import com.illuzionzstudios.mist.config.YamlConfig
import com.illuzionzstudios.mist.config.serialization.loader.YamlFileLoader
import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.tab.components.item.TabItem
import com.illuzionzstudios.tab.tab.components.loader.DynamicTextLoader
import com.illuzionzstudios.tab.tab.components.loader.TabItemLoader

/**
 * Load all groups for the players
 */
class GroupLoader : YamlFileLoader<List<Group>>("", "groups") {

    override fun loadYamlObject(file: YamlConfig?): List<Group> {
        val list: MutableList<Group> = ArrayList()

        file?.getKeys(false)?.forEach {
            val permission = file.getString("$it.permission") ?: ""
            val weight = file.getInt("$it.weight")
            val tabElement: TabItem = TabItemLoader(file.getConfigurationSection("$it.display.tab")!!).`object`
            val tagElements: MutableList<DynamicText> = ArrayList()
            file.getSections("$it.display.tag.text").forEach { element ->
                tagElements.add(DynamicTextLoader(file.getConfigurationSection("$it.display.tag.text." + element?.nodeKey)!!).`object`)
            }
            list.add(Group(it, permission, weight, tabElement, tagElements))
        }

        return list
    }

    override fun saveYaml() {
        TODO("Not yet implemented")
    }
}