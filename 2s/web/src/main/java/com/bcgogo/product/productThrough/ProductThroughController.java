package com.bcgogo.product.productThrough;

import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.constant.productThrough.ProductThroughConstant;
import com.bcgogo.enums.CategoryType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.ProductThroughSearchDTO;
import com.bcgogo.search.dto.ProductThroughSearchResultListDTO;
import com.bcgogo.search.dto.SearchMemoryConditionDTO;import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.CategoryDTO;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.txn.dto.StoreHouseDTO;
import com.bcgogo.txn.service.IStoreHouseService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品库存打通专用controller
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-4-18
 * Time: 上午11:56
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/productThrough.do")
public class ProductThroughController {
  private static final Logger LOG = LoggerFactory.getLogger(ProductThroughController.class);

  @RequestMapping(params = "method=redirectProductThroughDetail")
  public String redirectProductThroughDetail(ModelMap model, HttpServletRequest request,ProductThroughSearchDTO productThroughSearchDTO) {
    Long shopId = WebUtil.getShopId(request);
    if(productThroughSearchDTO == null) {
      return "/product/productThroughDetail";
    }
    try {
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);

        if (CollectionUtil.isNotEmpty(storeHouseDTOList)) {
          List<StoreHouseDTO> storeHouseDTOs = new ArrayList<StoreHouseDTO>();
          for (int index = 0; index < storeHouseDTOList.size(); index++) {
            storeHouseDTOs.add(storeHouseDTOList.get(index));
          }
          model.addAttribute("storeHouseDTOList", storeHouseDTOs);
        }
      }
      if(NumberUtil.isLongNumber(productThroughSearchDTO.getCustomerId())){
        CustomerDTO customerDTO = ServiceManager.getService(ICustomerService.class).getCustomerById(NumberUtil.longValue(productThroughSearchDTO.getCustomerId()),shopId);
        if(customerDTO!=null){
          model.addAttribute("customerId",customerDTO.getId());
          model.addAttribute("customerName",customerDTO.getName());
        }
      }
      if(NumberUtil.isLongNumber(productThroughSearchDTO.getSupplierId())){
        SupplierDTO supplierDTO = ServiceManager.getService(ISupplierService.class).getSupplierById(NumberUtil.longValue(productThroughSearchDTO.getSupplierId()),shopId);
        if(supplierDTO!=null){
          model.addAttribute("supplierId",supplierDTO.getId());
          model.addAttribute("supplierName",supplierDTO.getName());
        }
      }
      if(NumberUtil.isLongNumber(productThroughSearchDTO.getProductId())){
        ProductDTO productDTO = ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(NumberUtil.longValue(productThroughSearchDTO.getProductId()), shopId);
        if(productDTO!=null){
          model.addAttribute("productId",productDTO.getProductLocalInfoId());
          model.addAttribute("productName",productDTO.getName());
          model.addAttribute("productBrand",productDTO.getBrand());
          model.addAttribute("productSpec",productDTO.getSpec());
          model.addAttribute("productModel",productDTO.getModel());
          model.addAttribute("productVehicleBrand",productDTO.getProductVehicleBrand());
          model.addAttribute("productVehicleModel",productDTO.getProductVehicleModel());
          model.addAttribute("commodityCode",productDTO.getCommodityCode());
        }
      }
    } catch (Exception e) {
      LOG.error("productThrough.redirectProductThroughDetail");
      LOG.error(e.getMessage(), e);
    }


    return "/product/productThroughDetail";
  }

  /**
   * 获取商品出入查询结果记录
   *
   * @param model
   * @param request
   * @return
   */
  @ResponseBody
  @RequestMapping(params = "method=getProductThroughRecord")
  public Object getProductThroughRecord(ModelMap model, HttpServletRequest request, ProductThroughSearchDTO productThroughSearchDTO) {
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);

    Long shopId =WebUtil.getShopId(request);
    productThroughSearchDTO.setShopId(shopId);
    try {

      if (StringUtils.isNotBlank(productThroughSearchDTO.getProductKind())) {//按商品分类统计
        IProductService productService = ServiceManager.getService(IProductService.class);
        Long kindId = productService.getKindIdByName(shopId, productThroughSearchDTO.getProductKind());
        List<String> productIdSet = new ArrayList<String>();
        productIdSet.add("-1");
        if (kindId != null) {
          List<ProductDTO> productDTOList = productService.getProductDTOsByProductKindId(shopId, kindId);
          if (CollectionUtils.isNotEmpty(productDTOList)) {
            productIdSet.clear();
            for (ProductDTO productDTO : productDTOList) {
              productIdSet.add(productDTO.getProductLocalInfoIdStr());
            }
          }
        }
        productThroughSearchDTO.setProductIds(productIdSet.toArray(new String[productIdSet.size()]));
      }
      List<Object> result = new ArrayList<Object>();
      productThroughSearchDTO.setSort(productThroughSearchDTO.getSort());
      productThroughSearchDTO.verificationQueryTime();
      productThroughSearchDTO.setStatsFields(new String[]{"item_total", "item_total_cost_price"});
      productThroughSearchDTO.setPageStatsFields(new String[]{"item_total", "item_total_cost_price"});
      ProductThroughSearchResultListDTO resultListDTO = searchOrderService.queryInOutRecords(productThroughSearchDTO);
      Pager pager = new Pager(Integer.valueOf(String.valueOf(resultListDTO.getNumFound())), productThroughSearchDTO.getStartPageNo(), productThroughSearchDTO.getMaxRows());
      result.add(resultListDTO);
      result.add(pager);
      return result;
    }catch (Exception e){
      LOG.error("productThrough.getProductThroughRecord");
      LOG.error(e.getMessage(), e);
    }
    return null;
  }



}
