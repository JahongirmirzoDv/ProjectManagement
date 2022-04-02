package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others.error_handling

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentDialogHandledErrorBinding
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.HandledError

/**
 *Created by farrukh_kh on 9/8/21 11:34 AM
 *uz.rdo.projects.projectmanagement.presentation.ui.dialogs.others.error_handling
 **/
@AndroidEntryPoint
class HandledErrorDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogHandledErrorBinding? = null
    private val binding
        get() = _binding ?: throw NullPointerException(getString(R.string.null_binding))
    private val error by lazy { arguments?.getParcelable<HandledError>("error") }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogHandledErrorBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() = with(binding) {
        tvBack.setOnClickListener {
            findNavController().navigateUp()
        }
        btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        btnNegative.setOnClickListener {
            findNavController().navigateUp()
        }
        when (error) {
            is HandledError.AuthError -> setupUi(
                "Ro'yxatdan o'tish ma'lumotlarida xatolik",
                "Iltimos, qayta login yoki registratsiya qilib ko'ring"
            )
            is HandledError.BadRequestError -> setupUi(
                "Serverga so'rov yuborishda xatolik",
                "Iltimos, kiritilgan ma'lumotlar to'g'riligini tekshirib, qayta urinib ko'ring"
            )
            is HandledError.ConnectionError -> setupUi(
                "Internet aloqasi mavjud emas",
                "Qurilmangizda internet aloqasini tekshirib, qayta urinib ko'ring"
            )
            is HandledError.NotFoundError -> setupUi(
                "Siz izlayotgan ma'lumot mavjud emas",
                "Iltimos boshqa qismga kirib ko'ring",
                R.drawable.ic_error_not_found
            )
            is HandledError.ParsedError -> Unit
            is HandledError.PaymentError -> setupUi(
                "To'lov qilishda xatolik",
                "To'lov ma'lumotlarida xatolik ro'y berdi"
            )
            is HandledError.PermissionError -> setupUi(
                "Sizda ruxsat yo'q",
                "Bu harakatni bajarish uchun sizda ruxsat yo'q. Administrator yoki rahbaringiz bilan bog'laning"
            )
            is HandledError.ServerError -> setupUi(
                "Serverda xatolik",
                "Iltimos, administrator bilan bog'laning yoki birozdan so'ng urinib ko'ring"
            )
            is HandledError.UnknownError -> setupUi(
                "Noma'lum xatolik",
                "Iltimos, administrator bilan bog'laning yoki birozdan so'ng urinib ko'ring"
            )
            is HandledError.WrongCredentialsError -> setupUi(
                "Noto'g'ri parol yoki login",
                "Siz kiritgan parol yoki login noto'g'ri kiritildi. Iltimos qayta tekshirib yana kiriting."
            )
            is HandledError.ActivateCompanyError -> {
                setBtnVisibility(true)
                btnPositive.text = "Aktivlashtirish"
                btnPositive.setOnClickListener {
                    try {
                        findNavController().navigate(HandledErrorDialogFragmentDirections.toPaymentStaffConfigurationFragment())
                    } catch (e: Exception) {
                    }
                }
                setupUi(
                    "Iltimos kompaniyani aktivlashtiring",
                    "Siz tanlagan kompaniya uchun to’lov muddati keldi. Iltimos to'lovni amalga oshiring."
                )
            }
            is HandledError.CustomError -> setupUi(
                (error as HandledError.CustomError).titleMessage,
                (error as HandledError.CustomError).message,
                (error as HandledError.CustomError).iconId
            )
            is HandledError.StaffLimitError -> {
                btnPositive.setOnClickListener {
                    try {
                        findNavController().navigate(HandledErrorDialogFragmentDirections.toPaymentStaffConfigurationFragment())
                    } catch (e: Exception) {
                    }
                }
                setupUi(
                    "Xodimlar limiti tugadi",
                    "Sizning paket uchun xodimlar limiti tugadi. Yangi paket sotib oling"
                )
            }
            null -> Unit
        }
    }

    private fun setupUi(title: String, description: String, @DrawableRes imageId: Int? = null) =
        with(binding) {
            tvTitle.text = title
            tvDescription.text = description
            ivErrorIcon.setImageResource(imageId ?: R.drawable.ic_error_warning)
        }

    private fun setBtnVisibility(isVisible: Boolean) = with(binding) {
        btnNegative.isVisible = isVisible
        btnPositive.isVisible = isVisible
        tvBack.isVisible = !isVisible
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}