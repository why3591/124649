package com.fuckxgc.lovegreencode

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FuckTrackCardActivity : AppCompatActivity() {

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fuck_track_card)

        findViewById<View>(R.id.iv_bg)?.setOnClickListener {
            startActivity(
                Intent.parseUri(
                    "alipays://platformapi/startapp?appId=2021002116662889",
                    Intent.URI_INTENT_SCHEME
                )
            )
        }
        findViewById<View>(R.id.view_title_bar)?.setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.tv_track_phone_num)?.apply {
            fun updatePhoneNum() {
                prefs.getString("spk_track_phone_num", null)?.let {
                    runCatching {
                        this.text =
                            "${it.substring(0, 3)}****${it.substring(it.length - 4, it.length)}的动态行程卡"
                    }
                }
            }

            setOnClickListener {
                val inputEditTextField = EditText(this@FuckTrackCardActivity).apply {
                    hint = "只需要填写开头3位和末尾4位数字即可"
                    prefs.getString("spk_track_phone_num", null)?.let {
                        this.setText(it)
                    }
                }
                AlertDialog.Builder(this@FuckTrackCardActivity)
                    .setTitle("填写手机号")
                    .setView(inputEditTextField)
                    .setPositiveButton("确定") { _, _ ->
                        val editTextInput = inputEditTextField.text.toString()
                        prefs.edit {
                            putString("spk_track_phone_num", editTextInput)
                        }
                        updatePhoneNum()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
            updatePhoneNum()
        }

        findViewById<TextView>(R.id.tv_track_update_time)?.apply {
            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())
            val date = Date(System.currentTimeMillis())
            this.text = "更新于：${sdf.format(date)}"
        }

        findViewById<TextView>(R.id.tv_track_city)?.apply {
            fun updateCity() {
                prefs.getString("spk_track_city", null)?.let { this.text = it }
            }

            setOnClickListener {
                val inputEditTextField = EditText(this@FuckTrackCardActivity).apply {
                    hint = "例：四川省成都市"
                    prefs.getString("spk_track_city", null)?.let { this.setText(it) }
                }
                AlertDialog.Builder(this@FuckTrackCardActivity)
                    .setTitle("填写省份城市")
                    .setView(inputEditTextField)
                    .setPositiveButton("确定") { _, _ ->
                        val editTextInput = inputEditTextField.text.toString()
                        prefs.edit {
                            putString("spk_track_city", editTextInput)
                        }
                        updateCity()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
            updateCity()
        }
    }
}
