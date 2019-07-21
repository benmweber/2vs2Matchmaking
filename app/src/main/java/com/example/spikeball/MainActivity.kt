package com.example.spikeball

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.Manifest
import android.content.ClipboardManager
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


//import android.widget.Button
//import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast



class MainActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {

    private var mListOfGameTypes = arrayOf("FreeMode", "Turnier", "ShowStats")
    private var mWhichGameType = 0

    private val data = DataManager(this)

    private var permissionGranted = false

    private val REQUEST_PERMISSION_EXTERNAL_WRITE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkFileIOPermission()

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        data.loadPlayers()

        recyclerViewerForPlayerList.layoutManager = LinearLayoutManager(this)
        recyclerViewerForPlayerList.adapter = MyRecyclerViewerAdapter(data.mPlayers, this) { item : Player -> itemOnRecyclerViewClicked(item)}

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

     /*   exportButton.setOnClickListener{
            Toast.makeText(applicationContext, data.exportToFile(this), Toast.LENGTH_LONG).show()
        }

        reloadButton.setOnClickListener{
            Toast.makeText(applicationContext, data.importFromFile(this), Toast.LENGTH_LONG).show()
            recyclerViewerForPlayerList.adapter = MyRecyclerViewerAdapter(data.mPlayers, this) { item : Player -> itemOnRecyclerViewClicked(item)}
        }*/

        shareButton.setOnClickListener{
            val dataStr = data.savePlayers()
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,dataStr )
                type = "text/plain"
            }
            startActivity(sendIntent)
        }

        clipboardLoadButton.setOnClickListener{

            //Toast.makeText(applicationContext,"Add Button Pressed",Toast.LENGTH_SHORT).show()
            // Initialize a new instance of
            val builder = AlertDialog.Builder(this@MainActivity)

            // Set the alert dialog title
            builder.setTitle("Load data from clipboard")
            builder.setMessage("Are you sure?")
            builder.setPositiveButton("Yes"){ dialog, which ->

                val clipMgr = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

                val clipData = clipMgr.primaryClip.getItemAt(0).text.toString()

                if(clipData != "" && clipData != null && clipData.contains("{") && clipData.contains("}"))
                {
                    Toast.makeText(applicationContext,data.importFromDataString(clipData), Toast.LENGTH_LONG).show()
                    recyclerViewerForPlayerList.adapter = MyRecyclerViewerAdapter(data.mPlayers, this) { item : Player -> itemOnRecyclerViewClicked(item)}
                }
                else
                {
                    Toast.makeText(applicationContext,"NO CORRECT DATA IN CLIPBOARD! Copy data first ...", Toast.LENGTH_LONG).show()
                }

            }

            builder.setNegativeButton("Cancel"){dialog, which ->
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

       /* telegramImportButton.setOnClickListener{
            if(permissionGranted)
            {
                Toast.makeText(applicationContext, data.importFromDownloads(), Toast.LENGTH_LONG).show()
                recyclerViewerForPlayerList.adapter = MyRecyclerViewerAdapter(data.mPlayers, this) { item : Player -> itemOnRecyclerViewClicked(item)}
            }
            else
            {
                checkFileIOPermission()
            }
        }*/
    }

    fun addPlayer(){

        if(playerName.text.isNotEmpty()){

            var name = playerName.text.toString()
            //Toast.makeText(applicationContext,name,Toast.LENGTH_SHORT).show()
            playerName.text.clear()

            data.addPlayer(Player(name,500))

            recyclerViewerForPlayerList.adapter = MyRecyclerViewerAdapter(data.mPlayers, this) { item : Player -> itemOnRecyclerViewClicked(item)}
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

            data.deleteAllCheckedPlayers()

            recyclerViewerForPlayerList.adapter = MyRecyclerViewerAdapter(data.mPlayers, this) { item : Player -> itemOnRecyclerViewClicked(item)}
        }

        builder.setNegativeButton("No.. pls noooo"){dialog, which ->
            //Do something when user press the negativ button
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun doGameType(){

        when(mWhichGameType){
            0 -> startFreeMode()
            1 -> startTurnier()
            2 -> showStats()
        }
    }

    fun startFreeMode(){

        val intent = Intent(this, FreeMode::class.java)
        var selectedPlayers = arrayListOf<String>()

        data.mPlayers.forEachIndexed { index, player ->

            if(player.mIsChecked){
                selectedPlayers.add(player.mName)
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
            intent.putExtra("selectedPlayerList",selectedPlayers)

            startActivity(intent)

        }


    }

    fun startTurnier(){

    }

    //TODO: remove or renew
    fun showStats(){

        var selectedPlayers = mutableListOf<Player>()

        data.mPlayers.forEachIndexed { index, player ->

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

    fun checkFileIOPermission()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),REQUEST_PERMISSION_EXTERNAL_WRITE)

            // REQUEST_PERMISSION_EXTERNAL_WRITE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
        else
        {
            permissionGranted = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_EXTERNAL_WRITE ->  permissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

}
