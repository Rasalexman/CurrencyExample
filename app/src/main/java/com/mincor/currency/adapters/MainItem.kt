package com.mincor.currency.adapters

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mincor.currency.R
import com.mincor.currency.common.*
import com.mincor.currency.common.Consts.FONT_SIZE_16
import com.mincor.currency.common.Consts.IMAGE_SIZE_54
import com.mincor.currency.common.Consts.IMAGE_STATIC_URL
import com.mincor.currency.common.Consts.MARGIN_16
import com.mincor.currency.common.Consts.MARGIN_8
import com.mincor.currency.models.CurrencyModel
import com.rasalexman.kdispatcher.IKDispatcher
import com.rasalexman.kdispatcher.call
import org.jetbrains.anko.*

class MainItem(val currencyModel: CurrencyModel, var isFirst: Boolean = false) : AbstractItem<MainItem, MainItem.ViewHolder>() {

    override fun createView(ctx: Context, parent: ViewGroup?): View = MainItemUI().createView(AnkoContext.Companion.create(ctx, this))
    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)
    override fun getType(): Int = R.id.main_item_id
    override fun getLayoutRes(): Int = -1

    /**
     * our ViewHolder
     */
    class ViewHolder(view: View) : FastAdapter.ViewHolder<MainItem>(view), IKDispatcher, TextWatcher {
        private val currencyImage: ImageView = view.find(R.id.currency_image)
        private val currencyName: TextView = view.find(R.id.currency_name)
        private val currencyValue: EditText = view.find(R.id.currency_value)

        private var localModel: CurrencyModel? = null

        override fun bindView(item: MainItem, payloads: MutableList<Any>?) {
            // сохраняем ссылку на модель для ее дальнейшего изменения
            localModel = item.currencyModel
            // назначаем данные
            currencyName.text = localModel?.name
            currencyValue.setText(localModel?.valueToShow.toString())

            // как было показано на видео, можем менять значение только для первого элемента в списке
            currencyValue.isEnabled = item.isFirst
            if (item.isFirst) {
                item.currencyModel.callback = null
                currencyValue.requestFocus()
                currencyValue.addTextChangedListener(this)
            } else {
                currencyValue.removeTextChangedListener(this)
                item.currencyModel.callback = {
                    currencyValue.setText(it.toString())
                }
            }
            // load image
            currencyImage.load(IMAGE_STATIC_URL)
        }

        override fun afterTextChanged(p0: Editable?) {
            if (!p0.isNullOrEmpty()) {
                val amount = currencyValue.text.toString().toFloat()
                // сохраняем значение для показа
                localModel?.valueToShow = amount
                // отсылаем сообщение об изменении ячейки
                call(Consts.EVENT_CURRENCY_VALUE_CHANGED, amount)
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun unbindView(item: MainItem) {
            currencyImage.clear()
            currencyValue.removeTextChangedListener(this)
            item.currencyModel.callback = null
            currencyName.text = null
            currencyValue.text = null
            localModel = null
        }
    }

    inner class MainItemUI : AnkoComponent<MainItem> {
        override fun createView(ui: AnkoContext<MainItem>): View = with(ui) {
            // I'm prefer a relative layout cause its extremely fast and easy to use
            // we don's need such a long depth cause view is simple
            relativeLayout {
                backgroundColor = Color.WHITE
                lparams(matchParent)

                circleImage {
                    id = R.id.currency_image
                    background = roundedBg(color(R.color.colorAccent))
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }.lparams(dip(IMAGE_SIZE_54), dip(IMAGE_SIZE_54)) {
                    centerVertically()
                    setMargins(dip(MARGIN_16),dip(MARGIN_16),0, dip(MARGIN_16))
                }

                textView {
                    id = R.id.currency_name
                    textSize = FONT_SIZE_16
                    textColor = Color.BLACK
                    gravity = Gravity.START
                }.lparams(matchParent) {
                    rightOf(R.id.currency_image)
                    centerVertically()
                    leftMargin = dip(MARGIN_8)
                }

                editText {
                    id = R.id.currency_value
                    textSize = FONT_SIZE_16
                    textColor = Color.BLACK
                    inputType = InputType.TYPE_CLASS_NUMBER
                }.lparams {
                    rightMargin = dip(MARGIN_16)
                    alignParentRight()
                    centerVertically()
                }

                view { backgroundColor = Color.GRAY }.lparams(matchParent, dip(2)) {
                    alignParentBottom()
                }
            }
            /*
            // Yes this is an alternative way but we have more depths in layout so it's not recommended
            verticalLayout {
                backgroundColor = Color.WHITE
                linearLayout {
                    gravity = Gravity.CENTER_VERTICAL
                    lparams(matchParent) {
                        margin = dip(16)
                    }

                    circleImage {
                        id = R.id.currency_image
                        background = roundedBg(color(R.color.colorAccent))
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }.lparams(dip(54), dip(54))

                    textView {
                        id = R.id.currency_name
                        textSize = 16f
                        textColor = Color.BLACK
                        gravity = Gravity.START
                    }.lparams(matchParent) {
                        weight = 1f
                        leftMargin = dip(8)
                    }

                    editText {
                        id = R.id.currency_value
                        textSize = 16f
                        textColor = Color.BLACK
                        inputType = InputType.TYPE_CLASS_NUMBER
                    }
                }

                view { backgroundColor = Color.GRAY }.lparams(matchParent, dip(2))
            }*/

        }
    }
}
