package com.illuzionzstudios.tab.bukkit.membrane;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.illuzionzstudios.mist.controller.PluginController;
import com.illuzionzstudios.mist.plugin.SpigotPlugin;
import com.illuzionzstudios.tab.controller.TabController;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.*;

/*
 * Stores minecraft skins
 */
public enum Membrane implements Listener, PluginController<SpigotPlugin> {
    INSTANCE;

    /**
     * All our stored skins
     */
    private static final HashSet<CachedSkin> skins = new HashSet();

    /**
     * If the skin is unknown
     */
    private static CachedSkin UNKNOWN_SKIN = new CachedSkin("UNKNOWN", "eyJ0aW1lc3RhbXAiOjE0MTU4NzY5NDkxMTgsInByb2ZpbGVJZCI6IjY4MjVlMWFhNTA2NjQ4MjFhZmYxODA2MGM3NmI0NzY4IiwicHJvZmlsZU5hbWUiOiJDcnVua2xlU3RpY2tzIiwiaXNQdWJsaWMiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80ZGJiMjQyNGE1NTBlMTk0YjY0MDc1NGM0Mzk1YzJhYjM0NjdkZmNlYTQ1NWM4YmVjYjMyYTBmNjZkMmE1In19fQ==", "FG23YbDAsXVtWWh/flnIbYAYMEMdOhnbgMf0R1AFx0mLYylIm4C9ne/UzryD6FoZbjRo5eDL87XHGM3BWglooPaRs2IyP1SjlawAXToloazUn+D5U98r4TyV/sn6vds/LZxZg03SI1k3Tv0c/xEAVacR3ko63KbeFWvQ0SXfDtVDeh/EFzlcEZJvp0Ifr8J/NRNgzoaZzr8uE6G6Ta8Ha1v2gDTQBS1/1iSmhbOQzahEfhTA34R7rIKPfCYdK2tNi1uUXOoMEomjgNwjhemc3cJJy5K2nIcXmwNNLLoJD+ts/PydgTlmAr+TGuxXVd/1DXNkYTq6j20PYDKJnPq7JTyquN3rkiHJPsE+aGxg33gSQUr/e4ztjns9LDh3iWehKYwyfr70BcKIgIokzvQlARjSCNJ/XZ2SHVMnOXftWnkcchO1wDAWVQaSp+Iy9O1gMWZPxsie085ca/Pm8xowH2mTvajF5TNyNQ1z4zbzFHqZS0OcXGn+qOEbuatcfzVIBq7t8MyOeeac/rUIpPeBHuu2DV+58h3SSBVEVUUWVQ3h4mn3nenblxoboyMOug6Azg1TkvjSVgglVcfaXJkxU559KT72Z1ISon3sgAIgPOSJkl2PpKKK2XLwlHvb/c3tab+A7TT6mnokfMOdhWSnLPsUE/wtJ7F4EGzk0shM4T4=");

    private static final int MAX_SKIN_ENTRIES = 5000;

    public final Map<UUID, CachedSkin> displaySkins = Collections.synchronizedMap(new LinkedHashMap<UUID, CachedSkin>(MAX_SKIN_ENTRIES * 2) {
        private static final long serialVersionUID = 1L;

        protected boolean removeEldestEntry(Map.Entry<UUID, CachedSkin> eldest) {
            return size() > MAX_SKIN_ENTRIES;
        }

    });

    /**
     * Sets skin of game profile
     * could be an npc or player
     *
     * @param gameProfile Game profile object
     * @param skin        Skin ID
     */
    public static void setSkin(GameProfile gameProfile, String skin) {
        setSkin(gameProfile, skin, true);
    }

    public static void setSkin(GameProfile gameProfile, String skin, boolean showEntry) {
        CachedSkin cachedSkin = Membrane.INSTANCE.getSkinFromMemory(skin);

        WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromHandle(gameProfile);
        wrappedGameProfile.getProperties().put("skin", new WrappedSignedProperty("textures", cachedSkin.value, cachedSkin.signature));

        if (showEntry) {
            Membrane.INSTANCE.showEntry(wrappedGameProfile.getUUID(), cachedSkin);
        }
    }

    @Override
    public void initialize(SpigotPlugin plugin) {
    }

    @Override
    public void stop(SpigotPlugin plugin) {
    }

    public void showEntry(UUID uuid, CachedSkin skin) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            TabController.INSTANCE.getHandler().addSkin(uuid, skin.value, skin.signature, player);
        }

        displaySkins.put(uuid, skin);
    }

    public void removeEntry(UUID uuid) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            TabController.INSTANCE.getHandler().removeSkin(uuid, player);
            displaySkins.remove(uuid);
        }
    }

    public CachedSkin getSkinFromMemory(String name) {
        for (CachedSkin skin : skins) {
            if (skin.name.equalsIgnoreCase(name)) {
                return skin;
            }
        }
        return null;
    }
}
