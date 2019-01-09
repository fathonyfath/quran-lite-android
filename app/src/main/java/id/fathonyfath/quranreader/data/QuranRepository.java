package id.fathonyfath.quranreader.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import id.fathonyfath.quranreader.data.remote.QuranJsonService;
import id.fathonyfath.quranreader.data.remote.models.SurahResponse;
import id.fathonyfath.quranreader.models.Surah;

public class QuranRepository {

    private final QuranJsonService quranJsonService;

    public QuranRepository(QuranJsonService quranJsonService) {
        this.quranJsonService = quranJsonService;
    }

    public List<Surah> fetchAllSurah() {
        Map<Integer, SurahResponse> remoteResponse = quranJsonService.getSurahIndex();
        return parseSurahResponseFromRemote(remoteResponse);
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
}
