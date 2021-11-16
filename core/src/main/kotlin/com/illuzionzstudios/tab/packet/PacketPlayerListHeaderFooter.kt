package com.illuzionzstudios.tab.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.wrappers.WrappedChatComponent

class PacketPlayerListHeaderFooter : AbstractPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER) {

    /**
     * Header component
     */
    var header: WrappedChatComponent?
        get() {
            return handle?.chatComponents?.read(0)
        }
        set(value) {
            handle?.chatComponents?.write(0, value)
        }

    /**
     * Footer component
     */
    var footer: WrappedChatComponent?
        get() {
            return handle?.chatComponents?.read(1)
        }
        set(value) {
            handle?.chatComponents?.write(1, value)
        }
}