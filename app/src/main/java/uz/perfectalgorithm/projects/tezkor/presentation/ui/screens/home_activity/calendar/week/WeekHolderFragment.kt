package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.week

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentWeekHolderBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.adapters.WeekPagerAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.CalendarSharedVM
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import uz.perfectalgorithm.projects.tezkor.utils.calendar.STAFF_ID
import uz.perfectalgorithm.projects.tezkor.utils.calendar.WEEK_START_DATE_TIME
import uz.perfectalgorithm.projects.tezkor.utils.calendar.seconds

/***
 * bu Kalendar qismida hafta qismi uchun viewPager ui
 */

class WeekHolderFragment : Fragment() {
    private var _binding: FragmentWeekHolderBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(resources.getString(R.string.null_binding))

    private var pagerAdapter: WeekPagerAdapter? = null
    private var startDateTime = ""
    private var staffId = 0
    private val WEEK_COUNT = 200


    private var currentDateTS = 0L

    private val sharedVM: CalendarSharedVM by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startDateTime = requireArguments().getString(WEEK_START_DATE_TIME) ?: return
        staffId = requireArguments().getInt(STAFF_ID)
        currentDateTS = (DateTime.parse(startDateTime) ?: DateTime()).seconds()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekHolderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPagerAdapter()
    }

    override fun onResume() {
        super.onResume()
        sharedVM.selectionDate(Formatter.getDateTimeFromTS(currentDateTS),false)
    }

    private fun setUpPagerAdapter() {
        val weekTSs = getWeekTime(currentDateTS)
        pagerAdapter = WeekPagerAdapter(childFragmentManager, weekTSs, staffId)
        val defaultWeeklyPage = weekTSs.size / 2
        binding.weekViewPager.apply {
            adapter = pagerAdapter
            currentItem = defaultWeeklyPage
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    sharedVM.selectionDate(Formatter.getDateTimeFromTS(weekTSs[position]),false)
                }

                override fun onPageScrollStateChanged(state: Int) {}

            })
        }
    }

    private fun getWeekTime(currentDate: Long): List<Long> {
        val weekTS = ArrayList<Long>(WEEK_COUNT)
        val dateTime = Formatter.getDateTimeFromTS(currentDate)
        var currentWeek = dateTime.minusDays(WEEK_COUNT / 2 * 7)
        for (i in 0 until WEEK_COUNT) {
            weekTS.add(currentWeek.seconds())
            currentWeek = currentWeek.plusDays(7)
        }
        return weekTS
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}