package id.fathonyfath.quran.lite.data.json.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.fathonyfath.quran.lite.data.json.SearchIndexJSON;

public class SearchIndexJSONParser {

    public static List<SearchIndexJSON> parseJSONObject(JSONArray jsonArray) throws JSONException {
        final List<SearchIndexJSON> searchIndexes = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            searchIndexes.add(parseJSONObjectToSearchIndexJSON(jsonArray.getJSONObject(i)));
        }

        return searchIndexes;
    }

    public static JSONArray parseSearchIndexJSON(List<SearchIndexJSON> searchIndexesJSON) throws JSONException {
        final JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < searchIndexesJSON.size(); i++) {
            jsonArray.put(parseSearchIndexJSONToJSONObject(searchIndexesJSON.get(i)));
        }

        return jsonArray;
    }

    private static SearchIndexJSON parseJSONObjectToSearchIndexJSON(JSONObject jsonObject) throws JSONException {
        final SearchIndexJSON searchIndex = new SearchIndexJSON();
        final JSONArray indexesJSON = jsonObject.getJSONArray("indexes");
        final String[] indexes = new String[indexesJSON.length()];
        for (int i = 0; i < indexesJSON.length(); i++) {
            indexes[i] = indexesJSON.getString(i);
        }

        searchIndex.surah = SurahJSONParser.parseJSONObjectToSurahJSON(jsonObject.getJSONObject("surah"));
        searchIndex.indexes = indexes;

        return searchIndex;
    }

    private static JSONObject parseSearchIndexJSONToJSONObject(SearchIndexJSON searchIndexJSON) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < searchIndexJSON.indexes.length; i++) {
            jsonArray.put(searchIndexJSON.indexes[i]);
        }

        jsonObject.put("surah", SurahJSONParser.parseSurahJSONToJSONObject(searchIndexJSON.surah));
        jsonObject.put("indexes", jsonArray);
        return jsonObject;
    }
}
