package id.fathonyfath.quran.lite.useCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import id.fathonyfath.quran.lite.data.QuranRepository;
import id.fathonyfath.quran.lite.data.SearchIndexRepository;
import id.fathonyfath.quran.lite.models.SearchIndex;
import id.fathonyfath.quran.lite.models.Surah;
import id.fathonyfath.quran.lite.utils.scheduler.Schedulers;

public class DoSearchUseCase extends BaseUseCase {

    private final QuranRepository quranRepository;
    private final SearchIndexRepository searchIndexRepository;

    private final int nGramValue = 2;
    private final float coefficientThreshold = 0.75f;

    private UseCaseCallback<List<Surah>> callback;
    private String searchQuery;

    public DoSearchUseCase(QuranRepository quranRepository, SearchIndexRepository searchIndexRepository) {
        this.quranRepository = quranRepository;
        this.searchIndexRepository = searchIndexRepository;
    }

    public void setArguments(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    protected void task() {
        Schedulers.IO().execute(new Runnable() {
            @Override
            public void run() {
                startSearchProcess();
            }
        });
    }

    private void startSearchProcess() {
        if (!searchIndexRepository.isSearchIndexesExist()) {
            final List<Surah> surahList = quranRepository.fetchAllSurah(null, null);
            Schedulers.Computation().execute(new Runnable() {
                @Override
                public void run() {
                    final List<SearchIndex> searchIndexes = createSearchIndexesForSurahList(surahList);
                    Schedulers.IO().execute(new Runnable() {
                        @Override
                        public void run() {
                            searchIndexRepository.saveSearchIndexes(searchIndexes);

                            getSearchIndexesAndDoSearch();
                        }
                    });
                }
            });
        } else {
            getSearchIndexesAndDoSearch();
        }
    }

    private List<SearchIndex> createSearchIndexesForSurahList(List<Surah> surahList) {
        final List<SearchIndex> searchIndexes = new ArrayList<>();
        for (Surah surah : surahList) {
            searchIndexes.add(createSearchIndex(surah));
        }

        return searchIndexes;
    }

    private SearchIndex createSearchIndex(Surah surah) {
        final String keywordsBuilder = "qs. quran surat " + surah.getNameInLatin() + " nomor surat : " + surah.getNumber();
        final String keywords = keywordsBuilder.toLowerCase();
        final int indexesLength = keywords.length() - nGramValue + 1;
        final String[] indexes = new String[indexesLength];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = keywords.substring(i, i + nGramValue);
        }

        return new SearchIndex(surah, indexes);
    }

    private void getSearchIndexesAndDoSearch() {
        final List<SearchIndex> searchIndexes = searchIndexRepository.fetchSearchIndexes();
        Schedulers.Computation().execute(new Runnable() {
            @Override
            public void run() {
                doSearchOnSearchIndexes(searchIndexes);
            }
        });
    }

    private void doSearchOnSearchIndexes(List<SearchIndex> searchIndexes) {
        Map<SearchIndex, Float> searchIndexToCoefficient = new HashMap<>();

        String[] searchQueryIndexes = new String[this.searchQuery.length() - nGramValue + 1];
        for (int i = 0; i < searchQueryIndexes.length; i++) {
            searchQueryIndexes[i] = this.searchQuery.substring(i, i + nGramValue);
        }

        for (SearchIndex searchIndex : searchIndexes) {
            searchIndexToCoefficient.put(searchIndex, calculateDifferentialsBetween(
                    searchIndex.getIndexes(), searchQueryIndexes
            ));
        }

        searchIndexToCoefficient = sortByValue(searchIndexToCoefficient);

        List<Surah> searchResult = new ArrayList<>();

        for (Map.Entry<SearchIndex, Float> entry : searchIndexToCoefficient.entrySet()) {
            if (entry.getValue() > coefficientThreshold) {
                searchResult.add(entry.getKey().getSurah());
            }
        }

        postResultToMainThread(searchResult);
    }

    private void postResultToMainThread(final List<Surah> searchResult) {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResult(searchResult);
                }
            }
        });
    }

    private float calculateDifferentialsBetween(String[] surahIndex, String[] searchQueryIndex) {
        int commonCount = 0;
        for (String sourceIndex : surahIndex) {
            for (String targetIndex : searchQueryIndex) {
                if (sourceIndex.equalsIgnoreCase(targetIndex)) {
                    commonCount++;
                }
            }
        }

        float commonFloat = (float) commonCount;
        float dividerFloat = Math.max((float) surahIndex.length, (float) searchQueryIndex.length);
        return commonFloat / dividerFloat;
    }

    private Map<SearchIndex, Float> sortByValue(Map<SearchIndex, Float> map) {
        List<Map.Entry<SearchIndex, Float>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, comparingByValue());

        Map<SearchIndex, Float> result = new LinkedHashMap<>();
        for (Map.Entry<SearchIndex, Float> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private Comparator<Map.Entry<SearchIndex, Float>> comparingByValue() {
        return new Comparator<Map.Entry<SearchIndex, Float>>() {
            @Override
            public int compare(Map.Entry<SearchIndex, Float> o1, Map.Entry<SearchIndex, Float> o2) {
                return Float.compare(o2.getValue(), o1.getValue());
            }
        };
    }

    public static class Factory implements UseCaseFactory<DoSearchUseCase> {

        private final QuranRepository quranRepository;
        private final SearchIndexRepository searchIndexRepository;

        public Factory(QuranRepository quranRepository, SearchIndexRepository searchIndexRepository) {
            this.quranRepository = quranRepository;
            this.searchIndexRepository = searchIndexRepository;
        }

        @Override
        public DoSearchUseCase create() {
            return new DoSearchUseCase(this.quranRepository, this.searchIndexRepository);
        }
    }
}
