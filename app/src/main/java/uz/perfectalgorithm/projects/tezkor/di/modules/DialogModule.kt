package uz.perfectalgorithm.projects.tezkor.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.ImportanceDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.SectionsDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.adding.StatusDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.calendar.CalendarSectionsDialog
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.chat.QuickSheetDialogMaker
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.entry.reset_password.SuccessDialogMaker
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.home_activity.quick_ideas.QuickItemMoreSheetDialogMaker
import uz.perfectalgorithm.projects.tezkor.presentation.ui.dialogs.others.create_goal_map.CreateMapDialog
import javax.inject.Singleton

/**
 * Created by Jasurbek Kurganbaev on 21.06.2021 10:28
 **/

@Module
@InstallIn(SingletonComponent::class)
class DialogModule {

    @Provides
    @Singleton
    fun getQuickSheetDialogMaker(): QuickSheetDialogMaker = QuickSheetDialogMaker

    @Provides
    @Singleton
    fun getSelectionDialogMaker(): SectionsDialog = SectionsDialog

    @Provides
    @Singleton
    fun getGoalMapDialogMaker(): CreateMapDialog = CreateMapDialog

    @Provides
    @Singleton
    fun getStatusDialogMaker(): StatusDialog = StatusDialog

    @Provides
    @Singleton
    fun getImportanceDialogMaker(): ImportanceDialog = ImportanceDialog

    @Provides
    @Singleton
    fun getCalendarDialogMaker(): CalendarSectionsDialog = CalendarSectionsDialog

    @Provides
    @Singleton
    fun getItemMoreDialogMaker(): QuickItemMoreSheetDialogMaker = QuickItemMoreSheetDialogMaker

    @Provides
    @Singleton
    fun getSuccessDialogMaker(): SuccessDialogMaker = SuccessDialogMaker
}