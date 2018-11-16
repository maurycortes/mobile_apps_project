package mx.itesm.proyectofinalmoviles

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import mx.itesm.proyectofinalmoviles.MainActivity
import java.util.HashMap

class SessionManager {

    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    var con: Context
    var PRIVATE_MODE: Int = 0

    constructor(con: Context) {
        this.con = con
        pref = con.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    companion object {
        val PREF_NAME = "NAME"
        val IS_LOGIN = "LOGIN"
        val KEY_NAME = "username"
        val USER_TYPE = "user type"
    }

    fun createLoginSession(username: String, type: String) {
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_NAME, username)
        editor.putString(USER_TYPE, type)
        editor.commit()
    }

    fun checkLogin() {
        if(!this.isLoggedIn()) {
            val i = Intent(con, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            con.startActivity(i)
        }
    }

    fun getUserDetails() : HashMap<String, String> {
        val user: Map<String, String> = HashMap()
        (user as HashMap).put(KEY_NAME, pref.getString(KEY_NAME, null)!!)
        (user).put(USER_TYPE, pref.getString(USER_TYPE, null)!!)
        return user
    }

    fun LogoutUser() {
        editor.clear()
        editor.commit()

        val i = Intent(con, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        con.startActivity(i)
    }

    fun isLoggedIn(): Boolean {
        return pref.getBoolean(IS_LOGIN, false)
    }
}