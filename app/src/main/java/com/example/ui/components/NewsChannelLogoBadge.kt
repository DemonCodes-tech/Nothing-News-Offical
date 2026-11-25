package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Renders authentic, high-fidelity broadcast news channel logos
 * for global outlets (CNN, BBC, Sky News, Al Jazeera, DW, Reuters, CNBC,
 * Bloomberg, NHK World, CBC News, Fox News, MSNBC, CNA, France 24,
 * ABC, NBC, CBS, The Guardian, UN News, WION, AP, etc.).
 */
@Composable
fun NewsChannelLogoBadge(
    channelId: String,
    shortCode: String,
    badgeColorHex: Long = 0xFFCC0000,
    modifier: Modifier = Modifier
) {
    val norm = "${channelId}_${shortCode}".lowercase().replace("-", "_").trim()

    when {
        // 1. CNN International
        norm.contains("cnn") -> {
            Box(
                modifier = modifier
                    .size(width = 28.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFCC0000)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CNN",
                    color = Color.White,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.8).sp,
                    lineHeight = 10.sp
                )
                // Iconic central slit cutting through letters
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(1.dp)
                        .background(Color(0xFFCC0000))
                )
            }
        }

        // 2. BBC News (Iconic 3 sharp black squares)
        norm.contains("bbc") -> {
            Row(
                modifier = modifier.height(16.dp),
                horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("B", "B", "C").forEach { letter ->
                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(Color(0xFF000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter,
                            color = Color.White,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 8.5.sp
                        )
                    }
                }
            }
        }

        // 3. Sky News
        norm.contains("sky") -> {
            Box(
                modifier = modifier
                    .size(width = 34.dp, height = 18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFD91023), Color(0xFFFF3344))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = "sky",
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    )
                    Text(
                        text = "news",
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 6.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 4. Al Jazeera (Golden Arabic flame insignia on amber orange)
        norm.contains("al_jazeera") || norm.contains("aljazeera") || norm.contains("aje") || norm.contains("aja") -> {
            Box(
                modifier = modifier
                    .size(width = 24.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFE25A00)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(13.dp)) {
                    val path = Path().apply {
                        moveTo(size.width * 0.5f, 0f)
                        cubicTo(size.width * 0.9f, size.height * 0.35f, size.width, size.height * 0.7f, size.width * 0.5f, size.height)
                        cubicTo(0f, size.height * 0.7f, size.width * 0.1f, size.height * 0.35f, size.width * 0.5f, 0f)
                        close()
                    }
                    drawPath(path, Color(0xFFFFD700)) // Gold flame
                    drawCircle(Color(0xFFE25A00), radius = size.width * 0.18f, center = Offset(size.width * 0.5f, size.height * 0.62f))
                    drawCircle(Color(0xFFFFD700), radius = size.width * 0.08f, center = Offset(size.width * 0.5f, size.height * 0.62f))
                }
            }
        }

        // 5. DW (Deutsche Welle)
        norm.startsWith("dw") || norm.contains("deutsche") -> {
            Box(
                modifier = modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF002D5A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DW",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
            }
        }

        // 6. Reuters (Orbiting Dots Logo)
        norm.contains("reuters") -> {
            Box(
                modifier = modifier
                    .size(width = 24.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFFF8000)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(13.dp)) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.width * 0.36f
                    for (i in 0 until 12) {
                        val angle = (i * 30.0) * (Math.PI / 180.0)
                        val dotX = center.x + radius * cos(angle).toFloat()
                        val dotY = center.y + radius * sin(angle).toFloat()
                        drawCircle(Color.White, radius = 1.1.dp.toPx(), center = Offset(dotX, dotY))
                    }
                    drawCircle(Color.White, radius = 1.3.dp.toPx(), center = center)
                }
            }
        }

        // 7. CNBC (Peacock Multi-Color Feathers)
        norm.contains("cnbc") -> {
            Box(
                modifier = modifier
                    .size(width = 28.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF002F6C)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(0.8.dp)) {
                        listOf(
                            Color(0xFFFFCC00), // Yellow
                            Color(0xFFFF6600), // Orange
                            Color(0xFFE50000), // Red
                            Color(0xFF990099), // Purple
                            Color(0xFF0066CC), // Blue
                            Color(0xFF009933)  // Green
                        ).forEach { c ->
                            Box(
                                modifier = Modifier
                                    .size(width = 1.8.dp, height = 3.5.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(c)
                            )
                        }
                    }
                    Text(
                        text = "CNBC",
                        color = Color.White,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 7.sp
                    )
                }
            }
        }

        // 8. NBC News (Rainbow Peacock Emblem)
        norm.contains("nbc") -> {
            Box(
                modifier = modifier
                    .size(width = 26.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF002244)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(0.8.dp)) {
                        listOf(
                            Color(0xFFFFCC00),
                            Color(0xFFFF6600),
                            Color(0xFFE50000),
                            Color(0xFF990099),
                            Color(0xFF0066CC),
                            Color(0xFF009933)
                        ).forEach { c ->
                            Box(
                                modifier = Modifier
                                    .size(width = 1.8.dp, height = 4.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(c)
                            )
                        }
                    }
                    Text(
                        text = "NBC",
                        color = Color.White,
                        fontSize = 6.5.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 7.sp
                    )
                }
            }
        }

        // 9. CBC News (Canadian Exploding Gem)
        norm.contains("cbc") -> {
            Box(
                modifier = modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0001B)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(13.dp)) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(Color.White, radius = size.width * 0.24f, center = center)
                    drawCircle(Color(0xFFE0001B), radius = size.width * 0.12f, center = center)
                    val petalDist = size.width * 0.38f
                    listOf(
                        Offset(center.x, center.y - petalDist),
                        Offset(center.x, center.y + petalDist),
                        Offset(center.x - petalDist, center.y),
                        Offset(center.x + petalDist, center.y)
                    ).forEach { p ->
                        drawCircle(Color.White, radius = 1.3.dp.toPx(), center = p)
                    }
                }
            }
        }

        // 10. ABC News (Classic Black Circle)
        norm.startsWith("abc") -> {
            Box(
                modifier = modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF000000)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "abc",
                    color = Color.White,
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.3).sp
                )
            }
        }

        // 11. CBS News (Classic CBS Eye)
        norm.contains("cbs") -> {
            Box(
                modifier = modifier
                    .size(width = 24.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF003366)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(13.dp)) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(Color.White, radius = size.width * 0.45f, center = center)
                    drawCircle(Color(0xFF003366), radius = size.width * 0.23f, center = center)
                }
            }
        }

        // 12. Bloomberg
        norm.contains("bloom") -> {
            Box(
                modifier = modifier
                    .size(width = 22.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF1853FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "B",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 11.sp
                )
            }
        }

        // 13. NHK World-Japan (3-Egg Pods)
        norm.contains("nhk") -> {
            Row(
                modifier = modifier
                    .height(16.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF0072BC))
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("N", "H", "K").forEach { l ->
                    Box(
                        modifier = Modifier
                            .size(width = 6.dp, height = 10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = l,
                            color = Color(0xFF0072BC),
                            fontSize = 6.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 6.sp
                        )
                    }
                }
            }
        }

        // 14. France 24
        norm.contains("france") || norm.startsWith("f24") -> {
            Box(
                modifier = modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF0080C9)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "F",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 5.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 5.sp
                    )
                    Text(
                        text = "24",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 9.sp
                    )
                }
            }
        }

        // 15. RT International
        norm.startsWith("rt_") || norm == "rt" || norm.contains("rt_int") -> {
            Box(
                modifier = modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF00B020)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RT",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // 16. CGTN
        norm.contains("cgtn") -> {
            Box(
                modifier = modifier
                    .size(width = 28.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF0072C6)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CGTN",
                    color = Color.White,
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-0.4).sp
                )
            }
        }

        // 17. Fox News
        norm.contains("fox") -> {
            Box(
                modifier = modifier
                    .size(width = 26.dp, height = 18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF003366)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(1.5.dp)
                            .background(Color(0xFFCC0000))
                    )
                    Text(
                        text = "FOX",
                        color = Color.White,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 7.sp
                    )
                }
            }
        }

        // 18. MSNBC
        norm.contains("msnbc") -> {
            Box(
                modifier = modifier
                    .size(width = 30.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF001733)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "msnbc",
                    color = Color.White,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
            }
        }

        // 19. CNA (Channel NewsAsia)
        norm.contains("cna") -> {
            Box(
                modifier = modifier
                    .size(width = 24.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFC8102E)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "cna",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        // 20. Euronews
        norm.contains("euronews") -> {
            Box(
                modifier = modifier
                    .size(width = 26.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF003399)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = "euro",
                        color = Color.White,
                        fontSize = 6.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00D2C6))
                    )
                }
            }
        }

        // 21. Africanews
        norm.contains("africa") -> {
            Box(
                modifier = modifier
                    .size(width = 26.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF00A859)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "afri.",
                    color = Color.White,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // 22. NDTV
        norm.contains("ndtv") -> {
            Box(
                modifier = modifier
                    .size(width = 26.dp, height = 18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFED1C24)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NDTV",
                    color = Color.White,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 7.sp
                )
            }
        }

        // 23. TRT World
        norm.contains("trt") -> {
            Box(
                modifier = modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0072BC)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TRT",
                    color = Color.White,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // 24. UN News / UN OCHA / UNRWA
        norm.startsWith("un") || norm.contains("un_news") || norm.contains("un_ocha") || norm.contains("unrwa") -> {
            Box(
                modifier = modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF009EDB)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(Color.White, radius = size.width * 0.4f, center = center, style = Stroke(1.dp.toPx()))
                    drawLine(Color.White, Offset(center.x, 1f), Offset(center.x, size.height - 1f), strokeWidth = 0.8.dp.toPx())
                    drawLine(Color.White, Offset(1f, center.y), Offset(size.width - 1f, center.y), strokeWidth = 0.8.dp.toPx())
                }
            }
        }

        // 25. The Guardian (Navy Blue Circle 'G')
        norm.contains("guardian") -> {
            Box(
                modifier = modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF052962)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "g",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 11.sp
                )
            }
        }

        // 26. WION
        norm.contains("wion") -> {
            Box(
                modifier = modifier
                    .size(width = 26.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFFF5500)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "WION",
                    color = Color.White,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // 27. Associated Press (AP)
        norm == "ap_ap" || norm.contains("associated_press") -> {
            Box(
                modifier = modifier
                    .size(width = 22.dp, height = 18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AP",
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 8.5.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(1.5.dp)
                            .background(Color(0xFFFF0000))
                    )
                }
            }
        }

        // Default/Fallback: Brand Color Badge with Short Code
        else -> {
            Box(
                modifier = modifier
                    .size(width = 24.dp, height = 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(badgeColorHex)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = shortCode.take(3).uppercase(),
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 7.sp
                )
            }
        }
    }
}
