package com.bcgogo.config.service;

import com.bcgogo.api.ApiArea;
import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.response.AreaResponse;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.RecommendShopDTO;
import com.bcgogo.config.dto.RecommendTreeDTO;
import com.bcgogo.config.dto.ShopAdAreaDTO;
import com.bcgogo.config.model.*;
import com.bcgogo.enums.app.AreaType;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.shop.RecommendShopStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.CheckNode;
import com.bcgogo.user.dto.Node;
import com.bcgogo.utils.ArrayUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-11-4
 * Time: 下午5:00
 */
@Component
public class AreaService implements IAreaService {
  private static final Logger LOG = LoggerFactory.getLogger(AreaService.class);
  @Autowired
  private ConfigDaoManager configDaoManager;

  public ApiResponse obtainAppShopArea(Long provinceId, AreaType type) {
    ApiResponse apiResponse;
    if (provinceId == null && AreaType.CITY == type) {
      apiResponse = MessageCode.toApiResponse(MessageCode.OBTAIN_AREA_FAIL, "省ID为空");
    } else {
      AreaResponse response = new AreaResponse(MessageCode.toApiResponse(MessageCode.OBTAIN_AREA_SUCCESS));
      ConfigReader reader = configDaoManager.getReader();
      if (AreaType.PROVINCE == type) {
        AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(1l);
        response.setAreaList(areaDTO.getChildAppList(reader.getShopProvinceAreaNoGroupByAreaId()));
      } else {
        AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(provinceId);
        response.setAreaList(areaDTO.getChildAppList(reader.getShopCityAreaNoGroupByAreaId(provinceId)));
      }
      apiResponse = response;
    }
    return apiResponse;
  }

  @Override
  public ApiResponse obtainJuheSupportArea() {
    AreaResponse response = new AreaResponse(MessageCode.toApiResponse(MessageCode.OBTAIN_AREA_SUCCESS));
    AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(1l);
    List<ApiArea> apiAreaList = areaDTO.getChildJuheList();
    for (ApiArea apiArea : apiAreaList) {
      apiArea.setChildren(AreaCacheManager.getAreaDTOByNo(apiArea.getId()).getChildrenJuheList());
    }
    response.setAreaList(apiAreaList);
    return response;
  }

  @Override
  public Set<String> getJuheCityCodeByBaiduCityCode(Integer[] baiduCityCodes) {
    if(ArrayUtil.isEmpty(baiduCityCodes))return new HashSet<String>();
    ConfigReader reader = configDaoManager.getReader();
    return reader.getJuheCityCodeByBaiduCityCode(baiduCityCodes);
  }

  @Override
  public Set<Long> getAreaNoByJuheCityCode(String... juheCityCodes) {
    if(ArrayUtil.isEmpty(juheCityCodes))return new HashSet<Long>();
    ConfigReader reader = configDaoManager.getReader();
    return reader.getAreaNoByJuheCityCode(juheCityCodes);
  }

  @Override
  public List<ShopAdAreaDTO> getShopAdAreaDTOsByShopId(Long shopId){
    List<ShopAdAreaDTO> shopAdAreaDTOs = new ArrayList<ShopAdAreaDTO>();
    if(shopId == null){
      return shopAdAreaDTOs;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopAdArea> shopAdAreas = writer.getShopAdAreaByShopId(shopId);

    if(CollectionUtils.isNotEmpty(shopAdAreas)){
      for(ShopAdArea shopAdArea : shopAdAreas){
        ShopAdAreaDTO shopAdAreaDTO = shopAdArea.toDTO();
        if(shopAdArea.getAreaId() != null){
          AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(shopAdArea.getAreaId());
          if(areaDTO != null && StringUtils.isNotBlank(areaDTO.getName())) {
            shopAdAreaDTO.setAreaName(areaDTO.getName());
          }
        }
        shopAdAreaDTOs.add(shopAdAreaDTO);
      }
    }
    return shopAdAreaDTOs;
  }

  @Override
  public Node getShopAdAreaScopeByShopId(Long shopId) {
    List<ShopAdAreaDTO> shopAdAreaDTOList = getShopAdAreaDTOsByShopId(shopId);
    Set<Long> shopAdAreaIds = new HashSet<Long>();
    if(CollectionUtils.isNotEmpty(shopAdAreaDTOList)){
      for(ShopAdAreaDTO shopAdAreaDTO : shopAdAreaDTOList){
        if(shopAdAreaDTO != null && shopAdAreaDTO.getAreaId() != null){
          shopAdAreaIds.add(shopAdAreaDTO.getAreaId());
        }
      }
    }

    return getCheckedShopAdAreaScope(shopAdAreaIds);

  }

  @Override
  public Node getCheckedShopAdAreaScope(Set<Long> shopAdAreaIds) {
    Map<Long, AreaDTO> cacheAreaMap = AreaCacheManager.getAllAreaDTOMap();
    CheckNode root = new CheckNode();
    root.setId(-1L);     //根节点
    if (MapUtils.isEmpty(cacheAreaMap)) return root;
    List<Node> nodeList = new ArrayList<Node>();

    if (MapUtils.isNotEmpty(cacheAreaMap)) {
      for (AreaDTO areaDTO : cacheAreaMap.values()) {
        if (areaDTO.getLevel() == 1) {
          CheckNode firstLevelNode = areaDTO.toFirstLevelNode();
          if (shopAdAreaIds.contains(areaDTO.getNo())) {
            firstLevelNode.setChecked(true);
            firstLevelNode.setExpanded(true);
          }
          nodeList.add(firstLevelNode);
          boolean isAllChildRenSelectSelect = true;
          boolean isChildrenSelect = false;

          if (CollectionUtils.isNotEmpty(areaDTO.getChildAreaDTOList())) {
            for (AreaDTO childrenAreaDTO : areaDTO.getChildAreaDTOList()) {
              CheckNode secondLevelNode = childrenAreaDTO.toSecondLevelNode();
              if (shopAdAreaIds.contains(childrenAreaDTO.getNo())) {
                secondLevelNode.setChecked(true);
                isChildrenSelect = true;
              }else {
                isAllChildRenSelectSelect = false;
              }
              nodeList.add(secondLevelNode);
            }
          }
          if(!firstLevelNode.getChecked() && isAllChildRenSelectSelect && isChildrenSelect){
            firstLevelNode.setChecked(true);
            firstLevelNode.setExpanded(true);
          }
          if(!firstLevelNode.getExpanded() && isChildrenSelect){
            firstLevelNode.setExpanded(true);
          }
        }
      }
    }
    root.buildTree(root, nodeList);
    return root;

  }

  @Override
  public void saveOrUpdateShopAdArea(Long shopId, Set<Long> shopAdAreaIds) {
    if(shopId == null){
      return;
    }

    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try{
      List<ShopAdArea> shopAdAreas = writer.getShopAdAreaByShopId(shopId);
      if(CollectionUtils.isNotEmpty(shopAdAreas)){
        for(ShopAdArea shopAdArea : shopAdAreas){
          writer.delete(shopAdArea);
        }
      }
      if(CollectionUtils.isNotEmpty(shopAdAreaIds)){
        for(Long shopAdAreaId : shopAdAreaIds){
          ShopAdAreaDTO shopAdAreaDTO = AreaCacheManager.generateShopAdAreaDTO(shopAdAreaId);
          shopAdAreaDTO.setShopId(shopId);
          ShopAdArea shopAdArea = new ShopAdArea();
          shopAdArea.fromDTO(shopAdAreaDTO);
          writer.save(shopAdArea);
        }
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public Node getShopRecommendScopeByShopId(Long shopId) {
    IRecommendShopService recommendShopService = ServiceManager.getService(IRecommendShopService.class);
    List<RecommendShopDTO> recommendShopDTOs = recommendShopService.getShopRecommendDTOs(shopId);
    Set<Long> recommendIds = new HashSet<Long>();
    if(CollectionUtils.isNotEmpty(recommendShopDTOs)){
      for(RecommendShopDTO recommendShopDTO : recommendShopDTOs){
        if(recommendShopDTO != null && recommendShopDTO.getRecommendId() != null){
          recommendIds.add(recommendShopDTO.getRecommendId());
        }
      }
    }
    return getCheckedShopRecommendScope(recommendIds);
  }

  @Override
  public Node getCheckedShopRecommendScope(Set<Long> recommendIds) {
    IRecommendShopService recommendShopService = ServiceManager.getService(IRecommendShopService.class);
    List<RecommendTreeDTO> recommendTreeDTOs = recommendShopService.getRecommendTreeDTO();
    CheckNode root = new CheckNode();
    root.setId(-1L);     //根节点
    if (CollectionUtils.isEmpty(recommendTreeDTOs)) return root;
    List<Node> nodeList = new ArrayList<Node>();
    for(RecommendTreeDTO recommendTreeDTO : recommendTreeDTOs){
      CheckNode childNode = recommendTreeDTO.toNode();
      if (recommendIds.contains(recommendTreeDTO.getId())) {
        childNode.setChecked(true);
      }
      childNode.setLeaf(!root.getId().equals(childNode.getParentId()));
      if(root.getId().equals(childNode.getParentId())){
        childNode.setExpanded(true);
      }
      nodeList.add(childNode);
    }
    root.buildTree(root, nodeList);
    root.reBuildTreeForSort();
    root.reBuildTreeForChecked();
    return root;
  }

  @Override
  public void saveOrUpdateShopRecommend(Long shopId, Set<Long> recommendIds) {
    if(shopId == null){
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try{
      List<RecommendShop> recommendShops = writer.getRecommendShopByShopId(shopId);
      Set<Long> existRecommendIds = new HashSet<Long>();
      if(CollectionUtils.isNotEmpty(recommendIds)){
        List<RecommendTree> recommendTrees = writer.getRecommendTreeByRecommendIds(recommendIds);
        Set<Long> tempRecommendIds = new HashSet<Long>();
        if(CollectionUtils.isNotEmpty(recommendTrees)){
          for(RecommendTree recommendTree:recommendTrees){
            tempRecommendIds.add(recommendTree.getId());
          }
        }
        recommendIds = tempRecommendIds;
      }
      if(CollectionUtils.isNotEmpty(recommendShops)){
        for(RecommendShop recommendShop : recommendShops){
          existRecommendIds.add(recommendShop.getRecommendId());
          if(CollectionUtils.isEmpty(recommendIds) || !recommendIds.contains(recommendShop.getRecommendId())){
            recommendShop.setStatus(RecommendShopStatus.DISABLED);
            writer.update(recommendShop);
          }
        }
      }
      if(CollectionUtils.isNotEmpty(recommendIds)){
        for(Long recommendId : recommendIds){
          if(CollectionUtils.isEmpty(existRecommendIds) || !existRecommendIds.contains(recommendId)){
            RecommendShop recommendShop = new RecommendShop(recommendId,shopId);
            writer.save(recommendShop);
            existRecommendIds.add(recommendId);
          }
        }
      }


      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateArea(AreaDTO... areaDTOs){
    if(ArrayUtil.isEmpty(areaDTOs)) return;
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try{
      for(AreaDTO areaDTO:areaDTOs){
        Area area=null;
        if(areaDTO.getId()!=null){
          area=writer.getById(Area.class,areaDTO.getId());
        }else {
          area=new Area();
        }
        area.fromDTO(areaDTO);
        writer.saveOrUpdate(area);
        areaDTO.setId(area.getId());
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

}
