/*****************************************************************
 *  Package for MVVM plan data module
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.model.plan.repository.source.local.room.entity

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * Entity class for weekly plan module data in DB
 ****************************************************************/
@Entity(tableName = "planCalendarDay")
data class PlanCalendarDay(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val title: String? = null,
    val date: LocalDate, //
){
    @Ignore
    val isNew: Boolean = false
    fun isEmpty() : Boolean{
        return title.isNullOrBlank()
    }
}

@Entity(
    tableName = "planCalendarDayTask",
    foreignKeys = [
        ForeignKey(
            entity = PlanCalendarDay::class,
            parentColumns = ["bdId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class PlanCalendarDayTask(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val ownerId: Long,
    val title: String,
    val description: String? = null,
    val isDone : Boolean = false
){
    @Ignore
    val isNew: Boolean = false
}

/*****************************************************************
 * Entity class for weekly plan module data in DB
 ****************************************************************/
@Entity(tableName = "planCalendarMonth")
data class PlanCalendarMonth(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val title: String? = null,
    val date: LocalDate,
){
    @Ignore
    val isNew: Boolean = false
    fun isEmpty() : Boolean{
        return title.isNullOrBlank()
    }
}

@Entity(
    tableName = "planCalendarMonthTask",
    foreignKeys = [
        ForeignKey(
            entity = PlanCalendarMonth::class,
            parentColumns = ["bdId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class PlanCalendarMonthTask(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val ownerId: Long,
    val title: String,
    val description: String? = null,
    val isDone : Boolean = false
){
    @Ignore
    val isNew: Boolean = false
}

/*****************************************************************
 * Entity class for weekly plan module data in DB
 ****************************************************************/
@Entity(tableName = "planCalendarYear")
data class PlanCalendarYear(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val title: String? = null,
    val date: LocalDate,
){
    @Ignore
    val isNew: Boolean = false
    fun isEmpty() : Boolean{
        return title.isNullOrBlank()
    }
}

@Entity(
    tableName = "planCalendarYearTask",
    foreignKeys = [
        ForeignKey(
            entity = PlanCalendarYear::class,
            parentColumns = ["bdId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class PlanCalendarYearTask(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val ownerId: Long,
    val title: String,
    val description: String? = null,
    val isDone : Boolean = false
){
    @Ignore
    val isNew: Boolean = false
}