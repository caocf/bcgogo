package com.bcgogo.txn.service;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.ShoppingCartDTO;
import com.bcgogo.txn.dto.ShoppingCartItemDTO;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.txn.model.ShoppingCartItem;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.utils.ConfigConstant;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.PromotionsUtils;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 上午10:44
 */
@Service
public class ShoppingCartService implements IShoppingCartService {
  private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public void updateLoginUserShoppingCartInMemCache(Long shopId, Long userId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    int count = writer.countShoppingCartItemByUserId(shopId, userId);
    if(count>0){
      MemCacheAdapter.add(getMemCacheKey(shopId,userId), count);
    }else{
      removeLogoutUserShoppingCartInMemCache(shopId, userId);
    }
  }

  @Override
  public int getShoppingCartItemCountInMemCache(Long shopId, Long userId) throws Exception {
    return NumberUtil.intValue((Integer)MemCacheAdapter.get(getMemCacheKey(shopId,userId)));
  }

  @Override
  public void removeLogoutUserShoppingCartInMemCache(Long shopId, Long userId) throws Exception {
    MemCacheAdapter.delete(getMemCacheKey(shopId,userId));
  }

  @Override
  public void saveOrUpdateShoppingCartItems(Long shopId,Long userId, ShoppingCartItemDTO... shoppingCartItemDTOs) throws Exception {
    if (ArrayUtils.isEmpty(shoppingCartItemDTOs))
      throw new BcgogoException("shoppingCartItemDTO is null");

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShoppingCartItem shoppingCartItem = null;
      Double amount = 0d;
      for(ShoppingCartItemDTO shoppingCartItemDTO : shoppingCartItemDTOs ){
        if(shoppingCartItemDTO.getId()!=null){
          shoppingCartItem = writer.getById(ShoppingCartItem.class,shoppingCartItemDTO.getId());
          amount = shoppingCartItemDTO.getAmount();
        }else{
          shoppingCartItem = writer.getShoppingCartItemByUserIdAndProduct(shopId,userId,shoppingCartItemDTO.getProductLocalInfoId());
          if(shoppingCartItem==null){
            shoppingCartItem = new ShoppingCartItem();
            amount = shoppingCartItemDTO.getAmount();
          }else{
            amount = shoppingCartItem.getAmount()+shoppingCartItemDTO.getAmount();
          }
        }
        shoppingCartItem.fromDTO(shoppingCartItemDTO);
        shoppingCartItem.setAmount(amount);
        writer.saveOrUpdate(shoppingCartItem);
      }
      writer.commit(status);
      //更新men
      updateLoginUserShoppingCartInMemCache(shopId, userId);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void deleteShoppingCartItemById(Long shopId,Long userId, Long... shoppingCartItemIds) throws Exception {
    if (ArrayUtils.isEmpty(shoppingCartItemIds))
      throw new BcgogoException("shoppingCartItemIds is null");

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for(Long shoppingCartItemId : shoppingCartItemIds){
        writer.delete(ShoppingCartItem.class, shoppingCartItemId);
      }
      writer.commit(status);
      //更新men
      updateLoginUserShoppingCartInMemCache(shopId, userId);
    } finally {
      writer.rollback(status);
    }
  }
  @Override
  public void updateShoppingCartItemAmount(Long shopId, Long shoppingCartItemId, Double amount) throws Exception {
    if (shoppingCartItemId == null)
      throw new BcgogoException("shoppingCartItemId is null");

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShoppingCartItem shoppingCartItem = writer.getById(ShoppingCartItem.class, shoppingCartItemId);
      shoppingCartItem.setAmount(NumberUtil.doubleVal(amount));
      writer.saveOrUpdate(shoppingCartItem);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ShoppingCartDTO generateShoppingCartDTO(Long shopId, Long userId) throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
    shoppingCartDTO.setShopId(shopId);
    List<ShoppingCartItem> shoppingCartItemList = writer.getShoppingCartItemByUserId(shopId, userId);
    shoppingCartDTO.setShoppingCartItemCount(CollectionUtils.isEmpty(shoppingCartItemList)?0:shoppingCartItemList.size());
    shoppingCartDTO.setShoppingCartMaxCapacity(getShoppingCartMaxCapacity());

    double total = 0d;
    if(CollectionUtils.isNotEmpty(shoppingCartItemList)){
      List<ShoppingCartItemDTO> shoppingCartItemDTOs = new ArrayList<ShoppingCartItemDTO>();
      for(ShoppingCartItem shoppingCartItem:shoppingCartItemList){
        shoppingCartItemDTOs.add(shoppingCartItem.toDTO());
      }
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      imageService.addImageToShoppingCartItemDTO(shoppingCartItemDTOs,imageSceneList,true);

      //key :supplierShopId
      Map<Long,List<Long>> supplierProductIdMap = new HashMap<Long, List<Long>>();
      Map<Long,List<ShoppingCartItemDTO>> tempShoppingCartItemDTOMap = new LinkedHashMap<Long,List<ShoppingCartItemDTO>>();
      Set<Long> allProductIdSet = new HashSet<Long>();

      handleParamCollection(tempShoppingCartItemDTOMap, supplierProductIdMap, allProductIdSet,shoppingCartItemDTOs);
      //取详细数据   key:supplierShopId
      Map<Long,SupplierDTO> supplierDTOMap = supplierService.getSupplierBySupplierShopId(shopId,tempShoppingCartItemDTOMap.keySet().toArray(new Long[tempShoppingCartItemDTOMap.keySet().size()]));
      Map<Long,ShopDTO> supplierShopDTOMap = configService.getShopByShopId(tempShoppingCartItemDTOMap.keySet().toArray(new Long[tempShoppingCartItemDTOMap.keySet().size()]));
      Map<Long,ProductDTO> allProductDTOMap = productService.getProductDTOMapByProductLocalInfoIds(allProductIdSet);
      //供应商评分

      Map<Long,CommentStatDTO> supplierCommentStatDTOMap = supplierCommentService.getCommentStatByShopIds(tempShoppingCartItemDTOMap.keySet());

      Map<SupplierDTO,List<ShoppingCartItemDTO>> shoppingCartDetailMap = new LinkedHashMap<SupplierDTO, List<ShoppingCartItemDTO>>();
      List<ShoppingCartItemDTO> shoppingCartItemDTOList = null;
      SupplierDTO supplierDTO = null;
      CommentStatDTO commentStatDTO = null;
      List<Long> productIdList = null;
      for(Map.Entry<Long,List<ShoppingCartItemDTO>> entry : tempShoppingCartItemDTOMap.entrySet()){
        supplierDTO = supplierDTOMap.get(entry.getKey());
        if(supplierDTO==null){
          supplierDTO = new SupplierDTO();
          supplierDTO.fromSupplierShopDTO(supplierShopDTOMap.get(entry.getKey()));
//          supplierDTO.setPartShopDTOInfo(supplierShopDTOMap.get(entry.getKey()), false);
        }
        commentStatDTO = supplierCommentStatDTOMap.get(supplierDTO.getSupplierShopId());
        if(commentStatDTO !=null){
          commentStatDTO.calculate();
          supplierDTO.fromSupplierCommentStat(commentStatDTO);
        }
        productIdList = supplierProductIdMap.get(entry.getKey());
        shoppingCartItemDTOList = new ArrayList<ShoppingCartItemDTO>();
        for(ShoppingCartItemDTO shoppingCartItemDTO : entry.getValue()){
          shoppingCartItemDTO.setProductDTO(allProductDTOMap.get(shoppingCartItemDTO.getProductLocalInfoId()));
          if(!ProductStatus.InSales.equals(shoppingCartItemDTO.getSalesStatus())){
            shoppingCartItemDTO.setPrice(null);
            shoppingCartItemDTO.setQuotedPrice(null);
          }
          shoppingCartItemDTO.setTotal(NumberUtil.round(NumberUtil.doubleVal(shoppingCartItemDTO.getAmount())*NumberUtil.doubleVal(shoppingCartItemDTO.getPrice()),NumberUtil.MONEY_PRECISION));
          total +=shoppingCartItemDTO.getTotal();
          shoppingCartItemDTOList.add(shoppingCartItemDTO);
        }
        shoppingCartDetailMap.put(supplierDTO, shoppingCartItemDTOList);
      }
      shoppingCartDTO.setShoppingCartDetailMap(shoppingCartDetailMap);
    }
    shoppingCartDTO.setTotal(NumberUtil.round(total,NumberUtil.MONEY_PRECISION));
    return shoppingCartDTO;
  }


  @Override
  public void clearInvalidShoppingCartItems(Long shopId, Long userId) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);

    TxnWriter writer = txnDaoManager.getWriter();
    List<ShoppingCartItem> shoppingCartItemList = writer.getShoppingCartItemByUserId(shopId, userId);
    if (CollectionUtils.isNotEmpty(shoppingCartItemList)) {
      List<Long> productIdList = null;
      Set<Long> allProductIdSet = new HashSet<Long>();
      for(ShoppingCartItem shoppingCartItem : shoppingCartItemList){
        allProductIdSet.add(shoppingCartItem.getProductLocalInfoId());
        if (productIdList == null) {
          productIdList = new ArrayList<Long>();
        }
        productIdList.add(shoppingCartItem.getProductLocalInfoId());
      }
      Map<Long,ProductLocalInfoDTO> allProductLocalInfoDTOMap = productService.getProductLocalInfoMap(allProductIdSet.toArray(new Long[allProductIdSet.size()]));
      List<Long> invalidShoppingCartItemIdList = new ArrayList<Long>();
      ProductLocalInfoDTO  productLocalInfoDTO=null;
      for(ShoppingCartItem shoppingCartItem : shoppingCartItemList){
        productLocalInfoDTO = allProductLocalInfoDTOMap.get(shoppingCartItem.getProductLocalInfoId());
        if(productLocalInfoDTO==null || !ProductStatus.InSales.equals(productLocalInfoDTO.getSalesStatus())){
          invalidShoppingCartItemIdList.add(shoppingCartItem.getId());
          continue;
        }
      }

      if(CollectionUtils.isNotEmpty(invalidShoppingCartItemIdList)){
        deleteShoppingCartItemById(shopId,userId,invalidShoppingCartItemIdList.toArray(new Long[invalidShoppingCartItemIdList.size()]));
      }
    }
    updateLoginUserShoppingCartInMemCache(shopId,userId);
  }

  @Override
  public List<ShoppingCartItemDTO> getShoppingCartItemDTOById(Long shopId, Long userId,Long... shoppingCartItemId){
    List<ShoppingCartItemDTO> shoppingCartItemDTOList = new ArrayList<ShoppingCartItemDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<ShoppingCartItem> shoppingCartItemList = writer.getShoppingCartItemById(shopId, userId, shoppingCartItemId);
    if(CollectionUtils.isNotEmpty(shoppingCartItemList)){
      for(ShoppingCartItem shoppingCartItem : shoppingCartItemList){
        shoppingCartItemDTOList.add(shoppingCartItem.toDTO());
      }
    }
    return shoppingCartItemDTOList;
  }

  private void handleParamCollection(Map<Long, List<ShoppingCartItemDTO>> tempShoppingCartItemDTOMap, Map<Long, List<Long>> supplierProductIdMap, Set<Long> allProductIdSet,List<ShoppingCartItemDTO> shoppingCartItemDTOList) {
    List<Long> productIdList = null;
    List<ShoppingCartItemDTO> itemList = null;
    for(ShoppingCartItemDTO shoppingCartItemDTO : shoppingCartItemDTOList){
      allProductIdSet.add(shoppingCartItemDTO.getProductLocalInfoId());
      productIdList = supplierProductIdMap.get(shoppingCartItemDTO.getSupplierShopId());
      if (productIdList == null) {
        productIdList = new ArrayList<Long>();
      }
      productIdList.add(shoppingCartItemDTO.getProductLocalInfoId());
      supplierProductIdMap.put(shoppingCartItemDTO.getSupplierShopId(), productIdList);

      itemList = tempShoppingCartItemDTOMap.get(shoppingCartItemDTO.getSupplierShopId());
      if(itemList==null){
        itemList = new ArrayList<ShoppingCartItemDTO>();
      }
      itemList.add(shoppingCartItemDTO);
      tempShoppingCartItemDTOMap.put(shoppingCartItemDTO.getSupplierShopId(),itemList);
    }
  }

  @Override
  public int getShoppingCartMaxCapacity() throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    return NumberUtil.intValue(configService.getConfig(ConfigConstant.SHOPPING_CART_MAX_CAPACITY, ShopConstant.BC_SHOP_ID),0);
  }

  @Override
  public List<ShoppingCartItemDTO> getShopCarItemList(Long shopId, Long userId) throws Exception {

    List<ShoppingCartItemDTO> shoppingCartItemDTOs = new ArrayList<ShoppingCartItemDTO>();

    if (shopId == null) {
      throw new RuntimeException("getShopCarItemList,shopId is null.");
    }

    if (userId == null) {
      throw new RuntimeException("getShopCarItemList,userId is null.");
    }

    TxnWriter writer = txnDaoManager.getWriter();
    List<ShoppingCartItem> shoppingCartItemList = writer.getShoppingCartItemByUserId(shopId, userId);
    if (CollectionUtils.isNotEmpty(shoppingCartItemList)) {
      Set<Long> productLocalIds = new HashSet<Long>();
      for (ShoppingCartItem shoppingCartItem : shoppingCartItemList) {
        productLocalIds.add(shoppingCartItem.getProductLocalInfoId());
        shoppingCartItemDTOs.add(shoppingCartItem.toDTO());
      }
      Map<Long, ProductDTO> allProductDTOMap = ServiceManager.getService(IProductService.class).getProductDTOMapByProductLocalInfoIds(productLocalIds);
      IPromotionsService iPromotionsService = ServiceManager.getService(IPromotionsService.class);
      Map<Long, List<PromotionsDTO>> allPromotionsDTOMap = iPromotionsService.getPromotionsDTOMapByProductLocalInfoId(shopId, true, productLocalIds.toArray(new Long[productLocalIds.size()]));
      for (ShoppingCartItemDTO shoppingCartItemDTO : shoppingCartItemDTOs) {
        shoppingCartItemDTO.setProductDTO(allProductDTOMap.get(shoppingCartItemDTO.getProductLocalInfoId()));
        List<PromotionsDTO> promotions = allPromotionsDTOMap.get(shoppingCartItemDTO.getProductLocalInfoId());
        shoppingCartItemDTO.setPromotionsDTOList(promotions);
        // productDTO.setInSalesPriceAfterCal(PromotionsUtils.calculateBargainPrice(promotions,productDTO.getInSalesPrice()));
        shoppingCartItemDTO.setPrice(PromotionsUtils.calculateBargainPrice(promotions, shoppingCartItemDTO.getQuotedPrice()) == null ? 0.00d : PromotionsUtils.calculateBargainPrice(promotions, shoppingCartItemDTO.getQuotedPrice()));
        String[] result = PromotionsUtils.genPromotionTypesStr(promotions);
        shoppingCartItemDTO.setPromotionTypesShortStr(result[0]);
      }

    }

    return shoppingCartItemDTOs;

  }

  @Override
  public int getShoppingCartWarnCapacity() throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String max = configService.getConfig(ConfigConstant.SHOPPING_CART_WARN_CAPACITY, ShopConstant.BC_SHOP_ID);
    return Integer.parseInt(max);
  }

  private String getMemCacheKey(Long shopId,Long userId) {
    return MemcachePrefix.shoppingCartProductNumber.getValue() + shopId+"_"+userId;
  }
}
