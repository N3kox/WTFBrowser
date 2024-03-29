package com.example.wtfbrowser.page.setting

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.wtfbrowser.R
import com.example.wtfbrowser.page.browser.IBrowser
import com.example.wtfbrowser.utils.SharedPreferencesUtils

class SettingDialogKt : DialogFragment() {

    private lateinit var noPictureMode: CheckBox
    private lateinit var exitApp: TextView
    private lateinit var history: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 对话框全屏模式，去掉屏幕边界padding
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogView: View = inflater.inflate(R.layout.layout_setting_dialog, container, false)

        val foldButton: ImageView = dialogView.findViewById(R.id.nav_fold)
        foldButton.setOnClickListener {
            dismiss()
        }

        noPictureMode = dialogView.findViewById(R.id.no_picture_mode)
        noPictureMode.setOnCheckedChangeListener { buttonView, isChecked ->
            val sp: SharedPreferences? = SharedPreferencesUtils.getSettingSP(context)
            sp?.let {
                val editor: SharedPreferences.Editor = it.edit()
                if (isChecked) {
                    editor.putBoolean(SharedPreferencesUtils.KEY_NO_PIC_MODE, true)
                } else {
                    editor.putBoolean(SharedPreferencesUtils.KEY_NO_PIC_MODE, false)
                }
                editor.apply()
            }
        }

        history = dialogView.findViewById(R.id.history)
        history.setOnClickListener {
            if (context is IBrowser) {
                (context as IBrowser).provideNavController().showHistory()
            }
            dismiss()
        }

        exitApp = dialogView.findViewById(R.id.exit_app)
        exitApp.setOnClickListener {
            android.os.Process.killProcess(android.os.Process.myPid())
        }

        return dialogView
    }

    override fun onResume() {
        super.onResume()
        // 设置对话框在屏幕底部
        val param: WindowManager.LayoutParams? = dialog?.window?.attributes
        param?.let {
            it.gravity = Gravity.BOTTOM
            dialog?.window?.attributes = it
        }

        val sp: SharedPreferences? = SharedPreferencesUtils.getSettingSP(context)
        sp?.let {
            noPictureMode.isChecked = it.getBoolean(SharedPreferencesUtils.KEY_NO_PIC_MODE, false)
        }
    }
}