package uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.workers.others.offers

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.perfectalgorithm.projects.tezkor.databinding.OffersBottomsheetLayoutBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.EmptyBlock

object OffersDialog {
    fun showOffersSectionsBottomSheetDialog(
        context: Context,
        showOffers: EmptyBlock,
        showComplaints: EmptyBlock,
        showSatisfied: EmptyBlock,
        showNotSatisfied: EmptyBlock,

        ) {
        val dialog = BottomSheetDialog(context)
        val binding = OffersBottomsheetLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.apply {
            btnShowOffers.setOnClickListener { dialog.dismiss();showOffers() }
            btnShowComplaints.setOnClickListener { dialog.dismiss();showComplaints() }
            btnShowSatisfied.setOnClickListener { dialog.dismiss();showSatisfied() }
            btnShowNotSatisfied.setOnClickListener { dialog.dismiss();showNotSatisfied() }
        }
//        dialog.setCancelable(false)
        dialog.show()
    }

}