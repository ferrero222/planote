/*****************************************************************
 *  Package for MVVM plan data module
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.model.plan.repository.source.local.room.dao

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDay
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayTask
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonth
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthTask
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYear
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearTask
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * DAO base interface
 ****************************************************************/
interface PlanCalendarEntityBaseDao<Entity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertEntity(entity: Entity): Long
    @Update suspend fun updateEntity(entity: Entity)
    @Delete suspend fun deleteEntity(entity: Entity)
}

interface PlanCalendarTaskBaseDao<Task> {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertEntityTask(task: Task): Long
    @Update suspend fun updateEntityTask(task: Task)
    @Delete suspend fun deleteEntityTask(task: Task)
}

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * DAO class for DB.
 ****************************************************************/
@Dao
abstract class PlanCalendarDaysDao : PlanCalendarEntityBaseDao<PlanCalendarDay>,
    PlanCalendarTaskBaseDao<PlanCalendarDayTask> {
    @Query("DELETE FROM planCalendarDay WHERE date < :cutoffDate") abstract suspend fun deleteDaysBeforeThan(cutoffDate: LocalDate)
    @Query("SELECT * FROM planCalendarDay WHERE date < :cutoffDate ORDER BY date ASC") abstract fun getDaysBeforeThan(cutoffDate: LocalDate): Flow<List<PlanCalendarDay>>
    @Query("SELECT * FROM planCalendarDay WHERE date >= :cutoffDate ORDER BY date ASC") abstract fun getDaysAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarDay>>
    @Query("SELECT * FROM planCalendarDayTask WHERE ownerId = :dayId") abstract fun getTasksForDay(dayId: Long): Flow<List<PlanCalendarDayTask>>
}

@Dao
abstract class PlanCalendarMonthsDao : PlanCalendarEntityBaseDao<PlanCalendarMonth>,
    PlanCalendarTaskBaseDao<PlanCalendarMonthTask> {
    @Query("DELETE FROM planCalendarMonth WHERE date < :cutoffDate")  abstract suspend fun deleteMonthsBeforeThan(cutoffDate: LocalDate)
    @Query("SELECT * FROM planCalendarMonth WHERE date < :cutoffDate ORDER BY date ASC") abstract fun getMonthsBeforeThan(cutoffDate: LocalDate): Flow<List<PlanCalendarMonth>>
    @Query("SELECT * FROM planCalendarMonth WHERE date >= :cutoffDate ORDER BY date ASC") abstract fun getMonthsAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarMonth>>
    @Query("SELECT * FROM planCalendarMonthTask WHERE ownerId = :monthId") abstract fun getTasksForMonth(monthId: Long): Flow<List<PlanCalendarMonthTask>>
}

@Dao
abstract class PlanCalendarYearsDao : PlanCalendarEntityBaseDao<PlanCalendarYear>,
    PlanCalendarTaskBaseDao<PlanCalendarYearTask> {
    @Query("DELETE FROM planCalendarYear WHERE date < :cutoffDate")  abstract suspend fun deleteYearsBeforeThan(cutoffDate: LocalDate)
    @Query("SELECT * FROM planCalendarYear WHERE date < :cutoffDate ORDER BY date ASC") abstract fun getYearsBeforeThan(cutoffDate: LocalDate): Flow<List<PlanCalendarYear>>
    @Query("SELECT * FROM planCalendarYear WHERE date >= :cutoffDate ORDER BY date ASC") abstract fun getYearsAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarYear>>
    @Query("SELECT * FROM planCalendarYearTask WHERE ownerId = :yearId") abstract fun getTasksForYear(yearId: Long): Flow<List<PlanCalendarYearTask>>
}
