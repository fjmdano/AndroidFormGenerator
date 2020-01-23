package com.example.formapplication

import android.annotation.TargetApi
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerAdapter
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
        adapter = RecyclerAdapter(inputJsonArray)
        recyclerView.adapter = adapter

        /*
        print(inputJsonArray)
        print(inputJsonArray.length())
        for (i in 0 until inputJsonArray.length()) {
            var textInput = inputJsonArray.getJSONObject(i)
            print(textInput["hint"] as String)
        }
        */
    }
}
