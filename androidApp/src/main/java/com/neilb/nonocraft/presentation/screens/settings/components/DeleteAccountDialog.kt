package com.neilb.nonocraft.presentation.screens.settings.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.neilb.nonocraft.presentation.theme.DangerRed
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary

@Composable
fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Hesabı Sil", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Text("Bu işlem geri alınamaz. Tüm verilerin, bulmacaların ve ilerlemen kalıcı olarak silinecek. Emin misin?",
                color = TextMuted)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Evet, Sil", color = DangerRed, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = TextPrimary)
            }
        }
    )
}