package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.pushMessage.appoint.AppAppointParameter;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.VehicleDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-9-5
 * Time: 上午11:31
 */
public interface IAppointOrderService {
  AppointOrderDTO getAppointOrderById(Long shopId, Long appointOrderId);

  List<AppointOrderMaterialDTO> getAppointOrderMaterialDTOs(Long shopId, Long appointOrderId);

  List<AppointOrderServiceDetailDTO> getAppointOrderServiceDetailDTOs(Long shopId, Long appointOrderId);

  AppointOrderDTO getSimpleAppointOrderById(Long shopId, Long appointOrderId);

  List<AppointOrderServiceItemDTO> getAppointOrderServiceItemDTOs(Long shopId, Long appointOrderId);

  List<AppointOrderDTO> getRemindedAppointOrder(Long upTime, Long downTime, int start, int limit);

  //保存预约单逻辑
  void handleSaveAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception;

  //更新预约单逻辑
  void handleUpdateAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception;

  //接受预约单逻辑
  Result handleAcceptAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception;

  //校验预约单改单逻辑
  Result validateUpdateAppointOrder(AppointOrderDTO appointOrderDTO);

    //校验预约单接受逻辑
  Result validateAcceptAppointOrder(AppointOrderDTO appointOrderDTO);

  Result validateRefuseAppointOrder(AppointOrderDTO appointOrderDTO);

  Result handleRefuseAppointOrder(AppointOrderDTO appointOrderDTO)throws Exception;

  Result validateCancelAppointOrder(AppointOrderDTO appointOrderDTO);

  Result handleCancelAppointOrder(AppointOrderDTO appointOrderDTO)throws Exception;
  /**
   * 预约单查询
   * @param searchCondition
   * @return
   */
  List<AppointOrderDTO> searchAppointOrderDTOs(AppointOrderSearchCondition searchCondition);

  int countAppointOrderDTOs(AppointOrderSearchCondition searchCondition);

  Map<Long,List<AppointOrderServiceItemDTO>> getAppointOrderServiceItemDTOMap(Set<Long> ids);

   //校验能否创建预约单，如果本店服务没有设置，需要提醒客户跳转到
  Result validateCreateAppointOrder(Long shopId);

  //预约单生成其他单据时的校验
  Result validateCreateOtherOrder(AppointOrderDTO appointOrderDTO) throws Exception;

  //判断预约单能否生成洗车单，false的时候认为是生成施工单 ,只要预约内容中有一条是洗车就认为是true
  boolean isCreateWashBeauty(AppointOrderDTO appointOrderDTO);

  //预约单信息组装客户信息
  void generateCustomerInfo(CustomerDTO customerDTO, VehicleDTO vehicleDTO, AppointOrderDTO appointOrderDTO);

  //预约单生成洗车单，洗车单结算的时候处理预约单状态
  void handelAppointOrderAfterSaveWashBeauty(WashBeautyOrderDTO washBeautyOrderDTO) throws Exception;

  //预约单生成施工单，施工单结算的时候处理预约单状态
  void handelAppointOrderAfterSaveRepairOrder(RepairOrderDTO repairOrderDTO)throws Exception;

  void handelAppointOrderAfterSaveRepairDraft(DraftOrderDTO draftOrderDTO)throws Exception;

  public List<AppAppointParameter> createAppAppointParameter(AppointOrderDTO dbAppointOrderDTO);


  //接受预约单逻辑
  Result handleAutoAcceptAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception;

  /**
   * 后台自动接受预约单
   * @throws Exception
   */
  public void autoAcceptAppointOrderHalfHour() throws Exception;

  //根据customerId，VehicleId
  AppointOrderDTO generateAppointOrderByCustomerInfo(CustomerDTO customerInfo) throws Exception;

  //预约单的serviceDetail,Material生成施工单相关项目
  void generateCreateRepairOrderItem(RepairOrderDTO repairOrderDTO, AppointOrderDTO appointOrderDTO) throws Exception;

   //预约单的serviceDetail生成洗车单相关项目
  void generateCreateWashBeautyOrderItem(WashBeautyOrderDTO washBeautyOrderDTO, AppointOrderDTO appointOrderDTO) throws Exception;

  //根据故障中心生成预约单
  AppointOrderDTO generateAppointOrderByShopFaultCodeIds(Long shopId, String shopFaultInfoIds);

  public Result updateAppointOrderTime(AppointOrderDTO appointOrderDTO,Long appointOrderTime);

  void updateAppointOrder_status(AppointOrderDTO appointOrderDTO);

  AppointOrderDTO generateAppointOrderByCustomerId(Long customerId,Long shopId,String appUserNo) throws Exception;

  AppointOrderDTO generateAppointOrderByAppUserNo(Long shopId,String appUserNo) throws Exception;
  }
