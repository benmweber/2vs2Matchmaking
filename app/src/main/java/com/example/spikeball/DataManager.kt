package com.example.spikeball

import android.content.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DataManager {

        private var mContext : Context? = null

        private val SHARED_PREF_NAME = "matchmakingAppSharedPrefs"
        private val SP_PLAYER_NAME = "players"
        private val SP_MATCHUP_NAME = "matchups"

        var mPlayers = arrayListOf<Player>()
        var mMatchupHistory = arrayListOf<Matchup>()

        constructor(context:Context)
        {
            mContext = context
        }

        fun addPlayer(p : Player) : Boolean
        {
            if(getPlayer(p.mName) == null)
            {
                mPlayers.add(p)
                savePlayers()
            }
            else
            {
               return false
            }
            return true
        }

        // returns reference to player if it exists
        fun getPlayer(playerName: String) : Player?
        {
            return mPlayers.find { i -> i.mName == playerName }
        }

        fun deletePlayer(playerName: String)
        {
            mPlayers.removeIf { it.mName == playerName }
            savePlayers()
        }

        fun deleteAllCheckedPlayers()
        {
            var i = 0
            while(i < mPlayers.size){
                if(mPlayers[i].mIsChecked)
                {
                    mPlayers.removeAt(i)
                }
                else
                {
                    i++
                }
            }
            savePlayers()
        }

        fun savePlayers()
        {
            val shPref = mContext!!.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
            val editor = shPref.edit()
            val gson = Gson()
            var json : String = gson.toJson(mPlayers)
            editor.putString(SP_PLAYER_NAME,json)
            editor.apply()
        }

        fun loadPlayers()
        {
            val shPref = mContext!!.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
            val gson = Gson()
            var json = shPref.getString(SP_PLAYER_NAME,null)
            if (json != null)
            {
                val type = object : TypeToken<ArrayList<Player>>() { }.type
                mPlayers = gson.fromJson<ArrayList<Player>>(json, type)
            }
        }

        fun saveMatchupHistory()
        {
            val shPref = mContext!!.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
            val editor = shPref.edit()
            val gson = Gson()
            var json : String = gson.toJson(mMatchupHistory)
            editor.putString(SP_MATCHUP_NAME,json)
            editor.apply()
        }

        fun loadMatchupHistory()
        {
            val shPref = mContext!!.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
            val gson = Gson()
            var json = shPref.getString(SP_MATCHUP_NAME,null)
            val type = object : TypeToken<ArrayList<Player>>() { }.type
            mMatchupHistory = gson.fromJson<ArrayList<Matchup>>(json, type)
        }

}