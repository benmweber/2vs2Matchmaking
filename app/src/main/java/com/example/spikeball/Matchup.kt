package com.example.spikeball

class Matchup {

    var mTeamConstellation = emptyList<Team>()
    var mPlayers = emptyList<Player>()
    var mMatchupID = String()

    constructor()

    constructor(team1: Team, team2: Team) : this()
    {
        mPlayers = listOf(team1.mPlayer1,team1.mPlayer2,team2.mPlayer1,team2.mPlayer2)
        mTeamConstellation = listOf(team1,team2)
        mTeamConstellation = mTeamConstellation.sortedWith(compareBy({ it.mPlayerCombinationID }))
        mMatchupID = mTeamConstellation[0].mPlayerCombinationID + "VS" + mTeamConstellation[1].mPlayerCombinationID
    }

}


