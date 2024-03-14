package dev.fathony.android.quranlite.data.json.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dev.fathony.android.quranlite.data.json.SearchIndexJSON;

public class SearchIndexJSONParser {

    public static List<SearchIndexJSON> parseJSONObject(JSONArray jsonArray) throws JSONException {
        final List<SearchIndexJSON> searchIndices = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            searchIndices.add(parseJSONObjectToSearchIndexJSON(jsonArray.getJSONObject(i)));
        }

        return searchIndices;
    }

    public static JSONArray parseSearchIndexJSON(List<SearchIndexJSON> searchIndicesJSON) throws JSONException {
        final JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < searchIndicesJSON.size(); i++) {
            jsonArray.put(parseSearchIndexJSONToJSONObject(searchIndicesJSON.get(i)));
        }

        return jsonArray;
    }

    private static SearchIndexJSON parseJSONObjectToSearchIndexJSON(JSONObject jsonObject) throws JSONException {
        final SearchIndexJSON searchIndex = new SearchIndexJSON();
        final JSONArray indicesJSON = jsonObject.getJSONArray("indices");
        final String[] indices = new String[indicesJSON.length()];
        for (int i = 0; i < indicesJSON.length(); i++) {
            indices[i] = indicesJSON.getString(i);
        }

        searchIndex.surah = SurahJSONParser.parseJSONObjectToSurahJSON(jsonObject.getJSONObject("surah"));
        searchIndex.indices = indices;

        return searchIndex;
    }

    private static JSONObject parseSearchIndexJSONToJSONObject(SearchIndexJSON searchIndexJSON) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < searchIndexJSON.indices.length; i++) {
            jsonArray.put(searchIndexJSON.indices[i]);
        }

        jsonObject.put("surah", SurahJSONParser.parseSurahJSONToJSONObject(searchIndexJSON.surah));
        jsonObject.put("indices", jsonArray);
        return jsonObject;
    }
}
