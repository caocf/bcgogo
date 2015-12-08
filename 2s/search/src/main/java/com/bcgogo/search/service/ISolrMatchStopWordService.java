package com.bcgogo.search.service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-8-1
 * Time: 上午9:03
 * To change this template use File | Settings | File Templates.
 */
public interface ISolrMatchStopWordService {

  List<String> getSolrMatchStopWordList();

  void saveSolrMatchStopWord(List<String> words);
}
