package com.neilb.nonocraft.presentation.screens.intro

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.theme.BrightGreen
import com.neilb.nonocraft.presentation.theme.GridBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.milliseconds

private data class OnboardingSlide(
    val tag: String,
    val title: String,
    val titleAccent: String,
    val description: String,
    val accentColor: Color,
    val visual: @Composable (Color) -> Unit,
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
) {
    val primaryCyan = MaterialTheme.colorScheme.primary
    val secondaryPurple = MaterialTheme.colorScheme.secondary
    val tertiaryGold = MaterialTheme.colorScheme.tertiary
    val backgroundColor = MaterialTheme.colorScheme.background
    val errorColor = MaterialTheme.colorScheme.error

    val colors = listOf(primaryCyan, errorColor, tertiaryGold, secondaryPurple, BrightGreen)
    var selectedColor by remember { mutableIntStateOf(3) }

    val slides = rememberSlides(
        cyan = primaryCyan,
        selectedColor = selectedColor,
        colors = colors,
        setSelectedColor = { color -> selectedColor = color },
        gold = tertiaryGold
    )

    val pagerState = rememberPagerState { slides.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .navigationBarsPadding()
    ) {
        GridBackground()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 0.dp),
                beyondViewportPageCount = 1,
            ) { page ->
                SlideContent(
                    slide = slides[page],
                    pagerState = pagerState,
                    page = page,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            BottomSection(
                pagerState = pagerState,
                totalPages = slides.size,
                accentColor = slides[pagerState.currentPage].accentColor,
                onNext = {
                    scope.launch {
                        if (pagerState.currentPage < slides.size - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onFinishOnboarding()
                        }
                    }
                },
                onSkip = onFinishOnboarding,
            )
        }
    }
}

@Composable
private fun SlideContent(
    slide: OnboardingSlide,
    pagerState: PagerState,
    page: Int,
    modifier: Modifier = Modifier,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val pageOffset = (pagerState.currentPage - page + pagerState.currentPageOffsetFraction)
        .absoluteValue.coerceIn(0f, 1f)
    val contentAlpha = 1f - pageOffset * 0.6f

    Column(
        modifier = modifier
            .padding(horizontal = 28.dp)
            .alpha(contentAlpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Text(
            text = slide.tag,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp,
            color = slide.accentColor.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 24.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.1f)
                .clip(RoundedCornerShape(24.dp))
                .background(surfaceColor)
                .drawBehind {
                    drawRoundRect(
                        color = slide.accentColor.copy(alpha = 0.30f),
                        size = size,
                        cornerRadius = CornerRadius(24.dp.toPx()),
                        style = Stroke(width = 1.dp.toPx()),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            slide.visual(slide.accentColor)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = buildAnnotatedString {
                val accentStart = slide.title.indexOf(slide.titleAccent)
                if (accentStart >= 0) {
                    append(slide.title.substring(0, accentStart))
                    withStyle(SpanStyle(color = slide.accentColor)) {
                        append(slide.titleAccent)
                    }
                    append(slide.title.substring(accentStart + slide.titleAccent.length))
                } else {
                    append(slide.title)
                }
            },
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        Text(
            text = slide.description,
            fontSize = 14.sp,
            color = textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
        )
    }
}

@Composable
private fun BottomSection(
    pagerState: PagerState,
    totalPages: Int,
    accentColor: Color,
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    val isLastPage = pagerState.currentPage == totalPages - 1
    val backgroundColor = MaterialTheme.colorScheme.background
    val textMuted = MaterialTheme.colorScheme.outlineVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(bottom = 48.dp, top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        PageIndicator(
            pagerState = pagerState,
            count = totalPages,
            activeColor = accentColor
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(accentColor)
                .rippleClickable(onClick = onNext),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isLastPage) "Başla" else "Devam Et",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = backgroundColor,
            )
        }

        if (!isLastPage) {
            Text(
                text = "Atla",
                fontSize = 13.sp,
                color = textMuted,
                modifier = Modifier.rippleClickable(onClick = onSkip),
            )
        }
    }
}

@Composable
private fun PageIndicator(
    pagerState: PagerState,
    count: Int,
    activeColor: Color,
    modifier: Modifier = Modifier,
) {
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val isSelected = pagerState.currentPage == index

            val width by animateDpAsState(
                targetValue = if (isSelected) 24.dp else 8.dp,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                label = "dotWidth$index",
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(if (isSelected) activeColor else surfaceVariantColor)
            )
        }
    }
}


@Composable
private fun VisualSolve(accentColor: Color) {
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline

    val pattern = remember {
        listOf(
            listOf(0, 1, 0, 0, 0, 1, 0),
            listOf(1, 1, 1, 0, 1, 1, 1),
            listOf(1, 1, 1, 1, 1, 1, 1),
            listOf(0, 1, 1, 1, 1, 1, 0),
            listOf(0, 0, 1, 1, 1, 0, 0),
            listOf(0, 0, 0, 1, 0, 0, 0),
        )
    }

    var filledCount by remember { mutableIntStateOf(0) }
    val totalCells = pattern.sumOf { it.count { c -> c == 1 } }

    LaunchedEffect(Unit) {
        while (true) {
            for (i in 0..totalCells) {
                filledCount = i
                delay(80L.milliseconds)
            }
            delay(1200L.milliseconds)
            filledCount = 0
            delay(400L.milliseconds)
        }
    }

    var cellsFilled = 0

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Spacer(modifier = Modifier.width(28.dp))
            listOf("2", "4", "4", "4", "4", "4", "2").forEach { clue ->
                Box(modifier = Modifier.width(32.dp), contentAlignment = Alignment.Center) {
                    Text(clue, fontSize = 9.sp, color = accentColor, fontWeight = FontWeight.Medium)
                }
            }
        }

        pattern.forEach { row ->
            val rowClue = row.count { it == 1 }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.width(28.dp), contentAlignment = Alignment.CenterEnd) {
                    Text("$rowClue", fontSize = 9.sp, color = accentColor, fontWeight = FontWeight.Medium)
                }
                row.forEach { cell ->
                    val shouldBeFilled = cell == 1
                    val isFilled = if (shouldBeFilled) {
                        cellsFilled++
                        cellsFilled <= filledCount
                    } else false

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when {
                                    isFilled   -> accentColor
                                    cell == 1  -> surfaceVariantColor
                                    else       -> surfaceVariantColor
                                }
                            )
                            .drawBehind {
                                if (!isFilled) {
                                    drawRoundRect(
                                        color = borderColor,
                                        size = size,
                                        cornerRadius = CornerRadius(4.dp.toPx()),
                                        style = Stroke(width = 0.5.dp.toPx()),
                                    )
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun VisualCraft(selectedColor: Int, colors: List<Color>, setSelectedColor: (Int) -> Unit) {

    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline

    val pixelGrid = remember {
        listOf(
            listOf(0, 0, 1, 1, 1, 0, 0),
            listOf(0, 1, 1, 0, 1, 1, 0),
            listOf(1, 1, 1, 1, 1, 1, 1),
            listOf(1, 0, 1, 1, 1, 0, 1),
            listOf(0, 1, 1, 1, 1, 1, 0),
            listOf(0, 0, 1, 0, 1, 0, 0),
            listOf(0, 1, 0, 0, 0, 1, 0)
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            pixelGrid.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    row.forEach { cell ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (cell == 1) colors[selectedColor]
                                    else surfaceVariantColor
                                )
                                .drawBehind {
                                    if (cell == 0) {
                                        drawRoundRect(
                                            color = borderColor,
                                            size = size,
                                            cornerRadius = CornerRadius(3.dp.toPx()),
                                            style = Stroke(width = 0.5.dp.toPx()),
                                        )
                                    }
                                }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            colors.forEachIndexed { i, color ->
                val isSelected = i == selectedColor
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 28.dp else 22.dp)
                        .clip(CircleShape)
                        .background(color)
                        .drawBehind {
                            if (isSelected) {
                                drawCircle(
                                    color = color,
                                    radius = size.minDimension / 2f + 2.dp.toPx(),
                                    style = Stroke(width = 1.5.dp.toPx()),
                                )
                            }
                        }
                        .rippleClickable { setSelectedColor(i) }
                )
            }
        }
    }
}

@Composable
private fun VisualPremium(accentColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f))
                .drawBehind {
                    drawCircle(
                        color = accentColor.copy(alpha = 0.40f),
                        radius = size.minDimension / 2f,
                        style = Stroke(width = 1.5.dp.toPx()),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Text("★", fontSize = 22.sp, color = accentColor)
        }

        Spacer(modifier = Modifier.height(12.dp))

        val features = listOf(
            "Reklamsız deneyim",
            "Sınırsız kristal paketleri",
            "Ekstra yaşam kalpleri",
            "Gönderilerini öne çıkar"
        )

        features.forEach { feature ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("✓ ", color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(feature, color = accentColor, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun Modifier.rippleClickable(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = Color(0xFF0D0D10)),
            onClick = onClick,
        )
    )

@Composable
private fun rememberSlides(cyan: Color, selectedColor: Int, colors: List<Color>, setSelectedColor: (Int) -> Unit, gold: Color) =
    remember(selectedColor) {
        listOf(
            OnboardingSlide(
                tag          = "BULMACA · MANTIK · SINIRSIZ",
                title        = "Çöz!",
                titleAccent  = "Çöz",
                description  = "Her gün yenilenen, binlerce benzersiz nonogram bulmacasını keşfet. Mantığını konuştur.",
                accentColor  = cyan,
                visual       = { color -> VisualSolve(color) },
            ),
            OnboardingSlide(
                tag          = "TASARIM · PAYLAŞIM · TOPLULUK",
                title        = "Tasarla ve Paylaş!",
                titleAccent  = "Paylaş",
                description  = "Kendi pixel artını oluştur, toplulukla paylaş. Beğenileri topla, efsane ol.",
                accentColor  = colors[selectedColor],
                visual       = { color -> VisualCraft(selectedColor, colors, setSelectedColor) },
            ),
            OnboardingSlide(
                tag          = "PREMIUM · AVANTAJLAR",
                title        = "Sınırlarını Aş!",
                titleAccent  = "Sınırlarını",
                description  = "Premium ile reklamsız bir deneyim yaşa; sınırsız kristal, fazladan kalpler ve özel öne çıkarmalar kazan.",
                accentColor  = gold,
                visual       = { color -> VisualPremium(color) },
            ),
        )
    }