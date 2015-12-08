package com.bcgogo.supplier;

import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.ProductCategoryRelationDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.ProductQueryCondition;
import com.bcgogo.product.productCategoryCache.ProductCategoryCache;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: terry
 * Date: 13-8-1
 * Time: 下午1:42
 * To change this template use File | Settings | File Templates.
 */
@Service("shopProductCategoryHelper")
public class ShopProductCategoryHelper {

  public static final Logger LOG = LoggerFactory.getLogger(ShopProductCategoryHelper.class);

  public List<Node> getSimpleJsonShop2nd3rdProductCategorys(Long shopId) throws Exception {
    List<Node> results = new ArrayList<Node>();
    List<Long> inSaleProductLocalInfoIds = new ArrayList<Long>();
    IProductService productService = ServiceManager.getService(IProductService.class);
    IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
    ProductQueryCondition productQueryCondition = new ProductQueryCondition();
    productQueryCondition.setShopId(shopId);
    productQueryCondition.setProductStatus(ProductStatus.InSales);
    List<ProductDTO> productDTOs = productService.getProductsByCondition(productQueryCondition);
    if (CollectionUtils.isNotEmpty(productDTOs)) {
      for (ProductDTO productDTO : productDTOs) {
        inSaleProductLocalInfoIds.add(productDTO.getProductLocalInfoId());
      }
    }

    List<Long> productCategoryIds = new ArrayList<Long>();
    List<ProductCategoryRelationDTO> productCategoryRelationDTOs = ServiceManager.getService(IProductCategoryService.class).productCategoryRelationDTOQuery(shopId, inSaleProductLocalInfoIds.toArray(new Long[inSaleProductLocalInfoIds.size()]));
    if (CollectionUtils.isNotEmpty(productCategoryRelationDTOs)) {
      for (ProductCategoryRelationDTO productCategoryRelationDTO : productCategoryRelationDTOs) {
        if(!productCategoryIds.contains(productCategoryRelationDTO.getProductCategoryId())){
          productCategoryIds.add(productCategoryRelationDTO.getProductCategoryId());
        }
      }
    }

    if (CollectionUtils.isNotEmpty(productCategoryIds)) {
      List<ProductCategoryDTO> parentCategories = new ArrayList<ProductCategoryDTO>();
      for (Long id : productCategoryIds) {
        ProductCategoryDTO productCategoryDTO = ProductCategoryCache.getProductCategoryDTOById(id);
        if(productCategoryDTO == null){
          productCategoryDTO = productCategoryService.getProductCategoryDTOById(shopId, id);
          if(productCategoryDTO == null){
            continue;
          }
        }
        if(parentCategories.contains(productCategoryDTO)){
          continue;
        }
        Node node = new Node();
        node.fromProductCategoryDTO(productCategoryDTO);
        if (productCategoryDTO.getCategoryType() == ProductCategoryType.THIRD_CATEGORY) {
          ProductCategoryDTO parentCategoryDTO = ProductCategoryCache.getProductCategoryDTOById(productCategoryDTO.getParentId());
          if(!parentCategories.contains(parentCategoryDTO)){
            parentCategories.add(parentCategoryDTO);
            Node parentNode = new Node();
            parentNode.fromProductCategoryDTO(parentCategoryDTO);
            results.add(parentNode);
          }
        } else if ( productCategoryDTO.getCategoryType() == ProductCategoryType.SECOND_CATEGORY){
          parentCategories.add(productCategoryDTO);
        }
        results.add(node);
      }
    }
    return results;
  }

}