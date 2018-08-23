package com.mincor.currency.controllers

import android.content.Context
import android.graphics.Color
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.mincor.currency.R
import com.mincor.currency.adapters.MainItem
import com.mincor.currency.common.Consts
import com.mincor.currency.common.VerticalOffsetDecoration
import com.mincor.currency.contracts.IMainPageContract
import com.mincor.currency.controllers.base.BaseRecyclerController
import com.rasalexman.kdispatcher.IKDispatcher
import com.rasalexman.kdispatcher.subscribe
import com.rasalexman.kdispatcher.unsubscribe
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.kodein.di.generic.instance

/**
 * Created by a.minkin on 02.10.2017.
 */
class MainPageController : BaseRecyclerController(), IMainPageContract.IView, IKDispatcher {

    override val previousPosition get() = Consts.MAIN_PAGE_SCROLL_POSITION
    override val presenter: IMainPageContract.IPresenter by instance()

    override var itemDecoration: RecyclerView.ItemDecoration?
        get() = VerticalOffsetDecoration(activity!!.dip(4))
        set(value) {}

    // UI
    override fun getViewInstance(context: Context) = MainPageUI().createView(AnkoContext.create(context, this))

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.view = this
        presenter.checkLoaded()
        presenter.start()
    }

    override fun onDetach(view: View) {
        presenter.stop()
        super.onDetach(view)
    }

    override fun onItemClickListener(item: AbstractItem<*, *>, position: Int) {
        val firstItem = (mFastItemAdapter?.getAdapterItem(0) as MainItem)
        val selectedItem = (item as MainItem)
        // переключаем элемент элемент
        firstItem.isFirst = false
        selectedItem.isFirst = true
        // меняем элементы местами
        mFastItemAdapter?.move(position, 0)
        // скроллим к верхней позиции
        scrollToTop()
        // оповещаем адаптер
        mFastItemAdapter?.notifyDataSetChanged()
        // меняем значение остальных валют
        presenter.changeCurrency(selectedItem.currencyModel.name, position)
    }

    override fun addLoaded(list: List<MainItem>) {
        if (mFastItemAdapter?.itemCount == 0) mFastItemAdapter?.setNewList(list)
    }

    override fun addItem(item: MainItem) {
        mFastItemAdapter?.add(item)
    }

    override fun attachListeners() {
        subscribe<Float>(Consts.EVENT_CURRENCY_VALUE_CHANGED) {
            presenter.multiplyValues(it.data!!)
        }
    }

    override fun detachListeners() {
        unsubscribe(Consts.EVENT_CURRENCY_VALUE_CHANGED)
    }

    inner class MainPageUI : AnkoComponent<MainPageController> {
        override fun createView(ui: AnkoContext<MainPageController>): View = with(ui) {
            linearLayout {
                lparams(org.jetbrains.anko.matchParent, org.jetbrains.anko.matchParent)
                //----- ЛИСТ С РЕЗУЛЬТАТАМИ ПОИСКА
                recycler = recyclerView {
                    id = R.id.rv_controller
                    backgroundColor = Color.LTGRAY
                    lparams(matchParent, matchParent)
                }
            }
        }
    }
}