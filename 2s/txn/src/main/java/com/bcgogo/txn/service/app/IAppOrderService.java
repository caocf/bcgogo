package com.bcgogo.txn.service.app;

import com.bcgogo.api.ApiOrderHistoryResponse;
import com.bcgogo.api.AppOrderDTO;
import com.bcgogo.api.AppServiceDTO;
import com.bcgogo.api.response.ApiPageListResponse;
import com.bcgogo.common.Result;
import com.bcgogo.enums.app.AppUserType;

/**
 * 手机端单据相关接口
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-25
 * Time: 下午1:48
 * To change this template use File | Settings | File Templates.
 */
public interface IAppOrderService {

  /**
   * 手机端用户取消服务
   * @param userNo
   * @param orderId
   * @return
   */
  public Result appUserCancelOrder(String userNo,Long orderId);


  /**
   * 手机端预约服务
   * @param appServiceDTO
   * @return
   */
  public Result appUserAppointOrder(AppServiceDTO appServiceDTO);

  Result saveWXAppointOrder(AppServiceDTO appServiceDTO) ;


  /**
   * 根据orderId和类型获取单据详情
   * @param orderId
   * @param type
   * @return
   */
  public AppOrderDTO getAppOrderByOrderId(Long orderId,String type);

  /**
   * 手机端服务查询
   */
  public ApiOrderHistoryResponse getAppOrderHistory(String userNo, String pageNo, String pageSize, String[] status,AppUserType appUserType);

  /**
   * 取预约单pending，accept，to_do_repair,维修单 repair_dispatcher,repair_done,REPAIR_SETTLED,洗车单WASH_SETTLED
   */
  public ApiPageListResponse<AppOrderDTO> getAllAppOrderHistory(String UserNo, int pageNo, int pageSize,AppUserType appUserType) throws Exception;

}
