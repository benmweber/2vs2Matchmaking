package com.example.spikeball

import kotlin.random.Random

class MatchupManager(players:MutableList<Player>)
{
    private var mPlayers = players
    private var mMatchupHistory = mutableListOf<Matchup>()
    var mPendingMatchup = Matchup()

    fun addPlayer(player:Player)
    {
        mPlayers.add(player)
    }

    private fun updateProbabilityScores()
    {
        // TODO: add probability of playing with each other player, depending on last matchup and team partner
        for(player in mPlayers)
        {
            player.resetMMPScore()
            val result = mMatchupHistory.last().getAllPlayers().find{ i -> i.mName == player.mName }
            if(result == null)
            {
                player.mMatchMakingProbabilityScore = player.mMatchMakingProbabilityScore + 200
            }
            else
            {
                player.mMatchMakingProbabilityScore = player.mMatchMakingProbabilityScore - 70
            }
        }
    }

    fun skipPendingMatchup()
    {
        // update result of match and log
        mPendingMatchup.mSkipped = true
        mMatchupHistory.add(mPendingMatchup)

        // update mmr and new probabilities scores based on last match (ATTENTION, mPendingMatchup has to be added to mMatchupHistory beforehand!)
        updateProbabilityScores()
    }

    // Set the result of the match (true if team 1 won, false if team 2 won)
    fun confirmLastMatchupAsFinished(team1won : Boolean)
    {
        // update result of match, update mmrs and log

        //TODO: add dialogue for score data

        mPendingMatchup.setScore(arrayOf(15,6,3,15,15,1)) //TODO: NUR BEISPIEL
        mMatchupHistory.add(mPendingMatchup)

        // update mmr and new probabiliti scores based on last match (ATTENTION, mPendingMatchup has to be added to mMatchupHistory beforehand!)
        updateProbabilityScores()
    }

    fun resetMatchupHistory()
    {
        // reset scores, add pending matchup temporarily to history, then clear, so that there is not the same matchup after reset
        mMatchupHistory.add(mPendingMatchup)
        updateProbabilityScores()
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

        while(!finished && counter < 1000*mPlayers.size)
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
                // if matchup already exists in history AND the according matchup is NOT a skipped one, keep rerolling
                if(resultingMatchup.mMatchupID == mtchp.mMatchupID && !mtchp.mSkipped) {
                    finished = false
                }
            }
            counter++
        }

        mPendingMatchup = resultingMatchup
        return Pair(finished,resultingMatchup)
    }



    private fun getPlayerBasedOnScores(forbiddenPlayers:List<Player> = emptyList()) : Player
    {
        var finished = false
        var resultingPlayer = Player("ERROR",0)
        while (!finished) {
            var sum = 0
            for (player in mPlayers) {
                sum += player.mMatchMakingProbabilityScore
            }
            val resultRNG = Random.nextInt(sum)

            var temp = 0

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