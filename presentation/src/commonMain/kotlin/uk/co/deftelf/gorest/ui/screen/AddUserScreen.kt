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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gorest.presentation.generated.resources.Res
import gorest.presentation.generated.resources.add_user_title
import gorest.presentation.generated.resources.back
import gorest.presentation.generated.resources.birthday_label
import gorest.presentation.generated.resources.cancel
import gorest.presentation.generated.resources.email_label
import gorest.presentation.generated.resources.gender_label
import gorest.presentation.generated.resources.name_label
import gorest.presentation.generated.resources.ok
import gorest.presentation.generated.resources.select_date_description
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import uk.co.deftelf.gorest.domain.model.Gender
import uk.co.deftelf.gorest.presentation.adduser.AddUserEffect
import uk.co.deftelf.gorest.presentation.adduser.AddUserUiEvent
import uk.co.deftelf.gorest.presentation.adduser.AddUserViewModel

private fun Long.toLocalDate(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date

private fun LocalDate.formatted(): String {
    val month = month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$day $month $year"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddUserViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
            onDismissRequest = { viewModel.processIntent(AddUserUiEvent.HideDatePicker) },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.toLocalDate()?.let { date ->
                        viewModel.processIntent(AddUserUiEvent.UpdateBirthday(date))
                    }
                }) { Text(stringResource(Res.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.processIntent(AddUserUiEvent.HideDatePicker) }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.add_user_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
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
                onValueChange = { viewModel.processIntent(AddUserUiEvent.UpdateName(it)) },
                label = { Text(stringResource(Res.string.name_label)) },
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.processIntent(AddUserUiEvent.UpdateEmail(it)) },
                label = { Text(stringResource(Res.string.email_label)) },
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box {
                OutlinedTextField(
                    value = state.birthday?.formatted() ?: "",
                    onValueChange = {},
                    label = { Text(stringResource(Res.string.birthday_label)) },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = stringResource(Res.string.select_date_description))
                    },
                    isError = state.birthdayError != null,
                    supportingText = state.birthdayError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(modifier = Modifier
                    .matchParentSize()
                    .clickable { viewModel.processIntent(AddUserUiEvent.ShowDatePicker) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(Res.string.gender_label))
            Row {
                Gender.entries.forEach { gender ->
                    FilterChip(
                        selected = state.gender == gender,
                        onClick = { viewModel.processIntent(AddUserUiEvent.UpdateGender(gender)) },
                        label = { Text(gender.name) },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.processIntent(AddUserUiEvent.Submit) },
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(Res.string.add_user_title))
                }
            }
        }

        if (state.generalError != null) {
            val onDismiss = { viewModel.clearGeneralError() }
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(stringResource(Res.string.add_user_title)) },
                text = { Text(state.generalError ?: "") },
                confirmButton = {
                    TextButton(onClick = onDismiss) { Text(stringResource(Res.string.ok)) }
                },
            )
        }
    }
}
