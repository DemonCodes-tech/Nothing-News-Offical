package com.example.data.model

data class VerificationSource(
    val name: String,
    val credibilityScore: Int, // e.g. 99 (Tier 1)
    val timestamp: String, // e.g. "12m ago"
    val corroboratingExcerpt: String,
    val tier: String = "Tier 1"
)

data class DiscrepancyClaim(
    val claimText: String,
    val isContested: Boolean, // true if variance or unsubstantiated claim
    val varianceDetails: String,
    val establishedConsensus: String
)

data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val source: String,
    val category: String,
    val timestampString: String,
    val factScore: Int,
    val primarySourcesCount: Int,
    val editorialBiasPercent: Int = 0,
    val verifiedSources: List<VerificationSource> = emptyList(),
    val flaggedDiscrepancies: List<DiscrepancyClaim> = emptyList(),
    val crossCheckSources: List<String> = emptyList(),
    val keyFacts: List<String> = emptyList(),
    val imageType: String = "NEWS", // e.g. "DESALINATION", "MEDICAL", "INFRASTRUCTURE", "SATELLITE", "MARITIME", "CLIMATE", "ENERGY", "FINANCE"
    val imageCaption: String = "Verified wire telemetry photo // Objective documentation",
    val isSaved: Boolean = false,
    val readingProgress: Float = 0f,
    val readTimeMinutes: Int = 3
)
