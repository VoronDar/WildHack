package com.astery.thisapp

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class LocalStorage @Inject constructor(@ApplicationContext var context: Context) {


    @Throws(ValueNotFoundException::class)
    fun getToken(now: Date): String {
        val tokenValidUntil = getPref(context).getLong(tokenValidUntilPref(), 0)
        if (now.time < tokenValidUntil) throw ValueNotFoundException()
        return getPref(context).getString(tokenPref(), null) ?: throw ValueNotFoundException()
    }

    fun setToken(token: String, tokenValidUntil: Long) {
        getPref(context).edit {
            putLong(tokenValidUntilPref(), tokenValidUntil)
            putString(tokenPref(), token)
        }
    }

    /** it assumes, that this function may be called only after authorisation */
    @Throws(ValueNotFoundException::class)
    fun getRefreshToken(): String {
        return getPref(context).getString(tokenPref(), null) ?: throw ValueNotFoundException()

    }


    fun setRefreshToken(refreshToken: String) {
        getPref(context).edit {
            putString(refreshTokenPref(), refreshToken)
        }
    }


    fun isEntered(): Boolean {
        return getPref(context).getBoolean(isEnteredPref(), false)
    }


    fun setEntered(entered:Boolean) {
        getPref(context).edit {
            putBoolean(isEnteredPref(), entered)
        }
    }



    private fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences("prefs", 0)
    }


    private fun tokenPref() = "token"
    private fun tokenValidUntilPref() = "tokenvailduntil"
    private fun refreshTokenPref() = "refreshToken"
    private fun isEnteredPref() = "entered"

}

class ValueNotFoundException : Exception()