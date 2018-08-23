package com.mincor.currency.contracts

import com.mincor.currency.adapters.MainItem
import com.mincor.currency.controllers.base.mvp.IBasePresenter
import com.mincor.currency.controllers.base.mvp.IBaseView

interface IMainPageContract {
    interface IView : IBaseView<IPresenter> {
        /**
         * Add already loaded view elements if we change the screen or rotate
         *
         * @param list
         * List of all items that already loaded
         *
         */
        fun addLoaded(list: List<MainItem>)

        /**
         * Add single item to adapter
         *
         * @param item
         * View Item
         */
        fun addItem(item: MainItem)
    }
    interface IPresenter : IBasePresenter<IView> {
        /**
         * We check if we already loaded model and put views into adapter
         */
        fun checkLoaded()

        /**
         * Change current currency
         *
         * @param currencyName
         * Name of selected currency
         *
         * @param position
         * The position of selected element to change it in adapters store
         */
        fun changeCurrency(currencyName:String, position:Int)

        /**
         * Multiply of currency amount by selected value name
         *
         * @param multiplier
         * The amount to multiply another currencies
         */
        fun multiplyValues(multiplier:Float)
    }
}