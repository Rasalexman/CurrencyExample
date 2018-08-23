package com.mincor.currency.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.mincor.currency.controllers.MainPageController
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent

class MainActivity : AppCompatActivity() {

    // главный роутер приложения
    private var mainRouter: Router? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // главный контейнер приложения
        val container = linearLayout { lparams(matchParent, matchParent) }
        mainRouter = Conductor.attachRouter(this, container, savedInstanceState)
        if (!mainRouter!!.hasRootController()) {
            // показываем экран
            mainRouter!!.setRoot(RouterTransaction.with(MainPageController()))
        }
    }

    override fun onDestroy() {
        mainRouter = null
        super.onDestroy()
    }
}
