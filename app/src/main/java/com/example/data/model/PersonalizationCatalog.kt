package com.example.data.model

enum class FeedTab {
    MY_FEED,
    WORLD
}

enum class AlignmentMode {
    PARTIES,
    SPECTRUM,
    NON_PARTISAN
}

data class PoliticalSpectrumOption(
    val id: String,
    val label: String,
    val shortLabel: String
)

data class CountryOption(
    val code: String,
    val name: String,
    val regionLabel: String,
    val parties: List<String>
)

data class FeedTopicOption(
    val id: String,
    val label: String,
    val shortLabel: String,
    val feedCategory: String = "GLOBAL"
)

data class UserFeedProfile(
    val countryCode: String = "US",
    val alignmentMode: AlignmentMode = AlignmentMode.NON_PARTISAN,
    val selectedParties: Set<String> = emptySet(),
    val selectedSpectrum: String = "Center",
    val allSidesEnabled: Boolean = true,
    val selectedTopics: Set<String> = setOf(
        "WORLD_POLITICS",
        "TECH_SCIENCE",
        "AI_ROBOTICS",
        "SPORTS",
        "CARS_MOBILITY"
    )
)

data class BackgroundSyncPreferences(
    val workManagerEnabled: Boolean = true,
    val liveServiceEnabled: Boolean = false
)

object PersonalizationCatalog {

    val countries: List<CountryOption> = listOf(
        CountryOption(
            code = "US",
            name = "United States",
            regionLabel = "NA",
            parties = listOf("Democratic Party", "Republican Party", "Libertarian Party", "Green Party", "Independent")
        ),
        CountryOption(
            code = "UK",
            name = "United Kingdom",
            regionLabel = "EU",
            parties = listOf("Labour Party", "Conservative Party", "Liberal Democrats", "Green Party", "Reform UK", "SNP")
        ),
        CountryOption(
            code = "CA",
            name = "Canada",
            regionLabel = "NA",
            parties = listOf("Liberal Party", "Conservative Party", "NDP", "Bloc Québécois", "Green Party")
        ),
        CountryOption(
            code = "DE",
            name = "Germany",
            regionLabel = "EU",
            parties = listOf("SPD", "CDU/CSU", "Bündnis 90/Die Grünen", "FDP", "Die Linke")
        ),
        CountryOption(
            code = "FR",
            name = "France",
            regionLabel = "EU",
            parties = listOf("Renaissance", "Nouveau Front Populaire", "Les Républicains", "EELV Greens")
        ),
        CountryOption(
            code = "IN",
            name = "India",
            regionLabel = "AS",
            parties = listOf("BJP", "Indian National Congress", "AAP", "Regional Alliances")
        ),
        CountryOption(
            code = "JP",
            name = "Japan",
            regionLabel = "AS",
            parties = listOf("LDP", "Constitutional Democratic Party", "Komeito", "Nippon Ishin")
        ),
        CountryOption(
            code = "AU",
            name = "Australia",
            regionLabel = "OC",
            parties = listOf("Australian Labor", "Liberal-National Coalition", "Australian Greens", "Teal Independents")
        ),
        CountryOption(
            code = "PS",
            name = "Palestine",
            regionLabel = "ME",
            parties = listOf("Fatah", "PLO Civic Coalition", "Independent Civic Voices", "Humanitarian Desk")
        ),
        CountryOption(
            code = "BR",
            name = "Brazil",
            regionLabel = "SA",
            parties = listOf("Workers' Party (PT)", "Liberal Party (PL)", "MDB", "PSOL")
        ),
        CountryOption(
            code = "KR",
            name = "South Korea",
            regionLabel = "AS",
            parties = listOf("Democratic Party", "People Power Party", "Rebuilding Korea")
        ),
        CountryOption(
            code = "GLOBAL",
            name = "Global / Any Country",
            regionLabel = "ALL",
            parties = listOf("Progressive / Labour", "Moderate / Centrist", "Conservative / Market", "Green & Climate", "Civic Independent")
        )
    )

    val spectrumOptions: List<PoliticalSpectrumOption> = listOf(
        PoliticalSpectrumOption("Left", "Left", "LEFT"),
        PoliticalSpectrumOption("Center-Left", "Center-Left", "C-LEFT"),
        PoliticalSpectrumOption("Center", "Center", "CENTER"),
        PoliticalSpectrumOption("Center-Right", "Center-Right", "C-RIGHT"),
        PoliticalSpectrumOption("Right", "Right", "RIGHT")
    )

    val topicOptions: List<FeedTopicOption> = listOf(
        FeedTopicOption("SPORTS", "Sports", "SPORTS", "SPORTS"),
        FeedTopicOption("TECH_SCIENCE", "Tech & Gadgets", "TECH", "TECH"),
        FeedTopicOption("CARS_MOBILITY", "Cars & EVs", "CARS", "CARS"),
        FeedTopicOption("AI_ROBOTICS", "AI & Robotics", "AI", "AI"),
        FeedTopicOption("ECONOMY_FINANCE", "Economy & Markets", "ECONOMY", "ECONOMY"),
        FeedTopicOption("GAMING_MEDIA", "Gaming & Entertainment", "MEDIA", "ENTERTAINMENT"),
        FeedTopicOption("CULTURE_SPACE", "Space & Science", "SPACE", "TECH"),
        FeedTopicOption("HEALTH_MEDICINE", "Health & Medicine", "HEALTH", "HEALTH"),
        FeedTopicOption("CLIMATE_DISASTERS", "Climate & Disasters", "CLIMATE", "CLIMATE"),
        FeedTopicOption("WORLD_POLITICS", "World Politics", "POLITICS", "GLOBAL"),
        FeedTopicOption("NATIONAL_POLICY", "Domestic Policy", "POLICY", "GLOBAL"),
        FeedTopicOption("HUMANITARIAN", "Humanitarian Crisis", "HUMANITARIAN", "PALESTINE & GAZA")
    )

    fun findCountry(code: String): CountryOption {
        return countries.find { it.code.equals(code, ignoreCase = true) } ?: countries.first()
    }
}

object NewsPersonalizationEngine {

    fun curateMyFeed(
        articles: List<NewsArticle>,
        profile: UserFeedProfile,
        favoriteBroadcasters: Set<String>
    ): List<NewsArticle> {
        val country = PersonalizationCatalog.findCountry(profile.countryCode)
        val favoriteOutletCodes = favoriteBroadcasters.mapNotNull { id ->
            BroadcasterRegistry.allBroadcasters.find { it.id == id }?.shortCode
        }.toSet()

        val hasAnyCategoryMatchInPool = profile.selectedTopics.isNotEmpty() && articles.any { article ->
            article.topicTags.any { profile.selectedTopics.contains(it) }
        }

        val scored = articles.mapNotNull { article ->
            var score = 0
            val badges = mutableListOf<String>()

            // 1. Country matching
            val matchesCountryExact = profile.countryCode != "GLOBAL" &&
                article.countryTags.any { it.equals(profile.countryCode, ignoreCase = true) }
            val isGlobalWire = article.countryTags.any { it.equals("GLOBAL", ignoreCase = true) }

            if (matchesCountryExact) {
                score += 65
                badges.add("${country.code} DESK")
            } else if (profile.countryCode == "GLOBAL" || isGlobalWire) {
                score += 18
            }

            // 2. Political alignment matching (Parties / Spectrum / Non-Partisan)
            val matchesAlignment: Boolean
            var isCounterpoint = false

            when (profile.alignmentMode) {
                AlignmentMode.NON_PARTISAN -> {
                    matchesAlignment = article.politicalLeaning.equals("Non-Partisan", ignoreCase = true) ||
                        article.politicalLeaning.equals("Center", ignoreCase = true)
                    if (matchesAlignment) {
                        score += 45
                        badges.add("NON-PARTISAN")
                    } else if (profile.allSidesEnabled) {
                        isCounterpoint = true
                    }
                }
                AlignmentMode.PARTIES -> {
                    val hasSelectedParties = profile.selectedParties.isNotEmpty()
                    val directPartyMatch = hasSelectedParties && article.partyTags.any { p ->
                        profile.selectedParties.any { selected ->
                            p.contains(selected, ignoreCase = true) || selected.contains(p, ignoreCase = true)
                        }
                    }
                    val leaningFromParty = inferSpectrumFromParties(profile.selectedParties)
                    val matchesInferredSpectrum = leaningFromParty.isNotEmpty() &&
                        leaningFromParty.any { it.equals(article.politicalLeaning, ignoreCase = true) }

                    when {
                        directPartyMatch -> {
                            score += 75
                            badges.add("PARTY MATCH")
                            matchesAlignment = true
                        }
                        matchesInferredSpectrum -> {
                            score += 45
                            badges.add(article.politicalLeaning.uppercase())
                            matchesAlignment = true
                        }
                        article.politicalLeaning.equals("Non-Partisan", ignoreCase = true) -> {
                            score += 24
                            matchesAlignment = !hasSelectedParties
                        }
                        profile.allSidesEnabled -> {
                            isCounterpoint = true
                            matchesAlignment = false
                        }
                        else -> {
                            matchesAlignment = false
                        }
                    }
                }
                AlignmentMode.SPECTRUM -> {
                    val exactSpectrum = article.politicalLeaning.equals(profile.selectedSpectrum, ignoreCase = true)
                    val neutralBridge = article.politicalLeaning.equals("Non-Partisan", ignoreCase = true)
                    when {
                        exactSpectrum -> {
                            score += 65
                            badges.add(profile.selectedSpectrum.uppercase())
                            matchesAlignment = true
                        }
                        neutralBridge -> {
                            score += 25
                            matchesAlignment = true
                        }
                        profile.allSidesEnabled -> {
                            isCounterpoint = true
                            matchesAlignment = false
                        }
                        else -> {
                            matchesAlignment = false
                        }
                    }
                }
            }

            if (isCounterpoint) {
                score += 22
                badges.add("ALL-SIDES")
            }

            // 3. Chosen News Categories matching
            val matchingTopics = article.topicTags.filter { profile.selectedTopics.contains(it) }
            if (matchingTopics.isNotEmpty()) {
                score += 70 + (matchingTopics.size - 1) * 20
                val firstTopicLabel = PersonalizationCatalog.topicOptions
                    .find { it.id == matchingTopics.first() }?.shortLabel ?: "TOPIC"
                badges.add(firstTopicLabel)
            } else if (profile.selectedTopics.isEmpty()) {
                score += 12
            }

            // 4. Pinned Broadcaster boost
            val matchesPinnedOutlet = article.outletCode != null &&
                favoriteOutletCodes.any { it.equals(article.outletCode, ignoreCase = true) }
            if (matchesPinnedOutlet) {
                score += 20
                badges.add("PINNED")
            }

            val passesCategoryGate = !hasAnyCategoryMatchInPool || matchingTopics.isNotEmpty()
            val shouldInclude = passesCategoryGate && when {
                profile.allSidesEnabled -> score >= 20 || matchesCountryExact || matchingTopics.isNotEmpty() || isGlobalWire
                else -> matchesCountryExact || matchesAlignment || matchingTopics.isNotEmpty() || matchesPinnedOutlet
            }

            if (!shouldInclude) return@mapNotNull null

            val annotated = article.copy(
                matchBadges = badges.distinct().take(3)
            )
            annotated to score
        }

        val sorted = scored
            .sortedWith(
                compareByDescending<Pair<NewsArticle, Int>> { it.second }
                    .thenByDescending { it.first.factScore }
            )
            .map { it.first }

        return if (sorted.isNotEmpty()) {
            sorted
        } else {
            articles.take(6).map { it.copy(matchBadges = listOf("GLOBAL WIRE")) }
        }
    }

    private fun inferSpectrumFromParties(selectedParties: Set<String>): Set<String> {
        val result = mutableSetOf<String>()
        selectedParties.forEach { party ->
            val p = party.lowercase()
            when {
                p.contains("green") || p.contains("linke") || p.contains("psol") || p.contains("front populaire") -> {
                    result.add("Left")
                    result.add("Center-Left")
                }
                p.contains("democratic") || p.contains("labour") || p.contains("labor") || p.contains("ndp") ||
                    p.contains("spd") || p.contains("congress") || p.contains("workers") || p.contains("progressive") -> {
                    result.add("Center-Left")
                }
                p.contains("liberal dem") || p.contains("renaissance") || p.contains("komeito") ||
                    p.contains("teal") || p.contains("fdp") || p.contains("mdb") || p.contains("aap") || p.contains("centrist") -> {
                    result.add("Center")
                }
                p.contains("republican") || p.contains("conservative") || p.contains("cdu") || p.contains("ldp") ||
                    p.contains("bjp") || p.contains("people power") || p.contains("républicains") -> {
                    result.add("Center-Right")
                    result.add("Right")
                }
                p.contains("libertarian") || p.contains("reform") -> {
                    result.add("Right")
                }
                else -> result.add("Non-Partisan")
            }
        }
        return result
    }
}
