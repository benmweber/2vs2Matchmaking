package com.example.spikeball

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.graphics.Color
import android.widget.*
import androidx.appcompat.app.AlertDialog

//import android.widget.Button
//import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {

    var mListOfGameTypes = arrayOf("Endlosspiel", "Turnier", "ShowStats")
    var mWhichGameType = 1
    var mPlayerList = mutableListOf<Player>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPlayerList.clear()

        recyclerViewerForPlayerList.layoutManager = LinearLayoutManager(this)
        recyclerViewerForPlayerList.adapter = MyRecyclerViewerAdapter(mPlayerList, this)
        gameTypeSpinner.onItemSelectedListener = this

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, mListOfGameTypes)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        gameTypeSpinner.adapter = aa


        initButtons()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        // use position to know the selected item
        mWhichGameType = position
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }

    //Button Functions
    //add onClickListeners for all Buttons

    fun initButtons(){

        //val startButton = findViewById<Button>(R.id.startButton)
        doButton.setOnClickListener {
            // Do something in response to button
            doSomeThing()
        }
        //val addPlayerButton = findViewById<Button>(R.id.addPlayerButton)
        addPlayerButton.setOnClickListener {
            // Do something in response to button
            addPlayer()
        }

        //val deletePlayersButton = findViewById<Button>(R.id.deletePlayersButton)
        deletePlayersButton.setOnClickListener {
            // Do something in response to button
            deletePlayers()
        }
    }

    fun addPlayer(){

        if(!playerName.text.isEmpty()){
            var name = playerName.text.toString()
            Toast.makeText(applicationContext,name,Toast.LENGTH_SHORT).show()
            playerName.text.clear()
            val pl = Player(name)
            mPlayerList.add(pl)
        }

    }

    fun deletePlayers(){

        //Toast.makeText(applicationContext,"Add Button Pressed",Toast.LENGTH_SHORT).show()
        // Initialize a new instance of
        val builder = AlertDialog.Builder(this@MainActivity)

        // Set the alert dialog title
        builder.setTitle("App background color")

        // Display a message on alert dialog
        builder.setMessage("Are you want to set the app background color to RED?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES"){ dialog, which ->
            // Do something when user press the positive button
            Toast.makeText(applicationContext,"Ok, we change the app background.",Toast.LENGTH_SHORT).show()

            // Change the app background color
            root_layout.setBackgroundColor(Color.RED)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    fun doSomeThing(){
        Toast.makeText(applicationContext,mWhichGameType.toString(),Toast.LENGTH_SHORT).show()
    }

    fun startEndlosspiel(){}
    fun startTurnier(){}
    fun showStats(){}

}
