package com.bcgogo.user;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.service.ISupplierPayableService;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.service.ISupplierPayableService;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("RFSupplier.do")
public class RFSupplierController {
  private static final Logger LOGGER = LoggerFactory.getLogger(RFSupplierController.class);
  private static final String PAGE_SUPPLIERINFO = "/txn/supplierInfo";
  private static final String MODEL_CATEGORYLIST = "categoryList";
  private static final String MODEL_SETTLEMENTTYPELIST = "settlementTypeList";
  private static final String MODEL_INVOICECATEGORYLIST = "invoiceCategoryList";

  @RequestMapping(params = "method=ajaxSearchSupplierName")
  public void ajaxSearchSupplierName(HttpServletRequest request, HttpServletResponse response, String name) throws Exception {
    try {
      Long shopId = WebUtil.getShopId(request);
      String jsonStr = "[]";
      StringBuffer sb = new StringBuffer("[");
      if (StringUtils.isNotBlank(name)) {
        List<SupplierDTO> supplierDTOList;
        if (WebUtil.isChinese(name.charAt(0))) {
          supplierDTOList = ServiceManager.getService(IUserService.class).getSupplier(name.trim(), shopId);
        } else {
          String keyWord = PinyinUtil.converterToFirstSpell(name.trim());
          supplierDTOList = ServiceManager.getService(IUserService.class).getSupplierByZiMu(keyWord, shopId);
        }
        if (supplierDTOList != null && supplierDTOList.size() > 0) {
          {
            for (SupplierDTO supplierDTO : supplierDTOList) {
              sb.append("{\"name\":\"" + supplierDTO.getName() + "\",");
              if (supplierDTO.getContact() != null && !supplierDTO.getContact().equals("")&& !supplierDTO.getContact().equals("NULL") && !supplierDTO.getContact().equals("null")) {
                sb.append("\"contract\":\"" + supplierDTO.getContact() + "\",");
              }
              else
              {
                     sb.append("\"contract\":\"" + "\",");
              }
              if (supplierDTO.getMobile() != null && !supplierDTO.getMobile().equals("")) {
                sb.append("\"mobile\":\"" + supplierDTO.getMobile() + "\",");
              } else if (supplierDTO.getLandLine() != null && !supplierDTO.getLandLine().equals("")) {
                sb.append("\"mobile\":\"" + supplierDTO.getLandLine() + "\",");
              }
              else
              {
                      sb.append("\"mobile\":\"" +  "\",");
              }
              if (supplierDTO.getAddress() != null && !supplierDTO.getAddress().equals("")) {
                sb.append("\"address\":\"" + supplierDTO.getAddress() + "\",");
              }
              else
              {
                 sb.append("\"address\":\"" +  "\",");
              }
           if (supplierDTO.getId() != null) {
                sb.append("\"idStr\":\"" + supplierDTO.getIdString() + "\"},");
              }
            }
            sb.replace(sb.length() - 1, sb.length(), "]");
            jsonStr = sb.toString();
          }
        }
      }
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOGGER.debug("/RFSupplier.do");
      LOGGER.debug("method=ajaxSearchSupplierName");
      LOGGER.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOGGER.debug("name:" + name);
      WebUtil.reThrow(LOGGER, e);
    }

  }

  @RequestMapping(params = "method=ajaxSearchSupplier")
  public void ajaxSearchSupplier(HttpServletRequest request, HttpServletResponse response, String supplier) throws Exception {
    try {
      Long shopId = WebUtil.getShopId(request);
      List<SupplierDTO> supplierDTOList = ServiceManager.getService(IUserService.class).getSupplierByName(shopId, supplier);
      String jsonStr = "";
      if (supplierDTOList != null && supplierDTOList.size() > 0) {
        jsonStr = supplierDTOList.get(0).toJsonStr();
      }
      PrintWriter printWriter = response.getWriter();
      printWriter.write(jsonStr);
      printWriter.close();
    } catch (Exception e) {
      LOGGER.debug("/RFSupplier.do");
      LOGGER.debug("method=ajaxSearchSupplier");
      LOGGER.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOGGER.debug("supplier:" + supplier);
      WebUtil.reThrow(LOGGER, e);
    }
  }

  @RequestMapping(params = "method=ajaxSearchSupplierById")
  public void ajaxSearchSupplierById(HttpServletRequest request, HttpServletResponse response, String supplierId) throws Exception {
    try {
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
      Long shopId = WebUtil.getShopId(request);
	    List<SupplierDTO> supplierDTOList;
	    if(StringUtils.isNotBlank(supplierId)){
		    supplierDTOList = ServiceManager.getService(IUserService.class).getSupplierById(shopId, NumberUtil.longValue(supplierId));
        if(CollectionUtils.isNotEmpty(supplierDTOList)){
          List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(Long.valueOf(supplierId),shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额
          //应付款总额
          if(supplierDTOList.get(0).getCustomerId() != null) {
              Double payable = supplierPayableService.getSumReceivableByCustomerId(supplierDTOList.get(0).getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_PAYABLE);
              supplierDTOList.get(0).setTotalPayable(NumberUtil.round(doubleList.get(0),2) - NumberUtil.doubleVal(payable));
          } else {
              supplierDTOList.get(0).setTotalPayable(NumberUtil.round(doubleList.get(0),2));
          }
          //应收款总额
//          SupplierRecordDTO supplierRecordDTO = ServiceManager.getService(ISupplierPayableService.class).getSupplierRecordDTOBySupplierId(shopId,Long.parseLong(supplierId));
//          if(null != supplierRecordDTO && null != supplierRecordDTO.getDebt()){
//            supplierDTOList.get(0).setTotalDebt(NumberUtil.round(NumberUtil.numberValue(supplierRecordDTO.getDebt(),0D),NumberUtil.MONEY_PRECISION));
//          }
            List<Double> returnList = supplierPayableService.getSumPayableBySupplierId(Long.valueOf(supplierId), shopId,OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
            if(supplierDTOList.get(0).getCustomerId() != null) {
                Double receivable = supplierPayableService.getSumReceivableByCustomerId(supplierDTOList.get(0).getCustomerId(), shopId, OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
                supplierDTOList.get(0).setTotalDebt(NumberUtil.round(0-returnList.get(0),2) + NumberUtil.doubleVal(receivable));
            } else {
                supplierDTOList.get(0).setTotalDebt(NumberUtil.round(0-returnList.get(0),2));
            }
        }
      }else {
		    LOGGER.warn("RFSupplier.do?method=ajaxSearchSupplierById 接收到的参数supplierId:为空，请检查错误");
		    supplierDTOList = new ArrayList<SupplierDTO>();
	    }
      PrintWriter printWriter = response.getWriter();
      printWriter.write(JsonUtil.listToJson(supplierDTOList));
      printWriter.close();
    } catch (Exception e) {
      LOGGER.debug("/RFSupplier.do");
      LOGGER.debug("method=ajaxSearchSupplierById");
      LOGGER.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOGGER.debug("supplierId:" + supplierId);
      WebUtil.reThrow(LOGGER, e);
    }
  }

  @RequestMapping(params = "method=getSupplierByNameAndShopId")
  @ResponseBody
  public Object getSupplierByNameAndShopId(HttpServletRequest request, String name) throws Exception {
    boolean exactMatch = true;
    try {
      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
      Long shopId = WebUtil.getShopId(request);
      //1. 精确匹配供应商名字
      List<SupplierDTO> supplierDTOList = ServiceManager.getService(IUserService.class).getSupplierByNameAndShopId(shopId, name);
//      //2. 精确匹配不到的时候，模糊匹配
//      if (CollectionUtils.isEmpty(supplierDTOList)) {
//        exactMatch=false;
//        if (StringUtils.isNotBlank(name)) {
//          if (WebUtil.isChinese(name.charAt(0))) {   //匹配名字
//            supplierDTOList = ServiceManager.getService(IUserService.class).getSupplier(name.trim(), shopId);
//          } else {      //匹配首字母
//            String keyWord = PinyinUtil.converterToFirstSpell(name.trim());
//            supplierDTOList = ServiceManager.getService(IUserService.class).getSupplierByZiMu(keyWord, shopId);
//          }
//        }
//      }
      Map<String,Object> result = new HashMap<String,Object>();
      result.put("success","true");
      result.put("exactMatch", exactMatch);
      if (CollectionUtils.isNotEmpty(supplierDTOList)) {
        List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(supplierDTOList.get(0).getId(),shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);//应付款总额 实付总额

                 //应付款总额
        Double totalPayable = doubleList.get(0);
        supplierDTOList.get(0).setTotalPayable(totalPayable);
        SupplierRecordDTO supplierRecordDTO = supplierPayableService.getSupplierRecordDTOBySupplierId(shopId,supplierDTOList.get(0).getId());
        if(null != supplierRecordDTO && null != supplierRecordDTO.getDebt())
        {
          supplierDTOList.get(0).setTotalDebt(NumberUtil.round(NumberUtil.numberValue(supplierRecordDTO.getDebt(),0D),NumberUtil.MONEY_PRECISION));
        }
        result.put("supplierDTO",supplierDTOList.get(0));
      }
      return result;
    } catch (Exception e) {
      LOGGER.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOGGER.debug("name:" + name);
      WebUtil.reThrow(LOGGER, e);
    }
    return null;
  }
  //供应商页面双击修改供应商单位验证是否重复
  @RequestMapping(params = "method=isSupplierDuplicate")
  public void isSupplierDuplicate(HttpServletRequest request, HttpServletResponse response, String name,String supplierId) throws Exception {
    try {
      Long shopId = WebUtil.getShopId(request);
      List<SupplierDTO> supplierDTOList = ServiceManager.getService(IUserService.class).getSupplierByNameAndShopId(shopId, name);
      String jsonStr = "";
      if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(supplierId)) {
        StringBuffer sb = new StringBuffer("");
        if (CollectionUtils.isNotEmpty(supplierDTOList) && !supplierId.equals(String.valueOf(supplierDTOList.get(0).getId()))) {
          sb.append("[\"duplicate\"]");
        } else {
          sb.append("[\"notDuplicate\"]");
        }
        jsonStr = sb.toString();
        PrintWriter printWriter = response.getWriter();
        printWriter.write(jsonStr);
        printWriter.close();
      } else {
        throw new Exception("name or supplierId is null!");
      }
    } catch (Exception e) {
      LOGGER.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOGGER.debug("name:" + name+" supplierId:"+supplierId);
      WebUtil.reThrow(LOGGER, e);
    }

  }


  @RequestMapping(params = "method=showSupplier")
  public String showSupplier(HttpServletRequest request, ModelMap modelMap) {
    Map categoryList = TxnConstant.getCustomerTypeMap(request.getLocale());
    modelMap.addAttribute(MODEL_CATEGORYLIST, categoryList);
    Map settlementTypeList = TxnConstant.getSettlementTypeMap(request.getLocale());
    modelMap.addAttribute(MODEL_SETTLEMENTTYPELIST, settlementTypeList);
    Map invoiceCategoryList = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
    modelMap.addAttribute(MODEL_INVOICECATEGORYLIST, invoiceCategoryList);
    Map<String, String> customerTypeMap = TxnConstant.getCustomerTypeMap(request.getLocale());
    request.setAttribute("customerTypeMap", customerTypeMap);
    String supplierId = request.getParameter("supplierId");
    if(supplierId!=null){
      modelMap.addAttribute("supplierId",supplierId);
    }
      request.setAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
    return PAGE_SUPPLIERINFO;
  }


}
