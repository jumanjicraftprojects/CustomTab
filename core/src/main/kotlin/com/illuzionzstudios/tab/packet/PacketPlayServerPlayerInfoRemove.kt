package com.illuzionzstudios.tab.packet

import com.comphenix.protocol.PacketType
import java.util.*

class PacketPlayServerPlayerInfoRemove : AbstractPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE) {
    var data : List<UUID?>?
        get() {
            return handle?.uuidLists?.read(0)
        }
        set(value) {
            handle?.uuidLists?.write(0, value)
        }
}