package com.bcgogo.admin.product;

import com.bcgogo.util.WebUtil;
import com.bcgogo.constant.crm.productCategory.ProductCategoryConstant;
import com.bcgogo.enums.Product.ProductCategoryStatus;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.product.ProductCategory.ProdCategorySearchCondition;
import com.bcgogo.product.ProductCategory.ProdCategorySearchResult;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.NormalProductDTO;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.dto.Node;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品分类管理专用controller
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-12-18
 * Time: 下午9:21
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/productCategory.do")
public class ProductCategoryController {
  private static final Logger LOG = LoggerFactory.getLogger(ProductCategoryController.class);

  //获得分类树形
  @RequestMapping(params = "method=getProductCategory")
  @ResponseBody
  public Object getProductCategory(HttpServletRequest request, HttpServletResponse response) {
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    Node node = null;
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      node = productCategoryService.getProductCategory(shopId);
    } catch (Exception e) {
      LOG.error("/productCategory.do method=getProductCategory");
      LOG.error(e.getMessage(), e);
    }
    return node;
  }

  /**
   * 更新分类
   * @param request
   * @param node
   * @return
   */
  @RequestMapping(params = "method=updateProductCategory")
  @ResponseBody
  public Object updateProductCategory(HttpServletRequest request, Node node) {
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      if(node != null && node.getId() != null && node.getId()  == -1L) {
        result.put("success", true);
        result.put("node", node);
        return result;
      }


      ProductCategoryDTO productCategoryDTO = node.toProductCategoryDTO();
      String errorMessage = "";
      productCategoryDTO.setShopId(shopId);
      productCategoryDTO.setStatus(ProductCategoryStatus.ENABLED);

      if(StringUtil.isEmpty(productCategoryDTO.getName()) || StringUtil.isEmpty(productCategoryDTO.getName().trim())) {
        errorMessage = "该品名为空";
        result.put("duplicate", true);
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      }else if(productCategoryDTO.getName().length() > productCategoryDTO.getName().trim().length()) {
        errorMessage = "该品名有空格";
        result.put("duplicate", true);
        result.put("success", false);
        result.put("message", errorMessage);
        return result;
      }

      List<ProductCategoryDTO> productCategoryDTOList = productCategoryService.getProductCategoryDTOByNameParentId(productCategoryDTO.getShopId(), productCategoryDTO.getName(), productCategoryDTO.getParentId());
      if(CollectionUtils.isNotEmpty(productCategoryDTOList)) {
        if (productCategoryDTO.getId() == null) {
          errorMessage = "该品名已经存在";
          result.put("duplicate",true);
          result.put("success", false);
          result.put("message", errorMessage);
          return result;
        } else if (!productCategoryDTOList.get(0).getId().equals(productCategoryDTO.getId())) {
          errorMessage = "该品名已经存在";
          result.put("duplicate", true);
          result.put("success", false);
          result.put("message", errorMessage);
          return result;

        }
      }

      productCategoryDTO = productCategoryService.saveOrUpdateProductCategoryDTO(productCategoryDTO);
      ServiceManager.getService(IProductSolrWriterService.class).createProductCategorySolrIndex(shopId,productCategoryDTO.getId());
      result.put("success", true);
      result.put("node", productCategoryDTO);
    } catch (Exception e) {
      LOG.error("/productCategory.do method=updateProductCategory");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  /**
   * 根据名字或者id获得分类
   * @param request
   * @param prodCategorySearchCondition
   * @return
   */
  @RequestMapping(params = "method=getProductCategoryByNameOrId")
  @ResponseBody
  public Object getProductCategoryByNameOrId(HttpServletRequest request, ProdCategorySearchCondition prodCategorySearchCondition) {
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    ProdCategorySearchResult prodCategorySearchResult = null;
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null)
        throw new Exception("shopId is null!" + JsonUtil.objectToJson(prodCategorySearchCondition));
      prodCategorySearchCondition.setShopId(shopId);

      prodCategorySearchResult = productCategoryService.getProductCategoryDTOByCondition(prodCategorySearchCondition);
    } catch (Exception e) {
      prodCategorySearchResult = new ProdCategorySearchResult();
      prodCategorySearchResult.setSuccess(false);
      LOG.error("/admin/productCategory.do method=getUsersByDepartmentId " + JsonUtil.objectToJson(prodCategorySearchCondition));
      LOG.error(e.getMessage(), e);
    }
    return prodCategorySearchResult;
  }

  /**
   * 更新分类
   * @param request
   * @param productCategoryDTO
   * @return
   */
  @RequestMapping(params = "method=updateCategoryForm")
  @ResponseBody
  public Object updateCategoryForm(HttpServletRequest request, ProductCategoryDTO productCategoryDTO) {
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");
      productCategoryDTO.setShopId(shopId);

      if (productCategoryDTO == null) {
        throw new Exception("productCategoryDTO is null");
      }

      Long categoryId = productCategoryDTO.getId();

      ProductCategoryType productCategoryType = productCategoryDTO.getCategoryType();
      if (categoryId == null || productCategoryType == null) {
        throw new Exception("categoryId is null or productCategoryType is null" + productCategoryDTO.toString());
      }

      if (ProductCategoryType.THIRD_CATEGORY.equals(productCategoryDTO.getCategoryType())) {

        List<NormalProductDTO> normalProductDTOList = productService.getNormalProductDTOByCategoryId(categoryId);

        if (CollectionUtils.isNotEmpty(normalProductDTOList)) {
          ProductCategoryDTO dbProductCategoryDTO = productCategoryService.getProductCategoryDTOById(shopId, categoryId);

          if (null != dbProductCategoryDTO && StringUtils.isNotBlank(dbProductCategoryDTO.getName()) && !dbProductCategoryDTO.getName().equals(productCategoryDTO.getThirdCategoryName())) {
            result.put("success", false);
            result.put("message", ProductCategoryConstant.RELEVANCE_NORMAL_PRODUCT);
            return result;
          }
        }
      }

      ProductCategoryDTO newProductCategoryDTO = new ProductCategoryDTO();
      newProductCategoryDTO.setShopId(shopId);
      newProductCategoryDTO.setCategoryType(productCategoryType);
      newProductCategoryDTO.setStatus(ProductCategoryStatus.ENABLED);
      newProductCategoryDTO.setId(categoryId);

      result = productCategoryService.validateProductCategoryDTO(productCategoryDTO, newProductCategoryDTO);
      if (!(Boolean) result.get("success")) {
        return result;
      }
      newProductCategoryDTO = productCategoryService.saveOrUpdateProductCategoryDTO(newProductCategoryDTO);
      ServiceManager.getService(IProductSolrWriterService.class).createProductCategorySolrIndex(shopId,newProductCategoryDTO.getId());
      result.put("message", "更新成功!");
      result.put("success", true);
      result.put("node", newProductCategoryDTO);
    } catch (Exception e) {
      LOG.error("/productCategory.do method=updateProductCategory");
      LOG.error(e.getMessage(), e);
      result.put("success", false);
      result.put("message", "操作失败!");
    }
    return result;
  }

  /**
   * 获得一级分类
   * @param request
   * @param productCategoryDTO
   * @return
   */
  @RequestMapping(params = "method=getFirstCategory")
  @ResponseBody
  public Object getFirstCategory(HttpServletRequest request, ProductCategoryDTO productCategoryDTO) {
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    Long shopId = WebUtil.getShopId(request);
    return productCategoryService.getProductCategoryDTOByShopId(shopId);
  }

  /**
   * 获得二级分类
   * @param request
   * @param parameter
   * @return
   */
  @RequestMapping(params = "method=getSecondCategory")
  @ResponseBody
  public Object getSecondCategory(HttpServletRequest request,String parameter) {
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    Long shopId = WebUtil.getShopId(request);

    if (shopId == null) {
      return null;
    }
    Long parentId = null;
    if (StringUtil.isNotEmpty(parameter)) {
      List<ProductCategoryDTO> productCategoryDTOList = productCategoryService.getProductCategoryDTOByName(shopId, parameter);
      if (CollectionUtils.isNotEmpty(productCategoryDTOList)) {
        parentId = productCategoryDTOList.get(0).getId();
      }

    }
    return productCategoryService.getSecondCategoryDTOByParentId(shopId, parentId);

  }

  /**
   * 增加分类
   * @param request
   * @param productCategoryDTO
   * @return
   */
  @RequestMapping(params = "method=addProductCategory")
  @ResponseBody
  public Object addProductCategory(HttpServletRequest request ,ProductCategoryDTO productCategoryDTO) {
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    String errorMessage = "";
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId is null!");

      productCategoryDTO.setShopId(shopId);
      productCategoryDTO.setStatus(ProductCategoryStatus.ENABLED);

      if (productCategoryDTO.getCategoryType() == null) {
        result.put("success", false);
        errorMessage = "请先选择商品类别";
        result.put("message", errorMessage);
        return result;
      }

      ProductCategoryDTO newProductCategoryDTO = new ProductCategoryDTO();
      newProductCategoryDTO.setShopId(shopId);

      result = productCategoryService.validateProductCategoryDTO(productCategoryDTO, newProductCategoryDTO);
      if(!(Boolean)result.get("success")) {
        return result;
      }

      newProductCategoryDTO.setStatus(ProductCategoryStatus.ENABLED);
      productCategoryDTO = productCategoryService.saveOrUpdateProductCategoryDTO(newProductCategoryDTO);
      ServiceManager.getService(IProductSolrWriterService.class).createProductCategorySolrIndex(shopId,productCategoryDTO.getId());
      result.put("success", true);
      result.put("message", "添加成功!");
    } catch (Exception e) {
      result.put("success", false);
      errorMessage = "添加失败!";
      LOG.error("/productCategory.do method=updateProductCategory");
      LOG.error(e.getMessage(), e);
      result.put("message", errorMessage);
    }
    return result;
  }

}
