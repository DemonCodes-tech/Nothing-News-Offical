package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.SignalRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EasterEggOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(4500)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF0D0D0D))
                .border(1.5.dp, SignalRed, RoundedCornerShape(4.dp))
                .testTag("easter_egg_banner")
                .clickable { onDismiss() }
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SignalRed)
                    )
                    Text(
                        text = "SYS // RESTRICTED ACCESS",
                        style = MaterialTheme.typography.labelSmall,
                        color = SignalRed
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SignalRed)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Exact case-sensitive easter egg string: "Made By Demon"
                Text(
                    text = "Made By Demon",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "[KERNEL SIGNATURE VERIFIED // ZERO BIAS]",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF888888),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun MarinOriginStoryOverlay(
    visible: Boolean,
    isNothing: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = visible) {
        onDismiss()
    }

    val portraitScale = remember { Animatable(0.88f) }
    val cardOffsetY = remember { Animatable(28f) }
    val cardAlpha = remember { Animatable(0f) }

    LaunchedEffect(visible) {
        if (visible) {
            portraitScale.snapTo(0.88f)
            cardOffsetY.snapTo(28f)
            cardAlpha.snapTo(0f)
            launch {
                portraitScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
            launch {
                delay(70)
                cardOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = 0.85f,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                delay(70)
                cardAlpha.animateTo(1f, animationSpec = tween(240))
            }
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(220)) + scaleIn(
            initialScale = 0.94f,
            animationSpec = spring(dampingRatio = 0.85f, stiffness = 420f)
        ),
        exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.95f),
        modifier = modifier
    ) {
        val backdropBrush = if (isNothing) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0A0A0A),
                    Color(0xFF141010),
                    Color(0xFF0A0A0A)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.surfaceContainerLowest,
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.32f),
                    MaterialTheme.colorScheme.surface
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backdropBrush)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .testTag("marin_origin_easter_egg_overlay")
        ) {
            if (isNothing) {
                DotFieldBackground()
            } else {
                MaterialYouExpressiveBackground()
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                val isWide = maxWidth >= 600.dp

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 780.dp),
                    shape = RoundedCornerShape(if (isNothing) 6.dp else 24.dp),
                    color = if (isNothing) Color(0xF00D0D0D) else MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.96f),
                    tonalElevation = if (isNothing) 0.dp else 6.dp,
                    border = BorderStroke(
                        1.dp,
                        if (isNothing) SignalRed.copy(alpha = 0.72f) else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Header Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isNothing) SignalRed else MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = stringResource(
                                        if (isNothing) R.string.marin_origin_badge_nothing
                                        else R.string.marin_origin_badge_material
                                    ),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("marin_origin_close_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Close secret origin story",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Hero Story Area (Responsive Side-by-Side on Wide, Stacked on Compact)
                        if (isWide) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(0.42f)
                                        .fillMaxHeight()
                                        .graphicsLayer {
                                            scaleX = portraitScale.value
                                            scaleY = portraitScale.value
                                        },
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    if (!isNothing) {
                                        ExpressiveMarinStageBackdrop(step = 0)
                                    }
                                    Image(
                                        painter = painterResource(id = R.drawable.marin_origin_secret),
                                        contentDescription = "Marin Kitagawa secret origin portrait",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .testTag("marin_origin_portrait"),
                                        contentScale = ContentScale.Fit,
                                        colorFilter = marinColorFilter(isNothing)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(0.58f)
                                        .fillMaxHeight()
                                        .graphicsLayer { translationY = cardOffsetY.value }
                                        .alpha(cardAlpha.value),
                                    contentAlignment = Alignment.Center
                                ) {
                                    MarinOriginLoreCard(isNothing = isNothing)
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(0.46f)
                                        .fillMaxWidth()
                                        .graphicsLayer {
                                            scaleX = portraitScale.value
                                            scaleY = portraitScale.value
                                        },
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    if (!isNothing) {
                                        ExpressiveMarinStageBackdrop(step = 0)
                                    }
                                    Image(
                                        painter = painterResource(id = R.drawable.marin_origin_secret),
                                        contentDescription = "Marin Kitagawa secret origin portrait",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .testTag("marin_origin_portrait"),
                                        contentScale = ContentScale.Fit,
                                        colorFilter = marinColorFilter(isNothing)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(0.54f)
                                        .fillMaxWidth()
                                        .graphicsLayer { translationY = cardOffsetY.value }
                                        .alpha(cardAlpha.value),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    MarinOriginLoreCard(isNothing = isNothing)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bottom Dismiss CTA (>= 48dp touch target)
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("marin_origin_dismiss_button"),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            shape = RoundedCornerShape(if (isNothing) 4.dp else 20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isNothing) Color.White else MaterialTheme.colorScheme.primary,
                                contentColor = if (isNothing) Color.Black else MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = stringResource(
                                    if (isNothing) R.string.marin_origin_dismiss_nothing
                                    else R.string.marin_origin_dismiss_material
                                ),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarinOriginLoreCard(
    isNothing: Boolean
) {
    val cardShape = RoundedCornerShape(if (isNothing) 4.dp else 18.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 360.dp)
            .testTag("marin_origin_story_card"),
        shape = cardShape,
        color = if (isNothing) Color(0xFF151515) else MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(
            1.dp,
            if (isNothing) Color(0xFF2D2D2D) else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(
                    if (isNothing) R.string.marin_origin_title_nothing
                    else R.string.marin_origin_title_material
                ),
                style = if (isNothing) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleMedium,
                color = if (isNothing) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.marin_origin_story),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isNothing) Color(0xFFE0E0E0) else MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(if (isNothing) 2.dp else 10.dp),
                color = if (isNothing) Color(0xFF1F1212) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.48f),
                border = BorderStroke(
                    1.dp,
                    if (isNothing) SignalRed.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                )
            ) {
                Text(
                    text = stringResource(
                        if (isNothing) R.string.marin_origin_codename_note_nothing
                        else R.string.marin_origin_codename_note_material
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isNothing) SignalRed else MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                )
            }
        }
    }
}

