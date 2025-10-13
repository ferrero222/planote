/*****************************************************************
 *  Package for server view
 *  @author Ferrero
 *  @date 21.08.2025
 ****************************************************************/
package com.example.planote.view.plan

/*****************************************************************
 * Imported packages
 ****************************************************************/
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.planote.view.plan.component.CalendarBlock
import com.example.planote.view.plan.component.TodayBlock
import com.example.planote.view.plan.component.WeekBlock

/*****************************************************************
 * Top Level Functions
 ****************************************************************/
@Composable
fun PlannerPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Планировщик",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TodayBlock()
        WeekBlock()
        CalendarBlock()
    }
}