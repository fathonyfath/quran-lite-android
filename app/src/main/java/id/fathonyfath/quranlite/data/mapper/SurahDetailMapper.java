package id.fathonyfath.quranlite.data.mapper;

import id.fathonyfath.quranlite.data.json.SurahDetailJSON;
import id.fathonyfath.quranlite.data.json.SurahTafsirJSON;
import id.fathonyfath.quranlite.data.json.SurahTafsirSourceJSON;
import id.fathonyfath.quranlite.data.json.SurahTafsirsJSON;
import id.fathonyfath.quranlite.data.json.SurahTranslationJSON;
import id.fathonyfath.quranlite.data.json.SurahTranslationsJSON;
import id.fathonyfath.quranlite.models.SurahDetail;
import id.fathonyfath.quranlite.models.SurahTafsir;
import id.fathonyfath.quranlite.models.SurahTranslation;

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
