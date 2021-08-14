package com.illuzionzstudios.tab.command;

import com.illuzionzstudios.mist.command.SpigotCommandGroup;
import com.illuzionzstudios.mist.command.type.ReloadCommand;

/**
 * Main tab command. We mainly just have the reload command here
 */
public class CustomTabCommand extends SpigotCommandGroup {

    @Override
    public void registerSubcommands() {
        registerSubCommand(new ReloadCommand());
    }

}
