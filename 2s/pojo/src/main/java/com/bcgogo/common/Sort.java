package com.bcgogo.common;

/**
 * 排序封装类
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-22
 * Time: 下午5:57
 * To change this template use File | Settings | File Templates.
 */
public class Sort {

    private String orderBy;
    private String order;

    public String toString(){
        return " " + orderBy + " " + order + " ";
    }

    public String toOrderString(){
        return " order by " + orderBy + " " + order + " ";
    }

    public Sort(String orderBy, String order) {
        this.orderBy = orderBy;
        this.order = order;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public String getOrder() {
        return order;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
