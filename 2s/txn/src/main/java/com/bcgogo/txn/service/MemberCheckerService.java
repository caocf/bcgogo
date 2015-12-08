package com.bcgogo.txn.service;

import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.PasswordValidateStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.MemberServiceDTO;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.Member;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-7-20
 * Time: 下午7:15
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MemberCheckerService implements IMemberCheckerService {
  private static final Logger LOG = LoggerFactory.getLogger(MemberCheckerService.class);

  /**
   * 校验施工单是否含有会员消费相关信息
   *
   *
   * @param shopId
   * @param repairOrderDTO
   * @return
   */
  public String checkRepairOrderDTO(long shopId, RepairOrderDTO repairOrderDTO) throws Exception {
    String resultStr = "";
    //单据为空
    if (repairOrderDTO == null) {
      resultStr = MemberConstant.REPAIR_ORDER_NULL;
      return resultStr;
    }

    //单据不包含会员消费相关
    if (!this.containMemberAmount(repairOrderDTO) && !this.containMemberCountConsume(repairOrderDTO)) {
      resultStr = MemberConstant.MEMBER_VALIDATE_SUCCESS;
      return resultStr;
    }

    //单据包含会员相关信息 校验卡号和密码
    resultStr = this.checkMemberAndPassword(shopId, repairOrderDTO.getAccountMemberNo(), repairOrderDTO.getAccountMemberPassword());
    if (!StringUtil.isEmpty(resultStr)) {
      return resultStr;
    }

    //会员余额判断
    if (this.containMemberAmount(repairOrderDTO)) {
      resultStr = this.checkMemberBalance(shopId, repairOrderDTO.getAccountMemberNo(), repairOrderDTO.getMemberAmount());
      if (!StringUtil.isEmpty(resultStr)) {
        return resultStr;
      }
    }

    //会员计次划卡项目判断
    if (this.containMemberCountConsume(repairOrderDTO)) {
      resultStr = this.checkMemberService(shopId, repairOrderDTO);
      if (!StringUtil.isEmpty(resultStr)) {
        return resultStr;
      }
    }

    //校验通过 返回 success
    return MemberConstant.MEMBER_VALIDATE_SUCCESS;
  }

  @Override
  public String checkWashBeautyOrderDTO(long shopId, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    boolean flag = true;
    StringBuffer stringBuffer = new StringBuffer();
    IUserService userService = ServiceManager.getService(IUserService.class);
    //单据为空
    if (washBeautyOrderDTO == null) {
      stringBuffer.append(MemberConstant.WASH_BEAUTY_ORDER_NULL);
      return stringBuffer.toString();
    }

    if(null!= washBeautyOrderDTO.getCustomerId())
    {
      Customer customer = userService.getCustomerByCustomerId(washBeautyOrderDTO.getCustomerId(),shopId);
      if(null != customer && CustomerStatus.DISABLED.equals(customer.getStatus()))
      {
        stringBuffer.append(CustomerConstant.CUSTOMER_DISABLED_NO_SETTLE);
        return stringBuffer.toString();
      }
    }

    //消费券号唯一判断
    Boolean couponNoUsed = false;
    if(ArrayUtil.isNotEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())){
      for(WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
        if(StringUtils.isNotBlank(washBeautyOrderItemDTO.getCouponNo()) && ConsumeType.COUPON.equals(washBeautyOrderItemDTO.getConsumeTypeStr())) {
          couponNoUsed = ServiceManager.getService(ITxnService.class).validateCouponNoUsed(shopId,washBeautyOrderItemDTO.getCouponType(),washBeautyOrderItemDTO.getCouponNo());
          if(couponNoUsed) {
            return  "消费券" + washBeautyOrderItemDTO.getCouponNo() + "已经被使用，请重新输入";
          }
        }
      }
    }
    //单据不包含会员消费相关
    if (!this.containMemberAmountByWash(washBeautyOrderDTO) && !this.containMemberCountConsumeByWash(washBeautyOrderDTO)) {
      stringBuffer.append(MemberConstant.MEMBER_VALIDATE_SUCCESS);
      return stringBuffer.toString();
    }

    //单据包含会员相关信息 校验卡号和密码
    String resultStr = this.checkMemberAndPassword(shopId, washBeautyOrderDTO.getAccountMemberNo(), washBeautyOrderDTO.getAccountMemberPassword());
    if (!StringUtil.isEmpty(resultStr)) {
      stringBuffer.append(resultStr);
      return stringBuffer.toString();
    }

    //会员余额判断
    if (this.containMemberAmountByWash(washBeautyOrderDTO)) {
      resultStr = this.checkMemberBalance(shopId, washBeautyOrderDTO.getAccountMemberNo(), washBeautyOrderDTO.getMemberAmount());
      if (!StringUtil.isEmpty(resultStr)) {
        stringBuffer.append(resultStr);
        flag = false;
      }
    }

    //会员计次划卡项目判断
    if (this.containMemberCountConsumeByWash(washBeautyOrderDTO)) {
      resultStr = this.checkMemberServiceByWash(shopId, washBeautyOrderDTO);
      if (!StringUtil.isEmpty(resultStr)) {
        stringBuffer.append(resultStr);
        flag = false;
      }
    }

     if(washBeautyOrderDTO.getVechicleId()!=null&&washBeautyOrderDTO.getCustomerId()!=null){
       CustomerVehicleDTO customerVehicleDTO = userService.getCustomerVehicleDTOByVehicleIdAndCustomerId(washBeautyOrderDTO.getVechicleId(), washBeautyOrderDTO.getCustomerId());
       if(customerVehicleDTO==null){
         stringBuffer.append("客户信息异常，请刷新页面后重新操作。");
         flag = false;
       }
     }

    //校验通过 返回 success
    if (flag) {
      stringBuffer.append(MemberConstant.MEMBER_VALIDATE_SUCCESS);
      return stringBuffer.toString();
    }
    return stringBuffer.toString();
  }

  /**
   * 验证会员卡号和密码
   * @param shopId
   * @param memberNo 会员卡号
   * @param password 会员密码
   * @return 校验结果
   */
  public String checkMemberAndPassword(long shopId, String memberNo, String password) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    //保存校验结果
    StringBuffer stringBuffer = new StringBuffer();
    //使用储值卡进行消费 或者有计次划卡项目

    //会员号码为空
    if (StringUtil.isEmpty(memberNo) || StringUtil.isEmpty(memberNo.trim())) {
      return MemberConstant.MEMBER_NO_NEED;
    }

    //判断有无此会员号码
    Member member = membersService.getMemberByShopIdAndMemberNo(shopId, memberNo);
    if (member == null) {
      return MemberConstant.MEMBER_NOT_EXIST;
    }


    MemberDTO memberDTO = member.toDTO();

    //判断有无此会员号码
    if (memberDTO == null) {
      return MemberConstant.MEMBER_NOT_EXIST;
    }

    Customer customer = userService.getCustomerByCustomerId(member.getCustomerId(),member.getShopId());

    if(null != customer && CustomerStatus.DISABLED == customer.getStatus())
    {
      return MemberConstant.MEMBER_CUSTOMER_NOT_EXIST;
    }

    //判断会员的状态
    MemberStatus status = memberDTO.getStatus();
    if (MemberStatus.ENABLED != status) {
      return MemberConstant.MEMBER_INVALID;
    }

    //如果不验证密码 返回
    if(PasswordValidateStatus.UNVALIDATE == memberDTO.getPasswordStatus()) {
      return "";
    }

    //验证密码
    String passWord = memberDTO.getPassword();
    if(StringUtils.isBlank(passWord) && StringUtils.isBlank(password)){
      return "";
    }else if(StringUtils.isBlank(passWord) && StringUtils.isNotBlank(password)){
      return "";
    }else if(StringUtils.isNotBlank(passWord) && StringUtils.isBlank(password)){
      stringBuffer.append(MemberConstant.PASSWORD_NO_CORRECT);
    }else if(StringUtils.isNotBlank(passWord) && StringUtils.isNotBlank(password) && !passWord.equals(EncryptionUtil.encryptPassword(password, shopId))) {
      stringBuffer.append(MemberConstant.PASSWORD_NO_CORRECT);
    }

    return stringBuffer.toString();
  }

  /**
   * 判断会员余额是否不足
   * @param shopId
   * @param memberNo
   * @param memberAmount
   * @return
   */
  public String checkMemberBalance(long shopId, String memberNo, Double memberAmount) {

    IMembersService membersService = ServiceManager.getService(IMembersService.class);

    Member member = membersService.getMemberByShopIdAndMemberNo(shopId, memberNo);

    if (member.getBalance() == null || member.getBalance().doubleValue() <= 0) {
      return MemberConstant.MEMBER_BALANCE_NOT_ENOUGH;
    }

    if (member.getBalance().doubleValue() < memberAmount.doubleValue()) {
      return MemberConstant.MEMBER_BALANCE_NOT_ENOUGH;
    }
    return "";

  }

  /**
   * 判断该施工单下面的会员是否含有计次划卡服务
   * @param shopId
   * @param repairOrderDTO
   * @return
   * @throws Exception
   */
  public String checkMemberService(long shopId, RepairOrderDTO repairOrderDTO) throws Exception {

    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    //保存校验结果
    StringBuffer stringBuffer = new StringBuffer();

    //如果施工单没有施工项目
    if (ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      return stringBuffer.toString();
    }

    //已判断会员信息 这里不再判断
    String memberNo = repairOrderDTO.getAccountMemberNo();
    Member member = membersService.getMemberByShopIdAndMemberNo(shopId, memberNo);
    MemberDTO memberDTO = member.toDTO();

    //获得该会员下的计次划卡服务
    List<MemberServiceDTO> memberServiceDTOList = membersService.getAllMemberServiceByMemberId(shopId, member.getId());
    if(CollectionUtils.isNotEmpty(memberServiceDTOList)){
      memberDTO.setMemberServiceDTOs(memberServiceDTOList);
    }

    //遍历施工下的 每个 计次划卡项目 判断该会员的计次划卡服务是否含有
    for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
      if (StringUtil.isEmpty(repairOrderServiceDTO.getService())) {
        continue;
      }
      if (repairOrderServiceDTO.getConsumeType() == null) {
        continue;
      }
      if (ConsumeType.MONEY == repairOrderServiceDTO.getConsumeType()) {
        continue;
      }
      List<ServiceDTO> serviceDTOList = txnService.getServiceByServiceNameAndShopId(shopId, repairOrderServiceDTO.getService());
      if(CollectionUtils.isEmpty(serviceDTOList)){
        stringBuffer.append(repairOrderServiceDTO.getService()).append(",").append(MemberConstant.SHOP_NO_CONTAIN_SERVICE);
        continue;
      }

      Long serviceId = serviceDTOList.get(0).getId();
      //如果校验失败
      if (!MemberConstant.MEMBER_VALIDATE_SUCCESS.equals(this.memberContainService(memberDTO, serviceId, repairOrderDTO.getVechicle()))) {
        stringBuffer.append(repairOrderServiceDTO.getService()).append(",").append(this.memberContainService(memberDTO, serviceId, repairOrderDTO.getVechicle()));
      }
    }
    return stringBuffer.toString();
  }

  public String checkMemberServiceByWash(long shopId, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {

    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    //保存校验结果
    StringBuffer stringBuffer = new StringBuffer();

    //如果施工单没有施工项目
    if (washBeautyOrderDTO.getWashBeautyOrderItemDTOs() == null || washBeautyOrderDTO.getWashBeautyOrderItemDTOs().length <= 0) {
      return stringBuffer.toString();
    }

    //已判断会员信息 这里不再判断
    String memberNo = washBeautyOrderDTO.getAccountMemberNo();
    Member member = membersService.getMemberByShopIdAndMemberNo(shopId, memberNo);
    MemberDTO memberDTO = member.toDTO();

    //获得该会员下的计次划卡服务
    List<MemberServiceDTO> memberServiceDTOList = membersService.getMemberServiceEnabledByMemberId(shopId, member.getId());
    if (memberServiceDTOList != null && memberServiceDTOList.size() > 0) {
      memberDTO.setMemberServiceDTOs(memberServiceDTOList);
    }

		//遍历施工下的 每个 计次划卡项目 判断该会员的计次划卡服务是否含有
		for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
			if (washBeautyOrderItemDTO == null) {
				continue;
			}
			if (washBeautyOrderItemDTO.getConsumeTypeStr() == null) {
				continue;
			}
			if (washBeautyOrderItemDTO.getConsumeTypeStr().equals(ConsumeType.MONEY)) {
				continue;
			}
      if(washBeautyOrderItemDTO.getConsumeTypeStr().equals(ConsumeType.COUPON)) {
        continue;
      }
      Long serviceId = washBeautyOrderItemDTO.getServiceId();
      ServiceDTO serviceDTO = txnService.getServiceById(serviceId);
      //如果校验失败
      if (!MemberConstant.MEMBER_VALIDATE_SUCCESS.equals(this.memberContainService(memberDTO, serviceId, washBeautyOrderDTO.getLicenceNo()))) {
        stringBuffer.append(serviceDTO.getName()).append(",").append(this.memberContainService(memberDTO, serviceId, washBeautyOrderDTO.getLicenceNo()));
        break;
      }
    }
    return stringBuffer.toString();
  }


  /**
   * 根据施工服务内容 判断该会员是否含有此项服务
   *
   * @param memberDTO
   * @param serviceId
   * @return
   */
  public String memberContainService(MemberDTO memberDTO, Long serviceId, String vehicle) {
    if (memberDTO == null || serviceId == null) {
      return MemberConstant.MEMBER_NO_CONTAIN_SERVICE;
    }

    if(CollectionUtils.isEmpty(memberDTO.getMemberServiceDTOs())){
      return MemberConstant.MEMBER_NO_CONTAIN_SERVICE;
    }

    for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
      if (memberServiceDTO == null || memberServiceDTO.getId() == null) {
        continue;
      }

      if(!NumberUtil.isEqual(memberServiceDTO.getServiceId(),serviceId)) {
        continue;
      }

      return this.judgeMemberServiceAndServiceId(memberServiceDTO,serviceId,vehicle);
    }
    return MemberConstant.MEMBER_NO_CONTAIN_SERVICE;
  }


  /**
   * 判断施工单是否含有会员储值消费
   *
   * @param repairOrderDTO
   * @return
   */
  public boolean containMemberAmount(RepairOrderDTO repairOrderDTO) {

    if (repairOrderDTO == null) {
      return false;
    }
    if(repairOrderDTO.getMemberAmount() == null){
      return false;
    }

    if(NumberUtil.doubleVal(repairOrderDTO.getMemberAmount()) > 0){
      return true;
    }

    return false;
  }

  @Override
  public boolean containMemberAmountByWash(WashBeautyOrderDTO washBeautyOrderDTO) {

    if (washBeautyOrderDTO == null) {
      return false;
    }

    if (washBeautyOrderDTO.getMemberAmount() == null || washBeautyOrderDTO.getMemberAmount().doubleValue() <= 0) {
      return false;
    }

    if (washBeautyOrderDTO.getMemberAmount() != null && washBeautyOrderDTO.getMemberAmount().doubleValue() > 0) {
      return true;
    }
    return false;
  }

  /**
   * 判断该施工单下面是否含有计次划卡施工项目
   *
   * @param repairOrderDTO
   * @return
   */
  public boolean containMemberCountConsume(RepairOrderDTO repairOrderDTO) {

    if (repairOrderDTO == null) {
      return false;
    }

    if (repairOrderDTO.getServiceDTOs() == null || repairOrderDTO.getServiceDTOs().length <= 0) {
      return false;
    }

    for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
      if (repairOrderServiceDTO == null || StringUtil.isEmpty(repairOrderServiceDTO.getService())) {
        continue;
      }
      if (ConsumeType.TIMES == repairOrderServiceDTO.getConsumeType()) {
        return true;
      }
    }
    return false;
  }

  /**
   * 判断洗车美容单是否含有计次消费
   * @param washBeautyOrderDTO
   * @return
   */
  @Override
  public boolean containMemberCountConsumeByWash(WashBeautyOrderDTO washBeautyOrderDTO) {

    if (washBeautyOrderDTO == null) {
      return false;
    }

    if(ArrayUtils.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())){
      return false;
    }

	  for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
		  if (washBeautyOrderItemDTO == null) {
			  continue;
		  }
      if(ConsumeType.TIMES == washBeautyOrderItemDTO.getConsumeTypeStr()){
        return true;
      }
	  }
	  return false;
  }

  /**
   * 根据服务serviceId和车牌号vihicle,判断该项服务在会员memberService中是否有效
   * @param memberServiceDTO 会员所拥有的服务
   * @param serviceId 某个服务id
   * @param vehicle 车牌号
   * @return 校验结果
   */
  public String judgeMemberServiceAndServiceId(MemberServiceDTO memberServiceDTO,long serviceId,String vehicle) {

    //判断是否是限制车牌
    if (!StringUtil.isEmpty(memberServiceDTO.getVehicles()) && memberServiceDTO.getVehicles().indexOf(vehicle) == -1) {
      return "限车牌"+memberServiceDTO.getVehicles()+","+MemberConstant.VEHICLE_NO_IS_LIMIT;
    }

    //如果没有期限 没有次数 校验失败
    if (memberServiceDTO.getDeadline() == null || memberServiceDTO.getTimes() == null) {
      return MemberConstant.MEMBER_NO_CONTAIN_SERVICE;
    }

    //无限期
    if(memberServiceDTO.getDeadline().longValue() == -1) {

      //次数已经用完
      if (memberServiceDTO.getTimes().intValue() == 0) {
        return MemberConstant.MEMBER_SERVICE_OUT_COUNT;
      }
      //次数不为0 校验成功
      return MemberConstant.MEMBER_VALIDATE_SUCCESS;
    }

    //有限期 而且小于当前时间
    if (memberServiceDTO.getDeadline().longValue() < System.currentTimeMillis()) {
      return MemberConstant.MEMBER_SERVICE_OUT_DEADLINE;
    }

    //有限期 而且大于当前时间 次数小于1
    if(!NumberUtil.isEqualNegativeOne(memberServiceDTO.getTimes()) && memberServiceDTO.getTimes().intValue() < 1) {
      return MemberConstant.MEMBER_SERVICE_OUT_COUNT;
    }

    return MemberConstant.MEMBER_VALIDATE_SUCCESS;
  }

  /**
	 * 检查从前台传递过的 salesManDTO是否正确 并返回校验结果
	 * @param salesManDTO
	 * @return
	 */
  public String checkSalesManInfo(SalesManDTO salesManDTO,long shopId) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    StringBuffer checkResultStr = new StringBuffer();
    if (salesManDTO == null) {
      checkResultStr.append(SalesManConstant.SALES_MAN_INFO_EMPTY);
      return checkResultStr.toString();
    }

    //1.检查工号
    String salesManCode = salesManDTO.getSalesManCode();
    if (salesManCode == null) {
      salesManCode = "";
    }
    List<SalesManDTO> salesManDTOList = null;
    if (!StringUtil.isEmpty(salesManCode.trim())) {
      salesManDTOList = userService.getSalesManDTOByCodeOrName(salesManCode.trim(), null, shopId);
      if (salesManDTOList != null && salesManDTOList.size() > 1) {
        checkResultStr.append(SalesManConstant.SALES_MAN_CODE_EXIST);
        return checkResultStr.toString();
      }

      if (salesManDTOList != null && salesManDTOList.size() == 1) {
        SalesManDTO resultSalesManDTO = salesManDTOList.get(0);
        if (!resultSalesManDTO.getId().equals(salesManDTO.getId())) {
          checkResultStr.append(SalesManConstant.SALES_MAN_CODE_EXIST);
          return checkResultStr.toString();
        }
      }
    }

    //2.检查姓名
    String name = salesManDTO.getName();
    if (StringUtil.isEmpty(name.trim())) {
      checkResultStr.append(SalesManConstant.SALES_MAN_NAME_EMPTY);
      return checkResultStr.toString();
    }

    salesManDTOList = userService.getSalesManDTOByCodeOrName(null, name.trim(), shopId);
    if (salesManDTOList != null && salesManDTOList.size() > 1) {
      checkResultStr.append(SalesManConstant.SALES_MAN_NAME_EXIST);
      return checkResultStr.toString();
    }

    if (salesManDTOList != null && salesManDTOList.size() == 1) {
      SalesManDTO resultSalesManDTO = salesManDTOList.get(0);
      if (!resultSalesManDTO.getId().equals(salesManDTO.getId())) {
        checkResultStr.append(SalesManConstant.SALES_MAN_NAME_EXIST);
        return checkResultStr.toString();
      }
    }

    //3.检查入职日期
    String careerDateStr = salesManDTO.getCareerDateStr();
    try {
      Long careerDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, careerDateStr);
    } catch (ParseException e) {
      try {
        Long careerDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY2, salesManDTO.getCareerDateStr());
      } catch (ParseException e1) {
        checkResultStr.append(SalesManConstant.SALES_MAN_DATE_FORMAT_ERROR);
      }
    }
    //如果校验成功
    if (checkResultStr.length() == 0) {
      checkResultStr.append(SalesManConstant.SALES_MAN_INFO_VALIDATE_SUCCESS);
    }
    return checkResultStr.toString();
  }



   /**
   * 校验销售单是否含有会员消费相关信息
   *
   *
   * @param shopId
   * @param salesOrderDTO
   * @return
   */
  public String checkSalesOrderMemberInfo(long shopId, SalesOrderDTO salesOrderDTO) throws Exception {
    String resultStr = "";
    //单据为空
    if (salesOrderDTO == null) {
      resultStr = MemberConstant.REPAIR_ORDER_NULL;
      return resultStr;
    }

    if (NumberUtil.doubleVal(salesOrderDTO.getMemberAmount()) <= 0) {
      resultStr = MemberConstant.MEMBER_VALIDATE_SUCCESS;
      return resultStr;
    }

    //单据包含会员相关信息 校验卡号和密码
    resultStr = this.checkMemberAndPassword(shopId, salesOrderDTO.getAccountMemberNo(), salesOrderDTO.getAccountMemberPassword());
    if (!StringUtil.isEmpty(resultStr)) {
      return resultStr;
    }

    resultStr = this.checkMemberBalance(shopId, salesOrderDTO.getAccountMemberNo(), salesOrderDTO.getMemberAmount());
    if (!StringUtil.isEmpty(resultStr)) {
      return resultStr;
    }

    //校验通过 返回 success
    return MemberConstant.MEMBER_VALIDATE_SUCCESS;
  }



  @Override
  public String checkMemberBalance(long shopId,String memberNo,String memberPasswordStr,String memberAmountStr)throws Exception {

    String jsonStr = "";

    Double memberAmount = null;
    if (StringUtil.isEmpty(memberAmountStr)) {
      jsonStr = MemberConstant.MEMBER_VALIDATE_SUCCESS;
      return jsonStr;
    } else {
      memberAmount = Double.valueOf(memberAmountStr);
      if (NumberUtil.doubleVal(memberAmount) <= 0) {
        jsonStr = MemberConstant.MEMBER_VALIDATE_SUCCESS;
        return jsonStr;
      }
    }

    String checkResult = this.checkMemberAndPassword(shopId, memberNo, memberPasswordStr);
    if (StringUtils.isEmpty(checkResult)) {
      checkResult = this.checkMemberBalance(shopId, memberNo, memberAmount);
      if (StringUtil.isEmpty(checkResult)) {
        jsonStr = MemberConstant.MEMBER_VALIDATE_SUCCESS;
      } else {
        jsonStr = checkResult;
      }
    } else {
      jsonStr = checkResult;
    }

    return jsonStr;
  }


  public boolean checkDetailsArrearsInfo(double totalAmount,double payedAmount,double owedAmount,HttpServletRequest request) {

    double discount = NumberUtil.doubleValue(request.getParameter("discount"), 0);
    if (Math.abs(totalAmount - payedAmount - owedAmount - discount) > NumberUtil.doubleCompareResult) {
      return false;
    }

    double cashAmount = NumberUtil.doubleValue(request.getParameter("cashAmount"), 0);
    double bankCardAmount = NumberUtil.doubleValue(request.getParameter("bankAmount"), 0);
    double chequeAmount = NumberUtil.doubleValue(request.getParameter("bankCheckAmount"), 0);
    double memberAmount = NumberUtil.doubleValue(request.getParameter("memberAmount"), 0);
    double depositAmount = NumberUtil.doubleValue(request.getParameter("depositAmount"), 0);

    if (Math.abs(payedAmount - cashAmount - bankCardAmount - chequeAmount - memberAmount- depositAmount) > NumberUtil.doubleCompareResult) {
      return false;
    }

    return true;

  }


   /**
   * 销售单结算完成后 更改会员相关信息,
   * 在提交时已做校验 这里只做部分校验
   *
   * @param salesOrderDTO
   */
  @Override
  public void updateMemberInfo(SalesOrderDTO salesOrderDTO) {
    try {

      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);

      if (salesOrderDTO == null || salesOrderDTO.getShopId() == null) {
        return;
      }

      if(NumberUtil.doubleVal(salesOrderDTO.getMemberAmount()) <= 0){
        return;
      }

      String resultStr = "";
      Long shopId = null;
      try {
        shopId = salesOrderDTO.getShopId();
        resultStr = memberCheckerService.checkSalesOrderMemberInfo(shopId, salesOrderDTO);
      } catch (Exception e) {
        LOG.error("/memberCheckService.java");
        LOG.error("method=updateMemberInfo");
        LOG.error(e.getMessage(), e);
        LOG.error("销售单校验会员信息失败");
        return;
      }

      if (MemberConstant.MEMBER_VALIDATE_SUCCESS != resultStr) {
        LOG.error("/memberCheckService.do");
        LOG.error("method=updateMemberInfo");
        LOG.error("销售单校验会员信息失败");
        return;
      }

      Member member = membersService.getMemberByShopIdAndMemberNo(shopId, salesOrderDTO.getAccountMemberNo());
      if (member == null) {
        LOG.error("/memberCheckService.java");
        LOG.error("method=updateMemberInfo");
        LOG.error("shopId:" + salesOrderDTO);
        LOG.error(MemberConstant.MEMBER_NOT_EXIST + "," + MemberConstant.AJAX_SUBMIT_FAILURE);
        LOG.error(salesOrderDTO.toString());
        return;
      }

      MemberDTO memberDTO = member.toDTO();
      //如果使用会员储值金额进行结算 减去相应的金额

      memberDTO.setBalance(NumberUtil.toReserve(memberDTO.getBalance() - salesOrderDTO.getMemberAmount(), NumberUtil.MONEY_PRECISION));
      membersService.updateMember(memberDTO);
      member.fromDTO(memberDTO);

    } catch (Exception e) {
      LOG.error("/TxnService.java");
      LOG.error("method=updateMemberInfo");
      LOG.error("shopId:" + salesOrderDTO.getShopId());
      LOG.error(e.getMessage(), e);
      LOG.error(salesOrderDTO.toString());
      return;
    }
  }

}
