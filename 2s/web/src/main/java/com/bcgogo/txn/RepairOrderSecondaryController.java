package com.bcgogo.txn;

import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.*;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.secondary.*;
import com.bcgogo.txn.service.*;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.Vehicle;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/repairOrderSecondary.do")
public class RepairOrderSecondaryController {
  private static final Logger LOG = LoggerFactory.getLogger(RepairOrderSecondaryController.class);

  @RequestMapping(params = "method=printDebtRepairOrderSecondary")
  public void printDebtRepairOrderSecondary(HttpServletRequest request, HttpServletResponse response, Long repairOrderSecondaryId) {
    Long shopId = WebUtil.getShopId(request);
    Assert.notNull(shopId, "shopId is null!");
    Assert.notNull(repairOrderSecondaryId, "repairOrderSecondaryId is null!");
    IRepairOrderSecondaryService repairOrderSecondaryService = ServiceManager.getService(IRepairOrderSecondaryService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    IShopService shopService = ServiceManager.getService(IShopService.class);
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = null;
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String szDatetime1 = sdf.format(now);
    try {
      StringBuffer service = new StringBuffer(), material = new StringBuffer();
      ShopDTO shopDTO = shopService.getShopDTOById(shopId);
      RepairOrderSecondaryDTO repairOrderSecondaryDTO = repairOrderSecondaryService.findRepairOrderSecondaryById(shopId, repairOrderSecondaryId);
      if (repairOrderSecondaryDTO.getServiceDTOs() != null) {
        for (RepairOrderServiceSecondaryDTO repairOrderServiceSecondaryDTO : repairOrderSecondaryDTO.getServiceDTOs()) {
          service.append(repairOrderServiceSecondaryDTO.getService() + ",");
        }
        if (service.length() > 1) {
          service.delete(service.length() - 1, service.length());
        }
      }
      if (repairOrderSecondaryDTO.getItemDTOs() != null) {
        for (RepairOrderItemSecondaryDTO repairOrderItemSecondaryDTO : repairOrderSecondaryDTO.getItemDTOs()) {
          material.append(repairOrderItemSecondaryDTO.getProductName() + ",");
        }
        if (material.length() > 1) {
          material.delete(material.length() - 1, material.length());
        }
      }
      if (repairOrderSecondaryDTO.getTotal() == null) {
        repairOrderSecondaryDTO.setTotal(0.0);
      }
      if (repairOrderSecondaryDTO.getAccountDebtAmount() == null) {
        repairOrderSecondaryDTO.setAccountDebtAmount(0.0);
      }
      if (repairOrderSecondaryDTO.getSettledAmount() == null) {
        repairOrderSecondaryDTO.setSettledAmount(0.0);
      }
      CustomerDTO customerDTO = new CustomerDTO();
      customerDTO.setName(repairOrderSecondaryDTO.getCustomerName());
      customerDTO.setContact(repairOrderSecondaryDTO.getCustomerContact());
      customerDTO.setAddress(repairOrderSecondaryDTO.getCustomerAddress());
      customerDTO.setMobile(repairOrderSecondaryDTO.getCustomerMobile());
      List<DebtDTO> debtDTOList = new ArrayList<DebtDTO>();
      DebtDTO debtDTO = new DebtDTO();
      debtDTOList.add(debtDTO);
      debtDTO.setVehicleNumber(repairOrderSecondaryDTO.getVehicleLicense());
      debtDTO.setDate(repairOrderSecondaryDTO.getStartDateStr().substring(0, 10));
      debtDTO.setContent("保养/维修/美容");
      debtDTO.setService(service.toString());
      debtDTO.setMaterial(material.toString());
      debtDTO.setTotalAmount(repairOrderSecondaryDTO.getTotal());
      debtDTO.setDebt(repairOrderSecondaryDTO.getAccountDebtAmount());
      debtDTO.setSettledAmount(repairOrderSecondaryDTO.getTotal() - repairOrderSecondaryDTO.getAccountDebtAmount());
      String totalAmount = repairOrderSecondaryDTO.getTotal().toString();
      String payedAmount = repairOrderSecondaryDTO.getSettledAmount().toString();
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.DEBT);
      out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      if (printTemplateDTO != null) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String str = new String(bytes, "UTF-8");
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库
        StringResourceRepository repo = StringResourceLoader.getRepository();
        String myTemplateName = "balanceCount" + String.valueOf(WebUtil.getShopId(request));
        String myTemplate = str;
        repo.putStringResource(myTemplateName, myTemplate);
        Template template = ve.getTemplate(myTemplateName);
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        VelocityContext context = new VelocityContext();
        context.put("dataStr", szDatetime1);
        context.put("customerDTO", customerDTO);
        context.put("debtDTOList", debtDTOList);
        context.put("payedAmount", payedAmount);
        context.put("totalAmount", totalAmount);
        context.put("totalAmountStr", MoneyUtil.toBigType(totalAmount));
        context.put("payedAmountStr", MoneyUtil.toBigType(payedAmount));
        context.put("shopDTO", shopDTO);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }
    } catch (Exception e) {
      LOG.debug("/repairOrderSecondary.do?method=printDebtRepairOrderSecondary");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
    } finally {
      out.close();
    }
  }

  @RequestMapping(params = "method=printRepairOrderSecondary")
  public void printRepairOrderSecondary(HttpServletRequest request, HttpServletResponse response, Long repairOrderSecondaryId) {
    Long shopId = WebUtil.getShopId(request);
    Assert.notNull(shopId, "shopId is null!");
    Assert.notNull(repairOrderSecondaryId, "repairOrderSecondaryId is null!");
    IRepairOrderSecondaryService repairOrderSecondaryService = ServiceManager.getService(IRepairOrderSecondaryService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    IShopService shopService = ServiceManager.getService(IShopService.class);
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = null;
    try {
      out = response.getWriter();
      ShopDTO shopDTO = shopService.getShopDTOById(shopId);
      RepairOrderSecondaryDTO repairOrderSecondaryDTO = repairOrderSecondaryService.findRepairOrderSecondaryById(shopId, repairOrderSecondaryId);
      RepairOrderSettlementSecondaryDTO[] repairOrderSettlementSecondaryDTOs = repairOrderSecondaryDTO.getRepairOrderSettlementSecondaryDTOs();
      Double income = 0.0, discount = 0.0;
      String payee = "", incomeStr = "";
      if (repairOrderSettlementSecondaryDTOs != null) {
        for (RepairOrderSettlementSecondaryDTO repairOrderSettlementSecondaryDTO : repairOrderSettlementSecondaryDTOs) {
          income += repairOrderSettlementSecondaryDTO.getIncome() == null ? 0.0 : repairOrderSettlementSecondaryDTO.getIncome();
          discount += repairOrderSettlementSecondaryDTO.getDiscount() == null ? 0.0 : repairOrderSettlementSecondaryDTO.getDiscount();
        }
        payee = repairOrderSettlementSecondaryDTOs[repairOrderSettlementSecondaryDTOs.length - 1].getName();
        incomeStr = MoneyUtil.toBigType(income.toString());
      }
      RepairOrderServiceSecondaryDTO[] serviceDTOs = repairOrderSecondaryDTO.getServiceDTOs();
      StringBuffer serviceWorker = new StringBuffer();
      if (serviceDTOs != null) {
        Set<String> set = new HashSet<String>();
        for (RepairOrderServiceSecondaryDTO repairOrderServiceSecondaryDTO : serviceDTOs) {
          set.add(repairOrderServiceSecondaryDTO.getWorkers());
        }
        for (String worker : set) {
          serviceWorker.append(worker);
          serviceWorker.append(",");
        }
        if (serviceWorker.length() > 1) {
          serviceWorker.delete(serviceWorker.length() - 1, serviceWorker.length());
        }
      }
      Map fuelNumberList = TxnConstant.getFuelNumberMap(request.getLocale());
      repairOrderSecondaryDTO.setFuelNumber((String) fuelNumberList.get(repairOrderSecondaryDTO.getFuelNumber()));

      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(shopId, OrderTypes.REPAIR_SECONDARY);
      if (printTemplateDTO != null) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String str = new String(bytes, "UTF-8");
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库
        StringResourceRepository repo = StringResourceLoader.getRepository();
        String myTemplateName = "invoicingPrint" + String.valueOf(WebUtil.getShopId(request));
        String myTemplate = str;
        repo.putStringResource(myTemplateName, myTemplate);
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        VelocityContext context = new VelocityContext();
        context.put("repairOrderSecondaryDTO", repairOrderSecondaryDTO);
        context.put("shopDTO", shopDTO);
        context.put("income", income);
        context.put("incomeStr", incomeStr);
        context.put("discount", discount);
        context.put("payee", payee);
        context.put("serviceWorker", serviceWorker);
        context.put("isDebug", System.getProperty("is.developer.debug"));
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body><h1>没有可用的模板</h1></body><html>");
      }
    } catch (Exception e) {
      LOG.debug("/repairOrderSecondary.do?method=printRepairOrderSecondary");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
    } finally {
      out.close();
    }
  }

  @RequestMapping(params = "method=inquiryRepairOrderSecondary")
  public String inquiryRepairOrderSecondary(HttpServletRequest request, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    Long shopId = WebUtil.getShopId(request);
    Assert.notNull(shopId, "shopId is null!");
    IRepairOrderSecondaryService repairOrderSecondaryService = ServiceManager.getService(IRepairOrderSecondaryService.class);
    try {
      request.setAttribute("endDateStr", DateUtil.convertDateLongToString(new java.util.Date().getTime(), "yyyy-MM-dd"));
      RepairOrderSecondaryResponse repairOrderSecondaryResponse = repairOrderSecondaryService.statisticsRepairOrderSecondary(shopId, repairOrderSecondaryCondition);
      request.setAttribute("repairOrderSecondaryResponse", repairOrderSecondaryResponse);
    } catch (Exception e) {
      LOG.debug("/repairOrderSecondary.do?method=inquiryRepairOrderSecondary");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
    }
    return "/txn/secondary/vehicleConstructionInquiry";
  }

  @RequestMapping(params = "method=queryRepairOrderSecondary")
  @ResponseBody
  public Object queryRepairOrderSecondary(HttpServletRequest request, RepairOrderSecondaryCondition repairOrderSecondaryCondition) {
    Long shopId = WebUtil.getShopId(request);
    Assert.notNull(shopId, "shopId is null!");
    List<Object> objectList = new ArrayList<Object>();
    IRepairOrderSecondaryService repairOrderSecondaryService = ServiceManager.getService(IRepairOrderSecondaryService.class);
    try {
      int size = repairOrderSecondaryService.queryRepairOrderSecondarySize(shopId, repairOrderSecondaryCondition);
      if (size > 0) {
        RepairOrderSecondaryResponse repairOrderSecondaryResponse = repairOrderSecondaryService.queryRepairOrderSecondary(shopId, repairOrderSecondaryCondition);
        objectList.add(repairOrderSecondaryResponse);
        objectList.add(new Pager(size, repairOrderSecondaryCondition.getStartPageNo(), repairOrderSecondaryCondition.getMaxRows()));
      }
    } catch (Exception e) {
      LOG.debug("/repairOrderSecondary.do?method=queryRepairOrderSecondary");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
    }
    return objectList;
  }

  @RequestMapping(params = "method=debtRepairOrderSecondary")
  @ResponseBody
  public Object debtRepairOrderSecondary(HttpServletRequest request, Long repairOrderSecondaryId, Double settledAmount, Double accountDebtAmount, Double accountDiscount) {
    Long shopId = WebUtil.getShopId(request);
    Assert.notNull(shopId, "shopId is null!");
    Assert.notNull(repairOrderSecondaryId, "repairOrderSecondaryId is null!");
    Map result = new HashMap();
    IRepairOrderSecondaryService repairOrderSecondaryService = ServiceManager.getService(IRepairOrderSecondaryService.class);
    try {
      RepairOrderSecondaryDTO repairOrderSecondaryDTO = repairOrderSecondaryService.findRepairOrderSecondaryById(shopId, repairOrderSecondaryId);
      repairOrderSecondaryDTO.setSalesName(WebUtil.getUserName(request));
      repairOrderSecondaryDTO.setSettledAmount(settledAmount);
      repairOrderSecondaryDTO.setAccountDebtAmount(accountDebtAmount);
      repairOrderSecondaryDTO.setAccountDiscount(accountDiscount);
      repairOrderSecondaryDTO.setStatus(repairOrderSecondaryDTO.getAccountDebtAmount() != null && repairOrderSecondaryDTO.getAccountDebtAmount() > 0 ? OrderSecondaryStatus.REPAIR_DEBT : OrderSecondaryStatus.REPAIR_SETTLED);
      repairOrderSecondaryService.saveRepairOrderSecondary(repairOrderSecondaryDTO);
      result.put("success", true);
      result.put("data", repairOrderSecondaryDTO);
    } catch (Exception e) {
      result.put("success", false);
      LOG.debug("/repairOrderSecondary.do?method=debtRepairOrderSecondary");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=invalidRepairOrderSecondary")
  @ResponseBody
  public Object invalidRepairOrderSecondary(HttpServletRequest request, Long repairOrderSecondaryId) {
    Map result = new HashMap();
    Long shopId = WebUtil.getShopId(request);
    Assert.notNull(shopId, "shopId is null!");
    Assert.notNull(repairOrderSecondaryId, "repairOrderSecondaryId is null!");
    IRepairOrderSecondaryService repairOrderSecondaryService = ServiceManager.getService(IRepairOrderSecondaryService.class);
    try {
      int i = repairOrderSecondaryService.updateRepairOrderSecondaryOrderStatus(shopId, repairOrderSecondaryId, OrderStatus.REPAIR_REPEAL);
      result.put("success", i >= 1);
    } catch (Exception e) {
      result.put("success", false);
      LOG.debug("/repairOrderSecondary.do?method=invalidRepairOrderSecondary");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=showRepairOrderSecondary")
  public String showRepairOrderSecondary(HttpServletRequest request, Long repairOrderSecondaryId) {
    Long shopId = WebUtil.getShopId(request);
    Assert.notNull(shopId, "shopId is null!");
    Assert.notNull(repairOrderSecondaryId, "repairOrderSecondaryId is null!");
    IRepairOrderSecondaryService repairOrderSecondaryService = ServiceManager.getService(IRepairOrderSecondaryService.class);
    try {
      Double actualHoursTotal = 0.0, serviceTotal = 0.0;
      RepairOrderSecondaryDTO repairOrderSecondaryDTO = repairOrderSecondaryService.findRepairOrderSecondaryById(shopId, repairOrderSecondaryId);
      request.setAttribute("repairOrderSecondaryDTO", repairOrderSecondaryDTO);
      RepairOrderServiceSecondaryDTO[] repairOrderServiceSecondaryDTOs = repairOrderSecondaryDTO.getServiceDTOs();
      if (repairOrderServiceSecondaryDTOs != null) {
        for (RepairOrderServiceSecondaryDTO repairOrderServiceSecondaryDTO : repairOrderServiceSecondaryDTOs) {
          actualHoursTotal += repairOrderServiceSecondaryDTO.getActualHours() == null ? 0.0 : repairOrderServiceSecondaryDTO.getActualHours();
          serviceTotal += repairOrderServiceSecondaryDTO.getTotal() == null ? 0.0 : repairOrderServiceSecondaryDTO.getTotal();
        }
      }
      RepairOrderSettlementSecondaryDTO[] repairOrderSettlementSecondaryDTOs = repairOrderSecondaryDTO.getRepairOrderSettlementSecondaryDTOs();
      Double income = 0.0, discount = 0.0;
      if (repairOrderSettlementSecondaryDTOs != null) {
        for (RepairOrderSettlementSecondaryDTO repairOrderSettlementSecondaryDTO : repairOrderSettlementSecondaryDTOs) {
          income += repairOrderSettlementSecondaryDTO.getIncome() == null ? 0.0 : repairOrderSettlementSecondaryDTO.getIncome();
          discount += repairOrderSettlementSecondaryDTO.getDiscount() == null ? 0.0 : repairOrderSettlementSecondaryDTO.getDiscount();
        }
      }
      request.setAttribute("income", income);
      request.setAttribute("discount", discount);
      request.setAttribute("actualHoursTotal", actualHoursTotal);
      request.setAttribute("serviceTotal", serviceTotal);
      Map fuelNumberList = TxnConstant.getFuelNumberMap(request.getLocale());
      repairOrderSecondaryDTO.setFuelNumber((String) fuelNumberList.get(repairOrderSecondaryDTO.getFuelNumber()));
    } catch (Exception e) {
      LOG.debug("/repairOrderSecondary.do?method=showRepairOrderSecondary");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
    }
    return "/txn/secondary/vehicleConstructionFinish";
  }

  @RequestMapping(params = "method=updateRepairOrderSecondary")
  public String updateRepairOrderSecondary(HttpServletRequest request, Long repairOrderSecondaryId, Boolean again) {
    Long shopId = WebUtil.getShopId(request);
    Assert.notNull(shopId, "shopId is null!");
    Assert.notNull(repairOrderSecondaryId, "repairOrderSecondaryId is null!");
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    IRepairOrderSecondaryService repairOrderSecondaryService = ServiceManager.getService(IRepairOrderSecondaryService.class);
    try {
      RepairOrderSecondaryDTO repairOrderSecondaryDTO = repairOrderSecondaryService.findRepairOrderSecondaryById(shopId, repairOrderSecondaryId);
      repairOrderSecondaryDTO.setAgain(again);
      if (repairOrderSecondaryDTO.getServiceDTOs() == null || repairOrderSecondaryDTO.getServiceDTOs().length == 0) {
        RepairOrderServiceSecondaryDTO[] repairOrderServiceSecondaryDTOs = new RepairOrderServiceSecondaryDTO[]{new RepairOrderServiceSecondaryDTO()};
        repairOrderSecondaryDTO.setServiceDTOs(repairOrderServiceSecondaryDTOs);
      }
      if (repairOrderSecondaryDTO.getItemDTOs() == null || repairOrderSecondaryDTO.getItemDTOs().length == 0) {
        RepairOrderItemSecondaryDTO[] repairOrderItemSecondaryDTOs = new RepairOrderItemSecondaryDTO[]{new RepairOrderItemSecondaryDTO()};
        repairOrderSecondaryDTO.setItemDTOs(repairOrderItemSecondaryDTOs);
      }
      if (repairOrderSecondaryDTO.getOtherIncomeItemDTOs() == null || repairOrderSecondaryDTO.getOtherIncomeItemDTOs().length == 0) {
        RepairOrderOtherIncomeItemSecondaryDTO[] repairOrderOtherIncomeItemSecondaryDTOs = new RepairOrderOtherIncomeItemSecondaryDTO[]{new RepairOrderOtherIncomeItemSecondaryDTO()};
        repairOrderSecondaryDTO.setOtherIncomeItemDTOs(repairOrderOtherIncomeItemSecondaryDTOs);
      }
      request.setAttribute("repairOrderSecondaryDTO", repairOrderSecondaryDTO);
      Map fuelNumberList = TxnConstant.getFuelNumberMap(request.getLocale());
      request.setAttribute("fuelNumberList", fuelNumberList);
      VehicleDTO vehicleDTO = vehicleService.getVehicleDTOByLicenceNo(shopId, repairOrderSecondaryDTO.getVehicleLicense());
      CustomerDTO customerDTO = customerService.getCustomerById(repairOrderSecondaryDTO.getCustomerId());
      request.setAttribute("vehicleDTO", vehicleDTO);
      request.setAttribute("customerDTO", customerDTO);
    } catch (Exception e) {
      LOG.debug("/repairOrderSecondary.do?method=updateRepairOrderSecondary");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.debug("repairOrderSecondaryId:" + repairOrderSecondaryId);
      LOG.error(e.getMessage(), e);
    }
    return "/txn/secondary/vehicleConstruction";
  }

  @RequestMapping(params = "method=createRepairOrderSecondary")
  public String createRepairOrderSecondary(HttpServletRequest request, Long repairOrderId) {
    Long shopId = WebUtil.getShopId(request);
    Assert.notNull(shopId, "shopId is null!");
    IRepairOrderSecondaryService repairOrderSecondaryService = ServiceManager.getService(IRepairOrderSecondaryService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IRepairService repairService = ServiceManager.getService(IRepairService.class);
    RepairOrderSecondaryDTO repairOrderSecondaryDTO = null;
    try {
      repairOrderSecondaryDTO = repairOrderSecondaryService.findRepairOrderSecondaryByRepairOrderId(shopId, repairOrderId);
      if (repairOrderSecondaryDTO != null) {
        return showRepairOrderSecondary(request, repairOrderSecondaryDTO.getId());
      }
      RepairOrderDTO repairOrderDTO = rfiTxnService.getRepairOrderDTODetailById(repairOrderId, shopId);
      repairService.getProductInfo(repairOrderDTO);
      setServiceAndSaleTotal(repairOrderDTO);
      repairOrderSecondaryDTO = new RepairOrderSecondaryDTO();
      repairOrderSecondaryDTO.fromRepairOrderDTO(repairOrderDTO);
      {
        RepairOrderItemSecondaryDTO[] repairOrderItemSecondaryDTOs = repairOrderSecondaryDTO.getItemDTOs();
        if (repairOrderItemSecondaryDTOs != null) {
          IProductService productService = ServiceManager.getService(IProductService.class);
          for (RepairOrderItemSecondaryDTO repairOrderItemSecondaryDTO : repairOrderItemSecondaryDTOs) {
            ProductDTO productDTO = productService.getProductByProductLocalInfoId(repairOrderItemSecondaryDTO.getProductId(), shopId);
            if (productDTO != null) {
              repairOrderItemSecondaryDTO.setStorageUnit(productDTO.getStorageUnit());
              repairOrderItemSecondaryDTO.setSellUnit(productDTO.getSellUnit());
              repairOrderItemSecondaryDTO.setRate(productDTO.getRate());
            }
          }
        }
      }
      if (repairOrderSecondaryDTO.getServiceDTOs() == null || repairOrderSecondaryDTO.getServiceDTOs().length == 0) {
        RepairOrderServiceSecondaryDTO[] repairOrderServiceSecondaryDTOs = new RepairOrderServiceSecondaryDTO[]{new RepairOrderServiceSecondaryDTO()};
        repairOrderSecondaryDTO.setServiceDTOs(repairOrderServiceSecondaryDTOs);
      }
      if (repairOrderSecondaryDTO.getItemDTOs() == null || repairOrderSecondaryDTO.getItemDTOs().length == 0) {
        RepairOrderItemSecondaryDTO[] repairOrderItemSecondaryDTOs = new RepairOrderItemSecondaryDTO[]{new RepairOrderItemSecondaryDTO()};
        repairOrderSecondaryDTO.setItemDTOs(repairOrderItemSecondaryDTOs);
      }
      if (repairOrderSecondaryDTO.getOtherIncomeItemDTOs() == null || repairOrderSecondaryDTO.getOtherIncomeItemDTOs().length == 0) {
        RepairOrderOtherIncomeItemSecondaryDTO[] repairOrderOtherIncomeItemSecondaryDTOs = new RepairOrderOtherIncomeItemSecondaryDTO[]{new RepairOrderOtherIncomeItemSecondaryDTO()};
        repairOrderSecondaryDTO.setOtherIncomeItemDTOs(repairOrderOtherIncomeItemSecondaryDTOs);
      }
      request.setAttribute("repairOrderSecondaryDTO", repairOrderSecondaryDTO);
      Map fuelNumberList = TxnConstant.getFuelNumberMap(request.getLocale());
      request.setAttribute("fuelNumberList", fuelNumberList);
      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, repairOrderSecondaryDTO.getCustomerId());
      request.setAttribute("memberDTO", memberDTO);
      VehicleDTO vehicleDTO = vehicleService.getVehicleDTOByLicenceNo(shopId, repairOrderSecondaryDTO.getVehicleLicense());
      CustomerDTO customerDTO = customerService.getCustomerById(repairOrderSecondaryDTO.getCustomerId());
      request.setAttribute("vehicleDTO", vehicleDTO);
      request.setAttribute("customerDTO", customerDTO);
    } catch (Exception e) {
      LOG.debug("/repairOrderSecondary.do?method=createRepairOrderSecondary");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
    }
    return "/txn/secondary/vehicleConstruction";
  }

  @RequestMapping(params = "method=submitRepairOrderSecondary")
  @ResponseBody
  public Map submitRepairOrderSecondary(HttpServletRequest request, RepairOrderSecondaryDTO repairOrderSecondaryDTO) {
    Map result = new HashMap();
    Long shopId = WebUtil.getShopId(request);
    Assert.notNull(shopId, "shopId is null!");
    IRepairOrderSecondaryService repairOrderSecondaryService = ServiceManager.getService(IRepairOrderSecondaryService.class);
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    try {
      repairOrderSecondaryDTO.setSalesName(WebUtil.getUserName(request));
      repairOrderSecondaryDTO.setShopId(shopId);
      repairOrderSecondaryDTO.setVehicleModel(request.getParameter("model"));
      repairOrderSecondaryDTO.setVehicleBrand(request.getParameter("brand"));

      VehicleDTO vehicleDTO = vehicleService.getVehicleDTOById(repairOrderSecondaryDTO.getVehicleId());
      if (vehicleDTO != null) {
        repairOrderSecondaryDTO.setVehicleBuyDate(vehicleDTO.getCarDate());
        repairOrderSecondaryDTO.setVehicleChassisNo(vehicleDTO.getChassisNumber());
        repairOrderSecondaryDTO.setVehicleColor(vehicleDTO.getColor());
        repairOrderSecondaryDTO.setVehicleEngineNo(vehicleDTO.getEngineNo());
      }

      if (StringUtils.isNotEmpty(repairOrderSecondaryDTO.getProductSaler())) {
        int i = repairOrderSecondaryDTO.getProductSaler().lastIndexOf(",");
        if (i != -1) {
          repairOrderSecondaryDTO.setProductSaler(repairOrderSecondaryDTO.getProductSaler().substring(0, i));
        }
      }
      if (repairOrderSecondaryDTO.getServiceDTOs() != null) {
        for (RepairOrderServiceSecondaryDTO repairOrderServiceSecondaryDTO : repairOrderSecondaryDTO.getServiceDTOs()) {
          if (StringUtils.isNotEmpty(repairOrderServiceSecondaryDTO.getWorkers())) {
            int i = repairOrderServiceSecondaryDTO.getWorkers().lastIndexOf(",");
            if (i != -1) {
              repairOrderServiceSecondaryDTO.setWorkers(repairOrderServiceSecondaryDTO.getWorkers().substring(0, i));
            }
          }
        }
      }
      if (repairOrderSecondaryDTO.getStartDateStr() != null) {
        repairOrderSecondaryDTO.setStartDate(DateUtil.convertDateStringToDateLong("yyyy-MM-dd HH:mm", repairOrderSecondaryDTO.getStartDateStr()));
      }
      if (repairOrderSecondaryDTO.getEndDateStr() != null) {
        repairOrderSecondaryDTO.setEndDate(DateUtil.convertDateStringToDateLong("yyyy-MM-dd HH:mm", repairOrderSecondaryDTO.getEndDateStr()));
      }
      repairOrderSecondaryDTO.setStatus(repairOrderSecondaryDTO.getAccountDebtAmount() != null && repairOrderSecondaryDTO.getAccountDebtAmount() > 0 ? OrderSecondaryStatus.REPAIR_DEBT : OrderSecondaryStatus.REPAIR_SETTLED);
      repairOrderSecondaryDTO = repairOrderSecondaryService.saveRepairOrderSecondary(repairOrderSecondaryDTO);
      ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
          new OperationLogDTO(shopId,WebUtil.getUserId(request), repairOrderSecondaryDTO.getRepairOrderId(), ObjectTypes.REPAIR_ORDER, OperationTypes.REPAIR_ORDER_SECONDARY));
      result.put("success", true);
      result.put("data", repairOrderSecondaryDTO);
    } catch (IllegalArgumentException e) {
      result.put("success", false);
      result.put("response", e.getMessage());
    } catch (Exception e) {
      result.put("success", false);
      LOG.debug("/repairOrderSecondary.do?method=submitRepairOrderSecondary");
      LOG.debug("shopId:" + WebUtil.getShopId(request));
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  public void setServiceAndSaleTotal(RepairOrderDTO repairOrderDTO) {
    double salesTotal = 0.0;
    double serviceTotal = 0.0;
    double actualHoursTotal = 0.0;
    if (repairOrderDTO.getServiceDTOs() != null && repairOrderDTO.getServiceDTOs().length > 0) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (repairOrderServiceDTO == null || StringUtil.isEmpty(repairOrderServiceDTO.getService())) {
          continue;
        }
        if (ConsumeType.TIMES == repairOrderServiceDTO.getConsumeType()) {
          continue;
        }
        serviceTotal += repairOrderServiceDTO.getTotal();
        actualHoursTotal += NumberUtil.doubleVal(repairOrderServiceDTO.getActualHours());
      }
    }
    repairOrderDTO.setServiceTotal(NumberUtil.round(serviceTotal, NumberUtil.MONEY_PRECISION));
    repairOrderDTO.setActualHoursTotal(NumberUtil.round(actualHoursTotal, NumberUtil.MONEY_PRECISION));
    if (repairOrderDTO.getItemDTOs() != null && repairOrderDTO.getItemDTOs().length > 0) {
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (repairOrderItemDTO == null || StringUtil.isEmpty(repairOrderItemDTO.getProductName())) {
          continue;
        }
        salesTotal += repairOrderItemDTO.getTotal();
      }
    }
    repairOrderDTO.setSalesTotal(NumberUtil.round(salesTotal, NumberUtil.MONEY_PRECISION));
  }

  @RequestMapping(params = "method=settleAccounts")
  public String accountDetail(HttpServletRequest request) {
    String debt = request.getParameter("debt");
    request.setAttribute("debt", debt != null);
    return "/txn/secondary/settleAccounts";
  }
}
