package com.bcgogo.user.service;

import com.bcgogo.api.ApiMemberServiceDTO;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.txn.dto.MemberCardOrderDTO;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.Member;
import com.bcgogo.user.model.MemberCard;
import com.bcgogo.user.model.MemberCardService;
import com.bcgogo.user.model.MemberService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-3
 * Time: 下午1:37
 * To change this template use File | Settings | File Templates.
 */
public interface IMembersService {
  public MemberCardDTO getMemberCardDTOByCardName(Long shopId,String name);

  public MemberCardDTO saveOrUpdateMemberCard(MemberCardDTO memberCardDTO);

  public List<MemberCardDTO> getMemberCardDTOByShopIdAndNames(Long shopId,List<String> names);

  //查出正在使用的service的type
  public List<String> getMemberCardTypeByShopId(Long shopId);

  //查出全部的service
  public List<MemberCardDTO> getMemberCardDTOByShopId(Long shopId);

  //查出状态为ENABLED的service
  public List<MemberCardDTO> getMemberCardDTOByShopIdAndServiceStatus(Long shopId);

  public void deleteMemberCardServiceByMemberCardId(Long memberCardId);

  //查出全部的service
  public MemberCardDTO getMemberCardDTOByShopIdAndName(Long shopId,String name);

  //查出状态为ENABLED的service
  public MemberCardDTO getMemberCardDTOAndServiceByShopIdAndNameAndStatus(Long shopId,String name);

  //查出状态为ENABLED的service
  public List<MemberCardServiceDTO> getMemberCardServiceDTOByMemberCardIdAndStatus(Long memberCardId);

  //根据memberCardId和serviceId查出状态为ENABLED的service
  public MemberCardService getMemberCardServiceByMemberCardIdAndServiceIdAndStatus(Long memberCardId,Long serviceId);
  //查出全部的service
  public List<MemberCardServiceDTO> getMemberCardServiceDTOByMemberCardId(Long memberCardId);

  public void disabledMemberCardDTOById(Long shopId,Long id);

  public MemberCard getMemberCardById(Long shopId,Long id);

  //查出全部的service
  public MemberDTO getMemberByCustomerId(Long shopId,Long customerId) throws Exception;

  //查出未过期的和状态为ENABLED的service
  public MemberDTO getMemberDTOAndServiceByCustomerIdAndStatusAndDeadline(Long shopId,Long customerId) throws Exception;

  public List<MemberDTO> doWashCardToMemberInit() throws Exception;

  public void doMemberServiceInit(List<MemberDTO> memberDTOs) throws Exception;
  //根据memberId,serviceId查出状态为ENABLED的服务
  public MemberService getMemberServiceByMemberIdAndServiceIdAndStatus(Long MemberId,Long serviceId) throws Exception;

  //查出全部的service
  public MemberCardDTO getMemberCardDTOByCardId(Long shopId,Long MemberCardId);

  //查出状态为ENABLED的service
  public MemberCardDTO getMemberCardDTOByCardIdAndServiceStatus(Long shopId,Long MemberCardId);

  public void saveOrUpdateMember(MemberCardOrderDTO memberCardOrderDTO) throws Exception;

  public MemberService getMemberService(Long memberId,Long serviceId);
  //根据会员号获取会员信息，无视会员状态，取第一个
  public Member getMemberByShopIdAndMemberNo(Long shopId,String memberNo);
  //根据会员号获取有效的会员信息
  public Member getEnabledMemberByShopIdAndMemberNo(Long shopId,String memberNo);

  //查出店面所有在职员工
  public List<SalesManDTO> searchSaleManByShopIdAndKeyword(Long shopId, String keyWord);

  public SalesManDTO getSaleManDTOById(Long shopId,Long id);

  //根据会员ID查出未过期的和状态为ENABLED的service
  public List<MemberServiceDTO> getMemberServiceEnabledByMemberId(Long shopId,Long memberId) throws Exception;

  //创建新会员
  public MemberDTO createMember(MemberDTO memberDTO) throws BcgogoException;


  //更新会员
  public MemberDTO updateMember(MemberDTO memberDTO) throws Exception;

  //更新会员服务项目
  public void updateMemberService(MemberService memberService);

  /**
   * 根据会员id获得会员
   * @param memberId
   * @return
   */
  public Member getMemberById(Long memberId);

  public void saveOrUpdateMemberService(MemberServiceDTO memberServiceDTO) throws Exception;

  public MemberService getMemberServiceById(Long memberServiceId) throws Exception;

  /**
   * 根据会员id shop_id查出该会员服务,包括已经失效和次数为0的会员服务
   * @param shopId
   * @param memberId
   * @return
   * @throws Exception
   */
  public List<MemberServiceDTO> getAllMemberServiceByMemberId(Long shopId,Long memberId)throws Exception;

  /**
   * 根据shop_id memberId获得会员memberDto
   * @param shopId
   * @param memberId
   * @return
   */
  public MemberDTO getMemberDTOById(Long shopId,Long memberId);

  public boolean isMemberSwitchOn(Long shopId);

  /**
   * 根据会员所拥有的服务判断该会员的状态
   * @param memberDTO
   * @return MemberStatus ：有效 部分有效 失效
   */
  public MemberStatus getMemberStatusByMemberDTO(MemberDTO memberDTO);

  public int countMemberCardByShopId(Long shopId);

  public List<MemberService> getMemberServiceByServiceId(Long serviceId);

  public List<MemberCardService> getMemberCardServiceByServiceId(Long serviceId);

  public void saveOrUpdateMemberCardService(Long shopId,Long memberCardId,Long serviceId,MemberCardServiceDTO memberCardServiceDTO);

  Map<Long,MemberDTO> getMemberByCustomerIdSet(Long shopId, Set<Long> customerIds);

  List<Member> getMemberByShopId(Long id);

  void batchUpdateMemberPassword(List<Member> toUpdateMembers);

  public MemberServiceDTO saveMemberService(MemberServiceDTO memberServiceDTO);

  void invalidMember(Long shopId, Long id) throws Exception ;

  /**
   * 分页取数据
   * @param size 条数
   * @param page 第几页
   * @return
   */
  public List<MemberService> getMemberServiceForInitService(int size,int page);

  public int countMemberService();

  /**
   * 根据前台查询条件设置cusatoemrId
   * @param orderSearchConditionDTO
   */
  public void setCustomerIdByVehicleMemberName(OrderSearchConditionDTO orderSearchConditionDTO);

  //只查出会员信息
  public MemberDTO getMemberDTOByCustomerId(Long shopId,Long customerId) throws Exception;

  List<MemberService> getMemberServicesByMemberId(Long memberId);

  public List<MemberCardDTO> getEnableMemberCardDTOByShopId(Long shopId);

  public List<MemberService> getAllMemberService();

  /**
   * 根据会员卡号 带出会员（只有member基本信息）
   * @param memberNo
   * @param shopId
   * @return
   */
  public MemberDTO getEnabledMemberDTOByMemberNo(String memberNo,Long shopId);

  List<Long> getMemberCardShopIdsOfAppUser(String appUserNo);

  List<MemberDTO> getEnabledMemberLikeMemberNo(Long shopId,String memberNo);


  List<MemberDTO> getVehicleAvailableMemberDTOsByLicenceNo(Long shopId, String licenceNo);
}