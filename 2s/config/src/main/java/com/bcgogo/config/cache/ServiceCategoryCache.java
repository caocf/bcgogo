package com.bcgogo.config.cache;

import com.bcgogo.config.dto.ServiceCategoryDTO;
import com.bcgogo.config.service.IServiceCategoryService;
import com.bcgogo.enums.app.ServiceScope;
import com.bcgogo.enums.config.ServiceCategoryType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.Node;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-10
 * Time: 上午11:01
 */
public class ServiceCategoryCache {
  private static IServiceCategoryService serviceCategoryService;
  private static final Logger LOG = LoggerFactory.getLogger(ServiceCategoryCache.class);
  public static List<ServiceCategoryDTO> serviceCategoryDTOs = new ArrayList<ServiceCategoryDTO>();
  public static Map<Long,ServiceCategoryDTO> serviceCategoryDTOMap = new HashMap<Long, ServiceCategoryDTO>();

  public static IServiceCategoryService getServiceCategoryService() {
    return serviceCategoryService==null ? ServiceManager.getService(IServiceCategoryService.class) : serviceCategoryService;
  }

  public static ServiceCategoryDTO getServiceCategoryDTOById(Long id){
    return getServiceCategoryDTOMap().get(id);
  }

  public static List<ServiceCategoryDTO> getServiceCategoryDTOListByType(ServiceCategoryType... serviceCategoryTypes){
    List<ServiceCategoryDTO> result = new ArrayList<ServiceCategoryDTO>();
    if(ArrayUtils.isEmpty(serviceCategoryTypes)) return result;
    for(ServiceCategoryDTO serviceCategoryDTO:getServiceCategoryDTOList()){
      if(ArrayUtils.contains(serviceCategoryTypes,serviceCategoryDTO.getCategoryType()))
        result.add(serviceCategoryDTO);
    }
    return result;
  }

  public static List<ServiceCategoryDTO> getServiceCategoryDTOByServiceScope(String serviceScopeStr) {

    List<ServiceCategoryDTO> returnList = new ArrayList<ServiceCategoryDTO>();

    List<ServiceCategoryDTO> serviceCategoryDTOList = getServiceCategoryDTOList();

    Set<Long>firstCategoryIds = new HashSet<Long>();
    if (CollectionUtil.isNotEmpty(serviceCategoryDTOList)) {

      for (ServiceCategoryDTO serviceCategoryDTO : serviceCategoryDTOList) {
        if (StringUtils.isEmpty(serviceScopeStr) || StringUtil.isEmptyAppGetParameter(serviceScopeStr)) {
          if (ServiceCategoryType.SECOND_CATEGORY.equals(serviceCategoryDTO.getCategoryType())) {
            returnList.add(serviceCategoryDTO);
          }
        } else if (serviceCategoryDTO.getServiceScope() != null && serviceScopeStr.equals(serviceCategoryDTO.getServiceScope().name())) {
          firstCategoryIds.add(serviceCategoryDTO.getId());
        }
      }

      if (CollectionUtil.isNotEmpty(firstCategoryIds)) {
        for (ServiceCategoryDTO serviceCategoryDTO : serviceCategoryDTOList) {
          if (serviceCategoryDTO.getParentId() != null && firstCategoryIds.contains(serviceCategoryDTO.getParentId())) {
            returnList.add(serviceCategoryDTO);
          }
        }
      }
    }

    return returnList;
  }

  public static List<ServiceCategoryDTO> getServiceCategoryDTOList(){
    if (CollectionUtils.isEmpty(serviceCategoryDTOs)){
      serviceCategoryDTOs = getServiceCategoryService().getServiceCategoryDTO(ShopConstant.BC_SHOP_ID);
      if (CollectionUtils.isEmpty(serviceCategoryDTOs)) {
        LOG.warn("服务分类(serviceCategory)数据为空。");
      }
    }
    return serviceCategoryDTOs;
  }

  public static Map<Long, ServiceCategoryDTO> getServiceCategoryDTOMap() {
    if(MapUtils.isEmpty(serviceCategoryDTOMap)){
      List<ServiceCategoryDTO> serviceCategoryDTOs=getServiceCategoryDTOList();
      if(CollectionUtil.isNotEmpty(serviceCategoryDTOs)){
        for (ServiceCategoryDTO serviceCategoryDTO :serviceCategoryDTOs) {
          serviceCategoryDTOMap.put(serviceCategoryDTO.getId(), serviceCategoryDTO);
        }
      }
    }
    return serviceCategoryDTOMap;
  }

  public static List<Node> getAllTreeLeafNode(){
    List<Node> leafNodes=new ArrayList<Node>();
    List<ServiceCategoryDTO> serviceCategoryDTOs=getServiceCategoryDTOList();
    if (CollectionUtils.isEmpty(serviceCategoryDTOs)) {
      return leafNodes;
    }
     for (ServiceCategoryDTO categoryDTO:serviceCategoryDTOs) {
         if(categoryDTO==null){
           continue;
         }
       if(ServiceCategoryType.SECOND_CATEGORY.equals(categoryDTO.getCategoryType())){
            leafNodes.add(categoryDTO.toNode());
       }
     }
      return leafNodes;
  }

  public static Node getTreeNode(){
    Node root = new Node();
    root.setType(Node.Type.TOP_CATEGORY);
    List<ServiceCategoryDTO> serviceCategoryDTOs=getServiceCategoryDTOList();
    if (CollectionUtils.isEmpty(serviceCategoryDTOs)) {
      return root;
    }
    Map<Long,Node> firstCategoryMap = new HashMap<Long,Node>();
    List<ServiceCategoryDTO> secondCategoryList = new ArrayList<ServiceCategoryDTO>();
    ServiceCategoryType categoryType=null;
    for (ServiceCategoryDTO categoryDTO:serviceCategoryDTOs) {
      if(categoryDTO==null){
        continue;
      }
      categoryType=categoryDTO.getCategoryType();
      switch (categoryType){
        case FIRST_CATEGORY:
          firstCategoryMap.put(categoryDTO.getId(),categoryDTO.toNode());
          break;
        case SECOND_CATEGORY:
          secondCategoryList.add(categoryDTO);
      }
    }
    Node parent=null;
    List<Node> children=null;
    for(ServiceCategoryDTO categoryDTO:secondCategoryList){
      parent=firstCategoryMap.get(categoryDTO.getParentId());
      if(parent==null){
        continue;
      }
      children=parent.getChildren();
      if(children==null){
        children=new ArrayList<Node>();
      }
      children.add(categoryDTO.toNode());
    }
    List<Node> firstNodes=new ArrayList<Node>();
    firstNodes.addAll(firstCategoryMap.values());
    root.setChildren(firstNodes);
    return root;
  }

}
