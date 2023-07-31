package com.illuzionzstudios.tab.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction
import com.comphenix.protocol.wrappers.PlayerInfoData
import java.util.*

class PacketPlayServerPlayerInfo : AbstractPacket(PacketType.Play.Server.PLAYER_INFO) {

    /**
     * Action to update
     */
    var action: PlayerInfoAction?
        get() {
            return handle?.playerInfoActions?.read(0)?.first()
        }
        set(value) {
            handle?.playerInfoActions?.write(0, EnumSet.of(value))
        }

    /**
     * Data
     */
    var data: List<PlayerInfoData?>?
        get() {
            return handle?.playerInfoDataLists?.read(1)
        }
        set(value) {
            handle?.playerInfoDataLists?.write(1, value)
        }
}