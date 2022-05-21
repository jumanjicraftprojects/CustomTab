package com.illuzionzstudios.tab.packet

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.illuzionzstudios.mist.Logger
import com.illuzionzstudios.mist.scheduler.MinecraftScheduler
import org.bukkit.entity.Player
import java.lang.reflect.InvocationTargetException

/**
 * A custom packet to implement certain features
 */
abstract class AbstractPacket(
    var type: PacketType
) {

    protected var handle: PacketContainer? = PacketContainer(type)

    init {
        handle!!.modifier.writeDefaults()
    }

    /**
     * Send the current packet to the given receiver.
     * @param receivers - the receiver.
     * @throws RuntimeException If the packet cannot be sent.
     */
    open fun sendPacket(vararg receivers: Player?) {
        MinecraftScheduler.get()!!.desynchronize {
            try {
                for (receiver in receivers) ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, handle, false)
            } catch (e: InvocationTargetException) {
                Logger.displayError(e, "Cannot send packet")
            }
        }
    }

    /**
     * Simulate receiving the current packet from the given sender.
     * @param sender - the sender.
     * @throws RuntimeException If the packet cannot be received.
     */
    open fun receivePacket(sender: Player?) {
        MinecraftScheduler.get()!!.desynchronize {
            try {
                ProtocolLibrary.getProtocolManager().recieveClientPacket(sender, handle, false)
            } catch (e: Exception) {
                Logger.displayError(e, "Cannot receive packet")
            }
        }
    }

}