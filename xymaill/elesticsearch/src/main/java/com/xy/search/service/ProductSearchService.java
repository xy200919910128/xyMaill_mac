package com.xy.search.service;


import com.xy.search.vo.SearchParam;
import com.xy.search.vo.SearchResult;

public interface ProductSearchService {
    public SearchResult getSearchResult(SearchParam searchParam);
}
