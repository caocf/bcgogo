package com.bcgogo.config.service;

import com.bcgogo.config.dto.AgentProductDTO;
import com.bcgogo.config.model.AgentProduct;
import com.bcgogo.config.model.ShopAgentProduct;
import com.bcgogo.enums.DeletedType;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-17
 * Time: 下午2:44
 * To change this template use File | Settings | File Templates.
 */
public interface IAgentProductService {

  List<AgentProductDTO> getAgentProductDTO(Long shopId);

  List<AgentProduct> getAgentProduct(Long shopId);

  Boolean saveShopAgentProduct(Long shopId,Long ... agentProductIds);

  void updateShopAgentProductStatus(Long shopId, DeletedType deletedType);

  List<ShopAgentProduct> getShopAgentProduct(Long shopId);

}
