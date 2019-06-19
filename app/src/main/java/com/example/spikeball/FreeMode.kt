package com.example.spikeball

import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_free_mode.*
import kotlinx.android.synthetic.main.activity_main.*

class FreeMode : AppCompatActivity() {

    private val data = DataManager(this)
    private var matchupMgr : MatchupManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_mode)


        Toast.makeText(applicationContext,"LUL", Toast.LENGTH_SHORT).show()


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
            // TODO: build dialogue
            if(!matchupMgr!!.mMatchupHistoryResetFlag)
            {
                matchupMgr!!.confirmLastMatchupAsFinished(true)
            }
            else
            {
                matchupMgr!!.mMatchupHistoryResetFlag = false
            }

            // generate next matchup
            val nxtMatchup = matchupMgr!!.getNextMatchup()

            // check if algorithm terminated because all matchups have been played
            if(nxtMatchup.first)
            {
                displayMatchup(nxtMatchup.second)
            }
            else
            {
                Toast.makeText(applicationContext,"ALL MATCHUPS PLAYED, RESETTING...", Toast.LENGTH_SHORT).show()
                matchupMgr!!.resetMatchupHistory()
            }
        }
    }

    fun displayMatchup(matchupToDisplay:Matchup){

        player1box.text = matchupToDisplay.mTeamConstellation[0].mPlayer1.mName
        player2box.text = matchupToDisplay.mTeamConstellation[0].mPlayer2.mName

        player3box.text = matchupToDisplay.mTeamConstellation[1].mPlayer1.mName
        player4box.text = matchupToDisplay.mTeamConstellation[1].mPlayer2.mName

    }

}
