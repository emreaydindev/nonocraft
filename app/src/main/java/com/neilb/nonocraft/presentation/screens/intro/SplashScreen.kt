package com.neilb.nonocraft.presentation.screens.intro

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.neilb.nonocraft.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private const val LOGO_START_MS    = 600L
private const val LOGO_DURATION_MS = 1100
private const val NAME_START_MS    = 1500L
private const val NAME_DURATION_MS = 700
private const val TAG_START_MS     = 1900L
private const val TAG_DURATION_MS  = 600
private const val GRID_START_MS    = 2200L
private const val NAV_DELAY_MS     = 3200L

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToDashboard: () -> Unit,
) {
    val isLoggedIn = false

    var logoVisible  by remember { mutableStateOf(false) }
    var nameVisible  by remember { mutableStateOf(false) }
    var tagVisible   by remember { mutableStateOf(false) }
    var gridVisible  by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue  = if (logoVisible) 1f else 0.7f,
        animationSpec = tween(durationMillis = LOGO_DURATION_MS, easing = FastOutSlowInEasing),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue  = if (logoVisible) 1f else 0f,
        animationSpec = tween(durationMillis = LOGO_DURATION_MS, easing = FastOutSlowInEasing),
        label = "logoAlpha"
    )

    val glowRadius by animateFloatAsState(
        targetValue  = if (logoVisible) 280f else 0f,
        animationSpec = tween(durationMillis = LOGO_DURATION_MS + 400, easing = FastOutSlowInEasing),
        label = "glowRadius"
    )

    val nameAlpha by animateFloatAsState(
        targetValue  = if (nameVisible) 1f else 0f,
        animationSpec = tween(durationMillis = NAME_DURATION_MS, easing = FastOutSlowInEasing),
        label = "nameAlpha"
    )

    val nameOffset by animateDpAsState(
        targetValue  = if (nameVisible) 0.dp else 14.dp,
        animationSpec = tween(durationMillis = NAME_DURATION_MS, easing = FastOutSlowInEasing),
        label = "nameOffset"
    )

    val tagAlpha by animateFloatAsState(
        targetValue  = if (tagVisible) 1f else 0f,
        animationSpec = tween(durationMillis = TAG_DURATION_MS, easing = FastOutSlowInEasing),
        label = "tagAlpha"
    )

    val tagOffset by animateDpAsState(
        targetValue  = if (tagVisible) 0.dp else 8.dp,
        animationSpec = tween(durationMillis = TAG_DURATION_MS, easing = FastOutSlowInEasing),
        label = "tagOffset"
    )

    val gridAlpha by animateFloatAsState(
        targetValue  = if (gridVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1800, easing = LinearEasing),
        label = "gridAlpha"
    )

    LaunchedEffect(Unit) {
        delay(LOGO_START_MS.milliseconds)
        logoVisible = true

        delay((NAME_START_MS - LOGO_START_MS).milliseconds)
        nameVisible = true

        delay((TAG_START_MS - NAME_START_MS).milliseconds)
        tagVisible = true

        delay((GRID_START_MS - TAG_START_MS).milliseconds)
        gridVisible = true

        delay((NAV_DELAY_MS - GRID_START_MS).milliseconds)
        if (isLoggedIn) onNavigateToDashboard() else onNavigateToOnboarding()
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(gridAlpha)
        ) {
            drawGridBackground(this, primaryColor)
        }

        if (glowRadius > 1f) {
            Canvas(
                modifier = Modifier
                    .size(320.dp)
                    .align(Alignment.Center)
                    .offset(y = (-60).dp)
                    .alpha(logoAlpha * 0.7f)
            ) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.18f),
                            primaryColor.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        radius = glowRadius
                    ),
                    radius = size.minDimension / 2f
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_neon_cube_logo),
                contentDescription = "Nonocraft logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White)) { append("Nono") }
                    withStyle(SpanStyle(color = primaryColor)) { append("craft") }
                },
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .alpha(nameAlpha)
                    .offset {
                        IntOffset(x = 0, y = nameOffset.roundToPx())
                    }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Forge Your Grid",
                color = primaryColor.copy(alpha = 0.55f),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .alpha(tagAlpha)
                    .offset {
                        IntOffset(x = 0, y = tagOffset.roundToPx())
                    }
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .alpha(gridAlpha),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { idx ->
                PulsingDot(delayMs = idx * 200, color = primaryColor)
            }
        }
    }
}

private fun drawGridBackground(scope: DrawScope, primaryColor: Color) {
    val step = 72f
    val gridColor = primaryColor.copy(alpha = 0.04f)
    val w = scope.size.width
    val h = scope.size.height

    var x = 0f
    while (x <= w) {
        scope.drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
        x += step
    }
    var y = 0f
    while (y <= h) {
        scope.drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
        y += step
    }
}

@Composable
private fun PulsingDot(delayMs: Int, color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "dot")
    val alpha by infiniteTransition.animateFloat(
        initialValue   = 0.25f,
        targetValue    = 0.9f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(700, delayMillis = delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue   = 1f,
        targetValue    = 1.3f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(700, delayMillis = delayMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotScale"
    )
    Canvas(
        modifier = Modifier
            .size(6.dp)
            .scale(scale)
    ) {
        drawCircle(color = color.copy(alpha = alpha))
    }
}