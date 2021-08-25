package com.illuzionzstudios.tab;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.*;
import com.illuzionzstudios.tab.packet.AbstractPacket;
import com.illuzionzstudios.tab.packet.WrapperPlayServerPlayerInfo;
import com.illuzionzstudios.tab.ping.Latency;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * This handles caching and setting skins on that tab. Since
 * we need to use game profiles we need to use different
 * NMS code.
 */
public interface NMSHandler {

    /**
     * Set the avatar for a player
     *
     * @param x on tab
     * @param y on tab
     * @param player Player avatar to set
     * @param players Player's to display to
     */
    void setAvatar(int x, int y, Player player, Player... players);

    default void setAvatar(int x, int y, GameProfile profile, Player... players) {
        Property property = profile.getProperties().get("textures").iterator().next();

        this.setAvatar(x, y, property.getValue(), property.getSignature(), players);
    }

    default void setAvatar(int x, int y, String value, String signature, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        WrappedGameProfile gameProfile = this.getDisplayProfile(x, y);

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        data.add(
                new PlayerInfoData(
                        gameProfile,
                        Latency.FIVE.ping,
                        EnumWrappers.NativeGameMode.SURVIVAL,
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
                        EnumWrappers.NativeGameMode.SURVIVAL,
                        WrappedChatComponent.fromText("")
                )
        );

        playerInfo.setData(data);

        this.sendUnfilteredPacket(playerInfo, players);
    }

    void addSkin(Player player, Player... players);

    default void addSkin(UUID uuid, GameProfile profile, Player... players) {
        Iterator<Property> iterator = profile.getProperties().get("textures").iterator();
        // Make sure is valid
        if (iterator.hasNext()) {
            Property property = iterator.next();
            this.addSkin(uuid, property.getValue(), property.getSignature(), players);
        }
    }

    default void addSkin(UUID uuid, String value, String signature, Player... players) {
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
                        EnumWrappers.NativeGameMode.SURVIVAL,
                        WrappedChatComponent.fromText("")
                )
        );

        playerInfo.setData(data);

        this.removeSkin(uuid, players);
        this.sendUnfilteredPacket(playerInfo, players);
    }

    default void removeSkins(Collection<UUID> uuids, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();

        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        playerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        for (UUID uuid : uuids) {
            WrappedGameProfile gameProfile = this.getSkinProfile(uuid);

            data.add(
                    new PlayerInfoData(
                            gameProfile,
                            Latency.NONE.ping,
                            EnumWrappers.NativeGameMode.SURVIVAL,
                            WrappedChatComponent.fromText("")
                    )
            );
        }

        playerInfo.setData(data);
        this.sendUnfilteredPacket(playerInfo, players);
    }

    default void removeSkin(UUID uuid, Player... players) {
        List<PlayerInfoData> data = new ArrayList<>();
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        WrappedGameProfile gameProfile = this.getSkinProfile(uuid);

        playerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

        data.add(
                new PlayerInfoData(
                        gameProfile,
                        Latency.NONE.ping,
                        EnumWrappers.NativeGameMode.SURVIVAL,
                        WrappedChatComponent.fromText("")
                )
        );

        playerInfo.setData(data);

        this.sendUnfilteredPacket(playerInfo, players);
    }

    default void sendUnfilteredPacket(AbstractPacket packet, Player... players) {
        try {
            for (Player player : players) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet.getHandle(), false);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get game profile at x and y in tab
     *
     * @param x X of player
     * @param y Y of player
     */
    default WrappedGameProfile getDisplayProfile(int x, int y) {
        int id = x * 100 + y;

        return new WrappedGameProfile(
                UUID.nameUUIDFromBytes(ByteBuffer.allocate(16).putInt(id).array()),
                String.format("%c%d", '\u0000', id)
        );
    }

    default WrappedGameProfile getSkinProfile(UUID uuid) {
        return new WrappedGameProfile(
                uuid,
                String.format("\u00A7%c", '\u0001')
        );
    }

}
