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

import com.illuzionzstudios.mist.Logger;
import com.illuzionzstudios.mist.scheduler.MinecraftScheduler;
import com.illuzionzstudios.mist.scheduler.rate.Async;
import com.illuzionzstudios.mist.scheduler.rate.Rate;
import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown;
import com.illuzionzstudios.tab.CustomTab;
import com.illuzionzstudios.tab.components.column.TabColumn;
import com.illuzionzstudios.tab.components.loader.TabLoader;
import com.illuzionzstudios.tab.controller.TabController;
import com.illuzionzstudios.tab.settings.Settings;
import com.illuzionzstudios.tab.components.text.DynamicText;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

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
    private List<DynamicText> header;

    /**
     * List of footer elements
     */
    private List<DynamicText> footer;

    // REFRESH COOLDOWNS

    /**
     * Delay between updating header and footer
     */
    private final PresetCooldown headerFooterCooldown;

    /**
     * Loader for all data
     */
    @Getter
    private TabLoader loader;

    public Tab(Player player, TabLoader loader) {
        this.player = player;
        this.loader = loader;

        // Cooldowns
        headerFooterCooldown = new PresetCooldown(Settings.Refresh.HEADER_FOOTER_REFRESH.getInt());

        // Register scheduler for updating this tab
        MinecraftScheduler.get().registerSynchronizationService(this);

        this.header = loader.getHeader();
        this.footer = loader.getFooter();

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
            // PAPI
            if (CustomTab.isPapiEnabled())
                headerText.append(PlaceholderAPI.setPlaceholders(player, head.getVisibleText()));
            else
                headerText.append(head.getVisibleText());

            // Last element check
            if (!header.get(header.size() - 1).equals(head)) {
                headerText.append("\n");
            }

            head.changeText();
        });

        footer.forEach(foot -> {
            // PAPI
            if (CustomTab.isPapiEnabled())
                footerText.append(PlaceholderAPI.setPlaceholders(player, foot.getVisibleText()));
            else
                footerText.append(foot.getVisibleText());

            // Last element check
            if (!footer.get(footer.size() - 1).equals(foot)) {
                footerText.append("\n");
            }

            foot.changeText();
        });

        // Set the text in the tab
        API.setHeaderFooter(headerText.toString(), footerText.toString(), player);
    }

}
