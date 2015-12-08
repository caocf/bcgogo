package com.bcgogo.config.dto.image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-31
 * Time: 下午5:13
 * To change this template use File | Settings | File Templates.
 */
public class ImageCenterDTO {
  //保存和hidden 存储 已经存在的 路径
  private String shopBusinessLicenseImagePath;
  private List<String> shopImagePaths;

  private List<String> productInfoImagePaths;
  private String productMainImagePath;

  private List<String> customerIdentificationImagePaths;

  private String bcgogoProductListImageURL;
  private String bcgogoProductListSmallImageURL;
  private String bcgogoProductInfoSmallImageURL;
  private String bcgogoProductInfoBigImageURL;

  //显示用
  private DataImageDetailDTO recommendProductListImageDetailDTO;
  private DataImageDetailDTO productListSmallImageDetailDTO;
  private DataImageDetailDTO productListBigImageDetailDTO;
  private DataImageDetailDTO otherProductListImageDetailDTO;
  private List<DataImageDetailDTO> productInfoBigImageDetailDTOs;
  private List<DataImageDetailDTO> productInfoSmallImageDetailDTOs;
  private List<DataImageDetailDTO> productInfoDescriptionImageDetailDTOs;

  private DataImageDetailDTO shopBusinessLicenseImageDetailDTO;
  private DataImageDetailDTO shopBigMainImageDetailDTO;
  private DataImageDetailDTO shopSmallMainImageDetailDTO;
  private DataImageDetailDTO shopRQImageDetailDTO;
  private List<DataImageDetailDTO> shopBigImageDetailDTOs;
  private List<DataImageDetailDTO> shopImageDetailDTOs;
  private List<DataImageDetailDTO> shopSmallImageDetailDTOs;
  private List<DataImageDetailDTO> shopUploadImageDetailDTOs;
  private List<DataImageDetailDTO> customerIdentificationImageDetailDTOs;
  private List<DataImageDetailDTO> customerUploadIdentificationImageDetailDTOs;
  private List<DataImageDetailDTO> appUserBillBigImageDetailDTOs = new ArrayList<DataImageDetailDTO>();
  private List<DataImageDetailDTO> appUserBillSmallImageDetailDTOs = new ArrayList<DataImageDetailDTO>();

  private List<DataImageDetailDTO> shopAdvertImageDetailDTOs = new ArrayList<DataImageDetailDTO>();
  private DataImageDetailDTO shopAdvertSmallImage;


  public DataImageDetailDTO getShopAdvertSmallImage() {
    return shopAdvertSmallImage;
  }

  public void setShopAdvertSmallImage(DataImageDetailDTO shopAdvertSmallImage) {
    this.shopAdvertSmallImage = shopAdvertSmallImage;
  }

  public String getBcgogoProductListSmallImageURL() {
    return bcgogoProductListSmallImageURL;
  }

  public void setBcgogoProductListSmallImageURL(String bcgogoProductListSmallImageURL) {
    this.bcgogoProductListSmallImageURL = bcgogoProductListSmallImageURL;
  }

  public String getBcgogoProductListImageURL() {
    return bcgogoProductListImageURL;
  }

  public void setBcgogoProductListImageURL(String bcgogoProductListImageURL) {
    this.bcgogoProductListImageURL = bcgogoProductListImageURL;
  }

  public String getBcgogoProductInfoSmallImageURL() {
    return bcgogoProductInfoSmallImageURL;
  }

  public void setBcgogoProductInfoSmallImageURL(String bcgogoProductInfoSmallImageURL) {
    this.bcgogoProductInfoSmallImageURL = bcgogoProductInfoSmallImageURL;
  }

  public String getBcgogoProductInfoBigImageURL() {
    return bcgogoProductInfoBigImageURL;
  }

  public void setBcgogoProductInfoBigImageURL(String bcgogoProductInfoBigImageURL) {
    this.bcgogoProductInfoBigImageURL = bcgogoProductInfoBigImageURL;
  }

  public DataImageDetailDTO getOtherProductListImageDetailDTO() {
    return otherProductListImageDetailDTO;
  }

  public void setOtherProductListImageDetailDTO(DataImageDetailDTO otherProductListImageDetailDTO) {
    this.otherProductListImageDetailDTO = otherProductListImageDetailDTO;
  }

  public List<DataImageDetailDTO> getCustomerUploadIdentificationImageDetailDTOs() {
    return customerUploadIdentificationImageDetailDTOs;
  }

  public void setCustomerUploadIdentificationImageDetailDTOs(List<DataImageDetailDTO> customerUploadIdentificationImageDetailDTOs) {
    this.customerUploadIdentificationImageDetailDTOs = customerUploadIdentificationImageDetailDTOs;
  }

  public List<DataImageDetailDTO> getCustomerIdentificationImageDetailDTOs() {
    return customerIdentificationImageDetailDTOs;
  }

  public void setCustomerIdentificationImageDetailDTOs(List<DataImageDetailDTO> customerIdentificationImageDetailDTOs) {
    this.customerIdentificationImageDetailDTOs = customerIdentificationImageDetailDTOs;
  }

  public List<String> getCustomerIdentificationImagePaths() {
    return customerIdentificationImagePaths;
  }

  public void setCustomerIdentificationImagePaths(List<String> customerIdentificationImagePaths) {
    this.customerIdentificationImagePaths = customerIdentificationImagePaths;
  }

  public String getProductMainImagePath() {
    return productMainImagePath;
  }

  public void setProductMainImagePath(String productMainImagePath) {
    this.productMainImagePath = productMainImagePath;
  }

  public DataImageDetailDTO getShopBigMainImageDetailDTO() {
    return shopBigMainImageDetailDTO;
  }

  public void setShopBigMainImageDetailDTO(DataImageDetailDTO shopBigMainImageDetailDTO) {
    this.shopBigMainImageDetailDTO = shopBigMainImageDetailDTO;
  }

  public DataImageDetailDTO getShopSmallMainImageDetailDTO() {
    return shopSmallMainImageDetailDTO;
  }

  public void setShopSmallMainImageDetailDTO(DataImageDetailDTO shopSmallMainImageDetailDTO) {
    this.shopSmallMainImageDetailDTO = shopSmallMainImageDetailDTO;
  }

  public DataImageDetailDTO getShopRQImageDetailDTO() {
    return shopRQImageDetailDTO;
  }

  public void setShopRQImageDetailDTO(DataImageDetailDTO shopRQImageDetailDTO) {
    this.shopRQImageDetailDTO = shopRQImageDetailDTO;
  }

  public List<String> getShopImagePaths() {
    return shopImagePaths;
  }

  public void setShopImagePaths(List<String> shopImagePaths) {
    this.shopImagePaths = shopImagePaths;
  }

  public String getShopBusinessLicenseImagePath() {
    return shopBusinessLicenseImagePath;
  }

  public void setShopBusinessLicenseImagePath(String shopBusinessLicenseImagePath) {
    this.shopBusinessLicenseImagePath = shopBusinessLicenseImagePath;
  }

  public DataImageDetailDTO getProductListSmallImageDetailDTO() {
    return productListSmallImageDetailDTO;
  }

  public void setProductListSmallImageDetailDTO(DataImageDetailDTO productListSmallImageDetailDTO) {
    this.productListSmallImageDetailDTO = productListSmallImageDetailDTO;
  }

  public DataImageDetailDTO getProductListBigImageDetailDTO() {
    return productListBigImageDetailDTO;
  }

  public void setProductListBigImageDetailDTO(DataImageDetailDTO productListBigImageDetailDTO) {
    this.productListBigImageDetailDTO = productListBigImageDetailDTO;
  }

  public List<DataImageDetailDTO> getProductInfoBigImageDetailDTOs() {
    return productInfoBigImageDetailDTOs;
  }

  public void setProductInfoBigImageDetailDTOs(List<DataImageDetailDTO> productInfoBigImageDetailDTOs) {
    this.productInfoBigImageDetailDTOs = productInfoBigImageDetailDTOs;
  }

  public List<DataImageDetailDTO> getProductInfoSmallImageDetailDTOs() {
    return productInfoSmallImageDetailDTOs;
  }

  public void setProductInfoSmallImageDetailDTOs(List<DataImageDetailDTO> productInfoSmallImageDetailDTOs) {
    this.productInfoSmallImageDetailDTOs = productInfoSmallImageDetailDTOs;
  }

  public List<DataImageDetailDTO> getProductInfoDescriptionImageDetailDTOs() {
    return productInfoDescriptionImageDetailDTOs;
  }

  public void setProductInfoDescriptionImageDetailDTOs(List<DataImageDetailDTO> productInfoDescriptionImageDetailDTOs) {
    this.productInfoDescriptionImageDetailDTOs = productInfoDescriptionImageDetailDTOs;
  }

  public DataImageDetailDTO getShopBusinessLicenseImageDetailDTO() {
    return shopBusinessLicenseImageDetailDTO;
  }

  public void setShopBusinessLicenseImageDetailDTO(DataImageDetailDTO shopBusinessLicenseImageDetailDTO) {
    this.shopBusinessLicenseImageDetailDTO = shopBusinessLicenseImageDetailDTO;
  }

  public List<DataImageDetailDTO> getShopBigImageDetailDTOs() {
    return shopBigImageDetailDTOs;
  }

  public void setShopBigImageDetailDTOs(List<DataImageDetailDTO> shopBigImageDetailDTOs) {
    this.shopBigImageDetailDTOs = shopBigImageDetailDTOs;
  }

  public List<DataImageDetailDTO> getShopSmallImageDetailDTOs() {
    return shopSmallImageDetailDTOs;
  }

  public void setShopSmallImageDetailDTOs(List<DataImageDetailDTO> shopSmallImageDetailDTOs) {
    this.shopSmallImageDetailDTOs = shopSmallImageDetailDTOs;
  }

  public List<DataImageDetailDTO> getShopImageDetailDTOs() {
    return shopImageDetailDTOs;
  }

  public void setShopImageDetailDTOs(List<DataImageDetailDTO> shopImageDetailDTOs) {
    this.shopImageDetailDTOs = shopImageDetailDTOs;
  }

  public DataImageDetailDTO getRecommendProductListImageDetailDTO() {
    return recommendProductListImageDetailDTO;
  }

  public void setRecommendProductListImageDetailDTO(DataImageDetailDTO recommendProductListImageDetailDTO) {
    this.recommendProductListImageDetailDTO = recommendProductListImageDetailDTO;
  }

  public List<String> getProductInfoImagePaths() {
    return productInfoImagePaths;
  }

  public void setProductInfoImagePaths(List<String> productInfoImagePaths) {
    this.productInfoImagePaths = productInfoImagePaths;
  }

  public List<DataImageDetailDTO> getShopUploadImageDetailDTOs() {
    return shopUploadImageDetailDTOs;
  }

  public void setShopUploadImageDetailDTOs(List<DataImageDetailDTO> shopUploadImageDetailDTOs) {
    this.shopUploadImageDetailDTOs = shopUploadImageDetailDTOs;
  }

  public List<DataImageDetailDTO> getAppUserBillSmallImageDetailDTOs() {
    return appUserBillSmallImageDetailDTOs;
  }

  public void setAppUserBillSmallImageDetailDTOs(List<DataImageDetailDTO> appUserBillSmallImageDetailDTOs) {
    this.appUserBillSmallImageDetailDTOs = appUserBillSmallImageDetailDTOs;
  }

  public List<DataImageDetailDTO> getAppUserBillBigImageDetailDTOs() {
    return appUserBillBigImageDetailDTOs;
  }

  public void setAppUserBillBigImageDetailDTOs(List<DataImageDetailDTO> appUserBillBigImageDetailDTOs) {
    this.appUserBillBigImageDetailDTOs = appUserBillBigImageDetailDTOs;
  }

  public List<DataImageDetailDTO> getShopAdvertImageDetailDTOs() {
    return shopAdvertImageDetailDTOs;
  }

  public void setShopAdvertImageDetailDTOs(List<DataImageDetailDTO> shopAdvertImageDetailDTOs) {
    this.shopAdvertImageDetailDTOs = shopAdvertImageDetailDTOs;
  }
}
