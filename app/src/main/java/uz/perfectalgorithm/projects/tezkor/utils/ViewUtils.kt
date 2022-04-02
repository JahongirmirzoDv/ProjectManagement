package uz.perfectalgorithm.projects.tezkor.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.project.PersonData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.status.StatusData
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl
import java.util.*


fun inflateX(parent: ViewGroup, layout: Int): View {
    return LayoutInflater.from(parent.context)
        .inflate(layout, parent, false)
}

fun ViewGroup.inflate(@LayoutRes resId: Int): View =
    LayoutInflater.from(context).inflate(resId, this, false)

fun ViewGroup.addViews(@LayoutRes resId: Int): View {
    val view = inflate(resId)
    addView(view)
    return view
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

@SuppressLint("ServiceCast")
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}


fun ImageView.setTintColor(color: Int) =
    setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)

fun ImageView.setTint(color: Int) =
    setColorFilter(color, PorterDuff.Mode.SRC_IN)


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.inVisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}


fun EditText.textInputListener(f: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let { f.invoke(it.toString().trim()) }
        }

        override fun afterTextChanged(s: Editable?) {

        }

    })
}

fun AppCompatEditText.textInputListener(f: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let { f.invoke(it.toString().trim()) }
        }

        override fun afterTextChanged(s: Editable?) {

        }

    })
}

fun getPositionSpinnerStatusGroup(status: String) = when (status.lowercase(Locale.getDefault())) {
    "opened" -> 0
    "zapusk" -> 1
    else -> 2
}

fun Spinner.setPositionWithStatusGroup(status: String) {
    when (status.lowercase(Locale.getDefault())) {
        "opened" -> this.setSelection(0)
        "zapusk" -> this.setSelection(1)
        else -> this.setSelection(2)
    }
}

fun Fragment.showToast(string: String) {
    Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToastWithRes(@StringRes string: Int) {
    Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
}

fun Activity.changeStatusBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.statusBarColor = color
    }
}

fun Activity.changeNavigationBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.navigationBarColor = color
    }
}

fun Int.toDarkenColor(): Int {
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    hsv[2] *= 0.8f
    return Color.HSVToColor(hsv)
}

fun Spinner.onItemSelected(action: SingleBlock<Int>) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            action(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }
}

fun Spinner.setItems(items: List<String?>) {
    adapter = ArrayAdapter(
        context,
        android.R.layout.simple_spinner_item,
        items.filterNotNull()
    ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
}

fun <T> Spinner.setCustomItems(
    items: List<T?>,
    @LayoutRes itemViewId: Int = android.R.layout.simple_spinner_item,
    @LayoutRes dropDownItemViewId: Int = android.R.layout.simple_spinner_dropdown_item
) {
    adapter = ArrayAdapter(
        context,
        itemViewId,
        items
    ).apply { setDropDownViewResource(dropDownItemViewId) }
}

fun Pair<ImageView?, TextView?>.setImportance(
    importance: String,
    isDetailsScreen: Boolean = false
) {
    first?.setImageResource(
        when (importance) {
            "red" -> {
                second?.text = "Yuqori"
                if (isDetailsScreen) R.drawable.ic_file_text_red else R.drawable.ic_importance_flag
            }
            "yellow" -> {
                second?.text = "O'rta"
                if (isDetailsScreen) R.drawable.ic_file_text_yellow else R.drawable.ic_importance_flag_yellow
            }
            "green" -> {
                second?.text = "Past"
                if (isDetailsScreen) R.drawable.ic_file_text_green else R.drawable.ic_importance_flag_green
            }
            else -> {
                second?.text = ""
                0
            }
        }
    )
}

fun Pair<ImageView?, TextView?>.setStatus(status: StatusData) {
    first?.setImageResource(
        when (status.id) {
            1 -> R.drawable.ic_new
            2 -> R.drawable.ic_play_circle
            3 -> R.drawable.ic_check_circle
            else -> 0
        }
    )
    second?.text = status.title
}

fun Pair<ImageView?, TextView?>.setPerson(
    person: PersonData?
) {
//    if (person == null) {
//        first?.setImageResource(placeholder)
//    } else {
//        person.image?.let { first?.loadImageUrl(it) }
//    }
    when {
        person == null -> first?.setImageResource(R.drawable.ic_adding_person)
        person.image.isNullOrBlank() -> first?.setImageResource(R.drawable.ic_person)
        else -> first?.loadImageUrl(person.image, R.drawable.ic_person)
    }
    second?.text = person?.fullName ?: ""
}

fun CardView.setMatchParent() {
    val params = this.layoutParams
    params.width = LinearLayout.LayoutParams.MATCH_PARENT
}

fun CardView.setWrapContent() {
    val params = this.layoutParams
    params.width = LinearLayout.LayoutParams.WRAP_CONTENT
}







