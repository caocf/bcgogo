package com.bcgogo.search.dto;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.SolrIdPrefix;
import com.bcgogo.enums.notification.ContactGroupType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.PinyinUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-8-28
 * Time: 下午9:50
 * To change this template use File | Settings | File Templates.
 */
public class CustomerSupplierSolrIndexDTO {
  private static final Logger LOG = LoggerFactory.getLogger(CustomerSupplierSolrIndexDTO.class);
  private double balance;
  private Long id;
  private Long shop_id;
  private String shop_kind;
  private Long created_time;
  private String doc_type;
  private String customer_or_supplier;
  private String customer_or_supplier_shop_id;
  private String land_line;
  private String address;
  private String name;
  private String name_fl;
  private String name_py;
  private String name_fl_sort;
  private List<ContactDTO> contactDTOList;
  private List<String> license_nos;
  private List<String> vehicle_brands;
  private List<String> vehicle_models;
  private List<String> vehicle_colors;
  private List<String> vehicle_details;
  private String member_no;  //会员号  (客户)
  private String member_type;  //会员类型(客户)
  private Double total_amount;
  private Long last_expense_time;  //最后消费时间(客户)
  private Long last_inventory_time;  //最后消费时间(供应商)
  private Double total_debt;  //欠款总数
  private Double total_deposit; //定金
  private CustomerStatus status;
  private Double total_return_debt; //店铺欠别人的钱
  private RelationTypes relationType; //客户或者供应商关联关系

  private Double total_trade_amount;//供应商累计交易金额
  private Double total_return_amount;//供应商累计退货金额
  private String business_scope;//经营范围
  private List<Long> area_ids;// 客户供应商 所属地区
  private String area_info;//所属地区
  private List<VehicleDTO> vehicles;
  private Long dual_identity_id;//如果 既是客户又是供应商的时候 这个字段 记录对应的id
  private boolean is_obd;   //是否是obd用户
  private boolean appUser; //是否是app用户

  public CustomerSupplierSolrIndexDTO() {
  }

  public CustomerSupplierSolrIndexDTO(CustomerDTO customerDTO,String doc_type) {
    this.doc_type = doc_type;
    this.id = customerDTO.getId();
    this.shop_id = customerDTO.getShopId();
    this.customer_or_supplier = "customer";
    this.customer_or_supplier_shop_id = customerDTO.getCustomerShopId()==null?"":customerDTO.getCustomerShopId().toString();
    this.name = customerDTO.getName();
    this.address = customerDTO.getAddress();
    this.contactDTOList = customerDTO.getContactDTOList();
    this.created_time = customerDTO.getCreationDate();
    if (CollectionUtils.isNotEmpty(customerDTO.getVehicleDTOList())) {
      license_nos = new ArrayList<String>();
      vehicle_brands = new ArrayList<String>();
      vehicle_models = new ArrayList<String>();
      vehicle_colors = new ArrayList<String>();
      vehicle_details = new ArrayList<String>();
      for(VehicleDTO vehicleDTO:customerDTO.getVehicleDTOList()){
        StringBuffer sb = new StringBuffer();
        if(StringUtils.isNotBlank(vehicleDTO.getLicenceNo())){
          license_nos.add(vehicleDTO.getLicenceNo());
          sb.append(vehicleDTO.getLicenceNo());
        }
        if(StringUtils.isNotBlank(vehicleDTO.getBrand())){
          vehicle_brands.add(vehicleDTO.getBrand());
          sb.append(" ").append(vehicleDTO.getBrand());
        }
        if(StringUtils.isNotBlank(vehicleDTO.getModel())){
          vehicle_models.add(vehicleDTO.getModel());
          sb.append(" ").append(vehicleDTO.getModel());
        }
        if(StringUtils.isNotBlank(vehicleDTO.getColor())){
          vehicle_colors.add(vehicleDTO.getColor());
          sb.append(" ").append(vehicleDTO.getColor());
        }
        vehicle_details.add(sb.toString().trim());
      }
    }
    this.total_amount = customerDTO.getTotalAmount();
    this.total_debt = customerDTO.getTotalReceivable();
    this.last_expense_time = customerDTO.getLastExpenseTime();
    this.total_return_debt = customerDTO.getTotalReturnDebt();

    if (customerDTO.getMemberDTO() != null && customerDTO.getMemberDTO().getStatus() != MemberStatus.DISABLED) {
      this.member_no = customerDTO.getMemberDTO().getMemberNo();
      this.member_type = customerDTO.getMemberDTO().getType();
      this.balance = NumberUtil.doubleVal(customerDTO.getMemberDTO().getBalance());
    } else {
      this.member_type = "非会员";
    }
    this.status = customerDTO.getStatus();
    this.relationType = customerDTO.getRelationType();
    this.vehicles =  customerDTO.getVehicleDTOList();
    List<Long> areaIdList = new ArrayList<Long>();
    if(customerDTO.getProvince()!=null){
      areaIdList.add(customerDTO.getProvince());
    }
    if(customerDTO.getCity()!=null){
      areaIdList.add(customerDTO.getCity());
    }
    if(customerDTO.getRegion()!=null){
      areaIdList.add(customerDTO.getRegion());
    }
    this.setArea_ids(areaIdList);
    this.setArea_info(customerDTO.getAreaInfo());
    this.setDual_identity_id(customerDTO.getSupplierId());
    this.total_deposit = customerDTO.getDeposit();
    this.is_obd = customerDTO.getIsObd();
    this.setAppUser(customerDTO.isAppUser());
  }

  public CustomerSupplierSolrIndexDTO(SupplierDTO supplierDTO,String doc_type) {
    this.doc_type = doc_type;
    this.id = supplierDTO.getId();
    this.shop_id = supplierDTO.getShopId();
    this.land_line = supplierDTO.getLandLine();
    this.customer_or_supplier = "supplier";
    this.customer_or_supplier_shop_id = supplierDTO.getSupplierShopId()==null?"":supplierDTO.getSupplierShopId().toString();
    this.name = supplierDTO.getName();
    this.address = supplierDTO.getAddress();
    this.contactDTOList = supplierDTO.getContactDTOList();
    this.business_scope = supplierDTO.getBusinessScope();
    this.created_time = supplierDTO.getCreationDate();
    this.last_inventory_time = supplierDTO.getLastInventoryTime();
    this.total_debt = supplierDTO.getTotalDebt();
    this.total_return_debt = supplierDTO.getTotalReturnDebt();
    this.total_amount = supplierDTO.getTotalInventoryAmount();
    this.total_deposit = supplierDTO.getDeposit();
    this.status = supplierDTO.getStatus();
    this.relationType = supplierDTO.getRelationType();
    this.total_trade_amount = supplierDTO.getTotalTradeAmount();
    this.total_return_amount = supplierDTO.getTotalReturnAmount();
    List<Long> areaIdList = new ArrayList<Long>();
    if(supplierDTO.getProvince()!=null){
      areaIdList.add(supplierDTO.getProvince());
    }
    if(supplierDTO.getCity()!=null){
      areaIdList.add(supplierDTO.getCity());
    }
    if(supplierDTO.getRegion()!=null){
      areaIdList.add(supplierDTO.getRegion());
    }
    this.setArea_ids(areaIdList);
    this.setArea_info(supplierDTO.getAreaInfo());
    this.setDual_identity_id(supplierDTO.getCustomerId());
  }

  public SolrInputDocument toContactSolrDocument(ContactDTO contactDTO,String docType,SolrIdPrefix solrIdPrefix) throws Exception {
    PingyinInfo pingyinInfo = null;
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", solrIdPrefix+"_"+contactDTO.getId());
    doc.addField("doc_type", docType);
    doc.addField("shop_id", this.shop_id);
    doc.addField("customer_or_supplier_id", this.id);

    String name = this.name;
    if (StringUtils.isBlank(name)) {
      name = contactDTO.getName();
    }
    doc.addField("name", name);
    if (StringUtils.isNotBlank(name)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(name);
      this.name_fl = pingyinInfo.firstLetters;
      this.name_py = pingyinInfo.pingyin;
      this.name_fl_sort = pingyinInfo.firstLetter;
      doc.addField("name_fl", name_fl);
      doc.addField("name_py", name_py);
      doc.addField("name_fl_sort", name_fl_sort);
    }

    if(StringUtils.isNotBlank(contactDTO.getName())){
      doc.addField("contact", contactDTO.getName());
      pingyinInfo = PinyinUtil.getPingyinInfo(contactDTO.getName());
      String contact_fl = pingyinInfo.firstLetters;
      String contact_py = pingyinInfo.pingyin;
      doc.addField("contact_fl", contact_fl);
      doc.addField("contact_py", contact_py);
    }
    if (StringUtils.isNotBlank(contactDTO.getMobile())) {
      doc.addField("mobile", contactDTO.getMobile());
    }
    List<ContactGroupType> contactGroupTypeList = new ArrayList<ContactGroupType>();
    if("customer".equals(this.getCustomer_or_supplier())){
      contactGroupTypeList.add(ContactGroupType.CUSTOMER);
    }
    if("supplier".equals(this.getCustomer_or_supplier())){
      contactGroupTypeList.add(ContactGroupType.SUPPLIER);
    }
    if(StringUtils.isNotBlank(this.getMember_no())){
      contactGroupTypeList.add(ContactGroupType.MEMBER);
    }
    if(Boolean.TRUE.equals(contactDTO.getApp())){
      contactGroupTypeList.add(ContactGroupType.APP_CUSTOMER);
    }
    if(CollectionUtils.isNotEmpty(contactGroupTypeList)){
      for(ContactGroupType contactGroupType : contactGroupTypeList){
        doc.addField("contact_group_type", contactGroupType);
      }
    }else{
      doc.addField("contact_group_type", ContactGroupType.OTHERS);
    }
    return doc;
  }

  /**
   * 客户 供应商   名称  联系人  车牌号  用左右模糊  词的意义不大  不做分词 所以对应的拼音也不做分词
   * @return
   * @throws Exception
   */
  public SolrInputDocument toSolrDocument() throws Exception {
    SolrInputDocument doc = null;
    PingyinInfo pingyinInfo = null;
    try {
      doc = new SolrInputDocument();
      if (shop_id == null) throw new Exception("shopId is null");

      doc.addField("id", this.id);
      doc.addField("doc_type", this.doc_type);
      doc.addField("shop_id", this.shop_id);

      if (StringUtils.isNotBlank(land_line)) {
        doc.addField("land_line", this.land_line);
      }
      doc.addField("customer_or_supplier", this.customer_or_supplier);
      doc.addField("customer_or_supplier_shop_id", this.customer_or_supplier_shop_id);

      if (StringUtils.isNotBlank(name)) {
        doc.addField("name", this.name);
      } else {
        doc.addField("name", "未填写");
        LOG.warn("[{}-id:{}] name is null.", this.customer_or_supplier, this.id);
      }
      if (CollectionUtils.isNotEmpty(getContactDTOList())) {
        for(ContactDTO contactDTO:getContactDTOList()){
          if(contactDTO.getId()==null || (StringUtils.isBlank(contactDTO.getName()) && StringUtils.isBlank(contactDTO.getMobile()))) continue;

          doc.addField("contact_id", contactDTO.getId());
          if(StringUtils.isNotBlank(contactDTO.getName())){
            doc.addField("contact", contactDTO.getName());
            pingyinInfo = PinyinUtil.getPingyinInfo(contactDTO.getName());
            String contact_fl = pingyinInfo.firstLetters;
            String contact_py = pingyinInfo.pingyin;
            doc.addField("contact_fl", contact_fl);
            doc.addField("contact_py", contact_py);
          }else{
            doc.addField("contact", StringUtil.SOLR_PLACEHOLDER_STRING);
            doc.addField("contact_fl", StringUtil.SOLR_PLACEHOLDER_STRING);
            doc.addField("contact_py", StringUtil.SOLR_PLACEHOLDER_STRING);
          }
          if (!isContactMobileEmpty()) {
            if (StringUtils.isNotBlank(contactDTO.getMobile())) {
              doc.addField("mobile", contactDTO.getMobile());
            } else {
              doc.addField("mobile", StringUtil.SOLR_PLACEHOLDER_STRING);
            }
          }
        }

      }


      if (StringUtils.isNotBlank(address)) {
        doc.addField("address", this.address);
      }
      if (StringUtils.isNotBlank(business_scope)) {
        doc.addField("business_scope", this.business_scope);
      }
      doc.addField("total_amount", this.total_amount == null ? 0d : this.total_amount);
      if ("customer".equals(customer_or_supplier)) {
        doc.addField("last_expense_time", this.last_expense_time);
        if (StringUtils.isNotBlank(member_no)) {
          doc.addField("member_no", member_no);
          doc.addField("member_no_fl", PinyinUtil.converterToFirstSpell(member_no));
          doc.addField("member_no_py", PinyinUtil.converterToPingyin(member_no));
        }
        doc.addField("member_type", StringUtils.isNotBlank(this.member_type) ? this.member_type : "非会员");
        doc.addField("total_deposit", this.total_deposit);
        doc.addField("total_balance", this.balance);
      } else {
        doc.addField("last_inventory_time", this.last_inventory_time);
        doc.addField("total_deposit", this.total_deposit);
      }
      doc.addField("total_debt", (this.total_debt == null || total_debt < 0) ? 0d : total_debt);
      if (NumberUtil.doubleVal(total_debt) < 0) {
        LOG.warn("id:{},debt is illegal{}", id, total_debt);
      }
      doc.addField("created_time", this.created_time);
      if (!StringUtils.isBlank(name)) {
        pingyinInfo = PinyinUtil.getPingyinInfo(this.name);
        this.name_fl = pingyinInfo.firstLetters;
        this.name_py = pingyinInfo.pingyin;
        this.name_fl_sort = pingyinInfo.firstLetter;
        doc.addField("name_fl", name_fl);
        doc.addField("name_py", name_py);
        doc.addField("name_fl_sort", name_fl_sort);
      }
      if (CollectionUtils.isNotEmpty(license_nos)) {
        for (String license_no : license_nos) {
          if (StringUtils.isNotBlank(license_no)) {
            doc.addField("license_no", license_no);
            doc.addField("license_no_fl", PinyinUtil.converterToFirstSpell(license_no));
            doc.addField("license_no_py", PinyinUtil.converterToPingyin(license_no));
          }
        }
      }
      if (CollectionUtils.isNotEmpty(vehicle_brands)) {
        for (String vehicle_brand : vehicle_brands) {
          if (StringUtils.isNotBlank(vehicle_brand)) {
            doc.addField("vehicle_brand", vehicle_brand);
          }
        }
      }
      if (CollectionUtils.isNotEmpty(vehicle_models)) {
        for (String vehicle_model : vehicle_models) {
          if (StringUtils.isNotBlank(vehicle_model)) {
            doc.addField("vehicle_model", vehicle_model);
          }
        }
      }
      if (CollectionUtils.isNotEmpty(vehicle_colors)) {
        for (String vehicle_color : vehicle_colors) {
          if (StringUtils.isNotBlank(vehicle_color)) {
            doc.addField("vehicle_color", vehicle_color);
          }
        }
      }
      if (CollectionUtils.isNotEmpty(vehicle_details)) {
        for (String vehicle_detail : vehicle_details) {
          if (StringUtils.isNotBlank(vehicle_detail)) {
            doc.addField("vehicle_detail", vehicle_detail);
          }
        }
      }
      if (CollectionUtils.isNotEmpty(getArea_ids())) {
        for (Long area_id : getArea_ids()) {
          if (area_id!=null) {
            doc.addField("area_ids", area_id);
          }
        }
      }
      doc.addField("area_info",this.area_info);

      doc.addField("status", null == this.status ? CustomerStatus.ENABLED : this.status.toString());
      doc.addField("total_return_debt",NumberUtil.doubleVal(total_return_debt));
      doc.addField("total_return_amount", NumberUtil.doubleVal(total_return_amount));
      doc.addField("total_trade_amount", NumberUtil.doubleVal(total_trade_amount));
      doc.addField("relation_type",this.relationType);
      doc.addField("dual_identity_id",this.dual_identity_id);
      doc.addField("is_obd",this.is_obd);
      doc.addField("is_app", this.isAppUser());
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
    return doc;
  }

  public boolean isContactMobileEmpty() {
    if (CollectionUtils.isEmpty(getContactDTOList()))
      return true;
    for (ContactDTO contactDTO : getContactDTOList()) {
      if (StringUtils.isNotBlank(contactDTO.getMobile())) {
        return false;
      }
    }
    return true;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public String getShop_kind() {
    return shop_kind;
  }

  public void setShop_kind(String shop_kind) {
    this.shop_kind = shop_kind;
  }

  public List<ContactDTO> getContactDTOList() {
    return contactDTOList;
  }

  public void setContactDTOList(List<ContactDTO> contactDTOList) {
    this.contactDTOList = contactDTOList;
  }

  public String getArea_info() {
    return area_info;
  }

  public void setArea_info(String area_info) {
    this.area_info = area_info;
  }

  public List<Long> getArea_ids() {
    return area_ids;
  }

  public void setArea_ids(List<Long> area_ids) {
    this.area_ids = area_ids;
  }

  public List<VehicleDTO> getVehicles() {
    return vehicles;
  }

  public void setVehicles(List<VehicleDTO> vehicles) {
    this.vehicles = vehicles;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Long getDual_identity_id() {
    return dual_identity_id;
  }

  public void setDual_identity_id(Long dual_identity_id) {
    this.dual_identity_id = dual_identity_id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCreated_time() {
    return created_time;
  }

  public void setCreated_time(Long created_time) {
    this.created_time = created_time;
  }

  public String getCustomer_or_supplier() {
    return customer_or_supplier;
  }

  public void setCustomer_or_supplier(String customer_or_supplier) {
    this.customer_or_supplier = customer_or_supplier;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName_fl() {
    return name_fl;
  }

  public void setName_fl(String name_fl) {
    this.name_fl = name_fl;
  }

  public String getName_py() {
    return name_py;
  }

  public void setName_py(String name_py) {
    this.name_py = name_py;
  }

  public String getMember_no() {
    return member_no;
  }

  public void setMember_no(String member_no) {
    this.member_no = member_no;
  }

  public String getMember_type() {
    return member_type;
  }

  public void setMember_type(String member_type) {
    this.member_type = member_type;
  }

  public Double getTotal_amount() {
    return total_amount;
  }

  public void setTotal_amount(Double total_amount) {
    this.total_amount = total_amount;
  }

  public Long getLast_expense_time() {
    return last_expense_time;
  }

  public void setLast_expense_time(Long last_expense_time) {
    this.last_expense_time = last_expense_time;
  }

  public Long getLast_inventory_time() {
    return last_inventory_time;
  }

  public void setLast_inventory_time(Long last_inventory_time) {
    this.last_inventory_time = last_inventory_time;
  }

  public Double getTotal_debt() {
    return total_debt;
  }

  public void setTotal_debt(Double total_debt) {
    this.total_debt = total_debt;
  }


  public void setShop_id(Long shop_id) {
    this.shop_id = shop_id;
  }

  public Double getTotal_deposit() {
    return total_deposit;
  }

  public void setTotal_deposit(Double total_deposit) {
    this.total_deposit = total_deposit;
  }

  public String getName_fl_sort() {
    return name_fl_sort;
  }

  public void setName_fl_sort(String name_fl_sort) {
    this.name_fl_sort = name_fl_sort;
  }

  public CustomerStatus getStatus() {
    return status;
  }

  public void setStatus(CustomerStatus status) {
    this.status = status;
  }

  public String getCustomer_or_supplier_shop_id() {
    return customer_or_supplier_shop_id;
  }

  public void setCustomer_or_supplier_shop_id(String customer_or_supplier_shop_id) {
    this.customer_or_supplier_shop_id = customer_or_supplier_shop_id;
  }

  public Double getTotal_return_debt() {
    return total_return_debt;
  }

  public void setTotal_return_debt(Double total_return_debt) {
    this.total_return_debt = total_return_debt;
  }

  public RelationTypes getRelationType() {
    return relationType;
  }

  public void setRelationType(RelationTypes relationType) {
    this.relationType = relationType;
  }

  public Double getTotal_trade_amount() {
    return total_trade_amount;
  }

  public void setTotal_trade_amount(Double total_trade_amount) {
    this.total_trade_amount = total_trade_amount;
  }

  public Double getTotal_return_amount() {
    return total_return_amount;
  }

  public void setTotal_return_amount(Double total_return_amount) {
    this.total_return_amount = total_return_amount;
  }

  public String getBusiness_scope() {
    return business_scope;
  }

  public void setBusiness_scope(String business_scope) {
    this.business_scope = business_scope;
  }

  public String getLand_line() {
    return land_line;
  }

  public void setLand_line(String land_line) {
    this.land_line = land_line;
  }

  public boolean isIs_obd() {
    return is_obd;
  }

  public void setIs_obd(boolean is_obd) {
    this.is_obd = is_obd;
  }

  public boolean isAppUser() {
    return appUser;
  }

  public void setAppUser(boolean appUser) {
    this.appUser = appUser;
  }
}
