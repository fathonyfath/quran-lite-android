package id.fathonyfath.quran.lite.data.mapper;

import java.util.ArrayList;
import java.util.List;

import id.fathonyfath.quran.lite.data.json.SearchIndexJSON;
import id.fathonyfath.quran.lite.data.json.SurahJSON;
import id.fathonyfath.quran.lite.models.SearchIndex;
import id.fathonyfath.quran.lite.models.Surah;

public class SearchIndexToSearchIndexJSONMapper {

    public static List<SearchIndexJSON> map(List<SearchIndex> searchIndexes) {
        final List<SearchIndexJSON> searchIndexesJSON = new ArrayList<>();
        for (int i = 0; i < searchIndexes.size(); i++) {
            searchIndexesJSON.add(map(searchIndexes.get(i)));
        }

        return searchIndexesJSON;
    }

    private static SearchIndexJSON map(SearchIndex searchIndex) {
        final SearchIndexJSON searchIndexJSON = new SearchIndexJSON();
        searchIndexJSON.surah = mapSurahToSurahJSON(searchIndex.getSurah());
        searchIndexJSON.indexes = searchIndex.getIndexes().clone();
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
