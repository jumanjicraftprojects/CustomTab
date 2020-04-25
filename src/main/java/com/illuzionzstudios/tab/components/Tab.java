/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.components;

import com.illuzionzstudios.scheduler.MinecraftScheduler;
import com.illuzionzstudios.scheduler.sync.Async;
import com.illuzionzstudios.scheduler.sync.Rate;
import com.illuzionzstudios.scheduler.util.PresetCooldown;
import com.illuzionzstudios.tab.components.column.TabColumn;
import com.illuzionzstudios.tab.controller.TabController;
import com.illuzionzstudios.tab.settings.Settings;
import com.illuzionzstudios.tab.components.text.DynamicText;
import com.illuzionzstudios.tab.components.text.FrameText;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Tab displayed to a player
 *
 * Header and Footer handled here as columns
 * are independent of that
 */
public class Tab {

    /**
     * Player this tab is being displayed to
     */
    @Getter
    private final Player player;

    /**
     * Columns being displayed in the tab
     */
    private HashMap<Integer, TabColumn> columns = new HashMap<>();

    /**
     * List of header elements
     */
    private List<DynamicText> header = new ArrayList<>();

    /**
     * List of footer elements
     */
    private List<DynamicText> footer = new ArrayList<>();

    // REFRESH COOLDOWNS

    /**
     * Delay between updating header and footer
     */
    private final PresetCooldown headerFooterCooldown;

    public Tab(Player player) {
        this.player = player;

        // Cooldowns
        headerFooterCooldown = new PresetCooldown(Settings.HEADER_FOOTER_REFRESH.getInt());

        // Register scheduler for updating this tab
        MinecraftScheduler.get().registerSynchronizationService(this);

        FrameText header = new FrameText(20,
                "&c&lTab Header",
                "&4&lT&c&lab Header",
                "&c&lT&4&la&c&lb Header",
                "&c&lTa&4&lb &c&lHeader");

        this.header.add(new FrameText(-1, ""));
        this.header.add(header);
        this.header.add(new FrameText(-1, ""));

        FrameText footer = new FrameText(20,
                "&c&lTab Footer",
                "&4&lT&c&lab Footer",
                "&c&lT&4&la&c&lb Footer",
                "&c&lTa&4&lb &c&lFooter");

        this.footer.add(new FrameText(-1,""));
        this.footer.add(footer);
        this.footer.add(new FrameText(-1,""));

        // Start timers
        headerFooterCooldown.go();
    }

    /**
     * Called when destroying or disabling this tab
     */
    public void disable() {
        MinecraftScheduler.get().dismissSynchronizationService(this);

        columns.forEach((integer, column) -> {
            column.disable();
        });

        columns.clear();
    }

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

    /**
     * @param slot Hide a tab column in slot
     */
    public void hideColumn(int slot) {
        if (this.columns.containsKey(slot - 1)) {
            // Already displaying column, dismiss other
            this.columns.get(slot - 1).disable();
        }

        this.columns.remove(slot);
    }

    /**
     * Render the header and footer of the tab
     */
    @Async(rate = Rate.TICK)
    public void renderHeaderFooter() {
        // If not ready, don't render
        if (player == null || !headerFooterCooldown.isReady()) {
            return;
        }
        headerFooterCooldown.reset();
        headerFooterCooldown.go();

        TabController API = TabController.INSTANCE;

        // Build the header/footer text
        StringBuilder headerText = new StringBuilder();
        StringBuilder footerText = new StringBuilder();

        // Update text
        header.forEach(head -> {
            head.changeText();

            headerText.append(head.getVisibleText()).append("\n");

            // Last element check
            if (!header.get(header.size() - 1).equals(head)) {
                footerText.append("\n");
            }
        });

        footer.forEach(foot -> {
            foot.changeText();

            footerText.append(foot.getVisibleText());

            // Last element check
            if (!footer.get(footer.size() - 1).equals(foot)) {
                footerText.append("\n");
            }
        });

        // Set the text in the tab
        API.setHeaderFooter(headerText.toString(), footerText.toString(), player);
    }

}
