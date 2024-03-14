package dev.fathony.android.quranlite.data.mapper;

import java.util.ArrayList;
import java.util.List;

import dev.fathony.android.quranlite.data.json.SearchIndexJSON;
import dev.fathony.android.quranlite.data.json.SurahJSON;
import dev.fathony.android.quranlite.models.SearchIndex;
import dev.fathony.android.quranlite.models.Surah;

public class SearchIndexJSONToSearchIndexMapper {

    public static List<SearchIndex> map(List<SearchIndexJSON> searchIndicesJSON) {
        final List<SearchIndex> searchIndices = new ArrayList<>();
        for (int i = 0; i < searchIndicesJSON.size(); i++) {
            searchIndices.add(map(searchIndicesJSON.get(i)));
        }

        return searchIndices;
    }

    private static SearchIndex map(SearchIndexJSON searchIndexJSON) {
        return new SearchIndex(
                mapSurahJSONToSurah(searchIndexJSON.surah),
                searchIndexJSON.indices.clone()
        );
    }

    private static Surah mapSurahJSONToSurah(SurahJSON surahJSON) {
        return new Surah(
                surahJSON.number,
                surahJSON.name,
                surahJSON.name_latin,
                surahJSON.number_of_ayah
        );
    }
}
