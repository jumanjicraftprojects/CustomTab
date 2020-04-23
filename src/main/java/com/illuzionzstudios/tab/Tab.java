/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab;

import com.illuzionzstudios.tab.column.TabColumn;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Tab displayed to a player
 */
@RequiredArgsConstructor
public class Tab {

    /**
     * Player this tab is being displayed to
     */
    @Getter
    private final Player player;

    /**
     * Called when destroying or disabling this tab
     */
    public void disable() {
        columns.forEach((integer, column) -> {
            column.disable();
        });

        columns.clear();
    }

    /**
     * Columns being displayed in the tab
     */
    private HashMap<Integer, TabColumn> columns = new HashMap<>();

    /**
     * Start displaying a tab column
     *
     * @param slot Slot to display in
     * @param column The column to display
     */
    public void displayColumn(int slot, TabColumn column) {
        if (this.columns.containsKey(slot - 1)) {
            // Already displaying column, dismiss other
            this.columns.get(slot - 1).disable();
        }

        this.columns.put(slot - 1, column);
    }

}
