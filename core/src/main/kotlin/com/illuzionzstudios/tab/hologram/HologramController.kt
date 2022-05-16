package com.illuzionzstudios.tab.hologram

import com.illuzionzstudios.mist.controller.PluginController
import com.illuzionzstudios.mist.plugin.SpigotPlugin
import org.bukkit.Location
import java.util.*
import kotlin.collections.HashMap

object HologramController : PluginController {

    /**
     * Map of spawned holograms
     */
    private val spawnedHolograms: MutableMap<UUID, Hologram> = HashMap()

    override fun initialize(plugin: SpigotPlugin) {
    }

    override fun stop(plugin: SpigotPlugin) {
        spawnedHolograms.values.forEach {
            it.remove()
        }
        spawnedHolograms.clear()
    }

    /**
     * Create a new hologram
     */
    fun createHologram(location: Location, text: String): UUID {
        val uuid = UUID.randomUUID()
        val hologram = Hologram(uuid, location, text)
        this.spawnedHolograms[uuid] = hologram
        return uuid
    }

}