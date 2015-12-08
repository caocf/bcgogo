package com.bcgogo.customer;

import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.PopMessage;
import com.bcgogo.common.PrintHelper;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.SortObj;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.CustomerDepositDTO;
import com.bcgogo.txn.dto.DepositOrderDTO;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.service.ICustomerDepositService;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.IRunningStatService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.MoneyUtil;
import com.bcgogo.utils.NumberUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bcgogo.utils.JsonUtil;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 客户预收金controller
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-10
 * Time: 下午7:00
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/customerDeposit.do")
public class CustomerDepositController {

  private static final String CASH = "cash";
  private static final String BANK_CARD_AMOUNT = "bankCardAmount";
  private static final String CHECK_AMOUNT = "checkAmount";
  private static final String CHECK_NO = "checkNo";
  private static final String IN_OUT_FLAG = "inOutFlag";
  private static final String START_PAGE_NO = "startPageNo";
  private static final int PAGE_SIZE = 10;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private static final String CUSTOMER_ID = "customerId";

  private ICustomerDepositService getCustomerDepositService() {
    return ServiceManager.getService(ICustomerDepositService.class);
  }

  private IRunningStatService getRunningStatService() {
    return ServiceManager.getService(IRunningStatService.class);
  }

  /**
   * 通过shopId,customerId获取预收金总额统计
   *
   * @param request
   * @param modelMap
   * @param response
   * @return
   */
  @RequestMapping(params = "method=ajaxGetCustomerDeposit", method = RequestMethod.GET)
  public void getCustomerDeposit(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response) {

    Long shopId = WebUtil.getShopId(request);
    String customerId = request.getParameter(CUSTOMER_ID);
    if (StringUtils.isBlank(customerId))
      return;
    try {
      CustomerDepositDTO customerDepositDTO = getCustomerDepositService().queryCustomerDepositByShopIdAndCustomerId(shopId, Long.parseLong(customerId));
      returnJsonResult(response, String.valueOf(customerDepositDTO==null?0:customerDepositDTO.getActuallyPaid()));
    } catch (Exception e) {
      logger.error("ajaxGetCustomerDepositError:shopId is {},customerId is {},the error msg is {}", new Object[]{String.valueOf(shopId), customerId, e});
      return;
    }
  }

  /**
   * 预收金充值
   *
   * @param request
   * @param modelMap
   * @return
   */

  @RequestMapping(params = "method=addCustomerDeposit", method = RequestMethod.POST)
  @ResponseBody
  public Result ajaxAddCustomerDeposit(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response) {
    Long shopId = WebUtil.getShopId(request);
    String customerDepositDTO = request.getParameter("depositDTO");
    String print = request.getParameter("print");
    Result result = new Result(true);

    try {
      CustomerDepositDTO depositDTO = new Gson().fromJson(customerDepositDTO, CustomerDepositDTO.class);
      depositDTO.setShopId(shopId);
      depositDTO.setOperator(WebUtil.getUserName(request));
      result = getCustomerDepositService().customerDeposit(depositDTO);
      depositDTO.setPayTime(System.currentTimeMillis());
      // 统计流水
      getRunningStatService().runningStatFromCustomerDepositDTO(depositDTO, false);
      //ServiceManager.getService(ISupplierSolrWriteService.class).reindexSupplierBySupplierId(depositDTO.getCustomerId());
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(depositDTO.getCustomerId());
      if(StringUtils.isNotBlank(print) && print.equals("true")){
        result.setOperation("print");
      }
      return result;
    } catch (Exception e) {
      logger.error("sync add customer deposit error:param is {},the stack is:{}", customerDepositDTO, e);
      result =  new Result(false);
      result.setData(0D);
      return result;
    }
  }


  /**
   * 获取预收金取用记录
   *
   * @param request
   * @param modelMap
   * @param response
   */
  @RequestMapping(params = "method=queryDepositOrdersByCustomerIdOrSupplierId", method = RequestMethod.POST)
// 分页组件默认为POST方法
  @ResponseBody
  public PagingListResult ajaxDepositOrdersQuery(HttpServletRequest request, ModelMap modelMap, HttpServletResponse response, Integer startPageNo) {


    PagingListResult<DepositOrderDTO> resultList = new PagingListResult<DepositOrderDTO>();
    Long shopId = WebUtil.getShopId(request);
    String customerId = request.getParameter(CUSTOMER_ID);
    try {
      resultList.setSuccess(true);
      int start = startPageNo == null ? 1 : startPageNo;
      int pageSize = PAGE_SIZE;
      int totalCount = 0;
      Pager pager = new Pager(totalCount, start, pageSize);
      resultList.setPager(pager);
      if (StringUtils.isBlank(customerId)) {
        logger.error("[queryDepositOrdersByCustomerId],customerId is blank.");
        return resultList;
      }
      if (StringUtils.isBlank(request.getParameter(IN_OUT_FLAG))) {
        return resultList;
      }
      Long inOutFlag = Long.parseLong(request.getParameter(IN_OUT_FLAG)); // 0全部 1入 2出
      // 排序字段
      String sortName = request.getParameter("sortName");
      String sortFlag = request.getParameter("sortFlag");
      SortObj sortObj = new SortObj();
      if (StringUtils.isNotBlank(sortName) && StringUtils.isNotBlank(sortFlag)) {
        sortObj.setSortName(sortName);
        sortObj.setSortFlag(sortFlag);
      }

      totalCount = getCustomerDepositService().countDepositOrderByShopIdAndCustomerId(shopId, Long.parseLong(customerId), inOutFlag);
      pager = new Pager(totalCount, start, pageSize);
      resultList.setPager(pager);
      List<DepositOrderDTO> depositOrderDTOs = getCustomerDepositService().queryDepositOrdersByShopIdCustomerId(shopId, Long.parseLong(customerId), inOutFlag, sortObj,pager);
      if (!CollectionUtils.isEmpty(depositOrderDTOs)) {
        resultList.setResults(depositOrderDTOs);
        resultList.setSuccess(true);
        return resultList;
      }
      return resultList;
    } catch (Exception e) {
      logger.error("[ajaxDepositOrdersQuery]:shopId is {},customerId is {},", new Object[]{shopId, customerId});
    }
    return resultList;
  }

  private void returnJsonResult(HttpServletResponse response, String resultMsg) throws IOException {
    PopMessage popMessage = new PopMessage();
    popMessage.setMessage(resultMsg);
    String jsonStr = JsonUtil.objectToJson(popMessage);
    PrintWriter writer = response.getWriter();
    writer.write(jsonStr);
    writer.close();
  }

  @RequestMapping(params = "method=printDeposit")
  public void printDeposit(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    Long shopId = (Long) request.getSession().getAttribute("shopId");
    ShopDTO shopDTO = configService.getShopById(shopId);
    Long customerId = NumberUtil.longValue(request.getParameter("customerId"), 0L);
    String cashDeposit = request.getParameter("cashDeposit");
    String bankCardAmountDeposit = request.getParameter("bankCardAmountDeposit");
    String checkAmountDeposit = request.getParameter("checkAmountDeposit");
    String checkNoDeposit = request.getParameter("checkNoDeposit");
    String actuallyPaidDeposit = request.getParameter("actuallyPaidDeposit");

    CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(customerId);
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String szDatetime1 = sdf.format(now);
    try {
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.PRE_RECEIVE);
      String myTemplateName = "preReceive" + String.valueOf(WebUtil.getShopId(request));
      VelocityContext context = new VelocityContext();
      //把数据填入上下文
      context.put("dateStr", szDatetime1);                            //结算日期
      context.put("customerDTO", customerDTO);                       //供应商信息
      context.put("cashDeposit", cashDeposit);                        //现金
      context.put("bankCardAmountDeposit", bankCardAmountDeposit);  //银行卡
      context.put("checkAmountDeposit", checkAmountDeposit);        //支票
      context.put("actuallyPaidDeposit", actuallyPaidDeposit);       //实付
      context.put("actuallyPaidDepositStr", MoneyUtil.toBigType(actuallyPaidDeposit));       //实付大写
      context.put("shopDTO", shopDTO);                                //公司名称
      PrintHelper.generatePrintPage(response, printTemplateDTO.getTemplateHtml(), myTemplateName, context);
    }catch (Exception e) {
      logger.debug("/customerDeposit.do");
      logger.debug("customerId:" + customerId);
      logger.error(e.getMessage(), e);
    }
  }


}
