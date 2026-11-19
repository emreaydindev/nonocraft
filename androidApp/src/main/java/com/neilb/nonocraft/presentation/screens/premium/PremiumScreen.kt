package com.neilb.nonocraft.presentation.screens.premium

import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.ui.platform.LocalContext
import com.neilb.nonocraft.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.OfflineBolt
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neilb.nonocraft.presentation.adaptive.AdaptiveRow
import com.neilb.nonocraft.presentation.adaptive.isLargeFontScale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neilb.nonocraft.presentation.components.scrollbar
import com.neilb.nonocraft.presentation.screens.auth.components.AuthBackRow
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BackgroundDark
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.GameGold
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.PurpleAccent
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary
import com.neilb.nonocraft.presentation.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.milliseconds

private data class Plan(
    val id: String,
    val title: String,
    val price: String,
    val period: String,
    val monthlyEquiv: String? = null,
    val badge: String? = null,
    val accentColor: Color
)

private data class FeatureSlide(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val bulletPoints: List<String>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PremiumScreen(
    onNavigateBack: () -> Unit,
    viewModel: PremiumViewModel = viewModel()
) {
    val resources = LocalContext.current.resources
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    var selectedPlanId by remember { mutableStateOf("yearly") }
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is PremiumUiEvent.PurchaseSuccess -> {
                    showSuccess = true
                    delay(2500.milliseconds)
                    onNavigateBack()
                }
            }
        }
    }

    val plans = run {
        listOf(
            Plan(
                id = "monthly",
                title = stringResource(R.string.premium_monthly),
                price = "₺89",
                period = stringResource(R.string.premium_per_month),
                accentColor = NeonCyan
            ),
            Plan(
                id = "yearly",
                title = stringResource(R.string.premium_yearly),
                price = "₺599",
                period = stringResource(R.string.premium_per_year),
                monthlyEquiv = stringResource(R.string.premium_yearly_equiv, "₺49.90"),
                accentColor = GameGold
            )
        )
    }

    val slides = run {
        listOf(
            FeatureSlide(
                title = stringResource(R.string.premium_s1_title),
                description = stringResource(R.string.premium_s1_body),
                icon = Icons.Rounded.OfflineBolt,
                color = NeonCyan,
                bulletPoints = listOf(
                    stringResource(R.string.premium_s1_b1),
                    stringResource(R.string.premium_s1_b2),
                    stringResource(R.string.premium_s1_b3),
                    stringResource(R.string.premium_s1_b4)
                )
            ),
            FeatureSlide(
                title = stringResource(R.string.premium_s2_title),
                description = stringResource(R.string.premium_s2_body),
                icon = Icons.Rounded.AutoAwesome,
                color = PurpleAccent,
                bulletPoints = listOf(
                    stringResource(R.string.premium_s2_b1),
                    stringResource(R.string.premium_s2_b2),
                    stringResource(R.string.premium_s2_b3)
                )
            ),
            FeatureSlide(
                title = stringResource(R.string.premium_s3_title),
                description = stringResource(R.string.premium_s3_body),
                icon = Icons.Rounded.WorkspacePremium,
                color = GameGold,
                bulletPoints = listOf(
                    stringResource(R.string.premium_s3_b1),
                    stringResource(R.string.premium_s3_b2),
                    stringResource(R.string.premium_s3_b3)
                )
            )
        )
    }

    val pagerState = rememberPagerState { slides.size }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .navigationBarsPadding()
    ) {
        // At the largest font scales the whole paywall scrolls as one page and the slideshow gets a fixed
        // height, so the plans and the purchase button are never pushed off screen.
        val largeText = isLargeFontScale()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .then(if (largeText) Modifier.verticalScroll(rememberScrollState()) else Modifier)
        ) {
            // Top Bar
            AuthBackRow { onNavigateBack() }

            // Slideshow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (largeText) Modifier.height(440.dp) else Modifier.weight(1f))
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    FeatureSlideContent(
                        slide = slides[page],
                        pagerState = pagerState,
                        page = page
                    )
                }
            }

            // Slide Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clearAndSetSemantics { contentDescription = resources.getString(R.string.common_page_of, pagerState.currentPage + 1, slides.size) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(slides.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(targetValue = if (isSelected) 24.dp else 8.dp, label = "indicator_width")
                    val color = if (isSelected) slides[index].color else BorderDark

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Plans
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                plans.forEach { plan ->
                    PlanSelectionCard(
                        plan = plan,
                        isSelected = selectedPlanId == plan.id,
                        onClick = { selectedPlanId = plan.id }
                    )
                }
            }

            // Purchase CTA
            val selectedPlan = plans.find { it.id == selectedPlanId } ?: plans.last()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                PurchaseButton(
                    plan = selectedPlan,
                    isProcessing = isProcessing,
                    onClick = { viewModel.purchasePremium(selectedPlan.id) }
                )
            }

            Text(
                text = stringResource(R.string.premium_renewal_note),
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = AppFont,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 24.dp)
            )
        }

        // Overlays
        if (isProcessing) {
            ProcessingOverlay()
        }

        if (showSuccess) {
            SuccessOverlay()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FeatureSlideContent(
    slide: FeatureSlide,
    pagerState: PagerState,
    page: Int
) {
    val pageOffset = (pagerState.currentPage - page + pagerState.currentPageOffsetFraction).absoluteValue.coerceIn(0f, 1f)
    val alpha = 1f - pageOffset * 0.5f
    val scale = 1f - pageOffset * 0.1f

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .scale(scale)
            .scrollbar(
                scrollState = scrollState,
                thumbColor = slide.color.copy(alpha = 0.3f)
            )
            .verticalScroll(scrollState)
            .padding(horizontal = 32.dp)
            .padding(vertical = 16.dp), // Yukarıdan ve aşağıdan kaydırma rahatlığı için
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(slide.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = slide.icon,
                contentDescription = null,
                tint = slide.color,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = slide.title,
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppFont,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = slide.description,
            color = TextSecondary,
            fontSize = 14.sp,
            fontFamily = AppFont,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            slide.bulletPoints.forEach { point ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = slide.color,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = point,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontFamily = AppFont,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanSelectionCard(
    plan: Plan,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(targetValue = if (isSelected) plan.accentColor else BorderDark, label = "border_color")
    val bgColor by animateColorAsState(targetValue = if (isSelected) plan.accentColor.copy(alpha = 0.08f) else SurfaceDark, label = "bg_color")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(width = if (isSelected) 2.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .selectable(
                selected = isSelected,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color(0xFF0D0D10)),
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        AdaptiveRow(modifier = Modifier.fillMaxWidth(), columnSpacing = 8.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = plan.title,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = AppFont
                    )
                    if (plan.badge != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(plan.accentColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = plan.badge,
                                color = BackgroundDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AppFont
                            )
                        }
                    }
                }
                if (plan.monthlyEquiv != null) {
                    Text(
                        text = plan.monthlyEquiv,
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontFamily = AppFont
                    )
                }
            }

            Column(horizontalAlignment = if (isLargeFontScale()) Alignment.Start else Alignment.End) {
                Text(
                    text = plan.price,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                    softWrap = false
                )
                Text(
                    text = plan.period,
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontFamily = AppFont
                )
            }
        }
    }
}

@Composable
private fun PurchaseButton(
    plan: Plan,
    isProcessing: Boolean,
    onClick: () -> Unit
) {
    val buttonColor by animateColorAsState(targetValue = plan.accentColor, label = "button_color")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(buttonColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color(0xFF0D0D10)),
                enabled = !isProcessing,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = isProcessing, label = "button_content") { processing ->
            if (processing) {
                CircularProgressIndicator(
                    color = BackgroundDark,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.premium_continue_with_price, plan.price, plan.period),
                    color = BackgroundDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont
                )
            }
        }
    }
}

@Composable
private fun ProcessingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite }
        ) {
            CircularProgressIndicator(color = GameGold)
            Text(
                text = stringResource(R.string.premium_processing),
                color = TextPrimary,
                fontFamily = AppFont,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SuccessOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Assertive }
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(GameGold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.WorkspacePremium,
                    contentDescription = null,
                    tint = GameGold,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = stringResource(R.string.premium_welcome),
                color = GameGold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont
            )
            Text(
                text = stringResource(R.string.premium_welcome_body),
                color = TextSecondary,
                fontSize = 14.sp,
                fontFamily = AppFont
            )
        }
    }
}