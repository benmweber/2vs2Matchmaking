package com.example.spikeball

import kotlin.math.*

class Team
{
    var mCombinedMMR = 0
    var mMMRDiff = 0
    var mPlayer1 = Player()
    var mPlayer2 = Player()
    var mPlayerCombinationID = String()

    constructor()

    constructor(player1:Player,player2 : Player)
    {
        mPlayer1 = player1
        mPlayer2 = player2

        // sort both names in alphabetical order to identify match-ups correctly
        val playerNamesArray = arrayOf(mPlayer1.mName,mPlayer2.mName)
        playerNamesArray.sort()
        mPlayerCombinationID = playerNamesArray[0] + "&" + playerNamesArray[1]

        // calculate mmr
        mMMRDiff = abs(mPlayer1.mMMR - mPlayer2.mMMR)
        mCombinedMMR = (mPlayer1.mMMR + mPlayer2.mMMR) / 2
        // adjust combined mmr depending on difference inside team
        mCombinedMMR -=  (mMMRDiff * 0.15).toInt()
    }
}