package com.mincor.currency.models

import kotlin.properties.Delegates

data class CurrencyModel(var name:String, var valueByOne:Float = 1f, var callback:((Float)->Unit)? = null) {
    var valueToShow:Float by Delegates.observable(1f) { _, oldValue, newValue ->
        if(oldValue != newValue) callback?.let { it(newValue) }
    }
}