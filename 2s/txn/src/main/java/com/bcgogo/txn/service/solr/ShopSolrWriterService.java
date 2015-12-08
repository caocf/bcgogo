package com.bcgogo.txn.service.solr;

import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IServiceCategoryService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.shop.ShopState;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.enums.shop.ShopType;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.ShopSolrDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-9-10
 * Time: 下午1:01
 */
@Component
public class ShopSolrWriterService implements IShopSolrWriterService {
  private static final Logger LOG = LoggerFactory.getLogger(ShopSolrWriterService.class);

  @Override
  public void reCreateShopSolrIndexAll() throws Exception {
    IShopService shopService = ServiceManager.getService(IShopService.class);
    List<Long> ids = shopService.getActiveShopIds();
    reCreateShopIdSolrIndex(ids.toArray(new Long[ids.size()]));
  }

  @Override
  public void reCreateShopIdSolrIndex(Long... shopIds) throws Exception {
    if (ArrayUtil.isEmpty(shopIds)) return;
    IShopService shopService = ServiceManager.getService(IShopService.class);
    IShopVersionService shopVersionService = ServiceManager.getService(IShopVersionService.class);
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    Map<Long, ShopDTO> shopDTOMap = shopService.getShopByShopIds(shopIds);
    Map<Long, ShopVersionDTO> versionMap = shopVersionService.getAllShopVersionMap();
    Map<Long, List<Long>> shopServiceCategoryDTOMap = ServiceManager.getService(IServiceCategoryService.class)
        .getShopServiceCategoryMap(shopIds);
    Map<Long, CommentStatDTO> commentStatDTOMap = ServiceManager.getService(ISupplierCommentService.class)
        .getCommentStatByShopIds(new ArrayList<Long>(Arrays.asList(shopIds)));
    List<Long> shopServiceCategoryList;
    for (ShopDTO shopDTO : shopDTOMap.values()) {
      if (shopDTO.getShopState() != ShopState.ACTIVE) continue;
      if (!ShopStatus.isAuditedShopStatus(shopDTO.getShopStatus())) continue;
      ShopSolrDTO dto = shopDTO.toShopSolrDTO();
      ShopVersionDTO shopVersionDTO = versionMap.get(shopDTO.getShopVersionId());
      if (shopVersionDTO != null) {
        ShopType shopType = ShopType.lookupShopType(shopVersionDTO.getName());
        if (shopType != null) dto.setShopType(shopType.toString());
      }
      dto.from(commentStatDTOMap.get(shopDTO.getId()));
      AreaDTO areaDTO;
      if (dto.getCityNo() != null) {
        areaDTO = AreaCacheManager.getAreaDTOByNo(dto.getCityNo());
        if (areaDTO != null) {
          dto.setCity(areaDTO.getName());
          dto.setCityCode(areaDTO.getCityCode());
        }
      }
      if (dto.getProvinceNo() != null) {
        areaDTO = AreaCacheManager.getAreaDTOByNo(dto.getProvinceNo());
        if (areaDTO != null) {
          dto.setProvinceCode(areaDTO.getCityCode());
          if (dto.getCityCode() == null)
            dto.setCityCode(areaDTO.getCityCode());
          dto.setProvince(areaDTO.getName());
        }
      }
      if (dto.getRegionNo() != null) {
        areaDTO = AreaCacheManager.getAreaDTOByNo(dto.getRegionNo());
        if (areaDTO != null) {
          dto.setRegion(areaDTO.getName());
          if (dto.getCityCode() == null)
            dto.setCityCode(areaDTO.getCityCode());
        }

      }
      shopServiceCategoryList = shopServiceCategoryDTOMap.get(dto.getId());
      if (CollectionUtil.isNotEmpty(shopServiceCategoryList)) {
        dto.setServiceScopeIds(StringUtil.parseStringArray(new HashSet<Long>(shopServiceCategoryList)));
      }
      docs.add(dto.toSolrInputDocument());
    }
    if (CollectionUtils.isEmpty(docs)) return;
    deleteShop(shopIds);
    Long start = System.currentTimeMillis();
    LOG.debug("update shop start time :{}", DateUtil.convertDateLongToString(start, DateUtil.ALL));
    LOG.debug("docs.size : " + docs.size());
    SolrClientHelper.geShopSolrClient().addDocs(docs);
    LOG.debug("update shop end time : {}", DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.ALL));
    LOG.debug("cost : {} ms", (System.currentTimeMillis() - start));
  }

  private void deleteShop(Long... shopIds) throws Exception {
    Long start = System.currentTimeMillis();
    List<String> ids = new ArrayList<String>();
    for (Long id : shopIds) {
      ids.add(String.valueOf(id));
    }
    LOG.debug("delete shop start time : {}", DateUtil.convertDateLongToString(start, DateUtil.ALL));
    LOG.debug("docs.size : " + shopIds.length);
    SolrClientHelper.geShopSolrClient().deleteByIds(ids);
    LOG.debug("delete shop end time : {}", DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.ALL));
    LOG.debug("cost : {} ms", (System.currentTimeMillis() - start));
  }

}
