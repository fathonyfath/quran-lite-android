package id.fathonyfath.quran.lite.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import id.fathonyfath.quran.lite.data.json.SurahDetailJSON;
import id.fathonyfath.quran.lite.data.json.SurahJSON;
import id.fathonyfath.quran.lite.data.json.parser.SurahDetailJSONParser;
import id.fathonyfath.quran.lite.data.json.parser.SurahJSONParser;
import id.fathonyfath.quran.lite.data.mapper.SurahDetailMapper;
import id.fathonyfath.quran.lite.data.mapper.SurahMapper;
import id.fathonyfath.quran.lite.data.source.disk.QuranDiskSource;
import id.fathonyfath.quran.lite.data.source.network.QuranNetworkSource;
import id.fathonyfath.quran.lite.models.Surah;
import id.fathonyfath.quran.lite.models.SurahDetail;
import id.fathonyfath.quran.lite.utils.network.NetworkHelper;

public class QuranRepository {

    private final QuranDiskSource quranDiskSource;
    private final QuranNetworkSource quranNetworkSource;

    public QuranRepository(QuranDiskSource quranDiskSource, QuranNetworkSource quranNetworkSource) {
        this.quranDiskSource = quranDiskSource;
        this.quranNetworkSource = quranNetworkSource;
    }

    public List<Surah> fetchAllSurah(NetworkHelper.CancelSignal cancellationSignal,
                                     NetworkHelper.ProgressListener networkProgressListener) {
        final JSONObject surahIndexJSON;
        if (this.quranDiskSource.isSurahIndexExist()) {
            surahIndexJSON = this.quranDiskSource.getSurahIndex();
        } else {
            surahIndexJSON = this.quranNetworkSource.getSurahIndex(
                    cancellationSignal,
                    networkProgressListener);

            if (surahIndexJSON != null) {
                this.quranDiskSource.saveSurahIndex(surahIndexJSON);
            }
        }

        if (surahIndexJSON == null) {
            return null;
        }

        try {
            final Map<Integer, SurahJSON> parsedJSON = SurahJSONParser.parseJSONObject(surahIndexJSON);
            return SurahMapper.map(parsedJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public SurahDetail fetchSurahDetail(Surah surah,
                                        NetworkHelper.CancelSignal cancellationSignal,
                                        NetworkHelper.ProgressListener networkProgressListener) {
        final JSONObject surahDetailJSON;
        if (this.quranDiskSource.isSurahDetailAtNumberExist(surah.getNumber())) {
            surahDetailJSON = this.quranDiskSource.getSurahDetailAtNumber(surah.getNumber());
        } else {
            surahDetailJSON = this.quranNetworkSource.getSurahDetailAtNumber(
                    surah.getNumber(),
                    cancellationSignal,
                    networkProgressListener);

            if (surahDetailJSON != null) {
                this.quranDiskSource.saveSurahDetailAtNumber(surah.getNumber(), surahDetailJSON);
            }
        }

        if (surahDetailJSON == null) {
            return null;
        }

        try {
            final SurahDetailJSON parsedJSON = SurahDetailJSONParser.parseJSONObject(surahDetailJSON);
            return SurahDetailMapper.map(parsedJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
