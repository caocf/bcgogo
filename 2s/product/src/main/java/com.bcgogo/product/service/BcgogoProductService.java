package com.bcgogo.product.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.RecentlyUsedDataDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.ConfigService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IRecentlyUsedDataService;
import com.bcgogo.constant.NormalProductConstants;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.KindStatus;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.config.RecentlyUsedDataType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.BcgogoProductDTO;
import com.bcgogo.product.BcgogoProductPropertyDTO;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.StandardBrandModelCache.StandardBrandModelCache;
import com.bcgogo.product.dto.*;
import com.bcgogo.product.model.*;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleModelDTO;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.search.model.SearchWriter;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.user.dto.CheckNode;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-23
 * Time: 下午3:28
 */
@Component
public class BcgogoProductService implements IBcgogoProductService {


  private static final Logger LOG = LoggerFactory.getLogger(BcgogoProductService.class);
  @Autowired
  private ProductDaoManager productDaoManager;

  @Override
  public List<BcgogoProductDTO> getBcgogoProductDTOByPaymentType(PaymentType paymentType, Boolean isSimple) {
    ProductWriter writer=productDaoManager.getWriter();
    List<BcgogoProduct> bcgogoProductList = writer.getBcgogoProductDTOByPaymentType(paymentType);
    if(CollectionUtils.isNotEmpty(bcgogoProductList)){
      List<BcgogoProductDTO> bcgogoProductDTOList = new ArrayList<BcgogoProductDTO>();
      List<Long> bcgogoProductIdList=new ArrayList<Long>();
      for(BcgogoProduct bcgogoProduct:bcgogoProductList){
        bcgogoProductDTOList.add(bcgogoProduct.toDTO());
        bcgogoProductIdList.add(bcgogoProduct.getId());
      }
      if(isSimple){
        return bcgogoProductDTOList;
      }else{
        List<BcgogoProductProperty> bcgogoProductPropertyList = writer.getBcgogoProductPropertyByProductId(bcgogoProductIdList.toArray(new Long[bcgogoProductIdList.size()]));
        if(CollectionUtils.isNotEmpty(bcgogoProductPropertyList)){
          Map<Long,List<BcgogoProductPropertyDTO>> bcgogoProductPropertyDTOMap = new HashMap<Long, List<BcgogoProductPropertyDTO>>();
          List<BcgogoProductPropertyDTO> bcgogoProductPropertyDTOList = null;
          for(BcgogoProductProperty bcgogoProductProperty : bcgogoProductPropertyList){
            bcgogoProductPropertyDTOList = bcgogoProductPropertyDTOMap.get(bcgogoProductProperty.getProductId());
            if(bcgogoProductPropertyDTOList==null){
              bcgogoProductPropertyDTOList = new ArrayList<BcgogoProductPropertyDTO>();
            }
            bcgogoProductPropertyDTOList.add(bcgogoProductProperty.toDTO());
            bcgogoProductPropertyDTOMap.put(bcgogoProductProperty.getProductId(),bcgogoProductPropertyDTOList);
          }
          for(BcgogoProductDTO bcgogoProductDTO:bcgogoProductDTOList){
            bcgogoProductDTO.setPropertyDTOList(bcgogoProductPropertyDTOMap.get(bcgogoProductDTO.getId()));
          }
        }
      }
      return bcgogoProductDTOList;
    }
    return null;
  }

  @Override
  public BcgogoProductDTO getBcgogoProductDTOById(Long id) {
    ProductWriter writer=productDaoManager.getWriter();
    BcgogoProduct bcgogoProduct = writer.getById(BcgogoProduct.class,id);
    return bcgogoProduct.toDTO();
  }

  @Override
  public BcgogoProductPropertyDTO getBcgogoProductPropertyDTOById(Long id) {
    ProductWriter writer=productDaoManager.getWriter();
    BcgogoProductProperty bcgogoProductProperty = writer.getById(BcgogoProductProperty.class,id);
    return bcgogoProductProperty.toDTO();
  }

  @Override
  public void saveBcgogoProductDTO(BcgogoProductDTO bcgogoProductDTO) {
    ProductWriter writer=productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      BcgogoProduct bcgogoProduct = new BcgogoProduct();
      bcgogoProduct.fromDTO(bcgogoProductDTO);
      writer.save(bcgogoProduct);
      bcgogoProductDTO.setId(bcgogoProduct.getId());
      if(CollectionUtils.isNotEmpty(bcgogoProductDTO.getPropertyDTOList())){
        for(BcgogoProductPropertyDTO bcgogoProductPropertyDTO : bcgogoProductDTO.getPropertyDTOList()){
          BcgogoProductProperty bcgogoProductProperty = new BcgogoProductProperty();
          bcgogoProductPropertyDTO.setProductId(bcgogoProduct.getId());
          bcgogoProductProperty.fromDTO(bcgogoProductPropertyDTO);
          writer.save(bcgogoProductProperty);
          bcgogoProductPropertyDTO.setId(bcgogoProductProperty.getId());
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }
}