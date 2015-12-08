package com.bcgogo.config.service.excelimport;

import com.bcgogo.constant.ImportConstants;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-31
 * Time: 上午11:53
 * To change this template use File | Settings | File Templates.
 */
public class ExcelImportUtil {

  /**
   * 根据excel文件名判断文件版本
   *
   * @param fileName
   * @return
   */
  public static String getVersion(String fileName) {
    if (!StringUtil.isEmpty(fileName) && fileName.endsWith(ImportConstants.ExcelVersion.EXCEL_VERSION_2007_SUFFIX)) {
      return ImportConstants.ExcelVersion.EXCEL_VERSION_2007;
    }
    return ImportConstants.ExcelVersion.EXCEL_VERSION_2003;

  }

  /**
   * excel中手机、QQ等字段类型可能不确定(比如科学计数法)，需要处理
   *
   * @param value
   * @return
   */
  public static String dealWithNumberString(Object value) {
    if (value == null) {
      return StringUtil.EMPTY_STRING;
    }
    if (value instanceof Boolean || value instanceof String || value instanceof Byte) {
      return String.valueOf(value);
    }
    if (value instanceof Double) {
      return NumberUtil.scientificToDecimal((Double) value);
    }
    return StringUtil.EMPTY_STRING;
  }


}
