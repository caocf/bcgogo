package com.bcgogo.config.service.excelexport;

import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;

/**
 * 导出异常封装
 * Created with IntelliJ IDEA.
 * User: zoujianhong
 * Date: 13-6-5
 * Time: 上午10:11
 * To change this template use File | Settings | File Templates.
 */
public class ExcelExportException extends BcgogoException {

    public ExcelExportException() {
        super();
    }

    public ExcelExportException(String message) {
        super(message);
    }

    public ExcelExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelExportException(Throwable cause) {
        super(cause);
    }

    public ExcelExportException(BcgogoExceptionType type) {
        this(type.getMessage());
        this.type = type;
    }

    BcgogoExceptionType type;

    public BcgogoExceptionType getType() {
        return type;
    }


}
