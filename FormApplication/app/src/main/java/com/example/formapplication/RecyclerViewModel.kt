package com.example.formapplication

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.core.view.ViewCompat

class RecyclerViewModel() {

    private var keyMap: MutableMap<String, Int> = mutableMapOf()
    private val valueMap: MutableMap<String, Any> = mutableMapOf()

    companion object {
        private val TAG = "Model"
        var instance = RecyclerViewModel()
    }

    fun generateId(id: String): Int {
        val idNum = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            View.generateViewId()
        } else {
            ViewCompat.generateViewId()
        }
        // Map the id and idNum in keyMap
        keyMap.put(id, idNum)
        return idNum
    }


    fun getValues(view: View) {
        Log.i(TAG, "[CHECKID][START][GET VALUES!!!]: " + keyMap.size)
        for ((key, value) in keyMap) {
            // Get View
            val v = view.findViewById<View>(value)
            if (v is EditText) {
                Log.i(TAG, "[CHECKID][" + key + "]: id: " + v.text)
            } else if (v is Spinner) {
                Log.i(TAG, "[CHECKID][" + key + "]: id: " + v.selectedItem)
            } else if (v is CheckBox) {
                Log.i(TAG, "[CHECKID][" + key + "]: id: " + v.isChecked)
            } else {
                Log.i(TAG, "[CHECKID][not sure of type ;( ")
            }
        }
        Log.i(TAG, "[CHECKID][END][GET VALUES!!!]")
    }
}