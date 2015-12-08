package com.bcgogo.search.service;

import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.search.model.SearchWriter;
import com.bcgogo.search.model.SolrMatchStopWord;
import com.bcgogo.utils.ConfigConstant;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-8-1
 * Time: 上午9:04
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SolrMatchStopWordService implements ISolrMatchStopWordService {
  private static final Logger LOG = LoggerFactory.getLogger(SolrMatchStopWordService.class);
  @Autowired
  private SearchDaoManager searchDaoManager;

  //从memcache中取常用数据，如果不存在，先从数据库查找，然后放入memcache中
  @Override
  public List<String> getSolrMatchStopWordList() {
    String memCacheKey = getMemCacheKey(ConfigConstant.CONFIG_SHOP_ID);
    List<String> solrMatchStopWordList = (List<String>) MemCacheAdapter.get(memCacheKey);
    if (CollectionUtils.isEmpty(solrMatchStopWordList)) {
      solrMatchStopWordList = new ArrayList<String>();
      SearchWriter writer = searchDaoManager.getWriter();
      List<SolrMatchStopWord> solrMatchStopWords = writer.getAllSolrMatchStopWordList(ConfigConstant.CONFIG_SHOP_ID);
      if(CollectionUtils.isNotEmpty(solrMatchStopWords)){
        for(SolrMatchStopWord solrMatchStopWord:solrMatchStopWords){
          solrMatchStopWordList.add(solrMatchStopWord.getWord());
        }
      }
      MemCacheAdapter.add(memCacheKey, solrMatchStopWordList);
    }
    return solrMatchStopWordList;
  }

  @Override
  public void saveSolrMatchStopWord(List<String> words) {
    if(CollectionUtils.isNotEmpty(words)){
      SearchWriter writer = searchDaoManager.getWriter();
      Object status = writer.begin();
      try {
        for(String word:words){
          SolrMatchStopWord solrMatchStopWord = new SolrMatchStopWord();
          solrMatchStopWord.setWord(word);
          solrMatchStopWord.setShopId(ConfigConstant.CONFIG_SHOP_ID);
          writer.save(solrMatchStopWord);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
  }

  //memcache的KEY
  private String getMemCacheKey(Long shopId) {
    return MemcachePrefix.solrMatchStopWord.getValue() + String.valueOf(shopId);
  }
}
