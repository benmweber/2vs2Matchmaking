package com.example.spikeball

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_free_mode.*


class FreeMode : AppCompatActivity() {

    private val data = DataManager(this)
    private var matchupMgr : MatchupManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_mode)

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
    }

    // build dialog for outcome
    fun manageMatchupOutcome()
    {
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle("Match outcome")
        builder.setMessage("Which team won?")

        val team1ID = matchupMgr!!.mPendingMatchup.mTeamConstellation[0].mPlayerCombinationID
        val team2ID = matchupMgr!!.mPendingMatchup.mTeamConstellation[1].mPlayerCombinationID

        builder.setPositiveButton(team1ID){ dialog, which ->
            finishMatchup(true)
        }

        builder.setNegativeButton(team2ID){dialog, which ->
            finishMatchup(false)
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // sets the winner and gets next matchup
    fun finishMatchup(team1won:Boolean)
    {
        // set outcome of last match, unless history has been reset last time
        matchupMgr!!.confirmLastMatchupAsFinished(team1won)
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

        player1box.text = matchupToDisplay.mPlayers[0].mName + " (" + matchupToDisplay.mPlayers[0].mMMR + ")"
        player2box.text = matchupToDisplay.mPlayers[1].mName+ " (" + matchupToDisplay.mPlayers[1].mMMR + ")"

        player3box.text = matchupToDisplay.mPlayers[2].mName+ " (" + matchupToDisplay.mPlayers[2].mMMR + ")"
        player4box.text = matchupToDisplay.mPlayers[3].mName+ " (" + matchupToDisplay.mPlayers[3].mMMR + ")"

    }

}
