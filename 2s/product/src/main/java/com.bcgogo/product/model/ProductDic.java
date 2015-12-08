package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "product_dic")
public class ProductDic extends LongIdentifier {
  private String word;
  private Integer count;
  private String type;
  private String pinyinSort;

  @Column(name = "word")
  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  @Column(name = "count")
  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }
  @Column(name = "type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
  @Column(name = "pinyin_sort")
  public String getPinyinSort() {
    return pinyinSort;
  }

  public void setPinyinSort(String pinyinSort) {
    this.pinyinSort = pinyinSort;
  }
}