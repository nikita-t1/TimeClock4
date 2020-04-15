package com.studio.timeclock4

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.Display
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.studio.timeclock4.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val title by lazy {resources.getText(R.string.app_name_final).toString()}


    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.e("HELLO SIR")
        Timber.w(PreferenceHelper.read("enable saving", false).toString())
        Timber.w(PreferenceHelper.read("enable database recreation", false).toString())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        //TODO belongs into the Application class
        val appUpdater = AppUpdater(this)
//        appUpdater.showEvery(3)
        appUpdater.setUpdateFrom(UpdateFrom.JSON)
        appUpdater.setUpdateJSON(PreferenceHelper.read(PreferenceHelper.UPDATE_LINK, "URL to your JSON File"))
        appUpdater.setDisplay(Display.SNACKBAR)
        kotlin.runCatching {appUpdater.start()}



        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(bottom_nav, navController)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //Dark Icons
        window.statusBarColor = resources.getColor(R.color.alpha, null)     //Transparent Background


        navController.addOnDestinationChangedListener{ controller, destination, arguments ->
            if (BuildConfig.DEBUG){
                val ss = SpannableString(title)
                when(PreferenceHelper.read(PreferenceHelper.DEV_ColorTitle_U, false)){
                    true -> ss.setSpan(ForegroundColorSpan(PreferenceHelper.read(PreferenceHelper.DEV_TitleColor, 0)), 0, 1, 0)
                    false -> ss.setSpan(ForegroundColorSpan(PreferenceHelper.read(PreferenceHelper.DEV_TitleColor, 0)), 1, 5, 0)
                }
                findViewById<TextView>(R.id.toolbar_title).text = ss
            } else findViewById<TextView>(R.id.toolbar_title).text = title
            supportActionBar?.setDisplayHomeAsUpEnabled(false)

            if(destination.id == R.id.destination_listing) {
                window.statusBarColor = resources.getColor(R.color.light_pink, null)
                toolbar.visibility = View.GONE
                toolbar_title.visibility = View.GONE
            } else {
                window.statusBarColor = resources.getColor(R.color.alpha, null)     //Transparent Background
                toolbar.visibility = View.VISIBLE
                toolbar_title.visibility = View.VISIBLE
            }
            if (destination.id == R.id.destination_settings || destination.id == R.id.destination_timeSettings || destination.id == R.id.destination_about){
                findViewById<TextView>(R.id.toolbar_title).text = destination.label.toString()
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
            if (destination.id == R.id.destination_options) findViewById<TextView>(R.id.toolbar_title).text = destination.label.toString()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    override fun onResume() {
        super.onResume()
        Timber.e("HELLO MADAM")
    }

}
