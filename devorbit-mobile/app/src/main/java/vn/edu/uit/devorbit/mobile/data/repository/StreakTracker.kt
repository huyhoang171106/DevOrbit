package vn.edu.uit.devorbit.mobile.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vn.edu.uit.devorbit.mobile.data.datastore.SettingsDataStore
import vn.edu.uit.devorbit.mobile.data.local.dao.DailyActivityDao
import vn.edu.uit.devorbit.mobile.data.local.entity.DailyActivityEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakTracker @Inject constructor(
    private val dailyActivityDao: DailyActivityDao,
    private val settingsDataStore: SettingsDataStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /** Emitted after streak count changes; DashboardViewModel collects this to update UI. */
    private val _streakUpdated = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val streakUpdated: SharedFlow<Int> = _streakUpdated

    fun incrementReposViewed(studentCode: String) {
        if (studentCode.isBlank()) return
        scope.launch {
            mutex.withLock {
                val today = LocalDate.now().format(dateFormat)
                val existing = dailyActivityDao.getActivity(studentCode, today)
                dailyActivityDao.upsertActivity(
                    DailyActivityEntity(
                        studentCode = studentCode,
                        date = today,
                        reposViewed = (existing?.reposViewed ?: 0) + 1,
                        tasksCompleted = existing?.tasksCompleted ?: 0,
                        tasksTotal = existing?.tasksTotal ?: 0
                    )
                )
            }
            checkAndEmitStreak(studentCode)
        }
    }

    private suspend fun checkAndEmitStreak(studentCode: String) {
        val today = LocalDate.now().format(dateFormat)
        val todayActivity = dailyActivityDao.getActivity(studentCode, today)
        val todayRepos = todayActivity?.reposViewed ?: 0
        if (todayRepos < 3) return

        val lastStreakDate = settingsDataStore.getLastStreakDate(studentCode)
        if (lastStreakDate == today) return

        val yesterday = LocalDate.now().minusDays(1).format(dateFormat)
        val yesterdayActivity = dailyActivityDao.getActivity(studentCode, yesterday)
        val qualifiesForYesterday = (yesterdayActivity?.reposViewed ?: 0) >= 3
        val currentStreak = settingsDataStore.getStreakCount(studentCode)

        val newStreak = if (qualifiesForYesterday && lastStreakDate == yesterday) {
            currentStreak + 1
        } else {
            1
        }
        settingsDataStore.setStreak(studentCode, newStreak, today)
        _streakUpdated.emit(newStreak)
    }
}
