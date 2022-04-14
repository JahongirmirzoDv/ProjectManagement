package uz.perfectalgorithm.projects.tezkor.utils.error_handling

import android.content.Intent
import android.os.Parcelable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.RelativeLayout.ALIGN_PARENT_TOP
import android.widget.RelativeLayout.CENTER_HORIZONTAL
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import retrofit2.HttpException
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.CompanyErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors.OtherErrorsEnum
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.entry_activity.EntryActivity
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import java.io.IOException
import java.net.HttpURLConnection


sealed class HandledError(val title: String) : Parcelable {

    @Parcelize
    class ConnectionError(
        private val message: String = App.instance.getString(R.string.internet_error)
    ) :
        HandledError(message)

    @Parcelize
    class AuthError(
        private val message: String = App.instance.getString(R.string.registration_error)
    ) :
        HandledError(message)

    @Parcelize
    class PaymentError(
        private val message: String = App.instance.getString(R.string.payment_error)
    ) :
        HandledError(message)

    @Parcelize
    class NotFoundError(
        private val message: String = App.instance.getString(R.string.not_found_error)
    ) :
        HandledError(message)

    @Parcelize
    class PermissionError(
        private val message: String = App.instance.getString(R.string.permission_error)
    ) :
        HandledError(message)

    @Parcelize
    class BadRequestError(
        private val message: String = App.instance.getString(R.string.server_error)
    ) :
        HandledError(message)

    @Parcelize
    class ServerError(
        private val message: String = App.instance.getString(R.string.server_error_two)
    ) :
        HandledError(message)

    @Parcelize
    class UnknownError(
        private val message: String = App.instance.getString(R.string.unknown_error)
    ) :
        HandledError(message)

    @Parcelize
    class WrongCredentialsError(private val message: String = App.instance.getString(R.string.phone_number_error)) :
        HandledError(message)

    @Parcelize
    class ActivateCompanyError(private val message: String = App.instance.getString(R.string.company_error)) :
        HandledError(message)

    @Parcelize
    class StaffLimitError(private val message: String = App.instance.getString(R.string.employe_limit_error)) :
        HandledError(message)

    @Parcelize
    class CustomError(
        val titleMessage: String,
        val message: String,
        @DrawableRes val iconId: Int = R.drawable.ic_error_warning
    ) :
        HandledError(titleMessage)

    @Parcelize
    class ParsedError(
        val error: ParsedErrorResponse,
        private val message: String = App.instance.getString(R.string.error_error)
    ) : HandledError(message)

}

fun Fragment.handleException(exception: Exception) {
    exception.printStackTrace()
    exception.toHandledError().let {
        when (it) {
            is HandledError.ConnectionError -> makeSnackBar(it.title)
            is HandledError.ServerError -> makeSnackBar(it.title)
            is HandledError.AuthError -> {
                val localStorage = LocalStorage.instance
                localStorage.pref.edit().clear().apply()
                localStorage.completeIntro = true
                startActivity(Intent(requireActivity(), EntryActivity::class.java))
                requireActivity().finish()
            }
            else -> {
                if (findNavController().currentDestination?.id != R.id.handledErrorDialogFragment) {
                    findNavController().navigate(
                        R.id.handledErrorDialogFragment,
                        bundleOf(
                            "error" to it
                        )
                    )
                }
            }
        }
    }
}

fun Fragment.handleCustomException(
    exception: Exception,
    onParsed: SingleBlock<ParsedErrorResponse>
) {
    exception.printStackTrace()
    exception.parseError().let {
        if (it is HandledError.ParsedError) {
            onParsed(it.error)
        } else {
            when (it) {
                is HandledError.ConnectionError -> makeSnackBar(it.title)
                is HandledError.ServerError -> makeSnackBar(it.title)
                is HandledError.AuthError -> {
                    val localStorage = LocalStorage.instance
                    localStorage.pref.edit().clear().apply()
                    localStorage.completeIntro = true
                    startActivity(Intent(requireActivity(), EntryActivity::class.java))
                    requireActivity().finish()
                }
                else -> findNavController().navigate(
                    R.id.handledErrorDialogFragment,
                    bundleOf(
                        "error" to it
                    )
                )
            }
        }
    }
}

fun Exception.toHandledError() = when (this) {
    is IOException -> HandledError.ConnectionError()
    is HttpException -> getHttpError()
    else -> HandledError.UnknownError()
}

fun HttpException.getHttpError() = when {
    isActivateCompanyError() -> HandledError.ActivateCompanyError()
    isPermissionError() -> HandledError.PermissionError()
    isStaffLimitError() -> HandledError.StaffLimitError()
    code() == HttpURLConnection.HTTP_BAD_REQUEST -> HandledError.BadRequestError()
    code() == HttpURLConnection.HTTP_UNAUTHORIZED -> HandledError.AuthError()
    code() == HttpURLConnection.HTTP_PAYMENT_REQUIRED -> HandledError.PaymentError()
    code() == HttpURLConnection.HTTP_FORBIDDEN -> HandledError.PermissionError()
    code() == HttpURLConnection.HTTP_NOT_FOUND -> HandledError.NotFoundError()
    code() == HttpURLConnection.HTTP_BAD_METHOD -> HandledError.BadRequestError()
    code() == HttpURLConnection.HTTP_INTERNAL_ERROR -> HandledError.ServerError()
    code() == HttpURLConnection.HTTP_BAD_GATEWAY -> HandledError.ServerError()
    else -> HandledError.UnknownError()
}

fun Exception.parseError() = when (this) {
    is HttpException -> {
        val gson = GsonBuilder().create()
        try {
            HandledError.ParsedError(
                gson.fromJson(
                    response()?.errorBody()?.charStream(),
                    object : TypeToken<ParsedErrorResponse>() {}.type
                )
            )
        } catch (e: Exception) {
            toHandledError()
        }
    }
    else -> toHandledError()
}

fun HttpException.isActivateCompanyError() = try {
    val gson = GsonBuilder().create()
    val error = HandledError.ParsedError(
        gson.fromJson(
            response()?.errorBody()?.charStream(),
            object : TypeToken<ParsedErrorResponse>() {}.type
        )
    )
    (error.error.detail == CompanyErrorsEnum.ACTIVATE_COMPANY.message)
} catch (e: Exception) {
    false
}

fun HttpException.isPermissionError() = try {
    val gson = GsonBuilder().create()
    val error = HandledError.ParsedError(
        gson.fromJson(
            response()?.errorBody()?.charStream(),
            object : TypeToken<ParsedErrorResponse>() {}.type
        )
    )
    (error.error.detail == OtherErrorsEnum.YOU_DON_T_HAVE_PERMISSION.message)
} catch (e: Exception) {
    false
}

fun HttpException.isStaffLimitError() = try {
    val gson = GsonBuilder().create()
    val error = HandledError.ParsedError(
        gson.fromJson(
            response()?.errorBody()?.charStream(),
            object : TypeToken<ParsedErrorResponse>() {}.type
        )
    )
    (error.error.errors?.map { it.error }?.contains(OtherErrorsEnum.STAFF_LIMIT.message) == true)
} catch (e: Exception) {
    false
}

fun Fragment.makeErrorSnack(message: String = App.instance.getString(R.string.internet_error)) {
    makeSnackBar(message, R.drawable.bg_error_snackbar)
}

fun Fragment.makeSuccessSnack(message: String = App.instance.getString(R.string.sucsess_sucsess)) {
    makeSnackBar(message, R.drawable.bg_success_snackbar)
}

fun Fragment.makeSnackBar(
    message: String = App.instance.getString(R.string.internet_error),
    @DrawableRes background: Int = R.drawable.bg_error_snackbar
) {
    // TODO: 9/19/21 make shadow_layer visible with anim
    var parent = when {
        requireActivity() is HomeActivity -> requireActivity().findViewById(R.id.container)
        requireActivity() is EntryActivity -> requireActivity().findViewById(R.id.appNavHost)
        else -> view
    }
    if (parent == null) {
        parent = view
    }
    if (this is DialogFragment && parent != view) {
        findNavController().navigateUp()
    }
    parent?.let {
        val snackBar = Snackbar.make(it, message, Snackbar.LENGTH_LONG)
        try {
            val view = snackBar.view
            view.background =
                ResourcesCompat.getDrawable(resources, background, null)
            view.layoutParams.let { params ->
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT
                when (params) {
                    is FrameLayout.LayoutParams -> {
                        view.layoutParams =
                            params.apply {
                                gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                            }
                    }
                    is CoordinatorLayout.LayoutParams -> {
                        view.layoutParams =
                            params.apply {
                                gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                            }
                    }
                    is ConstraintLayout.LayoutParams -> {
                        view.layoutParams =
                            params.apply {
//                                gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                            }
                    }
                    is RelativeLayout.LayoutParams -> {
                        view.layoutParams =
                            params.apply {
                                addRule(ALIGN_PARENT_TOP or CENTER_HORIZONTAL)
                            }
                    }
                    is LinearLayout.LayoutParams -> {
                        view.layoutParams =
                            params.apply {
                                gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                            }
                    }
                    else -> {
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                }
                val textView =
                    view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_error_snack_bar,
                    0,
                    0,
                    0
                )
                textView.compoundDrawablePadding =
                    resources.getDimensionPixelOffset(R.dimen.snackbar_icon_padding)
            }
        } catch (e: Exception) {
        } finally {
            snackBar.show()
        }
    }
}


@Parcelize
data class ParsedErrorResponse(
    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("error")
    val errors: List<ErrorItem>? = null,
    @SerializedName("detail")
    val detail: String? = null,
) : Parcelable

@Parcelize
data class ErrorItem(
    @SerializedName("key")
    val key: String? = null,
    @SerializedName("error")
    val error: String? = null,
) : Parcelable