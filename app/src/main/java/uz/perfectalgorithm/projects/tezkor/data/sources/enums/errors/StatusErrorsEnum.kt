package uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors


enum class StatusErrorsEnum(val message: String) {
    STATUS_EXISTS("status_title_exists"),
    STATUS_IS_NOT_EMPTY("status_is_not_empty"),
    STATUS_IS_DEFAULT("status_is_default")
}