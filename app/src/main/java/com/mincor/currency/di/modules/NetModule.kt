package com.mincor.currency.di.modules

import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.mincor.currency.common.Consts.TAG_SERVER_URL
import com.mincor.currency.common.log
import com.squareup.moshi.Moshi
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.generic.with
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.mincor.currency.di.interfaces.IWebServerApi
import java.io.File


/**
 * Created by a.minkin on 25.10.2017.
 */

val netModule = Kodein.Module("netModule") {
    constant(TAG_SERVER_URL) with "https://revolut.duckdns.org/"
    bind<OkHttpClient>() with singleton { createOkHttpClient(instance(), instance("cache")) }
    bind<IWebServerApi>() with singleton { createWebServiceApi<IWebServerApi>(instance(), instance(TAG_SERVER_URL)) }
}

fun createOkHttpClient(cm: ConnectivityManager?, cachedDir: File): OkHttpClient {
    val httpClient = OkHttpClient.Builder()

    try {
        httpClient.cache(Cache(cachedDir, 10L * 1024L * 1024L)) // 10 MB Cache
    } catch (e: Exception) {
        log { "createOkHttpClient Couldn't create http cache because of IO problem. $e" }
    }

    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    // add logging as last interceptor
    httpClient.addInterceptor(logging)  // <-- this is the important line!

    /*httpClient.addInterceptor { chain ->
        var request = chain.request()
        request = if (NetModule.isNetworkAvailable(cm)) {
            // if there is connectivity, we tell the request it can reuse the data for sixty seconds.
            request.newBuilder()
                    .removeHeader("Pragma")
                    .addHeader("Cache-Control", "public, max-age=5000") //
                    .addHeader("Platform", "Android")         //
                    .addHeader("Connection", "close")
                    .build()

        } else {
            // If there’s no connectivity, we ask to be given only (only-if-cached) ‘stale’ data upto 7 days ago
            request.newBuilder().removeHeader("Pragma").addHeader("Cache-Control", "public, only-if-cached, max-stale=5000").build()
        }
        chain.proceed(request)
    }

    httpClient.addNetworkInterceptor { chain ->
        val originalResponse = chain.proceed(chain.request())
        // если получили неизмененный ответ прост овыходим и не запариваемся
        if(originalResponse.code() == 304) return@addNetworkInterceptor originalResponse

        val cacheControl = originalResponse.header("Cache-Control")
        return@addNetworkInterceptor if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
            originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .addHeader("Cache-Control", "public, max-age=600000")
                    .addHeader("Platform", "Android")
                    .addHeader("Connection", "close")
                    .build()
        } else {
            originalResponse
        }
    }*/

    return httpClient.build()
}

inline fun <reified F> createWebServiceApi(okHttpClient: OkHttpClient, url: String): F {
    val moshi = Moshi.Builder().build()
    val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    return retrofit.create(F::class.java)
}