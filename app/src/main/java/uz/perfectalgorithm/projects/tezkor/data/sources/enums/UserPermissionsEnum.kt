package uz.perfectalgorithm.projects.tezkor.data.sources.enums


/**
 * Created by Davronbek Daximjanov on 24.08.2021
 **/

enum class UserPermissionsEnum(val text: String) {
    ADD_USER("can_add_user"),
    EDIT_USER("can_edit_user"),
    DELETE_USER("can_delete_user"),

    ADD_DEPARTMENT("can_add_department"),
    EDIT_DEPARTMENT("can_edit_department"),
    DELETE_DEPARTMENT("can_delete_department"),

    ADD_POSITION("can_add_position"),
    EDIT_POSITION("can_edit_position"),
    DELETE_POSITION("can_delete_position"),

    MANAGE_CALENDAR("can_manage_calendar_of_company")
}