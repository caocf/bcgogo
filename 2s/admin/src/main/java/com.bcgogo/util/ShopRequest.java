package com.bcgogo.util;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.utils.DateUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: qxy
 * Date: 10/10/11
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShopRequest {

  public ShopDTO shopDTORequest(HttpServletRequest request, ShopDTO shopDTO)throws Exception{
    String name =  request.getParameter("name");
    if (name != null) {
      shopDTO.setName(java.net.URLDecoder.decode(name.trim(), "UTF-8"));
    }
    String legalRep =request.getParameter("legalRep");
    if (legalRep != null) {
      shopDTO.setLegalRep(java.net.URLDecoder.decode(legalRep.trim(), "UTF-8"));
    }
    String no =request.getParameter("no");
    if (no != null) {
      shopDTO.setNo(java.net.URLDecoder.decode(no.trim(), "UTF-8"));
    }
    String areaIdStr =request.getParameter("areaId");
    if (areaIdStr != null&&!"".equals(areaIdStr)) {
        Long areaId = Long.parseLong(java.net.URLDecoder.decode(areaIdStr.trim(), "UTF-8"));
      shopDTO.setAreaId(areaId);
    }

    String address =request.getParameter("address");
    if (address != null) {
      shopDTO.setAddress(java.net.URLDecoder.decode(address.trim(), "UTF-8"));
    }

    String zip =request.getParameter("zip"); ;
    if (zip != null) {
      shopDTO.setZip(java.net.URLDecoder.decode(zip.trim(), "UTF-8"));
    }

    String contact = request.getParameter("contact");
    if (contact != null) {
      shopDTO.setContact(java.net.URLDecoder.decode(contact.trim(), "UTF-8"));
    }

    String landline =request.getParameter("landline");
    if (landline != null) {
      shopDTO.setLandline(java.net.URLDecoder.decode(landline.trim(), "UTF-8"));
    }

    String mobile = request.getParameter("mobile");
    if (mobile != null) {
      shopDTO.setMobile(java.net.URLDecoder.decode(mobile.trim(), "UTF-8"));
    }

    String fax = request.getParameter("fax");
    if (fax != null) {
      shopDTO.setFax(java.net.URLDecoder.decode(fax.trim(),"UTF-8"));
    }

    String email =request.getParameter("email");
    if (email != null) {
      shopDTO.setEmail(java.net.URLDecoder.decode(email.trim(), "UTF-8"));
    }

    String qq = request.getParameter("qq");
    if (qq != null) {
      shopDTO.setQq(java.net.URLDecoder.decode(qq.trim(), "UTF-8"));
    }

    String bank =request.getParameter("bank");
    if (bank != null) {
      shopDTO.setBank(java.net.URLDecoder.decode(bank.trim(), "UTF-8"));
    }

    String account =request.getParameter("account");
    if (account != null) {
      shopDTO.setAccount(java.net.URLDecoder.decode(account.trim(), "UTF-8"));
    }

    String softPrice =request.getParameter("softPrice");
    if (softPrice != null) {
      shopDTO.setSoftPrice(new Double(java.net.URLDecoder.decode(softPrice.trim(), "UTF-8")));
    }
    String operationMode =request.getParameter("operationMode");
    if (operationMode != null) {
      shopDTO.setOperationMode(java.net.URLDecoder.decode(operationMode.trim(), "UTF-8"));
    }

    String businessHours =request.getParameter("businessHours");
    if (businessHours != null) {
      shopDTO.setBusinessHours(java.net.URLDecoder.decode(businessHours.trim(), "UTF-8"));
    }

    String established =request.getParameter("established") ;
    if (established != null) {
      shopDTO.setEstablished(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY,java.net.URLDecoder.decode(established.trim(), "UTF-8")));
    }

    String qualification =request.getParameter("qualification") ;
    if (qualification != null) {
      shopDTO.setQualification(java.net.URLDecoder.decode(qualification.trim(), "UTF-8"));
    }

    String personnel =request.getParameter("personnel");
    if (personnel != null) {
      shopDTO.setPersonnel(java.net.URLDecoder.decode(personnel.trim(), "UTF-8"));
    }

    String area =request.getParameter("area");
    if (area != null) {
      shopDTO.setArea(java.net.URLDecoder.decode(area.trim(), "UTF-8"));
    }

    String businessScope =request.getParameter("businessScope");
    if (businessScope != null) {
      shopDTO.setBusinessScope(java.net.URLDecoder.decode(businessScope.trim(), "UTF-8"));
    }

    String relatedBusiness =request.getParameter("relatedBusiness") ;
    if (relatedBusiness != null) {
      shopDTO.setRelatedBusiness(java.net.URLDecoder.decode(relatedBusiness.trim(), "UTF-8"));
    }

    String feature =request.getParameter("feature");
    if (feature != null) {
      shopDTO.setFeature(java.net.URLDecoder.decode(feature.trim(), "UTF-8"));
    }

    String storeManager =request.getParameter("storeManager") ;
    if (storeManager != null) {
      shopDTO.setStoreManager(java.net.URLDecoder.decode(storeManager.trim(), "UTF-8"));
    }

    String storeManagerMobile =request.getParameter("storeManagerMobile");
    if (storeManagerMobile != null) {
      shopDTO.setStoreManagerMobile(java.net.URLDecoder.decode(storeManagerMobile.trim(), "UTF-8"));
    }
    String shortname =request.getParameter("shortname");
    if (shortname != null) {
      shopDTO.setShortname(java.net.URLDecoder.decode(shortname.trim(), "UTF-8"));
    }
    String licencePlate =request.getParameter("licencePlate");
    if (licencePlate != null) {
      shopDTO.setLicencePlate(java.net.URLDecoder.decode(licencePlate.trim(), "UTF-8"));
    }
    return shopDTO;
  }
}
