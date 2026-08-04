package com.example.data.quran

import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.data.model.Word

object QuranDataProvider {

    val ALL_SURAHS = listOf(
        Surah(1, "الفاتحة", "Al-Fatihah", "The Opening", 7, "Meccan", 1),
        Surah(2, "البقرة", "Al-Baqarah", "The Cow", 286, "Medinan", 1),
        Surah(3, "آل عمران", "Ali 'Imran", "Family of Imran", 200, "Medinan", 3),
        Surah(4, "النساء", "An-Nisa", "The Women", 176, "Medinan", 4),
        Surah(5, "المائدة", "Al-Ma'idah", "The Table Spread", 120, "Medinan", 6),
        Surah(6, "الأنعام", "Al-An'am", "The Cattle", 165, "Meccan", 7),
        Surah(7, "الأعراف", "Al-A'raf", "The Heights", 206, "Meccan", 8),
        Surah(8, "الأنفال", "Al-Anfal", "The Spoils of War", 75, "Medinan", 9),
        Surah(9, "التوبة", "At-Tawbah", "The Repentance", 129, "Medinan", 10),
        Surah(10, "يونس", "Yunus", "Jonah", 109, "Meccan", 11),
        Surah(18, "الكهف", "Al-Kahf", "The Cave", 110, "Meccan", 15),
        Surah(36, "يس", "Yasin", "Ya-Sin", 83, "Meccan", 22),
        Surah(55, "الرحمن", "Ar-Rahman", "The Beneficent", 78, "Medinan", 27),
        Surah(56, "الواقعة", "Al-Waqi'ah", "The Inevitable", 96, "Meccan", 27),
        Surah(67, "الملك", "Al-Mulk", "The Sovereignty", 30, "Meccan", 29),
        Surah(78, "النبأ", "An-Naba", "The Tidings", 40, "Meccan", 30),
        Surah(87, "الأعلى", "Al-A'la", "The Most High", 19, "Meccan", 30),
        Surah(93, "الضحى", "Ad-Duha", "The Morning Hours", 11, "Meccan", 30),
        Surah(94, "الشرح", "Ash-Sharh", "The Relief", 8, "Meccan", 30),
        Surah(95, "التين", "At-Tin", "The Fig", 8, "Meccan", 30),
        Surah(96, "العلق", "Al-'Alaq", "The Clot", 19, "Meccan", 30),
        Surah(97, "القدر", "Al-Qadr", "The Night of Decree", 5, "Meccan", 30),
        Surah(108, "الكوثر", "Al-Kawthar", "Abundance", 3, "Meccan", 30),
        Surah(112, "الإخلاص", "Al-Ikhlas", "Sincerity", 4, "Meccan", 30),
        Surah(113, "الفلق", "Al-Falaq", "The Daybreak", 5, "Meccan", 30),
        Surah(114, "الناس", "An-Nas", "Mankind", 6, "Meccan", 30)
    )

    fun getAyahsForSurah(surahId: Int): List<Ayah> {
        return when (surahId) {
            1 -> listOf(
                Ayah(
                    id = 101, surahId = 1, ayahNumber = 1,
                    arabicText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    englishText = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                    transliteration = "Bismillahir-Rahmanir-Rahim",
                    words = listOf(
                        Word(1, "بِسْمِ", "Bismi", "In (the) name"),
                        Word(2, "اللَّهِ", "Allahi", "(of) Allah"),
                        Word(3, "الرَّحْمَٰنِ", "Ar-Rahmani", "The Entirely Merciful"),
                        Word(4, "الرَّحِيمِ", "Ar-Rahim", "The Especially Merciful")
                    )
                ),
                Ayah(
                    id = 102, surahId = 1, ayahNumber = 2,
                    arabicText = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                    englishText = "[All] praise is [due] to Allah, Lord of the worlds -",
                    transliteration = "Al-hamdu lillahi Rabbil-'alamin",
                    words = listOf(
                        Word(1, "الْحَمْدُ", "Al-hamdu", "All praise"),
                        Word(2, "لِلَّهِ", "Lillahi", "(is) for Allah"),
                        Word(3, "رَبِّ", "Rabbi", "Lord"),
                        Word(4, "الْعَالَمِينَ", "Al-'alamin", "(of) the worlds")
                    )
                ),
                Ayah(
                    id = 103, surahId = 1, ayahNumber = 3,
                    arabicText = "الرَّحْمَٰنِ الرَّحِيمِ",
                    englishText = "The Entirely Merciful, the Especially Merciful,",
                    transliteration = "Ar-Rahmanir-Rahim",
                    words = listOf(
                        Word(1, "الرَّحْمَٰنِ", "Ar-Rahmani", "The Entirely Merciful"),
                        Word(2, "الرَّحِيمِ", "Ar-Rahim", "The Especially Merciful")
                    )
                ),
                Ayah(
                    id = 104, surahId = 1, ayahNumber = 4,
                    arabicText = "مَالِكِ يَوْمِ الدِّينِ",
                    englishText = "Sovereign of the Day of Recompense.",
                    transliteration = "Maliki Yawmid-Din",
                    words = listOf(
                        Word(1, "مَالِكِ", "Maliki", "Master"),
                        Word(2, "يَوْمِ", "Yawmi", "(of the) Day"),
                        Word(3, "الدِّينِ", "Ad-Din", "(of) Recompense")
                    )
                ),
                Ayah(
                    id = 105, surahId = 1, ayahNumber = 5,
                    arabicText = "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
                    englishText = "It is You we worship and You we ask for help.",
                    transliteration = "Iyyaka na'budu wa iyyaka nasta'in",
                    words = listOf(
                        Word(1, "إِيَّاكَ", "Iyyaka", "You alone"),
                        Word(2, "نَعْبُدُ", "na'budu", "we worship"),
                        Word(3, "وَإِيَّاكَ", "wa iyyaka", "and You alone"),
                        Word(4, "نَسْتَعِينُ", "nasta'in", "we ask for help")
                    )
                ),
                Ayah(
                    id = 106, surahId = 1, ayahNumber = 6,
                    arabicText = "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ",
                    englishText = "Guide us to the straight path -",
                    transliteration = "Ihdinas-siratal-mustaqim",
                    words = listOf(
                        Word(1, "اهْدِنَا", "Ihdina", "Guide us"),
                        Word(2, "الصِّرَاطَ", "As-Sirata", "(to) the path"),
                        Word(3, "الْمُسْتَقِيمَ", "Al-Mustaqim", "the straight")
                    )
                ),
                Ayah(
                    id = 107, surahId = 1, ayahNumber = 7,
                    arabicText = "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
                    englishText = "The path of those upon whom You have bestowed favor, not of those who have earned [Your] anger or of those who are astray.",
                    transliteration = "Siratalladhina an'amta 'alayhim ghayril-maghdubi 'alayhim wa lad-dallin",
                    words = listOf(
                        Word(1, "صِرَاطَ", "Sirata", "The path"),
                        Word(2, "الَّذِينَ", "alladhina", "(of) those"),
                        Word(3, "أَنْعَمْتَ", "an'amta", "You bestowed favor"),
                        Word(4, "عَلَيْهِمْ", "'alayhim", "upon them"),
                        Word(5, "غَيْرِ", "ghayri", "not (of)"),
                        Word(6, "الْمَغْضُوبِ", "al-maghdubi", "those who earned anger"),
                        Word(7, "عَلَيْهِمْ", "'alayhim", "upon them"),
                        Word(8, "وَلَا", "wa la", "and not"),
                        Word(9, "الضَّالِّينَ", "ad-dallin", "those who go astray")
                    )
                )
            )

            112 -> listOf(
                Ayah(
                    id = 11201, surahId = 112, ayahNumber = 1,
                    arabicText = "قُلْ هُوَ اللَّهُ أَحَدٌ",
                    englishText = "Say, \"He is Allah, [who is] One,",
                    transliteration = "Qul Huwallahu Ahad",
                    words = listOf(
                        Word(1, "قُلْ", "Qul", "Say"),
                        Word(2, "هُوَ", "Huwa", "He (is)"),
                        Word(3, "اللَّهُ", "Allahu", "Allah"),
                        Word(4, "أَحَدٌ", "Ahad", "One")
                    )
                ),
                Ayah(
                    id = 11202, surahId = 112, ayahNumber = 2,
                    arabicText = "اللَّهُ الصَّمَدُ",
                    englishText = "Allah, the Eternal Refuge.",
                    transliteration = "Allahus-Samad",
                    words = listOf(
                        Word(1, "اللَّهُ", "Allahu", "Allah"),
                        Word(2, "الصَّمَدُ", "As-Samad", "The Eternal Absolute")
                    )
                ),
                Ayah(
                    id = 11203, surahId = 112, ayahNumber = 3,
                    arabicText = "لَمْ يَلِدْ وَلَمْ يُولَدْ",
                    englishText = "He neither begets nor is born,",
                    transliteration = "Lam yalid wa lam yulad",
                    words = listOf(
                        Word(1, "لَمْ", "Lam", "Not"),
                        Word(2, "يَلِدْ", "yalid", "He begets"),
                        Word(3, "وَلَمْ", "wa lam", "and not"),
                        Word(4, "يُولَدْ", "yulad", "He is born")
                    )
                ),
                Ayah(
                    id = 11204, surahId = 112, ayahNumber = 4,
                    arabicText = "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
                    englishText = "Nor is there to Him any equivalent.\"",
                    transliteration = "Wa lam yakun lahu kufuwan ahad",
                    words = listOf(
                        Word(1, "وَلَمْ", "Wa lam", "And not"),
                        Word(2, "يَكُن", "yakun", "there is"),
                        Word(3, "لَّهُ", "lahu", "unto Him"),
                        Word(4, "كُفُوًا", "kufuwan", "equivalent"),
                        Word(5, "أَحَدٌ", "ahad", "anyone")
                    )
                )
            )

            113 -> listOf(
                Ayah(11301, 113, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Say, \"I seek refuge in the Lord of daybreak", "Qul a'udhu bi Rabbil-falaq"),
                Ayah(11302, 113, 2, "مِن شَرِّ مَا خَلَقَ", "From the evil of that which He created", "Min sharri ma khalaq"),
                Ayah(11303, 113, 3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "And from the evil of darkness when it settles", "Wa min sharri ghasiqin idha waqab"),
                Ayah(11304, 113, 4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "And from the evil of the blowers in knots", "Wa min sharrin-naffathati fil-'uqad"),
                Ayah(11305, 113, 5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "And from the evil of an envier when he envies.\"", "Wa min sharri hasidin idha hasad")
            )

            114 -> listOf(
                Ayah(11401, 114, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Say, \"I seek refuge in the Lord of mankind,", "Qul a'udhu bi Rabbin-nas"),
                Ayah(11402, 114, 2, "مَلِكِ النَّاسِ", "The Sovereign of mankind.", "Malikin-nas"),
                Ayah(11403, 114, 3, "إِلَٰهِ النَّاسِ", "The God of mankind,", "Ilahin-nas"),
                Ayah(11404, 114, 4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "From the evil of the retreating whisperer -", "Min sharril-waswasil-khannas"),
                Ayah(11405, 114, 5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Who whispers [evil] into the breasts of mankind -", "Alladhi yuwaswisu fi sudurin-nas"),
                Ayah(11406, 114, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "From among the jinn and mankind.\"", "Minal-jinnati wan-nas")
            )

            else -> {
                val surah = ALL_SURAHS.find { it.id == surahId } ?: ALL_SURAHS.first()
                (1..surah.verseCount).take(15).map { ayahNum ->
                    Ayah(
                        id = surahId * 1000 + ayahNum,
                        surahId = surahId,
                        ayahNumber = ayahNum,
                        arabicText = "آيَةٌ كَرِيمَةٌ مِنْ سُورَةِ ${surah.nameArabic} - الآيَة $ayahNum",
                        englishText = "Ayah $ayahNum of Surah ${surah.nameEnglish} (${surah.translatedName}).",
                        transliteration = "Ayah $ayahNum of ${surah.nameEnglish}",
                        words = listOf(
                            Word(1, "آيَةٌ", "Ayah", "Verse"),
                            Word(2, "كَرِيمَةٌ", "Karimah", "Noble"),
                            Word(3, "مِنْ", "Min", "From"),
                            Word(4, "سُورَةِ", "Surati", "Surah"),
                            Word(5, surah.nameArabic, surah.nameEnglish, surah.translatedName)
                        )
                    )
                }
            }
        }
    }

    fun getAudioUrlForAyah(surahId: Int, ayahNumber: Int, reciterId: String): String {
        val formattedSurah = surahId.toString().padStart(3, '0')
        val formattedAyah = ayahNumber.toString().padStart(3, '0')
        // Standard Quran.com or MP3Quran verse audio URL format
        return "https://everyayah.com/data/Alafasy_128kbps/${formattedSurah}${formattedAyah}.mp3"
    }
}
