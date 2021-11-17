package com.illuzionzstudios.tab.tab

import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown
import com.illuzionzstudios.mist.util.TextUtil
import com.illuzionzstudios.tab.CustomTab
import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.model.FrameText
import com.illuzionzstudios.tab.settings.Locale
import com.illuzionzstudios.tab.tab.components.column.TabColumn
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player
import kotlin.math.ceil
import kotlin.math.floor

/**
 * An instance of a tab column
 */
class TabColumnInstance(
    val player: Player,
    val instance: TabInstance,
    val tab: TabColumn
) {

    var elementCooldown: PresetCooldown = PresetCooldown(1)

    /**
     * Elements to render
     */
    var elements: MutableList<DynamicText> = ArrayList()

    /**
     * Cursor between pages
     */
    var pageCursor = 0

    /**
     * Render this tab column for a player
     *
     * @param slot The slot of the column to render
     * @param player The player to render column for
     */
    fun render(slot: Int, displayTitles: Boolean, elementWidth: Int) {
        // Don't render if timer not ready
//        if (!elementCooldown.isReady) {
//            return
//        }
//        elementCooldown.reset()
//        elementCooldown.go()

        // Check if to refresh
        var check: MutableList<DynamicText> = ArrayList()
        check = tab.render(slot, player, displayTitles)

        // If not the same, re-render
        if (elements.isEmpty()) {
            elements = check
        } else {
            // Refresh
            if (check.size != elements.size) elements = check
        }

        // Our sub array, or our page
        val sub: MutableList<DynamicText?> = ArrayList(
            elements.subList(
                0.coerceAtLeast(pageCursor.coerceAtMost(elements.size)),
                elements.size.coerceAtMost(pageCursor + (tab.pageElements - 3))
            )
        )

        // If titles enabled
        if (displayTitles) {
            sub.add(0, tab.title)

            // Set our minimum tab length
            val width = StringBuilder()
            for (i in 0 until elementWidth) {
                width.append(" ")
            }
            sub.add(1, FrameText(-1, width.toString()))
        }

        val size = elements.size + 2 + floor((elements.size / tab.pageElements).toDouble())

        // If to show pagination info
        var pageInfo = false

        if (size >= tab.pageElements - 1) {
            // Calculate page length //
            val pageDelta: Double = (pageCursor + if (displayTitles) 3 else 1).toDouble() / tab.pageElements + 1
            val page = (if (pageDelta < 2) floor(pageDelta) else ceil(pageDelta)).toInt()
            val max = ceil((size + 2 * elements.size / tab.pageElements) / tab.pageElements).toInt()

            // If we can go to next page
            if (tab.pageInterval.isReady) {
                // Don't update if on a null page
                if (page > max) {
                    // Reset to page 1
                    elements = ArrayList()
                    pageCursor = 0
                    return
                }

                // Go to next page
                elements = check
                pageInfo = true
                tab.pageInterval.reset()
                tab.pageInterval.go()
            }

            // Pagination text
            val pagesText: String = Locale.TAB_PAGE_TEXT.toString("current_page", 1.coerceAtLeast(page)).toString("max_page", 1.coerceAtLeast(max)).toString()
            sub.add(FrameText(-1, pagesText))
        }

        // For elements in the sub tab
        for (i in 1..tab.pageElements) {
            val blank = i - 1 >= sub.size

            // Send update packet //
            var text = TextUtil.formatText(if (blank) "" else sub[i - 1]?.getVisibleText())

            // Set placeholders
            if (CustomTab.instance!!.papiEnabled) {
                // Try render anything else.
                // By this point it will be formatted by player
                text = PlaceholderAPI.setPlaceholders(player, text)
            }

            // Trim text
            if (text.length > elementWidth) {
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
                // Set text in that slot as our final text
                TabController.setText(slot, i, text, player)
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
        tab.title?.changeText()
        elements.forEach(DynamicText::changeText)

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
    }

}