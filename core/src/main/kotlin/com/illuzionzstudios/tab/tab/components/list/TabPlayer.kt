package com.illuzionzstudios.tab.tab.components.list

import com.illuzionzstudios.tab.CustomTab
import com.illuzionzstudios.tab.group.GroupController
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player

class TabPlayer(
    /**
     * Player associated
     */
    val tabPlayer: Player,

    /**
     * Way to sort this player
     */
    private val sortType: SortType,

    /**
     * Sort variable to be used it any
     */
    private val sortVariable: String
) : Comparable<TabPlayer?> {

    // The greater the number, the higher priority
    private val weight: Int
        get() {
            // Make sure has group
            if (GroupController.getGroup(tabPlayer) == null) return 0

            var weight = 0
            when (sortType) {
                SortType.WEIGHT -> weight += GroupController.getGroup(tabPlayer)?.weight ?: 0
                SortType.STRING_VARIABLE -> {
                }
                SortType.NUMBER_VARIABLE -> {
                    // PAPI Here
                    var toParse: String = sortVariable
                    if (CustomTab.instance!!.papiEnabled) toParse =
                        PlaceholderAPI.setPlaceholders(tabPlayer, toParse)
                    weight += toParse.toInt()
                }
                SortType.DISTANCE -> weight -= tabPlayer.location.distance(tabPlayer.location).toInt()
            }
            return weight
        }

    override fun compareTo(other: TabPlayer?): Int {
        return (other?.weight!!).compareTo(weight)
    }
}