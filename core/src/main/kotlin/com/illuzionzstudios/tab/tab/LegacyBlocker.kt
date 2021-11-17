package com.illuzionzstudios.tab.tab

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.illuzionzstudios.mist.plugin.SpigotPlugin

class LegacyBlocker(plugin: SpigotPlugin): PacketAdapter(plugin, PacketType.Play.Server.PLAYER_INFO) {

    override fun onPacketSending(event: PacketEvent) {
        event.isCancelled = true
    }

}