package com.bcgogo.txn.html;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.Product.RecommendSupplierType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.txn.dto.ActiveRecommendSupplierDTO;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.RepairOrderItemDTO;
import com.bcgogo.txn.service.IActiveRecommendSupplierService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-5-23
 * Time: 下午12:04
 * 供应商主动推荐
 */
@Component
public class ActiveRecommendSupplierHtmlBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(ActiveRecommendSupplierHtmlBuilder.class);
  @Autowired
  protected IActiveRecommendSupplierService activeRecommendSupplierService;

  public void buildActiveRecommendSuppliers(ModelMap model, RepairOrderDTO repairOrderDTO, Long shopVersionId) {
    if (BcgogoShopLogicResourceUtils.isNotActiveRecommendSupplier(shopVersionId)) {
      return;
    }
    RepairOrderItemDTO[] itemDTOs = repairOrderDTO.getItemDTOs();
    try {
      if (ArrayUtils.isEmpty(itemDTOs)) return;
      Map<RecommendSupplierType, ActiveRecommendSupplierDTO> map;
      StringBuilder result;
      StringBuffer productIdsAndAmounts = new StringBuffer();
      int i = 1;
      for (RepairOrderItemDTO itemDTO : itemDTOs) {
        if (itemDTO.getAmount() > (itemDTO.getInventoryAmount() + itemDTO.getReserved())) {
          productIdsAndAmounts.append(itemDTO.getProductId()).append("_").append(itemDTO.getAmount());
          if (i != itemDTOs.length) {
            productIdsAndAmounts.append(",");
          }
          i++;
        }
      }
      for (RepairOrderItemDTO itemDTO : itemDTOs) {
        if (itemDTO.getAmount() > (itemDTO.getInventoryAmount() + itemDTO.getReserved())) {
          map = activeRecommendSupplierService.obtainActiveRecommendSupplierByProductId(itemDTO.getProductId(), shopVersionId, itemDTO.getShopId(), itemDTO.getPurchasePrice(), true);
          result = buildActiveRecommendSupplier(itemDTO.getProductId(), map, repairOrderDTO, productIdsAndAmounts.toString());
          if (result != null && result.length() > 0)
            itemDTO.setActiveRecommendSupplierHtml(result.toString());
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

//  public Map<Long, String> buildActiveRecommendSuppliers(Map<Long, Map<RecommendSupplierType, ActiveRecommendSupplierDTO>> data) throws Exception {
//    Map<Long, String> results = new HashMap<Long, String>();
//    StringBuilder result;
//    for (Map.Entry<Long, Map<RecommendSupplierType, ActiveRecommendSupplierDTO>> set : data.entrySet()) {
//      result = buildActiveRecommendSupplier(set.getKey(), set.getValue());
//      if (result != null && result.length() > 0)
//        results.put(set.getKey(), result.toString());
//    }
//    return results;
//  }

  private StringBuilder buildActiveRecommendSupplier(Long productId, Map<RecommendSupplierType, ActiveRecommendSupplierDTO> data, RepairOrderDTO repairOrderDTO, String productIds) {
    StringBuilder result = new StringBuilder();
    result
        .append("<div class=\"tip\" style=\"margin-left:8px;\">")
        .append("    <div class=\"tipTop\"></div>")
        .append("    <div class=\"tipBody\">")
        .append("       <b class=\"yellow_color tipTittle\">温馨小提示</b>")
        .append("");
    if (data != null) {
      ActiveRecommendSupplierDTO leastConsume = data.get(RecommendSupplierType.LEAST_CONSUME),
          bcgogoRecommend = data.get(RecommendSupplierType.BCGOGO_RECOMMEND);
      //最近消费供应商
      SupplierDTO leastConsumeSupplier = null;
      if (leastConsume != null) {
        leastConsumeSupplier = leastConsume.getSupplierDTO();
        if (leastConsumeSupplier != null) {
          result
              .append("<div class=\"tipLine\">")
              .append("       <span class=\"tipName\" style=\"width:170px; display:inline-block;\">上次采购供应商：")
              .append("<span>").append(leastConsumeSupplier.getName())
              .append("</span></span><a class=\"blue_color buy j_recommend_buy\" action-type=\"repair-purchase\" href=\"RFbuy.do?method=create&supplierId=").append(leastConsumeSupplier.getId()).append("&productIds=").append("\" target=\"_blank\"")
              .append(" supplier_id=\"").append(leastConsumeSupplier.getId()).append("\" product_id=\"").append(productId).append("\">马上采购</a>")
              .append("   </div><div class=\"hr\"></div>");
        }
      }
      //bcgogo推荐供应商
      if (bcgogoRecommend != null) {
        ShopDTO bcgogoRecommendShop = bcgogoRecommend.getShopDTO();
        ProductDTO bcgogoRecommendProduct = bcgogoRecommend.getProductDTO();
        if (bcgogoRecommendProduct != null && bcgogoRecommendShop != null) {
          //推荐供应商与最近使用不是同一个供应商
//          if (leastConsumeSupplier == null || !leastConsumeSupplier.getId().equals(bcgogoRecommendSupplier.getId())) {
          String commodityCode = bcgogoRecommendProduct.getCommodityCode();
          result
              .append(" <div class=\"buyList\">")
              .append("        <div class=\"tipLine  yellow_color\"><b>推荐供应商：").append(bcgogoRecommendShop.getName()).append("</b></div>");
          if (StringUtils.isNotBlank(commodityCode) || StringUtils.isNotBlank(bcgogoRecommendProduct.getName()) || StringUtils.isNotBlank(bcgogoRecommendProduct.getBrand())) {
            result.append("    <div class=\"tipLine\">").append(StringUtils.isBlank(commodityCode) ? "" : (commodityCode + "  ")).append(bcgogoRecommendProduct.getName()).append(StringUtils.isBlank(bcgogoRecommendProduct.getBrand()) ? "" : bcgogoRecommendProduct.getBrand()).append("</div>");
          }
          if (StringUtils.isNotBlank(bcgogoRecommendProduct.getModel()) || StringUtils.isNotBlank(bcgogoRecommendProduct.getSpec())) {
            result.append("    <div class=\"tipLine\">").append(StringUtils.isBlank(bcgogoRecommendProduct.getModel()) ? "" : bcgogoRecommendProduct.getModel() + "   ").append(StringUtils.isBlank(bcgogoRecommendProduct.getSpec()) ? "" : bcgogoRecommendProduct.getSpec()).append("</div>");
          }
          if (StringUtils.isNotBlank(bcgogoRecommendProduct.getVehicleBrand()) || StringUtils.isNotBlank(bcgogoRecommendProduct.getVehicleModel())) {
            result.append("    <div class=\"tipLine\">").append(StringUtils.isBlank(bcgogoRecommendProduct.getVehicleBrand()) ? "" : bcgogoRecommendProduct.getVehicleBrand() + "   ").append(StringUtils.isBlank(bcgogoRecommendProduct.getVehicleModel()) ? "" : bcgogoRecommendProduct.getVehicleModel()).append("</div>");
          }
          result.append("      <div class=\"tipLine\">单价：<b class=\"yellow_color\">¥").append(bcgogoRecommendProduct.getPrice()).append("</b><a style=\"cursor: pointer;\" class=\"blue_color buy\" action-type=\"purchase-recommend-product\" product-id=\"").append(bcgogoRecommendProduct.getProductLocalInfoId()).append("\">马上采购</a></div>")
              .append("</div>");
//          }
        }
      } else {
        //发布求购信息
        result.append("<div class=\"tipLine\"><span>您还可以发布求购信息</span><a class=\"blue_color buy\" href=\"preBuyOrder.do?method=createPreBuyOrderByProductIdInfos&productIdInfos=").append(productIds).append("\" target=\"_blank\">马上发布</a></div>");
      }
    } else {
      //如果result为空
      //发布求购信息
      result.append("<div class=\"tipLine\"><span>您还可以发布求购信息</span><a class=\"blue_color buy\" href=\"preBuyOrder.do?method=createPreBuyOrderByProductIdInfos&productIdInfos=").append(productIds).append("\" target=\"_blank\">马上发布</a></div>");
    }
    result
        .append("  </div>")
        .append("<div class=\"tipBottom\"></div> ")
        .append("</div>");
    return result;
  }

  public void setActiveRecommendSupplierService(IActiveRecommendSupplierService activeRecommendSupplierService) {
    this.activeRecommendSupplierService = activeRecommendSupplierService;
  }
}
