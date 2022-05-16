package com.illuzionzstudios.tab.hologram

import com.illuzionzstudios.mist.config.locale.mist
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import java.util.*

/**
 * Represents a hologram entity that can display floating text
 */
class Hologram(val uuid: UUID, location: Location, val text: String) {

    var spawnedHologram: ArmorStand? = location.world?.spawn(location, ArmorStand::class.java)

    init {
        spawnedHologram?.setGravity(false)
        spawnedHologram?.setBasePlate(false)
        spawnedHologram?.isVisible = false
        spawnedHologram?.customName = text.mist.toString();
        spawnedHologram?.isCustomNameVisible = true

        spawnedHologram?.entityId
    }

    /**
     * Remove the hologram
     */
    fun remove() {
        this.spawnedHologram?.remove()
    }

}