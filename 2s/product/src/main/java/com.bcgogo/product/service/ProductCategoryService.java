package com.bcgogo.product.service;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.RecentlyUsedDataDTO;
import com.bcgogo.config.dto.ShopBusinessScopeDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IRecentlyUsedDataService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.crm.productCategory.ProductCategoryConstant;
import com.bcgogo.enums.Product.ProductCategoryStatus;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.enums.config.RecentlyUsedDataType;
import com.bcgogo.product.ProductCategory.ProdCategorySearchCondition;
import com.bcgogo.product.ProductCategory.ProdCategorySearchResult;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.ProductCategoryRelationDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.ProductCategory;
import com.bcgogo.product.model.ProductCategoryRelation;
import com.bcgogo.product.model.ProductDaoManager;
import com.bcgogo.product.model.ProductWriter;
import com.bcgogo.search.service.suggestion.ISearchSuggestionService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.CheckNode;
import com.bcgogo.product.productCategoryCache.ProductCategoryCache;
import com.bcgogo.user.dto.Node;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-12-18
 * Time: 下午11:55
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProductCategoryService implements IProductCategoryService {

  @Autowired
  private ProductDaoManager productDaoManager;

  @Override
  public Node getProductCategory(Long shopId) {
    Node root = new Node();
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductCategory> productCategoryList = writer.getProductCategoryByShopId(shopId);
    if (CollectionUtils.isEmpty(productCategoryList)) {
      return root;
    }
    List<Node> nodeList = null;

    Map<Long, List<Node>> secondTypeMap = new HashMap<Long, List<Node>>();
    for (ProductCategory productCategory : productCategoryList) {
      if (productCategory.getParentId() == null && productCategory.getCategoryType() == ProductCategoryType.TOP_CATEGORY) {
        root = productCategory.toNode();
      }
      if (productCategory.getCategoryType() == ProductCategoryType.SECOND_CATEGORY) {
        if (secondTypeMap.get(productCategory.getParentId()) == null) {
          nodeList = new ArrayList<Node>();
          secondTypeMap.put(productCategory.getParentId(), nodeList);
        }
        Node node = productCategory.toNode();
        secondTypeMap.get(productCategory.getParentId()).add(node);
      }
    }
    List<Node> nodes = new ArrayList<Node>();
    for (ProductCategory productCategory : productCategoryList) {
      if (productCategory.getCategoryType() == ProductCategoryType.FIRST_CATEGORY) {
        nodeList = secondTypeMap.get(productCategory.getId());
        Node node = productCategory.toNode();
        if (CollectionUtils.isNotEmpty(nodeList)) {
          node.setChildren(nodeList);
        }
        nodes.add(node);
      }
    }

    root.mergeAndBuildTree(root, nodes);
    return root;
  }

  public ProductCategoryDTO saveOrUpdateProductCategoryDTO(ProductCategoryDTO productCategoryDTO) {
    if (productCategoryDTO == null) {
      return productCategoryDTO;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    ProductCategory productCategory = null;
    try {
      if (productCategoryDTO.getId() == null) {
        productCategory = new ProductCategory(productCategoryDTO);
        writer.save(productCategory);
      } else {
        productCategory = writer.getById(ProductCategory.class, productCategoryDTO.getId());
        productCategory = productCategory.fromDTO(productCategoryDTO, false);
        writer.saveOrUpdate(productCategory);
      }
      writer.commit(status);
      productCategoryDTO = productCategory.toDTO();
    } finally {
      writer.rollback(status);
    }
    return productCategoryDTO;
  }

  public List<ProductCategoryDTO> getProductCategoryByNameOrId(Long productCategoryId, String name, Long shopId, Pager pager) {
    ProductWriter productWriter = productDaoManager.getWriter();
    List<ProductCategory> productCategoryList = null;

    if (productCategoryId == null && StringUtil.isEmpty(name)) {
      productCategoryList = productWriter.getProductCategoryFuzzyName(shopId, null, pager);
      List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();
      if(CollectionUtils.isNotEmpty(productCategoryList)){
        for(ProductCategory productCategory : productCategoryList){
          productCategoryDTOList.add(productCategory.toDTO());
        }
      }
      return productCategoryDTOList;
    }

    if (StringUtil.isNotEmpty(name)) {
      productCategoryList = productWriter.getProductCategoryFuzzyName(shopId, name, pager);
      List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();
      if(CollectionUtils.isNotEmpty(productCategoryList)){
        for(ProductCategory productCategory : productCategoryList){
          productCategoryDTOList.add(productCategory.toDTO());
        }
      }
      return productCategoryDTOList;
    }

    if (productCategoryId != null) {
      List<ProductCategoryDTO> productCategoryDTOList = this.getProductCategoryDTOByParentId(shopId, productCategoryId, pager);
      return productCategoryDTOList;
    }

    return null;
  }


  @Override//todo 该方法有严重的性能问题，需要优化 add by qxy
  public List<ProductCategoryDTO> fillProductCategoryDTOListInfo(List<ProductCategoryDTO> productCategoryDTOList) {
    if (CollectionUtils.isEmpty(productCategoryDTOList)) {
      return null;
    }
    for (ProductCategoryDTO productCategoryDTO : productCategoryDTOList) {
      this.fillProductCategoryDTOInfo(productCategoryDTO);
    }
    return productCategoryDTOList;
  }

  @Override
  public List<ProductCategoryRelationDTO> productCategoryRelationDTOQuery(Long shopId, Long... productLocalInfoIds) {
    List<ProductCategoryRelationDTO> result = new ArrayList<ProductCategoryRelationDTO>();
    if (ArrayUtils.isEmpty(productLocalInfoIds) || shopId==null) {
      return result;
    }
    ProductWriter productWriter = productDaoManager.getWriter();
    List<ProductCategoryRelation> productCategoryRelations =  productWriter.getProductCategoryRelations(shopId,productLocalInfoIds);
    if (CollectionUtils.isNotEmpty(productCategoryRelations)) {
      for (ProductCategoryRelation productCategoryRelation : productCategoryRelations) {
          result.add(productCategoryRelation.toDTO());
      }
    }
    return result;
  }

  @Override
  public Map<Long, Long> getProductCategoryRelationMap(Long shopId, Long... productLocalInfoIds) {
    Map<Long,Long> productCategoryRelationMap = new HashMap<Long, Long>();
    List<ProductCategoryRelationDTO> productCategoryRelationDTOList = productCategoryRelationDTOQuery(shopId, productLocalInfoIds);
    if(CollectionUtils.isNotEmpty(productCategoryRelationDTOList)){
      for(ProductCategoryRelationDTO productCategoryRelationDTO:productCategoryRelationDTOList){
        productCategoryRelationMap.put(productCategoryRelationDTO.getProductLocalInfoId(),productCategoryRelationDTO.getProductCategoryId());
      }
    }
    return productCategoryRelationMap;
  }

  @Override
  public ProductCategoryDTO fillProductCategoryDTOInfo(ProductCategoryDTO productCategoryDTO) {
    if (productCategoryDTO == null) {
      return null;
    }
    ProductWriter productWriter = productDaoManager.getWriter();
    if (productCategoryDTO.getCategoryType() == ProductCategoryType.TOP_CATEGORY || productCategoryDTO.getCategoryType() == ProductCategoryType.FIRST_CATEGORY) {
      productCategoryDTO.setFirstCategoryId(productCategoryDTO.getId());
      productCategoryDTO.setFirstCategoryName(productCategoryDTO.getName());
    } else if (productCategoryDTO.getCategoryType() == ProductCategoryType.SECOND_CATEGORY) {

      productCategoryDTO.setSecondCategoryId(productCategoryDTO.getId());
      productCategoryDTO.setSecondCategoryName(productCategoryDTO.getName());

      ProductCategory firstProductCategory = productWriter.getById(ProductCategory.class, productCategoryDTO.getParentId());
      productCategoryDTO.setFirstCategoryId(firstProductCategory.getId());
      productCategoryDTO.setFirstCategoryName(firstProductCategory.getName());

    } else if (productCategoryDTO.getCategoryType() == ProductCategoryType.THIRD_CATEGORY) {

      productCategoryDTO.setThirdCategoryName(productCategoryDTO.getName());
      productCategoryDTO.setThirdCategoryId(productCategoryDTO.getId());

      ProductCategory secondProductCategory = productWriter.getById(ProductCategory.class, productCategoryDTO.getParentId());
      productCategoryDTO.setSecondCategoryId(secondProductCategory.getId());
      productCategoryDTO.setSecondCategoryName(secondProductCategory.getName());

      ProductCategory firstProductCategory = productWriter.getById(ProductCategory.class, secondProductCategory.getParentId());
      productCategoryDTO.setFirstCategoryId(firstProductCategory.getId());
      productCategoryDTO.setFirstCategoryName(firstProductCategory.getName());
    }

    return productCategoryDTO;
  }


  public ProdCategorySearchResult getProductCategoryDTOByCondition(ProdCategorySearchCondition prodCategorySearchCondition) {
    if (prodCategorySearchCondition == null) {
      return null;
    }
    ProductWriter productWriter = productDaoManager.getWriter();
    ProdCategorySearchResult prodCategorySearchResult = new ProdCategorySearchResult();

    int totalRows = this.countCategoryByNameOrId(productWriter, prodCategorySearchCondition.getProductCategoryId(), prodCategorySearchCondition.getProductCategoryName(), prodCategorySearchCondition.getShopId());
    prodCategorySearchResult.setTotalRows(totalRows);

    if (prodCategorySearchCondition.isHasPager()) {
      Pager pager = new Pager();
      pager.setRowStart(prodCategorySearchCondition.getStart());
      pager.setPageSize(prodCategorySearchCondition.getLimit());
      prodCategorySearchCondition.setPager(pager);
    }
    List<ProductCategoryDTO> productCategoryDTOList = this.getProductCategoryByNameOrId(prodCategorySearchCondition.getProductCategoryId(), prodCategorySearchCondition.getProductCategoryName(), prodCategorySearchCondition.getShopId(), prodCategorySearchCondition.getPager());
    if (CollectionUtils.isEmpty(productCategoryDTOList)) {
      return null;
    }
    this.fillProductCategoryDTOListInfo(productCategoryDTOList);

    prodCategorySearchResult.setResults(productCategoryDTOList);
    prodCategorySearchResult.setSuccess(true);
    return prodCategorySearchResult;

  }

  public List<ProductCategoryDTO> getProductCategoryDTOByShopId(Long shopId) {
    List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();
    ProductWriter productWriter = productDaoManager.getWriter();
    List<ProductCategory> productCategoryList = productWriter.getProductCategoryByShopId(shopId);
    if(CollectionUtils.isNotEmpty(productCategoryList)){
      for(ProductCategory productCategory : productCategoryList){
        productCategoryDTOList.add(productCategory.toDTO());
      }
    }
    return productCategoryDTOList;
  }

  public List<ProductCategoryDTO> getProductCategoryDTOByParentId(Long shopId, Long parentId, Pager pager) {
    List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();
    ProductWriter productWriter = productDaoManager.getWriter();
    List<ProductCategory> productCategoryList = productWriter.getProductCategoryByParentId(shopId, parentId, pager);
    if(CollectionUtils.isNotEmpty(productCategoryList)){
      for(ProductCategory productCategory : productCategoryList){
        productCategoryDTOList.add(productCategory.toDTO());
      }
    }
    return productCategoryDTOList;
  }

  public ProductCategoryDTO getProductCategoryDTOById(Long shopId, Long id) {
    if (id == null) {
      return null;
    }
    ProductWriter productWriter = productDaoManager.getWriter();
    return productWriter.getById(ProductCategory.class, id).toDTO();
  }

  public List<ProductCategoryDTO> getProductCategoryDTOByNameParentId(Long shopId, String name, Long parentId) {
    List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();
    ProductWriter productWriter = productDaoManager.getWriter();
    List<ProductCategory> productCategoryList = productWriter.getProductCategoryByNameParentId(shopId, name, parentId);
    if(CollectionUtils.isNotEmpty(productCategoryList)){
      for(ProductCategory productCategory : productCategoryList){
        productCategoryDTOList.add(productCategory.toDTO());
      }
    }
    return productCategoryDTOList;
  }

  public List<ProductCategoryDTO> getProductCategoryDTOByName(Long shopId, String name) {
    List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();
    ProductWriter productWriter = productDaoManager.getWriter();
    List<ProductCategory> productCategoryList = productWriter.getProductCategoryByName(shopId, name);
    if(CollectionUtils.isNotEmpty(productCategoryList)){
      for(ProductCategory productCategory : productCategoryList){
        productCategoryDTOList.add(productCategory.toDTO());
      }
    }
    return productCategoryDTOList;
  }

  public Map<String, Object> validateProductCategoryDTO(ProductCategoryDTO productCategoryDTO, ProductCategoryDTO newProductCategoryDTO) {

    String errorMessage = "";
    Map<String, Object> result = new HashMap<String, Object>();


    result = this.validateCategoryName(productCategoryDTO, ProductCategoryType.FIRST_CATEGORY);
    if (!(Boolean) result.get("success")) {
      return result;
    }

    Long parentId = null;

    if (productCategoryDTO.getCategoryType() == ProductCategoryType.THIRD_CATEGORY || productCategoryDTO.getCategoryType() == ProductCategoryType.SECOND_CATEGORY) {
      result = this.validateCategoryName(productCategoryDTO, ProductCategoryType.SECOND_CATEGORY);
      if (!(Boolean) result.get("success")) {
        return result;
      }
      List<ProductCategoryDTO> productCategoryDTOList = this.getProductCategoryDTOByNameParentId(productCategoryDTO.getShopId(), productCategoryDTO.getFirstCategoryName(), -1L);
      if (CollectionUtils.isEmpty(productCategoryDTOList)) {
        errorMessage = ProductCategoryConstant.FIRST_CATEGORY_NO_EXIST;
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      } else if (productCategoryDTOList.size() > 1) {
        errorMessage = ProductCategoryConstant.FIRST_CATEGORY_ONE_MORE;
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      }
      parentId = productCategoryDTOList.get(0).getId();
    }


    if (productCategoryDTO.getCategoryType() == ProductCategoryType.FIRST_CATEGORY) {

      newProductCategoryDTO.setName(productCategoryDTO.getFirstCategoryName());
      newProductCategoryDTO.setCategoryType(ProductCategoryType.FIRST_CATEGORY);
      newProductCategoryDTO.setParentId(-1L);
    } else if (productCategoryDTO.getCategoryType() == ProductCategoryType.SECOND_CATEGORY) {

      newProductCategoryDTO.setParentId(parentId);

      List<ProductCategoryDTO> productCategoryDTOList = this.getProductCategoryDTOByNameParentId(productCategoryDTO.getShopId(), productCategoryDTO.getSecondCategoryName(), newProductCategoryDTO.getParentId());
      if (CollectionUtils.isNotEmpty(productCategoryDTOList)) {
        if (newProductCategoryDTO.getId() == null) {
          errorMessage = ProductCategoryConstant.SECOND_CATEGORY_EXIST;
          result.put("success", false);
          result.put("message", errorMessage);
          return result;
        } else if (!productCategoryDTOList.get(0).getId().equals(newProductCategoryDTO.getId())) {
          errorMessage = ProductCategoryConstant.SECOND_CATEGORY_EXIST;
          result.put("success", false);
          result.put("message", errorMessage);
          return result;
        }
      }
      newProductCategoryDTO.setName(productCategoryDTO.getSecondCategoryName());
      newProductCategoryDTO.setCategoryType(ProductCategoryType.SECOND_CATEGORY);
    } else if (productCategoryDTO.getCategoryType() == ProductCategoryType.THIRD_CATEGORY) {
      result = this.validateCategoryName(productCategoryDTO, ProductCategoryType.THIRD_CATEGORY);
      if (!(Boolean) result.get("success")) {
        return result;
      }

      List<ProductCategoryDTO> productCategoryDTOList = this.getProductCategoryDTOByNameParentId(productCategoryDTO.getShopId(), productCategoryDTO.getSecondCategoryName(), parentId);
      if (CollectionUtils.isEmpty(productCategoryDTOList)) {
        errorMessage = ProductCategoryConstant.SECOND_CATEGORY_NO_EXIST;
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      } else if (productCategoryDTOList.size() > 1) {
        errorMessage = ProductCategoryConstant.SECOND_CATEGORY_ONE_MORE;
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      }
      newProductCategoryDTO.setParentId(productCategoryDTOList.get(0).getId());

      productCategoryDTOList = this.getProductCategoryDTOByNameParentId(productCategoryDTO.getShopId(), productCategoryDTO.getThirdCategoryName(), productCategoryDTOList.get(0).getId());
      if (CollectionUtils.isNotEmpty(productCategoryDTOList)) {

        if (newProductCategoryDTO.getId() == null) {
          errorMessage = ProductCategoryConstant.THIRD_CATEGORY_EXIST;
          result.put("success", false);
          result.put("message", errorMessage);
          return result;
        } else if (!productCategoryDTOList.get(0).getId().equals(newProductCategoryDTO.getId())) {
          errorMessage = ProductCategoryConstant.THIRD_CATEGORY_EXIST;
          result.put("success", false);
          result.put("message", errorMessage);
          return result;
        }
      }

      newProductCategoryDTO.setName(productCategoryDTO.getThirdCategoryName());
      newProductCategoryDTO.setCategoryType(ProductCategoryType.THIRD_CATEGORY);
    } else {
      errorMessage = ProductCategoryConstant.PRODUCT_CATEGORY_ERROR;
      result.put("success", false);
      result.put("message", errorMessage);
      return result;
    }

    List<ProductCategoryDTO> productCategoryDTOList = this.getProductCategoryDTOByNameParentId(productCategoryDTO.getShopId(), newProductCategoryDTO.getName(), newProductCategoryDTO.getParentId());
    if (CollectionUtils.isNotEmpty(productCategoryDTOList)) {
      if (newProductCategoryDTO.getId() == null) {
        errorMessage = ProductCategoryConstant.THIRD_CATEGORY_EXIST;
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      } else if (!productCategoryDTOList.get(0).getId().equals(newProductCategoryDTO.getId())) {
        errorMessage = ProductCategoryConstant.THIRD_CATEGORY_EXIST;
        result.put("success", false);
        result.put("message", errorMessage);
        return result;

      }
    }
    result.put("success", true);
    result.put("message", errorMessage);
    return result;
  }

  public Map<String, Object> validateCategoryName(ProductCategoryDTO productCategoryDTO, ProductCategoryType productCategoryType) {
    Map<String, Object> result = new HashMap<String, Object>();
    String errorMessage = "";

    if (productCategoryType == ProductCategoryType.FIRST_CATEGORY) {

      if (StringUtils.isEmpty(productCategoryDTO.getFirstCategoryName())) {
        errorMessage = ProductCategoryConstant.FIRST_CATEGORY_EMPTY;
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      }
      if (productCategoryDTO.getFirstCategoryName().length() > productCategoryDTO.getFirstCategoryName().trim().length()) {
        errorMessage = ProductCategoryConstant.FIRST_CATEGORY_CONTAIN_SPACE;
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      }
    } else if (productCategoryType == ProductCategoryType.SECOND_CATEGORY) {
      if (StringUtils.isEmpty(productCategoryDTO.getSecondCategoryName())) {
        errorMessage = ProductCategoryConstant.SECOND_CATEGORY_EMPTY;
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      }
      if (productCategoryDTO.getSecondCategoryName().length() > productCategoryDTO.getSecondCategoryName().trim().length()) {
        errorMessage = ProductCategoryConstant.SECOND_CATEGORY_CONTAIN_SPACE;
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      }
    } else if (productCategoryType == ProductCategoryType.THIRD_CATEGORY) {
      if (StringUtils.isEmpty(productCategoryDTO.getThirdCategoryName())) {
        errorMessage = ProductCategoryConstant.THIRD_CATEGORY_EMPTY;
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      }
      if (productCategoryDTO.getThirdCategoryName().length() > productCategoryDTO.getThirdCategoryName().trim().length()) {
        errorMessage = ProductCategoryConstant.THIRD_CATEGORY_CONTAIN_SPACE;
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      }
    }

    result.put("success", true);
    result.put("message", ProductCategoryConstant.VALIDATE_SUCCESS);
    return result;

  }

  public int countCategoryByNameOrId(ProductWriter productWriter, Long productCategoryId, String name, Long shopId) {

    if (productCategoryId == null && StringUtil.isEmpty(name)) {
      List<ProductCategory> productCategoryList = productWriter.getProductCategoryFuzzyName(shopId, null, null);
      return CollectionUtils.isEmpty(productCategoryList) ? 0 : productCategoryList.size();
    }


    if (StringUtil.isNotEmpty(name)) {
      List<ProductCategory> productCategoryList = productWriter.getProductCategoryFuzzyName(shopId, name, null);
      return CollectionUtils.isEmpty(productCategoryList) ? 0 : productCategoryList.size();
    }

    if (productCategoryId != null) {

      List<ProductCategoryDTO> productCategoryDTOList = this.getProductCategoryDTOByParentId(shopId, productCategoryId, null);
      return CollectionUtils.isEmpty(productCategoryDTOList) ? 0 : productCategoryDTOList.size();
    }
    return 0;
  }

  public List<ProductCategoryDTO> getSecondCategoryDTOByParentId(Long shopId, Long parentId) {
    List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();
    ProductWriter productWriter = productDaoManager.getWriter();
    List<ProductCategory> productCategoryList = productWriter.getSecondCategoryByParentId(shopId, parentId);
    if(CollectionUtils.isNotEmpty(productCategoryList)){
      for(ProductCategory productCategory : productCategoryList){
        productCategoryDTOList.add(productCategory.toDTO());
      }
    }
    return productCategoryDTOList;
  }

  @Override
  public Node getBusinessScopeByShopId(Long shopId) {
    List<ProductCategoryDTO> productCategoryDTOList = ServiceManager.getService(IProductCategoryService.class)
        .getProductCategoryDTOByShopId(ShopConstant.BC_ADMIN_SHOP_ID);

    //经营范围
    Set<Long> shopIdSet = new HashSet<Long>();
    shopIdSet.add(shopId);
    List<ShopBusinessScopeDTO> shopBusinessScopeDTOList = ServiceManager.getService(IConfigService.class)
        .getShopBusinessScopeByShopId(shopIdSet);
    Map<Long, Object> map = new HashMap<Long, Object>();

    for (ShopBusinessScopeDTO dto : shopBusinessScopeDTOList) {
      map.put(dto.getProductCategoryId(), dto);
    }

    CheckNode root = (CheckNode) buildCheckProductCategoryTree(productCategoryDTOList, map);
    root.reBuildTreeForChecked();
    return root;
  }

  @Override
  public Node getCheckedBusinessScope(Long shopId, Set<Long> ids) {
    List<ProductCategoryDTO> productCategoryDTOList = this.getProductCategoryDTOByShopId(ShopConstant.BC_ADMIN_SHOP_ID);

    //经营范围
    List<ProductCategoryDTO> shopBusinessScopeDTOList = this.getProductCategoryDTOByIds(ids);
    Map<Long, Object> map = new HashMap<Long, Object>();
    for (ProductCategoryDTO dto : shopBusinessScopeDTOList) {
      map.put(dto.getId(), dto);
    }
    CheckNode root = (CheckNode) buildCheckProductCategoryTree(productCategoryDTOList, map);
    root.reBuildTreeForChecked();
    return root;
  }

  @Override
  public List<ProductCategoryDTO> getProductCategoryDTOByIds(Set<Long> ids) {
    List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();
    if (CollectionUtils.isEmpty(ids)) return productCategoryDTOList;
    ProductWriter productWriter = productDaoManager.getWriter();
    List<ProductCategory> entityList = productWriter.getProductCategoryByIds(ids);
    for (ProductCategory entity : entityList) {
      productCategoryDTOList.add(entity.toDTO());
    }
    return productCategoryDTOList;
  }

  private Node buildCheckProductCategoryTree(List<ProductCategoryDTO> productCategoryDTOList,
                                             Map<Long, Object> productCategoryMap) {
    Node root = new Node();
    if (CollectionUtils.isEmpty(productCategoryDTOList)) return root;
    Node node;
    List<Node> nodeList = new ArrayList<Node>();
    for (ProductCategoryDTO dto : productCategoryDTOList) {
      dto.setChecked(productCategoryMap.get(dto.getId()) != null);
      node = dto.toCheckNode();
      if (dto.getParentId() == null) {
        root = node;
        continue;
      }
      nodeList.add(node);
    }
    root.buildTree(root, nodeList);
    return root;
  }

   public List<ProductCategoryDTO> getThirdProductCategoryDTOByName(Long shopId, String name) {
     ProductWriter productWriter = productDaoManager.getWriter();
     List<ProductCategory> productCategoryList = productWriter.getThirdProductCategoryByName(shopId, name);
     List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();

     if (CollectionUtils.isEmpty(productCategoryList)) {
       return productCategoryDTOList;
     }
     for (ProductCategory productCategory : productCategoryList) {
       ProductCategoryDTO productCategoryDTO = productCategory.toDTO();

       ProductCategoryDTO secondProductCategory = ProductCategoryCache.getProductCategoryDTOById(productCategoryDTO.getParentId());
       if (secondProductCategory != null) {
         productCategoryDTO.setSecondCategoryName(secondProductCategory.getName());
         productCategoryDTO.setSecondCategoryId(secondProductCategory.getId());
       }
       productCategoryDTOList.add(productCategoryDTO);
     }
     return productCategoryDTOList;

   }

  @Override
  public void saveProductCategoryRelation(Long shopId, Long productCategoryId, Long productLocalInfoId){
    ProductWriter productWriter = productDaoManager.getWriter();
    Object status = productWriter.begin();
    try {
      ProductCategoryRelation productCategoryRelation = CollectionUtil.getFirst(productWriter.getProductCategoryRelations(shopId,productLocalInfoId));
      if(productCategoryRelation==null){
        productCategoryRelation = new ProductCategoryRelation();
        productCategoryRelation.setShopId(shopId);
        productCategoryRelation.setProductLocalInfoId(productLocalInfoId);
      }

      productCategoryRelation.setProductCategoryId(productCategoryId);
      productWriter.saveOrUpdate(productCategoryRelation);
      productWriter.commit(status);
    } finally {
      productWriter.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateRecentlyUsedProductCategory(Long shopId,Long userId, Long productCategoryId) {
    if(shopId==null || productCategoryId==null) return;
    IRecentlyUsedDataService recentlyUsedDataService = ServiceManager.getService(IRecentlyUsedDataService.class);
    recentlyUsedDataService.saveOrUpdateRecentlyUsedData(shopId,userId, RecentlyUsedDataType.USED_PRODUCT_CATEGORY,productCategoryId);
  }
  @Override
  public List<ProductCategoryDTO> getRecentlyUsedProductCategoryDTOList(Long shopId,Long userId){
    List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();
    if(shopId==null) return productCategoryDTOList;
    IRecentlyUsedDataService recentlyUsedDataService = ServiceManager.getService(IRecentlyUsedDataService.class);
    int maxSize = ConfigUtils.getRecentlyUsedProductCategoryNum();
    List<RecentlyUsedDataDTO> recentlyUsedDataDTOList = recentlyUsedDataService.getRecentlyUsedDataDTOList(shopId,userId, RecentlyUsedDataType.USED_PRODUCT_CATEGORY,maxSize);
    if(CollectionUtils.isNotEmpty(recentlyUsedDataDTOList)){
      Set<Long> prouctCategoryIdSet = new HashSet<Long>();
      for(RecentlyUsedDataDTO recentlyUsedDataDTO : recentlyUsedDataDTOList){
        prouctCategoryIdSet.add(recentlyUsedDataDTO.getDataId());
      }
      List<ProductCategoryDTO> productCategoryDTOs = getProductCategoryDTOByIds(prouctCategoryIdSet);
      fillProductCategoryDTOListInfo(productCategoryDTOs);
      Map<Long,ProductCategoryDTO> productCategoryDTOMap = new HashMap<Long, ProductCategoryDTO>();
      for(ProductCategoryDTO productCategoryDTO : productCategoryDTOs){
        productCategoryDTOMap.put(productCategoryDTO.getId(),productCategoryDTO);
      }
      ProductCategoryDTO productCategoryDTO = null;
      for(RecentlyUsedDataDTO recentlyUsedDataDTO : recentlyUsedDataDTOList){
        productCategoryDTO = productCategoryDTOMap.get(recentlyUsedDataDTO.getDataId());
        if(productCategoryDTO!=null){
          productCategoryDTOList.add(productCategoryDTO);
        }
      }
    }
    return productCategoryDTOList;
  }

  @Override
  public List<Long> getProductCategoryIdsByShopId(Long shopId,int start,int pageSize){
    ProductWriter productWriter = productDaoManager.getWriter();
    return productWriter.getProductCategoryIdsByShopId(shopId,start,pageSize);
  }
}
