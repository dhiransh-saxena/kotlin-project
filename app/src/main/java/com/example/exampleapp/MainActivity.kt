package com.example.exampleapp
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.*

class MainActivity : AppCompatActivity() {

    private val items = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listView: ListView
    private lateinit var noContentTextView: TextView
    private lateinit var myTextField: EditText
    private lateinit var button1: Button
    private lateinit var button2: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myTextField = findViewById(R.id.myTextField)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        listView = findViewById(R.id.listView)
        noContentTextView = findViewById(R.id.noContentTextView)

        adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items) {
            override fun getCount(): Int {
                return super.getCount().coerceAtLeast(1)
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getView(position, convertView, parent)
            }
        }

        var lastSelectedPosition = ListView.INVALID_POSITION

        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem = items[position]
            myTextField.setText(selectedItem)
            button1.text = "Edit"
            button2.text = "Delete"
            lastSelectedPosition = position
        }

        button1.setOnClickListener {
            val text = myTextField.text.toString()
            if (text.isNotEmpty()) {

                if (button1.text == "Submit") {
                    items.add(text)
                } else if (button1.text == "Edit") {
                    val position = lastSelectedPosition
                    if (position != ListView.INVALID_POSITION) {
                        items[position] = text
                        adapter.notifyDataSetChanged()
                        listView.clearChoices()
                    }
                }
                updateListVisibility()
                adapter.notifyDataSetChanged()
                myTextField.text.clear()
                button1.text = "Submit"
                button2.text = "Clear"

            }else{

                Toast.makeText(this, "Input cannot be empty", Toast.LENGTH_SHORT).show()

            }

        }

        button2.setOnClickListener {
            if (button2.text == "Clear") {
                myTextField.text.clear()
            } else if (button2.text == "Delete") {
                val position = lastSelectedPosition
                if (position != ListView.INVALID_POSITION) {
                    items.removeAt(position)
                    listView.clearChoices()
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
            listView.visibility = View.GONE
            noContentTextView.visibility = View.VISIBLE
        } else {
            listView.visibility = View.VISIBLE
            noContentTextView.visibility = View.GONE
        }
    }
}