package com.bcgogo.search.util;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.search.service.ISolrMatchStopWordService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.MMSegUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-2-22
 * Time: 下午1:36
 * To change this template use File | Settings | File Templates.
 */
public class SolrUtil {
  public static final Logger LOG = LoggerFactory.getLogger(SolrUtil.class);

  public static final String MATCH_RULE_PARTICIPLE_ALL = "MATCH_RULE_PARTICIPLE_ALL";
  public static final String MATCH_RULE_PARTICIPLE_SINGLE = "MATCH_RULE_PARTICIPLE_SINGLE";

  /**
   * 如果searchWord为空 data不为空  算命中   searchword不为空  data为空 也算命中
   * 如果data为空， 就说明这次匹配 没有命中
   * 如果完全一样 直接先加 10  （看情况修改）
   * 接下来分词：1.data中包含分词的后String，那么hitCount 加 当前String的长度 length==1 过滤掉
   *
   * @param data
   * @param searchWord
   * @return
   * @throws Exception
   */
  public static double getImitateSolrMatchScore(String data, String searchWord, String matchRule) throws Exception {
    if (StringUtils.isBlank(searchWord)) {
      return 0.1d;
    }
    if (StringUtils.isBlank(data)) {
      return 0.1d;
    }

    int hitCount = 0;
    String newData = data.trim().toLowerCase();
    String newSearchWord = searchWord.trim().toLowerCase();
    if (newSearchWord.equals(newData)) {
      hitCount += 100d;
    }
    List<String> searchWordParticiples = MMSegUtil.getTockenBySimpleSeg(newSearchWord);
    List<String> dataParticiples = MMSegUtil.getTockenBySimpleSeg(newData);


    Predicate predicate = new Predicate() {
      @Override
      public boolean evaluate(Object object) {
        ISolrMatchStopWordService solrMatchStopWordService = ServiceManager.getService(ISolrMatchStopWordService.class);
        List<String> solrMatchStopWordList = solrMatchStopWordService.getSolrMatchStopWordList();
        return (object instanceof String) && (StringUtils.length(object.toString()) > 1 && !solrMatchStopWordList.contains(object.toString()));
      }
    };
    CollectionUtils.filter(searchWordParticiples, predicate);
    CollectionUtils.filter(dataParticiples, predicate);

    if (MATCH_RULE_PARTICIPLE_ALL.equals(matchRule)) {
      if (CollectionUtils.isEqualCollection(searchWordParticiples, dataParticiples)) {
        hitCount += 50d;
      }
    } else {
      searchWordParticiples.retainAll(dataParticiples);
      for (String s : searchWordParticiples) {
        hitCount += s.length();
//      hitCount+=s.length()*(s.length()>1?10:1)/newSearchWord.length();
//      hitCount+=s.length()>1?1:0.1d;
      }
    }

    return hitCount;
  }

  public static double getAreaMatchScore(ShopDTO seedShopDTO, ShopDTO shopDTO) {
    if (seedShopDTO == null || shopDTO == null) return 0d;
    if (seedShopDTO.getCity() != null) {
      if (seedShopDTO.getCity().equals(shopDTO.getCity())) return 3d;
    }
    if (seedShopDTO.getProvince() != null) {
      if (seedShopDTO.getProvince().equals(shopDTO.getProvince())) return 2d;
    }
    return 1d;
  }

  public static int getSolrInputDocumentSize(SolrInputDocument doc) {
    StringBuffer sb = new StringBuffer();
    for (SolrInputField solrInputField : doc.values()) {
      if (CollectionUtils.isNotEmpty(solrInputField.getValues())) {
        for (Object o : solrInputField.getValues()) {
          sb.append(o == null ? "" : o.toString());
        }
      }
    }
    return sb.toString().getBytes().length;
  }



}
