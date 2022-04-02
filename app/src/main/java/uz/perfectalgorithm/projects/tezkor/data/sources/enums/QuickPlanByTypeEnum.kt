package uz.perfectalgorithm.projects.tezkor.data.sources.enums

/**
 *Created by farrukh_kh on 10/8/21 2:32 AM
 *uz.perfectalgorithm.projects.tezkor.data.sources.enums
 **/
enum class QuickPlanByTypeEnum(val isDone: Boolean?) {
    ALL(null),
    DONE(true),
    UNDONE(false)
}