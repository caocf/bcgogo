package com.bcgogo.web;

import com.bcgogo.common.*;
import com.bcgogo.common.StringUtil;
import com.bcgogo.config.customizerconfig.DefaultPageConfigGenerator;
import com.bcgogo.config.dto.*;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.model.*;
import com.bcgogo.config.service.*;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.upyun.UpYun;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.constant.OrderReceiptNoPrefix;
import com.bcgogo.enums.*;
import com.bcgogo.enums.config.AttachmentType;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.importData.ImportType;
import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.txn.finance.BuyChannels;
import com.bcgogo.enums.stat.businessAccountStat.CalculateType;
import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;
import com.bcgogo.enums.txn.finance.SmsCategory;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.enums.user.Status;
import com.bcgogo.enums.user.UserGroupType;
import com.bcgogo.enums.user.UserType;
import com.bcgogo.etl.service.IGsmDataTraceService;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.notification.dto.SmsIndexDTO;
import com.bcgogo.notification.model.*;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.smsSend.SmsUtil;
import com.bcgogo.product.dto.*;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.schedule.bean.ObdGsmPointTraceSchedule;
import com.bcgogo.schedule.bean.ObdGsmVehicleInfoTraceSchedule;
import com.bcgogo.schedule.bean.ShopCheckSchedule;
import com.bcgogo.schedule.bean.recommend.SellWellBusinessChanceSchedule;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.StatUtil;
import com.bcgogo.stat.dto.BusinessAccountDTO;
import com.bcgogo.stat.dto.BusinessAccountSearchConditionDTO;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.stat.model.BusinessAccount;
import com.bcgogo.stat.model.BusinessCategory;
import com.bcgogo.stat.model.StatDaoManager;
import com.bcgogo.stat.model.StatWriter;
import com.bcgogo.stat.service.IBusinessAccountService;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.dto.finance.ShopSmsRecordDTO;
import com.bcgogo.txn.dto.finance.SmsRechargeSearchCondition;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.model.finance.BcgogoSmsRecord;
import com.bcgogo.txn.model.finance.ShopSmsAccount;
import com.bcgogo.txn.model.finance.ShopSmsRecord;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.app.ISendVRegulationMsgToAppService;
import com.bcgogo.txn.service.finance.IBcgogoReceivableService;
import com.bcgogo.txn.service.finance.ISmsAccountService;
import com.bcgogo.txn.service.messageCenter.IMessageService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.pushMessage.IApplyPushMessageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.txn.service.pushMessage.ITradePushMessageService;
import com.bcgogo.txn.service.pushMessage.IVehicleFaultPushMessage;
import com.bcgogo.txn.service.recommend.IRecommendService;
import com.bcgogo.user.cache.RequestPerformanceCache;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.dto.permission.UserGroupDTO;
import com.bcgogo.user.model.*;
import com.bcgogo.user.model.SystemMonitor.UrlMonitorConfig;
import com.bcgogo.user.model.permission.Department;
import com.bcgogo.user.model.permission.User;
import com.bcgogo.user.model.permission.UserGroup;
import com.bcgogo.user.model.permission.UserGroupUser;
import com.bcgogo.user.model.userGuide.UserGuideFlow;
import com.bcgogo.user.model.userGuide.UserGuideHistory;
import com.bcgogo.user.service.*;
import com.bcgogo.user.service.excelimport.obd.ObdImportConstants;
import com.bcgogo.user.service.obd.IObdManagerService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.user.service.wx.*;
import com.bcgogo.user.service.wx.impl.WXAccountService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenfanglei
 * Date: 12-5-24
 * Time: 下午1:11
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/init.do")
public class MigrationController {
  private static final Logger LOG = LoggerFactory.getLogger(MigrationController.class);
  private static final Logger LOGGER = LoggerFactory.getLogger(MigrationController.class);

  public static final int PAGE_SIZE = 2000;//营业数据初始化 每次查询的最大条数
  public static final int BEGIN_DAY_EVERY_MONTH = 1;//默认每个月的第一天
  public static final int BUSINESS_STAT_TIME = 10000;//营业数据初始化 默认每天的最后10秒钟 10000为毫秒数
  public static final int FIRST_PAGE_NO = 1;//营业数据初始化 分页查询 默认的第一页
  public static final int COMPARE_RESULT = 1;//单据作废时间和统计时间比较结果 默认为1 即作废时间比统计时间大
  public static final int PRODUCT_SIZE = 1000;//初始化product_supplier表，每次查询的product数量

  private RFITxnService rfiTxnService;
  private ITxnService txnService;

  public RFITxnService getRfiTxnService() {
    if (rfiTxnService == null) {
      rfiTxnService = ServiceManager.getService(RFITxnService.class);
    }
    return rfiTxnService;
  }

  public void setRfiTxnService(RFITxnService rfiTxnService) {
    this.rfiTxnService = rfiTxnService;
  }

  public ITxnService getTxnService() {
    if (txnService == null) {
      txnService = ServiceManager.getService(ITxnService.class);
    }
    return txnService;
  }

  public void setTxnService(ITxnService txnService) {
    this.txnService = txnService;
  }

  @RequestMapping(params = "method=initPasswordForDevelopers")
  @ResponseBody
  public Object initPasswordForDevelopers(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
      UserWriter writer = userDaoManager.getWriter();
      List<User> userList = writer.getAllShopUser();
      Object status = writer.begin();
      try {
        if (CollectionUtil.isNotEmpty(userList)) {
          for (User user : userList) {
            user.setPassword(EncryptionUtil.encryptPassword("1", user.getShopId()));
            writer.update(user);
          }
          writer.commit(status);
        }
      } finally {
        writer.rollback(status);
      }
      return new Result("初始化成功", true);
    } catch (Exception e) {
      LOG.error("method=initPasswordForDevelopers" + e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }


  /**
   * 此方法用于初始化销售单成本价，此方法只调用一次
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=initGoodSale")
  public String initGoodSale(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Long userId = (Long) request.getSession().getAttribute("userId");
    if (userId != 1L) {
      return "/login";
    }
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    //先把sale_order_item中的cost_price和total_cost_price初始化，从inventory_search_index提取最新入库价

    List<SalesOrderDTO> salesOrderIDTOList = new ArrayList<SalesOrderDTO>();
    List<SalesOrderDTO> salesOrderIDTOList2 = new ArrayList<SalesOrderDTO>();
    try {

      int totalRows = txnService.countSalesOrder(null);
      int totalPages = totalRows % 100 == 0 ? totalRows / 100 : totalRows / 100 + 1;

      if (totalPages > 0) {
        for (int i = 0; i < totalPages; i++) {
          salesOrderIDTOList = txnService.getHundredCostPriceNUllSalesOrderDTOList();

          if (salesOrderIDTOList == null) {
            continue;
          }
          for (SalesOrderDTO salesOrderDTO : salesOrderIDTOList) {
            List<SalesOrderItem> salesOrderItemList = txnService.getSaleOrderItemListByOrderId(salesOrderDTO.getId());

            if (null == salesOrderItemList || 0 == salesOrderItemList.size()) {
              salesOrderDTO.setTotalCostPrice(Double.valueOf(0.0));
            } else {
              SalesOrderItemDTO[] itemDTOs = new SalesOrderItemDTO[salesOrderItemList.size()];
              double totalCostPrice = 0.0;
              int j = 0;
              for (SalesOrderItem salesOrderItem : salesOrderItemList) {
                Long productId = salesOrderItem.getProductId();

                InventorySearchIndex inventorySearchIndex = searchService.getInventorySearchIndexByProductId(productId);

                if (null == inventorySearchIndex) {
                  salesOrderItem.setCostPrice(Double.valueOf(0.0));
                  salesOrderItem.setTotalCostPrice(Double.valueOf(0.0));
                } else {
                  salesOrderItem.setCostPrice(inventorySearchIndex.getPurchasePrice() == null ? Double.valueOf(0.0) : inventorySearchIndex.getPurchasePrice());

                  salesOrderItem.setTotalCostPrice(Double.valueOf(salesOrderItem.getCostPrice().doubleValue() * salesOrderItem.getAmount()));
                }


                itemDTOs[j] = salesOrderItem.toDTO();

                j++;

                totalCostPrice += salesOrderItem.getTotalCostPrice() == null ? 0.0 : salesOrderItem.getTotalCostPrice().doubleValue();
              }
              salesOrderDTO.setItemDTOs(itemDTOs);

              salesOrderDTO.setTotalCostPrice(Double.valueOf(totalCostPrice));
            }

            salesOrderIDTOList2.add(salesOrderDTO);
          }

          txnService.updateSaleOrderAndItem(salesOrderIDTOList2);
        }

      }

      response.sendRedirect(request.getContextPath() + "/user.do?method=createmain");

    } catch (Exception e) {
      LOG.error("/init.do");
      LOG.error("method=initGoodSale");
      LOG.error("初始化销售单出错");
      LOG.error(e.getMessage(), e);
      response.sendRedirect("j_spring_security_logout");
    }
    return "/login";
  }

  /**
   * 此方法用于初始化施工单的成本价，此方法只调用一次，
   * 施工单中可能有不是材料单的服务（如：洗车）不在这中间初始化（repair_order_sercice,wash_order,item_index中不是材料单的部分）
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=initRepair")
  public String initRepairOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Long userId = (Long) request.getSession().getAttribute("userId");
    if (userId != 1L) {
      response.sendRedirect("/login");
    }

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    List<RepairOrderDTO> repairOrderDTOList = null;
    try {

      int totalRows = txnService.countRepairOrder(null);

      int totalPages = totalRows % 100 == 0 ? totalRows / 100 : totalRows / 100 + 1;

      List<RepairOrderItem> repairOrderItemList = new ArrayList<RepairOrderItem>();

      List<RepairOrderDTO> repairOrderDTOList2 = new ArrayList<RepairOrderDTO>();

      if (totalPages > 0) {
        for (int i = 0; i < totalPages; i++) {
          repairOrderDTOList = txnService.getHundredCostPriceNUllRepairOrderDTOList();

          if (repairOrderDTOList == null) {
            continue;
          }

          for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
            repairOrderItemList = txnService.getRepairOrderItemByRepairOrderId(repairOrderDTO.getId());
            if (null == repairOrderItemList || 0 == repairOrderItemList.size()) {
              repairOrderDTO.setTotalCostPrice(Double.valueOf(0.0));
            } else {
              RepairOrderItemDTO[] itemDTOs = new RepairOrderItemDTO[repairOrderItemList.size()];
              double totalCostPrice = 0.0;
              int j = 0;
              for (RepairOrderItem repairOrderItem : repairOrderItemList) {
                Long productId = repairOrderItem.getProductId();

                InventorySearchIndex inventorySearchIndex = searchService.getInventorySearchIndexByProductId(productId);

                if (null == inventorySearchIndex) {
                  repairOrderItem.setCostPrice(Double.valueOf(0.0));
                  repairOrderItem.setTotalCostPrice(Double.valueOf(0.0));
                } else {
                  repairOrderItem.setCostPrice(inventorySearchIndex.getPurchasePrice() == null ? Double.valueOf(0.0) : inventorySearchIndex.getPurchasePrice());

                  repairOrderItem.setTotalCostPrice(Double.valueOf(repairOrderItem.getCostPrice().doubleValue() * repairOrderItem.getAmount()));
                }


                itemDTOs[j] = repairOrderItem.toDTO();

                j++;

                totalCostPrice += repairOrderItem.getTotalCostPrice() == null ? 0.0 : repairOrderItem.getTotalCostPrice().doubleValue();
              }
              repairOrderDTO.setItemDTOs(itemDTOs);

              repairOrderDTO.setTotalCostPrice(Double.valueOf(totalCostPrice));
            }

            repairOrderDTOList2.add(repairOrderDTO);
          }

          txnService.updateRepairOrderAndItem(repairOrderDTOList2);
        }

      }
      response.sendRedirect(request.getContextPath() + "/user.do?method=createmain");
    } catch (Exception e) {
      LOG.error("/init.do");
      LOG.error("method=initRepair");
      LOG.error("初始化施工单出错");
      LOG.error(e.getMessage(), e);
      response.sendRedirect("j_spring_security_logout");
    }

    return "/login";
  }

  //smsBalance数据齐迁移 系统管理员
  @RequestMapping(params = "method=smsBalanceMigrate")
  public void smsBalanceMigrate(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    String warmStr = "";
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
      Integer totalRows = txnService.countSmsBalance();
      Pager pager = new Pager(totalRows, 1, 10);
      List<SmsBalance> balanceList = txnService.smsBalanceMigrate(pager);
      if (CollectionUtils.isEmpty(balanceList)) {
        LOG.warn("there is no data in sms_balance!");
        return;
      }
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      List<ShopBalance> shopBalanceList = new ArrayList<ShopBalance>();
      ShopBalance shopBalance = null;
      ShopBalanceDTO shopBalanceDTO = null;
      while (CollectionUtils.isNotEmpty(balanceList)) {
        for (SmsBalance smsBalance : balanceList) {
          shopBalance = new ShopBalance();
          shopBalance.setRechargeTotal(smsBalance.getRechargeTotal());
          shopBalance.setSmsBalance(smsBalance.getSmsBalance());
          shopBalance.setShopId(smsBalance.getShopId());
          shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(smsBalance.getShopId());
          if (shopBalanceDTO == null) {
            shopBalanceList.add(shopBalance);
          } else {
            warmStr = warmStr + "shopId :" + smsBalance.getShopId() + " exists in shopBalance!" + '\n';
          }
        }
        configService.batchSave(shopBalanceList);
        if (!pager.hasNextPage()) {
          break;
        }
        pager.gotoNextPage();
        balanceList = txnService.smsBalanceMigrate(pager);
      }
      if (StringUtils.isBlank(warmStr)) {
        LOG.info("sms_balance migrate success!");
      } else {
        LOG.warn(warmStr);
      }
    } catch (Exception e) {
      LOG.error("/init.do");
      LOG.error("method=smsBalanceMigrate");
      LOG.error("sms_balance migrate fail!");
      LOG.error(e.getMessage(), e);
    }

  }

  @RequestMapping(params = "method=initOrderIndex")
  public String initOrderIndex(ModelMap model, HttpServletRequest request) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IOrderIndexService orderIndexService = ServiceManager.getService(IOrderIndexService.class);
    long shopId = (Long) request.getSession().getAttribute("shopId");
    if (shopId != 0) {
      return "/";
    }

    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(2012, 0, 1, 0, 0, 0);
    long startTime = calendar.getTimeInMillis();
    calendar.clear();
    //初始化截止当前时间的数据
    calendar.setTime(new Date());
    //只初始化3。0上线以前的数据 3.0上线时间为2012.6.28
    //calendar.set(2012, 5, 28, 0, 0, 0);
    long endTime = calendar.getTimeInMillis();

    try {
      List<Shop> shopList = new ArrayList<Shop>();
      if (!StringUtil.isEmpty(request.getParameter("shopId"))) {
        Shop shopTmp = new Shop();
        ShopDTO shopDTO = new ShopDTO();
        shopDTO.setId(Long.valueOf(request.getParameter("shopId")));
        shopTmp = new Shop(shopDTO);
        shopList.add(shopTmp);

      } else {
        shopList = configService.getShop();
      }
      for (Shop shop : shopList) {
        shopId = shop.getId();
        try {
          LOG.info("后台开始初始化orderIndex,shop_id :" + shopId);


          //洗车
          List<ItemIndexDTO> itemIndexDTOList = searchService.getWashItemIndexList(shopId, startTime, endTime);
          if (itemIndexDTOList != null && itemIndexDTOList.size() > 0) {
            for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
              if (itemIndexDTO == null || itemIndexDTO.getOrderId() == null) {
                continue;
              }
              try {
                OrderIndexDTO orderIndexDTO = this.convertToOrderIndexDTO(itemIndexDTO, null, null, null, null);
                orderIndexDTO.setCreationDate(itemIndexDTO.getOrderTimeCreated());
                if (orderIndexDTO != null) {
                  this.saveOrderIndexToDbAndSolr(orderIndexDTO, itemIndexDTO.getOrderTimeCreated());
                }
              } catch (Exception e) {
                LOG.error("order index初始化失败，shop_id :" + shopId + ",order_id:" + itemIndexDTO.getOrderId() + "系统继续初始化下一个order");
                LOG.error(e.getMessage(), e);
                continue;
              }
            }
          }

          //销售
          List<SalesOrderDTO> salesOrderDTOList = txnService.getSalesOrderDTOListByVestDate(shopId, startTime, endTime);
          if (salesOrderDTOList != null && salesOrderDTOList.size() > 0) {
            for (SalesOrderDTO salesOrderDTO : salesOrderDTOList) {
              if (salesOrderDTO == null || salesOrderDTO.getId() == null) {
                continue;
              }
//              if (salesOrderDTO.getStatus() != null && salesOrderDTO.getStatus().equals(OrderStatus.SALE_REPEAL)) {
//                continue;
//              }

              try {
                OrderIndexDTO orderIndexDTO = this.convertToOrderIndexDTO(null, salesOrderDTO, null, null, null);
                orderIndexDTO.setCreationDate(salesOrderDTO.getCreationDate());
                if (orderIndexDTO != null) {
                  this.saveOrderIndexToDbAndSolr(orderIndexDTO, salesOrderDTO.getCreationDate());
                }
              } catch (Exception e) {
                LOG.error("order index初始化失败，shop_id :" + shopId + ",order_id:" + salesOrderDTO.getId() + "系统继续初始化下一个order");
                LOG.error(e.getMessage(), e);
                continue;
              }
            }
          }

          //施工
          List<RepairOrderDTO> repairOrderDTOList = txnService.getRepairOrderDTOListByCreated(shopId, startTime, endTime);
          if (repairOrderDTOList != null && repairOrderDTOList.size() > 0) {
            for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
              if (repairOrderDTO == null || repairOrderDTO.getId() == null) {
                continue;
              }

//              if (repairOrderDTO.getStatus() != null && repairOrderDTO.getStatus().equals(OrderStatus.REPAIR_REPEAL)) {
//                continue;
//              }

              try {
                OrderIndexDTO orderIndexDTO = this.convertToOrderIndexDTO(null, null, repairOrderDTO, null, null);
                orderIndexDTO.setCreationDate(repairOrderDTO.getCreationDate());
                if (orderIndexDTO != null) {
                  this.saveOrderIndexToDbAndSolr(orderIndexDTO, repairOrderDTO.getCreationDate());
                }
              } catch (Exception e) {
                LOG.error("order index初始化失败，shop_id :" + shopId + ",order_id:" + repairOrderDTO.getId() + "系统继续初始化下一个order");
                LOG.error(e.getMessage(), e);
                continue;
              }
            }
          }

          //采购
          List<PurchaseOrderDTO> purchaseOrderDTOList = txnService.getPurchaseOrderDTOListByShopId(shopId, startTime, endTime);
          if (purchaseOrderDTOList != null && purchaseOrderDTOList.size() > 0) {
            for (PurchaseOrderDTO purchaseOrderDTO : purchaseOrderDTOList) {
              if (purchaseOrderDTO == null || purchaseOrderDTO.getId() == null) {
                continue;
              }

//              if (purchaseOrderDTO.getStatus() != null && purchaseOrderDTO.getStatus().equals(OrderStatus.PURCHASE_ORDER_REPEAL)) {
//                continue;
//              }

              try {
                OrderIndexDTO orderIndexDTO = this.convertToOrderIndexDTO(null, null, null, purchaseOrderDTO, null);
                orderIndexDTO.setCreationDate(purchaseOrderDTO.getCreationDate());
                if (orderIndexDTO != null) {
                  this.saveOrderIndexToDbAndSolr(orderIndexDTO, purchaseOrderDTO.getCreationDate());
                }
              } catch (Exception e) {
                LOG.error("order index初始化失败，shop_id :" + shopId + ",order_id:" + purchaseOrderDTO.getId() + "系统继续初始化下一个order");
                LOG.error(e.getMessage(), e);
                continue;
              }
            }
          }

          //入库
          int totalNum = (int) txnService.countPurchaseInventoryByShopId(shopId, startTime, endTime);
          int pageSize = 100;

          if (totalNum >= 0) {
            int pageNum = totalNum % pageSize == 0 ? (totalNum / pageSize) : (totalNum / pageSize + 1);
            //分次读取数据
            for (int i = 0; i < pageNum; i++) {
              List<PurchaseInventoryDTO> purchaseInventoryDTOList = txnService.getPurchaseInventoryDTOList(shopId, i, pageSize, startTime, endTime);
              if (purchaseInventoryDTOList != null && purchaseInventoryDTOList.size() > 0) {
                for (PurchaseInventoryDTO purchaseInventoryDTO : purchaseInventoryDTOList) {
                  if (purchaseInventoryDTO != null && purchaseInventoryDTO.getId() != null) {
//                    if (purchaseInventoryDTO.getStatus() != null && purchaseInventoryDTO.getStatus().equals(OrderStatus.PURCHASE_INVENTORY_REPEAL)) {
//                      continue;
//                    }

                    try {
                      OrderIndexDTO orderIndexDTO = this.convertToOrderIndexDTO(null, null, null, null, purchaseInventoryDTO);
                      orderIndexDTO.setCreationDate(purchaseInventoryDTO.getCreationDate());
                      if (orderIndexDTO != null) {
                        this.saveOrderIndexToDbAndSolr(orderIndexDTO, purchaseInventoryDTO.getCreationDate());
                      }

                      if (purchaseInventoryDTO.getPurchaseOrderId() != null && purchaseInventoryDTO.getPurchaseOrderId() > 0) {
                        searchService.updatePurchaseOrderIndexStatus(orderIndexDTO.getShopId(), purchaseInventoryDTO.getPurchaseOrderId(), OrderStatus.PURCHASE_ORDER_DONE);

                        List<OrderIndexDTO> orderIndexDTOList = orderIndexService.getByOrderId(orderIndexDTO.getShopId(), purchaseInventoryDTO.getPurchaseOrderId());
                        if (orderIndexDTOList != null && orderIndexDTOList.size() == 1) {
                          OrderIndexDTO orderIndexDTO1 = orderIndexDTOList.get(0);
                          orderIndexDTO1.setOrderStatus(OrderStatus.PURCHASE_ORDER_DONE);
                          orderIndexDTO1.setOrderType(OrderTypes.PURCHASE);
                          Collection<OrderIndexDTO> orderIndexDTOList1 = new ArrayList<OrderIndexDTO>();
                          orderIndexDTOList1.add(orderIndexDTO1);
                          orderIndexService.addOrderIndexToSolr(orderIndexDTOList1);
                        }
                      }
                    } catch (Exception e) {
                      LOG.error("order index初始化失败，shop_id :" + shopId + ",order_id:" + purchaseInventoryDTO.getId() + "系统继续初始化下一个order");
                      LOG.error(e.getMessage(), e);
                      continue;
                    }
                  }
                }
              }
            }
          }
          LOG.info("后台结束初始化orderIndex,shop_id :" + shopId);
        } catch (Exception e) {
          LOG.error("order index初始化失败，shop_id :" + shopId + "系统继续初始化下一个店铺");
          LOG.error(e.getMessage(), e);
          continue;
        }
      }
    } catch (Exception e) {
      LOG.error("/init.do");
      LOG.error("method=initOrderIndex");
      LOG.error("order index初始化失败，shop_id :" + shopId + "系统返回，不再初始化");
      LOG.error(e.getMessage(), e);
      LOG.error(e.getMessage());
      return "/";
    }
    LOG.info("所有页面,后台结束初始化,跳转到登陆页面");
    return "stat/businessStatistics";
  }

  /**
   * 保存orderIndexDTO到数据库和solr中 如果有异常 抛出
   *
   * @param orderIndexDTO
   * @throws Exception
   */
  public void saveOrderIndexToDbAndSolr(OrderIndexDTO orderIndexDTO, long orderCreatedTime) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IOrderIndexService orderIndexService = ServiceManager.getService(IOrderIndexService.class);
    if (orderIndexDTO != null) {

      List<OrderIndexDTO> dtoList = searchService.getOrderIndexDTOByOrderId(orderIndexDTO.getShopId(), orderIndexDTO.getOrderId());
      if (dtoList == null || dtoList.size() <= 0) {
        searchService.saveOrUpdateOrderIndex(orderIndexDTO);
      }
      if (orderCreatedTime > 0) {
        orderIndexDTO.setCreationDate(orderCreatedTime);
      }

      List<OrderIndexDTO> orderIndexDTOs = new ArrayList<OrderIndexDTO>();
      orderIndexDTOs.add(orderIndexDTO);
      orderIndexService.addOrderIndexToSolr(orderIndexDTOs);

    }
  }

  /**
   * 把洗车单、销售单、施工单转换成OrderIndexDTO
   *
   * @param itemIndexDTO   洗车
   * @param salesOrderDTO  销售
   * @param repairOrderDTO 施工单
   * @return OrderIndexDTO
   * @throws Exception
   */
  public OrderIndexDTO convertToOrderIndexDTO(ItemIndexDTO itemIndexDTO, SalesOrderDTO salesOrderDTO, RepairOrderDTO repairOrderDTO,
                                              PurchaseOrderDTO purchaseOrderDTO, PurchaseInventoryDTO purchaseInventoryDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);

    //洗车
    if (itemIndexDTO != null) {
      OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
      orderIndexDTO.setShopId(itemIndexDTO.getShopId());
      orderIndexDTO.setOrderId(itemIndexDTO.getOrderId());
      orderIndexDTO.setOrderType(OrderTypes.WASH);
      orderIndexDTO.setOrderStatus(OrderStatus.WASH_SETTLED);
      orderIndexDTO.setOrderContent(itemIndexDTO.getItemName());
      orderIndexDTO.setOrderTotalAmount(itemIndexDTO.getOrderTotalAmount());
      orderIndexDTO.setCustomerOrSupplierId(itemIndexDTO.getCustomerId());
      orderIndexDTO.setVehicle(itemIndexDTO.getVehicle());
      orderIndexDTO.setArrears(0.0d);
      List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(itemIndexDTO.getCustomerId());
      if (customerRecordDTOList != null && customerRecordDTOList.size() > 0) {
        CustomerRecordDTO customerRecordDTO = customerRecordDTOList.get(0);
        orderIndexDTO.setCustomerOrSupplierName(customerRecordDTO.getName());
        orderIndexDTO.setContactNum(customerRecordDTO.getMobile());
        if (StringUtil.isEmpty(orderIndexDTO.getVehicle())) {
          orderIndexDTO.setVehicle(customerRecordDTO.getLicenceNo());
        }
      }
      if (itemIndexDTO.getOrderId() != null) {
        WashOrderDTO washOrderDTO = txnService.getWashOrder(itemIndexDTO.getOrderId());
        if (washOrderDTO != null && (!StringUtil.isEmpty(washOrderDTO.getWashWorker()))) {
          orderIndexDTO.setServiceWorker(washOrderDTO.getWashWorker());
        } else {
          orderIndexDTO.setServiceWorker(RfTxnConstant.ASSISTANT_NAME);
        }
      }

      return orderIndexDTO;
    } else if (salesOrderDTO != null) {
      OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
      orderIndexDTO.setShopId(salesOrderDTO.getShopId());
      orderIndexDTO.setOrderId(salesOrderDTO.getId());
      orderIndexDTO.setOrderType(OrderTypes.SALE);
      //orderIndex中的状态和salesOrderDTO的状态保持一致。
      if (salesOrderDTO.getStatus() == null) {
        orderIndexDTO.setOrderStatus(OrderStatus.SALE_DONE);
      } else {
        orderIndexDTO.setOrderStatus(salesOrderDTO.getStatus());
      }

      orderIndexDTO.setOrderTotalAmount(salesOrderDTO.getTotal());
      orderIndexDTO.setCustomerOrSupplierId(salesOrderDTO.getCustomerId());
      orderIndexDTO.setCustomerOrSupplierName(salesOrderDTO.getCustomer());
      List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(salesOrderDTO.getCustomerId());
      if (customerRecordDTOList != null && customerRecordDTOList.size() > 0) {
        CustomerRecordDTO customerRecordDTO = customerRecordDTOList.get(0);
        orderIndexDTO.setCustomerOrSupplierName(customerRecordDTO.getName());
        orderIndexDTO.setContactNum(customerRecordDTO.getMobile());
        orderIndexDTO.setVehicle(customerRecordDTO.getLicenceNo());
      }


      if (StringUtils.isEmpty(salesOrderDTO.getGoodsSaler())) {
        orderIndexDTO.setServiceWorker(RfTxnConstant.ASSISTANT_NAME);
      } else {
        orderIndexDTO.setServiceWorker(salesOrderDTO.getGoodsSaler());
      }

      String idString = " ( " + salesOrderDTO.getId().toString() + " ) ";
      StringBuffer str = new StringBuffer();
      str.append("销售内容:");
      List<ItemIndexDTO> itemIndexDTOs = searchService.getSalesOrderItemIndexList(salesOrderDTO.getShopId(), idString, "");
      if (itemIndexDTOs != null && itemIndexDTOs.size() > 0) {
        for (ItemIndexDTO dto : itemIndexDTOs) {
          if (dto == null) {
            continue;
          }
          str.append("(品名:").append(dto.getItemName());
          if (!StringUtils.isEmpty(dto.getItemBrand())) {
            str.append(",品牌:").append(dto.getItemBrand()).append(",单价:").append(dto.getItemPrice())
              .append("数量:").append(dto.getItemCount()).append(");");
          } else {
            str.append(",单价:").append(dto.getItemPrice()).append("数量:").append(dto.getItemCount())
              .append(");");
          }
        }
      }
      String orderContent = str.substring(0, str.length() - 1);
      if (orderContent.length() > 450) {
        orderContent = orderContent.substring(0, 450);
        orderContent = orderContent + "等";
      }
      orderIndexDTO.setOrderContent(orderContent);
      return orderIndexDTO;
    } else if (repairOrderDTO != null) {
      OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
      orderIndexDTO.setShopId(repairOrderDTO.getShopId());
      orderIndexDTO.setOrderId(repairOrderDTO.getId());
      orderIndexDTO.setOrderType(OrderTypes.REPAIR);
      orderIndexDTO.setOrderStatus(repairOrderDTO.getStatus());
      orderIndexDTO.setOrderTotalAmount(repairOrderDTO.getTotal());
      orderIndexDTO.setCustomerOrSupplierId(repairOrderDTO.getCustomerId());
      orderIndexDTO.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
      orderIndexDTO.setVehicle(repairOrderDTO.getVechicle());

      List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(repairOrderDTO.getCustomerId());
      if (customerRecordDTOList != null && customerRecordDTOList.size() > 0) {
        CustomerRecordDTO customerRecordDTO = customerRecordDTOList.get(0);
        if (StringUtil.isEmpty(orderIndexDTO.getCustomerOrSupplierName())) {
          orderIndexDTO.setCustomerOrSupplierName(customerRecordDTO.getName());
        }
        if (StringUtil.isEmpty(orderIndexDTO.getVehicle())) {
          orderIndexDTO.setVehicle(customerRecordDTO.getLicenceNo());
        }
        orderIndexDTO.setContactNum(customerRecordDTO.getMobile());
      }


      String serviceWorker = "";
      if (!StringUtils.isEmpty(repairOrderDTO.getServiceWorker())) {
        serviceWorker = serviceWorker + repairOrderDTO.getServiceWorker();
      }
      if (!StringUtils.isEmpty(repairOrderDTO.getProductSaler())) {
        if (StringUtils.isEmpty(serviceWorker)) {
          serviceWorker = serviceWorker + repairOrderDTO.getProductSaler();
        } else {
          serviceWorker = serviceWorker + "," + repairOrderDTO.getProductSaler();
        }
      }
      if (StringUtils.isEmpty(serviceWorker)) {
        serviceWorker = RfTxnConstant.ASSISTANT_NAME;
      }
      orderIndexDTO.setServiceWorker(serviceWorker);

      String idString = " ( " + repairOrderDTO.getId().toString() + " ) ";
      List<ItemIndexDTO> repairOrderItemList = searchService.getRepairOrderItemIndexList(repairOrderDTO.getShopId(), idString, "");

      StringBuffer strRepair = new StringBuffer();
      int repairIndex = 0;
      StringBuffer strSale = new StringBuffer();
      int saleIndex = 0;
      if (repairOrderItemList != null && repairOrderItemList.size() > 0) {
        for (ItemIndexDTO iDto : repairOrderItemList) {
          if (iDto == null) {
            continue;
          }
          if (iDto.getItemType().equals(ItemTypes.SERVICE)) {
            if (repairIndex == 0) {
              strRepair.append("施工内容:");
              repairIndex++;
            }
            if (!StringUtils.isEmpty(iDto.getItemName())) {
              strRepair.append("(");
              strRepair.append(iDto.getItemName());
            }
            if (iDto.getItemCount() != null && iDto.getItemPrice() != null) {
              strRepair.append(",").append(iDto.getItemCount() * iDto.getItemPrice());
              strRepair.append("元);");
            } else {
              strRepair.append(");");
            }
          } else if (iDto.getItemType().equals(ItemTypes.MATERIAL)) {
            if (saleIndex == 0) {
              strSale.append("销售内容:");
              saleIndex++;
            }
            if (!StringUtils.isEmpty(iDto.getItemName())) {
              strSale.append("(");
              strSale.append(iDto.getItemName());
            }
            if (iDto.getItemCount() != null && iDto.getItemPrice() != null) {
              strSale.append(",").append(iDto.getItemCount() * iDto.getItemPrice());
              strSale.append("元);");
            } else {
              strSale.append(");");
            }
          }
        }
      }
      String tmp = " ";
      String orderContent = strRepair.append(strSale.toString()).toString();
      if (orderContent.length() > 1) {
        tmp = orderContent.substring(0, orderContent.length() - 1);
      }
      if (tmp.length() > 450) {
        tmp = tmp.substring(0, 450);
        tmp = tmp + "等";
      }

      orderIndexDTO.setOrderContent(tmp);
      return orderIndexDTO;
    } else if (purchaseOrderDTO != null) {
      OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
      orderIndexDTO.setShopId(purchaseOrderDTO.getShopId());
      orderIndexDTO.setOrderId(purchaseOrderDTO.getId());
      orderIndexDTO.setOrderType(OrderTypes.PURCHASE);
      orderIndexDTO.setOrderStatus(purchaseOrderDTO.getStatus());

      StringBuffer orderContent = new StringBuffer();
      orderContent.append("采购商品:");
      List<ItemIndexDTO> itemIndexDTOList = searchService.getItemIndexDTOListByOrderId(orderIndexDTO.getShopId(), orderIndexDTO.getOrderId());
      if (itemIndexDTOList != null && itemIndexDTOList.size() > 0) {
        for (ItemIndexDTO purchaseOrderItem : itemIndexDTOList) {
          if (purchaseOrderItem == null) {
            continue;
          }

          orderContent.append("(");
          orderContent.append("品名:" + purchaseOrderItem.getItemName());
          if (!StringUtils.isEmpty(purchaseOrderItem.getItemBrand())) {
            orderContent.append("品牌:").append(purchaseOrderItem.getItemBrand());
          }
          if (purchaseOrderItem.getItemPrice() != null) {
            orderContent.append(",采购价:").append(purchaseOrderItem.getItemPrice());

          }
          if (purchaseOrderItem.getItemCount() != null) {
            orderContent.append("数量:").append(purchaseOrderItem.getItemCount());
          }
          orderContent.append(");");
        }
      }
      //数据表的长度为500，这里只存450个字符
      String str = orderContent.substring(0, orderContent.length() - 1);
      if (str.length() > 450) {
        str = str.substring(0, 450);
        str = str + "等";
      }
      orderIndexDTO.setOrderContent(str);

      orderIndexDTO.setOrderTotalAmount(purchaseOrderDTO.getTotal());
      orderIndexDTO.setCustomerOrSupplierId(purchaseOrderDTO.getSupplierId());
      orderIndexDTO.setCustomerOrSupplierName(purchaseOrderDTO.getSupplier());
      SupplierDTO supplierDTO = userService.getSupplierById(purchaseOrderDTO.getSupplierId());
      if (supplierDTO != null) {
        if (!StringUtil.isEmpty(supplierDTO.getMobile())) {
          orderIndexDTO.setContactNum(supplierDTO.getMobile());
        } else if (!StringUtil.isEmpty(supplierDTO.getLandLine())) {
          orderIndexDTO.setContactNum(supplierDTO.getLandLine());
        }
      }

      orderIndexDTO.setArrears(0d);
      if (StringUtils.isEmpty(purchaseOrderDTO.getBillProducer())) {
        orderIndexDTO.setServiceWorker(RfTxnConstant.ASSISTANT_NAME);
      } else {
        orderIndexDTO.setServiceWorker(purchaseOrderDTO.getBillProducer());
      }
      orderIndexDTO.setArrears(0.0d);
      return orderIndexDTO;
    } else if (purchaseInventoryDTO != null) {
      //生成orderIndexDTO
      OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
      orderIndexDTO.setShopId(purchaseInventoryDTO.getShopId());
      orderIndexDTO.setOrderId(purchaseInventoryDTO.getId());
      orderIndexDTO.setOrderType(OrderTypes.INVENTORY);
      //入库单状态
      orderIndexDTO.setOrderStatus(OrderStatus.PURCHASE_INVENTORY_DONE);

      orderIndexDTO.setOrderTotalAmount(purchaseInventoryDTO.getTotal());

      SupplierDTO supplierDTO = userService.getSupplierById(purchaseInventoryDTO.getSupplierId());
      if (supplierDTO != null) {
        if (!StringUtil.isEmpty(supplierDTO.getMobile())) {
          orderIndexDTO.setContactNum(supplierDTO.getMobile());
        } else if (!StringUtil.isEmpty(supplierDTO.getLandLine())) {
          orderIndexDTO.setContactNum(supplierDTO.getLandLine());
        }
      }

      if (StringUtils.isEmpty(purchaseInventoryDTO.getAcceptor())) {
        orderIndexDTO.setServiceWorker(RfTxnConstant.ASSISTANT_NAME);
      } else {
        orderIndexDTO.setServiceWorker(purchaseInventoryDTO.getAcceptor());
      }

      StringBuffer str = new StringBuffer();
      List<ItemIndexDTO> itemIndexDTOList = searchService.getItemIndexDTOListByOrderId(orderIndexDTO.getShopId(), orderIndexDTO.getOrderId());
      if (itemIndexDTOList != null && itemIndexDTOList.size() > 0) {
        str.append("入库商品:");
        for (ItemIndexDTO inventoryDTO : itemIndexDTOList) {
          str.append("(").append("品名:").append(inventoryDTO.getItemName());
          if (!StringUtils.isEmpty(inventoryDTO.getItemBrand())) {
            str.append("品牌:").append(inventoryDTO.getItemBrand());
          }
          if (inventoryDTO.getItemPrice() != null && inventoryDTO.getItemPrice() > 0.0) {
            str.append(",单价:").append(inventoryDTO.getItemPrice());
          }
          if (inventoryDTO.getItemCount() != null && inventoryDTO.getItemCount() > 0.0) {
            str.append(",数量:").append(inventoryDTO.getItemCount());
          }
          str.append(");");
        }
      }
      String orderContent = "";
      if (str.length() > 1) {
        orderContent = str.substring(0, str.length() - 1);
      }

      if (orderContent.length() > 450) {
        orderContent = orderContent.substring(0, 450);
        orderContent = orderContent + "等";
      }
      orderIndexDTO.setOrderContent(orderContent);
      orderIndexDTO.setCustomerOrSupplierId(purchaseInventoryDTO.getSupplierId());
      orderIndexDTO.setCustomerOrSupplierName(purchaseInventoryDTO.getSupplier());
      orderIndexDTO.setArrears(0.0d);
      return orderIndexDTO;
    }
    return null;
  }

  @RequestMapping(params = "method=initCustomerRecord")
  public String initCustomRecord(ModelMap model, HttpServletRequest request) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    long shopId = (Long) request.getSession().getAttribute("shopId");
    if (shopId != 0) {
      return "/";
    }

    try {
      List<Shop> shopList = configService.getShop();
      if (shopList != null && shopList.size() > 0) {
        for (Shop shop : shopList) {
          if (shop == null || shop.getId() == null) {
            continue;
          }

          try {
            shopId = shop.getId();

            int pageSize = 100;//每次查询的最大条数

            LOG.info("店面shop_id:" + shopId + ",后台开始初始化Receivable");

            //分页查询 先获得该店面下实收的条数
            int receivableTotalNumber = (int) txnService.countReceivableDTOByShopId(shopId);
            if (receivableTotalNumber <= 0) {
              LOG.info("店面shop_id:" + shopId + ",后台结束初始化Receivable，该店面没有Receivable记录");
              continue;
            }
            //页数
            int pageNum = receivableTotalNumber % pageSize == 0 ? (receivableTotalNumber / pageSize) : (receivableTotalNumber / pageSize + 1);
            //分次读取数据
            for (int i = 0; i < pageNum; i++) {
              List<ReceivableDTO> receivableDTOList = txnService.getReceivableDTOList(shopId, i, pageSize);
              if (receivableDTOList != null && receivableDTOList.size() > 0) {
                for (ReceivableDTO receivableDTO : receivableDTOList) {
                  if (receivableDTO != null && receivableDTO.getId() != null) {
                    //更新实收表
                    try {
                      txnService.updateReceivable(receivableDTO);
                    } catch (Exception e) {
                      LOG.error("初始化Receivable，shop_id :" + shopId + ",Receivable_id:" + receivableDTO.getId() + "系统继续初始化下一条receivable");
                      LOG.error(e.getMessage(), e);
                      continue;
                    }
                  }
                }
              }
            }
            LOG.info("店面shop_id:" + shopId + ",后台结束初始化Receivable");

            LOG.info("店面shop_id:" + shopId + ",后台开始初始化CustomRecord");

            //分页查询 先获得该店面下customerRecord的条数
            int totalNumber = (int) userService.countShopCustomerRecord(shopId);
            if (totalNumber <= 0) {
              LOG.info("店面shop_id:" + shopId + ",后台结束初始化CustomRecord，该店面没有customerRecord记录");
              continue;
            }


            //页数
            int pageTotalNum = totalNumber % pageSize == 0 ? (totalNumber / pageSize) : (totalNumber / pageSize + 1);
            //分次读取数据
            for (int index = 0; index < pageTotalNum; index++) {
              List<CustomerRecordDTO> customerRecordDTOList = userService.getShopCustomerRecordDTO(shopId, index, pageSize);
              if (customerRecordDTOList != null && customerRecordDTOList.size() > 0) {
                for (CustomerRecordDTO customerRecordDTO : customerRecordDTOList) {
                  if (customerRecordDTO == null) {
                    continue;
                  }
                  if (customerRecordDTO.getCustomerId() != null) {
                    long customerId = customerRecordDTO.getCustomerId();

                    double washTotal = 0.0; //洗车单金额
                    double salesTotal = 0;//销售单金额
                    double repairTotal = 0;//施工单金额
                    double newDebtTotal = 0;//新统计的总欠款

                    //洗车单总金额
                    washTotal = searchService.getWashTotalByCustomerId(shopId, customerId);

                    //销售单金额
                    List<SalesOrderDTO> salesOrderDTOList = txnService.getSalesOrderDTOListByCustomerId(shopId, customerId);
                    if (salesOrderDTOList != null && salesOrderDTOList.size() > 0) {
                      for (SalesOrderDTO salesOrderDTO : salesOrderDTOList) {
                        if (salesOrderDTO == null || salesOrderDTO.getId() == null) {
                          continue;
                        }

                        salesTotal += salesOrderDTO.getTotal();
                        //欠款
                        ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(salesOrderDTO.getShopId(), OrderTypes.SALE, salesOrderDTO.getId());
                        if (receivableDTO != null) {
                          newDebtTotal += receivableDTO.getDebt();
                        } else {
                          LOG.info("店面shop_id:" + shopId + ",sales_order_id:" + salesOrderDTO.getId() + ",该id下没有receiveable表记录");
                        }
                      }
                    }

                    //施工单金额
                    List<RepairOrderDTO> repairOrderDTOList = txnService.getRepairOrderDTOListByCustomerId(shopId, customerId);
                    if (repairOrderDTOList != null && repairOrderDTOList.size() > 0) {
                      for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
                        if (repairOrderDTO == null && repairOrderDTO.getId() == null) {
                          continue;
                        }
                        repairTotal += repairOrderDTO.getTotal();
                        //欠款
                        ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(repairOrderDTO.getShopId(), OrderTypes.REPAIR, repairOrderDTO.getId());
                        if (receivableDTO != null) {
                          newDebtTotal += receivableDTO.getDebt();
                        } else {
                          LOG.info("店面shop_id:" + shopId + ",repair_order_id:" + repairOrderDTO.getId() + ",该id下没有receiveable表记录");
                        }
                      }
                    }
                    //比较总金额 和原金额是否相等
                    double newTotal = washTotal + salesTotal + repairTotal;
                    BigDecimal newBigDecimal = new BigDecimal(String.valueOf(newTotal));
                    newTotal = newBigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    newBigDecimal = new BigDecimal(String.valueOf(newTotal));

                    double oldTotal = customerRecordDTO.getTotalAmount();
                    BigDecimal oldBigDecimal = new BigDecimal(String.valueOf(oldTotal));
                    oldTotal = oldBigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

                    int i = newBigDecimal.compareTo(oldBigDecimal);

                    //比较总欠款 和原欠款是否相等

                    BigDecimal newDebtBigDecimal = new BigDecimal(String.valueOf(newDebtTotal));
                    newDebtTotal = newDebtBigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    newDebtBigDecimal = new BigDecimal(String.valueOf(newDebtTotal));

                    double oldDebtTotal = customerRecordDTO.getTotalReceivable();
                    BigDecimal oldDebtBigDecimal = new BigDecimal(String.valueOf(oldDebtTotal));
                    oldDebtTotal = oldDebtBigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

                    int j = newDebtBigDecimal.compareTo(oldDebtBigDecimal);

                    if (i != 0 || j != 0) {//需要更新
                      try {
                        customerRecordDTO.setTotalAmount(newTotal);
                        customerRecordDTO.setTotalReceivable(newDebtTotal);
                        userService.updateCustomerRecordByMigration(customerRecordDTO);
                      } catch (Exception e) {
                        LOG.error("初始化CustomerRecord出错，shop_id :" + shopId + ",customerRecordId:" + customerRecordDTO.getId() + "系统继续初始化下一条CustomerRecord");
                        LOG.error(e.getMessage(), e);
                        continue;
                      }
                    }
                  }
                }
              }
            }
            LOG.info("店面shop_id:" + shopId + ",后台结束初始化CustomRecord");
          } catch (Exception e) {
            LOG.error("CustomerRecord初始化失败，shop_id :" + shopId + "系统继续初始化下一个店铺");
            LOG.error(e.getMessage(), e);
            continue;
          }
        }
      }
    } catch (Exception e) {
      LOG.error("/init.do");
      LOG.error("method=initCustomRecord");
      LOG.error("CustomerRecord初始化失败，出现严重问题，shop_id :" + shopId + "系统返回，不再初始化");
      LOG.error(e.getMessage(), e);
      return "/";
    }

    LOG.info("所有店面后台结束初始化CustomRecord");
    LOG.info("初始化没有出现异常,跳转到营业统计页面");
    return "stat/businessStatistics";
  }

  @RequestMapping(params = "method=initWashCardToMember")
  public String initWashCardToMember(ModelMap model, HttpServletRequest request) {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
    try {
      List<MemberDTO> memberDTOs = membersService.doWashCardToMemberInit();
      memberDTOs = txnService.doServiceAndCategoryInit(memberDTOs);
      membersService.doMemberServiceInit(memberDTOs);
    } catch (Exception e) {
      LOG.error("/init.do");
      LOG.error("method=initWashCardToMember");
      LOG.error("Member初始化失败，出现严重问题，系统返回，不再初始化");
      LOG.error(e.getMessage(), e);
      e.printStackTrace();
    }
    return "/login";
  }

  @RequestMapping(params = "method=initItemIndexProductId")
  public String initItemIndexProductId(ModelMap model, HttpServletRequest request) {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    try {
      Long startTime = System.currentTimeMillis();
      rfiTxnService.initIntemIndexProductId(model);
      Long endTime = System.currentTimeMillis();
      LOGGER.info("对itemIndex执行productId 初始化 结果:如下 ");
      LOGGER.info("对itemIndex执行productId 初始化 结果:成功更新{}条数据 ", model.get("successUpdateCount"));
      LOGGER.info("对itemIndex执行productId 初始化 结果:更新失败{}条数据 ", model.get("failUpdateCount"));
      LOGGER.info("对itemIndex执行productId 初始化 结果:更新失败的itemID为{} ", model.get("itemDTOItemIndexFailUpdate"));
      LOGGER.info("对itemIndex执行productId 初始化 结果:{}条数据productId已经存在不需要更新 ", model.get("noNeedUpdateCount"));
      LOGGER.info("对itemIndex执行productId 初始化 结果:itemIndex中itemID为{}的txn库记录对应的记录找不到", model.get("itemDTONotFound"));
      LOGGER.info("对itemIndex执行productId 初始化 结果:itemIndex中itemID为{}的txn库记录对应的记录找不到productID", model.get("itemDTOProductIDNotFound"));
      LOGGER.info("对itemIndex执行productId 初始化 结果:itemIndex中itemID为{}记录没有对应的ordertype无法更新productID", model.get("itemOrderTypeNotFount"));
      LOGGER.info("对itemIndex执行productId 初始化 共耗时{}秒 ", (endTime - startTime) / 1000);
    } catch (Exception e) {
      LOG.error("/init.do \n method=initItemIndexProductId 失败{}", e);
    }
    return "/login";
  }

  /**
   * Department 初始化
   */
  @RequestMapping(params = "method=initSaleMansDepartment")
  public void initSaleMansDepartment(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    PrintWriter print = response.getWriter();
    UserWriter writer = userDaoManager.getWriter();
    Set<String> set = new HashSet<String>();
    Object status;
    Map<String, Long> maps = new HashMap<String, Long>();

    status = writer.begin();
    Department d;
    try {
      //获得所有的SalesMan
      List<SalesMan> salesManList = writer.getAllSalesManForInitPermission();
      for (SalesMan sm : salesManList) {
        if (StringUtils.isBlank(sm.getDepartment())) continue;
        if (set.add(sm.getShopId() + "-" + sm.getDepartment())) {
          d = new Department();
          d.setName(sm.getDepartment());
          d.setShopId(sm.getShopId());
          d.setStatus(Status.active);
          writer.save(d);
          maps.put(d.getShopId() + "-" + d.getName(), d.getId());
        }
        sm.setDepartmentId(maps.get(sm.getShopId() + "-" + sm.getDepartment()));
        writer.update(sm);
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error(e.getMessage() + "Failed to initialize position！", e);
    } finally {
      writer.rollback(status);
    }
    print.write("SalesMan department init success!\n");
  }

  /**
   * 权限线上初始化
   * 员工
   */
  @RequestMapping(params = "method=initPermission")
  public void initPermission(HttpServletRequest request, HttpServletResponse response) throws IOException {
    IUserGroupService userGroupService = ServiceManager.getService(IUserGroupService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    PrintWriter print = response.getWriter();
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<SalesMan> salesManList = writer.getAllSalesMan();
      List<User> userList = writer.getAllUser();
      for (SalesMan sm : salesManList) {
        Iterator iterator = userList.iterator();
        while (iterator.hasNext()) {
          User user = (User) iterator.next();
          if (user.getShopId() == null) throw new Exception("user " + user.getId() + " shopId is null");
          if (sm.getShopId() == null) throw new Exception("SalesMan " + sm.getId() + " shopId is null");
          if (sm.getName() == null)
            throw new Exception("SalesMan " + sm.getName() + " name is null,please execute sql first!");
          if (user.getName() == null)
            throw new Exception("user " + user.getName() + " name is null,please execute sql first!");
          if (NumberUtil.isEqual(user.getShopId(), sm.getShopId()) && user.getName().equals(sm.getName())) {
            user.setSalesManId(sm.getId());
            user.setDepartmentId(sm.getDepartmentId());
            writer.save(user);
            print.write("user id[" + user.getId() + "] salesManId[" + user.getSalesManId() + "] success!\n");
            iterator.remove();
            break;
          }
        }
      }
      SalesManDTO salesMan;
      //把user 保存在 salesman
      for (User u : userList) {
        UserGroupDTO userGroup = userGroupService.getUserGroupByUserId(u.getId());
        if (userGroup == null)
          throw new Exception("user id[" + u.getId() + "] userGroup is null, 先处理脏数据【该账号不能登录系统，建议删除或默认分配一个该版本下的用户组】。");
        salesMan = new SalesManDTO();
        salesMan.setName(u.getName());
        salesMan.setMobile(u.getMobile());
        if (u.getDepartmentId() == null) {
          salesMan.setDepartmentName(SalesManDTO.defaultEmptyDepartment);
        } else {
          salesMan.setDepartmentId(u.getDepartmentId());
        }
        salesMan.setShopId(u.getShopId());
        salesMan.setUserGroupId(userGroup.getId());
        salesMan.setStatus(SalesManStatus.INSERVICE);
        salesMan = userService.saveOrUpdateSalesMan(salesMan, writer);
        u.setSalesManId(salesMan.getId());
        writer.update(u);
      }
      writer.commit(status);
      print.write("salesMan init success!\n");
    } catch (Exception e) {
      LOG.error(e.getMessage() + "职位初始化失败！", e);
      print.write("salesMan init rollback!\n");
    } finally {
      writer.rollback(status);
    }

  }

  /**
   * 权限线上初始化
   * 员工
   */
  @RequestMapping(params = "method=initUser")
  public void initUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IUserGroupService userGroupService = ServiceManager.getService(IUserGroupService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    PrintWriter print = response.getWriter();
    UserWriter writer = userDaoManager.getWriter();
    List<User> userList = writer.getAllUser();
    Map<Long, List<User>> map = new HashMap<Long, List<User>>();
    List<User> temp;
    for (User u : userList) {
      if (u.getUserNo().equals("130637099612")) {
        System.out.print(u);
      }
      if (CollectionUtils.isEmpty(map.get(u.getShopId()))) {
        temp = new ArrayList<User>();
        temp.add(u);
        map.put(u.getShopId(), temp);
        continue;
      }
      map.get(u.getShopId()).add(u);
    }
    Object status = writer.begin();
    try {
      for (Map.Entry entry : map.entrySet()) {
        temp = (List<User>) entry.getValue();
        if (CollectionUtils.isEmpty(temp)) continue;
        if (temp.size() == 1) {
          temp.get(0).setUserType(UserType.SYSTEM_CREATE);
          writer.update(temp.get(0));
        } else {
          boolean flag = true;
          for (User user : temp) {
            UserGroupDTO userGroup = null;
            if (flag) {
              userGroup = userGroupService.getUserGroupByUserId(user.getId());
              if (userGroup == null)
                throw new Exception("user id[" + user.getId() + "] userGroup is null, 先处理脏数据【该账号不能登录系统，建议删除或默认分配一个该版本下的用户组】。");
              if (UserGroupType.SHOP_GROUPNAME_ADMIN.getValue().equals(userGroup.getName()) || "系统管理".equals(userGroup.getName())) {
                flag = false;
                user.setUserType(UserType.SYSTEM_CREATE);
              } else {
                user.setUserType(UserType.NORMAL);
              }
            } else {
              user.setUserType(UserType.NORMAL);
            }
            writer.update(user);
          }
        }
      }
      writer.commit(status);
      print.write("user init success!\n");
    } catch (Exception e) {
      LOG.error(e.getMessage() + "user初始化失败！", e);
      print.write("user init rollback!\n");
    } finally {
      writer.rollback(status);
    }

  }

  @RequestMapping(params = "method=initUserGuideStep")
  public void initUserGuideStep(HttpServletRequest request, HttpServletResponse response) throws Exception {
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    PrintWriter print = response.getWriter();
    UserWriter writer = userDaoManager.getWriter();
    List<User> userList = writer.getAllUser();
    Object status = writer.begin();
    try {
      List<UserGuideFlow> userGuideFlows = ServiceManager.getService(UserDaoManager.class).getWriter().getAllUserGuideFlows();
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      for (User u : userList) {
        if (NumberUtil.isEqual(u.getShopId(), 0l)) continue;
        if (UserType.SYSTEM_CREATE == u.getUserType()) {
          ShopDTO shopDTO = configService.getShopById(u.getShopId());
          if (shopDTO == null) continue;
          if (shopDTO.getShopVersionId() == null) continue;
          UserGuideHistory history;
          boolean flag = false;
          for (UserGuideFlow userGuideFlow : userGuideFlows) {
            if (userGuideFlow.getShopVersions().contains(String.valueOf(shopDTO.getShopVersionId()))) {
              history = writer.getUserGuideHistoryByFlowName(u.getId(), userGuideFlow.getName());
              if (history != null) {
                continue;
              }
              history = new UserGuideHistory();
              history.setStatus(com.bcgogo.enums.user.userGuide.Status.WAITING);
              history.setUserId(u.getId());
              history.setCurrentStep(userGuideFlow.getFirstStepName());
              history.setFlowName(userGuideFlow.getName());
              flag = true;
              writer.save(history);
            }
          }
          if (flag) {
            u.setHasUserGuide(YesNo.YES);
            u.setFinishUserGuide(YesNo.NO);
            writer.update(u);
          }
        }
      }
      writer.commit(status);
      print.write("user init success!\n");
    } catch (Exception e) {
      LOG.error(e.getMessage() + "user初始化失败！", e);
      print.write("user init rollback!\n");
    } finally {
      writer.rollback(status);
    }
  }

  //初始化试用版待支付记录
  @RequestMapping(params = "method=initBcgogoReceivable")
  public void initBcgogoReceivable(HttpServletRequest request, HttpServletResponse response) throws IOException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    List<Shop> shopList = configService.getShop();
    for (Shop s : shopList) {
      //使用的产生一条待支付记录
      if (ShopStatus.REGISTERED_TRIAL == s.getShopStatus()) {
        try {
          receivableService.createSoftwareReceivable(s.getId(),WebUtil.getUserId(request),WebUtil.getUserName(request), BuyChannels.BACKGROUND_ENTRY);
        } catch (Exception e) {
          LOG.info(e.getMessage());
          response.getWriter().write("shop [" + s.getId() + "] failed," + e.getMessage() + "!\n");
        }
      }
    }
    response.getWriter().write("BcgogoReceivable init success!\n");
  }

  //初始化赠送支付记录
  @RequestMapping(params = "method=initShopSmsAccount")
  public void initShopSmsAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ISmsDonationLogService smsDonationLogService = ServiceManager.getService(ISmsDonationLogService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    List<Shop> shopList = configService.getShop();
    List<SmsDonationLog> smsDonationLogs;
    ShopSmsRecordDTO shopSmsRecordDTO;
    List<ShopSmsRecord> shopSmsRecords;
    for (Shop s : shopList) {
      smsDonationLogs = smsDonationLogService.getSmsDonationLogByShopId(s.getId());
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        for (SmsDonationLog log : smsDonationLogs) {
          shopSmsRecords = writer.getShopSmsRecordByShopId(log.getShopId(), log.getDonationTime());
          if (!shopSmsRecords.isEmpty()) continue;
          shopSmsRecordDTO = new ShopSmsRecordDTO();
          if (log.getDonationType() == DonationType.REGISTER_ACTIVATE) {
            shopSmsRecordDTO.setSmsCategory(SmsCategory.REGISTER_HANDSEL);
          } else {
            shopSmsRecordDTO.setSmsCategory(SmsCategory.RECOMMEND_HANDSEL);
          }
          shopSmsRecordDTO.setShopId(log.getShopId());
          shopSmsRecordDTO.setOperateTime(log.getDonationTime());
          shopSmsRecordDTO.setBalance(log.getValue());
          shopSmsRecordDTO.setNumber(Math.round(log.getValue() * 10));
          ShopSmsAccount account = writer.getShopSmsAccountByShopId(log.getShopId());
          if (account == null) {
            account = new ShopSmsAccount(log.getShopId());
          }
          account.setHandSelBalance(account.getHandSelBalance() + shopSmsRecordDTO.getBalance());
          account.setHandSelNumber(account.getRechargeNumber() + shopSmsRecordDTO.getNumber());

          account.setCurrentBalance(shopSmsRecordDTO.getBalance() + account.getCurrentBalance());
          account.setCurrentNumber(shopSmsRecordDTO.getNumber() + account.getCurrentNumber());

          writer.saveOrUpdate(new ShopSmsRecord(shopSmsRecordDTO));
          writer.saveOrUpdate(new BcgogoSmsRecord(shopSmsRecordDTO));
          writer.saveOrUpdate(account);
        }
        writer.commit(status);
      } catch (Exception e) {
        LOG.error(e.getMessage());
      } finally {
        writer.rollback(status);
      }
    }
    response.getWriter().write("init shop sms account!\n");
  }

  //初始化剩余短信记录
  @RequestMapping(params = "method=initShopSmsBalanceAccount")
  public void initShopSmsBalanceAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    List<Shop> shopList = configService.getShop();
    for (Shop s : shopList) {
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(s.getId());
        if (shopBalanceDTO == null) continue;
        ShopSmsAccount account = writer.getShopSmsAccountByShopId(s.getId());
        if (account == null) {
          account = new ShopSmsAccount(s.getId());
        }
        account.setCurrentBalance(account.getHandSelBalance() + shopBalanceDTO.getSmsBalance());
        account.setCurrentNumber(account.getRechargeNumber() + Math.round(shopBalanceDTO.getSmsBalance() * 10));
        writer.saveOrUpdate(account);
        writer.commit(status);
      } catch (Exception e) {
        LOG.error(e.getMessage());
      } finally {
        writer.rollback(status);
      }
    }
    response.getWriter().write("init shop sms balance account!\n");
  }


  //初始化充值记录
  @RequestMapping(params = "method=initShopSmsRechargeAccount")
  public void initShopSmsRechargeAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    List<Shop> shopList = null;
    shopList = configService.getShop();
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (Shop s : shopList) {
        List<SmsRechargeDTO> smsRechargeDTOs = smsRechargeService.getShopSmsRechargeList(s.getId(), 0, 100);
        for (SmsRechargeDTO smsRechargeDTO : smsRechargeDTOs) {
          ShopSmsRecordDTO dto = new ShopSmsRecordDTO();
          dto.setShopId(smsRechargeDTO.getShopId());
          dto.setBalance(smsRechargeDTO.getRechargeAmount());
          dto.setNumber(Math.round(smsRechargeDTO.getRechargeAmount() * 10));
          dto.setSmsCategory(SmsCategory.SHOP_RECHARGE);
          ShopSmsAccount account = writer.getShopSmsAccountByShopId(dto.getShopId());
          if (account == null) {
            account = new ShopSmsAccount(dto.getShopId());
          }
          writer.save(account);
          account.setRechargeBalance(account.getRechargeBalance() + dto.getBalance());
          account.setRechargeNumber(account.getRechargeNumber() + dto.getNumber());

          writer.saveOrUpdate(new ShopSmsRecord(dto));
          writer.saveOrUpdate(new BcgogoSmsRecord(dto));
          writer.saveOrUpdate(account);

        }
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error(e.getMessage());
    } finally {
      writer.rollback(status);
    }
    response.getWriter().write("init Shop Sms Recharge Account!\n");
  }


  //初始化充值记录
  @RequestMapping(params = "method=initShopSmsOutBoxAccount")
  public void initShopSmsOutBoxAccount(HttpServletRequest request, HttpServletResponse response, Long maxId) throws Exception {
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    Long startId = 0l;
    try {
      List<OutBox> outBoxList;
      Long number, shopId = null, time;
      Map<String, Long> shopNumberMap;
      Map<Long, Long> bcgogoNumberMap;
      boolean isSkip = false;
      while (true) {
        outBoxList = notificationService.getOutBox(PAGE_SIZE, startId);
        shopNumberMap = new HashMap<String, Long>();
        bcgogoNumberMap = new HashMap<Long, Long>();
        if (CollectionUtils.isEmpty(outBoxList)) break;
        for (OutBox outBox : outBoxList) {
          time = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", DateUtil.convertDateLongToDateString("yyyy-MM-dd", outBox.getCreationDate()));
          startId = outBox.getId();
          if (startId > maxId) {
            isSkip = true;
            break;
          }
          if (SenderType.bcgogo == outBox.getSender() || (outBox.getShopId() != null && outBox.getShopId().equals(-1L))) {
            if (outBox.getShopId() == null) continue;
            if (bcgogoNumberMap.get(time) == null)
              number = (long) SmsUtil.calculateSmsNum(outBox.getContent(), outBox.getSendMobile());
            else {
              number = bcgogoNumberMap.get(time) + SmsUtil.calculateSmsNum(outBox.getContent(), outBox.getSendMobile());
            }
            bcgogoNumberMap.put(time, number);
          } else {
            if (outBox.getShopId() == null) continue;
            if (shopNumberMap.get(outBox.getShopId() + "-" + time) == null)
              number = (long) SmsUtil.calculateSmsNum(outBox.getContent(), outBox.getSendMobile());
            else {
              number = shopNumberMap.get(outBox.getShopId() + "-" + time) + SmsUtil.calculateSmsNum(outBox.getContent(), outBox.getSendMobile());
            }
            shopNumberMap.put(outBox.getShopId() + "-" + time, number);
          }
        }

        for (Map.Entry<Long, Long> entrySet : bcgogoNumberMap.entrySet()) {
          time = entrySet.getKey();
          number = entrySet.getValue();
          createBcgogoConsumption(number / 10.0, number, writer, time);
        }

        for (Map.Entry<String, Long> entrySet : shopNumberMap.entrySet()) {
          shopId = Long.valueOf(entrySet.getKey().split("-")[0]);
          time = Long.valueOf(entrySet.getKey().split("-")[1]);
          number = entrySet.getValue();
          createShopSmsConsumption(shopId, number / 10.0, number, writer, time);
        }
        if (isSkip) {
          break;
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
//    configService.setConfig("SMS_ACCOUNT_OUT_BOX_START_ID", String.valueOf(startId), ShopConstant.BC_SHOP_ID);
    response.getWriter().write("init Shop Sms OutBox Account!\n");
  }

  public void createBcgogoConsumption(double balance, long number, TxnWriter writer, Long time) throws Exception {
    BcgogoSmsRecord bcgogoSmsRecord = new BcgogoSmsRecord(balance, number);
    bcgogoSmsRecord.setSmsCategory(SmsCategory.BCGOGO_CONSUME);
    bcgogoSmsRecord.setOperateTime(time);
    writer.saveOrUpdate(bcgogoSmsRecord);
  }

  public void createShopSmsConsumption(long shopId, double balance, long number, TxnWriter writer, Long time) throws Exception {
    //bcgogo账单中每天生成一条 店铺消费短信记录
    BcgogoSmsRecord bcgogoSmsRecord = new BcgogoSmsRecord(balance, number);
    bcgogoSmsRecord.setSmsCategory(SmsCategory.SHOP_CONSUME);
    bcgogoSmsRecord.setOperateTime(time);

    //shop账单详细 对应店铺每天生成一条 消费短信记录
    ShopSmsRecord shopSmsRecord = new ShopSmsRecord(shopId, balance, number);
    shopSmsRecord.setSmsCategory(SmsCategory.SHOP_CONSUME);
    shopSmsRecord.setOperateTime(time);

    //shop账单 中对应店铺统计更新
    ShopSmsAccount account = writer.getShopSmsAccountByShopId(shopId);
    if (account == null) {
      account = new ShopSmsAccount(shopId);
    }
    account.setConsumptionBalance(account.getConsumptionBalance() + balance);
    account.setConsumptionNumber(account.getConsumptionNumber() + number);

    writer.saveOrUpdate(shopSmsRecord);
    writer.saveOrUpdate(bcgogoSmsRecord);
    writer.saveOrUpdate(account);
  }

  /**
   * 初始化shop 与销售关系
   */
  @ResponseBody
  @RequestMapping(params = "method=initShopFollowId")
  public Result initShopFollowId(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserWriter userWriter = ServiceManager.getService(UserDaoManager.class).getWriter();
    ConfigWriter configWriter = ServiceManager.getService(ConfigDaoManager.class).getWriter();
    Object status = configWriter.begin();
    try {
      List<Shop> shopList = configWriter.getShop();
      for (Shop shop : shopList) {
        if (shop.getAgentId() == null) {
          SaleManShopMap map = configWriter.getSaleManShopMapByShopId(shop.getId());
          if (map == null || map.getUserId() == null) {
          } else{
            User user = userWriter.getById(User.class, map.getUserId());
            if (user != null) {
              shop.setAgent(user.getName());
              shop.setAgentId(user.getUserNo());
              shop.setAgentMobile(user.getMobile());
              shop.setFollowId(user.getId());
              shop.setFollowName(user.getName());
            }
          }
        }else{
          List<User> users = userWriter.getAllStateUserByUserNo(shop.getAgentId());
          boolean flag=false;
          if (CollectionUtil.isNotEmpty(users)) {
            if (users.size() > 1) {
              for (User user : users) {
                if (user.getStatus() != Status.deleted) {
                  shop.setFollowId(user.getId());
                  shop.setFollowName(user.getName());
                  flag = true;
                  break;
                }
              }
            } else if (!flag || users.size() == 1) {
              shop.setFollowId(users.get(0).getId());
              shop.setFollowName(users.get(0).getName());
            }
          } else {
            LOG.error("can't find user by agent id:{}", shop.getAgentId());
          }
        }
        configWriter.save(shop);
        List<MaintainShopLog> maintainShopLogs = configWriter.getMaintainShopLog(shop.getId());
        if (shop.getFollowId() != null && shop.getId() != null) {
          if (CollectionUtils.isEmpty(maintainShopLogs)
            || (CollectionUtils.isNotEmpty(maintainShopLogs)
            && !maintainShopLogs.get(0).getUserId().equals(shop.getFollowId()))) {
            configWriter.save(new MaintainShopLog(shop.getId(), shop.getFollowId(), shop.getShopStatus()));
          }
        }
      }
      configWriter.commit(status);
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      return new Result(e.getMessage() , false);
    } finally {
      configWriter.rollback(status);
    }
    return new Result("success", true);
  }

  /**
   * 初始化shop 与销售关系
   */
  @RequestMapping(params = "method=initShopSaleMap")
  public void initShopSaleMap(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserWriter userWriter = ServiceManager.getService(UserDaoManager.class).getWriter();
    ConfigWriter configWriter = ServiceManager.getService(ConfigDaoManager.class).getWriter();
    List<User> userList = userWriter.getUserByShopId(ShopConstant.BC_ADMIN_SHOP_ID);
    PrintWriter print = response.getWriter();
    Map<String, User> userMap = new HashMap<String, User>();
    for (User u : userList) {
      userMap.put(u.getUserNo(), u);
    }
    List<Shop> shopList = configWriter.getShop();
    List<SaleManShopMap> saleManShopMapList = new ArrayList<SaleManShopMap>();
    SaleManShopMap saleManShopMap;
    for (Shop shop : shopList) {
      if (shop.getAgentId() != null) {
        User user = userMap.get(shop.getAgentId().toString());
        if (user != null) {
          saleManShopMap = new SaleManShopMap();
          saleManShopMap.setShopId(shop.getId());
          saleManShopMap.setUserId(user.getId());
          saleManShopMapList.add(saleManShopMap);
        } else {
          print.write("shop id[" + shop.getId() + "] can't find [userNo" + shop.getAgentId() + "].This shop temporary assigned to jackchen \n");
          saleManShopMap = new SaleManShopMap();
          saleManShopMap.setShopId(shop.getId());
          saleManShopMap.setUserId(1l);
          saleManShopMapList.add(saleManShopMap);
        }
      } else {
        print.write("shop id[" + shop.getId() + "] can't find [userNo" + shop.getAgentId() + "].This shop temporary assigned to jackchen \n");
        saleManShopMap = new SaleManShopMap();
        saleManShopMap.setShopId(shop.getId());
        saleManShopMap.setUserId(1l);
        saleManShopMapList.add(saleManShopMap);
      }
    }
    Object status = configWriter.begin();
    try {
      for (SaleManShopMap map : saleManShopMapList) {
        configWriter.save(map);
      }
      configWriter.commit(status);
    } catch (Exception e) {
      LOG.error(e.getMessage() + "职位初始化失败！", e);
      print.write("Initialization shop and sales relationship fail!\n");
    } finally {
      configWriter.rollback(status);
    }
    print.write("Initialization shop and sales relationship success!\n");
  }


  /**
   * 员工管理上线前初始化用户为员工
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=initUserToSalesMan")
  public String initUserToSalesMan(HttpServletRequest request) {
    LOG.info("用户开始初始化到员工");
    LOG.info("开始时间:" + DateUtil.dateLongToStr(System.currentTimeMillis()));

    try {
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      long shopId = (Long) request.getSession().getAttribute("shopId");
      if (shopId != 0) {
        return "/";
      }

      List<Shop> shopList = configService.getShop();
      for (Shop shop : shopList) {
        shopId = shop.getId();
        if (shopId == 0) {
          continue;
        }
        try {
          this.initSalesManByShopId(shopId);
        } catch (Exception e) {
          LOG.error("shop_id:" + shopId + "初始化员工出错");
          LOG.error("系统继续初始化下一个店铺");
          LOG.error(e.getMessage(), e);
          continue;
        }
      }

    } catch (Exception e) {
      LOG.error("系统初始化员工出错，系统返回");
      LOG.error(e.getMessage(), e);
      return "/";
    }

    LOG.info("系统成功初始化所有店铺");
    LOG.info("结束时间:" + DateUtil.dateLongToStr(System.currentTimeMillis()));
    return "stat/businessStatistics";


  }

  /**
   * 根据shop_id初始化所有用户为员工
   *
   * @param shopId
   * @throws Exception
   */
  public void initSalesManByShopId(Long shopId) throws Exception {

    IUserService userService = ServiceManager.getService(IUserService.class);
    IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);

    List<UserDTO> userDTOList = userService.getShopUser(shopId);
    if (CollectionUtils.isEmpty(userDTOList)) {
      return;
    }
    for (UserDTO userDTO : userDTOList) {
      if (userDTO == null || StringUtil.isEmpty(userDTO.getName())) {
        continue;
      }

      SalesManDTO salesManDTO = new SalesManDTO();
      salesManDTO.setShopId(userDTO.getShopId());
      salesManDTO.setName(userDTO.getName());
      salesManDTO.setSex(Sex.MALE.toString());
      salesManDTO.setStatus(SalesManStatus.INSERVICE);
      salesManDTO.setMobile(userDTO.getMobile());
      salesManDTO.setMemo(userDTO.getMemo());
      if (SalesManConstant.SALES_MAN_INFO_VALIDATE_SUCCESS.equals(memberCheckerService.checkSalesManInfo(salesManDTO, shopId))) {
        userService.saveOrUpdateSalesMan(salesManDTO);
      }
    }
  }

  @RequestMapping(params = "method=initProductSupplier")
  public String initProductSupplier(HttpServletRequest request, Long shopId) {
    LOG.info("开始初始化ProductSupplier表");
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    try {
      List<ProductSupplierDTO> productSupplierDTOs = new LinkedList<ProductSupplierDTO>();
      long startId = 0L;
      do {
        Long endId = searchService.getItemIndexNextProductIdWithSupplier(shopId, startId, PRODUCT_SIZE);
        if (endId == null) {
          break;
        }
        if (startId != endId) {
          productSupplierDTOs.addAll(searchService.getProductSupplierDTO(null, startId, endId));
          startId = endId;
        } else {
          break;
        }
      } while (true);
      productService.saveProductSupplier(productSupplierDTOs);
    } catch (Exception e) {
      LOG.error("初始productSupplier出错" + e.getMessage(), e);
    } finally {
      return "初始productSupplier成功";
    }

  }

  @RequestMapping(params = "method=initSupplierRecord")
  public String initSupplierRecord(HttpServletRequest request, Long shopId) {
    LOG.info("开始初始化supplier_record表");

    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<Shop> shops = configService.getShop();
    for (Shop shop : shops) {
      try {
        LOG.debug("开始初始化shopId为{} 的supplier_record记录...", shop.getId());
        initSupplierRecordByShopId(shop.getId());
        LOG.debug("shopId为{}的supplier_record记录初始化完成.", shop.getId());
      } catch (Exception e) {
        LOG.error("初始化shopId为{} 的supplier_record记录时出错, 继续初始化下一店铺.", shop.getId());
        LOG.error(e.getMessage(), e);
      }
    }
    LOG.debug("初始化supplier_record表结束");

    return "初始化supplier_record表结束";
  }

  private void initSupplierRecordByShopId(Long shopId) throws Exception {
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    ISupplierPayableService payableService = ServiceManager.getService(ISupplierPayableService.class);
    ISupplierRecordService supplierRecordService = ServiceManager.getService(ISupplierRecordService.class);
    List<SupplierDTO> shopSuppliers = supplierService.getShopSuppliers(shopId);
    if (CollectionUtils.isEmpty(shopSuppliers)) {
      return;
    }

    for (SupplierDTO supplierDTO : shopSuppliers) {
      Double totalPayable = payableService.getSumPayableBySupplierId(supplierDTO.getId(), shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE).get(0);
      SupplierRecordDTO supplierRecordDTO = supplierRecordService.getSupplierRecordDTOBySupplierId(shopId, supplierDTO.getId());
      if (supplierRecordDTO == null) {
        supplierRecordDTO = new SupplierRecordDTO();
        supplierRecordDTO.setShopId(shopId);
        supplierRecordDTO.setSupplierId(supplierDTO.getId());
        supplierRecordDTO.setCreditAmount(totalPayable);
      } else {
        supplierRecordDTO.setCreditAmount(totalPayable);
      }
      supplierRecordService.saveOrUpdateSupplierRecord(supplierRecordDTO);
    }

  }

  @RequestMapping(params = "method=initShopPlan")
  public String initShopPlan(HttpServletRequest request, HttpServletResponse response) {
    IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    if (null == request.getSession().getAttribute("shopId") || 0L != (Long) request.getSession().getAttribute("shopId")) {
      LOG.error("初始化本地计划失败，请使用shopId 为 0的用户登录");
      return "/login";
    }
    try {
      int totalRows = shopPlanService.countPlans();
      int totalPages = totalRows % 100 == 0 ? totalRows / 100 : totalRows / 100 + 1;

      if (totalPages > 0) {
        for (int i = 0; i < totalPages; i++) {
          List<ShopPlan> shopPlanList = shopPlanService.getHundredShopPlans();
          Map<String, String> map = new HashMap<String, String>();
          for (ShopPlan shopPlan : shopPlanList) {
            String userInfo = "";
            String mobileInfo = "";
            if ("all".equals(shopPlan.getCustomerType())) {
              map.put("name", "全体客户");
              map.put("mobile", "全体客户");
              map.put("userId", "allCustomer");
              List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
              mapList.add(map);
              userInfo = JsonUtil.listToJson(mapList);
              shopPlan.setUserInfo(userInfo);
              shopPlan.setContact(map.get("mobile"));
              shopPlanService.updatePlan(shopPlan.toDTO());
            } else {
              String customerIds = shopPlan.getCustomerIds();

              String[] customerIdsStr = (customerIds.substring(0, customerIds.length() - 1)).split(",");

              String mobiles = "";
              List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
              for (int j = 0; j < customerIdsStr.length; j++) {
                CustomerDTO customerDTO = customerService.getCustomerById(Long.valueOf(customerIdsStr[i]));
                if (null == customerDTO) {
                  continue;
                }

                if (StringUtils.isBlank(mobiles) && StringUtils.isNotBlank(customerDTO.getMobile())) {
                  mobiles += customerDTO.getMobile();
                } else if (StringUtils.isNotBlank(mobiles) && StringUtils.isNotBlank(customerDTO.getMobile())) {
                  mobiles += "," + customerDTO.getMobile();
                }

                map.put("name", customerDTO.getName());
                map.put("mobile", customerDTO.getMobile());
                map.put("userId", customerDTO.getId().toString());
                mapList.add(map);
              }

              shopPlan.setContact(mobiles);
              shopPlan.setUserInfo(JsonUtil.listToJson(mapList));
              shopPlanService.updatePlan(shopPlan.toDTO());
            }
          }
        }
      }

    } catch (Exception e) {
      LOG.error("method=initShopPlan");
      LOG.error("更新本店计划失败");
      LOG.error(e.getMessage(), e);
    }
    return "/login";
  }

  @RequestMapping(params = "method=initReceiptNo")
  public void initReceiptNo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    long timeFirst = System.currentTimeMillis();
    Map<String, String> map = new HashMap<String, String>();
    PrintWriter out = response.getWriter();
    String jsonStr = "";
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    try {
      if (null == shopId || !Long.valueOf("0").equals(shopId)) {
        jsonStr = "please login by jackchen";
        return;
      }

      //初始化销售单

      jsonStr += "init sales_order " + initOrderReceiptNo(txnService, map, OrderTypes.SALE) + ",";
      map.clear();
      //初始化采购单

      jsonStr += "init purchase_order " + initOrderReceiptNo(txnService, map, OrderTypes.PURCHASE) + ",";
      map.clear();
      //初始化入库单

      jsonStr += "init inventory " + initOrderReceiptNo(txnService, map, OrderTypes.INVENTORY) + ",";
      map.clear();
      //初始化施工单

      jsonStr += "init repair_order " + initOrderReceiptNo(txnService, map, OrderTypes.REPAIR) + ",";
      map.clear();
      //初始化洗车美容单

      jsonStr += "init wash_beauty " + initOrderReceiptNo(txnService, map, OrderTypes.WASH_BEAUTY) + ",";
      map.clear();
      //初始化入库退货单

      jsonStr += "init purchase_return " + initOrderReceiptNo(txnService, map, OrderTypes.RETURN);
      map.clear();

      Long timeLast = System.currentTimeMillis();

      System.out.print("初始化执行时间：" + (timeLast - timeFirst));

    } catch (Exception e) {
      jsonStr = "init error";
      LOG.error("method=initReceiptNo");
      LOG.error(e.getMessage(), e);
    } finally {
      out.write(jsonStr);
      out.close();
    }
  }

  public String initOrderReceiptNo(ITxnService txnService, Map<String, String> map, OrderTypes types) {
    List orderList = new ArrayList();
    String result = "";
    int maxSize = 500;
    try {
      int totalRows = txnService.countOrderNoReceiptNo(null, types);

      int totalPages = totalRows % maxSize == 0 ? totalRows / maxSize : totalRows / maxSize + 1;

      if (totalPages > 0) {
        for (int i = 0; i < totalPages; i++) {

          orderList = txnService.getOrderDTONoReceiptNo(types, maxSize, i);

          if (orderList == null) {
            continue;
          }
          if (OrderTypes.SALE == types) {
            for (SalesOrder salesOrder : (List<SalesOrder>) orderList) {
              salesOrder.setReceiptNo(getReceiptNo(salesOrder.getShopId(), OrderReceiptNoPrefix.SALE, salesOrder.getCreationDate(), map));
              map.put(salesOrder.getShopId().toString() + salesOrder.getReceiptNo().substring(0, 8), salesOrder.getReceiptNo());
            }
          } else if (OrderTypes.PURCHASE == types) {
            for (PurchaseOrder purchaseOrder : (List<PurchaseOrder>) orderList) {
              purchaseOrder.setReceiptNo(getReceiptNo(purchaseOrder.getShopId(), OrderReceiptNoPrefix.PURCHASE, purchaseOrder.getCreationDate(), map));
              map.put(purchaseOrder.getShopId().toString() + purchaseOrder.getReceiptNo().substring(0, 8), purchaseOrder.getReceiptNo());
            }
          } else if (OrderTypes.INVENTORY == types) {
            for (PurchaseInventory purchaseInventory : (List<PurchaseInventory>) orderList) {
              purchaseInventory.setReceiptNo(getReceiptNo(purchaseInventory.getShopId(), OrderReceiptNoPrefix.INVENTORY, purchaseInventory.getCreationDate(), map));
              map.put(purchaseInventory.getShopId().toString() + purchaseInventory.getReceiptNo().substring(0, 8), purchaseInventory.getReceiptNo());
            }
          } else if (OrderTypes.REPAIR == types) {
            for (RepairOrder repairOrder : (List<RepairOrder>) orderList) {
              repairOrder.setReceiptNo(getReceiptNo(repairOrder.getShopId(), OrderReceiptNoPrefix.REPAIR, repairOrder.getCreationDate(), map));
              map.put(repairOrder.getShopId().toString() + repairOrder.getReceiptNo().substring(0, 8), repairOrder.getReceiptNo());
            }
          } else if (OrderTypes.WASH_BEAUTY == types) {
            for (WashBeautyOrder washBeautyOrder : (List<WashBeautyOrder>) orderList) {
              washBeautyOrder.setReceiptNo(getReceiptNo(washBeautyOrder.getShopId(), OrderReceiptNoPrefix.WASH_BEAUTY, washBeautyOrder.getCreationDate(), map));
              map.put(washBeautyOrder.getShopId().toString() + washBeautyOrder.getReceiptNo().substring(0, 8), washBeautyOrder.getReceiptNo());
            }
          } else if (OrderTypes.RETURN == types) {
            for (PurchaseReturn purchaseReturn : (List<PurchaseReturn>) orderList) {
              purchaseReturn.setReceiptNo(getReceiptNo(purchaseReturn.getShopId(), OrderReceiptNoPrefix.PURCHASE_RETURN, purchaseReturn.getCreationDate(), map));
              map.put(purchaseReturn.getShopId().toString() + purchaseReturn.getReceiptNo().substring(0, 8), purchaseReturn.getReceiptNo());
            }
          }

          txnService.updateOrderListReceiptNo(orderList, types);
        }

      }
      result = "success";
    } catch (Exception e) {
      LOG.error("initOrderReceiptNo");
      LOG.error(types.getName());
      LOG.error(e.getMessage(), e);
      result = "error";
    } finally {
      return result;
    }

  }

  public String getReceiptNo(Long shopId, OrderReceiptNoPrefix prefix, long time, Map<String, String> map) {
    String timeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, time);

    String shortTimeStr = timeStr.substring(2, 4) + timeStr.substring(5, 7) + timeStr.substring(8, 10);

    if (null == map.get(shopId.toString() + prefix.getPrefix() + shortTimeStr)) {
      return prefix.getPrefix() + shortTimeStr + "-001";
    }

    String oldReceiptNo = map.get(shopId.toString() + prefix.getPrefix() + shortTimeStr);

    int num = Integer.valueOf(oldReceiptNo.substring(9, 12));

    num += 1;

    String numStr = "";
    if (num < 10) {
      numStr = "00" + String.valueOf(num);
    } else if (num >= 10 && num <= 99) {
      numStr = "0" + String.valueOf(num);
    } else {
      numStr = String.valueOf(num);
    }

    return oldReceiptNo.substring(0, 9) + numStr;

  }


  /**
   * 作用：通过输入开始日期 统计 开始日期 到当天的营业数据
   * 如果不输入日期 默认统计2012年 到 当天的数据 包含当天
   * 如果输入日期 统计输入日期到 2013年 之前的数据
   * 后台通过输入url自动对店面进行统计 暂时只统计2012年数据
   *
   * @param model
   * @param request
   * @return
   */
  @RequestMapping(params = "method=businessStat")
  public String businessStat(ModelMap model, HttpServletRequest request) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
    try {
      //判断是否是jackchen用户 如果不是 返回到登陆页面
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      if (shopId != 0) {
        return "/";
      }

      LOG.info("系统开始初始化营业数据,开始时间为:" + DateUtil.dateLongToStr(System.currentTimeMillis()));

      int startMonth = 1; //数据初始化的开始日期 月份
      int startDay = 1;//数据初始化的开始日期 日
      int startYear = 2013;//数据初始化的开始日期 年

      Calendar calendar = Calendar.getInstance();

      String shopStr =
        "10000010001040005\n" +
          "10000010001040011\n" +
          "10000010001070007\n" +
          "10000010001170017\n" +
          "10000010001250026\n" +
          "10000010001270028\n" +
          "10000010001270029\n" +
          "10000010001330034\n" +
          "10000010001380039\n" +
          "10000010001380040\n" +
          "10000010001420042\n" +
          "10000010001460051\n" +
          "10000010001480048\n" +
          "10000010001580058\n" +
          "10000010001580060\n" +
          "10000010001600060\n" +
          "10000010001740074\n" +
          "10000010001950095\n" +
          "10000010002230124\n" +
          "10000010002580160\n" +
          "10000010002720173\n" +
          "10000010003050205\n" +
          "10000010003050207\n" +
          "10000010003690270\n" +
          "10000010003820284\n" +
          "10000010004090309\n" +
          "10000010004120314\n" +
          "10000010004280328\n" +
          "10000010004380338\n" +
          "10000010004380339\n" +
          "10000010004500350\n" +
          "10000010004500351\n" +
          "10000010004500353\n" +
          "10000010004660366\n" +
          "10000010004840385\n" +
          "10000010004840392\n" +
          "10000010004840393\n" +
          "10000010004870387\n" +
          "10000010005210421\n" +
          "10000010005450447\n" +
          "10000010005520452\n" +
          "10000010005560459\n" +
          "10000010005560464\n" +
          "10000010005740474\n" +
          "10000010005950495\n" +
          "10000010006090509\n" +
          "10000010006180518\n" +
          "10000010006180519\n" +
          "10000010006180525\n" +
          "10000010006220525\n" +
          "10000010006380538\n" +
          "10000010006430543\n" +
          "10000010006680568\n" +
          "10000010006900592\n" +
          "10000010006990600\n" +
          "10000010007030606\n" +
          "10000010007090610\n" +
          "10000010007090611\n" +
          "10000010007090618\n" +
          "10000010007090623\n" +
          "10000010007150620\n" +
          "10000010007150621\n" +
          "10000010007150625\n" +
          "10000010007150626\n" +
          "10000010007150627\n" +
          "10000010007210621\n" +
          "10000010007210623\n" +
          "10000010007300632\n" +
          "10000010007590659\n" +
          "10000010007660666\n" +
          "10000010007660667\n" +
          "10000010007730674\n" +
          "10000010008060706\n" +
          "10000010008390740";

      String[] shopIds = shopStr.split("\n");

      //3。设置统计的结束日期 营业数据都是累加值 结束日期只能为当前时间
      int currentMonth = DateUtil.getCurrentMonth();
      int currentDay = DateUtil.getCurrentDay();

      //判断url中是否输入了shop_id,如果没有输入 默认初始化所有店铺
      List<Shop> shopList = new ArrayList<Shop>();
      if (!StringUtil.isEmpty(request.getParameter("shopId"))) {
        shopIds = new String[]{request.getParameter("shopId")};
      }
      //每个月统计的第一天
      int dayStartEveryMonth = BEGIN_DAY_EVERY_MONTH;
      for (String idStr : shopIds) {
        List<BusinessStatDTO> businessStatDTOList = new ArrayList<BusinessStatDTO>();
        shopId = Long.valueOf(idStr);
        //遍历 开始日期  到 当前时间 统计每日的营业数据
        LOG.info("系统开始初始化shop_id:" + shopId + "的营业数据");
        try {
          for (int monthIndex = startMonth; monthIndex <= currentMonth; monthIndex++) {

            calendar.set(startYear, monthIndex - 1, 1, 0, 0, 0);

            //每个月统计的最后一天
            int lastDayOfCurrentMonth = calendar.getActualMaximum(Calendar.DATE);

            //如果统计到当前月份 统计的最后一天为当前日
            if (monthIndex == currentMonth) {
              lastDayOfCurrentMonth = currentDay;
            }

            //如果统计月份为开始月份 统计开始天数 为输入的开始日期
            if (monthIndex == startMonth) {
              dayStartEveryMonth = startDay;
            }

            for (int dayIndex = dayStartEveryMonth; dayIndex <= lastDayOfCurrentMonth; dayIndex++) {
              calendar.set(startYear, monthIndex - 1, dayIndex, 0, 0, 0);
              calendar.set(Calendar.MILLISECOND, 0);
              long startTime = calendar.getTimeInMillis();
              calendar.add(Calendar.DATE, 1);
              long endTime = calendar.getTimeInMillis();

              businessStatDTOList.add(this.countBusinessStat(shopId, startYear, monthIndex, dayIndex, startTime, endTime));
            }
          }
        } catch (Exception e) {
          LOG.error("/init.do");
          LOG.error("method=countBusinessStat");
          LOG.error(e.getMessage(), e);
          LOG.error(" 营业统计数据初始化出错 shop_id为" + shopId);
          LOG.error("系统继续初始化下一个店铺");
          continue;
        }
        List<BusinessStatDTO> lastYear = txnService.getLatestBusinessStat(shopId, (long) 2012, 1);
        BusinessStatDTO lastYearDTO = lastYear.get(0);

        BusinessStatDTO businessStatDTO = businessStatDTOList.get(0);
        Long statTime = businessStatDTO.getStatTime();
        businessStatDTO = businessStatService.calculateBusinessStat(businessStatDTO, lastYearDTO, false);
        businessStatDTO.setStatTime(statTime);
        txnService.saveBusinessStat(shopId, (long) startYear, businessStatDTOList);
        businessStatDTOList = new ArrayList<BusinessStatDTO>();
        LOG.info("系统结束初始化shop_id:" + shopId + "的营业数据，继续初始化下一个店铺");
      }


    } catch (Exception e) {
      LOG.error("/init.do");
      LOG.error("method=countBusinessStat");
      LOG.error(e.getMessage(), e);
      LOG.error(" 营业统计数据初始化失败");
      return "/";
    }
    LOG.info("系统结束所有店铺初始化营业数据，结束时间为:" + DateUtil.dateLongToStr(System.currentTimeMillis()));
    //返回到营业统计页面 代表初始化成功
    return "stat/businessStatistics";
  }


  /**
   * 根据shop_id 开始时间start_time  结束时间end_time统计营业数据
   *
   * @param shopId
   * @param year      年
   * @param month     月
   * @param day       日
   * @param startTime 开始时间start_time
   * @param endTime   结束时间end_time
   * @throws Exception
   */
  public BusinessStatDTO countBusinessStat(long shopId, long year, long month, long day, long startTime,
                                           long endTime) throws Exception {

    StatUtil statUtil = new StatUtil();

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);

    BusinessStatDTO businessStatDTO = new BusinessStatDTO();
    businessStatDTO.setShopId(shopId);
    businessStatDTO.setStatYear(year);
    businessStatDTO.setStatMonth(month);
    businessStatDTO.setStatDay(day);


    double repairSettleTotal = 0.0;
    double repairDebtTotal = 0.0;
    double repairCostTotal = 0;
    List<String> stringList = new ArrayList<String>();


    //查询时间使用单据时间vest_date
    List<RepairOrderDTO> repairOrderDTOList = txnService.getRepairOrderDTOList(shopId, startTime, endTime, FIRST_PAGE_NO, 1000, " order by created desc ", OrderStatus.REPAIR_SETTLED);

    if (CollectionUtils.isNotEmpty(repairOrderDTOList)) {

      for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
        //实收和欠款从receivable表拿
        ReceivableDTO receivableDTO = txnService.getReceivableByShopIdOrderId(repairOrderDTO.getShopId(), repairOrderDTO.getId());
        if (receivableDTO != null) {
          repairOrderDTO.setDebt(receivableDTO.getDebt());
          repairOrderDTO.setSettledAmount(receivableDTO.getSettledAmount());
        } else {
          repairOrderDTO.setDebt(0.0);
          repairOrderDTO.setSettledAmount(repairOrderDTO.getTotal());
        }

        repairCostTotal += NumberUtil.doubleVal(repairOrderDTO.getTotalCostPrice());
        repairSettleTotal += repairOrderDTO.getSettledAmount();
        repairDebtTotal += repairOrderDTO.getDebt();
      }
    }


    double washTotal = 0;

    List<ItemIndexDTO> itemIndexDTOList = searchService.getWashOrderItemIndexList(shopId, startTime, endTime, FIRST_PAGE_NO, 1000, " order by order_total_amount desc ");
    List<String> washList = statUtil.getStringList(null, itemIndexDTOList);
    washTotal += (Double.valueOf(washList.get(1)) + Double.valueOf(washList.get(2)));


    double saleSettleTotal = 0.0; //销售单实收
    double saleDebtTotal = 0.0;   //销售单欠款

    List<OrderSearchResultDTO> orderSearchResultDTOList = null;
    try {
      //使用归属时间进行查询vest_date
      orderSearchResultDTOList = txnService.getSalesOrderDTOList(shopId, startTime, endTime, 1, 1000, " order by created desc ");
    } catch (Exception ex) {
      LOG.error("/init.do");
      LOG.error("method=getSalesOrderDetail");
      LOG.error("营业统计:获得销售单详细列表，查询出现异常");
    }

    if (orderSearchResultDTOList != null && orderSearchResultDTOList.size() > 0) {
      for (OrderSearchResultDTO orderSearchResultDTO : orderSearchResultDTOList) {
        if (OrderTypes.SALE.getName().equals(orderSearchResultDTO.getOrderTypeValue())) {
          repairCostTotal += NumberUtil.doubleVal(orderSearchResultDTO.getTotalCostPrice());
        } else {
          repairCostTotal -= NumberUtil.doubleVal(orderSearchResultDTO.getTotalCostPrice());
        }
        //实收和欠款从receivable表拿
        ReceivableDTO receivableDTO = txnService.getReceivableByShopIdOrderId(orderSearchResultDTO.getShopId(), orderSearchResultDTO.getOrderId());
        saleSettleTotal += (receivableDTO == null ? 0D : receivableDTO.getSettledAmount());
        saleDebtTotal += (receivableDTO == null ? 0D : receivableDTO.getDebt());
      }
    }


    businessStatDTO.setWash(washTotal);
    businessStatDTO.setProductCost(repairCostTotal);
    businessStatDTO.setSales(saleDebtTotal + saleSettleTotal);
    businessStatDTO.setService(repairDebtTotal + repairSettleTotal);
    businessStatDTO.setStatTime(endTime - BUSINESS_STAT_TIME);//设置统计时间统一为每天23:59:50
    businessStatDTO.setStatSum(businessStatDTO.getWash() + businessStatDTO.getService() + businessStatDTO.getSales());

    return businessStatDTO;
  }

  @RequestMapping(params = "method=initPasswordToSHA256")
  public String initPassword(ModelMap model, HttpServletRequest request) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<Shop> shops = configService.getShop();
    LOG.info("开始初始化user表中的密码.");
    for (Shop shop : shops) {
      LOG.info("初始化店铺ID为 {} 的user密码.", shop.getId());
      try {
        List<UserDTO> users = userService.getShopUser(shop.getId());
        List<UserDTO> toUpdateUsers = new ArrayList<UserDTO>();
        if (CollectionUtils.isEmpty(users)) {
          LOG.info("店铺ID为 {} 无user密码需要更新.", shop.getId());
          continue;
        }
        for (UserDTO userDTO : users) {
          if (StringUtils.isNotBlank(userDTO.getPassword()) && userDTO.getPassword().length() < 33) {   //MD5加密的密码应小于等于32位
            userDTO.setPassword(EncryptionUtil.computeSHA256(userDTO.getPassword() + userDTO.getShopId()));
            toUpdateUsers.add(userDTO);
          }
        }
        userService.batchUpdateUserPassword(toUpdateUsers);
        LOG.info("初始化店铺ID为 {} 的user密码结束.", shop.getId());
        users = null;
        toUpdateUsers = null;
      } catch (Exception e) {
        LOG.error("初始化店铺ID为 {} 的user密码出错, 继续初始化下一店铺.", shop.getId());
        LOG.error(e.getMessage(), e);
      }
    }
    LOG.info("初始化user表中的密码结束.");

    LOG.info("开始初始化member表中的密码.");
    for (Shop shop : shops) {
      LOG.info("开始初始化店铺ID为 {} 的member密码.", shop.getId());
      try {
        List<Member> members = membersService.getMemberByShopId(shop.getId());
        List<Member> toUpdateMembers = new ArrayList<Member>();
        if (CollectionUtils.isEmpty(members)) {
          LOG.info("店铺ID为 {} 无member密码需要更新.", shop.getId());
          continue;
        }
        for (Member member : members) {
          if (StringUtils.isNotBlank(member.getPassword()) && member.getPassword().length() < 33) {
            member.setPassword(EncryptionUtil.computeSHA256(member.getPassword() + member.getShopId()));
            toUpdateMembers.add(member);
          }
        }
        membersService.batchUpdateMemberPassword(toUpdateMembers);
        LOG.info("初始化店铺ID为 {} 的member密码结束.", shop.getId());

        members = null;
        toUpdateMembers = null;
      } catch (Exception e) {
        LOG.error("初始化店铺ID为 {} 的member密码出错，继续初始化下一店铺.", shop.getId());
        LOG.error(e.getMessage(), e);
      }
    }
    LOG.info("初始化member表中的密码结束。");
    return "/login";
  }


  @RequestMapping(params = "method=initServiceTimeType")
  public void initServiceTimeType(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    boolean flag = false;
    PrintWriter out = response.getWriter();
    try {
      int totalRows = membersService.countMemberService();
      int maxSize = 1000;
      int totalPages = totalRows % maxSize == 0 ? totalRows / maxSize : totalRows / maxSize + 1;

      for (int i = 0; i < totalPages; i++) {
        List<MemberService> memberServiceList = membersService.getMemberServiceForInitService(maxSize, i);

        if (CollectionUtils.isEmpty(memberServiceList)) {
          continue;
        }

        List<Long> idList = new ArrayList<Long>();

        for (MemberService memberService : memberServiceList) {
          if (null != memberService.getServiceId()) {
            idList.add(memberService.getServiceId());
          }
        }

        if (idList.size() > 0) {
          txnService.initServiceTimeType(idList);
        }

      }

      flag = true;
    } catch (Exception e) {
      LOG.error("method=initServiceTimeType");
      LOG.error(e.getMessage(), e);
    } finally {
      if (flag) {
        out.write("success");
      } else {
        out.write("error");
      }
      out.close();
    }
  }

  @RequestMapping(params = "method=initReceivableForOrderId")
  public String initReceivableForOrderId(HttpServletRequest request, HttpServletResponse response) throws Exception {

    try {
      Long[] repairOrderIds = new Long[]{
      };
      Set<Long> repairOrderIdSet = new HashSet<Long>();
      for (Long repairOrderId : repairOrderIds) {
        repairOrderIdSet.add(repairOrderId);
      }
      repairOrderIds = getRfiTxnService().getDebtOrReceivableErrorRepairOrderIds();
      if (repairOrderIds != null && repairOrderIds.length > 0) {
        for (Long repairOrderId : repairOrderIds) {
          repairOrderIdSet.add(repairOrderId);
        }
      }
      if (repairOrderIdSet != null && !repairOrderIdSet.isEmpty()) {
        repairOrderIds = repairOrderIdSet.toArray(new Long[repairOrderIdSet.size()]);
      } else {
        return "/";
      }
      List<Long> ids1 = new ArrayList<Long>();
      List<Long> ids2 = new ArrayList<Long>();
      List<Long> ids3 = new ArrayList<Long>();
      List<Long> ids4 = new ArrayList<Long>();
      for (Long repairOrderId : repairOrderIdSet) {
        RepairOrderDTO repairOrderDTO = getTxnService().getRepairOrder(repairOrderId);
        if (repairOrderDTO == null) {
          continue;
        }

        if (NumberUtil.longValue(repairOrderDTO.getVestDate()) <= 0) {
          continue;
        }

        ReceivableDTO receivableDTO = getTxnService().getReceivableDTOByShopIdAndOrderId(repairOrderDTO.getShopId(), repairOrderDTO.getId());

        List<ReceptionRecordDTO> receptionRecordDTOList = getTxnService().getReceptionRecordByOrderId(repairOrderDTO.getShopId(), repairOrderDTO.getId(), null);
        if (CollectionUtils.isEmpty(receptionRecordDTOList)) {
          LOG.error("repairOrderId" + repairOrderDTO.getId() + "在 receivable debt reception_record 表中没有记录");
          continue;
        }
      }
      LOG.info("case1:{},case2:{},case3:{},case4:{}", new Object[]{ids1.size(), ids2.size(), ids3.size(), ids4.size()});
      LOG.info("case1:{},case2:{},case3:{},case4:{}", new Object[]{ids1.toString(), ids2.toString(), ids3.toString(), ids4.toString()});
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return "stat/businessStatistics";
  }

  @RequestMapping(params = "method=initPurchaseCostStat")
  public String initPurchaseCostStat(ModelMap model, HttpServletRequest request) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    try {
      //判断是否是jackchen用户 如果不是 返回到登陆页面
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      if (shopId != 0) {
        return "/";
      }

      long begin = System.currentTimeMillis();
      LOG.info("系统开始初始化采购成本统计数据，开始时间为:" + DateUtil.dateLongToStr(begin));
      initPurchaseInventoryMonthAndSupplierTranMonthStat(request, "false");
      initPurchaseReturnMonthStat(request);

      initSalesMonthStat(request);
      LOG.info("系统初始化采购成本统计数据结束. 共耗时: {} 秒", (System.currentTimeMillis() - begin) / 1000);
    } catch (Exception e) {
      LOG.error("采购成本统计初始化出错.");
      LOG.error(e.getMessage(), e);
      return "/";
    }
    return "/";
  }

  @RequestMapping(params = "method=initPurchaseInventoryMonthAndSupplierTranMonthStat")
  public String initPurchaseInventoryMonthAndSupplierTranMonthStat(HttpServletRequest request, String initPurchaseInventoryMonthStatOnly) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();
    long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
    if (shopId != 0) {
      return "/";
    }
    boolean initPurchaseInventoryMonthOnly = false;
    if (initPurchaseInventoryMonthStatOnly != null && initPurchaseInventoryMonthStatOnly.equals("true")) {
      initPurchaseInventoryMonthOnly = true;
      Object statusDel = writer.begin();
      writer.deletePurchaseInventoryMonthStat();
      writer.commit(statusDel);
      List<ProductModifyLogDTO> logs = txnService.getProductModifyLogByStatus(ProductModifyOperations.INVENTORY_INDEX_UPDATE, new StatProcessStatus[]{StatProcessStatus.NEW});
      txnService.batchUpdateProductModifyLogStatus(logs, StatProcessStatus.DONE);
    }
    long begin = System.currentTimeMillis();
    LOG.info("开始初始化采购成本统计表，供应商交易统计表. purchase_inventory_stat, purchase_inventory_month_stat, supplier_tran_stat,supplier_tran_month_stat.开始时间:{}", DateUtil.dateLongToStr(begin));

    List<Shop> shops = configService.getShop();
    for (int i = 0; i < shops.size(); i++) {
      Shop shop = shops.get(i);
      shopId = shop.getId();
      PurchaseInventory purchaseInventory = txnService.getFirstPurchaseInventoryByVestDate(shop.getId());
      if (purchaseInventory == null) {
        continue;
      }
      Long vestDate = purchaseInventory.getVestDate();
      long start = DateUtil.getStartTimeOfTimeDay(vestDate);
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(start);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      start = calendar.getTimeInMillis();

      Object status = writer.begin();

      try {
        while (start < System.currentTimeMillis()) {
          calendar.add(Calendar.MONTH, 1);
          long end = calendar.getTimeInMillis();
          List<PurchaseInventoryDTO> dtos = txnService.getPurchaseInventoryDTOByVestDate(shop.getId(), start, end);
          if (CollectionUtils.isEmpty(dtos)) {
            start = end;
            continue;
          }

          Map<Long, SupplierTranMonthStat> supplierMonthStatMap = new HashMap<Long, SupplierTranMonthStat>();
          Map<String, PurchaseInventoryMonthStat> purchaseInventoryMonthStatMap = new HashMap<String, PurchaseInventoryMonthStat>();

          Map<Long, List<PurchaseInventoryDTO>> dayPurchaseInventoryMap = new HashMap<Long, List<PurchaseInventoryDTO>>();
          for (PurchaseInventoryDTO purchaseInventoryDTO : dtos) {
            long statTime = purchaseInventoryDTO.getVestDate();

            long statTime6clock = DateUtil.get6clock(statTime);
            if (dayPurchaseInventoryMap.get(statTime6clock) == null) {
              List<PurchaseInventoryDTO> dayList = new ArrayList<PurchaseInventoryDTO>();
              dayList.add(purchaseInventoryDTO);
              dayPurchaseInventoryMap.put(statTime6clock, dayList);
            } else {
              dayPurchaseInventoryMap.get(statTime6clock).add(purchaseInventoryDTO);
            }

            Long supplierId = purchaseInventoryDTO.getSupplierId();

            //新建或更新当月的supplier
            if (supplierMonthStatMap.get(supplierId) == null) {
              SupplierTranMonthStat supplierTranMonthStat = new SupplierTranMonthStat();
              supplierTranMonthStat.setShopId(shop.getId());
              supplierTranMonthStat.setSupplierId(supplierId);
              supplierTranMonthStat.setStatYear(DateUtil.getYear(statTime));
              supplierTranMonthStat.setStatMonth(DateUtil.getMonth(statTime));
              supplierTranMonthStat.setTimes(1);
              supplierTranMonthStat.setTotal(purchaseInventoryDTO.getTotal());
              supplierMonthStatMap.put(supplierId, supplierTranMonthStat);
            } else {
              SupplierTranMonthStat supplierTranMonthStat = supplierMonthStatMap.get(supplierId);
              supplierTranMonthStat.setTimes(supplierTranMonthStat.getTimes() + 1);
              supplierTranMonthStat.setTotal(supplierTranMonthStat.getTotal() + purchaseInventoryDTO.getTotal());
              supplierMonthStatMap.put(supplierId, supplierTranMonthStat);
            }

            if (ArrayUtils.isEmpty(purchaseInventoryDTO.getItemDTOs())) {
              continue;
            }

            for (PurchaseInventoryItemDTO itemDTO : purchaseInventoryDTO.getItemDTOs()) {
              Long productId = itemDTO.getProductId();
              ProductDTO product = productService.getProductByProductLocalInfoId(productId, shopId);
              if (product == null) {
                continue;
              }
              String key = StringUtils.defaultIfEmpty(product.getName(), "") + "_"
                + StringUtils.defaultIfEmpty(product.getBrand(), "") + "_"
                + StringUtils.defaultIfEmpty(product.getVehicleBrand(), "") + "_"
                + StringUtils.defaultIfEmpty(product.getVehicleModel(), "");

              Double amount = itemDTO.getAmount() == null ? 0 : itemDTO.getAmount();
              ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
              if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
                amount = amount * productLocalInfoDTO.getRate();
              }

              //新建或更新当月的purchaseInventoryMonthStat统计数据
              if (purchaseInventoryMonthStatMap.get(key) == null) {
                PurchaseInventoryMonthStat purchaseInventoryMonthStat = new PurchaseInventoryMonthStat();
                purchaseInventoryMonthStat.setShopId(shop.getId());
                purchaseInventoryMonthStat.setProductName(StringUtils.defaultIfEmpty(product.getName(), ""));
                purchaseInventoryMonthStat.setProductBrand(StringUtils.defaultIfEmpty(product.getBrand(), ""));
                purchaseInventoryMonthStat.setVehicleBrand(StringUtils.defaultIfEmpty(product.getVehicleBrand(), ""));
                purchaseInventoryMonthStat.setVehicleModel(StringUtils.defaultIfEmpty(product.getVehicleModel(), ""));
                purchaseInventoryMonthStat.setStatYear(DateUtil.getYear(statTime));
                purchaseInventoryMonthStat.setStatMonth(DateUtil.getMonth(statTime));
                purchaseInventoryMonthStat.setAmount(amount);
                purchaseInventoryMonthStat.setTotal(itemDTO.getTotal());
                purchaseInventoryMonthStat.setTimes(1);
                purchaseInventoryMonthStatMap.put(key, purchaseInventoryMonthStat);
              } else {
                PurchaseInventoryMonthStat purchaseInventoryMonthStat = purchaseInventoryMonthStatMap.get(key);
                purchaseInventoryMonthStat.setAmount(purchaseInventoryMonthStat.getAmount() + amount);
                purchaseInventoryMonthStat.setTotal(purchaseInventoryMonthStat.getTotal() + itemDTO.getTotal());
                purchaseInventoryMonthStat.setTimes(purchaseInventoryMonthStat.getTimes() + 1);
                purchaseInventoryMonthStatMap.put(key, purchaseInventoryMonthStat);
              }
            }
          }

          if (!initPurchaseInventoryMonthOnly) {
            Set<Long> supplierMonthStatIdSet = supplierMonthStatMap.keySet();
            for (Long id : supplierMonthStatIdSet) {
              writer.save(supplierMonthStatMap.get(id));
            }
          }

          Set<String> inventoryMonthStatKeySet = purchaseInventoryMonthStatMap.keySet();
          for (String s : inventoryMonthStatKeySet) {
            writer.save(purchaseInventoryMonthStatMap.get(s));
          }
          writer.flush();


          Set<Long> dayKeySet = dayPurchaseInventoryMap.keySet();

          //记录最新一条数据，用于隔天数据累加
          Map<Long, PurchaseInventoryStat> productLastStatMap = new HashMap<Long, PurchaseInventoryStat>();
          Map<Long, SupplierTranStat> supplierLastStatMap = new HashMap<Long, SupplierTranStat>();
          for (Long dayKey : dayKeySet) {
            List<PurchaseInventoryDTO> purchaseInventoryDTOs = dayPurchaseInventoryMap.get(dayKey);

            //记录每一天的数据，如果当天内重复则直接累加
            Map<Long, PurchaseInventoryStat> purchaseInventoryStatMap = new HashMap<Long, PurchaseInventoryStat>();
            Map<Long, SupplierTranStat> supplierStatMap = new HashMap<Long, SupplierTranStat>();
            for (PurchaseInventoryDTO purchaseInventoryDTO : purchaseInventoryDTOs) {
              long statTime = purchaseInventoryDTO.getVestDate();
              //取得supplierTranStat前一条数据
              Long supplierId = purchaseInventoryDTO.getSupplierId();
              SupplierTranStat previousSupplierStat = supplierLastStatMap.get(supplierId);
              double total = previousSupplierStat == null ? 0 : previousSupplierStat.getTotal();
              int times = previousSupplierStat == null ? 0 : previousSupplierStat.getTimes();

              //新建或更新当天的supplierTranStat统计数据
              SupplierTranStat supplierTranStat = null;
              if (supplierStatMap.get(supplierId) == null) {
                supplierTranStat = new SupplierTranStat();
                supplierTranStat.setShopId(shop.getId());
                supplierTranStat.setSupplierId(supplierId);
                supplierTranStat.setStatTime(DateUtil.get6clock(statTime));
                supplierTranStat.setStatYear(DateUtil.getYear(statTime));
                supplierTranStat.setStatMonth(DateUtil.getMonth(statTime));
                supplierTranStat.setStatDay(DateUtil.getDay(statTime));
                supplierTranStat.setTimes(times + 1);
                supplierTranStat.setTotal(total + purchaseInventoryDTO.getTotal());
              } else {
                supplierTranStat = supplierStatMap.get(supplierId);
                supplierTranStat.setTimes(supplierTranStat.getTimes() + 1);
                supplierTranStat.setTotal(supplierTranStat.getTotal() + purchaseInventoryDTO.getTotal());
              }
              supplierStatMap.put(supplierId, supplierTranStat);
              supplierLastStatMap.put(supplierId, supplierTranStat);

              if (ArrayUtils.isEmpty(purchaseInventoryDTO.getItemDTOs())) {
                continue;
              }

              for (PurchaseInventoryItemDTO itemDTO : purchaseInventoryDTO.getItemDTOs()) {
                Long productId = itemDTO.getProductId();
                ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
                if (productLocalInfoDTO == null) {
                  continue;
                }
                PurchaseInventoryStat previousInventoryStat = productLastStatMap.get(productId);
                double previousAmount = previousInventoryStat == null ? 0 : previousInventoryStat.getAmount();
                double previousTotal = previousInventoryStat == null ? 0 : previousInventoryStat.getTotal();
                int previousTimes = previousInventoryStat == null ? 0 : previousInventoryStat.getTimes();
                Double amount = itemDTO.getAmount() == null ? 0 : itemDTO.getAmount();

                if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
                  amount = amount * productLocalInfoDTO.getRate();
                }

                //新建或更新当天的purchaseInventoryStat统计数据
                PurchaseInventoryStat purchaseInventoryStat = null;
                if (purchaseInventoryStatMap.get(productId) == null) {
                  purchaseInventoryStat = new PurchaseInventoryStat();
                  purchaseInventoryStat.setShopId(shop.getId());
                  purchaseInventoryStat.setProductId(productId);
                  purchaseInventoryStat.setStatTime(DateUtil.get6clock(statTime));
                  purchaseInventoryStat.setStatYear(DateUtil.getYear(statTime));
                  purchaseInventoryStat.setStatMonth(DateUtil.getMonth(statTime));
                  purchaseInventoryStat.setStatDay(DateUtil.getDay(statTime));
                  purchaseInventoryStat.setAmount(previousAmount + amount);
                  purchaseInventoryStat.setTotal(previousTotal + itemDTO.getTotal());
                  purchaseInventoryStat.setTimes(previousTimes + 1);
                } else {
                  purchaseInventoryStat = purchaseInventoryStatMap.get(productId);
                  purchaseInventoryStat.setAmount(purchaseInventoryStat.getAmount() + amount);
                  purchaseInventoryStat.setTotal(purchaseInventoryStat.getTotal() + itemDTO.getTotal());
                  purchaseInventoryStat.setTimes(purchaseInventoryStat.getTimes() + 1);
                }
                purchaseInventoryStatMap.put(productId, purchaseInventoryStat);
                productLastStatMap.put(productId, purchaseInventoryStat);
              }
            }

            if (!initPurchaseInventoryMonthOnly) {
              Set<Long> supplierIdSet = supplierStatMap.keySet();
              for (Long id : supplierIdSet) {
                writer.save(supplierStatMap.get(id));
              }

              Set<Long> productIdSet = purchaseInventoryStatMap.keySet();
              for (Long id : productIdSet) {
                writer.save(purchaseInventoryStatMap.get(id));
              }
            }
          }

          start = end;
        }
        writer.commit(status);
      } catch (Exception e) {
        writer.rollback(status);
        LOG.error("初始化采购成本统计表，供应商交易统计表出错. shopID:{}", shop.getId());
        LOG.error(e.getMessage(), e);
      } finally {
        writer.rollback(status);
      }
      LOG.info("初始化采购成本统计表，供应商交易统计表。shopID:{} 结束。已完成 {} 家，共 {} 家。", new Object[]{shop.getId(), i, shops.size()});
    }
    long end = System.currentTimeMillis();
    LOG.info("初始化采购成本统计表，供应商交易统计表结束. 结束时间：{}; 共耗时：{} 秒", DateUtil.dateLongToStr(end), (end - begin) / 1000);
    return "redirect:costStat.do?method=getCostStat";
  }

  @RequestMapping(params = "method=initPurchaseReturnMonthStat")
  public String initPurchaseReturnMonthStat(HttpServletRequest request) {
    long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
    if (shopId != 0) {
      return "/";
    }
    long begin = System.currentTimeMillis();
    LOG.info("开始初始化退货单相关统计表. purchase_inventory_stat_change, purchase_return_stat");

    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();
    List<Shop> shops = configService.getShop();

    for (int i = 0; i < shops.size(); i++) {
      Shop shop = shops.get(i);
      shopId = shop.getId();
      PurchaseReturn purchaseReturn = txnService.getFirstPurchaseReturnByVestDate(shop.getId());
      if (purchaseReturn == null) {
        continue;
      }
      Long vestDate = purchaseReturn.getVestDate();
      if (vestDate == null) {
        vestDate = purchaseReturn.getCreationDate();
      }
      long start = DateUtil.getStartTimeOfTimeDay(vestDate);
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(start);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      start = calendar.getTimeInMillis();

      Object status = writer.begin();

      try {
        while (start < System.currentTimeMillis()) {
          calendar.add(Calendar.MONTH, 1);
          long end = calendar.getTimeInMillis();
          List<PurchaseReturnDTO> dtos = txnService.getPurchaseReturnDTOByVestDate(shop.getId(), start, end);
          if (CollectionUtils.isEmpty(dtos)) {
            start = end;
            continue;
          }

//          Map<Long, PurchaseReturnMonthStat> supplierReturnStatMap = new HashMap<Long, PurchaseReturnMonthStat>();
//          Map<Long, PurchaseReturnMonthStat> productReturnStatMap = new HashMap<Long, PurchaseReturnMonthStat>();
          Map<String, PurchaseReturnMonthStat> supplierProductMap = new HashMap<String, PurchaseReturnMonthStat>();

//          Map<String, PurchaseInventoryMonthStat> purchaseInventoryMonthStatMap = new HashMap<String, PurchaseInventoryMonthStat>();
          for (PurchaseReturnDTO purchaseReturnDTO : dtos) {
            Long statTime = purchaseReturnDTO.getVestDate();
            if (statTime == null) {
              statTime = purchaseReturnDTO.getCreationDate();
            }
            Long supplierId = purchaseReturnDTO.getSupplierId();
            //新建或更新当月的supplier
//            if (supplierReturnStatMap.get(supplierId) == null) {
//              PurchaseReturnMonthStat purchaseReturnMonthStat = new PurchaseReturnMonthStat();
//              purchaseReturnMonthStat.setShopId(shop.getId());
//              purchaseReturnMonthStat.setSupplierId(supplierId);
//              purchaseReturnMonthStat.setStatYear(DateUtil.getYear(statTime));
//              purchaseReturnMonthStat.setStatMonth(DateUtil.getMonth(statTime));
//              purchaseReturnMonthStat.setTimes(1);
//              purchaseReturnMonthStat.setTotal(purchaseReturnDTO.getTotal());
//              supplierReturnStatMap.put(supplierId, purchaseReturnMonthStat);
//            } else {
//              PurchaseReturnMonthStat purchaseReturnMonthStat = supplierReturnStatMap.get(supplierId);
//              purchaseReturnMonthStat.setTimes(purchaseReturnMonthStat.getTimes() + 1);
//              purchaseReturnMonthStat.setTotal(purchaseReturnMonthStat.getTotal() + purchaseReturnDTO.getTotal());
//              supplierReturnStatMap.put(supplierId, purchaseReturnMonthStat);
//            }

            if (ArrayUtils.isEmpty(purchaseReturnDTO.getItemDTOs())) {
              continue;
            }

            for (PurchaseReturnItemDTO itemDTO : purchaseReturnDTO.getItemDTOs()) {
              Long productId = itemDTO.getProductId();
              ProductDTO product = productService.getProductByProductLocalInfoId(productId, shopId);
              if (product == null) {
                continue;
              }

              Double amount = itemDTO.getAmount() == null ? 0 : itemDTO.getAmount();
              ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
              if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
                amount = amount * productLocalInfoDTO.getRate();
              }

              String productIdSupplierId = String.valueOf(supplierId) + String.valueOf(productId);
              if (supplierProductMap.containsKey(productIdSupplierId)) {
                PurchaseReturnMonthStat monthStat = supplierProductMap.get(productIdSupplierId);
                monthStat.setTimes(monthStat.getTimes() + 1);
                monthStat.setAmount(monthStat.getAmount() + amount);
                monthStat.setTotal(monthStat.getTotal() + itemDTO.getTotal());
              } else {
                PurchaseReturnMonthStat monthStat = new PurchaseReturnMonthStat();
                monthStat.setShopId(shop.getId());
                monthStat.setProductId(productId);
                monthStat.setSupplierId(purchaseReturnDTO.getSupplierId());
                monthStat.setStatYear(DateUtil.getYear(statTime));
                monthStat.setStatMonth(DateUtil.getMonth(statTime));
                monthStat.setAmount(amount);
                monthStat.setTotal(itemDTO.getTotal());
                monthStat.setTimes(1);
                supplierProductMap.put(productIdSupplierId, monthStat);
              }


              //新建或更新当月的某商品累计数据
//              if(productReturnStatMap.get(productId) == null){
//                PurchaseReturnMonthStat purchaseReturnMonthStat = new PurchaseReturnMonthStat();
//                purchaseReturnMonthStat.setShopId(shop.getId());
//                purchaseReturnMonthStat.setProductId(productId);
//                purchaseReturnMonthStat.setStatYear(DateUtil.getYear(statTime));
//                purchaseReturnMonthStat.setStatMonth(DateUtil.getMonth(statTime));
//                purchaseReturnMonthStat.setAmount(amount);
//                purchaseReturnMonthStat.setTotal(itemDTO.getTotal());
//                purchaseReturnMonthStat.setTimes(1);
//                productReturnStatMap.put(productId, purchaseReturnMonthStat);
//              }else{
//                PurchaseReturnMonthStat purchaseReturnMonthStat = productReturnStatMap.get(productId);
//                purchaseReturnMonthStat.setAmount(purchaseReturnMonthStat.getAmount() + amount);
//                purchaseReturnMonthStat.setTotal(purchaseReturnMonthStat.getTotal() + itemDTO.getTotal());
//                purchaseReturnMonthStat.setTimes(purchaseReturnMonthStat.getTimes() + 1);
//                productReturnStatMap.put(productId, purchaseReturnMonthStat);
//              }

//              String key = StringUtils.defaultIfEmpty(product.getName(), "") + "_"
//                  + StringUtils.defaultIfEmpty(product.getBrand(), "") + "_"
//                  + StringUtils.defaultIfEmpty(product.getVehicleBrand(), "") +"_"
//                  + StringUtils.defaultIfEmpty(product.getVehicleModel(), "");

              //新建或更新当月的purchaseInventoryMonthStat统计数据      采购统计不再减去退货
//              if(purchaseInventoryMonthStatMap.get(key) == null){
//                PurchaseInventoryMonthStat purchaseInventoryMonthStat = new PurchaseInventoryMonthStat();
//                purchaseInventoryMonthStat.setShopId(shop.getId());
//                purchaseInventoryMonthStat.setProductName(StringUtils.defaultIfEmpty(product.getName(), ""));
//                purchaseInventoryMonthStat.setProductBrand(StringUtils.defaultIfEmpty(product.getBrand(), ""));
//                purchaseInventoryMonthStat.setVehicleBrand(StringUtils.defaultIfEmpty(product.getVehicleBrand(), ""));
//                purchaseInventoryMonthStat.setVehicleModel(StringUtils.defaultIfEmpty(product.getVehicleModel(), ""));
//                purchaseInventoryMonthStat.setStatYear(DateUtil.getYear(statTime));
//                purchaseInventoryMonthStat.setStatMonth(DateUtil.getMonth(statTime));
//                purchaseInventoryMonthStat.setAmount(-amount);
//                purchaseInventoryMonthStat.setTotal(-itemDTO.getTotal());
//                purchaseInventoryMonthStat.setTimes(-1);
//                purchaseInventoryMonthStatMap.put(key, purchaseInventoryMonthStat);
//              }else{
//                PurchaseInventoryMonthStat purchaseInventoryMonthStat = purchaseInventoryMonthStatMap.get(key);
//                purchaseInventoryMonthStat.setAmount(purchaseInventoryMonthStat.getAmount() - amount);
//                purchaseInventoryMonthStat.setTotal(purchaseInventoryMonthStat.getTotal() - itemDTO.getTotal());
//                purchaseInventoryMonthStat.setTimes(purchaseInventoryMonthStat.getTimes() - 1);
//                purchaseInventoryMonthStatMap.put(key, purchaseInventoryMonthStat);
//              }
            }
          }

          Set<String> supplierProductIdSet = supplierProductMap.keySet();
          for (String id : supplierProductIdSet) {
            PurchaseReturnMonthStat stat = supplierProductMap.get(id);
            writer.save(stat);
          }


          //供应商月统计表中的供应商粒度的记录
//          Set<Long> supplierIdSet = supplierReturnStatMap.keySet();
//          for(Long id:supplierIdSet){
//            PurchaseReturnMonthStat monthStat = supplierReturnStatMap.get(id);
//
//            //对应的更新supplier_tran_month_stat表
//            SupplierTranMonthStat supplierStat = writer.getSupplierTranMonthStat(shopId, id, monthStat.getStatYear(), monthStat.getStatMonth());
//            if(supplierStat == null){
//              supplierStat = new SupplierTranMonthStat();
//              supplierStat.setShopId(shopId);
//              supplierStat.setSupplierId(id);
//              supplierStat.setStatYear(monthStat.getStatYear());
//              supplierStat.setStatMonth(monthStat.getStatMonth());
//              supplierStat.setTimes(-monthStat.getTimes());
//              supplierStat.setTotal(-monthStat.getTotal());
//              writer.save(supplierStat);
//            }else{
//              supplierStat.setTimes(supplierStat.getTimes()-monthStat.getTimes());
//              supplierStat.setTotal(supplierStat.getTotal()-monthStat.getTotal());
//              writer.update(supplierStat);
//            }
//          }

          //退货月统计表中的产品粒度的记录
//          Set<Long> productIdSet = productReturnStatMap.keySet();
//          for(Long id:productIdSet){
//            PurchaseReturnMonthStat monthStat = productReturnStatMap.get(id);
//          }

          //更新或插入purchase_inventory_month_stat表中的记录
//          Set<String> inventoryMonthStatKeySet = purchaseInventoryMonthStatMap.keySet();
//          for(String s : inventoryMonthStatKeySet){
//            PurchaseInventoryMonthStat purchaseInventoryMonthStat = purchaseInventoryMonthStatMap.get(s);
//            PurchaseInventoryItemDTO itemDTO = new PurchaseInventoryItemDTO();
//            itemDTO.setProductName(purchaseInventoryMonthStat.getProductName());
//            itemDTO.setBrand(purchaseInventoryMonthStat.getProductBrand());
//            itemDTO.setVehicleBrand(purchaseInventoryMonthStat.getVehicleBrand());
//            itemDTO.setVehicleModel(purchaseInventoryMonthStat.getVehicleModel());
//            PurchaseInventoryMonthStat existingStat = writer.getPurchaseInventoryMonthStat(shopId, itemDTO, purchaseInventoryMonthStat.getStatYear(), purchaseInventoryMonthStat.getStatMonth());
//            if(existingStat == null){
//              writer.save(purchaseInventoryMonthStat);
//            }else{
//              existingStat.setAmount(existingStat.getAmount() + purchaseInventoryMonthStat.getAmount());    //已经是负值
//              existingStat.setTimes(existingStat.getTimes() + purchaseInventoryMonthStat.getTimes());
//              existingStat.setTotal(existingStat.getTotal() + purchaseInventoryMonthStat.getTotal());
//              writer.update(existingStat);
//            }
//          }

          start = end;
        }
        writer.commit(status);
      } catch (Exception e) {
        writer.rollback(status);
        LOG.error("初始化退货单相关统计表出错. shopID:{}", shop.getId());
        LOG.error(e.getMessage(), e);
      } finally {
        writer.rollback(status);
      }
      LOG.info("初始化退货单相关统计表。shopID:{} 结束。已完成 {} 家，共 {} 家。", new Object[]{shop.getId(), i, shops.size()});
    }

    long end = System.currentTimeMillis();
    LOG.info("初始化退货单相关统计表结束. 结束时间：{}; 共耗时：{} 秒", DateUtil.dateLongToStr(end), (end - begin) / 1000);
    return "redirect:costStat.do?method=getReturnStat";
  }

  @RequestMapping(params = "method=initSalesStat")
  public String initSalesStat(HttpServletRequest request) {
    long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
    if (shopId != 0) {
      return "/";
    }
    long begin = System.currentTimeMillis();
    LOG.info("开始初始化销售单相关统计表. sales_stat_change");
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();

    List<Shop> shops = configService.getShop();
    for (int i = 0; i < shops.size(); i++) {
      Shop shop = shops.get(i);
      shopId = shop.getId();
      Object status = writer.begin();

      try {
        Long startTime = DateUtil.getStartTimeOfTimeDay(shop.getCreationDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);

        while (calendar.getTimeInMillis() < System.currentTimeMillis()) {

          calendar.add(Calendar.DATE, 1);
          long endTime = calendar.getTimeInMillis();

          Map<Long, SalesStat> productStatMap = new HashMap<Long, SalesStat>();

          List<RepairOrderDTO> repairOrderDTOList = txnService.getRepairOrderListByVestDate(shop.getId(), startTime, endTime);

          if (CollectionUtils.isNotEmpty(repairOrderDTOList)) {
            for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
              if (repairOrderDTO.getStatus() == OrderStatus.REPAIR_REPEAL) {
                continue;
              }
              List<RepairOrderItem> repairOrderItemList = writer.getRepairOrderItemsByRepairOrderId(repairOrderDTO.getId());
              if (CollectionUtils.isEmpty(repairOrderItemList)) {
                continue;
              }
              for (RepairOrderItem repairOrderItem : repairOrderItemList) {
                long productId = repairOrderItem.getProductId();
                Double amount = repairOrderItem.getAmount();
                ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(repairOrderItem.getProductId(), shopId);
                if (UnitUtil.isStorageUnit(repairOrderItem.getUnit(), productLocalInfoDTO)) {
                  amount = amount * productLocalInfoDTO.getRate();
                }
                if (productStatMap.containsKey(productId)) {
                  SalesStat salesStat = productStatMap.get(productId);
                  salesStat.setAmount(salesStat.getAmount() + amount);
                  salesStat.setTotal(salesStat.getTotal() + repairOrderItem.getTotal());
                  salesStat.setTimes(salesStat.getTimes() + 1);
                } else {
                  SalesStat salesStat = new SalesStat();
                  salesStat.setShopId(shop.getId());
                  salesStat.setProductId(productId);
                  salesStat.setStatYear(DateUtil.getYear(repairOrderDTO.getVestDate()));
                  salesStat.setStatMonth(DateUtil.getMonth(repairOrderDTO.getVestDate()));
                  salesStat.setStatDay(DateUtil.getDay(repairOrderDTO.getVestDate()));
                  salesStat.setStatTime(DateUtil.get6clock(repairOrderDTO.getVestDate()));
                  salesStat.setAmount(amount);
                  salesStat.setTotal(repairOrderItem.getTotal());
                  salesStat.setTimes(1);
                  productStatMap.put(productId, salesStat);
                }
              }
            }
          }


          List<SalesOrderDTO> salesOrderDTOList = txnService.getSalesOrderDTOListByVestDate(shop.getId(), startTime, endTime);

          if (CollectionUtils.isNotEmpty(salesOrderDTOList)) {
            for (SalesOrderDTO salesOrderDTO : salesOrderDTOList) {
              if (salesOrderDTO.getStatus() == OrderStatus.SALE_REPEAL) {
                continue;
              }
              List<SalesOrderItem> salesOrderItemList = writer.getSalesOrderItemsByOrderId(salesOrderDTO.getId());
              if (CollectionUtils.isEmpty(salesOrderItemList)) {
                continue;
              }
              for (SalesOrderItem salesOrderItem : salesOrderItemList) {
                long productId = salesOrderItem.getProductId();

                Double amount = salesOrderItem.getAmount();
                ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(salesOrderItem.getProductId(), shopId);
                if (UnitUtil.isStorageUnit(salesOrderItem.getUnit(), productLocalInfoDTO)) {
                  amount = amount * productLocalInfoDTO.getRate();
                }
                if (productStatMap.containsKey(productId)) {
                  SalesStat salesStat = productStatMap.get(productId);
                  salesStat.setAmount(salesStat.getAmount() + amount);
                  salesStat.setTotal(salesStat.getTotal() + salesOrderItem.getTotal());
                  salesStat.setTimes(salesStat.getTimes() + 1);
                } else {
                  SalesStat salesStat = new SalesStat();
                  salesStat.setShopId(shop.getId());
                  salesStat.setProductId(productId);
                  salesStat.setStatYear(DateUtil.getYear(salesOrderDTO.getVestDate()));
                  salesStat.setStatMonth(DateUtil.getMonth(salesOrderDTO.getVestDate()));
                  salesStat.setStatDay(DateUtil.getDay(salesOrderDTO.getVestDate()));
                  salesStat.setStatTime(DateUtil.get6clock(salesOrderDTO.getVestDate()));
                  salesStat.setAmount(amount);
                  salesStat.setTotal(salesOrderItem.getTotal());
                  salesStat.setTimes(1);
                  productStatMap.put(productId, salesStat);
                }
              }
            }
          }


          Set<Long> productSet = productStatMap.keySet();
          for (Long id : productSet) {
            SalesStat stat = writer.getLatestSalesStatBeforeTime(shop.getId(), id, startTime);
            SalesStat newStat = productStatMap.get(id);
            if (stat != null) {
              newStat.setTotal(stat.getTotal() + newStat.getTotal());
              newStat.setAmount(stat.getAmount() + newStat.getAmount());
              newStat.setTimes(stat.getTimes() + newStat.getTimes());
            }
            writer.save(newStat);
          }
          startTime = endTime;
        }
        writer.commit(status);
      } catch (Exception e) {
        writer.rollback(status);
        LOG.error(e.getMessage(), e);
        LOG.error("初始化销售单 施工单 salesStat. shopID:{}", shop.getId());
      } finally {
        writer.rollback(status);
      }
      LOG.info("初始化销售单 施工单 salesStat。shopID:{} 结束。已完成 {} 家，共 {} 家。", new Object[]{shop.getId(), i, shops.size()});
    }

    long end = System.currentTimeMillis();
    LOG.info("初始化销售单相关统计表结束. 结束时间：{}; 共耗时：{} 秒", DateUtil.dateLongToStr(end), (end - begin) / 1000);
    return "stat/businessStatistics";
  }

  @RequestMapping(params = "method=initSalesMonthStat")
  public String initSalesMonthStat(HttpServletRequest request) {
    long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
    if (shopId != 0) {
      return "/";
    }
    long begin = System.currentTimeMillis();
    LOG.info("开始初始化销售月统计表. sales_month_stat");
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();

    List<Shop> shops = configService.getShop();
    for (int i = 0; i < shops.size(); i++) {
      Shop shop = shops.get(i);
      shopId = shop.getId();
      Object status = writer.begin();

      try {
        Long startTime = DateUtil.getStartTimeOfTimeDay(shop.getCreationDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);

        while (calendar.getTimeInMillis() < System.currentTimeMillis()) {

          int year = calendar.get(Calendar.YEAR);
          int month = calendar.get(Calendar.MONTH) + 1;
          if (month == 12) {
            calendar.set(year + 1, 0, 1, 0, 0, 0);
          } else {
            calendar.set(year, month, 1, 0, 0, 0);
          }

          long endTime = calendar.getTimeInMillis();

          Map<Long, SalesMonthStat> productStatMap = new HashMap<Long, SalesMonthStat>();

          List<RepairOrderDTO> repairOrderDTOList = txnService.getRepairOrderListByVestDate(shop.getId(), startTime, endTime);

          if (CollectionUtils.isNotEmpty(repairOrderDTOList)) {
            for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
              if (repairOrderDTO.getStatus() == OrderStatus.REPAIR_REPEAL) {
                continue;
              }
              List<RepairOrderItem> repairOrderItemList = writer.getRepairOrderItemsByRepairOrderId(repairOrderDTO.getId());
              if (CollectionUtils.isEmpty(repairOrderItemList)) {
                continue;
              }
              for (RepairOrderItem repairOrderItem : repairOrderItemList) {
                long productId = repairOrderItem.getProductId();

                Double amount = repairOrderItem.getAmount();
                ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(repairOrderItem.getProductId(), shopId);
                if (UnitUtil.isStorageUnit(repairOrderItem.getUnit(), productLocalInfoDTO)) {
                  amount = amount * productLocalInfoDTO.getRate();
                }

                if (productStatMap.containsKey(productId)) {
                  SalesMonthStat salesStat = productStatMap.get(productId);
                  salesStat.setAmount(salesStat.getAmount() + amount);
                  salesStat.setTotal(salesStat.getTotal() + repairOrderItem.getTotal());
                  salesStat.setTimes(salesStat.getTimes() + 1);
                } else {
                  SalesMonthStat salesStat = new SalesMonthStat();
                  salesStat.setShopId(shop.getId());
                  salesStat.setProductId(productId);
                  salesStat.setStatYear(DateUtil.getYear(repairOrderDTO.getVestDate()));
                  salesStat.setStatMonth(DateUtil.getMonth(repairOrderDTO.getVestDate()));
                  salesStat.setStatDay(-1);
                  salesStat.setStatTime(DateUtil.get6clock(repairOrderDTO.getVestDate()));
                  salesStat.setAmount(amount);
                  salesStat.setTotal(repairOrderItem.getTotal());
                  salesStat.setTimes(1);
                  productStatMap.put(productId, salesStat);
                }

                Inventory inventory = writer.getById(Inventory.class, productId);
                if (inventory != null && NumberUtil.longValue(inventory.getLastSalesTime()) < NumberUtil.longValue(repairOrderDTO.getVestDate())) {
                  inventory.setLastSalesTime(repairOrderDTO.getVestDate());
                  writer.update(inventory);
                }
              }
            }
          }


          List<SalesOrderDTO> salesOrderDTOList = txnService.getSalesOrderDTOListByVestDate(shop.getId(), startTime, endTime);

          if (CollectionUtils.isNotEmpty(salesOrderDTOList)) {
            for (SalesOrderDTO salesOrderDTO : salesOrderDTOList) {
              if (salesOrderDTO.getStatus() == OrderStatus.SALE_REPEAL) {
                continue;
              }
              List<SalesOrderItem> salesOrderItemList = writer.getSalesOrderItemsByOrderId(salesOrderDTO.getId());
              if (CollectionUtils.isEmpty(salesOrderItemList)) {
                continue;
              }
              for (SalesOrderItem salesOrderItem : salesOrderItemList) {
                long productId = salesOrderItem.getProductId();

                Double amount = salesOrderItem.getAmount();
                ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(salesOrderItem.getProductId(), shopId);
                if (UnitUtil.isStorageUnit(salesOrderItem.getUnit(), productLocalInfoDTO)) {
                  amount = amount * productLocalInfoDTO.getRate();
                }
                if (productStatMap.containsKey(productId)) {
                  SalesMonthStat salesStat = productStatMap.get(productId);
                  salesStat.setAmount(salesStat.getAmount() + amount);
                  salesStat.setTotal(salesStat.getTotal() + salesOrderItem.getTotal());
                  salesStat.setTimes(salesStat.getTimes() + 1);
                } else {
                  SalesMonthStat salesStat = new SalesMonthStat();
                  salesStat.setShopId(shop.getId());
                  salesStat.setProductId(productId);
                  salesStat.setStatYear(DateUtil.getYear(salesOrderDTO.getVestDate()));
                  salesStat.setStatMonth(DateUtil.getMonth(salesOrderDTO.getVestDate()));
                  salesStat.setStatDay(-1);
                  salesStat.setStatTime(DateUtil.get6clock(salesOrderDTO.getVestDate()));
                  salesStat.setAmount(amount);
                  salesStat.setTotal(salesOrderItem.getTotal());
                  salesStat.setTimes(1);
                  productStatMap.put(productId, salesStat);
                }

                Inventory inventory = writer.getById(Inventory.class, productId);
                if (inventory != null && NumberUtil.longValue(inventory.getLastSalesTime()) < NumberUtil.longValue(salesOrderDTO.getVestDate())) {
                  inventory.setLastSalesTime(salesOrderDTO.getVestDate());
                  writer.update(inventory);
                }
              }
            }
          }


          Set<Long> productSet = productStatMap.keySet();
          for (Long id : productSet) {
            SalesMonthStat newStat = productStatMap.get(id);
            writer.save(newStat);
          }
          startTime = endTime;
          writer.flush();
        }
        writer.commit(status);
      } catch (Exception e) {
        writer.rollback(status);
        LOG.error(e.getMessage(), e);
        LOG.error("初始化销售单 施工单 salesMonthStat. shopID:{}", shop.getId());
      } finally {
        writer.rollback(status);
      }
      LOG.info("初始化销售单 施工单 salesMonthStat。shopID:{} 结束。已完成 {} 家，共 {} 家。", new Object[]{shop.getId(), i, shops.size()});
    }

    long end = System.currentTimeMillis();
    LOG.info("初始化销售月统计表结束. 结束时间：{}; 共耗时：{} 秒", DateUtil.dateLongToStr(end), (end - begin) / 1000);
    return "redirect:salesStat.do?method=getGoodSaleCost";
  }

  public String initProductLastSalesTime() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();
    List<Shop> shops = configService.getShop();
    for (Shop shop : shops) {
      Object status = writer.begin();
      try {
        int totalNum = writer.countInventoryNumber(shop.getId());
        if (totalNum > 0) {
          Pager pager = new Pager(totalNum, FIRST_PAGE_NO, 5000);
          for (int index = 1; index <= pager.getTotalPage(); index++) {
            pager = new Pager(totalNum, index, 5000);

            List<Inventory> inventoryList = writer.getInventoryByShopId(shop.getId(), pager);
            if (CollectionUtils.isEmpty(inventoryList)) {
              continue;
            }

            for (Inventory inventory : inventoryList) {
              Long inventoryId = inventory.getId();
              Sort sort = new Sort(" a.vestDate ", " desc ");
              Long salesVestDate = NumberUtil.longValue(writer.getSalesVestDateByShopId(shop.getId(), inventoryId, sort));
              Long repairVestDate = NumberUtil.longValue(writer.getRepairVestDateByShopId(shop.getId(), inventoryId, sort));

              Long lastSalesVestDate = salesVestDate > repairVestDate ? salesVestDate : repairVestDate;
              if (NumberUtil.longValue(lastSalesVestDate) > 0) {
                inventory.setLastSalesTime(lastSalesVestDate);
                writer.update(inventory);
              }
            }
            writer.flush();
          }
        }
        writer.commit(status);
      } catch (Exception e) {
        writer.rollback(status);
        LOG.error("初始化库存单商品销售时间 出错. shopID:{}", shop.getId());
        LOG.error(e.getMessage(), e);
      } finally {
        writer.rollback(status);
      }
    }
    return "redirect:salesStat.do?method=getBadSaleCost";
  }

  @RequestMapping(params = "method=initVehicleServeMonthStat")
  public String initVehicleServeMonthStat(HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId != 0) {
      return "/";
    }
    long begin = System.currentTimeMillis();
    LOG.info("开始初始化车型统计月统计表. vehicle_serve_month_stat");
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object statusDel = writer.begin();
    writer.deleteVehicleServeMonthStat();
    writer.commit(statusDel);
    List<VehicleModifyLog> logs = customerService.getVehicleModifyLogByStatus(new StatProcessStatus[]{StatProcessStatus.NEW, StatProcessStatus.FAIL});
    customerService.batchUpdateVehicleModifyLogStatus(logs, StatProcessStatus.DONE);

    List<Shop> shops = configService.getShop();
    for (int i = 0; i < shops.size(); i++) {
      Shop shop = shops.get(i);
      shopId = shop.getId();
      RepairOrder firstRepairOrder = txnService.getFirstRepairOrderByVestDate(shopId);
      WashBeautyOrder firstWashBeautyOrder = txnService.getFirstWashBeautyOrderByVestDate(shopId);
      if (firstRepairOrder == null && firstWashBeautyOrder == null) {
        continue;
      }
      Object status = writer.begin();
      try {
        long firstOrderDate = shop.getCreationDate();
        if (firstRepairOrder != null) {
          firstOrderDate = firstRepairOrder.getVestDate() == null ? firstRepairOrder.getCreationDate() : firstRepairOrder.getVestDate();
        }
        if (firstWashBeautyOrder != null) {
          long washBeautyOrderFirstDate = firstWashBeautyOrder.getVestDate() == null ? firstWashBeautyOrder.getCreationDate() : firstWashBeautyOrder.getVestDate();
          firstOrderDate = firstOrderDate < washBeautyOrderFirstDate ? firstOrderDate : washBeautyOrderFirstDate;
        }

        int year = DateUtil.getYear(firstOrderDate);
        int month = DateUtil.getMonth(firstOrderDate);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, 1, 0, 0, 0);

        long startTime = calendar.getTimeInMillis();
        while (startTime < System.currentTimeMillis()) {
          year = DateUtil.getYear(startTime);
          month = DateUtil.getMonth(startTime);

          calendar.add(Calendar.MONTH, 1);
          long endTime = calendar.getTimeInMillis();

          Map<String, VehicleServeMonthStat> vehicleServeMonthStatMap = new HashMap<String, VehicleServeMonthStat>();

          List<RepairOrderDTO> repairOrderDTOList = txnService.getRepairOrderListByVestDate(shop.getId(), startTime, endTime);

          if (CollectionUtils.isNotEmpty(repairOrderDTOList)) {
            for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
              if (repairOrderDTO.getStatus() != OrderStatus.REPAIR_SETTLED) {
                continue;
              }
              VehicleDTO vehicleDTO = userService.getVehicleById(repairOrderDTO.getVechicleId());
              if (vehicleDTO == null) {
                continue;
              }

              StringBuffer key = new StringBuffer();
              if (StringUtils.isNotBlank(vehicleDTO.getBrand())) {
                key.append(vehicleDTO.getBrand().toUpperCase());
              } else {
                key.append("");
              }
              key.append("_");
              if (StringUtils.isNotBlank(vehicleDTO.getModel())) {
                key.append(vehicleDTO.getModel().toUpperCase());
              } else {
                key.append("");
              }

              VehicleServeMonthStat vehicleServeMonthStat = vehicleServeMonthStatMap.get(key.toString());
              if (vehicleServeMonthStat == null) {
                vehicleServeMonthStat = new VehicleServeMonthStat();
                vehicleServeMonthStat.setShopId(shopId);
                vehicleServeMonthStat.setStatYear(year);
                vehicleServeMonthStat.setStatMonth(month);
                vehicleServeMonthStat.setBrand(StringUtils.isBlank(vehicleDTO.getBrand()) ? "" : vehicleDTO.getBrand());
                vehicleServeMonthStat.setModel(StringUtils.isBlank(vehicleDTO.getModel()) ? "" : vehicleDTO.getModel());
                vehicleServeMonthStat.setRepairTimes(1);
                vehicleServeMonthStat.setRepairTotal(repairOrderDTO.getTotal());
                vehicleServeMonthStatMap.put(key.toString(), vehicleServeMonthStat);
              } else {
                vehicleServeMonthStat.setRepairTimes(vehicleServeMonthStat.getRepairTimes() + 1);
                vehicleServeMonthStat.setRepairTotal(vehicleServeMonthStat.getRepairTotal() + repairOrderDTO.getTotal());
              }
            }
          }

          List<WashBeautyOrderDTO> washBeautyOrderDTOList = txnService.getWashBeautyOrderDTOByVestDate(shop.getId(), startTime, endTime);

          if (CollectionUtils.isNotEmpty(washBeautyOrderDTOList)) {
            for (WashBeautyOrderDTO washBeautyOrderDTO : washBeautyOrderDTOList) {
              if (washBeautyOrderDTO.getStatus() != OrderStatus.WASH_SETTLED) {
                continue;
              }

              VehicleDTO vehicleDTO = userService.getVehicleById(washBeautyOrderDTO.getVechicleId());
              if (vehicleDTO == null) {
                continue;
              }
              StringBuffer key = new StringBuffer();
              if (StringUtils.isNotBlank(vehicleDTO.getBrand())) {
                key.append(vehicleDTO.getBrand().toUpperCase());
              } else {
                key.append("");
              }
              key.append("_");
              if (StringUtils.isNotBlank(vehicleDTO.getModel())) {
                key.append(vehicleDTO.getModel().toUpperCase());
              } else {
                key.append("");
              }

              VehicleServeMonthStat vehicleServeMonthStat = vehicleServeMonthStatMap.get(key.toString());
              if (vehicleServeMonthStat == null) {
                vehicleServeMonthStat = new VehicleServeMonthStat();
                vehicleServeMonthStat.setShopId(shopId);
                vehicleServeMonthStat.setStatYear(year);
                vehicleServeMonthStat.setStatMonth(month);
                vehicleServeMonthStat.setBrand(StringUtils.isBlank(vehicleDTO.getBrand()) ? "" : vehicleDTO.getBrand());
                vehicleServeMonthStat.setModel(StringUtils.isBlank(vehicleDTO.getModel()) ? "" : vehicleDTO.getModel());
                vehicleServeMonthStat.setWashTimes(1);
                vehicleServeMonthStat.setWashTotal(washBeautyOrderDTO.getTotal());
                vehicleServeMonthStatMap.put(key.toString(), vehicleServeMonthStat);
              } else {
                vehicleServeMonthStat.setWashTimes(vehicleServeMonthStat.getWashTimes() + 1);
                vehicleServeMonthStat.setWashTotal(vehicleServeMonthStat.getWashTotal() + washBeautyOrderDTO.getTotal());
              }
            }
          }

          Set<String> keySet = vehicleServeMonthStatMap.keySet();
          for (String key : keySet) {
            VehicleServeMonthStat monthStat = vehicleServeMonthStatMap.get(key);
            monthStat.setTotalConsume(monthStat.getRepairTotal() + monthStat.getWashTotal());
            monthStat.setTotalTimes(monthStat.getRepairTimes() + monthStat.getWashTimes());
            writer.save(monthStat);
          }
          startTime = endTime;
          writer.flush();
        }
        writer.commit(status);
      } catch (Exception e) {
        writer.rollback(status);
        LOG.error("初始化车型统计月统计表时出错。shopID:{}", shopId);
        LOG.error(e.getMessage(), e);
      } finally {
        writer.rollback(status);
      }
      LOG.info("初始化车型统计月统计表。shopID:{} 结束。已完成 {} 家，共 {} 家。", new Object[]{shop.getId(), i, shops.size()});
    }
    long end = System.currentTimeMillis();
    LOG.info("初始化车型统计月统计表结束. 结束时间：{}; 共耗时：{} 秒", DateUtil.dateLongToStr(end), (end - begin) / 1000);
    return "redirect:vehicleStat.do?method=carStatistics";
  }


  @RequestMapping(params = "method=initBusinessCategory")
  public void initBusinessCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<CategoryItemRelation> categoryItemRelationList = rfiTxnService.getCategoryItemRelation();
    PrintWriter out = response.getWriter();

    if (CollectionUtils.isEmpty(categoryItemRelationList)) {
      return;
    }

    Map<Long, Category> categoryMap = new HashMap<Long, Category>();

    for (CategoryItemRelation categoryItemRelation : categoryItemRelationList) {
      if (null == categoryItemRelation.getCategoryId() || null == categoryItemRelation.getServiceId()) {
        continue;
      }
      Category category = rfiTxnService.getCategory(categoryItemRelation.getCategoryId());

      if (null == category || StringUtils.isBlank(category.getCategoryName())) {

      }
      categoryMap.put(categoryItemRelation.getServiceId(), category);
    }

    boolean washBeautyOrderItemFlag = true;
    boolean repairOrderServiceFlag = true;
    try {

      List<WashBeautyOrderItem> washBeautyOrderItemList = rfiTxnService.getWashBeautyOrderItem();

      rfiTxnService.initWashBeautyOrderCategory(washBeautyOrderItemList, categoryMap);
    } catch (Exception e) {
      washBeautyOrderItemFlag = false;
      LOG.error("initWashBeautyOrderCategory");
      LOG.error(e.getMessage(), e);
    }

    try {
      List<RepairOrderService> repairOrderServiceList = txnService.getRepairOrderService();

      txnService.initRepairOrderServiceCategory(repairOrderServiceList, categoryMap);
    } catch (Exception e) {
      repairOrderServiceFlag = false;
      LOG.error("initRepairOrderServiceCategory");
      LOG.error(e.getMessage(), e);
    }

    StringBuffer stringBuffer = new StringBuffer("");

    if (!washBeautyOrderItemFlag) {
      stringBuffer.append("initWashBeautyOrderCategory error.");
    } else {
      stringBuffer.append("initWashBeautyOrderCategory success.");
    }

    if (!repairOrderServiceFlag) {
      stringBuffer.append("initRepairOrderServiceCategory error.");
    } else {
      stringBuffer.append("initRepairOrderServiceCategory success.");
    }

    out.write(stringBuffer.toString());

    out.flush();

    out.close();
  }


  @RequestMapping(params = "method=initCategory")
  public void initCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
    List<Category> categoryList = getCategoryListForInit();
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    PrintWriter out = response.getWriter();
    boolean flag = true;
    try {

      categoryList = rfiTxnService.initCategoryList(categoryList);

      if (CollectionUtils.isEmpty(categoryList)) {
        return;
      }

      for (Category category : categoryList) {
        List<Category> categoryList2 = rfiTxnService.getCategoryByNameNotDefault(category.getCategoryName());

        if (CollectionUtils.isEmpty(categoryList2)) {
          continue;
        }

        rfiTxnService.initCategoryRelationItem(category, categoryList2);
      }
    } catch (Exception e) {
      flag = false;
      LOG.error("method=initCategory");
      LOG.error(e.getMessage(), e);
    } finally {
      String str = "";
      if (flag) {
        str = "success";
      } else {
        str = "error";
      }

      out.write(str);
      out.flush();
      ;
      out.close();
    }
  }

  private List<Category> getCategoryListForInit() {
    List<String> categoryNameList = new ArrayList<String>();
    categoryNameList.add("精品");
    categoryNameList.add("机修");
    categoryNameList.add("装潢");
    categoryNameList.add("音响");
    categoryNameList.add("油漆");
    categoryNameList.add("精洗");
    categoryNameList.add("膜");
    categoryNameList.add("轮胎");
    List<Category> categoryList = new ArrayList<Category>();

    for (String str : categoryNameList) {
      Category category = new Category();
      category.setCategoryName(str);
      category.setCategoryType(CategoryType.BUSINESS_CLASSIFICATION);
      category.setShopId(-1L);

      categoryList.add(category);
    }

    return categoryList;
  }

  @RequestMapping(params = "method=initStoreHouse")
  public String initStoreHouse(ModelMap model, HttpServletRequest request) {
    IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
    try {
      //判断是否是jackchen用户 如果不是 返回到登陆页面
      long shopId = Long.parseLong(request.getSession().getAttribute("shopId").toString());
      if (shopId != 0) {
        return "/";
      }
      long begin = System.currentTimeMillis();
      LOG.info("系统开始初始化仓库，开始时间为:" + DateUtil.dateLongToStr(begin));
      List<Shop> shopList = ServiceManager.getService(IConfigService.class).getShop();
      for (Shop shop : shopList) {
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shop.getShopVersionId())) {
          LOG.info("Shop[{}，{}]的ShopVersion[{}] 初始化仓库！", new Object[]{shop.getId(), shop.getName(), shop.getShopVersionId()});
          storeHouseService.initDefaultStoreHouse(shop.getId());
        } else {
          LOG.info("Shop[{}，{}]的ShopVersion[{}] 不符合要求,跳过！", new Object[]{shop.getId(), shop.getName(), shop.getShopVersionId()});
        }
      }

      LOG.info("系统开始初始化仓库结束. 共耗时: {} 秒", (System.currentTimeMillis() - begin) / 1000);
    } catch (Exception e) {
      LOG.error("系统开始初始化仓库出错.");
      LOG.error(e.getMessage(), e);
      return "/";
    }
    return "/";
  }

  @RequestMapping(params = "method=moveSupplierReturnPayableToPayable")
  public void moveSupplierReturnPayableToPayable(HttpServletRequest request, HttpServletResponse response) throws Exception {
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    int size = 2000;
    int totalRows = supplierPayableService.countSupplierReturnPayable();

    if (totalRows == 0) {
      return;
    }
    PrintWriter out = response.getWriter();
    try {
      while (true) {

        List<SupplierReturnPayable> supplierReturnPayableList = supplierPayableService.getSupplierReturnPayable(size);

        if (CollectionUtils.isEmpty(supplierReturnPayableList)) {
          break;
        }

        List<Long> ids = new ArrayList<Long>();
        List<Long> orderIds = new ArrayList<Long>();
        for (SupplierReturnPayable supplierReturnPayable : supplierReturnPayableList) {
          ids.add(supplierReturnPayable.getId());
          orderIds.add(supplierReturnPayable.getPurchaseReturnId());
        }

        txnService.moveSupplierReturnPayableToPayable(ids, orderIds.toArray(new Long[orderIds.size()]));
      }
      if (supplierPayableService.countSupplierReturnPayable() == 0) {
        out.write("success");
      }

    } catch (Exception e) {
      LOG.error("method=moveSupplierReturnPayableToPayable");
      LOG.error(e.getMessage(), e);
    } finally {
      out.flush();
      out.close();
    }

  }

  @RequestMapping(params = "method=initNormalBrandAndModel")
  public void initNormalBrandAndModel(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);

    PrintWriter out = response.getWriter();
    try {

      List<ModelDTO> modelDTOList = productService.getModelHasBrandId();

      if (CollectionUtils.isEmpty(modelDTOList)) {
        return;
      }

      Set<Long> brandIds = new HashSet<Long>();

      for (ModelDTO modelDTO : modelDTOList) {
        if (null == modelDTO.getBrandId()) {
          continue;
        }
        brandIds.add(modelDTO.getBrandId());
      }

      List<BrandDTO> brandDTOList = productService.getBrandList(brandIds.toArray(new Long[brandIds.size()]));

      Map<Long, BrandDTO> brandDTOMap = BrandDTO.ListToMap(brandDTOList);

      for (ModelDTO modelDTO : modelDTOList) {
        BrandDTO brandDTO = brandDTOMap.get(modelDTO.getBrandId());

        if (null != brandDTO) {
          if (null == brandDTO.getModelDTOList()) {
            List<ModelDTO> modelDTOs = new ArrayList<ModelDTO>();
            modelDTOs.add(modelDTO);
            brandDTO.setModelDTOList(modelDTOs);
          } else {
            brandDTO.getModelDTOList().add(modelDTO);
          }

          brandDTOMap.put(modelDTO.getBrandId(), brandDTO);
        }
      }

      List<BrandDTO> brandDTOs = new ArrayList<BrandDTO>();

      Iterator it = brandDTOMap.values().iterator();

      while (it.hasNext()) {
        brandDTOs.add((BrandDTO) it.next());
      }

      productService.initNormalBrandAndModel(brandDTOList);

      out.write("success");

    } catch (Exception e) {
      LOG.error("method=initNormalBrandAndModel");
      LOG.error(e.getMessage(), e);
    } finally {
      out.flush();
      out.close();
    }

  }

  @RequestMapping(params = "method=initReceivableHistory")
  public String initReceivableHistory(HttpServletRequest request) {
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId != 0L) {
        return "/";
      }
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      List<Shop> shopList = configService.getShop();
      TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);

      Long indexShopId = null;
      Long receivableId = null;

      for (Shop shop : shopList) {
        TxnWriter writer = txnDaoManager.getWriter();
        Object status = writer.begin();

        try {
          indexShopId = shop.getId();

          List<PurchaseReturn> purchaseReturnList = writer.getPurchaseReturn(indexShopId);
          if (CollectionUtils.isNotEmpty(purchaseReturnList)) {

            for (PurchaseReturn purchaseReturn : purchaseReturnList) {
              Payable payable = writer.getPayableDTOByOrderId(indexShopId, purchaseReturn.getId(), false);

              List<PayableHistoryRecord> payableHistoryRecordList = writer.getSettledRecord(indexShopId, OrderTypes.RETURN, purchaseReturn.getId());


              if (CollectionUtils.isNotEmpty(payableHistoryRecordList)) {
                PayableHistoryRecord payableHistoryRecord = payableHistoryRecordList.get(0);
                if (payable == null) {
                  payable = new Payable(payableHistoryRecord);
                  payable.setReceiptNo(purchaseReturn.getReceiptNo());
                  writer.save(payable);
                }
                PayableHistoryDTO payableHistoryDTO = payable.toDTO().toPayableHistoryDTO();
                payableHistoryDTO.setCheckNo(payableHistoryRecord.getCheckNo());
                PayableHistory payableHistory = new PayableHistory(payableHistoryDTO);
                writer.save(payableHistory);
                payableHistoryRecord.setPayableHistoryId(payableHistory.getId());
                payableHistoryRecord.setPayableId(payable.getId());
                writer.update(payableHistoryRecord);
              }
            }
          }


          List<OrderStatus> orderStatusList = new ArrayList<OrderStatus>();
          orderStatusList.add(OrderStatus.REPAIR_REPEAL);
          orderStatusList.add(OrderStatus.SALE_REPEAL);
          orderStatusList.add(OrderStatus.WASH_REPEAL);
          int count = writer.countReceptionRecordBySopId(indexShopId, orderStatusList);
          if (count <= 0) {
            continue;
          }
          Pager pager = new Pager(count, FIRST_PAGE_NO, 2000);

          for (int index = 1; index <= pager.getTotalPage(); index++) {
            List<ReceptionRecord> receptionRecordList = writer.getReceptionRecordBySopId(indexShopId, pager, orderStatusList);
            if (CollectionUtils.isEmpty(receptionRecordList)) {
              continue;
            }

            for (ReceptionRecord receptionRecord : receptionRecordList) {
              receivableId = receptionRecord.getReceivableId();

              ReceivableHistory receivableHistory = receptionRecord.toReceivableHistory();
              if (receivableId != null) {
                Receivable receivable = writer.getById(Receivable.class, receivableId);
                if (receivable != null) {
                  receivableHistory.setCustomerId(receivable.getCustomerId());
                  if (receivableHistory.getMemberId() != null && receivable.getMemberId() != null && receivable.getMemberId().equals(receivableHistory.getMemberId())) {

                    receivableHistory.setMemberNo(receivable.getMemberNo());
                  }
                }
              }
              writer.save(receivableHistory);
              receptionRecord.setReceivableHistoryId(receivableHistory.getId());
              writer.update(receptionRecord);
            }
            writer.flush();
          }
          writer.commit(status);
        } finally {
          writer.rollback(status);
        }
      }


    } catch (Exception e) {
      LOG.error("初始化失败");
      LOG.error(e.getMessage(), e);
    }
    LOG.info("后台结束初始化,跳转到营业统计页面");
    return "stat/businessStatistics";
  }

  @RequestMapping(params = "method=initInventoryCheckRecord")
  public void initInventoryCheckRecord(HttpServletRequest request) {

    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    InventoryCheckDTO inventoryCheckDTO = new InventoryCheckDTO();
    List<InventoryCheck> inventoryChecks = txnService.getInventoryChecks(inventoryCheckDTO);
    if (CollectionUtil.isEmpty(inventoryChecks)) {
      LOG.warn("系统没有盘点记录！初始化异常！");
      return;
    }
    Map<String, String> map = new HashMap<String, String>();
    List<InventoryCheckItem> items = null;
    String receipt = "";
    LOG.info("开始初始化盘点记录！time=" + new Date());
    Object status = writer.begin();
    for (InventoryCheck inventoryCheck : inventoryChecks) {
      if (inventoryCheck == null || inventoryCheck.getId() == null) {
        LOG.error("inventoryCheck is null！");
        continue;
      }
      Double adjust_price_total = 0d;
      for (InventoryCheckItem item : writer.getInventoryCheckItem(inventoryCheck.getId())) {
        Double price = NumberUtil.doubleVal(item.getInventoryAveragePrice());
        Double actualAmount = NumberUtil.doubleVal(item.getActualInventoryAmount());
        Double amount = NumberUtil.doubleVal(item.getInventoryAmount());
        adjust_price_total += price * (actualAmount - amount);
      }
      inventoryCheck.setAdjustPriceTotal(adjust_price_total);
      receipt = getReceiptNo(WebUtil.getShopId(request), OrderReceiptNoPrefix.INVENTORY_CHECK, inventoryCheck.getCreationDate(), map);
      inventoryCheck.setReceiptNo(receipt);
      map.put(inventoryCheck.getShopId().toString() + inventoryCheck.getReceiptNo().substring(0, 8), inventoryCheck.getReceiptNo());
      writer.update(inventoryCheck);
    }
    writer.commit(status);
    LOG.info("初始化盘点记录成功！time=" + new Date());

  }

  @RequestMapping(params = "method=initShopArea")
  @ResponseBody
  public Object initShopArea(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      configService.initAllShopArea();
      return new Result("初始化成功", true);
    } catch (Exception e) {
      LOG.error("method=initShopArea" + e.getMessage(), e);
      return new Result("初始化失败", true);
    }
  }

  @RequestMapping(params = "method=initTxnShop")
  @ResponseBody
  public Object initTxnShop(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
      UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
      UserWriter userWriter = userDaoManager.getWriter();
      List<Long> shopIds = NumberUtil.parseLongValues(request.getParameter("shopIds"));
      List<Shop> shopList;
      ConfigWriter writer = configDaoManager.getWriter();
      if (CollectionUtil.isNotEmpty(shopIds)) {
        shopList = writer.getShopByShopId(shopIds.toArray(new Long[shopIds.size()]));
      } else {
        shopIds = NumberUtil.parseLongValues(request.getParameter("withoutShopIds"));
        if (CollectionUtil.isEmpty(shopIds)) return new Result("初始化数据为空！", false);
        shopList = writer.getShopWithoutShopIdByShopVersionId(10000010017531653L, shopIds.toArray(new Long[shopIds.size()]));
      }
      if (CollectionUtil.isEmpty(shopList)) {
        return new Result("初始化数据为空！", false);
      }
      List<UserGroupUser> userGroupUsers;
      UserGroupUser userGroupUser;
      List<UserGroup> userGroups = userWriter.getUserGroupsByShopVersionId(10000010037193620l);
      Map<String, Long> map = new HashMap<String, Long>();
      for (UserGroup userGroup : userGroups) {
        map.put(userGroup.getName(), userGroup.getId());
      }
      Object status = writer.begin();
      try {
        if (CollectionUtil.isNotEmpty(shopList)) {
          for (Shop shop : shopList) {
            shop.setShopVersionId(10000010037193620l);
            writer.update(shop);
          }
          writer.commit(status);
        }
      } finally {
        writer.rollback(status);
      }
      LOG.info("shopversion ini success");
      status = userWriter.begin();
      try {
        if (CollectionUtil.isNotEmpty(shopList)) {
          for (Shop shop : shopList) {
            List<User> userList = userWriter.getUserByShopId(shop.getId());
            //10000010017541668   销售顾问
            //10000010017541669   老板/财务
            //10000010017541670    经理/店长
            //10000010017541671    仓管
            //10000010017541672     前台
            for (User u : userList) {
              userGroupUsers = userWriter.getUserGroupUser(u.getId());
              if (!userGroupUsers.isEmpty()) {
                userGroupUser = userGroupUsers.get(0);
                if (NumberUtil.isEqual(userGroupUser.getUserGroupId(), 10000010017541668L)) {
                  userGroupUser.setUserGroupId(map.get("销售顾问"));
                } else if (NumberUtil.isEqual(userGroupUser.getUserGroupId(), 10000010017541669L)) {
                  userGroupUser.setUserGroupId(map.get("老板/财务"));
                } else if (NumberUtil.isEqual(userGroupUser.getUserGroupId(), 10000010017541670L)) {
                  userGroupUser.setUserGroupId(map.get("经理/店长"));
                } else if (NumberUtil.isEqual(userGroupUser.getUserGroupId(), 10000010017541671L)) {
                  userGroupUser.setUserGroupId(map.get("仓管"));
                } else if (NumberUtil.isEqual(userGroupUser.getUserGroupId(), 10000010017541672L)) {
                  userGroupUser.setUserGroupId(map.get("前台"));
                } else {
                  throw new Exception("userGroupUserId error : " + userGroupUser.getUserGroupId());
                }
                userWriter.update(userGroupUser);
              }
            }
          }
          userWriter.commit(status);
        }
      } finally {
        userWriter.rollback(status);
      }
      LOG.info("userGroup ini success");
      return new Result("初始化成功", true);
    } catch (Exception e) {
      LOG.error("method=initTxnShop" + e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }

  @RequestMapping(params = "method=initShopAttachment")
  @ResponseBody
  @Deprecated
  public Object initShopAttachment(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
      ConfigWriter writer = configDaoManager.getWriter();
      List<Shop> shopList = writer.getShop();
      Object status = writer.begin();
      Attachment attachment;
      try {
        if (CollectionUtil.isNotEmpty(shopList)) {
          for (Shop shop : shopList) {
            attachment = new Attachment();
            attachment.setShopId(shop.getId());
            attachment.setType(AttachmentType.SHOP_APPEARANCE_PHOTO);
            if (com.bcgogo.utils.StringUtil.isNotEmpty(shop.getPhoto()))
              attachment.setName(shop.getPhoto().length() > 100 ? shop.getPhoto().substring(0, 100) : shop.getPhoto());
            writer.save(attachment);
          }
          writer.commit(status);
        }
      } finally {
        writer.rollback(status);
      }
      return new Result("初始化成功", true);
    } catch (Exception e) {
      LOG.error("method=initShopAttachment" + e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }


  //初始化提醒事项
  @RequestMapping(params = "method=initRemindEvent")
  public void initRemindEvent(HttpServletRequest request) throws Exception {
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();

    LOG.info("初始化提醒事项开始");

    //从维修美容提醒表Repair_remind_event导出数据
    List<RepairRemindEvent> repairRemindEventList = getTxnService().getAllRepairRemindEvent();
    LOG.info("开始执行，维修美容提醒表Repair_remind_event导出数据");
    Object status = txnWriter.begin();
    try {
      if (!CollectionUtil.isEmpty(repairRemindEventList)) {
        for (RepairRemindEvent repairRemindEvent : repairRemindEventList) {
          //过滤掉无效的数据（单据不存在的、车牌号不存在的、客户不存在的）
          RepairOrderDTO repairOrderDTO = getRfiTxnService().getRepairOrderDTOById(repairRemindEvent.getRepairOrderId(), repairRemindEvent.getShopId());
          CustomerDTO customerDTO;
          VehicleDTO vehicleDTO;
          if (repairOrderDTO == null || repairOrderDTO.getCustomerId() == null || repairOrderDTO.getVechicleId() == null) {
            continue;
          }
          customerDTO = customerService.getCustomerById(repairOrderDTO.getCustomerId());
          vehicleDTO = userService.getVehicleById(repairOrderDTO.getVechicleId());
          if (customerDTO == null || vehicleDTO == null) {
            continue;
          }
          RemindEvent remindEvent = new RemindEvent();
          remindEvent.setShopId(repairRemindEvent.getShopId());
          remindEvent.setOrderId(repairRemindEvent.getRepairOrderId());
          remindEvent.setOrderType(OrderTypes.REPAIR.toString());
          remindEvent.setEventType(RemindEventType.REPAIR.toString());
          remindEvent.setEventStatus(repairRemindEvent.getEventTypeEnum().toString());
          remindEvent.setObjectId(repairRemindEvent.getProductId());  //商品ID
          remindEvent.setRemindTime(repairRemindEvent.getFinishTime());
          remindEvent.setRemindStatus(UserConstant.Status.ACTIVITY);
          remindEvent.setCustomerId(customerDTO.getId());
          remindEvent.setCustomerName(customerDTO.getName());
          remindEvent.setMobile(customerDTO.getMobile());
          remindEvent.setLicenceNo(vehicleDTO.getLicenceNo());
          remindEvent.setServices(repairRemindEvent.getService());
          remindEvent.setOldRemindEventId(repairRemindEvent.getId());
          txnWriter.save(remindEvent);
        }
      }
      txnWriter.commit(status);
      LOG.info("执行完毕，维修美容提醒表Repair_remind_event导出数据");
    } catch (Exception e) {
      LOG.error("执行出错，维修美容提醒表Repair_remind_event导出数据");
      LOG.error(e.getMessage(), e);
    } finally {
      txnWriter.rollback(status);
    }

    //从客户欠款表debt导出数据
    List<Debt> debtList = getTxnService().getAllDebt();
    LOG.info("开始执行，客户欠款表debt导出数据");
    status = txnWriter.begin();
    try {
      if (!CollectionUtil.isEmpty(debtList)) {
        for (Debt debt : debtList) {
          //过滤掉无效的数据（单据不存在的、客户不存在的）
          CustomerDTO customerDTO;
          if (debt.getCustomerId() == null || debt.getOrderId() == null) {
            continue;
          }
          if (debt.getDebt() <= 0) {
            continue;
          }
          customerDTO = customerService.getCustomerById(debt.getCustomerId());
          if (customerDTO == null) {
            continue;
          }
          if (OrderTypes.REPAIR.equals(debt.getOrderTypeEnum())) {
            RepairOrderDTO repairOrderDTO = getRfiTxnService().getRepairOrderDTOById(debt.getOrderId(), debt.getShopId());
            if (repairOrderDTO == null) {
              continue;
            }
          } else if (OrderTypes.WASH_BEAUTY.equals(debt.getOrderTypeEnum())) {
            WashBeautyOrderDTO washBeautyOrderDTO = getTxnService().getWashBeautyOrderDTOById(debt.getShopId(), debt.getOrderId());
            if (washBeautyOrderDTO == null) {
              continue;
            }
          } else if (OrderTypes.SALE.equals(debt.getOrderType())) {
            SalesOrderDTO salesOrderDTO = getTxnService().getSalesOrder(debt.getOrderId());
            if (salesOrderDTO == null) {
              continue;
            }
          } else if (OrderTypes.MEMBER_BUY_CARD.equals(debt.getOrderType())) {
            MemberCardOrderDTO memberCardOrderDTO = getTxnService().getMemberCardOrderDTOById(debt.getShopId(), debt.getOrderId());
            if (memberCardOrderDTO == null) {
              continue;
            }
          }

          RemindEvent remindEvent = new RemindEvent();
          remindEvent.setShopId(debt.getShopId());
          remindEvent.setOrderId(debt.getOrderId());
          remindEvent.setCustomerId(debt.getCustomerId());
          remindEvent.setCustomerName(customerDTO.getName());
          remindEvent.setMobile(customerDTO.getMobile());
          remindEvent.setOrderType(debt.getOrderTypeEnum().toString());
          remindEvent.setEventType(RemindEventType.DEBT.toString());
          remindEvent.setRemindTime(debt.getRemindTime());
          remindEvent.setRemindStatus(debt.getRemindStatus());
          remindEvent.setDebt(debt.getDebt());
          remindEvent.setOldRemindEventId(debt.getId());
          txnWriter.save(remindEvent);
        }
      }
      txnWriter.commit(status);
      LOG.info("执行完毕，客户欠款表debt导出数据");
    } catch (Exception e) {
      LOG.error("执行出错，客户欠款表debt导出数据");
      LOG.error(e.getMessage(), e);
    } finally {
      txnWriter.rollback(status);
    }

    //从进销存提醒表inventory_remind_event表导出数据
    List<InventoryRemindEvent> inventoryRemindEventList = getTxnService().getAllInventoryRemindEvent();
    LOG.info("开始执行，进销存提醒表inventory_remind_event表导出数据");
    status = txnWriter.begin();
    try {
      if (!CollectionUtil.isEmpty(inventoryRemindEventList)) {
        //过滤掉无效的数据（单据不存在的、供应商不存在的）
        for (InventoryRemindEvent inventoryRemindEvent : inventoryRemindEventList) {
          TxnWriter writer = ServiceManager.getService(TxnDaoManager.class).getWriter();
          PurchaseOrder purchaseOrder = writer.getById(PurchaseOrder.class, inventoryRemindEvent.getPurchaseOrderId());
          if (purchaseOrder == null) {
            continue;
          }
          SupplierDTO supplierDTO = supplierService.getSupplierById(purchaseOrder.getSupplierId(),purchaseOrder.getShopId());
          if (supplierDTO == null) {
            continue;
          }
          RemindEvent remindEvent = new RemindEvent();
          remindEvent.setShopId(inventoryRemindEvent.getShopId());
          remindEvent.setSupplierId(purchaseOrder.getSupplierId());
          remindEvent.setSupplierName(supplierDTO.getName());
          remindEvent.setMobile(supplierDTO.getMobile());
          remindEvent.setOrderId(inventoryRemindEvent.getPurchaseOrderId());
          remindEvent.setOrderType(OrderTypes.PURCHASE.toString());
          remindEvent.setEventType(RemindEventType.TXN.toString());
          remindEvent.setRemindTime(inventoryRemindEvent.getDeliverTime());
          remindEvent.setRemindStatus(UserConstant.Status.ACTIVITY);
          remindEvent.setOldRemindEventId(inventoryRemindEvent.getId());
          txnWriter.save(remindEvent);
        }
      }
      txnWriter.commit(status);
      LOG.info("执行完毕，进销存提醒表inventory_remind_event表导出数据");
    } catch (Exception e) {
      LOG.error("执行出错，进销存提醒表inventory_remind_event表导出数据");
      LOG.error(e.getMessage(), e);
    } finally {
      txnWriter.rollback(status);
    }

    //从客户预约服务表customer_service_job导出数据
    List<CustomerServiceJob> customerServiceJobList = userService.getAllCustomerServiceJob();
    LOG.info("开始执行，客户预约服务表customer_service_job导出数据");
    status = txnWriter.begin();
    try {
      if (!CollectionUtil.isEmpty(customerServiceJobList)) {
        for (CustomerServiceJob customerServiceJob : customerServiceJobList) {
          CustomerDTO customerDTO = customerService.getCustomerById(customerServiceJob.getCustomerId());
          if (customerDTO == null) {
            continue;
          }
          //生日不涉及车牌号
          if (customerServiceJob.getVehicleId() == null && customerServiceJob.getRemindType() != UserConstant.BIRTH_TIME) {
            continue;
          }
          VehicleDTO vehicleDTO = new VehicleDTO();
          if (customerServiceJob.getVehicleId() != null) {
            vehicleDTO = userService.getVehicleById(customerServiceJob.getVehicleId());
            if (vehicleDTO == null) {
              continue;
            }
          }
          RemindEvent remindEvent = new RemindEvent();
          remindEvent.setShopId(customerServiceJob.getShopId());
          remindEvent.setEventType(RemindEventType.CUSTOMER_SERVICE.toString());
          if (customerServiceJob.getRemindType() == UserConstant.INSURE_TIME) {
            remindEvent.setEventStatus(UserConstant.CustomerRemindType.INSURE_TIME);      //保险
          } else if (customerServiceJob.getRemindType() == UserConstant.EXAMINE_TIME) {
            remindEvent.setEventStatus(UserConstant.CustomerRemindType.EXAMINE_TIME);     //验车
          } else if (customerServiceJob.getRemindType() == UserConstant.MAINTAIN_TIME) {
            remindEvent.setEventStatus(UserConstant.CustomerRemindType.MAINTAIN_TIME);    //保养
          } else if (customerServiceJob.getRemindType() == UserConstant.BIRTH_TIME) {
            remindEvent.setEventStatus(UserConstant.CustomerRemindType.BIRTH_TIME);       //生日
          } else if (customerServiceJob.getRemindType() == UserConstant.APPOINT_SERVICE) {
            remindEvent.setEventStatus(UserConstant.CustomerRemindType.APPOINT_SERVICE); //自定义预约服务
          }
          remindEvent.setRemindTime(customerServiceJob.getRemindTime());
          remindEvent.setRemindStatus(customerServiceJob.getStatus());
          remindEvent.setCustomerId(customerServiceJob.getCustomerId());
          remindEvent.setCustomerName(customerDTO.getName());
          remindEvent.setMobile(customerDTO.getMobile());
          remindEvent.setAppointServiceId(customerServiceJob.getAppointServiceId());
          remindEvent.setLicenceNo(vehicleDTO.getLicenceNo());
          remindEvent.setOldRemindEventId(customerServiceJob.getId());
          txnWriter.save(remindEvent);
        }
      }
      txnWriter.commit(status);
      LOG.info("执行完毕，客户预约服务表customer_service_job导出数据");
    } catch (Exception e) {
      LOG.error("执行出错，客户预约服务表customer_service_job导出数据");
      LOG.error(e.getMessage(), e);
    } finally {
      txnWriter.rollback(status);
    }

    //从会员服务提醒表member_service导出数据
    List<MemberService> memberServiceList = membersService.getAllMemberService();
    LOG.info("开始执行，会员服务提醒表member_service导出数据");
    status = txnWriter.begin();
    try {
      if (!CollectionUtil.isEmpty(memberServiceList)) {
        for (MemberService memberService : memberServiceList) {
          Member member = membersService.getMemberById(memberService.getMemberId());
          if (member == null) {
            continue;
          }
          CustomerDTO customerDTO = userService.getCustomerById(member.getCustomerId());
          if (customerDTO == null) {
            continue;
          }
          RemindEvent remindEvent = new RemindEvent();
          remindEvent.setShopId(member.getShopId());
          remindEvent.setEventType(RemindEventType.MEMBER_SERVICE.toString());
          remindEvent.setEventStatus(UserConstant.CustomerRemindType.MEMBER_SERVICE);
          remindEvent.setRemindTime(memberService.getDeadline());
          remindEvent.setRemindStatus(memberService.getRemindStatus());
          remindEvent.setCustomerId(member.getCustomerId());
          remindEvent.setCustomerName(customerDTO.getName());
          remindEvent.setMobile(customerDTO.getMobile());
          remindEvent.setServiceId(memberService.getServiceId());
          remindEvent.setOldRemindEventId(memberService.getId());
          txnWriter.save(remindEvent);
        }
      }
      txnWriter.commit(status);
      LOG.info("执行完毕，会员服务提醒表member_service导出数据");
    } catch (Exception e) {
      LOG.error("执行出错，会员服务提醒表member_service导出数据");
      LOG.error(e.getMessage(), e);
    } finally {
      txnWriter.rollback(status);
    }

    LOG.info("初始化提醒事项完毕");
  }

  @RequestMapping(params = "method=initUrlMonitorConfig")
  public String initUrlMonitorConfig(HttpServletRequest request) {

    try {
      IRequestMonitorService requestMonitorService = ServiceManager.getService(IRequestMonitorService.class);
      List<UrlMonitorConfig> urlMonitorConfigList = requestMonitorService.getUrlMonitorConfig(null);
      if (CollectionUtils.isEmpty(urlMonitorConfigList)) {
        LOG.error("url配置表为空,初始化失败");
        return "/";
      }
      RequestPerformanceCache.resetUrlMonitorConfigHashTable();
    } catch (Exception e) {
      LOG.error("url配置失败");
      LOG.error(e.getMessage(), e);
      return "/";
    }
    LOG.info("后台结束初始化,跳转到营业统计页面");
    return "stat/businessStatistics";
  }

  @RequestMapping(params = "method=initShopNamePy")
  public void initShopNamePy(HttpServletRequest request, HttpServletResponse response) {
    PrintWriter out = null;
    try {
      out = response.getWriter();
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      configService.initAllShopNamePy();
      out.write("初始店铺name  py 成功");

    } catch (Exception e) {
      LOG.error("初始店铺name  py 失败");
      LOG.error(e.getMessage(), e);
    } finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=initCustomerStockData")
  public void initCustomerStockData(HttpServletRequest request, HttpServletResponse response) {
    PrintWriter out = null;
    try {
      out = response.getWriter();
      getTxnService().initCustomerStock();
      out.write("初始客户库存 最近交易数据 成功");

    } catch (Exception e) {
      LOG.error("初始客户库存 最近交易数据 失败");
      LOG.error(e.getMessage(), e);
    } finally {
      out.flush();
      out.close();
    }
  }

  //初始化刷卡机扫描枪开关
  @RequestMapping(params = "method=initShopScanningGroupStatus")
  public void initShopScanningGroupStatus(HttpServletRequest request, HttpServletResponse response) {
    PrintWriter out = null;
    try {
      out = response.getWriter();
      IUserService userService = ServiceManager.getService(IUserService.class);
      IShopService shopService = ServiceManager.getService(IShopService.class);
      List<Long> shopIds = shopService.getAllShopIds();
      String scanningBarcodeShopIds = "10000010007150620,10000010006990599,10000010006380538";  //只有扫描枪的店面
      String wholesalerShopVersionIds = ServiceManager.getService(IConfigService.class).getConfig("WholesalerShopVersions", ShopConstant.BC_SHOP_ID);
      for (Long shopId : shopIds) {
        if (scanningBarcodeShopIds.contains(shopId.toString())) {
          userService.saveUserSwitch(shopId, "SCANNING_BARCODE", MessageSwitchStatus.ON.toString());
          userService.saveUserSwitch(shopId, "SCANNING_CARD", MessageSwitchStatus.OFF.toString());
        } else {
          Long shopVersionId = shopService.getShopByShopIds(shopId).get(shopId).getShopVersionId();
          UserSwitchDTO userSwitchDTO;
          if (StringUtils.isNotBlank(wholesalerShopVersionIds) && wholesalerShopVersionIds.contains(String.valueOf(shopVersionId))) {
            userSwitchDTO = userService.getUserSwitchByShopIdAndScene(shopId, "SCANNING_BARCODE");
            if (userSwitchDTO == null) {
              userService.saveUserSwitch(shopId, "SCANNING_BARCODE", MessageSwitchStatus.ON.toString());
            }
          } else {
            userSwitchDTO = userService.getUserSwitchByShopIdAndScene(shopId, "SCANNING_BARCODE");
            if (userSwitchDTO == null) {
              userService.saveUserSwitch(shopId, "SCANNING_BARCODE", MessageSwitchStatus.OFF.toString());
            }
            userSwitchDTO = userService.getUserSwitchByShopIdAndScene(shopId, "SCANNING_CARD");
            if (userSwitchDTO == null) {
              userService.saveUserSwitch(shopId, "SCANNING_CARD", MessageSwitchStatus.ON.toString());
            }

          }

        }
      }
      out.write("初始化刷卡机扫描枪开关 成功");
    } catch (Exception e) {
      LOG.error("初始化刷卡机扫描枪开关 失败");
      LOG.error(e.getMessage(), e);
    } finally {
      out.flush();
      out.close();
    }

  }

  @RequestMapping(params = "method=initSupplierInventory")
  @ResponseBody
  public Object initSupplierInventory(HttpServletRequest request) {
    try {
      IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
      productThroughService.initSupplierInventory();
      return new Result("初始化成功", true);
    } catch (Exception e) {
      LOG.error("initSupplierInventory");
      LOG.error(e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }


  @RequestMapping(params = "method=initProductThrough")
  @ResponseBody
  public Object initProductThrough(HttpServletRequest request) {
    try {
      IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
      String shopIdStr = request.getParameter("shopId");
      if (NumberUtil.isLongNumber(shopIdStr)) {
        productThroughService.initProductThrough(Long.valueOf(shopIdStr));
        return new Result("初始化成功", true);
      } else {
        return new Result("初始化失败", false);
      }

    } catch (Exception e) {
      LOG.error("initSupplierInventory");
      LOG.error(e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }

  /**
   * add by zhuj
   * 将customer、supplier中的联系人信息 初始化到联系人表中 作为主联系人
   * 目前导入前没有做幂等性判断 请不要重新导入
   * @param request
   * @return
   */
  @RequestMapping(params = "method=initContact")
  @ResponseBody
  public Object initContact(HttpServletRequest request) {
    int pageSize = 1000;// 每页1000
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    UserWriter userWriter = userDaoManager.getWriter();
    ConfigWriter configWriter = ServiceManager.getService(ConfigDaoManager.class).getWriter();
    Long customerCount = userWriter.countCustomer();
    Long supplierCount = userWriter.countSupplier();
    Long shopCount = configWriter.countShop();
    int pageNum = 1;
    try {
      // customerContact init begin
      StopWatch totalCustomerWatch = new StopWatch();
      totalCustomerWatch.start();
      if (customerCount.intValue() % pageSize == 0) {
        pageNum = customerCount.intValue() / pageSize;
        for (int i = 0; i < pageNum; i++) {
          Object status = userWriter.begin();
          try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("initCustomerContactPage[" + i + "]");
            List<CustomerDTO> customerDTOs = ServiceManager.getService(ICustomerService.class).getCustomerByPageSizeAndStart(pageSize, pageSize * i + 1);
            if (!CollectionUtils.isEmpty(customerDTOs)) {
              for (CustomerDTO customerDTO : customerDTOs) {
                if (customerDTO.isValidMainContact()){
                  ContactDTO contactDTO = new ContactDTO();
                  contactDTO.setCustomerId(customerDTO.getId());
                  contactDTO.setDisabled(1);
                  contactDTO.setEmail(customerDTO.getEmail());
                  contactDTO.setName(customerDTO.getContact());
                  contactDTO.setQq(customerDTO.getQq());
                  contactDTO.setMobile(customerDTO.getMobile());
                  contactDTO.setIsMainContact(1);
                  contactDTO.setLevel(0);
                  contactDTO.setShopId(customerDTO.getShopId());
                  userWriter.save(new Contact().fromDTO(contactDTO));
                }
              }
              userWriter.commit(status);
              stopWatch.stop();
              LOGGER.info("initCustomerContactPage[" + i + "],totalTimeSeconds:" + stopWatch.getTotalTimeSeconds());
            }
          } finally {
            userWriter.rollback(status);
          }
        }
      } else {
        pageNum = (customerCount.intValue() / pageSize + 1);
        for (int i = 0; i < pageNum; i++) {
          Object status = userWriter.begin();
          try {
            if (i == pageNum - 1) {
              pageSize = customerCount.intValue() % pageSize;
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("initCustomerContactPage[" + i + "]");
            List<CustomerDTO> customerDTOs = ServiceManager.getService(ICustomerService.class).getCustomerByPageSizeAndStart(pageSize, pageSize * i + 1);
            if (!CollectionUtils.isEmpty(customerDTOs)) {
              for (CustomerDTO customerDTO : customerDTOs) {
                if (customerDTO.isValidMainContact()){
                  ContactDTO contactDTO = new ContactDTO();
                  contactDTO.setCustomerId(customerDTO.getId());
                  contactDTO.setDisabled(1);
                  contactDTO.setEmail(customerDTO.getEmail());
                  contactDTO.setName(customerDTO.getContact());
                  contactDTO.setQq(customerDTO.getQq());
                  contactDTO.setMobile(customerDTO.getMobile());
                  contactDTO.setIsMainContact(1);
                  contactDTO.setLevel(0);
                  contactDTO.setShopId(customerDTO.getShopId());
                  userWriter.save(new Contact().fromDTO(contactDTO));
                }
              }
              userWriter.commit(status);
              stopWatch.stop();
              LOGGER.info("initCustomerContactPage[" + i + "],totalTimeSeconds:" + stopWatch.getTotalTimeSeconds());
            }
          } finally {
            userWriter.rollback(status);
          }
        }
      }
      totalCustomerWatch.stop();
      LOGGER.info("totalCustomerInit :" + totalCustomerWatch.getTotalTimeSeconds());
      // customerConact init end

      // supplierContact init begin
      StopWatch totalSupplierWatch = new StopWatch();
      totalSupplierWatch.start();
      if (supplierCount.intValue() % pageSize == 0) {
        pageNum = supplierCount.intValue() / pageSize;
        for (int i = 0; i < pageNum; i++) {
          Object status = userWriter.begin();
          try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("initSupplierContactPage[" + i + "]");
            List<SupplierDTO> supplierDTOs = ServiceManager.getService(ISupplierService.class).getSuppliersByPageAndStart(pageSize, pageSize * i + 1);
            if (!CollectionUtils.isEmpty(supplierDTOs)) {
              for (SupplierDTO supplierDTO : supplierDTOs) {
                if (supplierDTO.isValidMainContact()){
                  ContactDTO contactDTO = new ContactDTO();
                  contactDTO.setSupplierId(supplierDTO.getId());
                  contactDTO.setDisabled(1);
                  contactDTO.setEmail(supplierDTO.getEmail());
                  contactDTO.setName(supplierDTO.getContact());
                  contactDTO.setQq(supplierDTO.getQq());
                  contactDTO.setMobile(supplierDTO.getMobile());
                  contactDTO.setIsMainContact(1);
                  contactDTO.setLevel(0);
                  contactDTO.setShopId(supplierDTO.getShopId());
                  userWriter.save(new Contact().fromDTO(contactDTO));
                }
              }
              userWriter.commit(status);
              stopWatch.stop();
              LOGGER.info("initSupplierContactPage[" + i + "],totalTimeSeconds:" + stopWatch.getTotalTimeSeconds());
            }
          } finally {
            userWriter.rollback(status);
          }
        }
      } else {
        pageNum = (supplierCount.intValue() / pageSize + 1);
        for (int i = 0; i < pageNum; i++) {
          Object status = userWriter.begin();
          try {
            if (i == pageNum - 1) {
              pageSize = supplierCount.intValue() % pageSize;
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("initSupplierContactPage[" + i + "]");
            List<SupplierDTO> supplierDTOs = ServiceManager.getService(ISupplierService.class).getSuppliersByPageAndStart(pageSize, pageSize * i + 1);
            if (!CollectionUtils.isEmpty(supplierDTOs)) {
              for (SupplierDTO supplierDTO : supplierDTOs) {
                if (supplierDTO.isValidMainContact()){
                  ContactDTO contactDTO = new ContactDTO();
                  contactDTO.setSupplierId(supplierDTO.getId());
                  contactDTO.setDisabled(1);
                  contactDTO.setEmail(supplierDTO.getEmail());
                  contactDTO.setName(supplierDTO.getContact());
                  contactDTO.setQq(supplierDTO.getQq());
                  contactDTO.setMobile(supplierDTO.getMobile());
                  contactDTO.setIsMainContact(1);
                  contactDTO.setLevel(0);
                  contactDTO.setShopId(supplierDTO.getShopId());
                  userWriter.save(new Contact().fromDTO(contactDTO));
                }
              }
              userWriter.commit(status);
              stopWatch.stop();
              LOGGER.info("initSupplierContactPage[" + i + "],totalTimeSeconds:" + stopWatch.getTotalTimeSeconds());
            }
          } finally {
            userWriter.rollback(status);
          }
        }
      }
      totalSupplierWatch.stop();
      LOGGER.info("totalSupplierInit :" + totalSupplierWatch.getTotalTimeSeconds());
      // supplierConact init end

      // shop contacts init begin
      StopWatch totalShopWatch = new StopWatch();
      totalShopWatch.start();
      if (shopCount.intValue() % pageSize == 0) {
        pageNum = shopCount.intValue() / pageSize;
        for (int i = 0; i < pageNum; i++) {
          Object status = configWriter.begin();
          try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("initShopContactPage[" + i + "]");
            List<Shop> shops = configWriter.getShops(i, pageSize);
            if (!CollectionUtils.isEmpty(shops)) {
              for (Shop shop : shops) {
                if (shop.toDTO().isValidMainContact()){
                  ContactDTO contactDTO = new ContactDTO();
                  contactDTO.setDisabled(1);
                  contactDTO.setEmail(shop.getEmail());
                  contactDTO.setName(shop.getContact());
                  contactDTO.setQq(shop.getQq());
                  contactDTO.setMobile(shop.getMobile());
                  contactDTO.setIsMainContact(1);
                  contactDTO.setIsShopOwner(1);
                  contactDTO.setShopId(shop.getId());
                  contactDTO.setLevel(0);
                  configWriter.save(new ShopContact().fromDTO(contactDTO));
                }
              }
              configWriter.commit(status);
              stopWatch.stop();
              LOGGER.info("initSupplierContactPage[" + i + "],totalTimeSeconds:" + stopWatch.getTotalTimeSeconds());
            }
          } finally {
            configWriter.rollback(status);
          }
        }
      } else {
        pageNum = (shopCount.intValue() / pageSize + 1);
        for (int i = 0; i < pageNum; i++) {
          Object status = configWriter.begin();
          try {
            if (i == pageNum - 1) {
              pageSize = shopCount.intValue() % pageSize;
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("initShopContactPage[" + i + "]");
            List<Shop> shops = configWriter.getShops(i, pageSize);
            if (!CollectionUtils.isEmpty(shops)) {
              for (Shop shop : shops) {
                if (shop.toDTO().isValidMainContact()){
                  ContactDTO contactDTO = new ContactDTO();
                  contactDTO.setDisabled(1);
                  contactDTO.setEmail(shop.getEmail());
                  contactDTO.setName(shop.getContact());
                  contactDTO.setQq(shop.getQq());
                  contactDTO.setMobile(shop.getMobile());
                  contactDTO.setIsMainContact(1);
                  contactDTO.setIsShopOwner(1);
                  contactDTO.setShopId(shop.getId());
                  contactDTO.setLevel(0);
                  configWriter.save(new ShopContact().fromDTO(contactDTO));
                }
              }
              configWriter.commit(status);
              stopWatch.stop();
              LOGGER.info("initSupplierContactPage[" + i + "],totalTimeSeconds:" + stopWatch.getTotalTimeSeconds());
            }
          } finally {
            configWriter.rollback(status);
          }
        }
      }
      totalShopWatch.stop();
      LOGGER.info("totalShopInit :" + totalShopWatch.getTotalTimeSeconds());
      // shop Conact init end

      return new Result("初始化成功", false);
    } catch (Exception e) {
      LOG.error("initContact");
      LOG.error(e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }

  @RequestMapping(params = "method=initContactBugFix")
  @ResponseBody
  public Object initContactBugFix(HttpServletRequest request){

    try {
      IUserService userService = ServiceManager.getService(IUserService.class);
      userService.initContactBugFix();
      return new Result("初始化成功", false);
    } catch (Exception e) {
      LOG.error("initContactBugFix");
      LOG.error(e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }

  @RequestMapping(params = "method=initShopContactBugFix")
  @ResponseBody
  public Object initShopContactBugFix(HttpServletRequest request){
    ConfigWriter configWriter = ServiceManager.getService(ConfigDaoManager.class).getWriter();
    Object status = configWriter.begin();
    try {
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      List<Shop> shops = configService.getTodoBugfixShops();
      for(Shop shop : shops){
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setDisabled(ContactConstant.ENABLED);
        contactDTO.setEmail(shop.getEmail());
        contactDTO.setName(shop.getLegalRep());
        contactDTO.setQq(shop.getQq());
        contactDTO.setMobile(shop.getMobile());
        contactDTO.setIsMainContact(ContactConstant.IS_MAIN_CONTACT);
        contactDTO.setIsShopOwner(ContactConstant.IS_SHOP_OWNER);
        contactDTO.setShopId(shop.getId());
        contactDTO.setLevel(0);
        configWriter.save(new ShopContact().fromDTO(contactDTO));
      }
      configWriter.commit(status);
      return new Result("初始化成功", false);
    } catch (Exception e) {
      LOG.error("initContactBugFix");
      LOG.error(e.getMessage(), e);
      return new Result("初始化失败", false);
    } finally{
      configWriter.rollback(status);
    }
  }


  @Autowired
  private DefaultPageConfigGenerator defaultPageConfigGenerator;

  @RequestMapping(params = "method=createDefaultPageConfig")
  @ResponseBody
  public Object createDefaultPageConfig(HttpServletRequest request) throws Exception {
    return defaultPageConfigGenerator.createDefaultPageConfig(request);
  }

  @RequestMapping(params = "method=schedule")
  @ResponseBody
  public Object scheduleTest(HttpServletRequest request) throws Exception {
//    IShopService shopService = ServiceManager.getService(IShopService.class);
//    IConfigService configService = ServiceManager.getService(IConfigService.class);
//    IClientApplyService clientApplyService = ServiceManager.getService(IClientApplyService.class);
//    IApplyPushMessageService applyPushMessageService = ServiceManager.getService(IApplyPushMessageService.class);
    try {
      ShopCheckSchedule schedule = new ShopCheckSchedule();
      schedule.processSmsJobs();
//      VehicleMaintainRemindSchedule schedule = new VehicleMaintainRemindSchedule();
//      schedule.processUpdateVehicleOBDMileage();
//      schedule.processUpdateMaintainRemind();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), false);
    }
    return new Result(true);
  }

  //手动处理推荐 schedule
  @RequestMapping(params = "method=processRecommendJobs")
  public void processRecommendJobs(HttpServletResponse response,String processType) {
    PrintWriter out = null;
    try {
      out = response.getWriter();
      IRecommendService recommendService = ServiceManager.getService(IRecommendService.class);
      ITradePushMessageService tradePushMessageService = ServiceManager.getService(ITradePushMessageService.class);
      if("MoveData".equals(processType)){
        recommendService.moveProductRecommendToTrace();
        recommendService.movePreBuyOrderItemRecommendToTrace();
        recommendService.moveShopRecommendToTrace();

        IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);

        pushMessageService.pushMessageMigration(PushMessageType.values(),5000);
        pushMessageService.pushMessageReceiverMigration(5000);
        pushMessageService.pushMessageFeedbackRecordMigration(5000);
        pushMessageService.movePushMessageReceiverRecordToTraceByPushTime(DateUtil.getTheDayTime());
        LOG.warn("MoveData schedule success!");
        out.write("MoveData schedule success!");
      }else if("ShopRecommend".equals(processType)){
        recommendService.processShopRecommend();
        LOG.warn("ShopRecommend schedule success!");
        out.write("ShopRecommend schedule success!");
      }else if("AccessoryRecommend".equals(processType)){
        recommendService.processAccessoryRecommend();
        LOG.warn("AccessoryRecommend schedule success!");
        out.write("AccessoryRecommend schedule success!");
      }else if("PreBuyOrderInformationRecommend".equals(processType)){
        recommendService.processPreBuyOrderInformationRecommend();
        LOG.warn("PreBuyOrderInformationRecommend schedule success!");
        out.write("PreBuyOrderInformationRecommend schedule success!");
      }else if("PushMessageBuild".equals(processType)){
        tradePushMessageService.processPushMessageBuildTask();
        LOG.warn("PushMessageBuild schedule success!");
        out.write("PushMessageBuild schedule success!");
      }else if("SellWellBusinessChanceBuild".equals(processType)){
        SellWellBusinessChanceSchedule schedule = new SellWellBusinessChanceSchedule();
        schedule.processSellWellBusinessChanceJobs();
        out.write("SellWellBusinessChanceBuild schedule success!");
      }else{
        recommendService.processAccessoryRecommend();
        recommendService.processPreBuyOrderInformationRecommend();
        recommendService.processShopRecommend();

        tradePushMessageService.processPushMessageBuildTask();
        LOG.warn("All schedule success!");
        out.write("All schedule success!");
      }

    } catch (Exception e) {
      LOG.error("schedule fail!");
      LOG.error(e.getMessage(), e);
      out.write("schedule fail");
    } finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=processSupplierReceivable")
  public void processSupplierReceivable(HttpServletResponse response) {
    PrintWriter out = null;
    try {
      out = response.getWriter();
      List<Payable> payables = ServiceManager.getService(ITxnService.class).getSupplierPayable();
      if(CollectionUtils.isNotEmpty(payables)) {
        ServiceManager.getService(ITxnService.class).saveRemindEvent(payables);
      }
      out.print("init supplier Debt Remind success!");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      out.write("process supplierReceivable fail");
    } finally {
      out.flush();
      out.close();
    }
  }



  @RequestMapping(params = "method=initOldImageToUpYun")
  @ResponseBody
  public Object initOldImageToUpYun(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<Shop> shopList = configService.getShop();
      Map<String, String> params=UpYunManager.getInstance().generateDefaultUpYunParams();
      for(Shop shop : shopList){
        AttachmentDTO shopPhoto = configService.getAttachmentByShopId(shop.getId(),AttachmentType.SHOP_APPEARANCE_PHOTO);
        if(shopPhoto!=null && StringUtils.isNotBlank(shopPhoto.getName()) && !ArrayUtils.isEmpty(shopPhoto.getContent())) {
          String shopPhotoPath = UpYunManager.getInstance().generateUploadImagePath(shop.getId(),shopPhoto.getName());
          if(UpYunManager.getInstance().writeFile(shopPhotoPath, shopPhoto.getContent(),true,params)){
            DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shop.getId(), shop.getId(), DataType.SHOP, ImageType.SHOP_MAIN_IMAGE, 0);
            Set<ImageType> imageTypeSet = new HashSet<ImageType>();
            imageTypeSet.add(ImageType.SHOP_MAIN_IMAGE);
            dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shop.getId(), shopPhotoPath));
            imageService.saveOrUpdateDataImageDTOs(shop.getId(),imageTypeSet,DataType.SHOP,shop.getId(),dataImageRelationDTO);
          }else{
            throw new Exception("店铺照片上传失败!shopId:"+shop.getId());
          }
        }
        AttachmentDTO shopBusinessLicense = configService.getAttachmentByShopId(shop.getId(),AttachmentType.SHOP_BUSINESS_LICENSE_PHOTO);

        if(shopBusinessLicense!=null && StringUtils.isNotBlank(shopBusinessLicense.getName()) && !ArrayUtils.isEmpty(shopBusinessLicense.getContent())) {
          String shopPhotoPath = UpYunManager.getInstance().generateUploadImagePath(shop.getId(),shopBusinessLicense.getName());
          if(UpYunManager.getInstance().writeFile(shopPhotoPath, shopBusinessLicense.getContent(),true,params)){
            DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shop.getId(), shop.getId(), DataType.SHOP, ImageType.SHOP_BUSINESS_LICENSE_IMAGE, 1);
            dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shop.getId(), shopPhotoPath));
            Set<ImageType> imageTypeSet = new HashSet<ImageType>();
            imageTypeSet.add(ImageType.SHOP_BUSINESS_LICENSE_IMAGE);
            imageService.saveOrUpdateDataImageDTOs(shop.getId(),imageTypeSet,DataType.SHOP,shop.getId(),dataImageRelationDTO);
          }else{
            throw new Exception("营业执照上传失败!shopId:"+shop.getId());
          }
        }
      }

      return new Result("initOldImageToUpYun初始化成功", true);
    } catch (Exception e) {
      LOG.error("method=initOldImageToUpYun" + e.getMessage(), e);
      return new Result("initOldImageToUpYun初始化失败", false);
    }
  }

  @RequestMapping(params = "method=initShopRQ")
  public void initShopRQ(HttpServletResponse response, Long shopId) {
    IImageService imageService = ServiceManager.getService(IImageService.class);
    PrintWriter out = null;
    try {
      out = response.getWriter();
      // 设置缩略图的参数
      Map<String, String> params = new HashMap<String, String>();
      // 若在 upyun 后台配置过缩略图版本号，则可以设置缩略图的版本名称
      // 注意：只有存在缩略图版本名称，才会按照配置参数制作缩略图，否则无效
      params.put(UpYun.PARAMS.KEY_X_GMKERL_THUMBNAIL.getValue(), ImageScene.IMAGE_AUTO.getImageVersion());
      Set<ImageType> imageTypes = new HashSet<ImageType>();
      imageTypes.add(ImageType.SHOP_RQ_IMAGE);
      if(shopId != null){
        ShopDTO shop = ServiceManager.getService(IConfigService.class).getShopById(shopId);
        if(shop == null){
          out.print("shop不存在！");
          return;
        }
        byte[] imageBytes = RQUtil.getRQImageByte(shop.getId().toString() + "," + shop.getName(), 110);
        String shopPhotoPath = UpYunManager.getInstance().generateUploadImagePath(shop.getId(),shop.getId()+"-rq.png");
        if(UpYunManager.getInstance().writeFile(shopPhotoPath, imageBytes,true,params)){
          DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shop.getId(), shop.getId(), DataType.SHOP, ImageType.SHOP_RQ_IMAGE, 0);
          dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shop.getId(), shopPhotoPath));
          imageService.saveOrUpdateDataImageDTOs(shop.getId(), imageTypes, DataType.SHOP, shop.getId(), dataImageRelationDTO);
        }else{
          throw new Exception("店铺二维码图片上传失败!shopId:"+shop.getId());
        }
      }else{
        List<Shop> shops = ServiceManager.getService(IConfigService.class).getShop();
        for(Shop shop : shops) {
          byte[] imageBytes = RQUtil.getRQImageByte(shop.getId().toString() + "," + shop.getName(), 110);
          String shopPhotoPath = UpYunManager.getInstance().generateUploadImagePath(shop.getId(),shop.getId()+"-rq.png");
          if(UpYunManager.getInstance().writeFile(shopPhotoPath, imageBytes,true,params)){
            DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shop.getId(), shop.getId(), DataType.SHOP, ImageType.SHOP_RQ_IMAGE, 0);
            dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shop.getId(), shopPhotoPath));
            imageService.saveOrUpdateDataImageDTOs(shop.getId(), imageTypes, DataType.SHOP, shop.getId(), dataImageRelationDTO);
          }else{
            throw new Exception("店铺二维码图片上传失败!shopId:"+shop.getId());
          }
          System.out.print(ConfigUtils.getUpYunDomainUrl()+shopPhotoPath);
        }
      }
      out.print("initShopRQ success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("initShopRQ failed!");
    } finally {
      out.flush();
      out.close();
    }

  }

  @RequestMapping(params = "method=initShopCustomerSupplierSync")
  @ResponseBody
  public Object initShopCustomerSupplierSync(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Result result = new Result();
    IRelatedShopUpdateService relatedShopUpdateService = ServiceManager.getService(IRelatedShopUpdateService.class);
    try {
      result = relatedShopUpdateService.initShopCustomerSupplierSync();
      return result;
    } catch (Exception e) {
      LOG.error("method=initShopCustomerSupplierSync" + e.getMessage(), e);
      return new Result("initShopCustomerSupplierSync初始化失败", false);
    }
  }

  @RequestMapping(params = "method=initErrorImagePath")
  public void initErrorImagePath(HttpServletRequest request, HttpServletResponse response,String opt) throws Exception {
    IImageService imageService = ServiceManager.getService(IImageService.class);
    PrintWriter out = null;
    try {
      out = response.getWriter();
      if("check".equals(opt)){
        imageService.getNotFindImageInYunByDBImagePath();
      }else{
        imageService.initErrorImagePath();
      }
      out.print("initErrorImagePath success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("initErrorImagePath failed!");
    } finally {
      out.flush();
      out.close();
    }

  }

  @RequestMapping(params = "method=initMoveMessage")
  public void initMoveMessage(HttpServletResponse response,String type) {
    IMessageService messageService = ServiceManager.getService(IMessageService.class);
    IApplyService applyService = ServiceManager.getService(IApplyService.class);
    IApplyPushMessageService applyPushMessageService = ServiceManager.getService(IApplyPushMessageService.class);
    PrintWriter out = null;
    try {
      out = response.getWriter();
      List<ShopDTO> shopDTOList = ServiceManager.getService(IConfigService.class).getActiveShop();
      if("ALL".equals(type)){
        for (ShopDTO shopDTO : shopDTOList) {
          List<ShopRelationInviteDTO> shopRelationInviteDTOList = applyService.getPendingShopRelationInviteDTOs(shopDTO.getId(), InviteType.CUSTOMER_INVITE);
          if(CollectionUtils.isNotEmpty(shopRelationInviteDTOList)){
            for(ShopRelationInviteDTO shopRelationInviteDTO:shopRelationInviteDTOList)
              applyPushMessageService.createApplyRelatedPushMessage(shopRelationInviteDTO.getOriginShopId(), shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getId(),shopRelationInviteDTO.getInviteTime(), PushMessageSourceType.APPLY_CUSTOMER);
          }
          shopRelationInviteDTOList = applyService.getPendingShopRelationInviteDTOs(shopDTO.getId(), InviteType.SUPPLIER_INVITE);
          if(CollectionUtils.isNotEmpty(shopRelationInviteDTOList)){
            for(ShopRelationInviteDTO shopRelationInviteDTO:shopRelationInviteDTOList)
              applyPushMessageService.createApplyRelatedPushMessage(shopRelationInviteDTO.getOriginShopId(), shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getId(),shopRelationInviteDTO.getInviteTime(), PushMessageSourceType.APPLY_SUPPLIER);
          }
        }
        messageService.moveInviteAndNoticeAndMessageToPushMessage();
      }else if("ShopRelationInvite".equals(type)){
        for (ShopDTO shopDTO : shopDTOList) {
          List<ShopRelationInviteDTO> shopRelationInviteDTOList = applyService.getPendingShopRelationInviteDTOs(shopDTO.getId(), InviteType.CUSTOMER_INVITE);
          if(CollectionUtils.isNotEmpty(shopRelationInviteDTOList)){
            for(ShopRelationInviteDTO shopRelationInviteDTO:shopRelationInviteDTOList)
              applyPushMessageService.createApplyRelatedPushMessage(shopRelationInviteDTO.getOriginShopId(), shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getId(),shopRelationInviteDTO.getInviteTime(), PushMessageSourceType.APPLY_CUSTOMER);
          }
          shopRelationInviteDTOList = applyService.getPendingShopRelationInviteDTOs(shopDTO.getId(), InviteType.SUPPLIER_INVITE);
          if(CollectionUtils.isNotEmpty(shopRelationInviteDTOList)){
            for(ShopRelationInviteDTO shopRelationInviteDTO:shopRelationInviteDTOList)
              applyPushMessageService.createApplyRelatedPushMessage(shopRelationInviteDTO.getOriginShopId(), shopRelationInviteDTO.getInvitedShopId(), shopRelationInviteDTO.getId(),shopRelationInviteDTO.getInviteTime(), PushMessageSourceType.APPLY_SUPPLIER);
          }
        }
      }

      for (ShopDTO shopDTO : shopDTOList) {
        List<Long> userIds = ServiceManager.getService(IUserCacheService.class).getUserIdsByShopId(shopDTO.getId());
        if(CollectionUtils.isNotEmpty(userIds)){
          for(Long userId : userIds){
            ServiceManager.getService(IPushMessageService.class).updatePushMessageCategoryStatNumberInMemCache(shopDTO.getId(), userId, PushMessageCategory.values());
          }
        }
      }
      LOG.error("initMoveMessage success!");
      out.print("initMoveMessage success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("initMoveMessage failed!");
    } finally {
      out.flush();
      out.close();
    }

  }

  @RequestMapping(params = "method=initBcgogoReceivableOrder")
  public void initBcgogoReceivableOrder(HttpServletResponse response) {
    PrintWriter out = null;
    try {
      out = response.getWriter();
      ServiceManager.getService(IBcgogoReceivableService.class).initBcgogoReceivableOrder();

      LOG.warn("initBcgogoReceivableOrder success!");
      out.print("initBcgogoReceivableOrder success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("initBcgogoReceivableOrder failed!");
    } finally {
      out.flush();
      out.close();
    }

  }

  @RequestMapping(params = "method=collectDuplicateProductNameAndUnits")
  public void collectDuplicateProductNameAndUnits(HttpServletResponse response) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    PrintWriter out = null;
    try {
      out = response.getWriter();
      productService.collectDuplicateProductNameAndUnits();
      LOG.warn("collectDuplicateProductNameAndUnits success!");
      out.print("collectDuplicateProductNameAndUnits success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("collectDuplicateProductNameAndUnits failed!");
    } finally {
      out.flush();
      out.close();
    }

  }

  /**
   * 把求购单拆成一个order对应一个item
   * @param response
   */
  @RequestMapping(params = "method=initPreBuyOrder")
  public void initPreBuyOrder(HttpServletResponse response) {
    PrintWriter out = null;
    try {
      out = response.getWriter();
      IPreBuyOrderService preBuyOrderService = ServiceManager.getService(IPreBuyOrderService.class);

      List<PreBuyOrderItemDTO> preBuyOrderItemDTOs= preBuyOrderService.getPreBuyOrderItemDetailDTO(new PreBuyOrderSearchCondition());
      if(CollectionUtil.isEmpty(preBuyOrderItemDTOs)){
        out.print("生产没有求购,报价数据！！！");
        return;
      }
      Map<Long,List<PreBuyOrderItemDTO>> preBuyOrderItemDTOMap =new HashMap<Long, List<PreBuyOrderItemDTO>>();
      for(PreBuyOrderItemDTO itemDTO:preBuyOrderItemDTOs){
        if(itemDTO==null||itemDTO.getId()==null){
          continue;
        }
        List<PreBuyOrderItemDTO> itemDTOs= preBuyOrderItemDTOMap.get(itemDTO.getPreBuyOrderId());
        if(itemDTOs==null){
          itemDTOs=new ArrayList<PreBuyOrderItemDTO>();
          preBuyOrderItemDTOMap.put(itemDTO.getPreBuyOrderId(),itemDTOs);
        }
        itemDTOs.add(itemDTO);
      }
      //1.开始拆order
      TxnWriter writer=ServiceManager.getService(TxnDaoManager.class).getWriter();
      Object status=writer.begin();
      int itemCount=0;
      for(Long itemId:preBuyOrderItemDTOMap.keySet()){
        List<PreBuyOrderItemDTO> itemDTOs=preBuyOrderItemDTOMap.get(itemId);
        if(CollectionUtil.isEmpty(itemDTOs)||itemDTOs.size()==1){
          continue;
        }
        PreBuyOrderItemDTO firstItemDTO=CollectionUtil.getFirst(itemDTOs);
        PreBuyOrderDTO preBuyOrderDTO=preBuyOrderService.getPreBuyOrderDTOById(firstItemDTO.getShopId(),firstItemDTO.getPreBuyOrderId());
        boolean hasSetDefault=false;
        for(PreBuyOrderItemDTO itemDTO:itemDTOs){
          itemCount++;
          if(!hasSetDefault){  //多个item 把循环遇到的第一个设成默认的
            hasSetDefault=true;
            continue;
          }
          PreBuyOrder preBuyOrder=new PreBuyOrder();
          preBuyOrder.fromDTO(preBuyOrderDTO);
          writer.save(preBuyOrder);
          PreBuyOrderItem preBuyOrderItem=writer.getById(PreBuyOrderItem.class,itemDTO.getId());
          preBuyOrderItem.setPreBuyOrderId(preBuyOrder.getId());
          writer.update(preBuyOrderItem);
        }
      }
      writer.commit(status);
      //2.修正报价单中preBuyOrderId
      QuotedPreBuyOrderSearchConditionDTO conditionDTO=new QuotedPreBuyOrderSearchConditionDTO();
      List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOs=preBuyOrderService.getQuotedPreBuyOrderItem(conditionDTO);
      if(CollectionUtil.isNotEmpty(quotedPreBuyOrderItemDTOs)){
        for(QuotedPreBuyOrderItemDTO itemDTO:quotedPreBuyOrderItemDTOs){
          PreBuyOrderItemDTO pItemDTO=preBuyOrderService.getPreBuyOrderItemDTOById(itemDTO.getPreBuyOrderItemId());
          if(pItemDTO==null){
            LOG.warn("can't find the preBuyOrderItem");
            continue;
          }
          itemDTO.setPreBuyOrderId(pItemDTO.getPreBuyOrderId());
          preBuyOrderService.simpleUpdateQuotedPreBuyOrderItem(itemDTO);
        }
      }
      LOG.info("初始化成功，共初始化求购单数据{}条",itemCount);
      out.print("初始化求购成功处理数据条数:"+itemCount);
    } catch (Exception e) {
      LOG.error("initPreBuyOrder"+e.getMessage(), e);
      out.print(e.getMessage());
    }finally {
      out.flush();
      out.close();
    }
  }



  @RequestMapping(params = "method=initBusinessAccount")
  @ResponseBody
  public Result initBusinessAccount(HttpServletRequest request,HttpServletResponse response) {
    Result result = new Result();
    result.setSuccess(false);

    try {
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
      List<Shop> shops = configService.getShop();
      RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);

      StatDaoManager statDaoManager = ServiceManager.getService(StatDaoManager.class);
      StatWriter writer = statDaoManager.getWriter();

      for (Shop shop : shops) {
        Long shopId = shop.getId();
        BusinessAccountSearchConditionDTO searchConditionDTO = new BusinessAccountSearchConditionDTO();
        searchConditionDTO.setAccountEnum(BusinessAccountEnum.STATUS_SAVE);
        List<BusinessAccountDTO> businessAccountDTOList = businessAccountService.getBusinessAccountsBySearchCondition(shopId, searchConditionDTO);
        if (CollectionUtils.isEmpty(businessAccountDTOList)) {
          continue;
        }
        Map<String, BusinessCategory> businessCategoryMap = new HashMap<String, BusinessCategory>();


        for (BusinessAccountDTO businessAccountDTO : businessAccountDTOList) {
          if (StringUtils.isNotEmpty(businessAccountDTO.getAccountCategory())) {
            BusinessCategory businessCategory = businessCategoryMap.get(businessAccountDTO.getAccountCategory());
            if (businessCategory == null) {
              businessCategory = new BusinessCategory();
              businessCategory.setItemName(businessAccountDTO.getAccountCategory());
              businessCategory.setShopId(shopId);
              businessCategory.setItemType("account");

              if (businessAccountDTO.getAccountCategory().equals("营业外收入")) {
                businessCategory.setMoneyCategory(MoneyCategory.income);
              } else {
                businessCategory.setMoneyCategory(MoneyCategory.expenses);
              }
            }

            if(StringUtils.isNotEmpty(businessAccountDTO.getBusinessCategory())) {
              CategoryDTO categoryDTO = rfiTxnService.getCategoryDTOByName(businessAccountDTO.getShopId(), businessAccountDTO.getBusinessCategory(), CategoryType.BUSINESS_CLASSIFICATION);
              if (categoryDTO != null) {
                businessAccountDTO.setBusinessCategoryId(categoryDTO.getId());
              }
            }
            businessCategory.setUseTime(businessCategory.getUseTime() + 1);
            businessCategoryMap.put(businessAccountDTO.getAccountCategory(), businessCategory);
          }
        }
        if (CollectionUtils.isNotEmpty(businessCategoryMap.values())) {
          Object status = writer.begin();
          try {
            for (BusinessCategory businessCategory : businessCategoryMap.values()) {
              writer.save(businessCategory);
            }
            writer.commit(status);
          } finally {
            writer.rollback(status);
          }
        }
        for (BusinessAccountDTO businessAccountDTO : businessAccountDTOList) {
          if (StringUtils.isNotEmpty(businessAccountDTO.getAccountCategory())) {
            BusinessCategory businessCategory = businessCategoryMap.get(businessAccountDTO.getAccountCategory());
            if (businessCategory != null) {
              BusinessAccount businessAccount = writer.getById(BusinessAccount.class, businessAccountDTO.getId());
              businessAccount.setAccountCategoryId(businessCategory.getId());
              businessAccountDTO.setAccountCategoryId(businessCategory.getId());
              businessAccount.setBusinessCategoryId(businessAccountDTO.getBusinessCategoryId());
              Object status = writer.begin();
              try {
                writer.update(businessAccount);
                writer.commit(status);
              } finally {
                writer.rollback(status);
              }
            }
          }
          businessAccountService.businessCategoryStatByDTO(businessAccountDTO, CalculateType.ADD);
        }
      }
      result.setSuccess(true);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.setMsg(false, e.getMessage());
    }
    return result;
  }


  @RequestMapping(params = "method=initAccountAssistant")
  @ResponseBody
  public Result initAccountAssistant(HttpServletRequest request,HttpServletResponse response) {
    Result result = new Result();
    result.setSuccess(false);

    try {
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
      List<Shop> shops = configService.getShop();

      IUserService userService = ServiceManager.getService(IUserService.class);

      StatDaoManager statDaoManager = ServiceManager.getService(StatDaoManager.class);
      StatWriter writer = statDaoManager.getWriter();

      int departmentFail = 0;
      int salesManFail = 0;
      int salesManDeptConflict = 0;
      int departmentSuccess = 0;
      int salesManSuccess = 0;

      for (Shop shop : shops) {
        Long shopId = shop.getId();
        BusinessAccountSearchConditionDTO searchConditionDTO = new BusinessAccountSearchConditionDTO();
        searchConditionDTO.setAccountEnum(BusinessAccountEnum.STATUS_SAVE);
        List<BusinessAccountDTO> businessAccountDTOList = businessAccountService.getBusinessAccountsBySearchCondition(shopId, searchConditionDTO);
        if (CollectionUtils.isEmpty(businessAccountDTOList)) {
          continue;
        }
        List<BusinessAccountDTO> updateList = new ArrayList<BusinessAccountDTO>();

        for (BusinessAccountDTO businessAccountDTO : businessAccountDTOList) {

          if (StringUtils.isEmpty(businessAccountDTO.getDept()) && StringUtils.isEmpty(businessAccountDTO.getPerson())) {
            continue;
          }

          boolean update = false;

          Long departmentId = null;
          if (StringUtils.isNotEmpty(businessAccountDTO.getDept()) && businessAccountDTO.getDepartmentId() == null) {
            List<DepartmentDTO> departmentDTOs = userService.getDepartmentNameByShopIdName(businessAccountDTO.getShopId(), businessAccountDTO.getDept());
            if (CollectionUtils.isNotEmpty(departmentDTOs)) {
              departmentId = CollectionUtil.getFirst(departmentDTOs).getId();
              businessAccountDTO.setDepartmentId(departmentId);
              update = true;
              departmentSuccess++;
            } else {
              departmentFail++;
            }
          }

          if (StringUtils.isNotEmpty(businessAccountDTO.getPerson()) && businessAccountDTO.getSalesManId() == null) {
            SalesMan salesMan = userService.getSalesManByName(businessAccountDTO.getShopId(), businessAccountDTO.getPerson());
            if (salesMan != null) {
              businessAccountDTO.setSalesManId(salesMan.getId());
              update = true;
              salesManSuccess++;
              if (salesMan.getDepartmentId() != null && departmentId != null && salesMan.getDepartmentId().longValue() != departmentId.longValue()) {
                salesManDeptConflict++;
              }
            } else {
              salesManFail++;
            }
          }

          if (update) {
            updateList.add(businessAccountDTO);
          }

        }

        if (CollectionUtils.isEmpty(updateList)) {
          continue;
        }

        for (BusinessAccountDTO businessAccountDTO : updateList) {

          if (StringUtil.isEmpty(businessAccountDTO.getDept()) && StringUtil.isEmpty(businessAccountDTO.getPerson())) {
            continue;
          }

          BusinessAccount businessAccount = writer.getById(BusinessAccount.class, businessAccountDTO.getId());
          businessAccount.setDepartmentId(businessAccountDTO.getDepartmentId());
          businessAccount.setSalesManId(businessAccountDTO.getSalesManId());
          Object status = writer.begin();
          try {
            writer.update(businessAccount);
            writer.commit(status);
          } finally {
            writer.rollback(status);
          }
        }
      }
      result.setSuccess(true);
      result.setMsg("departmentFail：" + departmentFail + ",salesManFail：" + salesManFail + ",salesManDeptConflict：" + salesManDeptConflict
        + ",departmentSuccess：" + departmentSuccess + ",salesManSuccess：" + salesManSuccess);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.setMsg(false, e.getMessage());
    }
    return result;
  }

  @RequestMapping(params = "method=initSmsRechargeReceiptNo")
  public void initSmsRechargeReceiptNo(HttpServletResponse response) {
    PrintWriter out = null;
    try {
      out = response.getWriter();
      ServiceManager.getService(ISmsRechargeService.class).initSmsRechargeReceiptNo();
      out.print("initSmsRechargeReceiptNo success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("initSmsRechargeReceiptNo failed!");
    } finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=initBcgogoReceivableRecord")
  public void initBcgogoReceivableRecord(HttpServletResponse response) {
    PrintWriter out = null;
    ISmsRechargeService smsRechargeService =ServiceManager.getService(ISmsRechargeService.class);
    try {
      out = response.getWriter();
      SmsRechargeSearchCondition smsRechargeSearchCondition = new SmsRechargeSearchCondition();
      smsRechargeSearchCondition.setLimit(Integer.MAX_VALUE);
      Result result = smsRechargeService.searchSmsRechargeResult(smsRechargeSearchCondition);
      if(result != null && result.getData() != null) {
        List<SmsRechargeDTO> smsRechargeDTOList = (List<SmsRechargeDTO>)result.getData();
        for(SmsRechargeDTO smsRechargeDTO : smsRechargeDTOList) {
          smsRechargeService.generateBcgogoReceivableRecord(smsRechargeDTO);
        }
      }
      out.print("initBcgogoReceivableRecord success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("initBcgogoReceivableRecord failed!");
    } finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=initSmsInfo")
  public void initSmsInfo(HttpServletResponse response){
    PrintWriter out = null;
    try {
      out = response.getWriter();
      NotificationDaoManager notificationDaoManager=ServiceManager.getService(NotificationDaoManager.class);
      NotificationWriter notificationWriter = notificationDaoManager.getWriter();
      Map<Long,List<OutBox>> shopOutBoxMap=new HashMap<Long, List<OutBox>>();
      List<OutBox> allOutBox=notificationWriter.getAllOutBox();
      List<OutBox> outBoxListTemp=null;
      //分店铺
      for(OutBox outBox:allOutBox){
        if(outBox==null||outBox.getShopId()==null){
          continue;
        }
        outBoxListTemp=shopOutBoxMap.get(outBox.getShopId());
        if(outBoxListTemp==null){
          outBoxListTemp=new ArrayList<OutBox>();
          shopOutBoxMap.put(outBox.getShopId(),outBoxListTemp);
        }
        outBoxListTemp.add(outBox);
      }
      //generate smsDTO
      List<OutBox> shopOutBoxList=null;
      Map<String,List<OutBox>> outBoxMapTemp=null;
      SmsDTO smsDTOTemp=null;
      List<SmsDTO> smsDTOs=new ArrayList<SmsDTO>();
      for(Long shopId:shopOutBoxMap.keySet()){
        shopOutBoxList=shopOutBoxMap.get(shopId);
        if(CollectionUtil.isEmpty(shopOutBoxList)) continue;
        outBoxMapTemp=new HashMap<String, List<OutBox>>();
        for(OutBox outBox:shopOutBoxList){
          if(StringUtil.isEmpty(outBox.getSmsId())){
            smsDTOTemp=generateSmsAndContact(shopId, outBox);
            if(smsDTOTemp!=null){
              smsDTOs.add(smsDTOTemp);
            }
            continue;
          }
          outBoxListTemp=outBoxMapTemp.get(outBox.getSmsId());
          if(outBoxListTemp==null){
            outBoxListTemp=new ArrayList<OutBox>();
            outBoxMapTemp.put(outBox.getSmsId(),outBoxListTemp);
          }
          outBoxListTemp.add(outBox);
        }
        for(String smsId:outBoxMapTemp.keySet()){
          outBoxListTemp=outBoxMapTemp.get(smsId);
          if(CollectionUtil.isEmpty(outBoxListTemp)) continue;
          if(outBoxListTemp.size()>100){
            System.out.print("warning...");
          }
          smsDTOTemp=generateSmsAndContact(shopId, outBoxListTemp.toArray(new OutBox[outBoxListTemp.size()]));
          if(smsDTOTemp!=null){
            smsDTOs.add(smsDTOTemp);
          }
        }
      }
      if(CollectionUtil.isEmpty(smsDTOs)){
        LOG.error("save sms exception!");
        return;
      }
      //创建一个contactMobileMap
      IContactService contactService=ServiceManager.getService(IContactService.class);
      Map<Long,List<ContactDTO>> shopContactDTOMap=new HashMap<Long, List<ContactDTO>>();
      for(SmsDTO smsDTO: smsDTOs){
        if(smsDTO==null||smsDTO.getShopId()==null) continue;
        if(CollectionUtil.isEmpty(smsDTO.getContactDTOs())) continue;
        if(shopContactDTOMap.get(smsDTO.getShopId())==null){
          shopContactDTOMap.put(smsDTO.getShopId(),new ArrayList<ContactDTO>(smsDTO.getContactDTOs()));
        }else {
          shopContactDTOMap.get(smsDTO.getShopId()).addAll(smsDTO.getContactDTOs());
        }
      }

      List<ContactDTO> contactDTOTemps=null;
      List<ContactDTO> contactDTOTemps2=null;
      Map<String,ContactDTO> contactMobileMap=new HashMap<String, ContactDTO>();     //key:shopId+contactId value:contactDTO
      for(Long shopId:shopContactDTOMap.keySet()){
        contactDTOTemps=shopContactDTOMap.get(shopId);
        List<String> mobiles=new ArrayList<String>();
        if(CollectionUtil.isNotEmpty(contactDTOTemps)){
          for(ContactDTO contactDTO:contactDTOTemps){
            if(StringUtil.isEmpty(contactDTO.getMobile())||mobiles.contains(contactDTO.getMobile())){
              continue;
            }
            mobiles.add(contactDTO.getMobile());
          }
        }
        contactDTOTemps2=contactService.getContactByCusOrSupOrShopIdOrName(null,null,shopId,null,mobiles.toArray(new String[mobiles.size()]));
        if(CollectionUtil.isNotEmpty(contactDTOTemps2)){
          for(ContactDTO contactDTO:contactDTOTemps2){
            if(contactDTO.getShopId()==null||StringUtil.isEmpty(contactDTO.getMobile())){
              continue;
            }
            contactMobileMap.put(ObjectUtil.generateKey(contactDTO.getShopId(),contactDTO.getMobile()),contactDTO);
          }
        }
      }
      //save contacts
      UserDaoManager userDaoManager=ServiceManager.getService(UserDaoManager.class);
      UserWriter userWriter=userDaoManager.getWriter();
      Object status=userWriter.begin();
      try{
        StringBuilder sb=new StringBuilder();
        Contact contactTemp=null;
        ContactDTO contactDTOTemp=null;
        for(SmsDTO smsDTO: smsDTOs){
          if(smsDTO==null) continue;
          contactDTOTemps=smsDTO.getContactDTOs();
          if(CollectionUtil.isEmpty(contactDTOTemps)) continue;
          for(ContactDTO contactDTO:contactDTOTemps){
            contactDTOTemp=contactMobileMap.get(ObjectUtil.generateKey(contactDTO.getShopId(),contactDTO.getMobile()));
            if(contactDTOTemp!=null){
              sb.append(contactDTOTemp.getId()).append(",");
              continue;
            }
            contactTemp=new Contact();
            contactTemp.fromDTO(contactDTO);
            userWriter.save(contactTemp);
            contactMobileMap.put(ObjectUtil.generateKey(contactDTO.getShopId(),contactDTO.getMobile()),contactDTOTemp);
            sb.append(contactTemp.getId()).append(",");
          }
          smsDTO.setContactIds(sb.toString().substring(0,sb.length()-1));
          if(smsDTO.getContactIds().length()>=10000){
            smsDTO.setContactIds(smsDTO.getContactIds().substring(0,10000));
            smsDTO.setContactIds(smsDTO.getContactIds().substring(0,smsDTO.getContactIds().lastIndexOf(",")));
            System.out.print("error");
          }
          sb.delete(0, sb.length());
        }
        userWriter.commit(status);
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
        out.print("initSmsInfo failed!");
        return;
      }finally {
        userWriter.rollback(status);
      }
      //save all sms
      status=notificationWriter.begin();
      try{
        Sms smsTemp=null;
        for(SmsDTO smsDTO:smsDTOs){
          if(smsDTO==null) continue;
          smsTemp=new Sms();
          smsTemp.fromDTO(smsDTO);
          smsTemp.setSmsFlag(true);
          notificationWriter.save(smsTemp);
          smsDTO.setId(smsTemp.getId());
        }
        notificationWriter.commit(status);
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
        out.print("initSmsInfo failed!");
        return;
      }finally {
        notificationWriter.rollback(status);
      }
      //save all smsIndex
      status=notificationWriter.begin();
      try{
        List<SmsIndex> smsIndexList=new ArrayList<SmsIndex>();
        SmsIndexDTO smsIndexDTOTemp=null;
        SmsIndex smsIndexTemp=null;
        for(SmsDTO smsDTO:smsDTOs){
          if(smsDTO==null) continue;
          smsIndexDTOTemp=new SmsIndexDTO();
          smsIndexDTOTemp.fromSmsDTO(smsDTO);
          smsIndexTemp=new SmsIndex();
          smsIndexTemp.fromDTO(smsIndexDTOTemp);
          smsIndexList.add(smsIndexTemp);
        }
        for(SmsIndex smsIndex:smsIndexList){
          notificationWriter.save(smsIndex);
        }
        notificationWriter.commit(status);
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }finally {
        notificationWriter.rollback(status);
      }
      out.print("initSmsInfo success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("initSmsInfo failed!");
    } finally {
      out.flush();
      out.close();
    }
  }

  private  SmsDTO generateSmsAndContact(Long shopId,OutBox... outBoxList){
    if(ArrayUtil.isEmpty(outBoxList)) return null;
    OutBox outBoxTemp=ArrayUtil.getFirst(outBoxList);
    Long userId=outBoxTemp.getUserId();

    try{
      List<String> mobileList=new ArrayList<String>();
      List<ContactDTO> contactDTOs=new ArrayList<ContactDTO>();
      for(OutBox outBox:outBoxList){
        if(StringUtil.isEmpty(outBox.getSendMobile())) continue;
        if(outBox.getSendMobile().contains(",")){
          outBox.setSendMobile(outBox.getSendMobile().split(",")[0]);
        }
        if(mobileList.contains(outBox.getSendMobile())){
          continue;
        }
        mobileList.add(outBox.getSendMobile());
        ContactDTO contactDTO=new ContactDTO();
        contactDTO.setShopId(shopId);
        contactDTO.setMobile(outBox.getSendMobile());
        contactDTO.setMainContact(1);
        contactDTO.setLevel(0);
        contactDTO.setDisabled(1);
        contactDTOs.add(contactDTO);
      }
      SmsDTO smsDTO=new SmsDTO();
      smsDTO.setUserId(outBoxTemp.getUserId());
      smsDTO.setShopId(shopId);
      smsDTO.setUserId(userId);
      smsDTO.setSmsType(SmsType.SMS_SENT);
      smsDTO.setSmsSendScene(outBoxTemp.getSmsSendScene());
      smsDTO.setSendTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, outBoxTemp.getSendTime()));
      smsDTO.setEditDate(outBoxTemp.getLastModified());
      smsDTO.setContent(outBoxTemp.getContent());
      smsDTO.setContactDTOs(contactDTOs);
      return smsDTO;
    }catch (Exception e){
      LOG.error("save sms exception,out_box_id is {}",outBoxTemp.getId());
      LOG.error(e.getMessage(),e);
      return null;
    }
  }


  @RequestMapping(params = "method=initShopSmsRecord")
  public void initShopSmsRecord(HttpServletResponse response) {
    PrintWriter out = null;
    ISmsAccountService smsAccountService = ServiceManager.getService(ISmsAccountService.class);
    try {
      out = response.getWriter();
      smsAccountService.initShopSmsAccount();
      out.print("initShopSmsRecord success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("initShopSmsRecord failed!");
    } finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=initShopSmsConsumeRecord")
  public void initShopSmsConsumeRecord(HttpServletResponse response) {
    PrintWriter out = null;
    ISmsAccountService smsAccountService = ServiceManager.getService(ISmsAccountService.class);
    try {
      out = response.getWriter();
      smsAccountService.initShopSmsConsumeAccount();
      out.print("initShopSmsConsumeAccount success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("initShopSmsConsumeAccount failed!");
    } finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=initOperationLog")
  public void initOperationLog(HttpServletResponse response) {
    PrintWriter out = null;
    IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
    try {
      out = response.getWriter();
      int startPageNo = 1, pageSize = 5000;
      List<OperationLog> operationLogList = null;
      while(true) {
        operationLogList = operationLogService.getOperationLogByPager(startPageNo, pageSize);
        if(CollectionUtils.isEmpty(operationLogList)) {
          break;
        }
        for(OperationLog operationLog : operationLogList) {
          if(operationLog.getUserId() != null) {
            UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserId(operationLog.getUserId());
            if(userDTO != null) {
              operationLog.setUserName(userDTO.getName());
            }
          }
        }
        operationLogService.updateOperationLog(operationLogList);
        startPageNo ++;
      }
      out.print("initOperationLog success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("initOperationLog failed!");
    } finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=initOperationLogType")
  public void initOperationLogType(HttpServletResponse response) {
    PrintWriter out = null;
    IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
    try {
      out = response.getWriter();
      operationLogService.initOperationLogType();
      out.print("initOperationLogType success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("initOperationLogType failed!");
    } finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=initFaultCodePushMessage")
  public void initFaultCodePushMessage(HttpServletResponse response) {
    PrintWriter out = null;
    IVehicleFaultPushMessage vehicleFaultPushMessage = ServiceManager.getService(IVehicleFaultPushMessage.class);
    try {
      out = response.getWriter();
      vehicleFaultPushMessage.initFaultCodePushMessage();
      out.print("initFaultCodePushMessage success!");
    } catch(Exception e) {
      LOG.error(e.getMessage(), e);
      out.print("initFaultCodePushMessage failed!");
    } finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=initJuheViolateRegulationRecord")
  @ResponseBody
  public Result initJuheViolateRegulationRecord(HttpServletResponse response) {
    Result result = new Result(false);
    IVehicleFaultPushMessage vehicleFaultPushMessage = ServiceManager.getService(IVehicleFaultPushMessage.class);
    try {
      ISendVRegulationMsgToAppService sendVRegulationMsgToAppService = ServiceManager.getService(ISendVRegulationMsgToAppService.class);
      sendVRegulationMsgToAppService.sendVRegulationMsgToApp();
      result.setSuccess(true);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
    }
    return result;
  }

  @RequestMapping(params = "method=toInitObdStoragePage")
  public String toInitObdStoragePage(HttpServletResponse response) {
    return "admin/init/initObdStorage";
  }

  @RequestMapping(params = "method=initObdStorage")
  public void initObdStorage(HttpServletRequest request, HttpServletResponse response) {
    response.setContentType("text/html");
    PrintWriter printWriter = null;
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    ImportType importType = ImportType.OBD_INVENTORY;
    ImportResult importResult = new ImportResult();
    Map<String,Object> result = new HashMap<String, Object>();
    try {
      printWriter= response.getWriter();
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      MultipartFile multipartFile = multipartRequest.getFile("uploadFile");
      String fileName = multipartFile.getOriginalFilename();
      InputStream inputStream = multipartFile.getInputStream();
      byte[] fileContent = BGIOUtil.readFromStream(inputStream);
      //先读取表头
      ImportContext importContext = new ImportContext();
      importContext.setShopId(WebUtil.getShopId(request));
      importContext.setUserId(WebUtil.getUserId(request));
      importContext.setUserName(WebUtil.getUserName(request));

      importContext.setFileContent(fileContent);
      importContext.setFileName(fileName);


      ImportRecordDTO importRecordDTO = new ImportRecordDTO();
      importRecordDTO.setStatus(ImportConstants.Status.STATUS_WAITING);
      importRecordDTO.setType(ImportType.OBD_INVENTORY.name());
      importRecordDTO.setFileName(fileName);
      importRecordDTO.setFileContent(fileContent);

      List<ImportRecordDTO> importRecordDTOList = new ArrayList<ImportRecordDTO>();
      importRecordDTOList.add(importRecordDTO);
      importContext.setImportRecordDTOList(importRecordDTOList);
      importContext.setType(ImportType.OBD_INVENTORY.name());
      importContext.setFieldMapping(ObdImportConstants.filedMap);
      importResult = obdManagerService.initImportOBDInventoryFromExcel(importContext);

      result.put("success",importResult.isSuccess());
      result.put("importResult",importResult);
      printWriter.write(JsonUtil.mapToJson(result));
    } catch (Exception e) {
      LOG.error("/obdManage.do?method=uploadObdInventory");
      LOG.error(e.getMessage(), e);
      importResult.setMessage("网络异常");
      importResult.setSuccess(false);
      result.put("success",importResult.isSuccess());
      result.put("importResult",importResult);
      if(printWriter != null){
        printWriter.write(JsonUtil.mapToJson(result));
      }
    } finally {
      if(printWriter!=null){
        printWriter.flush();
        printWriter.close();
      }
    }
  }

  @RequestMapping(params = "method=processLackAutoPreBuy")
  public void processLackAutoPreBuy(HttpServletRequest request, HttpServletResponse response) {
    PrintWriter out=null;
    try {
//      LOG.info("processLackAutoPreBuy begin");
//      IPreBuyOrderService preBuyOrderService=ServiceManager.getService(IPreBuyOrderService.class);
//      preBuyOrderService.processLackAutoPreBuy();
      out = response.getWriter();
//      PreBuyOrderInformationRecommendSchedule.processPreBuyOrderInformationRecommendJobs();
      out.print("processLackAutoPreBuy success!");
    } catch (Exception e) {
      LOG.error("init.do?method=processLackAutoPreBuy");
      LOG.error(e.getMessage(),e);
    }finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=synWXUser")
  public void synWXUser(HttpServletRequest request, HttpServletResponse response,String publicNo) {
    LOG.info("wx:synWXUser begin");
    PrintWriter out=null;
    try {
      IWXUserService wxUserService= ServiceManager.getService(IWXUserService.class);
      if(StringUtil.isEmpty(publicNo)){
        wxUserService.synALLWXUserDTOs();
      }else {
        wxUserService.synUserDTOList(publicNo);
      }
      out = response.getWriter();
      out.print("synWXUser success!");
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }finally {
      out.flush();
      out.close();
    }
  }


  @RequestMapping(params = "method=initWXShopAccount")
  public void initWXShopAccount(HttpServletRequest request, HttpServletResponse response) {
    LOG.info("wx:initWXShopAccount begin");
    PrintWriter out=null;
    try {
      IWXAccountService accountService=ServiceManager.getService(WXAccountService.class);
      List<ShopDTO> shopDTOs=ServiceManager.getService(ConfigService.class).getActiveShop();
      for(ShopDTO shopDTO:shopDTOs){
        if(shopDTO.getId()<1000L) continue;
        accountService.createDefaultWXShopAccount(shopDTO.getId());
      }
      out = response.getWriter();
      out.print("initWXShopAccount success, all finished!");
    } catch (Exception e) {
      out.print("initWXShopAccount failed!");
      LOG.error(e.getMessage(), e);
    }finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=initObdGsmDataTrace")
  public void initObdGsmDataTrace(HttpServletRequest request,HttpServletResponse response){
    LOG.info("initObdGsmDataTrace begin");
    PrintWriter out=null;
    try {
      new Thread(new Runnable() {
        @Override
        public void run() {
          if(!ObdGsmPointTraceSchedule.isLock()){
            try{
              IGsmDataTraceService gsmDataTraceService = ServiceManager.getService(IGsmDataTraceService.class);
              gsmDataTraceService.traceObdGsmPointData();
            }catch (Exception e){
              LOG.error("initObdGsmPointDataTrace error:", e);
            }finally {
              ObdGsmPointTraceSchedule.unLock();
            }
          }

        }
      }).start();

      Thread.sleep(10000);
      new Thread(new Runnable() {
        @Override
        public void run() {
          if(!ObdGsmVehicleInfoTraceSchedule.isLock()){
            try{
              IGsmDataTraceService gsmDataTraceService = ServiceManager.getService(IGsmDataTraceService.class);
              gsmDataTraceService.traceObdGsmVehicleData();
            }catch (Exception e){
              LOG.error("initObdGsmVehicleInfoDataTrace error:", e);
            }finally {
              ObdGsmVehicleInfoTraceSchedule.unLock();
            }
          }

        }
      }).start();


      out = response.getWriter();
      out.print("initObdGsmDataTrace success, view the background log!");
    } catch (Exception e) {
      out.print("initObdGsmDataTrace failed!");
      LOG.error(e.getMessage(), e);
    }finally {
      out.flush();
      out.close();
    }
  }
}
