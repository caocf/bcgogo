package com.bcgogo.txn.service.finance;

import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.config.ShopSearchCondition;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.RegisterInfoDTO;
import com.bcgogo.config.dto.ShopBargainRecordDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.*;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.shop.BargainStatus;
import com.bcgogo.enums.shop.RegisterType;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.enums.txn.finance.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.BcgogoProductDTO;
import com.bcgogo.product.BcgogoProductPropertyDTO;
import com.bcgogo.product.model.BcgogoProductProperty;
import com.bcgogo.product.service.IBcgogoProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.txn.dto.finance.*;
import com.bcgogo.txn.model.Service;
import com.bcgogo.txn.model.SmsRecharge;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.finance.*;
import com.bcgogo.txn.service.ISmsRechargeService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.OccupationDTO;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.*;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 下午5:29
 */
@Component
public class BcgogoReceivableService implements IBcgogoReceivableService {
  private static final Logger LOG = LoggerFactory.getLogger(BcgogoReceivableService.class);
  private static final Double ZERO = 0.0001D;
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public Result searchBcgogoReceivableOrderResult(BcgogoReceivableSearchCondition condition) throws BcgogoException {
    Result result = new Result(true);
    IShopBargainService shopBargainService = ServiceManager.getService(IShopBargainService.class);

    List<Long> shopIds = new ArrayList<Long>();
    if (StringUtils.isNotBlank(condition.getShopName()) || StringUtils.isNotBlank(condition.getFollowName()) || !ArrayUtils.isEmpty(condition.getBargainStatuses()) || !ArrayUtils.isEmpty(condition.getShopVersionIds())) {
      ShopSearchCondition shopSearchCondition = new ShopSearchCondition();
      shopSearchCondition.setName(condition.getShopName());
      shopSearchCondition.setFollowName(condition.getFollowName());
      shopSearchCondition.setShopVersionIds(condition.getShopVersionIds());
      shopSearchCondition.setBargainStatuses(condition.getBargainStatuses());
      shopIds = ServiceManager.getService(IShopService.class).getShopIdByShopCondition(shopSearchCondition);
      if (CollectionUtil.isEmpty(shopIds)) {
        result.setTotal(0);
        return result;
      }
      condition.setShopIds(shopIds);
    }
    TxnWriter writer = txnDaoManager.getWriter();
    result.setTotal(writer.countBcgogoReceivableOrder(condition));

    List<BcgogoReceivableOrder> bcgogoReceivableOrderList = writer.searchBcgogoReceivableOrderResult(condition);
    List<BcgogoReceivableOrderDTO> bcgogoReceivableOrderDTOList = new ArrayList<BcgogoReceivableOrderDTO>();
    if(CollectionUtils.isNotEmpty(bcgogoReceivableOrderList)){
      shopIds.clear();
      List<Long> orderIdList = new ArrayList<Long>();
      for (BcgogoReceivableOrder bcgogoReceivableOrder : bcgogoReceivableOrderList) {
        if (bcgogoReceivableOrder.getShopId() != null) shopIds.add(bcgogoReceivableOrder.getShopId());
        orderIdList.add(bcgogoReceivableOrder.getId());
      }
      Map<Long, ShopDTO> shopDTOMap = new HashMap<Long, ShopDTO>();
      if (CollectionUtil.isNotEmpty(shopIds)) {
        shopDTOMap = ServiceManager.getService(IShopService.class).getShopByShopIds(shopIds.toArray(new Long[shopIds.size()]));
      }
      Map<Long,ShopBargainRecordDTO> shopBargainRecordDTOMap = shopBargainService.getShopAuditPassBargainRecordMapByShopId(shopIds.toArray(new Long[shopIds.size()]));

      Map<Long,BcgogoReceivableRecordDTO> bcgogoReceivableOrderToBePaidRecordDTOMap = new HashMap<Long, BcgogoReceivableRecordDTO>();
      Map<Long, InstalmentPlanDTO> instalmentPlanDetailMap = new HashMap<Long, InstalmentPlanDTO>();//软件用
      Map<Long,List<BcgogoReceivableOrderItemDTO>> bcgogoReceivableOrderItemDTOMap = new HashMap<Long, List<BcgogoReceivableOrderItemDTO>>();//硬件用

      List<Object[]> objectsList = writer.getBcgogoReceivableOrderRecordAndRelationByOrderId(orderIdList.toArray(new Long[orderIdList.size()]));
      Set<Long> instalmentPlanIdSet = new HashSet<Long>();
      if(CollectionUtils.isNotEmpty(objectsList)){
        BcgogoReceivableOrderRecordRelation bcgogoReceivableOrderRecordRelation = null;
        BcgogoReceivableRecord bcgogoReceivableRecord = null;
        for(Object[] objects : objectsList){
          if(objects!=null && objects.length==2 && objects[0]!=null && objects[1]!=null){
            bcgogoReceivableOrderRecordRelation = (BcgogoReceivableOrderRecordRelation)objects[0];
            bcgogoReceivableRecord = (BcgogoReceivableRecord)objects[1];
            BcgogoReceivableRecordDTO bcgogoReceivableRecordDTO = bcgogoReceivableRecord.toDTO();
            bcgogoReceivableRecordDTO.setBcgogoReceivableOrderRecordRelationDTO(bcgogoReceivableOrderRecordRelation.toDTO());
            if(BcgogoReceivableStatus.TO_BE_PAID.toString().equals(bcgogoReceivableRecordDTO.getStatus())){
              bcgogoReceivableOrderToBePaidRecordDTOMap.put(bcgogoReceivableOrderRecordRelation.getBcgogoReceivableOrderId(),bcgogoReceivableRecordDTO);
            }
            if(ReceivableMethod.INSTALLMENT.equals(bcgogoReceivableOrderRecordRelation.getReceivableMethod()) &&  bcgogoReceivableOrderRecordRelation.getInstalmentPlanId()!=null){
              instalmentPlanIdSet.add(bcgogoReceivableOrderRecordRelation.getInstalmentPlanId());
            }
          }
        }
      }
      if(!ArrayUtils.isEmpty(condition.getPaymentTypes()) && Arrays.asList(condition.getPaymentTypes()).contains(PaymentType.HARDWARE.toString())){
        List<BcgogoReceivableOrderItem> bcgogoReceivableOrderItemList = writer.getBcgogoReceivableOrderItemByOrderId(orderIdList.toArray(new Long[orderIdList.size()]));
        if(CollectionUtils.isNotEmpty(bcgogoReceivableOrderItemList)){
          BcgogoReceivableOrderItemDTO bcgogoReceivableOrderItemDTO = null;
          List<BcgogoReceivableOrderItemDTO> bcgogoReceivableOrderItemDTOList = null;
          for(BcgogoReceivableOrderItem bcgogoReceivableOrderItem : bcgogoReceivableOrderItemList){
            bcgogoReceivableOrderItemDTO = bcgogoReceivableOrderItem.toDTO();
            bcgogoReceivableOrderItemDTOList = bcgogoReceivableOrderItemDTOMap.get(bcgogoReceivableOrderItemDTO.getOrderId());
            if(bcgogoReceivableOrderItemDTOList==null){
              bcgogoReceivableOrderItemDTOList = new ArrayList<BcgogoReceivableOrderItemDTO>();
            }

            if(StringUtils.isNotBlank(bcgogoReceivableOrderItemDTO.getImagePath())){
              bcgogoReceivableOrderItemDTO.setImageUrl(ImageUtils.generateUpYunImagePath(bcgogoReceivableOrderItemDTO.getImagePath(), ImageScene.PRODUCT_LIST_IMAGE_SMALL.getImageVersion()));
            }else{
              bcgogoReceivableOrderItemDTO.setImageUrl(ImageUtils.generateNotFindImageUrl(ImageScene.PRODUCT_LIST_IMAGE_SMALL.getImageVersion()));
            }
            bcgogoReceivableOrderItemDTOList.add(bcgogoReceivableOrderItemDTO);
            bcgogoReceivableOrderItemDTOMap.put(bcgogoReceivableOrderItemDTO.getOrderId(),bcgogoReceivableOrderItemDTOList);
          }
        }
      }else if(!ArrayUtils.isEmpty(condition.getPaymentTypes()) && Arrays.asList(condition.getPaymentTypes()).contains(PaymentType.SOFTWARE.toString())){
        if (CollectionUtil.isNotEmpty(instalmentPlanIdSet)) {
          instalmentPlanDetailMap = this.getInstalmentPlanDetailMap(instalmentPlanIdSet.toArray(new Long[instalmentPlanIdSet.size()]));
        }
      }

      Map<Long,ShopVersionDTO> shopVersionDTOMap = ServiceManager.getService(IShopVersionService.class).getAllShopVersionMap();
      BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO = null;
      ShopVersionDTO shopVersionDTO = null;
      for (BcgogoReceivableOrder bcgogoReceivableOrder : bcgogoReceivableOrderList) {
        bcgogoReceivableOrderDTO = bcgogoReceivableOrder.toDTO();
        ShopDTO shopDTO = shopDTOMap.get(bcgogoReceivableOrderDTO.getShopId());
        if (shopDTO != null){
          bcgogoReceivableOrderDTO.setShopName(shopDTO.getName());
          bcgogoReceivableOrderDTO.setShopMobile(shopDTO.getMobile());
          bcgogoReceivableOrderDTO.setShopOwner(shopDTO.getOwner());
          bcgogoReceivableOrderDTO.setFollowId(shopDTO.getFollowId());
          bcgogoReceivableOrderDTO.setFollowName(shopDTO.getFollowName());
          bcgogoReceivableOrderDTO.setBargainPrice(shopDTO.getBargainPrice());//软件用
          bcgogoReceivableOrderDTO.setBargainStatus(shopDTO.getBargainStatus());//软件用
          shopVersionDTO = shopVersionDTOMap.get(shopDTO.getShopVersionId());
          if(shopVersionDTO!=null){
            bcgogoReceivableOrderDTO.setShopVersion(shopVersionDTO.getValue());
            if(PaymentType.SOFTWARE.equals(bcgogoReceivableOrderDTO.getPaymentType()) && BargainStatus.AUDIT_PASS.equals(bcgogoReceivableOrderDTO.getBargainStatus())){
              bcgogoReceivableOrderDTO.setOldTotalAmount(shopBargainRecordDTOMap.get(shopDTO.getId()).getOriginalPrice());
            }
          }
        }
        bcgogoReceivableOrderDTO.setBcgogoReceivableOrderToBePaidRecordDTO(bcgogoReceivableOrderToBePaidRecordDTOMap.get(bcgogoReceivableOrderDTO.getId()));
        if(PaymentType.HARDWARE.equals(bcgogoReceivableOrderDTO.getPaymentType())){
          bcgogoReceivableOrderDTO.setBcgogoReceivableOrderItemDTOList(bcgogoReceivableOrderItemDTOMap.get(bcgogoReceivableOrderDTO.getId()));
        }else{
          if(bcgogoReceivableOrderDTO.getInstalmentPlanId()!=null){
            bcgogoReceivableOrderDTO.setInstalmentPlanDTO(instalmentPlanDetailMap.get(bcgogoReceivableOrderDTO.getInstalmentPlanId()));
          }
          bcgogoReceivableOrderDTO.generateInstallmentInfo();
        }
        bcgogoReceivableOrderDTOList.add(bcgogoReceivableOrderDTO);
      }
    }
    result.setData(bcgogoReceivableOrderDTOList);
    return result;
  }

  @Override
  public List<BcgogoReceivableOrderDTO> searchBcgogoReceivableOrderDTO(Long shopVersionId,BcgogoReceivableSearchCondition condition) throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    IBcgogoProductService bcgogoProductService = ServiceManager.getService(IBcgogoProductService.class);
    List<BcgogoProductDTO> bcgogoProductDTOList = bcgogoProductService.getBcgogoProductDTOByPaymentType(PaymentType.HARDWARE,true);
    Map<Long,BcgogoProductDTO> bcgogoProductDTOMap = new HashMap<Long, BcgogoProductDTO>();
    if(CollectionUtils.isNotEmpty(bcgogoProductDTOList)){
      for(BcgogoProductDTO bcgogoProductDTO : bcgogoProductDTOList){
        bcgogoProductDTOMap.put(bcgogoProductDTO.getId(),bcgogoProductDTO);
      }
    }
    List<BcgogoReceivableOrder> bcgogoReceivableOrderList = writer.searchBcgogoReceivableOrder(condition);
    List<BcgogoReceivableOrderDTO> bcgogoReceivableOrderDTOList = new ArrayList<BcgogoReceivableOrderDTO>();
    if(CollectionUtils.isNotEmpty(bcgogoReceivableOrderList)){
      List<Long> orderIdList = new ArrayList<Long>();
      for (BcgogoReceivableOrder bcgogoReceivableOrder : bcgogoReceivableOrderList) {
        orderIdList.add(bcgogoReceivableOrder.getId());
      }
      Map<Long,List<BcgogoReceivableOrderItemDTO>> bcgogoReceivableOrderItemDTOMap = new HashMap<Long, List<BcgogoReceivableOrderItemDTO>>();
      Map<Long,BcgogoReceivableRecordDTO> bcgogoReceivableOrderToBePaidRecordDTOMap = new HashMap<Long, BcgogoReceivableRecordDTO>();
      Map<Long,List<BcgogoReceivableRecordDTO>> bcgogoReceivableOrderPaidRecordDTOListMap = new HashMap<Long, List<BcgogoReceivableRecordDTO>>();
      Map<Long, InstalmentPlanDTO>  instalmentPlanDetailMap = new HashMap<Long, InstalmentPlanDTO>();
      List<Object[]> objectsList = writer.getBcgogoReceivableOrderRecordAndRelationByOrderId(orderIdList.toArray(new Long[orderIdList.size()]));
      Set<Long> instalmentPlanIdSet = new HashSet<Long>();
      if(CollectionUtils.isNotEmpty(objectsList)){
        BcgogoReceivableOrderRecordRelation bcgogoReceivableOrderRecordRelation = null;
        BcgogoReceivableRecord bcgogoReceivableRecord = null;
        List<BcgogoReceivableRecordDTO> bcgogoReceivablePaidRecordDTOList = null;
        for(Object[] objects : objectsList){
          if(objects!=null && objects.length==2 && objects[0]!=null && objects[1]!=null){
            bcgogoReceivableOrderRecordRelation = (BcgogoReceivableOrderRecordRelation)objects[0];
            bcgogoReceivableRecord = (BcgogoReceivableRecord)objects[1];
            BcgogoReceivableRecordDTO bcgogoReceivableRecordDTO = bcgogoReceivableRecord.toDTO();
            bcgogoReceivableRecordDTO.setBcgogoReceivableOrderRecordRelationDTO(bcgogoReceivableOrderRecordRelation.toDTO());
            if(BcgogoReceivableStatus.TO_BE_PAID.toString().equals(bcgogoReceivableRecordDTO.getStatus())){
              bcgogoReceivableOrderToBePaidRecordDTOMap.put(bcgogoReceivableOrderRecordRelation.getBcgogoReceivableOrderId(),bcgogoReceivableRecordDTO);
            }else{
              bcgogoReceivablePaidRecordDTOList = bcgogoReceivableOrderPaidRecordDTOListMap.get(bcgogoReceivableOrderRecordRelation.getBcgogoReceivableOrderId());
              if(bcgogoReceivablePaidRecordDTOList==null){
                bcgogoReceivablePaidRecordDTOList = new ArrayList<BcgogoReceivableRecordDTO>();
              }
              bcgogoReceivablePaidRecordDTOList.add(bcgogoReceivableRecordDTO);
              bcgogoReceivableOrderPaidRecordDTOListMap.put(bcgogoReceivableOrderRecordRelation.getBcgogoReceivableOrderId(),bcgogoReceivablePaidRecordDTOList);
            }
            if(ReceivableMethod.INSTALLMENT.equals(bcgogoReceivableOrderRecordRelation.getReceivableMethod()) &&  bcgogoReceivableOrderRecordRelation.getInstalmentPlanId()!=null){
              instalmentPlanIdSet.add(bcgogoReceivableOrderRecordRelation.getInstalmentPlanId());
            }
          }
        }
      }
      if (CollectionUtil.isNotEmpty(instalmentPlanIdSet)) {
        instalmentPlanDetailMap = this.getInstalmentPlanDetailMap(instalmentPlanIdSet.toArray(new Long[instalmentPlanIdSet.size()]));
      }

      List<BcgogoReceivableOrderItem> bcgogoReceivableOrderItemList = writer.getBcgogoReceivableOrderItemByOrderId(orderIdList.toArray(new Long[orderIdList.size()]));
      if(CollectionUtils.isNotEmpty(bcgogoReceivableOrderItemList)){
        BcgogoReceivableOrderItemDTO bcgogoReceivableOrderItemDTO = null;
        List<BcgogoReceivableOrderItemDTO> bcgogoReceivableOrderItemDTOList = null;
        for(BcgogoReceivableOrderItem bcgogoReceivableOrderItem : bcgogoReceivableOrderItemList){
          bcgogoReceivableOrderItemDTO = bcgogoReceivableOrderItem.toDTO();
          bcgogoReceivableOrderItemDTOList = bcgogoReceivableOrderItemDTOMap.get(bcgogoReceivableOrderItemDTO.getOrderId());
          if(bcgogoReceivableOrderItemDTOList==null){
            bcgogoReceivableOrderItemDTOList = new ArrayList<BcgogoReceivableOrderItemDTO>();
          }

          if(StringUtils.isNotBlank(bcgogoReceivableOrderItemDTO.getImagePath())){
            bcgogoReceivableOrderItemDTO.setImageUrl(ImageUtils.generateUpYunImagePath(bcgogoReceivableOrderItemDTO.getImagePath(), ImageScene.PRODUCT_LIST_IMAGE_SMALL.getImageVersion()));
          }else{
            bcgogoReceivableOrderItemDTO.setImageUrl(ImageUtils.generateNotFindImageUrl(ImageScene.PRODUCT_LIST_IMAGE_SMALL.getImageVersion()));
          }
          BcgogoProductDTO bcgogoProductDTO = bcgogoProductDTOMap.get(bcgogoReceivableOrderItemDTO.getProductId());
          if(bcgogoProductDTO!=null){
            bcgogoReceivableOrderItemDTO.setCanShow(bcgogoProductDTO.getShowToShopVersions().indexOf(shopVersionId.toString())>-1);
          }
          bcgogoReceivableOrderItemDTOList.add(bcgogoReceivableOrderItemDTO);
          bcgogoReceivableOrderItemDTOMap.put(bcgogoReceivableOrderItemDTO.getOrderId(),bcgogoReceivableOrderItemDTOList);
        }
      }

      BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO = null;
      for (BcgogoReceivableOrder bcgogoReceivableOrder : bcgogoReceivableOrderList) {
        bcgogoReceivableOrderDTO = bcgogoReceivableOrder.toDTO();
        bcgogoReceivableOrderDTO.setBcgogoReceivableOrderItemDTOList(bcgogoReceivableOrderItemDTOMap.get(bcgogoReceivableOrderDTO.getId()));
        bcgogoReceivableOrderDTO.setBcgogoReceivableOrderToBePaidRecordDTO(bcgogoReceivableOrderToBePaidRecordDTOMap.get(bcgogoReceivableOrderDTO.getId()));
        if(bcgogoReceivableOrderDTO.getInstalmentPlanId()!=null){
          bcgogoReceivableOrderDTO.setInstalmentPlanDTO(instalmentPlanDetailMap.get(bcgogoReceivableOrderDTO.getInstalmentPlanId()));
        }
        bcgogoReceivableOrderDTO.setBcgogoReceivableOrderPaidRecordDTOList(bcgogoReceivableOrderPaidRecordDTOListMap.get(bcgogoReceivableOrderDTO.getId()));
        bcgogoReceivableOrderDTO.generateInstallmentInfo();
        bcgogoReceivableOrderDTOList.add(bcgogoReceivableOrderDTO);
      }
    }
    return bcgogoReceivableOrderDTOList;
  }

  @Override
  public int countBcgogoReceivableOrderDTO(BcgogoReceivableSearchCondition condition) throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countBcgogoReceivableOrder(condition);
  }
  @Override
  public  Map<String,Integer> statBcgogoReceivableOrderByStatus(BcgogoReceivableSearchCondition condition) throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    List<Object[]> bcgogoReceivableOrderStatList = writer.statBcgogoReceivableOrderByStatus(condition);
    Map<String,Integer> statMap = new HashMap<String, Integer>();
    if(CollectionUtils.isNotEmpty(bcgogoReceivableOrderStatList)){
      for(Object[] objects:bcgogoReceivableOrderStatList){
        PaymentStatus status = (PaymentStatus)objects[0];
        Integer count = Integer.valueOf(objects[1].toString());
        statMap.put(status.toString(),count);
      }
      for(PaymentStatus paymentStatus:PaymentStatus.values()){
        if(!statMap.containsKey(paymentStatus.toString())){
          statMap.put(paymentStatus.toString(),0);
        }
      }
    }
    return statMap;
  }

  @Override
  public Result statBcgogoReceivableOrderByStatusResult(BcgogoReceivableSearchCondition condition) throws BcgogoException {
    Result result = new Result(true);

    List<Long> shopIds = new ArrayList<Long>();
    if (StringUtils.isNotBlank(condition.getShopName()) || StringUtils.isNotBlank(condition.getFollowName()) || !ArrayUtils.isEmpty(condition.getBargainStatuses()) || !ArrayUtils.isEmpty(condition.getShopVersionIds())) {
      ShopSearchCondition shopSearchCondition = new ShopSearchCondition();
      shopSearchCondition.setName(condition.getShopName());
      shopSearchCondition.setFollowName(condition.getFollowName());
      shopSearchCondition.setShopVersionIds(condition.getShopVersionIds());
      shopSearchCondition.setBargainStatuses(condition.getBargainStatuses());
      shopIds = ServiceManager.getService(IShopService.class).getShopIdByShopCondition(shopSearchCondition);
      if (CollectionUtil.isEmpty(shopIds)) {
        result.setTotal(0);
        return result;
      }
      condition.setShopIds(shopIds);
    }
    TxnWriter writer = txnDaoManager.getWriter();

    List<Object[]> bcgogoReceivableOrderStatList = writer.statBcgogoReceivableOrderByStatus(condition);
    Map<String,String> statMap = new HashMap<String, String>();
    if(CollectionUtils.isNotEmpty(bcgogoReceivableOrderStatList)){
      if(!ArrayUtils.isEmpty(condition.getPaymentTypes()) && Arrays.asList(condition.getPaymentTypes()).contains(PaymentType.SOFTWARE.toString())){
        Double waitPayAmount = 0d;Integer waitPayCount=0;
        for(Object[] objects:bcgogoReceivableOrderStatList){
          PaymentStatus status = (PaymentStatus)objects[0];
          if(PaymentStatus.NON_PAYMENT.equals(status) || PaymentStatus.PARTIAL_PAYMENT.equals(status)){
            waitPayCount+=NumberUtil.intValue(objects[1].toString());
            waitPayAmount+=NumberUtil.doubleVal(objects[2]);
          }else {
            String count = objects[1].toString();
            String sumAmount = String.valueOf(NumberUtil.toReserve(NumberUtil.doubleVal(objects[2]), NumberUtil.MONEY_PRECISION));
            statMap.put(status.toString(),"(<span style=\"color: #0000FF;\">"+count+"</span>条 <span style=\"color: #008000;\">"+sumAmount+"</span>元)");
          }
        }
        statMap.put("WAIT_PAYMENT","(<span style=\"color: #0000FF;\">"+waitPayCount.toString()+"</span>条 <span style=\"color: #008000;\">"+String.valueOf(NumberUtil.toReserve(waitPayAmount, NumberUtil.MONEY_PRECISION))+"</span>元)");
      }else{
        for(Object[] objects:bcgogoReceivableOrderStatList){
          PaymentStatus status = (PaymentStatus)objects[0];
          String count = objects[1].toString();
          String sumAmount = String.valueOf(NumberUtil.toReserve(NumberUtil.doubleVal(objects[2]), NumberUtil.MONEY_PRECISION));
          statMap.put(status.toString(),"(<span style=\"color: #0000FF;\">"+count+"</span>条 <span style=\"color: #008000;\">"+sumAmount+"</span>元)");
        }
      }

    }
    result.setData(statMap);
    return result;
  }

  @Override
  public Result statBcgogoReceivableOrderRecordByStatusResult(BcgogoReceivableSearchCondition condition) throws BcgogoException {
    Result result = new Result(true);

    List<Long> shopIds = new ArrayList<Long>();
    if (StringUtils.isNotBlank(condition.getShopName()) || StringUtils.isNotBlank(condition.getFollowName()) || !ArrayUtils.isEmpty(condition.getBargainStatuses()) || !ArrayUtils.isEmpty(condition.getShopVersionIds())) {
      ShopSearchCondition shopSearchCondition = new ShopSearchCondition();
      shopSearchCondition.setName(condition.getShopName());
      shopSearchCondition.setFollowName(condition.getFollowName());
      shopSearchCondition.setShopVersionIds(condition.getShopVersionIds());
      shopSearchCondition.setBargainStatuses(condition.getBargainStatuses());
      shopIds = ServiceManager.getService(IShopService.class).getShopIdByShopCondition(shopSearchCondition);
      if (CollectionUtil.isEmpty(shopIds)) {
        result.setTotal(0);
        return result;
      }
      condition.setShopIds(shopIds);
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<Object[]> bcgogoReceivableOrderRecordStatList = writer.statBcgogoReceivableOrderRecordByStatus(condition);
    Map<BcgogoReceivableStatus,Map<PaymentMethod,Pair<Integer,Double>>> tempMap = new HashMap<BcgogoReceivableStatus,Map<PaymentMethod,Pair<Integer,Double>>>();
    Map<String,String> statMap = new HashMap<String, String>();
    if(CollectionUtils.isNotEmpty(bcgogoReceivableOrderRecordStatList)){
      Map<PaymentMethod,Pair<Integer,Double>> subTempMap = null;
      for(Object[] objects:bcgogoReceivableOrderRecordStatList){
        BcgogoReceivableStatus status = BcgogoReceivableStatus.valueOf(objects[0].toString());
        PaymentMethod paymentMethod = PaymentMethod.valueOf(objects[1].toString());
        Integer num = Integer.valueOf(objects[2].toString());
        Double amountSum = NumberUtil.doubleVal(objects[3]);
        subTempMap = tempMap.get(status);
        if(subTempMap==null){
          subTempMap = new HashMap<PaymentMethod, Pair<Integer, Double>>();
        }
        subTempMap.put(paymentMethod,new Pair<Integer, Double>(num,amountSum));
        tempMap.put(status,subTempMap);
      }
      subTempMap = tempMap.get(BcgogoReceivableStatus.PENDING_REVIEW);
      if(MapUtils.isNotEmpty(subTempMap) && (subTempMap.get(PaymentMethod.DOOR_CHARGE)!=null || subTempMap.get(PaymentMethod.ONLINE_PAYMENT)!=null)){
        Integer total = 0;Double totalAmount = 0d;Double onlinePaymentAmount = 0d;Double doorChargeAmount = 0d;
        if(subTempMap.get(PaymentMethod.DOOR_CHARGE)!=null){
          total += NumberUtil.intValue(subTempMap.get(PaymentMethod.DOOR_CHARGE).getKey());
          totalAmount += NumberUtil.doubleVal(subTempMap.get(PaymentMethod.DOOR_CHARGE).getValue());
          doorChargeAmount = NumberUtil.toReserve(subTempMap.get(PaymentMethod.DOOR_CHARGE).getValue(), NumberUtil.MONEY_PRECISION);
        }
        if(subTempMap.get(PaymentMethod.ONLINE_PAYMENT)!=null){
          total += NumberUtil.intValue(subTempMap.get(PaymentMethod.ONLINE_PAYMENT).getKey());
          totalAmount += NumberUtil.doubleVal(subTempMap.get(PaymentMethod.ONLINE_PAYMENT).getValue());
          onlinePaymentAmount = NumberUtil.toReserve(subTempMap.get(PaymentMethod.ONLINE_PAYMENT).getValue(),NumberUtil.MONEY_PRECISION);
        }
        statMap.put(BcgogoReceivableStatus.PENDING_REVIEW.toString(),"<span style=\"color: #0000FF;\">"+total+"</span>条 <span style=\"color: #0000FF;\">"+NumberUtil.toReserve(totalAmount,NumberUtil.MONEY_PRECISION)+"</span>元(银联：<span style=\"color: #FF6600;\">"+onlinePaymentAmount+"</span>元；现金：<span style=\"color: #FF6600;\">"+doorChargeAmount+"</span>元)");
      }
      subTempMap = tempMap.get(BcgogoReceivableStatus.HAS_BEEN_PAID);
      if(MapUtils.isNotEmpty(subTempMap) &&  (subTempMap.get(PaymentMethod.DOOR_CHARGE)!=null || subTempMap.get(PaymentMethod.ONLINE_PAYMENT)!=null)){
        Integer total = 0;Double totalAmount = 0d;Double onlinePaymentAmount = 0d;Double doorChargeAmount = 0d;
        if(subTempMap.get(PaymentMethod.DOOR_CHARGE)!=null){
          total += NumberUtil.intValue(subTempMap.get(PaymentMethod.DOOR_CHARGE).getKey());
          totalAmount += NumberUtil.doubleVal(subTempMap.get(PaymentMethod.DOOR_CHARGE).getValue());
          doorChargeAmount = NumberUtil.toReserve(subTempMap.get(PaymentMethod.DOOR_CHARGE).getValue(),NumberUtil.MONEY_PRECISION);
        }
        if(subTempMap.get(PaymentMethod.ONLINE_PAYMENT)!=null){
          total += NumberUtil.intValue(subTempMap.get(PaymentMethod.ONLINE_PAYMENT).getKey());
          totalAmount += NumberUtil.doubleVal(subTempMap.get(PaymentMethod.ONLINE_PAYMENT).getValue());
          onlinePaymentAmount = NumberUtil.toReserve(subTempMap.get(PaymentMethod.ONLINE_PAYMENT).getValue(),NumberUtil.MONEY_PRECISION);
        }
        statMap.put(BcgogoReceivableStatus.HAS_BEEN_PAID.toString(),"<span style=\"color: #0000FF;\">"+total+"</span>条 <span style=\"color: #0000FF;\">"+NumberUtil.toReserve(totalAmount,NumberUtil.MONEY_PRECISION)+"</span>元(银联：<span style=\"color: #FF6600;\">"+onlinePaymentAmount+"</span>元；现金：<span style=\"color: #FF6600;\">"+doorChargeAmount+"</span>元)");
      }
    }
    result.setData(statMap);
    return result;
  }

  @Override
  public Result searchBcgogoReceivableResult(BcgogoReceivableSearchCondition condition, boolean isPage) throws BcgogoException {
    Result result = new Result(true);
//    if (condition.getStatus() == null) throw new BcgogoException("bcgogo receivable status is null!");

    List<Long> shopIds = new ArrayList<Long>();
    if (StringUtils.isNotBlank(condition.getShopName()) || StringUtils.isNotBlank(condition.getFollowName())) {
      ShopSearchCondition shopSearchCondition = new ShopSearchCondition();
      shopSearchCondition.setName(condition.getShopName());
      shopSearchCondition.setFollowName(condition.getFollowName());
      shopIds = ServiceManager.getService(IShopService.class).getShopIdByShopCondition(shopSearchCondition);
      if (CollectionUtil.isEmpty(shopIds)) {
        result.setTotal(0);
        return result;
      }
      condition.setShopIds(shopIds);
    }
    TxnWriter writer = txnDaoManager.getWriter();
    result.setTotal(writer.countBcgogoPayment(condition));
    if (!isPage) {
      condition.setStart(0);
      condition.setLimit(result.getTotal());
    }
    List<BcgogoReceivableRecordDTO> recordDTOList = writer.searchBcgogoReceivableResult(condition);
    Set<Long> userIds = new HashSet<Long>();
    for (BcgogoReceivableRecordDTO recordDTO : recordDTOList) {
      userIds.add(recordDTO.getAuditorId());
      userIds.add(recordDTO.getOperatorId());
      userIds.add(recordDTO.getPayeeId());
      userIds.add(recordDTO.getSubmitterId());
    }
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Map<Long, UserDTO> userDTOMap = userCacheService.getUserMap(userIds);
    UserDTO userDTO;
    Set<Long> instalmentPlanIdSet = new HashSet<Long>();
    shopIds.clear();
    for (BcgogoReceivableRecordDTO recordDTO : recordDTOList) {
      if (recordDTO.getShopId() != null) shopIds.add(recordDTO.getShopId());
      if (recordDTO.getInstalmentPlanId() != null) instalmentPlanIdSet.add(recordDTO.getInstalmentPlanId());
      userDTO = userDTOMap.get(recordDTO.getSubmitterId());
      if (userDTO != null) recordDTO.setSubmitterName(userDTO.getName());

      userDTO = userDTOMap.get(recordDTO.getOperatorId());
      if (userDTO != null) recordDTO.setOperatorName(userDTO.getName());

      userDTO = userDTOMap.get(recordDTO.getPayeeId());
      if (userDTO != null) recordDTO.setPayeeName(userDTO.getName());

      userDTO = userDTOMap.get(recordDTO.getAuditorId());
      if (userDTO != null) recordDTO.setAuditorName(userDTO.getName());

      if (recordDTO.getOrderPaymentType().equals(PaymentType.HARDWARE.name())) {
        recordDTO.setTime(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, recordDTO.getOrderStartTime()) + " 录入");
      } else {
        if(BcgogoReceivableStatus.TO_BE_PAID.toString().equals(recordDTO.getStatus())){
          if (!recordDTO.getReceivableMethod().equals(ReceivableMethod.UNCONSTRAINED.name()))
            recordDTO.setTime(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, recordDTO.getCurrentInstalmentPlanEndTime()) + "<br>支付截止");
        }
        if(BcgogoReceivableStatus.PENDING_REVIEW.toString().equals(recordDTO.getStatus())){
          if(PaymentMethod.ONLINE_PAYMENT.toString().equals(recordDTO.getPaymentMethod())){
            recordDTO.setTime(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, recordDTO.getRecordPaymentTime()) + "<br>在线支付");
          }else if(PaymentMethod.DOOR_CHARGE.toString().equals(recordDTO.getPaymentMethod())){
            recordDTO.setTime(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, recordDTO.getRecordPaymentTime()) +"<br>"+StringUtils.defaultIfEmpty(recordDTO.getPayeeName(),"")+ " 上门收取");
          }else{
            recordDTO.setTime(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, recordDTO.getRecordPaymentTime()));
          }
        }
        if(BcgogoReceivableStatus.HAS_BEEN_PAID.toString().equals(recordDTO.getStatus())){
          recordDTO.setTime(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, recordDTO.getAuditTime()) +"<br>"+StringUtils.defaultIfEmpty(recordDTO.getAuditorName(),"")+ " 审核入账");
        }
      }
    }
    ShopDTO shopDTO;ShopVersionDTO shopVersionDTO;
    InstalmentPlanDTO instalmentPlanDTO;
    Map<Long, ShopDTO> shopDTOMap = null;
    Map<Long, InstalmentPlanDTO> instalmentPlanDetailMap = null;
    if (CollectionUtil.isNotEmpty(shopIds)) {
      shopDTOMap = ServiceManager.getService(IShopService.class).getShopByShopIds(shopIds.toArray(new Long[shopIds.size()]));
    }
    if (CollectionUtil.isNotEmpty(instalmentPlanIdSet)) {
      instalmentPlanDetailMap = this.getInstalmentPlanDetailMap(instalmentPlanIdSet.toArray(new Long[instalmentPlanIdSet.size()]));
    }
    Map<Long,ShopVersionDTO> shopVersionDTOMap = ServiceManager.getService(IShopVersionService.class).getAllShopVersionMap();
    double totalReceivable = 0;
    for (BcgogoReceivableRecordDTO recordDTO : recordDTOList) {
      if (shopDTOMap != null) {
        shopDTO = shopDTOMap.get(recordDTO.getShopId());
        if (shopDTO != null){
          recordDTO.setShopName(shopDTO.getName());
          if(!PaymentType.SMS_RECHARGE.toString().equals(recordDTO.getOrderPaymentType())) {
            recordDTO.setFollowId(shopDTO.getFollowId());
            recordDTO.setFollowName(shopDTO.getFollowName());
          }
          recordDTO.setShopReviewDate(shopDTO.getReviewDate());
          shopVersionDTO = shopVersionDTOMap.get(shopDTO.getShopVersionId());
          if(shopVersionDTO!=null){
            recordDTO.setShopVersion(shopVersionDTO.getValue());
          }
        }

      }
      if (instalmentPlanDetailMap != null) {
        if (recordDTO.getInstalmentPlanId() != null) {
          instalmentPlanDTO = instalmentPlanDetailMap.get(recordDTO.getInstalmentPlanId());
          if (instalmentPlanDTO != null) {
            if (instalmentPlanDTO.getCurrentItem() != null) {
              recordDTO.setPeriodNumber(instalmentPlanDTO.getCurrentItem().getPeriodNumber());
            }
            recordDTO.setPeriods(instalmentPlanDTO.getPeriods());
          }
        }
      }

      if (condition.getStatus() == BcgogoReceivableStatus.TO_BE_PAID) {
        totalReceivable += NumberUtil.doubleVal(recordDTO.getOrderReceivableAmount());
      } else if (condition.getStatus() == BcgogoReceivableStatus.HAS_BEEN_PAID) {
        totalReceivable += NumberUtil.doubleVal(recordDTO.getRecordPaidAmount());
      }


    }
    result.setTitle(String.valueOf(NumberUtil.toReserve(totalReceivable, NumberUtil.MONEY_PRECISION)));
    result.setData(recordDTOList);
    return result;
  }

  public Map<Long, InstalmentPlanItemDTO> getInstalmentPlanItemDTOMap(Long... instalmentPlanItemIds) {
    Map<Long, InstalmentPlanItemDTO> instalmentPlanItemDTOMap = new HashMap<Long, InstalmentPlanItemDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<InstalmentPlanItem> itemsList = writer.getInstalmentPlanItemsByIds(instalmentPlanItemIds);
    for (InstalmentPlanItem item : itemsList) {
      instalmentPlanItemDTOMap.put(item.getId(), item.toDTO());
    }
    return instalmentPlanItemDTOMap;
  }

  public Map<Long, InstalmentPlanDTO> getInstalmentPlanDetailMap(Long... instalmentPlanIds) {
    Map<Long, InstalmentPlanDTO> instalmentPlanDetailMap = new HashMap<Long, InstalmentPlanDTO>();
    Map<Long, InstalmentPlanItemDTO> itemDTOMap = new HashMap<Long, InstalmentPlanItemDTO>();
    Map<Long, List<InstalmentPlanItemDTO>> planItemDTOListMap = new HashMap<Long, List<InstalmentPlanItemDTO>>();
    TxnWriter writer = txnDaoManager.getWriter();
    InstalmentPlanDTO instalmentPlanDTO;
    InstalmentPlanItemDTO instalmentPlanItemDTO;
    List<InstalmentPlan> planList = writer.getInstalmentPlanByIds(instalmentPlanIds);
    List<InstalmentPlanItem> itemsList = writer.getInstalmentPlanItemsByInstalmentPlanIds(instalmentPlanIds);
    List<InstalmentPlanItemDTO> instalmentPlanItemDTOList = null;
    for (InstalmentPlanItem item : itemsList) {
      itemDTOMap.put(item.getId(), item.toDTO());
      instalmentPlanItemDTOList = planItemDTOListMap.get(item.getInstalmentPlanId());
      if(instalmentPlanItemDTOList==null) instalmentPlanItemDTOList= new ArrayList<InstalmentPlanItemDTO>();
      instalmentPlanItemDTOList.add(item.toDTO());
      planItemDTOListMap.put(item.getInstalmentPlanId(),instalmentPlanItemDTOList);
    }
    for (InstalmentPlan plan : planList) {
      instalmentPlanDTO = plan.toDTO();
      instalmentPlanItemDTO = itemDTOMap.get(plan.getCurrentItemId());
      instalmentPlanItemDTO.setNextItem(itemDTOMap.get(instalmentPlanItemDTO.getNextItemId()));
      instalmentPlanDTO.setCurrentItem(instalmentPlanItemDTO);
      instalmentPlanDTO.setInstalmentPlanItemDTOList(planItemDTOListMap.get(plan.getId()));
      instalmentPlanDetailMap.put(plan.getId(), instalmentPlanDTO);
    }
    return instalmentPlanDetailMap;
  }

  @Override
  public void saveBcgogoReceivableOrderDTO(Long bcgogoUserId, BcgogoReceivableOrderDTO dto) throws Exception {
    if (dto.getShopId() == null) throw new BcgogoException("shop id is null!");

    IBcgogoAccountService bcgogoAccountService = ServiceManager.getService(IBcgogoAccountService.class);
    IBcgogoProductService bcgogoProductService = ServiceManager.getService(IBcgogoProductService.class);

    List<BcgogoReceivableOrderItemDTO> bcgogoReceivableOrderItemDTOList = dto.getBcgogoReceivableOrderItemDTOList();
    Iterator<BcgogoReceivableOrderItemDTO> iterator = bcgogoReceivableOrderItemDTOList.iterator();
    BcgogoReceivableOrderItemDTO bcgogoReceivableOrderItemDTO = null;
    Double orderTotalAmount =0d;
    while (iterator.hasNext()){
      bcgogoReceivableOrderItemDTO = iterator.next();
      if(NumberUtil.doubleVal(bcgogoReceivableOrderItemDTO.getAmount())>0){
        bcgogoReceivableOrderItemDTO.setBcgogoProductDTO(bcgogoProductService.getBcgogoProductDTOById(bcgogoReceivableOrderItemDTO.getProductId()));
        bcgogoReceivableOrderItemDTO.setBcgogoProductPropertyDTO(bcgogoProductService.getBcgogoProductPropertyDTOById(bcgogoReceivableOrderItemDTO.getProductPropertyId()));
        bcgogoReceivableOrderItemDTO.setTotal(NumberUtil.round(bcgogoReceivableOrderItemDTO.getAmount()*bcgogoReceivableOrderItemDTO.getPrice(),NumberUtil.MONEY_PRECISION));
        orderTotalAmount+=bcgogoReceivableOrderItemDTO.getTotal();
      }else{
        iterator.remove();
      }
    }
    dto.setTotalAmount(orderTotalAmount);
    if(CollectionUtils.isEmpty(bcgogoReceivableOrderItemDTOList)) return;

    BcgogoReceivableOrder order = new BcgogoReceivableOrder(dto);
    if(StringUtils.isEmpty(order.getReceiptNo())){
      order.setReceiptNo(ServiceManager.getService(ITxnService.class).getBcgogoOrderReceiptNo(ShopConstant.BC_ADMIN_SHOP_ID, OrderTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER,order.getCreatedTime()));
    }

    BcgogoReceivableOrderRecordRelation relation = new BcgogoReceivableOrderRecordRelation(dto);
    BcgogoReceivableRecord record = new BcgogoReceivableRecord(bcgogoUserId,dto);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(order);
      dto.setId(order.getId());
      dto.setPaymentType(order.getPaymentType());
      for(BcgogoReceivableOrderItemDTO orderItemDTO:bcgogoReceivableOrderItemDTOList){
        orderItemDTO.setOrderId(order.getId());
        BcgogoReceivableOrderItem bcgogoReceivableOrderItem = new BcgogoReceivableOrderItem();
        bcgogoReceivableOrderItem.fromDTO(orderItemDTO);
        writer.save(bcgogoReceivableOrderItem);
      }
      writer.save(record);
      //软硬件账单
      HardwareSoftwareAccountDTO accountDTO = bcgogoAccountService.getHardwareSoftwareAccountByShopId(order.getShopId());
      accountDTO.createHardwarePayable(order.getTotalAmount());
      bcgogoAccountService.updateHardwareSoftwareAccount(accountDTO, writer);

      relation.setBcgogoReceivableOrderId(order.getId());
      relation.setBcgogoReceivableRecordId(record.getId());
      writer.save(relation);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public BcgogoReceivableOrderDTO getBcgogoReceivableOrderDetail(Long shopId, Long orderId) throws Exception {
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    IShopBargainService shopBargainService = ServiceManager.getService(IShopBargainService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    BcgogoReceivableOrder bcgogoReceivableOrder = writer.getById(BcgogoReceivableOrder.class, orderId);
    if (!bcgogoReceivableOrder.getShopId().equals(shopId) && shopId!=null) throw new Exception("shopId is not matched!");

    BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO = bcgogoReceivableOrder.toDTO();
    StringBuilder addressDetail = new StringBuilder();
    if(bcgogoReceivableOrderDTO.getProvince()!=null){
      addressDetail.append( AreaCacheManager.getAreaDTOByNo(bcgogoReceivableOrderDTO.getProvince()).getName());
    }
    if(bcgogoReceivableOrderDTO.getCity()!=null){
      addressDetail.append( AreaCacheManager.getAreaDTOByNo(bcgogoReceivableOrderDTO.getCity()).getName());
    }
    if(bcgogoReceivableOrderDTO.getRegion()!=null){
      addressDetail.append( AreaCacheManager.getAreaDTOByNo(bcgogoReceivableOrderDTO.getRegion()).getName());
    }
    addressDetail.append(bcgogoReceivableOrderDTO.getAddress());
    bcgogoReceivableOrderDTO.setAddressDetail(addressDetail.toString());
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(bcgogoReceivableOrderDTO.getShopId());
    if (shopDTO != null){
      bcgogoReceivableOrderDTO.setShopName(shopDTO.getName());
      bcgogoReceivableOrderDTO.setShopMobile(shopDTO.getMobile());
      bcgogoReceivableOrderDTO.setFollowId(shopDTO.getFollowId());
      bcgogoReceivableOrderDTO.setFollowName(shopDTO.getFollowName());
      bcgogoReceivableOrderDTO.setShopOwner(shopDTO.getOwner());
      bcgogoReceivableOrderDTO.setBargainPrice(shopDTO.getBargainPrice());
      bcgogoReceivableOrderDTO.setBargainStatus(shopDTO.getBargainStatus());
      ShopVersionDTO shopVersionDTO= ServiceManager.getService(IShopVersionService.class).getShopVersionById(shopDTO.getShopVersionId());
      if(shopVersionDTO!=null){
        bcgogoReceivableOrderDTO.setShopVersion(shopVersionDTO.getValue());
        if(PaymentType.SOFTWARE.equals(bcgogoReceivableOrderDTO.getPaymentType()) && BargainStatus.AUDIT_PASS.equals(bcgogoReceivableOrderDTO.getBargainStatus())){
          Map<Long,ShopBargainRecordDTO> shopBargainRecordDTOMap = shopBargainService.getShopAuditPassBargainRecordMapByShopId(shopDTO.getId());
          bcgogoReceivableOrderDTO.setOldTotalAmount(shopBargainRecordDTOMap.get(shopDTO.getId()).getOriginalPrice());
        }
      }
    }

    List<BcgogoReceivableOrderItem> bcgogoReceivableOrderItemList = writer.getBcgogoReceivableOrderItemByOrderId(orderId);
    if (CollectionUtils.isNotEmpty(bcgogoReceivableOrderItemList)) {
      BcgogoReceivableOrderItemDTO bcgogoReceivableOrderItemDTO = null;
      List<BcgogoReceivableOrderItemDTO> bcgogoReceivableOrderItemDTOList = new ArrayList<BcgogoReceivableOrderItemDTO>();
      for (BcgogoReceivableOrderItem bcgogoReceivableOrderItem : bcgogoReceivableOrderItemList) {
        bcgogoReceivableOrderItemDTO = bcgogoReceivableOrderItem.toDTO();
        if(ChargeType.YEARLY.equals(bcgogoReceivableOrderDTO.getChargeType())){
          bcgogoReceivableOrderItemDTO.setTotal(ConfigUtils.getBcgogoSoftAnnualPrice());
          bcgogoReceivableOrderItemDTO.setPrice(ConfigUtils.getBcgogoSoftAnnualPrice());
        }
        if (StringUtils.isNotBlank(bcgogoReceivableOrderItemDTO.getImagePath())) {
          bcgogoReceivableOrderItemDTO.setImageUrl(ImageUtils.generateUpYunImagePath(bcgogoReceivableOrderItemDTO.getImagePath(), ImageScene.PRODUCT_LIST_IMAGE_SMALL.getImageVersion()));
        } else {
          bcgogoReceivableOrderItemDTO.setImageUrl(ImageUtils.generateNotFindImageUrl(ImageScene.PRODUCT_LIST_IMAGE_SMALL.getImageVersion()));
        }
        bcgogoReceivableOrderItemDTOList.add(bcgogoReceivableOrderItemDTO);
      }
      bcgogoReceivableOrderDTO.setBcgogoReceivableOrderItemDTOList(bcgogoReceivableOrderItemDTOList);
    }

    List<Object[]> objectsList = writer.getBcgogoReceivableOrderRecordAndRelationByOrderId(orderId);
    if (CollectionUtils.isNotEmpty(objectsList)) {
      BcgogoReceivableOrderRecordRelation bcgogoReceivableOrderRecordRelation = null;
      BcgogoReceivableRecord bcgogoReceivableRecord = null;
      Set<Long> userIds = new HashSet<Long>();
      List<BcgogoReceivableRecordDTO> bcgogoReceivablePaidRecordDTOList = new ArrayList<BcgogoReceivableRecordDTO>();
      for (Object[] objects : objectsList) {
        if (objects != null && objects.length == 2 && objects[0] != null && objects[1] != null) {
          bcgogoReceivableOrderRecordRelation = (BcgogoReceivableOrderRecordRelation) objects[0];
          bcgogoReceivableRecord = (BcgogoReceivableRecord) objects[1];
          BcgogoReceivableRecordDTO bcgogoReceivableRecordDTO = bcgogoReceivableRecord.toDTO();
          bcgogoReceivableRecordDTO.setBcgogoReceivableOrderRecordRelationDTO(bcgogoReceivableOrderRecordRelation.toDTO());
          if (bcgogoReceivableOrderDTO.getInstalmentPlanDTO()==null && bcgogoReceivableOrderRecordRelation.getInstalmentPlanId() != null && ReceivableMethod.INSTALLMENT.equals(bcgogoReceivableOrderRecordRelation.getReceivableMethod())) {
            bcgogoReceivableOrderDTO.setInstalmentPlanDTO(this.getInstalmentPlanDetailMap(bcgogoReceivableOrderRecordRelation.getInstalmentPlanId()).get(bcgogoReceivableOrderRecordRelation.getInstalmentPlanId()));
          }
          if (BcgogoReceivableStatus.TO_BE_PAID.toString().equals(bcgogoReceivableRecordDTO.getStatus())) {
            bcgogoReceivableOrderDTO.setBcgogoReceivableOrderToBePaidRecordDTO(bcgogoReceivableRecordDTO);
          } else {
            bcgogoReceivablePaidRecordDTOList.add(bcgogoReceivableRecordDTO);
          }
          if(bcgogoReceivableRecordDTO.getSubmitterId()!=null){
            userIds.add(bcgogoReceivableRecordDTO.getSubmitterId());
          }
          if(bcgogoReceivableRecordDTO.getOperatorId()!=null){
            userIds.add(bcgogoReceivableRecordDTO.getOperatorId());
          }
          if(bcgogoReceivableRecordDTO.getPayeeId()!=null){
            userIds.add(bcgogoReceivableRecordDTO.getPayeeId());
          }
          if(bcgogoReceivableRecordDTO.getAuditorId()!=null){
            userIds.add(bcgogoReceivableRecordDTO.getAuditorId());
          }
        }
      }
      Map<Long, UserDTO> userDTOMap = userCacheService.getUserMap(userIds);
      UserDTO userDTO = null;
      for (BcgogoReceivableRecordDTO recordDTO : bcgogoReceivablePaidRecordDTOList) {
        if(recordDTO.getSubmitterId()!=null){
          userDTO = userDTOMap.get(recordDTO.getSubmitterId());
          if (userDTO != null) recordDTO.setSubmitterName(userDTO.getName());
        }
        if(recordDTO.getOperatorId()!=null){
          userDTO = userDTOMap.get(recordDTO.getOperatorId());
          if (userDTO != null) recordDTO.setOperatorName(userDTO.getName());
        }
        if(recordDTO.getPayeeId()!=null){
          userDTO = userDTOMap.get(recordDTO.getPayeeId());
          if (userDTO != null) recordDTO.setPayeeName(userDTO.getName());
        }
        if(recordDTO.getAuditorId()!=null){
          userDTO = userDTOMap.get(recordDTO.getAuditorId());
          if (userDTO != null) recordDTO.setAuditorName(userDTO.getName());
        }
      }
      bcgogoReceivableOrderDTO.setBcgogoReceivableOrderPaidRecordDTOList(bcgogoReceivablePaidRecordDTOList);
    }
    bcgogoReceivableOrderDTO.generateInstallmentInfo();
    return bcgogoReceivableOrderDTO;
  }

  @Override
  public BcgogoReceivableOrderRecordRelationDTO getBcgogoReceivableOrderRecordRelationDTOById(Long shopId, Long id) {
    TxnWriter writer = txnDaoManager.getWriter();
    BcgogoReceivableOrderRecordRelation bcgogoReceivableOrderRecordRelation = writer.getById(BcgogoReceivableOrderRecordRelation.class,id);
    if(shopId.equals(bcgogoReceivableOrderRecordRelation.getShopId())){
      return bcgogoReceivableOrderRecordRelation.toDTO();
    }
    return null;
  }

  @Override
  public Boolean verifyAndGetBcgogoReceivableOrderDTO(BcgogoReceivableDTO dto,Long shopId,BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO) {
    if(!bcgogoReceivableOrderDTO.getShopId().equals(shopId)){
      return false;
    }
    if(NumberUtil.doubleVal(dto.getPaidAmount())>bcgogoReceivableOrderDTO.getReceivableAmount()){
      return false;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    BcgogoReceivableOrderRecordRelation bcgogoReceivableOrderRecordRelation = writer.getById(BcgogoReceivableOrderRecordRelation.class,dto.getBcgogoReceivableOrderRecordRelationId());
    BcgogoReceivableRecord bcgogoReceivableRecord = writer.getById(BcgogoReceivableRecord.class,bcgogoReceivableOrderRecordRelation.getBcgogoReceivableRecordId());
    if(NumberUtil.doubleVal(bcgogoReceivableRecord.getPaymentAmount())<=0 || !BcgogoReceivableStatus.TO_BE_PAID.equals(bcgogoReceivableRecord.getStatus())){
      return false;
    }
    return true;
  }

  @Override
  public void createSoftwareReceivable(Long shopId,Long userId,String userName,BuyChannels buyChannels) throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IBcgogoAccountService bcgogoAccountService = ServiceManager.getService(IBcgogoAccountService.class);
    IBcgogoProductService bcgogoProductService = ServiceManager.getService(IBcgogoProductService.class);
    IShopVersionService shopVersionService = ServiceManager.getService(IShopVersionService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    TxnWriter writer = txnDaoManager.getWriter();
    List<BcgogoReceivableOrderRecordRelation> receivableList = writer.getSoftwareReceivable(shopId);
    if (CollectionUtil.isNotEmpty(receivableList)) return;
    ShopVersionDTO shopVersionDTO = shopVersionService.getShopVersionById(shopDTO.getShopVersionId());
    if(ChargeType.ONE_TIME.equals(shopDTO.getChargeType())) {
      if(shopDTO.getSoftPrice() == null) {
        throw new BcgogoException("shop soft price is null");
      }
    }else{
      shopDTO.setSoftPrice(ConfigUtils.getBcgogoSoftAnnualPrice());
    }

    if (shopDTO.getTrialEndTime() == null) throw new BcgogoException("trial end time is null");

    BcgogoReceivableOrder order = new BcgogoReceivableOrder();
    order.createSoftwareOrder(shopId,(shopDTO.getBargainPrice() == null ? shopDTO.getSoftPrice() : shopDTO.getBargainPrice()), shopVersionDTO.getValue(), shopDTO.getTrialEndTime(),buyChannels,shopDTO.getChargeType());
    order.setUserId(userId);
    order.setReceiptNo(ServiceManager.getService(ITxnService.class).getBcgogoOrderReceiptNo(ShopConstant.BC_ADMIN_SHOP_ID,OrderTypes.BCGOGO_SOFTWARE_RECEIVABLE_ORDER,System.currentTimeMillis()));
    BcgogoReceivableOrderItem orderItem = new BcgogoReceivableOrderItem();
    BcgogoProductPropertyDTO bcgogoProductPropertyDTO = bcgogoProductService.getBcgogoProductPropertyDTOById(shopDTO.getShopVersionId());
    BcgogoProductDTO bcgogoProductDTO = bcgogoProductService.getBcgogoProductDTOById(bcgogoProductPropertyDTO.getProductId());

    orderItem.setUnit(bcgogoProductDTO.getUnit());
    orderItem.setProductText(bcgogoProductDTO.getText());
    orderItem.setProductName(bcgogoProductDTO.getName());
    orderItem.setProductId(bcgogoProductDTO.getId());


    orderItem.setAmount(1d);
    orderItem.setTotal(order.getTotalAmount());
    orderItem.setPrice(order.getTotalAmount());
    orderItem.setImagePath(bcgogoProductPropertyDTO.getImagePath());
    orderItem.setProductKind(bcgogoProductPropertyDTO.getKind());
    orderItem.setProductPropertyId(bcgogoProductPropertyDTO.getId());
    orderItem.setProductType(bcgogoProductPropertyDTO.getType());


    BcgogoReceivableOrderRecordRelation relation = new BcgogoReceivableOrderRecordRelation();
    relation.createSoftwareRelation(shopId);

    BcgogoReceivableRecord record = new BcgogoReceivableRecord();
    record.createSoftwareRecord(shopId);
    if (shopDTO.getFollowId() == null) {
      LOG.warn("shop[id={}] has no sale man!", shopId);
      record.setPayeeId(ShopConstant.JACK_CHEN_USER_ID);
    } else {
      record.setPayeeId(shopDTO.getFollowId());
    }
    if(record.getPayeeId()!=null){
      UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUserById(record.getPayeeId());
      record.setPayeeName(userDTO==null?null:userDTO.getName());
    }
    Object status = writer.begin();
    try {
      writer.saveOrUpdate(order);

      orderItem.setOrderId(order.getId());
      writer.saveOrUpdate(orderItem);

      HardwareSoftwareAccountDTO accountDTO = bcgogoAccountService.getHardwareSoftwareAccountByShopId(order.getShopId());
      accountDTO.createSoftwarePayable(order.getTotalAmount());
      bcgogoAccountService.updateHardwareSoftwareAccount(accountDTO, writer);

      writer.saveOrUpdate(record);
      relation.setBcgogoReceivableOrderId(order.getId());
      relation.setBcgogoReceivableRecordId(record.getId());
      writer.saveOrUpdate(relation);
      writer.commit(status);
      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      OperationLogDTO operationLogDTO = new OperationLogDTO(ShopConstant.BC_ADMIN_SHOP_ID,userId, order.getId(), ObjectTypes.BCGOGO_SOFTWARE_RECEIVABLE_ORDER, OperationTypes.CREATE);
      operationLogDTO.setContent(userName+"审核注册信息通过,生成销售单");
      operationLogService.saveOperationLog(operationLogDTO);

    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void hardwareReceivable(BcgogoReceivableDTO dto) throws BcgogoException {
    if (dto.getBcgogoReceivableOrderRecordRelationId() == null)
      throw new BcgogoException("bcgogoReceivableOrderRecordRelationId is null!");
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      BcgogoReceivableOrderRecordRelation relation = writer.getById(BcgogoReceivableOrderRecordRelation.class, dto.getBcgogoReceivableOrderRecordRelationId());
      BcgogoReceivableRecord record = writer.getById(BcgogoReceivableRecord.class, relation.getBcgogoReceivableRecordId());
      BcgogoReceivableOrder order = writer.getById(BcgogoReceivableOrder.class, relation.getBcgogoReceivableOrderId());
      dto.setBcgogoReceivableOrderId(order.getId());
      dto.setBcgogoReceivableOrderRecordRelationId(relation.getId());
      if(dto.getPayeeId()!=null){
        UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUserById(dto.getPayeeId());
        dto.setPayeeName(userDTO==null?null:userDTO.getName());
      }
      record.fromHardwareOfflineFullPayment(dto);
      relation.setAmount(dto.getPaidAmount());
      relation.setPaymentMethod(dto.getPaymentMethod());
      order.setReceivedAmount(dto.getPaidAmount());
      order.setReceivableAmount(order.getTotalAmount() - order.getReceivedAmount());
      order.setStatus(PaymentStatus.FULL_PAYMENT);
      //软硬件账单

      IBcgogoAccountService bcgogoAccountService = ServiceManager.getService(IBcgogoAccountService.class);
      HardwareSoftwareAccountDTO accountDTO = bcgogoAccountService.getHardwareSoftwareAccountByShopId(order.getShopId());
      accountDTO.createHardwareReceived(order.getReceivedAmount());
      bcgogoAccountService.updateHardwareSoftwareAccount(accountDTO, writer);

      writer.update(relation);
      writer.update(record);
      writer.update(order);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  //软件付款
  @Override
  public void softwareReceivable(BcgogoReceivableDTO dto) throws BcgogoException {
    if (dto.getBcgogoReceivableOrderRecordRelationId() == null)
      throw new BcgogoException("bcgogoReceivableOrderRecordRelationId is null!");
    if (ReceivableMethod.FULL == dto.getReceivableMethod()) {
      softwareFullReceivable(dto);
    } else if (ReceivableMethod.INSTALLMENT == dto.getReceivableMethod()) {
      softwareFirstInstalmentReceivable(dto);
    } else if (ReceivableMethod.UNCONSTRAINED == dto.getReceivableMethod()) {
      softwareSurplusReceivable(dto);
    }
  }

  //软件 全额付款
  private void softwareFullReceivable(BcgogoReceivableDTO dto) throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    BcgogoReceivableOrderRecordRelation relation = writer.getById(BcgogoReceivableOrderRecordRelation.class, dto.getBcgogoReceivableOrderRecordRelationId());
    BcgogoReceivableRecord record = writer.getById(BcgogoReceivableRecord.class, relation.getBcgogoReceivableRecordId());
    BcgogoReceivableOrder order = writer.getById(BcgogoReceivableOrder.class, relation.getBcgogoReceivableOrderId());
    dto.setBcgogoReceivableOrderId(order.getId());
    Object status = writer.begin();
    try {
      order.setReceivedAmount(dto.getPaidAmount());
      order.setReceivableAmount(NumberUtil.round((order.getTotalAmount() - order.getReceivedAmount()), 0));
      order.setStatus(PaymentStatus.FULL_PAYMENT);

      relation.setAmount(dto.getPaidAmount());
      record.setPaymentAmount(dto.getPaidAmount());

      //软硬件账单
      IBcgogoAccountService bcgogoAccountService = ServiceManager.getService(IBcgogoAccountService.class);
      HardwareSoftwareAccountDTO accountDTO = bcgogoAccountService.getHardwareSoftwareAccountByShopId(relation.getShopId());
      accountDTO.createSoftwareReceived(dto.getPaidAmount());
      bcgogoAccountService.updateHardwareSoftwareAccount(accountDTO, writer);

      //软件 全额付款
      if(dto.getPayeeId()!=null){
        UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUserById(dto.getPayeeId());
        dto.setPayeeName(userDTO==null?null:userDTO.getName());
      }
      record.fromSoftwareFullPayment(dto);
      writer.update(order);
      relation.setPaymentMethod(dto.getPaymentMethod());
      ServiceManager.getService(IShopService.class).updateShopPaymentStatus(record.getShopId(), PaymentStatus.FULL_PAYMENT, null);
      writer.update(relation);
      writer.update(record);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    afterFullReceived(record);
  }

  private void afterFullReceived(BcgogoReceivableRecord order) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(order.getShopId());
    if (!ShopStatus.isRegistrationPaid(shopDTO.getShopStatus())) return;
    RegisterInfoDTO infoDTO = configService.getRegisterInfoDTOByRegisterShopId(order.getShopId());
    final double customerInviteRechargeSms = 500d;
    final double supplierInviteRechargeSms = 200d;
    double smsDonationValue = 0d;
    if (infoDTO == null) {
      LOG.info("getRegisterInfoByRegisterShopId:{} is null .", order.getShopId());
      return;
    }
    if (infoDTO.getRegisterType() == RegisterType.CUSTOMER_INVITE) {
      smsDonationValue = customerInviteRechargeSms;
    } else if (infoDTO.getRegisterType() == RegisterType.SUPPLIER_INVITE) {
      smsDonationValue = supplierInviteRechargeSms;
    } else {
      LOG.info("registerType is {}", infoDTO.getRegisterType());
      return;
    }
    try{
      ShopSmsRecordDTO shopSmsRecordDTO = new ShopSmsRecordDTO();
      shopSmsRecordDTO.setSmsCategory(SmsCategory.RECOMMEND_HANDSEL);
      shopSmsRecordDTO.setNumber(Math.round(smsDonationValue * 10));
      shopSmsRecordDTO.setBalance(smsDonationValue);
      shopSmsRecordDTO.setShopId(infoDTO.getInviterShopId());
      shopSmsRecordDTO.setOperatorId(order.getSubmitterId());
      ServiceManager.getService(ISmsAccountService.class).createShopSmsHandsel(shopSmsRecordDTO);
      
      IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
      shopBalanceService.createSmsBalanceForInviterByRegisterShopId(infoDTO.getRegisterShopId(), smsDonationValue);
    }catch(BcgogoException e){
      LOG.error("BcgogoReceivableService.afterFullReceived出错。", e);
    }
  }

  //软件 分期付款
  private void softwareFirstInstalmentReceivable(BcgogoReceivableDTO dto) throws BcgogoException {
    if (dto.getInstalmentPlanAlgorithmId() == null)
      throw new BcgogoException("instalmentPlanAlgorithmId is null!");
    TxnWriter writer = txnDaoManager.getWriter();
    InstalmentPlanAlgorithm algorithm = writer.getById(InstalmentPlanAlgorithm.class, dto.getInstalmentPlanAlgorithmId());
    if (algorithm == null)
      throw new BcgogoException("algorithm[id=" + dto.getInstalmentPlanAlgorithmId() + "] is null!");
    BcgogoReceivableOrderRecordRelation relation = writer.getById(BcgogoReceivableOrderRecordRelation.class, dto.getBcgogoReceivableOrderRecordRelationId());
    BcgogoReceivableRecord record = writer.getById(BcgogoReceivableRecord.class, relation.getBcgogoReceivableRecordId());
    BcgogoReceivableOrder order = writer.getById(BcgogoReceivableOrder.class, relation.getBcgogoReceivableOrderId());
    dto.setBcgogoReceivableOrderId(order.getId());
    if (NumberUtil.isEqual(dto.getPaidAmount(), order.getReceivableAmount(), ZERO)) {
      softwareFullReceivable(dto);
      return;
    }
    //修改shop状态
    ServiceManager.getService(IShopService.class).updateShopStatus(order.getShopId(), ShopStatus.REGISTERED_PAID, PaymentStatus.PARTIAL_PAYMENT);
    Object status = writer.begin();
    try {
      //软硬件账单
      IBcgogoAccountService bcgogoAccountService = ServiceManager.getService(IBcgogoAccountService.class);
      HardwareSoftwareAccountDTO accountDTO = bcgogoAccountService.getHardwareSoftwareAccountByShopId(relation.getShopId());
      accountDTO.createSoftwareReceived(dto.getPaidAmount());
      bcgogoAccountService.updateHardwareSoftwareAccount(accountDTO, writer);

      order.setReceivedAmount(dto.getPaidAmount());
      order.setReceivableAmount(order.getTotalAmount() - order.getReceivedAmount());
      order.setStatus(PaymentStatus.PARTIAL_PAYMENT);
      order.setStartTime(dto.getPaymentTime());
      relation.setAmount(dto.getPaidAmount());
      record.setPaymentAmount(dto.getPaidAmount());
      record.fromInstallmentPayment(dto);
      //分期付款
      InstalmentPlan instalmentPlan = new InstalmentPlan();
      Map<Integer, InstalmentPlanItem> items = instalmentPlan.firstInstalmentPlan(order, algorithm, dto.getPaymentMethod());
      writer.saveOrUpdate(instalmentPlan);
      InstalmentPlanItem item, yItem, currentItem = null;
      for (int i = items.size(); i > 0; i--) {
        item = items.get(i);
        item.setInstalmentPlanId(instalmentPlan.getId());
        writer.saveOrUpdate(item);
        yItem = items.get(i - 1);
        if (yItem != null) {
          yItem.setNextItemId(item.getId());
        }
        if (item.getStatus() == PaymentStatus.PARTIAL_PAYMENT
            || (yItem != null && yItem.getStatus() == PaymentStatus.FULL_PAYMENT && item.getStatus() == PaymentStatus.NON_PAYMENT)) {
          instalmentPlan.setCurrentItemId(item.getId());
          instalmentPlan.setCurrentItemEndTime(item.getEndTime());
          currentItem = item;
        }
        instalmentPlan.setStatus(PaymentStatus.PARTIAL_PAYMENT);
      }
      relation.setReceivableMethod(ReceivableMethod.INSTALLMENT);
      relation.setInstalmentPlanId(instalmentPlan.getId());
      relation.setPaymentMethod(dto.getPaymentMethod());
      writer.saveOrUpdate(instalmentPlan);
      writer.saveOrUpdate(order);
      writer.saveOrUpdate(relation);
      writer.saveOrUpdate(record);
      createSoftwareInstalmentReceivable(order, instalmentPlan, currentItem, record.getPayeeId(), writer);
      ServiceManager.getService(IShopService.class).updateShopPaymentStatus(record.getShopId(), order.getStatus(), instalmentPlan.getCurrentItemEndTime());
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  //软件 剩余付款
  private void softwareSurplusReceivable(BcgogoReceivableDTO dto) throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    BcgogoReceivableOrderRecordRelation relation = writer.getById(BcgogoReceivableOrderRecordRelation.class, dto.getBcgogoReceivableOrderRecordRelationId());
    BcgogoReceivableRecord record = writer.getById(BcgogoReceivableRecord.class, relation.getBcgogoReceivableRecordId());
    BcgogoReceivableOrder order = writer.getById(BcgogoReceivableOrder.class, relation.getBcgogoReceivableOrderId());
    dto.setBcgogoReceivableOrderId(order.getId());
    Object status = writer.begin();
    try {
      order.setReceivedAmount(NumberUtil.round(dto.getPaidAmount() + order.getReceivedAmount(), 0));
      order.setReceivableAmount(NumberUtil.round(order.getReceivableAmount() - dto.getPaidAmount(), 0));

      if (NumberUtil.isZero(order.getReceivableAmount())) {
        order.setStatus(PaymentStatus.FULL_PAYMENT);
      } else {
        order.setStatus(PaymentStatus.PARTIAL_PAYMENT);
      }
      relation.setAmount(dto.getPaidAmount());
      record.setPaidAmount(dto.getPaidAmount());
      record.setPaymentAmount(dto.getPaidAmount());

      //软硬件账单
      IBcgogoAccountService bcgogoAccountService = ServiceManager.getService(IBcgogoAccountService.class);
      HardwareSoftwareAccountDTO accountDTO = bcgogoAccountService.getHardwareSoftwareAccountByShopId(relation.getShopId());
      accountDTO.createSoftwareReceived(dto.getPaidAmount());
      bcgogoAccountService.updateHardwareSoftwareAccount(accountDTO, writer);

      //软件 全额付款
      if(dto.getPayeeId()!=null){
        UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUserById(dto.getPayeeId());
        dto.setPayeeName(userDTO==null?null:userDTO.getName());
      }
      record.fromSoftwareSurplusPayment(dto);
      writer.update(order);
      relation.setPaymentMethod(dto.getPaymentMethod());
      ServiceManager.getService(IShopService.class).updateShopPaymentStatus(record.getShopId(), order.getStatus(), null);
      writer.update(relation);
      writer.update(record);


      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    if(order.getStatus()  == PaymentStatus.FULL_PAYMENT){
      afterFullReceived(record);
    }
  }

  //创建下一期待支付记录
  private void createSoftwareInstalmentReceivable(BcgogoReceivableOrder order, InstalmentPlan instalmentPlan, InstalmentPlanItem item, Long payeeId, TxnWriter writer) throws BcgogoException {
    if (item == null) throw new BcgogoException("instalment plan item is null. ");
    BcgogoReceivableRecord record = new BcgogoReceivableRecord();
    record.fromInstalmentPlanItem(item);
    record.setPayeeId(payeeId);
    if(payeeId!=null){
      UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUserById(payeeId);
      record.setPayeeName(userDTO==null?null:userDTO.getName());
    }
    writer.saveOrUpdate(record);

    BcgogoReceivableOrderRecordRelation relation = new BcgogoReceivableOrderRecordRelation();
    relation.setAmount(0.0d);
    relation.setShopId(order.getShopId());
    relation.setInstalmentPlanId(instalmentPlan.getId());
    relation.setInstalmentPlanItemId(item.getId());
    relation.setBcgogoReceivableOrderId(order.getId());
    relation.setBcgogoReceivableRecordId(record.getId());
    relation.setPaymentType(PaymentType.SOFTWARE);
    relation.setReceivableMethod(ReceivableMethod.INSTALLMENT);
    writer.saveOrUpdate(relation);

    order.setInstalmentPlanId(instalmentPlan.getId());
    order.setCurrentInstalmentPlanEndTime(item.getEndTime());
    writer.saveOrUpdate(order);
  }

  @Override
  public void instalmentReceivable(BcgogoReceivableDTO dto) throws BcgogoException {
    if (dto.getBcgogoReceivableOrderRecordRelationId() == null)
      throw new BcgogoException("bcgogoReceivableOrderRecordRelationId is null!");
    TxnWriter writer = txnDaoManager.getWriter();
    BcgogoReceivableOrderRecordRelation relation = writer.getById(BcgogoReceivableOrderRecordRelation.class, dto.getBcgogoReceivableOrderRecordRelationId());
    if (relation == null)
      throw new BcgogoException("BcgogoReceivableOrderRecordRelation[id=" + dto.getBcgogoReceivableOrderRecordRelationId() + "] is null!");
    BcgogoReceivableRecord record = writer.getById(BcgogoReceivableRecord.class, relation.getBcgogoReceivableRecordId());
    if (record == null)
      throw new BcgogoException("BcgogoReceivableRecord[id=" + relation.getBcgogoReceivableRecordId() + "] is null!");
    BcgogoReceivableOrder order = writer.getById(BcgogoReceivableOrder.class, relation.getBcgogoReceivableOrderId());
    dto.setBcgogoReceivableOrderId(order.getId());
    if (order == null)
      throw new BcgogoException("BcgogoReceivableOrder[id=" + relation.getBcgogoReceivableOrderId() + "] is null!");
    if (NumberUtil.round(dto.getPaidAmount() - order.getReceivableAmount(), 0) > ZERO)
      throw new BcgogoException("paid amount is larger than receivable amount!");
    if (order.getInstalmentPlanId() == null)
      throw new BcgogoException("BcgogoReceivableOrder instalment plan id is null!");
    InstalmentPlan instalmentPlan = writer.getById(InstalmentPlan.class, order.getInstalmentPlanId());
    if (instalmentPlan == null)
      throw new BcgogoException("instalmentPlan[id=" + order.getInstalmentPlanId() + "] is null!");

    Object status = writer.begin();
    try {
      order.setReceivedAmount(NumberUtil.round(dto.getPaidAmount() + order.getReceivedAmount(), 0));
      order.setReceivableAmount(NumberUtil.round(order.getReceivableAmount() - dto.getPaidAmount(), 0));
      //软硬件账单
      IBcgogoAccountService bcgogoAccountService = ServiceManager.getService(IBcgogoAccountService.class);
      HardwareSoftwareAccountDTO accountDTO = bcgogoAccountService.getHardwareSoftwareAccountByShopId(relation.getShopId());
      accountDTO.createSoftwareReceived(dto.getPaidAmount());
      bcgogoAccountService.updateHardwareSoftwareAccount(accountDTO, writer);

      if (NumberUtil.isZero(order.getReceivableAmount(), ZERO)) {
        //分期完成
        order.setStatus(PaymentStatus.FULL_PAYMENT);
        order.setEndTime(null);
        instalmentPlan.setStatus(PaymentStatus.FULL_PAYMENT);
        instalmentPlanItemReceivable(instalmentPlan, dto.getPaidAmount(), writer, new Pair<Integer, List<String>>(0, new ArrayList<String>()));
        afterFullReceived(record);
      } else {
        //还有分期未完成
        order.setStatus(PaymentStatus.PARTIAL_PAYMENT);
        instalmentPlan.setStatus(PaymentStatus.PARTIAL_PAYMENT);
        instalmentPlanItemReceivable(instalmentPlan, dto.getPaidAmount(), writer, new Pair<Integer, List<String>>(0, new ArrayList<String>()));
        createSoftwareInstalmentReceivable(order, instalmentPlan, writer.getById(InstalmentPlanItem.class, instalmentPlan.getCurrentItemId()), record.getPayeeId(), writer);
      }
      ServiceManager.getService(IShopService.class).updateShopPaymentStatus(record.getShopId(), order.getStatus(), instalmentPlan.getCurrentItemEndTime());
      writer.saveOrUpdate(order);
      writer.saveOrUpdate(relation);
      relation.setPaymentMethod(dto.getPaymentMethod());
      record.setStatus(BcgogoReceivableStatus.PENDING_REVIEW);
      record.setPaymentTime(dto.getPaymentTime());
      record.setPaidAmount(dto.getPaidAmount());
      record.setPayeeId(dto.getPayeeId());
      record.setSubmitterId(dto.getSubmitterId());
      record.setSubmitTime(System.currentTimeMillis());
      writer.saveOrUpdate(record);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 分期还款
   *
   * @param instalmentPlan 分期
   * @param paidAmount     付款金额
   * @param writer         TxnWriter
   * @param timeout        分期异常（Pair<Integer, List<String>>）
   * @throws BcgogoException
   */
  private void instalmentPlanItemReceivable(InstalmentPlan instalmentPlan, double paidAmount, TxnWriter writer, Pair<Integer, List<String>> timeout) throws BcgogoException {
    if (NumberUtil.isEqual(paidAmount, ZERO)) return;
    InstalmentPlanItem item = writer.getById(InstalmentPlanItem.class, instalmentPlan.getCurrentItemId());
    if (item == null)
      throw new BcgogoException("InstalmentPlanItem[id=" + instalmentPlan.getCurrentItemId() + "] is null!");
    timeout.setKey(timeout.getKey() == null ? 0 : timeout.getKey() + 1);
    timeout.getValue().add("InstalmentPlanItem[id=" + instalmentPlan.getCurrentItemId() + "]");
    if (timeout.getKey() > 20) {
      throw new BcgogoException("timeout exception! stack" + timeout.getValue().toString());
    }
    //改期应付金额>付款金额
    if (item.getPayableAmount() - paidAmount > ZERO) {
      item.setPaidAmount(NumberUtil.round(item.getPaidAmount() + paidAmount, 0));
      item.setPayableAmount(NumberUtil.round(item.getPayableAmount() - paidAmount, 0));
      item.setStatus(PaymentStatus.PARTIAL_PAYMENT);

      instalmentPlan.setCurrentItemId(item.getId());
      instalmentPlan.setCurrentItemEndTime(item.getEndTime());
      instalmentPlan.setPaidAmount(NumberUtil.round(instalmentPlan.getPaidAmount() + paidAmount, 0));
      instalmentPlan.setPayableAmount(NumberUtil.round(instalmentPlan.getPayableAmount() - paidAmount, 0));

      writer.update(item);
      writer.update(instalmentPlan);
//      paidAmount = 0;
    } else if (NumberUtil.isZero(NumberUtil.round((item.getPayableAmount() - paidAmount), 0), ZERO)) {
      //付款金额=改期金额

      item.setPaidAmount(NumberUtil.round((item.getPaidAmount() + paidAmount), 0));
      item.setPayableAmount(NumberUtil.round((item.getPayableAmount() - paidAmount), 0));
      item.setStatus(PaymentStatus.FULL_PAYMENT);
      if (item.getNextItemId() != null) {
        InstalmentPlanItem nextItem = writer.getById(InstalmentPlanItem.class, item.getNextItemId());
        instalmentPlan.setCurrentItemId(nextItem.getId());
        instalmentPlan.setCurrentItemEndTime(nextItem.getEndTime());
      }
      instalmentPlan.setPaidAmount(NumberUtil.round(instalmentPlan.getPaidAmount() + paidAmount, 0));
      instalmentPlan.setPayableAmount(NumberUtil.round(instalmentPlan.getPayableAmount() - paidAmount, 0));
      writer.update(item);
      writer.update(instalmentPlan);
//      paidAmount = 0;
    } else {
      //付款金额>该期金额
      paidAmount = NumberUtil.round(paidAmount - item.getPayableAmount(), 0);
      instalmentPlan.setPaidAmount(NumberUtil.round(instalmentPlan.getPaidAmount() + item.getPayableAmount(), 0));
      instalmentPlan.setPayableAmount(NumberUtil.round(instalmentPlan.getPayableAmount() - item.getPayableAmount(), 0));
      //如果付款金额>0，但是没有分期期数，数据异常
      if (item.getNextItemId() == null)
        throw new BcgogoException("amount:" + paidAmount + ",there is no next instalment item.");
      InstalmentPlanItem nextItem = writer.getById(InstalmentPlanItem.class, item.getNextItemId());
      instalmentPlan.setCurrentItemId(nextItem.getId());
      instalmentPlan.setCurrentItemEndTime(nextItem.getEndTime());

      item.setPaidAmount(item.getCurrentAmount());
      item.setPayableAmount(0d);
      item.setStatus(PaymentStatus.FULL_PAYMENT);

      writer.update(item);
      writer.update(instalmentPlan);
      instalmentPlanItemReceivable(instalmentPlan, paidAmount, writer, timeout);
    }
  }

  @Override
  public void auditReceivable(Long auditUserId,String auditUserName, Long bcgogoReceivableOrderRecordRelationId) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      BcgogoReceivableOrderRecordRelation relation = writer.getById(BcgogoReceivableOrderRecordRelation.class, bcgogoReceivableOrderRecordRelationId);
      BcgogoReceivableRecord record = writer.getById(BcgogoReceivableRecord.class, relation.getBcgogoReceivableRecordId());
      BcgogoReceivableOrder order = writer.getById(BcgogoReceivableOrder.class, relation.getBcgogoReceivableOrderId());
      if(relation.getSmsRechargeId() != null) {
         SmsRecharge smsRecharge = writer.getById(SmsRecharge.class,relation.getSmsRechargeId());
         smsRecharge.setStatus(BcgogoReceivableStatus.HAS_BEEN_PAID);
         smsRecharge.setAuditTime(System.currentTimeMillis());
         writer.update(smsRecharge);
      }
      record.setAuditorId(auditUserId);
      record.setAuditTime(System.currentTimeMillis());
      record.setStatus(BcgogoReceivableStatus.HAS_BEEN_PAID);
      writer.update(record);
      writer.commit(status);

      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      ObjectTypes objectTypes = null;
      if(PaymentType.HARDWARE.equals(order.getPaymentType())){
        objectTypes = ObjectTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER;
      }else if(PaymentType.SMS_RECHARGE.equals(order.getPaymentType())){
        objectTypes = ObjectTypes.BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER;
      }else if(PaymentType.SOFTWARE.equals(order.getPaymentType())){
        objectTypes = ObjectTypes.BCGOGO_SOFTWARE_RECEIVABLE_ORDER;
      }
      if(objectTypes!=null){
        OperationLogDTO operationLogDTO = null;
        if(relation.getSmsRechargeId() != null) {
          operationLogDTO = new OperationLogDTO(ShopConstant.BC_ADMIN_SHOP_ID,auditUserId,relation.getSmsRechargeId(),objectTypes, OperationTypes.AUDIT_PASS);
        } else {
          operationLogDTO = new OperationLogDTO(ShopConstant.BC_ADMIN_SHOP_ID,auditUserId,order.getId(),objectTypes, OperationTypes.AUDIT_PASS);
        }

        operationLogDTO.setContent(auditUserName+"审核入账￥"+record.getPaidAmount());
        operationLogService.saveOperationLog(operationLogDTO);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result getInstalmentPlanAlgorithms() {
    Result result = new Result(true);
    List<InstalmentPlanAlgorithmDTO> algorithmDtoList = new ArrayList<InstalmentPlanAlgorithmDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<InstalmentPlanAlgorithm> algorithmList = writer.getInstalmentPlanAlgorithms();
    for (InstalmentPlanAlgorithm algorithm : algorithmList) {
      algorithmDtoList.add(algorithm.toDTO());
    }
    result.setData(algorithmDtoList);
    result.setTotal(algorithmDtoList.size());
    return result;
  }

  @Override
  public Result getInstalmentPlanDetails(Long instalmentPlanId) {
    Result result = new Result(true);
    List<InstalmentPlanItemDTO> dtoList = new ArrayList<InstalmentPlanItemDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<InstalmentPlanItem> items = writer.getInstalmentPlanItemsByInstalmentPlanIds(instalmentPlanId);
    for (InstalmentPlanItem item : items) {
      dtoList.add(item.toDTO());
    }
    result.setData(dtoList);
    result.setTotal(dtoList.size());
    return result;
  }

  @Override
  public InstalmentPlanAlgorithmDTO getInstalmentPlanAlgorithmsById(Long instalmentPlanAlgorithmId) {
    TxnWriter writer = txnDaoManager.getWriter();
    InstalmentPlanAlgorithm instalmentPlanAlgorithm = writer.getById(InstalmentPlanAlgorithm.class, instalmentPlanAlgorithmId);
    return instalmentPlanAlgorithm == null ? null : instalmentPlanAlgorithm.toDTO();
  }

  @Override
  public Result addSoftwareReceived(UnconstrainedSoftwareReceivableDTO dto) throws BcgogoException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IShopService shopService = ServiceManager.getService(IShopService.class);
    IShopVersionService shopVersionService = ServiceManager.getService(IShopVersionService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    BcgogoReceivableOrder order = this.checkSoftwareOrder(dto.getShopId());
    ShopDTO shopDTO = configService.getShopById(dto.getShopId());
    if (shopDTO == null) throw new BcgogoException("shopId is null");
    ShopVersionDTO versionDTO = shopVersionService.getShopVersionById(shopDTO.getShopVersionId());
    List<BcgogoReceivableOrderRecordRelation> relationList = writer.getSoftwareReceivable(dto.getShopId());

    BcgogoReceivableRecord currentRecord = null;
    for (BcgogoReceivableOrderRecordRelation relation : relationList) {
      if (relation.getPaymentType() == PaymentType.SOFTWARE && relation.getReceivableMethod() != ReceivableMethod.UNCONSTRAINED) {
        if (relation.getReceivableMethod() == ReceivableMethod.INSTALLMENT)
          return new Result("该店铺选择分期支付。", false);
        else
          return new Result("该店铺选择全额支付。", false);
      } /*else {
        currentRecord = writer.getById(BcgogoReceivableRecord.class, relation.getBcgogoReceivableRecordId());
      }*/
    }
    Object status = writer.begin();
    try {
      //create order
      if (order == null) {
        order = new BcgogoReceivableOrder();
        order.setShopId(dto.getShopId());
        order.setTotalAmount(shopDTO.getSoftPrice());
        order.setReceivableAmount(NumberUtil.round((shopDTO.getSoftPrice() - dto.getReceivedAmount()), 0));
        if (order.getReceivableAmount() < 0) {
          return new Result("金额不得大于" + shopDTO.getSoftPrice() + "!", false, shopDTO.getSoftPrice());
        }
        order.setReceivedAmount(dto.getReceivedAmount());
        if (NumberUtil.isZero(order.getReceivableAmount())) {
          order.setStatus(PaymentStatus.FULL_PAYMENT);
          shopService.updateShopStatus(dto.getShopId(), ShopStatus.REGISTERED_PAID, PaymentStatus.FULL_PAYMENT);
        } else {
          order.setStatus(PaymentStatus.PARTIAL_PAYMENT);
          shopService.updateShopStatus(dto.getShopId(), ShopStatus.REGISTERED_PAID, PaymentStatus.PARTIAL_PAYMENT);
        }
        order.setReceivableContent(versionDTO.getValue() + " 【总额￥" + shopDTO.getSoftPrice() + "】");
        order.setStartTime(dto.getReceivedTime());
        order.setMemo("软件自由付款,无截止日期.");
        writer.saveOrUpdate(order);
      } else {
        if (order.getStatus() == PaymentStatus.FULL_PAYMENT) {
          return new Result("该账户已经支付完成!", false);
        }
        if (NumberUtil.round(dto.getReceivedAmount() - order.getReceivableAmount(), 0) > 0) {
          return new Result("金额不得大于" + order.getReceivableAmount() + "!", false, order.getReceivableAmount());
        }
        order.setReceivableAmount(NumberUtil.round(order.getReceivableAmount() - dto.getReceivedAmount(), 0));
        order.setReceivedAmount(NumberUtil.round(order.getReceivableAmount() + dto.getReceivedAmount(), 0));
        if (NumberUtil.isZero(order.getReceivableAmount())) {
          order.setStatus(PaymentStatus.FULL_PAYMENT);
          shopService.updateShopStatus(dto.getShopId(), ShopStatus.REGISTERED_PAID, PaymentStatus.FULL_PAYMENT);
        } else {
          order.setStatus(PaymentStatus.PARTIAL_PAYMENT);
          shopService.updateShopStatus(dto.getShopId(), ShopStatus.REGISTERED_PAID, PaymentStatus.PARTIAL_PAYMENT);
        }
        writer.saveOrUpdate(order);
      }

      //软硬件统计
      IBcgogoAccountService bcgogoAccountService = ServiceManager.getService(IBcgogoAccountService.class);
      HardwareSoftwareAccountDTO accountDTO = bcgogoAccountService.checkSoftwareAccountByShopId(dto.getShopId(), shopDTO.getSoftPrice());
      accountDTO.createSoftwareReceived(dto.getReceivedAmount());
      bcgogoAccountService.updateHardwareSoftwareAccount(accountDTO, writer);

      BcgogoReceivableRecord record = new BcgogoReceivableRecord();
      if(dto.getPayeeId()!=null){
        UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUserById(dto.getPayeeId());
        dto.setPayeeName(userDTO==null?null:userDTO.getName());
      }
      record.createUnconstrainedSoftwareReceived(dto);
      writer.saveOrUpdate(record);

      BcgogoReceivableOrderRecordRelation relation = new BcgogoReceivableOrderRecordRelation(dto.getShopId(), ReceivableMethod.UNCONSTRAINED, dto.getReceivedAmount());
      relation.setPaymentType(PaymentType.SOFTWARE);
      relation.setBcgogoReceivableOrderId(order.getId());
      relation.setPaymentMethod(PaymentMethod.DOOR_CHARGE);
      relation.setBcgogoReceivableRecordId(record.getId());
      writer.saveOrUpdate(relation);

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return new Result(true);
  }

  @Override
  public Result createSoftwareReceivable(UnconstrainedSoftwareReceivableDTO dto) throws BcgogoException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IShopService shopService = ServiceManager.getService(IShopService.class);
    IShopVersionService shopVersionService = ServiceManager.getService(IShopVersionService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    BcgogoReceivableOrder order = this.checkSoftwareOrder(dto.getShopId());
    ShopDTO shopDTO = configService.getShopById(dto.getShopId());
    if (shopDTO == null) throw new BcgogoException("shopId is null");
    ShopVersionDTO versionDTO = shopVersionService.getShopVersionById(shopDTO.getShopVersionId());
    List<BcgogoReceivableOrderRecordRelation> relationList = writer.getSoftwareReceivable(dto.getShopId());
    for (BcgogoReceivableOrderRecordRelation relation : relationList) {
      if (relation.getPaymentType() == PaymentType.SOFTWARE && relation.getReceivableMethod() != ReceivableMethod.UNCONSTRAINED) {
        if (relation.getReceivableMethod() == ReceivableMethod.INSTALLMENT)
          return new Result("该店铺选择分期支付。", false);
        else
          return new Result("该店铺选择全额支付。", false);
      }
      if (relation.getReceivableMethod() == ReceivableMethod.UNCONSTRAINED) {
        BcgogoReceivableRecord record = writer.getById(BcgogoReceivableRecord.class, relation.getBcgogoReceivableRecordId());
        if (record != null && record.getStatus() == BcgogoReceivableStatus.TO_BE_PAID) {
          LOG.warn("shop [id:{}] has unconstrained to be paid bcgogo receivable record.", dto.getShopId());
          return new Result("该店铺选择已经有待支付记录。", false);
        }
      }
    }
    Object status = writer.begin();
    try {
      //create order
      if (order == null) {
        order = new BcgogoReceivableOrder();
        order.setShopId(dto.getShopId());
        order.setTotalAmount(shopDTO.getSoftPrice());
        order.setReceivableAmount(shopDTO.getSoftPrice());
        order.setMemo("软件自由付款,无截止日期.");
        order.setReceivedAmount(0.0);
        order.setStatus(PaymentStatus.NON_PAYMENT);
        order.setReceivableContent(versionDTO.getValue() + " 【总额￥" + shopDTO.getSoftPrice() + "】");
        writer.saveOrUpdate(order);
      }
      if (order.getStatus() == PaymentStatus.FULL_PAYMENT) {
        return new Result("该账户已经支付完成!", false);
      }
      if (NumberUtil.round(dto.getReceivingAmount() - order.getReceivableAmount(), 0) > 0) {
        return new Result("金额不得大于" + order.getReceivableAmount() + "!", false, order.getReceivableAmount());
      }

      //软硬件统计
      IBcgogoAccountService bcgogoAccountService = ServiceManager.getService(IBcgogoAccountService.class);
      HardwareSoftwareAccountDTO accountDTO=  bcgogoAccountService.checkSoftwareAccountByShopId(dto.getShopId(), shopDTO.getSoftPrice());
//      accountDTO.createSoftwarePayable(dto.getReceivingAmount());
//      bcgogoAccountService.updateHardwareSoftwareAccount(accountDTO, writer);

      BcgogoReceivableRecord record = new BcgogoReceivableRecord();
      if(dto.getPayeeId()!=null){
        UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUserById(dto.getPayeeId());
        dto.setPayeeName(userDTO==null?null:userDTO.getName());
      }
      record.createUnconstrainedSoftwareReceivable(dto);
      writer.saveOrUpdate(record);

      BcgogoReceivableOrderRecordRelation relation = new BcgogoReceivableOrderRecordRelation(dto.getShopId(), ReceivableMethod.UNCONSTRAINED, dto.getReceivingAmount());
      relation.setPaymentType(PaymentType.SOFTWARE);
      relation.setBcgogoReceivableOrderId(order.getId());
      relation.setPaymentMethod(PaymentMethod.DOOR_CHARGE);
      relation.setBcgogoReceivableRecordId(record.getId());
      writer.saveOrUpdate(relation);

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return new Result(true);
  }


  private BcgogoReceivableOrder checkSoftwareOrder(Long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<BcgogoReceivableOrderRecordRelation> relationList = writer.getSoftwareReceivable(shopId);
    for (BcgogoReceivableOrderRecordRelation relation : relationList) {
      if (relation.getReceivableMethod() == ReceivableMethod.UNCONSTRAINED) {
        return writer.getById(BcgogoReceivableOrder.class, relation.getBcgogoReceivableOrderId());
      }
    }
    return null;
  }

  public Result validateBargainContext(long shopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<BcgogoReceivableOrderRecordRelation> relationList = writer.getSoftwareReceivable(shopId);
    for (BcgogoReceivableOrderRecordRelation relation : relationList) {
      if (relation.getPaymentType() == PaymentType.SOFTWARE) {
        if (relation.getReceivableMethod() == ReceivableMethod.INSTALLMENT)
          return new Result("该店铺选择分期支付。", false);
        else if (relation.getReceivableMethod() == ReceivableMethod.UNCONSTRAINED)
          return new Result("该店铺选择其他支付。", false);
        else {
          BcgogoReceivableOrder order = writer.getById(BcgogoReceivableOrder.class, relation.getBcgogoReceivableOrderId());
          if (order == null) return new Result("异常。", false);
          if (order.getStatus() != PaymentStatus.NON_PAYMENT) {
            return new Result("该店铺选择全额支付。", false);
          } else {
            return new Result(true);
          }
        }
      }
    }
    return new Result("异常。", false);
  }

  @Override
  public Result updateSoftwareReceivable(long shopId, double price) throws BcgogoException {
    TxnWriter writer = txnDaoManager.getWriter();
    this.validateBargainContext(shopId);
    List<BcgogoReceivableOrderRecordRelation> relationList = writer.getSoftwareReceivable(shopId);
    for (BcgogoReceivableOrderRecordRelation relation : relationList) {
      if (relation.getPaymentType() == PaymentType.SOFTWARE) {
        if (relation.getReceivableMethod() == ReceivableMethod.INSTALLMENT)
          return new Result("该店铺选择分期支付。", false);
        else if (relation.getReceivableMethod() == ReceivableMethod.UNCONSTRAINED)
          return new Result("该店铺选择其他支付。", false);
        else {
          BcgogoReceivableRecord record = writer.getById(BcgogoReceivableRecord.class, relation.getBcgogoReceivableRecordId());
          BcgogoReceivableOrder order = writer.getById(BcgogoReceivableOrder.class, relation.getBcgogoReceivableOrderId());
          if (order == null) return new Result("异常。", false);
          if (order.getStatus() != PaymentStatus.NON_PAYMENT) {
            return new Result("该店铺选择全额支付。", false);
          } else {
            Object status = writer.begin();
            try {
              ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(relation.getShopId());
              ShopVersionDTO versionDTO = ServiceManager.getService(IShopVersionService.class).getShopVersionById(shopDTO.getShopVersionId());
              //软硬件统计
              IBcgogoAccountService bcgogoAccountService = ServiceManager.getService(IBcgogoAccountService.class);
              HardwareSoftwareAccountDTO accountDTO = bcgogoAccountService.getHardwareSoftwareAccountByShopId(order.getShopId());
              accountDTO.createSoftwarePayable(price - order.getTotalAmount());
              bcgogoAccountService.updateHardwareSoftwareAccount(accountDTO, writer);
              order.setReceivableContent(versionDTO.getValue() + " 【总额￥" + price + "】");
              order.setTotalAmount(price);
              order.setReceivableAmount(price);
              writer.update(order);
              if(record!=null){
                record.setPaymentAmount(order.getTotalAmount());
                writer.update(record);
              }
              BcgogoReceivableOrderItem orderItem = CollectionUtil.getFirst(writer.getBcgogoReceivableOrderItemByOrderId(order.getId()));
              if(orderItem!=null){
                orderItem.setPrice(price);
                orderItem.setTotal(price);
                writer.update(orderItem);
              }
              writer.commit(status);
            } finally {
              writer.rollback(status);
            }
            return new Result("议价后修改待支付成功。", true);
          }
        }
      }
    }
    return new Result("异常。", false);
  }

  @Override
  public void initBcgogoReceivableOrder(){
    BcgogoReceivableSearchCondition condition = new BcgogoReceivableSearchCondition();
    condition.setStart(0);
    condition.setLimit(Integer.MAX_VALUE);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    IBcgogoProductService bcgogoProductService = ServiceManager.getService(IBcgogoProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<BcgogoReceivableOrder> bcgogoReceivableOrderList = writer.searchBcgogoReceivableOrderResult(condition);
      if(CollectionUtils.isNotEmpty(bcgogoReceivableOrderList)){
        for(BcgogoReceivableOrder bcgogoReceivableOrder:bcgogoReceivableOrderList){
          ShopDTO shopDTO = configService.getShopById(bcgogoReceivableOrder.getShopId());
          if(shopDTO!=null){
            if(PaymentType.HARDWARE.equals(bcgogoReceivableOrder.getPaymentType())){
              bcgogoReceivableOrder.setBuyChannels(BuyChannels.BACKGROUND_ENTRY);
              bcgogoReceivableOrder.setReceiptNo(txnService.getBcgogoOrderReceiptNo(ShopConstant.BC_ADMIN_SHOP_ID, OrderTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER,bcgogoReceivableOrder.getCreationDate()));
            }else if(PaymentType.SOFTWARE.equals(bcgogoReceivableOrder.getPaymentType())){
              BuyChannels buyChannels = BuyChannels.BACKGROUND_ENTRY;
              if(shopDTO!=null && StringUtils.isNotBlank(shopDTO.getAgent())){
                UserDTO userDTO = userCacheService.getUser(shopDTO.getAgent(),ShopConstant.BC_ADMIN_SHOP_ID);
                if(userDTO!=null && userDTO.getOccupationId()!=null){
                  OccupationDTO occupationDTO = userCacheService.getOccupationDTO(userDTO.getOccupationId());
                  if(occupationDTO!=null && (occupationDTO.getName().equals("业务") || occupationDTO.getName().equals("业务员"))){
                    buyChannels = BuyChannels.ONLINE_ORDERS;
                  }
                }
              }
              bcgogoReceivableOrder.setBuyChannels(buyChannels);
              bcgogoReceivableOrder.setReceiptNo(txnService.getBcgogoOrderReceiptNo(ShopConstant.BC_ADMIN_SHOP_ID, OrderTypes.BCGOGO_SOFTWARE_RECEIVABLE_ORDER,bcgogoReceivableOrder.getCreationDate()));
            }else if(PaymentType.SMS_RECHARGE.equals(bcgogoReceivableOrder.getPaymentType())){
              bcgogoReceivableOrder.setBuyChannels(BuyChannels.ONLINE_ORDERS);
              bcgogoReceivableOrder.setReceiptNo(txnService.getBcgogoOrderReceiptNo(ShopConstant.BC_ADMIN_SHOP_ID, OrderTypes.BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER,bcgogoReceivableOrder.getCreationDate()));
            }
            //orderItem
            String receivableContent = bcgogoReceivableOrder.getReceivableContent();
            List<BcgogoReceivableOrderItem> bcgogoReceivableOrderItemList = new ArrayList<BcgogoReceivableOrderItem>();
            if(PaymentType.HARDWARE.equals(bcgogoReceivableOrder.getPaymentType())){

              if(StringUtils.isNotBlank(receivableContent)){
                receivableContent = receivableContent.substring(0,receivableContent.indexOf("【"));
                String[] productInfos = receivableContent.split("、");

                if(!ArrayUtils.isEmpty(productInfos)){
                  for(int i=0;i<productInfos.length;i++){
                    String str = productInfos[i];
                    if(str.indexOf("读卡器")>-1){
                      String count = str.replace("读卡器","").replace("台","");
                      BcgogoReceivableOrderItem item = new BcgogoReceivableOrderItem();
                      item.setProductId(100000001l);
                      item.setProductName("读卡器");
                      item.setProductText("读卡器");
                      item.setOrderId(bcgogoReceivableOrder.getId());
                      item.setAmount(NumberUtil.doubleVal(count.trim()));
                      if(i==0){
                        item.setTotal(bcgogoReceivableOrder.getTotalAmount());
                        item.setPrice(NumberUtil.round(bcgogoReceivableOrder.getTotalAmount()/item.getAmount(),1));
                      }else{
                        item.setTotal(0d);
                        item.setPrice(0d);
                      }
                      bcgogoReceivableOrderItemList.add(item);
                    }
                    if(str.indexOf("扫描枪")>-1){
                      String count = str.replace("扫描枪", "").replace("台","");
                      BcgogoReceivableOrderItem item = new BcgogoReceivableOrderItem();
                      item.setProductId(100000002l);
                      item.setProductName("扫描枪");
                      item.setProductText("扫描枪");
                      item.setOrderId(bcgogoReceivableOrder.getId());
                      item.setAmount(NumberUtil.doubleVal(count.trim()));
                      if(i==0){
                        item.setTotal(bcgogoReceivableOrder.getTotalAmount());
                        item.setPrice(NumberUtil.round(bcgogoReceivableOrder.getTotalAmount()/item.getAmount(),1));
                      }else{
                        item.setTotal(0d);
                        item.setPrice(0d);
                      }
                      bcgogoReceivableOrderItemList.add(item);
                    }
                    if(str.indexOf("会员卡")>-1){
                      String count = str.replace("会员卡", "").replace("张","");
                      BcgogoReceivableOrderItem item = new BcgogoReceivableOrderItem();
                      item.setProductId(100000003l);
                      item.setProductName("会员卡");
                      item.setProductText("会员卡");
                      item.setOrderId(bcgogoReceivableOrder.getId());
                      item.setAmount(NumberUtil.doubleVal(count.trim()));
                      if(i==0){
                        item.setTotal(bcgogoReceivableOrder.getTotalAmount());
                        item.setPrice(NumberUtil.round(bcgogoReceivableOrder.getTotalAmount()/item.getAmount(),1));
                      }else{
                        item.setTotal(0d);
                        item.setPrice(0d);
                      }
                      bcgogoReceivableOrderItemList.add(item);
                    }
                  }
                }
              }
            } else if(PaymentType.SOFTWARE.equals(bcgogoReceivableOrder.getPaymentType())){
              if(shopDTO!=null && shopDTO.getShopVersionId()!=null){
                BcgogoReceivableOrderItem item = new BcgogoReceivableOrderItem();
                BcgogoProductPropertyDTO bcgogoProductPropertyDTO = bcgogoProductService.getBcgogoProductPropertyDTOById(shopDTO.getShopVersionId());
                BcgogoProductDTO bcgogoProductDTO = bcgogoProductService.getBcgogoProductDTOById(bcgogoProductPropertyDTO.getProductId());

                item.setUnit(bcgogoProductDTO.getUnit());
                item.setProductText(bcgogoProductDTO.getText());
                item.setProductName(bcgogoProductDTO.getName());
                item.setProductId(bcgogoProductDTO.getId());


                item.setAmount(1d);
                item.setTotal(bcgogoReceivableOrder.getTotalAmount());
                item.setPrice(bcgogoReceivableOrder.getTotalAmount());
                item.setImagePath(bcgogoProductPropertyDTO.getImagePath());
                item.setProductKind(bcgogoProductPropertyDTO.getKind());
                item.setProductPropertyId(bcgogoProductPropertyDTO.getId());
                item.setProductType(bcgogoProductPropertyDTO.getType());
                item.setOrderId(bcgogoReceivableOrder.getId());

                bcgogoReceivableOrderItemList.add(item);
              }
            }
            for(BcgogoReceivableOrderItem item:bcgogoReceivableOrderItemList){
              writer.save(item);
            }

            bcgogoReceivableOrder.setAddress(shopDTO.getAddress());
            bcgogoReceivableOrder.setCity(shopDTO.getCity());
            bcgogoReceivableOrder.setProvince(shopDTO.getProvince());
            bcgogoReceivableOrder.setRegion(shopDTO.getRegion());
            bcgogoReceivableOrder.setContact(shopDTO.getContact());
            bcgogoReceivableOrder.setMobile(shopDTO.getContactMobile());
            writer.update(bcgogoReceivableOrder);
          }
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public BcgogoReceivableOrderDTO shipBcgogoReceivableOrder(String userName,Long userId,Long bcgogoReceivableOrderId) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      BcgogoReceivableOrder bcgogoReceivableOrder = writer.getById(BcgogoReceivableOrder.class, bcgogoReceivableOrderId);
      bcgogoReceivableOrder.setStatus(PaymentStatus.SHIPPED);
      writer.update(bcgogoReceivableOrder);
      writer.commit(status);

      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      OperationLogDTO operationLogDTO = new OperationLogDTO(ShopConstant.BC_ADMIN_SHOP_ID, userId,bcgogoReceivableOrderId,ObjectTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER, OperationTypes.DISPATCH);
      operationLogDTO.setContent(userName+"发货");
      operationLogService.saveOperationLog(operationLogDTO);

      return bcgogoReceivableOrder.toDTO();
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public BcgogoReceivableOrderDTO cancelBcgogoReceivableOrder(Long shopId,Long bcgogoReceivableOrderId, Long cancelUserId,String cancelUserName,String cancelReason) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      BcgogoReceivableOrder bcgogoReceivableOrder = writer.getById(BcgogoReceivableOrder.class, bcgogoReceivableOrderId);
      if(!shopId.equals(ShopConstant.BC_ADMIN_SHOP_ID) && !shopId.equals(bcgogoReceivableOrder.getShopId())){
        throw new Exception("shopId is not matched!");
      }
      bcgogoReceivableOrder.setStatus(PaymentStatus.CANCELED);
      bcgogoReceivableOrder.setCancelReason(cancelReason);
      bcgogoReceivableOrder.setCancelUserId(cancelUserId);
      bcgogoReceivableOrder.setCancelTime(System.currentTimeMillis());
      writer.update(bcgogoReceivableOrder);
      writer.commit(status);
      return bcgogoReceivableOrder.toDTO();
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public BcgogoReceivableOrderItemDTO getBcgogoReceivableOrderItemDTO(Long orderItemId) {
    TxnWriter writer = txnDaoManager.getWriter();
    BcgogoReceivableOrderItem bcgogoReceivableOrderItem = writer.getById(BcgogoReceivableOrderItem.class,orderItemId);
    return bcgogoReceivableOrderItem.toDTO();
  }

  @Override
  public BcgogoReceivableOrderDTO getSimpleBcgogoReceivableOrderDTO(Long orderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    BcgogoReceivableOrder bcgogoReceivableOrder = writer.getById(BcgogoReceivableOrder.class, orderId);
    return bcgogoReceivableOrder.toDTO();
  }

  @Override
  public List<BcgogoReceivableDTO> getBcgogoReceivableDTOByRelationId(Long... relationId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getBcgogoReceivableDTOByRelationId(relationId);
  }

  @Override
  public void updateBcgogoReceivableOrderItemDTO(BcgogoReceivableOrderItemDTO bcgogoReceivableOrderItemDTO){
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      BcgogoReceivableOrderItem bcgogoReceivableOrderItem = writer.getById(BcgogoReceivableOrderItem.class, bcgogoReceivableOrderItemDTO.getId());
      Double oldTotal = bcgogoReceivableOrderItem.getTotal();
      bcgogoReceivableOrderItem.fromDTO(bcgogoReceivableOrderItemDTO);
      BcgogoReceivableOrder bcgogoReceivableOrder = writer.getById(BcgogoReceivableOrder.class,bcgogoReceivableOrderItem.getOrderId());
      bcgogoReceivableOrder.setTotalAmount(bcgogoReceivableOrder.getTotalAmount()-oldTotal+bcgogoReceivableOrderItem.getTotal());
      bcgogoReceivableOrder.setReceivableAmount(bcgogoReceivableOrder.getTotalAmount());
      BcgogoReceivableRecord bcgogoReceivableRecord = CollectionUtil.getFirst(writer.getBcgogoReceivableOrderToBePaidRecordByOrderId(bcgogoReceivableOrder.getId()));
      if(bcgogoReceivableRecord!=null){
        bcgogoReceivableRecord.setPaymentAmount(bcgogoReceivableOrder.getTotalAmount());
        writer.update(bcgogoReceivableRecord);
      }
      writer.update(bcgogoReceivableOrder);
      writer.update(bcgogoReceivableOrderItem);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
}
