package com.illuzionzstudios.tab.tab.instance

import com.illuzionzstudios.mist.Logger
import com.illuzionzstudios.mist.config.locale.MistString
import com.illuzionzstudios.mist.config.locale.mist
import com.illuzionzstudios.mist.debug.PerformanceTimer
import com.illuzionzstudios.mist.scheduler.MinecraftScheduler
import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown
import com.illuzionzstudios.mist.util.TextUtil
import com.illuzionzstudios.tab.CustomTab
import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.model.FrameText
import com.illuzionzstudios.tab.settings.Locale
import com.illuzionzstudios.tab.skin.SkinController
import com.illuzionzstudios.tab.tab.Ping
import com.illuzionzstudios.tab.tab.TabController
import com.illuzionzstudios.tab.tab.components.column.TabColumn
import com.illuzionzstudios.tab.tab.components.item.PlayerTabItem
import com.illuzionzstudios.tab.tab.components.item.TabItem
import com.illuzionzstudios.tab.tab.components.item.TextTabItem
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

/**
 * An instance of a tab column
 */
class TabColumnInstance(
    val player: Player,
    val instance: TabInstance,
    val tab: TabColumn
) {
    /**
     * Elements to render
     */
    var elements: MutableList<TabItem> = ArrayList()

    /**
     * Cursor between pages
     */
    var pageCursor = 0

    /**
     * Interval (in ticks) between switching pages
     */
    var interval: PresetCooldown = PresetCooldown(tab.pageInterval)

    /**
     * Render this tab column for a player
     *
     * @param slot The slot of the column to render
     * @param player The player to render column for
     */
    fun render(slot: Int, displayTitles: Boolean, elementWidth: Int) {
        // Check if to refresh
        val check: MutableList<TabItem> = tab.render(slot, player, displayTitles).filter { it.getFilter().test(player) }.toMutableList()
        var reloadSkins = false

        // If no elements, try get from to render
        if (elements.isEmpty()) {
            elements = check
        } else {
            elements = check
            // If current elements differ to new elements to render, re render elements
            if (check.size != elements.size) {
                reloadSkins = true
            }
        }

        // Our sub array, or our page
        val sub: MutableList<TabItem?> = ArrayList(
            elements.subList(
                0.coerceAtLeast(pageCursor.coerceAtMost(elements.size)),
                elements.size.coerceAtMost(pageCursor + (tab.pageElements - 3))
            )
        )

        // If titles enabled
        if (displayTitles) {
            // Center title
            sub.add(0, tab.title)

            // Set our minimum tab length
            val width = StringBuilder()
            for (i in 0 until elementWidth) {
                width.append(" ")
            }
            sub.add(1, TextTabItem(width.toString()))
        }

        val size = elements.size + 2 + floor((elements.size / tab.pageElements).toDouble())

        // If to show pagination info
        var pageInfo = false
        var currentPage = 1
        var maxPage = 1

        if (size >= tab.pageElements - 1 && tab.pageEnabled) {
            // Calculate page length
            val pageDelta: Double = (pageCursor + if (displayTitles) 3 else 1).toDouble() / tab.pageElements + 1
            currentPage = (if (pageDelta < 2) floor(pageDelta) else ceil(pageDelta)).toInt()
            maxPage = ceil((size + 2 * elements.size / tab.pageElements) / tab.pageElements).toInt().coerceAtMost(tab.maxPages)

            // If we can go to next page
            if (interval.isReady && MinecraftScheduler.get()?.hasElapsed(20.0) == true) {
                // Don't update if on a null page
                if (currentPage > maxPage) {
                    // Reset to page 1
                    elements = ArrayList()
                    pageCursor = 0
                    return
                }

                // Go to next page
                elements = check
                pageInfo = true
                interval.reset()
                interval.go()

                instance.avatarCache.row(slot).clear()
            }
            // Pagination text
            val pagesText: MistString = Locale.TAB_PAGE_TEXT.toString("current_page", max(1, currentPage)).toString("max_page", max(1, maxPage))
            sub.add(tab.pageItem ?: TextTabItem(SkinController.UNKNOWN_SKIN, -1, pagesText.toString()))
        }

        // For elements in the sub tab
        for (i in 1..tab.pageElements) {
            val element: TabItem? = if (i - 1 < sub.size) sub[i - 1] else null

            // Send update packet //
            var text = TextUtil.formatText(element?.getText()?.getVisibleText() ?: "")

            // Global placeholders
            text = text.mist.toString("current_page", max(1, currentPage)).toString("max_page", max(1, maxPage)).toString()

            // Set placeholders
            if (CustomTab.instance!!.papiEnabled) {
                text = if (element is PlayerTabItem) {
                    PlaceholderAPI.setPlaceholders(element.player, text)
                } else PlaceholderAPI.setPlaceholders(player, text)
            }

            // Trim text
            if (ChatColor.stripColor(text)?.length!! > elementWidth) {
                // Check for colour code
                var previousCode = false
                // If the text is currently bold
                var isBold = false
                // Total number of bold characters
                var boldChars = 0
                val chars = text.toCharArray()
                for (j in 0 until elementWidth) {
                    val c = chars[j]
                    if (c == '\u00a7') {
                        previousCode = true
                    } else if (previousCode) {
                        previousCode = false
                        isBold = c == 'l' || c == 'L'
                    } else {
                        boldChars += if (isBold) 1 else 0
                    }
                }

                // Bold chars count as 2 spaces
                text = text.substring(0, elementWidth - boldChars / 8 - 4)
            }

            // Check all elements with text
            if (i - 1 < sub.size) {
                if (element?.isCenter() == true) {
                    text = TextUtil.getCenteredString(text, elementWidth)
                }
                // Set text in that slot as our final text
                TabController.setText(slot, i, text, player)
                TabController.setPing(slot, i, element?.getPing() ?: Ping.FIVE, player)

                // Only set if custom skin
                if ((!instance.avatarCache.contains(slot, i) && element?.getSkin() != null) || (element?.getSkin() != instance.avatarCache.get(slot, i))) {
                    SkinController.setAvatar(slot, i, element?.getSkin() ?: SkinController.UNKNOWN_SKIN, player)
                    instance.avatarCache.put(slot, i, element?.getSkin() ?: SkinController.UNKNOWN_SKIN)
                }
            } else {
                // Otherwise text not defined so set blank
                TabController.setText(slot, i, "", player)

                // Make sure avatar is blank
                if (instance.avatarCache.contains(slot, i)) {
                    TabController.hideAvatar(slot, i, player)
                    instance.avatarCache.remove(slot, i)
                }
            }

            // Go to next page if applicable
            if (pageInfo) pageCursor++
        }

        // Update text and title
        tab.title?.getText()?.changeText()
        elements.forEach { element ->
            element.getText().changeText()
        }

        // If page display at bottom
        if (pageInfo) {
            pageCursor -= if (displayTitles) 3 else 1
        }

        // Check if cursor is greater than applicable
        // number of pages

        // Check if cursor is greater than applicable
        // number of pages
        if (pageCursor >= size - (if (displayTitles) 3 else 1) * elements.size / tab.pageElements) {
            // Reset to page 1
            elements = ArrayList()
            pageCursor = 0
        }

        if (reloadSkins) {
            instance.avatarCache.row(slot).clear()
        }
    }

}