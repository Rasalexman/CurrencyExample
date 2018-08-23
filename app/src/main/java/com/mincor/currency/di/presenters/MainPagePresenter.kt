package com.mincor.currency.di.presenters

import android.util.ArrayMap
import android.util.Log
import com.mincor.currency.adapters.MainItem
import com.mincor.currency.common.moveToTop
import com.mincor.currency.contracts.IMainPageContract
import com.mincor.currency.di.interfaces.IWebServerApi
import com.mincor.currency.models.CurrencyModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult
import java.util.concurrent.TimeUnit

/**
 * Fat Presenter
 */
class MainPagePresenter(private val webApi:IWebServerApi) : IMainPageContract.IPresenter {

    private var selectedCurrencyName:String = "EUR"                     // selected name
    private val currencyItems:MutableList<MainItem> = mutableListOf()   // local storage for view items cause we have a fat presenter)
    private val currencyModels:ArrayMap<String, CurrencyModel> = ArrayMap() // ArrayMap for multiply amounts
    private var timer:Disposable? = null                                // simple RxTimer

    override fun start() {
        getOrPutModelByName(selectedCurrencyName)
        launch(UI) {
            loadCurrency(currencyItems.size == 1) {
                startTimer()
            }
        }
    }

    override fun checkLoaded() {
        if(currencyItems.isNotEmpty()) {
            view?.addLoaded(currencyItems)
        }
    }

    override fun changeCurrency(currencyName:String, position:Int) {
        launch(UI) {
            // останавливаем таймер на время запроса
            stopTimer()
            // change position of item in list
            currencyItems.moveToTop(position)
            // назначаем текущую валюту для пересчета
            selectedCurrencyName = currencyName
            // обновляем список
            loadCurrency {
                // стартуем таймер занового
                startTimer()
            }
        }
    }

    override fun multiplyValues(multiplier: Float) {
        currencyModels.filter { it.value.name != selectedCurrencyName }.forEach { (_, currencyModel) ->
            currencyModel.valueToShow = currencyModel.valueByOne * multiplier
        }
    }

    // Start timer
    private fun startTimer() {
        stopTimer()
        timer = Observable.interval(1000, TimeUnit.MILLISECONDS).subscribe {
            launch(UI) {
                loadCurrency()
            }
        }
    }
    // stop timer
    private fun stopTimer() {
        timer?.dispose()
        timer = null
    }

    /**
     * Suspended loading currency function
     *
     * @param showLoading
     * does we need to show loading manually
     *
     * @param onFinishLoading
     * Lambda to make some action after server respond
     */
    private suspend fun loadCurrency(showLoading:Boolean = false, onFinishLoading:(()->Unit)? = null) {
        // так как у нас нет пагинации, то показываем загрузку только когда это необходимо
        if(showLoading) view?.showLoadingFooter()

        val result = withContext(CommonPool) { webApi.getCurrency(selectedCurrencyName).awaitResult() }
        when(result) {
            is Result.Ok -> {
                result.value.error?.let {
                    view?.displayError(it)
                } ?: result.value.rates?.let { map ->
                    // скрываем загрузку
                    view?.hideLoadingFooter()
                    // текущая модель валют
                    var currencyModel = getOrPutModelByName(selectedCurrencyName)
                    // множитель
                    val multiplier = currencyModel.valueToShow
                    // назначаем значения для показателей
                    map.forEach { (name, value) ->
                        currencyModel = getOrPutModelByName(name)
                        currencyModel.valueByOne = value
                        currencyModel.valueToShow = value * multiplier
                    }
                    // вызываем действие по окончанию
                    onFinishLoading?.let {
                        it()
                    }
                }
            }
            is Result.Error -> view?.displayError(result.exception.message())
            is Result.Exception -> view?.displayError(result.exception.localizedMessage)
        }
    }

    /**
     * Factory creation function
     *
     * @param currencyName
     * Current name of currency to be created or getted from map
     *
     * @return instance of CurrencyModel
     */
    private fun getOrPutModelByName(currencyName: String):CurrencyModel = currencyModels.getOrPut(currencyName) {
            val newModel = CurrencyModel(currencyName)
            val mainItem = MainItem(newModel, currencyName == selectedCurrencyName)
            currencyItems.add(mainItem)
            view?.addItem(mainItem)
            newModel
        }

    override fun stop() {
        view = null
        stopTimer()
    }

    override var view: IMainPageContract.IView? = null
}