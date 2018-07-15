package com.markiantorno.weathertestapp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.markiantorno.weathertestapp.objects.Main
import com.markiantorno.weathertestapp.services.CurrentWeatherService
import com.markiantorno.weathertestapp.ui.currentweather.CurrentWeatherDisplay
import com.markiantorno.weathertestapp.ui.currentweather.CurrentWeatherPresenter
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.temperature_display_view.*
import java.math.RoundingMode
import java.text.DecimalFormat

class LandingActivity : AppCompatActivity(), HasSupportFragmentInjector, CurrentWeatherDisplay {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var currentWeatherService: CurrentWeatherService

    lateinit var currentWeatherPresenter: CurrentWeatherPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        currentWeatherPresenter = CurrentWeatherPresenter(currentWeatherService)

        fab.setOnClickListener { view ->
            currentWeatherPresenter.fetchWeather("Toronto")
            Snackbar.make(view, "Fetching weather", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun weatherLoaded(mainWeather: Main) {
        current_temp.text = formatTemp(mainWeather.tempCelcius)
        max_temp.text = formatTemp(mainWeather.tempMaxCelcius)
        min_temp.text = formatTemp(mainWeather.tempMinCelcius)
    }

    private fun formatTemp(temp: Double?): String {
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.CEILING
        return df.format(temp)
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector
}
