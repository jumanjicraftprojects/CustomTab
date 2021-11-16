package com.illuzionzstudios.tab.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction
import com.comphenix.protocol.wrappers.PlayerInfoData

class PacketPlayServerPlayerInfo : AbstractPacket(PacketType.Play.Server.PLAYER_INFO) {

    /**
     * Action to update
     */
    var action: PlayerInfoAction?
        get() {
            return handle?.playerInfoAction?.read(0)
        }
        set(value) {
            handle?.playerInfoAction?.write(0, value)
        }

    /**
     * Data
     */
    var data: List<PlayerInfoData?>?
        get() {
            return handle?.playerInfoDataLists?.read(0)
        }
        set(value) {
            handle?.playerInfoDataLists?.write(0, value)
        }
}