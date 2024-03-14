package dev.fathony.android.quranlite.useCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.fathony.android.quranlite.data.QuranRepository;
import dev.fathony.android.quranlite.data.SearchIndexRepository;
import dev.fathony.android.quranlite.models.SearchIndex;
import dev.fathony.android.quranlite.models.Surah;
import dev.fathony.android.quranlite.utils.scheduler.Schedulers;

public class DoSearchUseCase extends BaseUseCase {

    private final QuranRepository quranRepository;
    private final SearchIndexRepository searchIndexRepository;

    private final int nGramValue = 2;
    private final float coefficientThreshold = 0.15f;

    private UseCaseCallback<List<Surah>> callback;
    private String searchQuery;

    public DoSearchUseCase(QuranRepository quranRepository, SearchIndexRepository searchIndexRepository) {
        this.quranRepository = quranRepository;
        this.searchIndexRepository = searchIndexRepository;
    }

    public void setArguments(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setCallback(UseCaseCallback<List<Surah>> callback) {
        this.callback = callback;
    }

    @Override
    protected void task() {
        if (searchQuery == null || searchQuery.isEmpty() || searchQuery.length() < nGramValue) {
            postErrorToMainThread(new IllegalStateException("Search query does not met the requirement."));
            return;
        }

        Schedulers.IO().execute(new Runnable() {
            @Override
            public void run() {
                startSearchProcess();
            }
        });
    }

    private void startSearchProcess() {
        if (!searchIndexRepository.isSearchIndicesExist() || !checkIfNGramsIsSame()) {
            final List<Surah> surahList = quranRepository.fetchAllSurah(null, null);
            Schedulers.Computation().execute(new Runnable() {
                @Override
                public void run() {
                    final List<SearchIndex> searchIndices = createSearchIndicesForSurahList(surahList);
                    Schedulers.IO().execute(new Runnable() {
                        @Override
                        public void run() {
                            searchIndexRepository.saveSearchIndices(searchIndices);

                            getSearchIndicesAndDoSearch();
                        }
                    });
                }
            });
        } else {
            getSearchIndicesAndDoSearch();
        }
    }

    private boolean checkIfNGramsIsSame() {
        final List<SearchIndex> searchIndices = searchIndexRepository.fetchSearchIndices();

        if (searchIndices == null) {
            return false;
        }

        if (searchIndices.isEmpty()) {
            return false;
        }

        final SearchIndex firstSearchIndex = searchIndices.get(0);
        if (firstSearchIndex.getIndices().length == 0) {
            return false;
        }

        final String index = firstSearchIndex.getIndices()[0];
        return !index.isEmpty() && index.length() == nGramValue;
    }

    private List<SearchIndex> createSearchIndicesForSurahList(List<Surah> surahList) {
        final List<SearchIndex> searchIndices = new ArrayList<>();
        for (Surah surah : surahList) {
            searchIndices.add(createSearchIndex(surah));
        }

        return searchIndices;
    }

    private SearchIndex createSearchIndex(Surah surah) {
        final String keywordsBuilder = surah.getNameInLatin() + " " + surah.getNumber();
        final String keywords = keywordsBuilder.toLowerCase();
        final int indicesLength = keywords.length() - nGramValue + 1;
        final String[] indices = new String[indicesLength];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = keywords.substring(i, i + nGramValue);
        }

        return new SearchIndex(surah, indices);
    }

    private void getSearchIndicesAndDoSearch() {
        final List<SearchIndex> searchIndices = searchIndexRepository.fetchSearchIndices();
        Schedulers.Computation().execute(new Runnable() {
            @Override
            public void run() {
                doSearchOnSearchIndices(searchIndices);
            }
        });
    }

    private void doSearchOnSearchIndices(List<SearchIndex> searchIndices) {
        Map<SearchIndex, Float> searchIndexToCoefficient = new HashMap<>();

        String[] searchQueryIndices = new String[this.searchQuery.length() - nGramValue + 1];
        for (int i = 0; i < searchQueryIndices.length; i++) {
            searchQueryIndices[i] = this.searchQuery.substring(i, i + nGramValue);
        }

        for (SearchIndex searchIndex : searchIndices) {
            searchIndexToCoefficient.put(searchIndex, calculateDifferentialsBetween(
                    searchIndex.getIndices(), searchQueryIndices
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

    private void postErrorToMainThread(final Exception e) {
        Schedulers.Main().execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    private float calculateDifferentialsBetween(String[] surahIndex, String[] searchQueryIndex) {
        int commonCount = 0;
        final Set<String> uniqueKeyword = new HashSet<>();

        uniqueKeyword.addAll(Arrays.asList(surahIndex));
        uniqueKeyword.addAll(Arrays.asList(searchQueryIndex));

        for (String sourceIndex : surahIndex) {
            for (String targetIndex : searchQueryIndex) {
                if (sourceIndex.equalsIgnoreCase(targetIndex)) {
                    commonCount++;
                }
            }
        }

        float commonFloat = (float) commonCount;
        float dividerFloat = uniqueKeyword.size();
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
