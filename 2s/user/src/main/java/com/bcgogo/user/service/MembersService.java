package com.bcgogo.user.service;

import com.bcgogo.api.ApiMemberServiceDTO;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopConfigService;
import com.bcgogo.enums.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.CarDTO;
import com.bcgogo.txn.dto.MemberCardOrderDTO;
import com.bcgogo.txn.dto.MemberCardOrderServiceDTO;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.*;
import com.bcgogo.utils.*;
import com.bcgogo.wx.WXArticleDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-3
 * Time: 下午1:40
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MembersService implements IMembersService {

  private static final Logger LOG = LoggerFactory.getLogger(MembersService.class);



  /**
   * 给单元测试用
   * @param memberDTO
   * @return
   */
  @Override
  public MemberDTO createMember(MemberDTO memberDTO)
  {
    if(null == memberDTO)
    {
      return null;
    }
    String key = "createMember_" + memberDTO.getCustomerId()==null?"":memberDTO.getCustomerId() + StringUtil.truncValue(memberDTO.getMemberNo());

    //设置会员状态为ENABLED
    memberDTO.setStatus(MemberStatus.ENABLED);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try{
      if(!BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, key))
        return null;
      Member member = new Member(memberDTO);

      writer.save(member);
      memberDTO.setId(member.getId());

      if(CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs()))
      {
        for(MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs())
        {
          memberServiceDTO.setMemberId(memberDTO.getId());
          MemberService memberService = new MemberService(memberServiceDTO);
          writer.save(memberService);
          memberServiceDTO.setId(memberService.getId());
        }
      }

      writer.commit(status);

      return memberDTO;
    }
    finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
    }
  }

  @Override
  public MemberCardDTO getMemberCardDTOByCardName(Long ShopId,String name)
  {
    UserWriter writer = userDaoManager.getWriter();
    try{
      MemberCard memberCard = writer.getCustomerCardByCardName(ShopId, name);
      if(null != memberCard)
      {
        return memberCard.toDTO();
      }
      return null;
    }catch(Exception ex){
      LOG.error("name",name);
      LOG.error("");
      LOG.error(ex.getMessage(),ex);
    }

    return null;
  }

  /**
   * 更新会员信息
   * @param memberDTO :会员信息
   * @return
   * @throws Exception
   */
  @Override
  public MemberDTO updateMember(MemberDTO memberDTO) throws Exception {

    if (memberDTO == null) {
      throw new BcgogoException(BcgogoExceptionType.NullException);
    }

    Long shopId = memberDTO.getShopId();
    if (shopId == null){
      throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
    }

    Long customerId = memberDTO.getCustomerId();
    if (customerId == null){
      throw new BcgogoException(BcgogoExceptionType.CustomerRecordNotFound);
    }

    UserWriter writer = userDaoManager.getWriter();
    Member member = null;

    String memberNo = memberDTO.getMemberNo();
    if(StringUtil.isEmpty(memberNo)){
      throw new BcgogoException(BcgogoExceptionType.MemberNoNotFound);
    }

    List<Member> memberList =new ArrayList<Member>();
    for(Member memberIndex:writer.getMemberDTOByMemberNo(shopId,memberNo)){
      if(memberIndex==null){
        continue;
      }
      if(!MemberStatus.DISABLED.equals(memberIndex.getStatus())){
        memberList.add(memberIndex);
      }
    }
    if(CollectionUtils.isEmpty(memberList)){
      throw new Exception(MemberConstant.MEMBER_NOT_EXIST + memberNo);
    }else if(memberList.size() > 1){
      throw new Exception(MemberConstant.TWO_OR_MORE_MEMBER_BY_ONE_MEMBER_NO + memberNo);
    }else if(memberList.size() == 1){
      member =  memberList.get(0);
      memberDTO.setId(member.getId());
      member.fromDTO(memberDTO);
    }

    if(member != null) {
      Object status = writer.begin();
      String key = "updateMember_" + memberDTO.getCustomerId() + "_" + memberDTO.getMemberNo();
      try {
        if(!BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, key))
          return null;
        writer.update(member);
        writer.commit(status);
        return member.toDTO();
      } finally {
        writer.rollback(status);
        BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
      }
    }



    return null ;
  }

  @Override
  public MemberCardDTO saveOrUpdateMemberCard(MemberCardDTO memberCardDTO)
  {
    if(null == memberCardDTO)
    {
      return null;
    }
    String key = "saveOrUpdateMemberCard_"+ StringUtil.truncValue(memberCardDTO.getName()) + "_"
      + (memberCardDTO.getShopId()==null?"":memberCardDTO.getShopId());

    //设置卡状态为ENABLED
    memberCardDTO.setStatus(MemberStatus.ENABLED);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try{
      if(!BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, key))
        return null;
      MemberCardDTO newMemberCardDTO = this.getMemberCardDTOByShopIdAndName(memberCardDTO.getShopId(),memberCardDTO.getName());

      if(null == newMemberCardDTO)
      {
        MemberCard memberCard = new MemberCard(memberCardDTO);
        writer.save(memberCard);
        memberCardDTO.setId(memberCard.getId());
      }
      else
      {
        MemberCard memberCard = writer.getById(MemberCard.class,newMemberCardDTO.getId());
        memberCard.copyFromMemberCardDTO(memberCardDTO);
        writer.update(memberCard);
        memberCardDTO.setId(memberCard.getId());
      }

      //保存service
      this.saveMemberCardService(writer,memberCardDTO);

      writer.commit(status);

      return memberCardDTO;
    }
    finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
    }
  }

  /**
   * 业务是先保存MemberCard然后再保存MemberCardService，所以这里先判断null和id
   * @param writer
   * @param memberCardDTO
   */
  public void saveMemberCardService(UserWriter writer,MemberCardDTO memberCardDTO)
  {
    if(null == memberCardDTO || null == memberCardDTO.getId())
    {
      return;
    }
    //获取该套餐卡上原来的服务
    List<MemberCardService> memberCardServices = writer.getMemberCardServiceByMemberCardId(memberCardDTO.getId());
    Map<Long,MemberCardServiceDTO> memberCardServiceDTOMap = new HashMap<Long, MemberCardServiceDTO>();
    if(CollectionUtils.isNotEmpty(memberCardDTO.getMemberCardServiceDTOs()))
    {
      memberCardServiceDTOMap = MemberCardServiceDTO.listToMap(memberCardDTO.getMemberCardServiceDTOs());
    }
    Map<Long,MemberCardService> memberCardServiceMap = new HashMap<Long, MemberCardService>();
    if(CollectionUtils.isNotEmpty(memberCardServices))
    {
      memberCardServiceMap = MemberCardService.listToMap(memberCardServices);
      //遍历套餐卡上原来的服务，如果在新设置的服务中有则更新，没有则把可用状态改为disabled
      for(MemberCardService memberCardService : memberCardServices)
      {
        if(null != memberCardServiceDTOMap.get(memberCardService.getServiceId()))
        {
          MemberCardServiceDTO memberCardServiceDTO = memberCardServiceDTOMap.get(memberCardService.getServiceId());
          memberCardService.setStatus(MemberStatus.ENABLED);
          memberCardService.setTerm(memberCardServiceDTO.getTerm());
          memberCardService.setTimes(memberCardServiceDTO.getTimes());
          memberCardService.setConsumeType(memberCardServiceDTO.getConsumeType());
          memberCardService.setMemberCardId(memberCardDTO.getId());
          writer.update(memberCardService);
          memberCardServiceDTO.setId(memberCardService.getId());
        }
        else
        {
          memberCardService.setStatus(MemberStatus.DISABLED);
          writer.update(memberCardService);
        }
      }
    }

    if(CollectionUtils.isEmpty(memberCardDTO.getMemberCardServiceDTOs()))
    {
      return;
    }

    //应为上面的for循环已经处理了一样的服务，所以新增的服务要添加进套餐上
    for(MemberCardServiceDTO memberCardServiceDTO : memberCardDTO.getMemberCardServiceDTOs())
    {
      if(null == memberCardServiceDTO.getServiceId())
      {
        continue;
      }
      if(null == memberCardServiceMap.get(memberCardServiceDTO.getServiceId()))
      {
        memberCardServiceDTO.setMemberCardId(memberCardDTO.getId());
        MemberCardService newMemberCardService = new MemberCardService(memberCardServiceDTO);
        newMemberCardService.setStatus(MemberStatus.ENABLED);
        writer.save(newMemberCardService);
        memberCardServiceDTO.setId(newMemberCardService.getId());
      }
    }
  }

  public void  saveOrUpdateMemberCardService(Long shopId,Long memberCardId,Long serviceId,MemberCardServiceDTO memberCardServiceDTO){
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try{
      MemberCardService memberCardService = writer.getMemberCardService(memberCardId,serviceId);
      if(null==memberCardService){
        //save operate
        memberCardService=new MemberCardService();
        memberCardService.setStatus(MemberStatus.ENABLED);
        memberCardService.setTerm(memberCardServiceDTO.getTerm());
        memberCardService.setTimes(memberCardServiceDTO.getTimes());
        memberCardService.setConsumeType(memberCardServiceDTO.getConsumeType());
        memberCardService.setServiceId(serviceId);
        memberCardService.setMemberCardId(memberCardId);
        writer.save(memberCardService);
        writer.commit(status);
        return ;
      }
      if(MemberStatus.ENABLED == memberCardService.getStatus()){
        return ;
      }
      else if(MemberStatus.DISABLED == memberCardService.getStatus())
      {
        memberCardService.setStatus(MemberStatus.ENABLED);
        writer.update(memberCardService);
        writer.commit(status);
      }
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void deleteMemberCardServiceByMemberCardId(Long memberCardId)
  {
    if(null != memberCardId)
      return;
    String key = "deleteMemberCardServiceByMemberCardId_" + memberCardId;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try{
      if(!BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, key))
        return;
      writer.deleteMemberCardServiceByMemberCardId(memberCardId);
      writer.commit(status);
    }finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
    }

  }

  @Override
  public List<MemberCardDTO> getMemberCardDTOByShopIdAndNames(Long shopId,List<String> names)
  {
    UserWriter writer = userDaoManager.getWriter();

    List<MemberCard> memberCards = writer.getMemberCardByShopIdAndNames(shopId, names);

    List<MemberCardDTO> memberCardDTOs = null;

    if(null == memberCards || memberCards.size()==0)
    {
      return null;
    }

    memberCardDTOs = new ArrayList<MemberCardDTO>();
    for(MemberCard memberCard : memberCards)
    {
      MemberCardDTO memberCardDTO = memberCard.toDTO();
      List<MemberCardServiceDTO> memberCardServiceDTOs= this.getMemberCardServiceDTOByMemberCardId(memberCard.getId());
      if(null != memberCardServiceDTOs && memberCardServiceDTOs.size() > 0)
      {
        memberCardDTO.setMemberCardServiceDTOs(memberCardServiceDTOs);
      }
      memberCardDTOs.add(memberCardDTO);
    }

    return memberCardDTOs;
  }

  @Override
  public List<String> getMemberCardTypeByShopId(Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getMemberCardTypeByShopId(shopId);
  }

  @Override
  public List<MemberCardDTO> getMemberCardDTOByShopId(Long shopId)
  {
    UserWriter writer = userDaoManager.getWriter();

    List<MemberCard> memberCards = writer.getMemberCardByShopId(shopId);

    List<MemberCardDTO> memberCardDTOs = null;

    if(null == memberCards || memberCards.size()==0)
    {
      return null;
    }

    memberCardDTOs = new ArrayList<MemberCardDTO>();
    for(MemberCard memberCard : memberCards)
    {
      MemberCardDTO memberCardDTO = memberCard.toDTO();
      List<MemberCardServiceDTO> memberCardServiceDTOs= this.getMemberCardServiceDTOByMemberCardId(memberCard.getId());
      if(null != memberCardServiceDTOs && memberCardServiceDTOs.size() > 0)
      {
        memberCardDTO.setMemberCardServiceDTOs(memberCardServiceDTOs);
      }
      memberCardDTOs.add(memberCardDTO);
    }

    return memberCardDTOs;
  }

  /**
   * 查出MemberCardDTO的集合，里面的service状态为ENABLED
   * @param shopId
   * @return
   */
  @Override
  public List<MemberCardDTO> getMemberCardDTOByShopIdAndServiceStatus(Long shopId)
  {
    UserWriter writer = userDaoManager.getWriter();

    List<MemberCard> memberCards = writer.getMemberCardByShopId(shopId);

    List<MemberCardDTO> memberCardDTOs = null;

    if(null == memberCards || memberCards.size()==0)
    {
      return null;
    }

    memberCardDTOs = new ArrayList<MemberCardDTO>();
    for(MemberCard memberCard : memberCards)
    {
      MemberCardDTO memberCardDTO = memberCard.toDTO();
      List<MemberCardServiceDTO> memberCardServiceDTOs= this.getMemberCardServiceDTOByMemberCardIdAndStatus(memberCard.getId());
      if(null != memberCardServiceDTOs && memberCardServiceDTOs.size() > 0)
      {
        memberCardDTO.setMemberCardServiceDTOs(memberCardServiceDTOs);
      }
      memberCardDTOs.add(memberCardDTO);
    }

    return memberCardDTOs;
  }

  @Override
  public MemberCardDTO getMemberCardDTOByShopIdAndName(Long shopId,String name)
  {
    UserWriter writer = userDaoManager.getWriter();

    MemberCard memberCard = writer.getMemberCardByShopIdAndName(shopId, name);

    if(null == memberCard)
    {
      return null;
    }

    MemberCardDTO memberCardDTO = memberCard.toDTO();

    List<MemberCardServiceDTO> memberCardServiceDTOs = this.getMemberCardServiceDTOByMemberCardId(memberCard.getId());

    if(null != memberCardServiceDTOs && memberCardServiceDTOs.size()>0)
    {
      memberCardDTO.setMemberCardServiceDTOs(memberCardServiceDTOs);
    }

    return memberCardDTO;
  }

  @Override
  public MemberCardDTO getMemberCardDTOAndServiceByShopIdAndNameAndStatus(Long shopId,String name)
  {
    UserWriter writer = userDaoManager.getWriter();

    MemberCard memberCard = writer.getMemberCardByShopIdAndName(shopId, name);

    if(null == memberCard)
    {
      return null;
    }

    MemberCardDTO memberCardDTO = memberCard.toDTO();

    List<MemberCardServiceDTO> memberCardServiceDTOs = this.getMemberCardServiceDTOByMemberCardIdAndStatus(memberCard.getId());

    if(null != memberCardServiceDTOs && memberCardServiceDTOs.size()>0)
    {
      memberCardDTO.setMemberCardServiceDTOs(memberCardServiceDTOs);
    }

    return memberCardDTO;
  }

  public List<MemberCardServiceDTO> getMemberCardServiceDTOByMemberCardId(Long memberCardId)
  {
    UserWriter writer = userDaoManager.getWriter();

    List<MemberCardService> memberCardServices = writer.getMemberCardServiceByMemberCardId(memberCardId);

    List<MemberCardServiceDTO> memberCardServiceDTOs = null;

    if(null == memberCardServices || memberCardServices.size()==0)
    {
      return null;
    }

    memberCardServiceDTOs = new ArrayList<MemberCardServiceDTO>();

    for(MemberCardService memberCardService : memberCardServices)
    {
      memberCardServiceDTOs.add(memberCardService.toDTO());
    }

    return memberCardServiceDTOs;
  }

  /**
   * 查出状态为ENABLED的service
   * @param memberCardId
   * @return
   */
  public List<MemberCardServiceDTO> getMemberCardServiceDTOByMemberCardIdAndStatus(Long memberCardId)
  {
    UserWriter writer = userDaoManager.getWriter();

    List<MemberCardService> memberCardServices = writer.getMemberCardServiceDTOByMemberCardIdAndStatus(memberCardId);

    List<MemberCardServiceDTO> memberCardServiceDTOs = null;

    if(null == memberCardServices || memberCardServices.size()==0)
    {
      return null;
    }

    memberCardServiceDTOs = new ArrayList<MemberCardServiceDTO>();

    for(MemberCardService memberCardService : memberCardServices)
    {
      memberCardServiceDTOs.add(memberCardService.toDTO());
    }

    return memberCardServiceDTOs;
  }
  @Override
  public MemberCardService getMemberCardServiceByMemberCardIdAndServiceIdAndStatus(Long memberCardId,Long serviceId)
  {
    UserWriter writer = userDaoManager.getWriter();

    if(null == memberCardId || null == serviceId)
    {
      return null;
    }

    return writer.getMemberCardServiceByMemberCardIdAndServiceIdAndStatus(memberCardId,serviceId);
  }

  public MemberCard getMemberCardById(Long shopId,Long id)
  {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getMemberCardById(shopId,id);
  }


  @Override
  public void disabledMemberCardDTOById(Long shopId,Long id)
  {
    UserWriter writer = userDaoManager.getWriter();

    Object status = writer.begin();

    try{
      MemberCard memberCard = writer.getMemberCardById(shopId,id);

      memberCard.setStatus(MemberStatus.DISABLED);

      writer.update(memberCard);

      writer.commit(status);
    }finally {
      writer.rollback(status);
    }

  }
  @Override
  public MemberDTO getMemberByCustomerId(Long shopId, Long customerId) throws Exception {
    if (null == shopId || null == customerId) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    Member member = writer.getMemberByCustomerId(shopId, customerId);
    if (member != null) {
      MemberDTO memberDTO = member.toDTO();
      if (null == member.getJoinDate()) {
        member.setJoinDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,
          DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, System.currentTimeMillis())));
      }
      memberDTO.setDateKeep(DateUtil.dateDifference(member.getJoinDate(), System.currentTimeMillis(), true));
      List<MemberService> memberServices = writer.getMemberServicesByMemberId(member.getId());
      if (memberServices.size() > 0) {
        List<MemberServiceDTO> memberServiceDTOs = new ArrayList<MemberServiceDTO>();
        Long serviceDeadLine = memberServices.get(0).getDeadline();
        for (MemberService memberService : memberServices) {
          if (Long.valueOf("-1").equals(serviceDeadLine)) {
            serviceDeadLine = memberService.getDeadline();
          } else if (!Long.valueOf("-1").equals(serviceDeadLine) && memberService.getDeadline() < serviceDeadLine && memberService.getDeadline() != -1) {
            serviceDeadLine = memberService.getDeadline();
          }

          MemberServiceDTO memberServiceDTO = memberService.toDTO();
          memberServiceDTOs.add(memberServiceDTO);
        }

        this.sortMemberServiceDTOs(memberServiceDTOs);
        memberDTO.setMemberServiceDTOs(memberServiceDTOs);

        if (Long.valueOf("-1").equals(serviceDeadLine)) {
          memberDTO.setServiceDeadLineStr(DeadlineLimitType.UNLIMITED.getType());
        } else {
          memberDTO.setServiceDeadLineStr(DateUtil.dateLongToStr(serviceDeadLine, DateUtil.DATE_STRING_FORMAT_DAY));
        }
      }
      return memberDTO;
    }
    return null;
  }

  @Override
  public MemberDTO getMemberDTOAndServiceByCustomerIdAndStatusAndDeadline(Long shopId, Long customerId) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    Member member = writer.getMemberByCustomerId(shopId,customerId);
    if(member != null){
      MemberDTO memberDTO = member.toDTO();
      int year = DateUtil.fieldDifference(member.getJoinDate(),System.currentTimeMillis(), Calendar.YEAR);
      int month = DateUtil.fieldDifference(member.getJoinDate(),System.currentTimeMillis(),Calendar.MONTH);
      String dateKeep = "";
      if(year > 0){
        dateKeep = year + "年";
      }
      if(month > 0){
        dateKeep += month + "月";
      }
      memberDTO.setDateKeep(dateKeep);
      List<MemberService> memberServices = writer.getMemberServicesByMemberIdAndStatusAndDeadline(member.getId());
      if(memberServices.size() > 0){
        List<MemberServiceDTO> memberServiceDTOs = new ArrayList<MemberServiceDTO>();
        Long serviceDeadLine = memberServices.get(0).getDeadline();
        for(MemberService memberService : memberServices){
          if(memberService.getDeadline() < serviceDeadLine){
            serviceDeadLine = memberService.getDeadline();
          }
          MemberServiceDTO memberServiceDTO = memberService.toDTO();
          memberServiceDTOs.add(memberServiceDTO);
        }
        memberDTO.setMemberServiceDTOs(memberServiceDTOs);
        memberDTO.setServiceDeadLineStr(DateUtil.dateLongToStr(serviceDeadLine,DateUtil.DATE_STRING_FORMAT_DAY));
      }
      return memberDTO;
    }
    return null;
  }

  @Override
  public MemberService getMemberServiceByMemberIdAndServiceIdAndStatus(Long memberId,Long serviceId) throws Exception
  {
    if(null == memberId || null == serviceId)
    {
      return null;
    }

    UserWriter writer = userDaoManager.getWriter();

    return writer.getMemberServiceByMemberIdAndServiceIdAndStatus(memberId, serviceId);
  }

  public void deleteMemberCardById(Long memberCardId)
  {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try{
      if(null != memberCardId)
      {
        writer.delete(MemberCard.class,memberCardId);
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }

  }

  @Override
  public MemberCardDTO getMemberCardDTOByCardId(Long shopId,Long memberCardId)
  {
    UserWriter writer = userDaoManager.getWriter();
    if(null == memberCardId)
    {
      return null;
    }
    MemberCard memberCard = writer.getMemberCardById(shopId,memberCardId);
    if(null == memberCard)
    {
      return null;
    }
    List<MemberCardServiceDTO> memberCardServiceDTOs= this.getMemberCardServiceDTOByMemberCardId(memberCard.getId());

    MemberCardDTO memberCardDTO = memberCard.toDTO();
    if(null != memberCardServiceDTOs)
    {
      memberCardDTO.setMemberCardServiceDTOs(memberCardServiceDTOs);
    }

    return memberCardDTO;
  }

  @Override
  public MemberCardDTO getMemberCardDTOByCardIdAndServiceStatus(Long shopId,Long memberCardId)
  {
    UserWriter writer = userDaoManager.getWriter();
    if(null == memberCardId)
    {
      return null;
    }
    MemberCard memberCard = writer.getMemberCardById(shopId,memberCardId);
    if(null == memberCard)
    {
      return null;
    }
    List<MemberCardServiceDTO> memberCardServiceDTOs= this.getMemberCardServiceDTOByMemberCardIdAndStatus(memberCard.getId());

    MemberCardDTO memberCardDTO = memberCard.toDTO();
    if(null != memberCardServiceDTOs)
    {
      memberCardDTO.setMemberCardServiceDTOs(memberCardServiceDTOs);
    }

    return memberCardDTO;
  }

  @Override
  public List<MemberDTO> doWashCardToMemberInit() throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try{
      List<CustomerCard> customerCards = writer.getAllWashCard();
      if(customerCards.size() > 0){
        List<MemberDTO> memberDTOs = new ArrayList<MemberDTO>();
        for(CustomerCard customerCard : customerCards){
          Member member = writer.getMemberByCustomerId(customerCard.getShopId(),customerCard.getCustomerId());
          if(member == null){
            List<CarDTO> carDTOs = userService.getVehiclesByCustomerId(customerCard.getShopId(),customerCard.getCustomerId());
            member = new Member();
            member.setShopId(customerCard.getShopId());
            member.setType("洗车卡");
            member.setBalance(0d);
            if(carDTOs.size() > 0){
              member.setMemberNo(carDTOs.get(0).getLicenceNo());
            }
            member.setJoinDate(System.currentTimeMillis());
            member.setCustomerId(customerCard.getCustomerId());
            member.setPasswordStatus(PasswordValidateStatus.UNVALIDATE);
          }
          member.setStatus(MemberStatus.ENABLED);
          writer.saveOrUpdate(member);
          memberDTOs.add(member.toDTO());
        }
        writer.commit(status);
        return memberDTOs;
      }
      return null;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void doMemberServiceInit(List<MemberDTO> memberDTOs){
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try{
      if(memberDTOs != null){
        List<CustomerCard> customerCards = writer.getAllWashCard();
        for(CustomerCard customerCard : customerCards){
          for(MemberDTO memberDTO : memberDTOs){
            if(customerCard.getCustomerId().equals(memberDTO.getCustomerId())){
              Member member = new Member(memberDTO);
              List<MemberService> memberServices = writer.getMemberServicesByMemberId(member.getId());
              MemberService memberService = null;
              int times = 0;
              if(customerCard.getWashRemain() != null){
                times = customerCard.getWashRemain().intValue();
              }
              if(memberServices.size() > 0){
                for(MemberService service : memberServices){
                  if(service.getServiceId().equals(memberDTO.getWashServiceId())){
                    memberService = service;
                    memberService.setTimes(memberService.getTimes() + times);
                    break;
                  }
                }
              }
              if(memberService == null){
                memberService = new MemberService();
                memberService.setMemberId(member.getId());
                memberService.setServiceId(memberDTO.getWashServiceId());
                memberService.setTimes(times);
              }
              memberService.setDeadline(-1l);
              memberService.setStatus(MemberStatus.ENABLED);
              memberService.setRemindStatus(UserConstant.Status.ACTIVITY);
              writer.saveOrUpdate(memberService);
              break;
            }
          }
        }
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateMember(MemberCardOrderDTO memberCardOrderDTO) throws Exception {
    if (memberCardOrderDTO == null) {
      return;
    }
    String key = "saveOrUpdateMember_" + StringUtil.truncValue(memberCardOrderDTO.getShopId().toString())+
      StringUtil.truncValue(null == memberCardOrderDTO.getCustomerId()?"":memberCardOrderDTO.getCustomerId().toString())+
      StringUtil.truncValue(memberCardOrderDTO.getCustomerName()) +
      StringUtil.truncValue(memberCardOrderDTO.getMemberCardName());

    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    UserWriter writer = userDaoManager.getWriter();
    Map<Long, MemberCardOrderServiceDTO> memberCardOrderServiceDTOMap = new HashMap<Long, MemberCardOrderServiceDTO>();
    Map<Long, MemberService> memberServiceMap = new HashMap<Long, MemberService>();
    memberCardOrderServiceDTOMap = MemberCardOrderServiceDTO.listToMap(memberCardOrderDTO.getMemberCardOrderServiceDTOs());
    Object status = writer.begin();
    try {

      if (!BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, key)) {
        return;
      }

      if (null != memberCardOrderDTO && null != memberCardOrderDTO.getMemberDTO()) {
        Member member = writer.getMemberByCustomerId(memberCardOrderDTO.getShopId(),memberCardOrderDTO.getCustomerId());
        if (null != member) {
          member.setType(memberCardOrderDTO.getMemberType());
          member.setMemberNo(memberCardOrderDTO.getMemberDTO().getMemberNo());

          member.setPasswordStatus(null == memberCardOrderDTO.getMemberDTO().getPasswordStatus() ? PasswordValidateStatus.UNVALIDATE :
            memberCardOrderDTO.getMemberDTO().getPasswordStatus());

          if (!TxnConstant.PWD_DEFAULT_RENDER_STR.equals(memberCardOrderDTO.getMemberDTO().getPassword())) {
            if (StringUtils.isNotBlank(memberCardOrderDTO.getMemberDTO().getPassword())) {
              member.setPassword(EncryptionUtil.encryptPassword(memberCardOrderDTO.getMemberDTO().getPassword(), memberCardOrderDTO.getShopId()));
            } else {
              member.setPassword(null);
            }
          }

          member.setBalance(memberCardOrderDTO.getMemberDTO().getBalance());
          member.setStatus(MemberStatus.ENABLED);
          member.setMemberDiscount(memberCardOrderDTO.getMemberDiscount());
          writer.update(member);
        } else {
          member = new Member();
          member.setPasswordStatus(null == memberCardOrderDTO.getMemberDTO().getPasswordStatus() ? PasswordValidateStatus.UNVALIDATE :
            memberCardOrderDTO.getMemberDTO().getPasswordStatus());
          member.setShopId(memberCardOrderDTO.getShopId());
          member.setCustomerId(memberCardOrderDTO.getCustomerId());
          member.setType(memberCardOrderDTO.getMemberType());
          member.setStatus(MemberStatus.ENABLED);
          member.setDeadline(memberCardOrderDTO.getMemberDTO().getDeadline());
          member.setJoinDate(memberCardOrderDTO.getEditDate());
          member.setBalance(memberCardOrderDTO.getMemberDTO().getBalance());
          member.setMemberNo(memberCardOrderDTO.getMemberDTO().getMemberNo());
          member.setPassword(memberCardOrderDTO.getMemberDTO().getPassword());
          if (!TxnConstant.PWD_DEFAULT_RENDER_STR.equals(memberCardOrderDTO.getMemberDTO().getPassword())) {
            if (StringUtils.isNotBlank(memberCardOrderDTO.getMemberDTO().getPassword())) {
              member.setPassword(EncryptionUtil.encryptPassword(memberCardOrderDTO.getMemberDTO().getPassword(), memberCardOrderDTO.getShopId()));
            } else {
              member.setPassword(null);
            }
          }
          member.setAccumulatePoints(memberCardOrderDTO.getMemberDTO().getAccumulatePoints());
          member.setServiceDiscount(memberCardOrderDTO.getMemberDTO().getServiceDiscount());
          member.setMaterialDiscount(memberCardOrderDTO.getMemberDTO().getMaterialDiscount());
          member.setMemberDiscount(memberCardOrderDTO.getMemberDiscount());
          writer.save(member);
          memberCardOrderDTO.getMemberDTO().setId(member.getId());
        }

        //取得是全部的member_service
        List<MemberService> memberServices = writer.getMemberServicesByMemberId(memberCardOrderDTO.getMemberDTO().getId());
        memberServiceMap = MemberService.listToMap(memberServices);

        if (CollectionUtils.isNotEmpty(memberServices)) {
          for (MemberService memberService : memberServices) {
            if (null != memberCardOrderServiceDTOMap.get(memberService.getServiceId())) {
              MemberCardOrderServiceDTO memberCardOrderServiceDTO = memberCardOrderServiceDTOMap.get(memberService.getServiceId());
              memberService.setStatus(MemberStatus.ENABLED);
              memberService.setVehicles(memberCardOrderServiceDTO.getVehicles());
              if (Integer.valueOf(TimesStatus.UNLIMITED.getStatus()).intValue() == memberCardOrderServiceDTO.getTimesStatus()) {
                memberService.setTimes(-1);
              } else {
                memberService.setTimes(memberCardOrderServiceDTO.getBalanceTimes());
              }
              if (1 == memberCardOrderServiceDTO.getDeadlineStatus()) {
                memberService.setDeadline(Long.valueOf("-1"));
              } else {
                memberService.setDeadline(memberCardOrderServiceDTO.getDeadline());
              }
              writer.update(memberService);
            } else {
              memberService.setTimes(0);
              memberService.setStatus(MemberStatus.DISABLED);
              writer.update(memberService);
            }
          }
        }

        if (null != memberCardOrderDTO && CollectionUtils.isNotEmpty(memberCardOrderDTO.getMemberCardOrderServiceDTOs())) {
          for (MemberCardOrderServiceDTO memberCardOrderServiceDTO : memberCardOrderDTO.getMemberCardOrderServiceDTOs()) {
            if (null != memberCardOrderServiceDTO.getServiceId() && null == memberServiceMap.get(memberCardOrderServiceDTO.getServiceId())) {
              MemberService memberService = new MemberService();
              memberService.setStatus(MemberStatus.ENABLED);
              memberService.setRemindStatus(UserConstant.Status.ACTIVITY);
              if (Integer.valueOf(TimesStatus.UNLIMITED.getStatus()).intValue() == memberCardOrderServiceDTO.getTimesStatus()) {
                memberService.setTimes(-1);
              } else {
                memberService.setTimes(memberCardOrderServiceDTO.getBalanceTimes());
              }
              if (1 == memberCardOrderServiceDTO.getDeadlineStatus()) {
                memberService.setDeadline(Long.valueOf("-1"));
              } else {
                memberService.setDeadline(memberCardOrderServiceDTO.getDeadline());
              }
              memberService.setMemberId(memberCardOrderDTO.getMemberDTO().getId());
              memberService.setServiceId(memberCardOrderServiceDTO.getServiceId());
              memberService.setVehicles(memberCardOrderServiceDTO.getVehicles());
              writer.save(memberService);
            }
          }
        }
      }

      customerService.updateCustomerAndCustomerRecord(writer,memberCardOrderDTO);
      writer.commit(status);
    }finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
    }
  }

  @Override
  public MemberService getMemberService(Long memberId,Long serviceId)
  {
    UserWriter writer = userDaoManager.getWriter();

    return writer.getMemberService(memberId,serviceId);

  }

  @Override
  public Member getMemberByShopIdAndMemberNo(Long shopId,String memberNo)
  {
    UserWriter writer = userDaoManager.getWriter();

    return writer.RFGetMemberByShopIdAndMemberNo(shopId,memberNo);
  }

  @Override
  public Member getEnabledMemberByShopIdAndMemberNo(Long shopId, String memberNo) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.RFGetMemberByShopIdAndMemberNo(shopId,memberNo);
  }

  @Override
  public List<SalesManDTO> searchSaleManByShopIdAndKeyword(Long shopId, String keyWord)
  {
    UserWriter writer = userDaoManager.getWriter();

    List<SalesMan> salesMans =  writer.getSaleManByShopIdAndOnJob(shopId, keyWord);

    List<SalesManDTO> salesManDTOs = null;
    if(CollectionUtils.isNotEmpty(salesMans))
    {
      salesManDTOs = new ArrayList<SalesManDTO>();
      for(SalesMan salesMan : salesMans)
      {
        SalesManDTO salesManDTO = salesMan.toDTO();
        salesManDTOs.add(salesManDTO);
      }
    }

    return salesManDTOs;
  }

  @Override
  public SalesManDTO getSaleManDTOById(Long shopId,Long id)
  {
    if(null == id)
    {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();

    SalesMan salesMan = writer.getSaleManDTOById(shopId,id);

    if(null == salesMan)
    {
      return null;
    }
    return salesMan.toDTO();
  }

  /**
   * 根据会员id shop_id查出会员的服务项目 只查出有效的施工项目
   * @param shopId
   * @param memberId
   * @return
   * @throws Exception
   */
  @Override
  public List<MemberServiceDTO> getMemberServiceEnabledByMemberId(Long shopId, Long memberId) throws Exception {

    UserWriter writer = userDaoManager.getWriter();

    List<MemberService> memberServices = writer.getMemberServicesByMemberId(memberId);

    if (CollectionUtils.isEmpty(memberServices)) {
      return null;
    }
    List<MemberServiceDTO> memberServiceDTOs = new ArrayList<MemberServiceDTO>();
    for (MemberService memberService : memberServices) {
      if (memberService == null) {
        continue;
      }
      MemberServiceDTO memberServiceDTO = memberService.toDTO();
      memberServiceDTOs.add(memberServiceDTO);
    }
    this.sortMemberServiceDTOs(memberServiceDTOs);
    return memberServiceDTOs;
  }

  /**
   * 更新会员服务项目
   * @param memberService
   */
  @Override
  public void updateMemberService(MemberService memberService) {
    UserWriter writer = userDaoManager.getWriter();
    if (memberService == null) {
      return;
    }
    String key = "updateMemberService_" + (memberService.getMemberId()==null?"":memberService.getMemberId())
      + (memberService.getServiceId()==null?"":memberService.getServiceId());
    Object status = writer.begin();
    try {
      if(!BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, key))
        return;
      if (memberService.getId() != null) {
        writer.update(memberService);
      } else {
        writer.save(memberService);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
    }
  }

  /**
   * 根据会员id获得会员信息
   * @param memberId
   * @return
   */
  @Override
  public Member getMemberById(Long memberId) {
    UserWriter writer = userDaoManager.getWriter();
    Member member = writer.getById(Member.class, memberId);
    return member;
  }

  @Override
  public void saveOrUpdateMemberService(MemberServiceDTO memberServiceDTO) throws Exception {
    if(memberServiceDTO == null){
      return;
    }
    UserWriter writer = userDaoManager.getWriter();
    String key = "saveOrUpdateMemberService_" + (memberServiceDTO.getMemberId()==null?"":memberServiceDTO.getMemberId())
      + (memberServiceDTO.getServiceId()==null?"":memberServiceDTO.getServiceId());
    Object status = writer.begin();

    try{
      if(!BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, key))
        return;
      MemberService memberService = writer.getById(MemberService.class,memberServiceDTO.getId());
      memberService.fromDTO(memberServiceDTO);
      writer.saveOrUpdate(memberService);
      writer.commit(status);
    } finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
    }
  }

  @Override
  public MemberService getMemberServiceById(Long memberServiceId) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    MemberService memberService = writer.getById(MemberService.class,memberServiceId);
    return memberService;
  }



  /**
   * 根据会员id shop_id查出该会员服务,包括已经失效和次数为0的会员服务
   * @param shopId
   * @param memberId
   * @return
   * @throws Exception
   */
  @Override
  public List<MemberServiceDTO> getAllMemberServiceByMemberId(Long shopId, Long memberId) throws Exception {

    UserWriter writer = userDaoManager.getWriter();

    List<MemberService> memberServices = writer.getAllMemberServiceByMemberId(memberId);

    if (CollectionUtils.isEmpty(memberServices)) {
      return null;
    }
    List<MemberServiceDTO> memberServiceDTOs = new ArrayList<MemberServiceDTO>();
    for (MemberService memberService : memberServices) {
      if (memberService == null) {
        continue;
      }
      MemberServiceDTO memberServiceDTO = memberService.toDTO();
      memberServiceDTOs.add(memberServiceDTO);
    }

    this.sortMemberServiceDTOs(memberServiceDTOs);

    return memberServiceDTOs;
  }

  @Override
  public MemberDTO getMemberDTOById(Long shopId,Long memberId)
  {
    UserWriter writer = userDaoManager.getWriter();
    Member member = writer.getMemberDTOById(shopId, memberId);
    if(member != null){
      MemberDTO memberDTO = member.toDTO();
      int year = DateUtil.fieldDifference(member.getJoinDate(),System.currentTimeMillis(), Calendar.YEAR);
      int month = DateUtil.fieldDifference(member.getJoinDate(),System.currentTimeMillis(),Calendar.MONTH);
      month = month - year * 12;
      String dateKeep = "";
      if(year > 0){
        dateKeep = year + "年";
      }
      if(month > 0){
        dateKeep += month + "月";
      }
      memberDTO.setDateKeep(dateKeep);
      List<MemberService> memberServices = writer.getMemberServicesByMemberId(memberId);
      if(memberServices.size() > 0){
        List<MemberServiceDTO> memberServiceDTOs = new ArrayList<MemberServiceDTO>();
        Long serviceDeadLine = memberServices.get(0).getDeadline();
        for(MemberService memberService : memberServices){
          if(memberService.getDeadline() < serviceDeadLine && memberService.getDeadline() != -1){
            serviceDeadLine = memberService.getDeadline();
          }
          MemberServiceDTO memberServiceDTO = memberService.toDTO();
          memberServiceDTOs.add(memberServiceDTO);
        }
        memberDTO.setMemberServiceDTOs(memberServiceDTOs);
        memberDTO.setServiceDeadLineStr(DateUtil.dateLongToStr(serviceDeadLine,DateUtil.DATE_STRING_FORMAT_DAY));
      }
      return memberDTO;
    }
    return null;
  }


  /**
   * 判断会员工能开还是关
   * @param shopId
   * @return
   */
  @Override
  public boolean isMemberSwitchOn(Long shopId)
  {

    IConfigService configService = ServiceManager.getService(IConfigService.class);

    String value = configService.getConfig(MemberConstant.MEMBER_CONFIG_SWITCH_NAME, Long.valueOf(-1));

    if(!TxnConstant.MEMBER_CONFIG_SWITCH.equals(value))
    {
      return false;
    }

    IShopConfigService shopConfigService = ServiceManager.getService(IShopConfigService.class);

    ShopConfigStatus switchStatus = shopConfigService.getConfigSwitchStatus(ShopConfigScene.MEMBER,shopId);

    if(null != switchStatus && ShopConfigStatus.ON == switchStatus)
    {
      return true;
    }

    return false;
  }

  /**
   * 根据会员所拥有的服务判断该会员的状态
   * @param memberDTO
   * @return MemberStatus ：有效 部分有效 失效
   */
  @Override
  public MemberStatus getMemberStatusByMemberDTO(MemberDTO memberDTO) {
    boolean isHasBalance = (NumberUtil.doubleVal(memberDTO.getBalance()) > 0 ? true : false);
    //返回值 会员状态 默认为失效
    MemberStatus memberStatus = MemberStatus.DISABLED;
    if (memberDTO == null || (CollectionUtils.isEmpty(memberDTO.getMemberServiceDTOs()) && !isHasBalance)) {
      return memberStatus;
    }

    List<MemberServiceDTO> memberServiceDTOList = memberDTO.getMemberServiceDTOs();
    int invalidServiceNum = 0; //失效服务的个数

    if (CollectionUtils.isEmpty(memberServiceDTOList)) {
      if (isHasBalance) {
        return MemberStatus.PARTENABLED;
      } else {
        return MemberStatus.DISABLED;
      }
    }

    for (MemberServiceDTO memberServiceDTO : memberServiceDTOList) {
      if (memberServiceDTO.getDeadline() == null || memberServiceDTO.getTimes() == null) {
        invalidServiceNum++;
        continue;
      }
      if (memberServiceDTO.getTimes() == 0) {
        invalidServiceNum++;
        continue;
      }
      if (memberServiceDTO.getDeadline().intValue() == TxnConstant.UNLIMITED) {
        continue;
      }
      if (memberServiceDTO.getDeadline() < System.currentTimeMillis()) {
        invalidServiceNum++;
        continue;
      }
    }

    if (invalidServiceNum == (memberServiceDTOList.size()) && !isHasBalance) {
      memberStatus = MemberStatus.DISABLED;
    } else if (invalidServiceNum == 0 && isHasBalance) {
      memberStatus = MemberStatus.ENABLED;
    } else {
      memberStatus = MemberStatus.PARTENABLED;
    }
    return memberStatus;
  }

  public int countMemberCardByShopId(Long shopId)
  {
    UserWriter writer = userDaoManager.getWriter();

    return writer.countMemberCardByShopId(shopId);
  }

  /**
   * 根据serviceId获取状态为ENABLED的MemberService集合
   * @param serviceId
   * @return
   */
  @Override
  public List<MemberService> getMemberServiceByServiceId(Long serviceId)
  {
    if(null == serviceId)
    {
      return null;
    }

    UserWriter writer = userDaoManager.getWriter();

    return writer.getMemberServiceByServiceId(serviceId);

  }

  /**
   * 根据serviceId获取状态为ENABLED的MemberCardService集合
   * @param serviceId
   * @return
   */
  @Override
  public List<MemberCardService> getMemberCardServiceByServiceId(Long serviceId)
  {
    if(null == serviceId)
    {
      return null;
    }

    UserWriter writer = userDaoManager.getWriter();

    return writer.getMemberCardServiceByServiceId(serviceId);
  }

  public Map<Long, MemberDTO> getMemberByCustomerIdSet(Long shopId, Set<Long> customerIds) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getMemberByCustomerIdSet(shopId, customerIds);
  }

  @Override
  public List<Member> getMemberByShopId(Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getMemberByShopId(shopId);
  }

  @Override
  public void batchUpdateMemberPassword(List<Member> members) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Long memberId = 0L;
    try{
      for(Member member : members){
        memberId = member.getId();
        writer.updateMemberPassword(member);
      }
      writer.commit(status);
    } catch(Exception e){
      LOG.error("更新ID为 {} 的member密码时出错.", memberId);
      LOG.error(e.getMessage(), e);
      writer.rollback(status);
    }finally{
      writer.rollback(status);
    }
  }

  /**
   * 导入数据的时候用到，先判断这个会员的这个服务有没有，没有就导入，有就不导入
   * 这是为了避免第一次导入失败但是已经导入了一部分，第二次又导入第一次已经导入进去的
   * 数据，
   * @param memberServiceDTO
   * @return
   */
  @Override
  public MemberServiceDTO saveMemberService(MemberServiceDTO memberServiceDTO)
  {
    if(null == memberServiceDTO || null == memberServiceDTO.getServiceId() || null == memberServiceDTO.getMemberId())
    {
      LOG.debug("saveMemberService");
      LOG.debug("memberService",memberServiceDTO);
      return null;
    }

    if(null == memberServiceDTO.getTimes() || null == memberServiceDTO.getDeadline())
    {
      LOG.error("memberService",memberServiceDTO);
      return null;
    }

    MemberService checkMemberService = getMemberService(memberServiceDTO.getMemberId(),memberServiceDTO.getServiceId());

    if(null != checkMemberService)
    {
      LOG.debug("memberId{}",checkMemberService.getMemberId());
      LOG.debug("此服务上次已经导入，这次不会导入{}",memberServiceDTO.getServiceName());
      LOG.debug("checkMemberService{}",checkMemberService);
      return null;
    }

    UserWriter writer = userDaoManager.getWriter();

    Object status = writer.begin();

    try{
      MemberService memberService = new MemberService(memberServiceDTO);
      writer.save(memberService);
      writer.commit(status);
      memberServiceDTO.setId(memberService.getId());
      return memberServiceDTO;
    }finally {
      writer.rollback(status);
    }
  }

  /**
   * 退卡时，此卡储值金额置0，所有服务剩余次数置0.
   * @param shopId
   * @param id
   */
  @Override
  public void invalidMember(Long shopId, Long id) throws Exception {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Long memberId = 0L;
    try{
      Member member = writer.getById(Member.class, id);
      if(member==null){
        LOG.error("ID为{}的member不存在！", id);
      }
      member.setBalance(0d);
      member.setStatus(MemberStatus.DISABLED);
      member.setMemberDiscount(null);
      writer.update(member);
      List<MemberService> memberServices = writer.getMemberServicesByMemberId(member.getId());
      if(CollectionUtils.isNotEmpty(memberServices)){
        for(MemberService memberService: memberServices){
          memberService.setTimes(0);
          memberService.setDeadline(0l);
//          memberService.setStatus(MemberStatus.DISABLED);
          writer.update(memberService);
        }
      }
      writer.commit(status);
    } catch(Exception e){
      LOG.error("退卡更新member相关信息时出错, memberID: {}.", memberId);
      LOG.error(e.getMessage(), e);
      writer.rollback(status);
      throw new Exception(e);
    }finally{
      writer.rollback(status);
    }
  }

  public List<MemberService> getMemberServicesByMemberId(Long memberId){
    if(memberId==null){
      return null;
    }
    UserWriter userWriter = userDaoManager.getWriter();
    return userWriter.getMemberServicesByMemberId(memberId);
  }

  @Autowired
  private UserDaoManager userDaoManager;


  private  void  sortMemberServiceDTOs(List memberServiceDTOs)
  {

    //会员消费项目按照：1）有次数未过期 2）有次数已过期 3）无次数 顺序排序
    Collections.sort(memberServiceDTOs, new Comparator<MemberServiceDTO>() {
      @Override
      public int compare(MemberServiceDTO o1, MemberServiceDTO o2) {
        if((o1.getTimes() != 0 && o2.getTimes() != 0) || (o1.getTimes() == 0 && o2.getTimes() == 0))
        {
          if(o1.getDeadline()>o2.getDeadline() )
          {
            if(o2.getDeadline()==-1)
              return 1;
            else
              return -1;
          }
          else
          {
            if(o1.getDeadline()==-1)
              return -1;
            else
              return 1;
          }
        }
        else
        {
          if(o1.getTimes() ==0 )
            return 1;
          else
            return -1;
        }
      }
    });

  }

  @Override
  public List<MemberService> getMemberServiceForInitService(int size,int page)
  {
    UserWriter writer = userDaoManager.getWriter();

    return writer.getMemberServiceForInitService(size,page);
  }

  @Override
  public int countMemberService()
  {
    UserWriter writer = userDaoManager.getWriter();

    return writer.countMemberService();
  }

  public void setCustomerIdByVehicleMemberName(OrderSearchConditionDTO orderSearchConditionDTO) {
    if (orderSearchConditionDTO == null) {
      return;
    }
    IUserService userService = ServiceManager.getService(IUserService.class);

    if (StringUtil.isEmpty(orderSearchConditionDTO.getCustomerName()) && StringUtil.isEmpty(orderSearchConditionDTO.getVehicle())
      && StringUtil.isEmpty(orderSearchConditionDTO.getAccountMemberNo())) {
      orderSearchConditionDTO.setCustomerOrSupplierIds(null);
      return;
    }

    if(StringUtil.isNotEmpty(orderSearchConditionDTO.getCustomerName())){
      orderSearchConditionDTO.setCustomerName(orderSearchConditionDTO.getCustomerName().trim());
    }
    if(StringUtil.isNotEmpty(orderSearchConditionDTO.getVehicle())){
      orderSearchConditionDTO.setVehicle(orderSearchConditionDTO.getVehicle().trim());
    }
    if(StringUtil.isNotEmpty(orderSearchConditionDTO.getAccountMemberNo())){
      orderSearchConditionDTO.setAccountMemberNo(orderSearchConditionDTO.getAccountMemberNo().trim());
    }

    if (StringUtil.isEmpty(orderSearchConditionDTO.getCustomerName())) {
      if (StringUtil.isEmpty(orderSearchConditionDTO.getVehicle())) {
        Member member = this.getMemberByShopIdAndMemberNo(orderSearchConditionDTO.getShopId(), orderSearchConditionDTO.getAccountMemberNo());
        if (member != null) {
          Long[] ids = new Long[1];
          ids[0] = member.getId();
          orderSearchConditionDTO.setMemberIds(ids);
          String[] idStr = new String[1];
          idStr[0] = member.getCustomerId().toString();
          orderSearchConditionDTO.setCustomerOrSupplierIds(idStr);
        }else{
          orderSearchConditionDTO.setCustomerOrSupplierIds(null);
        }
        return;
      } else if (StringUtil.isEmpty(orderSearchConditionDTO.getAccountMemberNo())) {
        List<Long> customerIds = userService.getCustomerIdByLicenceNo(orderSearchConditionDTO.getShopId(), orderSearchConditionDTO.getVehicle());
        if (CollectionUtils.isEmpty(customerIds)) {
          orderSearchConditionDTO.setCustomerOrSupplierIds(null);
        } else {
          String[] ids = new String[customerIds.size()];
          for (int index = 0; index < customerIds.size(); index++) {
            ids[index] = customerIds.get(index).toString();
          }
          orderSearchConditionDTO.setCustomerOrSupplierIds(ids);
        }
      }else {
        Member member = this.getMemberByShopIdAndMemberNo(orderSearchConditionDTO.getShopId(), orderSearchConditionDTO.getAccountMemberNo());
        if (member != null) {
          List<Long> customerIds = userService.getCustomerIdByLicenceNo(orderSearchConditionDTO.getShopId(), orderSearchConditionDTO.getVehicle());
          if(CollectionUtils.isEmpty(customerIds)){
            orderSearchConditionDTO.setCustomerOrSupplierIds(null);
          }else if (customerIds.contains(member.getCustomerId())) {
            Long[] ids = new Long[1];
            ids[0] = member.getId();
            orderSearchConditionDTO.setMemberIds(ids);
            String[] idStr = new String[1];
            idStr[0] = member.getCustomerId().toString();
            orderSearchConditionDTO.setCustomerOrSupplierIds(idStr);
          }
        }
      }
    } else {
      List<CustomerDTO> customerDTOList = userService.getAllCustomerByName(orderSearchConditionDTO.getShopId(), orderSearchConditionDTO.getCustomerName());
      if (CollectionUtils.isEmpty(customerDTOList)) {
        orderSearchConditionDTO.setCustomerOrSupplierIds(null);
        return;
      }

      Set<String> nameCustomerIds = new HashSet<String>();
      for(CustomerDTO customerDTO : customerDTOList){
        nameCustomerIds.add(customerDTO.getId().toString());
      }

      if (StringUtil.isNotEmpty(orderSearchConditionDTO.getVehicle()) && StringUtil.isNotEmpty(orderSearchConditionDTO.getAccountMemberNo())) {
        Member member = this.getMemberByShopIdAndMemberNo(orderSearchConditionDTO.getShopId(), orderSearchConditionDTO.getAccountMemberNo());
        if (member != null) {
          List<Long> customerIds = userService.getCustomerIdByLicenceNo(orderSearchConditionDTO.getShopId(), orderSearchConditionDTO.getVehicle());
          if (customerIds.contains(member.getCustomerId()) && nameCustomerIds.contains(member.getCustomerId().toString())) {
            Long[] ids = new Long[1];
            ids[0] = member.getId();
            orderSearchConditionDTO.setMemberIds(ids);
            String[] idStr = new String[1];
            idStr[0] = member.getCustomerId().toString();
            orderSearchConditionDTO.setCustomerOrSupplierIds(idStr);
          }
        }
      } else if (StringUtil.isEmpty(orderSearchConditionDTO.getVehicle()) && StringUtil.isNotEmpty(orderSearchConditionDTO.getAccountMemberNo())) {
        Member member = this.getMemberByShopIdAndMemberNo(orderSearchConditionDTO.getShopId(), orderSearchConditionDTO.getAccountMemberNo());
        if (member != null && nameCustomerIds.contains(member.getCustomerId().toString())) {
          Long[] ids = new Long[1];
          ids[0] = member.getId();
          orderSearchConditionDTO.setMemberIds(ids);
          String[] idStr = new String[1];
          idStr[0] = member.getCustomerId().toString();
          orderSearchConditionDTO.setCustomerOrSupplierIds(idStr);
        }
      }else if (StringUtil.isNotEmpty(orderSearchConditionDTO.getVehicle()) && StringUtil.isEmpty(orderSearchConditionDTO.getAccountMemberNo())) {
        List<Long> customerIds = userService.getCustomerIdByLicenceNo(orderSearchConditionDTO.getShopId(), orderSearchConditionDTO.getVehicle());
        if (CollectionUtils.isNotEmpty(customerIds)) {
          Set<String> customerIdSet = new HashSet<String>();
          for (Long customerId : customerIds) {
            if (nameCustomerIds.contains(customerId.toString())) {
              customerIdSet.add(customerId.toString());
            }
          }
          orderSearchConditionDTO.setCustomerOrSupplierIds(customerIdSet.toArray(new String[customerIdSet.size()]));
        }
      }else{
        orderSearchConditionDTO.setCustomerOrSupplierIds(nameCustomerIds.toArray(new String[nameCustomerIds.size()]));
      }
    }

  }


  @Override
  public MemberDTO getMemberDTOByCustomerId(Long shopId, Long customerId) throws Exception {
    if (null == shopId || null == customerId) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    Member member = writer.getMemberByCustomerId(shopId, customerId);
    if (member != null) {
      MemberDTO memberDTO = member.toDTO();
      return memberDTO;
    }
    return null;
  }

  @Override
  public List<MemberCardDTO> getEnableMemberCardDTOByShopId(Long shopId)
  {
    if(null == shopId)
    {
      return null;
    }

    UserWriter writer = userDaoManager.getWriter();
    List<MemberCard> memberCardList = writer.getEnableMemberCardDTOByShopId(shopId);

    if(CollectionUtils.isEmpty(memberCardList))
    {
      return null;
    }

    List<MemberCardDTO> memberCardDTOList = new ArrayList<MemberCardDTO>();

    for(MemberCard memberCard : memberCardList)
    {
      memberCardDTOList.add(memberCard.toDTO());
    }

    return memberCardDTOList;
  }

  public List<MemberService> getAllMemberService(){
    UserWriter writer = userDaoManager.getWriter();
    return writer.getAllMemberService();
  }

  @Override
  public MemberDTO getEnabledMemberDTOByMemberNo(String memberNo, Long shopId) {
    if(StringUtils.isBlank(memberNo) || shopId == null){
      return null;
    }
    Member member = userDaoManager.getWriter().getEnabledMemberByShopIdAndMemberNo(shopId,memberNo);
    if(member != null){
      return member.toDTO();
    }
    return null;
  }

  @Override
  public List<Long> getMemberCardShopIdsOfAppUser(String appUserNo) {
    UserWriter writer =  userDaoManager.getWriter();
    return writer.getMemberCardShopIdsOfAppUser(appUserNo);
  }

  @Override
  public List<MemberDTO> getEnabledMemberLikeMemberNo(Long shopId, String memberNo) {
    UserWriter writer = userDaoManager.getWriter();
    List<Member> members = writer.getEnabledMemberLikeMemberNo(shopId, memberNo);
    List<MemberDTO> memberDTOs = new ArrayList<MemberDTO>();
    if (CollectionUtil.isEmpty(members)) {
      return memberDTOs;
    }

    for (Member member : members) {
      memberDTOs.add(member.toDTO());
    }
    return memberDTOs;
  }

  @Override
  public List<MemberDTO> getVehicleAvailableMemberDTOsByLicenceNo(Long shopId,String licenceNo){
    UserWriter writer = userDaoManager.getWriter();
    List<MemberService> memberServiceList = writer.getVehicleAvailableMemberServicesByLicenceNo(shopId, licenceNo);
    List<MemberDTO> memberDTOs = new ArrayList<MemberDTO>();
    if (CollectionUtil.isEmpty(memberServiceList)) {
      Set<Long> memberIdSet = new HashSet<Long>();
      Set<Long> serviceIdSet = new HashSet<Long>();
      for(MemberService memberService:memberServiceList){
        memberIdSet.add(memberService.getMemberId());
        serviceIdSet.add(memberService.getServiceId());
      }

      List<Member> memberList = writer.getMembersByMemberIds(shopId,memberIdSet);
      if(CollectionUtils.isNotEmpty(memberList)){
        for(Member member:memberList){
          MemberDTO memberDTO = member.toDTO();
          List<MemberServiceDTO> memberServiceDTOList = new ArrayList<MemberServiceDTO>();
          for(MemberService memberService:memberServiceList){
            if(memberDTO.getId().equals(memberService.getMemberId()))
              memberServiceDTOList.add(memberService.toDTO());
          }
          memberDTO.setMemberServiceDTOs(memberServiceDTOList);
          memberDTOs.add(memberDTO);
        }
      }
    }

    return memberDTOs;
  }
}