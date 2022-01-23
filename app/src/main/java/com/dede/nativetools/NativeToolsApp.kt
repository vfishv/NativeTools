package com.dede.nativetools

import android.app.Application
import android.content.Context
import android.util.Log
import com.dede.nativetools.other.OtherPreferences
import com.dede.nativetools.util.installShortcuts
import com.dede.nativetools.util.isMainProcess
import com.dede.nativetools.util.setNightMode
import com.google.android.material.color.DynamicColors
import me.weishu.reflection.Reflection

class NativeToolsApp : Application() {

    companion object {
        private var instance: NativeToolsApp? = null

        fun getInstance(): NativeToolsApp {
            return checkNotNull(instance)
        }
    }

    override fun attachBaseContext(base: Context?) {
        instance = this
        super.attachBaseContext(base)
        val unseal = Reflection.unseal(base)
        Log.i("NativeToolsApp", "unseal: $unseal")
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(
            this,
            R.style.ThemeOverlay_AppTheme_DynamicColors
        )
        if (isMainProcess()) {
            installShortcuts()
            setNightMode(OtherPreferences.nightMode)
        }
    }

}