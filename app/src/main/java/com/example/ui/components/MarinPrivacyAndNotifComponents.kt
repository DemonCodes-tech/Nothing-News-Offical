package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.NothingGreenBorderDark
import com.example.ui.theme.NothingGreenBorderLight
import com.example.ui.theme.NothingGreenTintDark
import com.example.ui.theme.NothingGreenTintLight
import com.example.ui.theme.NothingRedTintDark
import com.example.ui.theme.NothingRedTintLight
import com.example.ui.theme.ScallopedSquircleShape
import com.example.ui.theme.SignalRed

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun openAppNotificationSettings(context: Context) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}

@Composable
fun MarinPrivacyStatementCard(
    isNothing: Boolean,
    compact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(if (isNothing) 4.dp else 14.dp)
    val imageSize = if (compact) 52.dp else 64.dp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("marin_privacy_card"),
        shape = shape,
        color = if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(
            1.dp,
            if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (compact) 8.dp else 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isNothing) {
                Box(
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.marin_privacy),
                        contentDescription = "Zero Data Privacy Guarantee",
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("marin_privacy_image"),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter,
                        colorFilter = marinColorFilter(true)
                    )
                }
            } else {
                Box(
                    modifier = Modifier.size(imageSize + 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ExpressiveAnimatedShapeBadge(
                        kind = ExpressiveShapeKind.ORGANIC_FLOWER,
                        size = imageSize + 4.dp,
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        rotationDurationMs = 15000
                    )
                    Box(
                        modifier = Modifier
                            .size(imageSize - 4.dp)
                            .clip(ScallopedSquircleShape(scallops = 10, scallopDepth = 0.08f))
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.marin_privacy),
                            contentDescription = "Zero Data Privacy Guarantee",
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("marin_privacy_image"),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.TopCenter,
                            colorFilter = marinColorFilter(false)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(SignalRed)
                    )
                    Text(
                        text = if (isNothing) "ZERO TELEMETRY // NO TRACKING" else "Zero Data Collection",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (compact) 9.sp else 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "\"We don't give a shit about your info or data.\"",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 10.5.sp else 11.5.sp,
                    lineHeight = if (compact) 14.sp else 15.sp
                )

                Text(
                    text = "No accounts, no analytics, no ad IDs, no servers. Preferences and saved wires stay 100% on your device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = if (compact) 9.sp else 10.sp,
                    lineHeight = if (compact) 12.sp else 13.5.sp
                )
            }
        }
    }
}

@Composable
fun DeviceNotificationAccessBanner(
    hasPermission: Boolean,
    isNothing: Boolean,
    onRequestPermission: () -> Unit,
    onSendTestNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(if (isNothing) 4.dp else 14.dp)
    val isDark = LocalIsDarkTheme.current
    val greenBg = if (isDark) NothingGreenTintDark else NothingGreenTintLight
    val greenBorder = if (isDark) NothingGreenBorderDark else NothingGreenBorderLight
    val redBg = if (isDark) NothingRedTintDark else NothingRedTintLight

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("device_notif_access_banner"),
        shape = shape,
        color = if (hasPermission) {
            if (isNothing) greenBg else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        } else {
            if (isNothing) redBg else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f)
        },
        border = BorderStroke(
            1.dp,
            if (hasPermission) {
                if (isNothing) greenBorder else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            } else {
                SignalRed.copy(alpha = 0.85f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (hasPermission) Icons.Outlined.NotificationsActive else Icons.Outlined.NotificationsOff,
                contentDescription = "Notification permission status",
                tint = if (hasPermission) MaterialTheme.colorScheme.primary else SignalRed,
                modifier = Modifier.size(18.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (hasPermission) {
                        if (isNothing) "DEVICE ALERTS // GRANTED" else "Device Alerts Enabled"
                    } else {
                        if (isNothing) "ALLOW DEVICE NOTIFICATIONS" else "Enable Real Notifications"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (hasPermission) MaterialTheme.colorScheme.onSurface else SignalRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp
                )
                Text(
                    text = if (hasPermission) {
                        "System dispatches ready for earthquakes, Gaza wires & WHO alerts."
                    } else {
                        "Grant notification access so NewsThing can push live alerts."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 8.5.sp,
                    lineHeight = 11.sp
                )
            }

            Button(
                onClick = if (hasPermission) onSendTestNotification else onRequestPermission,
                modifier = Modifier
                    .defaultMinSize(minHeight = 36.dp)
                    .testTag(if (hasPermission) "send_test_notif_button" else "grant_notif_permission_button"),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                shape = RoundedCornerShape(if (isNothing) 2.dp else 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasPermission) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        SignalRed
                    },
                    contentColor = if (hasPermission) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        Color.White
                    }
                )
            ) {
                Text(
                    text = if (hasPermission) {
                        if (isNothing) "TEST" else "Test"
                    } else {
                        if (isNothing) "ALLOW" else "Allow"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
fun MarinNotificationPermissionOverlay(
    visible: Boolean,
    isNothing: Boolean,
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return

    BackHandler(enabled = visible) {
        onDismiss()
    }

    val bubbleBg = MaterialTheme.colorScheme.surfaceContainerHigh
    val bubbleBorder = if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(if (isNothing) 4.dp else 20.dp))
                .border(
                    1.dp,
                    if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(if (isNothing) 4.dp else 20.dp)
                )
                .testTag("marin_notif_permission_overlay"),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SignalRed)
                        )
                        Text(
                            text = if (isNothing) "PERMISSION // LIVE WIRE ALERTS" else "Live Dispatch Notifications",
                            style = MaterialTheme.typography.labelMedium,
                            color = SignalRed,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Dismiss notification prompt",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Marin in overshoulder sweater asking for notification access
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.marin_step2),
                        contentDescription = "Marin Kitagawa asking for notification access",
                        modifier = Modifier
                            .size(width = 112.dp, height = 165.dp)
                            .testTag("marin_overshoulder_notif_image"),
                        contentScale = ContentScale.Fit,
                        colorFilter = marinColorFilter(isNothing)
                    )

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.Top
                    ) {
                        Canvas(
                            modifier = Modifier
                                .width(8.dp)
                                .height(22.dp)
                                .padding(top = 10.dp)
                        ) {
                            val path = Path().apply {
                                moveTo(size.width, 0f)
                                lineTo(0f, size.height / 2f)
                                lineTo(size.width, size.height)
                                close()
                            }
                            drawPath(path = path, color = bubbleBg)
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(if (isNothing) 4.dp else 14.dp))
                                .border(1.dp, bubbleBorder, RoundedCornerShape(if (isNothing) 4.dp else 14.dp)),
                            color = bubbleBg
                        ) {
                            Column(
                                modifier = Modifier.padding(9.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isNothing) "MARIN KITAGAWA // ALERTS" else "Marin Kitagawa • Alerts",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp
                                )
                                Text(
                                    text = "Hey! Allow device notifications so I can actually ping you for earthquakes, tsunamis, Gaza humanitarian updates, and WHO health alerts! Zero spam—only what you toggle on!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    lineHeight = 14.5.sp
                                )
                            }
                        }
                    }
                }

                // Attached Marin Privacy Statement Card
                MarinPrivacyStatementCard(
                    isNothing = isNothing,
                    compact = true
                )

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 46.dp)
                            .testTag("notif_prompt_later_button"),
                        shape = RoundedCornerShape(if (isNothing) 2.dp else 20.dp)
                    ) {
                        Text(
                            text = if (isNothing) "NOT NOW" else "Not Now",
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Button(
                        onClick = onRequestPermission,
                        modifier = Modifier
                            .weight(1.7f)
                            .defaultMinSize(minHeight = 46.dp)
                            .testTag("notif_prompt_allow_button"),
                        shape = RoundedCornerShape(if (isNothing) 2.dp else 20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = if (isNothing) "ALLOW NOTIFS ->" else "Allow Notifications",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContributorsOfNewsThingCard(
    isNothing: Boolean,
    contentPadding: androidx.compose.ui.unit.Dp = 12.dp,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    NothingCard(
        modifier = modifier.fillMaxWidth(),
        testTag = "settings_contributors_card",
        contentPadding = contentPadding
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                ) {
                    if (isNothing) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SignalRed)
                        )
                    } else {
                        ExpressiveAnimatedShapeBadge(
                            kind = ExpressiveShapeKind.ORGANIC_FLOWER,
                            size = 16.dp,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onTertiaryContainer)
                            )
                        }
                    }
                    Text(
                        text = androidx.compose.ui.res.stringResource(
                            if (isNothing) R.string.contributors_title_nothing else R.string.contributors_title_material
                        ),
                        style = if (isNothing) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(if (isNothing) 2.dp else 999.dp))
                        .background(if (isNothing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isNothing) "HALL OF FAME" else "Hall of Fame",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNothing) SignalRed else MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                }
            }

            // Attached Marin Heart Appreciation Strip ("Thanks Pookies!")
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(if (isNothing) 4.dp else 14.dp),
                color = if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceContainerHigh,
                border = BorderStroke(
                    1.dp,
                    if (isNothing) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(9.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isNothing) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    1.dp,
                                    SignalRed.copy(alpha = 0.55f),
                                    RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.marin_contributors),
                                contentDescription = "Marin Kitagawa saying Thanks Pookies!",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("contributors_marin_image"),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.TopCenter,
                                colorFilter = marinColorFilter(true)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier.size(68.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ExpressiveAnimatedShapeBadge(
                                kind = ExpressiveShapeKind.ORGANIC_FLOWER,
                                size = 68.dp,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.72f),
                                rotationDurationMs = 15000
                            )
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(ScallopedSquircleShape(scallops = 10, scallopDepth = 0.08f))
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.marin_contributors),
                                    contentDescription = "Marin Kitagawa saying Thanks Pookies!",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .testTag("contributors_marin_image"),
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.TopCenter,
                                    colorFilter = marinColorFilter(false)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = if (isNothing) "MARIN // COMMUNITY SHOUTOUT" else "Marin Kitagawa • Shoutout",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        )
                        Text(
                            text = androidx.compose.ui.res.stringResource(R.string.marin_contributors_thanks),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Huge love to the awesome humans helping shape NewsThing!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 10.sp,
                            lineHeight = 13.5.sp
                        )
                    }
                }
            }

            // Contributor Card: The Name Giver (Fall_Spezialgelagert on Reddit)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .clip(RoundedCornerShape(if (isNothing) 4.dp else 14.dp))
                    .clickable { openRedditProfile(context, "Fall_Spezialgelagert") }
                    .testTag("contributor_name_giver_card"),
                shape = RoundedCornerShape(if (isNothing) 4.dp else 14.dp),
                color = if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    1.dp,
                    if (isNothing) SignalRed.copy(alpha = 0.65f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(if (isNothing) 2.dp else 999.dp))
                                    .background(SignalRed)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isNothing) "THE NAME GIVER" else "The Name Giver",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(if (isNothing) 2.dp else 999.dp))
                                    .background(if (isNothing) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "REDDIT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isNothing) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Text(
                            text = "u/Fall_Spezialgelagert",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "He gave the app its name — \"NewsThing\" — on Reddit.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 10.sp,
                            lineHeight = 13.5.sp
                        )
                    }

                    Text(
                        text = if (isNothing) "[PROFILE]" else "Reddit ->",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Beta Testers Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
                    .testTag("contributors_beta_testers_heading"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(if (isNothing) SignalRed else MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = androidx.compose.ui.res.stringResource(
                        if (isNothing) {
                            R.string.contributors_beta_testers_heading_nothing
                        } else {
                            R.string.contributors_beta_testers_heading_material
                        }
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isNothing) SignalRed else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Individual Beta Tester Cards (Plain Display Cards, No External Links)
            BetaTesterContributorCard(
                testerName = androidx.compose.ui.res.stringResource(R.string.contributor_beta_tester_adomio),
                roleLabel = androidx.compose.ui.res.stringResource(R.string.contributor_beta_tester_role),
                description = androidx.compose.ui.res.stringResource(R.string.contributor_beta_tester_desc),
                isNothing = isNothing,
                testTag = "contributor_beta_tester_adomio_card"
            )

            BetaTesterContributorCard(
                testerName = androidx.compose.ui.res.stringResource(R.string.contributor_beta_tester_sharpxii),
                roleLabel = androidx.compose.ui.res.stringResource(R.string.contributor_beta_tester_role),
                description = androidx.compose.ui.res.stringResource(R.string.contributor_beta_tester_desc),
                isNothing = isNothing,
                testTag = "contributor_beta_tester_sharpxii_card"
            )
        }
    }
}

@Composable
private fun BetaTesterContributorCard(
    testerName: String,
    roleLabel: String,
    description: String,
    isNothing: Boolean,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(if (isNothing) 4.dp else 14.dp),
        color = if (isNothing) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (isNothing) SignalRed.copy(alpha = 0.50f) else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(if (isNothing) 2.dp else 999.dp))
                    .background(if (isNothing) SignalRed else MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isNothing) roleLabel.uppercase() else roleLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isNothing) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = testerName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 10.sp,
                lineHeight = 13.5.sp
            )
        }
    }
}
