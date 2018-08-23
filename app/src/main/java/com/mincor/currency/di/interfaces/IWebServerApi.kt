package com.mincor.currency.di.interfaces

import com.mincor.currency.models.CurrencyRespond
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface IWebServerApi {

    /**
     * Получаем список всех доступных валют по текущему выбранному показателю
     *
     * @param base
     * Текущий тип валюты
     */
    @GET("latest")
    @Headers("Content-type: application/json")
    fun getCurrency(@Query("base") base: String = ""): Call<CurrencyRespond>
}