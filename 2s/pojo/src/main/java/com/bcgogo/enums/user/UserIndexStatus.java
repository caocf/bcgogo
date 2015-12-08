package com.bcgogo.enums.user;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-8-20
 * Time: 下午2:56
 * To change this template use File | Settings | File Templates.
 */
public enum  UserIndexStatus {
    CREATED("未索引"),
    FINISHED("已完成");
    private String status;

    private UserIndexStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
