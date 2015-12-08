package com.bcgogo.config.model;

/**
 * 流量包
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/12
 * Time: 16:01.
 */
public class TrafficPackage {

    private long productId;

    private String name;

    private int remainingNum;

    private String introduce;

    private double price;

    private String pictureUrl;

    private double disCount;

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRemainingNum() {
        return remainingNum;
    }

    public void setRemainingNum(int remainingNum) {
        this.remainingNum = remainingNum;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public double getDisCount() {
        return disCount;
    }

    public void setDisCount(double disCount) {
        this.disCount = disCount;
    }
}
