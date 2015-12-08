package com.bcgogo.search.dto;

import com.bcgogo.enums.RelationTypes;
import com.bcgogo.user.CustomerVehicleResponse;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-8-28
 * Time: 下午11:53
 * To change this template use File | Settings | File Templates.
 */
public class CustomerSupplierSearchResultDTO {
  private Long id;
  private String idStr;
  private Long shopId;
  private Long createdTime;
  private String name;
  private Double totalDebt;
  private String contact;
  private String mobile;
  private List<ContactDTO> contactDTOList;
  private String landLine;
  private String address;
  private String customerOrSupplier;
  private String customerOrSupplierShopId;
  // customer
  private Long lastExpenseTime;
  private String lastExpenseTimeStr;
  private String memberNo;
  private String[] licenseNos;   //车牌号
  private List<String> vehicleDetailList;   //车辆详细
  private Double totalAmount;
  private String memberType;
  //supplier
  private Long lastInventoryTime;
  private RelationTypes relationType;
  private Double totalDeposit; //定金
  private Double totalReturnDebt; //店铺欠别人的钱
  private Double totalTradeAmount;//供应商累计交易金额
  private Double totalReturnAmount;//供应商累计退货金额
  private String businessScope;//经营范围

  //展示客户列表
  private Integer vehicleCount;
  private MemberDTO memberDTO;
  private Integer countCustomerReturn;
  private String areaInfo;

  private boolean isObd; //是否是obd客户
  private boolean isApp; //是否是app客户
  private List<String> obdVehicleNo;//有OBD的车牌号
  private List<String> appVehicleNo;//用车匹配出来的app的车牌号

  public CustomerSupplierSearchResultDTO(SolrDocument document) {
    this.id = Long.valueOf((String) document.getFirstValue("id"));
    if (null != this.id) {
      this.idStr = this.id.toString();
    }
    this.shopId = Long.valueOf((String) document.getFirstValue("shop_id"));


    this.address = (String) document.getFirstValue("address");
    this.customerOrSupplier = (String) document.getFirstValue("customer_or_supplier");
    this.customerOrSupplierShopId = (String) document.getFirstValue("customer_or_supplier_shop_id");

    this.name = (String) document.getFirstValue("name");
    this.landLine = (String) document.getFirstValue("land_line");
    Collection<Object> contactIds = document.getFieldValues("contact_id");
    Collection<Object> contacts = document.getFieldValues("contact");
    Collection<Object> mobiles = document.getFieldValues("mobile");
    if (CollectionUtils.isNotEmpty(contactIds) && CollectionUtils.isNotEmpty(contacts) && CollectionUtils.isNotEmpty(mobiles)) {
      List<ContactDTO> contactDTOList = new ArrayList<ContactDTO>();
      Long[] contactIdsArr = contactIds.toArray(new Long[contactIds.size()]);
      String[] contactArr = contacts.toArray(new String[contacts.size()]);

      String[] mobilesArr = null;
      if (CollectionUtils.isNotEmpty(mobiles)) {
        mobilesArr = mobiles.toArray(new String[mobiles.size()]);
      }
      for (int i = 0; i < contactIdsArr.length; i++) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setId(contactIdsArr[i]);

        if ("customer".equals(customerOrSupplier)) {
          contactDTO.setCustomerId(id);
        } else if ("supplier".equals(customerOrSupplier)) {
          contactDTO.setSupplierId(id);
        }

        contactDTO.setName(StringUtil.SOLR_PLACEHOLDER_STRING.equals(contactArr[i]) ? "" : contactArr[i]);
        if (CollectionUtils.isNotEmpty(mobiles)) {
          contactDTO.setMobile(StringUtil.SOLR_PLACEHOLDER_STRING.equals(mobilesArr[i]) ? "" : mobilesArr[i]);
        } else {
          contactDTO.setMobile("");
        }
        contactDTO.setIsMainContact(i == 0 ? 1 : 0);
        contactDTO.setLevel(i);
        if (i == 0) {
          setMobile(contactDTO.getMobile());
          setContact(contactDTO.getName());
        }
        if (contactDTO.isValidContact()) {
          contactDTOList.add(contactDTO);
        }
      }
      this.setContactDTOList(contactDTOList);
    }

    Collection licenseNoCollection = document.getFieldValues("license_no");
    this.licenseNos = licenseNoCollection == null ? null : (String[]) licenseNoCollection.toArray(new String[licenseNoCollection.size()]);
    Collection vehicleDetailCollection= document.getFieldValues("vehicle_detail");
    this.vehicleDetailList = CollectionUtils.isEmpty(vehicleDetailCollection)?null:(ArrayList<String>)vehicleDetailCollection;

    this.totalAmount = (Double) document.getFirstValue("total_amount");
    this.lastExpenseTime = (Long) document.getFirstValue("last_expense_time");
    if (lastExpenseTime != null) {
      this.lastExpenseTimeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, lastExpenseTime);
    }

    this.memberNo = (String) document.getFirstValue("member_no");
    this.memberType = (String) document.getFirstValue("member_type");
    this.lastInventoryTime = (Long) document.getFirstValue("last_inventory_time");
    this.createdTime = (Long) document.getFirstValue("created_time");
    this.totalDebt = (Double) document.getFirstValue("total_debt");
    this.totalReturnDebt = (Double) document.getFirstValue("total_return_debt");
    if (document.getFirstValue("relation_type") != null) {
      this.relationType = RelationTypes.valueOf((String) document.getFirstValue("relation_type"));
    }

    this.totalDeposit = (Double) document.getFirstValue("total_deposit");
    this.totalReturnDebt = (Double) document.getFirstValue("total_return_debt");
    this.totalTradeAmount = (Double) document.getFirstValue("total_trade_amount");
    this.totalReturnAmount = (Double) document.getFirstValue("total_return_amount");
    this.businessScope = (String) document.getFirstValue("business_scope");
    this.areaInfo = (String) document.getFirstValue("area_info");
    Object obd = document.getFirstValue("is_obd");
    if (obd != null) {
      this.isObd = (Boolean) obd;
    }

    Object app = document.getFirstValue("is_app");
    if (app != null) {
      this.isApp = (Boolean) app;
    }
  }

  public boolean getIsObd() {
    return isObd;
  }

  public void setIsObd(boolean isObd) {
    this.isObd = isObd;
  }

  public boolean getIsApp() {
     return isApp;
   }

   public void setIsApp(boolean isApp) {
     this.isApp = isApp;
   }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getLandLine() {
    return landLine;
  }

  public void setLandLine(String landLine) {
    this.landLine = landLine;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if (null != id) {
      this.idStr = id.toString();
    }
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getTotalDebt() {
    return totalDebt;
  }

  public void setTotalDebt(Double totalDebt) {
    this.totalDebt = totalDebt;
  }

  public List<ContactDTO> getContactDTOList() {
    return contactDTOList;
  }

  public void setContactDTOList(List<ContactDTO> contactDTOList) {
    this.contactDTOList = contactDTOList;
  }

  public String getCustomerOrSupplier() {
    return customerOrSupplier;
  }

  public void setCustomerOrSupplier(String customerOrSupplier) {
    this.customerOrSupplier = customerOrSupplier;
  }

  public Long getLastExpenseTime() {
    return lastExpenseTime;
  }

  public void setLastExpenseTime(Long lastExpenseTime) {
    this.lastExpenseTime = lastExpenseTime;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public String[] getLicenseNos() {
    return licenseNos;
  }

  public void setLicenseNos(String[] licenseNos) {
    this.licenseNos = licenseNos;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public Long getLastInventoryTime() {
    return lastInventoryTime;
  }

  public void setLastInventoryTime(Long lastInventoryTime) {
    this.lastInventoryTime = lastInventoryTime;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getCustomerOrSupplierShopId() {
    return customerOrSupplierShopId;
  }

  public void setCustomerOrSupplierShopId(String customerOrSupplierShopId) {
    this.customerOrSupplierShopId = customerOrSupplierShopId;
  }

  public Double getTotalReturnDebt() {
    return totalReturnDebt;
  }

  public void setTotalReturnDebt(Double totalReturnDebt) {
    this.totalReturnDebt = totalReturnDebt;
  }

  public RelationTypes getRelationType() {
    return relationType;
  }

  public void setRelationType(RelationTypes relationType) {
    this.relationType = relationType;
  }

  public Double getTotalDeposit() {
    return totalDeposit;
  }

  public void setTotalDeposit(Double totalDeposit) {
    this.totalDeposit = totalDeposit;
  }

  public Double getTotalTradeAmount() {
    return totalTradeAmount;
  }

  public void setTotalTradeAmount(Double totalTradeAmount) {
    this.totalTradeAmount = totalTradeAmount;
  }

  public Double getTotalReturnAmount() {
    return totalReturnAmount;
  }

  public void setTotalReturnAmount(Double totalReturnAmount) {
    this.totalReturnAmount = totalReturnAmount;
  }

  public String getBusinessScope() {
    return businessScope;
  }

  public void setBusinessScope(String businessScope) {
    this.businessScope = businessScope;
  }

  public Integer getVehicleCount() {
    return vehicleCount;
  }

  public void setVehicleCount(Integer vehicleCount) {
    this.vehicleCount = vehicleCount;
  }

  public MemberDTO getMemberDTO() {
    return memberDTO;
  }

  public void setMemberDTO(MemberDTO memberDTO) {
    this.memberDTO = memberDTO;
  }

  public Integer getCountCustomerReturn() {
    return countCustomerReturn;
  }

  public void setCountCustomerReturn(Integer countCustomerReturn) {
    this.countCustomerReturn = countCustomerReturn;
  }

  public String getLastExpenseTimeStr() {
    return lastExpenseTimeStr;
  }

  public void setLastExpenseTimeStr(String lastExpenseTimeStr) {
    this.lastExpenseTimeStr = lastExpenseTimeStr;
  }

  public String getAreaInfo() {
    return areaInfo;
  }

  public void setAreaInfo(String areaInfo) {
    this.areaInfo = areaInfo;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public List<String> getVehicleDetailList() {
    return vehicleDetailList;
  }

  public void setVehicleDetailList(List<String> vehicleDetailList) {
    this.vehicleDetailList = vehicleDetailList;
  }

  public List<String> getObdVehicleNo() {
    return obdVehicleNo;
  }

  public void setObdVehicleNo(List<String> obdVehicleNo) {
    this.obdVehicleNo = obdVehicleNo;
  }

  public List<String> getAppVehicleNo() {
    return appVehicleNo;
  }

  public void setAppVehicleNo(List<String> appVehicleNo) {
    this.appVehicleNo = appVehicleNo;
  }
}
