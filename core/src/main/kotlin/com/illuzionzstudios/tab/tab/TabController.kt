package com.illuzionzstudios.tab.tab

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.*
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode
import com.illuzionzstudios.mist.Logger
import com.illuzionzstudios.mist.controller.PluginController
import com.illuzionzstudios.mist.plugin.SpigotPlugin
import com.illuzionzstudios.mist.scheduler.MinecraftScheduler
import com.illuzionzstudios.mist.scheduler.rate.Async
import com.illuzionzstudios.mist.scheduler.rate.Rate
import com.illuzionzstudios.mist.util.TextUtil
import com.illuzionzstudios.tab.model.FrameText
import com.illuzionzstudios.tab.packet.PacketPlayServerPlayerInfo
import com.illuzionzstudios.tab.packet.PacketPlayerListHeaderFooter
import com.illuzionzstudios.tab.settings.Settings
import com.illuzionzstudios.tab.skin.CachedSkin
import com.illuzionzstudios.tab.skin.SkinController
import com.illuzionzstudios.tab.tab.components.Tab
import com.illuzionzstudios.tab.tab.components.column.SimpleColumn
import com.illuzionzstudios.tab.tab.components.column.TabColumn
import com.illuzionzstudios.tab.tab.components.list.type.OnlineList
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.nio.ByteBuffer
import java.util.*

/**
 * Controller for handling player tab instances and manipulating the tab
 */
object TabController : PluginController {

    /**
     * Slots for displaying in tab
     */
    const val DISPLAY_SLOT: Char = '\u0000'
    const val SKIN_SLOT: Char = '\u0001'

    /**
     * All loaded tab instances
     */
    val tabs: MutableMap<String, Tab> = HashMap()

    /**
     * All currently displaying tab instances
     */
    val displayedTabs: MutableMap<UUID, TabInstance> = HashMap()

    override fun initialize(plugin: SpigotPlugin) {
        MinecraftScheduler.get()!!.registerSynchronizationService(this)

//        ProtocolLibrary.getProtocolManager().addPacketListener(LegacyBlocker(plugin))

        // Load tabs
        val tab = Tab("default")
        val column1: TabColumn = OnlineList("online_list")
        val column2: TabColumn = SimpleColumn(
            "column", listOf(
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element"),
                FrameText(15, "&cTest Element", "&4Test Element")
            ).toMutableList()
        )
        column1.title = FrameText(-1, "&a&lOnline")
        column2.title = FrameText(15, "&c&lTest Title", "&4&lTest Title")

        tab.columns[1] = column1
        tab.columns[2] = column2
        tab.columns[3] = column1
        tab.columns[4] = column2
        tab.header = listOf(FrameText(15, "&cTest Element", "&4Test Element"))
        tabs["default"] = tab

        object : BukkitRunnable() {
            override fun run() {
                displayedTabs.forEach { (uuid, tab) ->
                    tab.render()
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 1)
    }

    override fun stop(plugin: SpigotPlugin) {
        MinecraftScheduler.get()!!.dismissSynchronizationService(this)
    }

    /**
     * Set the header and footer of the tab
     *
     * @param header  Header message
     * @param footer  Footer message
     * @param players Players to send to
     */
    fun setHeaderFooter(header: String?, footer: String?, vararg players: Player?) {
        MinecraftScheduler.get()?.desynchronize {
            val playerListHeaderFooter = PacketPlayerListHeaderFooter()
            playerListHeaderFooter.header = WrappedChatComponent.fromText(TextUtil.formatText(header))
            playerListHeaderFooter.footer = WrappedChatComponent.fromText(TextUtil.formatText(footer))
            playerListHeaderFooter.sendPacket(*players)
        }
    }

    /**
     * Set text in the tab at x and y for players
     *
     * @param x       X to set
     * @param y       Y to set
     * @param text    Text to set
     * @param players Players to set for
     */
    fun setText(x: Int, y: Int, text: String?, vararg players: Player?) {
        MinecraftScheduler.get()?.desynchronize {
            val data: MutableList<PlayerInfoData> = ArrayList()
            val playerInfo = PacketPlayServerPlayerInfo()
            playerInfo.action = EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME
            data.add(
                PlayerInfoData(
                    getDisplayProfile(x, y),
                    Ping.FIVE.ping,
                    NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText(text)
                )
            )
            playerInfo.data = data
            playerInfo.sendPacket(*players)
        }
    }

    /**
     * Set the ping at a certain slot
     *
     * @param x       X to set
     * @param y       Y to set
     * @param latency Latency to set
     * @param players Players to set for
     */
    fun setPing(x: Int, y: Int, latency: Ping, vararg players: Player?) {
        MinecraftScheduler.get()?.desynchronize {
            val data: MutableList<PlayerInfoData> = ArrayList()
            val playerInfo = PacketPlayServerPlayerInfo()
            playerInfo.action = EnumWrappers.PlayerInfoAction.UPDATE_LATENCY
            data.add(
                PlayerInfoData(
                    getDisplayProfile(x, y),
                    latency.ping,
                    NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText("")
                )
            )
            playerInfo.data = data
            playerInfo.sendPacket(*players)
        }
    }

    /**
     * Set perceived gamemode at x and y
     *
     * @param x        X to set
     * @param y        Y to set
     * @param gameMode
     * @param players
     */
    fun setGameMode(x: Int, y: Int, gameMode: NativeGameMode?, vararg players: Player?) {
        MinecraftScheduler.get()?.desynchronize {
            val data: MutableList<PlayerInfoData> = ArrayList()
            val playerInfo = PacketPlayServerPlayerInfo()
            playerInfo.action = EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE
            data.add(
                PlayerInfoData(
                    getDisplayProfile(x, y),
                    Ping.FIVE.ping,
                    gameMode,
                    WrappedChatComponent.fromText("")
                )
            )
            playerInfo.data = data
            playerInfo.sendPacket(*players)
        }
    }

    fun setGameMode(player: Player?, gameMode: NativeGameMode?, vararg players: Player?) {
        MinecraftScheduler.get()?.desynchronize {
            val data: MutableList<PlayerInfoData> = ArrayList()
            val playerInfo = PacketPlayServerPlayerInfo()
            playerInfo.action = EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE
            data.add(
                PlayerInfoData(
                    WrappedGameProfile.fromPlayer(player),
                    Ping.FIVE.ping,
                    gameMode,
                    WrappedChatComponent.fromText("")
                )
            )
            playerInfo.data = data
            playerInfo.sendPacket(*players)
        }
    }

    fun hideAvatar(x: Int, y: Int, vararg players: Player?) {
        SkinController.setDefaultAvatar(x, y, *players)
    }

    fun setAvatar(x: Int, y: Int, player: Player?, vararg players: Player?) {
        SkinController.setAvatar(x, y, player!!, *players)
    }

    fun addSkin(player: Player?, vararg players: Player?) {
        SkinController.addSkin(player!!, *players)
    }

    fun addSkins(skins: MutableMap<UUID?, CachedSkin?>, vararg players: Player?) {
        val data: MutableList<PlayerInfoData> = ArrayList()
        val playerInfo = PacketPlayServerPlayerInfo()
        playerInfo.action = EnumWrappers.PlayerInfoAction.ADD_PLAYER

        skins.forEach { (uuid: UUID?, skin: CachedSkin?) ->
            var gameProfile: WrappedGameProfile = getSkinProfile(uuid)
            val available = Bukkit.getPlayer(uuid!!)

            if (available != null) {
                gameProfile = WrappedGameProfile(uuid, WrappedGameProfile.fromPlayer(available).name)
            }

            gameProfile.properties.removeAll("textures")
            gameProfile.properties.put(
                "textures",
                WrappedSignedProperty(
                    "textures",
                    skin?.value,
                    skin?.signature
                )
            )
            data.add(
                PlayerInfoData(
                    gameProfile,
                    Ping.NONE.ping,
                    NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText("")
                )
            )
        }
        playerInfo.data = data

        SkinController.removeSkins(skins.keys, *players)
        playerInfo.sendPacket(*players)
    }

    /**
     * Remove a slot from the tab
     */
    fun removeSlot(x: Int, y: Int, vararg players: Player?) {
        val data: MutableList<PlayerInfoData> =
            ArrayList()
        val playerInfo = PacketPlayServerPlayerInfo()
        val gameProfile: WrappedGameProfile = getDisplayProfile(x, y)
        playerInfo.action = EnumWrappers.PlayerInfoAction.REMOVE_PLAYER
        data.add(
            PlayerInfoData(
                gameProfile,
                Ping.NONE.ping,
                NativeGameMode.SURVIVAL,
                WrappedChatComponent.fromText("")
            )
        )
        playerInfo.data = data
        playerInfo.sendPacket(*players)
    }

    /**
     * Remove a skin for a player
     */
    fun removeSkin(player: Player, vararg players: Player?) {
        SkinController.removeSkin(player.uniqueId, *players)
    }

    /**
     * Set the name on a profile for a UUID
     */
    fun setName(uuid: UUID?, name: String?, vararg players: Player?) {
        val data: MutableList<PlayerInfoData> =
            ArrayList()
        val playerInfo = PacketPlayServerPlayerInfo()
        playerInfo.action = EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME
        data.add(
            PlayerInfoData(
                getSkinProfile(uuid),
                Ping.FIVE.ping,
                NativeGameMode.SURVIVAL,
                WrappedChatComponent.fromText(name)
            )
        )
        playerInfo.data = data
        playerInfo.sendPacket(*players)
    }

    /**
     * When a player changes gamemode update on tab
     */
    @EventHandler
    fun onGamemodeUpdate(event: PlayerGameModeChangeEvent) {
        for (player in Bukkit.getOnlinePlayers()) {
            setGameMode(event.player, NativeGameMode.fromBukkit(event.newGameMode), player)
        }
    }

    /**
     * Handles player joining and showing tab instance
     */
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        MinecraftScheduler.get()!!.desynchronize {
            val tab = TabInstance(event.player, getTab(event.player)!!)

            // Send default list
            val playerInfo = PacketPlayServerPlayerInfo()
            playerInfo.action = EnumWrappers.PlayerInfoAction.ADD_PLAYER
            playerInfo.data = tab.initialList
            playerInfo.sendPacket(event.player)

            // Remove all players from the tab
            for (x in 1..tab.columns.size) {
                tab.avatarCache.remove(x, event.player.uniqueId)
                for (y in 1..tab.tab.columns[1]?.pageElements!!) {
                    hideAvatar(x, y, event.player)
                }
            }

            // Add skins for players
//            for (player in Bukkit.getOnlinePlayers()) {
//                // Make sure player exists
//                if (player == null) continue
//                addSkin(player, event.player)
//                if (event.player != player) {
//                    addSkin(event.player, player)
//                }
//            }

            // Now display tab to player
            displayedTabs[event.player.uniqueId] = tab
            tab.render(true)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        displayedTabs.remove(event.player.uniqueId)
    }

    /**
     * Renders all tab instances
     */
    @Async(rate = Rate.TICK)
    fun renderTabs() {
        Logger.debug("Ticking")
        displayedTabs.forEach { (uuid, tab) ->
            val player: Player? = Bukkit.getPlayer(uuid)
            if (player != null)
                tab.render()
        }
    }

    /**
     * Gets the highest tab the player has
     *
     * @param player The player to check
     */
    fun getTab(player: Player): Tab? {
        var highest: Tab? = null
        for (group in tabs.values) {
            // Has permission for tab
            if (player.hasPermission(group.permission) || group.permission.trim().equals("", true)) {
                val compare = (highest?.weight ?: 0).compareTo(group.weight)
                if (compare < 0) {
                    highest = group
                }
            }
        }
        return highest ?: tabs[Settings.DEFAULT_TAB.string]
    }

    /**
     * Get game profile at x and y in tab
     *
     * @param x X of player
     * @param y Y of player
     */
    fun getDisplayProfile(x: Int, y: Int): WrappedGameProfile {
        val id = x * 100 + y
        return WrappedGameProfile(
            UUID.nameUUIDFromBytes(ByteBuffer.allocate(16).putInt(id).array()), String.format("%c%d", DISPLAY_SLOT, id)
        )
    }

    /**
     * Get the game profile with texture data for a UUID
     */
    fun getSkinProfile(uuid: UUID?): WrappedGameProfile {
        return WrappedGameProfile(
            uuid, String.format("\u00A7%c", SKIN_SLOT)
        )
    }

}