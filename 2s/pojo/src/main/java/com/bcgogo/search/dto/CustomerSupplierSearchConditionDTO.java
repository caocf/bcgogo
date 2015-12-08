package com.bcgogo.search.dto;

import com.bcgogo.common.TwoTuple;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.notification.ContactGroupType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 12-5-11
 * Time: 上午9:15
 * 搜索条件DTO
 */
public class CustomerSupplierSearchConditionDTO {
  public static final String INFO = "info";
  public static final String MULTI_FIELD = "multiField";
  public static final String MULTI_FIELD_TO_SINGLE = "multiFieldToSingle";
  public static final String NAME = "name";
  public static final String MEMBER_NO = "member_no";
  public static final String CONTACT = "contact";
  public static final String MOBILE = "mobile";
  public static final String CUSTOMER = "customer";
  public static final String SUPPLIER = "supplier";

  public enum SearchStrategy {
    customerOrSupplierShopIdNotEmpty,
    mobileNotEmpty,
  }
  public enum SearchFieldStrategy {
    searchIncludeLicenseNo,
    searchIncludeMemberNo
  }
  private String ids;
  private String searchWord;
  private String searchField;
  private String term;//jquery autocomplete的searchWord；
  private Long shopId;
  private ShopKind shopKind;
  private Long wholesalerShopId;//批发商shopId
  private String name;
  private String contact;
  private String mobile;
  private String customerOrSupplier;
  private Long customerOrSupplierId;
  private Boolean hasDebt;
  private Double totalDebtUp;//累计应付上线
  private Double totalDebtDown; //累计应付下限
  private Boolean hasReturnDebt;
  private Boolean hasBalance;
  private Boolean hasTotalConsumption;
  private List<Long> relatedCustomerOrSupplierShopIds = new ArrayList<Long>();
  // customer
  private String lastExpenseTime;
  private String lastExpenseTimeStart;
  private String lastExpenseTimeEnd;
  private String memberNo;
  private String memberType;
  private String licenseNo;   //车牌号
  private Double totalAmount;
  private Double totalAmountUp;
  private Double totalAmountDown;
  private Double totalTradeAmountUp;//累计交易金额
  private Double totalTradeAmountDown;

  private String[] memberCardTypes;


  private Double totalReceivableUp;//累计应收上线
  private Double totalReceivableDown; //累计应收下限

  private Double totalPayableUp;   //累计应付
  private Double totalPayableDown;//累计应付

  //supplier
  private String lastInventoryTime;
  private String lastInventoryTimeStart;
  private String lastInventoryTimeEnd;

  //区域
  private Long province;//省
  private Long city; //市
  private Long region; //区

  //车辆信息
  private String vehicleModel;//型号
  private String vehicleBrand; //品牌
  private String vehicleColor; //颜色

  //定金
  private Boolean hasDeposit;//是否有定金

  private SearchStrategy[] searchStrategies; //查找策略
  private SearchFieldStrategy[] searchFieldStrategies; //查找策略
  private String sort;                    //排序规则
  private int start;
  private int rows = 15;//默认15
  //ajaxPaging.tag 对应的接口
  private int startPageNo = 1;
  private int maxRows = 15;//默认15
  private RelationTypes relationType;
  private Boolean isTodayAdd = false; //今日新增车辆的客户
  private TwoTuple<Integer,Set<Long>> toDayAddCustomer;
  private JoinSearchConditionDTO joinSearchConditionDTO;
  //通过比较uuid 来保证 商品建议和商品历史建议 的请求一致性
  private String uuid;

  private String filterType;
  private ContactGroupType contactGroupType;

  private int totalExportNum;


  public SearchFieldStrategy[] getSearchFieldStrategies() {
    return searchFieldStrategies;
  }

  public void setSearchFieldStrategies(SearchFieldStrategy[] searchFieldStrategies) {
    this.searchFieldStrategies = searchFieldStrategies;
  }

  public SearchStrategy[] getSearchStrategies() {
    return searchStrategies;
  }

  public void setSearchStrategies(SearchStrategy[] searchStrategies) {
    this.searchStrategies = searchStrategies;
  }

  public ContactGroupType getContactGroupType() {
    return contactGroupType;
  }

  public void setContactGroupType(ContactGroupType contactGroupType) {
    this.contactGroupType = contactGroupType;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getVehicleColor() {
    return vehicleColor;
  }

  public void setVehicleColor(String vehicleColor) {
    this.vehicleColor = vehicleColor;
  }

  public int getTotalExportNum() {
    return totalExportNum;
  }

  public void setTotalExportNum(int totalExportNum) {
    this.totalExportNum = totalExportNum;
  }

  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  public Double getTotalTradeAmountUp() {
    return totalTradeAmountUp;
  }

  public void setTotalTradeAmountUp(Double totalTradeAmountUp) {
    this.totalTradeAmountUp = totalTradeAmountUp;
  }

  public Double getTotalTradeAmountDown() {
    return totalTradeAmountDown;
  }

  public void setTotalTradeAmountDown(Double totalTradeAmountDown) {
    this.totalTradeAmountDown = totalTradeAmountDown;
  }

  public String getFilterType() {
    return filterType;
  }

  public void setFilterType(String filterType) {
    this.filterType = filterType;
  }

  public boolean isEmptyOfSuggestionCustomerInfo() {
    return StringUtil.isAllEmpty(searchWord, name, contact, mobile, memberNo, licenseNo);
  }

  public boolean isEmptyOfSuggestionSupplierInfo() {
    return StringUtil.isAllEmpty(searchWord, name, contact, mobile);
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getLicenseNo() {
    return licenseNo;
  }

  public void setLicenseNo(String licenseNo) {
    this.licenseNo = licenseNo;
  }

  public String getTerm() {
    return term;
  }

  public void setTerm(String term) {
    this.term = term;
    this.setSearchWord(term);
  }

  public String getSearchWord() {
    return searchWord;
  }

  public void setSearchWord(String searchWord) {
    this.searchWord = searchWord;
  }

  public String getSearchField() {
    return searchField;
  }

  public void setSearchField(String searchField) {
    this.searchField = searchField;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getWholesalerShopId() {
    return wholesalerShopId;
  }

  public void setWholesalerShopId(Long wholesalerShopId) {
    this.wholesalerShopId = wholesalerShopId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCustomerOrSupplier() {
    return customerOrSupplier;
  }

  public void setCustomerOrSupplier(String customerOrSupplier) {
    this.customerOrSupplier = customerOrSupplier;
  }

  public Boolean isHasDept() {
    return hasDebt;
  }

  public void setHasDebt(Boolean hasDebt) {
    this.hasDebt = hasDebt;
  }

  public String getLastExpenseTime() {
    return lastExpenseTime;
  }

  public void setLastExpenseTime(String lastExpenseTime) {
    this.lastExpenseTime = lastExpenseTime;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public String getMemberType() {
    return memberType;
  }

  public void setMemberType(String memberType) {
    this.memberType = memberType;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getLastInventoryTime() {
    return lastInventoryTime;
  }

  public void setLastInventoryTime(String lastInventoryTime) {
    this.lastInventoryTime = lastInventoryTime;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
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

  public Double getTotalDebtUp() {
    return totalDebtUp;
  }

  public void setTotalDebtUp(Double totalDebtUp) {
    this.totalDebtUp = totalDebtUp;
  }

  public Double getTotalDebtDown() {
    return totalDebtDown;
  }

  public void setTotalDebtDown(Double totalDebtDown) {
    this.totalDebtDown = totalDebtDown;
  }

  public String getLastExpenseTimeStart() {
    return lastExpenseTimeStart;
  }

  public void setLastExpenseTimeStart(String lastExpenseTimeStart) {
    this.lastExpenseTimeStart = lastExpenseTimeStart;
  }

  public String getLastExpenseTimeEnd() {
    return lastExpenseTimeEnd;
  }

  public void setLastExpenseTimeEnd(String lastExpenseTimeEnd) {
    this.lastExpenseTimeEnd = lastExpenseTimeEnd;
  }

  public Double getTotalAmountUp() {
    return totalAmountUp;
  }

  public void setTotalAmountUp(Double totalAmountUp) {
    this.totalAmountUp = totalAmountUp;
  }

  public Double getTotalAmountDown() {
    return totalAmountDown;
  }

  public void setTotalAmountDown(Double totalAmountDown) {
    this.totalAmountDown = totalAmountDown;
  }

  public String getLastInventoryTimeStart() {
    return lastInventoryTimeStart;
  }

  public void setLastInventoryTimeStart(String lastInventoryTimeStart) {
    this.lastInventoryTimeStart = lastInventoryTimeStart;
  }

  public String getLastInventoryTimeEnd() {
    return lastInventoryTimeEnd;
  }

  public void setLastInventoryTimeEnd(String lastInventoryTimeEnd) {
    this.lastInventoryTimeEnd = lastInventoryTimeEnd;
  }


  @Override
  public String toString() {
    return "CustomerSupplierSearchConditionDTO{" +
        ", searchWord='" + searchWord + '\'' +
        ", searchField=" + searchField +
        ", shopId=" + shopId +
        ", name=" + name +
        ", contact=" + contact +
        ", mobile=" + mobile +
        ", customerOrSupplier=" + customerOrSupplier +
        ", totalDebt=" + hasDebt +
        ", lastExpenseTime=" + lastExpenseTime +
        ", memberNo=" + memberNo +
        ", memberType=" + memberType +
        ", totalAmount=" + totalAmount +
        ", lastInventoryTime=" + lastInventoryTime +
        ", sort='" + sort + '\'' +
        ", start=" + start +
        ", rows=" + rows +
        '}';
  }

  public List<Long> getRelatedCustomerOrSupplierShopIds() {
    return relatedCustomerOrSupplierShopIds;
  }

  public void setRelatedCustomerOrSupplierShopIds(List<Long> relatedCustomerOrSupplierShopIds) {
    this.relatedCustomerOrSupplierShopIds = relatedCustomerOrSupplierShopIds;
  }

  public RelationTypes getRelationType() {
    return relationType;
  }

  public void setRelationType(RelationTypes relationType) {
    this.relationType = relationType;
  }

  public String getIds() {
    return ids;
  }

  public void setIds(String ids) {
    this.ids = ids;
  }

  public Long getCustomerOrSupplierId() {
    return customerOrSupplierId;
  }

  public void setCustomerOrSupplierId(Long customerOrSupplierId) {
    this.customerOrSupplierId = customerOrSupplierId;
  }

  public Double getTotalReceivableUp() {
    return totalReceivableUp;
  }

  public void setTotalReceivableUp(Double totalReceivableUp) {
    this.totalReceivableUp = totalReceivableUp;
  }

  public Double getTotalReceivableDown() {
    return totalReceivableDown;
  }

  public void setTotalReceivableDown(Double totalReceivableDown) {
    this.totalReceivableDown = totalReceivableDown;
  }

  public Long getProvince() {
    return province;
  }

  public void setProvince(Long province) {
    this.province = province;
  }

  public Long getCity() {
    return city;
  }

  public void setCity(Long city) {
    this.city = city;
  }

  public Long getRegion() {
    return region;
  }

  public void setRegion(Long region) {
    this.region = region;
  }

  public String[] getMemberCardTypes() {
    return memberCardTypes;
  }

  public void setMemberCardTypes(String[] memberCardTypes) {
    this.memberCardTypes = memberCardTypes;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public Double getTotalPayableUp() {
    return totalPayableUp;
  }

  public void setTotalPayableUp(Double totalPayableUp) {
    this.totalPayableUp = totalPayableUp;
  }

  public Double getTotalPayableDown() {
    return totalPayableDown;
  }

  public void setTotalPayableDown(Double totalPayableDown) {
    this.totalPayableDown = totalPayableDown;
  }

  public Boolean getHasDeposit() {
    return hasDeposit;
  }

  public void setHasDeposit(Boolean hasDeposit) {
    this.hasDeposit = hasDeposit;
  }

  public JoinSearchConditionDTO getJoinSearchConditionDTO() {
    return joinSearchConditionDTO;
  }

  public void setJoinSearchConditionDTO(JoinSearchConditionDTO joinSearchConditionDTO) {
    this.joinSearchConditionDTO = joinSearchConditionDTO;
  }

  public Boolean getHasReturnDebt() {
    return hasReturnDebt;
  }

  public void setHasReturnDebt(Boolean hasReturnDebt) {
    this.hasReturnDebt = hasReturnDebt;
  }

  public Boolean getHasBalance() {
    return hasBalance;
  }

  public void setHasBalance(Boolean hasBalance) {
    this.hasBalance = hasBalance;
  }

  public Boolean getHasTotalConsumption() {
    return hasTotalConsumption;
  }

  public void setHasTotalConsumption(Boolean hasTotalConsumption) {
    this.hasTotalConsumption = hasTotalConsumption;
  }

  public Boolean getTodayAdd() {
    return isTodayAdd;
  }

  public void setTodayAdd(Boolean todayAdd) {
    isTodayAdd = todayAdd;
  }

  public TwoTuple<Integer, Set<Long>> getToDayAddCustomer() {
    return toDayAddCustomer;
  }

  public void setToDayAddCustomer(TwoTuple<Integer, Set<Long>> toDayAddCustomer) {
    this.toDayAddCustomer = toDayAddCustomer;
  }
}
