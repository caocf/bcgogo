package com.bcgogo.user.service.excelimport.obd;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.excelimport.ExcelImportException;
import com.bcgogo.config.service.excelimport.ImportVerifier;
import com.bcgogo.enums.YesNo;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ValidateImportDataDTO;
import com.bcgogo.user.service.excelimport.customer.CustomerImportConstants;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by XinyuQiu on 14-6-30.
 */
@Component
public class ObdImportVerifier implements ImportVerifier {

  @Override
  public String verify(Map<String, Object> data, Map<String, String> fieldMapping, ValidateImportDataDTO validateImportDataDTO) throws ExcelImportException {

    Object imei = data.get(fieldMapping.get(ObdImportConstants.FieldName.IMEI));
    Object obdVersion = data.get(fieldMapping.get(ObdImportConstants.FieldName.OBD_VERSION));
    Object spec = data.get(fieldMapping.get(ObdImportConstants.FieldName.SPEC));
    Object color = data.get(fieldMapping.get(ObdImportConstants.FieldName.COLOR));
    Object pack = data.get(fieldMapping.get(ObdImportConstants.FieldName.PACK));
    Object crash = data.get(fieldMapping.get(ObdImportConstants.FieldName.CRASH));
    Object shake = data.get(fieldMapping.get(ObdImportConstants.FieldName.SHAKE));
    Object simNo = data.get(fieldMapping.get(ObdImportConstants.FieldName.SIM_NO));
    Object mobile = data.get(fieldMapping.get(ObdImportConstants.FieldName.MOBILE));
    Object useDate = data.get(fieldMapping.get(ObdImportConstants.FieldName.USE_DATE));
    Object usePeriod = data.get(fieldMapping.get(ObdImportConstants.FieldName.USE_PERIOD));

    if (!StringUtil.isAllEmpty(imei, obdVersion, spec, color, pack, crash, shake)) {
      String verifyImei = verifyImei(imei, validateImportDataDTO);
      if (StringUtils.isNotBlank(verifyImei)) {
        return verifyImei;
      }

      String verifyObdVersion = verifyObdVersion(obdVersion);
      if (StringUtils.isNotBlank(verifyObdVersion)) {
        return verifyObdVersion;
      }

      String verifySpec = verifySpec(spec);
      if (StringUtils.isNotBlank(verifySpec)) {
        return verifySpec;
      }

      String verifyColor = verifyColor(color);
      if (StringUtils.isNotBlank(verifyColor)) {
        return verifyColor;
      }

      String verifyPack = verifyPack(pack);
      if (StringUtils.isNotBlank(verifyPack)) {
        return verifyPack;
      }

      String verifyCrash = verifyCrash(crash);
      if (StringUtils.isNotBlank(verifyCrash)) {
        return verifyCrash;
      }

      String verifyShake = verifyShark(shake);
      if (StringUtils.isNotBlank(verifyShake)) {
        return verifyShake;
      }
    }

    if (!StringUtil.isAllEmpty(simNo, mobile, useDate, usePeriod)) {
      String verifySimNo = verifySimNo(simNo, validateImportDataDTO);
      if (StringUtils.isNotBlank(verifySimNo)) {
        return verifySimNo;
      }

      String verifyMobile = verifyMobile(mobile, validateImportDataDTO);
      if (StringUtils.isNotBlank(verifyMobile)) {
        return verifyMobile;
      }

      String verifyUseDate = verifyUseDate(useDate);
      if (StringUtils.isNotBlank(verifyUseDate)) {
        return verifyUseDate;
      }

      String verifyUsePeriod = verifyUsePeriod(usePeriod);
      if (StringUtils.isNotBlank(verifyUsePeriod)) {
        return verifyUsePeriod;
      }
    }
    return null;
  }

  private String verifyUsePeriod(Object usePeriod) {
    if (usePeriod == null || StringUtils.isBlank(usePeriod.toString())) {
      return ObdImportConstants.CheckResultMessage.USE_DATA_EMPTY;
    }
    String usePeriodStr = String.valueOf(usePeriod).trim();

    if (!(StringUtils.isNumeric(usePeriodStr) &&
        NumberUtils.toInt(usePeriodStr) >= 1 && NumberUtils.toInt(usePeriodStr) <= 5)) {
      return ObdImportConstants.CheckResultMessage.USE_PERIOD_ILLEGAL;
    }
    return null;
  }

  private String verifyUseDate(Object useDate) {
    if (useDate == null || StringUtils.isBlank(useDate.toString())) {
      return ObdImportConstants.CheckResultMessage.USE_DATA_EMPTY;
    }
    String useDateStr = String.valueOf(useDate).trim();
    //2014-06//
    String[] useDateArr = useDateStr.split("-");
    if (useDateArr.length != 2) {
      return ObdImportConstants.CheckResultMessage.USE_DATA_ILLEGAL;
    }

    if (!(useDateArr[0].length() == 4 && StringUtils.isNumeric(useDateArr[0]))) {
      return ObdImportConstants.CheckResultMessage.USE_DATA_ILLEGAL;
    }

    if (!(useDateArr[1].length() <= 2 && StringUtils.isNumeric(useDateArr[1]) &&
        NumberUtils.toInt(useDateArr[1]) >= 1 && NumberUtils.toInt(useDateArr[1]) <= 12)) {
      return ObdImportConstants.CheckResultMessage.USE_DATA_ILLEGAL;
    }
    return null;
  }

  private String verifySimNo(Object simNo, ValidateImportDataDTO validateImportDataDTO) {
    if (simNo == null || StringUtils.isBlank(simNo.toString())) {
      return ObdImportConstants.CheckResultMessage.SIM_NO_EMPTY;
    }
    String simNoStr = String.valueOf(simNo).trim();
    if (simNoStr.length() != ObdImportConstants.FieldLength.FIELD_LENGTH_SIM_NO) {
      return ObdImportConstants.CheckResultMessage.SIM_NO_LENGTH_ILLEGAL;
    }
//    if (!StringUtils.isNumeric(simNoStr)) {
//      return ObdImportConstants.CheckResultMessage.SIM_NO_NUMBER_ILLEGAL;
//    }

    if (validateImportDataDTO != null && MapUtils.isNotEmpty(validateImportDataDTO.getMobileNoObdSimDTOMap()) &&
        validateImportDataDTO.getMobileNoObdSimDTOMap().get(simNoStr) != null) {
      return "\"" + simNoStr + "\"" + ObdImportConstants.CheckResultMessage.SIM_NO_EXIST_IN_TABLE;
    }
    return null;
  }

  public String verifyMobile(Object mobile, ValidateImportDataDTO validateImportDataDTO) {
    if (mobile == null || StringUtils.isBlank(mobile.toString())) {
      return ObdImportConstants.CheckResultMessage.MOBILE_EMPTY;
    }
    String mobileStr = String.valueOf(mobile).trim();
    if (!RegexUtils.isMobile(mobileStr)) {
      return ObdImportConstants.CheckResultMessage.MOBILE_ILLEGAL;
    }
    if (validateImportDataDTO != null && MapUtils.isNotEmpty(validateImportDataDTO.getMobileObdSimDTOMap()) &&
        validateImportDataDTO.getMobileObdSimDTOMap().get(mobileStr) != null) {
      return "\"" + mobileStr + "\"" + ObdImportConstants.CheckResultMessage.MOBILE_EXIST_IN_TABLE;
    }
    return null;
  }

  private String verifySpec(Object spec) {
    if (spec == null || StringUtils.isBlank(spec.toString())) {
      return ObdImportConstants.CheckResultMessage.SPEC_EMPTY;
    }
    String obdVersionStr = String.valueOf(spec).trim();
    if (obdVersionStr.length() > ObdImportConstants.FieldLength.FIELD_LENGTH_SPEC) {
      return ObdImportConstants.CheckResultMessage.SPEC_LENGTH_ILLEGAL;
    }
    return null;
  }

  private String verifyPack(Object pack) {
    if (pack == null || StringUtils.isBlank(pack.toString())) {
      return ObdImportConstants.CheckResultMessage.PACK_EMPTY;
    }
    String obdVersionStr = String.valueOf(pack).trim();
    if (obdVersionStr.length() > ObdImportConstants.FieldLength.FIELD_LENGTH_PACK) {
      return ObdImportConstants.CheckResultMessage.PACK_LENGTH_ILLEGAL;
    }
    return null;
  }

  private String verifyCrash(Object crash) {
    if (crash == null || StringUtils.isBlank(crash.toString())) {
      return ObdImportConstants.CheckResultMessage.CRASH_EMPTY;
    }
    String crashStr = String.valueOf(crash).trim().toUpperCase();
    if (!(YesNo.YES.name().equals(crashStr) || YesNo.NO.name().equals(crashStr))) {
      return ObdImportConstants.CheckResultMessage.CRASH_ILLEGAL;
    }
    return null;
  }

  private String verifyShark(Object shake) {
    if (shake == null || StringUtils.isBlank(shake.toString())) {
      return ObdImportConstants.CheckResultMessage.SHAKE_EMPTY;
    }
    String crashStr = String.valueOf(shake).trim().toUpperCase();
    if (!(YesNo.YES.name().equals(crashStr) || YesNo.NO.name().equals(crashStr))) {
      return ObdImportConstants.CheckResultMessage.SHAKE_ILLEGAL;
    }
    return null;
  }

  private String verifyColor(Object color) {
    if (color == null || StringUtils.isBlank(color.toString())) {
      return ObdImportConstants.CheckResultMessage.COLOR_EMPTY;
    }
    String obdVersionStr = String.valueOf(color).trim();
    if (obdVersionStr.length() > ObdImportConstants.FieldLength.FIELD_LENGTH_COLOR) {
      return ObdImportConstants.CheckResultMessage.COLOR_LENGTH_ILLEGAL;
    }
    return null;
  }

  private String verifyObdVersion(Object obdVersion) {
    if (obdVersion == null || StringUtils.isBlank(obdVersion.toString())) {
      return ObdImportConstants.CheckResultMessage.OBD_VERSION_EMPTY;
    }
    String obdVersionStr = String.valueOf(obdVersion).trim();
    if (obdVersionStr.length() > ObdImportConstants.FieldLength.FIELD_LENGTH_OBD_VERSION) {
      return ObdImportConstants.CheckResultMessage.OBD_VERSION_LENGTH_ILLEGAL;
    }
    return null;
  }

  private String verifyImei(Object imei, ValidateImportDataDTO validateImportDataDTO) {
    if (imei == null || StringUtils.isBlank(imei.toString())) {
      return ObdImportConstants.CheckResultMessage.IMEI_EMPTY;
    }
    String iMeiStr = String.valueOf(imei).trim();
    if (iMeiStr.length() < ObdImportConstants.FieldLength.FIELD_LENGTH_IMEI) {
      return ObdImportConstants.CheckResultMessage.IMEI_LENGTH_ILLEGAL;
    }
    if (!StringUtils.isNumeric(iMeiStr)) {
      return ObdImportConstants.CheckResultMessage.IMEI_NUMBER_ILLEGAL;
    }

    if (validateImportDataDTO != null && MapUtils.isNotEmpty(validateImportDataDTO.getImeiObdDTOMap()) &&
        validateImportDataDTO.getImeiObdDTOMap().get(iMeiStr) != null) {
      return "\"" + iMeiStr + "\"" + ObdImportConstants.CheckResultMessage.IMEI_EXIST_IN_TABLE;
    }
    return null;
  }

}
