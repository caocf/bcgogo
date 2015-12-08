package com.bcgogo.user.dto;

import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.txn.dto.BcgogoOrderDto;
import com.bcgogo.txn.dto.CarDTO;
import com.bcgogo.txn.dto.ReceivableDTO;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-11-10
 * Time: 下午12:07
 * To change this template use File | Settings | File Templates.
 */
public class CustomerRecordDTO implements Serializable {
  private Long id;
  private String idString;
  private Long shopId;
  private String name;
  private String nameStr;
  private String company;
  private String mobile;
  private String memberNumber;
  private Long customerId;
  private String customerIdString;
  private String birthdayString;
  private Long birthday;
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
  private String carDateStr;
  private String carDateString;
  private double totalAmount;
  private double totalReceivable;
  private Long lastDate;
  private String lastDateStr;
  private String lastBill;
  private String lastBillShort;
  private double lastAmount;
  private Long repayDate;
  private String repayDateStr;
  private String contact;
  private String contactStr;
  private ContactDTO[] contacts; // 联系人列表 add by zhuj
  private Long contactId;
  private String contactIdStr;

  private String thirdCategoryIdStr;//新增客户、新增供应商营业分类三级分类ids

  public ContactDTO[] getContacts() {
    return contacts;
  }

  public void setContacts(ContactDTO[] contacts) {
    this.contacts = contacts;
  }

  private String phone;//固定电话
  private String phoneSecond;
  private String phoneThird;
  private String fax;
  private String bank;
  private String account;
  private String invoiceCategory;//发票类型
  private String settlementType;//结算方式
  private String shortName;    //简称
  private String area;         //区域
  private String bankAccountName;    //银行开户名
  private String customerKind;          //用户类别
  private Long washRemain;
  private int vehicleCount;      //客户的车辆数
  private String memo;
  private String memoStr;
  //会员
  private MemberDTO memberDTO;
  private CustomerDTO customerDTO;
  private String cardServices;  //该会员的会员卡的 购卡内容
  private String type;
  private String memberNo;
  private String lastChargeDateStr;
  private Double lastChargeAmount;
  private boolean isVIP;
  //客户店铺ID
  private Long customerShopId;
  private double totalReturnAmount;
  private Double totalPayable; //店面欠这个客户的总额；
  private int countCustomerReturn;//客户退货次数
  private CustomerStatus status;
  private RelationTypes relationType;     //客户关联类型
  private String relationTypeStr;//客户类型文案
  private Long province;
  private Long city;
  private Long region;
  private String identity;
  private Long supplierId;
  private String businessScope;
  private boolean isOnlineShop = false;
  private String[] businessScopes;
  private String otherBusinessScope;
  private CarDTO[] vehicles;

  private Long consumeTimes;  //客户累计消费次数
  private Long memberConsumeTimes;//客户会员卡累计消费次数
  private Double memberConsumeTotal;//客户会员卡累计消费金额

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
  }

  public String getContactIdStr() {
    return contactIdStr;
  }

  public void setContactIdStr(String contactIdStr) {
    this.contactIdStr = contactIdStr;
  }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

  public String[] getBusinessScopes() {
    return businessScopes;
  }

  public void setBusinessScopes(String[] businessScopes) {
    this.businessScopes = businessScopes;
  }

  public String getOtherBusinessScope() {
    return otherBusinessScope;
  }

  public void setOtherBusinessScope(String otherBusinessScope) {
    this.otherBusinessScope = otherBusinessScope;
  }

  public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getMemoStr() {
    return memoStr;
  }

  public void setMemoStr(String memoStr) {
    this.memoStr = memoStr;
  }


    public CarDTO[] getVehicles() {
        return vehicles;
    }

    public void setVehicles(CarDTO[] vehicles) {
        this.vehicles = vehicles;
    }

    public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getNameStr() {
    return nameStr;
  }

  public void setNameStr(String nameStr) {
    this.nameStr = nameStr;
  }

  public String getContactStr() {
    return contactStr;
  }

  public void setContactStr(String contactStr) {
    this.contactStr = contactStr;
  }

  public String getIdString() {
    return idString;
  }

  public void setIdString(String idString) {
    this.idString = idString;
  }

  public String getCustomerIdString() {
    return customerIdString;
  }

  public void setCustomerIdString(String customerIdString) {
    this.customerIdString = customerIdString;
  }

  public int getVehicleCount() {
    return vehicleCount;
  }

  public void setCarDateStr(String carDateStr) {
    this.carDateStr = carDateStr;
  }

  public void setLastDateStr(String lastDateStr) {
    this.lastDateStr = lastDateStr;
  }

  public void setVehicleCount(int vehicleCount) {
    this.vehicleCount = vehicleCount;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getBankAccountName() {
    return bankAccountName;
  }

  public void setBankAccountName(String bankAccountName) {
    this.bankAccountName = bankAccountName;
  }

  public String getCustomerKind() {
    return customerKind;
  }

  public void setCustomerKind(String customerKind) {
    this.customerKind = customerKind;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getSettlementType() {
    return settlementType;
  }

  public void setSettlementType(String settlementType) {
    this.settlementType = settlementType;
  }

  public String getInvoiceCategory() {
    return invoiceCategory;
  }

  public void setInvoiceCategory(String invoiceCategory) {
    this.invoiceCategory = invoiceCategory;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getBank() {
    return bank;
  }

  public void setBank(String bank) {
    this.bank = bank;
  }

  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getPhoneSecond() {
    return phoneSecond;
  }

  public void setPhoneSecond(String phoneSecond) {
    this.phoneSecond = phoneSecond;
  }

  public String getPhoneThird() {
    return phoneThird;
  }

  public void setPhoneThird(String phoneThird) {
    this.phoneThird = phoneThird;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getBirthdayString() {
    return birthdayString;
  }

  public void setBirthdayString(String birthdayString) {
    this.birthdayString = birthdayString;
  }

  public String getRepayDateStr() {
    if (this.getRepayDate() == null) {
      return "";
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date(this.getRepayDate());
    return sdf.format(date);
  }

  public void setRepayDateStr(String repayDateStr) {
    this.repayDateStr = repayDateStr;
  }

  public String getLastDateStr() {
    return lastDateStr;
  }

//    public void setLastDateStr(long lastDate) {
//
//    }

  public CustomerRecordDTO() {
  }

  public String getCarDateString() {
    return carDateString;
  }

  public void setCarDateString(String carDateString) {
    this.carDateString = carDateString;
  }

  public Long getBirthday() {
    return birthday;
  }

  public void setBirthday(Long birthday) {
    this.birthday = birthday;
  }

  public String getBirthdayStr() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    if (this.getBirthday() == null) {
      return " ";
    }
    Date date = new Date(this.getBirthday());
    return sdf.format(date);
  }


  public String getCarDateStr() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    if (this.getCarDate() == null) {
      return " ";
    }
    Date date = new Date(this.getCarDate());
    return sdf.format(date);
  }

  public void setCarDateStr(long carDate) {
//      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//      Date date = new Date(carDate);
//      this.carDateStr = sdf.format(date);
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
    if (id != null) {
      this.idString = id.toString();
    }
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getMemberNumber() {
    return memberNumber;
  }

  public void setMemberNumber(String memberNumber) {
    this.memberNumber = memberNumber;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    if (customerId != null) {
      this.customerIdString = customerId.toString();
    }
    this.customerId = customerId;
  }

  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  public String getVin() {
    return vin;
  }

  public void setVin(String vin) {
    this.vin = vin;
  }

  public Long getCarDate() {
    return carDate;
  }

  public void setCarDate(Long carDate) {
    this.carDate = carDate;
  }

  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = NumberUtil.toReserve(totalAmount,NumberUtil.MONEY_PRECISION);
  }

  public double getTotalReceivable() {
    return totalReceivable;
  }

  public void setTotalReceivable(double totalReceivable) {
    this.totalReceivable = NumberUtil.toReserve(totalReceivable,NumberUtil.MONEY_PRECISION);
  }

  public Long getLastDate() {
    return lastDate;
  }

  public void setLastDate(Long lastDate) {
    this.lastDate = lastDate;
    if (lastDate != null && !lastDate.equals(0L)) {
      this.lastDateStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd HH:mm",lastDate);
    } else {
      this.lastDateStr = StringUtil.EMPTY_STRING;
    }
  }

  public String getLastBill() {
    return lastBill;
  }

  public void setLastBill(String lastBill) {
    this.lastBill = lastBill;
  }

  public double getLastAmount() {
    return lastAmount;
  }

  public void setLastAmount(double lastAmount) {
    this.lastAmount = NumberUtil.toReserve(lastAmount,NumberUtil.MONEY_PRECISION);
  }

  public Long getRepayDate() {

    return repayDate;
  }

  public void setRepayDate(Long repayDate) {
    this.repayDate = repayDate;
  }

  public Long getWashRemain() {
    return this.washRemain;
  }

  public void setWashRemain(Long washRemain) {
    this.washRemain = washRemain;
  }

  public String getLastBillShort() {
    return lastBillShort;
  }

  public void setLastBillShort(String lastBillShort) {
    this.lastBillShort = lastBillShort;
  }

  public CustomerStatus getStatus() {
    return status;
  }

  public void setStatus(CustomerStatus status) {
    this.status = status;
  }

  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
  }

  public int getCountCustomerReturn() {
    return countCustomerReturn;
  }

  public void setCountCustomerReturn(int countCustomerReturn) {
     this.countCustomerReturn = countCustomerReturn;
  }

  public void copyFromDTO(CustomerRecordDTO customerRecordDTO) {
    if (!StringUtil.isEmpty(customerRecordDTO.account)) {
      this.account = customerRecordDTO.account;
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getAddress())) {
      this.address = customerRecordDTO.getAddress();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getArea())) {
      this.area = customerRecordDTO.getArea();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getBank())) {
      this.bank = customerRecordDTO.getBank();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getBankAccountName())) {
      this.bankAccountName = customerRecordDTO.getBankAccountName();
    }
    if (customerRecordDTO.getBirthday() != null) {
      this.birthday = customerRecordDTO.getBirthday();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getName())) {
      this.name = customerRecordDTO.getName();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getShortName())) {
      this.shortName = customerRecordDTO.getShortName();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getContact())) {
      this.contact = customerRecordDTO.getContact();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getMobile())) {
      this.mobile = customerRecordDTO.getMobile();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getPhone())) {
      this.phone = customerRecordDTO.getPhone();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getFax())) {
      this.fax = customerRecordDTO.getFax();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getMemberNumber())) {
      this.memberNumber = customerRecordDTO.getMemberNumber();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getEmail())) {
      this.email = customerRecordDTO.getEmail();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getQq())) {
      this.qq = customerRecordDTO.getQq();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getInvoiceCategory())) {
      this.invoiceCategory = customerRecordDTO.getInvoiceCategory();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getSettlementType())) {
      this.settlementType = customerRecordDTO.getSettlementType();
    }
    if (!StringUtil.isEmpty(customerRecordDTO.getCustomerKind())) {
      this.customerKind = customerRecordDTO.getCustomerKind();
    }
  }

  public String getLastChargeDateStr() {
    return lastChargeDateStr;
  }

  public void setLastChargeDateStr(String lastChargeDateStr) {
    this.lastChargeDateStr = lastChargeDateStr;
  }

  public Double getLastChargeAmount() {
    return lastChargeAmount;
  }

  public void setLastChargeAmount(Double lastChargeAmount) {
    this.lastChargeAmount = NumberUtil.toReserve(lastChargeAmount,NumberUtil.MONEY_PRECISION);
  }

  public MemberDTO getMemberDTO() {
    return memberDTO;
  }

  public void setMemberDTO(MemberDTO memberDTO) {
    this.memberDTO = memberDTO;
  }

  public CustomerDTO getCustomerDTO() {
    return customerDTO;
  }

  public void setCustomerDTO(CustomerDTO customerDTO) {
    this.customerDTO = customerDTO;
  }

  public String getCardServices() {
    return cardServices;
  }

  public void setCardServices(String cardServices) {
    this.cardServices = cardServices;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    if (StringUtils.isNotBlank(type) && (type.toUpperCase().contains("VIP"))) {
      this.isVIP = true;
    } else {
      this.isVIP = false;
    }
    this.type = type;
  }

  public boolean getIsVIP() {
    return isVIP;
  }

  public void setVIP(boolean VIP) {
    isVIP = VIP;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public double getTotalReturnAmount() {
    return totalReturnAmount;
  }

  public void setTotalReturnAmount(double totalReturnAmount) {
    this.totalReturnAmount = totalReturnAmount;
  }

  public Double getTotalPayable() {
    return totalPayable;
  }

  public void setTotalPayable(Double totalPayable) {
    this.totalPayable = totalPayable;
  }

  public RelationTypes getRelationType() {
    return relationType;
  }

  public void setRelationType(RelationTypes relationType) {
    this.relationType = relationType;
    if(relationType != null){
      this.setRelationTypeStr(relationType.getName());
    }else {
      this.setRelationTypeStr("");
    }
  }

    public Long getRegion() {
        return region;
    }

    public void setRegion(Long region) {
        this.region = region;
    }

    public Long getCity() {

        return city;
    }

    public void setCity(Long city) {
        this.city = city;
    }

    public Long getProvince() {

        return province;
    }

    public void setProvince(Long province) {
        this.province = province;
    }

  public String getThirdCategoryIdStr() {
    return thirdCategoryIdStr;
  }

  public void setThirdCategoryIdStr(String thirdCategoryIdStr) {
    this.thirdCategoryIdStr = thirdCategoryIdStr;
  }

    public boolean getIsOnlineShop() {
        return isOnlineShop;
    }

    public void setOnlineShop(boolean onlineShop) {
        isOnlineShop = onlineShop;
    }

  public Long getConsumeTimes() {
    return consumeTimes;
  }

  public void setConsumeTimes(Long consumeTimes) {
    this.consumeTimes = consumeTimes;
  }

  public Long getMemberConsumeTimes() {
    return memberConsumeTimes;
  }

  public void setMemberConsumeTimes(Long memberConsumeTimes) {
    this.memberConsumeTimes = memberConsumeTimes;
  }

  public Double getMemberConsumeTotal() {
    return memberConsumeTotal;
  }

  public void setMemberConsumeTotal(Double memberConsumeTotal) {
    this.memberConsumeTotal = NumberUtil.toReserve(memberConsumeTotal,NumberUtil.MONEY_PRECISION);
  }

  @Override
  public String toString() {
    return "CustomerRecordDTO{" +
        "id=" + id +
        ", idString='" + idString + '\'' +
        ", shopId=" + shopId +
        ", name='" + name + '\'' +
        ", nameStr='" + nameStr + '\'' +
        ", company='" + company + '\'' +
        ", mobile='" + mobile + '\'' +
        ", memberNumber='" + memberNumber + '\'' +
        ", customerId=" + customerId +
        ", customerIdString='" + customerIdString + '\'' +
        ", birthdayString='" + birthdayString + '\'' +
        ", birthday=" + birthday +
        ", qq='" + qq + '\'' +
        ", email='" + email + '\'' +
        ", address='" + address + '\'' +
        ", licenceNo='" + licenceNo + '\'' +
        ", brand='" + brand + '\'' +
        ", model='" + model + '\'' +
        ", year='" + year + '\'' +
        ", engine='" + engine + '\'' +
        ", vin='" + vin + '\'' +
        ", carDate=" + carDate +
        ", carDateStr='" + carDateStr + '\'' +
        ", carDateString='" + carDateString + '\'' +
        ", totalAmount=" + totalAmount +
        ", totalReceivable=" + totalReceivable +
        ", lastDate=" + lastDate +
        ", lastDateStr='" + lastDateStr + '\'' +
        ", lastBill='" + lastBill + '\'' +
        ", lastBillShort='" + lastBillShort + '\'' +
        ", lastAmount=" + lastAmount +
        ", repayDate=" + repayDate +
        ", repayDateStr='" + repayDateStr + '\'' +
        ", contact='" + contact + '\'' +
        ", contactStr='" + contactStr + '\'' +
        ", contacts=" + Arrays.toString(contacts) +
        ", contactId=" + contactId +
        ", contactIdStr='" + contactIdStr + '\'' +
        ", thirdCategoryIdStr='" + thirdCategoryIdStr + '\'' +
        ", phone='" + phone + '\'' +
        ", fax='" + fax + '\'' +
        ", bank='" + bank + '\'' +
        ", account='" + account + '\'' +
        ", invoiceCategory='" + invoiceCategory + '\'' +
        ", settlementType='" + settlementType + '\'' +
        ", shortName='" + shortName + '\'' +
        ", area='" + area + '\'' +
        ", bankAccountName='" + bankAccountName + '\'' +
        ", customerKind='" + customerKind + '\'' +
        ", washRemain=" + washRemain +
        ", vehicleCount=" + vehicleCount +
        ", memo='" + memo + '\'' +
        ", memoStr='" + memoStr + '\'' +
        ", memberDTO=" + memberDTO +
        ", customerDTO=" + customerDTO +
        ", cardServices='" + cardServices + '\'' +
        ", type='" + type + '\'' +
        ", memberNo='" + memberNo + '\'' +
        ", lastChargeDateStr='" + lastChargeDateStr + '\'' +
        ", lastChargeAmount=" + lastChargeAmount +
        ", isVIP=" + isVIP +
        ", customerShopId=" + customerShopId +
        ", totalReturnAmount=" + totalReturnAmount +
        ", totalPayable=" + totalPayable +
        ", countCustomerReturn=" + countCustomerReturn +
        ", status=" + status +
        ", relationType=" + relationType +
        ", relationTypeStr='" + relationTypeStr + '\'' +
        ", province=" + province +
        ", city=" + city +
        ", region=" + region +
        ", identity='" + identity + '\'' +
        ", supplierId=" + supplierId +
        ", businessScope='" + businessScope + '\'' +
        ", isOnlineShop=" + isOnlineShop +
        ", businessScopes=" + Arrays.toString(businessScopes) +
        ", otherBusinessScope='" + otherBusinessScope + '\'' +
        ", vehicles=" + Arrays.toString(vehicles) +
        ", consumeTimes=" + consumeTimes +
        ", memberConsumeTimes=" + memberConsumeTimes +
        ", memberConsumeTotal=" + memberConsumeTotal +
        '}';
  }

  public String getRelationTypeStr() {
    return relationTypeStr;
  }

  public void setRelationTypeStr(String relationTypeStr) {
    this.relationTypeStr = relationTypeStr;
  }

  public void fromCustomerDTO(CustomerDTO customerDTO) {
    if (customerDTO != null) {
      this.setCustomerId(customerDTO.getId());
      this.setShopId(customerDTO.getShopId());
      this.setName(customerDTO.getName());
      this.setShortName(customerDTO.getShortName());
      this.setAddress(customerDTO.getAddress());
      this.setContact(customerDTO.getContact());
      this.setContacts(customerDTO.getContacts()); // add by zhuj
      this.setMobile(customerDTO.getMobile());

      //汽配版中的多座机号
      this.setPhone(customerDTO.getLandLine());
      this.setPhoneSecond(customerDTO.getLandLineSecond());
      this.setPhoneThird(customerDTO.getLandLineThird());

      this.setFax(customerDTO.getFax());
      this.setMemberNumber(customerDTO.getMemberNumber());
      this.setMemo(customerDTO.getMemo());
      this.setArea(customerDTO.getArea());
      this.setEmail(customerDTO.getEmail());
      this.setBirthday(customerDTO.getBirthday());
      this.setQq(customerDTO.getQq());
      this.setBank(customerDTO.getBank());
      this.setBankAccountName(customerDTO.getBankAccountName());
      this.setAccount(customerDTO.getAccount());
      if (customerDTO.getInvoiceCategory() != null) {
        this.setInvoiceCategory(customerDTO.getInvoiceCategory().toString());
      }
      if (customerDTO.getSettlementType() != null) {
        this.setSettlementType(customerDTO.getSettlementType().toString());
      }
      this.setCustomerKind(customerDTO.getCustomerKind());
      this.setCustomerShopId(customerDTO.getCustomerShopId());
      this.setProvince(customerDTO.getProvince());
      this.setCity(customerDTO.getCity());
      this.setRegion(customerDTO.getRegion());
      this.setRelationType(customerDTO.getRelationType());
      this.setIdentity(customerDTO.getIdentity());
      this.setSupplierId(customerDTO.getSupplierId());
      this.setBirthdayString(customerDTO.getBirthdayString());
      this.setBirthday(customerDTO.getBirthday());
    }
  }

  public void fromWashBeauty(WashBeautyOrderDTO washBeautyOrderDTO) {
    this.setShopId(washBeautyOrderDTO.getShopId());
    this.setCustomerId(washBeautyOrderDTO.getCustomerId());
    this.setName(washBeautyOrderDTO.getCustomer());
    this.setContact(washBeautyOrderDTO.getContact());
    this.setMobile(washBeautyOrderDTO.getMobile());
    this.setLicenceNo(washBeautyOrderDTO.getLicenceNo());
    this.setBrand(washBeautyOrderDTO.getBrand());
    this.setModel(washBeautyOrderDTO.getModel());
    this.setLastAmount(washBeautyOrderDTO.getAfterMemberDiscountTotal());
    this.setLastDate(System.currentTimeMillis());
    this.setTotalReceivable(this.getTotalReceivable()+ washBeautyOrderDTO.getDebt());
    this.setTotalAmount(this.getTotalAmount()+ washBeautyOrderDTO.getSettledAmount() + washBeautyOrderDTO.getDebt());
    this.setRepayDate(washBeautyOrderDTO.getRepaymentTime());
    this.setAddress(washBeautyOrderDTO.getAddress());
  }
}


