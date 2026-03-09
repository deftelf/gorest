package uk.co.deftelf.gorest.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.presentation.adduser.AddUserEffect
import uk.co.deftelf.gorest.presentation.adduser.AddUserIntent
import uk.co.deftelf.gorest.presentation.adduser.AddUserViewModel

private fun Long.toLocalDate(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date

private fun LocalDate.formatted(): String {
    val month = month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$dayOfMonth $month $year"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddUserViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AddUserEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.birthday?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
    )

    if (state.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { viewModel.processIntent(AddUserIntent.HideDatePicker) },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.toLocalDate()?.let { date ->
                        viewModel.processIntent(AddUserIntent.UpdateBirthday(date))
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.processIntent(AddUserIntent.HideDatePicker) }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add User") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.processIntent(AddUserIntent.UpdateName(it)) },
                label = { Text("Name") },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.processIntent(AddUserIntent.UpdateEmail(it)) },
                label = { Text("Email") },
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box {
                OutlinedTextField(
                    value = state.birthday?.formatted() ?: "",
                    onValueChange = {},
                    label = { Text("Birthday") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Select date")
                    },
                    isError = state.birthdayError != null,
                    supportingText = state.birthdayError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(modifier = Modifier
                    .matchParentSize()
                    .clickable { viewModel.processIntent(AddUserIntent.ShowDatePicker) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Gender")
            Row {
                Gender.entries.forEach { gender ->
                    FilterChip(
                        selected = state.gender == gender,
                        onClick = { viewModel.processIntent(AddUserIntent.UpdateGender(gender)) },
                        label = { Text(gender.name) },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.processIntent(AddUserIntent.Submit) },
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Add User")
                }
            }
        }

        if (state.generalError != null) {
            val onDismiss = { viewModel.clearGeneralError() }
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Add User") },
                text = { Text(state.generalError ?: "") },
                confirmButton = {
                    TextButton(onClick = onDismiss) { Text("OK") }
                },
            )
        }
    }
}
