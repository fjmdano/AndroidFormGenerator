package com.example.formapplication

import android.os.Build
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_form_input.view.*
import org.json.JSONArray
import org.json.JSONObject
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.core.view.ViewCompat
import androidx.core.widget.TextViewCompat


class RecyclerAdapter(private val inputJsonArray: JSONArray) : RecyclerView.Adapter<RecyclerAdapter.FormItemHolder>() {

    lateinit var inflatedView: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.FormItemHolder {
        inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_form_input, parent, false)
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

    fun getValues() {
        RecyclerViewModel.instance.getValues(inflatedView)
    }

    class FormItemHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private var formItemObject: JSONObject? = null
        private var formItemArray: JSONArray? = null

        //3
        init {
            view.setOnClickListener(this)
        }

        fun bindFormItem(formItem: JSONArray) {
            // All items in the array will be displayed in a single row
            // Weight can be specified
            this.formItemArray = formItem

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
            this.formItemObject = formItem
            if (!formItem.has(LABEL_TYPE)) {
                return
            }
            when (formItem[LABEL_TYPE] as String) {
                TYPE_EDIT -> bindEditTextItem(formItem, linearLayout)
                TYPE_DROPDOWN -> bindSpinnerItem(formItem, linearLayout)
                TYPE_CHECKBOX -> bindCheckBoxItem(formItem, linearLayout)
                TYPE_AGREEMENT -> bindAgreementItem(formItem, linearLayout)
            }
        }

        private fun bindEditTextItem(editTextItem: JSONObject, linearLayout: LinearLayout = view.ll_item_row) {
            //add EditText
            val editText = EditText(view.context)
            val linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            //Set hint, note, type
            if (editTextItem.has(LABEL_HINT)) {
                editText.hint = editTextItem[LABEL_HINT] as String
            }
            if (editTextItem.has(LABEL_INPUTTYPE)) {
                when (editTextItem[LABEL_INPUTTYPE]) {
                    "number" -> editText.inputType = InputType.TYPE_CLASS_NUMBER
                    "date" -> editText.inputType = InputType.TYPE_CLASS_DATETIME
                    else -> editText.inputType = InputType.TYPE_CLASS_TEXT
                }
            }
            // Set ID for editText
            if (editTextItem.has(LABEL_ID)) {
                editText.id = RecyclerViewModel.instance.generateId(editTextItem[LABEL_ID] as String)
            }

            // Add editText to view
            editText.layoutParams = linearLayoutParam
            linearLayout.addView(editText)
            Log.i(TAG, "[CHECKID][" + editText.hint + "]: id: " + editText.id)

            if (editTextItem.has(LABEL_NOTE)) {
                val textView = TextView(view.context)
                textView.text = editTextItem[LABEL_NOTE] as String
                TextViewCompat.setTextAppearance(textView, android.R.style.TextAppearance_Small)

                textView.layoutParams = linearLayoutParam
                linearLayout.addView(textView)
                Log.i(TAG, "[CHECKID][" + textView.text + "]: id: " + textView.id)
            }
        }

        private fun bindSpinnerItem(spinnerItem: JSONObject, linearLayout: LinearLayout = view.ll_item_row) {
            val paramChild = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5F)
            val paramParent = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1F)

            // Layout: linearlayout with hint on the left and dropdown on the right
            val subLayout = LinearLayout(view.context)
            subLayout.orientation = LinearLayout.HORIZONTAL
            subLayout.layoutParams = paramParent

            // Add textview for the hint
            if (spinnerItem.has(LABEL_HINT)) {
                val hintTextView = TextView(view.context)
                hintTextView.text = spinnerItem[LABEL_HINT].toString()
                hintTextView.layoutParams = paramChild
                TextViewCompat.setTextAppearance(hintTextView, android.R.style.TextAppearance_Medium)
                subLayout.addView(hintTextView)
            }

            // Add spinner for the dropdown
            if (spinnerItem.has(LABEL_CHOICES)) {
                val spinner = Spinner(view.context)
                val choices = mutableListOf<String>()
                val rawChoices = spinnerItem.getJSONArray(LABEL_CHOICES)
                for (i in 0 until rawChoices.length()) {
                    choices.add(rawChoices.get(i).toString())
                }

                val dataAdapter = ArrayAdapter<String>(view.context, android.R.layout.simple_spinner_item, choices)
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.setAdapter(dataAdapter)

                // Set ID for editText
                if (spinnerItem.has(LABEL_ID)) {
                    spinner.id = RecyclerViewModel.instance.generateId(spinnerItem[LABEL_ID] as String)
                }
                spinner.layoutParams = paramChild
                Log.i(TAG,"[CHECKID][spinner]: id: " + spinner.id)
                subLayout.addView(spinner)

            }
            // Add the sublayout to the linearlayout
            linearLayout.addView(subLayout)
        }

        private fun bindCheckBoxItem(checkBoxItem: JSONObject, linearLayout: LinearLayout = view.ll_item_row) {
            val linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            // Create sublayout
            val subLayout = LinearLayout(view.context)
            subLayout.orientation = LinearLayout.VERTICAL
            subLayout.layoutParams = linearLayoutParam

            if (checkBoxItem.has(LABEL_HINT)) {
                // Create TextView
                val textView = TextView(view.context)
                textView.text = checkBoxItem[LABEL_HINT] as String
                textView.layoutParams = linearLayoutParam
                TextViewCompat.setTextAppearance(textView, android.R.style.TextAppearance_Medium)
                subLayout.addView(textView)
            }

            if (checkBoxItem.has(LABEL_CHOICES)) {
                val rawChoices = checkBoxItem.getJSONArray(LABEL_CHOICES)
                for (i in 0 until rawChoices.length()) {
                    //Create checkbox for each
                    val checkBox = CheckBox(view.context)
                    checkBox.text = rawChoices.get(i).toString()
                    checkBox.layoutParams = linearLayoutParam
                    checkBox.setOnClickListener(View.OnClickListener { v ->
                        val checked: Boolean = (v as CheckBox).isChecked
                        var text = "[" + v.text + "]"
                        if (checked) {
                            text += " checked"
                        } else {
                            text += " unchecked"
                        }
                        Toast.makeText(v.context, text, Toast.LENGTH_SHORT).show()
                    })
                    if (checkBoxItem.has(LABEL_ID)) {
                        checkBox.id = RecyclerViewModel.instance.generateId(checkBoxItem[LABEL_ID] as String)
                    }
                    subLayout.addView(checkBox)

                    Log.i(TAG,"[CHECKID][" + checkBox.text + "]: id: " + checkBox.id)
                }
                // Add the sublayout to the linearlayout
                linearLayout.addView(subLayout)
            }
        }

        private fun bindAgreementItem(agreementItem: JSONObject, linearLayout: LinearLayout = view.ll_item_row) {
            val linearLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            if (agreementItem.has(LABEL_HINT)) {
                val checkBox = CheckBox(view.context)
                checkBox.text = agreementItem[LABEL_HINT] as String
                if (agreementItem.has(LABEL_ID)) {
                    //checkBox.id = generateId(agreementItem[LABEL_ID] as String)

                }
                checkBox.layoutParams = linearLayoutParam

                Log.i(TAG,"[CHECKID][" + checkBox.text + "]: id: " + checkBox.id)
                linearLayout.addView(checkBox)
            }
        }

        override fun onClick(v: View) {
            Toast.makeText(v.context, "Clicked!", Toast.LENGTH_SHORT).show()
        }

        companion object {
            val LABEL_ID = "id"
            val LABEL_HINT = "hint"
            val LABEL_TYPE = "type"
            val LABEL_INPUTTYPE = "inputType"
            val LABEL_NOTE = "note"
            val LABEL_CHOICES = "choices"
            val LABEL_LINK = "link"

            val TYPE_EDIT = "edit"
            val TYPE_DROPDOWN = "dropdown"
            val TYPE_CHECKBOX = "checkbox"
            val TYPE_AGREEMENT = "agreement"

            private val TAG = "MyActivity"
        }
    }


    companion object {
        private val TAG = "MyActivity"
    }

}