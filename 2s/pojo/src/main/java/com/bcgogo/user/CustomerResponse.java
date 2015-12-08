package com.bcgogo.user;

import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-12-15
 * Time: 下午12:11
 * To change this template use File | Settings | File Templates.
 */
public class CustomerResponse implements Comparable<CustomerResponse>{

  private Long shopId;
  private Long customerId;
  private String unit;
  private String name;
  private String mobile;
  private String address;
  private double totalAmount;
  private double totalArrears;
  private int vehicleAmount;
  private String lastLicenceNo;
  private String lastTime;
  private double totalReturnAmount;

  private String contact;
  private String shortName;

  private String memberNumber;
  private String landLine;    //座机
  private Long birthDay;
  private String birthdayString;
  private String fax;   //传真
  private String qq;
  private String email;
  private Long lastId;
  private String type;
  private double totalPayable;
  private ContactDTO[] contacts ; // add by zhuj　联系人列表

  public ContactDTO[] getContacts() {
    return contacts;
  }

  public void setContacts(ContactDTO[] contacts) {
    this.contacts = contacts;
  }

  public String getType() {
    return type;
  }

    public Long getShopId() {
        return shopId;
    }

    public void setType(String type) {
    this.type = type;
  }

  public Long getLastId() {
    return lastId;
  }

  public void setLastId(Long lastId) {
    this.lastId = lastId;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getMemberNumber() {
    return memberNumber;
  }

  public void setMemberNumber(String memberNumber) {
    this.memberNumber = memberNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLandLine() {
    return landLine;
  }

  public void setLandLine(String landLine) {
    this.landLine = landLine;
  }

  public Long getBirthDay() {
    return birthDay;
  }

  public void setBirthDay(Long birthDay) {
    this.birthDay = birthDay;
  }

  public String getBirthDayString() {
    if (this.getBirthDay() == null) {
      return "";
    }
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
    Date d = new Date(this.getBirthDay());
    return sdf.format(d);
  }

  public void setBirthDayString(String birthdayString) {
    this.birthdayString = birthdayString;
  }

  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public double getTotalArrears() {
    return totalArrears;
  }

  public void setTotalArrears(double totalArrears) {
    this.totalArrears = totalArrears;
  }

  public int getVehicleAmount() {
    return vehicleAmount;
  }

  public void setVehicleAmount(int vehicleAmount) {
    this.vehicleAmount = vehicleAmount;
  }

  public String getLastLicenceNo() {
    return lastLicenceNo;
  }

  public void setLastLicenceNo(String lastLicenceNo) {
    this.lastLicenceNo = lastLicenceNo;
  }

  public String getLastTime() {
    return lastTime;
  }

  public void setLastTime(String lastTime) {
    this.lastTime = lastTime;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public double getTotalReturnAmount() {
    return totalReturnAmount;
  }

  public void setTotalReturnAmount(double totalReturnAmount) {
    this.totalReturnAmount = totalReturnAmount;
  }

  public double getTotalPayable() {
    return totalPayable;
  }

  public void setTotalPayable(double totalPayable) {
    this.totalPayable = totalPayable;
  }

  @Override
  public int compareTo(CustomerResponse o) {
     if(lastTime!=null&&o.getLastTime()!=null){
        return o.getLastTime().compareTo(lastTime);
     }else if(lastTime!=null&&o.getLastTime()==null){
       return -1;
     }else if(lastTime==null&&o.getLastTime()!=null){
       return 1;
     }
     return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean hasValidContact() {
    ContactDTO[] contactDTOs = this.getContacts();
    if (contactDTOs != null) {
      for (ContactDTO contactDTO : contactDTOs) {
        if (contactDTO != null && contactDTO.isValidContact()) {
          return true;
        }
      }
    }
    return false;
  }

}
