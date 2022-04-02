package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentMonthsHolderBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.adapters.MonthPagerAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.CalendarSharedVM
import uz.perfectalgorithm.projects.tezkor.utils.calendar.DAY_CODE
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import uz.perfectalgorithm.projects.tezkor.utils.calendar.STAFF_ID
import uz.perfectalgorithm.projects.tezkor.utils.timberLog

/***
 * Abduraxmonov Abdulla 11/09/2021
 * bu Kalendar qismida oy qismi uchun viewPager ui
 */

class MonthHolderFragment : Fragment() {
    private val PREFILLED_MONTHS = 240

    private var _binding: FragmentMonthsHolderBinding? = null
    private val binding: FragmentMonthsHolderBinding
        get() = _binding ?: throw NullPointerException(
            resources.getString(R.string.null_binding)
        )
    private var currentDayCode: String = ""
    private var staffId: Int = 0
    val sharedVM: CalendarSharedVM by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentDayCode =
            requireArguments().getString(DAY_CODE, Formatter.getDayCodeFromDateTime(DateTime.now()))
        staffId = requireArguments().getInt(STAFF_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthsHolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getMonths(code: String): List<String> {
        val months = ArrayList<String>(PREFILLED_MONTHS)
        val today = Formatter.getDateTimeFromCode(code).withDayOfMonth(1)
        for (i in (-PREFILLED_MONTHS / 2).rangeTo(PREFILLED_MONTHS / 2)) {
            months.add(Formatter.getDayCodeFromDateTime(today.plusMonths(i)))
        }
        return months
    }

    private fun setupFragment() {
        val monthList = getMonths(currentDayCode)
        binding.fragmentMonthsViewpager.apply {
            adapter =
                MonthPagerAdapter(childFragmentManager, monthList, staffId)
            val defaultMonthlyPage = monthList.size / 2
            addOnPageChangeListener(object :
                ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {
                    sharedVM.selectionDate(Formatter.getDateTimeFromCode(monthList[position]),false)
                }

                override fun onPageScrollStateChanged(state: Int) {
                }

            })
            currentItem = defaultMonthlyPage
            offscreenPageLimit = 1
        }
    }

    fun itemClick(dayCode: String) {
        findNavController().navigate(
            R.id.action_month_navigation_to_day_navigation,
            bundleOf(DAY_CODE to dayCode, STAFF_ID to staffId)
        )
    }
}
