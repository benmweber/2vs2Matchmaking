package com.example.spikeball

import kotlin.random.Random

fun main()
{
    val player1 = Player("Timo")
    val player2 = Player("Ben")
    val player3 = Player("Freddy")
    val player4 = Player("Thilo")
    val player5 = Player("Peter")
    val player6 = Player("Ugly")

    val mgr = MatchupManager(mutableListOf(player1,player2,player3,player4,player5,player6))

    mgr.addPlayer(Player("Ingo"))

    var result = true
    while(result)
    {
        val test = mgr.getNextMatchup()
        val win =Random.nextInt(2) == 1
        println(win)
        mgr.confirmLastMatchupAsFinished(win)
        print(test.second.mMatchupID +" " + test.first + "\t")
        for(player in mgr.mPlayers)
        {
            print("(Name/MMR: " +player.mName +"/"+player.mMMR + ") ")
        }
        println("")

        result = test.first
    }

}

