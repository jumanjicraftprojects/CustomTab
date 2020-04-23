/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.column;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.illuzionzstudios.scheduler.MinecraftScheduler;
import com.illuzionzstudios.scheduler.sync.Async;
import com.illuzionzstudios.scheduler.sync.Rate;
import com.illuzionzstudios.scheduler.sync.Sync;
import com.illuzionzstudios.scheduler.util.PresetCooldown;
import com.illuzionzstudios.tab.controller.TabController;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Default player tab
 */
public abstract class DefaultTab {

    /**
     * The player viewing this tab
     */
    protected Player player;
    
    private final TabController tab = TabController.INSTANCE;

    private final Table<Integer, Integer, UUID> avatarCache = HashBasedTable.create();

    private final PresetCooldown refreshCooldown = new PresetCooldown(40);

    private boolean loaded = false;

    public DefaultTab(Player player) {
        this.player = player;

        MinecraftScheduler.get().registerSynchronizationService(this);
        refreshCooldown.go();
    }

    @Sync(rate = Rate.SEC)
    protected void updateHeaderFooter() {
        if (!loaded) {
            return;
        }

        if (player == null) {
            return;
        }
        
        tab.setHeaderFooter("Test Header", "Test Footer", player);
    }

    protected boolean simulationAllowed() {
        return true;
    }

    protected abstract List<Player> getActivePlayers();

    protected Comparator<Player> getComparator() {
        return (o1, o2) -> {
            if (player.equals(o1) && !o2.equals(player)) {
                return -1;
            }
            return 2;
        };
    }

    protected String getDisplayName(Player player) {
        return (player == this.player ? ChatColor.GOLD.toString() : "") + player.getName();
    }

    @Async(rate = Rate.SEC)
    public void render() {
        if (!refreshCooldown.isReady()) {
            return;
        }

        if (player == null) {
            return;
        }

        refreshCooldown.go();

        int slot = 0;

        List<Player> players = getActivePlayers();
        players.sort(getComparator());

        List<String> names = new ArrayList<>();

        // Fill name list with priority players first //
        for (Player pl : players) {
            String name = getDisplayName(pl);
            names.add(name);
        }

        // Add our simulated player list //
//        if (simulationAllowed()) {
//            for (String name : PlayerSimulator.INSTANCE.getCurrentSimulatedPlayers()) {
//                names.add(ChatColor.GRAY + name);
//            }
//        }

        for (int y = 1; y <= 20; y++) {
            for (int x = 1; x <= 4; x++) {
                if (names.size() > slot) {
                    Player player = players.get(slot);

                    if (!avatarCache.contains(x, y) || !avatarCache.get(x, y).equals(player.getUniqueId())) {
                        tab.setAvatar(x, y, player, this.player);
                        avatarCache.put(x, y, player.getUniqueId());
                    }

                    String name = names.get(slot);
                    name = name.length() > 19 ? name.substring(0, 19) : name;
                    tab.setText(x, y, name, this.player);
                } else {
                    tab.setText(x, y, "", this.player);

                    if (avatarCache.contains(x, y)) {
                        avatarCache.remove(x, y);
                        tab.hideAvatar(x, y, this.player);
                    }
                }
                slot++;
            }
        }

        loaded = true;
    }


}
