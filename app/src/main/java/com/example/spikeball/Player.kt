package com.example.spikeball

class Player
{
    // data
    var mName = ""
    var mMMR = 0
    var mScorePoints = arrayOf(0,0)
    var mScoreSets = arrayOf(0,0)
    var mScoreWinsLosses = arrayOf(0,0)

    // flags & stuff
    var mMatchMakingProbabilityScore = 0
    var mIsChecked = false //TODO: remove and improve

    constructor()
    constructor(name: String, mmr: Int = 500)
    {
        mMMR = mmr
        mName = name
    }

    init{
        resetMMPScore()
    }

    fun resetMMPScore()
    {
        mMatchMakingProbabilityScore = 100
    }

    fun updateScoreData(playedMatchup:Matchup) : Boolean
    {
        var counter = 0
        var isTeam1 = false
        for (pl in playedMatchup.getAllPlayers())
        {
            if(pl.mName == mName)
            {
                isTeam1 = counter < 2
            }
            else
            {
                counter++
            }
        }

        // no matching player found
        if(counter == 4)
        {
            return false
        }

        // update score now
        var m = 0
        var n = 0

        if(isTeam1)
        {
            m = 0
            n = 1
        }
        else
        {
            m = 1
            n = 0
        }

        if(playedMatchup.mScorePointsBoX[2] == 0 && playedMatchup.mScorePointsBoX[3] == 0) // BO1
        {
                mScorePoints[0] += playedMatchup.mScorePointsBoX[m]
                mScorePoints[1] += playedMatchup.mScorePointsBoX[n]
        }
        else   // BO3
        {
            for(i in 0..2)
            {
                mScorePoints[0] += playedMatchup.mScorePointsBoX[m+2*i]
                mScorePoints[1] += playedMatchup.mScorePointsBoX[n+2*i]
            }
        }

        mScoreSets[0] += playedMatchup.mScoreSets[m]
        mScoreSets[1] += playedMatchup.mScoreSets[n]

        if(playedMatchup.mScoreSets[m] > playedMatchup.mScoreSets[n])
        {
            mScoreWinsLosses[0]++
        }
        else
        {
            mScoreWinsLosses[1]++
        }

        return true
    }

}