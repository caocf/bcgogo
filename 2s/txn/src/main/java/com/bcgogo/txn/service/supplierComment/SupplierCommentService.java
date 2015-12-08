package com.bcgogo.txn.service.supplierComment;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.config.dto.ApplyShopSearchCondition;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.supplierComment.CommentRecordType;
import com.bcgogo.enums.supplierComment.CommentStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PurchaseInventoryDTO;
import com.bcgogo.txn.dto.PurchaseOrderDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;
import com.bcgogo.txn.dto.supplierComment.*;
import com.bcgogo.txn.model.PurchaseOrder;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.supplierComment.CommentRecord;
import com.bcgogo.txn.model.supplierComment.CommentStat;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.IShopSolrWriterService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 供应商点评接口实现类
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-13
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SupplierCommentService implements ISupplierCommentService {

  private static final Logger LOG = LoggerFactory.getLogger(SupplierCommentService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;

  /**
   * 保存或者更行供应商评价记录
   *
   * @param supplierCommentRecordDTO
   */
  @Override
  public void saveOrUpdateSupplierCommentRecord(SupplierCommentRecordDTO supplierCommentRecordDTO) {
    if (supplierCommentRecordDTO == null) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    CommentRecord commentRecord = new CommentRecord();
    if (supplierCommentRecordDTO.getId() == null) {
      commentRecord = commentRecord.fromSupplierCommentRecordDTO(supplierCommentRecordDTO);
    } else {
      commentRecord = writer.getById(CommentRecord.class, supplierCommentRecordDTO.getId());
      if (commentRecord == null) {
        return;
      }
      commentRecord = commentRecord.fromSupplierCommentRecordDTO(supplierCommentRecordDTO);
    }

    Object status = writer.begin();
    try {
      if (commentRecord.getId() == null) {
        writer.save(commentRecord);
      } else {
        writer.update(commentRecord);
      }
      writer.commit(status);

    } finally {
      writer.rollback(status);
    }
  }


  /**
   * 更新手机端用户评价记录
   *
   * @param commentRecord
   */
  @Override
  public void saveOrUpdateCommentRecord(CommentRecord commentRecord) {
    if (commentRecord == null) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    Object status = writer.begin();
    try {

      writer.update(commentRecord);
      writer.commit(status);

    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 在评价之前进行校验:
   * 1.是否所有项目都已打分
   * 2.是否是在线采购单
   * 3.采购单是否已经评价过
   *
   * @param supplierCommentRecordDTO
   * @return
   */
  public Result validateBeforeSupplierComment(SupplierCommentRecordDTO supplierCommentRecordDTO) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Result result = new Result();
    result.setSuccess(false);
    try {
      if (StringUtil.isEmpty(supplierCommentRecordDTO.getPurchaseInventoryIdStr()) || !NumberUtil.isNumber(supplierCommentRecordDTO.getPurchaseInventoryIdStr())) {
        result.setMsg(CommentConstant.PURCHASE_INVENTORY_NULL);
        return result;
      }
      Long purchaseInventoryId = Long.valueOf(supplierCommentRecordDTO.getPurchaseInventoryIdStr());

      PurchaseInventoryDTO purchaseInventoryDTO = txnService.getPurchaseInventoryById(purchaseInventoryId, supplierCommentRecordDTO.getCustomerShopId());
      if (purchaseInventoryDTO == null) {
        result.setMsg(CommentConstant.PURCHASE_INVENTORY_NULL);
        return result;
      }

      if (purchaseInventoryDTO.getPurchaseOrderId() == null) {
        result.setMsg(CommentConstant.PURCHASE_ORDER_NULL);
        return result;
      }
      Long purchaseOrderId = purchaseInventoryDTO.getPurchaseOrderId();

      PurchaseOrderDTO purchaseOrderDTO = txnService.getPurchaseOrderById(purchaseOrderId);
      if (purchaseOrderDTO == null || purchaseOrderDTO.getSupplierShopId() == null) {
        result.setMsg(CommentConstant.PURCHASE_ORDER_NULL);
        return result;
      }else{
        supplierCommentRecordDTO.setReceiptNo(purchaseOrderDTO.getReceiptNo());
      }

      SupplierCommentRecordDTO commentRecordDTO = this.getCommentRecordByOrderId(purchaseOrderId, supplierCommentRecordDTO.getCustomerShopId());
      if (commentRecordDTO != null) {
        result.setMsg(CommentConstant.SUPPLIER_COMMENT_DONE);
        return result;
      }

      SalesOrderDTO salesOrderDTO = txnService.getSalesOrderByPurchaseOrderIdShopId(purchaseOrderId, purchaseOrderDTO.getSupplierShopId());
      if (salesOrderDTO == null) {
        result.setMsg(CommentConstant.SALE_ORDER_NUll);
        return result;
      }

      if (NumberUtil.doubleVal(supplierCommentRecordDTO.getQualityScore()) <= 0 || NumberUtil.doubleVal(supplierCommentRecordDTO.getAttitudeScore()) <= 0 ||
          NumberUtil.doubleVal(supplierCommentRecordDTO.getPerformanceScore()) <= 0 || NumberUtil.doubleVal(supplierCommentRecordDTO.getSpeedScore()) <= 0) {
        result.setMsg(CommentConstant.COMMENT_SCORE_NULL);
        return result;
      }
      if(StringUtil.isNotEmpty(supplierCommentRecordDTO.getCommentContent()) && supplierCommentRecordDTO.getCommentContent().length() > CommentConstant.COMMENT_LENGTH) {
        result.setMsg(CommentConstant.COMMENT_LONG);
        return result;
      }

      result.setSuccess(true);
      supplierCommentRecordDTO.setSupplierCommentInfo(purchaseOrderDTO, purchaseInventoryDTO, salesOrderDTO);

      return result;

    } catch (Exception e) {
      LOG.error("SupplierCommentService.validateBeforeSupplierComment,supplierCommentRecordDTO:" + supplierCommentRecordDTO.toString());
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg(CommentConstant.SUPPLIER_COMMENT_FAIL);
    }
    return result;
  }

  /**
   * 根据采购单id和shopId获得采购单评价记录
   *
   * @param orderId
   * @param customerShopId
   * @return
   */
  public SupplierCommentRecordDTO getCommentRecordByOrderId(Long orderId, Long customerShopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<CommentRecord> commentRecordList = writer.getCommentRecordByOrderId(customerShopId, orderId);
    if (CollectionUtils.isEmpty(commentRecordList)) {
      return null;
    }
    if (commentRecordList.size() > 1) {
      LOG.error("SupplierCommentService.getCommentRecordByOrderId,customerShopId:" + customerShopId + ",orderId:" + orderId + ",评价记录record多于1条");
    }
    return commentRecordList.get(0).toSupplierCommentRecordDTO();
  }

  /**
   * 根据供应商评价记录id追加内容
   *
   * @param supplierCommentRecordDTO
   */
  public Result addSupplierCommentContent(SupplierCommentRecordDTO supplierCommentRecordDTO) {
    Result result = new Result();
    result.setSuccess(false);

    try {

      if (StringUtil.isEmpty(supplierCommentRecordDTO.getSupplierCommentRecordIdStr()) || !NumberUtil.isNumber(supplierCommentRecordDTO.getSupplierCommentRecordIdStr())) {
        result.setMsg(CommentConstant.SUPPLIER_COMMENT_NULL);
        return result;
      }
      if (StringUtil.isEmpty(supplierCommentRecordDTO.getAddCommentContent())) {
        result.setMsg(CommentConstant.ADD_COMMENT_CONTENT_EMPTY);
        return result;
      }else if(supplierCommentRecordDTO.getAddCommentContent().length() > CommentConstant.COMMENT_LENGTH) {
        result.setMsg(CommentConstant.COMMENT_LONG);
        return result;
      }

      TxnWriter writer = txnDaoManager.getWriter();
      CommentRecord commentRecord = writer.getById(CommentRecord.class, Long.valueOf(supplierCommentRecordDTO.getSupplierCommentRecordIdStr()));
      if (commentRecord == null) {
        result.setMsg(CommentConstant.SUPPLIER_COMMENT_NULL);
        return result;
      }

      if(StringUtil.isNotEmpty(commentRecord.toSupplierCommentRecordDTO().getAddCommentContent())) {
        result.setMsg(CommentConstant.ADD_COMMENT_CONTENT_DONE);
        return result;
      }

      String daysStr = "";
      Long days = (DateUtil.get6clock(System.currentTimeMillis()) - DateUtil.get6clock(NumberUtil.longValue(commentRecord.getCommentTime()))) / DateUtil.DAY_MILLION_SECONDS;
      if (days == 0L) {
        daysStr = CommentConstant.ADD_CURRENT_DAY + CommentConstant.ADD_CONTENT_FORMAT;
      } else {
        daysStr = days + CommentConstant.ADD_DAY + CommentConstant.ADD_CONTENT_FORMAT;
      }
      commentRecord.setCommentContent(commentRecord.getCommentContent() + CommentConstant.COMMENT_SPACE + daysStr + supplierCommentRecordDTO.getAddCommentContent());

      Object status = writer.begin();
      try {
        writer.update(commentRecord);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
      result.setSuccess(true);

    } catch (Exception e) {
      LOG.error("SupplierCommentService.addSupplierCommentContent,supplierCommentRecordDTO:" + supplierCommentRecordDTO.toString());
      LOG.error(e.getMessage(), e);
      result.setMsg(CommentConstant.ADD_COMMENT_CONTENT_FAIL);
    }
    return result;
  }


  /**
   * 根据店铺id统计该店铺的点评数据
   *
   * @param shopId
   * @throws Exception
   */

  public void commentStatByShopId(Long shopId,Long shopVersionId) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    List<CommentRecord> commentRecordList = this.getCommentRecordByShopId(shopId, null);
    if (CollectionUtil.isEmpty(commentRecordList)) {
      commentRecordList = new ArrayList<CommentRecord>();
    }

    int orderCount  = getOrderCountByShopId(shopId,shopVersionId);

    CommentStatDTO commentStatDTO = this.getCommentStatByShopId(shopId);
    if (commentStatDTO == null) {
      commentStatDTO = new CommentStatDTO();
      commentStatDTO.setShopId(shopId);
    }
    commentStatDTO.setOrderAmount((long)orderCount);

    for (CommentRecord commentRecord : commentRecordList) {
      if (commentRecord.getCommentStatus() == CommentStatus.STAT) {
        continue;
      }

      if (commentRecord.getCommentRecordType() == CommentRecordType.APP_TO_SHOP) {
        AppUserCommentRecordDTO appUserCommentRecordDTO = commentRecord.toAppUserCommentRecordDTO();
        commentStatDTO.calculateFromAppUserRecordDTO(appUserCommentRecordDTO);

      } else {
        SupplierCommentRecordDTO supplierCommentRecordDTO = commentRecord.toSupplierCommentRecordDTO();
        commentStatDTO.calculateFromSupplierRecordDTO(supplierCommentRecordDTO);
      }
      commentRecord.setCommentStatus(CommentStatus.STAT);
      this.saveOrUpdateCommentRecord(commentRecord);
    }

    commentStatDTO.setStatTime(System.currentTimeMillis());
    this.saveOrUpdateSupplierCommentStat(commentStatDTO);
    IShopSolrWriterService shopSolrWriterService = ServiceManager.getService(IShopSolrWriterService.class);
    shopSolrWriterService.reCreateShopIdSolrIndex(shopId);

  }


  /**
   * 根据shop_id获取成交笔数
   * @param shopId
   * @return
   */
  public int getOrderCountByShopId(Long shopId,Long shopVersionId) {

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    if (shopVersionId == 10000010017531653L || shopVersionId == 10000010017531657L || shopVersionId == 10000010037193619L
        || shopVersionId == 10000010037193620L) {
      List<PurchaseOrder> purchaseOrderList = txnService.getPurchaseOrderBySupplierShopId(shopId);
      if (CollectionUtil.isEmpty(purchaseOrderList)) {
        return 0;
      }
      int orderCount = 0;
      for (PurchaseOrder purchaseOrder : purchaseOrderList) {
        PurchaseInventoryDTO purchaseInventoryDTO = txnService.getPurchaseInventoryIdByPurchaseOrderId(purchaseOrder.getShopId(), purchaseOrder.getId());
        if (purchaseInventoryDTO != null) {
          orderCount++;
        }
      }
      return orderCount;
    }

    TxnWriter writer = txnDaoManager.getWriter();
    int repairCount = writer.countRepairOrderByShopIdStatus(shopId, OrderStatus.REPAIR_SETTLED);
    int washBeautyCount = writer.countWashBeautyOrderByShopIdStatus(shopId, OrderStatus.WASH_SETTLED);
    return repairCount + washBeautyCount;
  }

  /**
   * 根据供应商店铺id和点评记录状态来获得点评记录
   *
   * @param supplierShopId
   * @param commentStatus
   * @return
   */
  public List<CommentRecord> getCommentRecordByShopId(Long supplierShopId, CommentStatus commentStatus) {
    TxnWriter writer = txnDaoManager.getWriter();

    List<CommentRecord> commentRecordList = writer.getCommentRecordByShopId(supplierShopId, commentStatus);
    return commentRecordList;
  }

  /**
   * 根据供应商店铺id获得该店铺的点评统计值
   *
   * @param supplierShopId
   * @return
   */
  public CommentStatDTO getCommentStatByShopId(Long supplierShopId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<CommentStat> commentStatList = writer.getCommentStatByShopId(supplierShopId);
    if (CollectionUtils.isEmpty(commentStatList)) {
      return null;
    }
    if (commentStatList.size() > 1) {
      LOG.error("SupplierCommentService.getSupplierCommentStatByShopId,supplierShopId:" + supplierShopId + ",点评统计记录多于1条");
    }
    return commentStatList.get(0).toDTO();
  }

  public CommentStatDTO getShopCommentStat(Long supplierShopId){
    TxnWriter writer = txnDaoManager.getWriter();
    CommentStat commentStat =CollectionUtil.getFirst(writer.getCommentStatByShopId(supplierShopId));
    if (commentStat==null) {
      return null;
    }
    CommentStatDTO commentStatDTO=commentStat.toDTO();
    commentStatDTO.calculate();
    return commentStatDTO;
  }

  /**
   * 保存或者更新供应商点评统计数据
   *
   * @param commentStatDTO
   */
  public void saveOrUpdateSupplierCommentStat(CommentStatDTO commentStatDTO) {
    if (commentStatDTO == null) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    CommentStat commentStat = null;

    try {
      if (commentStatDTO.getId() != null) {
        commentStat = writer.getById(CommentStat.class, commentStatDTO.getId());
        if (commentStat == null) {
          return;
        }
        commentStat = commentStat.fromDTO(commentStatDTO);
        writer.update(commentStat);
      } else {
        commentStat = new CommentStat();
        commentStat = commentStat.fromDTO(commentStatDTO);
        writer.save(commentStat);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 根据某个店铺关联的供应商id获得该供应商的店铺id
   *
   * @param supplierId
   * @return
   */
  public Long getSupplierShopIdBySupplierId(Long supplierId) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    if (supplierId == null) {
      return null;
    }
    SupplierDTO supplierDTO = userService.getSupplierById(supplierId);
    if (supplierDTO == null || supplierDTO.getSupplierShopId() == null) {
      return null;
    }
    return supplierDTO.getSupplierShopId();
  }


  /**
   * 根据供应商店铺id统计record记录条数
   *
   * @param shopId
   * @param commentStatus
   * @return
   */
  public int countSupplierCommentRecord(Long shopId, CommentStatus commentStatus) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countSupplierCommentRecord(shopId, commentStatus);
  }

  /**
   * 分页获得点评记录
   *
   * @param shopId
   * @param commentStatus
   * @param pager
   * @param sort
   * @return
   */
  public List<SupplierCommentRecordDTO> getSupplierCommentByPager(Long shopId, CommentStatus commentStatus, Pager pager, Sort sort) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<CommentRecord> commentRecordList = writer.getSupplierCommentByPager(shopId, commentStatus, pager, sort);
    if (CollectionUtils.isEmpty(commentRecordList)) {
      return null;
    }
    List<SupplierCommentRecordDTO> supplierCommentRecordDTOList = new ArrayList<SupplierCommentRecordDTO>();
    for (CommentRecord commentRecord : commentRecordList) {
      supplierCommentRecordDTOList.add(commentRecord.toSupplierCommentRecordDTO());
    }
    return supplierCommentRecordDTOList;
  }

  /**
   * 根据供应商 获得供应商评分统计值
   * @param supplierShopIds
   * @return
   */
  public Map<Long,CommentStatDTO> getCommentStatByShopIds(Collection<Long> supplierShopIds) {
    Map<Long, CommentStatDTO> map = new HashMap<Long, CommentStatDTO>();
    if (CollectionUtils.isEmpty(supplierShopIds)) {
      return map;
    }

    TxnWriter writer = txnDaoManager.getWriter();
    List<CommentStat> commentStatList = writer.getCommentStatBySupplier(supplierShopIds);
    if (CollectionUtils.isEmpty(commentStatList)) {
      return map;
    }
    for (CommentStat commentStat : commentStatList) {
      map.put(commentStat.getShopId(), commentStat.toDTO());
    }

    return map;
  }


  /**
   * 设置每个供应商的评价分数
   * @param supplierDTOList
   * @param shopSearchConditions
   */
  public void setSupplierCommentStat(Collection<SupplierDTO> supplierDTOList,Collection<ApplyShopSearchCondition> shopSearchConditions) {
    if (CollectionUtils.isEmpty(supplierDTOList) && CollectionUtil.isEmpty(shopSearchConditions)) {
      return;
    }

    List<Long> supplierShopIds = new ArrayList<Long>();

    if (CollectionUtil.isNotEmpty(supplierDTOList)) {

      for (SupplierDTO supplierDTO : supplierDTOList) {
        if (supplierDTO.getRelationType() == RelationTypes.UNRELATED || supplierDTO.getSupplierShopId() == null) {
          continue;
        }
        supplierShopIds.add(supplierDTO.getSupplierShopId());
      }
      if (CollectionUtils.isEmpty(supplierShopIds)) {
        return;
      }

      Map<Long, CommentStatDTO> map = getCommentStatByShopIds(supplierShopIds);
      if (MapUtils.isEmpty(map)) {
        return;
      }

      for (SupplierDTO supplierDTO : supplierDTOList) {
        if (supplierDTO.getRelationType() == RelationTypes.UNRELATED || supplierDTO.getSupplierShopId() == null) {
          continue;
        }
        CommentStatDTO commentStatDTO = map.get(supplierDTO.getSupplierShopId());
        if(commentStatDTO == null){
          continue;
        }
        commentStatDTO.calculate();
        if (commentStatDTO != null) {
          supplierDTO.fromSupplierCommentStat(commentStatDTO);
        }
      }
      return;
    }

    for (ApplyShopSearchCondition applyShopSearchCondition : shopSearchConditions) {
      supplierShopIds.add(applyShopSearchCondition.getShopId());
    }
    Map<Long, CommentStatDTO> map = getCommentStatByShopIds(supplierShopIds);
    if (MapUtils.isEmpty(map)) {
      return;
    }

    for (ApplyShopSearchCondition condition : shopSearchConditions) {

      CommentStatDTO commentStatDTO = map.get(condition.getShopId());
      if(commentStatDTO == null){
        continue;
      }
      commentStatDTO.calculate();
      if (commentStatDTO != null) {
        condition.fromSupplierCommentStat(commentStatDTO);
      }
    }
  }

  /**
   * 依据shopId和搜索关键字查询评价记录条数
   * @param shopId
   * @param commentRecordDTO
   * @return
   */
  public int countSupplierCommentRecordByKeyword(Long shopId,CommentRecordDTO commentRecordDTO){
    TxnWriter writer = txnDaoManager.getWriter();
    commentRecordDTO.initSearchTime();
    return writer.countSupplierCommentRecordByKeyword(shopId,commentRecordDTO);
  }

  /**
   * 依据shopId和搜索关键字获得差评\中评\好评个数
   * @param shopId
   * @param commentRecordDTO
   * @param commentType
   * @return
   */
  public int countCommentTypeRecordByKeyword(Long shopId,CommentRecordDTO commentRecordDTO,String commentType){
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countCommentTypeRecordByKeyword(shopId,commentRecordDTO,commentType);
  }

}
