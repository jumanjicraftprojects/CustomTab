package com.illuzionzstudios.tab.tab.instance

import com.illuzionzstudios.mist.config.locale.MistString
import com.illuzionzstudios.mist.config.locale.mist
import com.illuzionzstudios.mist.scheduler.MinecraftScheduler
import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown
import com.illuzionzstudios.mist.util.TextUtil
import com.illuzionzstudios.tab.CustomTab
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
    private var pageCursor = 0

    /**
     * Interval (in ticks) between switching pages
     */
    var interval: PresetCooldown = PresetCooldown(tab.pageInterval)

    /**
     * Render this tab column for a player
     */
    fun render(slot: Int, displayTitles: Boolean, elementWidth: Int) {
        // Our constant stream of elements to render
        val check: MutableList<TabItem> = tab.render(slot, player, displayTitles).filter { it.getFilter().test(player) }.toMutableList()

        // Update elements to render
        elements = check

        // Offset for some rendering depending if titles are rendered
        val titleOffset = if (displayTitles) 3 else 1

        // This is the sublist for the page we are trying to render
        val sub: MutableList<TabItem?> = ArrayList(
            elements.subList(
                // Start from beginning of page cursor
                0.coerceAtLeast(pageCursor.coerceAtMost(elements.size)),
                // End at page cursor plus elements to render per page
                elements.size.coerceAtMost(pageCursor + (tab.pageElements - (if (displayTitles) 2 else 0) - (if (tab.pageEnabled) 1 else 0)))
            )
        )

        // If titles enabled
        if (displayTitles) {
            // Insert title at top
            sub.add(0, tab.title)

            // Set our minimum tab length
            val width = StringBuilder()
            for (i in 0..elementWidth) {
                width.append(" ")
            }
            // Blank item
            sub.add(1, TextTabItem(width.toString()))
        }

        // Size of each page                  -   Max amount of pages needed  -
        val size = elements.size + 2 + floor((elements.size / tab.pageElements).toDouble())

        // If to show pagination info
        var pageInfo = false
        // Page info
        var currentPage = 1
        var maxPage = 1

        if (size >= tab.pageElements && tab.pageEnabled) {
            // Calculate page length
            val pageDelta: Double = (pageCursor + titleOffset).toDouble() / tab.pageElements + 1
            currentPage = (if (pageDelta < 2) floor(pageDelta) else ceil(pageDelta)).toInt()
            maxPage = ceil((size + 2 * elements.size / tab.pageElements) / tab.pageElements).toInt().coerceAtMost(tab.maxPages)

            // If we can go to next page
            if (interval.isReady && MinecraftScheduler.get()?.hasElapsed(20.0) == true) {
                // Don't update if on a null page
                if (currentPage > maxPage) {
                    // Reset to page 1
                    pageCursor = 0
                    return
                }

                // Go to next page
                pageInfo = true
                interval.reset()
                interval.go()

                instance.avatarCache.row(slot).clear()
            }
            // Pagination text
            val pagesText: MistString = Locale.TAB_PAGE_TEXT.toString("current_page", max(1, currentPage)).toString("max_page", max(1, maxPage))
            sub.add(tab.pageItem ?: TextTabItem(SkinController.UNKNOWN_SKIN, -1, pagesText.toString()))
        }

        // For elements in the sub tab (elements to render)
        for (i in 1..tab.pageElements) {
            // Current element to render
            val element: TabItem? = if (i - 1 < sub.size) sub[i - 1] else null

            // Get raw text to display
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
                // Otherwise, text not defined so set blank
                TabController.setText(slot, i, "", player)

                // Make sure avatar is blank
                if (instance.avatarCache.contains(slot, i)) {
                    TabController.hideAvatar(slot, i, player)
                    instance.avatarCache.remove(slot, i)
                }
            }

            // Increment page cursor for rendering
            if (pageInfo) pageCursor++
        }

        // Update text and title
        tab.title?.getText()?.changeText()
        elements.forEach { element ->
            element.getText().changeText()
        }

        // If page display at bottom
        if (pageInfo) {
            pageCursor -= titleOffset
        }

        // Check if cursor is greater than total pages
        if (pageCursor >= size - titleOffset * elements.size / tab.pageElements) {
            pageCursor = 0
        }
    }

}