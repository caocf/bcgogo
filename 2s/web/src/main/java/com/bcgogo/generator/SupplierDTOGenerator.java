package com.bcgogo.generator;

import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 职责：从reques对象中抽取信息，封装成供应商对象
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-17
 * Time: 上午10:28
 * To change this template use File | Settings | File Templates.
 */

@Component
public class SupplierDTOGenerator {

  public SupplierDTO generate(HttpServletRequest request, SupplierDTO supplierDTO) throws GeneratorException {
    if (supplierDTO == null) {
      supplierDTO = new SupplierDTO();
    }
    String idStr = request.getParameter("supplierId");
    Long id = null;
    String[] scope = null;
    if (idStr != null && idStr.length() > 0 && !"0".equals(idStr)) {
      id = Long.parseLong(idStr);
    }
    String shopId = String.valueOf(request.getSession().getAttribute("shopId"));
    String name = request.getParameter("name");                    //单位
    String mobile = request.getParameter("mobile");                //手机
    String landLine = request.getParameter("landLine");           //座机
    String landLineSecond=request.getParameter("landLineSecond");
    String landLineThird=request.getParameter("landLineThird");
    String fax = request.getParameter("fax");                      //传真
    String address = request.getParameter("address");                    //地址
    String qq = request.getParameter("qq");                        //qq
    String email = request.getParameter("email");                  //email
    String bank = request.getParameter("bank");                    //开户行
    String accountName = request.getParameter("accountName");     //开户名
    String account = request.getParameter("account");             //账户
    String category = request.getParameter("category");                  //客户类型
    String abbr = request.getParameter("abbr");                       //简称
    String contact = request.getParameter("contact");                    //联系人

    String thirdCategoryIdStr = request.getParameter("thirdCategoryIdStr"); //经营范围
    String VehicleModelIdStr = request.getParameter("VehicleModelIdStr"); //经营范围
    String selectBrandModel = request.getParameter("selectBrandModel"); //经营范围
    supplierDTO.setThirdCategoryIdStr(thirdCategoryIdStr);
    supplierDTO.setVehicleModelIdStr(VehicleModelIdStr);
    supplierDTO.setSelectBrandModel(StringUtils.isNotBlank(selectBrandModel)?VehicleSelectBrandModel.valueOf(selectBrandModel):null);
    Long customerId = null;
    if (request.getParameter("customerId") != null && !"".equals(request.getParameter("customerId"))) {
      customerId = Long.valueOf(request.getParameter("customerId"));
    }
    //add by zhuj 生成联系人列表 目前最多是3个撒
    // 整个这个类 有时间就干掉吧 真心看不下去。。。 页面转为form的形式。。。
    supplierDTO.setContacts(fillContactArray(request));
    if (id != null && !ArrayUtils.isEmpty(supplierDTO.getContacts())) {// 如果supplierId存在 修改supplier信息
      for (ContactDTO contactDTO: supplierDTO.getContacts()){
        if (contactDTO != null && contactDTO.isValidContact()) {
          contactDTO.setSupplierId(id);
          contactDTO.setCustomerId(supplierDTO.getCustomerId());
        }
      }
    }
    // add end

    String settlementType = request.getParameter("settlementType");     //结算方式
    String invoiceCategory = request.getParameter("invoiceCategory");//发票类型
    String identity = request.getParameter("identity");
    String memo = request.getParameter("memo");

    Long province = null;
    Long city = null;
    Long region = null;
    if (!"".equals(request.getParameter("province")) && null != request.getParameter("province")) {
      province = Long.parseLong(request.getParameter("province"));
    }
    if (!"".equals(request.getParameter("city")) && null != request.getParameter("city")) {
      city = Long.parseLong(request.getParameter("city"));
    }
    if (!"".equals(request.getParameter("region")) && null != request.getParameter("region")) {
      region = Long.parseLong(request.getParameter("region"));
    }
    //是否更新"同时客户"关系.
    String updateDualRoleStr = request.getParameter("updateDualRole");
    boolean updateDualRole = true;
    if(StringUtils.isNotBlank(updateDualRoleStr) && updateDualRoleStr.trim().equals("false")){
      updateDualRole = false;
    }

    //更多供应商信息处理
    if (name == null) {
      name = request.getParameter("supplier");
    }
    if (abbr == null) {
      abbr = request.getParameter("shortName");
    }
    if (email == null) {
      email = request.getParameter("email");
    }

    supplierDTO.setShopId(NumberUtil.longValue(shopId, 0));
    //中文
    try {
      if (id != null) {
        supplierDTO.setId(id);
      }
      if (name != null) {
        supplierDTO.setName(name);
      }
      if (abbr != null) {
        supplierDTO.setAbbr(abbr);
      }
      if (landLine != null) {
        supplierDTO.setLandLine(landLine);
      }
      if(landLineSecond!=null){
        supplierDTO.setLandLineSecond(landLineSecond);
      }
      if(landLineThird!=null){
        supplierDTO.setLandLineThird(landLineThird);
      }
      if (fax != null) {
        supplierDTO.setFax(fax);
      }
      if (address != null) {
        supplierDTO.setAddress(address);
      }
      if (qq != null) {
        supplierDTO.setQq(qq);
      }
      if (email != null) {
        supplierDTO.setEmail(email);
      }
      if (bank != null) {
        supplierDTO.setBank(bank);
      }
      if (accountName != null) {
        supplierDTO.setAccountName(accountName);
      }
      if (account != null) {
        supplierDTO.setAccount(account);
      }

      if ((category != null) && (!category.trim().equals(""))) {
        Long cat = null;
        try {
          cat = Long.parseLong(category);
        } catch (Exception e) {
          throw new GeneratorException(e);
        }
        if (cat != null) {
          supplierDTO.setCategory(cat);
        }
      }
      if (contact != null) {
        supplierDTO.setContact(contact);
      }

      if ((settlementType != null) && (!settlementType.trim().equals(""))) {
        Long settlemetnTypeId = null;
        try {
          settlemetnTypeId = Long.parseLong(settlementType);
        } catch (Exception e) {
          throw new GeneratorException(e);
        }
        if (settlemetnTypeId != null) {
          supplierDTO.setSettlementTypeId(settlemetnTypeId);
        }
      }
      if (mobile != null) {
        supplierDTO.setMobile(mobile);
      }
      if ((invoiceCategory != null) && (!invoiceCategory.trim().equals(""))) {
        Long invoiceCategoryId = null;
        try {
          invoiceCategoryId = Long.parseLong(invoiceCategory);
        } catch (Exception e) {
          throw new GeneratorException(e);
        }
        if (invoiceCategoryId != null) {
          supplierDTO.setInvoiceCategoryId(invoiceCategoryId);
        }
      }

      if (province != null) {
        if(!province.equals(supplierDTO.getProvince())) {
          supplierDTO.setChangeArea(true);
        }
        supplierDTO.setProvince(province);
      }
      if (city != null) {
        if(!city.equals(supplierDTO.getCity())) {
          supplierDTO.setChangeArea(true);
        }
        supplierDTO.setCity(city);
      }
        if(!NumberUtil.isEqual(region,supplierDTO.getRegion())) {
          supplierDTO.setChangeArea(true);
        }
        supplierDTO.setRegion(region);

      if(updateDualRole){
        if (identity == null || "".equals(identity)) {
          supplierDTO.setIdentity(null);
        } else if ("isCustomer".equals(identity)) {
          supplierDTO.setIdentity("isCustomer");
        }
        if (supplierDTO.getId() == null) {
          supplierDTO.setCustomerId(customerId);
        }
      }
      if(StringUtils.isNotBlank(memo)){
        supplierDTO.setMemo(memo);
      }

    } catch (Exception e) {
      throw new GeneratorException(e);
    }
    return supplierDTO;
  }

  public SupplierDTO generateOnlineSupplier(HttpServletRequest request, SupplierDTO supplierDTO) throws GeneratorException {
    if (supplierDTO == null) {
      supplierDTO = new SupplierDTO();
    }
    String idStr = request.getParameter("supplierId");
    Long id = null;
    if (idStr != null && idStr.length() > 0 && !"0".equals(idStr)) {
      id = Long.parseLong(idStr);
    }
    String shopId = String.valueOf(request.getSession().getAttribute("shopId"));
    String bank = request.getParameter("bank");                    //开户行
    String accountName = request.getParameter("accountName");     //开户名
    String account = request.getParameter("account");             //账户
    String category = request.getParameter("category");                  //客户类型
    String abbr = request.getParameter("abbr");                       //简称
    String contact = request.getParameter("contact");                    //联系人
    String fax = request.getParameter("faxStr");                      //传真
    String landline = request.getParameter("landlineStr");           //座机
    Long customerId = null;
    if (request.getParameter("customerId") != null && !"".equals(request.getParameter("customerId"))) {
      customerId = Long.valueOf(request.getParameter("customerId"));
    }
    //add by zhuj 生成联系人列表 目前最多是3个撒
    // 整个这个类 有时间就干掉吧 真心看不下去。。。 页面转为form的形式。。。
    supplierDTO.setContacts(fillContactArray(request));
    if (id != null && !ArrayUtils.isEmpty(supplierDTO.getContacts())) { // 如果supplierId存在 修改supplier信息
      for (ContactDTO contactDTO : supplierDTO.getContacts()) {
        if (contactDTO != null && contactDTO.isValidContact()) {
          contactDTO.setSupplierId(id);
          contactDTO.setCustomerId(supplierDTO.getCustomerId());
        }
      }
    }
    // add end

    String settlementType = request.getParameter("settlementType");     //结算方式
    String invoiceCategory = request.getParameter("invoiceCategory");//发票类型
    String identity = request.getParameter("identity");
    String memo = request.getParameter("memo");
    //是否更新"同时客户"关系.
    String updateDualRoleStr = request.getParameter("updateDualRole");
    boolean updateDualRole = true;
    if (StringUtils.isNotBlank(updateDualRoleStr) && updateDualRoleStr.trim().equals("false")) {
      updateDualRole = false;
    }

    //更多供应商信息处理
    supplierDTO.setShopId(NumberUtil.longValue(shopId, 0));
    //中文
    try {
      if (id != null) {
        supplierDTO.setId(id);
      }
      if (abbr != null) {
        supplierDTO.setAbbr(abbr);
      }
      if (bank != null) {
        supplierDTO.setBank(bank);
      }
      if (accountName != null) {
        supplierDTO.setAccountName(accountName);
      }
      if (account != null) {
        supplierDTO.setAccount(account);
      }
      if(fax != null){
        supplierDTO.setFax(fax);
      }
      if(landline != null){
        supplierDTO.setLandLine(landline);
      }

      if ((category != null) && (!category.trim().equals(""))) {
        Long cat = null;
        try {
          cat = Long.parseLong(category);
        } catch (Exception e) {
          throw new GeneratorException(e);
        }
        if (cat != null) {
          supplierDTO.setCategory(cat);
        }
      }
      if (contact != null) {
        supplierDTO.setContact(contact);
      }

      if ((settlementType != null) && (!settlementType.trim().equals(""))) {
        Long settlemetnTypeId = null;
        try {
          settlemetnTypeId = Long.parseLong(settlementType);
        } catch (Exception e) {
          throw new GeneratorException(e);
        }
        if (settlemetnTypeId != null) {
          supplierDTO.setSettlementTypeId(settlemetnTypeId);
        }
      }
      if ((invoiceCategory != null) && (!invoiceCategory.trim().equals(""))) {
        Long invoiceCategoryId = null;
        try {
          invoiceCategoryId = Long.parseLong(invoiceCategory);
        } catch (Exception e) {
          throw new GeneratorException(e);
        }
        if (invoiceCategoryId != null) {
          supplierDTO.setInvoiceCategoryId(invoiceCategoryId);
        }
      }

      if (updateDualRole) {
        if (identity == null || "".equals(identity)) {
          supplierDTO.setIdentity(null);
        } else if ("isCustomer".equals(identity)) {
          supplierDTO.setIdentity("isCustomer");
        }
        if (supplierDTO.getId() == null) {
          supplierDTO.setCustomerId(customerId);
        }
      }
      if (StringUtils.isNotBlank(memo)) {
        supplierDTO.setMemo(memo);
      }

    } catch (Exception e) {
      throw new GeneratorException(e);
    }
    return supplierDTO;
  }



  /**
   * 从request抓取联系人列表  并且过滤isValidContact
   *
   * @param request
   */
  public static ContactDTO[] fillContactArray(HttpServletRequest request) {
    List<ContactDTO> contactDTOList = new ArrayList<ContactDTO>();
    Long shopId = WebUtil.getShopId(request);
    String pageType = request.getParameter("pageType");
    if (StringUtils.isNotBlank(pageType)) {
      if (StringUtils.indexOf(pageType, "Add") > 0) {
        // 第一个联系人
        ContactDTO contactDTO1 = new ContactDTO();
        contactDTO1.setShopId(shopId);
        contactDTO1.setDisabled(1);
        String id0 = request.getParameter("contacts3[0].id");
        contactDTO1.setIdStr(id0);
        if (StringUtils.isNotBlank(id0)) {
          contactDTO1.setId(Long.parseLong(id0));
        }
        contactDTO1.setEmail(request.getParameter("contacts3[0].email"));
        contactDTO1.setMobile(request.getParameter("contacts3[0].mobile"));
        contactDTO1.setQq(request.getParameter("contacts3[0].qq"));
        String contactDTO1MainContact = request.getParameter("contacts3[0].mainContact");
        if (StringUtils.isNotBlank(contactDTO1MainContact)) {
          contactDTO1.setIsMainContact(Integer.parseInt(contactDTO1MainContact));
        }
        String contacts1Level = request.getParameter("contacts3[0].level");
        contactDTO1.setLevel(NumberUtil.intValue(contacts1Level,0));
        contactDTO1.setName(request.getParameter("contacts3[0].name"));

        contactDTOList.add(contactDTO1);

        // 第二个联系人
        ContactDTO contactDTO2 = new ContactDTO();
        contactDTO2.setShopId(shopId);
        contactDTO2.setDisabled(1);
        String id1 = request.getParameter("contacts3[1].id");
        contactDTO2.setIdStr(id1);
        if (StringUtils.isNotBlank(id1)) {
          contactDTO2.setId(Long.parseLong(id1));
        }
        contactDTO2.setEmail(request.getParameter("contacts3[1].email"));
        contactDTO2.setMobile(request.getParameter("contacts3[1].mobile"));
        contactDTO2.setQq(request.getParameter("contacts3[1].qq"));
        String contactDTO2MainContact = request.getParameter("contacts3[1].mainContact");
        if (StringUtils.isNotBlank(contactDTO2MainContact)) {
          contactDTO2.setIsMainContact(Integer.parseInt(contactDTO2MainContact));
        }
        String contacts2Level = request.getParameter("contacts3[1].level");
        contactDTO2.setLevel(NumberUtil.intValue(contacts2Level,1));
        contactDTO2.setName(request.getParameter("contacts3[1].name"));

        contactDTOList.add(contactDTO2);

        // 第三个联系人
        ContactDTO contactDTO3 = new ContactDTO();
        contactDTO3.setShopId(shopId);
        contactDTO3.setDisabled(1);
        String id2 = request.getParameter("contacts3[2].id");
        contactDTO3.setIdStr(id2);
        if (StringUtils.isNotBlank(id2)) {
          contactDTO3.setId(Long.parseLong(id2));
        }
        contactDTO3.setEmail(request.getParameter("contacts3[2].email"));
        contactDTO3.setMobile(request.getParameter("contacts3[2].mobile"));
        contactDTO3.setQq(request.getParameter("contacts3[2].qq"));
        String contactDTO3MainContact = request.getParameter("contacts3[2].mainContact");
        if (StringUtils.isNotBlank(contactDTO3MainContact)) {
          contactDTO3.setIsMainContact(Integer.parseInt(contactDTO3MainContact));
        }
        String contacts3Level = request.getParameter("contacts3[2].level");
        contactDTO3.setLevel(NumberUtil.intValue(contacts3Level,2));
        contactDTO3.setName(request.getParameter("contacts3[2].name"));

        contactDTOList.add(contactDTO3);
      } else {
        contactDTOList.addAll(buildNormalKeys(request, "contacts2"));
      }
    } else {
      // 第一个联系人
      ContactDTO contactDTO1 = new ContactDTO();
      contactDTO1.setShopId(shopId);
      contactDTO1.setDisabled(1);
      String id0 = request.getParameter("contacts[0].id");
      contactDTO1.setIdStr(id0);
      if (StringUtils.isNotBlank(id0)) {
        contactDTO1.setId(Long.parseLong(id0));
      }
      contactDTO1.setEmail(request.getParameter("contacts[0].email"));
      contactDTO1.setMobile(request.getParameter("contacts[0].mobile"));
      contactDTO1.setQq(request.getParameter("contacts[0].qq"));
      String contactDTO1MainContact = request.getParameter("contacts[0].mainContact");
      if (StringUtils.isNotBlank(contactDTO1MainContact)) {
        contactDTO1.setIsMainContact(Integer.parseInt(contactDTO1MainContact));
      }
      String contacts1Level = request.getParameter("contacts[0].level");
      contactDTO1.setLevel(NumberUtil.intValue(contacts1Level,0));
      contactDTO1.setName(request.getParameter("contacts[0].name"));

      contactDTOList.add(contactDTO1);

      // 第二个联系人
      ContactDTO contactDTO2 = new ContactDTO();
      contactDTO2.setShopId(shopId);
      contactDTO2.setDisabled(1);
      String id1 = request.getParameter("contacts[1].id");
      contactDTO2.setIdStr(id1);
      if (StringUtils.isNotBlank(id1)) {
        contactDTO2.setId(Long.parseLong(id1));
      }
      contactDTO2.setEmail(request.getParameter("contacts[1].email"));
      contactDTO2.setMobile(request.getParameter("contacts[1].mobile"));
      contactDTO2.setQq(request.getParameter("contacts[1].qq"));
      String contactDTO2MainContact = request.getParameter("contacts[1].mainContact");
      if (StringUtils.isNotBlank(contactDTO2MainContact)) {
        contactDTO2.setIsMainContact(Integer.parseInt(contactDTO2MainContact));
      }
      String contacts2Level = request.getParameter("contacts[1].level");
      contactDTO2.setLevel(NumberUtil.intValue(contacts2Level,1));
      contactDTO2.setName(request.getParameter("contacts[1].name"));

      contactDTOList.add(contactDTO2);

      // 第三个联系人
      ContactDTO contactDTO3 = new ContactDTO();
      contactDTO3.setShopId(shopId);
      contactDTO3.setDisabled(1);
      String id2 = request.getParameter("contacts[2].id");
      contactDTO3.setIdStr(id2);
      if (StringUtils.isNotBlank(id2)) {
        contactDTO3.setId(Long.parseLong(id2));
      }
      contactDTO3.setEmail(request.getParameter("contacts[2].email"));
      contactDTO3.setMobile(request.getParameter("contacts[2].mobile"));
      contactDTO3.setQq(request.getParameter("contacts[2].qq"));
      String contactDTO3MainContact = request.getParameter("contacts[2].mainContact");
      if (StringUtils.isNotBlank(contactDTO3MainContact)) {
        contactDTO3.setIsMainContact(Integer.parseInt(contactDTO3MainContact));
      }
      String contacts3Level = request.getParameter("contacts[2].level");
      contactDTO3.setLevel(NumberUtil.intValue(contacts3Level,2));
      contactDTO3.setName(request.getParameter("contacts[2].name"));

      contactDTOList.add(contactDTO3);
    }

    if(CollectionUtils.isNotEmpty(contactDTOList)){
      Iterator<ContactDTO> iterator = contactDTOList.iterator();
      while (iterator.hasNext()){
        if(!iterator.next().isValidContact()){
          iterator.remove();
        }
      }
    }
    if(CollectionUtils.isNotEmpty(contactDTOList)){
      return contactDTOList.toArray(new ContactDTO[contactDTOList.size()]);
    }
    return null;
  }

  private static List<ContactDTO> buildNormalKeys(HttpServletRequest request,String prefix) {
    List<ContactDTO> contactDTOList = new ArrayList<ContactDTO>();
    for (int i = 0; i < 6; i++) {
      ContactDTO contactDTO = new ContactDTO();
      contactDTO.setShopId(WebUtil.getShopId(request));
      contactDTO.setDisabled(1);
      String id = request.getParameter(prefix + "[" + i + "].id");
      contactDTO.setIdStr(id);
      if (StringUtils.isNotBlank(id)) {
        contactDTO.setId(Long.parseLong(id));
      }
      contactDTO.setEmail(request.getParameter(prefix + "[" + i + "].email"));
      contactDTO.setMobile(request.getParameter(prefix + "[" + i + "].mobile"));
      contactDTO.setQq(request.getParameter(prefix + "[" + i + "].qq"));
      String contactDTOMainContact = request.getParameter(prefix + "[" + i + "].mainContact");
      if (StringUtils.isNotBlank(contactDTOMainContact)) {
        contactDTO.setIsMainContact(Integer.parseInt(contactDTOMainContact));
      }
      String contactsLevel = request.getParameter(prefix + "[" + i + "].level");
      if (StringUtils.isNotBlank(contactsLevel)) {
        Integer level = Integer.parseInt(contactsLevel);
        if (level >= 3) {
          level -= 2;
        }
        contactDTO.setLevel(level);
      }
      contactDTO.setName(request.getParameter(prefix + "[" + i + "].name"));
      if (contactDTO.isValidContact()) {
        contactDTOList.add(contactDTO);
      }
    }
    return contactDTOList;
  }

}
