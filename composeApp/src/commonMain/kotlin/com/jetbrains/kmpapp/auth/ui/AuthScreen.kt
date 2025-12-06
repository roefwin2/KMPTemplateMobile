package com.jetbrains.kmpapp.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetbrains.kmpapp.auth.AuthUiState
import com.jetbrains.kmpapp.auth.AuthViewModel
import kmp_app_template.composeapp.generated.resources.Res
import kmp_app_template.composeapp.generated.resources.auth_subtitle
import kmp_app_template.composeapp.generated.resources.auth_title
import kmp_app_template.composeapp.generated.resources.confirm_password_label
import kmp_app_template.composeapp.generated.resources.create_account_action
import kmp_app_template.composeapp.generated.resources.email_label
import kmp_app_template.composeapp.generated.resources.login_action
import kmp_app_template.composeapp.generated.resources.login_mode_action
import kmp_app_template.composeapp.generated.resources.password_label
import kmp_app_template.composeapp.generated.resources.register_mode_action
import kmp_app_template.composeapp.generated.resources.success_message
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AuthContent(
        state = uiState,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onToggleMode = viewModel::toggleMode,
        onSubmit = viewModel::submit,
    )
}

@Composable
private fun AuthContent(
    state: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onToggleMode: () -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(Res.string.auth_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(Res.string.auth_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(Modifier.padding(16.dp)) {
                    AuthTextField(
                        label = stringResource(Res.string.email_label),
                        value = state.email,
                        onValueChange = onEmailChanged,
                        leadingIcon = { Icon(Icons.Filled.MailOutline, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                    )

                    Spacer(Modifier.height(12.dp))

                    AuthTextField(
                        label = stringResource(Res.string.password_label),
                        value = state.password,
                        onValueChange = onPasswordChanged,
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = if (state.isLoginMode) ImeAction.Done else ImeAction.Next),
                    )

                    if (!state.isLoginMode) {
                        Spacer(Modifier.height(12.dp))
                        AuthTextField(
                            label = stringResource(Res.string.confirm_password_label),
                            value = state.confirmPassword,
                            onValueChange = onConfirmPasswordChanged,
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    if (state.errorMessage != null) {
                        StatusRow(
                            text = state.errorMessage,
                            isError = true,
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    if (state.successMessage != null) {
                        StatusRow(
                            text = state.successMessage,
                            isError = false,
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    Button(
                        onClick = onSubmit,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            if (state.isLoginMode) stringResource(Res.string.login_action)
                            else stringResource(Res.string.create_account_action)
                        )
                    }

                    TextButton(onClick = onToggleMode, enabled = !state.isLoading) {
                        Text(
                            if (state.isLoginMode) stringResource(Res.string.register_mode_action)
                            else stringResource(Res.string.login_mode_action)
                        )
                    }
                }
            }
        }

        if (state.authenticatedUser != null) {
            Spacer(Modifier.height(32.dp))
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Text(stringResource(Res.string.success_message, state.authenticatedUser.email))
            }
        } else {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(),
    )
}

@Composable
private fun StatusRow(
    text: String,
    isError: Boolean,
) {
    val icon = if (isError) Icons.Filled.Error else Icons.Filled.CheckCircle
    val tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = tint)
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = tint,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
