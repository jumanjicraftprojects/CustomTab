package com.illuzionzstudios.tab.skin

import com.illuzionzstudios.mist.config.YamlConfig
import com.illuzionzstudios.mist.config.serialization.loader.YamlFileLoader

/**
 * Load all skins from the skin file
 */
class SkinLoader : YamlFileLoader<List<CachedSkin>>("", "skins") {

    override fun loadYamlObject(file: YamlConfig?): List<CachedSkin> {
        val list: MutableList<CachedSkin> = ArrayList()

        file?.getKeys(false)?.forEach {
            list.add(CachedSkin(it, file.getString("$it.value") ?: "", file.getString("$it.signature") ?: ""))
        }

        return list
    }

    override fun saveYaml() {
        TODO("Not yet implemented")
    }
}