package com.example.formgenerator

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.core.view.ViewCompat

class FormRecyclerModel() {

    private val keyMap: MutableMap<String, Int> = mutableMapOf()
    private val valueMap: MutableMap<String, Any> = mutableMapOf()

    companion object {
        private val TAG = "FormRecyclerModel"
        val TYPE_EDIT = "edit"
        val TYPE_DROPDOWN = "dropdown"
        val TYPE_CHECKBOX = "checkbox"
        val TYPE_AGREEMENT = "agreement"
    }

    fun updateValueInMap(id: String, value: Any, type: String) {
        // Check the type is same
        when (type) {
            TYPE_EDIT, TYPE_DROPDOWN -> {
                if ((valueMap[id] !is String) || (value !is String)) {
                    Log.i(TAG, "[updateValueInMap] Not correct format")
                    return
                }
                valueMap[id] = value
            }
            TYPE_CHECKBOX -> {
                if ((valueMap[id] !is MutableList<*>) || (value !is String)) {
                    Log.i(TAG, "[updateValueInMap] Not correct format")
                    return
                }
                // If value in map, add. Else, delete
                if (value in (valueMap[id] as MutableList<*>)) {
                    (valueMap[id] as MutableList<*>).remove(value)
                } else {
                    (valueMap[id] as MutableList<String>).add(value)
                }
            }
            TYPE_AGREEMENT -> {
                if ((valueMap[id] !is Boolean) || (value !is Boolean)) {
                    Log.i(TAG, "[updateValueInMap] Not correct format")
                    return
                }
                valueMap[id] = value
            }
        }
    }

    fun generateId(id: String, type: String, checkBoxNumber: Int = 0): Int {
        val idNum = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            View.generateViewId()
        } else {
            ViewCompat.generateViewId()
        }
        // Map the id and idNum in keyMap
        if (type == TYPE_CHECKBOX) {
            //Add number to the id for better identification
            keyMap.put(id + checkBoxNumber, idNum)
        } else {
            keyMap.put(id, idNum)
        }
        when (type) {
            TYPE_EDIT -> valueMap.put(id, "")
            TYPE_CHECKBOX -> {
                if (id !in valueMap) {
                    valueMap.put(id, mutableListOf<String>())
                }
            }
            TYPE_DROPDOWN -> valueMap.put(id, "")
            TYPE_AGREEMENT -> valueMap.put(id, false)
        }
        return idNum
    }


    fun getValues(): MutableMap<String, Any> {
        for ((key, value) in valueMap) {
            Log.i(TAG, "[" + key + "]: value: " + value.toString())
        }

        return valueMap
    }
}