/**
 * Copyright © 2020 Property of Illuzionz Studios, LLC
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law. Any licensing of this software overrides
 * this statement.
 */
package com.illuzionzstudios.tab.controller;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.*;
import com.illuzionzstudios.config.Config;
import com.illuzionzstudios.config.ConfigSection;
import com.illuzionzstudios.core.bukkit.controller.BukkitController;
import com.illuzionzstudios.core.util.Logger;
import com.illuzionzstudios.tab.CustomTab;
import com.illuzionzstudios.tab.components.Tab;
import com.illuzionzstudios.tab.bukkit.membrane.CachedSkin;
import com.illuzionzstudios.tab.bukkit.membrane.Membrane;
import com.illuzionzstudios.tab.listener.LegacyBlocker;
import com.illuzionzstudios.tab.packet.AbstractPacket;
import com.illuzionzstudios.tab.packet.WrapperPlayServerPlayerInfo;
import com.illuzionzstudios.tab.packet.WrapperPlayServerPlayerListHeaderFooter;
import com.illuzionzstudios.tab.settings.Settings;
import com.illuzionzstudios.tab.struct.Latency;
import com.illuzionzstudios.tab.template.TabLoader;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Main controller for the player tab
 */
public enum TabController implements Listener, BukkitController<Plugin> {

    INSTANCE;

    /**
     * The currently displayed tabs
     */
    @Getter
    public HashMap<UUID, Tab> displayedTabs = new HashMap<>();

    /**
     * Loaded tab loaders to load tab columns
     */
    @Getter
    public HashMap<String, TabLoader> loaders = new HashMap<>();

    /**
     * Display a tab to the player
     *
     * @param player Player to show tab
     * @param tab Tab to display
     */
    public void displayTab(Player player, Tab tab) {
        if (this.displayedTabs.containsKey(player.getUniqueId())) {
            // Already displaying tab, dismiss other
            this.displayedTabs.get(player.getUniqueId()).disable();
        }

        this.displayedTabs.put(player.getUniqueId(), tab);
    }

    /**
     * @param player Clear all displayed tabs for player
     */
    public void clearTabs(Player player) {
        if (this.displayedTabs.containsKey(player.getUniqueId())) {
            // Already displaying tab, dismiss other
            this.displayedTabs.get(player.getUniqueId()).disable();
        }

        this.displayedTabs.remove(player.getUniqueId());
    }

    public static final char DISPLAY_SLOT;
    public static final char SKIN_SLOT;

    static {
        DISPLAY_SLOT = '\u0000';
        SKIN_SLOT = '\u0001';
    }

    /**
     * Initial list of player slots
     */
    public final List<PlayerInfoData> initialList = new ArrayList<>();

    public void initialize(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        ProtocolLibrary.getProtocolManager().addPacketListener(new LegacyBlocker(plugin));

        // Add default player slots
        for (int x = 1; x <= Settings.TAB_COLUMNS.getInt(); x++) {
            for (int y = 1; y <= Settings.PAGE_ELEMENTS.getInt(); y++) {
                this.initialList.add(
                        new PlayerInfoData(
                                this.getDisplayProfile(x, y),
                                Latency.FIVE.ping,
                                EnumWrappers.NativeGameMode.NOT_SET,
                                WrappedChatComponent.fromText("")
                        )
                );
            }
        }

        // Load tab loaders
        Config config = CustomTab.getInstance().getCoreConfig();

        // Loop through and create a loader for each section
        for (ConfigSection section : config.getSections("Tab.Column")) {
            // Notify the column has been loaded
            Logger.info("Loaded tab column '" + section.getName() + "'");
            this.loaders.put(section.getName().toLowerCase(), new TabLoader(section));
        }
    }

    @Override
    public void stop(Plugin plugin) {
    }

    /**
     * Get game profile at x and y in tab
     *
     * @param x X of player
     * @param y Y of player
     */
    private WrappedGameProfile getDisplayProfile(int x, int y) {
        int id = x * 100 + y;

        return new WrappedGameProfile(
                UUID.nameUUIDFromBytes(ByteBuffer.allocate(16).putInt(id).array()),
                String.format("%c%d", TabController.DISPLAY_SLOT, id)
        );
    }

    private WrappedGameProfile getSkinProfile(UUID uuid) {
        return new WrappedGameProfile(
                uuid,
                String.format("\u00A7%c", TabController.SKIN_SLOT)
        );
    }

    /**
     * Set the header and footer of the tab
     *
     * @param header Header message
     * @param footer Footer message
     * @param players Players to send to
     */
    public void setHeaderFooter(String header, String footer, Player... players) {
        WrapperPlayServerPlayerListHeaderFooter playerListHeaderFooter = new WrapperPlayServerPlayerListHeaderFooter();

        header = ChatColor.translateAlternateColorCodes('&', header);
        footer = ChatColor.translateAlternateColorCodes('&', footer);

        playerListHeaderFooter.setHeader(WrappedChatComponent.fromText(header));
        playerListHeaderFooter.setFooter(WrappedChatComponent.fromText(footer));

        this.sendUnfilteredPacket(playerListHeaderFooter, players);
    }

    /**
     * Set text in the tab at x and y for players
     *
     * @param x X to set
     * @param y Y to set
     * @param text Text to set
     * @param players Players to set for
     */
    public void setText(int x, int y, String text, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);

        data.add(
                new PlayerInfoData(
                        this.getDisplayProfile(x, y),
                        Latency.FIVE.ping,
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText(text)
                )
        );

        playerInfo.setData(data);

        this.sendUnfilteredPacket(playerInfo, players);
    }

    /**
     * Set the ping at a certain slot
     *
     * @param x X to set
     * @param y Y to set
     * @param latency Latency to set
     * @param players Players to set for
     */
    public void setPing(int x, int y, Latency latency, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY);

        data.add(
                new PlayerInfoData(
                        this.getDisplayProfile(x, y),
                        latency.ping,
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText("")
                )
        );

        playerInfo.setData(data);

        this.sendUnfilteredPacket(playerInfo, players);
    }

    /**
     * Set perceived gamemode at x and y
     *
     * @param x X to set
     * @param y Y to set
     * @param gameMode
     * @param players
     */
    public void setGameMode(int x, int y, EnumWrappers.NativeGameMode gameMode, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE);

        data.add(
                new PlayerInfoData(
                        this.getDisplayProfile(x, y),
                        Latency.FIVE.ping,
                        gameMode,
                        WrappedChatComponent.fromText("")
                )
        );

        playerInfo.setData(data);

        this.sendUnfilteredPacket(playerInfo, players);
    }

    public void setGameMode(Player player, EnumWrappers.NativeGameMode gameMode, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE);

        data.add(
                new PlayerInfoData(WrappedGameProfile.fromPlayer(player),
                        Latency.FIVE.ping,
                        gameMode,
                        WrappedChatComponent.fromText("")
                )
        );

        playerInfo.setData(data);
        this.sendUnfilteredPacket(playerInfo, players);
    }


    public void hideAvatar(int x, int y, Player... players) {
        this.setAvatar(
                x,
                y,
                "eyJ0aW1lc3RhbXAiOjE0MTU4NzY5NDkxMTgsInByb2ZpbGVJZCI6IjY4MjVlMWFhNTA2NjQ4MjFhZmYxODA2MGM3NmI0NzY4IiwicHJvZmlsZU5hbWUiOiJDcnVua2xlU3RpY2tzIiwiaXNQdWJsaWMiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80ZGJiMjQyNGE1NTBlMTk0YjY0MDc1NGM0Mzk1YzJhYjM0NjdkZmNlYTQ1NWM4YmVjYjMyYTBmNjZkMmE1In19fQ==",
                "FG23YbDAsXVtWWh/flnIbYAYMEMdOhnbgMf0R1AFx0mLYylIm4C9ne/UzryD6FoZbjRo5eDL87XHGM3BWglooPaRs2IyP1SjlawAXToloazUn+D5U98r4TyV/sn6vds/LZxZg03SI1k3Tv0c/xEAVacR3ko63KbeFWvQ0SXfDtVDeh/EFzlcEZJvp0Ifr8J/NRNgzoaZzr8uE6G6Ta8Ha1v2gDTQBS1/1iSmhbOQzahEfhTA34R7rIKPfCYdK2tNi1uUXOoMEomjgNwjhemc3cJJy5K2nIcXmwNNLLoJD+ts/PydgTlmAr+TGuxXVd/1DXNkYTq6j20PYDKJnPq7JTyquN3rkiHJPsE+aGxg33gSQUr/e4ztjns9LDh3iWehKYwyfr70BcKIgIokzvQlARjSCNJ/XZ2SHVMnOXftWnkcchO1wDAWVQaSp+Iy9O1gMWZPxsie085ca/Pm8xowH2mTvajF5TNyNQ1z4zbzFHqZS0OcXGn+qOEbuatcfzVIBq7t8MyOeeac/rUIpPeBHuu2DV+58h3SSBVEVUUWVQ3h4mn3nenblxoboyMOug6Azg1TkvjSVgglVcfaXJkxU559KT72Z1ISon3sgAIgPOSJkl2PpKKK2XLwlHvb/c3tab+A7TT6mnokfMOdhWSnLPsUE/wtJ7F4EGzk0shM4T4=",
                players
        );
    }

    public void setAvatar(int x, int y, Player player, Player... players) {
        this.setAvatar(x, y, ((CraftPlayer) player).getProfile(), players);
    }

    public void setAvatar(int x, int y, GameProfile profile, Player... players) {
        Property property = profile.getProperties().get("textures").iterator().next();

        this.setAvatar(x, y, property.getValue(), property.getSignature(), players);
    }

    public void setAvatar(int x, int y, String value, String signature, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        WrappedGameProfile gameProfile = this.getDisplayProfile(x, y);

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        data.add(
                new PlayerInfoData(
                        gameProfile,
                        Latency.FIVE.ping,
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText("")
                )
        );

        playerInfo.setData(data);

        this.sendUnfilteredPacket(playerInfo, players);

        data.clear();

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        gameProfile.getProperties().removeAll("textures");

        gameProfile.getProperties().put("textures",
                new WrappedSignedProperty(
                        "textures",
                        value,
                        signature
                )
        );

        data.add(
                new PlayerInfoData(
                        gameProfile,
                        Latency.FIVE.ping,
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText("")
                )
        );

        playerInfo.setData(data);

        this.sendUnfilteredPacket(playerInfo, players);
    }

    public void addSkin(Player player, Player... players) {
        this.addSkin(player.getUniqueId(), ((CraftPlayer) player).getProfile(), players);
    }

    public void addSkin(UUID uuid, GameProfile profile, Player... players) {
        Property property = profile.getProperties().get("textures").iterator().next();

        this.addSkin(uuid, property.getValue(), property.getSignature(), players);
    }


    public void addSkins(Map<UUID, CachedSkin> skins, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        skins.forEach(((uuid, skin) -> {
            WrappedGameProfile gameProfile = this.getSkinProfile(uuid);

            Player available = Bukkit.getPlayer(uuid);

            if (available != null) {
                gameProfile = new WrappedGameProfile(uuid, WrappedGameProfile.fromPlayer(available).getName());
            }

            gameProfile.getProperties().removeAll("textures");

            gameProfile.getProperties().put("textures",
                    new WrappedSignedProperty(
                            "textures",
                            skin.value,
                            skin.signature
                    )
            );
            data.add(
                    new PlayerInfoData(
                            gameProfile,
                            Latency.NONE.ping,
                            EnumWrappers.NativeGameMode.NOT_SET,
                            WrappedChatComponent.fromText("")
                    )
            );

        }));


        playerInfo.setData(data);

        this.removeSkins(skins.keySet(), players);
        this.sendUnfilteredPacket(playerInfo, players);
    }

    public void addSkin(UUID uuid, String value, String signature, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        WrappedGameProfile gameProfile = this.getSkinProfile(uuid);

        Player available = Bukkit.getPlayer(uuid);

        if (available != null) {
            gameProfile = new WrappedGameProfile(uuid, WrappedGameProfile.fromPlayer(available).getName());
        }

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        gameProfile.getProperties().removeAll("textures");

        gameProfile.getProperties().put("textures",
                new WrappedSignedProperty(
                        "textures",
                        value,
                        signature
                )
        );

        data.add(
                new PlayerInfoData(
                        gameProfile,
                        Latency.NONE.ping,
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText("")
                )
        );

        playerInfo.setData(data);

        this.removeSkin(uuid, players);
        this.sendUnfilteredPacket(playerInfo, players);
    }

    public void removeSlot(int x, int y, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        WrappedGameProfile gameProfile = this.getDisplayProfile(x, y);

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        data.add(
                new PlayerInfoData(
                        gameProfile,
                        Latency.NONE.ping,
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText("")
                )
        );

        playerInfo.setData(data);
        this.sendUnfilteredPacket(playerInfo, players);
    }

    public void removeSkin(Player player, Player... players) {
        this.removeSkin(player.getUniqueId(), players);
    }

    public void removeSkins(Collection<UUID> uuids, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();

        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        playerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        for (UUID uuid : uuids) {
            WrappedGameProfile gameProfile = this.getSkinProfile(uuid);

            data.add(
                    new PlayerInfoData(
                            gameProfile,
                            Latency.NONE.ping,
                            EnumWrappers.NativeGameMode.NOT_SET,
                            WrappedChatComponent.fromText("")
                    )
            );
        }

        playerInfo.setData(data);
        this.sendUnfilteredPacket(playerInfo, players);
    }

    public void removeSkin(UUID uuid, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        WrappedGameProfile gameProfile = this.getSkinProfile(uuid);

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        data.add(
                new PlayerInfoData(
                        gameProfile,
                        Latency.NONE.ping,
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText("")
                )
        );

        playerInfo.setData(data);

        this.sendUnfilteredPacket(playerInfo, players);
    }

    public void setName(UUID uuid, String name, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);

        data.add(
                new PlayerInfoData(
                        this.getSkinProfile(uuid),
                        Latency.FIVE.ping,
                        EnumWrappers.NativeGameMode.NOT_SET,
                        WrappedChatComponent.fromText(name)
                )
        );

        playerInfo.setData(data);

        this.sendUnfilteredPacket(playerInfo, players);
    }

    private void sendUnfilteredPacket(AbstractPacket packet, Player... players) {
        try {
            for (Player player : players) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet.getHandle(), false);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // Set the gamemode when gamemode is changed
    @EventHandler
    public void onGamemodeUpdate(PlayerGameModeChangeEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            TabController.INSTANCE.setGameMode(event.getPlayer(), EnumWrappers.NativeGameMode.fromBukkit(event.getNewGameMode()), player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        playerInfo.setData(this.initialList);

        this.sendUnfilteredPacket(playerInfo, event.getPlayer());

        // Load skins for player
        TabController.INSTANCE.addSkins(Membrane.INSTANCE.displaySkins, event.getPlayer());

        // Make sure no skins loaded
        for (int x = 1; x <= Settings.TAB_COLUMNS.getInt(); x++) {
            for (int y = 1; y <= Settings.PAGE_ELEMENTS.getInt(); y++) {
                this.hideAvatar(x, y, event.getPlayer());
            }
        }

        // Add skins for players
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.addSkin(player, event.getPlayer());

            if (!event.getPlayer().equals(player)) {
                this.addSkin(event.getPlayer(), player);
            }
        }
    }

}
