package com.illuzionzstudios.tab.tab.components

import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.illuzionzstudios.tab.CustomTab
import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.settings.Settings
import com.illuzionzstudios.tab.tab.Ping
import com.illuzionzstudios.tab.tab.TabController
import com.illuzionzstudios.tab.tab.components.column.TabColumn
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player

/**
 * A tab data object that contains info about the tab and options
 * to display to players. One tab instance is created per player. This is
 * because the information on the tab is usually unique to the player
 * in terms of placeholders etc
 */
class Tab(
    /**
     * ID of this tab
     */
    val id: String
) {

    /**
     * Initial list of player slots. Dummy slots before data is filled in
     */
    val initialList: MutableList<PlayerInfoData> = ArrayList()

    // ----------------------------------------
    // General options
    // ----------------------------------------

    /**
     * Permission needed to view this tab
     */
    var permission: String = ""

    /**
     * The weight of the tab
     */
    var weight: Int = 1

    // ----------------------------------------
    // Column options
    // ----------------------------------------

    /**
     * If to display titles for each column
     */
    var displayTitles: Boolean = true

    /**
     * The minimum width for each text element on the tab
     */
    var elementWidth: Int = 50

    /**
     * A map of the tab columns to the slot they're in
     */
    var columns: MutableMap<Int, TabColumn> = HashMap()

    // ----------------------------------------
    // Header and footer
    // ----------------------------------------

    var header: List<DynamicText> = ArrayList()
    var footer: List<DynamicText> = ArrayList()

    init {
        // Add default player slots
        for (x in 1..columns.size) {
            for (y in 1..columns[0]?.pageElements!!) {
                initialList.add(
                    PlayerInfoData(
                        TabController.getDisplayProfile(x, y),
                        Ping.FIVE.ping,
                        EnumWrappers.NativeGameMode.SURVIVAL,
                        WrappedChatComponent.fromText("Test")
                    )
                )
            }
        }
    }

    /**
     * Render this tab for a player
     */
    fun render(player: Player) {
        // Build the header/footer text
        val headerText = StringBuilder()
        val footerText = StringBuilder()

        // Update text
        header.forEach { head: DynamicText ->
            // PAPI
            if (CustomTab.instance!!.papiEnabled) headerText.append(
                PlaceholderAPI.setPlaceholders(
                    player,
                    head.getVisibleText()
                )
            ) else headerText.append(head.getVisibleText())

            // Last element check
            if (header[header.size - 1] != head) {
                headerText.append("\n")
            }

            // Change for next render
            head.changeText()
        }

        footer.forEach { foot: DynamicText ->
            // PAPI
            if (CustomTab.instance!!.papiEnabled) footerText.append(
                PlaceholderAPI.setPlaceholders(
                    player,
                    foot.getVisibleText()
                )
            ) else footerText.append(foot.getVisibleText())

            // Last element check
            if (footer[footer.size - 1] != foot) {
                footerText.append("\n")
            }

            // Change for next render
            foot.changeText()
        }

        // Render columns
        columns.forEach { (slot, column) ->
            column.render(slot + 1, player, displayTitles, elementWidth)
        }

        // Set the header and footer
        TabController.setHeaderFooter(headerText.toString(), footerText.toString(), player)
    }

}