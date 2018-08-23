package com.mincor.currency.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.mincor.currency.BuildConfig
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.custom.ankoView
import java.util.*

/**
 * UTILS SECTION
 * */
fun View.drawable(@DrawableRes resource: Int): Drawable? = ContextCompat.getDrawable(context, resource)
fun View.color(@ColorRes resource: Int): Int = ContextCompat.getColor(context, resource)
fun View.string(stringRes: Int): String = context.getString(stringRes)

/***
 * Custom View For somethings like lines
 * */
fun roundedBg(col: Int, corners: Float = 100f, withStroke: Boolean = false, strokeColor: Int = Color.LTGRAY, strokeWeight: Int = 2) = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
    cornerRadius = corners
    setColor(col)
    if (withStroke) setStroke(strokeWeight, strokeColor)
}

//
fun <T> MutableList<T>.moveToTop(i: Int) {
    this.add(0, this.removeAt(i))
}

/**
 * Circle image just for example how easily you can use any custom component in anko dsl
 */
inline fun ViewManager.circleImage(init: CircleImageView.() -> Unit): CircleImageView {
    return ankoView({ CircleImageView(it) }, theme = 0, init = init)
}

inline fun log(lambda: () -> String?) {
    if (BuildConfig.DEBUG) {
        Log.d("------>", lambda() ?: "")
    }
}

/**
 * GLIDE IMAGE LOADING
 * */
val Context.glide: GlideRequests
    get() = GlideApp.with(this.applicationContext)

fun ImageView.load(path: String, progress: ProgressBar? = null, loaderHandler: (()->Unit)? = null) {
    val layParams = this.layoutParams
    progress?.show()

    if (path.contains(".gif")) {
        val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .override(layParams.width, layParams.height)

        val reqListener = object : RequestListener<GifDrawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                progress?.hide(true)
                loaderHandler?.let { it() }
                return false
            }

            override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                progress?.hide(true)
                loaderHandler?.let { it() }
                return false
            }
        }
        context.glide.asGif().load(path).listener(reqListener).apply(requestOptions).into(this)
    } else {
        val requestOptions = RequestOptions().dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .dontAnimate()
                .override(layParams.width, layParams.height)
                .encodeFormat(Bitmap.CompressFormat.WEBP)
                .format(DecodeFormat.PREFER_RGB_565)

        val reqListener = object : RequestListener<Bitmap> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                progress?.hide(true)
                loaderHandler?.let { it() }
                return false
            }

            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                progress?.hide(true)
                loaderHandler?.let { it() }
                return false
            }
        }
        context.glide.asBitmap().load(path).listener(reqListener).apply(requestOptions).into(this)
    }

}

fun ImageView.load(pathRes: Int) {
    context.glide.load(pathRes).into(this)
}

fun ImageView.clear() {
    context.glide.clear(this)
    this.setImageResource(0)
    this.setImageBitmap(null)
    this.setImageDrawable(null)
}

fun ViewGroup.clear() {
    var childView: View
    repeat(this.childCount) {
        childView = this.getChildAt(it)
        when (childView) {
            is ViewGroup -> (childView as ViewGroup).clear()
            is ImageView -> (childView as ImageView).clear()
            is Button -> (childView as Button).setOnClickListener(null)
            is TextView -> {
                (childView as TextView).text = null
                (childView as TextView).setOnClickListener(null)
            }
        }
    }
}

/**
 * Toggle's view's visibility. If View is visible, then sets to gone. Else sets Visible
 */
fun View.toggle() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

var View.visible
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else GONE
    }

fun View.hide(gone: Boolean = true) {
    visibility = if (gone) GONE else INVISIBLE
}

fun View.show() {
    visibility = VISIBLE
}
