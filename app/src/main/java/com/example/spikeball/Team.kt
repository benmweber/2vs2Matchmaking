package com.example.spikeball

class Team(player1:Player,player2 : Player)
{
    var mPlayer1 = player1
    var mPlayer2 = player2
    var mPlayerCombinationID = String()

    init {
        // sort both names in alphabetical order to identify match-ups correctly
        val playerNamesArray = arrayOf(mPlayer1.mName,mPlayer2.mName)
        playerNamesArray.sort()
        mPlayerCombinationID = playerNamesArray[0] + "&" + playerNamesArray[1]
    }

}