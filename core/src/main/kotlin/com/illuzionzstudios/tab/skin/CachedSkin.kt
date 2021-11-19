package com.illuzionzstudios.tab.skin

import com.comphenix.protocol.wrappers.WrappedSignedProperty

data class CachedSkin(val name: String, val value: String, val signature: String) {

    /**
     * Gets the property from this skin
     */
    fun getProperty(): WrappedSignedProperty {
        return WrappedSignedProperty("textures", value, signature)
    }

}