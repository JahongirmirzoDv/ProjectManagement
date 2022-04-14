package uz.perfectalgorithm.projects.tezkor.data.sources.enums

import android.content.Context
import android.content.res.Resources
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App


enum class QuickPlanByTimeEnum(val text: String) {
    BY_DAY("Kunlik"),
    BY_WEEK("Haftalik"),
    BY_MONTH("Oylik"),
    BY_YEAR("Yillik"),
    ALL("Barchasi")

}