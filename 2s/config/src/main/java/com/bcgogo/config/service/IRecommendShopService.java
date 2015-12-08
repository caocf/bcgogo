package com.bcgogo.config.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.RecommendShopDTO;
import com.bcgogo.config.dto.RecommendTreeDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.user.dto.Node;

import java.util.List;

/**
 * Created by XinyuQiu on 14-8-20.
 */
public interface IRecommendShopService {

  List<RecommendTreeDTO> getRecommendTreeDTO();

  List<RecommendShopDTO> getShopRecommendDTOs(Long shopId);

  Result validateSaveOrUpdateRecommendTreeDTO(RecommendTreeDTO recommendTreeDTO);

  void saveOrUpdateShopRecommend(RecommendTreeDTO recommendTreeDTO);

  Result validateDeleteRecommendTreeDTO(RecommendTreeDTO recommendTreeDTO);

  void deleteShopRecommend(RecommendTreeDTO recommendTreeDTO);

  void updateShopRecommendImgInfo(DataImageRelationDTO dataImageRelationDTO);

  Node getRecommendShopTreeNode();

}
