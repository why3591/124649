package com.fuckxgc.lovegreencode

import android.app.Application
import android.os.Process
import kotlin.system.exitProcess

class FuckApp : Application() {

    companion object {
        fun fuckSelf() {
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
    }
}
