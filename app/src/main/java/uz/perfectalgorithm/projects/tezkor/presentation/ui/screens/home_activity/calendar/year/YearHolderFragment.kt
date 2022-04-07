package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.year

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentYearHolderBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.adapters.YearPagerAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.CalendarSharedVM
import uz.perfectalgorithm.projects.tezkor.utils.calendar.DAY_CODE
import uz.perfectalgorithm.projects.tezkor.utils.livedata.EventObserver

/***
 * bu Kalendar qismida yil qismi uchun viewPager ui
 */

class YearHolderFragment : Fragment() {

    private var _binding: FragmentYearHolderBinding? = null
    private val binding: FragmentYearHolderBinding
        get() = _binding ?: throw NullPointerException(
            resources.getString(
                R.string.null_binding
            )
        )
    private lateinit var pagerAdapter: YearPagerAdapter
    private val YEARS_COUNT = 60
    var year = DateTime.now().yearOfEra
    var lastYear = DateTime.now().yearOfEra
    val calendarSharedVM: CalendarSharedVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYearHolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        loadObserver()
    }

    private fun loadObserver() {
        calendarSharedVM.clickMonthLiveData.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                R.id.to_monthNavigation,
                bundleOf(DAY_CODE to it)
            )
        })
    }

    override fun onResume() {
        super.onResume()
        calendarSharedVM.selectionDate(
            DateTime().withDate(lastYear, 1, 1),
            true
        )
    }

    private fun setUpAdapter() {
        val years = getYears(year)
        pagerAdapter = YearPagerAdapter(requireActivity(), years)
        val defaultYearPage = years.size / 2
        binding.vpYear.apply {
            adapter = pagerAdapter
            setCurrentItem(defaultYearPage, false)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    calendarSharedVM.selectionDate(DateTime().withDate(years[position], 1, 1), true)
                    lastYear = years[position]
                }
            })
        }
    }

    private fun getYears(currentYear: Int): List<Int> {
        val years = ArrayList<Int>(YEARS_COUNT)
        years += currentYear - YEARS_COUNT / 2..currentYear + YEARS_COUNT / 2
        return years
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

