package com.fuckxgc.lovegreencode

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PermissionUtils.SimpleCallback
import com.google.zxing.Result
import com.yzq.zxinglibrary.common.Constant
import com.yzq.zxinglibrary.decode.DecodeImgCallback
import com.yzq.zxinglibrary.decode.DecodeImgThread
import com.yzq.zxinglibrary.decode.ImageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun TextView.setUserName(name: String) {
    if (name.length >= 2) {
        text = "${name[0]}*${name[name.length - 1]}"
    } else if (name.isNotEmpty()) {
        text = "${name[0]}*"
    }
}

fun TextView.setCardNumber(number: String) {
    if (number.length > 3) {
        text = "${number[0]}${number[1]}**************" +
            "${number[number.length - 2]}${number[number.length - 1]}"
    }
}

fun TextView.updateRefreshBtn(owner: LifecycleOwner) = owner.lifecycleScope.launchWhenCreated {
    val tv = this@updateRefreshBtn
    if (tv.text != "点击更新") return@launchWhenCreated
    repeat(60) { idx ->
        val time = 60 - idx
        tv.apply {
            text = "点击更新（$time）"
            setTextColor(Color.parseColor("#FF81d6b9"))
        }
        delay(1000L)
        if (time <= 1) {
            tv.apply {
                text = "点击更新"
                setTextColor(Color.parseColor("#FF00a76f"))
            }
        }
    }
}

class FuckXGCActivity : AppCompatActivity() {

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fuck_xgc)

        findViewById<View>(R.id.iv_bg)?.setOnClickListener {
            startActivity(
                Intent.parseUri(
                    "alipays://platformapi/startapp?appId=2021002116662889",
                    Intent.URI_INTENT_SCHEME
                )
            )
        }
        findViewById<ImageView>(R.id.iv_green_code)?.apply {
            setOnLongClickListener {
                PermissionUtils.permission(PermissionConstants.STORAGE)
                    .callback(object : SimpleCallback {
                        override fun onGranted() {
                            startActivityForResult(
                                Intent(Intent.ACTION_PICK).apply { this.type = "image/*" },
                                Constant.REQUEST_IMAGE
                            )
                        }

                        override fun onDenied() {
                        }
                    })
                    .request()
                true
            }
            lifecycleScope.launchWhenCreated {
                withContext(Dispatchers.IO) {
                    val str = prefs.getString(
                        "spk_green_code_decode_str",
                        null
                    ) ?: return@withContext null
                    return@withContext CodeCreator.createQRCode(str, 512, 512, null)
                }?.let {
                    this@apply.setImageBitmap(it)
                }
            }
        }
        findViewById<View>(R.id.btn_scan)?.apply {
            setOnClickListener {
                startActivity(Intent(this@FuckXGCActivity, FuckXGCScanActivity::class.java))
            }
            setOnLongClickListener {
                // "alipays://platformapi/startapp?saId=10000007"
                startActivity(Intent(this@FuckXGCActivity, FuckXGCLocationActivity::class.java))
                true
            }
        }

        findViewById<TextView>(R.id.tv_top_marquee)?.run {
            fun updateTopMarquee() {
                val str = prefs.getString("spk_top_marquee", null)
                    ?: "场所码一定要扫吗？被赋黄码怎么办......四川疾控专家权威解答"
                val strList = mutableListOf<String>()
                repeat(60) {
                    strList += str
                }
                text = strList.joinToString(separator = "    ")
                post { requestFocus() }
            }
            updateTopMarquee()

            setOnClickListener {
                val inputEditTextField = EditText(this@FuckXGCActivity).apply {
                    setText(
                        prefs.getString("spk_top_marquee", null)
                            ?: "场所码一定要扫吗？被赋黄码怎么办......四川疾控专家权威解答"
                    )
                }
                AlertDialog.Builder(this@FuckXGCActivity)
                    .setTitle("自定义顶部提示")
                    .setView(inputEditTextField)
                    .setPositiveButton("确定") { _, _ ->
                        val editTextInput = inputEditTextField.text.toString()
                        prefs.edit {
                            putString("spk_top_marquee", editTextInput)
                        }
                        updateTopMarquee()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        }

        findViewById<TextView>(R.id.tv_username)?.run {
            val name = prefs.getString("spk_username", null) ?: "习*泽"
            setUserName(name)
            setOnClickListener {
                val inputEditTextField = EditText(this@FuckXGCActivity).apply {
                    hint = "例如：习明泽，只需输入习泽"
                    setText(this@run.text)
                }
                AlertDialog.Builder(this@FuckXGCActivity)
                    .setTitle("填写姓名")
                    .setMessage("无需全名，只用首尾两个字即可（两个字名字只需输入姓）")
                    .setView(inputEditTextField)
                    .setPositiveButton("确定") { _, _ ->
                        val editTextInput = inputEditTextField.text.toString()
                        prefs.edit {
                            putString("spk_username", editTextInput)
                        }
                        setUserName(editTextInput)
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        }
        findViewById<TextView>(R.id.tv_card_number)?.run {
            val number = prefs.getString("spk_card_number", null) ?: "11**************01"
            setCardNumber(number)
            setOnClickListener {
                val inputEditTextField = EditText(this@FuckXGCActivity).apply {
                    hint = "例如：11**************01，只需输入1101"
                    setText(this@run.text)
                }
                AlertDialog.Builder(this@FuckXGCActivity)
                    .setTitle("填写身份证号")
                    .setMessage("无需完整号码，只用首两位和末两位即可")
                    .setView(inputEditTextField)
                    .setPositiveButton("确定") { _, _ ->
                        val editTextInput = inputEditTextField.text.toString()
                        prefs.edit {
                            putString("spk_card_number", editTextInput)
                        }
                        setCardNumber(editTextInput)
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        }

        updateTime()

        findViewById<MarqueeView>(R.id.marquee_view)?.apply {
            repeat(60) {
                val view = View.inflate(this@FuckXGCActivity, R.layout.item_horscrollview, null)
                this.addViewInQueue(view)
            }
            this.setScrollSpeed(6)
            this.setScrollDirection(MarqueeView.RIGHT_TO_LEFT)
            this.setViewMargin(0)
            this.startScroll()
        }

        val nowTime = System.currentTimeMillis()
        findViewById<TextView>(R.id.tv_time_refresh)?.apply {
            val sdf = SimpleDateFormat("MM/dd HH时", Locale.getDefault())
            val date = Date(
                nowTime
                    - TimeUnit.HOURS.toMillis(Random(nowTime).nextLong(10, 12))
            )
            text = sdf.format(date)
        }
        findViewById<TextView>(R.id.btn_time_refresh)?.apply {
            text = "点击更新"
            setTextColor(Color.parseColor("#FF00a76f"))
            setOnClickListener { updateRefreshBtn(this@FuckXGCActivity) }
        }
        findViewById<TextView>(R.id.tv_city_refresh)?.apply {
            val sdf = SimpleDateFormat("MM/dd HH时", Locale.getDefault())
            val date = Date(nowTime)
            text = sdf.format(date)
        }

        findViewById<View>(R.id.btn_track_card)?.setOnClickListener {
            startActivity(Intent(this, FuckTrackCardActivity::class.java))
        }
    }

    private fun updateTime() = lifecycleScope.launchWhenCreated {
        repeat(3600) {
            val sdf1 = SimpleDateFormat("HH:mm:", Locale.getDefault())
            val sdf2 = SimpleDateFormat("ss", Locale.getDefault())
            val sdf3 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = Date(System.currentTimeMillis())
            findViewById<TextView>(R.id.tv_time_hhmm)?.text = sdf1.format(date)
            findViewById<TextView>(R.id.tv_time_ss)?.text = sdf2.format(date)
            findViewById<TextView>(R.id.tv_time_date)?.text = sdf3.format(date)
            delay(1000L)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return
        if (requestCode == Constant.REQUEST_IMAGE && resultCode == RESULT_OK) {
            val path = ImageUtil.getImageAbsolutePath(this, data.data)
            DecodeImgThread(path, object : DecodeImgCallback {
                override fun onImageDecodeSuccess(result: Result) {
                    prefs.edit {
                        putString("spk_green_code_decode_str", result.text)
                    }
                    val img = CodeCreator.createQRCode(result.text, 512, 512, null)
                    lifecycleScope.launchWhenCreated {
                        findViewById<ImageView>(R.id.iv_green_code)?.setImageBitmap(img)
                    }
                }

                override fun onImageDecodeFailed() {
                }
            }).start()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        FuckApp.fuckSelf()
    }
}
