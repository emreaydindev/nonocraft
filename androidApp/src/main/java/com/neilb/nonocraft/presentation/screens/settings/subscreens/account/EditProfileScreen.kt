package com.neilb.nonocraft.presentation.screens.settings.subscreens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.components.rippleClickable
import com.neilb.nonocraft.presentation.theme.*

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            username = it.username
            email = it.email
            bio = it.bio ?: ""
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is EditProfileUiEvent.Success) {
                onNavigateBack()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark).navigationBarsPadding()) {
        GridBackground()

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = TextPrimary)
                }
                Text("Profili Düzenle", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = AppFont)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                CustomTextField(value = username, onValueChange = { username = it }, label = "Kullanıcı Adı")
                // Email typically shouldn't be edited here if we have a separate change email flow, 
                // but let's keep it for now as a read-only or editable if backend supports it.
                CustomTextField(value = email, onValueChange = { email = it }, label = "E-posta Adresi", enabled = false)
                CustomTextField(value = bio, onValueChange = { bio = it }, label = "Hakkımda", singleLine = false, modifier = Modifier.height(100.dp))

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(NeonCyan)
                        .rippleClickable { viewModel.updateProfile(username, bio) }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Değişiklikleri Kaydet", color = BackgroundDark, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = AppFont)
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = AppFont, color = TextMuted) },
        singleLine = singleLine,
        enabled = enabled,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = BorderDark,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark,
            disabledBorderColor = BorderDark,
            disabledTextColor = TextMuted,
            disabledLabelColor = TextMuted
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}