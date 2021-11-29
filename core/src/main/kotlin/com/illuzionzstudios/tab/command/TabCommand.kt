package com.illuzionzstudios.tab.command

import com.illuzionzstudios.mist.command.SpigotCommandGroup
import com.illuzionzstudios.mist.command.SpigotSubCommand
import com.illuzionzstudios.mist.command.response.ReturnType
import com.illuzionzstudios.mist.command.type.ReloadCommand

class TabCommand: SpigotCommandGroup() {

    override fun registerSubcommands() {
        registerSubCommand(ReloadCommand())
        registerSubCommand(TestCommand())
    }

    inner class TestCommand: SpigotSubCommand("test") {

        override fun onCommand(): ReturnType {
            val toExecute = "var greeting='hello world';" +
                    "print(greeting);" +
                    "greeting"

            return ReturnType.SUCCESS
        }

    }
}