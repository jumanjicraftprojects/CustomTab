/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.components.column;

import com.illuzionzstudios.tab.components.text.DynamicText;
import com.illuzionzstudios.tab.template.TabLoader;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This is a tab column loaded from a
 * configuration file.
 */
public class CustomColumn extends TabColumn {

    /**
     * Our loader for options from config
     */
    private TabLoader loader;

    /**
     * Create a new custom column. Loader
     * is parsed in when creating instance
     *
     * @param player Player showing tab
     * @param loader Our loader to display data
     */
    public CustomColumn(Player player, TabLoader loader) {
        super(player, loader.getSlot());
        this.loader = loader;
    }

    @Override
    protected void render(List<DynamicText> elements) {
        elements.addAll(loader.getElements());
    }

    @Override
    public DynamicText getTitle() {
        return loader.getTitle();
    }
}
