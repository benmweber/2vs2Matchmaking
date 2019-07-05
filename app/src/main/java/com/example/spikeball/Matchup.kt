package com.example.spikeball

import kotlin.math.*

class Matchup {

    var mTeamConstellation = emptyList<Team>()
    var mPlayers = emptyList<Player>()
    var mMatchupID = String()
    var mMMRGlobalAvg = 0
    var mWinner = Team()
    var mLoser = Team()
    var mSkipped = false

    constructor()

    constructor(team1: Team, team2: Team) : this() {
        mPlayers = listOf(team1.mPlayer1, team1.mPlayer2, team2.mPlayer1, team2.mPlayer2)
        mTeamConstellation = listOf(team1, team2)
        mTeamConstellation = mTeamConstellation.sortedWith(compareBy({ it.mPlayerCombinationID }))
        mMatchupID = mTeamConstellation[0].mPlayerCombinationID + "!" + mTeamConstellation[1].mPlayerCombinationID
        mMMRGlobalAvg = (mTeamConstellation[0].mCombinedMMR + mTeamConstellation[1].mCombinedMMR) / 2
    }

    fun setWinner(team1won: Boolean) {
        if (mTeamConstellation.isNotEmpty()) {
            if (team1won) {
                mWinner = mTeamConstellation[0]
                mLoser = mTeamConstellation[1]
            } else {
                mWinner = mTeamConstellation[1]
                mLoser = mTeamConstellation[0]
            }

            updateMMRforPlayer(mWinner.mPlayer1,true)
            updateMMRforPlayer(mWinner.mPlayer2,true)
            updateMMRforPlayer(mLoser.mPlayer1,false)
            updateMMRforPlayer(mLoser.mPlayer2,false)
        }
    }


    // calculates the mmr change for a player depending of the result of the game and stores this information in the mMMRUpdateResults member variable for later application
    private fun updateMMRforPlayer(player: Player, isWinner: Boolean)  {

        var mmrChange = 0
        val diff = player.mMMR - mMMRGlobalAvg

        if (isWinner) {
            if (diff >= 0) // winner + higher mmr than avg = expected win -> small gain and the greater the diff, the smaller the gain
            {
                mmrChange = (25 - 0.2 * abs(diff)).toInt()
                if(mmrChange < 10)
                {
                    mmrChange = 10
                }
            }
            else // winner + lower mmr than avg = unexpected win -> big gain and the greater the diff, the greater the gain
            {
                mmrChange = (25 + 0.2 * abs(diff)).toInt()
                if(mmrChange > 50)
                {
                    mmrChange = 50
                }
            }
        } else {
            if (diff >= 0) // loser + higher mmr than avg = unexpected loss -> big loss and the greater the diff, the bigger the loss
            {
                mmrChange = (-25 - 0.2 * abs(diff)).toInt()
                if(mmrChange < -50)
                {
                    mmrChange = -50
                }
            }
            else // loser + lower mmr than avg = expected loss -> small loss and the greater the diff, the smaller the loss
            {
                mmrChange = (-25 + 0.2 * abs(diff)).toInt()
                if(mmrChange > -10)
                {
                    mmrChange = -10
                }
            }
        }

        player.mMMR += mmrChange
    }
}


