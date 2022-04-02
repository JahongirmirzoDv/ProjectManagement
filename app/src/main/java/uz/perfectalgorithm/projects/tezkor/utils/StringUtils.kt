package uz.perfectalgorithm.projects.tezkor.utils

import android.content.Context
import uz.perfectalgorithm.projects.tezkor.R

fun Int.getPermissionsTitles(context: Context): String =
    when (this) {
        1 -> {
            context.getString(R.string.can_add_user)
        }
        2 -> {
            context.getString(R.string.can_edit_user)
        }
        3 -> {
            context.getString(R.string.can_delete_user)
        }
        4 -> {
            context.getString(R.string.can_add_department)
        }
        5 -> {
            context.getString(R.string.can_edit_department)
        }
        6 -> {
            context.getString(R.string.can_delete_department)
        }
        7 -> {
            context.getString(R.string.add_position)
        }
        8 -> {
            context.getString(R.string.can_edit_position)
        }
        9 -> {
            context.getString(R.string.can_delete_position)
        }
        10 -> {
            context.getString(R.string.can_manage_calendar_of_company)
        }

        else -> ""
    }

