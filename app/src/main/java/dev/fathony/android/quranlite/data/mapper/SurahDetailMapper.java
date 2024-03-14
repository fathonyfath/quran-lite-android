package dev.fathony.android.quranlite.data.mapper;

import dev.fathony.android.quranlite.data.json.SurahDetailJSON;
import dev.fathony.android.quranlite.data.json.SurahTafsirJSON;
import dev.fathony.android.quranlite.data.json.SurahTafsirSourceJSON;
import dev.fathony.android.quranlite.data.json.SurahTafsirsJSON;
import dev.fathony.android.quranlite.data.json.SurahTranslationJSON;
import dev.fathony.android.quranlite.data.json.SurahTranslationsJSON;
import dev.fathony.android.quranlite.models.SurahDetail;
import dev.fathony.android.quranlite.models.SurahTafsir;
import dev.fathony.android.quranlite.models.SurahTranslation;

public class SurahDetailMapper {

    public static SurahDetail map(SurahDetailJSON remoteResponse) {
        final SurahTranslation surahTranslation = mapSurahTranslationsJSON(remoteResponse.translations, "id");
        final SurahTafsir surahTafsir = mapSurahTafsirsJSON(remoteResponse.tafsir, "id", "kemenag");

        return new SurahDetail(
                remoteResponse.number,
                remoteResponse.name,
                remoteResponse.name_latin,
                remoteResponse.number_of_ayah,
                remoteResponse.text,
                surahTranslation,
                surahTafsir
        );
    }

    private static SurahTranslation mapSurahTranslationsJSON(SurahTranslationsJSON translations, String locale) {
        final SurahTranslationJSON surahTranslation = translations.translations.get(locale);

        if (surahTranslation != null) {
            return new SurahTranslation(
                    surahTranslation.name,
                    surahTranslation.text
            );
        }

        return null;
    }

    private static SurahTafsir mapSurahTafsirsJSON(SurahTafsirsJSON tafsir, String locale, String release) {
        final SurahTafsirJSON tafsirResponse = tafsir.tafsir.get(locale);

        if (tafsirResponse != null) {
            final SurahTafsirSourceJSON tafsirSource = tafsirResponse.sources.get(release);
            if (tafsirSource != null) {
                return new SurahTafsir(
                        tafsirSource.name,
                        tafsirSource.source,
                        tafsirSource.text
                );
            }
        }

        return null;
    }
}
