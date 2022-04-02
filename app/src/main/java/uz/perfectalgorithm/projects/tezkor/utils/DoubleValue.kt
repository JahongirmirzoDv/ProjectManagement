package uz.perfectalgorithm.projects.tezkor.utils

import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Created by Jasurbek Kurganbaev on 8/23/2021 5:59 PM
 **/
fun roundOffDecimal(number: Double): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(number).toDouble()
}