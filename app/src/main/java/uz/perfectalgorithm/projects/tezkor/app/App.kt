package uz.perfectalgorithm.projects.tezkor.app

import android.app.Application
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import com.mocklets.pluto.Pluto
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import uz.perfectalgorithm.projects.tezkor.BuildConfig
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage




@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        super.onCreate()
        Lingver.init(this)
        instance = this
        Pluto.initialize(this)
        LocalStorage.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }

    companion object {
        lateinit var instance: App
    }


}