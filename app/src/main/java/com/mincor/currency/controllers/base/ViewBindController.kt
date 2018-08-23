package com.mincor.currency.controllers.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.mincor.currency.common.clear
import com.mincor.currency.common.clearAfterDestroyView
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

abstract class ViewBindController : Controller, KodeinAware {

    override val kodein: Kodein by lazy {
        (applicationContext as KodeinAware).kodein
    }

    protected constructor()
    protected constructor(args: Bundle) : super(args)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val viewInstance = getViewInstance(inflater.context)
        onViewCreated(viewInstance)
        return viewInstance
    }

    abstract fun getViewInstance(context: Context):View
    abstract fun onViewCreated(view:View)

    override fun onDestroyView(view: View) {
        (view as? ViewGroup)?.clear()
        super.onDestroyView(view)
        clearAfterDestroyView<ViewBindController>()
    }
}
