package com.example.spikeball

fun main()
{
    val player1 = Player("Timo",200)
    val player2 = Player("Ben",200)
    val player3 = Player("Freddy",200)
    val player4 = Player("Thilo",200)
    val player5 = Player("Peter",200)
    val player6 = Player("Ugly",200)

    val mgr = MatchupManager(mutableListOf(player1,player2,player3,player4,player5,player6))


    (1..4)
        .forEach { _ ->
            val test = mgr.getNextMatchup()
            mgr.confirmLastMatchupPlayed()
            println(test.second.mMatchupID +" " + test.first)
        }

    mgr.addPlayer(Player("NewPenis",200))

    println("\nAdd more players\n")

    var result = true
    while(result)
    {
        val test = mgr.getNextMatchup()
        mgr.confirmLastMatchupPlayed()
        println(test.second.mMatchupID +" " + test.first)
        result = test.first
    }

}