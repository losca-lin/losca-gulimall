package com.losca.gulimall.search.service;

import com.losca.gulimall.search.vo.SearchParam;
import com.losca.gulimall.search.vo.SearchResult;

import java.io.IOException;

public interface SearchService {
    SearchResult search(SearchParam searchParam) throws IOException;
}
