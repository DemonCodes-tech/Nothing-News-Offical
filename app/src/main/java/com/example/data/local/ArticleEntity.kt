package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.DiscrepancyClaim
import com.example.data.model.NewsArticle
import com.example.data.model.VerificationSource

@Entity(tableName = "saved_articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val source: String,
    val category: String,
    val timestampString: String,
    val factScore: Int,
    val primarySourcesCount: Int,
    val editorialBiasPercent: Int,
    val crossCheckSourcesJoined: String,
    val keyFactsJoined: String,
    val verifiedSourcesSerialized: String,
    val discrepanciesSerialized: String,
    val imageType: String,
    val imageCaption: String,
    val readingProgress: Float,
    val readTimeMinutes: Int,
    val savedAtTimestamp: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): NewsArticle {
        val sourcesList = if (verifiedSourcesSerialized.isBlank()) {
            emptyList()
        } else {
            verifiedSourcesSerialized.split("###").mapNotNull { entry ->
                val parts = entry.split(":::")
                if (parts.size >= 4) {
                    VerificationSource(
                        name = parts[0],
                        credibilityScore = parts[1].toIntOrNull() ?: 98,
                        timestamp = parts[2],
                        corroboratingExcerpt = parts[3],
                        tier = parts.getOrNull(4) ?: "Tier 1"
                    )
                } else null
            }
        }

        val discrepanciesList = if (discrepanciesSerialized.isBlank()) {
            emptyList()
        } else {
            discrepanciesSerialized.split("###").mapNotNull { entry ->
                val parts = entry.split(":::")
                if (parts.size >= 4) {
                    DiscrepancyClaim(
                        claimText = parts[0],
                        isContested = parts[1].toBooleanStrictOrNull() ?: false,
                        varianceDetails = parts[2],
                        establishedConsensus = parts[3]
                    )
                } else null
            }
        }

        return NewsArticle(
            id = id,
            title = title,
            summary = summary,
            content = content,
            source = source,
            category = category,
            timestampString = timestampString,
            factScore = factScore,
            primarySourcesCount = primarySourcesCount,
            editorialBiasPercent = editorialBiasPercent,
            verifiedSources = sourcesList,
            flaggedDiscrepancies = discrepanciesList,
            crossCheckSources = if (crossCheckSourcesJoined.isBlank()) emptyList() else crossCheckSourcesJoined.split("||"),
            keyFacts = if (keyFactsJoined.isBlank()) emptyList() else keyFactsJoined.split("||"),
            imageType = imageType,
            imageCaption = imageCaption,
            isSaved = true,
            readingProgress = readingProgress,
            readTimeMinutes = readTimeMinutes
        )
    }

    companion object {
        fun fromDomainModel(article: NewsArticle): ArticleEntity {
            val serializedSources = article.verifiedSources.joinToString("###") { s ->
                "${s.name}:::${s.credibilityScore}:::${s.timestamp}:::${s.corroboratingExcerpt}:::${s.tier}"
            }
            val serializedDiscrepancies = article.flaggedDiscrepancies.joinToString("###") { d ->
                "${d.claimText}:::${d.isContested}:::${d.varianceDetails}:::${d.establishedConsensus}"
            }

            return ArticleEntity(
                id = article.id,
                title = article.title,
                summary = article.summary,
                content = article.content,
                source = article.source,
                category = article.category,
                timestampString = article.timestampString,
                factScore = article.factScore,
                primarySourcesCount = article.primarySourcesCount,
                editorialBiasPercent = article.editorialBiasPercent,
                crossCheckSourcesJoined = article.crossCheckSources.joinToString("||"),
                keyFactsJoined = article.keyFacts.joinToString("||"),
                verifiedSourcesSerialized = serializedSources,
                discrepanciesSerialized = serializedDiscrepancies,
                imageType = article.imageType,
                imageCaption = article.imageCaption,
                readingProgress = article.readingProgress,
                readTimeMinutes = article.readTimeMinutes
            )
        }
    }
}
