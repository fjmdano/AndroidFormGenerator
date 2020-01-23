package com.example.formapplication

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_form_input.view.*
import org.json.JSONArray
import org.json.JSONObject


class RecyclerAdapter(private val inputJsonArray: JSONArray) : RecyclerView.Adapter<RecyclerAdapter.FormItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.FormItemHolder {
        val inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_form_input, parent, false)
        return FormItemHolder(inflatedView)
    }

    override fun getItemCount(): Int = inputJsonArray.length()

    override fun onBindViewHolder(holder: RecyclerAdapter.FormItemHolder, position: Int) {
        val element = inputJsonArray.get(position)
        if (inputJsonArray.get(position) is JSONObject) {
            // Element is a JSONObject
            val formItem = inputJsonArray.getJSONObject(position)
            holder.bindFormItem(formItem)
        } else {
            // Element is a JSONArray
            val formItem = inputJsonArray.getJSONArray(position)
            holder.bindFormItem(formItem)
        }
    }

    //1
    class FormItemHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        //2
        private var formItem: JSONObject? = null

        //3
        init {
            view.setOnClickListener(this)
        }

        fun bindFormItem(formItem: JSONArray) {
            view.ll_item_row.orientation = LinearLayout.HORIZONTAL
            for (i in 0 until formItem.length()) {
                val subLayout = LinearLayout(view.context)
                val linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                subLayout.orientation = LinearLayout.VERTICAL
                subLayout.layoutParams = linearLayoutParam

                val subItem = formItem.getJSONObject(i)
                bindFormItem(subItem, subLayout)
                view.ll_item_row.addView(subLayout)
            }

        }

        fun bindFormItem(formItem: JSONObject, linearLayout: LinearLayout = view.ll_item_row) {
            //add EditText
            val editText = EditText(view.context)
            lateinit var linearLayoutParam: LinearLayout.LayoutParams
            if (linearLayout == view.ll_item_row) {
                linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            } else {
                linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }

            this.formItem = formItem
            //Set hint, note, type
            if (formItem.has("hint")) {
                editText.hint = formItem["hint"] as String
            }
            if (formItem.has("type")) {
                when (formItem["type"]) {
                    "number" -> editText.inputType = InputType.TYPE_CLASS_NUMBER
                    "date" -> editText.inputType = InputType.TYPE_CLASS_DATETIME
                    else -> editText.inputType = InputType.TYPE_CLASS_TEXT
                }
            }
            //Add editText to view
            editText.layoutParams = linearLayoutParam
            linearLayout.addView(editText)

            if (formItem.has("note")) {
                val textView = TextView(view.context)
                textView.text = formItem["note"] as String

                textView.layoutParams = linearLayoutParam
                linearLayout.addView(textView)
            }
        }

        //4
        override fun onClick(v: View) {
            Toast.makeText(v.context, "Clicked!", Toast.LENGTH_SHORT).show()
        }

        companion object {
            //5
            private val PHOTO_KEY = "PHOTO"
        }
    }

}