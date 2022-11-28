package com.fuckxgc.lovegreencode

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class FuckXGCLocationActivity : AppCompatActivity() {

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fuck_xgclocation)
        findViewById<View>(R.id.iv_title)?.setOnClickListener {
            onBackPressed()
        }

        updateCurLoc()

        findViewById<View>(R.id.lyt_loc_info)?.apply {
            fun showImportDialog() {
                val inputEditTextField = EditText(this@FuckXGCLocationActivity).apply {
                    hint = "每个地点信息内不同字段用@#分隔，不同地点信息之间用&*分隔，没理解的话可以先手动新建几个地点再来查看导入/导出"
                    height = ConvertUtils.dp2px(200f)

                    val locList = mutableListOf<String>()
                    (prefs.getStringSet("spk_loc_info_ks", null) ?: emptySet()).forEach {
                        locList += prefs.getString(it, null) ?: ""
                    }
                    setText(locList.joinToString("&*"))
                }
                AlertDialog.Builder(this@FuckXGCLocationActivity)
                    .setTitle("导入/导出全部地点")
                    .setMessage("将之前按格式导出的地点数据复制到此处，可进行批量导入")
                    .setView(inputEditTextField)
                    .setPositiveButton("导出到剪贴板") { _, _ ->
                        ClipboardUtils.copyText(inputEditTextField.text.toString())
                        ToastUtils.showLong("数据已复制到剪贴板，请及时备份")
                    }
                    .setNegativeButton("批量导入") { _, _ ->
                        runCatching {
                            val str = inputEditTextField.text.toString()
                            if (str.isBlank()) return@runCatching
                            prefs.edit {
                                val ks = mutableSetOf<String>()
                                str.split("&*").forEach {
                                    val vs = it.split("@#")
                                    putString(vs[0], it)
                                    ks += vs[0]
                                }
                                remove("spk_loc_info_ks")
                                putStringSet("spk_loc_info_ks", ks)
                                putString("spk_cur_loc_k", ks.first())
                            }
                            updateCurLoc()
                        }
                    }
                    .show()
            }

            setOnLongClickListener {
                val kList = mutableListOf<String>().apply {
                    addAll(prefs.getStringSet("spk_loc_info_ks", null) ?: emptySet())
                }
                AlertDialog.Builder(
                    this@FuckXGCLocationActivity
                ).setItems(kList.toTypedArray()) { _, which ->
                    showEditLocInfoDialog(kList[which])
                }.setPositiveButton("新建") { _, _ ->
                    showEditLocInfoDialog()
                }.setNegativeButton("导入/导出") { _, _ ->
                    showImportDialog()
                }.setTitle("选择当前地点").show()
                true
            }
        }

        findViewById<TextView>(R.id.tv_loc_username)?.apply {
            val name = prefs.getString("spk_username", null) ?: "习*泽"
            setUserName(name)
        }
        findViewById<TextView>(R.id.tv_loc_card_number)?.apply {
            val number = prefs.getString("spk_card_number", null) ?: "11**************01"
            setCardNumber(number)
        }

        updateTime()

        val nowTime = System.currentTimeMillis()
        findViewById<TextView>(R.id.tv_loc_time_refresh)?.apply {
            val sdf = SimpleDateFormat("MM/dd HH时", Locale.getDefault())
            val date = Date(nowTime - TimeUnit.HOURS.toMillis(Random(nowTime).nextLong(10, 12)))
            text = sdf.format(date)
        }
        findViewById<TextView>(R.id.btn_loc_time_refresh)?.apply {
            text = "点击更新"
            setTextColor(Color.parseColor("#FF00a76f"))
            setOnClickListener { updateRefreshBtn(this@FuckXGCLocationActivity) }
        }
        findViewById<TextView>(R.id.tv_loc_city_refresh)?.apply {
            val sdf = SimpleDateFormat("MM/dd HH时", Locale.getDefault())
            val date = Date(nowTime)
            text = sdf.format(date)
        }
    }

    private fun showEditLocInfoDialog(spKey: String? = null) {
        var dialog: AlertDialog? = null

        fun showData(window: Window, oldSpKey: String) {
            val oldV = prefs.getString(oldSpKey, null) ?: return
            val oldVs = oldV.split("@#")
            runCatching {
                window.findViewById<EditText>(R.id.edt_loc_name)?.apply {
                    setText(oldVs[0])
                }
                window.findViewById<EditText>(R.id.edt_loc_type)?.apply { setText(oldVs[1]) }
                window.findViewById<EditText>(R.id.edt_loc_city)?.apply { setText(oldVs[2]) }
                window.findViewById<EditText>(R.id.edt_loc_addr)?.apply { setText(oldVs[3]) }
            }
        }

        fun saveData(window: Window, oldSpKey: String? = null) {
            val ks = mutableSetOf<String>().apply {
                addAll(prefs.getStringSet("spk_loc_info_ks", null) ?: emptySet())
            }
            val newV = StringBuilder()
            var newLocName = ""
            window.findViewById<EditText>(R.id.edt_loc_name)?.apply {
                newLocName = this.text.toString()
                newV.append(newLocName).append("@#")
            }
            window.findViewById<EditText>(R.id.edt_loc_type)?.apply {
                newV.append(this.text.toString()).append("@#")
            }
            window.findViewById<EditText>(R.id.edt_loc_city)?.apply {
                newV.append(this.text.toString()).append("@#")
            }
            window.findViewById<EditText>(R.id.edt_loc_addr)?.apply {
                newV.append(this.text.toString())
            }
            val newK = oldSpKey.takeIf { it == newLocName } ?: newLocName
            if (newK.isBlank()) return
            prefs.edit {
                if (oldSpKey != null) {
                    if (oldSpKey != newK) {
                        remove(oldSpKey)
                        ks -= oldSpKey
                        ks += newK
                    }
                } else {
                    ks += newK
                }
                putStringSet("spk_loc_info_ks", ks)
                putString("spk_cur_loc_k", newK)
                putString(newK, newV.toString())
            }
            updateCurLoc()
        }

        dialog = AlertDialog.Builder(this).setView(
            R.layout.layout_loc_info_create
        ).setPositiveButton("保存并选中") { _, _ ->
            dialog?.window?.let { saveData(it, spKey) }
        }.setNegativeButton(if (spKey == null) "取消" else "删除") { _, _ ->
            if (spKey != null) prefs.edit {
                remove(spKey)
                val newSet = mutableSetOf<String>().apply {
                    addAll(prefs.getStringSet("spk_loc_info_ks", null) ?: emptySet())
                }
                newSet -= spKey
                putStringSet("spk_loc_info_ks", newSet)
                if (spKey == prefs.getString("spk_cur_loc_k", null)) {
                    putString("spk_cur_loc_k", newSet.first())
                }
            }
        }.create().apply {
            show()
            window?.let { w -> spKey?.let { k -> showData(w, k) } }
        }
    }

    private fun updateTime() = lifecycleScope.launchWhenCreated {
        repeat(3600) {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = Date(System.currentTimeMillis())
            findViewById<TextView>(R.id.tv_loc_time)?.text = sdf.format(date)
            delay(1000L)
        }
    }

    private fun updateCurLoc() {
        val k = prefs.getString("spk_cur_loc_k", null) ?: return
        val v = prefs.getString(k, null) ?: return
        val vs = v.split("@#")
        runCatching {
            findViewById<TextView>(R.id.tv_loc_name)?.text = vs[0]
            findViewById<TextView>(R.id.tv_loc_type)?.text = vs[1]
            findViewById<TextView>(R.id.tv_loc_city)?.text = vs[2]
            findViewById<TextView>(R.id.tv_loc_addr)?.text = vs[3]
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, FuckXGCActivity::class.java))
    }
}
