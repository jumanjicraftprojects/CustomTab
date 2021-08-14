package com.illuzionzstudios.tab.listener;

import com.illuzionzstudios.tab.controller.TabController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Simply used to register tabs on join
 */
public class TabRegisterListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TabController.INSTANCE.showTab(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        // Handle unregistering tabs
        TabController.INSTANCE.clearTabs(event.getPlayer());
    }

}
