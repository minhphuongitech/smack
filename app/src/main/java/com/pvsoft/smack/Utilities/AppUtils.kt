package com.pvsoft.smack.Utilities

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import com.pvsoft.smack.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by minhp on 1/13/2018.
 */
class AppUtils {
    companion object {
        fun errorToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        fun hideKeyboard(context: Context, activity: Activity) {
            val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputManager.isAcceptingText) {
                inputManager.hideSoftInputFromWindow(activity.currentFocus.windowToken, 0)
            }
        }

        fun showAlertDialog(context: Context, activity: Activity, positiveText: String, negativeText: String, onClickPositiveButtonListener: (Boolean, View) -> Unit) {
            val builder = android.app.AlertDialog.Builder(context)
            val dialogView = activity.layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                    .setPositiveButton(positiveText) { dialogInterface, i ->
                        onClickPositiveButtonListener(true, dialogView)
                    }
                    .setNegativeButton(negativeText) { dialogInterface, i ->
                        onClickPositiveButtonListener(false, dialogView)
                    }.show()
        }

        /***
         * 2017-09-11T01:16:13.858Z
         * Monday 4:35 PM
         */
        fun formatDateString(isoString: String): String {
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            var convertedDate = Date()
            try {
                convertedDate = isoFormatter.parse(isoString)
            } catch (e: ParseException) {
                Log.d("PARSE", "Cannot parse date")
            }

            val outDateString = SimpleDateFormat("E, h:mm a", Locale.getDefault())
            return outDateString.format(convertedDate)
        }
    }
}