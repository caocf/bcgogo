package com.bcgogo.admin.product;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.Product.NormalProductStatType;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.ProductModifyFields;
import com.bcgogo.product.ProductCategory.NormalProductStatSearchResult;
import com.bcgogo.product.dto.NormalProductDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.service.INormalProductStatService;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.txn.dto.NormalProductInventoryStatDTO;
import com.bcgogo.txn.model.PurchaseInventoryItem;
import com.bcgogo.txn.model.PurchaseInventoryStat;
import com.bcgogo.txn.model.PurchaseInventoryStatChange;
import com.bcgogo.txn.service.IInventoryService;
import com.bcgogo.txn.service.IPurchaseCostStatService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * CRM->店铺财务统计->当前各个店铺的采购分析专用controller
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-12-29
 * Time: 下午3:59
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/shopProductInventory.do")
public class ShopProductInventoryController {

  private static final Logger LOG = LoggerFactory.getLogger(ShopProductInventoryController.class);


  /**
   * 根据前台查询条件 获得各个店铺的采购分析
   * @param request
   * @param response
   * @param searchConditionDTO
   * @return
   */
  @RequestMapping(params = "method=getShopProductInventoryStatByCondition")
  @ResponseBody
  public Object getNormalProductStatByCondition(HttpServletRequest request, HttpServletResponse response, SearchConditionDTO searchConditionDTO) {

    try {
      IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IShopVersionService shopVersionService = ServiceManager.getService(IShopVersionService.class);
      RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);


      List<Long> shopIdList = null;
      if (StringUtil.isNotEmpty(searchConditionDTO.getShopVersion()) || searchConditionDTO.getProvinceId() != null || searchConditionDTO.getCityId() != null ||
          searchConditionDTO.getRegionId() != null) {
        List<Long> shopVersionIds = new ArrayList<Long>();
        if ("REPAIR_VERSION".equals(searchConditionDTO.getShopVersion())) {
          shopVersionIds.add(10000010017531654L);
          shopVersionIds.add(10000010017531655L);
          shopVersionIds.add(10000010039823882L);
        } else if ("WHOLESALER_VERSION".equals(searchConditionDTO.getShopVersion())) {
          shopVersionIds.add(10000010017531657L);
          shopVersionIds.add(10000010037193619L);
          shopVersionIds.add(10000010037193620L);
          shopVersionIds.add(10000010017531653L);
        }
        shopIdList = configService.getShopByShopVersionAndArea(shopVersionIds.toArray(new Long[shopVersionIds.size()]), searchConditionDTO.getProvinceId(), searchConditionDTO.getCityId(),
            searchConditionDTO.getRegionId());
        if (CollectionUtils.isEmpty(shopIdList)) {
          return null;
        }else{
          searchConditionDTO.setShopIds(shopIdList.toArray(new Long[shopIdList.size()]));
        }
      }

      Map map = new HashMap();
      List<ProductDTO> productDTOList = null;
      searchConditionDTO.setIncludeBasic(false);
      searchConditionDTO.setRows(searchConditionDTO.getLimit());
      searchConditionDTO.setStart((searchConditionDTO.getPage() - 1) * searchConditionDTO.getLimit());
      searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_NORMAL_PRODUCT});
      if (StringUtils.isBlank(searchConditionDTO.getSort()) && searchConditionDTO.isEmptyOfProductInfo() && StringUtils.isBlank(searchConditionDTO.getSearchWord())) {
        searchConditionDTO.setSort("storage_time desc,inventory_amount desc");
      } else {
        searchConditionDTO.setSort(TxnConstant.sortCommandMap.get(searchConditionDTO.getSort()));
      }
      ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
      ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryProductWithUnknownField(searchConditionDTO);
      productDTOList = productSearchResultListDTO != null ? productSearchResultListDTO.getProducts() : null;


      List<Long> shopIds =new ArrayList<Long>();
      List<Long> productIdSet = new ArrayList<Long>();
      for (ProductDTO productDTO : productDTOList) {
        shopIds.add(productDTO.getShopId());
        productIdSet.add(productDTO.getProductLocalInfoId());
      }

      Map<Long, ShopDTO> shopDTOMap = new HashMap<Long, ShopDTO>();
      if (CollectionUtil.isNotEmpty(shopIds)) {
        shopDTOMap = configService.getShopByShopId(shopIds.toArray(new Long[shopIds.size()]));
      }
      Map<Long, ShopVersionDTO> shopVersionDTOMap = shopVersionService.getAllShopVersionMap();

      List<NormalProductInventoryStatDTO> statDTOList = new ArrayList<NormalProductInventoryStatDTO>();

      Long fromTime = System.currentTimeMillis();
      if (searchConditionDTO.getNormalProductStatType() == NormalProductStatType.WEEK) {
        fromTime = DateUtil.getLastWeekStartTime();
      } else if (searchConditionDTO.getNormalProductStatType() == NormalProductStatType.MONTH) {
        fromTime = DateUtil.getStartTimeOfTimeDay(DateUtil.getLastMonthTime(Calendar.getInstance()));
      } else if (searchConditionDTO.getNormalProductStatType() == NormalProductStatType.THREE_MONTH) {
        fromTime = DateUtil.getStartTimeOfTimeDay(DateUtil.getLastThreeMonthTime(Calendar.getInstance()));
      } else if (searchConditionDTO.getNormalProductStatType() == NormalProductStatType.HALF_YEAR) {
        fromTime = DateUtil.getStartTimeOfTimeDay(DateUtil.getLastHalfYearTime(Calendar.getInstance()));
      } else if (searchConditionDTO.getNormalProductStatType() == NormalProductStatType.YEAR) {
        fromTime = DateUtil.getStartTimeOfTimeDay(DateUtil.getLastYearTime(Calendar.getInstance()));
      }
      Long endTime = System.currentTimeMillis();

      Map<Long, List<NormalProductInventoryStatDTO>> itemMap = new HashMap<Long, List<NormalProductInventoryStatDTO>>();
      if (CollectionUtil.isNotEmpty(productIdSet)) {
        itemMap =  rfiTxnService.getProductTopPriceByProductIdTime(productIdSet.toArray(new Long[productIdSet.size()]), fromTime, endTime);
      }


      if (CollectionUtils.isNotEmpty(productDTOList)) {
        for (ProductDTO productDTO : productDTOList) {
          NormalProductInventoryStatDTO statDTO = productDTO.toNormalProductInventoryStatDTO();
          PurchaseInventoryStat stat = purchaseCostStatService.getCostStat(productDTO.getShopId(), productDTO.getProductLocalInfoId(), fromTime, endTime);
          PurchaseInventoryStatChange statChange = purchaseCostStatService.getCostStatChange(productDTO.getShopId(), productDTO.getProductLocalInfoId(), fromTime, endTime);
          statDTO.setTimes(stat.getTimes() + statChange.getTimes());
          statDTO.setAmount(stat.getAmount() + statChange.getAmount());
          statDTO.setTotal(stat.getTotal() + statChange.getTotal());
          statDTO.setAveragePrice(productDTO.getInventoryAveragePrice() == null ? 0D : productDTO.getInventoryAveragePrice());
          statDTO.setLastInventoryDate(productDTO.getLastPurchaseDateStr());
          statDTO.setInventoryAmount(productDTO.getInventoryNum());
//          statDTO.setShopVersion(shopVersionDTOMap.get(shopDTOMap.get(productDTO.getShopId()).getShopVersionId()).getValue());

          List<NormalProductInventoryStatDTO> statDTOs = itemMap.get(productDTO.getProductLocalInfoId());

          statDTO.setBottomPriceSet(false);
          if (CollectionUtil.isNotEmpty(statDTOs)) {
            for (NormalProductInventoryStatDTO inventoryStatDTO : statDTOs) {
              if (statDTOs.size() > 1 && StringUtil.isNotEmpty(inventoryStatDTO.getUnit()) && inventoryStatDTO.getUnit().equals(productDTO.getStorageUnit()) && NumberUtil.longValue(productDTO.getRate()) > 0) {
                inventoryStatDTO.setTopPrice(inventoryStatDTO.getTopPrice() / productDTO.getRate());
                inventoryStatDTO.setBottomPrice(inventoryStatDTO.getBottomPrice() / productDTO.getRate());
              }

              if(statDTO.isBottomPriceSet()){
                statDTO.setBottomPrice(NumberUtil.doubleVal(statDTO.getBottomPrice()) > inventoryStatDTO.getBottomPrice() ? inventoryStatDTO.getBottomPrice() : NumberUtil.doubleVal(statDTO.getBottomPrice()));
              }else{
                statDTO.setBottomPrice(inventoryStatDTO.getBottomPrice());
              }
              statDTO.setBottomPriceSet(true);

              statDTO.setTopPrice(NumberUtil.doubleVal(statDTO.getTopPrice()) < inventoryStatDTO.getTopPrice() ? inventoryStatDTO.getTopPrice() : NumberUtil.doubleVal(statDTO.getTopPrice()));
            }
          }

          statDTO.setPriceStr(statDTO.getTopPrice() + "/" + statDTO.getBottomPrice());
          statDTOList.add(statDTO);
        }
      }

      map.put("totalRows", Integer.valueOf(String.valueOf(productSearchResultListDTO != null ? productSearchResultListDTO.getNumFound() : 0)));
      map.put("result", statDTOList);
      return map;
    } catch (Exception e) {
      LOG.error("shopProductInventory.do method=getShopProductInventoryStatByCondition");
      LOG.error(searchConditionDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    return null;
  }
}
