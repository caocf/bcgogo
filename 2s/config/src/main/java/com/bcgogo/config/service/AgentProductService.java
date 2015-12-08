package com.bcgogo.config.service;

import com.bcgogo.config.dto.AgentProductDTO;
import com.bcgogo.config.model.AgentProduct;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.ShopAgentProduct;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-17
 * Time: 下午2:43
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AgentProductService implements IAgentProductService{
  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public List<AgentProductDTO> getAgentProductDTO(Long shopId){
    List<AgentProduct> agentProducts=getAgentProduct(shopId);
    List<AgentProductDTO> agentProductDTOs=new ArrayList<AgentProductDTO>();
    if(CollectionUtil.isNotEmpty(agentProducts)){
      for (AgentProduct agentProduct:agentProducts){
        agentProductDTOs.add(agentProduct.toDTO());
      }
    }
    return agentProductDTOs;
  }

  @Override
  public List<AgentProduct> getAgentProduct(Long shopId){
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getAgentProductByShopId(shopId);
  }

  @Override
  public List<ShopAgentProduct> getShopAgentProduct(Long shopId){
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getShopAgentProductByShopId(shopId);
  }

  public Boolean saveShopAgentProduct(Long shopId,Long ... agentProductIds){
    if(ArrayUtil.isEmpty(agentProductIds)){
      return false;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status=writer.begin();
    try{
      for(Long agentProductId:agentProductIds){
        ShopAgentProduct shopAgentProduct=new ShopAgentProduct();
        shopAgentProduct.setAgentProductId(agentProductId);
        shopAgentProduct.setShopId(shopId);
        shopAgentProduct.setDeleted(DeletedType.FALSE);
        writer.save(shopAgentProduct);
        writer.commit(status);
      }
    }finally {
      writer.rollback(status);
    }
    return true;
  }

  @Override
  public void updateShopAgentProductStatus(Long shopId, DeletedType deletedType) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status=writer.begin();
    try{
      writer.updateShopAgentProductStatus(shopId, deletedType);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

}
