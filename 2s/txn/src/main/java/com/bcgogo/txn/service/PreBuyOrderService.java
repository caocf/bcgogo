package com.bcgogo.txn.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.RecentlyUsedDataDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.config.service.IRecentlyUsedDataService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.enums.txn.preBuyOrder.PreBuyOrderValidDate;
import com.bcgogo.enums.txn.pushMessage.PushMessageScene;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.PreBuyOrderSearchCondition;
import com.bcgogo.search.dto.QuotedPreBuyOrderSearchConditionDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.pushMessage.PushMessageBuildTaskDTO;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.utils.*;
import com.bcgogo.txn.service.pushMessage.ITradePushMessageService;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-5-14
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PreBuyOrderService implements IPreBuyOrderService {
  private static final Logger LOG = LoggerFactory.getLogger(AllocateRecordService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public void savePreBuyOrder(Long shopId, PreBuyOrderDTO... preBuyOrderDTOs) throws Exception {
    if (ArrayUtil.isEmpty(preBuyOrderDTOs)){
      LOG.warn(BcgogoExceptionType.IllegalArgument.getMessage());
      return;
    }
    ShopDTO shopDTO =ServiceManager.getService(IConfigService.class).getShopById(shopId);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //保存单据
      for (PreBuyOrderDTO orderDTO : preBuyOrderDTOs) {
        if(orderDTO==null||ArrayUtil.isEmpty(orderDTO.getItemDTOs())){
          LOG.warn(BcgogoExceptionType.IllegalArgument.getMessage());
          continue;
        }
        PreBuyOrder preBuyOrder = new PreBuyOrder();
        preBuyOrder.fromDTO(orderDTO);
        writer.save(preBuyOrder);
        orderDTO.setId(preBuyOrder.getId());
        for(PreBuyOrderItemDTO itemDTO:orderDTO.getItemDTOs()){
          itemDTO.setPreBuyOrderId(preBuyOrder.getId());
          PreBuyOrderItem item=new PreBuyOrderItem(itemDTO);
          item.setShopId(shopId);
          item.setShopKind(shopDTO.getShopKind());
          writer.save(item);
          itemDTO.setId(item.getId());
        }
      }
      writer.commit(status);
      //保存求购单据主图辅图
      IImageService imageService=ServiceManager.getService(IImageService.class);
      Set<ImageType> imageTypeSet = new HashSet<ImageType>();
      imageTypeSet.add(ImageType.PRODUCT_MAIN_IMAGE);
      imageTypeSet.add(ImageType.PRODUCT_AUXILIARY_IMAGE);
      for (PreBuyOrderDTO orderDTO : preBuyOrderDTOs) {
        if(orderDTO==null||ArrayUtil.isEmpty(orderDTO.getItemDTOs())){
          LOG.warn(BcgogoExceptionType.IllegalArgument.getMessage());
          continue;
        }
        for(PreBuyOrderItemDTO itemDTO:orderDTO.getItemDTOs()){
          if(itemDTO.getImageCenterDTO()==null||CollectionUtils.isEmpty(itemDTO.getImageCenterDTO().getProductInfoImagePaths())){
            continue;
          }
          List<DataImageRelationDTO> dataImageRelationDTOList = new ArrayList<DataImageRelationDTO>();
          List<String> imageUrlList = itemDTO.getImageCenterDTO().getProductInfoImagePaths();
          DataImageRelationDTO dataImageRelationDTO = null;
          int i=0;
          for(String imageUrl:imageUrlList){
            if(StringUtils.isNotBlank(imageUrl)){
              dataImageRelationDTO = new DataImageRelationDTO(shopId,itemDTO.getId(), DataType.ORDER,i==0?ImageType.PRODUCT_MAIN_IMAGE:ImageType.PRODUCT_AUXILIARY_IMAGE,i);
              dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId,imageUrl));
              dataImageRelationDTOList.add(dataImageRelationDTO);
              i++;
            }
          }
          imageService.saveOrUpdateDataImageDTOs(shopId,imageTypeSet,DataType.ORDER ,itemDTO.getId(),dataImageRelationDTOList.toArray(new DataImageRelationDTO[dataImageRelationDTOList.size()]));
        }
      }
      List<OperationLogDTO> operationLogDTOs=new ArrayList<OperationLogDTO>();
      for (PreBuyOrderDTO orderDTO:preBuyOrderDTOs) {
        operationLogDTOs.add(new OperationLogDTO(shopId,orderDTO.getUserId(),orderDTO.getId(),ObjectTypes.PRE_BUY_ORDER, OperationTypes.CREATE));
      }
      ServiceManager.getService(IOperationLogService.class).saveOperationLog(operationLogDTOs.toArray(new OperationLogDTO[operationLogDTOs.size()]));
    } catch (Exception e){
      LOG.error(e.getMessage(),e);
      throw e;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<QuotedPreBuyOrderDTO> getQuotedPreBuyOrderDtoList(Long shopId, int rowStart, int pageSize) throws Exception {
    if (shopId == null) {
      throw new RuntimeException("getQuotedPreBuyOrderDtoList,shopId is null.");
    }
    List<QuotedPreBuyOrderDTO> quotedPreBuyOrderDTOs = new ArrayList<QuotedPreBuyOrderDTO>();
    List<Long> quotedPreBuyOrderIds = new ArrayList<Long>();
    List<Long> preBuyOrderIds = new ArrayList<Long>();
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<QuotedPreBuyOrder> quotedPreBuyOrders = txnWriter.getQuotedPreBuyOrders(shopId, rowStart, pageSize);
    if (CollectionUtils.isNotEmpty(quotedPreBuyOrders)) {
      for (QuotedPreBuyOrder quotedPreBuyOrder : quotedPreBuyOrders) {
        quotedPreBuyOrderDTOs.add(quotedPreBuyOrder.toDTO());
        quotedPreBuyOrderIds.add(quotedPreBuyOrder.getId());
        preBuyOrderIds.add(quotedPreBuyOrder.getPreBuyOrderId());
      }
    }

    if (CollectionUtils.isNotEmpty(quotedPreBuyOrderIds)) {
      Map<Long, List<QuotedPreBuyOrderItemDTO>> result = new HashMap<Long, List<QuotedPreBuyOrderItemDTO>>();
      List<QuotedPreBuyOrderItem> quotedPreBuyOrderItemList = txnWriter.getQuotedPreBuyOrderItems(quotedPreBuyOrderIds.toArray(new Long[quotedPreBuyOrderIds.size()]));
      if (CollectionUtils.isNotEmpty(quotedPreBuyOrderItemList)) {
        for (QuotedPreBuyOrderItem item : quotedPreBuyOrderItemList) {
          if (item == null) {
            continue;
          }
          List<QuotedPreBuyOrderItemDTO> itemDTOs = result.get(item.getQuotedPreBuyOrderId());
          if (CollectionUtils.isNotEmpty(itemDTOs)) {
            itemDTOs.add(item.toDTO());
          } else {
            List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOs = new ArrayList<QuotedPreBuyOrderItemDTO>();
            quotedPreBuyOrderItemDTOs.add(item.toDTO());
            result.put(item.getQuotedPreBuyOrderId(), quotedPreBuyOrderItemDTOs);
          }
        }
      }
      for (QuotedPreBuyOrderDTO quotedPreBuyOrderDTO : quotedPreBuyOrderDTOs) {
        List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = result.get(quotedPreBuyOrderDTO.getId());
        quotedPreBuyOrderDTO.setItemDTOs(quotedPreBuyOrderItemDTOList.toArray(new QuotedPreBuyOrderItemDTO[quotedPreBuyOrderItemDTOList.size()]));
      }
    }

    if (CollectionUtils.isNotEmpty(preBuyOrderIds)) {
      Map<Long, PreBuyOrderDTO> preBuyOrderDTOMap = new HashMap<Long, PreBuyOrderDTO>();
      for (Long id : preBuyOrderIds) {
        PreBuyOrderDTO preBuyOrderDTO = getSimplePreBuyOrderDTOById(id);
        preBuyOrderDTOMap.put(id, preBuyOrderDTO);
      }
      for (QuotedPreBuyOrderDTO quotedPreBuyOrderDTO : quotedPreBuyOrderDTOs) {
        PreBuyOrderDTO preBuyOrderDTO = preBuyOrderDTOMap.get(quotedPreBuyOrderDTO.getPreBuyOrderId());
        quotedPreBuyOrderDTO.setTitle(preBuyOrderDTO.getTitle()); // 这里会有nullPointer?
      }
    }

    return quotedPreBuyOrderDTOs;

  }

  @Override
  public void simpleUpdateQuotedPreBuyOrderItem(QuotedPreBuyOrderItemDTO itemDTO){
    if(itemDTO==null||itemDTO.getId()==null) return;
    TxnWriter writer=ServiceManager.getService(TxnDaoManager.class).getWriter();
    QuotedPreBuyOrderItem item=writer.getById(QuotedPreBuyOrderItem.class,itemDTO.getId());
    if(item==null) return;
    Object status=writer.begin();
    try{
      item.fromDTO(itemDTO);
      writer.update(item);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateQuotedPreBuyOrder(Long shopId, QuotedPreBuyOrderDTO quotedPreBuyOrderDTO) throws Exception {
    if (quotedPreBuyOrderDTO == null)
      throw new BcgogoException("quotedPreBuyOrderDTO is null");

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      OperationTypes operationType = null;
      List<QuotedPreBuyOrderItem> dbItems = null;
      if (quotedPreBuyOrderDTO.getId() != null) {
        QuotedPreBuyOrder quotedPreBuyOrder = writer.getById(QuotedPreBuyOrder.class, quotedPreBuyOrderDTO.getId());
        quotedPreBuyOrder = quotedPreBuyOrder.fromDTO(quotedPreBuyOrderDTO);
        writer.update(quotedPreBuyOrder);
        operationType = OperationTypes.UPDATE;
        dbItems = writer.getQuotedPreBuyOrderItemsByQuotedPreBuyOrderId(quotedPreBuyOrder.getId());
      } else {
        QuotedPreBuyOrder quotedPreBuyOrder = new QuotedPreBuyOrder();
        writer.save(quotedPreBuyOrder.fromDTO(quotedPreBuyOrderDTO));
        quotedPreBuyOrderDTO.setId(quotedPreBuyOrder.getId());
        operationType = OperationTypes.CREATE;
      }

      if (CollectionUtils.isNotEmpty(dbItems)) {
        for (QuotedPreBuyOrderItem dbItem : dbItems) {
          writer.delete(dbItem);
        }
      }
      //保存或者更新item
      PreBuyOrderItem preBuyOrderItem = null;
      for (QuotedPreBuyOrderItemDTO itemDTO : quotedPreBuyOrderDTO.getItemDTOs()) {
        itemDTO.setQuotedPreBuyOrderId(quotedPreBuyOrderDTO.getId());
        itemDTO.setPreBuyOrderId(quotedPreBuyOrderDTO.getPreBuyOrderId());
        itemDTO.setShopId(quotedPreBuyOrderDTO.getShopId());
        itemDTO.setQuotedDate(quotedPreBuyOrderDTO.getVestDate());
        QuotedPreBuyOrderItem quotedPreBuyOrderItem = new QuotedPreBuyOrderItem();
        quotedPreBuyOrderItem = quotedPreBuyOrderItem.fromDTO(itemDTO);
        writer.save(quotedPreBuyOrderItem);
        preBuyOrderItem = writer.getById(PreBuyOrderItem.class, itemDTO.getPreBuyOrderItemId());
        preBuyOrderItem.setQuotedCount(NumberUtil.intValue(preBuyOrderItem.getQuotedCount()) + 1);
        writer.update(preBuyOrderItem);

        itemDTO.setId(quotedPreBuyOrderItem.getId());


      }

      writer.commit(status);

      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      operationLogService.saveOperationLog(new OperationLogDTO(shopId, quotedPreBuyOrderDTO.getUserId(), quotedPreBuyOrderDTO.getId(), ObjectTypes.QUOTED_PRE_BUY_ORDER, operationType));
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public PreBuyOrderDTO getPreBuyOrderDTOById(Long shopId, Long preBuyOrderId) throws Exception {
    PreBuyOrderDTO preBuyOrderDTO = null;
    TxnWriter writer = txnDaoManager.getWriter();
    PreBuyOrder preBuyOrder = writer.getById(PreBuyOrder.class, preBuyOrderId);
    if (preBuyOrder != null) {
      preBuyOrderDTO = preBuyOrder.toDTO();
      List<PreBuyOrderItem> preBuyOrderItemList = writer.getPreBuyOrderItemsByPreBuyOrderId(preBuyOrder.getId());
      List<QuotedPreBuyOrder> quotedPreBuyOrderList = writer.getQuotedPreBuyOrdersByPreBuyOrderId(preBuyOrder.getId());
      Map<Long, QuotedPreBuyOrderDTO> quotedPreBuyOrderDTOMap = new HashMap<Long, QuotedPreBuyOrderDTO>();
      if (CollectionUtils.isNotEmpty(quotedPreBuyOrderList)) {
        for (QuotedPreBuyOrder quotedPreBuyOrder : quotedPreBuyOrderList) {
          quotedPreBuyOrderDTOMap.put(quotedPreBuyOrder.getId(), quotedPreBuyOrder.toDTO());
        }
      }
      if (CollectionUtils.isNotEmpty(preBuyOrderItemList)) {
        List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = new ArrayList<PreBuyOrderItemDTO>();
        PreBuyOrderItemDTO preBuyOrderItemDTO = null;
        List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = null;
        QuotedPreBuyOrderDTO quotedPreBuyOrderDTO = null;
        for (PreBuyOrderItem preBuyOrderItem : preBuyOrderItemList) {
          preBuyOrderItemDTO = preBuyOrderItem.toDTO();
          quotedPreBuyOrderItemDTOList = this.getQuotedPreBuyOrderItemDTOsByPreBuyOrderItemId(null, preBuyOrderItem.getId());
          if (CollectionUtils.isNotEmpty(quotedPreBuyOrderItemDTOList)) {
            for (QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO : quotedPreBuyOrderItemDTOList) {
              quotedPreBuyOrderDTO = quotedPreBuyOrderDTOMap.get(quotedPreBuyOrderItemDTO.getQuotedPreBuyOrderId());
              ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(quotedPreBuyOrderDTO.getShopId());
              quotedPreBuyOrderItemDTO.setShopName(shopDTO.getName());
              quotedPreBuyOrderItemDTO.setQuotedDate(quotedPreBuyOrderDTO.getVestDate());
            }
          }
          preBuyOrderItemDTO.setQuotedPreBuyOrderItemDTOList(quotedPreBuyOrderItemDTOList);
          preBuyOrderItemDTOList.add(preBuyOrderItemDTO);
        }
        preBuyOrderDTO.setItemDTOs(preBuyOrderItemDTOList.toArray(new PreBuyOrderItemDTO[preBuyOrderItemDTOList.size()]));
        preBuyOrderDTO.setItemDTO(preBuyOrderItemDTOList.get(0));
      }
    }
    ServiceManager.getService(IRecentlyUsedDataService.class).addViewedCountToPreBuyOrderItem(preBuyOrderDTO.getItemDTO());
    return preBuyOrderDTO;
  }

  @Override
  public List<PreBuyOrderDTO> getPreBuyOrdersByShopId(Long shopId, int pageStart, int pageSize) {
    if (shopId == null) {
      throw new RuntimeException("getPreBuyOrdersByShopId,shopId is null.");
    }
    List<PreBuyOrderDTO> preBuyOrderDTOs = new ArrayList<PreBuyOrderDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<PreBuyOrder> preBuyOrders = writer.getPreBuyOrdersByShopId(shopId, pageStart, pageSize);
    if (CollectionUtils.isNotEmpty(preBuyOrders)) {
      for (PreBuyOrder preBuyOrder : preBuyOrders) {
        PreBuyOrderDTO preBuyOrderDTO = preBuyOrder.toDTO();
        List<PreBuyOrderItemDTO> preBuyOrderItemDTOs = new ArrayList<PreBuyOrderItemDTO>();
        // commented by zhuj  N+1 查询 由于是分页查询 且目前数量限制为2 如果分页大小改变 这边重构掉
        List<PreBuyOrderItem> preBuyOrderItems = writer.getPreBuyOrderItemsByPreBuyOrderId(preBuyOrder.getId());
        if (CollectionUtils.isNotEmpty(preBuyOrderItems)) {
          int orderQuotedCount = 0; // 单个求购单下的报价次数
          for (PreBuyOrderItem preBuyOrderItem : preBuyOrderItems) {
            preBuyOrderItemDTOs.add(preBuyOrderItem.toDTO());
            orderQuotedCount += preBuyOrderItem.getQuotedCount() == null ? 0L : preBuyOrderItem.getQuotedCount().intValue();
          }
          preBuyOrderDTO.setItemDTOs(preBuyOrderItemDTOs.toArray(new PreBuyOrderItemDTO[preBuyOrderItemDTOs.size()]));
          preBuyOrderDTO.setQuotedCount(orderQuotedCount);
        }
        preBuyOrderDTOs.add(preBuyOrderDTO);
      }
    }
    return preBuyOrderDTOs;
  }

  public List<PreBuyOrderItemDTO> getPreBuyOrderItemDetailDTO(PreBuyOrderSearchCondition condition) throws ParseException {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PreBuyOrderItemDTO> itemDTOs=new ArrayList<PreBuyOrderItemDTO>();
    List<Object[]> results=writer.getPreBuyOrderItemDetailDTO(condition);
    if(CollectionUtil.isEmpty(results)){
      return itemDTOs;
    }
    for(Object[] result:results){
      if(result==null){
        continue;
      }
      PreBuyOrderItem item=(PreBuyOrderItem)result[0];
      PreBuyOrder preBuyOrder=(PreBuyOrder)result[1];
      PreBuyOrderItemDTO itemDTO=item.toDTO();
      itemDTO.setPreBuyOrderDTO(preBuyOrder.toDTO());
      itemDTOs.add(itemDTO);
    }
    return itemDTOs;
  }


  @Override
  public List<PreBuyOrderItemDTO> getLatestPreBuyOrderItemDTO(Long shopId,int pageStart, int pageSize) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PreBuyOrderItemDTO> itemDTOs=new ArrayList<PreBuyOrderItemDTO>();
      IConfigService configService=ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO=configService.getShopById(shopId);
    if(shopDTO==null){
      return itemDTOs;
    }
    List<PreBuyOrderItem> latestPreBuyOrderItems = writer.getLatestPreBuyOrderItem(pageStart,pageSize,shopDTO.getShopKind());
    if(CollectionUtils.isEmpty(latestPreBuyOrderItems)){
      return itemDTOs;
    }

    Map<Long,List<PreBuyOrderItem>> preItemMap=new HashMap<Long, List<PreBuyOrderItem>>();
    Set<Long> shopIdSet = new HashSet<Long>();
    for(PreBuyOrderItem orderItem:latestPreBuyOrderItems){
      if(orderItem==null){
        continue;
      }
      List<PreBuyOrderItem> orderItems= preItemMap.get(orderItem.getPreBuyOrderId());
      if(com.bcgogo.utils.CollectionUtil.isEmpty(orderItems)){
        orderItems=new ArrayList<PreBuyOrderItem>();
        preItemMap.put(orderItem.getPreBuyOrderId(),orderItems);
      }
      orderItems.add(orderItem);
    }
    List<PreBuyOrder> preBuyOrders=writer.getPreBuyOrder(ArrayUtil.toLongArr(preItemMap.keySet()));
    Map<Long,PreBuyOrder> preOrderMap=new HashMap<Long,PreBuyOrder>();
    if(CollectionUtils.isNotEmpty(preBuyOrders)){
      for(PreBuyOrder preBuyOrder:preBuyOrders){
        preOrderMap.put(preBuyOrder.getId(),preBuyOrder);
        if(!shopIdSet.contains(preBuyOrder.getShopId())){
          shopIdSet.add(preBuyOrder.getShopId());
        }
      }
    }
    Map<Long, ShopDTO> shopDTOMap = configService.getShopByShopId(shopIdSet.toArray(new Long[shopIdSet.size()]));
    for(PreBuyOrderItem item:latestPreBuyOrderItems){
      PreBuyOrder preBuyOrder=preOrderMap.get(item.getPreBuyOrderId());
      if(shopDTOMap!=null&& com.bcgogo.utils.CollectionUtil.isNotEmpty(shopDTOMap.keySet())){
        shopDTO=shopDTOMap.get(preBuyOrder.getShopId());
      }
      PreBuyOrderItemDTO itemDTO=item.toDTO();
      if(shopDTO!=null){
        itemDTO.setShopName(shopDTO.getName());
        itemDTO.setShopAreaInfo(configService.getShopAreaInfoByShopDTO(shopDTO));
      }
      itemDTO.setEndDateStr(DateUtil.convertDateLongToDateString(DateUtil.DEFAULT,preBuyOrder.getEndDate()));
      itemDTO.setBusinessChanceTypeStr(preBuyOrder.getBusinessChanceType().getName());
      itemDTO.setProductInfo(itemDTO.getProductInfo());
      itemDTOs.add(itemDTO);
    }
//    itemDTOs=filterPreBuyOrderItemByShopKind(shopId,itemDTOs.toArray(new PreBuyOrderItemDTO[itemDTOs.size()]));
    addMyQuotedToPreBuyOrderItemDTO(shopId,itemDTOs.toArray(new PreBuyOrderItemDTO[itemDTOs.size()]));
    return itemDTOs;
  }

   @Override
  public List<PreBuyOrderItemDTO>  filterPreBuyOrderItemByShopKind(Long shopId,PreBuyOrderItemDTO ...preBuyOrderItemDTOs){
    List<PreBuyOrderItemDTO> itemDTOs=new ArrayList<PreBuyOrderItemDTO>();
    if(ArrayUtil.isEmpty(preBuyOrderItemDTOs)) {
      return itemDTOs;
    }
    IConfigService configService=ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO=configService.getShopById(shopId);
    if(shopDTO==null||shopDTO.getShopKind()==null){
      return itemDTOs;
    }
    ShopDTO tShopDTO=null;
    for(PreBuyOrderItemDTO itemDTO:preBuyOrderItemDTOs){
      tShopDTO=configService.getShopById(itemDTO.getShopId());
      if(tShopDTO==null||!shopDTO.getShopKind().equals(tShopDTO.getShopKind())){
        continue;
      }
      itemDTOs.add(itemDTO);
    }
    return itemDTOs;
  }

  @Override
  public PreBuyOrderDTO getSimplePreBuyOrderDTOById(Long preBuyOrderId) throws Exception {
    PreBuyOrderDTO preBuyOrderDTO = null;
    TxnWriter writer = txnDaoManager.getWriter();
    PreBuyOrder preBuyOrder = writer.getById(PreBuyOrder.class, preBuyOrderId);
    if (preBuyOrder != null) {
      preBuyOrderDTO = preBuyOrder.toDTO();
      List<PreBuyOrderItem> preBuyOrderItemList = writer.getPreBuyOrderItemsByPreBuyOrderId(preBuyOrder.getId());
      if (CollectionUtils.isNotEmpty(preBuyOrderItemList)) {
        List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = new ArrayList<PreBuyOrderItemDTO>();
        for (PreBuyOrderItem preBuyOrderItem : preBuyOrderItemList) {
          preBuyOrderItemDTOList.add(preBuyOrderItem.toDTO());
        }
        preBuyOrderDTO.setItemDTOs(preBuyOrderItemDTOList.toArray(new PreBuyOrderItemDTO[preBuyOrderItemDTOList.size()]));
      }
    }

    return preBuyOrderDTO;
  }

  @Override
  public PreBuyOrderDTO getSimplePreBuyOrderDTOByQuotedPreBuyOrderItemId(Long quotedPreBuyOrderItemId) throws Exception {
    PreBuyOrderDTO preBuyOrderDTO = null;
    TxnWriter writer = txnDaoManager.getWriter();
    PreBuyOrder preBuyOrder = writer.getPreBuyOrderByQuotedPreBuyOrderItemId(quotedPreBuyOrderItemId);
    if (preBuyOrder != null) {
      preBuyOrderDTO = preBuyOrder.toDTO();
      List<PreBuyOrderItem> preBuyOrderItemList = writer.getPreBuyOrderItemsByPreBuyOrderId(preBuyOrder.getId());
      if (CollectionUtils.isNotEmpty(preBuyOrderItemList)) {
        List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = new ArrayList<PreBuyOrderItemDTO>();
        for (PreBuyOrderItem preBuyOrderItem : preBuyOrderItemList) {
          preBuyOrderItemDTOList.add(preBuyOrderItem.toDTO());
        }
        preBuyOrderDTO.setItemDTOs(preBuyOrderItemDTOList.toArray(new PreBuyOrderItemDTO[preBuyOrderItemDTOList.size()]));
      }
    }

    return preBuyOrderDTO;
  }

  @Override
  public List<QuotedPreBuyOrderItemDTO> getLatestQuotedPreBuyOrderItemByCustomerShopId(Long customerShopId,Integer limit){
    TxnWriter writer = txnDaoManager.getWriter();
    QuotedPreBuyOrderSearchConditionDTO conditionDTO=new QuotedPreBuyOrderSearchConditionDTO();
    conditionDTO.setCustomerShopId(customerShopId);
    conditionDTO.setLimit(limit);
    conditionDTO.setSort(new Sort("editDate","desc"));
    List<Object[]> dataList= writer.getQuotedPreBuyOrder(conditionDTO);
    List<QuotedPreBuyOrderItemDTO> itemDTOs=new ArrayList<QuotedPreBuyOrderItemDTO>();
    if(CollectionUtil.isEmpty(dataList)) return itemDTOs;
    for (Object[] data:dataList){
      QuotedPreBuyOrderItem item=(QuotedPreBuyOrderItem)data[1];
      itemDTOs.add(item.toDTO());
    }
    Set<Long> productIdSet = new HashSet<Long>();
    for(QuotedPreBuyOrderItemDTO itemDTO:itemDTOs){
      if(!productIdSet.contains(itemDTO.getProductId())){
        productIdSet.add(itemDTO.getProductId());
      }
    }
    Map<Long, ProductDTO> productDTOMap = ServiceManager.getService(IProductService.class).getProductDTOMapByProductLocalInfoIds(productIdSet);
    for(QuotedPreBuyOrderItemDTO itemDTO:itemDTOs){
      itemDTO.setProductDTOWithOutUnit(productDTOMap.get(itemDTO.getProductId()));
    }
    return itemDTOs;
  }

  @Override
  public PreBuyOrderItemDTO getPreBuyOrderItemDTOById(Long preBuyOrderItemId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    PreBuyOrderItem preBuyOrderItem = writer.getById(PreBuyOrderItem.class, preBuyOrderItemId);
    if(preBuyOrderItem==null) return null;
    return preBuyOrderItem.toDTO();
  }

  @Override
  public Map<Long,PreBuyOrderItemDTO> getPreBuyOrderItemDTOMapByIds(Long shopId,Long... preBuyOrderItemIds){
    List<PreBuyOrderItemDTO> itemDTOs=getPreBuyOrderItemDTOByIds(shopId,preBuyOrderItemIds);
    Map<Long,PreBuyOrderItemDTO> itemDTOMap=new HashMap<Long, PreBuyOrderItemDTO>();
    if(CollectionUtil.isNotEmpty(itemDTOs)){
      for (PreBuyOrderItemDTO itemDTO:itemDTOs){
        itemDTO.setProductInfo(itemDTO.generateProductInfo());
        try {
          PreBuyOrderDTO preBuyOrderDTO = this.getPreBuyOrderDTOById(itemDTO.getShopId(),itemDTO.getPreBuyOrderId());
          if(preBuyOrderDTO != null) {
            itemDTO.setEndDateCount(preBuyOrderDTO.getEndDateCount());
            itemDTO.setStatusStr(preBuyOrderDTO.getStatusStr());
          }
        } catch (Exception e) {
          LOG.error(e.getMessage(),e);
        }

        itemDTOMap.put(itemDTO.getId(),itemDTO);
      }
    }
    return itemDTOMap;
  }

  @Override
  public  List<PreBuyOrderItemDTO> getPreBuyOrderItemDTOByIds(Long shopId,Long... preBuyOrderItemIds){
    TxnWriter writer = txnDaoManager.getWriter();
    List<PreBuyOrderItemDTO> itemDTOs=new ArrayList<PreBuyOrderItemDTO>();
    if(ArrayUtil.isEmpty(preBuyOrderItemIds)) return itemDTOs;
    List<PreBuyOrderItem> items = writer.getPreBuyOrderItemDTOByIds(shopId,preBuyOrderItemIds);
    if(CollectionUtil.isNotEmpty(items)){
      for (PreBuyOrderItem item:items){
        itemDTOs.add(item.toDTO());
      }
    }
    return itemDTOs;
  }



  @Override
  public Long countPreBuyOrderItems(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countPreBuyOrderItems(shopId);
  }

  @Override
  public Long countValidPreBuyOrderItems(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countValidPreBuyOrderItems(shopId);
  }

  @Override
  public Long countQuotedPreBuyOrders(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countQuotedPreBuyOrders(shopId);
  }

  @Override
  public Long countQuotedPreBuyOrderItems(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countQuotedPreBuyOrderItems(shopId);
  }


  @Override
  public Long countOrdersFromQuotedPreBuyOrder(Long shopId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countOrdersFromQuotedPreBuyOrder(shopId);
  }

  @Override
  public int countQuotedPreBuyOrderSupplier(QuotedPreBuyOrderSearchConditionDTO conditionDTO){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countQuotedPreBuyOrderSupplier(conditionDTO);
  }

  @Override
  public List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOsByPreBuyOrderItemId(Long shopId, Long preBuyOrderItemId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<QuotedPreBuyOrderItem> quotedPreBuyOrderItemList = writer.getQuotedPreBuyOrderItemsByPreBuyOrderItemId(shopId, preBuyOrderItemId);
    if (CollectionUtils.isNotEmpty(quotedPreBuyOrderItemList)) {
      Set<Long> productIdSet = new HashSet<Long>();
      for (QuotedPreBuyOrderItem quotedPreBuyOrderItem : quotedPreBuyOrderItemList) {
        productIdSet.add(quotedPreBuyOrderItem.getProductId());
      }
      Map<Long, ProductDTO> productDTOMap = ServiceManager.getService(IProductService.class).getProductDTOMapByProductLocalInfoIds(productIdSet);
      List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = new ArrayList<QuotedPreBuyOrderItemDTO>();
      QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO = null;
      for (QuotedPreBuyOrderItem quotedPreBuyOrderItem : quotedPreBuyOrderItemList) {
        quotedPreBuyOrderItemDTO = quotedPreBuyOrderItem.toDTO();
        quotedPreBuyOrderItemDTO.setProductDTOWithOutUnit(productDTOMap.get(quotedPreBuyOrderItem.getProductId()));
        quotedPreBuyOrderItemDTOList.add(quotedPreBuyOrderItemDTO);
      }
      return quotedPreBuyOrderItemDTOList;
    }
    return null;
  }

  @Override
  public List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOsByPreBuyOrderId(Long shopId, Long preBuyOrderId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<QuotedPreBuyOrderItem> quotedPreBuyOrderItemList = writer.getQuotedPreBuyOrderItemsByPreBuyOrderId(shopId, preBuyOrderId);
    if (CollectionUtils.isNotEmpty(quotedPreBuyOrderItemList)) {
      List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = new ArrayList<QuotedPreBuyOrderItemDTO>();
      for (QuotedPreBuyOrderItem quotedPreBuyOrderItem : quotedPreBuyOrderItemList) {
        quotedPreBuyOrderItemDTOList.add(quotedPreBuyOrderItem.toDTO());
      }
      return quotedPreBuyOrderItemDTOList;
    }
    return null;
  }

  @Override
  public List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOsByItemId(Long... quotedPreBuyOrderItemIds) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<QuotedPreBuyOrderItem> quotedPreBuyOrderItemList = writer.getQuotedPreBuyOrderItemsByItemId(quotedPreBuyOrderItemIds);
    if (CollectionUtils.isNotEmpty(quotedPreBuyOrderItemList)) {
      List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = new ArrayList<QuotedPreBuyOrderItemDTO>();
      for (QuotedPreBuyOrderItem quotedPreBuyOrderItem : quotedPreBuyOrderItemList) {
        quotedPreBuyOrderItemDTOList.add(quotedPreBuyOrderItem.toDTO());
      }
      return quotedPreBuyOrderItemDTOList;
    }
    return null;
  }

  @Override
  public QuotedPreBuyOrderDTO getQuotedPreBuyOrderDTO(Long quotedPreBuyOrderId){
    TxnWriter writer = txnDaoManager.getWriter();
    QuotedPreBuyOrder quotedPreBuyOrder= writer.getById(QuotedPreBuyOrder.class,quotedPreBuyOrderId);
    if(quotedPreBuyOrder!=null){
      return quotedPreBuyOrder.toDTO();
    }
    return null;
  }

  @Override
  public List<QuotedPreBuyOrderDTO> getQuotedPreBuyOrderIdsByItemId(Long shopId, Long... quotedBuyOrderItemId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<QuotedPreBuyOrder> quotedPreBuyOrderList = writer.getQuotedPreBuyOrdersByItemId(shopId, quotedBuyOrderItemId);
    if (CollectionUtils.isNotEmpty(quotedPreBuyOrderList)) {
      List<QuotedPreBuyOrderDTO> quotedPreBuyOrderDTOList = new ArrayList<QuotedPreBuyOrderDTO>();
      for (QuotedPreBuyOrder quotedPreBuyOrder : quotedPreBuyOrderList) {
        quotedPreBuyOrderDTOList.add(quotedPreBuyOrder.toDTO());
      }
      return quotedPreBuyOrderDTOList;
    }
    return null;
  }

  @Override
  public QuotedPreBuyOrderItemDTO getQuotedPreBuyOrderItemDTOById(Long quotedPreBuyOrderItemId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    QuotedPreBuyOrderItem item= writer.getById(QuotedPreBuyOrderItem.class,quotedPreBuyOrderItemId);
    return item==null?null:item.toDTO();
  }

  @Override
  public List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOsByIds(Long shopId,Long... quotedPreBuyOrderItemIds){
    List<QuotedPreBuyOrderItemDTO> itemDTOs=new ArrayList<QuotedPreBuyOrderItemDTO>();
    if(ArrayUtil.isEmpty(quotedPreBuyOrderItemIds)){
      return itemDTOs;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    if(ArrayUtil.isEmpty(quotedPreBuyOrderItemIds)){
      return itemDTOs;
    }
    List<QuotedPreBuyOrderItem> quotedPreBuyOrderItems = writer.getQuotedPreBuyOrderItemDTOsByIds(shopId, quotedPreBuyOrderItemIds);
    if(CollectionUtil.isNotEmpty(quotedPreBuyOrderItems)){
      for (QuotedPreBuyOrderItem item:quotedPreBuyOrderItems){
        itemDTOs.add(item.toDTO());
      }
    }
    return itemDTOs;
  }

  @Override
  public void addMyQuotedToPreBuyOrderItemDTO(Long shopId,PreBuyOrderItemDTO ...preBuyOrderItemDTOs){
    if(shopId==null||ArrayUtil.isEmpty(preBuyOrderItemDTOs)) return;
    List<Long> preBuyOrderItemIds=new ArrayList<Long>();
    for(PreBuyOrderItemDTO itemDTO :preBuyOrderItemDTOs){
      preBuyOrderItemIds.add(itemDTO.getId());
    }
    QuotedPreBuyOrderSearchConditionDTO conditionDTO=new QuotedPreBuyOrderSearchConditionDTO();
    conditionDTO.setShopId(shopId);
    conditionDTO.setPreBuyOrderItemIds(ArrayUtil.toLongArr(preBuyOrderItemIds));
    List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOs=getQuotedPreBuyOrderItem(conditionDTO);
    if(CollectionUtil.isEmpty(quotedPreBuyOrderItemDTOs)) return;
    Map<Long,QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOMap=new HashMap<Long, QuotedPreBuyOrderItemDTO>();
    for(QuotedPreBuyOrderItemDTO itemDTO:quotedPreBuyOrderItemDTOs){
      quotedPreBuyOrderItemDTOMap.put(itemDTO.getPreBuyOrderItemId(),itemDTO);
    }
    for(PreBuyOrderItemDTO itemDTO :preBuyOrderItemDTOs){
      itemDTO.setMyQuotedPreBuyOrderItemDTO(quotedPreBuyOrderItemDTOMap.get(itemDTO.getId()));
    }
  }

  @Override
  public List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItem(QuotedPreBuyOrderSearchConditionDTO conditionDTO){
    TxnWriter writer = txnDaoManager.getWriter();
    List<Object[]> dataList= writer.getQuotedPreBuyOrder(conditionDTO);
    List<QuotedPreBuyOrderItemDTO> itemDTOs=new ArrayList<QuotedPreBuyOrderItemDTO>();
    if(CollectionUtil.isEmpty(dataList)) return itemDTOs;
    for (Object[] data:dataList){
      QuotedPreBuyOrderItem item=(QuotedPreBuyOrderItem)data[1];
      itemDTOs.add(item.toDTO());
    }
    Set<Long> productIdSet = new HashSet<Long>();
    for(QuotedPreBuyOrderItemDTO itemDTO:itemDTOs){
      if(!productIdSet.contains(itemDTO.getProductId())){
        productIdSet.add(itemDTO.getProductId());
      }
    }
    Map<Long, ProductDTO> productDTOMap = ServiceManager.getService(IProductService.class).getProductDTOMapByProductLocalInfoIds(productIdSet);
    for(QuotedPreBuyOrderItemDTO itemDTO:itemDTOs){
      itemDTO.setProductDTOWithOutUnit(productDTOMap.get(itemDTO.getProductId()));
    }
    return itemDTOs;
  }

  public int countSupplierOtherQuotedItems(Long quoterShopId,Long preBuyerShopId,Long quotedPreBuyOrderItemId){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countSupplierOtherQuotedItems(quoterShopId,preBuyerShopId,quotedPreBuyOrderItemId);
  }

  public List<QuotedPreBuyOrderItemDTO> getSupplierOtherQuotedItems(Long quoterShopId,Long preBuyerShopId,Long quotedPreBuyOrderItemId,Pager pager){
    List<QuotedPreBuyOrderItemDTO> itemDTOs=new ArrayList<QuotedPreBuyOrderItemDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<QuotedPreBuyOrderItem> items=writer.getSupplierOtherQuotedItems(quoterShopId,preBuyerShopId,quotedPreBuyOrderItemId,pager);
    if(CollectionUtil.isNotEmpty(items)){
      Set<Long> quotedPreBuyOrderIds=new HashSet<Long>();
      for(QuotedPreBuyOrderItem item:items){
        quotedPreBuyOrderIds.add(item.getQuotedPreBuyOrderId());
      }
      List<QuotedPreBuyOrder> orders=writer.getQuotedPreBuyOrdersByIds(quotedPreBuyOrderIds);
      Map<Long,QuotedPreBuyOrder> orderMap=new HashMap<Long, QuotedPreBuyOrder>();
      if(CollectionUtil.isNotEmpty(orders)){
        for (QuotedPreBuyOrder order:orders){
          orderMap.put(order.getId(),order);
        }
      }
      QuotedPreBuyOrder order=null;
      for(QuotedPreBuyOrderItem item:items){
        order=orderMap.get(item.getQuotedPreBuyOrderId());
        QuotedPreBuyOrderItemDTO itemDTO=item.toDTO();
        if(order!=null){
          itemDTO.setQuotedPreBuyOrder(order.toDTO());
        }
        itemDTOs.add(itemDTO);
      }
    }
    return itemDTOs;
  }



  @Override
  public Long countQuotedPreBuyOrdersByPreBuyOrderId(Long shopId, Long preBuyOrderId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countQuotedPreBuyOrdersByPreBuyOrderId(shopId, preBuyOrderId);
  }

  @Override
  public List<ProductDTO> preBuyOrderFilter(List<ProductDTO> productDTOs, Long shopId) throws Exception {
    if (CollectionUtils.isEmpty(productDTOs)) return new ArrayList<ProductDTO>();
    Set<String> productNames = new HashSet<String>();
    Map<String, ProductDTO> result = new HashMap<String, ProductDTO>();
    for (ProductDTO productDTO : productDTOs) {
      productNames.add(productDTO.getName());
      result.put(getKey(productDTO.getName(), productDTO.getBrand(), productDTO.getSpec(), productDTO.getModel()
        , productDTO.getVehicleModel() , productDTO.getVehicleBrand(), productDTO.getCommodityCode()), productDTO);
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<PreBuyOrderItem> items = writer.getActivePreBuyOrderItemList(shopId, productNames);
    for (PreBuyOrderItem item : items) {     //轮胎横滨854555帕萨特上海大众轮胎    BBBB小型汽车2000型B
      result.remove(getKey(item.getProductName(), item.getProductBrand(), item.getProductSpec(), item.getProductModel()
        ,item.getProductVehicleModel(), item.getProductVehicleBrand(),  item.getCommodityCode()));
    }
    return new ArrayList<ProductDTO>(result.values());
  }

  public String getKey(String productName, String productBrand, String productSpec, String productModel,
                       String productVehicleModel, String productVehicleBrand, String commodityCode) {
    return (StringUtils.isBlank(productName) ? "name" : productName)
      + (StringUtils.isBlank(productBrand) ? "brand" : productBrand)
      + (StringUtils.isBlank(productSpec) ? "spec" : productSpec)
      + (StringUtils.isBlank(productModel) ? "model" : productModel)
      + (StringUtils.isBlank(productVehicleModel) ? "vm" : productVehicleModel)
      + (StringUtils.isBlank(productVehicleBrand) ? "vb" : productVehicleBrand)
      + (StringUtils.isBlank(commodityCode) ? "cc" : commodityCode);
  }

  @Override
  public List<QuotedPreBuyOrderDTO> getQuotedPreBuyOrdersByQuotePreBuyOrderIds(Set<Long> quotedPreBuyOrderIds) {
    List<QuotedPreBuyOrderDTO> quotedPreBuyOrderDTOs = new ArrayList<QuotedPreBuyOrderDTO>();
    if (CollectionUtils.isEmpty(quotedPreBuyOrderIds)) {
      return quotedPreBuyOrderDTOs;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<QuotedPreBuyOrder> quotedPreBuyOrders = writer.getQuotedPreBuyOrdersByIds(quotedPreBuyOrderIds);
    if (CollectionUtils.isNotEmpty(quotedPreBuyOrders)) {
      for (QuotedPreBuyOrder quotedPreBuyOrder : quotedPreBuyOrders) {
        quotedPreBuyOrderDTOs.add(quotedPreBuyOrder.toDTO());
      }
    }
    return quotedPreBuyOrderDTOs;
  }

  private List<PreBuyOrderItemDTO> getPreBuyOrderItemDTOByProductDTO(Long shopId,ProductDTO productDTO){
    if(StringUtil.isAllEmpty(productDTO.getName(),productDTO.getBrand(),productDTO.getModel(),productDTO.getSpec(),productDTO.getProductVehicleBrand(),productDTO.getProductVehicleModel())){
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<PreBuyOrderItem> preBuyOrderItemList = writer.getPreBuyOrderItemByProductDTO(shopId,productDTO);
    if(CollectionUtils.isNotEmpty(preBuyOrderItemList)){
      List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = new ArrayList<PreBuyOrderItemDTO>();
      for(PreBuyOrderItem preBuyOrderItem:preBuyOrderItemList){
        preBuyOrderItemDTOList.add(preBuyOrderItem.toDTO());
      }
      return preBuyOrderItemDTOList;
    }
    return null;
  }
  @Override
  public List<PreBuyOrderDTO> createPreBuyOrderByLackRepairOrderDTO(RepairOrderDTO repairOrderDTO) throws Exception {
    List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = new ArrayList<PreBuyOrderItemDTO>();
    if(!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())){
      for(RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()){
        if(NumberUtil.doubleVal(repairOrderItemDTO.getAmount())-NumberUtil.doubleVal(repairOrderItemDTO.getInventoryAmount())-NumberUtil.doubleVal(repairOrderItemDTO.getReserved())<=0 || StringUtil.isAllEmpty(repairOrderItemDTO.getProductName(),repairOrderItemDTO.getBrand(),repairOrderItemDTO.getModel(),repairOrderItemDTO.getSpec(),repairOrderItemDTO.getVehicleBrand(),repairOrderItemDTO.getVehicleModel())){
          continue;
        }
        List<PreBuyOrderItemDTO> dbPreBuyOrderItemDTOList = getPreBuyOrderItemDTOByProductDTO(repairOrderDTO.getShopId(),new ProductDTO(repairOrderDTO.getShopId(),repairOrderItemDTO));
        if(CollectionUtils.isEmpty(dbPreBuyOrderItemDTOList)){
          preBuyOrderItemDTOList.add(new PreBuyOrderItemDTO(repairOrderItemDTO));
        }
      }
    }

    if(CollectionUtils.isEmpty(preBuyOrderItemDTOList)) return null;
    List<PreBuyOrderDTO> orderDTOs=new ArrayList<PreBuyOrderDTO>();
    for (PreBuyOrderItemDTO itemDTO:preBuyOrderItemDTOList){
      PreBuyOrderDTO preBuyOrderDTO = new PreBuyOrderDTO();
      preBuyOrderDTO.setUserId(repairOrderDTO.getUserId());
      preBuyOrderDTO.setShopId(repairOrderDTO.getShopId());
      preBuyOrderDTO.setEditDate(System.currentTimeMillis());
      preBuyOrderDTO.setEditor(repairOrderDTO.getUserName());
      preBuyOrderDTO.setEditorId(repairOrderDTO.getUserId());
      preBuyOrderDTO.setBusinessChanceType(BusinessChanceType.Lack);
      //归属时间
      preBuyOrderDTO.setVestDate(DateUtil.getTheDayTime());
      preBuyOrderDTO.setEndDate(DateUtil.getTheDayTime()+DateUtil.DAY_MILLION_SECONDS*(PreBuyOrderValidDate.SECOND_DAY.getValue()-1));
      PreBuyOrderItemDTO[] itemDTOs= new PreBuyOrderItemDTO[1];
      itemDTOs[0]=itemDTO;
      preBuyOrderDTO.setItemDTOs(itemDTOs);
      orderDTOs.add(preBuyOrderDTO);
    }
    ServiceManager.getService(IImageService.class).addImageToPreBuyOrderFromProduct(new PreBuyOrderDTO[orderDTOs.size()]);
    this.savePreBuyOrder(repairOrderDTO.getShopId(),orderDTOs.toArray(new PreBuyOrderDTO[orderDTOs.size()]));
    createPushMessageBuildTaskDTOsByPreBuyOrderDTO(orderDTOs.toArray(new PreBuyOrderDTO[orderDTOs.size()]));
    return orderDTOs;
  }

  private void createPushMessageBuildTaskDTOsByPreBuyOrderDTO(PreBuyOrderDTO... preBuyOrderDTOs) throws Exception {
    if(ArrayUtils.isEmpty(preBuyOrderDTOs)){
      return;
    }
    ITradePushMessageService tradePushMessageService = ServiceManager.getService(ITradePushMessageService.class);
    List<PushMessageBuildTaskDTO> pushMessageBuildTaskDTOList = new ArrayList<PushMessageBuildTaskDTO>();
    for (PreBuyOrderDTO orderDTO:preBuyOrderDTOs){
      if(orderDTO==null||ArrayUtils.isEmpty(orderDTO.getItemDTOs())){
        LOG.warn(BcgogoExceptionType.IllegalArgument.getMessage());
        continue;
      }
      for(PreBuyOrderItemDTO preBuyOrderItemDTO : orderDTO.getItemDTOs()){
        PushMessageBuildTaskDTO pushMessageBuildTaskDTO = new PushMessageBuildTaskDTO();
        pushMessageBuildTaskDTO.setCreateTime(System.currentTimeMillis());
        pushMessageBuildTaskDTO.setScene(PushMessageScene.PRE_BUY_ORDER_INFORMATION);
        pushMessageBuildTaskDTO.setSeedId(preBuyOrderItemDTO.getId());
        pushMessageBuildTaskDTO.setShopId(orderDTO.getShopId());
        pushMessageBuildTaskDTOList.add(pushMessageBuildTaskDTO);

        pushMessageBuildTaskDTO = new PushMessageBuildTaskDTO();
        pushMessageBuildTaskDTO.setCreateTime(System.currentTimeMillis());
        pushMessageBuildTaskDTO.setScene(PushMessageScene.PRE_BUY_ORDER_ACCESSORY);
        pushMessageBuildTaskDTO.setSeedId(preBuyOrderItemDTO.getId());
        pushMessageBuildTaskDTO.setShopId(orderDTO.getShopId());
        pushMessageBuildTaskDTOList.add(pushMessageBuildTaskDTO);
      }
    }
    tradePushMessageService.savePushMessageBuildTaskDTO(pushMessageBuildTaskDTOList.toArray(new PushMessageBuildTaskDTO[pushMessageBuildTaskDTOList.size()]));
  }

  @Override
  public List<PreBuyOrderDTO> createPreBuyOrderByProductDTO(Long shopId,BusinessChanceType businessChanceType,ProductDTO... productDTOs) throws Exception {
    if(ArrayUtils.isEmpty(productDTOs)){
      return null;
    }
    List<PreBuyOrderItemDTO> preBuyOrderItemDTOList = new ArrayList<PreBuyOrderItemDTO>();
    for(ProductDTO productDTO : productDTOs){
      List<PreBuyOrderItemDTO> dbPreBuyOrderItemDTOList = getPreBuyOrderItemDTOByProductDTO(shopId,productDTO);
      if(CollectionUtils.isEmpty(dbPreBuyOrderItemDTOList)){
        PreBuyOrderItemDTO preBuyOrderItemDTO = new PreBuyOrderItemDTO();
        preBuyOrderItemDTO.setProductDTOWithOutUnit(productDTO);
        preBuyOrderItemDTO.setUnit(productDTO.getStorageUnit());
        preBuyOrderItemDTO.setAmount(productDTO.getSalesAmount());
        preBuyOrderItemDTO.setShopId(shopId);
        preBuyOrderItemDTOList.add(preBuyOrderItemDTO);
      }
    }
    List<PreBuyOrderDTO> orderDTOs=new ArrayList<PreBuyOrderDTO>();
    for(PreBuyOrderItemDTO itemDTO:preBuyOrderItemDTOList){
      PreBuyOrderDTO preBuyOrderDTO = new PreBuyOrderDTO();
      preBuyOrderDTO.setShopId(shopId);
      preBuyOrderDTO.setEditDate(System.currentTimeMillis());
      preBuyOrderDTO.setBusinessChanceType(businessChanceType);
      //归属时间
      preBuyOrderDTO.setVestDate(DateUtil.getTheDayTime());
      preBuyOrderDTO.setEndDate(DateUtil.getTheDayTime()+DateUtil.DAY_MILLION_SECONDS*(PreBuyOrderValidDate.FIFTEEN_DAY.getValue()-1));
      PreBuyOrderItemDTO[] itemDTOs=new PreBuyOrderItemDTO[1];
      itemDTOs[0]=itemDTO;
      preBuyOrderDTO.setItemDTOs(itemDTOs);
      orderDTOs.add(preBuyOrderDTO);
    }
    ServiceManager.getService(IImageService.class).addImageToPreBuyOrderFromProduct(new PreBuyOrderDTO[orderDTOs.size()]);
    this.savePreBuyOrder(shopId,orderDTOs.toArray(new PreBuyOrderDTO[orderDTOs.size()]));
    createPushMessageBuildTaskDTOsByPreBuyOrderDTO(orderDTOs.toArray(new PreBuyOrderDTO[orderDTOs.size()]));
    return orderDTOs;
  }

  @Override
  public Long countValidPreBuyOrderItemsByType(Long shopId, BusinessChanceType type) {
    if(shopId == null || type == null) {
      LOG.error("shopId is null or BusinessChanceType is null!");
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countValidPreBuyOrderItemsByType(shopId, type);
  }

  @Override
  public List<PreBuyOrderDTO> getValidPreBuyOrderDTOsByShopIdWithoutSelf(Long shopId, Long preBuyOrderId) {
    if (shopId == null) {
      throw new RuntimeException("getValidPreBuyOrderItemDTOsByShopId,shopId is null.");
    }
    List<PreBuyOrderDTO> preBuyOrderDTOs = new ArrayList<PreBuyOrderDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<PreBuyOrder> preBuyOrders = writer.getValidPreBuyOrdersWithoutSelf(shopId, preBuyOrderId);
    if (CollectionUtils.isNotEmpty(preBuyOrders)) {
      for (PreBuyOrder preBuyOrder : preBuyOrders) {
        PreBuyOrderDTO preBuyOrderDTO = preBuyOrder.toDTO();
        List<PreBuyOrderItemDTO> preBuyOrderItemDTOs = new ArrayList<PreBuyOrderItemDTO>();
        List<PreBuyOrderItem> preBuyOrderItems = writer.getPreBuyOrderItemsByPreBuyOrderId(preBuyOrder.getId());
        if (CollectionUtils.isNotEmpty(preBuyOrderItems)) {
          int orderQuotedCount = 0; // 单个求购单下的报价次数
          for (PreBuyOrderItem preBuyOrderItem : preBuyOrderItems) {
            if(preBuyOrderItem==null){
              continue;
            }
            preBuyOrderItemDTOs.add(preBuyOrderItem.toDTO());
            orderQuotedCount += preBuyOrderItem.getQuotedCount() == null ? 0L : preBuyOrderItem.getQuotedCount().intValue();
          }
          preBuyOrderDTO.setItemDTOs(preBuyOrderItemDTOs.toArray(new PreBuyOrderItemDTO[preBuyOrderItemDTOs.size()]));
          preBuyOrderDTO.setItemDTO(preBuyOrderItems.get(0).toDTO());
          preBuyOrderDTO.setQuotedCount(orderQuotedCount);
        }
        preBuyOrderDTOs.add(preBuyOrderDTO);
      }
    }
    return preBuyOrderDTOs;
  }

  @Override
  public Map<String, Object> getValidPreBuyOrderInfo(PreBuyOrderSearchCondition condition) {
    Map<String,Object> validPreBuyOrderInfo = new HashMap<String, Object>();
    IPreBuyOrderService preBuyOrderService = ServiceManager.getService(IPreBuyOrderService.class);
    Long countPreBuyNormal = preBuyOrderService.countValidPreBuyOrderItemsByType(condition.getShopId(), BusinessChanceType.Normal);
    Long countPreBuySellWell = preBuyOrderService.countValidPreBuyOrderItemsByType(condition.getShopId(), BusinessChanceType.SellWell);
    Long countPreBuyLack = preBuyOrderService.countValidPreBuyOrderItemsByType(condition.getShopId(), BusinessChanceType.Lack);
    List<PreBuyOrderDTO> preBuyOrderDTOList = preBuyOrderService.getValidPreBuyOrderDTOsByShopIdWithoutSelf(condition.getShopId(), condition.getNonePreBuyOrderId());
    validPreBuyOrderInfo.put("countPreBuyNormal",countPreBuyNormal);
    validPreBuyOrderInfo.put("countPreBuySellWell",countPreBuySellWell);
    validPreBuyOrderInfo.put("countPreBuyLack",countPreBuyLack);
    validPreBuyOrderInfo.put("preBuyOrderDTOList",preBuyOrderDTOList);
    return validPreBuyOrderInfo;
  }

  @Override
  public List<PreBuyOrderDTO> getOtherShopPreBuyOrders(PreBuyOrderSearchCondition condition) {
    List<PreBuyOrderDTO> preBuyOrderDTOs = new ArrayList<PreBuyOrderDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<PreBuyOrder> preBuyOrderList = writer.getOtherShopPreBuyOrders(condition);
    if(CollectionUtil.isNotEmpty(preBuyOrderList)) {
      for(PreBuyOrder preBuyOrder : preBuyOrderList) {
        PreBuyOrderDTO preBuyOrderDTO = preBuyOrder.toDTO();
        List<PreBuyOrderItemDTO> preBuyOrderItemDTOs = new ArrayList<PreBuyOrderItemDTO>();
        List<PreBuyOrderItem> preBuyOrderItems = writer.getPreBuyOrderItemsByPreBuyOrderId(preBuyOrder.getId());
        if (CollectionUtils.isNotEmpty(preBuyOrderItems)) {
          int orderQuotedCount = 0; // 单个求购单下的报价次数
          for (PreBuyOrderItem preBuyOrderItem : preBuyOrderItems) {
            preBuyOrderItemDTOs.add(preBuyOrderItem.toDTO());
            orderQuotedCount += preBuyOrderItem.getQuotedCount() == null ? 0L : preBuyOrderItem.getQuotedCount().intValue();
          }
          preBuyOrderDTO.setItemDTOs(preBuyOrderItemDTOs.toArray(new PreBuyOrderItemDTO[preBuyOrderItemDTOs.size()]));
          preBuyOrderDTO.setItemDTO(preBuyOrderItems.get(0).toDTO());
          preBuyOrderDTO.setQuotedCount(orderQuotedCount);

        }
        preBuyOrderDTOs.add(preBuyOrderDTO);
      }
    }
    return preBuyOrderDTOs;
  }

  public List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOsByPager(Long preBuyOrderItemId, int pageStart, int pageSize) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList = new ArrayList<QuotedPreBuyOrderItemDTO>();
    List<QuotedPreBuyOrderItem> quotedPreBuyOrderItemList = writer.getQuotedPreBuyOrderItemsByPager(preBuyOrderItemId,pageStart,pageSize);
    if (CollectionUtils.isNotEmpty(quotedPreBuyOrderItemList)) {
      Set<Long> productIdSet = new HashSet<Long>();
      for (QuotedPreBuyOrderItem quotedPreBuyOrderItem : quotedPreBuyOrderItemList) {
        productIdSet.add(quotedPreBuyOrderItem.getProductId());
      }
      Map<Long, ProductDTO> productDTOMap = ServiceManager.getService(IProductService.class).getProductDTOMapByProductLocalInfoIds(productIdSet);

      QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO = null;
      for (QuotedPreBuyOrderItem quotedPreBuyOrderItem : quotedPreBuyOrderItemList) {
        quotedPreBuyOrderItemDTO = quotedPreBuyOrderItem.toDTO();
        quotedPreBuyOrderItemDTO.setProductDTOWithOutUnit(productDTOMap.get(quotedPreBuyOrderItem.getProductId()));
        ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(quotedPreBuyOrderItemDTO.getShopId());
        quotedPreBuyOrderItemDTO.setShopName(shopDTO.getName());
        Set<Long> quotedPreBuyOrderIds = new HashSet<Long>();
        quotedPreBuyOrderIds.add(quotedPreBuyOrderItemDTO.getQuotedPreBuyOrderId());
        List<QuotedPreBuyOrderDTO> quotedPreBuyOrderDTOList = this.getQuotedPreBuyOrdersByQuotePreBuyOrderIds(quotedPreBuyOrderIds);
        if(CollectionUtil.isNotEmpty(quotedPreBuyOrderDTOList)) {
          QuotedPreBuyOrderDTO quotedPreBuyOrderDTO = quotedPreBuyOrderDTOList.get(0);
          quotedPreBuyOrderItemDTO.setQuotedDateStr(quotedPreBuyOrderDTO.getVestDateStr());
        }
        quotedPreBuyOrderItemDTO.setCountSupplierOtherQuoted(this.countSupplierOtherQuotedItems(quotedPreBuyOrderItemDTO.getShopId(),getPreBuyOrderItemDTOById(preBuyOrderItemId).getShopId(),quotedPreBuyOrderItemDTO.getId()));
        quotedPreBuyOrderItemDTO.setQqArray(shopDTO.getQqArray());
        quotedPreBuyOrderItemDTOList.add(quotedPreBuyOrderItemDTO);
      }

    }
    return quotedPreBuyOrderItemDTOList;
  }

  @Override
  public Long countOtherShopPreBuyOrders(Long shopId,Long noneShopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countOtherShopPreBuyOrders(shopId,noneShopId);
  }

  @Override
  public void processLackAutoPreBuy() throws Exception {
    IConfigService configService= ServiceManager.getService(IConfigService.class);
    List<ShopDTO> shopDTOs=configService.getActiveShop();
    IPreBuyOrderService preBuyOrderService= ServiceManager.getService(IPreBuyOrderService.class);
    IProductService productService= ServiceManager.getService(IProductService.class);
    for(ShopDTO shopDTO:shopDTOs){
      Long shopId=shopDTO.getId();
      if(shopId.equals(10000010009160816l)){
        System.out.print("bb");
      }
      List<Long> lackProductIds=productService.getLackAutoPreBuyProductId(shopId);
      if(CollectionUtil.isEmpty(lackProductIds)){
        continue;
      }
      PreBuyOrderSearchCondition condition=new PreBuyOrderSearchCondition();
      condition.setValid(true);
      condition.setProductIds(lackProductIds.toArray(new Long[lackProductIds.size()]));
      List<PreBuyOrderItemDTO> existItemDTOs=preBuyOrderService.getPreBuyOrderItemDetailDTO(condition);
      List<Long> existProductIds=new ArrayList<Long>();
      if(CollectionUtil.isNotEmpty(existItemDTOs)){
        for(PreBuyOrderItemDTO itemDTO:existItemDTOs){
          existProductIds.add(itemDTO.getProductId());
        }
      }
      List<Long> productIds=new ArrayList<Long>();
      //剔除已经发过求购的productId
      for(Long productId:lackProductIds){
        if(existProductIds.contains(productId)){
          continue;
        }
        productIds.add(productId);
      }
      List<ProductDTO> productDTOs=productService.getProductDTOByIds(productIds.toArray(new Long[productIds.size()]));
      if(CollectionUtil.isNotEmpty(productDTOs)){
        List<PreBuyOrderDTO> orderDTOs=new ArrayList<PreBuyOrderDTO>();
        List<PreBuyOrderItemDTO> itemDTOs=new ArrayList<PreBuyOrderItemDTO>();
        for(ProductDTO productDTO:productDTOs){
          PreBuyOrderDTO orderDTO=new PreBuyOrderDTO();
          orderDTO.setShopId(productDTO.getShopId());
          orderDTO.setEditDate(System.currentTimeMillis());
          orderDTO.setVestDate(System.currentTimeMillis());
          orderDTO.setEndDate(DateUtil.getDateByDay(PreBuyOrderValidDate.SEVEN_DAY.getValue()));
          orderDTO.setBusinessChanceType(BusinessChanceType.Lack);
          orderDTO.setPreBuyOrderValidDate(PreBuyOrderValidDate.SEVEN_DAY);
          PreBuyOrderItemDTO itemDTO=new PreBuyOrderItemDTO();
          itemDTO.setProductId(productDTO.getProductLocalInfoId());
          itemDTO.setCommodityCode(productDTO.getCommodityCode());
          itemDTO.setProductName(productDTO.getName());
          itemDTO.setBrand(productDTO.getBrand());
          itemDTO.setSpec(productDTO.getSpec());
          itemDTO.setModel(productDTO.getModel());
          itemDTO.setVehicleBrand(productDTO.getVehicleBrand());
          itemDTO.setVehicleModel(productDTO.getVehicleModel());
          itemDTO.setAmount(1D);
          itemDTO.setUnit(productDTO.getUnit());
          PreBuyOrderItemDTO [] preBuyOrderItemDTOs=new PreBuyOrderItemDTO[1];
          preBuyOrderItemDTOs[0]=itemDTO;
          itemDTOs.add(itemDTO);
          orderDTO.setItemDTOs(preBuyOrderItemDTOs);
          orderDTOs.add(orderDTO);
        }
        preBuyOrderService.savePreBuyOrder(shopId,orderDTOs.toArray(new PreBuyOrderDTO[orderDTOs.size()]));
        //推送消息
        ITradePushMessageService tradePushMessageService = ServiceManager.getService(ITradePushMessageService.class);
        List<PushMessageBuildTaskDTO> pushMessageBuildTaskDTOList = new ArrayList<PushMessageBuildTaskDTO>();
        for(PreBuyOrderItemDTO preBuyOrderItemDTO : itemDTOs){
          PushMessageBuildTaskDTO pushMessageBuildTaskDTO = new PushMessageBuildTaskDTO();
          pushMessageBuildTaskDTO.setCreateTime(System.currentTimeMillis());
          pushMessageBuildTaskDTO.setScene(PushMessageScene.PRE_BUY_ORDER_INFORMATION);
          pushMessageBuildTaskDTO.setSeedId(preBuyOrderItemDTO.getId());
          pushMessageBuildTaskDTO.setShopId(shopId);
          pushMessageBuildTaskDTOList.add(pushMessageBuildTaskDTO);

          pushMessageBuildTaskDTO = new PushMessageBuildTaskDTO();
          pushMessageBuildTaskDTO.setCreateTime(System.currentTimeMillis());
          pushMessageBuildTaskDTO.setScene(PushMessageScene.PRE_BUY_ORDER_ACCESSORY);
          pushMessageBuildTaskDTO.setSeedId(preBuyOrderItemDTO.getId());
          pushMessageBuildTaskDTO.setShopId(shopId);
          pushMessageBuildTaskDTOList.add(pushMessageBuildTaskDTO);
        }
        tradePushMessageService.savePushMessageBuildTaskDTO(pushMessageBuildTaskDTOList.toArray(new PushMessageBuildTaskDTO[pushMessageBuildTaskDTOList.size()]));
        List<Long> orderIds=new ArrayList<Long>();
        for(PreBuyOrderDTO orderDTO:orderDTOs){
          orderIds.add(orderDTO.getId());
        }
        ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(
          ServiceManager.getService(IConfigService.class).getShopById(shopId), OrderTypes.PRE_BUY_ORDER, ArrayUtil.toLongArr(orderIds));
        LOG.info("lack auto save pre buy num"+itemDTOs.size());
      }
    }
    LOG.info("lack auto pre buy end");
  }

}
