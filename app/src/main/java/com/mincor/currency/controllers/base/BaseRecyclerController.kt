package com.mincor.currency.controllers.base

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.OnClickListener
import com.mikepenz.fastadapter_extensions.items.ProgressItem
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener
import com.mincor.currency.common.Consts
import com.mincor.currency.common.ScrollPosition
import com.mincor.currency.common.log

/**
 * Created by Alex on 07.01.2017.
 */

abstract class BaseRecyclerController : BaseController, OnClickListener<AbstractItem<*, *>> {

    protected constructor()
    protected constructor(args: Bundle) : super(args)

    init {
        retainViewMode = RetainViewMode.RETAIN_DETACH
    }

    protected var recycler: RecyclerView? = null

    // layout manager for recycler
    protected open var layoutManager: RecyclerView.LayoutManager? = null
    // направление размещения элементов в адаптере
    protected open val layoutManagerOrientation: Int = LinearLayoutManager.VERTICAL
    // бесконечный слушатель слушатель скролла
    private var scrollListener: RecyclerView.OnScrollListener? = null
    // custom decorator
    protected open var itemDecoration: RecyclerView.ItemDecoration? = null
    // save our FastAdapter
    protected var mFastItemAdapter: FastItemAdapter<AbstractItem<*, *>>? = null
    // последняя сохраненная позиция (index & offset) прокрутки ленты
    protected open val previousPosition: ScrollPosition? = null
    // крутилка прогресса)
    private val progressItem = ProgressItem().withEnabled(false)
    // корличесвто элементов до того как пойдет запрос на скролл пагинацию
    protected open val visibleScrollCount get() = Consts.SCROLL_VISIBLE_THRESHOLD

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)

        setRVLayoutManager()    // менеджер лайаута
        setItemDecorator()      // декоратор лайаута
        createAdapter()         // адаптер
        setRVCAdapter()         // назначение
        addEventHook()          // events from adapter
        setRVCScroll()          // scroll listener
    }

    // менеджер лайаута
    private fun setRVLayoutManager() {
        layoutManager ?: let {
            layoutManager = LinearLayoutManager(this.activity, layoutManagerOrientation, false)
            recycler?.layoutManager = layoutManager
        }
    }

    // промежутки в адаптере
    protected open fun setItemDecorator() {
        itemDecoration?.let { recycler?.addItemDecoration(it) }
    }

    // создаем адаптеры
    private fun createAdapter() {
        mFastItemAdapter ?: let {
            mFastItemAdapter = FastItemAdapter()
            addClickListenerToAdapter()
        }
    }

    protected open fun addClickListenerToAdapter(){
        mFastItemAdapter!!.withOnClickListener(this)
    }

    //назначаем адаптеры
    private fun setRVCAdapter() {
        recycler?.adapter ?: let {
            recycler?.setHasFixedSize(false)
            recycler?.swapAdapter(mFastItemAdapter, false)
        }
    }

    //
    protected open fun addEventHook() {}

    // слушатель бесконечная прокрутка
    private fun setRVCScroll() {
         scrollListener ?: let {
             scrollListener = object : EndlessRecyclerOnScrollListener(layoutManager!!, visibleScrollCount) {
                 override fun onLoadMore(currentPage: Int) {
                     loadNextDataFromApi(currentPage)
                 }
             }
             recycler?.addOnScrollListener(scrollListener!!)
         }
    }

    // Показываем загрузку
    open fun showLoadingFooter() {
        hideLoadingFooter()
        mFastItemAdapter?.add(progressItem)
    }

    // прячем загрузку
    open fun hideLoadingFooter() {
        val position = mFastItemAdapter?.getAdapterPosition(progressItem) ?: -1
        if (position > -1) mFastItemAdapter?.remove(position)
    }

    //--------- CALL BACKS FOR RECYCLER VIEW ACTIONS
    protected open fun loadNextDataFromApi(page: Int) {}

    override fun onClick(v: View?, adapter: IAdapter<AbstractItem<*, *>>?, item: AbstractItem<*, *>, position: Int): Boolean {
        log {  "ITEM CLICKED ON POSITION $position" }
        onItemClickListener(item, position)
        return false
    }

    protected open fun onItemClickListener(item: AbstractItem<*, *>, position: Int) {}

    override fun onDetach(view: View) {
        savePreviousPosition()
        hideLoadingFooter()
        super.onDetach(view)
    }

    // если хотим сохранить последнюю проскролленную позицию
    protected open fun savePreviousPosition() {
        recycler?.let { rec ->
            previousPosition?.let {
                val v = rec.getChildAt(0)
                it.index = (rec.layoutManager as? LinearLayoutManager?)?.findFirstVisibleItemPosition() ?: 0
                it.top = v?.let { it.top - rec.paddingTop } ?: 0
            }
        }
    }

    // прокручиваем к ранее выбранным элементам
    open fun applyScrollPosition() {
        recycler?.let { rec ->
            stopRecyclerScroll()
            previousPosition?.let {
                (rec.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(it.index, it.top)
            }
        }
    }


    protected fun stopRecyclerScroll(){
        // останавливаем прокрутку
        recycler?.stopScroll()
    }

    private fun clearFastAdapter() {
        mFastItemAdapter?.apply {
            withOnClickListener(null)
            eventHooks?.clear()
            clear()
            notifyDataSetChanged()
            notifyAdapterDataSetChanged()
        }
    }

    // скролл к верхней записи
    protected fun scrollToTop() {
        stopRecyclerScroll()
        // сбрасываем прокрутку
        previousPosition?.drop()
        // применяем позицию
        applyScrollPosition()
    }

    private fun clearRecycler() {
        recycler?.apply {
            removeAllViews()
            removeAllViewsInLayout()
            adapter = null
            layoutManager = null
            itemAnimator = null
            clearOnScrollListeners()
            recycledViewPool.clear()

            itemDecoration?.let {
                removeItemDecoration(it)
            }
        }
    }

    override fun onDestroyView(view: View) {
        clearFastAdapter()
        clearRecycler()

        mFastItemAdapter = null
        layoutManager = null
        scrollListener = null
        itemDecoration = null
        recycler = null
        scrollListener = null
        super.onDestroyView(view)
    }
}
