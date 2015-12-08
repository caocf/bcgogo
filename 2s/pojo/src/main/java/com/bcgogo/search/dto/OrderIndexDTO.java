package com.bcgogo.search.dto;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PayMethod;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.txn.dto.*;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-20
 * Time: 下午4:39
 * To change this template use File | Settings | File Templates.
 */
public class OrderIndexDTO implements Serializable {
  private static final Logger LOG = LoggerFactory.getLogger(OrderIndexDTO.class);
  private Long id;
  private String idStr;
  private Long shopId;         //店面ID
  private String shopName;
  private String shopKind;
  private String editor;
  private List<Long> shopAreaIdList;
  private String shopAreaInfo;
  private Long orderId;        //单据ID
  private String orderIdStr;        //单据ID
  private OrderTypes orderType;    // 单据类型
  private String orderContent;      //单据内容
  private OrderStatus orderStatus;     //  状态
  private String orderStatusValue;     //状态Value
  private Double orderTotalAmount; //单据总额
  private Double orderTotalCostPrice;//成本
  private Double discount;//折扣
  private Double orderDebt; //
  private Double orderSettled; //单据实收
  private Double strikeAmount;//冲账
  private String vehicle;      // 车牌号
  private String vehicleBrand;
  private String vehicleModel;
  private String vehicleColor;
  private String vehicleContact;
  private String vehicleMobile;
  private Long customerOrSupplierId;    // 客户或供应商ID
  private String customerOrSupplierName;   // 客户或供应商名字  private Double orderTotalAmount; //单据金额总计
  private Double arrears; //欠款；
  private Long paymentTime;//还款时间
  private String paymentTimeStr;
  private String contact;//联系人
  private String contactNum;//联系方式
  private String address;
  private String serviceWorker; //维修美容单中的施工人 或者洗车单中的洗车人   根据，区分
  private String serviceWorkerStr;//施工人如果长度超过4，则截取放在这里
  private String salesMans; //销售人员  根据，区分
  private Set<String> operators; //操作人员
  private Set<Long> operatorIds;
  private List<PayMethod> payMethods; //结算方式
  private Double memberBalancePay;//会员储值支付
  private List<String> payPerProjects = new ArrayList<String>();//计次收费项目
  private Set<String> couponTypes = new HashSet<String>(); //消费券类型
  private String url;//点击详情的超链接字符串

  private String washingStr;
  private String salesStr;
  private String serviceStr;
  @Deprecated
  private long creationDate; //请使用vestDate 单据归属时间
  @Deprecated
  private String createdDateStr;

  private String washing;//当washingStr的长度大于10的时候，截取前十个字符，用户在assistantDetail页面显示
  private String sales;  //当salesStr的长度大于10的时候，截取前十个字符，用户在assistantDetail页面显示
  private String service; //当serviceStr的长度大于10的时候，截取前十个字符，用户在assistantDetail页面显示

  private List<ItemIndexDTO> itemIndexDTOList;
  private List<ItemIndexDTO> inOutRecordDTOList;

  private String startDateStr;
  private Long endDate; //出场时间
  private String endDateStr;
  private Long vestDate; //归属时间
  private String vestDateStr;


  private CustomerDTO customerDTO;
  private SupplierDTO supplierDTO;

  private DebtDTO debtDTO;
  private ReceivableDTO receivableDTO;

  private RepairOrderDTO repairOrderDTO;          //施工单
  private WashBeautyOrderDTO washBeautyOrderDTO;  //洗车美容
  private SalesOrderDTO salesOrderDTO;            //销售
  private PurchaseReturnDTO purchaseReturnDTO;  //入库退货
  private MemberCardOrderDTO memberCardOrderDTO;//会员卡
  private PurchaseOrderDTO purchaseOrderDTO;   //采购单
  private PurchaseInventoryDTO purchaseInventoryDTO;   //入库单
  private MemberCardReturnDTO memberCardReturnDTO;  //会员退卡

  private Double worth;   //储值新增金额
  private Double memberBalance;//储值余额
  private Double memberLastRecharge;//上次储值余额
  private String receiptNo;

  private CustomerStatus customerStatus;

  private String memberNo;//客户本身会员账号
  private String memberType;//客户本身会员类型
  private Double memberLastBuyTotal;//上次购卡金额
  private Long memberLastBuyDate;//上次购卡时间
  private String  memberLastBuyDateStr;
  private String memberStatus;
  private String memo;

  //结算时使用的会员消费信息 存入solr用于会员消费统计
  private Long accountMemberId;//结算会员id
  private String accountMemberNo;//结算时使用的会员号码

  private Double memberDiscountRatio;
  private Double afterMemberDiscountTotal;
  //营业统计
  private String orderTypeStr;//单据类型字符串

  private String productNames;
  private String storehouseName;
  private Long storehouseId;


  private String debtType;//单据欠款类型 用于区分是应收还是应付 应收:receivable 应付:payable

  private Long customerOrSupplierShopId;//关联单据对方的shopID

  private String title;//预购标题
  private BusinessChanceType businessChanceType;
  private List<Long> customerOrSupplierAreaIdList;   //客户或者供应商的区域ID

  private Long inventoryVestDate;//采购单对应的入库时间

  public BusinessChanceType getBusinessChanceType() {
    return businessChanceType;
  }

  public void setBusinessChanceType(BusinessChanceType businessChanceType) {
    this.businessChanceType = businessChanceType;
  }

  public Long getInventoryVestDate() {
    return inventoryVestDate;
  }

  public void setInventoryVestDate(Long inventoryVestDate) {
    this.inventoryVestDate = inventoryVestDate;
  }

  public List<Long> getCustomerOrSupplierAreaIdList() {
    return customerOrSupplierAreaIdList;
  }

  public void setCustomerOrSupplierAreaIdList(List<Long> customerOrSupplierAreaIdList) {
    this.customerOrSupplierAreaIdList = customerOrSupplierAreaIdList;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public String getShopKind() {
    return shopKind;
  }

  public void setShopKind(String shopKind) {
    this.shopKind = shopKind;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDebtType() {
    return debtType;
  }

  public void setDebtType(String debtType) {
    this.debtType = debtType;
  }

  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }

  public String getStorehouseName() {
    return storehouseName;
  }

  public void setStorehouseName(String storehouseName) {
    this.storehouseName = storehouseName;
  }

  public String getOrderTypeStr() {
    return orderTypeStr;
  }

  public void setOrderTypeStr(String orderTypeStr) {
    this.orderTypeStr = orderTypeStr;
  }

  public Long getAccountMemberId() {
    return accountMemberId;
  }

  public void setAccountMemberId(Long accountMemberId) {
    this.accountMemberId = accountMemberId;
  }

  public String getAccountMemberNo() {
    return accountMemberNo;
  }

  public void setAccountMemberNo(String accountMemberNo) {
    this.accountMemberNo = accountMemberNo;
  }

  public OrderIndexDTO(){

  }

  public Double getMemberBalancePay() {
    return memberBalancePay;
  }

  public void setMemberBalancePay(Double memberBalancePay) {
    this.memberBalancePay = memberBalancePay;
  }

  public Double getMemberLastRecharge() {
    return memberLastRecharge;
  }

  public void setMemberLastRecharge(Double memberLastRecharge) {
    this.memberLastRecharge = memberLastRecharge;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Double getOrderDebt() {
    return orderDebt;
  }

  public void setOrderDebt(Double orderDebt) {
    this.orderDebt = orderDebt;
  }

  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  public Double getOrderSettled() {
    return orderSettled;
  }

  public void setOrderSettled(Double orderSettled) {
    this.orderSettled = orderSettled;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDateStr = DateUtil.convertDateLongToString(endDate);
    this.endDate = endDate;
  }

  public Double getMemberLastBuyTotal() {
    return memberLastBuyTotal;
  }

  public void setMemberLastBuyTotal(Double memberLastBuyTotal) {
    this.memberLastBuyTotal = memberLastBuyTotal;
  }

  public Long getMemberLastBuyDate() {
    return memberLastBuyDate;
  }

  public void setMemberLastBuyDate(Long memberLastBuyDate) {
    this.memberLastBuyDate = memberLastBuyDate;
    memberLastBuyDateStr = DateUtil.convertDateLongToDateString("yyyy-MM-dd",memberLastBuyDate);
  }

  public String getMemberLastBuyDateStr() {
    return memberLastBuyDateStr;
  }

  public void setMemberLastBuyDateStr(String memberLastBuyDateStr) {
    this.memberLastBuyDateStr = memberLastBuyDateStr;
  }

  public Double getOrderTotalCostPrice() {
    return orderTotalCostPrice;
  }

  public void setOrderTotalCostPrice(Double orderTotalCostPrice) {
    this.orderTotalCostPrice = orderTotalCostPrice;
  }

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public List<String> getPayPerProjects() {
    return payPerProjects;
  }

  public void setPayPerProjects(List<String> payPerProjects) {
    this.payPerProjects = payPerProjects;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    if(StringUtils.isBlank(vestDateStr)){
      this.vestDateStr=DateUtil.convertDateLongToDateString("yyyy-MM-dd",vestDate);
    }
    this.vestDate = vestDate;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  public String getVehicleContact() {

    return vehicleContact;
  }

  public void setVehicleContact(String vehicleContact) {
    this.vehicleContact = vehicleContact;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public CustomerDTO getCustomerDTO() {
    return customerDTO;
  }

  public void setCustomerDTO(CustomerDTO customerDTO) {
    this.customerDTO = customerDTO;
  }

  public SupplierDTO getSupplierDTO() {
    return supplierDTO;
  }

  public void setSupplierDTO(SupplierDTO supplierDTO) {
    this.supplierDTO = supplierDTO;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if (id != null) {
      this.idStr = String.valueOf(id);
    }
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getServiceWorkerStr() {
    return serviceWorkerStr;
  }

  public void setServiceWorkerStr(String serviceWorkerStr) {
    this.serviceWorkerStr = serviceWorkerStr;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getOrderIdStr() {
    return orderIdStr;
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }

  public void setOrderId(Long orderId) {
    if (orderId != null) {
      orderIdStr = String.valueOf(orderId);
    }
    this.orderId = orderId;
  }

  public void setOrderType(OrderTypes orderType) {
    //设置超链接字符串
    String urlStr = "#";
    if (orderType != null && getOrderId() != null) {
      if (orderType == OrderTypes.REPAIR) {
        urlStr = "txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + this.getOrderId().toString();
      } else if (orderType == OrderTypes.SALE) {
        urlStr = "sale.do?method=toSalesOrder&menu-uid=WEB.TXN.SALE_MANAGE.SALE&salesOrderId=" + this.getOrderId().toString();
      } else if (orderType == OrderTypes.PURCHASE) {
//           urlStr="storage.do?method=getProducts&type=txn&supplierId=" + this.getCustomerOrSupplierId().toString()
//               + "&purchaseOrderId="+this.getOrderId();
        if(getOrderStatus() == OrderStatus.SELLER_PENDING){
          urlStr = "sale.do?method=toOnlinePendingPurchaseOrder&purchaseOrderId=" + getOrderId().toString();
        }else{
          urlStr = "RFbuy.do?method=show&id=" + this.getOrderId().toString();
        }

      } else if (orderType == OrderTypes.INVENTORY) {
        urlStr = "storage.do?method=getPurchaseInventory&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE&purchaseInventoryId=" + this.getOrderId().toString()
               + "&type=txn&menu-uid=WEB.TXN.PURCHASE_MANAGE.STORAGE";
      } else if (orderType == OrderTypes.WASH_BEAUTY) {
        urlStr = "washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" + this.getOrderId().toString();
      } else if(orderType == OrderTypes.RETURN){
        urlStr = "goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=" + this.getOrderId().toString();
        }else if(orderType == OrderTypes.SALE_RETURN){
        urlStr = "salesReturn.do?method=showSalesReturnOrderBySalesReturnOrderId&salesReturnOrderId=" + this.getOrderId().toString();
        }
    } else {
      urlStr = "#";
    }
    this.setUrl(urlStr);
    this.orderType = orderType;

  }

  public void setOrderContent(String orderContent) {
    this.orderContent = orderContent;
  }

  public void setOrderTotalAmount(Double orderTotalAmount) {
    this.orderTotalAmount = orderTotalAmount;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    if (orderStatus == null) return;
    this.orderStatusValue = orderStatus.getName();
    this.orderStatus = orderStatus;
  }

  public String getOrderStatusValue() {
    return orderStatusValue;
  }

  public void setOrderStatusValue(String orderStatusValue) {
    this.orderStatusValue = orderStatusValue;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public void setCustomerOrSupplierId(Long customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  public void setArrears(Double arrears) {
    this.arrears = arrears;
  }

  public void setPaymentTime(Long paymentTime) {
    String paymentTimeStr = "";
    if (paymentTime != null) {
      if (paymentTime > 0) {
         this.paymentTime = paymentTime;
         Date createTimeDate = new Date(paymentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
         paymentTimeStr = sdf.format(createTimeDate);
         this.setPaymentTimeStr(paymentTimeStr);

      } else {
         this.setPaymentTimeStr("");
       }
    } else {
      this.setPaymentTimeStr("");
    }
  }

  public void setPaymentTimeStr(String paymentTimeStr) {
    this.paymentTimeStr = paymentTimeStr;
  }

  public void setContactNum(String contactNum) {
    this.contactNum = contactNum;
  }

  public void setServiceWorker(String serviceWorker) {
    if (!StringUtil.isEmpty(serviceWorker)) {
      this.serviceWorker = serviceWorker;
      String assistantStr = serviceWorker;
      if (assistantStr.length() > 4) {
        assistantStr = assistantStr.substring(0, 4);
        assistantStr = assistantStr + "...";
        this.setServiceWorkerStr(assistantStr);
      } else {
        this.setServiceWorkerStr(assistantStr);
      }
    } else {
      this.serviceWorker = "";
      this.setServiceWorkerStr("");
    }
  }


  public Long getShopId() {
    return shopId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public String getOrderContent() {
    return orderContent;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public Double getOrderTotalAmount() {
    return orderTotalAmount;
  }

  public String getVehicle() {
    return vehicle;
  }

  public Long getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public Double getArrears() {
    return arrears;
  }

  public Long getPaymentTime() {
    return paymentTime;
  }

  public String getPaymentTimeStr() {
    return paymentTimeStr;
  }

  public String getContactNum() {
    return contactNum;
  }

  public String getServiceWorker() {
    return serviceWorker;
  }


  public void setServiceStr(String serviceStr) {

    if (!StringUtil.isEmpty(serviceStr)) {
      this.serviceStr = serviceStr;
      if (serviceStr.length() > 10) {
        String str = serviceStr.substring(0, 10);
        str = str + "...";
         this.setService(str);
      } else {
        this.setService(serviceStr);
      }
    } else {
      this.serviceStr = "";
      this.setService("");
    }
  }

  public void setWashingStr(String washingStr) {
    if (!StringUtil.isEmpty(washingStr)) {
      this.washingStr = washingStr;
      if (washingStr.length() > 10) {
        String str = washingStr.substring(0, 10);
        str = str + "...";
         this.setWashing(str);
      } else {
        this.setWashing(washingStr);
      }
    } else {
      this.washingStr = "";
      this.setWashing("");
    }
  }

  public void setSalesStr(String salesStr) {
    if (!StringUtil.isEmpty(salesStr)) {
      this.salesStr = salesStr;
      if (salesStr.length() > 10) {
        String str = salesStr.substring(0, 10);
        str = str + "...";
         this.setSales(str);
      } else {
        this.setSales(salesStr);
      }
    } else {
      this.salesStr = "";
      this.setSales("");
    }
  }

  public String getWashingStr() {
    return washingStr;
  }

  public String getSalesStr() {
    return salesStr;
  }

  public String getServiceStr() {
    return serviceStr;
  }

  public long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(long creationDate) {
    this.createdDateStr = DateUtil.convertDateLongToString(creationDate);
    this.creationDate = creationDate;
  }

  public String getCreatedDateStr() {
    return createdDateStr;
  }

  public void setCreatedDateStr(String createdDateStr) {
    this.createdDateStr = createdDateStr;
  }

  public String getSales() {
    return sales;
  }

  public String getService() {
    return service;
  }

  public String getWashing() {
    return washing;
  }


  public void setWashing(String washing) {
    this.washing = washing;
  }

  public void setSales(String sales) {
    this.sales = sales;
  }

  public void setService(String service) {
    this.service = service;
  }

  public Double getWorth() {
    return worth;
  }

  public void setWorth(Double worth) {
    this.worth = worth;
  }

  public Double getMemberBalance() {
    return memberBalance;
  }

  public void setMemberBalance(Double memberBalance) {
    this.memberBalance = memberBalance;
  }

  public List<ItemIndexDTO> getItemIndexDTOList() {
    return itemIndexDTOList;
  }

  public void setItemIndexDTOList(List<ItemIndexDTO> itemIndexDTOList) {
    this.itemIndexDTOList = itemIndexDTOList;
  }

  public String getSalesMans() {
    return salesMans;
  }

  public void setSalesMans(String salesMans) {
    this.salesMans = salesMans;
  }

  public Set<String> getOperators() {
    return operators;
  }

  public void setOperators(Set<String> operators) {
    this.operators = operators;
  }

  public List<PayMethod> getPayMethods() {
    return payMethods;
  }

  public void setPayMethods(List<PayMethod> payMethods) {
    this.payMethods = payMethods;
  }

  public SalesOrderDTO getSalesOrderDTO() {
    return salesOrderDTO;
  }

  public void setSalesOrderDTO(SalesOrderDTO salesOrderDTO) {
    this.salesOrderDTO = salesOrderDTO;
  }

  public PurchaseReturnDTO getPurchaseReturnDTO() {
    return purchaseReturnDTO;
  }

  public void setPurchaseReturnDTO(PurchaseReturnDTO purchaseReturnDTO) {
    this.purchaseReturnDTO = purchaseReturnDTO;
  }

  public MemberCardOrderDTO getMemberCardOrderDTO() {
    return memberCardOrderDTO;
  }

  public void setMemberCardOrderDTO(MemberCardOrderDTO memberCardOrderDTO) {
    this.memberCardOrderDTO = memberCardOrderDTO;
  }

  public PurchaseOrderDTO getPurchaseOrderDTO() {
    return purchaseOrderDTO;
  }

  public void setPurchaseOrderDTO(PurchaseOrderDTO purchaseOrderDTO) {
    this.purchaseOrderDTO = purchaseOrderDTO;
  }

  public PurchaseInventoryDTO getPurchaseInventoryDTO() {
    return purchaseInventoryDTO;
  }

  public void setPurchaseInventoryDTO(PurchaseInventoryDTO purchaseInventoryDTO) {
    this.purchaseInventoryDTO = purchaseInventoryDTO;
  }

  public DebtDTO getDebtDTO() {
    return debtDTO;
  }

  public void setDebtDTO(DebtDTO debtDTO) {
    this.debtDTO = debtDTO;
  }

  public ReceivableDTO getReceivableDTO() {
    return receivableDTO;
  }

  public void setReceivableDTO(ReceivableDTO receivableDTO) {
    this.receivableDTO = receivableDTO;
  }

  public RepairOrderDTO getRepairOrderDTO() {
    return repairOrderDTO;
  }

  public void setRepairOrderDTO(RepairOrderDTO repairOrderDTO) {
    this.repairOrderDTO = repairOrderDTO;

  }

  public WashBeautyOrderDTO getWashBeautyOrderDTO() {
    return washBeautyOrderDTO;
  }

  public void setWashBeautyOrderDTO(WashBeautyOrderDTO washBeautyOrderDTO) {
    this.washBeautyOrderDTO = washBeautyOrderDTO;
  }

  public MemberCardReturnDTO getMemberCardReturnDTO() {
    return memberCardReturnDTO;
  }

  public void setMemberCardReturnDTO(MemberCardReturnDTO memberCardReturnDTO) {
    this.memberCardReturnDTO = memberCardReturnDTO;
  }

  public boolean hasArrears() {
    if (this.arrears != null && this.arrears > 0) {
      return true;
    }
    return false;
  }

  //根据单据类型判断是供应商 还是 供应商
  public boolean hasCustomer() {
    return this.orderType != null && !"INVENTORY,PURCHASE,RETURN".contains(this.orderType.name());
  }


  public boolean hasMember() {
    return this.orderType != null && "MEMBER_BUY_CARD,WASH_BEAUTY_TICKET,SALE,REPAIR".contains(this.orderType.name());
  }

  public boolean hasServiceWorkers() {
    return this.orderType != null && "WASH_BEAUTY_TICKET,REPAIR".contains(this.orderType.name());
  }

  public boolean hasVehicle() {
    return this.orderType != null && ("WASH_BEAUTY,REPAIR".contains(this.orderType.name()));
  }

  public boolean isWashBeautyOrder() {
    return this.orderType != null && ("WASH_BEAUTY".equals(this.orderType.name()));
  }

  public boolean isRepairOrder() {
    return this.orderType != null && ("REPAIR".equals(this.orderType.name()));
  }

  public boolean isPurchase() {
    return this.orderType != null && ("PURCHASE".equals(this.orderType.name()));
  }

  public boolean isInventory() {
    return this.orderType != null && ("INVENTORY".equals(this.orderType.name()));
  }

  public boolean isSale() {
    return this.orderType != null && ("SALE".equals(this.orderType.name()));
  }

  public boolean isReturn() {
    return this.orderType != null ? ("RETURN".equals(this.orderType.name())) : false;
  }

  public boolean isMember() {
    return this.orderType != null ? ("MEMBER_BUY_CARD".equals(this.orderType.name())) : false;
  }

  public boolean isMemberReturn(){
    return this.orderType != null ? ("MEMBER_RETURN_CARD".equals(this.orderType.name())) : false;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public void setMemberStatus(String memberStatus) {
    this.memberStatus = memberStatus;
  }

  public String getMemberStatus() {
    return memberStatus;
  }

  public CustomerStatus getCustomerStatus() {
    return customerStatus;
  }

  public void setCustomerStatus(CustomerStatus customerStatus) {
    this.customerStatus = customerStatus;
  }

  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }

  public Double getMemberDiscountRatio() {
    return memberDiscountRatio;
  }

  public void setMemberDiscountRatio(Double memberDiscountRatio) {
    this.memberDiscountRatio = memberDiscountRatio;
  }

  public Double getAfterMemberDiscountTotal() {
    return afterMemberDiscountTotal;
  }

  public void setAfterMemberDiscountTotal(Double afterMemberDiscountTotal) {
    this.afterMemberDiscountTotal = afterMemberDiscountTotal;
  }

  public String getProductNames() {
    return productNames;
  }

  public void setProductNames(String productNames) {
    this.productNames = productNames;
  }

  public Long getCustomerOrSupplierShopId() {
    return customerOrSupplierShopId;
  }

  public void setCustomerOrSupplierShopId(Long customerOrSupplierShopId) {
    this.customerOrSupplierShopId = customerOrSupplierShopId;
  }

  public List<ItemIndexDTO> getInOutRecordDTOList() {
    return inOutRecordDTOList;
  }

  public void setInOutRecordDTOList(List<ItemIndexDTO> inOutRecordDTOList) {
    this.inOutRecordDTOList = inOutRecordDTOList;
  }

  public String getShopAreaInfo() {
    return shopAreaInfo;
  }

  public void setShopAreaInfo(String shopAreaInfo) {
    this.shopAreaInfo = shopAreaInfo;
  }

  public List<Long> getShopAreaIdList() {
    return shopAreaIdList;
  }

  public void setShopAreaIdList(List<Long> shopAreaIdList) {
    this.shopAreaIdList = shopAreaIdList;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public Set<String> getCouponTypes() {
    return couponTypes;
  }

  public void setCouponTypes(Set<String> couponTypes) {
    this.couponTypes = couponTypes;
  }

  public String getVehicleColor() {
    return vehicleColor;
  }

  public void setVehicleColor(String vehicleColor) {
    this.vehicleColor = vehicleColor;
  }

  public Set<Long> getOperatorIds() {
    return operatorIds;
  }

  public void setOperatorIds(Set<Long> operatorIds) {
    this.operatorIds = operatorIds;
  }

  public void setShopInfo(ShopDTO shopDTO){
    if(shopDTO.getId().equals(this.getShopId())){
      this.setShopName(shopDTO.getName());
      this.setShopKind(shopDTO.getShopKind()==null?"":shopDTO.getShopKind().toString());
      this.setShopAreaInfo(shopDTO.getAreaName());
      List<Long> areaIdList =new ArrayList<Long>();
      if(shopDTO.getProvince()!=null){
        areaIdList.add(shopDTO.getProvince());
      }
      if(shopDTO.getCity()!=null){
        areaIdList.add(shopDTO.getCity());
      }
      if(shopDTO.getRegion()!=null){
        areaIdList.add(shopDTO.getRegion());
      }
      this.setShopAreaIdList(areaIdList);
    }else{
      LOG.error("shopId no equals,order id:"+this.getOrderId()+",shopDTO id:"+shopDTO.getId()+",order shopId:"+shopId);
    }
  }
}
