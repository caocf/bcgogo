package com.bcgogo.notification.invitationCode;

import com.bcgogo.common.Result;
import com.bcgogo.enums.notification.InvitationCodeType;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.notification.dto.InvitationCodeDTO;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-17
 * Time: 下午8:01
 * 邀请码 生产者
 */
public interface InvitationCodeGeneratorClient {

  /**
   * 创建邀请码 （批量）
   *
   * @param invitationCodeType 邀请类型
   * @param inviterType        邀请者类型       必须
   * @param inviterId          邀请者id         必须
   * @param inviteeType        被邀请者类型     必须
   * @param inviteeIds         List<Long> 被邀请者id       必须
   * @param expirationTime     失效时间   非必须  默认10天
   * @param checkingDuplicated 是否需要检查重复   必须
   * @throws Exception
   */
  List<InvitationCodeDTO> createInvitationCodes(InvitationCodeType invitationCodeType, OperatorType inviterType, Long inviterId, OperatorType inviteeType, List<Long> inviteeIds, Long expirationTime, boolean checkingDuplicated) throws Exception;


  /**
   * 创建邀请码
   *
   * @param invitationCodeType 邀请类型
   * @param inviterType        邀请者类型       必须
   * @param inviterId          邀请者id         必须
   * @param inviteeType        被邀请者类型     必须
   * @param inviteeId          被邀请者id       必须
   * @param expirationTime     失效时间   非必须  默认10天
   * @throws Exception
   */
  String createInvitationCode(InvitationCodeType invitationCodeType, OperatorType inviterType, Long inviterId, OperatorType inviteeType, Long inviteeId, Long expirationTime) throws Exception;

  /**
   * 查找有效邀请码
   *
   * @param code 邀请码值
   */
  InvitationCodeDTO findEffectiveInvitationCodeByCode(String code);

  /**
   * 查找邀请码 (有效，过期，已使用)
   *
   * @param code 邀请码值
   * @return
   */
  InvitationCodeDTO findInvitationCodeByCode(String code);

  /**
   * 更新邀请码状态为已被使用
   *
   * @param code 邀请码值
   */
  void updateInvitationCodeToUsed(String code);

  /**
   * 回收失效邀请码
   *
   * @param pageSize 每页大小
   */
  void recycleInvitationCode(Integer pageSize);

  //邀请码校验
  InvitationCodeDTO validateInvitationCode(String invitationCode, Result result, Map<String, Object> resultData);
}
