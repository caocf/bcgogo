package com.bcgogo.enums;

import com.bcgogo.utils.ArrayUtil;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-10-16
 * Time: 下午4:44
 * To change this template use File | Settings | File Templates.
 */
public class PromotionsEnum{

  public enum PromotionsTypes {
    NONE("未参加促销"),
    MLJ("满立减"),
    MJS("满就送"),
    BARGAIN("特价商品"),
    FREE_SHIPPING("送货上门"),
    SPECIAL_CUSTOMER("客户优惠");

    private final String name;

    private PromotionsTypes(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    private static Map<String, PromotionsTypes> lookup = new HashMap<String, PromotionsTypes>();
    static{
      for(PromotionsTypes type : PromotionsTypes.values()){
        lookup.put(type.toString(), type);
      }
    }

    public static PromotionsTypes parseToPromotionsTypes(String typeStr) {
      PromotionsTypes type = lookup.get(typeStr);
      if(type == null)
        throw new IllegalArgumentException("PromotionsTypes " + typeStr +"不存在!");
      return type;
    }

    //不包括 SPECIAL_CUSTOMER 和 NONE
    public static String[] generateTypeArray() {
      PromotionsTypes[] types= PromotionsTypes.values();
      if(ArrayUtil.isEmpty(types)){
        return new String[]{};
      }
      String[] typeArray=new String[types.length-1];
      for(int i=0;i<types.length;i++){
        if(types[i].toString().equals("SPECIAL_CUSTOMER")
          ||types[i].toString().equals("NONE")){
          continue;
        }
        typeArray[i]=types[i].toString();
      }
      return typeArray;
    }


  }

  public enum PromotionsTypesShort {

    MLJ("MLJ", "减"),
    MJS("MJS", "送"),
    BARGAIN("BARGAIN", "特价"),
    FREE_SHIPPING("FREE_SHIPPING", "送货");

    private String code;
    private String name;

    private PromotionsTypesShort(String code, String name) {
      this.code = code;
      this.name = name;
    }

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public static PromotionsTypesShort getPromotionsTypesShortByCode(String code){
      if(StringUtils.isBlank(code))
        return null;
      for (PromotionsTypesShort promotionsTypesShort:PromotionsTypesShort.values()){
          if (StringUtils.equals(promotionsTypesShort.getCode(),code))
            return promotionsTypesShort;
      }
      return null;
    }

  }


  public enum PromotionsRanges {
    ALL("全部商品参与"),
    EXCEPT("部分商品不参与"),
    PARTLY("部分商品参与");
    private final String name;

    private PromotionsRanges(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

  }

  public enum PromotionsRuleType{
    DISCOUNT_FOR_OVER_MONEY("满金额打折扣"),
    DISCOUNT_FOR_OVER_AMOUNT("满数量打折扣"),
    REDUCE_FOR_OVER_MONEY("满金额减金额"),
    REDUCE_FOR_OVER_AMOUNT("满数量减金额"),
    GIVE_FOR_OVER_MONEY("满金额送礼物或预付金"),
    GIVE_FOR_OVER_AMOUNT("满数量送礼物或预付金");
    private final String type;
    PromotionsRuleType(String type){
      this.type=type;
    }
  }

  public enum PromotionStatus{
    UN_USED("未使用"),
    UN_STARTED("未开始"),
    USING("进行中"),
    SUSPEND("已暂停"),
    EXPIRE("已结束");
    private final String name;
    PromotionStatus(String name){
      this.name=name;
    }
    public String getName() {
      return name;
    }

    private static Map<String, PromotionStatus> lookup = new HashMap<String, PromotionStatus>();
    static{
      for(PromotionStatus status : PromotionStatus.values()){
        lookup.put(status.toString(), status);
      }
    }

    public static PromotionStatus parseToPromotionStatus(String orderType) {
      PromotionStatus status = lookup.get(orderType);
      if(status == null)
        return PromotionStatus.UN_USED;
      return status;
    }
  }

  public enum PromotionsLimiter {
    OVER_MONEY("满金额"),
    OVER_AMOUNT("满数量");
    private final String name;
    private PromotionsLimiter(String name) {
      this.name = name;
    }
    public String getName() {
      return name;
    }
  }

  public enum GiftType {
    GIFT("送礼物"),
    DEPOSIT("送预付金");
    private final String name;
    private GiftType(String name) {
      this.name = name;
    }
    public String getName() {
      return name;
    }
  }

  public enum PostType {
    POST("包邮"),
    UN_POST("不包邮");
    private final String name;
    private PostType(String name) {
      this.name = name;
    }
    public String getName() {
      return name;
    }
  }

  public enum PromotionsAreaType {
    COUNTRY("全国"),
    PROVINCE("本省"),
    CITY("本市"),
    OTHER("自定义");
    private final String name;
    private PromotionsAreaType(String name) {
      this.name = name;
    }
    public String getName() {
      return name;
    }
  }

   public enum BargainType {
    DISCOUNT("折扣"),
    BARGAIN("特价");
    private final String name;
    private BargainType(String name) {
      this.name = name;
    }
    public String getName() {
      return name;
    }
  }

}

