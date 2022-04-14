package uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors


enum class CompanyErrorsEnum(val message: String) {
    ACTIVATE_COMPANY("activate_company"),
    WRONG_COMPANY("Incorrect Company!"),
    USERNAME_EXISTS("Company with this username already exists."),
    COMPANY_NAME_EXIST("Company with this title already exists."),
    WRONG_EMAIL("Enter a valid email address.")
}