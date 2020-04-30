/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.command;

import com.illuzionzstudios.command.type.GlobalCommand;
import com.illuzionzstudios.core.locale.player.Message;
import com.illuzionzstudios.tab.CustomTab;
import com.illuzionzstudios.tab.command.sub.ReloadCommand;

public class CustomTabCommand extends GlobalCommand {

    public CustomTabCommand(CustomTab plugin) {
        super("customtab", "tab", "tablist");

        addSubCommand(new ReloadCommand(plugin));
    }

    @Override
    public void onCommand(String s, String[] strings) {
        // By default execute help command

        // Help message
        Message help = CustomTab.getInstance().getLocale().getMessage("general.help")
                .processPlaceholder("version", CustomTab.getInstance().getPluginVersion());

        // Send to executor
        help.sendMessage(commandSender);
    }

}
