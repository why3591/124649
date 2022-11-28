package com.fuckxgc.lovegreencode

import android.app.Application
import com.uuzuche.lib_zxing.activity.ZXingLibrary

class FuckApp: Application() {

    override fun onCreate() {
        super.onCreate()
        ZXingLibrary.initDisplayOpinion(this)
    }
}
