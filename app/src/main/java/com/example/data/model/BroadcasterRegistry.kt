package com.example.data.model

data class Broadcaster(
    val id: String,
    val name: String,
    val shortCode: String,
    val region: String,
    val country: String,
    val language: String = "English",
    val badgeColorHex: Long = 0xFFD71921,
    val feedUrl: String? = null
)

object BroadcasterRegistry {

    val allBroadcasters: List<Broadcaster> = listOf(
        // A
        Broadcaster("abc_news", "ABC News", "ABC", "Americas", "United States", "English", 0xFF000000, "https://abcnews.go.com/abcnews/internationalheadlines"),
        Broadcaster("abc_australia", "ABC News (Australia)", "ABC-AU", "Asia-Pacific", "Australia", "English", 0xFF003865),
        Broadcaster("africanews", "Africanews", "AFR-N", "Africa", "Pan-African", "English", 0xFF00A859),
        Broadcaster("africa_24", "Africa 24", "AFR-24", "Africa", "Pan-African", "French", 0xFF007A33),
        Broadcaster("aaj_tak", "Aaj Tak", "AAJ-TAK", "South Asia", "India", "Hindi", 0xFFE51D24),
        Broadcaster("al_jazeera_en", "Al Jazeera English", "AJE", "Middle East", "Qatar", "English", 0xFFE25A00, "https://www.aljazeera.com/xml/rss/all.xml"),
        Broadcaster("al_jazeera_ar", "Al Jazeera Arabic", "AJA", "Middle East", "Qatar", "Arabic", 0xFFE25A00),
        Broadcaster("al_arabiya", "Al Arabiya", "ARABIYA", "Middle East", "UAE/Saudi", "Arabic", 0xFF8A1538),
        Broadcaster("al_hadath", "Al Hadath", "HADATH", "Middle East", "UAE", "Arabic", 0xFFB22222),
        Broadcaster("alhurra", "Alhurra", "ALHURRA", "Middle East", "United States", "Arabic", 0xFF1C3F94),
        Broadcaster("arirang_tv", "Arirang TV", "ARIRANG", "Asia-Pacific", "South Korea", "English", 0xFF005BAA),
        Broadcaster("arte", "Arte", "ARTE", "Europe", "Franco-German", "French/German", 0xFFFF5500),

        // B
        Broadcaster("bbc_news", "BBC News", "BBC", "Europe", "United Kingdom", "English", 0xFFBB1919, "https://feeds.bbci.co.uk/news/world/rss.xml"),
        Broadcaster("bloomberg", "Bloomberg Television", "BLOOM", "Americas", "United States", "English", 0xFF1853FF, "https://feeds.bloomberg.com/markets/news.rss"),
        Broadcaster("bandnews", "BandNews TV", "BAND", "Americas", "Brazil", "Portuguese", 0xFF006633),
        Broadcaster("belsat_tv", "Belsat TV", "BELSAT", "Europe", "Poland/Belarus", "Belarusian", 0xFF0044AA),

        // C
        Broadcaster("cbc_news", "CBC News", "CBC", "Americas", "Canada", "English", 0xFFE0001B, "https://www.cbc.ca/cmlink/rss-world"),
        Broadcaster("cbs_news", "CBS News", "CBS", "Americas", "United States", "English", 0xFF003366),
        Broadcaster("cgtn", "CGTN", "CGTN", "Asia-Pacific", "China", "English", 0xFF0072C6),
        Broadcaster("cna", "CNA (Channel NewsAsia)", "CNA", "Asia-Pacific", "Singapore", "English", 0xFFC8102E, "https://www.channelnewsasia.com/api/v1/rss-outbound-feed?_format=xml"),
        Broadcaster("cnbc", "CNBC", "CNBC", "Americas", "United States", "English", 0xFF002F6C, "https://search.cnbc.com/rs/search/view.html?partnerId=2000&keywords=world&sort=date"),
        Broadcaster("cnn_int", "CNN International", "CNN", "Americas", "United States", "English", 0xFFCC0000, "http://rss.cnn.com/rss/edition_world.rss"),
        Broadcaster("cnn_brasil", "CNN Brazil", "CNN-BR", "Americas", "Brazil", "Portuguese", 0xFFCC0000),
        Broadcaster("ctv_news", "CTV News", "CTV", "Americas", "Canada", "English", 0xFF002D62),

        // D
        Broadcaster("dw", "DW (Deutsche Welle)", "DW", "Europe", "Germany", "English", 0xFF002D5A, "https://rss.dw.com/rdf/rss-en-world"),
        Broadcaster("dd_india", "DD India", "DD-IND", "South Asia", "India", "English", 0xFFFF6F00),

        // E
        Broadcaster("euronews", "Euronews", "EURONEWS", "Europe", "European Union", "English", 0xFF00529C, "https://www.euronews.com/rss?level=theme&name=news"),
        Broadcaster("enca", "eNCA", "ENCA", "Africa", "South Africa", "English", 0xFFED1C24),

        // F
        Broadcaster("fox_news", "Fox News", "FOX", "Americas", "United States", "English", 0xFF003366),
        Broadcaster("france_24", "France 24", "F24", "Europe", "France", "English", 0xFF0080C9, "https://www.france24.com/en/rss"),

        // G
        Broadcaster("globo_news", "GloboNews", "GLOBO", "Americas", "Brazil", "Portuguese", 0xFF005696),
        Broadcaster("global_news", "Global News", "GLOBAL", "Americas", "Canada", "English", 0xFFE31B23),
        Broadcaster("the_guardian", "The Guardian", "GUARDIAN", "Europe", "United Kingdom", "English", 0xFF052962, "https://www.theguardian.com/world/rss"),

        // I
        Broadcaster("india_today", "India Today", "IND-TDY", "South Asia", "India", "English", 0xFFD8232A),
        Broadcaster("i24news", "i24NEWS", "i24", "Middle East", "Israel", "English", 0xFF003366),

        // K
        Broadcaster("kbs_world", "KBS World", "KBS", "Asia-Pacific", "South Korea", "Korean/English", 0xFF004098),

        // M
        Broadcaster("msnbc", "MSNBC", "MSNBC", "Americas", "United States", "English", 0xFF003366),

        // N
        Broadcaster("nbc_news", "NBC News", "NBC", "Americas", "United States", "English", 0xFF002244, "https://feeds.nbcnews.com/nbcnews/public/world"),
        Broadcaster("ndtv", "NDTV 24x7", "NDTV", "South Asia", "India", "English", 0xFFED1C24, "https://feeds.feedburner.com/ndtvnews-world-news"),
        Broadcaster("nhk_world", "NHK World-Japan", "NHK", "Asia-Pacific", "Japan", "English", 0xFF0072BC, "https://www3.nhk.or.jp/nhkworld/en/news/rss/index.xml"),
        Broadcaster("newzroom_afrika", "Newzroom Afrika", "NEWZ-AFR", "Africa", "South Africa", "English", 0xFF333333),

        // P
        Broadcaster("press_tv", "Press TV", "PRESS-TV", "Middle East", "Iran", "English", 0xFF004B87),

        // R
        Broadcaster("reuters", "Reuters", "REUTERS", "Global", "United Kingdom / Global", "English", 0xFFFF8000, "https://www.reutersagency.com/feed/?best-topics=world&post_type=best"),
        Broadcaster("rt_int", "RT International", "RT", "Europe", "Russia", "English", 0xFF009900),

        // S
        Broadcaster("sabc_news", "SABC News", "SABC", "Africa", "South Africa", "English", 0xFFFFD100),
        Broadcaster("sky_news", "Sky News", "SKY", "Europe", "United Kingdom", "English", 0xFFFF0000, "https://feeds.skynews.com/feeds/rss/world.xml"),
        Broadcaster("sky_news_arabia", "Sky News Arabia", "SKY-AR", "Middle East", "UAE", "Arabic", 0xFFCC0000),
        Broadcaster("sbs_world", "SBS World News", "SBS", "Asia-Pacific", "Australia", "English", 0xFFFF5500),

        // T
        Broadcaster("taiwan_plus", "TaiwanPlus", "TW-PLUS", "Asia-Pacific", "Taiwan", "English", 0xFF00C7B1),
        Broadcaster("telesur", "Telesur", "TELESUR", "Americas", "Pan-Latin", "Spanish", 0xFFE20613),
        Broadcaster("tagesschau", "Tagesschau", "TAGESSCHAU", "Europe", "Germany", "German", 0xFF003366),
        Broadcaster("times_now", "Times Now", "TIMES-NOW", "South Asia", "India", "English", 0xFFED1C24),
        Broadcaster("trt_world", "TRT World", "TRT", "Middle East", "Turkey", "English", 0xFF0072BC, "https://www.trtworld.com/rss/world.rss"),
        Broadcaster("tv5monde", "TV5Monde", "TV5", "Europe", "France", "French", 0xFF003865),
        Broadcaster("tve_int", "TVE Internacional", "TVE", "Europe", "Spain", "Spanish", 0xFF002F6C),

        // U
        Broadcaster("un_news", "UN News Global Wire", "UN", "Global", "United Nations", "English", 0xFF009EDB, "https://news.un.org/feed/subscribe/en/news/all/rss.xml"),

        // W
        Broadcaster("wion", "WION", "WION", "South Asia", "India", "English", 0xFFFF5500),
        Broadcaster("welt", "Welt", "WELT", "Europe", "Germany", "German", 0xFF002244),

        // Y
        Broadcaster("ytn", "YTN", "YTN", "Asia-Pacific", "South Korea", "Korean", 0xFF003399)
    ).sortedBy { it.name }

    val regions: List<String> = listOf("ALL OUTLETS", "GLOBAL", "AMERICAS", "EUROPE", "MIDDLE EAST", "ASIA-PACIFIC", "SOUTH ASIA", "AFRICA")

    fun findBroadcaster(nameOrCode: String): Broadcaster? {
        return allBroadcasters.firstOrNull {
            it.name.contains(nameOrCode, ignoreCase = true) ||
            it.shortCode.equals(nameOrCode, ignoreCase = true) ||
            it.id.equals(nameOrCode, ignoreCase = true)
        }
    }
}
