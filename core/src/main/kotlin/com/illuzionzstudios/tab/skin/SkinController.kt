package com.illuzionzstudios.tab.skin

import com.comphenix.protocol.wrappers.*
import com.illuzionzstudios.mist.controller.PluginController
import com.illuzionzstudios.mist.plugin.SpigotPlugin
import com.illuzionzstudios.mist.scheduler.MinecraftScheduler
import com.illuzionzstudios.tab.packet.PacketPlayServerPlayerInfo
import com.illuzionzstudios.tab.tab.Ping
import com.illuzionzstudios.tab.tab.TabController
import com.mojang.authlib.GameProfile
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

/**
 * Handles our skins for the player and tabs
 */
object SkinController : PluginController {

    /**
     * All our stored skins
     */
    private val skins: HashSet<CachedSkin?> = HashSet()

    /**
     * Default skin for if none is supplied
     */
    private val UNKNOWN_SKIN: CachedSkin = CachedSkin(
        "unknown",
        "eyJ0aW1lc3RhbXAiOjE0MTU4NzY5NDkxMTgsInByb2ZpbGVJZCI6IjY4MjVlMWFhNTA2NjQ4MjFhZmYxODA2MGM3NmI0NzY4IiwicHJvZmlsZU5hbWUiOiJDcnVua2xlU3RpY2tzIiwiaXNQdWJsaWMiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80ZGJiMjQyNGE1NTBlMTk0YjY0MDc1NGM0Mzk1YzJhYjM0NjdkZmNlYTQ1NWM4YmVjYjMyYTBmNjZkMmE1In19fQ==",
        "FG23YbDAsXVtWWh/flnIbYAYMEMdOhnbgMf0R1AFx0mLYylIm4C9ne/UzryD6FoZbjRo5eDL87XHGM3BWglooPaRs2IyP1SjlawAXToloazUn+D5U98r4TyV/sn6vds/LZxZg03SI1k3Tv0c/xEAVacR3ko63KbeFWvQ0SXfDtVDeh/EFzlcEZJvp0Ifr8J/NRNgzoaZzr8uE6G6Ta8Ha1v2gDTQBS1/1iSmhbOQzahEfhTA34R7rIKPfCYdK2tNi1uUXOoMEomjgNwjhemc3cJJy5K2nIcXmwNNLLoJD+ts/PydgTlmAr+TGuxXVd/1DXNkYTq6j20PYDKJnPq7JTyquN3rkiHJPsE+aGxg33gSQUr/e4ztjns9LDh3iWehKYwyfr70BcKIgIokzvQlARjSCNJ/XZ2SHVMnOXftWnkcchO1wDAWVQaSp+Iy9O1gMWZPxsie085ca/Pm8xowH2mTvajF5TNyNQ1z4zbzFHqZS0OcXGn+qOEbuatcfzVIBq7t8MyOeeac/rUIpPeBHuu2DV+58h3SSBVEVUUWVQ3h4mn3nenblxoboyMOug6Azg1TkvjSVgglVcfaXJkxU559KT72Z1ISon3sgAIgPOSJkl2PpKKK2XLwlHvb/c3tab+A7TT6mnokfMOdhWSnLPsUE/wtJ7F4EGzk0shM4T4="
    )

    private const val MAX_SKIN_ENTRIES = 5000

    /**
     * A map of skins to a UUID
     */
    val displaySkins: MutableMap<UUID?, CachedSkin?>? =
        Collections.synchronizedMap(object : LinkedHashMap<UUID?, CachedSkin?>(MAX_SKIN_ENTRIES * 2) {
            override fun removeEldestEntry(eldest: Map.Entry<UUID?, CachedSkin?>): Boolean {
                return size > MAX_SKIN_ENTRIES
            }
        })

    override fun initialize(plugin: SpigotPlugin) {
        skins.add(UNKNOWN_SKIN)
    }

    override fun stop(plugin: SpigotPlugin) {
        skins.clear()
    }

    /**
     * Sets skin of game profile
     * could be an npc or player.
     * Mostly useful for setting a fake player in
     * tab for custom images
     *
     * @param gameProfile Game profile object
     * @param skin        Skin ID
     * @param showEntry   If to show entry in tab
     */
    @JvmOverloads
    fun setSkin(gameProfile: WrappedGameProfile?, skin: String?, showEntry: Boolean = true) {
        MinecraftScheduler.get()?.desynchronize {
            val cachedSkin: CachedSkin = getSkinFromMemory(skin)!!
            gameProfile?.properties?.put(
                "skin",
                WrappedSignedProperty("textures", cachedSkin.value, cachedSkin.signature)
            )
            if (showEntry) showEntry(gameProfile?.uuid, cachedSkin)
        }
    }

    /**
     * Show the skin entry on the tab from a cached skin
     */
    private fun showEntry(uuid: UUID?, skin: CachedSkin) {
        for (player in Bukkit.getOnlinePlayers()) {
            addSkin(uuid, skin.value, skin.signature, player)
        }
        displaySkins!![uuid] = skin
    }

    /**
     * Remove a skin entry for a player
     */
    fun removeEntry(uuid: UUID?) {
        for (player in Bukkit.getOnlinePlayers()) {
            removeSkin(uuid, player)
            displaySkins!!.remove(uuid)
        }
    }

    /**
     * Retrieve a cached skin by the name of the skin
     */
    fun getSkinFromMemory(name: String?): CachedSkin? {
        for (skin in skins) {
            if (skin?.name.equals(name, true)) {
                return skin
            }
        }
        return null
    }

    fun setDefaultAvatar(x: Int, y: Int, vararg players: Player?) {
        setAvatar(x, y, UNKNOWN_SKIN.value, UNKNOWN_SKIN.signature, *players)
    }

    /**
     * Set the skin avatar at a slot to a profiles skin
     */
    fun setAvatar(x: Int, y: Int, profile: WrappedGameProfile, vararg players: Player?) {
        MinecraftScheduler.get()?.desynchronize {
            val property = profile.properties["textures"].iterator().next()
            this.setAvatar(x, y, property.value, property.signature, *players)
        }
    }

    /**
     * Set the skin avatar at a slot to a skin texture
     */
    fun setAvatar(x: Int, y: Int, value: String?, signature: String?, vararg players: Player?) {
        MinecraftScheduler.get()?.desynchronize {
            // Clear old avatar
            val data: MutableList<PlayerInfoData> = ArrayList()
            val playerInfo = PacketPlayServerPlayerInfo()
            val gameProfile: WrappedGameProfile = TabController.getDisplayProfile(x, y)
            playerInfo.action = EnumWrappers.PlayerInfoAction.REMOVE_PLAYER
            data.add(
                PlayerInfoData(
                    gameProfile,
                    Ping.FIVE.ping,
                    EnumWrappers.NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText("")
                )
            )
            playerInfo.data = data
            playerInfo.sendPacket(*players)

            data.clear()

            // Insert new avatar
            playerInfo.action = EnumWrappers.PlayerInfoAction.ADD_PLAYER
            gameProfile.properties.removeAll("textures")
            gameProfile.properties.put(
                "textures",
                WrappedSignedProperty(
                    "textures",
                    value,
                    signature
                )
            )
            data.add(
                PlayerInfoData(
                    gameProfile,
                    Ping.FIVE.ping,
                    EnumWrappers.NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText("")
                )
            )
            playerInfo.data = data
            playerInfo.sendPacket(*players)
        }
    }

    /**
     * Set the skin avatar at a slot to a players head texture
     */
    fun setAvatar(x: Int, y: Int, player: Player, vararg players: Player?) {
        setAvatar(x, y, WrappedGameProfile.fromPlayer(player), *players)
    }

    /**
     * Assign the skin instance of a player
     */
    fun addSkin(player: Player, vararg players: Player?) {
        this.addSkin(player.uniqueId, WrappedGameProfile.fromPlayer(player), *players)
    }

    /**
     * Assigns a skin of a profile to a UUID
     */
    fun addSkin(uuid: UUID?, profile: WrappedGameProfile, vararg players: Player?) {
        MinecraftScheduler.get()?.desynchronize {
            val iterator: Iterator<WrappedSignedProperty> = profile.properties["textures"].iterator()
            // Make sure is valid
            if (iterator.hasNext()) {
                val property = iterator.next()
                this.addSkin(uuid, property.value, property.signature, *players)
            }
        }
    }

    /**
     * Assigns a skin of a texture to a UUID
     */
    fun addSkin(uuid: UUID?, value: String?, signature: String?, vararg players: Player?) {
        MinecraftScheduler.get()?.desynchronize {
            val data: MutableList<PlayerInfoData> = ArrayList()
            val playerInfo = PacketPlayServerPlayerInfo()
            var gameProfile: WrappedGameProfile = TabController.getSkinProfile(uuid)
            val available = Bukkit.getPlayer(uuid!!)
            if (available != null) {
                gameProfile = WrappedGameProfile(uuid, WrappedGameProfile.fromPlayer(available).name)
            }
            playerInfo.action = EnumWrappers.PlayerInfoAction.ADD_PLAYER
            gameProfile.properties.removeAll("textures")
            gameProfile.properties.put(
                "textures",
                WrappedSignedProperty(
                    "textures",
                    value,
                    signature
                )
            )
            data.add(
                PlayerInfoData(
                    gameProfile,
                    Ping.NONE.ping,
                    EnumWrappers.NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText("")
                )
            )
            playerInfo.data = data
            removeSkin(uuid, *players)
            playerInfo.sendPacket(*players)
        }
    }

    /**
     * Remove the skin instances on a collection of UUIDs
     */
    fun removeSkins(uuids: Collection<UUID?>, vararg players: Player?) {
        MinecraftScheduler.get()?.desynchronize {
            val data: MutableList<PlayerInfoData> = ArrayList()
            val playerInfo = PacketPlayServerPlayerInfo()
            playerInfo.action = EnumWrappers.PlayerInfoAction.REMOVE_PLAYER

            for (uuid in uuids) {
                val gameProfile: WrappedGameProfile = TabController.getSkinProfile(uuid)
                data.add(
                    PlayerInfoData(
                        gameProfile,
                        Ping.NONE.ping,
                        EnumWrappers.NativeGameMode.SURVIVAL,
                        WrappedChatComponent.fromText("")
                    )
                )
            }

            playerInfo.data = data
            playerInfo.sendPacket(*players)
        }
    }

    /**
     * Remove the skin from a UUID
     */
    fun removeSkin(uuid: UUID?, vararg players: Player?) {
        MinecraftScheduler.get()?.desynchronize {
            val data: MutableList<PlayerInfoData> = ArrayList()
            val playerInfo = PacketPlayServerPlayerInfo()
            val gameProfile: WrappedGameProfile = TabController.getSkinProfile(uuid)
            playerInfo.action = EnumWrappers.PlayerInfoAction.REMOVE_PLAYER
            data.add(
                PlayerInfoData(
                    gameProfile,
                    Ping.NONE.ping,
                    EnumWrappers.NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText("")
                )
            )
            playerInfo.data = data
            playerInfo.sendPacket(*players)
        }
    }
}