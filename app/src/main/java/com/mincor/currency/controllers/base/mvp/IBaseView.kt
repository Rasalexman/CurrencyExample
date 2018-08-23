package com.mincor.currency.controllers.base.mvp

/**
 * Created by a.minkin on 25.10.2017.
 */
interface IBaseView<out T : IBasePresenter<*>> {
    val presenter: T
    fun hideLoadingFooter()
    fun showLoadingFooter()
    fun displayError(error: String)
}