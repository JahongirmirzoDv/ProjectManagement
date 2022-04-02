package uz.perfectalgorithm.projects.tezkor.utils.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.TintTypedArray
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.internal.ThemeEnforcement
import com.google.android.material.navigation.NavigationBarMenuView
import com.google.android.material.navigation.NavigationBarView
import uz.perfectalgorithm.projects.tezkor.R

class MyBottomNavigationView : NavigationBarView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, R.attr.bottomNavigationStyle)

    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int
    ): super(context, attrs, defStyleAttr, R.style.Widget_Design_BottomNavigationView)


    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ): super(context, attrs, defStyleAttr, defStyleRes)


    override fun getMaxItemCount(): Int = 6

    @SuppressLint("RestrictedApi")
    override fun createNavigationBarMenuView(context: Context): NavigationBarMenuView {
        return BottomNavigationMenuView(context)
    }
}