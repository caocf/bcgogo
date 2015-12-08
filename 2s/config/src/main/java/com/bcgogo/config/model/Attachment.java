package com.bcgogo.config.model;

import com.bcgogo.config.dto.AttachmentDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.config.AttachmentType;
import com.bcgogo.model.LongIdentifier;
import org.apache.commons.lang.ArrayUtils;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: XiaoJian
 * Date: 10/10/11
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "attachment")
public class Attachment extends LongIdentifier {
  private String name;
  private byte[] content;
  private AttachmentType type;
  private Long shopId;
  private String memo;

  public AttachmentDTO toDTO(){
    AttachmentDTO attachmentDTO = new AttachmentDTO();
    attachmentDTO.setId(this.getId());
    attachmentDTO.setName(this.getName());
    attachmentDTO.setContent(this.getContent());
    attachmentDTO.setShopId(this.getShopId());
    attachmentDTO.setMemo(this.getMemo());
    return attachmentDTO;
  }

  public void setShopPhoto(ShopDTO shopDTO) {
    if (shopDTO != null) {
      this.setShopId(shopDTO.getId());
      this.setType(AttachmentType.SHOP_APPEARANCE_PHOTO);
      this.setContent(shopDTO.getShopPhoto());
      this.setName(shopDTO.getPhoto());
    }
  }

  public void setShopBusinessLicensePhoto(ShopDTO shopDTO){
    if (shopDTO != null) {
        this.setShopId(shopDTO.getId());
        this.setType(AttachmentType.SHOP_BUSINESS_LICENSE_PHOTO);
        this.setContent(shopDTO.getBusinessLicense());
        this.setName(shopDTO.getBusinessLicenseName());
      }
  }

  public Attachment() {
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "content")
  @Lob
  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  public AttachmentType getType() {
    return type;
  }

  public void setType(AttachmentType type) {
    this.type = type;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
}