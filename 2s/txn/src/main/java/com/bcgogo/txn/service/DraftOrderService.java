package com.bcgogo.txn.service;

import com.bcgogo.enums.DraftOrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.PageException;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.dto.DraftOrderSearchDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-8
 * Time: 上午5:16
 * To change this template use File | Settings | File Templates.
 */
@Component
public class DraftOrderService implements IDraftOrderService{
  private static final Logger LOG = LoggerFactory.getLogger(DraftOrderService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  /**
   * 根据shopId和orderType查询所有单据草稿
   * @param draftOrderSearchDTO
   * @return
   * @throws PageException
   */
  public List<DraftOrderDTO> getDraftOrders(DraftOrderSearchDTO draftOrderSearchDTO) throws PageException, ParseException {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    IUserService userService=ServiceManager.getService(IUserService.class);
    List <DraftOrderDTO> draftOrderDTOs = new ArrayList<DraftOrderDTO>();
    Long startTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, draftOrderSearchDTO.getStartTime());
    Long endTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, draftOrderSearchDTO.getEndTime());
    List<DraftOrder> draftOrders = txnWriter.getDraftOrders(draftOrderSearchDTO.getShopId(),draftOrderSearchDTO.getUserId(),
        draftOrderSearchDTO.getVehicleId(), draftOrderSearchDTO.getPager(),draftOrderSearchDTO.getOrderTypes(),startTime,endTime);
    UserDTO userDTO =  userService.getUserByUserId(draftOrderSearchDTO.getUserId());
    for(DraftOrder draftOrder:draftOrders){
      DraftOrderDTO draftOrderDTO=draftOrder.toDTO();
      if(userDTO != null) {
        draftOrderDTO.setUserName(userDTO.getName());
      }
      draftOrderDTOs.add(draftOrderDTO);
    }
    return draftOrderDTOs;
  }

  /**
   * 加载draftOrder和draftOrderItems,比较耗性能，可用方法lazyLoadDraftOrderId替换此
   * @param shopId
   * @param draftOrderId
   * @return
   * @throws Exception
   */
  public DraftOrderDTO getOrderByDraftOrderId(Long shopId,Long shopVersionId,Long draftOrderId) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    DraftOrder draftOrder= txnWriter.getDraftOrderById(shopId,draftOrderId);
    if(draftOrder==null){
      return null;
    }
    List<DraftOrderItem> draftOrderItems = txnWriter.getItemsByDraftOrderId(draftOrder.getId());
    return generateDraftOrderDTO(shopVersionId,draftOrder,draftOrderItems);
  }

  /**
   * 仅仅加载draftOrder，不加载draftOrderItem
   * @param shopId
   * @param draftOrderId
   * @return
   */
  public DraftOrder lazyLoadDraftOrderId(Long shopId,Long draftOrderId){
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.getDraftOrderById(shopId,draftOrderId);
  }

  public List<DraftOrder> getDraftOrdersByCustomerOrSupplierId(Long shopId,Long customerId){
    TxnWriter txnWriter = txnDaoManager.getWriter();
    return txnWriter.getDraftOrdersByCustomerOrSupplierId(shopId,customerId);
  }

  /**
   *  保存或更新单据草稿
   * @param draftOrderDTO
   * @return
   */
  public DraftOrderDTO saveOrUpdateDraftOrder(DraftOrderDTO draftOrderDTO) throws BcgogoException {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    DraftOrder draftOrder=null;
    if(draftOrderDTO.getId()!=null){
      draftOrder=this.lazyLoadDraftOrderId(draftOrderDTO.getShopId(),draftOrderDTO.getId());
    }else if(draftOrderDTO.getTxnOrderId()!=null){
      draftOrder = txnWriter.getDraftOrderByTxnOrderId(draftOrderDTO.getShopId(),draftOrderDTO.getTxnOrderId());
    }
    Object status = txnWriter.begin();
    try{
      if(draftOrder==null) {
        draftOrder= new DraftOrder();
        draftOrder.setStatus(DraftOrderStatus.DRAFT_SAVED);
        draftOrder.setSaveTime(DateUtil.getToday(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, new Date()));
        txnWriter.save(draftOrder.fromDTO(draftOrderDTO));
        draftOrderDTO.setId(draftOrder.getId());
      }else {
        draftOrder.setStatus(DraftOrderStatus.DRAFT_SAVED);
        draftOrder.setSaveTime(DateUtil.getToday(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, new Date()));
        txnWriter.update(draftOrder.fromDTO(draftOrderDTO));
      }
      this.saveDraftOrderItems(draftOrderDTO, txnWriter);
      this.saveDraftOrderOtherIncomeItems(draftOrderDTO, txnWriter);
      txnWriter.commit(status);

      return draftOrder.toDTO();
    }catch (Exception e){
      LOG.error("保存草稿出现异常！");
      throw new BcgogoException(e.getMessage(), e);
    } finally {
      txnWriter.rollback(status);
    }
  }

  /**
   * 删除草稿
   * @param draftOrderId
   * @return
   * @throws BcgogoException
   */
  public DraftOrder deleteDraftOrder(Long shopId,Long draftOrderId) throws BcgogoException {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    DraftOrder draftOrder=this.lazyLoadDraftOrderId(shopId,draftOrderId);
    if(draftOrder==null){
      return null;
    }
    Object status = txnWriter.begin();
    try{
      draftOrder.setStatus(DraftOrderStatus.DRAFT_REPEAL);
      txnWriter.update(draftOrder);
      txnWriter.commit(status);
      return draftOrder;
    }catch (Exception e){
      LOG.error("删除草稿出现异常！");
      throw new BcgogoException(e.getMessage(), e);
    }finally{
      txnWriter.rollback(status);
    }
  }
  /**
   * 删除草稿
   * @param txnOrderId
   * @return
   * @throws BcgogoException
   */
  public DraftOrder deleteDraftOrderByTxnOrderId(Long shopId,Long txnOrderId) throws BcgogoException {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    DraftOrder draftOrder=txnWriter.getDraftOrderByTxnOrderId(shopId,txnOrderId);
    if(draftOrder==null){
      return null;
    }
    Object status = txnWriter.begin();
    try{
      draftOrder.setStatus(DraftOrderStatus.DRAFT_REPEAL);
      txnWriter.update(draftOrder);
      txnWriter.commit(status);
      return draftOrder;
    }catch (Exception e){
      LOG.error("删除草稿出现异常！");
      throw new BcgogoException(e.getMessage(), e);
    }finally{
      txnWriter.rollback(status);
    }
  }
  /**
   * 保存草稿所有项目
   * @param draftOrderDTO
   * @param txnWriter
   */
  private void saveDraftOrderItems(DraftOrderDTO draftOrderDTO, TxnWriter txnWriter){
    if(draftOrderDTO.getItemDTOs()==null||draftOrderDTO.getShopId()==null||draftOrderDTO.getId()==null) return;
    txnWriter.deleteDraftOrderItemsByDraftOrderId(draftOrderDTO.getShopId(),draftOrderDTO.getId());
    for (DraftOrderItemDTO item:draftOrderDTO.getItemDTOs()){
      DraftOrderItem draftOrderItem=new DraftOrderItem();
      item.setDraftOrderId(draftOrderDTO.getId());
      draftOrderItem.fromDTO(item);
      txnWriter.save(draftOrderItem);
    }
  }

  public int countDraftOrders(DraftOrderSearchDTO draftOrderSearchDTO) throws Exception{
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Long startTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, draftOrderSearchDTO.getStartTime());
    Long endTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, draftOrderSearchDTO.getEndTime());
    return txnWriter.countDraftOrders(draftOrderSearchDTO.getShopId(),draftOrderSearchDTO.getUserId(), draftOrderSearchDTO.getVehicleId() ,draftOrderSearchDTO.getOrderTypes(), startTime, endTime);
  }

  public void setTxnDaoManager(TxnDaoManager txnDaoManager) {
    this.txnDaoManager = txnDaoManager;
  }

  /**
   * 根据单据类型计算对象草稿条数
   * @param draftOrderSearchDTO
   * @return
   */
  public List countDraftOrderByOrderType(DraftOrderSearchDTO draftOrderSearchDTO) throws Exception{
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Long startTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, draftOrderSearchDTO.getStartTime());
    Long endTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, draftOrderSearchDTO.getEndTime());
    return txnWriter.countDraftOrderByOrderType(draftOrderSearchDTO.getShopId(), draftOrderSearchDTO.getUserId(), draftOrderSearchDTO.getOrderTypes(), startTime, endTime);
  }

  /**
   * @param vechicle
   * @return
   */
  public DraftOrderDTO getDraftOrderByVechicle(Long shopId,Long shopVersionId,String vechicle) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    DraftOrder draftOrder= txnWriter.getDraftOrderByVechicle(shopId,vechicle);
    if(draftOrder==null){
      return null;
    }
    List<DraftOrderItem> draftOrderItems = txnWriter.getItemsByDraftOrderId(draftOrder.getId());
    return generateDraftOrderDTO(shopVersionId,draftOrder,draftOrderItems);
  }

  /**
   * @param txnOrderId     todo 需要性能优化 by qxy
   * @return
   */
  public DraftOrderDTO getDraftOrderByTxnOrderId(Long shopId,Long shopVersionId,Long txnOrderId) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    DraftOrder draftOrder= txnWriter.getDraftOrderByTxnOrderId(shopId,txnOrderId);
    if(draftOrder==null){
      return null;
    }
    List<DraftOrderItem> draftOrderItems = txnWriter.getItemsByDraftOrderId(draftOrder.getId());
    return generateDraftOrderDTO(shopVersionId,draftOrder,draftOrderItems);
  }

  private DraftOrderDTO generateDraftOrderDTO(Long shopVersionId,DraftOrder draftOrder,List<DraftOrderItem> draftOrderItems) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    DraftOrderDTO draftOrderDTO=draftOrder.toDTO();
    IProductService productService = ServiceManager.getService(IProductService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    if (draftOrderDTO.getBillProducerId() != null) {
      UserDTO userDTO = userService.getUserByUserId(draftOrderDTO.getBillProducerId());
      if (userDTO != null) {
        draftOrderDTO.setBillProducer(userDTO.getName());
      } else {
        SalesManDTO salesManDTO = membersService.getSaleManDTOById(draftOrder.getShopId(), draftOrderDTO.getBillProducerId());
        if (salesManDTO != null)
          draftOrderDTO.setBillProducer(salesManDTO.getName());
      }
    }

    List<DraftOrderItemDTO> draftOrderItemDTOs=new ArrayList<DraftOrderItemDTO>();
    ProductDTO productDTO = null;
    InventoryDTO inventoryDTO = null;
    StoreHouseInventoryDTO storeHouseInventoryDTO = null;
    DraftOrderItemDTO draftOrderItemDTO;
    for (DraftOrderItem draftOrderItem : draftOrderItems) {
      draftOrderItemDTO = draftOrderItem.toDTO();
      draftOrderItemDTOs.add(draftOrderItemDTO);
      if (OrderTypes.PURCHASE.equals(draftOrder.getOrderTypeEnum()) && draftOrder.getCustomerOrSupplierShopId() != null) {
        if (draftOrderItem.getSupplierProductLocalInfoId() != null) {
          productDTO = productService.getProductByProductLocalInfoId(draftOrderItem.getSupplierProductLocalInfoId(), draftOrder.getCustomerOrSupplierShopId());
        }
      } else {
        if (draftOrderItem.getProductLocalInfoId() != null) {
          productDTO = productService.getProductByProductLocalInfoId(draftOrderItem.getProductLocalInfoId(), draftOrderItem.getShopId());
          inventoryDTO = txnService.getInventoryByShopIdAndProductId(draftOrderItem.getShopId(), draftOrderItem.getProductLocalInfoId());
          if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)&& draftOrder.getStorehouseId() != null) {
            storeHouseInventoryDTO = storeHouseService.getStoreHouseInventoryDTO(draftOrder.getStorehouseId(), draftOrderItem.getProductLocalInfoId());
            inventoryDTO.setAmount(storeHouseInventoryDTO == null ? 0d : storeHouseInventoryDTO.getAmount());
          }
          draftOrderItemDTO.toLastedProductAndInventoryInfo(productDTO, inventoryDTO, draftOrderDTO);
        }
      }
      //检查item上的商品编码是否已经被别的商品使用，如果是则将当前单据上的商品编码改为该商品的默认编码
      if (StringUtils.isNotBlank(draftOrderItemDTO.getCommodityCode())) {
        List<ProductDTO> productDTOs = null;
        if (OrderTypes.PURCHASE.equals(draftOrder.getOrderTypeEnum()) && draftOrder.getCustomerOrSupplierShopId() != null) {
          productDTOs = productService.getProductDTOsByCommodityCodes(draftOrder.getCustomerOrSupplierShopId(), draftOrderItemDTO.getCommodityCode());
        } else {
          productDTOs = productService.getProductDTOsByCommodityCodes(draftOrder.getShopId(), draftOrderItemDTO.getCommodityCode());
        }
        //商品编码修改Flag true 表示使用productDTO的商品编码，或者置空。false表示使用草稿箱中的编码
        boolean commodityCodeFlag = true;
        if (CollectionUtils.isNotEmpty(productDTOs)) {
          for (ProductDTO temProduct : productDTOs) {
            if (productDTO != null && productDTO.getId().equals(temProduct.getId())) {
              commodityCodeFlag = false;
              break;
            }
          }
        } else {
          commodityCodeFlag = false;
        }
        if (commodityCodeFlag) {
          if (productDTO != null) {
            draftOrderItemDTO.setCommodityCode(productDTO.getCommodityCode());
          } else {
            draftOrderItemDTO.setCommodityCode(null);
          }
          draftOrderItemDTO.setCommodityCodeModifyFlag(false);
        } else {
          draftOrderItemDTO.setCommodityCodeModifyFlag(true);
        }
      } else if (draftOrder.getShopId() != null) {
        if (productDTO != null && StringUtils.isBlank(productDTO.getCommodityCode())) {
          draftOrderItemDTO.setCommodityCodeModifyFlag(false);
        } else {
          draftOrderItemDTO.setCommodityCodeModifyFlag(true);
        }
      }
    }
    List<DraftOrderOtherIncomeItem> orderOtherIncomeItemList = txnWriter.getOtherIncomeItemsByDraftOrderId(draftOrderDTO.getId());
    List<DraftOrderOtherIncomeItemDTO> orderOtherIncomeItemDTOList = null;
    if(CollectionUtils.isNotEmpty(orderOtherIncomeItemList)) {
      orderOtherIncomeItemDTOList = new ArrayList<DraftOrderOtherIncomeItemDTO>();
      for (DraftOrderOtherIncomeItem item : orderOtherIncomeItemList) {
        orderOtherIncomeItemDTOList.add(item.toDTO());
      }

    }

    draftOrderDTO.setOtherIncomeItemDTOList(orderOtherIncomeItemDTOList);
    draftOrderDTO.setItemDTOs(draftOrderItemDTOs.toArray(new DraftOrderItemDTO[draftOrderItemDTOs.size()]));
    return draftOrderDTO;
  }

  @Override
  public List<DraftOrder> getDraftOrder(Long shopId,List<OrderTypes> orderTypesList,Long customerId)
  {
    TxnWriter writer = txnDaoManager.getWriter();

    return writer.getDraftOrder(shopId,orderTypesList,customerId);
  }

  @Override
  public List<DraftOrder> deleteDraftOrderList(List<DraftOrder> draftOrderList) throws BcgogoException
  {
    if(CollectionUtils.isEmpty(draftOrderList))
    {
      return null;
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();

    Object status = txnWriter.begin();
    try{
      for(DraftOrder draftOrder : draftOrderList)
      {
        if(null != draftOrder.getId())
        {
          draftOrder.setStatus(DraftOrderStatus.DRAFT_REPEAL);
          txnWriter.update(draftOrder);
        }
      }

      txnWriter.commit(status);
      return draftOrderList;
    }catch (Exception e){
      LOG.error("删除草稿出现异常！");
      throw new BcgogoException(e.getMessage(), e);
    }finally{
      txnWriter.rollback(status);
    }
  }

    /**
   * 保存草稿所有费用项目
   * @param draftOrderDTO
   * @param txnWriter
   */
  private void saveDraftOrderOtherIncomeItems(DraftOrderDTO draftOrderDTO, TxnWriter txnWriter){
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    txnWriter.deleteDraftOrderOtherIncomeItemsByDraftOrderId(draftOrderDTO.getShopId(),draftOrderDTO.getId());
    if(CollectionUtils.isEmpty(draftOrderDTO.getOtherIncomeItemDTOList())) return;
    for (DraftOrderOtherIncomeItemDTO itemDTO:draftOrderDTO.getOtherIncomeItemDTOList()){
      DraftOrderOtherIncomeItem draftOrderOtherIncomeItem=new DraftOrderOtherIncomeItem();
      itemDTO.setOrderId(draftOrderDTO.getId());
      itemDTO.setShopId(draftOrderDTO.getShopId());
      draftOrderOtherIncomeItem.fromDTO(itemDTO);
      txnWriter.save(draftOrderOtherIncomeItem);
    }
  }

}
