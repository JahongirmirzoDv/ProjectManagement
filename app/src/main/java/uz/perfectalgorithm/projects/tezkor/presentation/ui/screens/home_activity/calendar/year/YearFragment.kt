package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.calendar.year

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.joda.time.DateTime
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentYearBinding
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.calendar.CalendarSharedVM
import uz.perfectalgorithm.projects.tezkor.utils.calendar.Formatter
import uz.perfectalgorithm.projects.tezkor.utils.calendar.YEAR_NAME
import uz.perfectalgorithm.projects.tezkor.utils.timberLog

/***
 * bu Kalendar qismida yil uchun ui qismi asosiy qism SmallMonthView custom qilingan va yildagi
 * oylar shundan olingan
 */

class YearFragment : Fragment() {

    private var year = 0

    companion object {
        fun newInstance(year: Int): YearFragment {
            val fragment = YearFragment()
            val bundle = Bundle()
            bundle.putInt(YEAR_NAME, year)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var dateTime: DateTime
    private var _binding: FragmentYearBinding? = null
    private val binding: FragmentYearBinding
        get() = _binding ?: throw NullPointerException(
            resources.getString(
                R.string.null_binding
            )
        )
    val calendarSharedVM: CalendarSharedVM by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        year = arguments?.getInt(YEAR_NAME) ?: DateTime.now().yearOfEra
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYearBinding.inflate(layoutInflater, container, false)
        dateTime = DateTime().withDate(year, 1, 1)
        setFirstDay(dateTime)
        setDateYear()
        return binding.root
    }


    private fun setDateYear() {
        binding.month2.days = dateTime.withMonthOfYear(2).dayOfMonth().maximumValue
    }


    private fun setFirstDay(dateTime: DateTime) {
        binding.month1.firstDay = dateTime.withMonthOfYear(1).dayOfWeek
        binding.month2.firstDay = dateTime.withMonthOfYear(2).dayOfWeek
        binding.month3.firstDay = dateTime.withMonthOfYear(3).dayOfWeek
        binding.month4.firstDay = dateTime.withMonthOfYear(4).dayOfWeek
        binding.month5.firstDay = dateTime.withMonthOfYear(5).dayOfWeek
        binding.month6.firstDay = dateTime.withMonthOfYear(6).dayOfWeek
        binding.month7.firstDay = dateTime.withMonthOfYear(7).dayOfWeek
        binding.month8.firstDay = dateTime.withMonthOfYear(8).dayOfWeek
        binding.month9.firstDay = dateTime.withMonthOfYear(9).dayOfWeek
        binding.month10.firstDay = dateTime.withMonthOfYear(10).dayOfWeek
        binding.month11.firstDay = dateTime.withMonthOfYear(11).dayOfWeek
        binding.month12.firstDay = dateTime.withMonthOfYear(12).dayOfWeek



        binding.month1.setOnClickListener {
            clickMonth(1)
        }
        binding.month2.setOnClickListener {
            clickMonth(2)
        }
        binding.month3.setOnClickListener {
            clickMonth(3)
        }
        binding.month4.setOnClickListener {
            clickMonth(4)
        }
        binding.month5.setOnClickListener {
            clickMonth(5)
        }
        binding.month6.setOnClickListener {
            clickMonth(6)
        }
        binding.month7.setOnClickListener {
            clickMonth(7)
        }
        binding.month8.setOnClickListener {
            clickMonth(8)
        }
        binding.month9.setOnClickListener {
            clickMonth(9)
        }
        binding.month10.setOnClickListener {
            clickMonth(10)
        }
        binding.month11.setOnClickListener {
            clickMonth(11)
        }
        binding.month12.setOnClickListener {
            clickMonth(12)
        }

        timberLog(currentMonth().toString(), "LLLLLL")
        if (isCurrentYear(year)) {
            when (currentMonth()) {
                1 -> {
                    binding.month1.isCurrentMonth = true
                }
                2 -> {
                    binding.month2.isCurrentMonth = true
                }
                3 -> {
                    binding.month3.isCurrentMonth = true
                }
                4 -> {
                    binding.month4.isCurrentMonth = true
                }
                5 -> {
                    binding.month5.isCurrentMonth = true
                }
                6 -> {
                    binding.month6.isCurrentMonth = true
                }
                7 -> {
                    binding.month7.isCurrentMonth = true
                }
                8 -> {
                    binding.month8.isCurrentMonth = true
                }
                9 -> {
                    binding.month9.isCurrentMonth = true
                }
                10 -> {
                    binding.month10.isCurrentMonth = true
                }
                11 -> {
                    binding.month11.isCurrentMonth = true
                }
                12 -> {
                    binding.month12.isCurrentMonth = true
                }
            }
        }
    }

    private fun clickMonth(monthCount: Int) {
        calendarSharedVM.clickedMonthEvent(
            Formatter.getDayCodeFromDateTime(
                DateTime().withDate(year, monthCount, 1)
            )
        )
    }

    private fun isCurrentYear(year: Int): Boolean {
        return year == DateTime.now().yearOfEra
    }

    private fun currentMonth(): Int =
        DateTime.now().monthOfYear


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}