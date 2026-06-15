package com.neilb.nonocraft.presentation.screens.auth.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.DangerRed
import com.neilb.nonocraft.presentation.theme.GameGold
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.SurfaceVariantDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary
import com.neilb.nonocraft.presentation.theme.TextSecondary
import com.neilb.nonocraft.R

@Composable
fun AuthGridBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val step = 72f
        val color = NeonCyan.copy(alpha = 0.09f)
        var x = 0f
        while (x <= size.width) {
            drawLine(color, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
            x += step
        }
        var y = 0f
        while (y <= size.height) {
            drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
            y += step
        }
    }
}

@Composable
fun AuthLogoRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(NeonCyan.copy(alpha = 0.10f))
                .border(1.dp, NeonCyan.copy(alpha = 0.25f), RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.ic_neon_cube_logo),
                    contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
        }

    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = TextPrimary)) { append("Nono") }
            withStyle(SpanStyle(color = NeonCyan))    { append("craft") }
        },
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = AppFont,
    )
}
}
@Composable
fun AuthBackRow(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .noRippleClickable(onBack)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(SurfaceDark)
                .border(0.5.dp, BorderDark, RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text("←", color = TextSecondary, fontSize = 16.sp)
        }
        Text("Geri", color = TextSecondary, fontSize = 14.sp, fontFamily = AppFont)
    }
}

@Composable
fun AuthIconBox(
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(NeonCyan.copy(alpha = 0.10f))
            .border(1.5.dp, NeonCyan.copy(alpha = 0.28f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}

@Composable
fun GoogleSignInButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(0.5.dp, BorderDark, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0xFF4285F4)),
            contentAlignment = Alignment.Center,
        ) {
            Text("G", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Text(label, color = TextPrimary, fontSize = 14.sp, fontFamily = AppFont)
    }
}

@Composable
fun AuthDivider(label: String = "veya") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(Modifier.weight(1f).height(0.5.dp).background(BorderDark))
        Text(label, color = TextMuted, fontSize = 11.sp, fontFamily = AppFont)
        Box(Modifier.weight(1f).height(0.5.dp).background(BorderDark))
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val borderColor = when {
        isError -> DangerRed.copy(alpha = 0.70f)
        value.isNotEmpty() -> NeonCyan.copy(alpha = 0.40f)
        else -> BorderDark
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.sp,
            letterSpacing = 0.3.sp,
            fontFamily = AppFont,
            modifier = Modifier.padding(bottom = 5.dp),
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
            cursorBrush = SolidColor(NeonCyan),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            singleLine = true,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(13.dp))
                        .background(SurfaceDark)
                        .border(0.5.dp, borderColor, RoundedCornerShape(13.dp))
                        .padding(horizontal = 14.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                placeholder,
                                color = TextMuted,
                                fontSize = 13.sp,
                                fontFamily = AppFont
                            )
                        }
                        innerTextField()
                    }

                    if (trailingIcon != null) {
                        Spacer(Modifier.width(8.dp))
                        trailingIcon()
                    }
                }
            },
        )

        if (isError && errorMessage != null) {
            Text(
                text = "⚠ $errorMessage",
                color = DangerRed,
                fontSize = 11.sp,
                fontFamily = AppFont,
                modifier = Modifier.padding(start = 4.dp, top = 5.dp),
            )
        }
    }
}

@Composable
fun AuthPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "••••••••",
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var visible by remember { mutableStateOf(false) }

    AuthTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        isError = isError,
        errorMessage = errorMessage,
        keyboardOptions = keyboardOptions.copy(
            keyboardType = KeyboardType.Password,
        ),
        keyboardActions = keyboardActions,
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(
                onClick = { visible = !visible },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (visible)
                        Icons.Filled.VisibilityOff
                    else
                        Icons.Filled.Visibility,
                    contentDescription = if (visible)
                        "Şifreyi gizle"
                    else
                        "Şifreyi göster",
                    tint = TextMuted,
                )
            }
        },
    )
}

enum class PasswordStrength { WEAK, MEDIUM, STRONG }

fun evaluatePasswordStrength(password: String): PasswordStrength {
    if (password.length < 6) return PasswordStrength.WEAK
    val hasUpper  = password.any { it.isUpperCase() }
    val hasDigit  = password.any { it.isDigit() }
    val hasSymbol = password.any { !it.isLetterOrDigit() }
    val score = listOf(hasUpper, hasDigit, hasSymbol).count { it }
    return when {
        password.length >= 8 && score >= 2 -> PasswordStrength.STRONG
        score >= 1 -> PasswordStrength.MEDIUM
        else -> PasswordStrength.WEAK
    }
}

@Composable
fun PasswordStrengthBar(
    password: String,
    modifier: Modifier = Modifier,
) {
    val strength = evaluatePasswordStrength(password)
    val (filledSegments, barColor, label) = when (strength) {
        PasswordStrength.WEAK   -> Triple(1, DangerRed,       "Zayıf")
        PasswordStrength.MEDIUM -> Triple(2, GameGold,         "Orta")
        PasswordStrength.STRONG -> Triple(3, NeonCyan,         "Güçlü")
    }

    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 4.dp),
        ) {
            repeat(3) { idx ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (idx < filledSegments) barColor else SurfaceVariantDark),
                )
            }
        }
        Text(
            text = label,
            color = barColor.copy(alpha = 0.80f),
            fontSize = 11.sp,
            fontFamily = AppFont,
            modifier = Modifier.padding(start = 2.dp),
        )
    }
}

@Composable
fun PasswordRequirementRow(text: String, met: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 3.dp),
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(
                    if (met) NeonCyan.copy(alpha = 0.15f) else SurfaceVariantDark
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (met) "✓" else "·",
                color = if (met) NeonCyan else TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = text,
            color = if (met) NeonCyan.copy(alpha = 0.85f) else TextMuted,
            fontSize = 12.sp,
            fontFamily = AppFont,
        )
    }
}

@Composable
fun PrimaryAuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) NeonCyan else NeonCyan.copy(alpha = 0.35f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) Color(0xFF0A0A0F) else TextMuted,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = AppFont,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
fun OutlineAuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = NeonCyan,
            fontSize = 14.sp,
            fontFamily = AppFont,
            letterSpacing = 0.3.sp,
        )
    }
}

@Composable
fun GhostAuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(0.5.dp, BorderDark, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = TextMuted,
            fontSize = 13.sp,
            fontFamily = AppFont,
        )
    }
}

@Composable
fun InfoBanner(
    message: String,
    subMessage: String? = null,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val bgColor   = if (isError) DangerRed.copy(alpha = 0.10f) else NeonCyan.copy(alpha = 0.08f)
    val brdColor  = if (isError) DangerRed.copy(alpha = 0.35f) else NeonCyan.copy(alpha = 0.30f)
    val textColor = if (isError) DangerRed                      else NeonCyan

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(bgColor)
            .border(0.5.dp, brdColor, RoundedCornerShape(13.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(if (isError) "⚠" else "✓", color = textColor, fontSize = 15.sp)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = message,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = AppFont,
            )
            if (subMessage != null) {
                Text(
                    text = subMessage,
                    color = textColor.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    fontFamily = AppFont,
                )
            }
        }
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = MutableInteractionSource(),
    indication = null,
    onClick = onClick,
)