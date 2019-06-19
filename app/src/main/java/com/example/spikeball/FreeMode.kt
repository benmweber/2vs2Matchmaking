package com.example.spikeball

import android.os.Bundle
import android.provider.ContactsContract
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_free_mode.*

class FreeMode : AppCompatActivity() {


    val data = DataManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_free_mode)


        // get player data by using names of selected players

       val selectedPlayerNames = intent.getStringArrayListExtra("selectedPlayerList")


        // generate matchups

        //val matchupManager: MatchupManager = MatchupManager(selectedPlayers)

        data.loadPlayers()

        var lul = data.getPlayer("timo")
        lul!!.mName = "timoNEW" // works!

    }



    fun displayMatchup(matchupToDisplay:Matchup){

        player1box.text = matchupToDisplay.mTeamConstellation[0].mPlayer1.mName
        player2box.text = matchupToDisplay.mTeamConstellation[0].mPlayer2.mName

        player3box.text = matchupToDisplay.mTeamConstellation[1].mPlayer1.mName
        player4box.text = matchupToDisplay.mTeamConstellation[1].mPlayer2.mName

    }

}
