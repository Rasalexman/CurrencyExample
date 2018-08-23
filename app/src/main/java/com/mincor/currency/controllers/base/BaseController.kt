package com.mincor.currency.controllers.base

import android.os.Bundle
import android.view.View
import com.mincor.currency.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

abstract class BaseController : ViewBindController {

    protected constructor()
    protected constructor(args: Bundle) : super(args)

    override fun onAttach(view: View) {
        super.onAttach(view)
        attachListeners()
    }

    override fun onDetach(view: View) {
        detachListeners()
        super.onDetach(view)
    }

    /**
     * Когда текущая вьюха была создана
     */
    override fun onViewCreated(view: View) {}

    /**
     * Показываем ошибку
     */
    fun displayError(error: String) {
        activity?.alert {
            title = activity!!.getString(R.string.warning)
            message = error
            yesButton {  }
        }
    }

    /**
     * Назначаем слушателей для текущего Контроллера
     */
    protected open fun attachListeners() {}

    /**
     * Удаляем слушателей для текущего контроллера
     */
    protected open fun detachListeners() {}
}
