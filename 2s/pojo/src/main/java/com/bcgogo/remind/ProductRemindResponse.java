package com.bcgogo.remind;

/**
 * Created by IntelliJ IDEA.
 * User: lijie
 * Date: 11-11-17
 * Time: 下午5:46
 * To change this template use File | Settings | File Templates.
 */
public class ProductRemindResponse {
    private String remind;
    private Long nowNumber;
    private Long adviseNumber;
    private String productCategory;
    private String brand;
    private String specification;     //规格
    private String productModel;      //型号
    private String carBrand;
    private int carYear;
    private String engine;             //排量
    private String supplier;           //供应商

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }

    public Long getNowNumber() {
        return nowNumber;
    }

    public void setNowNumber(Long nowNumber) {
        this.nowNumber = nowNumber;
    }

    public Long getAdviseNumber() {
        return adviseNumber;
    }

    public void setAdviseNumber(Long adviseNumber) {
        this.adviseNumber = adviseNumber;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public int getCarYear() {
        return carYear;
    }

    public void setCarYear(int carYear) {
        this.carYear = carYear;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

}
