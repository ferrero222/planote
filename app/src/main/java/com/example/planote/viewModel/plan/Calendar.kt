/*****************************************************************
 *  Package for MVVM plan data repository
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.viewModel.plan

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planote.model.plan.repository.PlanCalendarDaysRepository
import com.example.planote.model.plan.repository.PlanCalendarMonthsRepository
import com.example.planote.model.plan.repository.PlanCalendarYearsRepository
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDay
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarDayTask
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonth
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarMonthTask
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYear
import com.example.planote.model.plan.repository.source.local.room.entity.PlanCalendarYearTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import java.time.LocalDate
import javax.inject.Inject

/*****************************************************************
 * Data
 ****************************************************************/
/*****************************************************************
 * Instance of calendar state
 ****************************************************************/
data class PlanCalendarDataHolder(
    val days: List<PlanCalendarDay> = emptyList(),
    val months: List<PlanCalendarMonth> = emptyList(),
    val years: List<PlanCalendarYear> = emptyList(),
    val selectedDayTasks: List<PlanCalendarDayTask> = emptyList(),
    val selectedMonthTasks: List<PlanCalendarMonthTask> = emptyList(),
    val selectedYearTasks: List<PlanCalendarYearTask> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/*****************************************************************
 * Interfaces
 ****************************************************************/
/*****************************************************************
 * Interface containing all public methods implementations of
 * viewModel class
 ****************************************************************/
interface PlanCalendarImplements {
    fun getCurrentDateImpl() : LocalDate
    fun updateDay(day: PlanCalendarDay)
    fun updateMonth(month: PlanCalendarMonth)
    fun updateYear(year: PlanCalendarYear)
    fun updateDayTask(day: PlanCalendarDay, task: PlanCalendarDayTask)
    fun updateMonthTask(month: PlanCalendarMonth, task: PlanCalendarMonthTask)
    fun updateYearTask(year: PlanCalendarYear, task: PlanCalendarYearTask)
    fun deleteDay(day: PlanCalendarDay)
    fun deleteMonth(month: PlanCalendarMonth)
    fun deleteYear(year: PlanCalendarYear)
    fun deleteDayTask(day: PlanCalendarDay, task: PlanCalendarDayTask)
    fun deleteMonthTask(month: PlanCalendarMonth, task: PlanCalendarMonthTask)
    fun deleteYearTask(year: PlanCalendarYear, task: PlanCalendarYearTask)
    fun getTasksById(bdId: Long)
}

/*****************************************************************
 * Classes
 ****************************************************************/
/*****************************************************************
 * ViewModel class for calendar
 ****************************************************************/
@HiltViewModel
class PlanCalendarViewModel @Inject constructor(
    private val daysRepository: PlanCalendarDaysRepository,
    private val monthsRepository: PlanCalendarMonthsRepository,
    private val yearsRepository: PlanCalendarYearsRepository,
) : ViewModel(), PlanCalendarImplements {
    /************************************************************
     * Private variables
     ************************************************************/
    /** val for calendar data state **/
    private val _dataState = MutableStateFlow(PlanCalendarDataHolder())
    /** val for bdId state for gettin tasks **/
    private val _taskState = MutableStateFlow<(Long?)>(null)
    /** val for getting current date **/
    private val currentDate: LocalDate
        get() = LocalDate.now()

    /************************************************************
     * Public variables
     ************************************************************/
    /** val for instance to _planCalendarDataState for read in @Composable **/
    val dataState: StateFlow<PlanCalendarDataHolder> = _dataState.asStateFlow()

    /*************************************************************
     * Constructors and init
     *************************************************************/
    init {
        dataStateHandler()
        taskStateHandler()
    }

    /*************************************************************
    * Private functions
    *************************************************************/
    /*************************************************************
     * Subscribe function to calendar data. Each time when
     * entity of data base will be changed this function will be
     * called and change the state of calendar data with new
     * changed data from database
     * @param None
     * @return None
     ************************************************************/
    private fun dataStateHandler() {
        viewModelScope.launch {
            combine(
                daysRepository.getDaysAfterThan(currentDate),
                monthsRepository.getMonthsAfterThan(currentDate),
                yearsRepository.getYearsAfterThan(currentDate)
            ) { days, months, years ->
                Triple(days, months, years)
            }.collect { (days, months, years) ->
                _dataState.update { state ->
                    state.copy(
                        days = days,
                        months = months,
                        years = years,
                        isLoading = false
                    )
                }
            }
        }
    }

    /*************************************************************
     * Function to get tasks for entities in reactive way. We
     * subscribe to bdId state @_planCalendarTaskState and if it
     * will be changed it will resubscribe to tasks entities and
     * get them for new bsId state in _planCalendarDataState state
     * @param None
     * @return None
     ************************************************************/
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    private fun taskStateHandler() {
         viewModelScope.launch {
             _taskState
                 .filterNotNull()
                 .flatMapLatest { bdId ->
                     combine(
                         daysRepository.getTasksForDay(bdId),
                         monthsRepository.getTasksForMonth(bdId),
                         yearsRepository.getTasksForYear(bdId)
                     ) { dayTasks, monthTasks, yearTasks ->
                         Triple(dayTasks, monthTasks, yearTasks)
                     }
                 }
                 .collect { (dayTasks, monthTasks, yearTasks) ->
                     _dataState.update { it.copy(
                         selectedDayTasks = dayTasks,
                         selectedMonthTasks = monthTasks,
                         selectedYearTasks = yearTasks,
                         isLoading = false
                     ) }
                 }
         }
    }

    /*************************************************************
     *
     * @param None
     * @return None
     ************************************************************/
    private fun clearError() {
        _dataState.update { it.copy(error = null) }
    }

    /*************************************************************
     * Public functions
     *************************************************************/
    /*************************************************************
     *
     * @param None
     * @return None
     ************************************************************/
    override fun getCurrentDateImpl(): LocalDate = currentDate

    /*************************************************************
     *
     * @param None
     * @return None
     ************************************************************/
    override fun updateDay(day: PlanCalendarDay) {
        viewModelScope.launch {
            try {
                if (day.isNew) {
                    daysRepository.insertDay(day)
                }
                else {
                    if(day.isEmpty()) deleteDay(day)
                    else daysRepository.updateDay(day)
                }
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Day adding error: ${e.message}") }
            }
        }
    }
    override fun updateMonth(month: PlanCalendarMonth) {
        viewModelScope.launch {
            try {
                if (month.isNew) {
                    monthsRepository.insertMonth(month)
                }
                else {
                    if(month.isEmpty()) deleteMonth(month)
                    else monthsRepository.updateMonth(month)
                }
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Month adding error: ${e.message}") }
            }
        }
    }
    override fun updateYear(year: PlanCalendarYear) {
        viewModelScope.launch {
            try {
                if (year.isNew) {
                    yearsRepository.insertYear(year)
                }
                else {
                    if(year.isEmpty()) deleteYear(year)
                    else yearsRepository.updateYear(year)
                }
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Year adding error: ${e.message}") }
            }
        }
    }

    /*************************************************************
     *
     * @param None
     * @return None
     ************************************************************/
    override fun updateDayTask(day: PlanCalendarDay, task: PlanCalendarDayTask) {
        viewModelScope.launch {
            try {
                if (day.isNew) daysRepository.insertDay(day)
                else           daysRepository.updateDay(day)
                if (task.isNew) daysRepository.insertDayTask(task)
                else            daysRepository.updateDayTask(task)
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Day adding task error: ${e.message}") }
            }
        }
    }
    override fun updateMonthTask(month: PlanCalendarMonth, task: PlanCalendarMonthTask) {
        viewModelScope.launch {
            try {
                if (month.isNew) monthsRepository.insertMonth(month)
                else             monthsRepository.updateMonth(month)
                if (task.isNew) monthsRepository.insertMonthTask(task)
                else            monthsRepository.updateMonthTask(task)
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Year adding task error: ${e.message}") }
            }
        }
    }
    override fun updateYearTask(year: PlanCalendarYear, task: PlanCalendarYearTask) {
        viewModelScope.launch {
            try {
                if (year.isNew) yearsRepository.insertYear(year)
                else            yearsRepository.updateYear(year)
                if (task.isNew) yearsRepository.insertYearTask(task)
                else            yearsRepository.updateYearTask(task)
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Year adding task error: ${e.message}") }
            }
        }
    }

    /*************************************************************
     *
     * @param None
     * @return None
     ************************************************************/
    override fun deleteDay(day: PlanCalendarDay) {
        viewModelScope.launch {
            try {
                if(!day.isNew) daysRepository.deleteDay(day)
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Day deleting error: ${e.message}") }
            }
        }
    }
    override fun deleteMonth(month: PlanCalendarMonth) {
        viewModelScope.launch {
            try {
                if(!month.isNew) monthsRepository.deleteMonth(month)
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Month deleting error: ${e.message}") }
            }
        }
    }
    override fun deleteYear(year: PlanCalendarYear) {
        viewModelScope.launch {
            try {
                if(!year.isNew) yearsRepository.deleteYear(year)
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Year deleting error: ${e.message}") }
            }
        }
    }

    /*************************************************************
     *
     * @param None
     * @return None
     ************************************************************/
    override fun deleteDayTask(day: PlanCalendarDay, task: PlanCalendarDayTask) {
        viewModelScope.launch {
            try {
                if(!day.isNew && !task.isNew) daysRepository.deleteDayTask(task)
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Day deleting task error: ${e.message}") }
            }
        }
    }
    override fun deleteMonthTask(month: PlanCalendarMonth, task: PlanCalendarMonthTask) {
        viewModelScope.launch {
            try {
                if(!month.isNew && !task.isNew) monthsRepository.deleteMonthTask(task)
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Month deleting task error: ${e.message}") }
            }
        }
    }
    override fun deleteYearTask(year: PlanCalendarYear, task: PlanCalendarYearTask) {
        viewModelScope.launch {
            try {
                if(!year.isNew && !task.isNew) yearsRepository.deleteYearTask(task)
            } catch (e: Exception) {
                _dataState.update { it.copy(error = "DB: Year deleting task error: ${e.message}") }
            }
        }
    }

    /*************************************************************
     *
     * @param None
     * @return None
     ************************************************************/
    override fun getTasksById(bdId: Long) {
        _taskState.value = bdId
    }
}
/*****************************************************************
 * End of class
 ****************************************************************/