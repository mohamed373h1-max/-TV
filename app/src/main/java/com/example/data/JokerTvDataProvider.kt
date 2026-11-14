package com.example.data

import com.example.model.CartoonItem
import com.example.model.EgyptianMovie
import com.example.model.Episode
import com.example.model.FootballMatch
import com.example.model.MediaType
import com.example.model.RamadanSeries
import com.example.model.ServerInfo
import com.example.model.SportsChannel
import com.example.model.UserComment

object JokerTvDataProvider {

  // Verified live HTTP 200 public streams (tested HLS manifests + open MP4 feeds) + local offline backup
  const val STREAM_LOCAL_RAW = "LOCAL_RAW"
  const val STREAM_LIVE_SPORTS_HLS = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8"
  const val STREAM_LIVE_ARABIC_HLS = "https://live-hls-web-aja.getaj.net/AJA/index.m3u8"
  const val STREAM_ACTION_FILM_HLS = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"
  const val STREAM_CINEMA_REEL_HLS = "https://cdn.theoplayer.com/video/star_wars_episode_vii-the_force_awakens_official_comic-con_2015_reel_(2015)/index.m3u8"
  const val STREAM_JW_HLS = "https://cdn.jwplayer.com/manifests/pZxWPRg4.m3u8"
  const val STREAM_REMOTE_OCEANS = "https://vjs.zencdn.net/v/oceans.mp4"
  const val STREAM_REMOTE_SINTEL = "https://media.w3.org/2010/05/sintel/trailer.mp4"
  const val STREAM_REMOTE_SINTEL_480 = "https://download.blender.org/durian/trailer/sintel_trailer-480p.mp4"
  const val STREAM_REMOTE_OPEN_MP4 = "https://media.w3.org/2010/05/video/movie_300.mp4"

  private val sportsStreams = listOf(
    STREAM_LIVE_SPORTS_HLS,
    STREAM_LIVE_ARABIC_HLS,
    STREAM_ACTION_FILM_HLS,
    STREAM_REMOTE_OCEANS
  )

  private val seriesStreams = listOf(
    STREAM_ACTION_FILM_HLS,
    STREAM_CINEMA_REEL_HLS,
    STREAM_LIVE_ARABIC_HLS,
    STREAM_JW_HLS,
    STREAM_REMOTE_OPEN_MP4
  )

  private val movieStreams = listOf(
    STREAM_CINEMA_REEL_HLS,
    STREAM_ACTION_FILM_HLS,
    STREAM_JW_HLS,
    STREAM_REMOTE_OCEANS,
    STREAM_REMOTE_OPEN_MP4
  )

  private val cartoonStreams = listOf(
    STREAM_REMOTE_SINTEL,
    STREAM_REMOTE_SINTEL_480,
    STREAM_JW_HLS,
    STREAM_ACTION_FILM_HLS
  )

  fun createDefaultServers(
    contentId: String,
    streamIndexOffset: Int = 0,
    mediaType: MediaType = MediaType.CHANNEL
  ): List<ServerInfo> {
    val pool = when (mediaType) {
      MediaType.CHANNEL, MediaType.MATCH -> sportsStreams
      MediaType.SERIES -> seriesStreams
      MediaType.MOVIE -> movieStreams
      MediaType.CARTOON -> cartoonStreams
    }

    val safeOffset = ((streamIndexOffset % pool.size) + pool.size) % pool.size
    val url1 = pool[safeOffset % pool.size]
    val url2 = pool[(safeOffset + 1) % pool.size]
    val url3 = pool[(safeOffset + 2) % pool.size]

    return listOf(
      ServerInfo(
        id = "${contentId}_srv1",
        name = "سيرفر جوكر 1 (FHD - بث مباشر HLS)",
        quality = "FHD 1080p",
        streamUrl = url1,
        pingMs = 18,
        isVip = true,
        isOnline = true
      ),
      ServerInfo(
        id = "${contentId}_srv2",
        name = "سيرفر جوكر 2 (HD - احتياطي سريع)",
        quality = "HD 720p",
        streamUrl = url2,
        pingMs = 24,
        isVip = false,
        isOnline = true
      ),
      ServerInfo(
        id = "${contentId}_srv3",
        name = "سيرفر جوكر 3 (SD - للنت الضعيف)",
        quality = "SD 480p",
        streamUrl = url3,
        pingMs = 31,
        isVip = false,
        isOnline = true
      ),
      ServerInfo(
        id = "${contentId}_srv4",
        name = "سيرفر أوفلاين احتياطي (فيديو تجريبي محلي)",
        quality = "تجريبي محلي",
        streamUrl = STREAM_LOCAL_RAW,
        pingMs = 5,
        isVip = false,
        isOnline = false
      )
    )
  }

  val sportsChannels: List<SportsChannel> = listOf(
    SportsChannel(
      id = "bein_1",
      name = "beIN SPORTS 1 HD",
      category = "بي إن سبورت",
      logoUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=400&auto=format&fit=crop&q=80",
      isLive = true,
      currentEvent = "قمة الدوري الإنجليزي: مانشستر سيتي ضد آرسنال",
      qualityBadge = "4K VIP",
      servers = createDefaultServers("bein_1", 0)
    ),
    SportsChannel(
      id = "bein_2",
      name = "beIN SPORTS 2 HD",
      category = "بي إن سبورت",
      logoUrl = "https://images.unsplash.com/photo-1522778119026-d647f0596c20?w=400&auto=format&fit=crop&q=80",
      isLive = true,
      currentEvent = "الدوري الإسباني: ريال مدريد ضد أتلتيكو مدريد",
      qualityBadge = "FHD",
      servers = createDefaultServers("bein_2", 1)
    ),
    SportsChannel(
      id = "bein_3",
      name = "beIN SPORTS 3 HD",
      category = "بي إن سبورت",
      logoUrl = "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=400&auto=format&fit=crop&q=80",
      isLive = true,
      currentEvent = "دوري أبطال أوروبا: باريس سان جيرمان ضد بايرن ميونخ",
      qualityBadge = "FHD",
      servers = createDefaultServers("bein_3", 2)
    ),
    SportsChannel(
      id = "bein_4",
      name = "beIN SPORTS 4 HD",
      category = "بي إن سبورت",
      logoUrl = "https://images.unsplash.com/photo-1489944445391-11dd35574549?w=400&auto=format&fit=crop&q=80",
      isLive = true,
      currentEvent = "الدوري الفرنسي: مارسيليا ضد موناكو",
      qualityBadge = "HD",
      servers = createDefaultServers("bein_4", 3)
    ),
    SportsChannel(
      id = "bein_5",
      name = "beIN SPORTS 5 HD",
      category = "بي إن سبورت",
      logoUrl = "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=400&auto=format&fit=crop&q=80",
      isLive = true,
      currentEvent = "الدوري الإيطالي: يوفنتوس ضد إنتر ميلان",
      qualityBadge = "FHD",
      servers = createDefaultServers("bein_5", 4)
    ),
    SportsChannel(
      id = "bein_6",
      name = "beIN SPORTS 6 HD",
      category = "بي إن سبورت",
      logoUrl = "https://images.unsplash.com/photo-1560272564-c83b66b1ad12?w=400&auto=format&fit=crop&q=80",
      isLive = false,
      currentEvent = "الدوري الألماني: بروسيا دورتموند ضد لايبزيج",
      qualityBadge = "HD",
      servers = createDefaultServers("bein_6", 5)
    ),
    SportsChannel(
      id = "bein_7",
      name = "beIN SPORTS 7 HD",
      category = "بي إن سبورت",
      logoUrl = "https://images.unsplash.com/photo-1518091043644-c1d4457512c6?w=400&auto=format&fit=crop&q=80",
      isLive = true,
      currentEvent = "دوري أبطال أفريقيا: الأهلي ضد الترجي التونسي",
      qualityBadge = "FHD",
      servers = createDefaultServers("bein_7", 6)
    ),
    SportsChannel(
      id = "bein_8",
      name = "beIN SPORTS 8 HD",
      category = "بي إن سبورت",
      logoUrl = "https://images.unsplash.com/photo-1551958219-acbc608c6377?w=400&auto=format&fit=crop&q=80",
      isLive = false,
      currentEvent = "بطولة التنس الكبرى: نهائي ويمبلدون",
      qualityBadge = "HD",
      servers = createDefaultServers("bein_8", 7)
    ),
    SportsChannel(
      id = "bein_9",
      name = "beIN SPORTS 9 HD",
      category = "بي إن سبورت",
      logoUrl = "https://images.unsplash.com/photo-1504450758481-7338eba7524a?w=400&auto=format&fit=crop&q=80",
      isLive = false,
      currentEvent = "سباقات فورمولا 1: جائزة موناكو الكبرى",
      qualityBadge = "FHD",
      servers = createDefaultServers("bein_9", 0)
    ),
    SportsChannel(
      id = "bein_afc",
      name = "beIN SPORTS AFC 1",
      category = "بي إن سبورت",
      logoUrl = "https://images.unsplash.com/photo-1431324155629-1a6deb1dec8d?w=400&auto=format&fit=crop&q=80",
      isLive = true,
      currentEvent = "دوري أبطال آسيا: الهلال السعودي ضد العين الإماراتي",
      qualityBadge = "FHD",
      servers = createDefaultServers("bein_afc", 1)
    ),
    SportsChannel(
      id = "ontime_1",
      name = "ON Time Sports 1 HD",
      category = "أون تايم سبورت",
      logoUrl = "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=400&auto=format&fit=crop&q=80",
      isLive = true,
      currentEvent = "الدوري المصري الممتاز: الأهلي ضد الزمالك (كلاسيكو العرب)",
      qualityBadge = "FHD",
      servers = createDefaultServers("ontime_1", 2)
    ),
    SportsChannel(
      id = "ontime_2",
      name = "ON Time Sports 2 HD",
      category = "أون تايم سبورت",
      logoUrl = "https://images.unsplash.com/photo-1459865264687-595d652de67e?w=400&auto=format&fit=crop&q=80",
      isLive = true,
      currentEvent = "كأس مصر: بيراميدز ضد الإسماعيلي",
      qualityBadge = "HD",
      servers = createDefaultServers("ontime_2", 3)
    ),
    SportsChannel(
      id = "ssc_1",
      name = "SSC Sports 1 HD",
      category = "قنوات SSC",
      logoUrl = "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?w=400&auto=format&fit=crop&q=80",
      isLive = true,
      currentEvent = "دوري روشن السعودي: النصر ضد الاتحاد",
      qualityBadge = "FHD",
      servers = createDefaultServers("ssc_1", 4)
    ),
    SportsChannel(
      id = "ad_sports_1",
      name = "أبو ظبي الرياضية 1 HD",
      category = "أبو ظبي الرياضية",
      logoUrl = "https://images.unsplash.com/photo-1511886929837-354d827aae26?w=400&auto=format&fit=crop&q=80",
      isLive = true,
      currentEvent = "كأس السوبر الإيطالي وكأس إيطاليا",
      qualityBadge = "HD",
      servers = createDefaultServers("ad_sports_1", 5)
    )
  )

  val todayMatches: List<FootballMatch> = listOf(
    FootballMatch(
      id = "match_1",
      homeTeam = "ريال مدريد",
      awayTeam = "إيه سي ميلان",
      homeLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/5/56/Real_Madrid_CF.svg/180px-Real_Madrid_CF.svg.png",
      awayLogo = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/Logo_of_AC_Milan.svg/180px-Logo_of_AC_Milan.svg.png",
      homeScore = 1,
      awayScore = 3,
      championship = "دوري أبطال أوروبا - مرحلة الدوري",
      timeText = "22:00",
      minute = "78' مباشر",
      isLive = true,
      commentator = "عصام الشوالي",
      channelName = "beIN SPORTS 1 HD",
      channelId = "bein_1",
      stadium = "ملعب سانتياغو برنابيو - مدريد",
      servers = createDefaultServers("match_1", 0)
    ),
    FootballMatch(
      id = "match_2",
      homeTeam = "الأهلي المصري",
      awayTeam = "الزمالك",
      homeLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/8/8c/Al_Ahly_SC_logo.svg/180px-Al_Ahly_SC_logo.svg.png",
      awayLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/0/04/ZamalekSC.png/180px-ZamalekSC.png",
      homeScore = 2,
      awayScore = 1,
      championship = "الدوري المصري الممتاز - كلاسيكو العرب",
      timeText = "19:00",
      minute = "85' مباشر",
      isLive = true,
      commentator = "مدحت شلبي ومحمد الكواليني",
      channelName = "ON Time Sports 1 HD",
      channelId = "ontime_1",
      stadium = "ستاد القاهرة الدولي",
      servers = createDefaultServers("match_2", 1)
    ),
    FootballMatch(
      id = "match_3",
      homeTeam = "مانشستر سيتي",
      awayTeam = "فينورد روتردام",
      homeLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/e/eb/Manchester_City_FC_badge.svg/180px-Manchester_City_FC_badge.svg.png",
      awayLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/8/88/Feyenoord_Rotterdam_logo.svg/180px-Feyenoord_Rotterdam_logo.svg.png",
      homeScore = 3,
      awayScore = 1,
      championship = "دوري أبطال أوروبا - الجولة 5",
      timeText = "22:00",
      minute = "65' مباشر",
      isLive = true,
      commentator = "خليل البلوشي",
      channelName = "beIN SPORTS 2 HD",
      channelId = "bein_2",
      stadium = "ملعب الاتحاد - مانشستر",
      servers = createDefaultServers("match_3", 2)
    ),
    FootballMatch(
      id = "match_4",
      homeTeam = "ليفربول",
      awayTeam = "باير ليفركوزن",
      homeLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/0/0c/Liverpool_FC.svg/180px-Liverpool_FC.svg.png",
      awayLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/5/59/Bayer_04_Leverkusen_logo.svg/180px-Bayer_04_Leverkusen_logo.svg.png",
      homeScore = 4,
      awayScore = 0,
      championship = "دوري أبطال أوروبا",
      timeText = "انتهت",
      minute = "انتهت المباراة",
      isLive = false,
      commentator = "حفيظ دراجي",
      channelName = "beIN SPORTS 3 HD",
      channelId = "bein_3",
      stadium = "ملعب آنفيلد رود - ليفربول",
      servers = createDefaultServers("match_4", 3)
    ),
    FootballMatch(
      id = "match_5",
      homeTeam = "الهلال السعودي",
      awayTeam = "النصر السعودي",
      homeLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/b/b9/Al_Hilal_SFC_Logo.svg/180px-Al_Hilal_SFC_Logo.svg.png",
      awayLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/e/ed/Al_Nassr_FC_logo.svg/180px-Al_Nassr_FC_logo.svg.png",
      homeScore = null,
      awayScore = null,
      championship = "دوري روشن السعودي للمحترفين - ديربي الرياض",
      timeText = "21:00",
      minute = "تبدأ في 21:00",
      isLive = false,
      commentator = "فهد العتيبي وفارس عوض",
      channelName = "SSC Sports 1 HD",
      channelId = "ssc_1",
      stadium = "ملعب المملكة أرينا - الرياض",
      servers = createDefaultServers("match_5", 4)
    ),
    FootballMatch(
      id = "match_6",
      homeTeam = "بيراميدز",
      awayTeam = "الإسماعيلي",
      homeLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/9/90/Pyramids_FC_logo.svg/180px-Pyramids_FC_logo.svg.png",
      awayLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/6/64/Ismaily_SC_logo.png/180px-Ismaily_SC_logo.png",
      homeScore = null,
      awayScore = null,
      championship = "الدوري المصري الممتاز",
      timeText = "17:30",
      minute = "اليوم 17:30",
      isLive = false,
      commentator = "أيمن الكاشف",
      channelName = "ON Time Sports 2 HD",
      channelId = "ontime_2",
      stadium = "ستاد 30 يونيو (الدفاع الجوي)",
      servers = createDefaultServers("match_6", 1)
    ),
    FootballMatch(
      id = "match_7",
      homeTeam = "باريس سان جيرمان",
      awayTeam = "أتلتيكو مدريد",
      homeLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/a/a7/Paris_Saint-Germain_F.C..svg/180px-Paris_Saint-Germain_F.C..svg.png",
      awayLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/f/f4/Atletico_Madrid_2017_logo.svg/180px-Atletico_Madrid_2017_logo.svg.png",
      homeScore = 1,
      awayScore = 2,
      championship = "دوري أبطال أوروبا",
      timeText = "انتهت",
      minute = "انتهت المباراة",
      isLive = false,
      commentator = "عامر الخوذيري",
      channelName = "beIN SPORTS 4 HD",
      channelId = "bein_4",
      stadium = "ملعب حديقة الأمراء - باريس",
      servers = createDefaultServers("match_7", 2)
    ),
    FootballMatch(
      id = "match_8",
      homeTeam = "النجم الأحمر بلغراد",
      awayTeam = "برشلونة",
      homeLogo = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/FK_Crvena_zvezda_logo.svg/180px-FK_Crvena_zvezda_logo.svg.png",
      awayLogo = "https://upload.wikimedia.org/wikipedia/en/thumb/4/47/FC_Barcelona_%28crest%29.svg/180px-FC_Barcelona_%28crest%29.svg.png",
      homeScore = 2,
      awayScore = 5,
      championship = "دوري أبطال أوروبا",
      timeText = "انتهت",
      minute = "انتهت المباراة",
      isLive = false,
      commentator = "أحمد البلوشي",
      channelName = "beIN SPORTS 5 HD",
      channelId = "bein_5",
      stadium = "ملعب راجكو ميتيتش",
      servers = createDefaultServers("match_8", 3)
    )
  )

  // Creates the full, complete set of episodes with individual servers and durations
  private fun createEpisodes(seriesId: String, count: Int = 30): List<Episode> {
    return (1..count).map { num ->
      val epTitle = if (num == count) "الحلقة $num (الأخيرة)" else "الحلقة $num"
      Episode(
        number = num,
        title = epTitle,
        duration = "${40 + (num % 12)} دقيقة",
        servers = createDefaultServers("${seriesId}_ep$num", num - 1, MediaType.SERIES)
      )
    }
  }

  val ramadanSeries: List<RamadanSeries> = listOf(
    RamadanSeries(
      id = "series_hashashin",
      title = "الحشاشين",
      seasonYear = 2024,
      genre = "تاريخي وإثارة وتشويق",
      posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500&auto=format&fit=crop&q=80",
      rating = 4.9f,
      ratingCount = 14200,
      director = "بيتر ميمي",
      cast = listOf("كريم عبد العزيز", "فتحي عبد الوهاب", "ميرنا نور الدين", "أحمد عيد", "نيقولا معوض"),
      synopsis = "تدور الأحداث في القرن الحادي عشر حول مؤسس جماعة الحشاشين حسن الصباح، وقيادته للفرقة التي اشتهرت بتنفيذ اغتيالات دموية لشخصيات بارزة في تلك المرحلة التاريخية وسط قلعة ألموت الحصينة.",
      episodesCount = 30,
      episodes = createEpisodes("hashashin", 30)
    ),
    RamadanSeries(
      id = "series_jaafar",
      title = "جعفر العمدة",
      seasonYear = 2024,
      genre = "دراما شعبية وتشويق",
      posterUrl = "https://images.unsplash.com/photo-1533488765986-dfa2a9939acd?w=500&auto=format&fit=crop&q=80",
      rating = 4.8f,
      ratingCount = 28900,
      director = "محمد سامي",
      cast = listOf("محمد رمضان", "زينة", "هالة صدقي", "إيمان العاصي", "مي كساب", "منة فضالي", "أحمد داش"),
      synopsis = "جعفر العمدة رجل من حي السيدة زينب، متزوج من أربع نساء، يعيش مأساة مريرة بعد اختطاف ابنه الرضيع منذ أكثر من 19 عاماً، ويخوض رحلة شاقة للبحث عنه ومواجهة أعدائه.",
      episodesCount = 30,
      episodes = createEpisodes("jaafar", 30)
    ),
    RamadanSeries(
      id = "series_neama",
      title = "نعمة الأفوكاتو",
      seasonYear = 2024,
      genre = "دراما قانونية وإثارة",
      posterUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=500&auto=format&fit=crop&q=80",
      rating = 4.7f,
      ratingCount = 11500,
      director = "محمد سامي",
      cast = listOf("مي عمر", "أحمد زاهر", "أروى جودة", "كمال أبو رية", "سلوى عثمان"),
      synopsis = "محامية ذكية وطموحة تكرس حياتها لمساعدة زوجها المتعثر مالياً، لكنها تصدم بخيانته ومؤامرته لقتلها، لتعود من الموت وتخطط لانتقام قانوني ذكي وشامل.",
      episodesCount = 16,
      episodes = createEpisodes("neama", 16)
    ),
    RamadanSeries(
      id = "series_kabeer8",
      title = "الكبير أوي 8",
      seasonYear = 2024,
      genre = "كوميديا وفانتازيا",
      posterUrl = "https://images.unsplash.com/photo-1514306191717-452ec28c7814?w=500&auto=format&fit=crop&q=80",
      rating = 4.6f,
      ratingCount = 9800,
      director = "أحمد الجندي",
      cast = listOf("أحمد مكي", "رحمة أحمد", "بيومي فؤاد", "محمد سلام", "مصطفى غريب", "حاتم صلاح"),
      synopsis = "مغامرات جديدة ومضحكة لعمدة المزاريطة الكبير وجوني وحزلقوم، مع مفارقات لا تنتهي في القرية ومحاولات غريبة لتحقيق الثراء والشهرة بطرق مبتكرة.",
      episodesCount = 30,
      episodes = createEpisodes("kabeer8", 30)
    ),
    RamadanSeries(
      id = "series_haq_arab",
      title = "حق عرب",
      seasonYear = 2024,
      genre = "دراما شعبية وأكشن",
      posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=500&auto=format&fit=crop&q=80",
      rating = 4.7f,
      ratingCount = 12400,
      director = "إسماعيل فاروق",
      cast = listOf("أحمد العوضي", "دينا فؤاد", "رياض الخولي", "وفاء عامر", "وليد فواز"),
      synopsis = "عرب السويركي شاب شهم يترعرع في حي المغربلين بالقاهرة القديمة، يعمل لدى تاجر أخشاب كبير، وتحدث صراعات دامية وأسرار عائلية تهز أركان الحارة.",
      episodesCount = 30,
      episodes = createEpisodes("haq_arab", 30)
    ),
    RamadanSeries(
      id = "series_maddah",
      title = "المداح 4: أسطورة العودة",
      seasonYear = 2024,
      genre = "رعب وتشويق وإثارة",
      posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500&auto=format&fit=crop&q=80",
      rating = 4.8f,
      ratingCount = 15300,
      director = "أحمد سمير فرج",
      cast = listOf("حمادة هلال", "فتحي عبد الوهاب", "سهر الصايغ", "هبة مجدي", "خالد سرحان", "دياب"),
      synopsis = "يواصل صابر المداح حروبه المستمرة والضارية ضد ملوك الجان وقوى الشر التي تحاول السيطرة عليه وإيذاء أسرته، في مواجهة ملحمية مليئة بالغموض والأسرار.",
      episodesCount = 30,
      episodes = createEpisodes("maddah", 30)
    ),
    RamadanSeries(
      id = "series_moalem",
      title = "المعلم",
      seasonYear = 2024,
      genre = "دراما شعبية واجتماعية",
      posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=500&auto=format&fit=crop&q=80",
      rating = 4.5f,
      ratingCount = 8900,
      director = "مرقس عادل",
      cast = listOf("مصطفى شعبان", "هاجر أحمد", "أحمد فؤاد سليم", "انتصار", "سلوى خطاب"),
      synopsis = "تاجر سمك شاب يحاول استعادة سمعة والده بعد مؤامرة دمرت تجارته وسجنته ظلماً في سوق السمك، ويخوض معارك تجارية وشخصية لاسترداد مكانته.",
      episodesCount = 30,
      episodes = createEpisodes("moalem", 30)
    ),
    RamadanSeries(
      id = "series_ashghal",
      title = "أشغال شقة",
      seasonYear = 2024,
      genre = "كوميدي عائلي واجتماعي",
      posterUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=500&auto=format&fit=crop&q=80",
      rating = 4.9f,
      ratingCount = 21000,
      director = "خالد دياب",
      cast = listOf("هشام ماجد", "أسماء جلال", "شيرين", "مصطفى غريب", "سلوى محمد علي"),
      synopsis = "حمدي طبيب شرعي وزوجته ياسمين يرزقان بتوأم، ويضطران للاستعانة بمدبرات منزل لرعاية الطفلين، ما يوقعهما في مواقف كوميدية ومفارقات يومية جنونية.",
      episodesCount = 15,
      episodes = createEpisodes("ashghal", 15)
    ),
    RamadanSeries(
      id = "series_atwla",
      title = "العتاولة",
      seasonYear = 2024,
      genre = "أكشن وإثارة وتشويق",
      posterUrl = "https://images.unsplash.com/photo-1509281373149-e957c6296406?w=500&auto=format&fit=crop&q=80",
      rating = 4.8f,
      ratingCount = 18600,
      director = "أحمد خالد موسى",
      cast = listOf("أحمد السقا", "طارق لطفي", "باسم سمرة", "زينة", "مي كساب", "فريدة سيف النصر"),
      synopsis = "الشقيقان نصار وخضر يمارسان أعمال السرقة والجريمة المتوارثة في الإسكندرية، حتى يقرر أحدهما التوبة والابتعاد عن هذا العالم، فتبدأ حرب طاحنة بين الأشقاء وعيسى الوزان.",
      episodesCount = 30,
      episodes = createEpisodes("atwla", 30)
    ),
    RamadanSeries(
      id = "series_ramadan_2025_future",
      title = "جودر: ألف ليلة وليلة",
      seasonYear = 2025,
      genre = "فانتازيا تاريخية ومغامرات",
      posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500&auto=format&fit=crop&q=80",
      rating = 4.8f,
      ratingCount = 9200,
      director = "إسلام خيري",
      cast = listOf("ياسر جلال", "نور اللبنانية", "ياسمين رئيس", "تارا عماد", "وفاء عامر"),
      synopsis = "حكاية الصياد جودر ابن عمر المصري وما حدث له مع إخوته والمؤامرات السحرية التي تحيط به لفك كنوز الشمردل الأربعة في ملحمة أسطورية ساحرة.",
      episodesCount = 15,
      episodes = createEpisodes("gawdar", 15)
    )
  )

  val egyptianMovies: List<EgyptianMovie> = listOf(
    EgyptianMovie(
      id = "mov_welad_rizk_3",
      title = "ولاد رزق 3: القاضية",
      releaseYear = 2024,
      genre = "أكشن وإثارة وجريمة",
      posterUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500&auto=format&fit=crop&q=80",
      rating = 4.9f,
      ratingCount = 35200,
      durationMinutes = 135,
      director = "طارق العريان",
      cast = listOf("أحمد عز", "عمرو يوسف", "كريم قاسم", "آسر ياسين", "علي صبحي", "محمد لطفي"),
      synopsis = "بعد سنوات من الانفصال، يعود ولاد رزق لتنفيذ عملية سطو خطيرة ومصيرية في الرياض بمشاركة شبح قديم يطاردهم، في أضخم إنتاج سينمائي عربي حطم الأرقام القياسية.",
      boxOffice = "250 مليون جنيه مصري",
      servers = createDefaultServers("welad_rizk_3", 0, MediaType.MOVIE)
    ),
    EgyptianMovie(
      id = "mov_harreefa",
      title = "الحريفة",
      releaseYear = 2024,
      genre = "كوميدي شبابي ورياضي",
      posterUrl = "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=500&auto=format&fit=crop&q=80",
      rating = 4.8f,
      ratingCount = 22400,
      durationMinutes = 110,
      director = "رؤوف السيد",
      cast = listOf("نور النبوي", "أحمد غزي", "كزبرة", "نور إيهاب", "خالد الذهبي", "أحمد حسام ميدو"),
      synopsis = "ماجد لاعب كرة قدم شاب يضطر للانتقال من مدرسته الدولية الراقية إلى مدرسة حكومية بعد إفلاس والده، لينضم لفريق كرة القدم للشارع ويخوض بطولة كبرى لتغيير مستقبله.",
      boxOffice = "75 مليون جنيه مصري",
      servers = createDefaultServers("harreefa", 1, MediaType.MOVIE)
    ),
    EgyptianMovie(
      id = "mov_beit_alruby",
      title = "بيت الروبي",
      releaseYear = 2023,
      genre = "كوميدي عائلي واجتماعي",
      posterUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=500&auto=format&fit=crop&q=80",
      rating = 4.7f,
      ratingCount = 29000,
      durationMinutes = 118,
      director = "بيتر ميمي",
      cast = listOf("كريم عبد العزيز", "كريم محمود عبد العزيز", "نور اللبنانية", "تارا عماد"),
      synopsis = "إبراهيم الروبي يعيش في هدوء منعزلاً في طابا بعد أزمة سوشيال ميديا سابقة، لكن شقيقه الأصغر يقنعه بالعودة للقاهرة لإنهاء أوراق خاصة، ليتحول بالصدفة لإنفلونسر شهير.",
      boxOffice = "130 مليون جنيه مصري",
      servers = createDefaultServers("beit_alruby", 2, MediaType.MOVIE)
    ),
    EgyptianMovie(
      id = "mov_fasel_lahazat",
      title = "فاصل من اللحظات اللذيذة",
      releaseYear = 2024,
      genre = "كوميدي فانتازيا ورومانسي",
      posterUrl = "https://images.unsplash.com/photo-1478720568477-152d9b164e26?w=500&auto=format&fit=crop&q=80",
      rating = 4.7f,
      ratingCount = 18300,
      durationMinutes = 105,
      director = "أحمد الجندي",
      cast = listOf("هشام ماجد", "هنا الزاهد", "محمد ثروت", "بيومي فؤاد", "الطفل جان رامز"),
      synopsis = "صالح ودرية زوجان يعيشان حياة بائسة وكئيبة مليئة بالمشاكل، وفجأة تفتح بوابة لعالم موازٍ يعيشان فيه كنسخ سعيدة ومثالية، ما يقلب حياتهما رأساً على عقب.",
      boxOffice = "60 مليون جنيه مصري",
      servers = createDefaultServers("fasel_lahazat", 3, MediaType.MOVIE)
    ),
    EgyptianMovie(
      id = "mov_shaqqo",
      title = "شقو",
      releaseYear = 2024,
      genre = "أكشن وجريمة وتشويق",
      posterUrl = "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=500&auto=format&fit=crop&q=80",
      rating = 4.6f,
      ratingCount = 16800,
      durationMinutes = 120,
      director = "كريم السبكي",
      cast = listOf("عمرو يوسف", "محمد ممدوح", "أمينة خليل", "دينا الشربيني", "يسرا"),
      synopsis = "مجموعة من الأصدقاء الخارجين عن القانون يمارسون أعمالاً إجرامية مشبوهة، لكن عملية سطو غير محسوبة تقودهم إلى صراعات قاتلة مع زعماء عصابات وأجهزة أمنية.",
      boxOffice = "78 مليون جنيه مصري",
      servers = createDefaultServers("shaqqo", 4, MediaType.MOVIE)
    ),
    EgyptianMovie(
      id = "mov_al_serb",
      title = "السرب",
      releaseYear = 2024,
      genre = "حربي وأكشن ووطني",
      posterUrl = "https://images.unsplash.com/photo-1508614589041-895b88991e3e?w=500&auto=format&fit=crop&q=80",
      rating = 4.8f,
      ratingCount = 20500,
      durationMinutes = 130,
      director = "أحمد نادر جلال",
      cast = listOf("أحمد السقا", "شريف منير", "دياب", "محمد ممدوح", "نيللي كريم", "آسر ياسين"),
      synopsis = "ملحمة وطنية حقيقية توثق الضربات الجوية الدقيقة التي نفذتها القوات الجوية المصرية ضد التنظيمات الإرهابية في درنة بليبيا رداً على استشهاد مواطنين مصريين.",
      boxOffice = "45 مليون جنيه مصري",
      servers = createDefaultServers("al_serb", 5, MediaType.MOVIE)
    ),
    EgyptianMovie(
      id = "mov_laeb_ma_eyal",
      title = "اللعب مع العيال",
      releaseYear = 2024,
      genre = "كوميدي ومغامرات",
      posterUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=500&auto=format&fit=crop&q=80",
      rating = 4.5f,
      ratingCount = 14200,
      durationMinutes = 112,
      director = "شريف عرفة",
      cast = listOf("محمد إمام", "أسماء جلال", "ماجد الكدواني", "باسم سمرة", "بيومي فؤاد"),
      synopsis = "مدرس تاريخ شاب يُنقل للتدريس في مدرسة نائية في صحراء سيناء، ليصطدم بعادات وتقاليد البدو ويدخل في مغامرات طريفة مع الطلاب وأهالي المنطقة.",
      boxOffice = "52 مليون جنيه مصري",
      servers = createDefaultServers("laeb_ma_eyal", 6, MediaType.MOVIE)
    ),
    EgyptianMovie(
      id = "mov_esabat_max",
      title = "عصابة الماكس",
      releaseYear = 2024,
      genre = "كوميدي أكشن",
      posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500&auto=format&fit=crop&q=80",
      rating = 4.4f,
      ratingCount = 11200,
      durationMinutes = 105,
      director = "حسام سليمان",
      cast = listOf("أحمد فهمي", "روبي", "لبلبة", "أوس أوس", "حاتم صلاح"),
      synopsis = "الماكس معتاد على تنفيذ عمليات التهريب والسرقة منفرداً، حتى يجد نفسه مجبراً على الاستعانة بعائلة مزيفة ومجنونة لتمرير شحنة آثار ثمينة للغردقة.",
      boxOffice = "38 مليون جنيه مصري",
      servers = createDefaultServers("esabat_max", 7, MediaType.MOVIE)
    ),
    EgyptianMovie(
      id = "mov_ahl_kahf",
      title = "أهل الكهف",
      releaseYear = 2024,
      genre = "تاريخي ملحمي وفانتازيا",
      posterUrl = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=500&auto=format&fit=crop&q=80",
      rating = 4.7f,
      ratingCount = 13500,
      durationMinutes = 140,
      director = "عمرو عرفة",
      cast = listOf("خالد النبوي", "غادة عادل", "محمد ممدوح", "محمد فراج", "ريم مصطفى", "محمود حميدة"),
      synopsis = "اقتباس عن مسرحية توفيق الحكيم الخالدة حول ثلاثة أشخاص يستيقظون داخل كهف ليجدوا أنفسهم قد ناموا لأكثر من ثلاثة قرون، ويواجهون عالماً تغيّر بالكامل.",
      boxOffice = "25 مليون جنيه مصري",
      servers = createDefaultServers("ahl_kahf", 0, MediaType.MOVIE)
    ),
    EgyptianMovie(
      id = "mov_voy_voy",
      title = "فوي فوي فوي!",
      releaseYear = 2023,
      genre = "دراما كوميدية سوداء",
      posterUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=500&auto=format&fit=crop&q=80",
      rating = 4.8f,
      ratingCount = 19400,
      durationMinutes = 108,
      director = "عمر هلال",
      cast = listOf("محمد فراج", "نيللي كريم", "بيومي فؤاد", "طه دسوقي", "أمجد الحجار"),
      synopsis = "شاب مصري فقير يدعي فقدان البصر للانضمام لفريق كرة القدم للمكفوفين، في محاولة للهروب والهجرة غير الشرعية إلى أوروبا خلال بطولة دولية في بولندا.",
      boxOffice = "35 مليون جنيه مصري",
      servers = createDefaultServers("voy_voy", 1, MediaType.MOVIE)
    )
  )

  private fun createCartoonEpisodes(cartoonId: String, count: Int, isMovie: Boolean = false): List<Episode> {
    if (isMovie) {
      return listOf(
        Episode(
          number = 1,
          title = "الفيلم الكامل (مدبلج بالعربي)",
          duration = "105 دقيقة",
          servers = createDefaultServers("${cartoonId}_full", 0, MediaType.CARTOON)
        )
      )
    }
    return (1..count).map { num ->
      Episode(
        number = num,
        title = "الحلقة $num (مدبلج بالعربي)",
        duration = "${22 + (num % 5)} دقيقة",
        servers = createDefaultServers("${cartoonId}_ep$num", num - 1, MediaType.CARTOON)
      )
    }
  }

  val cartoonsList: List<CartoonItem> = listOf(
    CartoonItem(
      id = "cartoon_conan",
      title = "المحقق كونان",
      originalTitle = "Detective Conan",
      category = "أنمي ياباني",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "مركز الزهرة • سبيستون",
      releaseYear = 2024,
      genre = "غموض وتحقيق وتشويق",
      posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500&auto=format&fit=crop&q=80",
      rating = 4.9f,
      ratingCount = 34500,
      synopsis = "سينشي كودو محقق طالب ثانوي بارع يتقلص جسده ليصبح طفلاً صغيراً بعد تناوله عقاراً غامضاً من المنظمة السوداء، فيواصل حل أصعب الجرائم متخفياً باسم كونان إيدوغاوا.",
      episodesCount = 50,
      isMovie = false,
      episodes = createCartoonEpisodes("conan", 50)
    ),
    CartoonItem(
      id = "cartoon_onepiece",
      title = "ون بيس (ملك القراصنة)",
      originalTitle = "One Piece",
      category = "أنمي ياباني",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "استوديوهات الدبلجة العربية",
      releaseYear = 2024,
      genre = "أكشن ومغامرات وبحار",
      posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500&auto=format&fit=crop&q=80",
      rating = 4.9f,
      ratingCount = 41200,
      synopsis = "مغامرة ملحمية يتبع فيها مونكي دي لوفي وطاقم قبعة القش رحلتهم عبر الجراند لاين للبحث عن الكنز الأسطوري ون بيس ليصبح لوفي ملك القراصنة.",
      episodesCount = 50,
      isMovie = false,
      episodes = createCartoonEpisodes("onepiece", 50)
    ),
    CartoonItem(
      id = "cartoon_naruto",
      title = "ناروتو شيبودن",
      originalTitle = "Naruto Shippuden",
      category = "أنمي ياباني",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "مركز الزهرة بالعربي",
      releaseYear = 2023,
      genre = "أكشن وفنون قتالية",
      posterUrl = "https://images.unsplash.com/photo-1618336753974-aae8e04506aa?w=500&auto=format&fit=crop&q=80",
      rating = 4.9f,
      ratingCount = 38900,
      synopsis = "يعود ناروتو أوزوماكي إلى قرية الورق بعد سنوات من التدريب الشاق لإنقاذ صديقه ساسكي ومواجهة منظمة الأكاتسوكي وتحقيق حلمه بأن يصبح الهوكاجي.",
      episodesCount = 40,
      isMovie = false,
      episodes = createCartoonEpisodes("naruto", 40)
    ),
    CartoonItem(
      id = "cartoon_dragonball",
      title = "دراغون بول سوبر",
      originalTitle = "Dragon Ball Super",
      category = "أنمي ياباني",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "مركز الزهرة • سبيستون",
      releaseYear = 2024,
      genre = "أكشن وقوة خارقة",
      posterUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=500&auto=format&fit=crop&q=80",
      rating = 4.8f,
      ratingCount = 31000,
      synopsis = "بعد هزيمة ماجين بو، يواجه غوكو وفيجيتا تحديات كونية غير مسبوقة ضد آلهة الدمار ومقاتلين من أكوان موازية في بطولة القوة الكبرى.",
      episodesCount = 40,
      isMovie = false,
      episodes = createCartoonEpisodes("dragonball", 40)
    ),
    CartoonItem(
      id = "cartoon_aot",
      title = "هجوم العمالقة",
      originalTitle = "Attack on Titan",
      category = "أنمي ياباني",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "الدبلجة العربية الاحترافية",
      releaseYear = 2024,
      genre = "أكشن وفانتازيا وملحمة",
      posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=500&auto=format&fit=crop&q=80",
      rating = 4.9f,
      ratingCount = 29400,
      synopsis = "في عالم تعيش فيه البشرية خلف أسوار عملاقة للحماية من العمالقة آكلي البشر، ينضم إيرين ييغر وأصدقاؤه إلى فيلق الاستطلاع لكشف سر العمالقة واستعادة الحرية.",
      episodesCount = 25,
      isMovie = false,
      ageRating = "+13 • أكشن وإثارة",
      episodes = createCartoonEpisodes("aot", 25)
    ),
    CartoonItem(
      id = "cartoon_hxh",
      title = "القناص (هنتر × هنتر)",
      originalTitle = "Hunter x Hunter",
      category = "أنمي ياباني",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "مركز الزهرة • سبيستون",
      releaseYear = 2023,
      genre = "مغامرات وأكشن وتشويق",
      posterUrl = "https://images.unsplash.com/photo-1541562232579-512a21360020?w=500&auto=format&fit=crop&q=80",
      rating = 4.9f,
      ratingCount = 27800,
      synopsis = "ينطلق الفتى غون فريكس في رحلة محفوفة بالمخاطر لاجتياز اختبار الصيادين والبحث عن والده الصياد الأسطوري برفقة كيلوا وكورابيكا وليوريو.",
      episodesCount = 36,
      isMovie = false,
      episodes = createCartoonEpisodes("hxh", 36)
    ),
    CartoonItem(
      id = "cartoon_tsubasa",
      title = "كابتن ماجد (الشبح)",
      originalTitle = "Captain Tsubasa",
      category = "أنمي ياباني",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "مركز الزهرة • سبيستون",
      releaseYear = 2024,
      genre = "رياضي وكرة قدم",
      posterUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=500&auto=format&fit=crop&q=80",
      rating = 4.8f,
      ratingCount = 25600,
      synopsis = "يحلم الفتى الموهوب ماجد كامل بقيادة منتخبه نحو بطولة العالم لكرة القدم، ويخوض منافسات مثيرة مع بسام ووليد ورفاقه على المستطيل الأخضر.",
      episodesCount = 30,
      isMovie = false,
      episodes = createCartoonEpisodes("tsubasa", 30)
    ),
    CartoonItem(
      id = "cartoon_demonslayer",
      title = "قاتل الشياطين",
      originalTitle = "Demon Slayer",
      category = "أنمي ياباني",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "استوديوهات الدبلجة العربية",
      releaseYear = 2024,
      genre = "أكشن وفانتازيا",
      posterUrl = "https://images.unsplash.com/photo-1519681393784-d120267933ba?w=500&auto=format&fit=crop&q=80",
      rating = 4.9f,
      ratingCount = 26400,
      synopsis = "بعد مأساة قاسية تحل بعائلته، ينضم الشاب الطيب تانجيرو كامادو إلى فيلق قاتلي الشياطين بحثاً عن علاج يعيد شقيقته نيزوكو إلى طبيعتها البشرية.",
      episodesCount = 26,
      isMovie = false,
      ageRating = "+13 • أكشن",
      episodes = createCartoonEpisodes("demonslayer", 26)
    ),
    CartoonItem(
      id = "cartoon_tom_jerry",
      title = "توم وجيري",
      originalTitle = "Tom and Jerry",
      category = "كرتون عالمي",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "وارنر براذرز بالعربية",
      releaseYear = 2023,
      genre = "كوميدي وعائلي وكلاسيكي",
      posterUrl = "https://images.unsplash.com/photo-1514306191717-452ec28c7814?w=500&auto=format&fit=crop&q=80",
      rating = 4.9f,
      ratingCount = 52000,
      synopsis = "أشهر مطاردة كوميدية في تاريخ الرسوم المتحركة بين القط توم والفأر الذكي جيري في حلقات كلاسيكية وحديثة ممتعة لجميع أفراد العائلة.",
      episodesCount = 30,
      isMovie = false,
      episodes = createCartoonEpisodes("tomjerry", 30)
    ),
    CartoonItem(
      id = "cartoon_gumball",
      title = "عالم غامبول المدهش",
      originalTitle = "The Amazing World of Gumball",
      category = "كرتون عالمي",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "كرتون نتورك بالعربية",
      releaseYear = 2024,
      genre = "كوميدي وعائلي ومغامرات",
      posterUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=500&auto=format&fit=crop&q=80",
      rating = 4.8f,
      ratingCount = 33700,
      synopsis = "غامبول واترسون قط أزرق مشاغب يخوض مع شقيقه السمكة داروين مغامرات مضحكة ومواقف غريبة في مدينة إلمور المليئة بالشخصيات الطريفة.",
      episodesCount = 30,
      isMovie = false,
      episodes = createCartoonEpisodes("gumball", 30)
    ),
    CartoonItem(
      id = "cartoon_spongebob",
      title = "سبونج بوب سكوير بانتس",
      originalTitle = "SpongeBob SquarePants",
      category = "كرتون عالمي",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "نيكلوديون بالعربية",
      releaseYear = 2024,
      genre = "كوميدي وعائلي",
      posterUrl = "https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=500&auto=format&fit=crop&q=80",
      rating = 4.8f,
      ratingCount = 44100,
      synopsis = "الإسفنجة الصفراء المرحة سبونج بوب وصديقه بسيط نجم البحر يعيشان في قاع الهامور ويصنعان أشهى سلطع برجر وسط مواقف كوميدية لا تنتهي.",
      episodesCount = 30,
      isMovie = false,
      episodes = createCartoonEpisodes("spongebob", 30)
    ),
    CartoonItem(
      id = "cartoon_ben10",
      title = "بن 10 (أومنيفرس)",
      originalTitle = "Ben 10",
      category = "كرتون عالمي",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "كرتون نتورك بالعربية",
      releaseYear = 2023,
      genre = "أكشن وخيال علمي",
      posterUrl = "https://images.unsplash.com/photo-1535016120720-40c646be5580?w=500&auto=format&fit=crop&q=80",
      rating = 4.7f,
      ratingCount = 24900,
      synopsis = "الفتى بن تينيسون يمتلك ساعة الأومنيتريكس الفضائية التي تتيح له التحول إلى عشرات الأبطال الخارقين لحماية الأرض من الغزاة والأشرار.",
      episodesCount = 26,
      isMovie = false,
      episodes = createCartoonEpisodes("ben10", 26)
    ),
    CartoonItem(
      id = "cartoon_simba",
      title = "الأسد الملك (سيمبا)",
      originalTitle = "The Lion King",
      category = "أفلام أنميشن",
      dubbingLanguage = "مدبلج بالمصرية والعربية",
      dubbingStudio = "ديزني بالعربي",
      releaseYear = 2023,
      genre = "مغامرات وعائلي وموسيقي",
      posterUrl = "https://images.unsplash.com/photo-1546182990-dffeafbe841d?w=500&auto=format&fit=crop&q=80",
      rating = 5.0f,
      ratingCount = 58000,
      synopsis = "تحفة ديزني الخالدة باللهجة المصرية المحببة حول الشبل سيمبا ورحلته بمساعدة تيمون وبومبا لاستعادة عرش الغابة من عمه سكار.",
      episodesCount = 1,
      isMovie = true,
      episodes = createCartoonEpisodes("simba", 1, isMovie = true)
    ),
    CartoonItem(
      id = "cartoon_spiderverse",
      title = "سبايدرمان: عبر عالم العنكبوت",
      originalTitle = "Spider-Man: Across the Spider-Verse",
      category = "أفلام أنميشن",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "سوني بيكتشرز بالعربية",
      releaseYear = 2024,
      genre = "أكشن ومغامرات وخيال علمي",
      posterUrl = "https://images.unsplash.com/photo-1635805737707-575885ab0820?w=500&auto=format&fit=crop&q=80",
      rating = 4.9f,
      ratingCount = 36800,
      synopsis = "ينطلق مايلز موراليس في رحلة مذهلة عبر الأكوان المتعددة ليقابل جيشاً من أبطال العنكبوت المكلفين بحماية وجود العوالم الموازية.",
      episodesCount = 1,
      isMovie = true,
      episodes = createCartoonEpisodes("spiderverse", 1, isMovie = true)
    ),
    CartoonItem(
      id = "cartoon_toystory",
      title = "حكاية لعبة (توي ستوري)",
      originalTitle = "Toy Story",
      category = "أفلام أنميشن",
      dubbingLanguage = "مدبلج بالمصرية",
      dubbingStudio = "ديزني وبيكسار بالعربي",
      releaseYear = 2023,
      genre = "كوميدي وعائلي ومغامرات",
      posterUrl = "https://images.unsplash.com/photo-1558060370-d644479cb6f7?w=500&auto=format&fit=crop&q=80",
      rating = 4.9f,
      ratingCount = 41500,
      synopsis = "عندما يغادر البشر الغرفة تدب الحياة في الألعاب، ويخوض وودي وباز يطير مغامرة مدهشة مليئة بالصداقة والكوميديا.",
      episodesCount = 1,
      isMovie = true,
      episodes = createCartoonEpisodes("toystory", 1, isMovie = true)
    ),
    CartoonItem(
      id = "cartoon_frozen",
      title = "ملكة الثلج (فروزن)",
      originalTitle = "Frozen",
      category = "أفلام أنميشن",
      dubbingLanguage = "مدبلج بالعربي",
      dubbingStudio = "ديزني بالعربي",
      releaseYear = 2024,
      genre = "موسيقي وفانتازيا وعائلي",
      posterUrl = "https://images.unsplash.com/photo-1516912481808-3406841bd33c?w=500&auto=format&fit=crop&q=80",
      rating = 4.8f,
      ratingCount = 39200,
      synopsis = "تنطلق الأميرة آنا برفقة كريستوف ورجل الثلج أولاف في رحلة جليدية للبحث عن شقيقتها إلسا وإذابة الشتاء الأبدي عن مملكة أريندل.",
      episodesCount = 1,
      isMovie = true,
      episodes = createCartoonEpisodes("frozen", 1, isMovie = true)
    )
  )

  val sampleComments: List<UserComment> = listOf(
    UserComment(
      id = "c_1",
      itemId = "series_hashashin",
      authorName = "أحمد المصري",
      rating = 5,
      commentText = "أعظم إنتاج في تاريخ الدراما العربية! تمثيل كريم عبد العزيز أسطوري والإخراج والخدع البصرية بجودة هوليوودية خالصة.",
      timestampText = "منذ يومين",
      likes = 142
    ),
    UserComment(
      id = "c_2",
      itemId = "series_hashashin",
      authorName = "سارة التميمي",
      rating = 5,
      commentText = "الموسيقى التصويرية تأخذك لعالم آخر، والسيرفرات في جوكر TV شغالة بسرعة رهيبة بدون أي تقطيع!",
      timestampText = "منذ 4 أيام",
      likes = 89
    ),
    UserComment(
      id = "c_3",
      itemId = "mov_welad_rizk_3",
      authorName = "محمود الزملكاوي",
      rating = 5,
      commentText = "فيلم للتاريخ ومطاردات عالمية! أفضل جزء في السلسلة بلا منازع، وأحمد عز وعمرو يوسف ثنائي مدمر.",
      timestampText = "منذ 3 ساعات",
      likes = 215
    ),
    UserComment(
      id = "c_4",
      itemId = "mov_harreefa",
      authorName = "كابتن زياد",
      rating = 5,
      commentText = "فيلم شبابي مبهج ومحفز، الأغاني وكرة الشارع خلتني أعيش جو زمان، تسلم إيدكم يا جوكر TV.",
      timestampText = "أمس",
      likes = 67
    ),
    UserComment(
      id = "c_5",
      itemId = "series_jaafar",
      authorName = "الحاج إبراهيم",
      rating = 5,
      commentText = "جعفر العمدة يظل الرقم واحد في الإثارة والتشويق، كل حلقة تشدك أكتر من اللي قبلها.",
      timestampText = "منذ أسبوع",
      likes = 188
    ),
    UserComment(
      id = "c_6",
      itemId = "series_ashghal",
      authorName = "نورهان هشام",
      rating = 5,
      commentText = "أكتر مسلسل ضحكني من سنين! هشام ماجد وأسماء جلال كيمياء غير عادية كوميديا راقية ونظيفة.",
      timestampText = "منذ 5 أيام",
      likes = 134
    ),
    UserComment(
      id = "c_7",
      itemId = "cartoon_conan",
      authorName = "يوسف العتيبي",
      rating = 5,
      commentText = "أجمل قسم كرتون مدبلج بالعربي! دبلجة مركز الزهرة الأصلية لكونان والصفاء في الصورة مذهل.",
      timestampText = "منذ ساعتين",
      likes = 96
    ),
    UserComment(
      id = "c_8",
      itemId = "cartoon_onepiece",
      authorName = "عمر الشامي",
      rating = 5,
      commentText = "كل حلقات ون بيس مدبلجة عربي وشغالة فوراً بدون أي أخطاء، شكراً جوكر TV!",
      timestampText = "أمس",
      likes = 112
    )
  )
}
