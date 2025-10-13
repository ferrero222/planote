/*****************************************************************
 *  Repository source package
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.model.plan.repository

/*****************************************************************
 * Imported packages
 ****************************************************************/
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDay
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayTask
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonth
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthTask
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYear
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearTask
import com.example.planote.model.plan.repository.source.local.room.dao.PlanCalendarDaysDao
import com.example.planote.model.plan.repository.source.local.room.dao.PlanCalendarMonthsDao
import com.example.planote.model.plan.repository.source.local.room.dao.PlanCalendarYearsDao
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * DB repository's
 ****************************************************************/
class PlanCalendarDaysRepository(private val daysDao: PlanCalendarDaysDao) {
    suspend fun insertDay(day: PlanCalendarDay): Long = daysDao.insertEntity(day)
    suspend fun updateDay(day: PlanCalendarDay) = daysDao.updateEntity(day)
    suspend fun deleteDay(day: PlanCalendarDay) = daysDao.deleteEntity(day)
    suspend fun insertDayTask(task: PlanCalendarDayTask): Long = daysDao.insertEntityTask(task)
    suspend fun updateDayTask(task: PlanCalendarDayTask) = daysDao.updateEntityTask(task)
    suspend fun deleteDayTask(task: PlanCalendarDayTask) = daysDao.deleteEntityTask(task)
    suspend fun deleteDaysBeforeThan(cutoffDate: LocalDate) = daysDao.deleteDaysBeforeThan(cutoffDate)
    fun getDaysBeforeThan(cutoffDate: LocalDate): Flow<List<PlanCalendarDay>> = daysDao.getDaysBeforeThan(cutoffDate)
    fun getDaysAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarDay>> = daysDao.getDaysAfterThan(cutoffDate)
    fun getTasksForDay(dayId: Long): Flow<List<PlanCalendarDayTask>> = daysDao.getTasksForDay(dayId)
}

class PlanCalendarMonthsRepository(private val monthsDao: PlanCalendarMonthsDao) {
    suspend fun insertMonth(month: PlanCalendarMonth): Long = monthsDao.insertEntity(month)
    suspend fun updateMonth(month: PlanCalendarMonth) = monthsDao.updateEntity(month)
    suspend fun deleteMonth(month: PlanCalendarMonth) = monthsDao.deleteEntity(month)
    suspend fun insertMonthTask(task: PlanCalendarMonthTask): Long = monthsDao.insertEntityTask(task)
    suspend fun updateMonthTask(task: PlanCalendarMonthTask) = monthsDao.updateEntityTask(task)
    suspend fun deleteMonthTask(task: PlanCalendarMonthTask) = monthsDao.deleteEntityTask(task)
    suspend fun deleteMonthsBeforeThan(cutoffDate: LocalDate) = monthsDao.deleteMonthsBeforeThan(cutoffDate)
    fun getMonthsBeforeThan(cutoffDate: LocalDate): Flow<List<PlanCalendarMonth>> = monthsDao.getMonthsBeforeThan(cutoffDate)
    fun getMonthsAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarMonth>> = monthsDao.getMonthsAfterThan(cutoffDate)
    fun getTasksForMonth(monthId: Long): Flow<List<PlanCalendarMonthTask>> = monthsDao.getTasksForMonth(monthId)
}

class PlanCalendarYearsRepository(private val yearsDao: PlanCalendarYearsDao) {
    suspend fun insertYear(year: PlanCalendarYear): Long = yearsDao.insertEntity(year)
    suspend fun updateYear(year: PlanCalendarYear) = yearsDao.updateEntity(year)
    suspend fun deleteYear(year: PlanCalendarYear) = yearsDao.deleteEntity(year)
    suspend fun insertYearTask(task: PlanCalendarYearTask): Long = yearsDao.insertEntityTask(task)
    suspend fun updateYearTask(task: PlanCalendarYearTask) = yearsDao.updateEntityTask(task)
    suspend fun deleteYearTask(task: PlanCalendarYearTask) = yearsDao.deleteEntityTask(task)
    suspend fun deleteYearsBeforeThan(cutoffDate: LocalDate) = yearsDao.deleteYearsBeforeThan(cutoffDate)
    fun getYearsBeforeThan(cutoffDate: LocalDate): Flow<List<PlanCalendarYear>> = yearsDao.getYearsBeforeThan(cutoffDate)
    fun getYearsAfterThan(cutoffDate: LocalDate): Flow<List<PlanCalendarYear>> = yearsDao.getYearsAfterThan(cutoffDate)
    fun getTasksForYear(yearId: Long): Flow<List<PlanCalendarYearTask>> = yearsDao.getTasksForYear(yearId)
}
