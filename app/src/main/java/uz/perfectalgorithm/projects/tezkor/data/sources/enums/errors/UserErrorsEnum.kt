package uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors

/**
 *Created by farrukh_kh on 9/11/21 8:03 AM
 *uz.perfectalgorithm.projects.tezkor.data.sources.enums
 **/
enum class UserErrorsEnum(val message: String) {
    USER_ID_REQUIRED("This field is required."),
    INCORRECT_USER("incorrect_user"),
    INCORRECT_COMPANY("incorrect_company"),
    PHONE_NUMBER_EXISTS("user with this phone number already exists."),
    INVALID_EMAIL("Enter a valid email address.")
}