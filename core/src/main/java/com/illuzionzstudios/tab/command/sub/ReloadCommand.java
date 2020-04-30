package com.illuzionzstudios.tab.command.sub;

import com.illuzionzstudios.command.type.SubCommand;
import com.illuzionzstudios.tab.CustomTab;
import com.illuzionzstudios.tab.struct.Permission;

/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */

public class ReloadCommand extends SubCommand {

    private final CustomTab plugin;

    public ReloadCommand(CustomTab plugin) {
        super("reload", "rl");

        this.plugin = plugin;

        this.requiredPermission = Permission.RELOAD;
    }

    @Override
    public void onCommand(String s, String[] strings) {
        plugin.reloadConfig();
        plugin.getLocale().getMessage("general.reload").sendPrefixedMessage(commandSender);
    }

    @Override
    public boolean isConsoleAllowed() {
        return true;
    }
}
