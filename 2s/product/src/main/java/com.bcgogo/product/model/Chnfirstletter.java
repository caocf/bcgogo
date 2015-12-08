package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.ChnfirstletterDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zcl
 * Date: 12-2-19
 * Time: 上午9:46
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "chnfirstletter")
public class Chnfirstletter extends LongIdentifier {
  private String hanzi;
  private String py;
  private String firstLetter;

  public Chnfirstletter() {
  }

  public ChnfirstletterDTO toDTO() {
    ChnfirstletterDTO chnDTO = new ChnfirstletterDTO();
    chnDTO.setHanzi(this.getHanzi());
    chnDTO.setPy(this.getPy());
    chnDTO.setFirstLetter(this.getFirstLetter());
    return chnDTO;
  }

  @Column(name = "hanzi", length = 10)
  public String getHanzi() {
    return hanzi;
  }

  public void setHanzi(String hanzi) {
    this.hanzi = hanzi;
  }

  @Column(name = "py", length = 20)
  public String getPy() {
    return py;
  }

  public void setPy(String py) {
    this.py = py;
  }

  @Column(name = "firstLetter", length = 5)
  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }
}
