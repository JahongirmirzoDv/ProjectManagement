package uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors


enum class PackageErrorsEnum(val message: String) {
    PACKAGE_NOT_FOUND("package not found"),
    PRICE_LIST_NOT_FOUND("object not found"),
    LOW_BALANCE("low balance"),
    STAFF_INCORRECT_COUNT("must be greater than zero"),
    COMPANY_PACKAGE_NOT_FOUND("not found"),
}