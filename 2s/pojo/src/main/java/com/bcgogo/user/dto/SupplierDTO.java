package com.bcgogo.user.dto;

import com.bcgogo.base.BaseDTO;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.enums.user.RelationChangeEnum;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultDTO;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-10-28
 * Time: 上午10:54
 * To change this template use File | Settings | File Templates.
 */
public class SupplierDTO extends BaseDTO implements Serializable {
  private static final Logger LOG = LoggerFactory.getLogger(SupplierDTO.class);

  private String idString;
  private Long shopId;
  private Long userId;
  private Long creationDate;
  private String code;
  private Long supplierShopId;
  private String supplierShopIdString;
  private String name;
  private String abbr;
  private Long category;
  private String categoryStr;
  private Long areaId;
  private String areaStr;
  private String contact;
  private Long contactId;
  private String contactIdStr;
  private ContactDTO[] contacts; // add by zhuj 联系人列表
  private List<ContactDTO> contactDTOList;
  private String mobile;
  //多座机号
  private String landLine;
  private String landLineSecond;
  private String landLineThird;
  private String landLineForAll;
  private String fax;
  private String email;
  private String zip;
  private String address;
  private String qq;
  private String qqArray;//该供应商下所有的qq号
  private String legalRep;
  private String bank;
  private String accountName;
  private String account;
  private String taxNo;
  private String billingAddress;
  private Long invoiceCategoryId;
  private String invoiceCategory;
  private Long settlementTypeId;
  private String settlementType;
  private String invoiceTitle;
  private String businessScope;
  private String[] businessScopes;
  private String otherBusinessScope;
  private String memo;
  private String firstLetters;        //zhangchuanlong

  private Double totalInventoryAmount;
  private Long lastOrderId;
  private String lastOrderProducts;
  private Long lastOrderTime;
  private String lastOrderTimeStr;
  private OrderTypes lastOrderType;
  private String lastOrderTypeDesc;
  private String lastOrderLink;
  private boolean isUpdate;  //判断这个客户是新增还是更新原有的
  private Long lastInventoryTime;    //最后入库时间
  private String lastInventoryTimeStr;    //最后入库时间
  private Double totalDebt;   //欠款总额
  private Long parentId;   //客户合并，保留客户Id

  private Double creditAmount;//应付款总额
  private Double deposit;//定金总额

  /**畅销品、滞销品统、退回统计使用**/
  private double returnAmount;   //销售数量 畅销滞销商品统计使用
  private double returnTotal;     //销售总额畅销滞销商品统计使用
  private int returnProductCategories; //商品在某段时间内退货的数量
  private int returnTimes; //商品在某段时间内退货的数量
  private double queryResult; //返回结果 用于饼图
  private String queryResultStr;
  private String score;
  private CustomerStatus status = CustomerStatus.ENABLED;

  private Double totalTradeAmount;  //累计交易金额
  private Double totalReturnAmount; //累计退货金额
  private Double totalPayable;

  //客户和供应商的relationType废弃掉了，不再使用了，请注意。by qxy
  private RelationTypes relationType;     //关联类型
  private String relationTypeStr;


  private Long invitationCodeSendDate; //邀请码发送时间
  private Integer invitationCodeSendTimes; //邀请码发送次数

  private Double totalReturnDebt; //店铺欠供应商钱
  private int countSupplierReturn;//入库退货次数

  private Double totalReceivable;

  //供应商评价
  private Double totalAverageScore;  //总平均分
  private Double qualityAverageScore; //质量平均分
  private Double performanceAverageScore;//性价比平均分
  private Double speedAverageScore;//发货速度平均分
  private Double attitudeAverageScore; //服务态度平均分
  private Long commentRecordCount; //评分参数人数
  private Long province;
  private Long city;
  private Long region;
  private String identity;
  private String identityStr;
  private Long customerId;
  private String customerIdStr;
  private String areaInfo;
  private Boolean isPermanentDualRole; //既是供应商又是客户且做过对账单的标记，做过对账单关联的一方删除的时候同时删除另外一方
  private List<String> mobileList=new ArrayList<String>();

  private boolean fromManagePage;   //用于判断是否来自客户详情页面，删除联系人逻辑需要
  private boolean changeArea;

  private String thirdCategoryIdStr;//新增客户、新增供应商营业分类三级分类ids
  private List<Long> thirdCategoryIds = new ArrayList<Long>();//客户的经营范围三类ids
  private String thirdCategoryNodeListJson;
  private boolean isCancel;
  private boolean isOnlineShop = false;//是否所有字段都可以修改

  private String vehicleModelContent;
  private String vehicleModelIdStr;
  private List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList;
  private String shopVehicleBrandModelDTOListJson;
  private VehicleSelectBrandModel selectBrandModel;//选择车型时 是否是全部车型

  private String shortMemo;//memo的简写
  private boolean hasMainContact = false;//供应商是否有主联系人

  public void setBusinessScopeInfo(List<BusinessScopeDTO> businessScopeDTOList,Node node){
    if (CollectionUtils.isNotEmpty(businessScopeDTOList)) {
      StringBuffer businessScope = new StringBuffer();
      List<Node> nodeList = new ArrayList<Node>();
      Map<Long,List<Node>> thirdNodeMap = new HashMap<Long, List<Node>>();
      List<Node> thirdNodeList = null;
      for (BusinessScopeDTO businessScopeDTO : businessScopeDTOList) {
        businessScope.append(businessScopeDTO.getProductCategoryId()).append(",");
        Node n = node.findNodeInTree(businessScopeDTO.getProductCategoryId());
        nodeList.add(n);
        thirdNodeList = thirdNodeMap.get(n.getParentId());
        if(thirdNodeList==null){
          thirdNodeList = new ArrayList<Node>();
        }
        thirdNodeList.add(n);
        thirdNodeMap.put(n.getParentId(),thirdNodeList);
      }
      this.setThirdCategoryIdStr(businessScope.toString());


      if(CollectionUtils.isNotEmpty(nodeList)){
        Set<Node> pNodes = new HashSet<Node>();
        Iterator<Node> iterator = nodeList.iterator();
        while (iterator.hasNext()){
          Node n = iterator.next();
          Node pn = node.findNodeInTree(n.getParentId());
          if(pn.getChildren().size()==thirdNodeMap.get(n.getParentId()).size()){
            iterator.remove();
            pNodes.add(pn);
          }
        }
        nodeList.addAll(pNodes);
        try {
          setThirdCategoryNodeListJson(URLEncoder.encode(JsonUtil.listToJson(nodeList), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
      }
    }
  }
  public String getThirdCategoryNodeListJson() {
    return thirdCategoryNodeListJson;
  }

  public void setThirdCategoryNodeListJson(String thirdCategoryNodeListJson) {
    this.thirdCategoryNodeListJson = thirdCategoryNodeListJson;
  }

  public String getVehicleModelContent() {
    return vehicleModelContent;
  }

  public void setVehicleModelContent(String vehicleModelContent) {
    this.vehicleModelContent = vehicleModelContent;
  }

  public void setVehicleBrandModelRelationInfo(List<VehicleBrandModelRelationDTO> vehicleBrandModelRelationDTOList) {
    if(VehicleSelectBrandModel.ALL_MODEL.equals(this.getSelectBrandModel())){
      vehicleModelContent="全部车型";
      return;
    }
    if(CollectionUtils.isNotEmpty(vehicleBrandModelRelationDTOList)){
      List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = new ArrayList<ShopVehicleBrandModelDTO>();
      vehicleModelContent="";vehicleModelIdStr="";
      for(VehicleBrandModelRelationDTO vehicleBrandModelRelationDTO : vehicleBrandModelRelationDTOList){
        vehicleModelIdStr+=vehicleBrandModelRelationDTO.getModelId()+",";
        vehicleModelContent+=vehicleBrandModelRelationDTO.getModelName()+",";
        shopVehicleBrandModelDTOList.add(new ShopVehicleBrandModelDTO(vehicleBrandModelRelationDTO));
      }
      this.setShopVehicleBrandModelDTOList(shopVehicleBrandModelDTOList);
      if(vehicleModelIdStr.length()>1) vehicleModelIdStr = vehicleModelIdStr.substring(0,vehicleModelIdStr.length()-1);
      if(vehicleModelContent.length()>1) vehicleModelContent = vehicleModelContent.substring(0,vehicleModelContent.length()-1);
    }
  }

  public String getVehicleModelIdStr() {
    return vehicleModelIdStr;
  }

  public void setVehicleModelIdStr(String vehicleModelIdStr) {
    this.vehicleModelIdStr = vehicleModelIdStr;
  }

  public List<ShopVehicleBrandModelDTO> getShopVehicleBrandModelDTOList() {
    return shopVehicleBrandModelDTOList;
  }

  public void setShopVehicleBrandModelDTOList(List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList) {
    this.shopVehicleBrandModelDTOList = shopVehicleBrandModelDTOList;
    if(CollectionUtils.isNotEmpty(shopVehicleBrandModelDTOList)){
      try {
        setShopVehicleBrandModelDTOListJson(URLEncoder.encode(JsonUtil.listToJson(shopVehicleBrandModelDTOList), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
      }
    }
  }

  public String getShopVehicleBrandModelDTOListJson() {
    return shopVehicleBrandModelDTOListJson;
  }

  public void setShopVehicleBrandModelDTOListJson(String shopVehicleBrandModelDTOListJson) {
    this.shopVehicleBrandModelDTOListJson = shopVehicleBrandModelDTOListJson;
  }

  public VehicleSelectBrandModel getSelectBrandModel() {
    return selectBrandModel;
  }

  public void setSelectBrandModel(VehicleSelectBrandModel selectBrandModel) {
    this.selectBrandModel = selectBrandModel;
  }

  public String getIdentityStr() {
    return identityStr;
  }

  public void setIdentityStr(String identityStr) {
    this.identityStr = identityStr;
  }

  public boolean isCancel() {
    return isCancel;
  }

  public void setCancel(boolean cancel) {
    isCancel = cancel;
  }

  public boolean getChangeArea() {
    return changeArea;
  }

  public void setChangeArea(boolean changeArea) {
    this.changeArea = changeArea;
  }

  public List<Long> getThirdCategoryIds() {
    return thirdCategoryIds;
  }

  public void setThirdCategoryIds(List<Long> thirdCategoryIds) {
    this.thirdCategoryIds = thirdCategoryIds;
  }

  public String getThirdCategoryIdStr() {
    return thirdCategoryIdStr;
  }

  public void setThirdCategoryIdStr(String thirdCategoryIdStr) {
    this.thirdCategoryIdStr = thirdCategoryIdStr;
  }

  public List<String> getMobileList() {
    return mobileList;
  }

  public void setMobileList(List<String> mobileList) {
    this.mobileList = mobileList;
  }

  public List<ContactDTO> getContactDTOList() {
    return contactDTOList;
  }

  public void setContactDTOList(List<ContactDTO> contactDTOList) {
    this.contactDTOList = contactDTOList;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
    this.setIdentityStr(StringUtils.isNotBlank(identity)?"供应商/客户":"");
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

  public String getCustomerIdStr() {
    return customerId==null?null:customerId.toString();
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
    if(contactId!=null){
      this.contactIdStr = contactId.toString();
    }
  }

  public String getContactIdStr() {
    return contactIdStr;
  }

  public void setContactIdStr(String contactIdStr) {
    this.contactIdStr = contactIdStr;
  }

  //经营范围
  public void toBusinessScopes() {
    StringBuilder bus = new StringBuilder();
    if (!ArrayUtils.isEmpty(this.getBusinessScopes())) {
      for (String s : this.getBusinessScopes()) {
        bus.append(s).append(",");
      }
    }
    if (StringUtils.isNotBlank(this.getOtherBusinessScope())) {
      bus.append("<other:").append(this.getOtherBusinessScope()).append(">,");
    }
    this.setBusinessScope(StringUtil.subString(bus.toString()));
  }

  public String fromBusinessScopes() {
    StringBuilder builder = new StringBuilder();
    List<String> bus = new ArrayList<String>();
    String[] busArray;
    if (StringUtils.isNotBlank(getBusinessScope())) {
      busArray = getBusinessScope().split(",");
      for (String str : busArray) {
        if (str.contains("<")) {
          String[] s = str.split(":");
          String special = s[1].replace(">", "");
          if (s[0].replace("<", "").equals("other")) {
            builder.append("其他：").append(special).append(",");
            this.setOtherBusinessScope(special);
          }
        } else {
          builder.append(str).append(",");
          bus.add(str);
        }
      }
      this.setBusinessScopes(bus.toArray(new String[bus.size()]));
    }
    return StringUtil.subString(builder.toString());
  }

  public SupplierDTO(CustomerSupplierSearchResultDTO searchResultDTO) {
    this.setName(searchResultDTO.getName());
    this.setContact(searchResultDTO.getContact());
    this.setMobile(searchResultDTO.getMobile());
    this.setLandLine(searchResultDTO.getLandLine());
    this.setAddress(searchResultDTO.getAddress());
    this.setBusinessScope(searchResultDTO.getBusinessScope());
    this.setId(searchResultDTO.getId());
    this.setIdString(this.getIdStr());
    this.setShopId(searchResultDTO.getShopId());

    this.setTotalDebt(searchResultDTO.getTotalDebt());
    this.setTotalReturnAmount(searchResultDTO.getTotalReturnAmount());
    this.setDeposit(searchResultDTO.getTotalDeposit());
    this.setTotalReturnDebt(searchResultDTO.getTotalReturnDebt());
    this.setTotalTradeAmount(searchResultDTO.getTotalTradeAmount());
    this.setBusinessScope(searchResultDTO.getBusinessScope());
    this.setSupplierShopId(NumberUtil.longValue(searchResultDTO.getCustomerOrSupplierShopId()));
    this.setSupplierShopIdString(searchResultDTO.getCustomerOrSupplierShopId());
    this.setLastInventoryTime(searchResultDTO.getLastInventoryTime());
    this.setRelationType(searchResultDTO.getRelationType());
    this.setAreaInfo(searchResultDTO.getAreaInfo());
    this.setContactDTOList(searchResultDTO.getContactDTOList());
  }

  /*****/
  public Long getInvitationCodeSendDate() {
    return invitationCodeSendDate;
  }

  public void setInvitationCodeSendDate(Long invitationCodeSendDate) {
    this.invitationCodeSendDate = invitationCodeSendDate;
  }

  public Integer getInvitationCodeSendTimes() {
    return invitationCodeSendTimes;
  }

  public void setInvitationCodeSendTimes(Integer invitationCodeSendTimes) {
    this.invitationCodeSendTimes = invitationCodeSendTimes;
  }

  public String getScore() {
    return score;
  }

  public void setScore(String score) {
    this.score = score;
  }
  public double getQueryResult() {
    return queryResult;
  }

  public void setQueryResult(double queryResult) {
    this.queryResult = queryResult;
  }


  public String getQueryResultStr() {
    return queryResultStr;
  }

  public void setQueryResultStr(String queryResultStr) {
    this.queryResultStr = queryResultStr;
  }

  public CustomerStatus getStatus() {
    return status;
  }

  public void setStatus(CustomerStatus status) {
    this.status = status;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  private SupplierRecordDTO supplierRecordDTO;
  public Double getTotalDebt() {
    return totalDebt;
  }

  public void setTotalDebt(Double totalDebt) {
    this.totalDebt = totalDebt;
  }

  public Long getLastInventoryTime() {
    return lastInventoryTime;
  }

  public void setLastInventoryTime(Long lastInventoryTime) {
    lastInventoryTimeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, lastInventoryTime);
    this.lastInventoryTime = lastInventoryTime;
  }

  public String getLastInventoryTimeStr() {
    return lastInventoryTimeStr;
  }

  public void setLastInventoryTimeStr(String lastInventoryTimeStr) {
    this.lastInventoryTimeStr = lastInventoryTimeStr;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }
  public boolean isUpdate() {
    return isUpdate;
  }

  public Double getCreditAmount() {
    return creditAmount;
  }

  public String getCategoryStr() {
    return categoryStr;
  }

  public void setCategoryStr(String categoryStr) {
    this.categoryStr = categoryStr;
  }

  public void setCreditAmount(Double creditAmount) {
    this.creditAmount = creditAmount;
  }

  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = deposit;
  }

  public void setUpdate(boolean update) {
    isUpdate = update;
  }

  public boolean getIsOnlineShop() {
    return isOnlineShop;
  }

  public void setOnlineShop(boolean onlineShop) {
    isOnlineShop = onlineShop;
  }

  //luyi
  public void set(PurchaseOrderDTO purchaseOrderDTO) {
    this.setId(purchaseOrderDTO.getSupplierId());
    shopId = purchaseOrderDTO.getShopId();
    //code
    //supplierShopId
    supplierShopId = purchaseOrderDTO.getSupplierShopId();
    name = purchaseOrderDTO.getSupplier();
    abbr = purchaseOrderDTO.getAbbr();
    category = purchaseOrderDTO.getCategory();
    //areaId
    contact = purchaseOrderDTO.getContact();  
    mobile = purchaseOrderDTO.getMobile();
    landLine = purchaseOrderDTO.getLandline();
    fax = purchaseOrderDTO.getFax();
    //email
    email = purchaseOrderDTO.getEmail();
    //zip
    address = purchaseOrderDTO.getAddress();
    qq = purchaseOrderDTO.getQq();
    //legalRep
    bank = purchaseOrderDTO.getBank();
    accountName = purchaseOrderDTO.getAccountName();
    account = purchaseOrderDTO.getAccount();
    //taxNo
    //billingAddress
    invoiceCategoryId = purchaseOrderDTO.getInvoiceCategory();
    settlementTypeId = purchaseOrderDTO.getSettlementType();
    //memo
    firstLetters = PinyinUtil.converterToFirstSpell(name);
      province =  purchaseOrderDTO.getProvince();
      city = purchaseOrderDTO.getCity();
      region = purchaseOrderDTO.getRegion();

    ContactDTO contactDTO = new ContactDTO();
    if (purchaseOrderDTO.getContactId() == null || ArrayUtils.isEmpty(getContacts())) {
      contactDTO.setDisabled(1);
      contactDTO.setIsMainContact(1);
      contactDTO.setCustomerId(purchaseOrderDTO.getSupplierId());
      contactDTO.setLevel(0);
      contactDTO.setShopId(purchaseOrderDTO.getShopId());
      contactDTO.setMobile(purchaseOrderDTO.getMobile());
      contactDTO.setName(purchaseOrderDTO.getContact());
    }else {
      boolean isUpdate = false;
      for (ContactDTO temDTO : getContacts()) {
        if (temDTO == null) {
          continue;
        }
        if (NumberUtil.isEqual(purchaseOrderDTO.getContactId(), temDTO.getId())) {
          isUpdate = true;
          contactDTO = temDTO;
          contactDTO.setMobile(purchaseOrderDTO.getMobile());
          contactDTO.setName(purchaseOrderDTO.getContact());
        }
      }
      if (!isUpdate) {
        contactDTO.setDisabled(1);
        contactDTO.setIsMainContact(1);
        contactDTO.setSupplierId(purchaseOrderDTO.getSupplierId());
        contactDTO.setLevel(0);
        contactDTO.setShopId(purchaseOrderDTO.getShopId());
        contactDTO.setMobile(purchaseOrderDTO.getMobile());
        contactDTO.setName(purchaseOrderDTO.getContact());
      }
    }
    ContactDTO[] contactDTOs = new ContactDTO[1];
    contactDTOs[0] = contactDTO;
    setContacts(contactDTOs);
  }

  public void set(PurchaseInventoryDTO purchaseInventoryDTO) {
    this.setName(purchaseInventoryDTO.getSupplier());
    this.setContact(purchaseInventoryDTO.getContact());
    this.setMobile(purchaseInventoryDTO.getMobile());
    this.setLandLine(purchaseInventoryDTO.getLandline());
    this.setAddress(purchaseInventoryDTO.getAddress());
    this.setBank(purchaseInventoryDTO.getBank());
    this.setAccount(purchaseInventoryDTO.getAccount());
    this.setAccountName(purchaseInventoryDTO.getAccountName());
    this.setCategory(StringUtil.nullToObject(purchaseInventoryDTO.getCategory()));
    this.setAbbr(purchaseInventoryDTO.getAbbr());
    this.setSettlementTypeId(purchaseInventoryDTO.getSettlementType());
    this.setFax(purchaseInventoryDTO.getFax());
    this.setQq(purchaseInventoryDTO.getQq());
    this.setInvoiceCategoryId(purchaseInventoryDTO.getInvoiceCategory());
    this.setEmail(purchaseInventoryDTO.getEmail());
    this.setId(purchaseInventoryDTO.getSupplierId());
    this.setShopId(purchaseInventoryDTO.getShopId());
      this.setProvince(purchaseInventoryDTO.getProvince());
      this.setCity(purchaseInventoryDTO.getCity());
      this.setRegion(purchaseInventoryDTO.getRegion());

    ContactDTO contactDTO = new ContactDTO();
    if (purchaseInventoryDTO.getContactId() == null || ArrayUtils.isEmpty(getContacts())) {
      contactDTO.setDisabled(1);
      contactDTO.setIsMainContact(1);
      contactDTO.setCustomerId(purchaseInventoryDTO.getSupplierId());
      contactDTO.setLevel(0);
      contactDTO.setShopId(purchaseInventoryDTO.getShopId());
      contactDTO.setMobile(purchaseInventoryDTO.getMobile());
      contactDTO.setName(purchaseInventoryDTO.getContact());
    }else {
      boolean isUpdate = false;
      for (ContactDTO temDTO : getContacts()) {
        if (temDTO == null) {
          continue;
        }
        if (NumberUtil.isEqual(purchaseInventoryDTO.getContactId(), temDTO.getId())) {
          isUpdate = true;
          contactDTO = temDTO;
          contactDTO.setMobile(purchaseInventoryDTO.getMobile());
          contactDTO.setName(purchaseInventoryDTO.getContact());
        }
      }
      if (!isUpdate) {
        contactDTO.setDisabled(1);
        contactDTO.setIsMainContact(1);
        contactDTO.setSupplierId(purchaseInventoryDTO.getSupplierId());
        contactDTO.setLevel(0);
        contactDTO.setShopId(purchaseInventoryDTO.getShopId());
        contactDTO.setMobile(purchaseInventoryDTO.getMobile());
        contactDTO.setName(purchaseInventoryDTO.getContact());
      }
    }
    ContactDTO[] contactDTOs = new ContactDTO[1];
    contactDTOs[0] = contactDTO;
    setContacts(contactDTOs);
  }

  public void set(PurchaseReturnDTO purchaseReturnDTO) {
    this.setName(purchaseReturnDTO.getSupplier());
    this.setContact(purchaseReturnDTO.getContact());
    this.setMobile(purchaseReturnDTO.getMobile());
    this.setLandLine(purchaseReturnDTO.getLandline());
    this.setAddress(purchaseReturnDTO.getAddress());
    this.setBank(purchaseReturnDTO.getBank());
    this.setAccount(purchaseReturnDTO.getAccount());
    this.setAccountName(purchaseReturnDTO.getAccountName());
    this.setCategory(StringUtil.nullToObject(purchaseReturnDTO.getCategory()));
    this.setAbbr(purchaseReturnDTO.getAbbr());
    this.setSettlementTypeId(purchaseReturnDTO.getSettlementType());
    this.setFax(purchaseReturnDTO.getFax());
    this.setQq(purchaseReturnDTO.getQq());
    this.setInvoiceCategoryId(purchaseReturnDTO.getInvoiceCategory());
    this.setEmail(purchaseReturnDTO.getEmail());
    this.setId(purchaseReturnDTO.getSupplierId());
    this.setShopId(purchaseReturnDTO.getShopId());
    this.setProvince(purchaseReturnDTO.getProvince());
    this.setCity(purchaseReturnDTO.getCity());
    this.setRegion(purchaseReturnDTO.getRegion());

    ContactDTO contactDTO = new ContactDTO();
    if (purchaseReturnDTO.getContactId() == null || ArrayUtils.isEmpty(getContacts())) {
      contactDTO.setDisabled(1);
      contactDTO.setIsMainContact(1);
      contactDTO.setCustomerId(purchaseReturnDTO.getSupplierId());
      contactDTO.setLevel(0);
      contactDTO.setShopId(purchaseReturnDTO.getShopId());
      contactDTO.setMobile(purchaseReturnDTO.getMobile());
      contactDTO.setName(purchaseReturnDTO.getContact());
    }else {
      boolean isUpdate = false;
      for (ContactDTO temDTO : getContacts()) {
        if (temDTO == null) {
          continue;
        }
        if (NumberUtil.isEqual(purchaseReturnDTO.getContactId(), temDTO.getId())) {
          isUpdate = true;
          contactDTO = temDTO;
          contactDTO.setMobile(purchaseReturnDTO.getMobile());
          contactDTO.setName(purchaseReturnDTO.getContact());
        }
      }
      if (!isUpdate) {
        contactDTO.setDisabled(1);
        contactDTO.setIsMainContact(1);
        contactDTO.setSupplierId(purchaseReturnDTO.getSupplierId());
        contactDTO.setLevel(0);
        contactDTO.setShopId(purchaseReturnDTO.getShopId());
        contactDTO.setMobile(purchaseReturnDTO.getMobile());
        contactDTO.setName(purchaseReturnDTO.getContact());
      }
    }
    ContactDTO[] contactDTOs = new ContactDTO[1];
    contactDTOs[0] = contactDTO;
    setContacts(contactDTOs);
  }

  public String toJsonStr() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("{\"supplierIdStr\":\"" + (StringUtils.isBlank(idString) ? "" : idString) + "\",");
    stringBuffer.append("\"supplier\":\"" + StringUtil.truncValue(name) + "\",");
    stringBuffer.append("\"contact\":\"" + StringUtil.truncValue(contact) + "\",");
    // TODO zhuj
    stringBuffer.append("\"mobile\":\"" + StringUtil.truncValue(mobile) + "\",");
    stringBuffer.append("\"bank\":\"" + StringUtil.truncValue(bank) + "\",");
    stringBuffer.append("\"account\":\"" + StringUtil.truncValue(account) + "\",");
    stringBuffer.append("\"accountName\":\"" + StringUtil.truncValue(accountName) + "\",");
    stringBuffer.append("\"businessScope\":\"" + StringUtil.truncValue(businessScope) + "\",");
    stringBuffer.append("\"category\":\"" + (category == null ? "" : category) + "\",");
    stringBuffer.append("\"abbr\":\"" + StringUtil.truncValue(abbr) + "\",");
    stringBuffer.append("\"settlementType\":\"" + (settlementTypeId == null ? "" : settlementTypeId) + "\",");
    stringBuffer.append("\"landLine\":\"" + StringUtil.truncValue(landLine) + "\",");
    stringBuffer.append("\"fax\":\"" + StringUtil.truncValue(fax) + "\",");
    stringBuffer.append("\"qq\":\"" + StringUtil.truncValue(qq) + "\",");
    stringBuffer.append("\"email\":\"" + StringUtil.truncValue(email) + "\",");
    stringBuffer.append("\"invoiceCategory\":\"" + (invoiceCategoryId == null ? "" : invoiceCategoryId) + "\",");
    stringBuffer.append("\"address\":\"" + StringUtil.truncValue(address) + "\"}");
    return stringBuffer.toString().replaceAll("\r\n", "<br/>");
  }


  public String getFirstLetters() {
    return firstLetters;
  }

  public void setFirstLetters(String firstLetters) {
    this.firstLetters = firstLetters;
  }

  public SupplierDTO() {
  }

  public String getIdString() {
    return idString;
  }

  public void setIdString(String idString) {
    this.idString = idString;
  }

  public String getSupplierShopIdString() {
    return supplierShopIdString;
  }

  public void setSupplierShopIdString(String supplierShopIdString) {
    this.supplierShopIdString = supplierShopIdString;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    if(supplierShopId!=null)
      supplierShopIdString = String.valueOf(supplierShopId);
    this.supplierShopId = supplierShopId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    if(name != null && name.length() > 0){
      firstLetters = PinyinUtil.converterToFirstSpell(name);
    }
  }

  public String getAbbr() {
    return abbr;
  }

  public void setAbbr(String abbr) {
    this.abbr = abbr;
  }

  public Long getCategory() {
    return category;
  }

  public void setCategory(Long category) {
    this.category = category;
  }

  public Long getAreaId() {
    return areaId;
  }

  public void setAreaId(Long areaId) {
    this.areaId = areaId;
  }

  public String getAreaStr() {
    return areaStr;
  }

  public void setAreaStr(String areaStr) {
    this.areaStr = areaStr;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getLandLine() {
    return landLine;
  }

  public void setLandLine(String landLine) {
    this.landLine = landLine;
  }

  public String getLandLineSecond() {
    return landLineSecond;
  }

  public void setLandLineSecond(String landLineSecond) {
    this.landLineSecond = landLineSecond;
  }

  public String getLandLineThird() {
    return landLineThird;
  }

  public void setLandLineThird(String landLineThird) {
    this.landLineThird = landLineThird;
  }

  public String getLandLineForAll() {
    return landLineForAll;
  }

  public void setLandLineForAll(String landLineForAll) {
    this.landLineForAll = landLineForAll;
  }

  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getQq() {
    return qq;
  }

  public void setQq(String qq) {
    this.qq = qq;
  }

  public String getQqArray() {
    StringBuffer qqStr = new StringBuffer();
    if (!ArrayUtils.isEmpty(contacts)) {
      for (int i = 0; i < contacts.length; i++) {
        if (contacts[i] != null && StringUtil.isNotEmpty(contacts[i].getQq())) {
          qqStr.append(contacts[i].getQq()).append(",");
        }
      }
      if (qqStr.length() > 0) {
        setQqArray(qqStr.toString());
      }
    }
    return qqArray;
  }
  public void fillingContacts (){
    ContactDTO[] tempContacts = this.contacts;

    boolean hasMainContact = false;

    this.contacts = new ContactDTO[3];
    if(!ArrayUtils.isEmpty(tempContacts)){
      for (int i = 0; i < tempContacts.length; i++) {
        if (tempContacts[i] != null && tempContacts[i].isValidContact()) {
          contacts[i] = tempContacts[i];
        }

        if (tempContacts[i] != null && NumberUtil.intValue(tempContacts[i].getIsMainContact()) == 1) {
          hasMainContact = true;
        }
      }
    }

    if (!hasMainContact) {
      if (contacts[0] == null) {
        contacts[0] = new ContactDTO();
      }
      contacts[0].setIsMainContact(1);
    }


  }
  public void setQqArray(String qqArray) {
    this.qqArray = qqArray;
  }

  public String getLegalRep() {
    return legalRep;
  }

  public void setLegalRep(String legalRep) {
    this.legalRep = legalRep;
  }

  public String getBank() {
    return bank;
  }

  public void setBank(String bank) {
    this.bank = bank;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getTaxNo() {
    return taxNo;
  }

  public void setTaxNo(String taxNo) {
    this.taxNo = taxNo;
  }

  public String getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(String billingAddress) {
    this.billingAddress = billingAddress;
  }

  public Long getInvoiceCategoryId() {
    return invoiceCategoryId;
  }

  public void setInvoiceCategoryId(Long invoiceCategoryId) {
    this.invoiceCategoryId = invoiceCategoryId;
  }

  public Long getSettlementTypeId() {
    return settlementTypeId;
  }

  public void setSettlementTypeId(Long settlementTypeId) {
    this.settlementTypeId = settlementTypeId;
  }

  public String getInvoiceTitle() {
    return invoiceTitle;
  }

  public void setInvoiceTitle(String invoiceTitle) {
    this.invoiceTitle = invoiceTitle;
  }

  public String getInvoiceCategory() {
    return invoiceCategory;
  }

  public void setInvoiceCategory(String invoiceCategory) {
    this.invoiceCategory = invoiceCategory;
  }

  public String getSettlementType() {
    return settlementType;
  }

  public void setSettlementType(String settlementType) {
    this.settlementType = settlementType;
  }

  public String getBusinessScope() {
    return businessScope;
  }

  public void setBusinessScope(String businessScope) {
    this.businessScope = businessScope;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
    this.setShortMemo(StringUtil.getShortStr(memo,50));
  }

  public Double getTotalInventoryAmount() {
    return totalInventoryAmount;
  }

  public Long getLastOrderId() {
    return lastOrderId;
  }

  public void setTotalInventoryAmount(Double totalInventoryAmount) {
    this.totalInventoryAmount = totalInventoryAmount;
  }

  public void setLastOrderId(Long lastOrderId) {
    this.lastOrderId = lastOrderId;
    if(this.lastOrderId != null && this.lastOrderType != null && this.lastOrderLink == null){
      if(OrderTypes.PURCHASE.equals(this.lastOrderType)){
        this.lastOrderLink = "RFbuy.do?method=show&id=" + this.lastOrderId;
      }else if(OrderTypes.INVENTORY.equals(this.lastOrderType)){
        this.lastOrderLink = "storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&type=txn&purchaseInventoryId=" + this.lastOrderId;
      }
    }
  }

  public String getLastOrderProducts() {
    return lastOrderProducts;
  }

  public Long getLastOrderTime() {
    return lastOrderTime;
  }

  public void setLastOrderProducts(String lastOrderProducts) {
    this.lastOrderProducts = lastOrderProducts;
  }

  public void setLastOrderTime(Long lastOrderTime) {
    this.lastOrderTime = lastOrderTime;
    if(this.lastOrderTime != null && !this.lastOrderTime.equals(0L)){
      this.lastOrderTimeStr = DateUtil.dateLongToStr(this.lastOrderTime);
    }
  }

  public OrderTypes getLastOrderType() {
    return lastOrderType;
  }

  public void setLastOrderType(OrderTypes lastOrderType) {
    this.lastOrderType = lastOrderType;
    if(lastOrderType!=null){
      this.lastOrderTypeDesc = this.lastOrderType.getName();
    }
    if(this.lastOrderId != null && this.lastOrderType != null && this.lastOrderLink == null){
      if(OrderTypes.PURCHASE.equals(this.lastOrderType)){
        this.lastOrderLink = "RFbuy.do?method=show&id=" + this.lastOrderId;
      }else if(OrderTypes.INVENTORY.equals(this.lastOrderType)){
        this.lastOrderLink = "storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&type=txn&purchaseInventoryId=" + this.lastOrderId;
      }
    }
  }

  public String getLastOrderLink() {
    return lastOrderLink;
  }

  public void setLastOrderLink(String lastOrderLink) {
    this.lastOrderLink = lastOrderLink;
  }

  public String getLastOrderTimeStr() {
    return lastOrderTimeStr;
  }

  public String getLastOrderTypeDesc() {
    return lastOrderTypeDesc;
  }

  public void setLastOrderTimeStr(String lastOrderTimeStr) {
    this.lastOrderTimeStr = lastOrderTimeStr;
  }

  public void setLastOrderTypeDesc(String lastOrderTypeDesc) {
    this.lastOrderTypeDesc = lastOrderTypeDesc;
  }

  public SupplierRecordDTO getSupplierRecordDTO() {
    return supplierRecordDTO;
  }

  public void setSupplierRecordDTO(SupplierRecordDTO supplierRecordDTO) {
    this.supplierRecordDTO = supplierRecordDTO;
  }

  public double getReturnAmount() {
    return returnAmount;
  }

  public void setReturnAmount(double returnAmount) {
    this.returnAmount = returnAmount;
  }

  public double getReturnTotal() {
    return returnTotal;
  }

  public void setReturnTotal(double returnTotal) {
    this.returnTotal = returnTotal;
  }

  public int getReturnProductCategories() {
    return returnProductCategories;
  }

  public void setReturnProductCategories(int returnProductCategories) {
    this.returnProductCategories = returnProductCategories;
  }

  public int getReturnTimes() {
    return returnTimes;
  }

  public void setReturnTimes(int returnTimes) {
    this.returnTimes = returnTimes;
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

  public Double getTotalPayable() {
    return totalPayable;
  }

  public void setTotalPayable(Double totalPayable) {
    this.totalPayable = totalPayable;
  }

  public Double getTotalReturnDebt() {
    return totalReturnDebt;
  }

  public void setTotalReturnDebt(Double totalReturnDebt) {
    this.totalReturnDebt = totalReturnDebt;
  }

  public int getCountSupplierReturn() {
    return countSupplierReturn;
  }

  public void setCountSupplierReturn(int countSupplierReturn) {
    this.countSupplierReturn = countSupplierReturn;
  }

  public RelationTypes getRelationType() {
    return relationType;
  }

  public void setRelationType(RelationTypes relationType) {
    this.relationType = relationType;
  }

  public String getRelationTypeStr() {
    return relationTypeStr;
  }

  public void setRelationTypeStr(String relationTypeStr) {
    this.relationTypeStr = relationTypeStr;
  }

  public Double getTotalReceivable() {
    return totalReceivable;
  }

  public void setTotalReceivable(Double totalReceivable) {
    this.totalReceivable = totalReceivable;
  }


  public Double getTotalAverageScore() {
    return totalAverageScore;
  }

  public void setTotalAverageScore(Double totalAverageScore) {
    this.totalAverageScore = totalAverageScore;
  }

  public Double getQualityAverageScore() {
    return qualityAverageScore;
  }

  public void setQualityAverageScore(Double qualityAverageScore) {
    this.qualityAverageScore = qualityAverageScore;
  }

  public Double getPerformanceAverageScore() {
    return performanceAverageScore;
  }

  public void setPerformanceAverageScore(Double performanceAverageScore) {
    this.performanceAverageScore = performanceAverageScore;
  }

  public Double getSpeedAverageScore() {
    return speedAverageScore;
  }

  public void setSpeedAverageScore(Double speedAverageScore) {
    this.speedAverageScore = speedAverageScore;
  }

  public Double getAttitudeAverageScore() {
    return attitudeAverageScore;
  }

  public void setAttitudeAverageScore(Double attitudeAverageScore) {
    this.attitudeAverageScore = attitudeAverageScore;
  }

  public Long getCommentRecordCount() {
    return commentRecordCount;
  }

  public void setCommentRecordCount(Long commentRecordCount) {
    this.commentRecordCount = commentRecordCount;
  }

  public Boolean getPermanentDualRole() {
    return isPermanentDualRole;
  }

  public void setPermanentDualRole(Boolean permanentDualRole) {
    isPermanentDualRole = permanentDualRole;
  }

  public ContactDTO[] getContacts() {
    return contacts;
  }

  public void setContacts(ContactDTO[] contacts) {
    this.contacts = contacts;

    if (ArrayUtil.isEmpty(contacts)) {
      setHasMainContact(false);
    } else {
      for (ContactDTO contactDTO : contacts) {
        if (contactDTO != null && NumberUtil.intValue(contactDTO.getIsMainContact()) == 1) {
          setHasMainContact(true);
        }
      }
    }

  }

  /**
   * 比较供应商信息与DTO中信息是否一致。比较的字段包括：
   * name, contact, mobile, landline, address，supplierShopId
   * @param supplierDTO
   * @return
   */
  public boolean compareHistory(SupplierDTO supplierDTO) {
    if(!StringUtil.compareSame(supplierDTO.getName(), getName())){
      return false;
    }
    if(!StringUtil.compareSame(supplierDTO.getLandLine(), getLandLine())){
      return false;
    }

    if(StringUtils.isBlank(supplierDTO.getContact()) || StringUtils.isBlank(supplierDTO.getMobile())){
      return true;
    }else{
      if(ArrayUtils.isEmpty(getContacts()) || !hasValidContact()){
        return false;
      }
      boolean historyContactExist = false;
      for(ContactDTO contactDTO : getContacts()){
        if(contactDTO != null && NumberUtil.isEqual(contactDTO.getId(), supplierDTO.getContactId())){
          historyContactExist = true;
          if(!StringUtil.compareSame(supplierDTO.getContact(), contactDTO.getName())){
            return false;
          }
          if(!StringUtil.compareSame(supplierDTO.getMobile(), contactDTO.getMobile())){
            return false;
          }
        }
      }
      if(!historyContactExist){
        return false;
      }
    }
    return true;
  }

  /**
   * @param historySupplierDTO
   * @return
   */
  public RelationChangeEnum compareRelationHistory(SupplierDTO historySupplierDTO) {
    if(this.getSupplierShopId() == null && (historySupplierDTO == null ||historySupplierDTO.getSupplierShopId() == null)){
      return RelationChangeEnum.UNCHANGED;
    }else if(this.getSupplierShopId() == null && historySupplierDTO!= null && historySupplierDTO.getSupplierShopId() != null){
      return RelationChangeEnum.RELATED_TO_UNRELATED;
    }else if(this.getSupplierShopId() != null && (historySupplierDTO == null ||historySupplierDTO.getSupplierShopId() == null)){
      return RelationChangeEnum.UNRELATED_TO_RELATED;
    }else if(this.getSupplierShopId() != null  && historySupplierDTO!= null
        && historySupplierDTO.getSupplierShopId() != null ){
       if(this.getSupplierShopId().equals(historySupplierDTO.getSupplierShopId())){
         return RelationChangeEnum.UNCHANGED;
       }else {
         return RelationChangeEnum.RELATED_CHANGED;
       }
    }else {
      return RelationChangeEnum.UNCHANGED;
    }
  }

  public List<String> getContactMobiles() {
    if (!ArrayUtils.isEmpty(this.getContacts())) {
      for (ContactDTO dto : this.getContacts()) {
        if (dto != null) {
          mobileList.add(dto.getMobile());
        }
      }
    }
    return mobileList;
  }

  public void fromSupplierShopDTO(ShopDTO supplierShopDTO) {
    if(supplierShopDTO == null){
      return;
    }
    this.setSupplierShopId(supplierShopDTO.getId());
    this.setName(supplierShopDTO.getName());
    this.setAddress(supplierShopDTO.getAddress());
    this.setProvince(supplierShopDTO.getProvince());
    this.setCity(supplierShopDTO.getCity());
    this.setRegion(supplierShopDTO.getRegion());
    this.setMobile(supplierShopDTO.getStoreManagerMobile());
    this.setContact(supplierShopDTO.getStoreManager());
    this.setContacts(supplierShopDTO.getContacts()); // add by zhuj
    this.setLandLine(supplierShopDTO.getLandline());
    this.setFax(supplierShopDTO.getFax());
    this.setEmail(supplierShopDTO.getEmail());
    this.setQq(supplierShopDTO.getQq());
    this.setBank(supplierShopDTO.getBank());
    this.setAccount(supplierShopDTO.getAccount());
    this.setBusinessScope(supplierShopDTO.getBusinessScope());
    this.setSelectBrandModel(supplierShopDTO.getShopSelectBrandModel());
    StringBuffer qqStr = new StringBuffer();
    try{
      if(!ArrayUtils.isEmpty(supplierShopDTO.getContacts())){
        ContactDTO[] contactDTOs = supplierShopDTO.getContacts();
        ContactDTO[] supplierContactDTOs = new ContactDTO[0];
        for (int i = 0; i < contactDTOs.length; i++) {
          if (contactDTOs[i] != null && contactDTOs[i].isValidContact()) { // 数组可以存储null
            ContactDTO supplierContactDTO = contactDTOs[i].clone();
            if(supplierContactDTO != null && StringUtil.isNotEmpty(supplierContactDTO.getQq())){
              qqStr.append(supplierContactDTO.getQq()).append(",");
            }

            mobileList.add(supplierContactDTO.getMobile());
            supplierContactDTO.setShopId(null);
            supplierContactDTO.setCustomerId(null);
            supplierContactDTO.setSupplierId(null);
            supplierContactDTO.setId(null);
            supplierContactDTO.setIdStr(null);
            if(supplierContactDTO.getIsShopOwner() !=null && supplierContactDTO.getIsShopOwner() == 1){
              supplierContactDTO.setIsMainContact(1);
              supplierContactDTO.setLevel(0);
            }else{
              supplierContactDTO.setIsMainContact(0);
            }
            supplierContactDTOs = (ContactDTO[])ArrayUtils.add(supplierContactDTOs, supplierContactDTO);
          }
        }
        if(qqStr.length() >0){
          setQqArray(qqStr.toString());
        }
        this.setContacts(supplierContactDTOs);
      }
    }catch(CloneNotSupportedException e){
      LOG.error("SupplierDTO.fromSupplierShopDTO error.", e);
      LOG.error("supplierShopDTO:{}", supplierShopDTO.toString());
    }
  }

  //用于显示供应商评分score panel
  public void fromSupplierCommentStat(CommentStatDTO commentStatDTO) {
    if (commentStatDTO == null) {
      return;
    }
    this.setTotalAverageScore(commentStatDTO.getTotalScore());
    this.setCommentRecordCount(commentStatDTO.getRecordAmount());
    this.setQualityAverageScore(commentStatDTO.getQualityTotalScore());
    this.setPerformanceAverageScore(commentStatDTO.getPerformanceTotalScore());
    this.setSpeedAverageScore(commentStatDTO.getSpeedTotalScore());
    this.setAttitudeAverageScore(commentStatDTO.getAttitudeTotalScore());
  }

  //在线退货要跟新的字段
  public void setByOnlinePurchaseReturn(PurchaseReturnDTO purchaseReturnDTO) {
    if (purchaseReturnDTO != null) {
      this.setContact(purchaseReturnDTO.getContact());
      this.setMobile(purchaseReturnDTO.getMobile());
      this.setProvince(purchaseReturnDTO.getProvince());
      this.setCity(purchaseReturnDTO.getCity());
      this.setRegion(purchaseReturnDTO.getRegion());
      this.setAddress(purchaseReturnDTO.getAddress());
    }
  }

  public void setAreaInfo(String areaInfo) {
    this.areaInfo = areaInfo;
  }

  public String getAreaInfo() {
    return areaInfo;
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

  public boolean isFromManagePage() {
    return fromManagePage;
  }

  public void setFromManagePage(boolean fromManagePage) {
    this.fromManagePage = fromManagePage;
  }

  public void fromCustomerRecordDTO(CustomerRecordDTO customerRecordDTO) {
    this.setName(customerRecordDTO.getName());
    this.setProvince(customerRecordDTO.getProvince());
    this.setCity(customerRecordDTO.getCity());
    this.setRegion(customerRecordDTO.getRegion());
    this.setAddress(customerRecordDTO.getAddress());
    this.setAbbr(customerRecordDTO.getShortName());
    this.setContact(customerRecordDTO.getContact());
    this.setContacts(customerRecordDTO.getContacts()); // add by zhuj
    this.setMobile(customerRecordDTO.getMobile());
    this.setEmail(customerRecordDTO.getEmail());
    this.setQq(customerRecordDTO.getQq());
    this.setLandLine(customerRecordDTO.getPhone());
    this.setFax(customerRecordDTO.getFax());
    this.setBank(customerRecordDTO.getBank());
    this.setAccount(customerRecordDTO.getAccount());
    this.setAccountName(customerRecordDTO.getBankAccountName());
    if(StringUtils.isNotBlank(customerRecordDTO.getBusinessScope())) {
        this.setBusinessScope(customerRecordDTO.getBusinessScope());
    }
    this.setMemo(customerRecordDTO.getMemo());
    if(StringUtil.isNotEmpty(customerRecordDTO.getSettlementType()))this.setSettlementTypeId(Long.valueOf(customerRecordDTO.getSettlementType()));
    if(StringUtil.isNotEmpty(customerRecordDTO.getInvoiceCategory()))this.setInvoiceCategoryId(Long.valueOf(customerRecordDTO.getInvoiceCategory()));
//          this.setSettlementType(customerRecordDTO.getSettlementType());
//          this.setInvoiceCategory(customerRecordDTO.getInvoiceCategory());
  }

  public void fromCustomerDTO(CustomerDTO customerDTO,String businessScope) {
    this.setShopId(customerDTO.getShopId());
    this.setName(customerDTO.getName());
    this.setMobile(customerDTO.getMobile());
    this.setLandLine(customerDTO.getLandLine());
    this.setLandLineSecond(customerDTO.getLandLineSecond());
    this.setLandLineThird(customerDTO.getLandLineThird());
    this.compositeLandline();
    this.setContact(customerDTO.getContact());
    this.setContacts(customerDTO.getContacts()); // add by zhuj
    this.setFax(customerDTO.getFax());
    this.setAddress(customerDTO.getAddress());
    this.setQq(customerDTO.getQq());
    this.setEmail(customerDTO.getEmail());
    this.setBank(customerDTO.getBank());
    this.setAccountName(customerDTO.getBankAccountName());
    this.setAccount(customerDTO.getAccount());
    this.setMemo(customerDTO.getMemo());
    this.setInvoiceCategory(customerDTO.getInvoiceCategoryStr());
    this.setSettlementType(customerDTO.getSettlementTypeStr());
    this.setSettlementTypeId(customerDTO.getSettlementType());
    this.setInvoiceCategoryId(customerDTO.getInvoiceCategory());
    this.setAbbr(customerDTO.getShortName());
    this.setProvince(customerDTO.getProvince());
    this.setCity(customerDTO.getCity());
    this.setRegion(customerDTO.getRegion());
    if (businessScope != null) {
      this.setBusinessScope(businessScope);
    }
    this.setSelectBrandModel(customerDTO.getSelectBrandModel());
    this.setVehicleModelIdStr(customerDTO.getVehicleModelIdStr());
  }


  public void setPartShopDTOInfo(ShopDTO supplierShopDTO,Boolean isRelated) {
    if(supplierShopDTO != null){
      if(isRelated){
        this.setName(supplierShopDTO.getName());
        this.setSupplierShopId(supplierShopDTO.getId());
        this.setMobile(supplierShopDTO.getStoreManagerMobile());
        this.setContact(supplierShopDTO.getStoreManager());
        this.setContacts(supplierShopDTO.getContacts()); // add by zhuj
        this.setLandLine(supplierShopDTO.getLandline());
        this.setFax(supplierShopDTO.getFax());
        this.setEmail(supplierShopDTO.getEmail());
        this.setBusinessScope(supplierShopDTO.getBusinessScopeStr());
        this.setQqArray(supplierShopDTO.getQqArray());
      }else{
        this.setName(supplierShopDTO.getName());
        this.setSupplierShopId(supplierShopDTO.getId());
        if(StringUtils.isNotBlank(supplierShopDTO.getStoreManagerMobile()) && supplierShopDTO.getStoreManagerMobile().length()>3){
          this.setMobile(supplierShopDTO.getStoreManagerMobile().substring(0,3)+"********");
        }

        if(StringUtils.isNotBlank(supplierShopDTO.getStoreManager())){
          this.setContact(supplierShopDTO.getStoreManager().substring(0,1)+"**");
        }

        // TODO zhuj

        this.setLandLine("********");
        this.setFax("********");
        if(StringUtils.isNotBlank(supplierShopDTO.getEmail()) && supplierShopDTO.getEmail().indexOf("@")>-1){
          this.setEmail("******@"+supplierShopDTO.getEmail().split("@")[1]);
        }else{
          this.setEmail("******");
        }
        this.setBusinessScope(supplierShopDTO.getBusinessScopeStr());
      }

    }
  }

  public boolean hasValidContact() {
    ContactDTO[] contactDTOs = this.getContacts();
    if (!ArrayUtils.isEmpty(contactDTOs)) {
      for (ContactDTO contactDTO : contactDTOs) {
        if (contactDTO != null && contactDTO.isValidContact()) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isValidMainContact(){
    if (StringUtils.isBlank(this.getContact()) && StringUtils.isBlank(this.getMobile())
        && StringUtils.isBlank(this.getEmail()) && StringUtils.isBlank(this.getQq())) {
      return false;
    }
    return true;
  }

  public String getMainContactMobile() {
    if(ArrayUtil.isNotEmpty(this.getContacts())){
      for (ContactDTO dto : getContacts()) {
        if(dto!=null && StringUtil.isNotEmpty(dto.getMobile())){
          return dto.getMobile();
        }
      }
    }
    return "";
  }
  //既是客户又是供应商的更新
  public void updateFromSaleOrderDTO(SalesOrderDTO salesOrderDTO) {
    if (salesOrderDTO != null) {
      setMobile(salesOrderDTO.getMobile());
      setName(salesOrderDTO.getCustomer());
      setShopId(salesOrderDTO.getShopId());
      setContact(salesOrderDTO.getContact());
      setLandLine(salesOrderDTO.getLandline());
      if (getRelationType() == null) {
        setRelationType(RelationTypes.UNRELATED);
      }
      ContactDTO contactDTO = new ContactDTO();
      //单据上填写了联系人信息
      if (!(StringUtils.isEmpty(salesOrderDTO.getContact()) && StringUtils.isEmpty(salesOrderDTO.getMobile()))) {
        if (salesOrderDTO.getContactId() == null || ArrayUtils.isEmpty(getContacts())) {
          contactDTO.setDisabled(ContactConstant.ENABLED);
          contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
          contactDTO.setCustomerId(salesOrderDTO.getCustomerId());
          contactDTO.setSupplierId(getId());
          contactDTO.setLevel(ContactConstant.LEVEL_0);
          contactDTO.setShopId(salesOrderDTO.getShopId());
          contactDTO.setMobile(salesOrderDTO.getMobile());
          contactDTO.setName(salesOrderDTO.getContact());
        } else {
          boolean isUpdate = false;
          for (ContactDTO temDTO : getContacts()) {
            if (temDTO == null) {
              continue;
            }
            if (NumberUtil.isEqual(salesOrderDTO.getContactId(), temDTO.getId())) {
              isUpdate = true;
              contactDTO = temDTO;
              contactDTO.setMobile(salesOrderDTO.getMobile());
              contactDTO.setName(salesOrderDTO.getCustomer());
            }
          }
          if (!isUpdate) {
            contactDTO.setDisabled(ContactConstant.ENABLED);
            contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
            contactDTO.setCustomerId(salesOrderDTO.getCustomerId());
            contactDTO.setSupplierId(getId());
            contactDTO.setLevel(ContactConstant.LEVEL_0);
            contactDTO.setShopId(salesOrderDTO.getShopId());
            contactDTO.setMobile(salesOrderDTO.getMobile());
            contactDTO.setName(salesOrderDTO.getContact());
          }
        }
        ContactDTO[] contactDTOs = new ContactDTO[1];
        contactDTOs[0] = contactDTO;
        setContacts(contactDTOs);
      }
    }
  }

  public void updateFromSaleReturnDTO(SalesReturnDTO salesReturnDTO) {
     if (salesReturnDTO != null) {
       setMobile(salesReturnDTO.getMobile());
       setName(salesReturnDTO.getCustomer());
       setShopId(salesReturnDTO.getShopId());
       setContact(salesReturnDTO.getContact());
       setLandLine(salesReturnDTO.getLandline());
       if (getRelationType() == null) {
         setRelationType(RelationTypes.UNRELATED);
       }
       ContactDTO contactDTO = new ContactDTO();
       //单据上填写了联系人信息
       if (!(StringUtils.isEmpty(salesReturnDTO.getContact()) && StringUtils.isEmpty(salesReturnDTO.getMobile()))) {
         if (salesReturnDTO.getContactId() == null || ArrayUtils.isEmpty(getContacts())) {
           contactDTO.setDisabled(ContactConstant.ENABLED);
           contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
           contactDTO.setCustomerId(salesReturnDTO.getCustomerId());
           contactDTO.setSupplierId(getId());
           contactDTO.setLevel(ContactConstant.LEVEL_0);
           contactDTO.setShopId(salesReturnDTO.getShopId());
           contactDTO.setMobile(salesReturnDTO.getMobile());
           contactDTO.setName(salesReturnDTO.getContact());
         } else {
           boolean isUpdate = false;
           for (ContactDTO temDTO : getContacts()) {
             if (temDTO == null) {
               continue;
             }
             if (NumberUtil.isEqual(salesReturnDTO.getContactId(), temDTO.getId())) {
               isUpdate = true;
               contactDTO = temDTO;
               contactDTO.setMobile(salesReturnDTO.getMobile());
               contactDTO.setName(salesReturnDTO.getCustomer());
             }
           }
           if (!isUpdate) {
             contactDTO.setDisabled(ContactConstant.ENABLED);
             contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
             contactDTO.setCustomerId(salesReturnDTO.getCustomerId());
             contactDTO.setSupplierId(getId());
             contactDTO.setLevel(ContactConstant.LEVEL_0);
             contactDTO.setShopId(salesReturnDTO.getShopId());
             contactDTO.setMobile(salesReturnDTO.getMobile());
             contactDTO.setName(salesReturnDTO.getContact());
           }
         }
         ContactDTO[] contactDTOs = new ContactDTO[1];
         contactDTOs[0] = contactDTO;
         setContacts(contactDTOs);
       }
     }
   }

  public void updateMobile(String mobile) {
    if(!ArrayUtils.isEmpty(getContacts()) && hasValidContact()){
      for(ContactDTO contactDTO : getContacts()){
        if(contactDTO!=null && contactDTO.getIsMainContact()!=null && contactDTO.getIsMainContact() == 1){
          contactDTO.setMobile(mobile);
          break;
        }
      }
    }else{
      ContactDTO contactDTO = new ContactDTO(null, getName(), mobile, getEmail(), getQq(), null, getId(), getShopId(), 0, 1, 1, null);
      ContactDTO[] contactDTOs = new ContactDTO[]{contactDTO};
      setContacts(contactDTOs);
    }
  }

  public SupplierDTO fromCustomerOrSupplierDTO(CustomerOrSupplierDTO csDTO) {
    if(csDTO==null) return null;
    this.setShopId(csDTO.getShopId());
    this.setName(csDTO.getName());
    this.setContact(csDTO.getContact());
    this.setAbbr(csDTO.getAbbr());
    this.setMobile(csDTO.getMobile());
    this.setLandLine(csDTO.getLandline());
    this.setFax(csDTO.getFax());
    this.setQq(csDTO.getQq());
    this.setAreaId(csDTO.getAreaId());
    this.setBank(csDTO.getBank());
    this.setCategory(csDTO.getCategory());
    this.setEmail(csDTO.getEmail());
    this.setAccount(csDTO.getAccount());
    this.setAccountName(csDTO.getAccountName());
    this.setAddress(csDTO.getAddress());
//    this.setBusinessScope(csDTO.getBusinessScope()); 营业分类暂时不做更新，待新需求
    this.setSettlementTypeId(csDTO.getSettlementType());
    this.setInvoiceCategoryId(csDTO.getInvoiceCategory());
    this.setCategory(csDTO.getCategory());
    if(csDTO.getCustomerOrSupplierId()==null){
      this.setRelationType(RelationTypes.UNRELATED);
    }
    if (StringUtils.isNotEmpty(csDTO.getContact()) || StringUtils.isNotEmpty(csDTO.getMobile())) {
      ContactDTO contactDTO = new ContactDTO();
      if (csDTO.getContactId() == null || ArrayUtils.isEmpty(getContacts())) {
        contactDTO.setDisabled(ContactConstant.ENABLED);
        contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
        contactDTO.setCustomerId(getCustomerId());
        contactDTO.setSupplierId(csDTO.getCustomerOrSupplierId());
        contactDTO.setLevel(ContactConstant.LEVEL_0);
        contactDTO.setShopId(csDTO.getShopId());
        contactDTO.setMobile(csDTO.getMobile());
        contactDTO.setName(csDTO.getContact());
        contactDTO.setEmail(csDTO.getEmail());
        contactDTO.setQq(csDTO.getQq());
      } else {
        boolean isUpdate = false;
        for (ContactDTO temDTO : getContacts()) {
          if (temDTO == null) {
            continue;
          }
          if (NumberUtil.isEqual(csDTO.getContactId(), temDTO.getId())) {
            isUpdate = true;
            contactDTO = temDTO;
            contactDTO.setMobile(csDTO.getMobile());
            contactDTO.setName(csDTO.getContact());
          }
        }
        if (!isUpdate) {
          contactDTO.setDisabled(ContactConstant.ENABLED);
          contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
          contactDTO.setCustomerId(getCustomerId());
          contactDTO.setSupplierId(csDTO.getCustomerOrSupplierId());
          contactDTO.setLevel(ContactConstant.LEVEL_0);
          contactDTO.setShopId(csDTO.getShopId());
          contactDTO.setMobile(csDTO.getMobile());
          contactDTO.setName(csDTO.getContact());
          contactDTO.setEmail(csDTO.getEmail());
          contactDTO.setQq(csDTO.getQq());
        }
      }
      ContactDTO[] contactDTOs = new ContactDTO[1];
      contactDTOs[0] = contactDTO;
      setContacts(contactDTOs);
    }
    return this;
  }

  public void setContactListUsingArray(){
    if(ArrayUtils.isEmpty(getContacts())){
      return;
    }
    contactDTOList = new ArrayList<ContactDTO>();
    for(ContactDTO contactDTO : getContacts()){
      if(contactDTO == null){
        continue;
      }
      contactDTOList.add(contactDTO);
    }
  }

  public Set<Long> buildAreaNoSet() {
    Set<Long> areaNos = new HashSet<Long>();
    areaNos.add(this.getProvince());
    areaNos.add(this.getCity());
    areaNos.add(this.getRegion());
    return areaNos;
  }

  public void setAreaByAreaNo(Map<Long, AreaDTO> areaMap) {
    StringBuffer sb = new StringBuffer();
    if (areaMap != null && !areaMap.isEmpty()) {
      if (this.getProvince() != null) {
        AreaDTO provinceArea = areaMap.get(this.getProvince());
        if (provinceArea != null) {
          sb.append(provinceArea.getName());
        }
      }
      if (this.getCity() != null) {
        AreaDTO cityArea = areaMap.get(this.getCity());
        if (cityArea != null) {
          sb.append(cityArea.getName());
        }
      }
      if (this.getRegion() != null) {
        AreaDTO regionArea = areaMap.get(this.getRegion());
        if (regionArea != null) {
          sb.append(regionArea.getName());
        }
      }
    }
    this.setAreaStr(sb.toString()); // 页面里面使用areaStr这个字段
  }

  //校验是否至少有1个联系人是新增的
  public boolean isAddContacts() {
    boolean isAdd = false;
    if(!ArrayUtils.isEmpty(getContacts())){
       for(ContactDTO contactDTO :getContacts()){
         if(contactDTO != null && contactDTO.getId() == null && contactDTO.isValidContact()){
           isAdd = true;
           break;
         }
       }
    }
    return isAdd;
  }


  public void filledUnEditinfo(SupplierDTO dbSupplierDTO){
    this.setSupplierShopId(dbSupplierDTO.getSupplierShopId());
    this.setLastOrderId(dbSupplierDTO.getLastOrderId());
    this.setLastOrderProducts(dbSupplierDTO.getLastOrderProducts());
    this.setLastOrderTime(dbSupplierDTO.getLastOrderTime());
    this.setLastOrderType(dbSupplierDTO.getLastOrderType());
    this.setTotalInventoryAmount(dbSupplierDTO.getTotalInventoryAmount());
    this.setLastInventoryTime(dbSupplierDTO.getLastInventoryTime());
    this.setScore(dbSupplierDTO.getScore());
    this.setInvitationCodeSendTimes(dbSupplierDTO.getInvitationCodeSendTimes());
    this.setInvitationCodeSendDate(dbSupplierDTO.getInvitationCodeSendDate());
    this.setPermanentDualRole(dbSupplierDTO.getPermanentDualRole());
  }

  public String getShortMemo() {
    return shortMemo;
  }

  public void setShortMemo(String shortMemo) {
    this.shortMemo = shortMemo;
  }

  public void compositeLandline(){
    StringBuffer landLineForAll=new StringBuffer("");
    if(StringUtils.isNotBlank(this.getLandLine())){
      landLineForAll.append(this.getLandLine());
    }
    if(StringUtils.isNotBlank(this.getLandLineSecond())){
      if(StringUtils.isBlank(this.getLandLine())){
        landLineForAll.append(this.getLandLineSecond());
      }else{
        landLineForAll.append("/").append(this.getLandLineSecond());
      }
    }
    if(StringUtils.isNotBlank(this.getLandLineThird())){
      if(StringUtils.isNotBlank(this.getLandLineSecond())||StringUtils.isNotBlank(this.getLandLine())){
        landLineForAll.append("/").append(this.getLandLineThird());
      }else{
        landLineForAll.append(this.getLandLineThird());
      }

    }
    this.setLandLineForAll(landLineForAll.toString());
  }


  public boolean isHasMainContact() {
    return hasMainContact;
  }

  public void setHasMainContact(boolean hasMainContact) {
    this.hasMainContact = hasMainContact;
  }
}
