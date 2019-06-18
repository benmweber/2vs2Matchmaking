package com.example.spikeball

import android.content.SharedPreferences

class DataInterface {

    companion object StaticFunctions
    {
        val mNameOfSharedPrefForPlayerList = "sharedPrefForPlayerList"
        var mSharedPrefsForPlayerList: SharedPreferences? = null
        var mEditorOfSharedPrefsForPlayerList: SharedPreferences.Editor? = null

        fun GetPlayer(name:String)
        {
            
        }
    }
}