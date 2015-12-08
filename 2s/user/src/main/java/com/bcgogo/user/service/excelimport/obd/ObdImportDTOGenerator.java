package com.bcgogo.user.service.excelimport.obd;

import com.bcgogo.api.ObdSimBindDTO;
import com.bcgogo.config.service.excelimport.ExcelImportException;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.app.ObdMirrorType;
import com.bcgogo.enums.app.ObdSimOwnerType;
import com.bcgogo.enums.app.ObdType;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by XinyuQiu on 14-6-30.
 */
@Component
public class ObdImportDTOGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(ObdImportDTOGenerator.class);


  public ObdSimBindDTO generate(Map<String, Object> data, Map<String, String> fieldMapping, Long userId, String userName) throws ExcelImportException {
    if (userId == null) {
      return null;
    }
    String imei = generateDataField(data, fieldMapping, ObdImportConstants.FieldName.IMEI);
    String obdVersion = generateDataField(data, fieldMapping, ObdImportConstants.FieldName.OBD_VERSION);
    String spec = generateDataField(data, fieldMapping, ObdImportConstants.FieldName.SPEC);
    String color = generateDataField(data, fieldMapping, ObdImportConstants.FieldName.COLOR);
    String pack = generateDataField(data, fieldMapping, ObdImportConstants.FieldName.PACK);
    String crash = generateDataField(data, fieldMapping, ObdImportConstants.FieldName.CRASH);
    String shake = generateDataField(data, fieldMapping, ObdImportConstants.FieldName.SHAKE);
    String simNo = generateDataField(data, fieldMapping, ObdImportConstants.FieldName.SIM_NO);
    String mobile = generateDataField(data, fieldMapping, ObdImportConstants.FieldName.MOBILE);
    String useDate = generateDataField(data, fieldMapping, ObdImportConstants.FieldName.USE_DATE);
    String usePeriod = generateDataField(data, fieldMapping, ObdImportConstants.FieldName.USE_PERIOD);
    String gsm_mirror = generateDataField(data, fieldMapping, ObdImportConstants.FieldName.GSM_MIRROR);

    ObdSimBindDTO obdSimBindDTO = new ObdSimBindDTO();
    obdSimBindDTO.setImei(imei);
    obdSimBindDTO.setObdVersion(obdVersion);
    obdSimBindDTO.setSpec(spec);
    obdSimBindDTO.setColor(color);
    obdSimBindDTO.setPack(pack);
    obdSimBindDTO.setOpenCrash(YesNo.convertYesNo(crash));
    obdSimBindDTO.setOpenShake(YesNo.convertYesNo(shake));
    obdSimBindDTO.setSimNo(simNo);
    obdSimBindDTO.setMobile(mobile);
    obdSimBindDTO.setUseDateStr(useDate);
    if ("后视镜".equals(gsm_mirror)) {
      obdSimBindDTO.setObdMirrorType(ObdMirrorType.MIRROR);
    } else if (ObdType.POBD.toString().equals(gsm_mirror)) {
      obdSimBindDTO.setObdMirrorType(ObdMirrorType.POBD);
    } else if (ObdType.SGSM.toString().equals(gsm_mirror)) {
      obdSimBindDTO.setObdMirrorType(ObdMirrorType.SGSM);
    } else {
      obdSimBindDTO.setObdMirrorType(ObdMirrorType.GSM);
    }

    if (StringUtils.isNotBlank(useDate)) {
      try {
        obdSimBindDTO.setUseDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_YEAR_MON, useDate));
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
    }
    if (StringUtils.isNotBlank(usePeriod) && StringUtils.isNumeric(usePeriod)) {
      obdSimBindDTO.setUsePeriod(NumberUtil.intValue(usePeriod));
    }
    obdSimBindDTO.setOwnerName(userName);
    obdSimBindDTO.setOwnerId(userId);
    obdSimBindDTO.setOwnerType(ObdSimOwnerType.STORAGE);
    return obdSimBindDTO;
  }

  private String generateDataField(Map<String, Object> data, Map<String, String> fieldMapping, String field) {
    Object filedValueObject = data.get(fieldMapping.get(field));
    return filedValueObject != null && StringUtils.isNotBlank(filedValueObject.toString()) ? filedValueObject.toString().trim().toUpperCase() : null;
  }
}
