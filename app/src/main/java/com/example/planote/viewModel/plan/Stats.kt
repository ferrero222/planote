package com.example.planote.viewModel.plan

//private fun deleteOldData(yearsToKeep: Int = 2) {
//    viewModelScope.launch {
//        _uiState.update { it.copy(isLoading = true) }
//        try {
//            val cutoffDate = currentDate.minusYears(yearsToKeep.toLong())
//            daysRepository.deleteDaysBeforeThan(cutoffDate)
//            monthsRepository.deleteMonthsBeforeThan(cutoffDate)
//            yearsRepository.deleteYearsBeforeThan(cutoffDate)
//            _uiState.update { it.copy(isLoading = false) }
//        } catch (e: Exception) {
//            _uiState.update { it.copy(error = "Error while cleanup old data ${e.message}") }
//        }
//    }
//}