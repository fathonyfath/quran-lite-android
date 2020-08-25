package id.fathonyfath.quran.lite.data;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import id.fathonyfath.quran.lite.data.json.SearchIndexJSON;
import id.fathonyfath.quran.lite.data.json.parser.SearchIndexJSONParser;
import id.fathonyfath.quran.lite.data.mapper.SearchIndexJSONToSearchIndexMapper;
import id.fathonyfath.quran.lite.data.mapper.SearchIndexToSearchIndexJSONMapper;
import id.fathonyfath.quran.lite.data.source.disk.SearchIndexDiskSource;
import id.fathonyfath.quran.lite.models.SearchIndex;

public class SearchIndexRepository {

    private final SearchIndexDiskSource searchIndexDiskSource;

    public SearchIndexRepository(SearchIndexDiskSource searchIndexDiskSource) {
        this.searchIndexDiskSource = searchIndexDiskSource;
    }

    public void saveSearchIndexes(List<SearchIndex> searchIndexes) {
        try {
            List<SearchIndexJSON> searchIndexesJSON = SearchIndexToSearchIndexJSONMapper.map(searchIndexes);
            JSONArray jsonArray = SearchIndexJSONParser.parseSearchIndexJSON(searchIndexesJSON);
            searchIndexDiskSource.saveSearchIndexes(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<SearchIndex> fetchSearchIndexes() {
        try {
            JSONArray jsonArray = searchIndexDiskSource.getSearchIndexes();
            List<SearchIndexJSON> searchIndexesJSON = SearchIndexJSONParser.parseJSONObject(jsonArray);
            return SearchIndexJSONToSearchIndexMapper.map(searchIndexesJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isSearchIndexesExist() {
        return searchIndexDiskSource.isSearchIndexesExist();
    }
}
