package uz.perfectalgorithm.projects.tezkor.data.sources.enums.errors

/**
 *Created by farrukh_kh on 9/8/21 5:07 PM
 *uz.rdo.projects.projectmanagement.data.sources.datasource.comment
 **/
enum class RegisterErrorsEnum(val message: String) {
    PHONE_NUMBER_EXISTS("phone_number_already_exists"),
    INVALID_PHONE_NUMBER("invalid_phone_number"),
    WAIT_3_MINUTE("wait_3_minute"),
    INCORRECT_CODE("incorrect_code"),
    INCORRECT_PHONE_NUMBER("incorrect_phone_number")
}