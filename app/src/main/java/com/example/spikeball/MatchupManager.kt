package com.example.spikeball

import kotlin.random.Random

class MatchupManager(players:MutableList<Player>)
{
    private var mPlayers = players
    private var mMatchupHistory = mutableListOf<Matchup>()
    private var mPendingMatchup = Matchup()
    var mMatchupHistoryResetFlag = false

    fun addPlayer(player:Player)
    {
        mPlayers.add(player)
    }

    private fun updateProbabilityScores()
    {
        for(player in mPlayers)
        {
            val result = mMatchupHistory.last().mPlayers.find{ i -> i.mName == player.mName }
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

  private fun applyMMRChanges()
  {
        for(dataPair in mMatchupHistory.last().mMMRUpdateResults)
        {
            val player = mPlayers.find { i -> i.mName == dataPair.first }
            if(player != null)
            {
                player.mMMR += dataPair.second
            }
        }
  }


    // Set the result of the match (true if team 1 won, false if team 2 won)
    fun confirmLastMatchupAsFinished(team1won : Boolean)
    {
        // reset all probability scores
        for(player in mPlayers)
        {
            player.resetMMPScore()
        }

        // update result of match and log
        mPendingMatchup.setWinner(team1won)
        mMatchupHistory.add(mPendingMatchup)

        // update mmr and new probabiliti scores based on last match (ATTENTION, mPendingMatchup has to be added to mMatchupHistory beforehand!)
        applyMMRChanges()
        updateProbabilityScores()
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

        while(!finished && counter < 100000)
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
                if(resultingMatchup.mMatchupID == mtchp.mMatchupID) {
                    finished = false
                }
            }
            counter++
        }

        mPendingMatchup = resultingMatchup
        return Pair(finished,resultingMatchup)
    }

    fun resetMatchupHistory()
    {
        mMatchupHistory.clear()
        mMatchupHistoryResetFlag = true
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