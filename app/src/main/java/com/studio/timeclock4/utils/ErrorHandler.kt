package com.studio.timeclock4.utils

import android.content.Context
import com.studio.timeclock4.BuildConfig
import es.dmoral.toasty.Toasty

object ErrorHandler{

    lateinit var context : Context

    fun init(context: Context){
        this.context = context
    }

    fun react(error: ErrorTypes) {
        if(BuildConfig.DEBUG){
            showToast(error)
        }
    }

    private fun showToast(error : ErrorTypes){
        Toasty.error(context, error.errorString + " {ERROR${error.ordinal+1}}").show()
    }

}

enum class ErrorTypes(val errorString: String) {
    ERROR01("Database Not Found"),
    ERROR02("StartDate Not Set"),
    ERROR03("Huge Frame Drop"),
    ERROR04("StartTime Not Found"),
    ERROR05("No WorkDay to Edit")

}









