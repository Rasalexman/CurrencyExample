package com.mincor.currency.common

/**
 * Created by a.minkin on 26.12.2017.
 */
data class ScrollPosition(var index:Int = 0, var top:Int = 0){
    fun drop(){
        index = 0
        top = 0
    }
}
