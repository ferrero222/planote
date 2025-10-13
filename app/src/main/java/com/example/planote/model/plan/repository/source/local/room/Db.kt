/*****************************************************************
 *  Room package
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.model.plan.repository.source.local.room

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeek
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDay
import com.example.planote.model.plan.repository.source.local.room.entity.PlanWeekDayTask
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDay
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayTask
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonth
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthTask
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYear
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearTask
import com.example.planote.model.plan.repository.source.local.room.dao.PlanCalendarDaysDao
import com.example.planote.model.plan.repository.source.local.room.dao.PlanCalendarMonthsDao
import com.example.planote.model.plan.repository.source.local.room.dao.PlanCalendarYearsDao
import com.example.planote.model.plan.repository.source.local.room.dao.PlanWeekDao
import java.time.LocalDate

/*****************************************************************
 * Classes
 ****************************************************************/
/****************************************************************
 * Type converters for DB
 ***************************************************************/
class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromListString(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun toListString(data: String?): List<String>? {
        return data?.split(",")?.map { it.trim() }
    }
}

/****************************************************************
 * Class for DB hold
 ***************************************************************/
@Database(
    entities = [PlanCalendarDay::class, PlanCalendarDayTask::class,
                PlanCalendarMonth::class, PlanCalendarMonthTask::class,
                PlanCalendarYear::class, PlanCalendarYearTask::class,
                PlanWeek::class, PlanWeekDay::class, PlanWeekDayTask::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PlanDataBase : RoomDatabase() {
    abstract fun daoPlanCalendarDays(): PlanCalendarDaysDao
    abstract fun daoPlanCalendarMonths(): PlanCalendarMonthsDao
    abstract fun daoPlanCalendarYears(): PlanCalendarYearsDao
    abstract fun daoPlanWeek(): PlanWeekDao
}