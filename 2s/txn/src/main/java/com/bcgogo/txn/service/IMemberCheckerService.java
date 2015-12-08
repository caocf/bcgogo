package com.bcgogo.txn.service;


import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.MemberServiceDTO;
import com.bcgogo.user.dto.SalesManDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-7-20
 * Time: 下午7:15
 * To change this template use File | Settings | File Templates.
 */

public interface IMemberCheckerService {

  /**
   * 校验施工单是否含有会员消费相关信息
   *
   *
   * @param shopId
   * @param repairOrderDTO
   * @return
   */
  public String checkRepairOrderDTO(long shopId, RepairOrderDTO repairOrderDTO) throws Exception;
  public String checkWashBeautyOrderDTO(long shopId, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception;

  /**
   * 验证会员卡号和密码
   *
   * @param shopId
   * @param memberNo 会员卡号
   * @param password 会员密码
   * @return 校验结果
   */
  public String checkMemberAndPassword(long shopId, String memberNo, String password) throws Exception;


  /**
   * 判断会员余额是否不足
   *
   * @param shopId
   * @param memberNo
   * @param memberAmount
   * @return
   */
  public String checkMemberBalance(long shopId, String memberNo, Double memberAmount);


  /**
   * 判断该施工单下面的会员是否含有计次划卡服务
   *
   * @param shopId
   * @param repairOrderDTO
   * @return
   * @throws Exception
   */
  public String checkMemberService(long shopId, RepairOrderDTO repairOrderDTO) throws Exception;

  /**
   * 根据施工服务内容 判断该会员是否含有此项服务
   *
   * @param memberDTO
   * @param serviceId
   * @return
   */
  public String memberContainService(MemberDTO memberDTO, Long serviceId, String vehicleNo);

  /**
   * 判断施工单是否含有会员储值消费
   *
   * @param repairOrderDTO
   * @return
   */
  public boolean containMemberAmount(RepairOrderDTO repairOrderDTO);

  /**
   * 判断该施工单下面是否含有计次划卡施工项目
   *
   * @param repairOrderDTO
   * @return
   */
  public boolean containMemberCountConsume(RepairOrderDTO repairOrderDTO);

  /**
   * 根据服务serviceId和车牌号vihicle,判断该项服务在会员memberService中是否有效
   * @param memberServiceDTO 会员所拥有的服务
   * @param serviceId 某个服务id
   * @param vehicle 车牌号
   * @return 校验结果
   */
  public String judgeMemberServiceAndServiceId(MemberServiceDTO memberServiceDTO,long serviceId,String vehicle);


  /**
   * 根据shop_id和员工信息校验是否正确
   * @param salesManDTO
   * @param shopId
   * @return
   */
  public String checkSalesManInfo(SalesManDTO salesManDTO,long shopId);

   /**
   * 判断洗车美容单是否包含 使用会员储值
   * @param washBeautyOrderDTO
   * @return
   */
  public boolean containMemberAmountByWash(WashBeautyOrderDTO washBeautyOrderDTO);

  /**
   * 判断洗车美容单是否含有计次消费
   * @param washBeautyOrderDTO
   * @return
   */
  public boolean containMemberCountConsumeByWash(WashBeautyOrderDTO washBeautyOrderDTO);

  /**
   * 校验销售单是否能进行会员结算
   * @param shopId
   * @param salesOrderDTO
   * @return
   * @throws Exception
   */
  public String checkSalesOrderMemberInfo(long shopId, SalesOrderDTO salesOrderDTO) throws Exception;

  /**
   * 校验会员储值是否不足
   * @param shopId
   * @param memberNo
   * @param memberPasswordStr
   * @param memberAmountStr
   * @return
   * @throws Exception
   */
  public String checkMemberBalance(long shopId,String memberNo,String memberPasswordStr,String memberAmountStr) throws Exception;

  /**
   * 校验欠款结算数据是否正确
   * @param totalAmount
   * @param payedAmount
   * @param owedAmount
   * @param request
   * @return
   */
  public boolean checkDetailsArrearsInfo(double totalAmount,double payedAmount,double owedAmount,HttpServletRequest request);


  /**
   * 销售单结算完成后 更改会员相关信息,
   * 在提交时已做校验 这里只做部分校验
   *
   * @param salesOrderDTO
   */
  public void updateMemberInfo(SalesOrderDTO salesOrderDTO);
}
