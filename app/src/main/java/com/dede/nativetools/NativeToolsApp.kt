package com.dede.nativetools

import android.app.Application
import android.content.Context
import com.dede.nativetools.ui.LauncherReceiver

class NativeToolsApp : Application() {

    companion object {
        private var instance: NativeToolsApp? = null
        fun getInstance(): NativeToolsApp {
            return checkNotNull(instance) {}
        }
    }

    override fun attachBaseContext(base: Context?) {
        instance = this
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        LauncherReceiver.launcher(this)
    }
}