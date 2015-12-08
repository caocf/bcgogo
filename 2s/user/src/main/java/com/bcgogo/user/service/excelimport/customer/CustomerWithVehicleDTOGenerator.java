package com.bcgogo.user.service.excelimport.customer;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.excelimport.ExcelImportUtil;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.PasswordValidateStatus;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.*;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 根据从excel中解析出的一行数据构造客户（含车辆）信息
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-7
 * Time: 下午5:12
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CustomerWithVehicleDTOGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(CustomerWithVehicleDTOGenerator.class);

  public CustomerWithVehicleDTO generate(Map<String, Object> data, Map<String, String> fieldMapping, Long shopId) throws Exception {
    if (shopId == null) {
      return null;
    }
    CustomerWithVehicleDTO customerWithVehicleDTO = new CustomerWithVehicleDTO();
    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setShopId(shopId);
    customerDTO.setRelationType(RelationTypes.UNRELATED);
    MemberDTO memberDTO = new MemberDTO();
    memberDTO.setShopId(shopId);
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.NAME)) != null) {
      customerDTO.setName(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.NAME))).trim());
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.SHORT_NAME)) != null) {
      customerDTO.setShortName(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.SHORT_NAME))).trim());
    }
    if(data.get(fieldMapping.get(CustomerImportConstants.FieldName.CITY)) != null && data.get(CustomerImportConstants.PROVINCE_AND_CITY_NO) != null) {
       String provinceAndCityNo = String.valueOf(data.get(CustomerImportConstants.PROVINCE_AND_CITY_NO));
       String[] provinceAndCityNoArray = provinceAndCityNo.split(",");
       if(provinceAndCityNoArray.length > 1) {
         customerDTO.setProvince(Long.valueOf(provinceAndCityNoArray[0]));
         customerDTO.setCity(Long.valueOf(provinceAndCityNoArray[1]));
       } else {
         customerDTO.setProvince(Long.valueOf(provinceAndCityNoArray[0]));
       }
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.ADDRESS)) != null) {
      customerDTO.setAddress(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.ADDRESS))).trim());
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.CONTACT)) != null) {
      customerDTO.setContact(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.CONTACT))).trim());
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.MOBILE)) != null) {
      customerDTO.setMobile(ExcelImportUtil.dealWithNumberString(data.get(fieldMapping.get(CustomerImportConstants.FieldName.MOBILE))).trim());
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.LANDLINE)) != null) {
      customerDTO.setLandLine(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.LANDLINE))).trim());
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.FAX)) != null) {
      customerDTO.setFax(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.FAX))).trim());
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.INVOICE_CATEGORY)) != null) {
      String invoiceCategoryStr = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.INVOICE_CATEGORY))).trim();

      Long invoiceCategory = 4L;
      if("普通发票".equals(invoiceCategoryStr))
      {
        invoiceCategory = 1L;
      }
      else if("增值税发票".equals(invoiceCategoryStr))
      {
        invoiceCategory = 2L;
      }
      else if("内部".equals(invoiceCategoryStr))
      {
        invoiceCategory = 3L;
      }
      customerDTO.setInvoiceCategory(invoiceCategory);
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.SETTLEMENT_TYPE)) != null) {
      String settlementType = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.SETTLEMENT_TYPE))).trim();

      if("现金".equals(settlementType))
      {
        customerDTO.setSettlementType(1L);
      }
      else if("月结".equals(settlementType))
      {
        customerDTO.setSettlementType(2L);
      }
      else if("货到付款".equals(settlementType))
      {
        customerDTO.setSettlementType(3L);
      }
      else if("季付".equals(settlementType))
      {
        customerDTO.setSettlementType(4L);
      }
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.CUSTOMER_KIND)) != null) {
      String customerKindStr = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.CUSTOMER_KIND))).trim();

      String customerKind = "4";
      if("普通".equals(customerKindStr))
      {
        customerKind = "1";
      }
      else if("单位".equals(customerKindStr))
      {
        customerKind = "2";
      }
      else if("大客户".equals(customerKindStr))
      {
        customerKind = "3";
      }
      customerDTO.setCustomerKind(customerKind);
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.AREA)) != null) {
      String area = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.AREA))).trim();
      if("本地".equals(area))
      {
        customerDTO.setArea("1");
      }
      else if("外地".equals(area))
      {
        customerDTO.setArea("2");
      }
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.BIRTHDAY)) != null) {
      try {
        customerDTO.setBirthday(DateUtil.convertDateStringToDateLong(DateUtil.MONTH_DATE, String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.BIRTHDAY))).trim()));
      } catch (ParseException e) {
        LOG.error("解析生日失败 ： " + customerDTO.getName());
        LOG.error(e.getMessage(), e);
      }
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.QQ)) != null) {
      customerDTO.setQq(ExcelImportUtil.dealWithNumberString(data.get(fieldMapping.get(CustomerImportConstants.FieldName.QQ))).trim());
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.EMAIL)) != null) {
      customerDTO.setEmail(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.EMAIL))).trim());
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.BANK)) != null) {
      customerDTO.setBank(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.BANK))).trim());
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.BANK_ACCOUNT_NAME)) != null) {
      customerDTO.setBankAccountName(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.BANK_ACCOUNT_NAME))).trim());
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.ACCOUNT)) != null) {
      customerDTO.setAccount(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.ACCOUNT))).trim());
    }
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.MEMBER_NO))!= null)
    {
      memberDTO.setMemberNo(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.MEMBER_NO))).trim());
      memberDTO.setPasswordStatus(PasswordValidateStatus.UNVALIDATE);
      memberDTO.setStatus(MemberStatus.ENABLED);

      if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.TYPE)) != null)
      {
        String type = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.TYPE))).trim();
        if(StringUtils.isNotBlank(type))
        {
          memberDTO.setType(type);
        }
        else
        {
          memberDTO.setType("会员卡");
        }
      }
      else
      {
        memberDTO.setType("会员卡");
      }
      if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.BALANCE)) != null)
      {
        String balance = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.BALANCE))).trim();
        if(StringUtils.isNotBlank(balance))
        {
          memberDTO.setBalance(Double.valueOf(balance));
        }
        else
        {
          memberDTO.setBalance(0D);
        }

      }
      if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.ACCUMULATE_POINTS)) != null)
      {
        String accumulate_points = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.ACCUMULATE_POINTS))).trim();

        if(StringUtils.isNotBlank(accumulate_points))
        {
          memberDTO.setAccumulatePoints(Integer.valueOf(((accumulate_points).split("\\."))[0]));
        }
      }
      if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.MEMBER_DISCOUNT)) != null)
      {
        String memberDiscount = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.MEMBER_DISCOUNT))).trim();
        if(StringUtils.isNotBlank(memberDiscount))
        {
          memberDTO.setMemberDiscount(NumberUtil.round(Double.valueOf(memberDiscount)/10,2));
        }

      }
//      if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.SERVICE_DISCOUNT)) != null)
//      {
//        String serviceDiscount = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.SERVICE_DISCOUNT))).trim();
//        if(StringUtils.isNotBlank(serviceDiscount))
//        {
//          memberDTO.setServiceDiscount(Double.valueOf(serviceDiscount));
//        }
//
//      }
//      if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.MATERIAL_DISCOUNT)) != null)
//      {
//        String materialDiscount = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.MATERIAL_DISCOUNT))).trim();
//        if(StringUtils.isNotBlank(materialDiscount))
//        {
//          memberDTO.setMaterialDiscount(Double.valueOf(materialDiscount));
//        }
//
//      }
      if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.JOIN_DATE)) != null)
      {
        memberDTO.setJoinDateStr(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.JOIN_DATE))).trim());
        if(StringUtils.isNotBlank(memberDTO.getJoinDateStr()))
        {
          memberDTO.setJoinDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,memberDTO.getJoinDateStr()));
        }
        else
        {
          memberDTO.setJoinDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,System.currentTimeMillis())));
        }
      }
      else
      {
          memberDTO.setJoinDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,System.currentTimeMillis())));
      }
      if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.DEADLINE)) != null)
      {
        memberDTO.setDeadlineStr(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.DEADLINE))).trim());
        if(StringUtils.isNotBlank(memberDTO.getDeadlineStr()))
        {
          long date = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,memberDTO.getDeadlineStr()) + 86399000L;
          memberDTO.setDeadline(date);
        }

      }
    }

    //手机号那一列自动匹配（是手机号的setMobile ,不是手机号的setLandline）
    if(StringUtils.isNotBlank(customerDTO.getMobile()))
    {
      String[] mobiles = customerDTO.getMobile().trim().split("/");

      String mobile = "";

      mobile = RegexUtils.format(mobiles[0]).trim();
      if(StringUtils.isNotBlank(mobile))
      {
        if(RegexUtils.isMobile(mobile))
        {
          customerDTO.setMobile(mobile);
        }
        else
        {
          customerDTO.setMobile("");
          if(StringUtils.isBlank(customerDTO.getLandLine()))
          {
            customerDTO.setLandLine(mobile);
          }
        }
      }


      if(mobiles.length>1 && StringUtils.isNotBlank(RegexUtils.format(mobiles[1]).trim()))
      {
        mobile = RegexUtils.format(mobiles[1]).trim();
        if(StringUtils.isBlank(customerDTO.getMobile()))
        {
          if(RegexUtils.isMobile(mobile))
          {
            customerDTO.setMobile(mobile);
          }
          else
          {
            customerDTO.setMobile("");
          }
        }
        if(StringUtils.isBlank(customerDTO.getLandLine()) && !RegexUtils.isMobile(mobile))
        {
          customerDTO.setLandLine(mobile);
        }
      }

    }

    customerWithVehicleDTO.setCustomerDTO(customerDTO);
    customerWithVehicleDTO.setMemberDTO(memberDTO);
    CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
    customerRecordDTO.setName(customerDTO.getName());
    customerRecordDTO.setContact(customerDTO.getContact());
    customerRecordDTO.setEmail(customerDTO.getEmail());
    customerRecordDTO.setMobile(customerDTO.getMobile());
    customerRecordDTO.setArea(customerDTO.getArea());
    customerRecordDTO.setAddress(customerDTO.getAddress());
    customerRecordDTO.setFax(customerDTO.getFax());
    customerRecordDTO.setQq(customerDTO.getQq());
    customerRecordDTO.setBank(customerDTO.getBank());
    customerRecordDTO.setAccount(customerDTO.getBank());
    customerRecordDTO.setShopId(customerDTO.getShopId());
    customerRecordDTO.setProvince(customerDTO.getProvince());
    customerRecordDTO.setCity(customerDTO.getCity());
    customerWithVehicleDTO.setCustomerRecordDTO(customerRecordDTO);


    String licenceNos = null;
    if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_LICENCE_NO)) != null) {
      licenceNos = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_LICENCE_NO))).trim();
    }
    if (StringUtils.isNotBlank(licenceNos)) {

      List<VehicleDTO> vehicleDTOList = new ArrayList<VehicleDTO>();

      licenceNos = licenceNos.replaceAll(" ","");

      if(-1 != licenceNos.indexOf(","))
      {
        String[] licenceNoArray = licenceNos.split(",");
        int length = licenceNoArray.length;
        String licenceNo = "";
        for(int i=0;i<length;i++)
        {
          licenceNo = licenceNoArray[i];
          VehicleDTO vehicleDTO = new VehicleDTO();
          vehicleDTO.setShopId(shopId);
          vehicleDTO.setLicenceNo(licenceNo);
          if(length == 1)
          {
            if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_BRAND)) != null) {
              vehicleDTO.setBrand(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_BRAND))).trim());
            }
            if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_MODEL)) != null) {
              vehicleDTO.setModel(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_MODEL))).trim());
            }
            if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_YEAR)) != null) {
              vehicleDTO.setYear(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_YEAR))).trim());
            }

            if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_CHASSIS_NUMBER)) != null) {
              vehicleDTO.setChassisNumber(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_CHASSIS_NUMBER))));
              vehicleDTO.setChassisNumber(vehicleDTO.getChassisNumber().replace(" ","").toUpperCase());
            }

            if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_ENGINE_NO)) != null) {
              vehicleDTO.setEngineNo(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_ENGINE_NO))).trim());
            }
            if(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_ENGINE)) != null){
              vehicleDTO.setEngine(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_ENGINE))).trim());
            }
          }

          vehicleDTOList.add(vehicleDTO);
        }
      }
      else
      {
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setShopId(shopId);
        vehicleDTO.setLicenceNo(licenceNos);
        if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_BRAND)) != null) {
          vehicleDTO.setBrand(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_BRAND))).trim());
        }
        if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_MODEL)) != null) {
          vehicleDTO.setModel(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_MODEL))).trim());
        }
        if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_YEAR)) != null) {
          vehicleDTO.setYear(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_YEAR))).trim());
        }

        if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_CHASSIS_NUMBER)) != null) {
          vehicleDTO.setChassisNumber(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_CHASSIS_NUMBER))).trim());
          vehicleDTO.setChassisNumber(vehicleDTO.getChassisNumber().replace(" ","").toUpperCase());
        }

        if (data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_ENGINE_NO)) != null) {
          vehicleDTO.setEngineNo(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_ENGINE_NO))).trim());
        }
        if(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_ENGINE)) != null){
          vehicleDTO.setEngine(String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_ENGINE))).trim());
        }

        vehicleDTOList.add(vehicleDTO);
      }

      customerWithVehicleDTO.setVehicleDTOList(vehicleDTOList);
    }
    return customerWithVehicleDTO;
  }

}
