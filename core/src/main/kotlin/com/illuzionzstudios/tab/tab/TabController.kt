package com.illuzionzstudios.tab.tab

import com.comphenix.protocol.wrappers.*
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode
import com.illuzionzstudios.mist.Logger
import com.illuzionzstudios.mist.config.YamlConfig
import com.illuzionzstudios.mist.config.serialization.loader.DirectoryLoader
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
import com.illuzionzstudios.tab.skin.SkinLoader
import com.illuzionzstudios.tab.tab.components.Tab
import com.illuzionzstudios.tab.tab.components.column.SimpleColumn
import com.illuzionzstudios.tab.tab.components.column.TabColumn
import com.illuzionzstudios.tab.tab.components.item.TabItem
import com.illuzionzstudios.tab.tab.components.list.TabList
import com.illuzionzstudios.tab.tab.components.list.type.OnlineList
import com.illuzionzstudios.tab.tab.components.loader.TabColumnLoader
import com.illuzionzstudios.tab.tab.components.loader.TabListLoader
import com.illuzionzstudios.tab.tab.components.loader.TabLoader
import com.illuzionzstudios.tab.tab.instance.TabInstance
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.ChatPaginator
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
    val columns: MutableMap<String, TabColumn> = HashMap()
    val lists: MutableMap<String, TabList> = HashMap()

    /**
     * All currently displaying tab instances
     */
    val displayedTabs: MutableMap<UUID, TabInstance> = HashMap()

    override fun initialize(plugin: SpigotPlugin) {
        MinecraftScheduler.get()!!.registerSynchronizationService(this)

        // Make sure skins file exists
        YamlConfig.loadInternalYaml(plugin, "", "skins.yml")
        // Load skins from skins.yml
        SkinController.skins.addAll(SkinLoader().`object`)

        // If files/directories don't exists, create
        YamlConfig.loadInternalYaml(plugin, "columns", "features.yml")
        YamlConfig.loadInternalYaml(plugin, "columns", "player_info.yml")
        YamlConfig.loadInternalYaml(plugin, "columns", "server_info.yml")

        YamlConfig.loadInternalYaml(plugin, "lists", "online_list.yml")

        YamlConfig.loadInternalYaml(plugin, "tabs", "default.yml")

        // Load columns
        DirectoryLoader(TabColumnLoader::class.java, "columns").loaders.forEach {
            columns[it.`object`.id] = it.`object`
        }

        // Load lists
        DirectoryLoader(TabListLoader::class.java, "lists").loaders.forEach {
            lists[it.`object`.id] = it.`object`
        }

        // Load tabs
        DirectoryLoader(TabLoader::class.java, "tabs").loaders.forEach {
            tabs[it.`object`.id] = it.`object`
        }

        // Reshow tabs if needed
        Bukkit.getOnlinePlayers().forEach {
            displayTab(it)
        }
    }

    override fun stop(plugin: SpigotPlugin) {
        MinecraftScheduler.get()!!.dismissSynchronizationService(this)

        // Clear all running tab instances
        displayedTabs.clear()
        // Clear all loaded tabs
        tabs.clear()
        columns.clear()
        lists.clear()
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
                    WrappedChatComponent.fromChatMessage(text)[0]
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
        displayTab(event.player)
    }

    /**
     * Displays the tab to the player
     */
    fun displayTab(player: Player, tab: Tab? = getTab(player)) {
        MinecraftScheduler.get()!!.desynchronize {
            val tab = TabInstance(player, tab!!)

            // Send default list
            val playerInfo = PacketPlayServerPlayerInfo()
            playerInfo.action = EnumWrappers.PlayerInfoAction.ADD_PLAYER
            playerInfo.data = tab.initialList
            playerInfo.sendPacket(player)

            // Now display tab to player
            displayedTabs[player.uniqueId] = tab
            tab.render()
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