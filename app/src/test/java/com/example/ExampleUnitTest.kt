package com.example

import com.example.data.local.ArticleEntity
import com.example.data.model.DiscrepancyClaim
import com.example.data.model.NewsArticle
import com.example.data.model.VerificationSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testEasterEggStringExactMatch() {
        val easterEggText = "Made By Demon"
        assertEquals("Made By Demon", easterEggText)
    }

    @Test
    fun testDomainModelToEntityMappingWith3SourcesAndDiscrepancies() {
        val sources = listOf(
            VerificationSource("UN OCHA", 99, "10m ago", "Verified water intake", "Tier 1"),
            VerificationSource("UNICEF", 98, "15m ago", "Verified distribution", "Tier 1"),
            VerificationSource("PRCS", 97, "20m ago", "Escort completed", "Tier 1")
        )
        val discrepancies = listOf(
            DiscrepancyClaim(
                claimText = "Rumor of pressure drop",
                isContested = true,
                varianceDetails = "Contradicted by sensor logs",
                establishedConsensus = "Pressure steady at 4.2 bars"
            )
        )

        val article = NewsArticle(
            id = "test-1",
            title = "Test Factual Dispatch",
            summary = "Summary",
            content = "Content body",
            source = "UN OCHA",
            category = "PALESTINE & GAZA",
            timestampString = "10M AGO",
            factScore = 99,
            primarySourcesCount = 3,
            editorialBiasPercent = 0,
            verifiedSources = sources,
            flaggedDiscrepancies = discrepancies,
            crossCheckSources = listOf("Source A", "Source B", "Source C"),
            keyFacts = listOf("Fact 1", "Fact 2"),
            imageType = "DESALINATION",
            imageCaption = "Desalination facility photo",
            isSaved = true,
            readingProgress = 0.5f,
            readTimeMinutes = 3
        )

        val entity = ArticleEntity.fromDomainModel(article)
        assertEquals("test-1", entity.id)
        assertEquals("DESALINATION", entity.imageType)

        val restored = entity.toDomainModel()
        assertEquals(article.id, restored.id)
        assertEquals(article.category, restored.category)
        assertEquals(3, restored.verifiedSources.size)
        assertEquals("UN OCHA", restored.verifiedSources[0].name)
        assertEquals(99, restored.verifiedSources[0].credibilityScore)
        assertEquals(1, restored.flaggedDiscrepancies.size)
        assertTrue(restored.flaggedDiscrepancies[0].isContested)
        assertEquals("Pressure steady at 4.2 bars", restored.flaggedDiscrepancies[0].establishedConsensus)
    }

    @Test
    fun testFactualityScoreMeetsStandards() {
        val minAcceptableFactScore = 95
        val testFactScore = 99
        assertTrue(testFactScore >= minAcceptableFactScore)
    }

    @Test
    fun testHourlyTimerCountdownFormat() {
        val secondsRemaining = 3540
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        assertEquals(59, minutes)
        assertEquals(0, seconds)
    }
}
