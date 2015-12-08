package com.bcgogo.utils;

import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.txn.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-15
 * Time: 上午10:13
 * To change this template use File | Settings | File Templates.
 */
public class PromotionsUtils {
  private static final Logger LOG = LoggerFactory.getLogger(PromotionsUtils.class);

  //查询product 是否包含促销
  public final static String ADD_PROMOTIONS_PRODUCT_CURRENT = "add_promotions_product_current";//不过滤当前的
  public final static String ADD_PROMOTIONS_PRODUCT = "add_promotions_product";
  public final static String PRODUCT_IN_PROMOTIONS = "product_in_promotions";
  public final static String PROMOTIONS_PRODUCT = "promotions_product";//查询页面

  private final static String SEPARATOR_SEMICOLONS = "；"; // 促销每个单位的分隔符
  private final static String SEPARATOR_CONTENT_MIDDLE = "|"; // 每个促销 时间和实际内容的分隔符
  private final static String SEPARATOR_CONTENT = "_";// 促销之间的分隔符

  //可以参加全场促销的类型
  public static PromotionsEnum.PromotionsTypes[] getAllRangePromotionsType() {
    PromotionsEnum.PromotionsTypes[] types= {
      PromotionsEnum.PromotionsTypes.MLJ,
      PromotionsEnum.PromotionsTypes.MJS,
      PromotionsEnum.PromotionsTypes.FREE_SHIPPING
    };

    return types;
  }

  public static Map<String,ProductDTO> covertToProductPromotions(List<ProductDTO> productDTOs){
    Map<String,ProductDTO> pMap=new HashMap<String, ProductDTO>();
    if(CollectionUtil.isEmpty(productDTOs)){
      return pMap;
    }
    for(ProductDTO productDTO:productDTOs){
      setPromotionsProductToProductDTO(productDTO);
      pMap.put(productDTO.getProductLocalInfoIdStr(),productDTO);
    }
    return pMap;
  }

  public static void setPromotionsProductToProductDTO(ProductDTO productDTO) {
    List<PromotionsProductDTO> promotionsProductDTOs=new ArrayList<PromotionsProductDTO>();
    productDTO.setPromotionsProductDTOList(promotionsProductDTOs);
    List<PromotionsDTO> promotionsDTOs=productDTO.getPromotionsDTOs();
    if(CollectionUtil.isNotEmpty(promotionsDTOs)){
      for(PromotionsDTO promotionsDTO:promotionsDTOs){
        PromotionsProductDTO[] promotionsProductDTOList=  promotionsDTO.getPromotionsProductDTOList();
        if(ArrayUtil.isNotEmpty(promotionsProductDTOList)){
          for(PromotionsProductDTO promotionsProductDTO:promotionsProductDTOList){
            if(productDTO.getProductLocalInfoIdStr().equals(promotionsProductDTO.getProductLocalInfoIdStr())){
              promotionsProductDTOs.add(promotionsProductDTO);
            }
          }
        }
      }
    }
  }

  public static List<PromotionsRuleMJSDTO> getMJSGift(PromotionsEnum.PromotionsLimiter limiter, List<PromotionsRuleDTO> ruleDTOs, double total, double amount){
    if(CollectionUtil.isEmpty(ruleDTOs)){
      return null;
    }
    Collections.sort(ruleDTOs, PromotionsRuleDTO.SORT_BY_LEVEL);
    Collections.reverse(ruleDTOs);
    for(PromotionsRuleDTO ruleDTO : ruleDTOs){
      if(ruleDTO==null) continue;
      Double minAmount=NumberUtil.doubleVal(ruleDTO.getMinAmount());
      List<PromotionsRuleMJSDTO> mjsdtos=ruleDTO.getPromotionsRuleMJSDTOs();
      //over money
      if(PromotionsEnum.PromotionsLimiter.OVER_MONEY.equals(limiter)&&(total-minAmount>-0.001)){
        return mjsdtos;
      }else if(PromotionsEnum.PromotionsLimiter.OVER_AMOUNT.equals(limiter)&&(amount-minAmount>-0.001)){  //over amount
        return mjsdtos;
      }
    }
    return null;
  }

  public static Boolean satisfyFreeShipping(PromotionsEnum.PromotionsLimiter limiter, List<PromotionsRuleDTO> ruleDTOs, double total, double amount){
    if(CollectionUtil.isEmpty(ruleDTOs)){
      return false;
    }
    Collections.sort(ruleDTOs, PromotionsRuleDTO.SORT_BY_LEVEL);
    Collections.reverse(ruleDTOs);
    for(PromotionsRuleDTO ruleDTO : ruleDTOs){
      if(ruleDTO==null) continue;
      Double minAmount=NumberUtil.doubleVal(ruleDTO.getMinAmount());
      //over money
      if(PromotionsEnum.PromotionsLimiter.OVER_MONEY.equals(limiter)&&(total-minAmount>-0.001)){
        return true;
      }
      //over amount
      if(PromotionsEnum.PromotionsLimiter.OVER_AMOUNT.equals(limiter)&&(amount-minAmount>-0.001)){
        return true;
      }
    }
    return false;
  }

  public static PromotionOrderRecordDTO calculateBargainPrice(ProductDTO productDTO,Double oldPrice,Double amount){
    List<PromotionsDTO> promotionsDTOs=productDTO.getPromotionsDTOs();
    if(CollectionUtil.isEmpty(promotionsDTOs)||oldPrice==null||amount==null){
      return null;
    }
    PromotionOrderRecordDTO recordDTO=new PromotionOrderRecordDTO();
    recordDTO.setNewPrice(oldPrice);
    for(PromotionsDTO promotionsDTO:promotionsDTOs){
      if(promotionsDTO==null|| !PromotionsEnum.PromotionsTypes.BARGAIN.equals(promotionsDTO.getType())){
        continue;
      }
      PromotionsEnum.PromotionsTypes type=promotionsDTO.getType();
      PromotionsProductDTO[] promotionsProductDTOs= promotionsDTO.getPromotionsProductDTOList();
      if(ArrayUtil.isEmpty(promotionsProductDTOs)){
        return recordDTO;
      }
      PromotionsProductDTO promotionsProductDTO=promotionsProductDTOs[0];
      PromotionsEnum.BargainType bargainType=promotionsProductDTO.getBargainType();
      double discountAmount=NumberUtil.doubleVal(promotionsProductDTO.getDiscountAmount());
      if(PromotionsEnum.BargainType.BARGAIN.equals(bargainType)){
        if(discountAmount<=0||oldPrice<discountAmount){         //特价的商品不能比原价还贵
          return recordDTO;
        }
        recordDTO.setPromotionsId(promotionsDTO.getId());
        recordDTO.setNewPrice(discountAmount);
      }else if(PromotionsEnum.BargainType.DISCOUNT.equals(bargainType)){
        if(discountAmount<=0||discountAmount>=10){
          return recordDTO;
        }
        recordDTO.setPromotionsId(promotionsDTO.getId());
        recordDTO.setNewPrice(NumberUtil.round(oldPrice *discountAmount/ 10, NumberUtil.MONEY_PRECISION));
      }
    }
    return recordDTO;
  }

  /**
   * 计算特价
   *
   * @param promotionsDTOs
   * @param oldPrice
   * @return
   */
  public static Double calculateBargainPrice(List<PromotionsDTO> promotionsDTOs, Double oldPrice) {
    if (CollectionUtil.isEmpty(promotionsDTOs) || oldPrice == null) {
      return null;
    }
    for (PromotionsDTO promotionsDTO : promotionsDTOs) {
      if (!PromotionsEnum.PromotionsTypes.BARGAIN.equals(promotionsDTO.getType())) {
        continue;
      }
      PromotionsProductDTO[] promotionsProductDTOs = promotionsDTO.getPromotionsProductDTOList();
      if (ArrayUtil.isEmpty(promotionsProductDTOs)) {
        return oldPrice;
      }
      PromotionsProductDTO promotionsProductDTO = promotionsProductDTOs[0];
      PromotionsEnum.BargainType bargainType = promotionsProductDTO.getBargainType();
      double discountAmount = NumberUtil.doubleVal(promotionsProductDTO.getDiscountAmount());
      if (PromotionsEnum.BargainType.BARGAIN.equals(bargainType)) {
        if (discountAmount <= 0 || oldPrice < discountAmount) {
          return oldPrice;
        }
        return discountAmount;
      } else if (PromotionsEnum.BargainType.DISCOUNT.equals(bargainType)) {
        if (discountAmount <= 0 || discountAmount >= 10) {
          return oldPrice;
        }
        return NumberUtil.round(oldPrice * discountAmount / 10, NumberUtil.MONEY_PRECISION);
      }
    }
    return oldPrice;
  }

  /**
   * 生成促销title
   * @param promotionsDTOs
   * @return string[0]是简写，string[1]是促销全称
   */
  public static String[] genPromotionTypesStr(List<PromotionsDTO> promotionsDTOs) {
    String[] result = new String[2];
    if (CollectionUtil.isEmpty(promotionsDTOs))
      return result;

    if (promotionsDTOs.size() == 1) {
      result[0] = promotionsDTOs.get(0).getTypeStr();
      result[1] = result[0];
    } else {
      StringBuilder promotionTypesShortStr = new StringBuilder("");
      StringBuilder promotionTypesStr = new StringBuilder("");
      List<PromotionsEnum.PromotionsTypes> promotionsTypes = new ArrayList<PromotionsEnum.PromotionsTypes>();
      for (PromotionsDTO promotionsDTO : promotionsDTOs) {
        promotionsTypes.add(promotionsDTO.getType());
      }
      if (promotionsTypes.contains(PromotionsEnum.PromotionsTypes.FREE_SHIPPING)){
        promotionTypesShortStr.append(PromotionsEnum.PromotionsTypesShort.getPromotionsTypesShortByCode(PromotionsEnum.PromotionsTypes.FREE_SHIPPING.toString()).getName()).append("+");
      }
      for (PromotionsEnum.PromotionsTypes promotionsTypesTemp : promotionsTypes) {
        if (promotionsTypesTemp != PromotionsEnum.PromotionsTypes.FREE_SHIPPING) {
          promotionTypesShortStr.append(PromotionsEnum.PromotionsTypesShort.getPromotionsTypesShortByCode(promotionsTypesTemp.toString()).getName()).append("+");
        }
        promotionTypesStr.append(promotionsTypesTemp.getName()).append("+");
      }
      result[0] = promotionTypesShortStr.substring(0, promotionTypesShortStr.length()-1);
      result[1] = promotionTypesStr.substring(0, promotionTypesStr.length()-1);
    }
    return result;
  }

  public static String genPromotionShortTitle(List<PromotionsDTO> promotionsDTOs){
    String[] result=genPromotionTypesStr(promotionsDTOs);
    return result[0];
  }

  /**
   * 满立减- 单种促销是“满金额减XX”的情况时，合并计算所有适用于此促销的商品优惠
   * @param mljItems
   * @param limiter
   * @param ruleDTOs
   * @param total
   * @param amount
   * @return 满足此种促销的所有商品总优惠额
   */
  public static double calculateMLJOrderPrice(List<PurchaseOrderItemDTO> mljItems, PromotionsEnum.PromotionsLimiter limiter, List<PromotionsRuleDTO> ruleDTOs, double total, double amount){
    double pTotal=0;
    if(CollectionUtil.isEmpty(ruleDTOs)){
      return pTotal;
    }
    Collections.sort(ruleDTOs, PromotionsRuleDTO.SORT_BY_LEVEL);
    Collections.reverse(ruleDTOs);
    for(PromotionsRuleDTO ruleDTO : ruleDTOs){
      if(ruleDTO==null) continue;
      Double minAmount=NumberUtil.doubleVal(ruleDTO.getMinAmount());
      Double discountAmount=NumberUtil.doubleVal(ruleDTO.getDiscountAmount());
      PromotionsEnum.PromotionsRuleType ruleType=ruleDTO.getPromotionsRuleType();
      //over money
      if(PromotionsEnum.PromotionsLimiter.OVER_MONEY.equals(limiter)&&(total-minAmount>-0.001)){
        if(PromotionsEnum.PromotionsRuleType.DISCOUNT_FOR_OVER_MONEY.equals(ruleType)&&total>0){
          double oldPrice=NumberUtil.doubleVal(total);
          double newPrice= NumberUtil.round(total * discountAmount/ 10, NumberUtil.MONEY_PRECISION);
          pTotal=NumberUtil.round(oldPrice-newPrice);
          break;                 //满足最高 level 要 break。防止重复计算
        }else if(PromotionsEnum.PromotionsRuleType.REDUCE_FOR_OVER_MONEY.equals(ruleType)&&total>0){
          pTotal=discountAmount;
          break;
        }
      }
    }
    return pTotal;
  }

  /**
   * 返回单条的减后总额
   * @param limiter
   * @param ruleDTOs
   * @param totalPrice 优惠前单条总额
   * @param amount
   * @return
   */
  public static Double calculateMLJPrice(PromotionsEnum.PromotionsLimiter limiter, List<PromotionsRuleDTO> ruleDTOs, double totalPrice, double amount){
    double newPrice=totalPrice;
    if(CollectionUtil.isEmpty(ruleDTOs)){
      return newPrice;
    }
    Collections.sort(ruleDTOs, PromotionsRuleDTO.SORT_BY_LEVEL);
    Collections.reverse(ruleDTOs);
    for(PromotionsRuleDTO ruleDTO : ruleDTOs){
      if(ruleDTO==null) continue;
      Double minAmount=NumberUtil.doubleVal(ruleDTO.getMinAmount());
      Double discountAmount=NumberUtil.doubleVal(ruleDTO.getDiscountAmount());
      PromotionsEnum.PromotionsRuleType ruleType=ruleDTO.getPromotionsRuleType();
      //over money
      if(PromotionsEnum.PromotionsLimiter.OVER_MONEY.equals(limiter)&&(totalPrice-minAmount>-0.001)){
        if(PromotionsEnum.PromotionsRuleType.DISCOUNT_FOR_OVER_MONEY.equals(ruleType)&&totalPrice>0){
          newPrice=NumberUtil.round(totalPrice * ruleDTO.getDiscountAmount() / 10, NumberUtil.MONEY_PRECISION);
          break;                 //满足最高 level 要 break。防止重复计算
        }else if(PromotionsEnum.PromotionsRuleType.REDUCE_FOR_OVER_MONEY.equals(ruleType)&&totalPrice>0){
          newPrice=NumberUtil.round(totalPrice-ruleDTO.getDiscountAmount(),NumberUtil.MONEY_PRECISION);
          break;
        }
      }
      //over amount
      if(PromotionsEnum.PromotionsLimiter.OVER_AMOUNT.equals(limiter)&&(amount-minAmount>-0.001)){
        if(PromotionsEnum.PromotionsRuleType.DISCOUNT_FOR_OVER_AMOUNT.equals(ruleType)&&amount>0){
          newPrice=NumberUtil.round(totalPrice * ruleDTO.getDiscountAmount() / 10, NumberUtil.MONEY_PRECISION);
          break;
        }else if(PromotionsEnum.PromotionsRuleType.REDUCE_FOR_OVER_AMOUNT.equals(ruleType)&&amount>0){
          newPrice=NumberUtil.round(totalPrice-ruleDTO.getDiscountAmount(), NumberUtil.MONEY_PRECISION);
          break;
        }
      }
    }
    return newPrice;
  }



  public static List<PromotionsRuleDTO>  getPromotionsRule(List<PurchaseOrderItemDTO> pItemDTOs,PromotionsEnum.PromotionsTypes types, Map<Long,ProductDTO> pMap){
    PromotionsDTO promotionsDTO= getPromotions(pItemDTOs, types, pMap);
    if(promotionsDTO==null) {
      return null;
    }
    return promotionsDTO.getPromotionsRuleDTOList();
  }

  public static PromotionsDTO getPromotions(List<PurchaseOrderItemDTO> pItemDTOs,PromotionsEnum.PromotionsTypes types, Map<Long,ProductDTO> pMap){
    PurchaseOrderItemDTO itemDTO=CollectionUtil.getFirst(pItemDTOs);
    ProductDTO productDTO = pMap.get(itemDTO.getSupplierProductId());
    List<PromotionsDTO> promotionsDTOs=productDTO.getPromotionsDTOs();
    PromotionsDTO promotions=null;
    for(PromotionsDTO promotionsDTO:promotionsDTOs){
      if(types.equals(promotionsDTO.getType())){
        promotions=promotionsDTO;
      }
    }
    return promotions;
  }

  public static PromotionsDTO getPromotionsDTO(ProductDTO productDTO,PromotionsEnum.PromotionsTypes type){
    if(productDTO==null) return null;
    List<PromotionsDTO> promotionsDTOs= productDTO.getPromotionsDTOs();
    if(CollectionUtil.isEmpty(promotionsDTOs)){
      return null;
    }
    for(PromotionsDTO promotionsDTO:promotionsDTOs){
      if(type.equals(promotionsDTO.getType())){
        if(NumberUtil.isNumber(promotionsDTO.getIdStr())){
          promotionsDTO.setId(Long.parseLong(promotionsDTO.getIdStr()));
        }
        return promotionsDTO;
      }
    }
    return null;
  }

  public static List<PromotionsDTO> generatePromotionsFromRecord( List<PromotionOrderRecordDTO> recordDTOs){
    if(CollectionUtil.isEmpty(recordDTOs)){
      return null;
    }
    List<PromotionsDTO> promotionsDTOs=new ArrayList<PromotionsDTO>();
    for(PromotionOrderRecordDTO recordDTO:recordDTOs){
      promotionsDTOs.add((PromotionsDTO)JsonUtil.jsonToObject(recordDTO.getPromotionsJson(),PromotionsDTO.class));
    }
    return promotionsDTOs;
  }


  public static PromotionsEnum.PromotionsLimiter getPromotionsLimiter(List<PurchaseOrderItemDTO> pItemDTOs,PromotionsEnum.PromotionsTypes types, Map<Long,ProductDTO> pMap){
    PromotionsDTO promotionsDTO= getPromotions(pItemDTOs,types,pMap);
    if(promotionsDTO==null) {
      return null;
    }
    return promotionsDTO.getPromotionsLimiter();
  }

  public static Set<Long> generatePromotionsIds(ProductDTO productDTO){
    Set<Long> promotionsIdSet=new HashSet<Long>();
    if(productDTO==null){
      return promotionsIdSet;
    }
    List<PromotionsDTO> promotionsDTOs=productDTO.getPromotionsDTOs();
    if(CollectionUtil.isNotEmpty(promotionsDTOs)){
      for (PromotionsDTO promotionsDTO:promotionsDTOs){
        if(promotionsDTO==null) continue;
        promotionsIdSet.add(promotionsDTO.getId());
      }
    }
    return promotionsIdSet;
  }

  public static Boolean hasPromotionsWithPriceFlag(ProductDTO productDTO, PurchaseOrderItemDTO itemDTO, PromotionsEnum.PromotionsTypes type){
    if(productDTO==null||type==null) return false;
    List<PromotionsDTO> promotionsDTOs=productDTO.getPromotionsDTOs();
    if(CollectionUtil.isEmpty(promotionsDTOs) || itemDTO.getCustomPriceFlag()){
      return false;
    }
    for(PromotionsDTO promotionsDTO:promotionsDTOs){
      if(type.equals(promotionsDTO.getType())){
        return true;
      }
    }
    return false;
  }

  public static Boolean  hasPromotions(ProductDTO productDTO, PurchaseOrderItemDTO itemDTO, PromotionsEnum.PromotionsTypes type){
    if(productDTO==null||type==null) return false;
    List<PromotionsDTO> promotionsDTOs=productDTO.getPromotionsDTOs();
    if(CollectionUtil.isEmpty(promotionsDTOs) || itemDTO.getCustomPriceFlag()){
      return false;
    }
    for(PromotionsDTO promotionsDTO:promotionsDTOs){
      if(type.equals(promotionsDTO.getType())){
        return true;
      }
    }
    return false;
  }

  public static Boolean hasBargain(List<PromotionsDTO> promotionsDTOs){
    if(CollectionUtil.isEmpty(promotionsDTOs)) return false;
    for(PromotionsDTO promotionsDTO:promotionsDTOs){
      if(PromotionsEnum.PromotionsTypes.BARGAIN.equals(promotionsDTO.getType())){
        return true;
      }
    }
    return false;
  }

  private static String generateMLJContent(PromotionsDTO promotionsDTO){
    if(promotionsDTO==null||!PromotionsEnum.PromotionsTypes.MLJ.equals(promotionsDTO.getType())){
      return "";
    }
    List<PromotionsRuleDTO> ruleDTOs=promotionsDTO.getPromotionsRuleDTOList();
    if(CollectionUtil.isEmpty(ruleDTOs)){
      return "";
    }
    StringBuffer sb=new StringBuffer();
    if(PromotionsEnum.PromotionsLimiter.OVER_AMOUNT.equals(promotionsDTO.getPromotionsLimiter())){
      sb.append("每件商品");
    }else if(PromotionsEnum.PromotionsLimiter.OVER_MONEY.equals(promotionsDTO.getPromotionsLimiter())){
      sb.append("单笔金额");
    }
    for(PromotionsRuleDTO ruleDTO:ruleDTOs){
      Double minAmount=ruleDTO.getMinAmount();
      Double discountAmount=ruleDTO.getDiscountAmount();
      PromotionsEnum.PromotionsRuleType ruleType=ruleDTO.getPromotionsRuleType();
      if(PromotionsEnum.PromotionsRuleType.DISCOUNT_FOR_OVER_MONEY.equals(ruleType)){
        sb.append("满").append(minAmount).append("元，打").append(discountAmount).append("折");
        sb.append(SEPARATOR_SEMICOLONS);
      }else if(PromotionsEnum.PromotionsRuleType.DISCOUNT_FOR_OVER_AMOUNT.equals(ruleType)){
        sb.append("满").append(minAmount).append("件，打").append(discountAmount).append("折");
        sb.append(SEPARATOR_SEMICOLONS);
      }else if(PromotionsEnum.PromotionsRuleType.REDUCE_FOR_OVER_MONEY.equals(ruleType)){
        sb.append("满").append(minAmount).append("元，减").append(discountAmount).append("元");
        sb.append(SEPARATOR_SEMICOLONS);
      }else if(PromotionsEnum.PromotionsRuleType.REDUCE_FOR_OVER_AMOUNT.equals(ruleType)){
        sb.append("满").append(minAmount).append("件，减").append(discountAmount).append("元");
        sb.append(SEPARATOR_SEMICOLONS);
      }
    }
    return sb.toString();
  }

  private static String generateMJSContent(PromotionsDTO promotionsDTO){
    if(promotionsDTO==null||!PromotionsEnum.PromotionsTypes.MJS.equals(promotionsDTO.getType())){
      return "";
    }
    List<PromotionsRuleDTO> ruleDTOs=promotionsDTO.getPromotionsRuleDTOList();
    if(CollectionUtil.isEmpty(ruleDTOs)){
      return "";
    }
    StringBuffer sb=new StringBuffer();
    sb.append("单个商品");
    for(PromotionsRuleDTO ruleDTO:ruleDTOs){
      Double minAmount=ruleDTO.getMinAmount();
      List<PromotionsRuleMJSDTO> ruleMJSDTOs=ruleDTO.getPromotionsRuleMJSDTOs();
      String temp="";
      if(CollectionUtil.isEmpty(ruleMJSDTOs)){
        continue;
      }
      if(PromotionsEnum.PromotionsLimiter.OVER_AMOUNT.equals(promotionsDTO.getPromotionsLimiter())){
        sb.append("满"+minAmount+"件,");
      }else if(PromotionsEnum.PromotionsLimiter.OVER_MONEY.equals(promotionsDTO.getPromotionsLimiter())){
        sb.append("满"+minAmount+"元,");
      }
      for(PromotionsRuleMJSDTO ruleMJSDTO:ruleMJSDTOs){
        Double amount=ruleMJSDTO.getAmount();
        sb.append("送").append(ruleMJSDTO.getGiftName());
        if(NumberUtil.round(amount)>0){
          sb.append(" ").append(amount).append(" 件 ");
        }
      }
      sb.append(SEPARATOR_SEMICOLONS);
    }
    return sb.toString();
  }

  private static String generateFreeShippingContent(PromotionsDTO promotionsDTO){
    if(promotionsDTO==null||!PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(promotionsDTO.getType())){
      return "";
    }
    List<PromotionsRuleDTO> ruleDTOs=promotionsDTO.getPromotionsRuleDTOList();
    if(CollectionUtil.isEmpty(ruleDTOs)){
      return "";
    }
    StringBuilder sb=new StringBuilder();
    PromotionsEnum.PromotionsLimiter limiter=promotionsDTO.getPromotionsLimiter();
    if(limiter==null){
      sb.append("送货上门");
    }else{
      PromotionsRuleDTO ruleDTO=CollectionUtil.getFirst(promotionsDTO.getPromotionsRuleDTOList());
      Double minAmount=ruleDTO.getMinAmount();
      if(PromotionsEnum.PromotionsLimiter.OVER_MONEY.equals(limiter)){
        if(NumberUtil.round(minAmount)>0){
          sb.append("满").append(minAmount).append("元，");
        }
        sb.append("送货上门");
      }else if(PromotionsEnum.PromotionsLimiter.OVER_AMOUNT.equals(limiter)){
        if(NumberUtil.round(minAmount)>0){
          sb.append("满").append(minAmount).append("件，");
        }
        sb.append("送货上门");
      }
    }
    sb.append(SEPARATOR_SEMICOLONS);
    return  sb.toString();
  }

  private static String generateBargainContent(PromotionsDTO promotionsDTO,Double inSalesPrice){
    if(promotionsDTO==null||!PromotionsEnum.PromotionsTypes.BARGAIN.equals(promotionsDTO.getType())){
      return "";
    }
    PromotionsProductDTO bargainProduct=ArrayUtil.getFirst(promotionsDTO.getPromotionsProductDTOList());
    if(bargainProduct==null){
      return "";
    }
    StringBuilder sb=new StringBuilder();
    Double discountAmount=NumberUtil.round(bargainProduct.getDiscountAmount());
    sb.append("特价商品,");
    if(PromotionsEnum.BargainType.BARGAIN.equals(bargainProduct.getBargainType())){
      sb.append("省").append(NumberUtil.subtraction(inSalesPrice - discountAmount)).append("元");
    }else  if(PromotionsEnum.BargainType.DISCOUNT.equals(bargainProduct.getBargainType())){
      sb.append("打").append(discountAmount).append("折");
    }
    Double limitAmount=NumberUtil.round(bargainProduct.getLimitAmount());
    if(limitAmount>0){
      sb.append("，每位限购").append(limitAmount).append("件");
    }
    sb.append(SEPARATOR_SEMICOLONS);
    return sb.toString();
  }

  public static String generatePromotionsContent(List<PromotionsDTO> promotionsDTOs){
    if(CollectionUtil.isEmpty(promotionsDTOs)){
      return "";
    }
    StringBuilder sb=new StringBuilder();
    for(PromotionsDTO promotionsDTO:promotionsDTOs){
      if(PromotionsEnum.PromotionsTypes.MLJ.equals(promotionsDTO.getType())){
        sb.append(generateMLJContent(promotionsDTO));
      }else  if(PromotionsEnum.PromotionsTypes.MJS.equals(promotionsDTO.getType())){
        sb.append(generateMJSContent(promotionsDTO));
      }else  if(PromotionsEnum.PromotionsTypes.FREE_SHIPPING.equals(promotionsDTO.getType())){
        sb.append(generateFreeShippingContent(promotionsDTO));
      }else  if(PromotionsEnum.PromotionsTypes.BARGAIN.equals(promotionsDTO.getType())){
        sb.append(generateBargainContent(promotionsDTO, promotionsDTO.getInSalesPrice()));
      }
      sb.append(SEPARATOR_CONTENT_MIDDLE);
      sb.append("活动时间：").append(promotionsDTO.getStartTimeStr());
      if(StringUtil.isEmpty(promotionsDTO.getEndTimeStr())){
        sb.append(",不限期");
      }else{
        sb.append("至").append(promotionsDTO.getEndTimeStr());
      }
      sb.append(";");
      sb.append(SEPARATOR_CONTENT);
    }
    return sb.toString();
  }

  public static String generatePromotionsContent(ProductDTO productDTO){
    List<PromotionsDTO> promotionsDTOs=productDTO.getPromotionsDTOs();
    if(CollectionUtil.isEmpty(promotionsDTOs)){
      return "";
    }
    for(PromotionsDTO promotionsDTO:promotionsDTOs){
      promotionsDTO.setInSalesPrice(productDTO.getInSalesPrice());
    }
    return generatePromotionsContent(promotionsDTOs);
  }

  public static String generatePromotionsContent(ProductHistoryDTO productHistoryDTO){
    List<PromotionOrderRecordDTO> recordDTOs=productHistoryDTO.getPromotionOrderRecordDTOs();
    if(CollectionUtil.isEmpty(recordDTOs)){
      return "";
    }
    List<PromotionsDTO> promotionsDTOs=generatePromotionsFromRecord(recordDTOs);
    if(CollectionUtil.isEmpty(promotionsDTOs)){
      return "";
    }
    for(PromotionsDTO promotionsDTO:promotionsDTOs){
      promotionsDTO.setInSalesPrice(productHistoryDTO.getInSalesPrice());
    }
    return generatePromotionsContent(promotionsDTOs);
  }



}
