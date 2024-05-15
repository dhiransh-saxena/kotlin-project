package com.example.exampleapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: MyRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var noContentTextView: TextView
    private lateinit var myTextField: EditText
    private lateinit var button1: Button
    private lateinit var button2: Button
    private var lastSelectedPosition = RecyclerView.NO_POSITION
    private var items = mutableListOf<DataItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        items = dbHelper.getAllItems().mapIndexed { index, item -> DataItem(index.toLong(), item) }.toMutableList()

        myTextField = findViewById(R.id.myTextField)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        recyclerView = findViewById(R.id.recyclerView)
        noContentTextView = findViewById(R.id.noContentTextView)

        adapter = MyRecyclerAdapter(items, object : MyRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedItem = items[position].item
                myTextField.setText(selectedItem)
                button1.text = "Edit"
                button2.text = "Delete"
                lastSelectedPosition = position
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        button1.setOnClickListener {
            val text = myTextField.text.toString()
            if (text.isNotEmpty()) {
                if (button1.text == "Submit") {
                    val id = dbHelper.addItem(text)
                    items.add(DataItem(id, text))
                } else if (button1.text == "Edit") {
                    val position = lastSelectedPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = items[position]
                        dbHelper.updateItem(item.id, text)
                        items[position] = item.copy(item = text)
                        adapter.notifyItemChanged(position)
                        recyclerView.clearFocus()
                    }
                }
                updateListVisibility()
                adapter.notifyDataSetChanged()
                myTextField.text.clear()
                button1.text = "Submit"
                button2.text = "Clear"
            } else {
                Toast.makeText(this, "Input cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        button2.setOnClickListener {
            if (button2.text == "Clear") {
                myTextField.text.clear()
            } else if (button2.text == "Delete") {
                val position = lastSelectedPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    dbHelper.deleteItem(item.id)
                    items.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    recyclerView.clearFocus()
                }
            }
            updateListVisibility()
            adapter.notifyDataSetChanged()
            myTextField.text.clear()
            button1.text = "Submit"
            button2.text = "Clear"
        }

        updateListVisibility()
    }

    private fun updateListVisibility() {
        if (items.isEmpty()) {
            recyclerView.visibility = View.GONE
            noContentTextView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            noContentTextView.visibility = View.GONE
        }
    }
}
