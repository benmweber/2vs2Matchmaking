package com.example.spikeball

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_free_mode.*
import android.text.InputType
import android.widget.EditText
import androidx.constraintlayout.solver.widgets.Helper


class FreeMode : AppCompatActivity() {

    private val data = DataManager(this)
    private var matchupMgr : MatchupManager? = null

    private var bo3active = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_mode)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // get player data by using names of selected players
        val selectedPlayerNames = intent.getStringArrayListExtra("selectedPlayerList")
        var newList = ArrayList<Player>()

        data.loadPlayers()

        for(name in selectedPlayerNames)
        {
            data.getPlayer(name)?.let { newList.add(it) }
        }

        // initialize matchup manager
        matchupMgr = MatchupManager(newList)

        // display first matchup
        displayMatchup(matchupMgr!!.getNextMatchup().second)

        // buttons
        finishedButton.setOnClickListener {
            // confirm outcome of last match, unless history has been reset last time button was pressed
            manageMatchupOutcome()
        }

        skipButton.setOnClickListener{
            // skip matchup
            skipNextMatchup()
        }

        setupNextMatchupButton.setOnClickListener{
            // setup next matchup
            setupNextMatchup()
        }

        // setup switch listener for bo1 bo3 switch
        bo3switch.setOnCheckedChangeListener { buttonView, isChecked ->
            bo3active = isChecked
       }

        // init score boxes type
        set1team1.inputType = InputType.TYPE_CLASS_NUMBER
        set1team2.inputType = InputType.TYPE_CLASS_NUMBER
        set2team1.inputType = InputType.TYPE_CLASS_NUMBER
        set2team2.inputType = InputType.TYPE_CLASS_NUMBER
        set3team1.inputType = InputType.TYPE_CLASS_NUMBER
        set3team2.inputType = InputType.TYPE_CLASS_NUMBER
    }

    // build dialog for outcome
    fun manageMatchupOutcome()
    {
        var inputScore = arrayOf(0,0,0,0,0,0)
        var emptyBoxes = arrayOf(false,false,false,false,false,false)

        // TODO: prettier?
        // read out all boxes and save the state of the box (empty or not)
        if(set1team1.text.toString() != "")
        {
            inputScore[0] = set1team1.text.toString().toInt()
        }
        else
        {
            emptyBoxes[0] = true
        }

        if(set1team2.text.toString() != "")
        {
            inputScore[1] = set1team2.text.toString().toInt()
        }
        else
        {
            emptyBoxes[1] = true
        }

        if(set2team1.text.toString() != "")
        {
            inputScore[2] = set2team1.text.toString().toInt()
        }
        else
        {
            emptyBoxes[2] = true
        }

        if(set2team2.text.toString() != "")
        {
            inputScore[3] = set2team2.text.toString().toInt()
        }
        else
        {
            emptyBoxes[3] = true
        }

        if(set3team1.text.toString() != "")
        {
            inputScore[4] = set3team1.text.toString().toInt()
        }
        else
        {
            emptyBoxes[4] = true
        }

        if(set3team2.text.toString() != "")
        {
            inputScore[5] = set3team2.text.toString().toInt()
        }
        else
        {
            emptyBoxes[5] = true
        }

        // check if relevant boxes are set
        if(emptyBoxes[0] || emptyBoxes [1])
        {
            var toast = Toast.makeText(applicationContext,"Missing scores in set 1! Retry after typing in scores...", Toast.LENGTH_LONG)
            toast.show()
            return
        }

        if(bo3active)
        {
            // if bo 3 and second set is missing, always return
            if(emptyBoxes[2] || emptyBoxes [3])
            {
                var toast = Toast.makeText(applicationContext,"Missing scores in set 2! Retry after typing in scores...", Toast.LENGTH_LONG)
                toast.show()
                return
            }

            val sets = Matchup.getSetsFromScore(inputScore)

            // if there are 3 sets, check last two boxes
            if(sets[0] + sets[1] == 3 && (emptyBoxes[4] || emptyBoxes[5]))
            {
                var toast = Toast.makeText(applicationContext,"Missing scores in set 3! Retry after typing in scores...", Toast.LENGTH_LONG)
                toast.show()
                return
            }

            // if not enough sets for bo3 are given, return
            if( sets[0] < 2 && sets[1] < 2)
            {
                var toast = Toast.makeText(applicationContext,"Not enough sets! Retry after typing in scores...", Toast.LENGTH_LONG)
                toast.show()
                return
            }
        }

        // set outcome of last match, unless history has been reset last time
        matchupMgr!!.confirmLastMatchupAsFinished(inputScore)
        data.savePlayers()
        getNewMatchup()
    }

    // manually set the player constellation for next matchup
    fun setupNextMatchup()
    {
        // dialog to pick players from a popup menu
        var toast = Toast.makeText(applicationContext,"Just click on SKIP until the desired matchup is generated ;)", Toast.LENGTH_LONG)
        toast.setGravity(0,0,100)
        toast.show()
    }

    fun skipNextMatchup()
    {
        matchupMgr!!.skipPendingMatchup()
        getNewMatchup()
    }

    fun getNewMatchup()
    {
        // generate next matchup
        var nxtMatchup = matchupMgr!!.getNextMatchup()

        // check if algorithm terminated because all matchups have been played
        if(nxtMatchup.first)
        {
            displayMatchup(nxtMatchup.second)
        }
        else
        {
            var toast = Toast.makeText(applicationContext,"ALL MATCHUPS PLAYED, RESETTING...", Toast.LENGTH_LONG)
            toast.setGravity(0,0,100)
            toast.show()

            matchupMgr!!.resetMatchupHistory()
            displayMatchup(matchupMgr!!.getNextMatchup().second)
        }
    }

    fun displayMatchup(matchupToDisplay:Matchup){

        val players = matchupToDisplay.getAllPlayers()

        player1box.text = players[0].mName + " (" + players[0].mMMR + ")"
        player2box.text = players[1].mName+ " (" + players[1].mMMR + ")"

        player3box.text = players[2].mName+ " (" + players[2].mMMR + ")"
        player4box.text = players[3].mName+ " (" + players[3].mMMR + ")"
    }

}
