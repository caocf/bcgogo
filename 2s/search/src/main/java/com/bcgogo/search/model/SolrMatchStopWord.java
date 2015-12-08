package com.bcgogo.search.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *在精确推送匹配的时候 忽略一些没有意义的词组:例如：总成
 */
@Entity
@Table(name = "solr_match_stop_word")
public class SolrMatchStopWord extends LongIdentifier {
  private Long shopId;
  private String word;

  @Column(name = "word", length = 200)
  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }
  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
}
