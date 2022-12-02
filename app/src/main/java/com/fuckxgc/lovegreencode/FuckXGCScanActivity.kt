package com.fuckxgc.lovegreencode

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PermissionUtils.SimpleCallback
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import kotlinx.coroutines.delay

class FuckXGCScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fuck_xgcsan)
        PermissionUtils.permission(PermissionConstants.CAMERA)
            .callback(object : SimpleCallback {
                override fun onGranted() {
                    lifecycleScope.launchWhenCreated {
                        startActivity(
                            Intent(
                                this@FuckXGCScanActivity,
                                CaptureActivity::class.java
                            ).apply {
                                putExtra(Constant.INTENT_ZXING_CONFIG, ZxingConfig().apply {
                                    isPlayBeep = false
                                    isShake = false
                                    isFullScreenScan = true
                                    isShowbottomLayout = false
                                })
                            })
                        delayStartLocActivity(2000L)
                    }
                }

                override fun onDenied() {
                    lifecycleScope.launchWhenCreated {
                        delayStartLocActivity(1000L)
                    }
                }
            })
            .request()
    }

    private suspend fun delayStartLocActivity(time: Long) {
        delay(time)
        startActivity(
            Intent(
                this@FuckXGCScanActivity,
                FuckXGCLocationActivity::class.java
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        )
        finish()
    }
}
