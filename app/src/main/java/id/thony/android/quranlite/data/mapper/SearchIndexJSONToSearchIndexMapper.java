package id.thony.android.quranlite.data.mapper;

import java.util.ArrayList;
import java.util.List;

import id.thony.android.quranlite.data.json.SearchIndexJSON;
import id.thony.android.quranlite.data.json.SurahJSON;
import id.thony.android.quranlite.models.SearchIndex;
import id.thony.android.quranlite.models.Surah;

public class SearchIndexJSONToSearchIndexMapper {

    public static List<SearchIndex> map(List<SearchIndexJSON> searchIndexesJSON) {
        final List<SearchIndex> searchIndexes = new ArrayList<>();
        for (int i = 0; i < searchIndexesJSON.size(); i++) {
            searchIndexes.add(map(searchIndexesJSON.get(i)));
        }

        return searchIndexes;
    }

    private static SearchIndex map(SearchIndexJSON searchIndexJSON) {
        return new SearchIndex(
                mapSurahJSONToSurah(searchIndexJSON.surah),
                searchIndexJSON.indexes.clone()
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
