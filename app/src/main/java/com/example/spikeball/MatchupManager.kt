package com.example.spikeball

import kotlin.random.Random

class MatchupManager(players:MutableList<Player>)
{
    var mPlayers = players
    var mMatchupLog = mutableListOf<Matchup>()
    var mPrevMatchup = Matchup()

    init
    {

    }

    fun addPlayer(player:Player)
    {
        mPlayers.add(player)
    }

    fun updateProbabilityScores()
    {
        for(player in mPlayers)
        {
            val result = mPrevMatchup.mPlayers.find{ i -> i.mName == player.mName }
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

    // after a match has been played, confirm the result and log the matchup
    fun confirmMatchupPlayed(playedMatchup:Matchup)
    {
        mMatchupLog.add(playedMatchup)
    }

    fun confirmLastMatchupPlayed()
    {
        for(player in mPlayers)
        {
            player.resetMMPScore()
        }
        mMatchupLog.add(mPrevMatchup)
        updateProbabilityScores()
    }

    // Gets a matchup that has not been played yet
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

            for(mtchp in mMatchupLog)
            {
                if(resultingMatchup.mMatchupID == mtchp.mMatchupID) {
                    finished = false
                }
            }
            counter++
        }

        mPrevMatchup = resultingMatchup
        return Pair(finished,resultingMatchup)
    }

 /*   fun GetTourneeMatchups() : List<Matchup>
    {

    }*/

    fun  getPlayerBasedOnScores(forbiddenPlayers:List<Player> = emptyList()) : Player
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