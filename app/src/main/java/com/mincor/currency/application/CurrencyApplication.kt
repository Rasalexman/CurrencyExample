package com.mincor.currency.application

import android.app.Application
import com.mincor.currency.contracts.IMainPageContract
import com.mincor.currency.di.modules.netModule
import com.mincor.currency.di.presenters.MainPagePresenter
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class CurrencyApplication : Application(), KodeinAware {
    ///----- DEPENDENCY INJECTION by Kodein
    override val kodein = Kodein.lazy {
        import(androidModule(this@CurrencyApplication))
        import(netModule)
        bind<IMainPageContract.IPresenter>() with singleton { MainPagePresenter(instance()) }
    }
}