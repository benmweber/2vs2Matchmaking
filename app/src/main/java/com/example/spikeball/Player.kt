package com.example.spikeball

class Player(name: String, mmr: Int)
{
    var mMatchMakingProbabilityScore = 0
    var mMmr = mmr
    var mName = name

    init{
        resetMMPScore()
    }

    fun resetMMPScore()
    {
        mMatchMakingProbabilityScore = 100
    }

}