/*****************************************************************
 *  DAO package for room
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
import androidx.room.Query
import androidx.room.Update
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeek
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDay
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDayTask
import kotlinx.coroutines.flow.Flow

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * DAO interface for DB
 ****************************************************************/
@Dao
interface PlanWeekDao {
    @Insert suspend fun insertWeek(week: PlanWeek): Long
    @Update suspend fun updateWeek(week: PlanWeek)
    @Delete suspend fun deleteWeek(weekly: PlanWeek)
    @Query("SELECT * FROM planWeek") fun getWeeks(): Flow<List<PlanWeek>>
    @Query("SELECT * FROM planWeekDay WHERE ownerId = :weekId") fun getWeekDays(weekId : Long): Flow<List<PlanWeekDay>>
    @Query("SELECT * FROM planWeekDayTask WHERE ownerId = :dayId") fun getDayTasks(dayId : Long): Flow<List<PlanWeekDayTask>>
}