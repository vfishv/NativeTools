@file:JvmName("ContextKt")

package com.dede.nativetools.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.dede.nativetools.BuildConfig
import com.dede.nativetools.NativeToolsApp
import com.dede.nativetools.R
import com.google.android.material.color.MaterialColors
import java.io.InputStream
import kotlin.properties.ReadOnlyProperty


val globalContext: Context
    get() = NativeToolsApp.getInstance()

inline fun <reified T : Any> Context.requireSystemService(): T {
    return checkNotNull(applicationContext.getSystemService())
}

inline fun <reified T : Any> Context.systemService(): ReadOnlyProperty<Context, T> {
    return ReadOnlyProperty { _, _ -> requireSystemService() }
}

fun Context.launchActivity(intent: Intent) {
    intent.runCatching(this::startActivity).onFailure(Throwable::printStackTrace)
}

fun Context.startService(intent: Intent, foreground: Boolean) {
    if (foreground) {
        ContextCompat.startForegroundService(this, intent)
    } else {
        this.startService(intent)
    }
}

fun Context.assets(fileName: String): InputStream {
    return assets.open(fileName)
}

@Suppress("UNCHECKED_CAST")
fun <T : Drawable> Context.requireDrawable(@DrawableRes drawableId: Int): T {
    return checkNotNull(AppCompatResources.getDrawable(this, drawableId) as T)
}

@ColorInt
fun Context.color(@ColorRes colorId: Int): Int {
    return ContextCompat.getColor(this, colorId)
}

@ColorInt
fun Context.color(@AttrRes attrId: Int, @ColorInt default: Int): Int {
    return MaterialColors.getColor(this, attrId, default)
}

fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.toast(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun Context.browse(url: String) {
    ChromeTabsBrowser.launchUrl(this, Uri.parse(url))
}

fun Context.browse(@StringRes urlId: Int) {
    this.browse(this.getString(urlId))
}

fun Context.market(packageName: String) {
    val market = Intent(Intent.ACTION_VIEW)
        .setData("market://details?id=$packageName")
        .newTask()
        .toChooser(R.string.chooser_label_market)
    startActivity(market)
}

fun Context.share(@StringRes textId: Int) {
    val intent = Intent(Intent.ACTION_SEND, Intent.EXTRA_TEXT to getString(textId))
        .setType("text/plain")
        .newTask()
        .toChooser(R.string.action_share)
    startActivity(intent)
}

fun Context.emailTo(@StringRes addressRes: Int) {
    val uri = Uri.parse("mailto:${getString(addressRes)}")
    val intent = Intent(Intent(Intent.ACTION_SENDTO, uri))
        .newTask()
        .toChooser(R.string.action_feedback)
    startActivity(intent)
}

fun Context.copy(text: String) {
    val clipboardManager = this.requireSystemService<ClipboardManager>()
    clipboardManager.setPrimaryClip(ClipData.newPlainText("text", text))
}

fun Context.getVersionSummary() =
    getString(R.string.summary_about_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)