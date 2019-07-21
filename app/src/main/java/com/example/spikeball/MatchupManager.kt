package com.example.spikeball

import kotlin.random.Random

class MatchupManager(players:MutableList<Player>)
{
    private var mPlayers = players
    private var mMatchupHistory = mutableListOf<Matchup>()
    var mPendingMatchup = Matchup()
    var mCachedMatchup = Matchup()
    var mCached = false

    // parameters
    private val mDepth = 2
    private val mChangeFactor = 0.1

    fun addPlayer(player:Player)
    {
        mPlayers.add(player)
    }

    // Rules:
    // 1. Players that didnt play last round ALWAYS play next round (except there is no matchup left)
    // 2. Players that played two times in a row shouldnt play a third time
    // 3. Players shouldn't play with the same team two times in a row

    private fun updateProbabilityScores()
    {
        for(player in mPlayers)
        {
            player.resetMMPScore()

            // check if player played the last X (= mDepth) matches

            var playedLastMatchups = arrayOfNulls<Boolean>(mDepth)

            for (i in 0 until mDepth)
            {
                playedLastMatchups[i] = false
            }

            //if matchuphistory is of the same size as depth, lookup the players of the last matches and store in the array (most recent game first in array)
            if(mMatchupHistory.size >= mDepth)
            {
                for (i in 0 until mDepth)
                {
                    val tempResult = mMatchupHistory[mMatchupHistory.lastIndex - i].getAllPlayers().find{ i -> i.mName == player.mName }

                    playedLastMatchups[i] = tempResult != null
                }
            }
            // if depth is bigger than history size, only check the available matches, then fill the rest of the array with false
            else
            {
                for (i in 0 until mMatchupHistory.size)
                {
                    val tempResult = mMatchupHistory[mMatchupHistory.lastIndex - i].getAllPlayers().find{ i -> i.mName == player.mName }

                    playedLastMatchups[i] = tempResult != null
                }
            }

            // update probability
            for(played in playedLastMatchups)
            {
                if(played!!)
                {
                    player.mMatchMakingProbabilityScore =  player.mMatchMakingProbabilityScore * mChangeFactor
                    if (player.mMatchMakingProbabilityScore < 10.0)
                    {
                        player.mMatchMakingProbabilityScore = 10.0
                    }
                }
                else
                {
                    break
                }

            }

        }
    }

    fun resetAllPlayerMMPScores()
    {
        for(player in mPlayers)
        {
            player.resetMMPScore()
        }
    }

    // Set the result of the match (true if team 1 won, false if team 2 won)
    fun confirmLastMatchupAsFinished(score:Array<Int>)
    {
        // update result of match, update mmrs and log
        mPendingMatchup.setScore(score)
        mMatchupHistory.add(mPendingMatchup)

        // update mmr and new probabiliti scores based on last match (ATTENTION, mPendingMatchup has to be added to mMatchupHistory beforehand!)
        updateProbabilityScores()
    }

    fun cachePendingMatchup()
    {
        mCachedMatchup = mPendingMatchup
        mCached = true
    }

    fun cacheLastMatchupFromHistory()
    {
        mCachedMatchup = mMatchupHistory.last()
        mCached = true
    }

    fun resetMatchupHistory()
    {
        // cache matchup for improved matchup generation
        cacheLastMatchupFromHistory()
        mMatchupHistory.clear()
    }

    // Gets a matchup that has not been played yet
    // Example: 4 players -> A B C D
    // AB vs CD
    // AC vs BD
    // AD vs BC
    // -> 3 matchups

    fun getNextMatchup() : Pair<Boolean,Matchup>
    {
        var resultingMatchup = Matchup()

        var finished = false
        var counter = 0
        var teamRetries = 0
        val counterThreshold = 2000*mPlayers.size


        while(!finished && counter < counterThreshold)
        {
            val tempPlayers = mutableListOf<Player>()
            for(i in 0..3)
            {
                val tempP = getPlayerBasedOnScores(tempPlayers)
                tempPlayers.add(tempP)
            }

            resultingMatchup = Matchup(Team(tempPlayers[0],tempPlayers[1]),Team(tempPlayers[2],tempPlayers[3]))

            finished = true

            for(mtchp in mMatchupHistory)
            {
                // if matchup already exists in history keep rerolling
                if(resultingMatchup.mMatchupID == mtchp.mMatchupID) {
                    finished = false
                }
            }

            // if any of the teams played in this exact constellation in last X ( =mDepth) matchups, retry ( after X tries, ignore this)
            if(finished && teamRetries < 50 && mMatchupHistory.size > 0)
            {
                var tempDepth = mDepth

                if(mMatchupHistory.size < mDepth)
                {
                    tempDepth = mMatchupHistory.size
                }

                for(i in 0..1)
                {
                    for (j in 0..1)
                    {
                        for (k in 0 until tempDepth)
                        {
                            if(resultingMatchup.mTeamConstellation[i].mPlayerCombinationID == mMatchupHistory[mMatchupHistory.lastIndex - k].mTeamConstellation[j].mPlayerCombinationID)
                            {
                                finished = false
                                teamRetries++
                            }
                        }
                    }
                }
            }

            // if skipped or reset matchup history, check for team constellations in cached matchup
            if(finished && mCached)
            {
                for(i in 0..1)
                {
                    for (j in 0..1)
                    {
                            if(resultingMatchup.mTeamConstellation[i].mPlayerCombinationID == mCachedMatchup.mTeamConstellation[j].mPlayerCombinationID)
                            {
                                finished = false
                            }
                    }
                }
            }

            counter++
        }

        mCached = false
        mPendingMatchup = resultingMatchup
        return Pair(finished,resultingMatchup)
    }




    private fun getPlayerBasedOnScores(forbiddenPlayers:List<Player> = emptyList()) : Player
    {
        var finished = false
        var resultingPlayer = Player("ERROR",0)
        while (!finished) {
            var sum = 0.0
            for (player in mPlayers) {
                sum += player.mMatchMakingProbabilityScore
            }
            val resultRNG = Random.nextInt(sum.toInt())

            var temp = 0.0

            for (player in mPlayers) {
                temp += player.mMatchMakingProbabilityScore
                if (resultRNG < temp) {
                    resultingPlayer = player
                    finished = true

                    for(fPlayer in forbiddenPlayers)
                    {
                        if(fPlayer.mName == resultingPlayer.mName)
                        {
                            finished = false
                        }
                    }
                    break
                }
            }
        }
        return resultingPlayer
    }


}