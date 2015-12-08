package com.bcgogo.config;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IRecommendShopService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.Node;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-21
 * Time: 上午11:27
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/recommendShop.do")
public class RecommendShopController {

  private static final Logger LOG = LoggerFactory.getLogger(RecommendShopController.class);

  @Autowired
  IRecommendShopService recommendShopService;

  @RequestMapping(params="method=getRecommendTreeNode")
  @ResponseBody
  public Object getCarSeries(HttpServletRequest request,ModelMap modelMap) {
    try{
      Node root=recommendShopService.getRecommendShopTreeNode();
      //统计节点对于数据数量
      IConfigService configService= ServiceManager.getService(IConfigService.class);
      ShopDTO shopDTO=configService.getShopById(WebUtil.getShopId(request));
      Map<Long,Node> nodeMap=new HashMap<Long, Node>();
      List<Long> parentIds=new ArrayList<Long>();
      List<Node> nodeList=root.getChildren();
      for(Node pNode:nodeList){
        for(Node node:pNode.getChildren()){
          nodeMap.put(node.getId(),node);
          parentIds.add(node.getId());
        }
      }
      //stat info
      Map<Long,Integer> countMap=configService.countRecommendShopByShopArea(shopDTO.getProvince(),shopDTO.getCity(),shopDTO.getRegion(),parentIds.toArray(new Long[parentIds.size()]));
      if(MapUtils.isNotEmpty(countMap)){
        for(Long nodeId:nodeMap.keySet()){
          Node node=nodeMap.get(nodeId);
          node.setChildSize(NumberUtil.intValue(countMap.get(nodeId)));
        }
        for(Node pNode:nodeList){
          List<Node> nodes=pNode.getChildren();
          int size=0;
          if(CollectionUtil.isNotEmpty(nodes)){
            for(Node node:nodes){
              size+=NumberUtil.intValue(countMap.get(node.getId()));
            }
          }
          pNode.setChildSize(size);
        }
      }
      return root;
    }catch (Exception e){
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params="method=getRecommendShopBySeries")
  @ResponseBody
  public Object getRecommendShopBySeries(HttpServletRequest request,Long parentId){
    try{
      IConfigService configService= ServiceManager.getService(IConfigService.class);
      ShopDTO shopDTO=configService.getShopById(WebUtil.getShopId(request));
      return configService.getRecommendShopByShopArea(parentId,shopDTO.getProvince(),shopDTO.getCity(),shopDTO.getRegion());
    }catch (Exception e){
      LOG.error(e.getMessage(), e);
      return null;
    }
  }



}
