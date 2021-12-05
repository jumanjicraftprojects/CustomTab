package com.illuzionzstudios.tab.tab.components.list

import com.illuzionzstudios.tab.tab.components.column.TabColumn
import com.illuzionzstudios.tab.tab.components.item.TabItem
import com.illuzionzstudios.tab.tab.components.item.TextTabItem
import com.illuzionzstudios.tab.tab.components.list.type.OnlineList
import java.util.function.Predicate

/**
 * An instance of a tab list which auto-fills
 * elements and sorts them based on variables
 */
abstract class TabList<T>(id: String) : TabColumn(id) {

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
     * Filter each entry in the list
     */
    var filter: Predicate<T> = Predicate<T> { true }

    /**
     * The text element for each list element
     */
    var elementText: TabItem = TextTabItem(-1, "&f%player_name%")

    companion object {

        /**
         * Get a tab list from type. Currently only online list
         */
        fun getListFromType(type: ListType, id: String): TabList<*>? {
            return OnlineList(id)
        }

    }

}