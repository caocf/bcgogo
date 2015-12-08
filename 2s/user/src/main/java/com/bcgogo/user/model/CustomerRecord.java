package com.bcgogo.user.model;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InsuranceOrderDTO;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.RelatedShopUpdateLogDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-11-10
 * Time: 上午11:22
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "customer_record")
public class CustomerRecord extends LongIdentifier {
  private Long shopId;
  private String name;
  private String company;
  private String mobile;
  private String memberNumber;
  private Long customerId;
  private String qq;
  private String email;
  private String address;
  private String licenceNo;
  private String brand;
  private String model;
  private String year;
  private String engine;
  private String vin;
  private Long carDate;
  private double totalAmount;
  private double totalReceivable;
  private Long lastDate;
  private String lastBill;
  private String lastBillShort;
  private double lastAmount;
  private Long repayDate;
  private String contact;
  private String memo;
  private CustomerStatus status;
  private Long customerShopId;
  private double totalReturnAmount;
  private Double totalPayable;//店面欠这个客户的总额；
  private Long province;
  private Long city;
  private Long region;

  private Long consumeTimes;  //客户累计消费次数
  private Long memberConsumeTimes;//客户会员卡累计消费次数
  private Double memberConsumeTotal;//客户会员卡累计消费金额

  public CustomerRecord() {
  }
  
  public CustomerRecord(CustomerRecordDTO customerRecordDTO){
    this.setId(customerRecordDTO.getId());
    this.setShopId(customerRecordDTO.getShopId());
    this.setName(customerRecordDTO.getName());
    this.setCompany(customerRecordDTO.getCompany());
    this.setMobile(customerRecordDTO.getMobile());
    this.setMemberNumber(customerRecordDTO.getMemberNumber());
    this.setCustomerId(customerRecordDTO.getCustomerId());
    this.setQq(customerRecordDTO.getQq());
    this.setEmail(customerRecordDTO.getEmail());
    this.setAddress(customerRecordDTO.getAddress());
    this.setBrand(customerRecordDTO.getBrand());
    this.setModel(customerRecordDTO.getModel());
    this.setYear(customerRecordDTO.getYear());
    this.setEngine(customerRecordDTO.getEngine());
    this.setVin(customerRecordDTO.getVin());
    this.setCarDate(customerRecordDTO.getCarDate());
    this.setTotalAmount(customerRecordDTO.getTotalAmount());
    this.setTotalReceivable(customerRecordDTO.getTotalReceivable());
    this.setLastDate(customerRecordDTO.getLastDate());
    this.setLastBill(customerRecordDTO.getLastBill());
    this.setLastAmount(customerRecordDTO.getLastAmount());
    this.setLicenceNo(customerRecordDTO.getLicenceNo());
    this.setRepayDate(customerRecordDTO.getRepayDate());
    this.setContact(customerRecordDTO.getContact());
    this.setMemo(customerRecordDTO.getMemo());
    this.setStatus(customerRecordDTO.getStatus());
    this.setCustomerShopId(customerRecordDTO.getCustomerShopId());
    this.setTotalReturnAmount(customerRecordDTO.getTotalReturnAmount());
    this.setTotalPayable(customerRecordDTO.getTotalPayable());
    this.setProvince(customerRecordDTO.getProvince());
    this.setCity(customerRecordDTO.getCity());
    this.setRegion(customerRecordDTO.getRegion());

    this.setConsumeTimes(NumberUtil.longValue(customerRecordDTO.getConsumeTimes()));
    this.setMemberConsumeTimes(NumberUtil.longValue(customerRecordDTO.getMemberConsumeTimes()));
    this.setMemberConsumeTotal(NumberUtil.toReserve(customerRecordDTO.getMemberConsumeTotal(),NumberUtil.MONEY_PRECISION));
  }

  public CustomerRecord(InsuranceOrderDTO insuranceOrderDTO) {
    this.setShopId(insuranceOrderDTO.getShopId());
    this.setCustomerId(insuranceOrderDTO.getCustomerId());
    this.setName(insuranceOrderDTO.getCustomer());
    this.setMobile(insuranceOrderDTO.getMobile());
    this.setLicenceNo(insuranceOrderDTO.getLicenceNo());
    this.setBrand(insuranceOrderDTO.getBrand());
    this.setModel(insuranceOrderDTO.getModel());
  }

  public CustomerRecord fromDTO(CustomerRecordDTO customerRecordDTO){
    if(customerRecordDTO == null){
        return this;
    }
    if(customerRecordDTO.getId() != null){
        this.setId(customerRecordDTO.getId());
    }
    this.setShopId(customerRecordDTO.getShopId());
    this.setName(customerRecordDTO.getName());
    this.setCompany(customerRecordDTO.getCompany());
    this.setMobile(customerRecordDTO.getMobile());
    this.setMemberNumber(customerRecordDTO.getMemberNumber());
    this.setCustomerId(customerRecordDTO.getCustomerId());
    this.setQq(customerRecordDTO.getQq());
    this.setMemo(customerRecordDTO.getMemo());
    this.setEmail(customerRecordDTO.getEmail());
    this.setAddress(customerRecordDTO.getAddress());
    this.setBrand(customerRecordDTO.getBrand());
    this.setModel(customerRecordDTO.getModel());
    this.setYear(customerRecordDTO.getYear());
    this.setEngine(customerRecordDTO.getEngine());
    this.setVin(customerRecordDTO.getVin());
    this.setCarDate(customerRecordDTO.getCarDate());
    this.setTotalAmount(customerRecordDTO.getTotalAmount());
    this.setTotalReceivable(customerRecordDTO.getTotalReceivable());
    if(null!=customerRecordDTO.getLastDate())
      this.setLastDate(customerRecordDTO.getLastDate());
    if(null!=customerRecordDTO.getLastBill())
      this.setLastBill(customerRecordDTO.getLastBill());
    this.setLastAmount(customerRecordDTO.getLastAmount());
    this.setLicenceNo(customerRecordDTO.getLicenceNo());
    if(null!=customerRecordDTO.getRepayDate())
      this.setRepayDate(customerRecordDTO.getRepayDate());
    this.setContact(customerRecordDTO.getContact());
    if(null!=customerRecordDTO.getLastBillShort())
      this.setLastBillShort(customerRecordDTO.getLastBillShort());
    this.setStatus(customerRecordDTO.getStatus());
    this.setCustomerShopId(customerRecordDTO.getCustomerShopId());
    this.setTotalReturnAmount(customerRecordDTO.getTotalReturnAmount());
    this.setTotalPayable(customerRecordDTO.getTotalPayable());
    this.setProvince(customerRecordDTO.getProvince());
    this.setCity(customerRecordDTO.getCity());
    this.setRegion(customerRecordDTO.getRegion());

    this.setConsumeTimes(NumberUtil.longValue(customerRecordDTO.getConsumeTimes()));
    this.setMemberConsumeTimes(NumberUtil.longValue(customerRecordDTO.getMemberConsumeTimes()));
    this.setMemberConsumeTotal(NumberUtil.toReserve(customerRecordDTO.getMemberConsumeTotal(),NumberUtil.MONEY_PRECISION));
    return this;
  }

  public CustomerRecord fromDTONotCopyId(CustomerRecordDTO customerRecordDTO){
    if(customerRecordDTO == null){
        return this;
    }
    this.setShopId(customerRecordDTO.getShopId());
    this.setName(customerRecordDTO.getName());
    this.setCompany(customerRecordDTO.getCompany());
    this.setMobile(customerRecordDTO.getMobile());
    this.setMemberNumber(customerRecordDTO.getMemberNumber());
    this.setCustomerId(customerRecordDTO.getCustomerId());
    this.setQq(customerRecordDTO.getQq());
    this.setMemo(customerRecordDTO.getMemo());
    this.setEmail(customerRecordDTO.getEmail());
    this.setAddress(customerRecordDTO.getAddress());
    this.setBrand(customerRecordDTO.getBrand());
    this.setModel(customerRecordDTO.getModel());
    this.setYear(customerRecordDTO.getYear());
    this.setEngine(customerRecordDTO.getEngine());
    this.setVin(customerRecordDTO.getVin());
    this.setCarDate(customerRecordDTO.getCarDate());
    this.setTotalAmount(customerRecordDTO.getTotalAmount());
    this.setTotalReceivable(customerRecordDTO.getTotalReceivable());
    if(null!=customerRecordDTO.getLastDate())
      this.setLastDate(customerRecordDTO.getLastDate());
    if(null!=customerRecordDTO.getLastBill())
      this.setLastBill(customerRecordDTO.getLastBill());
    this.setLastAmount(customerRecordDTO.getLastAmount());
    this.setLicenceNo(customerRecordDTO.getLicenceNo());
    if(null!=customerRecordDTO.getRepayDate())
      this.setRepayDate(customerRecordDTO.getRepayDate());
    this.setContact(customerRecordDTO.getContact());
    if(null!=customerRecordDTO.getLastBillShort())
      this.setLastBillShort(customerRecordDTO.getLastBillShort());
    this.setStatus(customerRecordDTO.getStatus());
    this.setCustomerShopId(customerRecordDTO.getCustomerShopId());
    this.setTotalReturnAmount(customerRecordDTO.getTotalReturnAmount());
    this.setTotalPayable(customerRecordDTO.getTotalPayable());
    this.setProvince(customerRecordDTO.getProvince());
    this.setCity(customerRecordDTO.getCity());
    this.setRegion(customerRecordDTO.getRegion());

    this.setConsumeTimes(NumberUtil.longValue(customerRecordDTO.getConsumeTimes()));
    this.setMemberConsumeTimes(NumberUtil.longValue(customerRecordDTO.getMemberConsumeTimes()));
    this.setMemberConsumeTotal(NumberUtil.toReserve(customerRecordDTO.getMemberConsumeTotal(),NumberUtil.MONEY_PRECISION));
    return this;
  }

  public CustomerRecordDTO toDTO(){
    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
    customerRecordDTO.setId(this.getId());
    customerRecordDTO.setShopId(this.getShopId());
    customerRecordDTO.setCompany(this.getCompany());
    customerRecordDTO.setMobile(this.getMobile());
    customerRecordDTO.setMemberNumber(this.getMemberNumber());
    customerRecordDTO.setCustomerId(this.getCustomerId());
    customerRecordDTO.setQq(this.getQq());
    customerRecordDTO.setEmail(this.getEmail());
    customerRecordDTO.setAddress(this.getAddress());
    customerRecordDTO.setBrand(this.getBrand());
    customerRecordDTO.setModel(this.getModel());
    customerRecordDTO.setYear(this.getYear());
    customerRecordDTO.setEngine(this.getEngine());
    customerRecordDTO.setVin(this.getVin());
    customerRecordDTO.setCarDate(this.getCarDate());
    //customerRecordDTO.setCarDateStr(this.getCarDate());
    customerRecordDTO.setTotalAmount(this.getTotalAmount());
    customerRecordDTO.setTotalReceivable(this.getTotalReceivable());
    customerRecordDTO.setLastDate(this.getLastDate());
    //customerRecordDTO.setLastDateStr(this.getLastDate());
    customerRecordDTO.setLastBill(this.getLastBill());
    customerRecordDTO.setLastBillShort(this.getLastBillShort());
    customerRecordDTO.setLastAmount(this.getLastAmount());
    customerRecordDTO.setLicenceNo(this.getLicenceNo());
    customerRecordDTO.setRepayDate(this.getRepayDate());
    customerRecordDTO.setCustomerShopId(this.getCustomerShopId());
    customerRecordDTO.setAddress(this.getAddress());

    customerRecordDTO.setName(this.getName());
    if(!StringUtil.isEmpty(getName())){
        if(this.getName().length()>7){
          customerRecordDTO.setNameStr(this.getName().substring(0, 7) + "...");
       }else{
          customerRecordDTO.setNameStr(this.getName());
          }
    }else{
        customerRecordDTO.setNameStr("");
   }
    customerRecordDTO.setContact(this.getContact());
    if(!StringUtil.isEmpty(getContact())){
        if(this.getContact().length()>5){
          customerRecordDTO.setContactStr(this.getContact().substring(0, 5) + "...");
       }else{
          customerRecordDTO.setContactStr(this.getContact());
          }
    }else{
        customerRecordDTO.setContactStr("");
   }

    customerRecordDTO.setMemo(this.getMemo());
    if(!StringUtil.isEmpty(getMemo())){
        if(this.getMemo().length()>13){
          customerRecordDTO.setMemoStr(this.getMemo().substring(0, 13) + "...");
       }else{
          customerRecordDTO.setMemoStr(this.getMemo());
          }
    }else{
        customerRecordDTO.setMemoStr("");
   }

    customerRecordDTO.setStatus(this.getStatus());
    customerRecordDTO.setTotalReturnAmount(getTotalReturnAmount());
    customerRecordDTO.setTotalPayable(this.getTotalPayable());
    customerRecordDTO.setProvince(this.getProvince());
    customerRecordDTO.setCity(this.getCity());
    customerRecordDTO.setRegion(this.getRegion());

    customerRecordDTO.setConsumeTimes(NumberUtil.longValue(this.getConsumeTimes()));
    customerRecordDTO.setMemberConsumeTimes(NumberUtil.longValue(this.getMemberConsumeTimes()));
    customerRecordDTO.setMemberConsumeTotal(NumberUtil.toReserve(this.getMemberConsumeTotal(),NumberUtil.MONEY_PRECISION));
    return customerRecordDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "name", length = 20)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "company", length = 20)
  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  @Column(name = "mobile", length = 20)
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "member_number", length = 20)
  public String getMemberNumber() {
    return memberNumber;
  }

  public void setMemberNumber(String memberNumber) {
    this.memberNumber = memberNumber;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "qq", length = 20)
  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  @Column(name = "email", length = 50)
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "address", length = 50)
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Column(name = "licence_no", length = 20)
  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  @Column(name = "brand", length = 20)
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "model", length = 20)
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "year", length = 20)
  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  @Column(name = "engine", length = 20)
  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  @Column(name = "vin", length = 20)
  public String getVin() {
    return vin;
  }

  public void setVin(String vin) {
    this.vin = vin;
  }

  @Column(name = "car_date")
  public Long getCarDate() {
    return carDate;
  }

  public void setCarDate(Long carDate) {
    this.carDate = carDate;
  }

  @Column(name = "total_amount")
  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = NumberUtil.toReserve(totalAmount,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "total_receivable")
  public double getTotalReceivable() {
    return totalReceivable;
  }

  public void setTotalReceivable(double totalReceivable) {
    this.totalReceivable = NumberUtil.toReserve(totalReceivable,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "last_date")
  public Long getLastDate() {
    return lastDate;
  }

  public void setLastDate(Long lastDate) {
    this.lastDate = lastDate;
  }

  @Column(name = "last_bill", length = 500)
  public String getLastBill() {
    return lastBill;
  }

  public void setLastBill(String lastBill) {
    if (StringUtils.isNotBlank(lastBill) && lastBill.length() > 500) {
      this.lastBill = lastBill.substring(0, 490);
    } else {
      this.lastBill = lastBill;
    }
  }

  @Column(name = "last_amount")
  public double getLastAmount() {
    return lastAmount;
  }

  public void setLastAmount(double lastAmount) {
    this.lastAmount = NumberUtil.toReserve(lastAmount,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "repay_date")
  public Long getRepayDate() {
    return repayDate;
  }

  public void setRepayDate(Long repayDate) {
    this.repayDate = repayDate;
  }
   @Column(name = "contact")
  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getLastBillShort() {
    return lastBillShort;
  }

  public void setLastBillShort(String lastBillShort) {
    this.lastBillShort = lastBillShort;
  }
  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="status")
  public CustomerStatus getStatus() {
    return status;
  }

  public void setStatus(CustomerStatus status) {
    this.status = status;
  }

  @Column(name = "customer_shop_id")
  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
  }

  @Column(name = "total_return_amount")
  public double getTotalReturnAmount() {
    return totalReturnAmount;
  }

  public void setTotalReturnAmount(double totalReturnAmount) {
    this.totalReturnAmount = NumberUtil.toReserve(totalReturnAmount,NumberUtil.MONEY_PRECISION);
  }

  @Column(name="total_payable")
  public Double getTotalPayable() {
    return totalPayable;
  }

  public void setTotalPayable(Double totalPayable) {
    this.totalPayable = totalPayable;
  }

  @Column(name = "province")
  public Long getProvince() {
    return province;
  }

  public void setProvince(Long province) {
    this.province = province;
  }

  @Column(name = "city")
  public Long getCity() {
    return city;
  }

  public void setCity(Long city) {
    this.city = city;
  }

  @Column(name = "region")
  public Long getRegion() {
    return region;
  }

  public void setRegion(Long region) {
    this.region = region;
  }

  @Column(name = "consume_times")
  public Long getConsumeTimes() {
    return consumeTimes;
  }

  public void setConsumeTimes(Long consumeTimes) {
    this.consumeTimes = consumeTimes;
  }

  @Column(name = "member_consume_times")
  public Long getMemberConsumeTimes() {
    return memberConsumeTimes;
  }

  public void setMemberConsumeTimes(Long memberConsumeTimes) {
    this.memberConsumeTimes = memberConsumeTimes;
  }

  @Column(name = "member_consume_total")
  public Double getMemberConsumeTotal() {
    return memberConsumeTotal;
  }

  public void setMemberConsumeTotal(Double memberConsumeTotal) {
    this.memberConsumeTotal = NumberUtil.toReserve(memberConsumeTotal, NumberUtil.MONEY_PRECISION);
  }

  public void fromRelatedShop(ShopDTO shopDTO, List<RelatedShopUpdateLogDTO> relatedShopUpdateLogDTOs) throws Exception {
    if (shopDTO == null) {
      return;
    }
    if (relatedShopUpdateLogDTOs == null) {
      relatedShopUpdateLogDTOs = new ArrayList<RelatedShopUpdateLogDTO>();
    }
    //name, address,landLine,province,city, region
    CustomerRecordDTO customerRecordDTO = this.toDTO();
    if (!StringUtil.compareSame(getName(), shopDTO.getName())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerRecordDTO, "name", getName(), shopDTO.getName()));
      setName(shopDTO.getName());
    }
    if (!StringUtil.compareSame(getAddress(), shopDTO.getAddress())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerRecordDTO, "address", getAddress(), shopDTO.getAddress()));
      setAddress(shopDTO.getAddress());
    }

    if (!StringUtil.compareSame(getProvince(), shopDTO.getProvince())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerRecordDTO, "province",
          StringUtil.valueOf(getProvince()), StringUtil.valueOf(shopDTO.getProvince())));
      setProvince(shopDTO.getProvince());
    }

    if (!StringUtil.compareSame(getCity(), shopDTO.getCity())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerRecordDTO, "city",
          StringUtil.valueOf(getCity()), StringUtil.valueOf(shopDTO.getCity())));
      setCity(shopDTO.getCity());
    }

    if (!StringUtil.compareSame(getRegion(), shopDTO.getRegion())) {
      relatedShopUpdateLogDTOs.add(new RelatedShopUpdateLogDTO(customerRecordDTO, "region",
          StringUtil.valueOf(getRegion()), StringUtil.valueOf(shopDTO.getRegion())));
      setRegion(shopDTO.getRegion());
    }
  }
}

