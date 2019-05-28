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
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.AlarmClock.EXTRA_MESSAGE

//import android.widget.Button
//import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {

    var mListOfGameTypes = arrayOf("Endlosspiel", "Turnier", "ShowStats")
    var mWhichGameType = 0
    var mPlayerList = mutableListOf<Player>()




    //preferences in which Players are saved
    val mNameOfSharedPrefForPlayerList = "sharedPrefForPlayerList"
    var mSharedPrefsForPlayerList: SharedPreferences? = null
    var mEditorOfSharedPrefsForPlayerList: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Init of preferences in which Players are saved
        mSharedPrefsForPlayerList = this.getSharedPreferences(mNameOfSharedPrefForPlayerList, Context.MODE_PRIVATE)
        mEditorOfSharedPrefsForPlayerList = mSharedPrefsForPlayerList!!.edit()


        mPlayerList.clear()
        loadPlayersFromPrefs()
        recyclerViewerForPlayerList.layoutManager = LinearLayoutManager(this)
        recyclerViewerForPlayerList.adapter = MyRecyclerViewerAdapter(mPlayerList, this) { item : Player -> itemOnRecyclerViewClicked(item)}


        //dropdownMenu
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

    fun itemOnRecyclerViewClicked(item: Player){
        item.mIsChecked = !item.mIsChecked
        //Toast.makeText(applicationContext,item.mIsChecked.toString(), Toast.LENGTH_SHORT).show()
    }

    //Button Functions
    //add onClickListeners for all Buttons
    fun initButtons(){

        //val startButton = findViewById<Button>(R.id.startButton)
        doButton.setOnClickListener {
            // Do something in response to button
            doGameType()
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

    fun loadPlayersFromPrefs(){

        var i = 0
        var isNotEmpty = true

        while(isNotEmpty){

            if(mSharedPrefsForPlayerList!!.contains("Player" + i.toString())){

                var nameOfPlayerEntryInPref =  mSharedPrefsForPlayerList!!.getString("Player" + i.toString(), "blank")
                var mmrOfPlayerEntryInPref = mSharedPrefsForPlayerList!!.getInt(nameOfPlayerEntryInPref+"MMR", 0)
                var newPl = Player(nameOfPlayerEntryInPref, mmrOfPlayerEntryInPref)
                mPlayerList.add(newPl)
                i++
            }
            else{
                isNotEmpty = false
            }
        }
    }

    fun addPlayer(){

        if(playerName.text.isNotEmpty()){

            var name = playerName.text.toString()
            Toast.makeText(applicationContext,name,Toast.LENGTH_SHORT).show()
            playerName.text.clear()
            val pl = Player(name,500)

            mPlayerList.add(pl)
            mEditorOfSharedPrefsForPlayerList!!.putString("Player" + mPlayerList.indexOf(pl).toString(), pl.mName)
            mEditorOfSharedPrefsForPlayerList!!.putInt(name+"MMR", pl.mMMR)
            mEditorOfSharedPrefsForPlayerList!!.apply()
            recyclerViewerForPlayerList.adapter = MyRecyclerViewerAdapter(mPlayerList, this) { item : Player -> itemOnRecyclerViewClicked(item)}
        }

    }

    fun deletePlayers(){


        //Toast.makeText(applicationContext,"Add Button Pressed",Toast.LENGTH_SHORT).show()
        // Initialize a new instance of
        val builder = AlertDialog.Builder(this@MainActivity)

        // Set the alert dialog title
        builder.setTitle("Deletion of selected Players")

        // Display a message on alert dialog
        builder.setMessage("Are you sure you want to delete the selected Players? They will also be deleted in realLife")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("YES FUCK THEM!"){ dialog, which ->
            // Do something when user press the positive button
            Toast.makeText(applicationContext,"yeeeesch Fuck THEM. We dont need them",Toast.LENGTH_SHORT).show()

            var originalListSize = mPlayerList.size


            var i = 0
            while(i < mPlayerList.size){
                if(mPlayerList[i].mIsChecked){
                    mEditorOfSharedPrefsForPlayerList!!.remove("Player" + i.toString())
                    mEditorOfSharedPrefsForPlayerList!!.remove(mPlayerList[i].mName + "MMR")
                    mEditorOfSharedPrefsForPlayerList!!.apply()
                    mPlayerList.removeAt(i)
                }
                else{
                    i++
                }
            }

            var newListSize = mPlayerList.size

            //rearrranging position of players in Prefs
            mPlayerList.forEachIndexed { index, player ->
                mEditorOfSharedPrefsForPlayerList!!.putString("Player" + index.toString(), player.mName)
                mEditorOfSharedPrefsForPlayerList!!.apply()
            }
            //deleting the duplicates in prefs
            for(l in newListSize until originalListSize){
                mEditorOfSharedPrefsForPlayerList!!.remove("Player" + i.toString())
                mEditorOfSharedPrefsForPlayerList!!.apply()
            }

            recyclerViewerForPlayerList.adapter = MyRecyclerViewerAdapter(mPlayerList, this) { item : Player -> itemOnRecyclerViewClicked(item)}

        }
        builder.setNegativeButton("No.. pls nOooo"){dialog, which ->
            //Do something when user press the negativ button

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    fun doGameType(){

        //Toast.makeText(applicationContext,mWhichGameType.toString(),Toast.LENGTH_SHORT).show()

        when(mWhichGameType){
            0 -> startEndlosspiel()
            1 -> startTurnier()
            2 -> showStats()
        }
    }

    fun startEndlosspiel(){

        var selectedPlayers = mutableListOf<Player>()

        mPlayerList.forEachIndexed { index, player ->

            if(player.mIsChecked){
                selectedPlayers.add(player)
            }
        }
        if(selectedPlayers.isEmpty() || selectedPlayers.size < 4){

            val builder = AlertDialog.Builder(this@MainActivity)

            // Set the alert dialog title
            builder.setTitle("Stats")

            builder.setMessage("not enough players selected dipshit")
            builder.setNeutralButton("OK, i guess i suck") { _, _ -> }

            val dialog: AlertDialog = builder.create()
            dialog.show()

        }
        else{

            val matchupManager: MatchupManager = MatchupManager(selectedPlayers)
            val matchup = matchupManager.getNextMatchup()
            textView6.text = matchup.second.mPlayers[0].mName
            textView7.text = matchup.second.mPlayers[1].mName
            textView8.text = matchup.second.mPlayers[2].mName
            textView9.text = matchup.second.mPlayers[3].mName
        }


    }

    fun startTurnier(){

    }

    fun showStats(){

        var selectedPlayers = mutableListOf<Player>()

        mPlayerList.forEachIndexed { index, player ->

            if(player.mIsChecked){
                selectedPlayers.add(player)
            }
        }

        if(selectedPlayers.isEmpty()){

            val builder = AlertDialog.Builder(this@MainActivity)

            // Set the alert dialog title
            builder.setTitle("Stats")

            builder.setMessage("no players selected dipshit")
            builder.setNeutralButton("OK, i guess i suck") { _, _ -> }

            val dialog: AlertDialog = builder.create()
            dialog.show()

        }
        else {

            val arrayOfSelectedPlayers = arrayOfNulls<String>(selectedPlayers.size)
            arrayOfSelectedPlayers.forEachIndexed { index, _ ->
                arrayOfSelectedPlayers[index] =
                    "Name: " + selectedPlayers[index].mName + ", MMR:" + selectedPlayers[index].mMMR.toString()
            }

            val builder = AlertDialog.Builder(this@MainActivity)

            // Set the alert dialog title
            builder.setTitle("Stats")

            builder.setItems(arrayOfSelectedPlayers) { _, _ -> }
            builder.setNeutralButton("OK") { _, _ -> }

            val dialog: AlertDialog = builder.create()
            dialog.show()

        }
    }

}
