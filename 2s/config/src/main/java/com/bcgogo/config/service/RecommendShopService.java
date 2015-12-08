package com.bcgogo.config.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.RecommendShopDTO;
import com.bcgogo.config.dto.RecommendTreeDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.DataImageDetailDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.RecommendShop;
import com.bcgogo.config.model.RecommendTree;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.shop.RecommendTreeStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.recommend.ShopRecommendDTO;
import com.bcgogo.user.dto.CheckNode;
import com.bcgogo.user.dto.Node;
import com.bcgogo.utils.CollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by XinyuQiu on 14-8-20.
 */
@Component
public class RecommendShopService implements IRecommendShopService{
  private static final Logger LOG = LoggerFactory.getLogger(AreaService.class);

  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public List<RecommendTreeDTO> getRecommendTreeDTO() {
    ConfigWriter writer = configDaoManager.getWriter();
    List<RecommendTree> recommendTrees = writer.getRecommendTree();
    List<RecommendTreeDTO> recommendTreeDTOs = new ArrayList<RecommendTreeDTO>();
    Set<Long> recommendIds = new HashSet<Long>();
    if(CollectionUtils.isNotEmpty(recommendTrees)){
      for(RecommendTree recommendTree : recommendTrees){
        if(recommendTree != null && recommendTree.getId() != null){
          recommendIds.add(recommendTree.getId());
        }
      }
    }
    IImageService imageService = ServiceManager.getService(IImageService.class);
    Map<Long,DataImageDetailDTO> dataImageDetailDTOMap = imageService.getRecommendTreeImgMapByDataIds(recommendIds);
    if(CollectionUtils.isNotEmpty(recommendTrees)){
      for(RecommendTree recommendTree : recommendTrees){
        RecommendTreeDTO recommendTreeDTO = recommendTree.toDTO();
        DataImageDetailDTO dataImageDetailDTO =dataImageDetailDTOMap.get(recommendTreeDTO.getId());
        if(dataImageDetailDTO != null){
          recommendTreeDTO.setImageUrl(dataImageDetailDTO.getImageURL());
        }
        recommendTreeDTOs.add(recommendTreeDTO);
      }
    }
    return recommendTreeDTOs;
  }

  @Override
  public List<RecommendShopDTO> getShopRecommendDTOs(Long shopId) {
    List<RecommendShopDTO> recommendShopDTOs = new ArrayList<RecommendShopDTO>();
    if(shopId == null){
      return recommendShopDTOs;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    List<RecommendShop> recommendShops = writer.getRecommendShopByShopId(shopId);
    if(CollectionUtils.isNotEmpty(recommendShops)){
      for(RecommendShop recommendShop : recommendShops){
        recommendShopDTOs.add(recommendShop.toDTO());
      }
    }
    return recommendShopDTOs;
  }

  @Override
  public Result validateSaveOrUpdateRecommendTreeDTO(RecommendTreeDTO recommendTreeDTO) {
    if (recommendTreeDTO != null) {
      if (recommendTreeDTO.getParentId() != null) {

      }
    }
    return new Result();
  }

  @Override
  public void saveOrUpdateShopRecommend(RecommendTreeDTO recommendTreeDTO) {

    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try{
      RecommendTree recommendTree = null;
      if(recommendTreeDTO.getId() != null){
        recommendTree = writer.getById(RecommendTree.class,recommendTreeDTO.getId());
      }
      recommendTreeDTO.setStatus(RecommendTreeStatus.ENABLED);
      if(recommendTree == null){
        recommendTree = new RecommendTree();
        recommendTree.fromDTO(recommendTreeDTO);
        writer.save(recommendTree);
        recommendTreeDTO.setId(recommendTree.getId());
      }else {
        recommendTreeDTO.setImageId(recommendTree.getImageId());
        recommendTree.fromDTO(recommendTreeDTO);
        writer.update(recommendTree);
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }

  }


  @Override
  public Result validateDeleteRecommendTreeDTO(RecommendTreeDTO recommendTreeDTO) {
    if(recommendTreeDTO.getId()!=null){
      List<RecommendShopDTO> recommendShopDTOs = getAllRecommendShopDTOByRecommendId(recommendTreeDTO.getId());

      if(CollectionUtils.isNotEmpty(recommendShopDTOs)){
        Set<Long> shopIds = new HashSet<Long>();
        for(RecommendShopDTO recommendShopDTO : recommendShopDTOs){
          if(recommendShopDTO!= null && recommendShopDTO.getShopId() != null){
            shopIds.add(recommendShopDTO.getShopId());
          }
        }
        if(CollectionUtils.isNotEmpty(shopIds)){
          IConfigService configService = ServiceManager.getService(IConfigService.class);
          List<ShopDTO> shopDTOs = configService.getShopByIds(shopIds.toArray(new Long[shopIds.size()]));
          if(CollectionUtils.isNotEmpty(shopDTOs)){
            StringBuilder sb = new StringBuilder();
            sb.append("当前类目下有以下店面，无法删除。店面为：");
            for(ShopDTO shopDTO : shopDTOs){
              sb.append(shopDTO.getName()).append(",");
            }
            return new Result(sb.toString(),false);
          }
        }
      }
    }
    return new Result();
  }

  public List<RecommendShopDTO> getAllRecommendShopDTOByRecommendId(Long recommendId){
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ConfigWriter writer = configDaoManager.getWriter();
    List<RecommendShop> recommendShops = writer.getRecommendShopByRecommendId(recommendId);
    List<RecommendShopDTO> recommendShopDTOs = new ArrayList<RecommendShopDTO>();
    if(CollectionUtils.isNotEmpty(recommendShops)){
      for(RecommendShop recommendShop : recommendShops){
        if(recommendShop!= null){
          recommendShopDTOs.add(recommendShop.toDTO());
        }
      }
    }
    return recommendShopDTOs;
  }

  @Override
  public void deleteShopRecommend(RecommendTreeDTO recommendTreeDTO) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try{
      RecommendTree recommendTree ;
      if(recommendTreeDTO.getId() != null){
        recommendTree = writer.getById(RecommendTree.class,recommendTreeDTO.getId());
        if(recommendTree != null){
          recommendTree.setStatus(RecommendTreeStatus.DISABLED);
          writer.update(recommendTree);
          writer.commit(status);
        }
      }
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateShopRecommendImgInfo(DataImageRelationDTO dataImageRelationDTO) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try{
      RecommendTree recommendTree ;
      if(dataImageRelationDTO.getDataId() != null){
        recommendTree = writer.getById(RecommendTree.class,dataImageRelationDTO.getDataId() );
        if(recommendTree != null){
          recommendTree.setImageId(dataImageRelationDTO.getImageId());
          writer.update(recommendTree);
          writer.commit(status);
        }
      }
    }finally {
      writer.rollback(status);
    }
  }
  @Override
  public Node getRecommendShopTreeNode() {
    List<RecommendTreeDTO> recommendTreeDTOs =getRecommendTreeDTO();
    CheckNode root = new CheckNode();
    root.setId(-1L);     //????
    if (CollectionUtils.isEmpty(recommendTreeDTOs)) return root;
    List<Node> nodeList = new ArrayList<Node>();
    for(RecommendTreeDTO recommendTreeDTO : recommendTreeDTOs){
      CheckNode childNode = recommendTreeDTO.toNode();
      nodeList.add(childNode);
    }
    root.buildTree(nodeList);
    root.reBuildTreeForSort();
    return root;

  }


}
