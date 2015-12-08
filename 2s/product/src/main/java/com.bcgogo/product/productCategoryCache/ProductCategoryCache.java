package com.bcgogo.product.productCategoryCache;

import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.Node;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 商品分类（经营范围)cahce
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-6-20
 * Time: 下午6:15
 */
public class ProductCategoryCache {

  private static IProductCategoryService productCategoryService;

  public static IProductCategoryService getProductCategoryService() {
    return productCategoryService == null ? ServiceManager.getService(IProductCategoryService.class) : productCategoryService;
  }

  private static final Logger LOG = LoggerFactory.getLogger(ProductCategoryCache.class);


  public static List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>(); // 对应的营业分类
  public static Node node;

  public static Node getNode() {
    if (node == null) {
      node = getProductCategoryNode(null,getProductCategoryDTOList());
    }
    return node;
  }


  public static ProductCategoryDTO getProductCategoryDTOById(Long id){
    return getProductCategoryDTOMap().get(id);
  }
  public static List<ProductCategoryDTO> getProductCategoryDTOListByType(ProductCategoryType... productCategoryTypes) {
    List<ProductCategoryDTO> result = new ArrayList<ProductCategoryDTO>();
    if(ArrayUtils.isEmpty(productCategoryTypes)) return result;
    for(ProductCategoryDTO productCategoryDTO:getProductCategoryDTOList()){
      if(ArrayUtils.contains(productCategoryTypes,productCategoryDTO.getCategoryType()))
        result.add(productCategoryDTO);
    }
    return result;
  }
  public static Node searchProductCategoryNode(String searchWord) {
    return getProductCategoryNode(searchWord,getProductCategoryDTOList());
  }


  public static  List<ProductCategoryDTO> getProductCategoryDTOList() {
    if (CollectionUtils.isEmpty(productCategoryDTOList)) {
      List<ProductCategoryDTO> dbProductCategoryDTOList = getProductCategoryService().getProductCategoryDTOByShopId(ShopConstant.BC_ADMIN_SHOP_ID);
      getProductCategoryService().fillProductCategoryDTOListInfo(dbProductCategoryDTOList);
      if (CollectionUtils.isEmpty(dbProductCategoryDTOList)) {
        LOG.error("getProductCategoryDTOList productCategory is empty");
      } else {
        productCategoryDTOList = dbProductCategoryDTOList;
      }
    }
 /*
   * 防止 外部操作 静态变量
   */
    return new ArrayList<ProductCategoryDTO>(productCategoryDTOList);
  }

  public static Map<Long, ProductCategoryDTO> getProductCategoryDTOMap() {
    Map<Long,ProductCategoryDTO> productCategoryDTOMap = new HashMap<Long, ProductCategoryDTO>();
    for (ProductCategoryDTO productCategoryDTO : getProductCategoryDTOList()) {
      productCategoryDTOMap.put(productCategoryDTO.getId(), productCategoryDTO);
    }
    return productCategoryDTOMap;
  }

  /**
   * 只查  2   3  级
   * @param searchWord
   * @return
   */
  public static String[] searchCategoryNodeIds(String searchWord) {
    if(StringUtils.isEmpty(searchWord)){
      return null;
    }
    List<ProductCategoryDTO> productCategoryDTOList=getProductCategoryDTOList();
    if (CollectionUtils.isEmpty(productCategoryDTOList)) {
      return null;
    }
    List<String> categoryNodeIds = new ArrayList<String>();
    Iterator<ProductCategoryDTO> iterator = productCategoryDTOList.iterator();
    while (iterator.hasNext()){
      ProductCategoryDTO productCategoryDTO = iterator.next();
      if (productCategoryDTO.getCategoryType() == ProductCategoryType.SECOND_CATEGORY||productCategoryDTO.getCategoryType() == ProductCategoryType.THIRD_CATEGORY) {
        if(productCategoryDTO.getName().indexOf(searchWord)>-1){
          categoryNodeIds.add(productCategoryDTO.getIdStr());
        }
      }
    }
    return categoryNodeIds.toArray(new String[categoryNodeIds.size()]);
  }


  /**
   * 只查  2   3  级
   * @param searchWord
   * @param productCategoryDTOList
   * @return
   */
  private static Node getProductCategoryNode(String searchWord,List<ProductCategoryDTO> productCategoryDTOList) {
    Node root = new Node();
    if (CollectionUtils.isEmpty(productCategoryDTOList)) {
      return root;
    }
    List<Node> nodeList = new ArrayList<Node>();

    if(StringUtils.isNotBlank(searchWord)){
      Map<Long,ProductCategoryDTO> productCategoryDTOMap = new HashMap<Long, ProductCategoryDTO>();
      for (ProductCategoryDTO productCategoryDTO : productCategoryDTOList) {
        productCategoryDTOMap.put(productCategoryDTO.getId(), productCategoryDTO);
      }
      Iterator<ProductCategoryDTO> iterator = productCategoryDTOList.iterator();
      while (iterator.hasNext()){
        ProductCategoryDTO productCategoryDTO = iterator.next();
        if (productCategoryDTO.getCategoryType() == ProductCategoryType.SECOND_CATEGORY) {
          if(productCategoryDTO.getName().indexOf(searchWord)==-1){
            iterator.remove();
          }
        } else if (productCategoryDTO.getCategoryType() == ProductCategoryType.THIRD_CATEGORY) {
          if(productCategoryDTO.getName().indexOf(searchWord)==-1 && productCategoryDTO.getSecondCategoryName().indexOf(searchWord)==-1){
            iterator.remove();
          }
        }
      }
      //补三级的父
      for (ProductCategoryDTO productCategoryDTO : new ArrayList<ProductCategoryDTO>(productCategoryDTOList)) {
        if (productCategoryDTO.getCategoryType() == ProductCategoryType.THIRD_CATEGORY) {
          ProductCategoryDTO parent = productCategoryDTOMap.get(productCategoryDTO.getParentId());
          if(!productCategoryDTOList.contains(parent)){
            productCategoryDTOList.add(parent);
          }
        }
      }
    }


    for (ProductCategoryDTO productCategoryDTO : productCategoryDTOList) {
      if (productCategoryDTO.getParentId() == null && productCategoryDTO.getCategoryType() == ProductCategoryType.TOP_CATEGORY) {
        root = productCategoryDTO.toNode();
        root.setLevel(0);
      } else {
        nodeList.add(productCategoryDTO.toNode());
      }
    }

    Map<Long, List<Node>> firstTypeMap = new HashMap<Long, List<Node>>();
    Map<Long, List<Node>> secondTypeMap = new HashMap<Long, List<Node>>();
    Map<Long, List<Node>> thirdTypeMap = new HashMap<Long, List<Node>>();

    List<Node> nodes = new ArrayList<Node>();
    for (ProductCategoryDTO productCategory : productCategoryDTOList) {
      if (productCategory.getParentId() == null && productCategory.getCategoryType() == ProductCategoryType.TOP_CATEGORY) {
        root = productCategory.toNode();
        root.setLevel(0);
      }
      Node node = productCategory.toNode();
      if(node.getType() == null){
        continue;
      }
      nodes.add(node);
      if (productCategory.getCategoryType() == ProductCategoryType.SECOND_CATEGORY) {
        if (secondTypeMap.get(productCategory.getParentId()) == null) {
          nodeList = new ArrayList<Node>();
          secondTypeMap.put(productCategory.getParentId(), nodeList);
        }
        secondTypeMap.get(productCategory.getParentId()).add(node);
      } else if (productCategory.getCategoryType() == ProductCategoryType.THIRD_CATEGORY) {
        if (thirdTypeMap.get(productCategory.getParentId()) == null) {
          nodeList = new ArrayList<Node>();
          thirdTypeMap.put(productCategory.getParentId(), nodeList);
        }
        thirdTypeMap.get(productCategory.getParentId()).add(node);
      } else if (productCategory.getCategoryType() == ProductCategoryType.FIRST_CATEGORY) {
        if (firstTypeMap.get(productCategory.getParentId()) == null) {
          nodeList = new ArrayList<Node>();
          firstTypeMap.put(productCategory.getParentId(), nodeList);
        }
        firstTypeMap.get(productCategory.getParentId()).add(node);
      }
    }

    Iterator<Node> nodeIterator = nodes.iterator();
    while (nodeIterator.hasNext()){
      Node node = nodeIterator.next();
      if (node.getType().toString().equals(ProductCategoryType.FIRST_CATEGORY.toString())) {
        node.setLevel(1);
        nodeList = secondTypeMap.get(node.getId());
        if (CollectionUtils.isNotEmpty(nodeList)) {
          node.setChildren(nodeList);
        }
      } else if (node.getType().toString().equals(ProductCategoryType.SECOND_CATEGORY.toString())) {
        node.setLevel(2);
        nodeList = thirdTypeMap.get(node.getId());
        if (CollectionUtils.isNotEmpty(nodeList)) {
          node.setChildren(nodeList);
        }
        if(StringUtils.isNotBlank(searchWord) && !node.hasChildren()){
          nodeIterator.remove();
        }
      } else if (node.getType().toString().equals(ProductCategoryType.TOP_CATEGORY.toString())) {
        node.setLevel(0);
        nodeList = firstTypeMap.get(node.getId());
        if (CollectionUtils.isNotEmpty(nodeList)) {
          root.setChildren(nodeList);
        }
      }else{
        node.setLevel(3);
      }
    }
    return root;
  }
}
