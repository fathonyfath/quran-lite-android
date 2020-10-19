package id.thony.android.quranlite.data;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import id.thony.android.quranlite.data.json.SearchIndexJSON;
import id.thony.android.quranlite.data.json.parser.SearchIndexJSONParser;
import id.thony.android.quranlite.data.mapper.SearchIndexJSONToSearchIndexMapper;
import id.thony.android.quranlite.data.mapper.SearchIndexToSearchIndexJSONMapper;
import id.thony.android.quranlite.data.source.disk.SearchIndexDiskSource;
import id.thony.android.quranlite.models.SearchIndex;

public class SearchIndexRepository {

    private final SearchIndexDiskSource searchIndexDiskSource;

    public SearchIndexRepository(SearchIndexDiskSource searchIndexDiskSource) {
        this.searchIndexDiskSource = searchIndexDiskSource;
    }

    public void saveSearchIndices(List<SearchIndex> searchIndices) {
        try {
            List<SearchIndexJSON> searchIndicesJSON = SearchIndexToSearchIndexJSONMapper.map(searchIndices);
            JSONArray jsonArray = SearchIndexJSONParser.parseSearchIndexJSON(searchIndicesJSON);
            searchIndexDiskSource.saveSearchIndices(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<SearchIndex> fetchSearchIndices() {
        try {
            JSONArray jsonArray = searchIndexDiskSource.getSearchIndices();
            List<SearchIndexJSON> searchIndicesJSON = SearchIndexJSONParser.parseJSONObject(jsonArray);
            return SearchIndexJSONToSearchIndexMapper.map(searchIndicesJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isSearchIndicesExist() {
        return searchIndexDiskSource.isSearchIndicesExist();
    }
}
