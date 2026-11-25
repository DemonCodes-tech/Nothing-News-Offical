package com.example.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

const val NEWSTHING_GITHUB_URL = "https://github.com/DemonCodes-tech/Newsthing"
const val CURRENT_RELEASE_VERSION = "v2.2.0"

data class AppReleaseNote(
    val version: String,
    val title: String,
    val summary: String,
    val isCurrent: Boolean = false,
    val oldHighlights: List<String> = emptyList(),
    val newHighlights: List<String> = emptyList()
)

val NEWSTHING_RELEASE_LOG = listOf(
    AppReleaseNote(
        version = CURRENT_RELEASE_VERSION,
        title = "Easier Reading, Playful Motion & Design Fixes",
        summary = "Clearer text in Nothing OS Light Mode, bigger text up to 150%, floating morphing shapes, pulsing red dots, gentle touch vibrations, squiggling reading bars, and faster news loading.",
        isCurrent = true,
        oldHighlights = listOf(
            "Where It Started (Nothing News v1.0.0): Launched as a clean black-and-white news reader inspired by Nothing OS, with 3-source fact checking, red alert accents, and an offline Saved Library.",
            "What Felt Rough Before (v1.0.0 -> v2.1.0): Stories loaded slowly one by one, words could disappear on Nothing Light Mode buttons, app text size couldn't scale for easier reading, backgrounds stayed still, and reading bars were plain straight lines."
        ),
        newHighlights = listOf(
            "Clear Words in Light Mode: Fixed a color bug in Nothing OS Light Mode so selected buttons and cards never hide text.",
            "Bigger App Text (Up to 150%): Added an easy text-size slider in Settings and the setup guide to make every word in the app larger and easier to read.",
            "Floating Material You Shapes: Added colorful background shapes and subtle corner accents that float around the screen and smoothly shift between 9 playful forms.",
            "Interactive Touch & Bounce: Tap or drag any floating shape to feel a gentle vibration, watch it change shape right away, and see it bounce away.",
            "Pulsing Red Dots in Nothing OS: Upgraded the Nothing OS background with glowing red dot waves that pulse gently and ripple wherever you touch.",
            "Gentle Vibrations With a Switch: Added soft touch vibrations across buttons, switches, shapes, and dots, plus a Settings switch to turn them off anytime.",
            "Squiggling & Dot Progress Bars: Replaced straight reading bars with a wavy squiggling line in Material You and black-to-red dots in Nothing OS across articles, saved stories, and setup.",
            "Faster & Cleaner Live News: News feeds now load together at once for quicker updates, with messy web symbols cleaned up automatically.",
            "More Alert Controls & Reader Zoom: Added all 5 breaking-news alert switches in Settings along with quick A- / Aa / A+ text sizing inside articles.",
            "Saved Search & Quick Reset Buttons: Added instant search and a safe Clear All option in your Saved Library, plus one-tap X buttons to clear searches and filters."
        )
    ),
    AppReleaseNote(
        version = "v2.1.0",
        title = "The Complete Evolution: From Nothing News to NewsThing",
        summary = "Contributors of NewsThing (u/Fall_Spezialgelagert), real Android device notifications (POST_NOTIFICATIONS), Marin's zero-data pledge, and complete GitHub release history.",
        isCurrent = false,
        oldHighlights = listOf(
            "Original Identity (Nothing News v1.0.0): Launched as a stark OLED black monochrome (#0D0D0D) wire reader inspired by Nothing OS.",
            "Original Limitations: Had only a small static wire feed, simulated notification toggles without system alerts, and no contributor or GitHub update hub."
        ),
        newHighlights = listOf(
            "Rebrand & Contributors of NewsThing: Renamed from Nothing News to NewsThing thanks to u/Fall_Spezialgelagert (\"The Name Giver\" on Reddit), featured alongside Marin saying \"Thanks Pookies!\".",
            "Live Dual-OS Engine, 100+ Broadcasters, Real Device Notifications (POST_NOTIFICATIONS), and Zero-Telemetry Privacy Promise."
        )
    ),
    AppReleaseNote(
        version = "v2.0.0",
        title = "NewsThing Rebrand, Marin Guide & GitHub Updates",
        summary = "Launched NewsThing branding, interactive onboarding with Marin Kitagawa, theme-adaptive B&W Nothing OS portraits, Settings Update Log, and direct GitHub updates."
    ),
    AppReleaseNote(
        version = "v1.5.0",
        title = "100+ Global Broadcasters & Channel Logos",
        summary = "Added custom badges for CNN, BBC, Al Jazeera, DW, Reuters, NHK, and 100+ verified channels plus pinned favorite carousel sorting."
    ),
    AppReleaseNote(
        version = "v1.2.0",
        title = "Dual-OS Engine & Adaptive Layout",
        summary = "Real-time switching between OLED Nothing OS Space Mono monochrome and Material You dynamic color with foldable and tablet support."
    ),
    AppReleaseNote(
        version = "v1.0.0",
        title = "Original \"Nothing News\" Launch",
        summary = "Where it all began: originally titled Nothing News with the 3-Source Fact Audit protocol, Signal Red variance radar, offline Saved Library, and OLED dot-matrix design."
    )
)

/**
 * Pure achromatic contrast ColorMatrix for Marin portraits when Nothing OS mode is active.
 * Keeps monochrome balanced without color tinting; returns null in Material You mode.
 */
fun marinColorFilter(isNothing: Boolean): ColorFilter? {
    if (!isNothing) return null
    val c = 1.15f
    val t = (-0.5f * c + 0.5f) * 255f
    val rW = 0.2126f * c
    val gW = 0.7152f * c
    val bW = 0.0722f * c
    val monoColorMatrix = ColorMatrix(
        floatArrayOf(
            rW, gW, bW, 0f, t,
            rW, gW, bW, 0f, t,
            rW, gW, bW, 0f, t,
            0f, 0f, 0f, 1f, 0f
        )
    )
    return ColorFilter.colorMatrix(monoColorMatrix)
}

fun openGitHubRepository(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(NEWSTHING_GITHUB_URL)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "No browser app found to open $NEWSTHING_GITHUB_URL",
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun openRedditProfile(context: Context, username: String) {
    val url = "https://www.reddit.com/user/$username/"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "No browser app found to open $url",
            Toast.LENGTH_SHORT
        ).show()
    }
}
