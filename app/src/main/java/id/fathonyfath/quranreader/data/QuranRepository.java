package id.fathonyfath.quranreader.data;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import id.fathonyfath.quranreader.data.remote.OnDownloadProgressListener;
import id.fathonyfath.quranreader.data.remote.QuranJsonService;
import id.fathonyfath.quranreader.data.remote.models.SurahDetailResponse;
import id.fathonyfath.quranreader.data.remote.models.SurahResponse;
import id.fathonyfath.quranreader.data.remote.models.SurahTafsirResponse;
import id.fathonyfath.quranreader.data.remote.models.SurahTafsirSourceResponse;
import id.fathonyfath.quranreader.data.remote.models.SurahTafsirsResponse;
import id.fathonyfath.quranreader.data.remote.models.SurahTranslationResponse;
import id.fathonyfath.quranreader.data.remote.models.SurahTranslationsResponse;
import id.fathonyfath.quranreader.models.Surah;
import id.fathonyfath.quranreader.models.SurahDetail;
import id.fathonyfath.quranreader.models.SurahTafsir;
import id.fathonyfath.quranreader.models.SurahTranslation;

public class QuranRepository {

    private final QuranJsonService quranJsonService;
    private final OnDownloadProgressListener onDownloadProgressListener = new OnDownloadProgressListener() {
        @Override
        public void onDownloadProgress(int currentProgress, int maxProgress) {
            if (QuranRepository.this.onProgressListener != null) {
                float progress = ((float) currentProgress) / ((float) maxProgress) * 100.0f;
                QuranRepository.this.onProgressListener.onProgress(progress);
            }
        }
    };

    private OnProgressListener onProgressListener;

    public QuranRepository(QuranJsonService quranJsonService) {
        this.quranJsonService = quranJsonService;
        this.quranJsonService.setOnDownloadProgressListener(onDownloadProgressListener);
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public List<Surah> fetchAllSurah() {
        final Map<Integer, SurahResponse> remoteResponse = quranJsonService.getSurahIndex();
        return parseSurahResponseFromRemote(remoteResponse);
    }

    public SurahDetail fetchSurahDetail(Surah surah) {
        final Pair<String, SurahDetailResponse> remoteResponse = quranJsonService.getSurahDetailAtNumber(surah.getNumber());
        return parseSurahDetailResponseFromRemote(remoteResponse.second);
    }

    private List<Surah> parseSurahResponseFromRemote(Map<Integer, SurahResponse> remoteResponse) {
        final List<Surah> surahList = new ArrayList<>();
        for (SurahResponse surahResponse : remoteResponse.values()) {
            surahList.add(new Surah(
                    surahResponse.number,
                    surahResponse.name,
                    surahResponse.name_latin,
                    surahResponse.number_of_ayah
            ));
        }
        return surahList;
    }

    private SurahDetail parseSurahDetailResponseFromRemote(SurahDetailResponse remoteResponse) {
        final SurahTranslation surahTranslation = parseSurahTranslationsResponseToSurahTranslation(remoteResponse.translations, "id");
        final SurahTafsir surahTafsir = parseSurahTafsirResponseToSurahTafsir(remoteResponse.tafsir, "id", "kemenag");

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

    private SurahTranslation parseSurahTranslationsResponseToSurahTranslation(SurahTranslationsResponse translations, String locale) {
        final SurahTranslationResponse surahTranslation = translations.translations.get(locale);

        if (surahTranslation != null) {
            return new SurahTranslation(
                    surahTranslation.name,
                    surahTranslation.text
            );
        }

        return null;
    }

    private SurahTafsir parseSurahTafsirResponseToSurahTafsir(SurahTafsirsResponse tafsir, String locale, String release) {
        final SurahTafsirResponse tafsirResponse = tafsir.tafsir.get(locale);

        if (tafsirResponse != null) {
            final SurahTafsirSourceResponse tafsirSource = tafsirResponse.sources.get(release);
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
