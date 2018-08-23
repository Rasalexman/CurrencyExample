package com.mincor.currency.models

data class CurrencyRespond(val base:String? = null, val date:String? = null, val rates:Map<String, Float>? = null, val error:String? = null)