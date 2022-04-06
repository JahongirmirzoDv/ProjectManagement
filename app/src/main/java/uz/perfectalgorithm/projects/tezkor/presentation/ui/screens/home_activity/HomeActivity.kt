package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import io.github.hyuwah.draggableviewlib.DraggableListener
import io.github.hyuwah.draggableviewlib.DraggableView
import io.github.hyuwah.draggableviewlib.setupDraggable
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.group.ChatGroupListResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ActivityHomeBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.SectionsDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.QuickSheetDialogMaker
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.CalendarFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.chat.NavigationChatFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.OthersFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.others.setting.LanguageFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.NavigationMeetingFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.tasks.NavigationTasksFragment
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.workers.WorkersFragment
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.HomeActivityViewModel
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.chat.company_group.CompanyGroupChatViewModel
import uz.perfectalgorithm.projects.tezkor.utils.SharedPref
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hide
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.keypboard.hideKeyboard
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import uz.perfectalgorithm.projects.tezkor.utils.views.BottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.views.OnItemMenuListener
import uz.perfectalgorithm.projects.tezkor.utils.visible
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null
    private val binding: ActivityHomeBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")
    private val sharedPref by lazy { SharedPref(this) }

    @Inject
    lateinit var sectionsDialog: SectionsDialog

    @Inject
    lateinit var sheetDialog: QuickSheetDialogMaker

    @Inject
    lateinit var storage: LocalStorage

    private var lastOnBackPressed: Long = 0

    var listenBackClick: EmptyBlock? = null

    private val homeViewModel: HomeActivityViewModel by viewModels()
    private val viewModel: CompanyGroupChatViewModel by viewModels()

    private lateinit var dvImageDraggable: DraggableView<LinearLayout>

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }

    private val debugPos = object : DraggableListener {
        override fun onPositionChanged(view: View) {
            if (view.x.toInt() < 300) {
                binding.quickButton.rotation = 180f
                binding.quickIdeaImage.rotation = 180f
            } else {
                binding.quickButton.rotation = 0f
                binding.quickIdeaImage.rotation = 0f
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseMessaging.getInstance().subscribeToTopic("all")
        var locale = Locale(storage.lan ?: "uz")

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        Locale.setDefault(locale)
        val resources: Resources = resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        binding.apply {
            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                when (destination.id) {
                    R.id.navigation_chat -> {
                        bottomNavigationView.setDefaultSelectedMenu(BottomMenu.CHAT)
                    }
                    R.id.navigation_calendar -> {
                        bottomNavigationView.setDefaultSelectedMenu(BottomMenu.CALENDAR)
                    }
                    R.id.navigation_task -> {
                        bottomNavigationView.setDefaultSelectedMenu(BottomMenu.TASK)
                    }
                    R.id.navigation_meeting -> {
                        bottomNavigationView.setDefaultSelectedMenu(BottomMenu.MEETING)
                    }
                    R.id.navigation_workers -> {
                        bottomNavigationView.setDefaultSelectedMenu(BottomMenu.WORKER)
                    }
                    R.id.navigation_others -> {
                        bottomNavigationView.setDefaultSelectedMenu(BottomMenu.DAShBOARD)
                    }
                    R.id.navigation_settings -> {
                        bottomNavigationView.setDefaultSelectedMenu(BottomMenu.DAShBOARD)
                    }
                }
            }
        }

        setSupportActionBar(binding.toolbar)
        initBottomNavMenu()
        loadViews()
        loadObservers()
//        navigateFragment(intent)
    }

    @SuppressLint("ResourceAsColor")
    private fun dialog(): Locale {
        var locale = Locale(storage.lan ?: "uz")
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.language_dialog)
        dialog.show()

        dialog.findViewById<CardView>(R.id.en_lan_card).setOnClickListener {
            it.setBackgroundColor(R.color.tanlash)
            sharedPref.language = true
            LanguageFragment.setLocale(this, "en")
            storage.lan = "en"
            locale = Locale(storage.lan ?: "en")
            dialog.cancel()
            this.recreate()
        }

        dialog.findViewById<CardView>(R.id.ru_lan_card).setOnClickListener {
            it.setBackgroundColor(R.color.tanlash)
            sharedPref.language = true
            LanguageFragment.setLocale(this, "ru")
            storage.lan = "ru"
            locale = Locale(storage.lan ?: "ru")
            dialog.cancel()
            this.recreate()
        }

        dialog.findViewById<CardView>(R.id.uz_lan_card).setOnClickListener {
            it.setBackgroundColor(R.color.tanlash)
            sharedPref.language = true
            LanguageFragment.setLocale(this, "uz")
            storage.lan = "uz"
            locale = Locale(storage.lan ?: "uz")
            dialog.cancel()
            this.recreate()
        }
        return locale
    }

    override fun onBackPressed() {

        when (navController.currentDestination?.id) {
            R.id.navigation_chat -> {
                if (doubleClicked()) {
                    finishAffinity()
                }
                return
            }

            R.id.editProfileFragment -> {
                listenBackClick?.invoke()
                return
            }

            else -> {
                super.onBackPressed()
            }
        }
        hideKeyboard()
    }

    private fun doubleClicked(): Boolean {
        return if (lastOnBackPressed + 2000 >= System.currentTimeMillis()) {
            true
        } else {
            val text = getString(R.string.exit_message)
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            lastOnBackPressed = System.currentTimeMillis()
            false
        }
    }

    private val createTaskObserver = Observer<Unit> {
        findNavController(R.id.createTaskFragment)
    }
    private val getNotificationCount = Observer<Int> {
        if (it != 0) {
            binding.tvNotification.visible()
            binding.tvNotification.text = it.toString()
        }
    }

    private fun loadObservers() {
        homeViewModel.openCreateTaskScreen.observe(this, createTaskObserver)
        homeViewModel.notificationCount.observe(this, getNotificationCount)

        viewModel.chatListLiveData.observe(this, chatListObserver)
        viewModel.progressLiveData.observe(this, progressObserver)
    }

    private val progressObserver = Observer<Boolean> {

    }

    private val chatListObserver =
        Observer<List<ChatGroupListResponse.GroupChatDataItem>> { chatList ->
            if (chatList.isEmpty()) {
            } else {
                viewModel.mChatList.clear()
                viewModel.mChatList.addAll(chatList.filter { it.isChannel == viewModel.isOnlyWriteAdmin })
                storage.selectedCompanyName = chatList[0].title ?: ""
            }
        }

    fun changeTitle() {
        binding.toolbarCompanyName.title = storage.selectedCompanyName
    }

    private fun loadViews() {
        homeViewModel.getNotificationCount()
        if (storage.selectedCompanyName.isEmpty()) {
            viewModel.getChatList()
        } else {
            binding.toolbarCompanyName.title = storage.selectedCompanyName
        }
        dvImageDraggable = binding.quickButton.setupDraggable()
            .setStickyMode(DraggableView.Mode.STICKY_X)
            .setListener(debugPos)
            .setAnimated(true)
            .build()

        binding.quickButton.setOnClickListener {
            sheetDialog.showBottomSheetDialog(this, {
                navController.navigate(R.id.quickIdeasBoxesFragment)
            }, {
                navController.navigate(R.id.quickPlansFragment)
            })
        }

        binding.notificationLayout.setOnClickListener {
            binding.tvNotification.hide()
            navController.navigate(R.id.navigation_notification)
        }

        binding.toolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }
    }

    fun findStartDestination(graph: NavGraph): NavDestination? {
        var startDestination: NavDestination? = graph
        while (startDestination is NavGraph) {
            val parent = startDestination
            startDestination = parent.findNode(parent.startDestination)
        }
        return startDestination
    }


    private fun initBottomNavMenu() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_chat,
                R.id.navigation_calendar,
                R.id.navigation_task,
                R.id.navigation_meeting,
                R.id.navigation_workers,
                R.id.navigation_others,
                R.id.quickPlansFragment,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
//        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        binding.bottomNavigationView.itemSelectedListener = object : OnItemMenuListener {
            var isSetStart = false
            override fun onSelectedItem(itemMenu: BottomMenu) {
                if (itemMenu == binding.bottomNavigationView.selectedItem) return
                var builder = NavOptions.Builder().setLaunchSingleTop(true)
                binding.bottomNavigationView.selectedItem
                val isC =
                    navController.currentDestination?.parent?.findNode(itemMenu.resId) is ActivityNavigator.Destination
                if (isC) {
                    builder.setEnterAnim(R.anim.nav_default_enter_anim)
                        .setExitAnim(R.anim.nav_default_exit_anim)
                        .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                        .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                } else {
                    builder.setEnterAnim(R.animator.nav_default_enter_anim)
                        .setExitAnim(R.animator.nav_default_exit_anim)
                        .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
                        .setPopExitAnim(R.animator.nav_default_pop_exit_anim)
                }
                if (!isSetStart) {
                    builder.setPopUpTo(
                        findStartDestination(navController.graph)?.id
                            ?: R.navigation.home_navigation,
                        false
                    )
                    isSetStart = true
                }
                val options = builder.build()
                try {
                    navController.popBackStack()
                    navController.navigate(itemMenu.resId, null, options)
                } catch (e: IllegalArgumentException) {
                }
//                when (itemMenu) {
////                    BottomMenu.CHAT -> {
////                        navController.navigate(R.id.navigation_chat)
////                    }
////                    BottomMenu.CALENDAR -> {
////                        navController.navigate(R.id.navigation_calendar)
////
////                    }
////                    BottomMenu.TASK -> {
////                        navController.navigate(R.id.navigation_task)
////
////                    }
////                    BottomMenu.MEETING -> {
////                        navController.navigate(R.id.navigation_meeting)
////
////                    }
////                    BottomMenu.WORKER -> {
////                        navController.navigate(R.id.navigation_workers)
////
////                    }
////                    BottomMenu.DAShBOARD -> {
////                        navController.navigate(R.id.navigation_others)
////
////                    }
////                }
            }

        }


        binding.buttonAdd.setOnClickListener {
            sectionsDialog.showSectionsBottomSheetDialog(this, {
                navController.navigate(R.id.navigation_create_note)
            },
                {
                    navController.navigate(R.id.createTaskFragment)
                },
                {
                    navController.navigate(R.id.createProjectFragment)
                },
                {
                    navController.navigate(R.id.createMeetingFragment)
                },
                {
                    navController.navigate(R.id.createDatingFragment)
                })
        }
    }

    private fun openFragmentMainScreen(screen: BottomMenu) {
        hideAllMainScreen(screen)

        var fragment = supportFragmentManager.findFragmentByTag(screen.resId.toString())

        if (fragment != null && fragment.isHidden) {
            supportFragmentManager.beginTransaction().show(fragment).commitNow()
        } else if (fragment == null) {

            fragment = when (screen) {
                BottomMenu.CHAT -> NavigationChatFragment()
                BottomMenu.CALENDAR -> CalendarFragment()
                BottomMenu.TASK -> NavigationTasksFragment()
                BottomMenu.MEETING -> NavigationMeetingFragment()
                BottomMenu.WORKER -> WorkersFragment()
                BottomMenu.DAShBOARD -> OthersFragment()
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.nav_host_fragment, fragment, screen.resId.toString()).commitNow()

        }

    }

    private fun hideAllMainScreen(screen: BottomMenu) {
        val fragmentsMain: ArrayList<Fragment> = arrayListOf()
        BottomMenu.values().filter { it != screen }.forEach {
            val fragment = supportFragmentManager.findFragmentByTag(it.resId.toString())
            if (fragment != null)
                fragmentsMain.add(fragment)
        }

        fragmentsMain.forEach {
            supportFragmentManager.beginTransaction().hide(it).commitNow()
        }
    }

    fun setNavigationIcon() {
        try {
            binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showBottomMenu() {
        try {
            binding.bottomNavigationView.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideBottomMenu() {
        try {
            binding.bottomNavigationView.hide()
        } catch (e: Exception) {
            e.printStackTrace()
            timberLog("${e.printStackTrace()},")
        }
    }

    fun showAppBar() {
        try {
            binding.appBarLayout.show()
        } catch (e: Exception) {

        }
    }

    fun hideAppBar() {
        try {
            binding.appBarLayout.hide()
        } catch (e: Exception) {

        }
    }

    fun appBarSize(): Int {
        return try {
            binding.appBarLayout.height + binding.bottomNavigationView.height
        } catch (e: Exception) {
            0
        }
    }

    fun showQuickButton() {
        try {
            binding.quickButton.show()
        } catch (e: Exception) {

        }
    }

    fun hideQuickButton() {
        try {
            binding.quickButton.hide()
        } catch (e: Exception) {
            e.printStackTrace()
            timberLog("${e.printStackTrace()},")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

//
    //    fun changeCount(count: Int) {
//        binding.tvNotification.text = count.toString()
//    }

//    private fun navigateFragment(intent: Intent?) {
//        if (intent != null && intent.action == CALENDAR_ACTION) {
//            val eventTypeVsId = intent.getStringExtra(EVENT_TYPE_VS_ID)?.split(" ")
//            val eventId = eventTypeVsId?.get(0)?.toInt()
//            val eventType = eventTypeVsId?.get(1)
//            timberLog(eventId.toString())
//            timberLog(eventType.toString())
//            if (eventId != 0) {
//                when (eventType) {
//                    "task" -> navController.navigate(R.id.action_global_task_detail)
//                    "note" -> navController.navigate(R.id.action_global_note_detail)
//                    "dating" -> navController.navigate(R.id.action_global_dating_detail)
//                    "meeting" -> navController.navigate(R.id.action_global_meeting_detail)
//                }
//            }
//        }
//    }


    fun onBackClickListener(emptyBlock: EmptyBlock) {
        listenBackClick = emptyBlock
    }

}