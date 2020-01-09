package com.studio.timeclock4.utils

import android.content.Context
import es.dmoral.toasty.Toasty

object ErrorHandler{

    lateinit var context : Context

    fun init(context: Context){
        this.context = context
    }

    fun react(error: ErrorTypes) {
        showToast(error)
    }

    private fun showToast(error : ErrorTypes){
        Toasty.error(context, error.errorString).show()
    }

}

enum class ErrorTypes(val errorString: String) {
    ERROR01("Database Not Found"),
    ERROR02("StartDate Not Set")
}









