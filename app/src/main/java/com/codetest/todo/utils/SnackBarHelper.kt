package com.codetest.todo.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class SnackBarHelper {
    companion object {
        fun infoSnackBar(contextView: View, text: String, action: String?,callback:View.OnClickListener?=null) {
            Snackbar.make(contextView, text, Snackbar.LENGTH_LONG)
                .setAction(action) {
                    callback?.onClick(it)
                    // Responds to click on the action
                }
                .show()
        }
    }
}