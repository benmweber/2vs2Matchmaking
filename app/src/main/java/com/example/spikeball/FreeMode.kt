package com.example.spikeball

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_free_mode.*

class FreeMode : AppCompatActivity() {

    //val matchupManager: MatchupManager = MatchupManager(selectedPlayers)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_mode)


       var playerNames = intent.getStringArrayListExtra("selectedPlayerList")

        displayMatchup(Matchup(Team(Player(playerNames[0]),Player(playerNames[1])),Team(Player(playerNames[2]),Player(playerNames[3]))))
    }

    fun checkForSelectedPlayers(){

    }

    fun displayMatchup(matchupToDisplay:Matchup){

        player1box.text = matchupToDisplay.mTeamConstellation[0].mPlayer1.mName
        player2box.text = matchupToDisplay.mTeamConstellation[0].mPlayer2.mName

        player3box.text = matchupToDisplay.mTeamConstellation[1].mPlayer1.mName
        player4box.text = matchupToDisplay.mTeamConstellation[1].mPlayer2.mName
    }

}
