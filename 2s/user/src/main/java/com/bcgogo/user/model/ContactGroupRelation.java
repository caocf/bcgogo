package com.bcgogo.user.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.user.ContactType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-12-18
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "contact_group_relation")
public class ContactGroupRelation extends LongIdentifier {
  private Long contactId;
  private Long contactGroupId;
  private Long customerOrSupplierId;
  private ContactType contactType;
  private DeletedType deleted = DeletedType.FALSE;

  @Column(name = "contact_id")
  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
  }

  @Column(name = "contact_group_id")
  public Long getContactGroupId() {
    return contactGroupId;
  }

  public void setContactGroupId(Long contactGroupId) {
    this.contactGroupId = contactGroupId;
  }

  @Column(name = "customer_or_supplier_id")
  public Long getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public void setCustomerOrSupplierId(Long customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
  }

  @Column(name="contact_type")
  @Enumerated(EnumType.STRING)
  public ContactType getContactType() {
    return contactType;
  }

  public void setContactType(ContactType contactType) {
    this.contactType = contactType;
  }

  @Column(name="deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
