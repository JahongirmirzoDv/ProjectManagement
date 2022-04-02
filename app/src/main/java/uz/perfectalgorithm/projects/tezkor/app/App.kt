package uz.perfectalgorithm.projects.tezkor.app

import android.app.Application
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import uz.perfectalgorithm.projects.tezkor.BuildConfig
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage

/**
 * Created by Raximjanov Davronbek on 16-06-2021
 **/


@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        super.onCreate()
        instance = this
        LocalStorage.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }

    companion object {
        lateinit var instance: App
    }


}