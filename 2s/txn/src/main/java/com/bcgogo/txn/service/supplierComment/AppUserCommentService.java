package com.bcgogo.txn.service.supplierComment;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.ShopOrderCommentDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.supplierComment.CommentStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.txn.dto.supplierComment.*;
import com.bcgogo.txn.model.RepairOrder;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.WashBeautyOrder;
import com.bcgogo.txn.model.supplierComment.CommentRecord;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.ctc.wstx.util.DataUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-23
 * Time: 下午5:06
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AppUserCommentService implements IAppUserCommentService {

  private static final Logger LOG = LoggerFactory.getLogger(AppUserCommentService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;


  /**
   * 保存或者更新手机端用户评价记录
   * @param appUserCommentRecordDTO
   */
  public void saveOrUpdateAppUserCommentRecord(AppUserCommentRecordDTO appUserCommentRecordDTO) {
    if (appUserCommentRecordDTO == null) {
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();

    CommentRecord commentRecord = new CommentRecord();
    if (appUserCommentRecordDTO.getId() == null) {
      commentRecord = commentRecord.fromAppUserCommentRecordDTO(appUserCommentRecordDTO);
    } else {
      commentRecord = writer.getById(CommentRecord.class, appUserCommentRecordDTO.getId());
      if (commentRecord == null) {
        return;
      }
      commentRecord = commentRecord.fromAppUserCommentRecordDTO(appUserCommentRecordDTO);
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
   * 保存或者更新微信端用户评价记录
   * @param commentRecordDTO
   */
  public void saveOrUpdateWXUserCommentRecord(AppUserCommentRecordDTO commentRecordDTO) {
    if (commentRecordDTO == null)  return ;
    TxnWriter writer = txnDaoManager.getWriter();
    CommentRecord commentRecord = null;
    if (commentRecordDTO.getId()!= null) {
      commentRecord = writer.getById(CommentRecord.class, commentRecordDTO.getId());
    }
    if (commentRecord == null) {
      commentRecord=new CommentRecord();
    }
    commentRecord = commentRecord.fromAppUserCommentRecordDTO(commentRecordDTO);
    Object status = writer.begin();
    try {
      writer.saveOrUpdate(commentRecord);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  /**
   * 手机端用户评价店铺前校验
   * @param shopOrderCommentDTO
   *    *校验： 1.加锁 这个单据正在评价不能评价 2.这个单据已评价3.单据已作废
   * @return
   */
  public Result validateAndSaveAppUserCommentShop(ShopOrderCommentDTO shopOrderCommentDTO) {
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    Result result = new Result();
    result.setSuccess(false);

    TxnWriter txnWriter = txnDaoManager.getWriter();
    //判断这个单据是施工单还是洗车美容单
    OrderTypes orderType = null;
    Long orderId = shopOrderCommentDTO.getOrderId();
    Long shopId = null;

    AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(shopOrderCommentDTO.getUserNo(), null);
    if (appUserDTO == null) {
      result.setMsg("该用户不存在");
      return result;
    }

    RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, orderId);
    if (repairOrder == null) {
      WashBeautyOrder washBeautyOrder = txnWriter.getById(WashBeautyOrder.class, orderId);
      if (washBeautyOrder == null) {
        result.setMsg(CommentConstant.ORDER_NULL);
        return result;
      }
      shopId = washBeautyOrder.getShopId();
      orderType = OrderTypes.WASH_BEAUTY;
      shopOrderCommentDTO.setReceiptNo(washBeautyOrder.getReceiptNo());
      shopOrderCommentDTO.setCustomerId(washBeautyOrder.getCustomerId());
    } else {
      orderType = OrderTypes.REPAIR;
      shopId = repairOrder.getShopId();
      shopOrderCommentDTO.setReceiptNo(repairOrder.getReceiptNo());
      shopOrderCommentDTO.setCustomerId(repairOrder.getCustomerId());
    }

    ShopDTO shopDTO = configService.getShopByIdWithoutContacts(shopId);

    //判断是否已经评价
    List<CommentRecord> commentRecordList = txnWriter.getCommentRecordByOrderId(null, orderId);
    if (CollectionUtils.isNotEmpty(commentRecordList)) {
      result.setMsg(CommentConstant.COMMENT_DONE);
      return result;
    }

    //判断字数
    if (StringUtils.isNotEmpty(shopOrderCommentDTO.getCommentContent()) && shopOrderCommentDTO.getCommentContent().length() > CommentConstant.APP_USER_COMMENT_LENGTH) {
      result.setMsg(CommentConstant.COMMENT_LONG);
      return result;
    }
    try {
      AppUserCommentRecordDTO appUserCommentRecordDTO = new AppUserCommentRecordDTO(shopOrderCommentDTO, shopDTO, appUserDTO, orderId, orderType);
      this.saveOrUpdateAppUserCommentRecord(appUserCommentRecordDTO);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      return result;
    }
    result.setSuccess(true);
    return result;
  }

  /**
   * 根据单据id和
   * @param commentatorShopId
   * @param orderId
   * @return
   */
  public AppUserCommentRecordDTO getAppUserCommentRecordByOrderId(Long commentatorShopId, Long orderId) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<CommentRecord> commentRecordList = txnWriter.getCommentRecordByOrderId(null, orderId);

    if (CollectionUtils.isEmpty(commentRecordList)) {
      return null;
    }

    if (commentRecordList.size() > 1) {
      LOG.error("commentatorShopId" + commentatorShopId + ",orderId" + orderId + "有多条commentRecord");
    }

    return CollectionUtil.getFirst(commentRecordList).toAppUserCommentRecordDTO();
  }

  @Override
  public AppUserCommentSearchResultDTO getAppUserCommentRecordByShopId(Long shopId, Pager pager, Sort sort) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ICustomerService customerService=ServiceManager.getService(ICustomerService.class);
    AppUserCommentSearchResultDTO appUserCommentSearchResultDTO = new AppUserCommentSearchResultDTO();
    List<AppUserCommentRecordDTO>  appUserCommentRecordDTOs = new ArrayList<AppUserCommentRecordDTO>();
    if(shopId == null) {
      LOG.error("getAppUserCommentRecordByShopId error: shopId is null");
      return appUserCommentSearchResultDTO;
    }
    //评分统计
    CommentStatDTO commentStatDTO = supplierCommentService.getCommentStatByShopId(shopId);
    if(commentStatDTO != null) {
      //综合评分
      if(commentStatDTO.getTotalScore() + NumberUtil.doubleVal(commentStatDTO.getCommentTotalScore()) > 0 && commentStatDTO.getRecordAmount() != 0) {
        commentStatDTO.setAverageScore(NumberUtil.round((commentStatDTO.getTotalScore() + NumberUtil.doubleVal(commentStatDTO.getCommentTotalScore())) / commentStatDTO.getRecordAmount(), 1));
      }

    }
    appUserCommentSearchResultDTO.setCommentStatDTO(commentStatDTO);
    //评分记录
    List<CommentRecord> commentRecordList = txnWriter.getSupplierCommentByPager(shopId, CommentStatus.STAT, pager, sort);
    if(CollectionUtil.isNotEmpty(commentRecordList)) {
      for (CommentRecord commentRecord : commentRecordList) {
        AppUserCommentRecordDTO appUserCommentRecordDTO = commentRecord.toAppUserCommentRecordDTO();
        //获取单据对应客户的姓名和手机号
        CustomerDTO customerDTO=customerService.getCustomerById(appUserCommentRecordDTO.getCustomerId(),shopId);
        if(customerDTO==null){
          appUserCommentRecordDTO.setCustomerName("--");
          appUserCommentRecordDTO.setMobile("--");
        }else{
          appUserCommentRecordDTO.setCustomerName(customerDTO.getName()==null?"--":customerDTO.getName());
          appUserCommentRecordDTO.setMobile(customerDTO.getMobile()==null?"--":customerDTO.getMobile());
        }
        //单据号
        if (appUserCommentRecordDTO.getOrderId() != null) {
          if (OrderTypes.WASH_BEAUTY.equals(appUserCommentRecordDTO.getOrderType())) {
            WashBeautyOrderDTO washBeautyOrderDTO = txnService.getWashBeautyOrderDTOById(shopId, appUserCommentRecordDTO.getOrderId());
            if (washBeautyOrderDTO != null) {
              appUserCommentRecordDTO.setReceiptNo(washBeautyOrderDTO.getReceiptNo());
              appUserCommentRecordDTO.setVechicle(washBeautyOrderDTO.getVechicle());
            }
          } else if (OrderTypes.REPAIR.equals(appUserCommentRecordDTO.getOrderType())) {
            RepairOrderDTO repairOrderDTO = txnService.getRepairOrder(shopId, appUserCommentRecordDTO.getOrderId());
            if (repairOrderDTO != null) {
              appUserCommentRecordDTO.setReceiptNo(repairOrderDTO.getReceiptNo());
              appUserCommentRecordDTO.setVechicle(repairOrderDTO.getVechicle());
            }
          }
        }
        appUserCommentRecordDTOs.add(appUserCommentRecordDTO);
      }
    }
    appUserCommentSearchResultDTO.setAppUserCommentRecordDTOs(appUserCommentRecordDTOs);
    return appUserCommentSearchResultDTO;
  }

  @Override
  public AppUserCommentSearchResultDTO getAppCommentRecordByShopIdAndKeyword(Long shopId,Pager pager,CommentRecordDTO commentRecordDTO) throws Exception {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ICustomerService customerService=ServiceManager.getService(ICustomerService.class);
    AppUserCommentSearchResultDTO appUserCommentSearchResultDTO = new AppUserCommentSearchResultDTO();
    List<AppUserCommentRecordDTO>  appUserCommentRecordDTOs = new ArrayList<AppUserCommentRecordDTO>();
    if(shopId == null) {
      LOG.error("getAppUserCommentRecordByShopIdAndKeyword error: shopId is null");
      return appUserCommentSearchResultDTO;
    }
    commentRecordDTO.initSearchTime();
    //评分统计
    CommentStatDTO commentStatDTO = supplierCommentService.getCommentStatByShopId(shopId);
    if(commentStatDTO != null) {
      //综合评分
      if(commentStatDTO.getTotalScore() + NumberUtil.doubleVal(commentStatDTO.getCommentTotalScore()) > 0 && commentStatDTO.getRecordAmount() != 0) {
        double dd=(commentStatDTO.getTotalScore() + NumberUtil.doubleVal(commentStatDTO.getCommentTotalScore())) / commentStatDTO.getRecordAmount();

        commentStatDTO.setAverageScore(Math.round( dd * 10 ) / 10.0);
      }
      int badComment=supplierCommentService.countCommentTypeRecordByKeyword(shopId,commentRecordDTO,"badComment")<=0?0:supplierCommentService.countCommentTypeRecordByKeyword(shopId,commentRecordDTO,"badComment");
      int mediumComment=supplierCommentService.countCommentTypeRecordByKeyword(shopId,commentRecordDTO,"mediumComment")<=0?0:supplierCommentService.countCommentTypeRecordByKeyword(shopId,commentRecordDTO,"mediumComment");
      int goodComment=supplierCommentService.countCommentTypeRecordByKeyword(shopId,commentRecordDTO,"goodComment")<=0?0:supplierCommentService.countCommentTypeRecordByKeyword(shopId,commentRecordDTO,"goodComment");
      commentStatDTO.setBadCommentAmount(Integer.valueOf(badComment).longValue());
      commentStatDTO.setMediumCommentAmount(Integer.valueOf(mediumComment).longValue());
      commentStatDTO.setGoodCommentAmount(Integer.valueOf(goodComment).longValue());

    }
    appUserCommentSearchResultDTO.setCommentStatDTO(commentStatDTO);
    //评分记录
    List<CommentRecord> commentRecordList = txnWriter.getSupplierCommentByPagerAndKeyword(shopId, pager,commentRecordDTO);
    if(CollectionUtil.isNotEmpty(commentRecordList)) {
      for (CommentRecord commentRecord : commentRecordList) {
        AppUserCommentRecordDTO appUserCommentRecordDTO = commentRecord.toAppUserCommentRecordDTO();
        //获取单据对应客户的姓名和手机号
        CustomerDTO customerDTO=customerService.getCustomerById(appUserCommentRecordDTO.getCustomerId(),shopId);
        if(customerDTO==null){
          appUserCommentRecordDTO.setCustomerName("");
          appUserCommentRecordDTO.setMobile("");
        }else{
          appUserCommentRecordDTO.setCustomerName(StringUtils.isBlank(customerDTO.getName())?"":customerDTO.getName());
          appUserCommentRecordDTO.setMobile(StringUtils.isBlank(customerDTO.getMobile())?"":customerDTO.getMobile());
        }
        //单据号
        if (appUserCommentRecordDTO.getOrderId() != null) {
          if (OrderTypes.WASH_BEAUTY.equals(appUserCommentRecordDTO.getOrderType())) {
            WashBeautyOrderDTO washBeautyOrderDTO = txnService.getWashBeautyOrderDTOById(shopId, appUserCommentRecordDTO.getOrderId());
            if (washBeautyOrderDTO != null) {
              appUserCommentRecordDTO.setReceiptNo(washBeautyOrderDTO.getReceiptNo());
              appUserCommentRecordDTO.setVechicle(washBeautyOrderDTO.getVechicle());
            }
          } else if (OrderTypes.REPAIR.equals(appUserCommentRecordDTO.getOrderType())) {
            RepairOrderDTO repairOrderDTO = txnService.getRepairOrder(shopId, appUserCommentRecordDTO.getOrderId());
            if (repairOrderDTO != null) {
              appUserCommentRecordDTO.setReceiptNo(repairOrderDTO.getReceiptNo());
              appUserCommentRecordDTO.setVechicle(repairOrderDTO.getVechicle());
            }
          }else if (OrderTypes.SALE.equals(appUserCommentRecordDTO.getOrderType())) {
            SalesOrderDTO salesOrderDTO = txnService.getSalesOrder(appUserCommentRecordDTO.getOrderId(), shopId);
            if (salesOrderDTO != null) {
              appUserCommentRecordDTO.setReceiptNo(salesOrderDTO.getReceiptNo());
              appUserCommentRecordDTO.setVechicle(salesOrderDTO.getLicenceNo());
            }
          }
        }
        appUserCommentRecordDTOs.add(appUserCommentRecordDTO);
      }
    }
    appUserCommentSearchResultDTO.setAppUserCommentRecordDTOs(appUserCommentRecordDTOs);
    return appUserCommentSearchResultDTO;

  }




}
