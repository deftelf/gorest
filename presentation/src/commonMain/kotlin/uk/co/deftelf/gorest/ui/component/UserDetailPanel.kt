package uk.co.deftelf.gorest.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import gorest.presentation.generated.resources.Res
import gorest.presentation.generated.resources.birthday_label
import gorest.presentation.generated.resources.birthday_with_age
import gorest.presentation.generated.resources.gender_label
import gorest.presentation.generated.resources.id_label
import gorest.presentation.generated.resources.select_user_prompt
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import uk.co.deftelf.gorest.domain.model.User
import uk.co.deftelf.gorest.ui.util.ageInYears

private fun Instant.formattedDate(): String {
    val date = toLocalDateTime(TimeZone.currentSystemDefault()).date
    val month = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "${date.dayOfMonth} $month ${date.year}"
}

@Composable
fun UserDetailPanel(
    user: User?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = if (user == null) Alignment.Center else Alignment.TopStart,
    ) {
        if (user == null) {
            Text(
                text = stringResource(Res.string.select_user_prompt),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        } else {
            Column {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
                Spacer(modifier = Modifier.height(16.dp))
                DetailRow(stringResource(Res.string.gender_label), user.gender.name)
                user.birthday?.let { birthday ->
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        stringResource(Res.string.birthday_label),
                        stringResource(Res.string.birthday_with_age, birthday.formattedDate(), birthday.ageInYears()),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(stringResource(Res.string.id_label), user.id.toString())
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
