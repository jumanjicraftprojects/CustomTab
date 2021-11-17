package com.illuzionzstudios.tab.tab

import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown
import com.illuzionzstudios.tab.CustomTab
import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.tab.components.Tab
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

/**
 * An instance of a tab rendering for a player
 */
class TabInstance(
    val player: Player,
    val tab: Tab
) {

    /**
     * Initial list of player slots. Dummy slots before data is filled in
     */
    val initialList: MutableList<PlayerInfoData> = ArrayList()

    /**
     * Column instances
     */
    val columns: MutableMap<Int, TabColumnInstance> = HashMap()

    /**
     * Cached icon skins
     */
    var avatarCache: Table<Int, Int, UUID> = HashBasedTable.create()

    private val refresh: PresetCooldown = PresetCooldown(100)

    init {
        // Add default player slots
        for (x in 1..tab.columns.size) {
            for (y in 1..tab.columns[1]?.pageElements!!) {
                initialList.add(
                    PlayerInfoData(
                        TabController.getDisplayProfile(x, y),
                        Ping.FIVE.ping,
                        EnumWrappers.NativeGameMode.SURVIVAL,
                        WrappedChatComponent.fromText("")
                    )
                )
            }
        }

        for (column in tab.columns) {
            this.columns[column.key] = TabColumnInstance(player, this, column.value)
        }

        refresh.go()
    }

    /**
     * Render this tab for a player
     */
    fun render() {
        // Build the header/footer text
        val headerText = StringBuilder()
        val footerText = StringBuilder()

        // Update text
        tab.header.forEach { head: DynamicText ->
            // PAPI
            if (CustomTab.instance!!.papiEnabled) headerText.append(
                PlaceholderAPI.setPlaceholders(
                    player,
                    head.getVisibleText()
                )
            ) else headerText.append(head.getVisibleText())

            // Last element check
            if (tab.header[tab.header.size - 1] != head) {
                headerText.append("\n")
            }

            // Change for next render
            head.changeText()
        }

        tab.footer.forEach { foot: DynamicText ->
            // PAPI
            if (CustomTab.instance!!.papiEnabled) footerText.append(
                PlaceholderAPI.setPlaceholders(
                    player,
                    foot.getVisibleText()
                )
            ) else footerText.append(foot.getVisibleText())

            // Last element check
            if (tab.footer[tab.footer.size - 1] != foot) {
                footerText.append("\n")
            }

            // Change for next render
            foot.changeText()
        }

//        if (refresh.isReady) {
//            // Add skins for players
//            for (player in Bukkit.getOnlinePlayers()) {
//                // Make sure player exists
//                if (player == null) continue
//                TabController.addSkin(player, this.player)
//                if (this.player != player) {
//                    TabController.addSkin(this.player, player)
//                }
//            }
//
//            refresh.reset()
//            refresh.go()
//        }

        // Render columns
        columns.forEach { (slot, column) ->
            column.render(slot, tab.displayTitles, tab.elementWidth)
        }

        // Set the header and footer
        TabController.setHeaderFooter(headerText.toString(), footerText.toString(), player)
    }

}