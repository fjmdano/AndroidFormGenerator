package com.example.formapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: FormRecyclerAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openJsonFile()
    }

    fun openJsonFile() {
        val json_string = applicationContext.assets.open("form_inputs.json").bufferedReader().use{
            it.readText()
        }
        print(json_string)

        val jsonObject = JSONObject(json_string)
        val inputJsonArray = jsonObject.getJSONArray("edittextbox")
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = linearLayoutManager
        adapter = FormRecyclerAdapter(inputJsonArray)
        recyclerView.adapter = adapter
    }

    fun onClickSubmitButton(view: View) {
        Toast.makeText(this, "Submit!", Toast.LENGTH_SHORT).show()
        adapter.getValues()
    }
}
