package com.bcgogo.txn.service;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.*;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.service.IBaseProductService;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.sms.ISendSmsService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.Member;
import com.bcgogo.user.model.MemberService;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.service.ConsumingService;
import com.bcgogo.user.service.IConsumingService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-6-18
 * Time: 上午10:38
 * To change this template use File | Settings | File Templates.
 */
@org.springframework.stereotype.Service
public class WashBeautyService implements IWashBeautyService {
  private static final Logger LOG = LoggerFactory.getLogger(WashBeautyService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  private IConsumingService consumingService;
  private ITxnService txnService;
  public IConsumingService getConsumingService() {
    if(consumingService==null) {
      consumingService = ServiceManager.getService(IConsumingService.class);
    }
    return consumingService;
  }
  public ITxnService getTxnService() {
    return txnService == null ? ServiceManager.getService(ITxnService.class) : txnService;
  }
  @Override
  public void initWashBeautyService(TxnWriter writer, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
    //批量保存营业分类
    Set<String> categoryNames = washBeautyOrderDTO.getCategoryNames();
    Set<Long> serviceIds = washBeautyOrderDTO.getServiceIds();
    Map<String, CategoryDTO> categoryDTOMap = rfiTxnService.getAndSaveCategoryDTOByNames(washBeautyOrderDTO.getShopId(),
        CategoryType.BUSINESS_CLASSIFICATION, categoryNames.toArray(new String[categoryNames.size()]));
    Map<Long, Service> serviceMap = rfiTxnService.getServiceMapByIds(washBeautyOrderDTO.getShopId(), serviceIds);
    //批量处理serviceHistory
    Map<Long, ServiceHistoryDTO> serviceHistoryDTOMap = serviceHistoryService.batchGetOrSaveServiceHistoryByServiceIds(
        writer, washBeautyOrderDTO.getShopId(), serviceIds);

    for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
      if (null == washBeautyOrderItemDTO || null == washBeautyOrderItemDTO.getServiceId()) {
        continue;
      }
      Long serviceId = washBeautyOrderItemDTO.getServiceId();
      Service service = serviceMap.get(serviceId);
      if (service != null){
        if (ServiceStatus.DISABLED.equals(service.getStatus())) {
          service.setStatus(ServiceStatus.ENABLED);
        }
        service.setUseTimes(NumberUtil.longValue(service.getUseTimes()) + 1);
        writer.update(service);
        washBeautyOrderItemDTO.setServiceName(service.getName());
      }

      ServiceHistoryDTO serviceHistoryDTO = serviceHistoryDTOMap.get(serviceId);
      washBeautyOrderItemDTO.setServiceHistoryId(serviceHistoryDTO == null ? null : serviceHistoryDTO.getId());
      if (StringUtils.isNotBlank(washBeautyOrderItemDTO.getBusinessCategoryName())) {
        CategoryDTO categoryDTO = categoryDTOMap.get(washBeautyOrderItemDTO.getBusinessCategoryName());
        if (categoryDTO != null) {
          washBeautyOrderItemDTO.setBusinessCategoryId(categoryDTO.getId());
        }
      }
    }
    //批量保存service和营业分类对应关系
    batchSaveOrUpdateCategoryItemRelation(writer, washBeautyOrderDTO);
  }

  //保存service和营业分类关系
  private void batchSaveOrUpdateCategoryItemRelation(TxnWriter writer, WashBeautyOrderDTO washBeautyOrderDTO) {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    if (!ArrayUtils.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
      Set<Long> serviceIds = washBeautyOrderDTO.getServiceIds();
      Map<Long, List<CategoryItemRelation>> categoryItemRelationMap = rfiTxnService.getCategoryItemRelationMapByServiceIds(
          serviceIds.toArray(new Long[serviceIds.size()]));
      for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
        if (washBeautyOrderItemDTO.getServiceId() == null) {
          continue;
        }
        if (washBeautyOrderItemDTO.getBusinessCategoryId() != null) {
          List<CategoryItemRelation> categoryItemRelations = categoryItemRelationMap.get(washBeautyOrderItemDTO.getServiceId());
          if (CollectionUtils.isNotEmpty(categoryItemRelations)) {
            for (CategoryItemRelation categoryItemRelation : categoryItemRelations) {
              if (!washBeautyOrderItemDTO.getBusinessCategoryId().equals(categoryItemRelation.getCategoryId())) {
                categoryItemRelation.setCategoryId(washBeautyOrderItemDTO.getBusinessCategoryId());
                writer.update(categoryItemRelation);
              }
            }
          } else {
            categoryItemRelations = new ArrayList<CategoryItemRelation>();
            CategoryItemRelation categoryItemRelation = new CategoryItemRelation(washBeautyOrderItemDTO.getBusinessCategoryId(), washBeautyOrderItemDTO.getServiceId());
            writer.save(categoryItemRelation);
            categoryItemRelations.add(categoryItemRelation);
          }
          categoryItemRelationMap.put(washBeautyOrderItemDTO.getServiceId(), categoryItemRelations);
        }
      }
    }
  }

  /**
   * @param washBeautyOrderDTO
   */
  @Override
  public void setServiceWorks(WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    if (washBeautyOrderDTO == null || ArrayUtils.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
      return;
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    //施工人
    Set<String> allWorkers = new LinkedHashSet<String>();  //所有施工人
    for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
      if (washBeautyOrderItemDTO == null || washBeautyOrderItemDTO.getServiceId() == null || StringUtil.isEmpty(washBeautyOrderItemDTO.getSalesMan())) {
        continue;
      }
      washBeautyOrderItemDTO.setSalesMan(washBeautyOrderItemDTO.getSalesMan().replace("，", ","));
      for (String itemWorker : washBeautyOrderItemDTO.getSalesMan().split(",")) {
        if (StringUtils.isBlank(itemWorker)) {
          continue;
        }
        allWorkers.add(itemWorker.trim());
      }
    }
    Map<String, SalesManDTO> salesManDTOMap = userService.getSalesManDTOMap(washBeautyOrderDTO.getShopId(), allWorkers);
    for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
      if (washBeautyOrderItemDTO == null || washBeautyOrderItemDTO.getServiceId() == null || StringUtil.isEmpty(washBeautyOrderItemDTO.getSalesMan())) {
        continue;
      }
      Set<String> itemWorkersSet = new LinkedHashSet<String>();
      Set<Long> itemWorkerIdsSet = new LinkedHashSet<Long>();
      //遍历以逗号分隔的工人,如果没有就新增, 有的话setId到workerIds, 顺便去重.
      for (String itemWorker : washBeautyOrderItemDTO.getSalesMan().split(",")) {
        if (StringUtils.isBlank(itemWorker)) {
          continue;
        }
        itemWorker = itemWorker.trim();
        SalesManDTO salesManDTO = salesManDTOMap.get(itemWorker);
        if (salesManDTO != null) {
          itemWorkerIdsSet.add(salesManDTO.getId());
        } else {
          SalesManDTO newSalesMan = new SalesManDTO();
          newSalesMan.setName(itemWorker);
          newSalesMan.setShopId(washBeautyOrderDTO.getShopId());
          newSalesMan.setStatus(SalesManStatus.ONTRIAL);
          newSalesMan.setDepartmentName(SalesManDTO.defaultEmptyDepartment);
          userService.saveOrUpdateSalesMan(newSalesMan);
          itemWorkerIdsSet.add(newSalesMan.getId());
          salesManDTOMap.put(itemWorker, newSalesMan);
        }
        itemWorkersSet.add(itemWorker);
      }
      String commaWorkers = CollectionUtil.collectionToCommaString(itemWorkersSet);
      String commaWorkerIds = CollectionUtil.collectionToCommaString(itemWorkerIdsSet);
      washBeautyOrderItemDTO.setSalesMan(commaWorkers);
      washBeautyOrderItemDTO.setSalesManIds(commaWorkerIds);

    }
    if (CollectionUtils.isNotEmpty(allWorkers)) {
      washBeautyOrderDTO.setServiceWorker(CollectionUtil.collectionToCommaString(allWorkers));
    } else {
      washBeautyOrderDTO.setServiceWorker("");
    }
  }


  @Override
  public WashBeautyOrderDTO saveWashBeautyOrder(Long shopId, Long userId, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    //洗车单有关联的预约单的话，把预约单的appUserNo 放到洗车单里面
    if (washBeautyOrderDTO.getAppointOrderId() != null) {
      IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
      AppointOrderDTO appointOrderDTO = appointOrderService.getSimpleAppointOrderById(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getAppointOrderId());
      if (appointOrderDTO != null && StringUtils.isNotBlank(appointOrderDTO.getAppUserNo())) {
        washBeautyOrderDTO.setAppUserNo(appointOrderDTO.getAppUserNo());
      }
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<ItemIndex> itemIndexes = new ArrayList<ItemIndex>();
    Long memberId = null;
    Member member = null;
    if (StringUtils.isNotBlank(washBeautyOrderDTO.getAccountMemberNo())) {
      member = membersService.getMemberByShopIdAndMemberNo(shopId, washBeautyOrderDTO.getAccountMemberNo());
    }
    if (member != null) {
      washBeautyOrderDTO.setMemberId(member.getId());
      washBeautyOrderDTO.setMemberNo(member.getMemberNo());
      washBeautyOrderDTO.setMemberDTO(member.toDTO());
    }
    Object status = writer.begin();
    try {
      WashBeautyOrder washBeautyOrder = new WashBeautyOrder(washBeautyOrderDTO);
      washBeautyOrder.setStatus(OrderStatus.WASH_SETTLED);
      writer.save(washBeautyOrder);
      washBeautyOrderDTO.setId(washBeautyOrder.getId());
      initWashBeautyService(writer,washBeautyOrderDTO);
      StringBuffer orderContent = new StringBuffer();//施工内容
      for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
        if (washBeautyOrderItemDTO == null) {
          continue;
        }
        washBeautyOrderItemDTO.setShopId(washBeautyOrderDTO.getShopId());
        washBeautyOrderItemDTO.setWashBeautyOrderId(washBeautyOrderDTO.getId());
        WashBeautyOrderItem washBeautyOrderItem = new WashBeautyOrderItem(washBeautyOrderItemDTO);
        writer.save(washBeautyOrderItem);
        washBeautyOrderItemDTO.setId(washBeautyOrderItem.getId());
        orderContent.append(washBeautyOrderItemDTO.getServiceName()).append(",");
        itemIndexes = doItemIndex(itemIndexes, memberId, washBeautyOrder, washBeautyOrderItem, washBeautyOrderItemDTO.getServiceName(), washBeautyOrderDTO);
      }
      if (orderContent.length() > 1) {
        washBeautyOrderDTO.setOrderContent(orderContent.substring(0, orderContent.length() - 1));
      }
      this.doReceivablePart(washBeautyOrderDTO, washBeautyOrder, writer);
      writer.commit(status);
      //保存orderIndex和itemIndex
      this.saveOrderIndexAndItemIndex(shopId, washBeautyOrder, itemIndexes, washBeautyOrderDTO);
      washBeautyOrderDTO.setCustomerId(washBeautyOrder.getCustomerId());
      washBeautyOrderDTO.setId(washBeautyOrder.getId());
      washBeautyOrderDTO.setStatus(washBeautyOrder.getStatus());
      return washBeautyOrderDTO;
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public void accountMemberWithWashBeauty(WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    //会员短信模板
    VelocityContext context = new VelocityContext();
    Map<String, String> remainItems = new LinkedHashMap<String, String>();    //初始化剩余项目次数
    Map<String, String> consumeItems = new LinkedHashMap<String, String>();  //本次洗车美容使用的会员服务
    context.put(SmsConstant.VelocityMsgTemplateConstant.consumeAmount, 0);
    context.put(SmsConstant.VelocityMsgTemplateConstant.remainAmount, 0);
    context.put(SmsConstant.VelocityMsgTemplateConstant.consumeItems, consumeItems);
    context.put(SmsConstant.VelocityMsgTemplateConstant.remainItems, remainItems);
    List<MemberService> memberServices = new ArrayList<MemberService>();   //本洗车美容单上所使用的会员服务
    Long memberId = null;
    Member member = null;
    ShopDTO shopDTO = configService.getShopById(washBeautyOrderDTO.getShopId());
    UserWriter writer = ServiceManager.getService(UserDaoManager.class).getWriter();
    Object status = writer.begin();
    try {
      if (StringUtils.isNotBlank(washBeautyOrderDTO.getAccountMemberNo())) {
        member = writer.getMemberByShopIdAndMemberNo(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getAccountMemberNo());
      }
      if (member != null) {
        if (member.getBalance() != null && washBeautyOrderDTO.getMemberAmount() != null && member.getBalance() > 0) {
          if (member.getBalance() < washBeautyOrderDTO.getMemberAmount()) {
            LOG.error("method=saveWashBeautyOrderAndSendSms" + MemberConstant.MEMBER_BALANCE_NOT_ENOUGH + "washBeautyOrderDTO :{}", washBeautyOrderDTO);
            return;
          }
          context.put(SmsConstant.VelocityMsgTemplateConstant.consumeAmount, washBeautyOrderDTO.getMemberAmount());
          member.setBalance(NumberUtil.doubleVal(member.getBalance()) - NumberUtil.doubleVal(washBeautyOrderDTO.getMemberAmount()));
        }
        context.put(SmsConstant.VelocityMsgTemplateConstant.remainAmount, member.getBalance());
        writer.update(member);
        memberId = member.getId();
        washBeautyOrderDTO.setMemberId(memberId);
        washBeautyOrderDTO.setMemberDTO(member.toDTO());
      }
       memberServices = writer.getMemberServicesByMemberId(memberId);
      Map<Long, MemberService> membersServiceMap = new HashMap<Long, MemberService>();
      if (CollectionUtils.isNotEmpty(memberServices)) {
        for (MemberService memberService : memberServices) {
          if (memberService.getServiceId() != null) {
            membersServiceMap.put(memberService.getServiceId(), memberService);
          }
        }
      }
      Map<Long, Service> serviceMap = rfiTxnService.getServiceMapByIds(washBeautyOrderDTO.getShopId(), membersServiceMap.keySet());
      for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
        if (washBeautyOrderItemDTO == null || washBeautyOrderItemDTO.getServiceId() == null) {
          continue;
        }
        if (ConsumeType.TIMES.equals(washBeautyOrderItemDTO.getConsumeTypeStr())) {
          MemberService memberService = membersServiceMap.get(washBeautyOrderItemDTO.getServiceId());
          if (memberService != null) {
            int times = memberService.getTimes();
            if (-1 != times) {
              if (times > 0) {
                memberService.setTimes(times - 1);
                writer.update(memberService);
                consumeItems.put(washBeautyOrderItemDTO.getServiceName(), "1");
              }
            } else {
              consumeItems.put(washBeautyOrderItemDTO.getServiceName(), "1");
            }
          }
        }
      }
      //取出所有的剩余次数
      if (MapUtils.isNotEmpty(membersServiceMap)) {
        for (MemberService memberService : membersServiceMap.values()) {
          Service service = serviceMap.get(memberService.getServiceId());
          if (service != null) {
            if (memberService.getTimes() > 0) {  //有剩余次数才放进remainItems
              remainItems.put(service.getName(), String.valueOf(memberService.getTimes()));
            } else if (memberService.getTimes() == -1) {
              remainItems.put(service.getName(), "无限");
            }
          }
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    //发送会员短信
    CustomerDTO cardOwner = ServiceManager.getService(IUserService.class).getCustomerWithMemberByMemberNoShopId(washBeautyOrderDTO.getAccountMemberNo(), washBeautyOrderDTO.getShopId());
    if (cardOwner != null && washBeautyOrderDTO.isSendMemberSms() && StringUtils.isNotEmpty(cardOwner.getMobile())) {
      String smsContent=ServiceManager.getService(ISmsService.class).sendMemberMsgToCardOwner(cardOwner, shopDTO, context);
      ContactDTO contactDTO=new ContactDTO();
      contactDTO.setMobile(cardOwner.getMobile());
      ServiceManager.getService(ISendSmsService.class).sendSms(washBeautyOrderDTO.getShopId(),washBeautyOrderDTO.getUserId(),smsContent,false,true,true,contactDTO);
    }
  }

  @Override
  public WashBeautyOrderDTO accountMemberWithWashBeauty_camera(WashBeautyOrderDTO washBeautyOrderDTO,String[] serviceIds) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    //会员短信模板
//    VelocityContext context = new VelocityContext();
    Map<String, String> remainItems = new LinkedHashMap<String, String>();    //初始化剩余项目次数
    Map<String, String> consumeItems = new LinkedHashMap<String, String>();  //本次洗车美容使用的会员服务
//    context.put(SmsConstant.VelocityMsgTemplateConstant.consumeAmount, 0);
//    context.put(SmsConstant.VelocityMsgTemplateConstant.remainAmount, 0);
//    context.put(SmsConstant.VelocityMsgTemplateConstant.consumeItems, consumeItems);
//    context.put(SmsConstant.VelocityMsgTemplateConstant.remainItems, remainItems);
    List<MemberService> memberServices = new ArrayList<MemberService>();   //本洗车美容单上所使用的会员服务
    Long memberId = null;
    Member member = null;
    ShopDTO shopDTO = configService.getShopById(washBeautyOrderDTO.getShopId());
    UserWriter writer = ServiceManager.getService(UserDaoManager.class).getWriter();
    Object status = writer.begin();
    try {
      if (StringUtils.isNotBlank(washBeautyOrderDTO.getAccountMemberNo())) {
        member = writer.getMemberByShopIdAndMemberNo(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getAccountMemberNo());
      }
      if(member!=null){
        memberId = member.getId();
      }
      memberServices = writer.getMemberServicesByMemberId(memberId);
      Map<Long, MemberService> membersServiceMap = new HashMap<Long, MemberService>();
      if (CollectionUtils.isNotEmpty(memberServices)) {
        for (MemberService memberService : memberServices) {
          if (memberService.getServiceId() != null) {
            membersServiceMap.put(memberService.getServiceId(), memberService);
          }
        }
      }
      Double total = 0D;
      MemberService memberService = null;
        //////
        WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = new WashBeautyOrderItemDTO[serviceIds.length];
        for(int i=0;i<serviceIds.length;i++){
          ServiceDTO serviceDTO = new ServiceDTO();
          serviceDTO = getServiceById(Long.valueOf(serviceIds[i]));
          if(serviceDTO!=null&&serviceDTO.getId()!=null){
             memberService = membersServiceMap.get(serviceDTO.getId());
          }

          if (memberService != null) {
            int times = memberService.getTimes();
            if (-1 != times && times > 0) {
                memberService.setTimes(times - 1);
                writer.update(memberService);
                washBeautyOrderItemDTOs[i] = new WashBeautyOrderItemDTO();
                washBeautyOrderItemDTOs[i].fromServiceDTO(serviceDTO);
                washBeautyOrderItemDTOs[i].setConsumeTypeStr(ConsumeType.TIMES);
            } else if(-1 ==times){
              washBeautyOrderItemDTOs[i] = new WashBeautyOrderItemDTO();
              washBeautyOrderItemDTOs[i].fromServiceDTO(serviceDTO);
              washBeautyOrderItemDTOs[i].setConsumeTypeStr(ConsumeType.TIMES);
            }else{
              washBeautyOrderItemDTOs[i] = new WashBeautyOrderItemDTO();
              washBeautyOrderItemDTOs[i].fromServiceDTO(serviceDTO);
              washBeautyOrderItemDTOs[i].setConsumeTypeStr(ConsumeType.MONEY);
              total+= serviceDTO.getPrice();
            }
          }else{
            washBeautyOrderItemDTOs[i] = new WashBeautyOrderItemDTO();
            washBeautyOrderItemDTOs[i].fromServiceDTO(serviceDTO);
            washBeautyOrderItemDTOs[i].setConsumeTypeStr(ConsumeType.MONEY);
            total+= serviceDTO.getPrice();
          }
        }
      //判断total的值，如果total>0,查找会员卡储值，储值不够现金支付
      if (member != null&& total >0) {
        if (member.getBalance() != null  && member.getBalance() >= 0) {
            if (member.getBalance() >= total) {
              member.setBalance(NumberUtil.doubleVal(member.getBalance()) - NumberUtil.doubleVal(total));
              washBeautyOrderDTO.setTotal(NumberUtil.toReserve(0D, NumberUtil.MONEY_PRECISION));
              washBeautyOrderDTO.setSettledAmount(NumberUtil.toReserve(0D, NumberUtil.MONEY_PRECISION));
            }else{
              washBeautyOrderDTO.setTotal(NumberUtil.doubleVal(total) - NumberUtil.doubleVal(member.getBalance()));
              washBeautyOrderDTO.setSettledAmount(NumberUtil.doubleVal(total) - NumberUtil.doubleVal(member.getBalance()));
              member.setBalance(NumberUtil.toReserve(0D, NumberUtil.MONEY_PRECISION));
            }
          }
        writer.update(member);
        memberId = member.getId();
        washBeautyOrderDTO.setMemberId(memberId);
        washBeautyOrderDTO.setMemberDTO(member.toDTO());
      } else{
        washBeautyOrderDTO.setTotal(NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION));
        washBeautyOrderDTO.setSettledAmount(NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION));
      }


        washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs);


      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return washBeautyOrderDTO;
  }

  public ServiceDTO getServiceById(Long id) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    if(null == id)
    {
      return null;
    }

    Service service = writer.getById(Service.class, id);
    ServiceDTO serviceDTO = null;

    if (null != service) {
      serviceDTO = service.toDTO();
    }
    return serviceDTO;
  }


  public List<ItemIndex> doItemIndex(List<ItemIndex> itemIndexes, Long memberId, WashBeautyOrder washBeautyOrder,
                                     WashBeautyOrderItem washBeautyOrderItem, String name, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    ItemIndex itemIndex = new ItemIndex();
    itemIndex.setOrderStatusEnum(washBeautyOrder.getStatus());
    itemIndex.setCustomerId(washBeautyOrder.getCustomerId());
    itemIndex.setCustomerOrSupplierName(washBeautyOrder.getCustomer());
    itemIndex.setItemCostPrice(0d);
    itemIndex.setTotalCostPrice(0d);
    itemIndex.setItemId(washBeautyOrderItem.getId());
    itemIndex.setItemName(name);
    itemIndex.setItemPrice(washBeautyOrderItem.getPrice());
    itemIndex.setItemTypeEnum(ItemTypes.WASH);
    itemIndex.setMemberCardId(memberId);
    itemIndex.setOrderId(washBeautyOrder.getId());
    itemIndex.setOrderTimeCreated(washBeautyOrder.getVestDate());
    itemIndex.setOrderTotalAmount(washBeautyOrder.getTotal());
    itemIndex.setOrderTypeEnum(OrderTypes.WASH_BEAUTY);
    itemIndex.setServiceId(washBeautyOrderItem.getServiceId());
    itemIndex.setShopId(washBeautyOrder.getShopId());
    itemIndex.setVehicle(washBeautyOrder.getVechicle());
    itemIndex.setIncreasedTimes(1);
    itemIndex.setArrears(washBeautyOrderDTO.getDebt());
    itemIndex.setPaymentTime(("").equals(washBeautyOrderDTO.getHuankuanTime()) ? null :
        DateUtil.convertDateStringToDateLong("yyyy-MM-dd", washBeautyOrderDTO.getHuankuanTime()));
    itemIndex.setBusinessCategoryId(washBeautyOrderItem.getBusinessCategoryId());
    itemIndex.setBusinessCategoryName(washBeautyOrderItem.getBusinessCategoryName());
    itemIndex.setAfterMemberDiscountOrderTotal(washBeautyOrderDTO.getAfterMemberDiscountTotal());
    itemIndexes.add(itemIndex);
    return itemIndexes;
  }

  public void doReceivablePart(WashBeautyOrderDTO washBeautyOrderDTO, WashBeautyOrder washBeautyOrder, TxnWriter writer) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long remindTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, washBeautyOrderDTO.getHuankuanTime());
    IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    Receivable receivable = new Receivable();
    receivable.setDiscount(washBeautyOrderDTO.getOrderDiscount());
    receivable.setSettledAmount(washBeautyOrderDTO.getSettledAmount());
    receivable.setBankCard(washBeautyOrderDTO.getBankAmount());
    receivable.setCash(washBeautyOrderDTO.getCashAmount());
    receivable.setDebt(washBeautyOrderDTO.getDebt());
    receivable.setMemberBalancePay(washBeautyOrderDTO.getMemberAmount());
    receivable.setTotal(washBeautyOrderDTO.getTotal());
    receivable.setShopId(washBeautyOrderDTO.getShopId());
    receivable.setOrderType(Long.valueOf(SearchConstant.HISTORYSEARCH_ORDERTYPE_WASHBEAUTY));
//    receivable.setMemberId(washBeautyOrderDTO.getMemberId());
    receivable.setOrderTypeEnum(OrderTypes.WASH_BEAUTY);
    receivable.setOrderId(washBeautyOrder.getId());
    receivable.setLastPayeeId(washBeautyOrderDTO.getUserId());
    receivable.setLastPayee(washBeautyOrderDTO.getUserName());
    receivable.setCustomerId(washBeautyOrder.getCustomerId());
    receivable.setVestDate(washBeautyOrder.getVestDate());
    receivable.setReceiptNo(washBeautyOrder.getReceiptNo());
    receivable.setLastReceiveDate(washBeautyOrder.getCreationDate());
    receivable.setCheque(washBeautyOrderDTO.getBankCheckAmount());
    receivable.setAfterMemberDiscountTotal(washBeautyOrderDTO.getAfterMemberDiscountTotal());
    receivable.setMemberDiscountRatio(washBeautyOrderDTO.getMemberDiscountRatio());
    receivable.setRemindTime(remindTime);
    receivable.setOrderDebtType(OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);

    receivable.setCoupon(washBeautyOrderDTO.getCouponAmount());//代金券
//    if(washBeautyOrderDTO.getConsumingRecordId()!=null) {
//      CouponConsumeRecordDTO couponConsumeRecordDTO=getConsumingService().getCouponConsumeRecordById(washBeautyOrderDTO.getConsumingRecordId());
//      if(couponConsumeRecordDTO!=null) {
//        receivable.setCouponConsume(couponConsumeRecordDTO.getCoupon().doubleValue());
//      }
//    }
    if (memberCheckerService.containMemberAmountByWash(washBeautyOrderDTO)
        || memberCheckerService.containMemberCountConsumeByWash(washBeautyOrderDTO)
        || null != washBeautyOrderDTO.getMemberDiscountRatio()) {
      receivable.setMemberId(washBeautyOrderDTO.getMemberId());
      receivable.setMemberNo(washBeautyOrderDTO.getMemberNo());
    }
    receivable.setStatusEnum(ReceivableStatus.FINISH);
    writer.save(receivable);

    ReceivableDTO receivableDTO = receivable.toDTO();
    ReceivableHistoryDTO receivableHistoryDTO = receivableDTO.toReceivableHistoryDTO();
    ReceivableHistory receivableHistory = new ReceivableHistory(receivableHistoryDTO);
    writer.save(receivableHistory);

    ReceptionRecord receptionRecord = new ReceptionRecord();
    receptionRecord.setReceivableHistoryId(receivableHistory.getId());
    receptionRecord.setDayType(DayType.OTHER_DAY);
    receptionRecord.setReceivableId(receivable.getId());
    receptionRecord.setReceiveTime(receivable.getCreationDate());
    receptionRecord.setMemberBalancePay(receivable.getMemberBalancePay());
    receptionRecord.setAmount(receivable.getSettledAmount());
    receptionRecord.setChequeNo(washBeautyOrderDTO.getBankCheckNo());
    receptionRecord.setMemberId(washBeautyOrderDTO.getMemberId());
    receptionRecord.setCash(washBeautyOrderDTO.getCashAmount());
    receptionRecord.setBankCard(washBeautyOrderDTO.getBankAmount());
    receptionRecord.setCheque(washBeautyOrderDTO.getBankCheckAmount());
    receptionRecord.setToPayTime(washBeautyOrderDTO.getRepaymentTime());
    receptionRecord.setOrderId(washBeautyOrder.getId());
    receptionRecord.setShopId(washBeautyOrder.getShopId());
    receptionRecord.setReceptionDate(System.currentTimeMillis());
    receptionRecord.setRecordNum(0);
    receptionRecord.setOriginDebt(0d);
    receptionRecord.setOrderStatusEnum(washBeautyOrder.getStatus());
    receptionRecord.setOrderTypeEnum(OrderTypes.WASH_BEAUTY);
    receptionRecord.setDiscount(washBeautyOrderDTO.getOrderDiscount());
    receptionRecord.setRemainDebt(washBeautyOrderDTO.getDebt());
    receptionRecord.setOrderTotal(washBeautyOrderDTO.getTotal());
    receptionRecord.setAfterMemberDiscountTotal(washBeautyOrderDTO.getAfterMemberDiscountTotal());
    receptionRecord.setMemberDiscountRatio(washBeautyOrderDTO.getMemberDiscountRatio());
    receptionRecord.setPayee(washBeautyOrderDTO.getUserName());
    receptionRecord.setPayeeId(washBeautyOrderDTO.getUserId());
    receptionRecord.setCoupon(washBeautyOrderDTO.getCouponAmount()); //代金券
    writer.save(receptionRecord);
    for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
      //服务记录
      ReceptionServiceTimes receptionServiceTimes = new ReceptionServiceTimes();
      receptionServiceTimes.setReceptionRecordId(receptionRecord.getId());
      receptionServiceTimes.setServiceId(washBeautyOrderItemDTO.getServiceId());
      receptionServiceTimes.setOriginAmount(washBeautyOrderItemDTO.getPrice());
      receptionServiceTimes.setTimes(1);
      writer.save(receptionServiceTimes);
      //支付记录
      ReceivableServiceTimes receivableServiceTimes = new ReceivableServiceTimes();
      receivableServiceTimes.setOriginAmount(washBeautyOrderItemDTO.getPrice());
      receivableServiceTimes.setReceivableId(receivable.getId());
      receivableServiceTimes.setServiceId(washBeautyOrderItemDTO.getServiceId());
      receivableServiceTimes.setTimes(1);
      writer.save(receivableServiceTimes);
    }
    if (washBeautyOrderDTO.getDebt() > 0) {
      Debt debt = new Debt();
      debt.setCustomerId(washBeautyOrder.getCustomerId());
      debt.setDebt(washBeautyOrderDTO.getDebt());
      debt.setOrderId(washBeautyOrder.getId());
      debt.setReceiptNo(washBeautyOrder.getReceiptNo());
      debt.setOrderTime(washBeautyOrder.getCreationDate());
      debt.setOrderType(TxnConstant.OrderType.ORDER_TYPE_WASH_BEAUTY);
      debt.setOrderTypeEnum(OrderTypes.WASH_BEAUTY);
      debt.setRecievableId(receivable.getId());
      debt.setSettledAmount(washBeautyOrderDTO.getSettledAmount());
      debt.setShopId(washBeautyOrder.getShopId());
      debt.setTotalAmount(washBeautyOrder.getTotal());
      debt.setVehicleNumber(washBeautyOrder.getVechicle());
      debt.setContent(washBeautyOrderDTO.getOrderContent());
      debt.setService(washBeautyOrderDTO.getOrderContent());
      Long payTime = System.currentTimeMillis();
      debt.setPayTime(payTime);
      debt.setRemindTime(remindTime);
      debt.setStatusEnum(DebtStatus.ARREARS);
      debt.setRemindStatus(UserConstant.Status.ACTIVITY);
      writer.save(debt);
      //更新remind_event的deleted_type
      writer.updateDebtRemindDeletedType(washBeautyOrderDTO.getShopId(),washBeautyOrder.getCustomerId(),"customer",DeletedType.FALSE);
      txnService.saveRemindEvent(writer, debt, washBeautyOrderDTO.getCustomer(), washBeautyOrderDTO.getMobile());

    }
  }

  public void saveOrderIndexAndItemIndex(Long shopId, WashBeautyOrder washBeautyOrder, List<ItemIndex> itemIndexes, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IOrderIndexService orderIndexService = ServiceManager.getService(IOrderIndexService.class);
    washBeautyOrderDTO.setCustomerId(washBeautyOrder.getCustomerId());
    washBeautyOrderDTO.setCustomer(washBeautyOrder.getCustomer());
    washBeautyOrderDTO.setTotal(washBeautyOrder.getTotal());
    washBeautyOrderDTO.setId(washBeautyOrder.getId());
    washBeautyOrderDTO.setVechicle(washBeautyOrder.getVechicle());
    washBeautyOrderDTO.setReceiptNo(washBeautyOrder.getReceiptNo());
    washBeautyOrderDTO.setShopId(washBeautyOrder.getShopId());
    MemberDTO memberDTO = ServiceManager.getService(IMembersService.class).getMemberByCustomerId(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getCustomerId());
    if (memberDTO != null) {
      washBeautyOrderDTO.setMemberNo(memberDTO.getMemberNo());
      washBeautyOrderDTO.setMemberType(memberDTO.getType());
    }
    OrderIndexDTO orderIndexDTO = washBeautyOrderDTO.toOrderIndexDTO();
    //判断计次收费项目
    if (!ArrayUtils.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
      for (WashBeautyOrderItemDTO item : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
        if ( ConsumeType.TIMES.equals(item.getPayType()) || item.getConsumeTypeStr().equals(ConsumeType.TIMES)) {
          ServiceDTO serviceDTO = ServiceManager.getService(ITxnService.class).getServiceById(item.getServiceId());
          if (serviceDTO == null) {
            LOG.warn("service[id:{}] get by id is null.", item.getServiceId());
          } else {
            orderIndexDTO.getPayPerProjects().add(serviceDTO.getName());
          }
        }
      }
    }
    searchService.saveOrUpdateOrderIndex(orderIndexDTO);
    for (ItemIndex itemIndex : itemIndexes) {
      searchService.addItemIndex(itemIndex);
    }
  }


  public WashBeautyOrderDTO saveWashBeautyOrderAndSendSmsLastVersion(Long shopId, Long userId, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IBaseProductService baseProductService = ServiceManager.getService(IBaseProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);

    VelocityContext context = new VelocityContext();
    Map<String, String> remainItems = new LinkedHashMap<String, String>();    //初始化剩余项目次数
    Map<String, String> consumeItems = new LinkedHashMap<String, String>();  //本次洗车美容使用的会员服务
    context.put(SmsConstant.VelocityMsgTemplateConstant.consumeAmount, 0);
    context.put(SmsConstant.VelocityMsgTemplateConstant.remainAmount, 0);
    context.put(SmsConstant.VelocityMsgTemplateConstant.consumeItems, consumeItems);
    context.put(SmsConstant.VelocityMsgTemplateConstant.remainItems, remainItems);

    TxnWriter writer = txnDaoManager.getWriter();
    List<ItemIndex> itemIndexes = new ArrayList<ItemIndex>();
    List<MemberService> memberServices = new ArrayList<MemberService>();   //本洗车美容单上所使用的会员服务
    Long memberId = null;
    Member member = null;
    if (StringUtils.isNotBlank(washBeautyOrderDTO.getAccountMemberNo())) {
      member = membersService.getMemberByShopIdAndMemberNo(shopId, washBeautyOrderDTO.getAccountMemberNo());
    }
    if (member != null) {
      if (member.getBalance() != null && washBeautyOrderDTO.getMemberAmount() != null && member.getBalance() > 0) {
        if (member.getBalance() < washBeautyOrderDTO.getMemberAmount()) {
          LOG.error("method=saveWashBeautyOrderAndSendSms" + MemberConstant.MEMBER_BALANCE_NOT_ENOUGH + "washBeautyOrderDTO :{}", washBeautyOrderDTO);
          return null;
        }
        context.put(SmsConstant.VelocityMsgTemplateConstant.consumeAmount, washBeautyOrderDTO.getMemberAmount());
        member.setBalance(NumberUtil.doubleVal(member.getBalance()) - NumberUtil.doubleVal(washBeautyOrderDTO.getMemberAmount()));
      }
      context.put(SmsConstant.VelocityMsgTemplateConstant.remainAmount, member.getBalance());
      membersService.updateMember(member.toDTO());
      memberId = member.getId();
      washBeautyOrderDTO.setMemberId(memberId);
      washBeautyOrderDTO.setMemberDTO(member.toDTO());
    }
    Object status = writer.begin();
    try {
      WashBeautyOrder washBeautyOrder = new WashBeautyOrder(washBeautyOrderDTO);
      washBeautyOrder.setStatus(OrderStatus.WASH_SETTLED);
      writer.save(washBeautyOrder);
      washBeautyOrderDTO.setId(washBeautyOrder.getId());

      StringBuffer orderContent = new StringBuffer();//施工内容
      for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
        if (washBeautyOrderItemDTO == null) {
          continue;
        }
        washBeautyOrderItemDTO.setShopId(washBeautyOrderDTO.getShopId());
        washBeautyOrderItemDTO.setWashBeautyOrderId(washBeautyOrderDTO.getId());
        WashBeautyOrderItem washBeautyOrderItem = new WashBeautyOrderItem(washBeautyOrderItemDTO);
        writer.save(washBeautyOrderItem);
        washBeautyOrderItemDTO.setId(washBeautyOrderItem.getId());
        orderContent.append(washBeautyOrderItemDTO.getServiceName()).append(",");
        itemIndexes = doItemIndex(itemIndexes, memberId, washBeautyOrder, washBeautyOrderItem, washBeautyOrderItemDTO.getServiceName(), washBeautyOrderDTO);
        if (washBeautyOrderItem.getPayType().equals(ConsumeType.TIMES)) {
          MemberService memberService = membersService.getMemberServiceByMemberIdAndServiceIdAndStatus(memberId, washBeautyOrderItemDTO.getServiceId());
          if (memberService != null) {
            memberServices.add(memberService);
            consumeItems.put(washBeautyOrderItemDTO.getServiceName(), "1");
          }
        }
      }
      if (orderContent.length() > 1) {
        washBeautyOrderDTO.setOrderContent(orderContent.substring(0, orderContent.length() - 1));
      }
      this.doReceivablePart(washBeautyOrderDTO, washBeautyOrder, writer);
      writer.commit(status);
      //用到的会员服务次数-1  //无限次不减
      for (MemberService m : memberServices) {
        int times = m.getTimes();
        if (-1 != times) {
          if (0 == times) {
            throw new Exception("会员服务为0次，已不能继续使用");
          }
          m.setTimes(times - 1);
        }
        membersService.updateMemberService(m);
      }
      //拿出所有的会员服务取出剩余次数，放到短信模板里
      List<MemberServiceDTO> memberServiceDTOList = null;
      if (null != member) {
        memberServiceDTOList = membersService.getMemberServiceEnabledByMemberId(shopId, member.getId());
      }
      //取出所有的剩余次数
      if (CollectionUtils.isNotEmpty(memberServiceDTOList)) {
        for (MemberServiceDTO memberServiceDTO : memberServiceDTOList) {
          Service service = txnService.getServiceById(shopId, memberServiceDTO.getServiceId());
          if (service == null) {
            LOG.error("/RFTxnService.java, method=saveWashBeautyOrderAndSendSms, shopId:{}", shopId);
            LOG.error("会员member_id为:{}的客户具有的服务id:{}不存在", memberServiceDTO.getMemberId(), memberServiceDTO.getServiceId());
          }
          if (memberServiceDTO.getTimes() > 0) {  //有剩余次数才放进remainItems
            remainItems.put(service.getName(), String.valueOf(memberServiceDTO.getTimes()));
          } else if (memberServiceDTO.getTimes() == -1) {
            remainItems.put(service.getName(), "无限");
          }
        }
      }
      //发送会员短信
      CustomerDTO cardOwner = ServiceManager.getService(IUserService.class).getCustomerWithMemberByMemberNoShopId(washBeautyOrderDTO.getAccountMemberNo(), shopId);
      if (cardOwner != null && washBeautyOrderDTO.isSendMemberSms() && StringUtils.isNotEmpty(cardOwner.getMobile())) {
        ServiceManager.getService(ISmsService.class).sendMemberMsgToCardOwner(cardOwner, shopDTO, context);
      }

      //保存orderIndex和itemIndex
      this.saveOrderIndexAndItemIndex(shopId, washBeautyOrder, itemIndexes, washBeautyOrderDTO);
      washBeautyOrderDTO.setCustomerId(washBeautyOrder.getCustomerId());
      washBeautyOrderDTO.setId(washBeautyOrder.getId());
      return washBeautyOrderDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Map<Long, List<WashBeautyOrderItemDTO>> getWashBeautyOrderItemDTOMap(Set<Long> washBeautyOrderIds) {
    Map<Long, List<WashBeautyOrderItemDTO>> washBeautyOrderItemDTOMap = new HashMap<Long, List<WashBeautyOrderItemDTO>>();
    if (CollectionUtils.isNotEmpty(washBeautyOrderIds)) {
      TxnWriter writer = txnDaoManager.getWriter();
      List<WashBeautyOrderItem> washBeautyOrderItems = writer.getWashBeautyOrderItemByShopIdAndOrderIds(null, washBeautyOrderIds.toArray(new Long[washBeautyOrderIds.size()]));
      if (CollectionUtils.isNotEmpty(washBeautyOrderItems)) {
        Set<Long> serviceHistoryIds = new HashSet<Long>();
        Set<Long> serviceIds = new HashSet<Long>();
        for (WashBeautyOrderItem washBeautyOrderItem : washBeautyOrderItems) {
          if (washBeautyOrderItem != null && washBeautyOrderItem.getServiceHistoryId() != null) {
            serviceHistoryIds.add(washBeautyOrderItem.getServiceHistoryId());
          }
        }
        IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
        Map<Long, ServiceHistoryDTO> serviceHistoryDTOMap = serviceHistoryService.getServiceHistoryByServiceHistoryIdSet(null, serviceHistoryIds);
        List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOs = new ArrayList<WashBeautyOrderItemDTO>();
        for (WashBeautyOrderItem washBeautyOrderItem : washBeautyOrderItems) {
          if (washBeautyOrderItem != null) {
            WashBeautyOrderItemDTO washBeautyOrderItemDTO = washBeautyOrderItem.toDTO();
            if (washBeautyOrderItem.getServiceHistoryId() != null) {
              ServiceHistoryDTO serviceHistoryDTO = serviceHistoryDTOMap.get(washBeautyOrderItem.getServiceHistoryId());
              if (serviceHistoryDTO != null && StringUtils.isNotBlank(serviceHistoryDTO.getName())) {
                washBeautyOrderItemDTO.setServiceName(serviceHistoryDTO.getName());
              } else if (washBeautyOrderItemDTO.getServiceId() != null) {
                serviceIds.add(washBeautyOrderItemDTO.getServiceId());
              }
            }
            washBeautyOrderItemDTOs.add(washBeautyOrderItemDTO);
          }
        }
        Map<Long, ServiceDTO> serviceDTOMap = null;
        if (CollectionUtils.isNotEmpty(serviceIds)) {
          ITxnService txnService = ServiceManager.getService(ITxnService.class);
          serviceDTOMap = txnService.getServiceByServiceIdSet(null, serviceIds);
        }
        if (serviceDTOMap == null) {
          serviceDTOMap = new HashMap<Long, ServiceDTO>();
        }
        if (CollectionUtils.isNotEmpty(serviceIds)) {
          for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderItemDTOs) {
            if (washBeautyOrderItemDTO != null && StringUtils.isEmpty(washBeautyOrderItemDTO.getServiceName())
                && washBeautyOrderItemDTO.getServiceId() != null) {
              ServiceDTO serviceDTO = serviceDTOMap.get(washBeautyOrderItemDTO.getServiceId());
              if (serviceDTO != null && StringUtils.isNotBlank(serviceDTO.getName())) {
                washBeautyOrderItemDTO.setServiceName(serviceDTO.getName());
              }
            }
          }
        }
        if (CollectionUtils.isNotEmpty(washBeautyOrderItemDTOs)) {
          for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderItemDTOs) {
            if (washBeautyOrderItemDTO != null && washBeautyOrderItemDTO.getWashBeautyOrderId() != null) {
              List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOList = washBeautyOrderItemDTOMap.get(washBeautyOrderItemDTO.getWashBeautyOrderId());
              if (washBeautyOrderItemDTOList == null) {
                washBeautyOrderItemDTOList = new ArrayList<WashBeautyOrderItemDTO>();
              }
              washBeautyOrderItemDTOList.add(washBeautyOrderItemDTO);
              washBeautyOrderItemDTOMap.put(washBeautyOrderItemDTO.getWashBeautyOrderId(), washBeautyOrderItemDTOList);
            }
          }
        }
      }
    }
    return washBeautyOrderItemDTOMap;
  }

  public void updateConsumingRecordFromRepairOrder(WashBeautyOrderDTO washBeautyOrderDTO){
    Long consumingRecordId = washBeautyOrderDTO.getConsumingRecordId();
    if(consumingRecordId!=null) {
      CouponConsumeRecordDTO couponConsumeRecordDTO = ServiceManager.getService(ConsumingService.class).getCouponConsumeRecordById(consumingRecordId);
      if (couponConsumeRecordDTO != null) {
        couponConsumeRecordDTO.setOrderId(washBeautyOrderDTO.getId());
        couponConsumeRecordDTO.setOrderStatus(washBeautyOrderDTO.getStatus());
        couponConsumeRecordDTO.setConsumerTime(System.currentTimeMillis());
        couponConsumeRecordDTO.setProduct("洗车美容");
        couponConsumeRecordDTO.setSumMoney(washBeautyOrderDTO.getTotal());

        String customerInfo = washBeautyOrderDTO.getCustomer() + "/" + washBeautyOrderDTO.getLicenceNo();
        couponConsumeRecordDTO.setCustomerInfo(customerInfo);
//        getConsumingService().updateConsumingRecordFromOrderInfo(washBeautyOrderDTO.getShopId(), couponConsumeRecordDTO);

        try {
//          TxnWriter writer = txnDaoManager.getWriter();
//        //更新receivable
//          ReceivableDTO receivableDTO =getTxnService().getReceivableByShopIdOrderId(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getId());
//          if(receivableDTO!=null&&couponConsumeRecordDTO.getCoupon()!=null) {
//            getTxnService().updateReceivableCouponConsume(washBeautyOrderDTO.getShopId(), receivableDTO.getId(), couponConsumeRecordDTO.getCoupon().doubleValue());
//          }
          ServiceManager.getService(IConsumingService.class).updateConsumingRecordFromOrderInfo(washBeautyOrderDTO.getShopId(), couponConsumeRecordDTO);
        } catch (Exception e) {
          LOG.error("更新receivable出错");
          LOG.error(e.getMessage(), e);
          e.printStackTrace();
        }
      }
    }
  }
}
