package com.example.spikeball

class Player
{


    var mMatchMakingProbabilityScore = 0
    var mMMR = 0
    var mName = ""

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

}