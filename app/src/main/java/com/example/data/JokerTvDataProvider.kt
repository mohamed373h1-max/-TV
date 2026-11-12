package com.example.data

import com.example.model.EgyptianMovie
import com.example.model.Episode
import com.example.model.FootballMatch
import com.example.model.RamadanSeries
import com.example.model.ServerInfo
import com.example.model.SportsChannel
import com.example.model.UserComment

object JokerTvDataProvider {

  // Highly reliable working stream sources (both local zero-latency resource and verified HTTP 200 public CDN links)
  const val STREAM_LOCAL_RAW = "LOCAL_RAW"
  const val STREAM_REMOTE_OCEANS = "https://vjs.zencdn.net/v/oceans.mp4"
  const val STREAM_REMOTE_SINTEL = "https://media.w3.org/2010/05/sintel/trailer.mp4"
  const val STREAM_REMOTE_HLS = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"

  fun createDefaultServers(contentId: String, streamIndexOffset: Int = 0): List<ServerInfo> {
    return listOf(
      ServerInfo(
        id = "${contentId}_srv1",
        name = "سيرفر جوكر 1 (FHD - فائق السرعة مباشر)",
        quality = "FHD 1080p 60fps",
        streamUrl = STREAM_LOCAL_RAW,
        pingMs = 12,
        isVip = true,
        isOnline = true
      ),
      ServerInfo(
        id = "${contentId}_srv2",
        name = "سيرفر جوكر 2 (HD - بدون تقطيع سحابي)",
        quality = "HD 720p",
        streamUrl = STREAM_REMOTE_OCEANS,
        pingMs = 26,
        isVip = false,
        isOnline = true
      ),
      ServerInfo(
        id = "${contentId}_srv3",
        name = "سيرفر جوكر 3 (SD - للنت الضعيف وباقات الموبايل)",
        quality = "SD 480p",
        streamUrl = STREAM_REMOTE_SINTEL,
        pingMs = 16,
        isVip = false,
        isOnline = true
      ),
      ServerInfo(
        id = "${contentId}_srv4",
        name = "سيرفر جوكر 4 (VIP 4K - بث مباشر HLS)",
        quality = "4K Ultra VIP",
        streamUrl = STREAM_REMOTE_HLS,
        pingMs = 34,
        isVip = true,
        isOnline = true
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
        servers = createDefaultServers("${seriesId}_ep$num", num - 1)
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
      servers = createDefaultServers("welad_rizk_3", 0)
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
      servers = createDefaultServers("harreefa", 1)
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
      servers = createDefaultServers("beit_alruby", 2)
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
      servers = createDefaultServers("fasel_lahazat", 3)
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
      servers = createDefaultServers("shaqqo", 4)
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
      servers = createDefaultServers("al_serb", 5)
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
      servers = createDefaultServers("laeb_ma_eyal", 6)
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
      servers = createDefaultServers("esabat_max", 7)
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
      servers = createDefaultServers("ahl_kahf", 0)
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
      servers = createDefaultServers("voy_voy", 1)
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
    )
  )
}
