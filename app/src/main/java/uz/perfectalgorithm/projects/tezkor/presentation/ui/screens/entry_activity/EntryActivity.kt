package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.databinding.ActivityEntryBinding
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class EntryActivity : AppCompatActivity() {

    private var _binding: ActivityEntryBinding? = null
    private val binding get() = _binding!!

    private lateinit var graph: NavGraph
    private lateinit var host: NavHostFragment


    @Inject
    lateinit var storgage: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEntryBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        val locale = Locale(storgage.lan?:"uz")
        Locale.setDefault(locale)
        val resources: Resources = resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            storgage.firebaseToken = it
            Timber.tag("fcm").d(it)
        }.addOnCanceledListener {

        }
        loadViews()
        loadObservers()
    }

    private fun loadObservers() {

    }

    private fun loadViews() {
        binding.apply {
            host =
                supportFragmentManager.findFragmentById(R.id.entry_host_fragment) as NavHostFragment
            graph = host.navController.navInflater.inflate(R.navigation.entry_navigation)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


