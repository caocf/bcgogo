package com.bcgogo.stat;

import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.PrintHelper;
import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.InOutFlag;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.DepositStatConditionDTO;
import com.bcgogo.stat.dto.DepositStatQueryResult;
import com.bcgogo.txn.dto.DepositOrderDTO;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.service.IDepositOrderStatService;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IContactService;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-21
 * Time: 下午5:35
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/depositOrdersStat.do")
public class DepositOrderStatController {
  // 是预收款还是预付款
  private static final String TYPE = "type";
  private static final String CUSTOMER_STAT = "customer"; //客户统计
  private static final String SUPPLIER_STAT = "supplier"; //供应商统计
  private static final int PAGE_SIZE = 10;
  private static final String IN_OUT_FLAG = "inOutFlag";
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @RequestMapping(params = "method=renderCustomerDepositOrderQueryPage", method = RequestMethod.GET)
  public String renderCustomerDepositOrderQueryPage(HttpServletRequest request, ModelMap modelMap) {
    modelMap.addAttribute("statType", CUSTOMER_STAT);
    return "/stat/customerDepositStat";
  }

  @RequestMapping(params = "method=renderSupplierDepositOrderQueryPage", method = RequestMethod.GET)
  public String renderSupplierDepositOrderQueryPage(HttpServletRequest request, ModelMap modelMap) {
    modelMap.addAttribute("statType", SUPPLIER_STAT);
    return "/stat/supplierDepositStat";
  }


  /**
   * 查询预收款、预付款
   *
   * @param request
   * @param modelMap
   * @param startPageNo
   * @return
   */
  @RequestMapping(params = "method=ajaxQueryDepositOrders")
  @ResponseBody
  public PagingListResult ajaxQueryDepositOrders(HttpServletRequest request, ModelMap modelMap, DepositStatConditionDTO depositStatConditionDTO, Integer startPageNo, Integer maxRows) {
    PagingListResult<DepositStatQueryResult> resultList = new PagingListResult<DepositStatQueryResult>();
    resultList.setSuccess(true);
    Long shopId = WebUtil.getShopId(request);
    try {
      depositStatConditionDTO.setShopId(shopId);
      int start = startPageNo == null ? 1 : startPageNo;
      int pageSize = (maxRows == null) ? PAGE_SIZE : maxRows;
      int totalCount = 0;
      Pager pager = new Pager(totalCount, start, pageSize);
      resultList.setPager(pager);

      // 名称存在 但是id为空 标示名称不匹配 不查询
      if ((StringUtils.isNotBlank(depositStatConditionDTO.getCustomerName()) || StringUtils.isNotBlank(depositStatConditionDTO.getSupplierName())) && (depositStatConditionDTO.getCustomerId() == null || depositStatConditionDTO.getCustomerId() == 0) && (depositStatConditionDTO.getSupplierId() == null || depositStatConditionDTO.getSupplierId() == 0)) {
        return resultList;
      }

      // 如果手机存在 查询联系人
      if (mobileContactQuery(depositStatConditionDTO, shopId)) return resultList;

      IDepositOrderStatService depositOrderStatService = ServiceManager.getService(IDepositOrderStatService.class);
      totalCount = depositOrderStatService.countDepositOrdersByStatCondition(depositStatConditionDTO);
      List<DepositOrderDTO> depositOrderDTOs = depositOrderStatService.queryDepositOrdersByStatCondition(depositStatConditionDTO, pager);
      pager = new Pager(totalCount, start, pageSize);
      resultList.setPager(pager);
      if (!CollectionUtils.isEmpty(depositOrderDTOs)) {
        resultList.setResults(buildQueryResultList(depositOrderDTOs, depositStatConditionDTO.getType(), shopId));
        resultList.setData(buildStatFields(depositOrderDTOs));
        resultList.setSuccess(true);
        return resultList;
      }
      return resultList;
    } catch (Exception e) {
      logger.error("/depositOrdersStat.do?method=ajaxQueryDepositOrders", e);
      return resultList;
    }
  }

  /**
   * 通过手机查询联系人列表，获取customer和supplier id列表
   * @param depositStatConditionDTO
   * @param shopId
   * @return true 直接返回 页面输入的id不在mobile对应的id列表中 false 继续做查询
   */
  private boolean mobileContactQuery(DepositStatConditionDTO depositStatConditionDTO, Long shopId) {
    if (StringUtils.isNotBlank(depositStatConditionDTO.getCustomerMobile()) || StringUtils.isNotBlank(depositStatConditionDTO.getSupplierMobile())) { // 这个判断冗余，标识这边是要处理手机相关
      IContactService contactService = ServiceManager.getService(IContactService.class);
      List<ContactDTO> contactDTOs;
      List<Long> ids = new ArrayList<Long>();
      if (StringUtils.isNotBlank(depositStatConditionDTO.getCustomerMobile())) {
        contactDTOs = contactService.getContactByCusOrSupOrShopIdOrName(null, null, shopId, null, depositStatConditionDTO.getCustomerMobile());
        if (!CollectionUtils.isEmpty(contactDTOs)) {
          filterCustomerContactList(contactDTOs);
          if (!CollectionUtils.isEmpty(contactDTOs)) {
            for (ContactDTO contactDTO : contactDTOs) {
              if (StringUtils.isNotBlank(depositStatConditionDTO.getCustomerMobile())) {
                ids.add(contactDTO.getCustomerId());
              }
            }
            depositStatConditionDTO.setCustomerIds(ids);
          }
        }
        if (CollectionUtils.isEmpty(ids)){
          return true;
        }
        if (depositStatConditionDTO.getCustomerId() != null && !ids.contains(depositStatConditionDTO.getCustomerId())) {
          return true;
        }
      } else {
        contactDTOs = contactService.getContactByCusOrSupOrShopIdOrName(null, null, shopId, null, depositStatConditionDTO.getSupplierMobile());
        if (!CollectionUtils.isEmpty(contactDTOs)) {
          filterSupplierContactList(contactDTOs);
          if (!CollectionUtils.isEmpty(contactDTOs)) {
            for (ContactDTO contactDTO : contactDTOs) {
              if (StringUtils.isNotBlank(depositStatConditionDTO.getSupplierMobile())) {
                ids.add(contactDTO.getSupplierId());
              }
            }
            depositStatConditionDTO.setSupplierIds(ids);
          }
        }
        if (CollectionUtils.isEmpty(ids)){
          return true;
        }
        if (depositStatConditionDTO.getSupplierId() != null && !ids.contains(depositStatConditionDTO.getSupplierId())) {
          return true;
        }
      }
    }
    return false;
  }

  private void filterCustomerContactList(List<ContactDTO> contactDTOs) {
    if (CollectionUtils.isEmpty(contactDTOs)) {
      return;
    }
    Iterator<ContactDTO> contactIterator = contactDTOs.iterator();
    while (contactIterator.hasNext()) {
      ContactDTO contact = contactIterator.next();
      if (contact.getCustomerId() == null || contact.getCustomerId() == 0L) {
        contactIterator.remove();
      }
    }
  }

  private void filterSupplierContactList(List<ContactDTO> contactDTOs) {
    if (CollectionUtils.isEmpty(contactDTOs)) {
      return;
    }
    Iterator<ContactDTO> contactIterator = contactDTOs.iterator();
    while (contactIterator.hasNext()) {
      ContactDTO contact = contactIterator.next();
      if (contact.getSupplierId() == null || contact.getSupplierId() == 0L) {
        contactIterator.remove();
      }
    }
  }

  private List<DepositStatQueryResult> buildQueryResultList(List<DepositOrderDTO> depositOrderDTOs, String type, Long shopId) throws BcgogoException {
    List<DepositStatQueryResult> depositStatQueryResults = new ArrayList<DepositStatQueryResult>();

    if (StringUtils.equals(type, CUSTOMER_STAT)) {
      List<Long> customerIds = new ArrayList<Long>();
      for (DepositOrderDTO depositOrderDTO : depositOrderDTOs) {
        customerIds.add(depositOrderDTO.getCustomerId());
      }
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      List<CustomerDTO> customerDTOs = customerService.getCustomerByIds(customerIds);
      for (DepositOrderDTO depositOrderDTO : depositOrderDTOs) {
        DepositStatQueryResult result = new DepositStatQueryResult();
        org.springframework.beans.BeanUtils.copyProperties(depositOrderDTO, result);
        for (CustomerDTO customerDTO : customerDTOs) {
          if (customerDTO.getId().equals(depositOrderDTO.getCustomerId())) {
            result.setName(customerDTO.getName());
            if (StringUtils.isNotBlank(customerDTO.getMobile())) {
              result.setMobile(customerDTO.getMobile());
            } else {
              result.setMobile("");
            }
          }
        }
        depositStatQueryResults.add(result);
      }
    } else {
      List<Long> supplierIds = new ArrayList<Long>();
      for (DepositOrderDTO depositOrderDTO : depositOrderDTOs) {
        supplierIds.add(depositOrderDTO.getSupplierId());
      }
      Set<Long> ids = new HashSet<Long>();
      ids.addAll(supplierIds);
      ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
      Map<Long, SupplierDTO> supplierDTOMap = supplierService.getSupplierByIdSet(shopId, ids);
      for (DepositOrderDTO depositOrderDTO : depositOrderDTOs) {
        DepositStatQueryResult result = new DepositStatQueryResult();
        org.springframework.beans.BeanUtils.copyProperties(depositOrderDTO, result);
        for (SupplierDTO supplierDTO : supplierDTOMap.values()) {
          if (supplierDTO.getId().equals(depositOrderDTO.getSupplierId())) {
            result.setName(supplierDTO.getName());
            if (StringUtils.isNotBlank(supplierDTO.getMobile())) {
              result.setMobile(supplierDTO.getMobile());
            } else {
              result.setMobile("");
            }
          }
        }
        depositStatQueryResults.add(result);
      }
    }

    return depositStatQueryResults;
  }

  public List<Double> buildStatFields(List<DepositOrderDTO> depositOrderDTOs) {
    List<Double> inOutStat = new ArrayList<Double>();
    Double intStat = 0.00;
    Double outStat = 0.00;
    for (DepositOrderDTO depositOrderDTO : depositOrderDTOs) {
      if (depositOrderDTO.getInOut() == InOutFlag.IN_FLAG.getCode()) {
        intStat += depositOrderDTO.getActuallyPaid();
      } else {
        outStat += depositOrderDTO.getActuallyPaid();
      }
    }
    inOutStat.add(NumberUtil.toReserve(intStat, NumberUtil.MONEY_PRECISION));
    inOutStat.add(NumberUtil.toReserve(outStat, NumberUtil.MONEY_PRECISION));
    return inOutStat;
  }

  @RequestMapping(params = "method=printDepositStat")
  public void printDepositStat(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
                               DepositStatConditionDTO depositStatConditionDTO, Integer currentPage, Integer maxRows) {
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    PagingListResult<DepositStatQueryResult> resultList = new PagingListResult<DepositStatQueryResult>();
    resultList = ajaxQueryDepositOrders(request, modelMap, depositStatConditionDTO, currentPage, maxRows);
    if(resultList != null && CollectionUtils.isNotEmpty(resultList.getResults())){
      for (DepositStatQueryResult result : resultList.getResults()) {
        String depositTypeStr = "";
        if (StringUtils.isNotBlank(result.getDepositType())) {
          String[] types = result.getDepositType().split("\\|");
          if (types.length > 0) {
            depositTypeStr = types[1];
          }
        }
        result.setDepositTypeStr(depositTypeStr);
      }
    }
    Long shopId = WebUtil.getShopId(request);
    try {
      ShopDTO shopDTO = configService.getShopById(shopId);
      if (shopDTO != null && StringUtil.isEmpty(shopDTO.getMobile())) {
        shopDTO.setMobile(shopDTO.getLandline());
      }
      String statType = request.getParameter("type");
      OrderTypes statTypeEnum;
      if (statType.equals(SUPPLIER_STAT)) {
        statTypeEnum = OrderTypes.PRE_PAY_STATISTICS;
      } else {
        statTypeEnum = OrderTypes.PRE_RECEIVE_STATISTICS;
      }
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), statTypeEnum);
      String myTemplateName = "preReceiveStat" + String.valueOf(WebUtil.getShopId(request));
      VelocityContext context = new VelocityContext();
      List<DepositStatQueryResult> results = resultList.getResults();
      context.put("startTime", depositStatConditionDTO.getStartTimeStr());
      context.put("endTime", depositStatConditionDTO.getEndTimeStr());
      double inTotal = 0d;
      if(resultList.getData()!=null){
        inTotal = ((List<Double>) resultList.getData()).get(0);
      }
      double outTotal = 0d;
      if(resultList.getData()!=null){
        outTotal = ((List<Double>) resultList.getData()).get(1);
      }
      context.put("inTotal", inTotal);
      context.put("outTotal", outTotal);
      context.put("resultList", results);
      context.put("shopName", shopDTO.getName());
      context.put("shopDTO", shopDTO);
      PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
    } catch (Exception e) {
      logger.error("/depositOrdersStat.do?method=printDepositStat", e);
    }
  }


}
