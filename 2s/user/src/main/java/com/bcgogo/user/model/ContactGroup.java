package com.bcgogo.user.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.notification.ContactGroupType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.ContactGroupDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-12-18
 * Time: 下午3:58
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "contact_group")
public class ContactGroup extends LongIdentifier{
  private Long shopId;
  private String name;
  private ContactGroupType contactGroupType;
  private DeletedType deleted = DeletedType.FALSE;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name="contact_group_type")
  @Enumerated(EnumType.STRING)
  public ContactGroupType getContactGroupType() {
    return contactGroupType;
  }

  public void setContactGroupType(ContactGroupType contactGroupType) {
    this.contactGroupType = contactGroupType;
  }

  @Column(name="deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public ContactGroupDTO toDTO(){
    ContactGroupDTO groupDTO=new ContactGroupDTO();
    groupDTO.setId(this.getId());
    groupDTO.setShopId(this.getShopId());
    groupDTO.setName(this.getName());
    groupDTO.setContactGroupType(this.getContactGroupType());
    return groupDTO;
  }

}
