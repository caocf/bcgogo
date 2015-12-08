package com.bcgogo.product.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: WWW
 * Date: 12-2-19
 * Time: 上午9:54
 * To change this template use File | Settings | File Templates.
 */
public class ChnfirstletterDTO implements Serializable {
  private String hanzi ;
  private String py;
  private  String firstLetter;

  public ChnfirstletterDTO(){}
  public String getHanzi() {
    return hanzi;
  }

  public void setHanzi(String hanzi) {
    this.hanzi = hanzi;
  }

  public String getPy() {
    return py;
  }

  public void setPy(String py) {
    this.py = py;
  }

  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }


}
