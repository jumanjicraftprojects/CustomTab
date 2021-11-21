package com.illuzionzstudios.tab.tab.components.list

import com.illuzionzstudios.tab.model.DynamicText
import com.illuzionzstudios.tab.model.FrameText
import com.illuzionzstudios.tab.tab.components.column.TabColumn
import com.illuzionzstudios.tab.tab.components.list.type.OnlineList

/**
 * An instance of a tab list which auto-fills
 * elements and sorts them based on variables
 */
abstract class TabList(id: String) : TabColumn(id) {

    /**
     * The type of tab list
     */
    var listType: ListType = ListType.ONLINE_PLAYERS

    /**
     * How to sort list elements
     */
    var sorter: SortType = SortType.WEIGHT

    /**
     * The placeholder to sort list elements by. Only applies
     * to number_variable sort type
     */
    var sortVariable: String = ""

    /**
     * The text element for each list element
     */
    var elementText: DynamicText? = FrameText(-1, "&f%player_name%")

    companion object {

        /**
         * Get a tab list from type. Currently only online luist
         */
        fun getListFromType(type: ListType, id: String): TabList? {
            return OnlineList(id)
        }

    }

}