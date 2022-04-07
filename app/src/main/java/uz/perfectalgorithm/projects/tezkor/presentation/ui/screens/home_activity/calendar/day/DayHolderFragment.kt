package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDayHolderrBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.adapters.DayPagerAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.CalendarSharedVM
import uz.perfectalgorithm.projects.tezkor.utils.calendar.DAY_CODE
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import uz.perfectalgorithm.projects.tezkor.utils.calendar.STAFF_ID

/***
 * bu Kalendar qismida day qismi uchun viewPager ui
 */

class DayHolderFragment : Fragment() {
    private var _binding: FragmentDayHolderrBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(resources.getString(R.string.null_binding))

    private var pagerAdapter: DayPagerAdapter? = null
    private var currentDayCode = ""
    private var staffId = -1
    private val DAY_COUNT = 400
    private var viewState = 0
    val sharedVM: CalendarSharedVM by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentDayCode = requireArguments().getString(DAY_CODE) ?: return
        staffId = requireArguments().getInt(STAFF_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayHolderrBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPagerAdapter()
        loadObserver()
    }

    override fun onResume() {
        super.onResume()
        sharedVM.selectionDate(Formatter.getDateTimeFromCode(currentDayCode),false)
    }

    private fun loadObserver() {
        sharedVM.rowHeightLiveData.observe(viewLifecycleOwner, changeRowHeight)
        sharedVM.changeStateLiveData.observe(viewLifecycleOwner, changeViewState)
    }

    private fun setUpPagerAdapter() {
        val days = getDayTime(currentDayCode)
        pagerAdapter = DayPagerAdapter(childFragmentManager, days, staffId = staffId)
        val defaultWeeklyPage = days.size / 2
        binding.dayViewPager.apply {
            adapter = pagerAdapter
            currentItem = defaultWeeklyPage
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    pagerAdapter!!.getViewState(viewState)
                }

                override fun onPageSelected(position: Int) {
                    sharedVM.selectionDate(Formatter.getDateTimeFromCode(days[position]),false)
                }

                override fun onPageScrollStateChanged(state: Int) {}

            })
        }
    }

    private fun getDayTime(currentDayCode: String): List<String> {
        val days = ArrayList<String>(DAY_COUNT)
        val currentDate = Formatter.getDateTimeFromCode(currentDayCode)
        for (i in (-DAY_COUNT / 2).rangeTo((DAY_COUNT / 2))) {
            val dayCode = Formatter.getDayCodeFromDateTime(currentDate.plusDays(i))
            days.add(dayCode)
        }
        return days
    }

    private val changeRowHeight = Observer<Float> {
        pagerAdapter?.updateScrollY(binding.dayViewPager.currentItem, it)
    }
    private val changeViewState = Observer<Int> {
        pagerAdapter?.updateViewState(binding.dayViewPager.currentItem, it)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}