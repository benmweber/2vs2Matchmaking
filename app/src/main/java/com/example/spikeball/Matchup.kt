package com.example.spikeball

import kotlin.math.*

class Matchup {

    // pre match data
    var mTeamConstellation = emptyList<Team>()
    var mMatchupID = String()

    // post match data
    var mScorePointsBoX = arrayOf(0,0,0,0,0,0)
    var mScoreSets = arrayOf(0,0)

    // flags
    var mSkipped = false //TODO: maybe remove? not very "pretty"

    constructor()

    constructor(team1: Team, team2: Team) : this() {
        mTeamConstellation = listOf(team1, team2)
        mTeamConstellation = mTeamConstellation.sortedWith(compareBy({ it.mPlayerCombinationID }))
        mMatchupID = mTeamConstellation[0].mPlayerCombinationID + "!" + mTeamConstellation[1].mPlayerCombinationID
    }

    fun getAllPlayers() : ArrayList<Player>
    {
       return arrayListOf<Player>(mTeamConstellation[0].mPlayer1,mTeamConstellation[0].mPlayer2,mTeamConstellation[1].mPlayer1,mTeamConstellation[1].mPlayer2)
    }

    fun getWinnerTeam() : Team
    {
        // check if score is set
        if(mScoreSets[0] == 0 && mScoreSets[1] == 0)
        {
            return Team()
        }

        if(mScoreSets[0] > mScoreSets[1])
        {
            return mTeamConstellation[0]
        }
        else
        {
            return mTeamConstellation[1]
        }
    }

    fun setScore(score : Array<Int>) {

        mScorePointsBoX = score

        for(i in 0..2)
        {
            if(mScorePointsBoX[0+2*i] > mScorePointsBoX[1+2*i] && (mScorePointsBoX[0+2*i] + mScorePointsBoX[1+2*i]) != 0)
            {
                mScoreSets[0]++
            }
            else
            {
                mScoreSets[1]++
            }
        }

        // update scores
        for(pl in getAllPlayers())
        {
            pl.updateScoreData(this)
        }

        // update MMRs
        updateMMRforPlayer(mTeamConstellation[0].mPlayer1,true,mScoreSets[0] > mScoreSets[1])
        updateMMRforPlayer(mTeamConstellation[0].mPlayer2,true,mScoreSets[0] > mScoreSets[1])
        updateMMRforPlayer(mTeamConstellation[1].mPlayer1,false,mScoreSets[0] < mScoreSets[1])
        updateMMRforPlayer(mTeamConstellation[1].mPlayer2,false,mScoreSets[0] < mScoreSets[1])
    }

    // calculates the mmr change for a player depending of the result of the game
    private fun updateMMRforPlayer(player: Player, playersIsTeam1: Boolean, playerIsWinner: Boolean)  {

        //TODO: add additional layer that scales mmr change according to difference inside of team
        var mmrChange = 0
        var diff = mTeamConstellation[0].mCombinedMMR - mTeamConstellation[1].mCombinedMMR

        // if team 1 and won -> team 1 won
        // if diff is positive -> expected win
        // if diff is negative -> unexpected win

        // switch polarity if player not in team 1
        if(!playersIsTeam1)
        {
            diff *=-1
        }

        if (playerIsWinner) {
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


