package com.bcgogo.txn.service.exportExcel;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-7-31
 * Time: 下午2:46
 * To change this template use File | Settings | File Templates.
 */
public enum ExportExcelStatus {
    WAITING("等待"),
    SUCCESS("成功"),
    FAILED("失败");
    String status;

    public String getStatus() {
        return status;
    }

    ExportExcelStatus(String status) {
        this.status = status;
    }
}
