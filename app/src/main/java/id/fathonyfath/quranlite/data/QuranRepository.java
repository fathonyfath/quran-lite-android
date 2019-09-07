package id.fathonyfath.quranlite.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import id.fathonyfath.quranlite.data.json.SurahDetailJSON;
import id.fathonyfath.quranlite.data.json.SurahJSON;
import id.fathonyfath.quranlite.data.json.parser.SurahDetailJSONParser;
import id.fathonyfath.quranlite.data.json.parser.SurahJSONParser;
import id.fathonyfath.quranlite.data.mapper.SurahDetailMapper;
import id.fathonyfath.quranlite.data.mapper.SurahMapper;
import id.fathonyfath.quranlite.data.source.disk.QuranDiskSource;
import id.fathonyfath.quranlite.data.source.network.QuranNetworkSource;
import id.fathonyfath.quranlite.models.Surah;
import id.fathonyfath.quranlite.models.SurahDetail;
import id.fathonyfath.quranlite.utils.network.NetworkHelper;

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
        }

        try {
            final Map<Integer, SurahJSON> parsedJSON = SurahJSONParser.parseJSONObject(surahIndexJSON);
            return SurahMapper.map(parsedJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public SurahDetail fetchSurahDetail(Surah surah) {
        final JSONObject surahDetailJSON;
        if (this.quranDiskSource.isSurahDetailAtNumberExist(surah.getNumber())) {
            surahDetailJSON = this.quranDiskSource.getSurahDetailAtNumber(surah.getNumber());
        } else {
            surahDetailJSON = this.quranNetworkSource.getSurahDetailAtNumber(
                    surah.getNumber(),
                    null,
                    null);
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
