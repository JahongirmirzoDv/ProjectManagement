package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCalendarBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.calendar.CalendarSectionsDialog
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.CalendarSharedVM
import uz.perfectalgorithm.projects.tezkor.utils.calendar.*
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter.getMonthName
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter.getTodayCode
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.showBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver
import uz.perfectalgorithm.projects.tezkor.utils.timberLog
import java.util.*
import javax.inject.Inject

/***
 * bu Kalendar uchun obshiy fragment
 */

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding: FragmentCalendarBinding
        get() = _binding ?: throw NullPointerException(resources.getString(R.string.null_binding))

    private var dayCode = getTodayCode()
    private var weekDate = getThisWeekDateTime()
    private var staffId: Int = 0
    private var navController: NavController? = null

    val sharedVM: CalendarSharedVM by activityViewModels()

    @Inject
    lateinit var sectionsDialog: CalendarSectionsDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listenBackStack()


        staffId = arguments?.getInt(STAFF_ID) ?: -1

        if (staffId > 0) {
            hideBottomMenu()
            hideAppBar()
        } else {
            showBottomMenu()
            showAppBar()
        }
        buttonAction()
        loadSharedVM()
    }

    @SuppressLint("SetTextI18n")
    private fun loadSharedVM() {
        sharedVM.setSelectionItem.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.second) {
                    binding.tvCurrentDate.text = it.first.yearOfEra.toString()

                } else {
                    binding.tvCurrentDate.text = getMonthName(
                        requireContext(),
                        it.first.monthOfYear
                    ) + " " + it.first.yearOfEra
                }
            }
        }

        sharedVM.createEventLiveData.observe(viewLifecycleOwner, EventObserver {
            findNavController()
                .navigate(
                    it.first,
                    bundleOf(START_DATE to it.second)
                )
        })
        sharedVM.clickedDetailLiveData.observe(viewLifecycleOwner, EventObserver {
            findNavController()
                .navigate(
                    it.first,
                    it.second
                )
        })
    }

    private fun buttonAction() {
        binding.fabAdd.setOnClickListener {
            createEvent()
        }
        binding.itemDialog.setOnClickListener {
            sectionsDialog.showCalendarBottomSheetDialog(requireContext(), {

                val nestedNavHostFragment =
                    childFragmentManager.findFragmentById(R.id.calendar_fragment_container) as? NavHostFragment
                navController = nestedNavHostFragment?.navController
                navController?.navigate(
                    R.id.year_navigation
                )
            }, {
                timberLog("yearHolder", "KKKCalendar@")
                val nestedNavHostFragment =
                    childFragmentManager.findFragmentById(R.id.calendar_fragment_container) as? NavHostFragment
                navController = nestedNavHostFragment?.navController
                navController?.navigate(
                    R.id.month_navigation,
                    bundleOf(DAY_CODE to dayCode, STAFF_ID to staffId)
                )
            },
                {
                    val nestedNavHostFragment =
                        childFragmentManager.findFragmentById(R.id.calendar_fragment_container) as? NavHostFragment
                    navController = nestedNavHostFragment?.navController
                    navController?.navigate(
                        R.id.week_navigation,
                        bundleOf(WEEK_START_DATE_TIME to weekDate, STAFF_ID to staffId)
                    )
                },
                {
                    val nestedNavHostFragment =
                        childFragmentManager.findFragmentById(R.id.calendar_fragment_container) as? NavHostFragment
                    navController = nestedNavHostFragment?.navController
                    navController?.navigate(
                        R.id.day_navigation,
                        bundleOf(DAY_CODE to dayCode, STAFF_ID to staffId)
                    )
                })
        }

    }

    private fun createEvent() {
        sectionsDialog.showSectionsBottomSheetDialog(requireContext(), {
            findNavController().navigate(
                R.id.action_navigation_calendar_to_navigation_create_note
            )
        }, {
            findNavController().navigate(
                R.id.action_navigation_calendar_to_navigation_create_task
            )
        },
            {
                findNavController().navigate(
                    R.id.action_navigation_calendar_to_navigation_create_meeting
                )
            },
            {
                findNavController().navigate(
                    R.id.action_navigation_calendar_to_navigation_create_dating
                )
            })


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getThisWeekDateTime(): String {
        val currentOffsetHours = TimeZone.getDefault().rawOffset / 1000 / 60 / 60
        val useHours = if (currentOffsetHours >= 10) 8 else 12
        var thisWeek =
            DateTime().withDayOfWeek(1).withHourOfDay(useHours)
        if (DateTime().minusDays(7).seconds() > thisWeek.seconds()) {
            thisWeek = thisWeek.plusDays(7)
        }
        return thisWeek.toString()
    }


    private fun listenBackStack() {

        // Get NavHostFragment
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.calendar_fragment_container)

        // ChildFragmentManager of the current NavHostFragment
        val navHostChildFragmentManager = navHostFragment?.childFragmentManager
        val nestedNavHostFragment =
            childFragmentManager.findFragmentById(R.id.calendar_fragment_container) as? NavHostFragment
        navController = nestedNavHostFragment?.navController
        binding.tvView.text = navController?.currentDestination?.label.toString()


        navHostChildFragmentManager?.addOnBackStackChangedListener {
            val nestedNavHostFragment =
                childFragmentManager.findFragmentById(R.id.calendar_fragment_container) as? NavHostFragment
            navController = nestedNavHostFragment?.navController

            binding.tvView.text = navController?.currentDestination?.label.toString()
        }


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backStackEntryCount = navHostChildFragmentManager!!.backStackEntryCount
                if (backStackEntryCount == 0) {
                    OnBackPressedCallback@ this.isEnabled = false
                    requireActivity().onBackPressed()
                } else {
                    navController?.navigateUp()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


}