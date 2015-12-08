package com.bcgogo.txn.service.messageCenter;

import com.bcgogo.common.PagingListResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopRelationInviteDTO;
import com.bcgogo.enums.txn.message.ReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.remind.dto.message.NoticeDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-22
 * Time: 下午12:01
 * 消息中心-通知 接口
 */
public interface INoticeService {


  /**
   * @param dto NoticeDTO{
   *            senderShopId:请求者ShopId          必须
   *            NoticeType:关联请求通知            必须
   *            content:内容                       必须
   *            receiverShopId:shopId              必须
   *            receiverIds:本店的供应商或客户    非必须 “，”分开
   *            }
   */
  NoticeDTO createNotice(NoticeDTO dto) throws BcgogoException;


  /**
   * 接受客户申请给自己的通知 提示合并客户信息
   * SUPPLIER_ACCEPT_TO_SUPPLIER
   * @param customerShopDTO
   * @param shopRelationInviteDTO
   * @param customerDTO
   */
  void createSupplierAcceptNoticeToSupplier(ShopDTO customerShopDTO, ShopRelationInviteDTO shopRelationInviteDTO, CustomerDTO customerDTO) throws Exception;

  /**
   * 供应商关联客户成功通知  提示客户合并供应商
   * SUPPLIER_ACCEPT_TO_CUSTOMER
   * @param supplierShopDTO
   * @param shopRelationInviteDTO
   * @param supplierDTO
   */
  void createSupplierAcceptNoticeToCustomer(ShopDTO supplierShopDTO, ShopRelationInviteDTO shopRelationInviteDTO, SupplierDTO supplierDTO) throws Exception;

  /**
   * 关联供应商成功通知  提示客户合并供应商
   * CUSTOMER_ACCEPT_TO_SUPPLIER
   * @param customerShopDTO
   * @param shopRelationInviteDTO
   * @param customerDTO
   * @throws Exception
   */
  void createCustomerAcceptNoticeToSupplier(ShopDTO customerShopDTO, ShopRelationInviteDTO shopRelationInviteDTO, CustomerDTO customerDTO)throws Exception;

  /**
   * 供应商申请添加客户店，客户接受后给客户的通知
   * CUSTOMER_ACCEPT_TO_CUSTOMER
   * @param shopDTO
   * @param shopRelationInviteDTO
   * @param supplierDTO
   * @throws Exception
   */
  void createCustomerAcceptNoticeToCustomer(ShopDTO shopDTO, ShopRelationInviteDTO shopRelationInviteDTO, SupplierDTO supplierDTO)throws Exception;

  /**
   * 取消店铺关联
   * @param senderShopId
   * @param receiverShopId
   * @param cancelMsg
   */
  void createCancelNotice(Long senderShopId, Long receiverShopId, String cancelMsg) throws Exception;


  void createRefuseNotice(ShopDTO shopDTO, ShopRelationInviteDTO shopRelationInviteDTO) throws Exception;
}
