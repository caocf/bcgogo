package com.bcgogo.stat;

import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.BusinessAccountEnum;
import com.bcgogo.enums.CategoryType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.stat.businessAccountStat.CalculateType;
import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.BusinessAccountDTO;
import com.bcgogo.stat.dto.BusinessAccountSearchConditionDTO;
import com.bcgogo.stat.dto.BusinessCategoryDTO;
import com.bcgogo.stat.service.IBusinessAccountService;
import com.bcgogo.txn.dto.CategoryDTO;
import com.bcgogo.txn.dto.OtherIncomeKindDTO;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.service.IBusinessStatService;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.user.dto.DepartmentDTO;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于营业外记账
 * Created by IntelliJ IDEA.
 * User: Li jinlong
 * Date: 12-9-19
 * Time: 下午7:22
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/businessAccount.do")
public class BusinessAccountController {

  private static final Log LOG = LogFactory.getLog(BusinessAccountController.class);
  //用于分页显示，每页20条记录
  private static final Integer maxRows = 20;

  /**
   * 营业外记账搜索页面初始化
   *
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=initBusinessAccountSearch")
  public String initBusinessAccountSearch(ModelMap model, HttpServletRequest request) throws Exception {
    BusinessAccountSearchConditionDTO businessAccountSearchConditionDTO = new BusinessAccountSearchConditionDTO();
    IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
    IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
    Long shopId = WebUtil.getShopId(request);
    model.addAttribute("businessAccountSearchConditionDTO", businessAccountSearchConditionDTO);
    return "stat/businessAccountSearch";
  }


  /**
   * 营业外记账新增
   *
   * @param model
   * @param request
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=addBusinessAccount")
  public String addBusinessAccount(ModelMap model, HttpServletRequest request,BusinessAccountDTO businessAccountDTO) throws Exception {
    IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null)
        return "/";
      //isNewBusinessAccount字段判断是新增还是保存
      String isNewBusinessAccount = request.getParameter("isNewBusinessAccount");
      if (StringUtils.isNotBlank(isNewBusinessAccount)) {
        businessAccountDTO = new BusinessAccountDTO();
        businessAccountDTO.setEditDateStr(DateUtil.format(DateUtil.DATE_STRING_FORMAT_DAY, new Date()));
        businessAccountDTO.setShopId(shopId);
      } else {

        if (NumberUtil.longValue(businessAccountDTO.getDepartmentId()) > 0) {
          DepartmentDTO departmentDTO = userService.getDepartmentById(businessAccountDTO.getDepartmentId());
          if (departmentDTO != null) {
            businessAccountDTO.setDept(departmentDTO.getName());
          }
        }

        if (NumberUtil.longValue(businessAccountDTO.getSalesManId()) > 0) {
          SalesManDTO salesManDTO = userService.getSalesManDTOById(businessAccountDTO.getSalesManId());
          if (salesManDTO != null) {
            businessAccountDTO.setPerson(salesManDTO.getName());
          }
        }


        IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
        businessAccountService.saveOrUpdateBusinessCategoryFromDTO(businessAccountDTO);
        //保存营业分类
        String businessCategory = businessAccountDTO.getBusinessCategory();

        CategoryDTO categoryDTO = rfiTxnService.saveCategory(shopId, businessCategory);
        if(categoryDTO !=null){
          businessAccountDTO.setBusinessCategoryId(categoryDTO.getId());
        }
        businessAccountDTO = businessAccountService.saveBusinessAccount(businessAccountDTO);



        businessAccountDTO.setUserId(WebUtil.getUserId(request));
        businessAccountDTO.setUserName(WebUtil.getUserName(request));
        //流水统计
        businessStatService.statFromBusinessAccountDTO(null, businessAccountDTO, com.bcgogo.enums.BusinessAccountEnum.STATUS_SAVE);
        //营业外记账统计
        businessAccountService.businessCategoryStatByDTO(businessAccountDTO, CalculateType.ADD);
      }
      model.addAttribute("businessAccountDTO", businessAccountDTO);
      List<DepartmentDTO> departmentDTOList = ServiceManager.getService(IUserCacheService.class).getDepartmentByName(shopId, "");
      model.addAttribute("departmentDTOList", departmentDTOList);

    } catch (Exception e) {
      LOG.error("/businessAccount.do");
      LOG.error("method=addBusinessAccount");
      LOG.error("shopId:" + WebUtil.getShopId(request) + ",userId:" + WebUtil.getUserId(request));
      LOG.error(e.getMessage(), e);
    }
    return "stat/businessAccountAdd";
  }


  /**
   * 营业外记账编辑页面初始化及保存
   *
   * @param model
   * @param request
   * @param
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=editBusinessAccount")
  public String editBusinessAccount(ModelMap model, HttpServletRequest request,BusinessAccountDTO businessAccountDTO) throws Exception {
    IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null)
        return "/";
      String businessAccountId = request.getParameter("id");
      //isEditBusinessAccount字段判断是修改页面初始化还是保存修改
      String isEditBusinessAccount = request.getParameter("isEditBusinessAccount");
      IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
      if (StringUtils.isNotBlank(isEditBusinessAccount)) {
        businessAccountDTO = businessAccountService.getBusinessAccountById(Long.valueOf(businessAccountId));

        if (businessAccountDTO != null && businessAccountDTO.getDepartmentId() != null) {
          List<SalesManDTO> salesManDTOList = userService.getSalesManByDepartmentId(WebUtil.getShopId(request), businessAccountDTO.getDepartmentId());
          model.addAttribute("salesManDTOList", salesManDTOList);
        }

      } else {

        if (NumberUtil.longValue(businessAccountDTO.getDepartmentId()) > 0) {
          DepartmentDTO departmentDTO = userService.getDepartmentById(businessAccountDTO.getDepartmentId());
          if (departmentDTO != null) {
            businessAccountDTO.setDept(departmentDTO.getName());
          }
        }

        if (NumberUtil.longValue(businessAccountDTO.getSalesManId()) > 0) {
          SalesManDTO salesManDTO = userService.getSalesManDTOById(businessAccountDTO.getSalesManId());
          if (salesManDTO != null) {
            businessAccountDTO.setPerson(salesManDTO.getName());
          }
        }

        //流水统计
        BusinessAccountDTO oldBusinessAccountDTO =  businessAccountService.getBusinessAccountById(Long.valueOf(businessAccountId));
        businessStatService.statFromBusinessAccountDTO(oldBusinessAccountDTO,businessAccountDTO, com.bcgogo.enums.BusinessAccountEnum.STATUS_UPDATE);

        businessAccountService.saveOrUpdateBusinessCategoryFromDTO(businessAccountDTO);

        //保存营业分类
        String businessCategory = businessAccountDTO.getBusinessCategory();
        CategoryDTO categoryDTO = rfiTxnService.saveCategory(shopId,businessCategory);
        if(categoryDTO !=null){
          businessAccountDTO.setBusinessCategoryId(categoryDTO.getId());
        }

        businessAccountService.updateBusinessAccount(businessAccountDTO);
        //营业外记账统计
        businessAccountService.businessCategoryStatByDTO(oldBusinessAccountDTO, CalculateType.MINUS);
        businessAccountService.businessCategoryStatByDTO(businessAccountDTO, CalculateType.ADD);
      }
      model.addAttribute("businessAccountDTO", businessAccountDTO);

      List<DepartmentDTO> departmentDTOList = ServiceManager.getService(IUserCacheService.class).getDepartmentByName(shopId, "");
      model.addAttribute("departmentDTOList", departmentDTOList);


    } catch (Exception e) {
      LOG.error("/businessAccount.do");
      LOG.error("method=editBusinessAccount");
      LOG.error("shopId:" + WebUtil.getShopId(request) + ",userId:" + WebUtil.getUserId(request));
      LOG.error(e.getMessage(), e);
    }

    return "stat/businessAccountEdit";

  }

  /**
   * 营业外记账删除
   *
   * @param model
   * @param request
   * @param response
   * @throws Exception
   */
  @RequestMapping(params = "method=deleteBusinessAccount")
  @ResponseBody
  public Object deleteBusinessAccount(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
    BusinessAccountDTO businessAccountDTO = null;
    try {
      String businessAccountId = request.getParameter("businessAccountId");
      IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
      if (businessAccountId != null) {

        businessAccountDTO = businessAccountService.deleteBusinessAccountById(Long.valueOf(businessAccountId));

         //流水统计
        businessStatService.statFromBusinessAccountDTO(null,businessAccountDTO, com.bcgogo.enums.BusinessAccountEnum.STATUS_DELETE);
        //营业外记账统计
        businessAccountService.businessCategoryStatByDTO(businessAccountDTO, CalculateType.MINUS);


      }

    } catch (Exception e) {

      LOG.debug("/businessAccount.do");
      LOG.debug("method=deleteBusinessAccount");
      LOG.debug("shopId:" + WebUtil.getShopId(request) + ",userId:" + WebUtil.getUserId(request));
    }
    return businessAccountDTO;


  }


  /**
   * 根据查询条件查询 营业外记账
   *
   * @param request
   * @param response
   */
  @RequestMapping(params = "method=searchBusinessAccount")
  @ResponseBody
  public PagingListResult<BusinessAccountDTO> searchBusinessAccount(HttpServletRequest request, HttpServletResponse response,BusinessAccountSearchConditionDTO searchConditionDTO) throws Exception {
    PagingListResult<BusinessAccountDTO> result = new PagingListResult<BusinessAccountDTO>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return result;
      }

//      String editDateStartStr = request.getParameter("editDateStartStr");
//      String editDateEndStr = request.getParameter("editDateEndStr");
//      String accountCategory = request.getParameter("accountCategory");
//      String docNo = request.getParameter("docNo");
//      String dept = request.getParameter("dept");
//      String person = request.getParameter("person");
      String startPageNo = request.getParameter("startPageNo");

      String moneyCategoryStr = searchConditionDTO.getMoneyCategoryStr();

      MoneyCategory moneyCategory = null;
      if (StringUtils.isNotEmpty(moneyCategoryStr)) {
        if (moneyCategoryStr.contains(MoneyCategory.income.name()) && !moneyCategoryStr.contains(MoneyCategory.expenses.name())) {
          moneyCategory = MoneyCategory.income;
        }
        if (moneyCategoryStr.contains(MoneyCategory.expenses.name()) && !moneyCategoryStr.contains(MoneyCategory.income.name())) {
          moneyCategory = MoneyCategory.expenses;
        }
      }
      searchConditionDTO.setMoneyCategory(moneyCategory);

      Long businessCategoryId = null;
      if (StringUtils.isNotEmpty(searchConditionDTO.getBusinessCategory())) {
        RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
        CategoryDTO categoryDTO = rfiTxnService.getCategoryDTOByName(shopId, searchConditionDTO.getBusinessCategory(), CategoryType.BUSINESS_CLASSIFICATION);
        if (categoryDTO != null) {
          businessCategoryId = categoryDTO.getId();
          searchConditionDTO.setBusinessCategoryId(businessCategoryId);
        }
      }


      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Long editDateStart = null;
      if (StringUtils.isNotBlank(searchConditionDTO.getEditDateStartStr())) {
        editDateStart = dateFormat.parse(searchConditionDTO.getEditDateStartStr()).getTime();
        searchConditionDTO.setStartTime(editDateStart);
      }
      Long editDateEnd = null;
      if (StringUtils.isNotBlank(searchConditionDTO.getEditDateEndStr())) {
        editDateEnd = dateFormat.parse(searchConditionDTO.getEditDateEndStr()).getTime();
        searchConditionDTO.setEndTime(editDateEnd);
      }

      String accountStatus = BusinessAccountEnum.STATUS_SAVE.getName();
      searchConditionDTO.setAccountEnum(BusinessAccountEnum.STATUS_SAVE);

      IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
      //取得查询结果总条数
      List<String> strings = businessAccountService.countBusinessAccountsBySearchCondition(shopId, searchConditionDTO);
      int totalRows = Integer.parseInt(strings.get(0));
      //计算取得收入和支出总金额
      //Double sum = businessAccountService.getSumBySearchCondition(shopId, editDateStart, editDateEnd, accountCategory, docNo, dept, accountStatus, person, null);
      //计算取得收入总金额
      Double incomeSum = 0D;
      //计算取得支出总金额
      Double expensesSum = 0D;
      if (moneyCategory == MoneyCategory.income) {
        incomeSum = NumberUtil.toReserve(Double.valueOf(strings.get(1)), NumberUtil.MONEY_PRECISION);
      } else if (moneyCategory == MoneyCategory.expenses) {
        expensesSum = NumberUtil.toReserve(Double.valueOf(strings.get(1)), NumberUtil.MONEY_PRECISION);
      } else if (moneyCategory == null) {
        searchConditionDTO.setMoneyCategory(MoneyCategory.income);
        incomeSum = businessAccountService.getSumBySearchCondition(shopId, searchConditionDTO);
        searchConditionDTO.setMoneyCategory(MoneyCategory.expenses);
        expensesSum = businessAccountService.getSumBySearchCondition(shopId, searchConditionDTO);
      }
      searchConditionDTO.setMoneyCategory(moneyCategory);


      Double sum = NumberUtil.toReserve(incomeSum - expensesSum, NumberUtil.MONEY_PRECISION);
      Pager pager = new Pager(totalRows, Integer.valueOf(startPageNo), maxRows);

      searchConditionDTO.setMaxRows(maxRows);
      searchConditionDTO.setRowStart(pager.getRowStart());
      // 取得分页结果
      List<BusinessAccountDTO> businessAccountDTOList = businessAccountService.getBusinessAccountsBySearchCondition(shopId, searchConditionDTO);

      //组装金额JSONString 和 搜索条件:为打印准备
      Map<String, String> statistic = new HashMap<String, String>();
      statistic.put("sum", sum.toString());
      statistic.put("incomeSum", incomeSum.toString());
      statistic.put("expensesSum", expensesSum.toString());
      statistic.put("editDateStartStr", StringUtils.isEmpty(searchConditionDTO.getEditDateStartStr()) ? "" : searchConditionDTO.getEditDateStartStr());
      statistic.put("editDateEndStr", StringUtils.isEmpty(searchConditionDTO.getEditDateEndStr()) ? "" : searchConditionDTO.getEditDateEndStr());
      statistic.put("accountCategory", StringUtils.isEmpty(searchConditionDTO.getAccountCategory()) ? "" : searchConditionDTO.getAccountCategory());
      statistic.put("startPageNo", startPageNo);
      statistic.put("moneyCategoryStr", moneyCategoryStr);

      result.setResults(businessAccountDTOList);
      result.setPager(pager);
      result.setData(statistic);

      return result;
    } catch (Exception e) {
      LOG.debug("/businessAccount.do?method=searchBusinessAccount" + "shopId:" + WebUtil.getShopId(request) + ",userId:" + WebUtil.getUserId(request));
      LOG.error(e.getMessage(), e);
      return new PagingListResult<BusinessAccountDTO>();
    }
  }

  /**
   * 取得营业外记账类别   记账类别（固定5项：房租、工资提成、水电杂项、其他、营业外收入）
   * @param model
   * @param shopId
   */
  private void getAccountCategoryDropDownInfo(ModelMap model, Long shopId) {
    IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
    List accountCategoryList = businessAccountService.getBusinessCategoryByItemType(shopId, BusinessAccountEnum.CATEGORY_ACCOUNT.getName());
    model.addAttribute("accountCategoryList", accountCategoryList);

  }


  /**
   * 取得营业外记账营业类别
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @RequestMapping(params = "method=getBusinessCategory")
  @ResponseBody
  public Object getBusinessCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
    CategoryDTO[] categoryDTOArray = txnService.getCategoryByShopId(shopId);
    return categoryDTOArray;
  }

  @RequestMapping(params = "method=getDateToPrint")
  public void getDateToPrint(HttpServletRequest request,HttpServletResponse response)
  {
    String sum = request.getParameter("sum");
    String incomeSum = request.getParameter("incomeSum");
    String expensesSum = request.getParameter("expensesSum");
    String editDateStartStr = request.getParameter("editDateStartStr");
    String editDateEndStr = request.getParameter("editDateEndStr");
    String accountCategory = request.getParameter("accountCategory");
    String dataList = request.getParameter("dataList");
    String pagerStr = request.getParameter("pager");
    List<BusinessAccountDTO> businessAccountDTOList = null;
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);

    String moneyCategoryStr = request.getParameter("moneyCategoryStr");

    String moneyCategory = null;
    if (StringUtils.isNotEmpty(moneyCategoryStr)) {
      if (moneyCategoryStr.contains(MoneyCategory.income.name()) && !moneyCategoryStr.contains(MoneyCategory.expenses.name())) {
        moneyCategory = "收入";
      }
      if (moneyCategoryStr.contains(MoneyCategory.expenses.name()) && !moneyCategoryStr.contains(MoneyCategory.income.name())) {
        moneyCategory = "支出";
      }
    }

    if(StringUtils.isBlank(dataList))
    {
      return;
    }

    try
    {

      businessAccountDTOList = new Gson().fromJson(dataList, new TypeToken<List<BusinessAccountDTO>>() {}.getType());
      Pager pager = new Gson().fromJson(pagerStr,new TypeToken<Pager>() {}.getType());

      Double pageIncome = 0D;
      Double pageExpress = 0D;
      if(CollectionUtils.isNotEmpty(businessAccountDTOList))
      {
        for(BusinessAccountDTO businessAccountDTO : businessAccountDTOList)
        {

          businessAccountDTO.setBusinessAccountStr("");
          if (NumberUtil.doubleVal(businessAccountDTO.getCash()) > 0) {
            businessAccountDTO.setBusinessAccountStr(businessAccountDTO.getBusinessAccountStr() + "现金" + businessAccountDTO.getCash() + " ");
          }
          if (NumberUtil.doubleVal(businessAccountDTO.getCheck()) > 0) {
            businessAccountDTO.setBusinessAccountStr(businessAccountDTO.getBusinessAccountStr() + "支票" + businessAccountDTO.getCheck() + " ");

          }
          if (NumberUtil.doubleVal(businessAccountDTO.getUnionpay()) > 0) {
            businessAccountDTO.setBusinessAccountStr(businessAccountDTO.getBusinessAccountStr() + "银联" + businessAccountDTO.getUnionpay() + " ");

          }

          if(businessAccountDTO.getMoneyCategory().equals(MoneyCategory.income))
          {
            pageIncome +=businessAccountDTO.getTotal();
            businessAccountDTO.setMoneyCategoryStr(MoneyCategory.income.name());
          }
          else
          {
            pageExpress +=businessAccountDTO.getTotal();
            businessAccountDTO.setMoneyCategoryStr(MoneyCategory.expenses.name());
          }
        }
      }

      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.BUSINESS_ACCOUNT);
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if(null != printTemplateDTO)
      {
        byte bytes[]=printTemplateDTO.getTemplateHtml();
        String str = new String(bytes,"UTF-8");

        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库

        StringResourceRepository repo = StringResourceLoader.getRepository();

        String myTemplateName = "businessAccountPrint"+ String.valueOf(WebUtil.getShopId(request));

        String myTemplate =  str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName,"UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("sum", NumberUtil.round(Double.valueOf(sum), NumberUtil.MONEY_PRECISION));
        context.put("incomeSum", "收入" + incomeSum + "元");
        context.put("expensesSum", "支出" + expensesSum + "元");
        context.put("pageIncome", "收入" + NumberUtil.round(pageIncome, NumberUtil.MONEY_PRECISION) + "元");
        context.put("pageExpress", "支出" + NumberUtil.round(pageExpress, NumberUtil.MONEY_PRECISION) + "元");

        if (pageIncome >= pageExpress) {
          context.put("pageIncomeStr", "收入" + Math.abs(NumberUtil.toReserve(pageIncome - pageExpress, NumberUtil.MONEY_PRECISION)) + "元");
        } else {
          context.put("pageIncomeStr", "支出" + Math.abs(NumberUtil.toReserve(pageIncome - pageExpress, NumberUtil.MONEY_PRECISION)) + "元");
        }

        if (NumberUtil.doubleVal(incomeSum) >= NumberUtil.doubleVal(expensesSum)) {
          context.put("sumStr", "收入" + Math.abs(NumberUtil.toReserve(pageIncome - pageExpress, NumberUtil.MONEY_PRECISION)) + "元");
        } else {
          context.put("sumStr", "支出" + Math.abs(NumberUtil.toReserve(pageIncome - pageExpress, NumberUtil.MONEY_PRECISION)) + "元");
        }


        context.put("editDateStartStr",editDateStartStr);
        context.put("moneyCategory",moneyCategory);
        if(StringUtils.isNotBlank(editDateStartStr) && StringUtils.isBlank(editDateEndStr))
        {
          editDateEndStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,System.currentTimeMillis());
        }
        context.put("editDateEndStr",editDateEndStr);
        context.put("accountCategory",accountCategory);
        context.put("businessAccountDTOList",businessAccountDTOList);
        context.put("pager",pager);
        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      }
      else
      {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }

      out.close();

    }
    catch(Exception e)
    {
      LOG.error("method=getDateToPrint");
      LOG.error("businessAccountDTOList{}"+businessAccountDTOList);
      LOG.error(e.getMessage(),e);
    }
  }

  @RequestMapping(params = "method=getBusinessCategoryLikeItemName")
  @ResponseBody
  public Map getBusinessCategoryLikeItemName(HttpServletRequest request, HttpServletResponse response) {
    IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
    Map map = new HashMap();
    Long shopId = WebUtil.getShopId(request);
    if(shopId == null){
      return map;
    }

    List<BusinessCategoryDTO> businessCategoryDTOs = businessAccountService.getBusinessCategoryLikeItemName(shopId, (String) request.getParameter("keyWord"));

    map.put("uuid", (String) request.getParameter("uuid"));
    map.put("data", businessCategoryDTOs);
    return map;
  }

  @RequestMapping(params = "method=getSalesManByDepartmentId")
  @ResponseBody
  public List<SalesManDTO> getSalesManByDepartmentId(HttpServletRequest request,HttpServletResponse response){
    IUserService userService = ServiceManager.getService(IUserService.class);

    String departmentIdStr = request.getParameter("departmentIdStr");
    if(!NumberUtil.isLongNumber(departmentIdStr)){
      return null;
    }
    List<SalesManDTO> salesManDTOList = userService.getSalesManByDepartmentId(WebUtil.getShopId(request),NumberUtil.longValue(departmentIdStr));
    return salesManDTOList;
  }

  @RequestMapping(params = "method=getDepartmentLikeName")
  @ResponseBody
  public Map getDepartmentLikeName(HttpServletRequest request, HttpServletResponse response) {
    Map map = new HashMap();
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return map;
    }
    List<DepartmentDTO> departmentDTOList = ServiceManager.getService(IUserCacheService.class).getDepartmentByName(shopId, request.getParameter("keyWord"));
    map.put("uuid", (String) request.getParameter("uuid"));
    map.put("data", departmentDTOList);
    return map;
  }

}
