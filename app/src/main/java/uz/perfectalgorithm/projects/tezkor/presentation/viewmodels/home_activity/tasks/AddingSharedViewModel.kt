package uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.tasks

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 *Created by farrukh_kh on 7/24/21 4:22 PM
 *uz.rdo.projects.projectmanagement.presentation.viewmodels.home_activity.tasks
 **/

/**
 * Projectda xodimlar uchun yaratilingan umumiy sharedViewModel
 *
 * **/
@HiltViewModel
class AddingSharedViewModel @Inject constructor() : ViewModel() {

    private val _isFunctionalGroupNeedsRefresh = MutableStateFlow(false)
    val isFunctionalGroupNeedsRefresh: StateFlow<Boolean> get() = _isFunctionalGroupNeedsRefresh

    private val _isFunctionalColumnNeedsRefresh = MutableStateFlow(false)
    val isFunctionalColumnNeedsRefresh: StateFlow<Boolean> get() = _isFunctionalColumnNeedsRefresh

    private val _isProjectColumnNeedsRefresh = MutableStateFlow(false)
    val isProjectColumnNeedsRefresh: StateFlow<Boolean> get() = _isProjectColumnNeedsRefresh

    private val _isProjectNeedsRefresh = MutableStateFlow(false)
    val isProjectNeedsRefresh: StateFlow<Boolean> get() = _isProjectNeedsRefresh

    private val _isTaskNeedsRefresh = MutableStateFlow(false)
    val isTaskNeedsRefresh: StateFlow<Boolean> get() = _isTaskNeedsRefresh

    private val _isOwnTaskNeedsRefresh = MutableStateFlow(false)
    val isOwnTaskNeedsRefresh: StateFlow<Boolean> get() = _isTaskNeedsRefresh

    private val _isGoalNeedsRefresh = MutableStateFlow(false)
    val isGoalNeedsRefresh: StateFlow<Boolean> get() = _isGoalNeedsRefresh

    private val _isMeetingNeedsRefresh = MutableStateFlow(false)
    val isMeetingNeedsRefresh: StateFlow<Boolean> get() = _isMeetingNeedsRefresh

    private val _isDatingNeedsRefresh = MutableStateFlow(false)
    val isDatingNeedsRefresh: StateFlow<Boolean> get() = _isDatingNeedsRefresh

    private val _isQuickPlanNeedsRefresh = MutableStateFlow(false)
    val isQuickPlanNeedsRefresh: StateFlow<Boolean> get() = _isQuickPlanNeedsRefresh

    private val _isTacticPlanNeedsRefresh = MutableStateFlow(false)
    val isTacticPlanNeedsRefresh: StateFlow<Boolean> get() = _isTacticPlanNeedsRefresh

    private val _isMemberStateNeedsRefresh = MutableStateFlow(false)
    val isMemberStateNeedsRefresh: StateFlow<Boolean> get() = _isMemberStateNeedsRefresh

    private val _isProjectDeleted = MutableStateFlow(false)
    val isProjectDeleted: StateFlow<Boolean> get() = _isProjectDeleted

    private val _isTaskDeleted = MutableStateFlow(false)
    val isTaskDeleted: StateFlow<Boolean> get() = _isTaskDeleted

    private val _isGoalDeleted = MutableStateFlow(false)
    val isGoalDeleted: StateFlow<Boolean> get() = _isGoalDeleted

    private val _isMeetingDeleted = MutableStateFlow(false)
    val isMeetingDeleted: StateFlow<Boolean> get() = _isMeetingDeleted

    private val _isDatingDeleted = MutableStateFlow(false)
    val isDatingDeleted: StateFlow<Boolean> get() = _isDatingDeleted

    private val _isQuickPlanDeleted = MutableStateFlow(false)
    val isQuickPlanDeleted: StateFlow<Boolean> get() = _isQuickPlanDeleted

    private val _isTacticPlanDeleted = MutableStateFlow(false)
    val isTacticPlanDeleted: StateFlow<Boolean> get() = _isTacticPlanDeleted

    fun setFunctionalGroupNeedsRefresh(isChanged: Boolean) {
        _isFunctionalGroupNeedsRefresh.value = isChanged
    }

    fun setFunctionalColumnNeedsRefresh(isChanged: Boolean) {
        _isFunctionalColumnNeedsRefresh.value = isChanged
    }

    fun setProjectColumnNeedsRefresh(isChanged: Boolean) {
        _isProjectColumnNeedsRefresh.value = isChanged
    }

    fun setProjectNeedsRefresh(isChanged: Boolean) {
        _isProjectNeedsRefresh.value = isChanged
    }

    fun setTaskNeedsRefresh(isChanged: Boolean) {
        _isTaskNeedsRefresh.value = isChanged
    }

    fun setOwnTaskNeedsRefresh(isChanged: Boolean) {
        _isTaskNeedsRefresh.value = isChanged
    }

    fun setGoalNeedsRefresh(isChanged: Boolean) {
        _isGoalNeedsRefresh.value = isChanged
    }

    fun setMeetingNeedsRefresh(isChanged: Boolean) {
        _isMeetingNeedsRefresh.value = isChanged
    }

    fun setDatingNeedsRefresh(isChanged: Boolean) {
        _isDatingNeedsRefresh.value = isChanged
    }

    fun setQuickPlanNeedsRefresh(isChanged: Boolean) {
        _isQuickPlanNeedsRefresh.value = isChanged
    }

    fun setTacticPlanNeedsRefresh(isChanged: Boolean) {
        _isTacticPlanNeedsRefresh.value = isChanged
    }

    fun setMemberStateNeedsRefresh(isChanged: Boolean) {
        _isMemberStateNeedsRefresh.value = isChanged
    }

    fun setProjectDeleted(isDeleted: Boolean) {
        _isProjectDeleted.value = isDeleted
        if (isDeleted) {
            _isProjectNeedsRefresh.value = true
        }
    }

    fun setTaskDeleted(isDeleted: Boolean) {
        _isTaskDeleted.value = isDeleted
        if (isDeleted) {
            _isTaskNeedsRefresh.value = true
        }
    }

    fun setGoalDeleted(isDeleted: Boolean) {
        _isGoalDeleted.value = isDeleted
        if (isDeleted) {
            _isGoalNeedsRefresh.value = true
        }
    }

    fun setMeetingDeleted(isDeleted: Boolean) {
        _isMeetingDeleted.value = isDeleted
        if (isDeleted) {
            _isMeetingNeedsRefresh.value = true
        }
    }

    fun setDatingDeleted(isDeleted: Boolean) {
        _isDatingDeleted.value = isDeleted
        if (isDeleted) {
            _isDatingNeedsRefresh.value = true
        }
    }

    fun setQuickPlanDeleted(isDeleted: Boolean) {
        _isQuickPlanDeleted.value = isDeleted
        if (isDeleted) {
            _isQuickPlanNeedsRefresh.value = true
        }
    }

    fun setTacticPlanDeleted(isDeleted: Boolean) {
        _isTacticPlanDeleted.value = isDeleted
        if (isDeleted) {
            _isTacticPlanNeedsRefresh.value = true
        }
    }
}