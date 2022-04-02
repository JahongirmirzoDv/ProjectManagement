package uz.perfectalgorithm.projects.tezkor.utils.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.ViewCustomBottomMenuBinding

class CustomBottomNavigationView : RelativeLayout {
    var selectedItem: BottomMenu = BottomMenu.CALENDAR
    var oldSelectedItem: BottomMenu = BottomMenu.CALENDAR
    var itemSelectedListener: OnItemMenuListener? = null


    private val binding =
        ViewCustomBottomMenuBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context?) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        attrs?.let {
//            val array =
//                context.obtainStyledAttributes(attrs, R.styleable.CustomBottomNavigationView)
//            if (array.hasValue(R.styleable.CustomBottomNavigationView_itemMenuIconColor)) {
//                binding.ivSchedule.imageTintList =
//                    array.getColorStateList(R.styleable.CustomBottomNavigationView_itemMenuIconColor)
//                binding.ivCip.imageTintList =
//                    array.getColorStateList(R.styleable.CustomBottomNavigationView_itemMenuIconColor)
//                binding.ivDirectory.imageTintList =
//                    array.getColorStateList(R.styleable.CustomBottomNavigationView_itemMenuIconColor)
//                binding.ivAccount.imageTintList =
//                    array.getColorStateList(R.styleable.CustomBottomNavigationView_itemMenuIconColor)
//            }
//            if (array.hasValue(R.styleable.CustomBottomNavigationView_itemMenuTextColor)) {
//                binding.tvSchedule.setTextColor(
//                    array.getColor(
//                        R.styleable.CustomBottomNavigationView_itemMenuTextColor,
//                        ContextCompat.getColor(context, R.color.selector_bnv_color)
//                    )
//                )
//                binding.tvCip.setTextColor(
//                    array.getColor(
//                        R.styleable.CustomBottomNavigationView_itemMenuTextColor,
//                        ContextCompat.getColor(context, R.color.selector_bnv_color)
//                    )
//                )
//                binding.tvDirectory.setTextColor(
//                    array.getColor(
//                        R.styleable.CustomBottomNavigationView_itemMenuTextColor,
//                        ContextCompat.getColor(context, R.color.selector_bnv_color)
//                    )
//                )
//                binding.tvAccount.setTextColor(
//                    array.getColor(
//                        R.styleable.CustomBottomNavigationView_itemMenuTextColor,
//                        ContextCompat.getColor(context, R.color.selector_bnv_color)
//                    )
//                )
//            }
//            if (array.hasValue(R.styleable.CustomBottomNavigationView_itemMenuTextSize)) {
//                binding.tvSchedule.textSize =
//                    array.getDimension(R.styleable.CustomBottomNavigationView_itemMenuTextSize, 18f)
//                binding.tvCip.textSize =
//                    array.getDimension(R.styleable.CustomBottomNavigationView_itemMenuTextSize, 18f)
//                binding.tvDirectory.textSize =
//                    array.getDimension(R.styleable.CustomBottomNavigationView_itemMenuTextSize, 18f)
//                binding.tvAccount.textSize =
//                    array.getDimension(R.styleable.CustomBottomNavigationView_itemMenuTextSize, 18f)
//            }
//            array.recycle()
        }

        with(binding) {

            llChat.setOnClickListener {
                setSelectable(llChat.id)
                scaleAnim(ivSchedule)
                itemSelectedListener?.onSelectedItem(BottomMenu.CHAT)
                oldSelectedItem = selectedItem
                selectedItem = BottomMenu.CHAT
            }
            llCalendar.setOnClickListener {
                setSelectable(llCalendar.id)
                scaleAnim(ivCip)
                itemSelectedListener?.onSelectedItem(BottomMenu.CALENDAR)
                oldSelectedItem = selectedItem
                selectedItem = BottomMenu.CALENDAR
            }
            llTask.setOnClickListener {
                setSelectable(llTask.id)
                scaleAnim(ivDirectory)
                itemSelectedListener?.onSelectedItem(BottomMenu.TASK)
                oldSelectedItem = selectedItem
                selectedItem = BottomMenu.TASK
            }
            llMeeting.setOnClickListener {
                setSelectable(llMeeting.id)
                scaleAnim(ivAccount)
                itemSelectedListener?.onSelectedItem(BottomMenu.MEETING)
                oldSelectedItem = selectedItem
                selectedItem = BottomMenu.MEETING
            }
            llWork.setOnClickListener {
                setSelectable(llWork.id)
                scaleAnim(ivWork)
                itemSelectedListener?.onSelectedItem(BottomMenu.WORKER)
                oldSelectedItem = selectedItem
                selectedItem = BottomMenu.WORKER
            }
            llMore.setOnClickListener {
                setSelectable(llMore.id)
                scaleAnim(ivMore)
                itemSelectedListener?.onSelectedItem(BottomMenu.DAShBOARD)
                oldSelectedItem = selectedItem
                selectedItem = BottomMenu.DAShBOARD
            }
        }
    }


    fun setDefaultSelectedMenu(defaultSelectItem: BottomMenu) {
        this.selectedItem = defaultSelectItem
        when (defaultSelectItem) {
            BottomMenu.CALENDAR -> {
                setSelectable(binding.llCalendar.id)
                itemSelectedListener?.onSelectedItem(BottomMenu.CALENDAR)
            }
            BottomMenu.TASK -> {
                setSelectable(binding.llTask.id)

                itemSelectedListener?.onSelectedItem(BottomMenu.TASK)
            }
            BottomMenu.CHAT -> {
                setSelectable(binding.llChat.id)


                itemSelectedListener?.onSelectedItem(BottomMenu.CHAT)
            }
            BottomMenu.DAShBOARD -> {

                setSelectable(binding.llMore.id)

                itemSelectedListener?.onSelectedItem(BottomMenu.DAShBOARD)
            }
            BottomMenu.MEETING -> {
                setSelectable(binding.llMeeting.id)
                itemSelectedListener?.onSelectedItem(BottomMenu.MEETING)
            }
            BottomMenu.WORKER -> {
                setSelectable(binding.llWork.id)
                itemSelectedListener?.onSelectedItem(BottomMenu.WORKER)
            }
        }
    }

    private fun setSelectable(viewId: Int) {
        when (viewId) {
            R.id.ll_chat -> {
                setScheduleSelected(true)
                setCIPSelected(false)
                setDirectorySelected(false)
                setAccountSelected(false)
                setMoreSelected(false)
                setWorkerSelected(false)
            }
            R.id.ll_calendar -> {
                setScheduleSelected(false)
                setCIPSelected(true)
                setDirectorySelected(false)
                setAccountSelected(false)
                setMoreSelected(false)
                setWorkerSelected(false)
            }
            R.id.ll_task -> {
                setScheduleSelected(false)
                setCIPSelected(false)
                setDirectorySelected(true)
                setAccountSelected(false)
                setMoreSelected(false)
                setWorkerSelected(false)
            }
            R.id.ll_meeting -> {
                setScheduleSelected(false)
                setCIPSelected(false)
                setDirectorySelected(false)
                setAccountSelected(true)
                setMoreSelected(false)
                setWorkerSelected(false)
            }
            R.id.llWork -> {
                setScheduleSelected(false)
                setCIPSelected(false)
                setDirectorySelected(false)
                setAccountSelected(false)
                setMoreSelected(false)
                setWorkerSelected(true)
            }
            R.id.llMore -> {
                setScheduleSelected(false)
                setCIPSelected(false)
                setDirectorySelected(false)
                setAccountSelected(false)
                setMoreSelected(true)
                setWorkerSelected(false)
            }
        }
    }

    private fun setScheduleSelected(isSelected: Boolean) {
        with(binding) {
            llChat.isSelected = isSelected
            ivSchedule.isSelected = isSelected
        }
    }

    private fun setCIPSelected(isSelected: Boolean) {
        with(binding) {

            llCalendar.isSelected = isSelected
            ivCip.isSelected = isSelected
        }
    }

    private fun setDirectorySelected(isSelected: Boolean) {
        with(binding) {
            llTask.isSelected = isSelected
            ivDirectory.isSelected = isSelected
        }
    }

    private fun setAccountSelected(isSelected: Boolean) {
        with(binding) {
            llMeeting.isSelected = isSelected
            ivAccount.isSelected = isSelected
        }
    }

    private fun setWorkerSelected(isSelected: Boolean) {
        with(binding) {
            llWork.isSelected = isSelected
            ivWork.isSelected = isSelected
        }
    }

    private fun setMoreSelected(isSelected: Boolean) {
        with(binding) {
            llMore.isSelected = isSelected
            ivMore.isSelected = isSelected
        }
    }

    private fun scaleAnim(view: View) {
        view.scaleY = 0.9f
        view.scaleX = 0.9f
        view.animate().setDuration(200)
            .scaleX(1f)
            .scaleY(1f)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

}

interface OnItemMenuListener {
    fun onSelectedItem(itemMenu: BottomMenu)
}

enum class BottomMenu(val resId:Int) {
    CALENDAR(R.id.navigation_calendar), TASK(R.id.navigation_task), CHAT(R.id.navigation_chat),
    DAShBOARD(R.id.navigation_others), MEETING(R.id.navigation_meeting), WORKER(R.id.navigation_workers);
}
