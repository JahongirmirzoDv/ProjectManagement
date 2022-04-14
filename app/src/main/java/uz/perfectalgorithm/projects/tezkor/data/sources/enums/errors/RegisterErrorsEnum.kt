package uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors


enum class RegisterErrorsEnum(val message: String) {
    PHONE_NUMBER_EXISTS("phone_number_already_exists"),
    INVALID_PHONE_NUMBER("invalid_phone_number"),
    WAIT_3_MINUTE("wait_3_minute"),
    INCORRECT_CODE("incorrect_code"),
    INCORRECT_PHONE_NUMBER("incorrect_phone_number")
}