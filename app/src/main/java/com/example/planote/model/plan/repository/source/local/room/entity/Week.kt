/*****************************************************************
 *  Entity package for room
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.model.plan.repository.source.local.room.entity

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDate

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * Entity class for weekly plan module data in DB
 ****************************************************************/
@Entity(tableName = "planWeek")
data class PlanWeek(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val title: String? = null,
    val active: Boolean = false,
    )

@Entity(
    tableName = "planWeekDay",
    foreignKeys = [
        ForeignKey(
            entity = PlanWeek::class,
            parentColumns = ["bdId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class PlanWeekDay(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val ownerId: Long,
    val title: String,
    val description: String? = null,
)

@Entity(
    tableName = "planWeekDayTask",
    foreignKeys = [
        ForeignKey(
            entity = PlanWeekDay::class,
            parentColumns = ["bdId"],
            childColumns = ["ownerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])]
)
data class PlanWeekDayTask(
    @PrimaryKey(autoGenerate = true)
    val bdId: Long = 0,
    val ownerId: Long,
    val title: String,
    val description: String? = null,
    val timeStart: LocalDate,
    val timeEnd: LocalDate,
    val isDone : Boolean = false
)