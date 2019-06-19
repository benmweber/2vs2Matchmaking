package com.example.spikeball

import android.content.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DataManager {

        var mContext : Context? = null

        val SHARED_PREF_NAME = "matchmakingAppSharedPrefs"
        val PLAYER_PREF_JSON_NAME = "players"

        var mPlayers = arrayListOf<Player>()
        var mMatchupHistory = arrayListOf<Matchup>()

        constructor(context:Context)
        {
            mContext = context
            //loadPlayers()
            //loadMatchupHistory()
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

        fun getPlayer(playerName: String) : Player?
        {
            return mPlayers.find { i -> i.mName == playerName }
        }

        fun deletePlayer(playerName: String)
        {

        }

        fun savePlayers()
        {
            val shPref = mContext!!.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
            val editor = shPref.edit()
            val gson = Gson()
            var json : String = gson.toJson(mPlayers)
            editor.putString(PLAYER_PREF_JSON_NAME,json)
            editor.apply()
        }

        fun loadPlayers()
        {
            val shPref = mContext!!.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
            val gson = Gson()
            var json = shPref.getString(PLAYER_PREF_JSON_NAME,null)

            val type = object : TypeToken<ArrayList<Player>>() { }.type

            mPlayers = gson.fromJson<ArrayList<Player>>(json, type)
        }

        fun saveMatchupHistory()
        {
            val shPref = mContext!!.getSharedPreferences("MySharedPrefs",Context.MODE_PRIVATE)
            val editor = shPref.edit()
            val gson = Gson()
            var json : String = gson.toJson(mMatchupHistory)
            editor.putString("player list",json)
            editor.apply()
        }

        fun loadMatchupHistory()
        {
            val shPref = mContext!!.getSharedPreferences("MySharedPrefs",Context.MODE_PRIVATE)
            val gson = Gson()
            var json = shPref.getString("player list",null)

            val type = object : TypeToken<ArrayList<Player>>() { }.type

            mMatchupHistory = gson.fromJson<ArrayList<Matchup>>(json, type)
        }

}