package com.bcgogo.search.dto;

/**
 * User: ZhangJuntao
 * Date: 12-5-11
 * Time: 上午9:15
 * 搜索条件DTO
 */
public class SearchMemoryConditionDTO {
  public final static String PRODUCT_INFO = "product_info";
  public final static String PRODUCT_NAME = "product_name";
  public final static String PRODUCT_BRAND = "product_brand";
  private String searchWord;
  private String searchField;
  private String productName;
  private Long shopId;
  private String uuid;

  public SearchMemoryConditionDTO() {

  }
  public SearchMemoryConditionDTO(OrderSearchConditionDTO searchConditionDTO) {
    this.setProductName(searchConditionDTO.getProductName());
    this.setSearchField(searchConditionDTO.getSearchField());
    this.setSearchWord(searchConditionDTO.getSearchWord());
    this.setShopId(searchConditionDTO.getShopId());
    this.setUuid(searchConditionDTO.getUuid());
  }
  public SearchMemoryConditionDTO(SearchConditionDTO searchConditionDTO) {
    this.setProductName(searchConditionDTO.getProductName());
    this.setSearchField(searchConditionDTO.getSearchField());
    this.setSearchWord(searchConditionDTO.getSearchWord());
    this.setShopId(searchConditionDTO.getShopId());
    this.setUuid(searchConditionDTO.getUuid());
  }


  public boolean searchFieldEquals(String... strings) {
    boolean flag = false;
    if (strings == null || strings.length == 0) return flag;
    for (String str : strings) {
      if (searchField.equals(str)) {
        flag = true;
        break;
      }
    }
    return flag;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getSearchWord() {
    return searchWord;
  }

  public void setSearchWord(String searchWord) {
    this.searchWord = searchWord;
  }



  public String getSearchField() {
    return searchField;
  }

  public void setSearchField(String searchField) {
    this.searchField = searchField;
  }


  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

}
