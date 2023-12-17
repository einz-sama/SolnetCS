package com.einz.solnetcs.data

import android.content.Context

class LoginPreferences(context: Context) {
    private val preference = context.getSharedPreferences("loginPref", Context.MODE_PRIVATE)

    fun setIdCustomer(idCustomer:String){
        val edit = preference.edit()
        edit.putString("idCustomer",idCustomer)
        edit.apply()
    }

    fun getIdCustomer():String? {
        return preference.getString("idCustomer", null)
    }

    fun clearData(){
        val edit = preference.edit().clear()
        edit.apply()
    }
}