package com.bcgogo.config;

import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ExportRecordDTO;
import com.bcgogo.config.service.excelexport.BcgogoExcelDataExporter;
import com.bcgogo.config.service.excelexport.BcgogoExportDataIterator;
import com.bcgogo.config.service.excelexport.ExportResult;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.service.vehicle.ISearchVehicleService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.assistantStat.AssistantAchievementStatDTO;
import com.bcgogo.txn.dto.assistantStat.AssistantStatSearchDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoSearchConditionDTO;
import com.bcgogo.txn.service.RemindEventStrategy;
import com.bcgogo.txn.service.RemindEventStrategySelector;
import com.bcgogo.txn.service.exportExcel.*;
import com.bcgogo.txn.service.pushMessage.faultCode.IShopFaultInfoService;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-7-26
 * Time: 上午10:54
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/export.do")
public class ExportExcelController {
    private static final Logger LOG = LoggerFactory.getLogger(ExportExcelController.class);
    /**
     * 库存页面的导出
     */
    @RequestMapping(params = "method=exportInventory")
    @ResponseBody
    public Object exportInventory(HttpServletRequest request, HttpServletResponse response, SearchConditionDTO searchConditionDTO) {
        ExportResult exportResult = null;
        Long shopId = (Long) request.getSession().getAttribute("shopId");
        searchConditionDTO.setShopId(shopId);
        ExportRecordDTO exportRecordDTO = new ExportRecordDTO();
        exportRecordDTO.setShopId(shopId);
        exportRecordDTO.setUserNo(WebUtil.getUserNo(request));
        exportRecordDTO.setScene(ExportExcelScene.INVENTORY.toString());
        BcgogoExportDataIterator bcgogoExportDataIterator = null;   //迭代器，负责数据的读取与装配
        try {
            bcgogoExportDataIterator = new InventoryExportDataIterator(searchConditionDTO);
            exportResult = ServiceManager.getService(BcgogoExcelDataExporter.class).export(bcgogoExportDataIterator,
                ImportConstants.ExcelVersion.EXCEL_VERSION_2003, exportRecordDTO);
        }  catch (Exception e) {
             LOG.error(e.getMessage(),e);
        }
        if(exportResult == null) {
            LOG.error("保存库存导出文件失败");
            return null;
        }else {
            LOG.info("保存库存导出文件成功");
            return exportResult;
        }

    }

    @RequestMapping(params = "method=exportCustomer")
    @ResponseBody
    public Object exportCustomer(HttpServletRequest request, HttpServletResponse response, CustomerSupplierSearchConditionDTO searchConditionDTO, JoinSearchConditionDTO joinSearchConditionDTO) {
       ExportResult exportResult = new ExportResult();
        Long shopId = (Long) request.getSession().getAttribute("shopId");
        searchConditionDTO.setShopId(shopId);
        ExportRecordDTO exportRecordDTO = new ExportRecordDTO();
        exportRecordDTO.setShopId(shopId);
        exportRecordDTO.setUserNo(WebUtil.getUserNo(request));
        exportRecordDTO.setScene(ExportExcelScene.CUSTOMER.toString());
       //迭代器，负责数据的读取与装配
       BcgogoExportDataIterator bcgogoExportDataIterator = null;
        try {
            bcgogoExportDataIterator = new CustomerExportDataIterator(searchConditionDTO,joinSearchConditionDTO);
            exportResult = ServiceManager.getService(BcgogoExcelDataExporter.class).export(bcgogoExportDataIterator, ImportConstants.ExcelVersion.EXCEL_VERSION_2003.toString(), exportRecordDTO);
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }
        if(exportResult == null) {
            LOG.error("生成客户导出文件失败");
            return null;
        }else {
            LOG.info("生成客户导出文件成功");
            return exportResult;
        }
    }

    @RequestMapping(params = "method=exportOrder")
    @ResponseBody
    public Object exportOrder(HttpServletRequest request, HttpServletResponse response, OrderSearchConditionDTO orderSearchConditionDTO) {
        ExportResult exportResult = new ExportResult();
        Long shopId = (Long) request.getSession().getAttribute("shopId");
        orderSearchConditionDTO.setShopId(shopId);
        orderSearchConditionDTO.setWholesaler(ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
        ExportRecordDTO exportRecordDTO = new ExportRecordDTO();
        exportRecordDTO.setShopId(shopId);
        exportRecordDTO.setUserNo(WebUtil.getUserNo(request));
        exportRecordDTO.setScene(ExportExcelScene.ORDER.toString());
        //迭代器，负责数据的读取与装配
        BcgogoExportDataIterator bcgogoExportDataIterator = null;
        try {
            bcgogoExportDataIterator = new OrderExportDataIterator(orderSearchConditionDTO);
            exportResult = ServiceManager.getService(BcgogoExcelDataExporter.class).export(bcgogoExportDataIterator, ImportConstants.ExcelVersion.EXCEL_VERSION_2003.toString(), exportRecordDTO);
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }
        if(exportResult == null) {
            LOG.error("生成单据导出文件失败");
            return null;
        }else {
            LOG.info("生成单据导出文件成功");
            return exportResult;
        }
    }

    @RequestMapping(params = "method=exportCustomerTransaction")
    @ResponseBody
    public Object exportCustomerTransaction(HttpServletRequest request, HttpServletResponse response, OrderSearchConditionDTO orderSearchConditionDTO) {
        ExportResult exportResult = new ExportResult();
        Long shopId = (Long) request.getSession().getAttribute("shopId");
        orderSearchConditionDTO.setShopId(shopId);
        ExportRecordDTO exportRecordDTO = new ExportRecordDTO();
        exportRecordDTO.setShopId(shopId);
        exportRecordDTO.setUserNo(WebUtil.getUserNo(request));
        exportRecordDTO.setScene(ExportExcelScene.CUSTOMER_TRANSACTION.toString());
        //迭代器，负责数据的读取与装配
        BcgogoExportDataIterator bcgogoExportDataIterator = null;
        try {
            bcgogoExportDataIterator = new CustomerTransactionExportDataIterator(orderSearchConditionDTO);
            exportResult = ServiceManager.getService(BcgogoExcelDataExporter.class).export(bcgogoExportDataIterator, ImportConstants.ExcelVersion.EXCEL_VERSION_2003.toString(), exportRecordDTO);
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }
        if(exportResult == null) {
            LOG.error("生成客户交易导出文件失败");
            return null;
        }else {
            LOG.info("生成客户交易导出文件成功");
            return exportResult;
        }
    }


    @RequestMapping(params = "method=exportBusinessStaffTransaction")
    @ResponseBody
    public Object exportBusinessStaffTransaction(HttpServletRequest request, AssistantStatSearchDTO assistantStatSearchDTO) {
        ExportResult exportResult = new ExportResult();
        Long shopId = WebUtil.getShopId(request);
        if (shopId == null) {
            return null;
        }
        assistantStatSearchDTO = assistantStatSearchDTO == null ? new AssistantStatSearchDTO() : assistantStatSearchDTO;
        assistantStatSearchDTO.setShopId(shopId);
        IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);

        if (StringUtil.isEmpty(assistantStatSearchDTO.getAchievementStatTypeStr())) {
            return assistantStatSearchDTO.getAssistantAchievementStatDTOList();
        }
        assistantStatSearchDTO.setTime();
        List<Long> resultList = assistantStatService.countAssistantStatByCondition(assistantStatSearchDTO);
        int totalNum = CollectionUtils.isEmpty(resultList) ? 0 : resultList.size();
        if (totalNum <= 0) {
            return assistantStatSearchDTO.getAssistantAchievementStatDTOList();
        }
        assistantStatSearchDTO.setMaxRows(totalNum);
        try{
        Pager pager = new Pager(totalNum, assistantStatSearchDTO.getStartPageNo(), assistantStatSearchDTO.getMaxRows());

        Set<Long> ids = new HashSet<Long>();

        for (int index = 0; index < assistantStatSearchDTO.getMaxRows(); index++) {
            if (index + pager.getRowStart() < resultList.size()) {
                ids.add(resultList.get(index + pager.getRowStart()));
            }
        }
        if (CollectionUtils.isEmpty(ids)) {
            return assistantStatSearchDTO.getAssistantAchievementStatDTOList();
        }
        List<AssistantAchievementStatDTO> assistantAchievementStatDTOList = assistantStatService.getAssistantStatByIds(assistantStatSearchDTO, ids);
        assistantStatSearchDTO.setAssistantAchievementStatDTOList(assistantAchievementStatDTOList);
        }catch (Exception e){
            e.printStackTrace();
        }
        ExportRecordDTO exportRecordDTO = new ExportRecordDTO();
        exportRecordDTO.setShopId(shopId);
        exportRecordDTO.setUserNo(WebUtil.getUserNo(request));
        exportRecordDTO.setScene(ExportExcelScene.BUSINESS_STAFF_STAT.toString());
        //迭代器，负责数据的读取与装配
        BcgogoExportDataIterator bcgogoExportDataIterator = null;
        try {
            bcgogoExportDataIterator = new BusinessStaffDataIterator(assistantStatSearchDTO);
            exportResult = ServiceManager.getService(BcgogoExcelDataExporter.class).export(bcgogoExportDataIterator, ImportConstants.ExcelVersion.EXCEL_VERSION_2003.toString(), exportRecordDTO);
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }
        if(exportResult == null) {
            LOG.error("生成员工业绩导出文件失败");
            return null;
        }else {
            LOG.info("生成员工业绩导出文件成功");
            return exportResult;
        }
    }


  @RequestMapping(params = "method=exportBusinessStat")
  @ResponseBody
  public Object exportBusinessStat(HttpServletRequest request, HttpServletResponse response, String arrayType, String type, String pageType, String dateStr, int totalNum) {
    ExportResult exportResult = new ExportResult();
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    ExportRecordDTO exportRecordDTO = new ExportRecordDTO();
    exportRecordDTO.setShopId(shopId);
    exportRecordDTO.setUserNo(WebUtil.getUserNo(request));
    if("repair".equals(pageType)) {
      exportRecordDTO.setScene(ExportExcelScene.REPAIR_BUSINESS_STAT.toString());
    } else if("sale".equals(pageType)) {
      exportRecordDTO.setScene(ExportExcelScene.SALES_BUSINESS_STAT.toString());
    } else if("wash".equals(pageType)) {
      exportRecordDTO.setScene(ExportExcelScene.WASH_BUSINESS_STAT.toString());
    }
    //迭代器，负责数据的读取与装配
    BcgogoExportDataIterator bcgogoExportDataIterator = null;
    try {
      bcgogoExportDataIterator = new BusinessStatExportDataIterator(arrayType, type, pageType, dateStr, shopId, totalNum);
      exportResult = ServiceManager.getService(BcgogoExcelDataExporter.class).export(bcgogoExportDataIterator, ImportConstants.ExcelVersion.EXCEL_VERSION_2003.toString(), exportRecordDTO);
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    if(exportResult == null) {
      LOG.error("生成营业额导出文件失败");
      return null;
    }else {
      LOG.info("生成营业额导出文件成功");
      return exportResult;
    }
  }

  @RequestMapping(params = "method=exportAssistantStatDetail")
  @ResponseBody
  public Object exportAssistantStatDetail(HttpServletRequest request, HttpServletResponse response, AssistantStatSearchDTO assistantStatSearchDTO) {
    ExportResult exportResult = new ExportResult();
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    assistantStatSearchDTO.setShopId(shopId);
    ExportRecordDTO exportRecordDTO = new ExportRecordDTO();
    exportRecordDTO.setShopId(shopId);
    exportRecordDTO.setUserNo(WebUtil.getUserNo(request));
    if(assistantStatSearchDTO != null && StringUtil.isNotEmpty(assistantStatSearchDTO.getPageType())) {
      if("assistantServiceRecord".equals(assistantStatSearchDTO.getPageType())) {
        exportRecordDTO.setScene(ExportExcelScene.REPAIR_ASSISTANT_STAT.toString());
      } else if("assistantWashRecord".equals(assistantStatSearchDTO.getPageType())) {
        exportRecordDTO.setScene(ExportExcelScene.WASH_ASSISTANT_STAT.toString());
      } else if("assistantProductRecord".equals(assistantStatSearchDTO.getPageType())) {
        exportRecordDTO.setScene(ExportExcelScene.SALES_ASSISTANT_STAT.toString());
      } else if("assistantMemberRecord".equals(assistantStatSearchDTO.getPageType())) {
        exportRecordDTO.setScene(ExportExcelScene.MEMBER_ASSISTANT_STAT.toString());
      } else if("assistantBusinessAccountRecord".equals(assistantStatSearchDTO.getPageType())) {
        exportRecordDTO.setScene(ExportExcelScene.BUSINESS_ACCOUNT_ASSISTANT_STAT.toString());
      }
    }

    //迭代器，负责数据的读取与装配
    BcgogoExportDataIterator bcgogoExportDataIterator = null;
    try {
      bcgogoExportDataIterator = new AssistantStatDetailExportIterator(assistantStatSearchDTO);
      exportResult = ServiceManager.getService(BcgogoExcelDataExporter.class).export(bcgogoExportDataIterator, ImportConstants.ExcelVersion.EXCEL_VERSION_2003.toString(), exportRecordDTO);
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    if(exportResult == null) {
      LOG.error("生成员工业绩明细导出文件失败");
      return null;
    }else {
      LOG.info("生成员工业绩明细导出文件成功");
      return exportResult;
    }
  }


  /**
   * 客户服务提醒导出
   */

  @RequestMapping(params = "method=exportCustomerRemind")
  @ResponseBody
  public Object exportCustomerRemind(HttpServletRequest request, HttpServletResponse response) {
    ExportResult exportResult = null;
    Long shopId = WebUtil.getShopId(request);
    //是否过期
    String isOverdueStr = request.getParameter("isOverdue");
    //是否已提醒
    String hasRemindStr = request.getParameter("hasRemind");
    Boolean isOverdue = null;
    Boolean hasRemind = null;
    if(StringUtil.isNotEmpty(isOverdueStr)){
      isOverdue = Boolean.parseBoolean(isOverdueStr);
    }
    if(StringUtil.isNotEmpty(hasRemindStr)){
      hasRemind = Boolean.parseBoolean(hasRemindStr);
    }
    try {
    RemindEventStrategySelector remindEventStrategySelector = ServiceManager.getService(RemindEventStrategySelector.class);
    RemindEventStrategy customerRemindEventStrategy = remindEventStrategySelector.selectStrategy(RemindEventType.CUSTOMER_SERVICE);
    int totalCount = customerRemindEventStrategy.countRemindEvent(shopId, isOverdue, hasRemind, getFlashTime());
    BcgogoExportDataIterator bcgogoExportDataIterator = new CustomerRemindExportDataIterator(shopId, isOverdue,hasRemind,totalCount);   //迭代器，负责数据的读取与装配
    ExportRecordDTO exportRecordDTO = new ExportRecordDTO();
    exportRecordDTO.setShopId(shopId);
    exportRecordDTO.setUserNo(WebUtil.getUserNo(request));
    exportRecordDTO.setScene(ExportExcelScene.CUSTOMER_REMIND.toString());

      exportResult = ServiceManager.getService(BcgogoExcelDataExporter.class).export(bcgogoExportDataIterator,
          ImportConstants.ExcelVersion.EXCEL_VERSION_2003, exportRecordDTO);
    }  catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    if(exportResult == null) {
      LOG.error("保存库存导出文件失败");
      return null;
    }else {
      LOG.info("保存库存导出文件成功");
      return exportResult;
    }

  }

  //闪动提醒判断的开始时间：昨天的23:59:59-999
  private Long getFlashTime() throws Exception {
    return DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date()) - 1;
  }

  /**
   * 客户服务提醒导出
   */

  @RequestMapping(params = "method=exportShopFaultInfo")
  @ResponseBody
  public Object exportShopFaultInfo(HttpServletRequest request, HttpServletResponse response,FaultInfoSearchConditionDTO searchCondition) {
    IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
    ExportResult exportResult = null;
    Long shopId = WebUtil.getShopId(request);

    try {
      searchCondition.setShopId(shopId);
      if (RegexUtils.isMobile(searchCondition.getMobile()) && StringUtil.isEmpty(searchCondition.getVehicleNo())) {
        searchCondition.setMobiles(ServiceManager.getService(ICustomerService.class).getAppUserMobileByContactMobile(
            searchCondition.getShopId(), searchCondition.getMobile()));
      }
      int totalRows = shopFaultInfoService.countShopFaultInfoList(searchCondition);
      BcgogoExportDataIterator bcgogoExportDataIterator = new ShopFaultInfoExportDataIterator(searchCondition,totalRows);   //迭代器，负责数据的读取与装配
      ExportRecordDTO exportRecordDTO = new ExportRecordDTO();
      exportRecordDTO.setShopId(shopId);
      exportRecordDTO.setUserNo(WebUtil.getUserNo(request));
      exportRecordDTO.setScene(ExportExcelScene.SHOP_FAULT_INFO.toString());

      exportResult = ServiceManager.getService(BcgogoExcelDataExporter.class).export(bcgogoExportDataIterator,
          ImportConstants.ExcelVersion.EXCEL_VERSION_2003, exportRecordDTO);
    }  catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    if(exportResult == null) {
      LOG.error("保存库存导出文件失败");
      return null;
    }else {
      LOG.info("保存库存导出文件成功");
      return exportResult;
    }

  }

  /**
   * 车辆管理导出
   */

  @RequestMapping(params = "method=exportVehicleList")
  @ResponseBody
  public Object exportVehicleList(HttpServletRequest request, HttpServletResponse response,VehicleSearchConditionDTO vehicleSearchConditionDTO) {
    IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
    ExportResult exportResult = null;
    Long shopId = WebUtil.getShopId(request);

    try {
      vehicleSearchConditionDTO.setShopId(shopId);
      vehicleSearchConditionDTO.setSearchStrategies(new VehicleSearchConditionDTO.SearchStrategy[]{VehicleSearchConditionDTO.SearchStrategy.SEARCH_STRATEGY_STATS});
      vehicleSearchConditionDTO.setStatsFields(new String[]{VehicleSearchConditionDTO.StatsFields.OBD_ID.getName(),VehicleSearchConditionDTO.StatsFields.IS_MOBILE_VEHICLE.getName(),VehicleSearchConditionDTO.StatsFields.VEHICLE_TOTAL_CONSUME_AMOUNT.getName()});
      if(StringUtils.isNotBlank(vehicleSearchConditionDTO.getCustomerInfo())){
        JoinSearchConditionDTO joinSearchConditionDTO = new JoinSearchConditionDTO();
        joinSearchConditionDTO.setShopId(shopId);
        joinSearchConditionDTO.setFromColumn("id");
        joinSearchConditionDTO.setToColumn("customer_id");
        joinSearchConditionDTO.setFromIndex(SolrClientHelper.BcgogoSolrCore.CUSTOMER_SUPPLIER_CORE.getValue());
        joinSearchConditionDTO.setCustomerOrSupplier(new String[]{"customer"});
        joinSearchConditionDTO.setCustomerOrSupplierInfo(vehicleSearchConditionDTO.getCustomerInfo());
        vehicleSearchConditionDTO.setJoinSearchConditionDTO(joinSearchConditionDTO);
      }
      vehicleSearchConditionDTO.setMaxRows(1);
      VehicleSearchResultDTO vehicleSearchResultDTO = ServiceManager.getService(ISearchVehicleService.class).queryVehicle(vehicleSearchConditionDTO);

      BcgogoExportDataIterator bcgogoExportDataIterator = new VehicleListExportDataIterator(NumberUtil.intValue(vehicleSearchResultDTO.getNumFound()) ,vehicleSearchConditionDTO);   //迭代器，负责数据的读取与装配
      ExportRecordDTO exportRecordDTO = new ExportRecordDTO();
      exportRecordDTO.setShopId(shopId);
      exportRecordDTO.setUserNo(WebUtil.getUserNo(request));
      exportRecordDTO.setScene(ExportExcelScene.VEHICLE_LIST.toString());

      exportResult = ServiceManager.getService(BcgogoExcelDataExporter.class).export(bcgogoExportDataIterator,
          ImportConstants.ExcelVersion.EXCEL_VERSION_2003, exportRecordDTO);
    }  catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    if(exportResult == null) {
      LOG.error("保存库存导出文件失败");
      return null;
    }else {
      LOG.info("保存库存导出文件成功");
      return exportResult;
    }

  }


}
