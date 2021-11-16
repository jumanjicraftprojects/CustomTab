package com.illuzionzstudios.tab.tab.components.column

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.illuzionzstudios.mist.Logger
import com.illuzionzstudios.mist.scheduler.timer.PresetCooldown
import com.illuzionzstudios.mist.util.TextUtil
import com.illuzionzstudios.tab.CustomTab
import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.model.FrameText
import com.illuzionzstudios.tab.settings.Locale
import com.illuzionzstudios.tab.tab.TabController
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

/**
 * An instance of a tab column
 */
abstract class TabColumn(
    /**
     * ID of this tab
     */
    val id: String
) {

    // ----------------------------------------
    // Page options
    // ----------------------------------------

    /**
     * The height of each column
     */
    var pageElements: Int = 15

    /**
     * The amount of ticks between changing through pages
     */
    var pageInterval: PresetCooldown = PresetCooldown(100)

    /**
     * Cached icon skins
     */
    protected var avatarCache: Table<Int, Int, UUID> = HashBasedTable.create()

    /**
     * Cursor between pages
     */
    var pageCursor = 0

    /**
     * The title for this column
     */
    var title: DynamicText? = null

    /**
     * A list of elements in this column
     */
    var elements: MutableList<DynamicText> = ArrayList()

    /**
     * Render this tab column for a player
     *
     * @param slot The slot of the column to render
     * @param player The player to render column for
     */
    fun render(slot: Int, player: Player?, displayTitles: Boolean, elementWidth: Int) {
        if (player == null) return

        // Check if to refresh
        var check: MutableList<DynamicText> = ArrayList()
        elements = render(slot, player, displayTitles)
//        Logger.debug("Elements: $elements")

//        // If not the same, re-render
//        if (elements.isEmpty()) {
//            elements = check
//        } else {
//            // Refresh
//            if (check.size != elements.size) elements = check
//        }

        // Our sub array, or our page
        val sub: MutableList<DynamicText?> = ArrayList(
            elements.subList(
                0.coerceAtLeast(pageCursor.coerceAtMost(elements.size)),
                elements.size.coerceAtMost(pageCursor + (pageElements - 3))
            )
        )

        // If titles enabled
        if (displayTitles) {
            sub.add(0, title)

            // Set our minimum tab length
            val width = StringBuilder()
            for (i in 0 until elementWidth) {
                width.append(" ")
            }
            sub.add(1, FrameText(-1, width.toString()))
        }

        val size = elements.size + 2 + floor((elements.size / pageElements).toDouble())

        // If to show pagination info
        var pageInfo = false

        if (size >= pageElements - 1) {
            // Calculate page length //
            val pageDelta: Double = (pageCursor + if (displayTitles) 3 else 1).toDouble() / pageElements + 1
            val page = (if (pageDelta < 2) floor(pageDelta) else ceil(pageDelta)).toInt()
            val max = ceil((size + 2 * elements.size / pageElements) / pageElements).toInt()

            // If we can go to next page
            if (pageInterval.isReady) {
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
                pageInterval.reset()
                pageInterval.go()
            }

            // Pagination text
            val pagesText: String = Locale.TAB_PAGE_TEXT
                .toString("current_page", 1.coerceAtLeast(page))
                .toString("max_page", 1.coerceAtLeast(max)).toString()
            sub.add(FrameText(-1, pagesText))
        }

        // For elements in the sub tab
        for (i in 1..pageElements) {
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
                if (avatarCache.contains(slot, i)) {
                    avatarCache.remove(slot, i)
                    TabController.hideAvatar(slot, i, player)
                }
            }

            // Go to next page if applicable
            if (pageInfo) pageCursor++
        }

        // Update text and title
        title?.changeText()
        elements.forEach(DynamicText::changeText)

        // If page display at bottom
        if (pageInfo) {
            pageCursor -= if (displayTitles) 3 else 1
        }

        // Check if cursor is greater than applicable
        // number of pages

        // Check if cursor is greater than applicable
        // number of pages
        if (pageCursor >= size - (if (displayTitles) 3 else 1) * elements.size / pageElements) {
            // Reset to page 1
            elements = ArrayList()
            pageCursor = 0
        }
    }

    /**
     * Custom implementation to add elements
     */
    abstract fun render(slot: Int, player: Player?, displayTitles: Boolean): MutableList<DynamicText>

}