package dev.fathony.android.quranlite.data.mapper;

import java.util.ArrayList;
import java.util.List;

import dev.fathony.android.quranlite.data.json.SearchIndexJSON;
import dev.fathony.android.quranlite.data.json.SurahJSON;
import dev.fathony.android.quranlite.models.SearchIndex;
import dev.fathony.android.quranlite.models.Surah;

public class SearchIndexToSearchIndexJSONMapper {

    public static List<SearchIndexJSON> map(List<SearchIndex> searchIndices) {
        final List<SearchIndexJSON> searchIndicesJSON = new ArrayList<>();
        for (int i = 0; i < searchIndices.size(); i++) {
            searchIndicesJSON.add(map(searchIndices.get(i)));
        }

        return searchIndicesJSON;
    }

    private static SearchIndexJSON map(SearchIndex searchIndex) {
        final SearchIndexJSON searchIndexJSON = new SearchIndexJSON();
        searchIndexJSON.surah = mapSurahToSurahJSON(searchIndex.getSurah());
        searchIndexJSON.indices = searchIndex.getIndices().clone();
        return searchIndexJSON;
    }

    private static SurahJSON mapSurahToSurahJSON(Surah surah) {
        final SurahJSON surahJSON = new SurahJSON();
        surahJSON.number = surah.getNumber();
        surahJSON.name = surah.getName();
        surahJSON.name_latin = surah.getNameInLatin();
        surahJSON.number_of_ayah = surah.getNumberOfAyah();
        return surahJSON;
    }
}
