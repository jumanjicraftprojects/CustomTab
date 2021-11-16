package com.illuzionzstudios.tab.tab.components.list

enum class SortType {

    /**
     * This means the closest people will appear near the top
     */
     DISTANCE,

    /**
     * This will sort by alphabetical order based
     * on a variable. This can be passed in from placeholders
     */
    STRING_VARIABLE,

    /**
     * This will sort with the highest numbers
     * appearing at the top. Can be passed in from placeholders
     */
    NUMBER_VARIABLE,

    /**
     * This sorts by weight, with higher numbers appearing
     * at the top. Usually taken from player groups.
     */
    WEIGHT

}