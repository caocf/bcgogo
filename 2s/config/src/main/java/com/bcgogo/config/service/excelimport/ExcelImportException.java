package com.bcgogo.config.service.excelimport;


import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;

/**
 * 导入功能专用异常
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-5
 * Time: 下午3:15
 * To change this template use File | Settings | File Templates.
 */
public class ExcelImportException extends BcgogoException {

  public ExcelImportException() {
  }

  public ExcelImportException(String message) {
    super(message);
  }

  public ExcelImportException(String message, Throwable cause) {
    super(message, cause);
  }

  public ExcelImportException(Throwable cause) {
    super(cause);
  }

  public ExcelImportException(BcgogoExceptionType type) {
    super(type);
  }
}
