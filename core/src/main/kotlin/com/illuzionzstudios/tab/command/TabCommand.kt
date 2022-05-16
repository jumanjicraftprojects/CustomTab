package com.illuzionzstudios.tab.command

import com.illuzionzstudios.mist.command.SpigotCommandGroup
import com.illuzionzstudios.mist.command.SpigotSubCommand
import com.illuzionzstudios.mist.command.response.ReturnType
import com.illuzionzstudios.mist.command.type.ReloadCommand
import com.illuzionzstudios.tab.hologram.HologramController

class TabCommand: SpigotCommandGroup() {

    override fun registerSubcommands() {
        registerSubCommand(ReloadCommand())
//        registerSubCommand(HologramCommand())
    }

    inner class HologramCommand: SpigotSubCommand("hologram") {

        init {
            minArguments = 1
            description = "Spawn a hologram"
        }

        override fun onCommand(): ReturnType {
            val text: String = args[0]
            HologramController.createHologram(player!!.location, text)

            return ReturnType.SUCCESS
        }

    }
}