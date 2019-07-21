package com.example.spikeball

import android.content.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*


class DataManager {

        private var mContext : Context? = null

        private val SHARED_PREF_NAME = "matchmakingAppSharedPrefs"
        private val SP_PLAYER_NAME = "players"
        private val SP_MATCHUP_NAME = "matchups"

        private val BACKUP_FILE_NAME = "PlayersBKP"
        private val DOWNLOADS_DIR = "/storage/emulated/0/Download"

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

        fun savePlayers() : String
        {
            val shPref = mContext!!.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
            val editor = shPref.edit()
            val gson = Gson()
            var json : String = gson.toJson(mPlayers)
            editor.putString(SP_PLAYER_NAME,json)
            editor.apply()
            return json
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

        // TODO: add some kind of team history


        fun exportToFile(context:Context) : String
        {
            //TODO: specify other directory, so that it isnt deleted upon app deinstallation
            val path = context.getExternalFilesDir(null)
            val letDirectory = File(path, "EXPORT")
            letDirectory.mkdirs()
            val file = File(letDirectory, BACKUP_FILE_NAME + ".txt")
            FileOutputStream(file).use {
                it.write(savePlayers().toByteArray())
            }
            return path.absolutePath.toString()
        }

        fun importFromFile(context:Context) : String
        {
            // read file
            val path = context.getExternalFilesDir(null)
            val letDirectory = File(path, "EXPORT")
            val file = File(letDirectory, BACKUP_FILE_NAME + ".txt")

            var json = FileInputStream(file).bufferedReader().use { it.readText() }

            val gson = Gson()

            if (json != null)
            {
                val type = object : TypeToken<ArrayList<Player>>() { }.type
                mPlayers = gson.fromJson<ArrayList<Player>>(json, type)
                savePlayers()
                return "Imported data from " + file.absolutePath.toString()
            }

            return "No data file called '" + BACKUP_FILE_NAME + ".txt' is available in " + letDirectory.absolutePath.toString()
        }

        fun importFromDownloads() : String
        {
            var filenames = arrayListOf<String>()

            File(DOWNLOADS_DIR).walk().filter { p -> p.name.endsWith(".txt") && p.name.contains("PlayersBKP")}.forEach { filenames.add( it.name) }
            filenames.sort()

            var fileName : String

            if( filenames[filenames.lastIndex] == BACKUP_FILE_NAME + ".txt")
            {
                if(filenames.size > 1)
                {
                    fileName = filenames[filenames.lastIndex-1]
                }
                else
                {
                    fileName = filenames[0]
                }
            }
            else
            {
                fileName = filenames.last()
            }
            
            val file = File(DOWNLOADS_DIR,fileName)

            var json = FileInputStream(file).bufferedReader().use { it.readText() }

            val gson = Gson()

            if (json != null)
            {
                val type = object : TypeToken<ArrayList<Player>>() { }.type
                mPlayers = gson.fromJson<ArrayList<Player>>(json, type)
                savePlayers()
                return "Imported data from " + file.absolutePath.toString()
            }

            return "No data file called '" + BACKUP_FILE_NAME + ".txt' was found in " + DOWNLOADS_DIR
        }


}