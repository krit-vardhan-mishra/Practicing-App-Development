package com.just_for_fun.taskview

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView

object ToastUtil {

    fun showCustomToast(context: Context, message: String) {
        val dialog = Dialog(context)
        val view: View = LayoutInflater.from(context).inflate(R.layout.custom_toast_layout, null)
        val textView: TextView = view.findViewById(R.id.toast_message)
        textView.text = message
        dialog.setContentView(view)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)

        val params = dialog.window?.attributes
        params?.gravity = Gravity.BOTTOM
        params?.y = 150
        dialog.window?.attributes = params

        dialog.show()

        Handler().postDelayed({
            dialog.dismiss()
        }, 2000)
    }
}