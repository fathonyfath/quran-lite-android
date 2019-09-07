package id.fathonyfath.quranlite.data_old;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import id.fathonyfath.quranlite.data_old.disk.QuranDiskService;
import id.fathonyfath.quranlite.data_old.models.SurahDetailResponse;
import id.fathonyfath.quranlite.data_old.models.SurahResponse;
import id.fathonyfath.quranlite.data_old.models.SurahTafsirResponse;
import id.fathonyfath.quranlite.data_old.models.SurahTafsirSourceResponse;
import id.fathonyfath.quranlite.data_old.models.SurahTafsirsResponse;
import id.fathonyfath.quranlite.data_old.models.SurahTranslationResponse;
import id.fathonyfath.quranlite.data_old.models.SurahTranslationsResponse;
import id.fathonyfath.quranlite.data_old.remote.OnDownloadProgressListener;
import id.fathonyfath.quranlite.data_old.remote.QuranJsonService;
import id.fathonyfath.quranlite.models.Surah;
import id.fathonyfath.quranlite.models.SurahDetail;
import id.fathonyfath.quranlite.models.SurahTafsir;
import id.fathonyfath.quranlite.models.SurahTranslation;

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

    private final QuranDiskService quranDiskService;

    private OnProgressListener onProgressListener;

    public QuranRepository(QuranJsonService quranJsonService, QuranDiskService quranDiskService) {
        this.quranJsonService = quranJsonService;
        this.quranJsonService.setOnDownloadProgressListener(onDownloadProgressListener);

        this.quranDiskService = quranDiskService;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public List<Surah> fetchAllSurah() {
        Map<Integer, SurahResponse> response;
        if (this.quranDiskService.isSurahIndexExist()) {
            response = this.quranDiskService.getSurahIndex();
        } else {
            response = this.quranJsonService.getSurahIndex();
        }

        return parseSurahResponseFromRemote(response);
    }

    public SurahDetail fetchSurahDetail(Surah surah) {
        Pair<String, SurahDetailResponse> response;
        if (this.quranDiskService.isSurahDetailAtNumberExist(surah.getNumber())) {
            response = this.quranDiskService.getSurahDetailAtNumber(surah.getNumber());
        } else {
            response = this.quranJsonService.getSurahDetailAtNumber(surah.getNumber());
        }

        return parseSurahDetailResponseFromRemote(response.second);
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
