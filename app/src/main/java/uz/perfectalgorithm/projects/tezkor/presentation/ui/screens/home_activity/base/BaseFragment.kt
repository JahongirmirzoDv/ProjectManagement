package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others.offers.LoadingAskDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others.offers.LoadingDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.HomeActivity

/**
 * Asosiy BaseFragment bu fragment Vazifa, Proyekt, Maqsad kabi fragmentlar uchun
 * asosiy fragment bo'lib ishlagan. (Ularning detail va update fragmentlari uchun)
 *
 * **/
@AndroidEntryPoint
open class BaseFragment : Fragment() {

    var loadingAskDialog: LoadingAskDialog? = null
    var loadingDialog: LoadingDialog? = null

    fun showLoadingDialog() {
        loadingDialog = LoadingDialog()
        loadingDialog!!.show(
            (requireActivity() as HomeActivity).supportFragmentManager,
            "loading Dialog"
        )

//        loadingDialog!!.stopClickListener {
//            loadingDialog?.dismiss()
//            showLoadingAskDialog()
//        }
    }

    private fun showLoadingAskDialog() {
        loadingAskDialog = LoadingAskDialog(requireContext())
        loadingAskDialog!!.show()
        loadingAskDialog!!.stopClickListener {
            lifecycleScope.launchWhenResumed {
                findNavController().navigateUp()
            }
            loadingAskDialog!!.dismiss()
        }
    }


}