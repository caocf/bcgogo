package com.bcgogo.smsrecharge;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopBalanceService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.constant.ChinaPayConstants;
import com.bcgogo.constant.SmsRechargeConstants;
import com.bcgogo.enums.PaymentWay;
import com.bcgogo.enums.RechargeMethod;
import com.bcgogo.enums.sms.StatType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.generator.SmsRechargeDTOGenerator;
import com.bcgogo.payment.chinapay.ChinaPay;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.payment.service.IChinapayService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.SmsRechargeCompleteDTO;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.txn.dto.finance.SmsRecordSearchCondition;
import com.bcgogo.txn.model.PreferentialPolicy;
import com.bcgogo.txn.service.ISmsRechargeService;
import com.bcgogo.txn.service.finance.ISmsAccountService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-20
 * Time: 下午8:32
 */

@Controller
@RequestMapping("/smsrecharge.do")
public class SmsRechargeController {
  private static final Logger LOG = LoggerFactory.getLogger(SmsRechargeController.class);
    /**
     * 短信充值第一步：进入充值页面
     * @param request
     * @return
     * @throws BcgogoException
     */
  @RequestMapping(params = "method=smsrecharge")
  public String SMSRecharge(HttpServletRequest request) throws BcgogoException {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    if (shopId == null || shopId.equals(0L)) {
      return "/";
    }
    //充值金额
    try {
      double rechargeAmount = NumberUtil.doubleValue(request.getParameter("rechargeamount"), 0);
      request.setAttribute("rechargeAmount", rechargeAmount);
    } catch (Exception e) {
      LOG.debug("/smsrecharge.do");
      LOG.debug("method=smsrecharge");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error("failed to parse rechargeAmount !");
      LOG.error(e.getMessage(),e);
    }
    int currentPage = NumberUtil.intValue(request.getParameter("pageNo"), 1);
    Pager pager = new Pager(currentPage);

    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
    //短信余额、充值总金额
    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
    if (shopBalanceDTO != null) {
      request.setAttribute("smsBalance", shopBalanceDTO.getSmsBalance());
      request.setAttribute("rechargeTotal", shopBalanceDTO.getRechargeTotal());
    }
    //记录数
    int totalCount = smsRechargeService.countShopSmsRecharge(shopId);
    request.setAttribute("recordCount", totalCount);
    //分页充值记录列表
    if(totalCount > 0){
        List<SmsRechargeDTO> smsRechargeDTOList = smsRechargeService.getShopSmsRechargeList(shopId, pager.getCurrentPage(), pager.getPageSize());
        request.setAttribute("smsRechargeDTOList", smsRechargeDTOList);
    }
    pager = new Pager(totalCount, pager.getCurrentPage());
    request.setAttribute("pager", pager);
    //组装充值金额以及赠送金额，前台默认显示100  500   1000 这3种充值金额,按照金额的大小排序
    List<Double> defaultRechargeAmounts = new ArrayList<Double>();
    defaultRechargeAmounts.add(100.0);
    defaultRechargeAmounts.add(500.0);
    defaultRechargeAmounts.add(1000.0);
    List<PreferentialPolicy> preferentialPolicyList = new ArrayList<PreferentialPolicy>();
    Result result = smsRechargeService.getSmsPreferentialPolicy();
    if(result != null && result.getData() != null) {
      preferentialPolicyList = (List<PreferentialPolicy>)result.getData();
      request.setAttribute("hasPreferentialPolicy",true);
    }
    for(PreferentialPolicy preferentialPolicy : preferentialPolicyList) {
      defaultRechargeAmounts.remove(preferentialPolicy.getRechargeAmount());
    }
    for(int i = 0; i< defaultRechargeAmounts.size(); i++) {
      PreferentialPolicy preferentialPolicy = new PreferentialPolicy();
      preferentialPolicy.setRechargeAmount(defaultRechargeAmounts.get(i));
      preferentialPolicy.setPresentAmount(0.0);
      preferentialPolicyList.add(preferentialPolicy);
    }
    Collections.sort(preferentialPolicyList);
    request.setAttribute("preferentialPolicyList",preferentialPolicyList);
    //优惠策略的图片
    if(request.getAttribute("hasPreferentialPolicy") != null) {
      String url = ImageUtils.generateUpYunImagePath(ConfigUtils.getSmsPreferentialPolicyImagePath(),null);
      request.setAttribute("preferentialPolicyImageURL",url);
    }
    return "/smsrecharge/smsrecharge";
  }

    /**
     * 短信充值第二步：充值
     * @param request
     * @return
     * @throws BcgogoException
     */
  @RequestMapping(params = "method=smsrecharging")
  public String SMSRecharging(HttpServletRequest request) throws BcgogoException {
    if (request.getSession().getAttribute("shopId") == null || request.getSession().getAttribute("userId") == null) {
      return "/";
    }
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    //店铺ID
    long shopId = (Long)request.getSession().getAttribute("shopId");
    //充值金额
    double rechargeAmount = NumberUtil.doubleValue(request.getParameter("rechargeamount"), 0);
    //赠送金额
    double presentAmount = NumberUtil.doubleValue(request.getParameter("presentAmount"), 0);
    if(rechargeAmount <= 0){
        request.setAttribute("info", "充值金额无效！");
        return SMSRecharge(request);
    }
    request.setAttribute("rechargeAmount", rechargeAmount);
    //获取短信余额、充值总金额
    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
    if (shopBalanceDTO != null) {
      request.setAttribute("smsBalance", shopBalanceDTO.getSmsBalance());
      request.setAttribute("rechargeTotal", shopBalanceDTO.getRechargeTotal());
    }
    //充值时间
    request.setAttribute("rechargeTime", DateUtil.dateToStr(Calendar.getInstance().getTime()));
    //保存充值记录
    SmsRechargeDTO smsRechargeDTO = this.smsRechargeDTOGenerator.generate(request, shopBalanceDTO);
    smsRechargeDTO.setRechargeMethod(RechargeMethod.CUSTOMER_RECHARGE);
    smsRechargeDTO.setPaymentWay(PaymentWay.CHINA_PAY);
    smsRechargeDTO = smsRechargeService.createSmsRecharge(smsRechargeDTO);
    //保存银联付款提交记录，付款单
    ChinapayDTO chinapayDTO = chinapayService.pay(smsRechargeDTO.getId(), NumberUtil.yuanToFen(rechargeAmount), shopId, SmsRechargeConstants.CHINA_PAY_ORDER_DEC_SMS, ChinaPayConstants.SMS_BG_RET_URL, ChinaPayConstants.SMS_PAGE_RET_URL);
    if(smsRechargeDTO == null || chinapayDTO == null){
       request.setAttribute("info", "暂时不能充值！");
       return SMSRecharge(request);
    }
    //更新充值单状态为已提交银联、更新充值单序号
    smsRechargeDTO.setRechargeNumber(chinapayDTO.getOrdId());
    smsRechargeDTO.setState(SmsRechargeConstants.RechargeState.RECHARGE_STATE_COMMIT);
    smsRechargeDTO = smsRechargeService.updateSmsRecharge(smsRechargeDTO);
    //充值单序号
    request.setAttribute("rechargeNumber", smsRechargeDTO.getRechargeNumber());
    //提交银联的form表单
    request.setAttribute("chinapayForm", ChinaPay.commitFormOfDefray(chinapayDTO));
    return "/smsrecharge/smsrecharging";
  }

    /**
     * 短信充值第三步：充值完成
     * @param request
     * @return
     */
  @RequestMapping(params = "method=smsrechargecomplete")
  public String smsRechargeComplete(HttpServletRequest request) throws BcgogoException {
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    smsRechargeService.updateSmsRechargePayTime(Calendar.getInstance().getTimeInMillis(), request.getParameter("OrdId"));
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    ChinapayDTO chinapayDTO = ChinaPay.gengerateChinapayDTO(request);
    chinapayService.pgReceive(chinapayDTO);
    if(LOG.isDebugEnabled()){
        LOG.debug("chinapayDTO.getOrdId() : " + chinapayDTO.getOrdId());
    }
    return rechargeComplete(request, chinapayDTO.getOrdId());
  }

  @RequestMapping(params = "method=smsrechargejump")
  public String smsRechargeJump(HttpServletRequest request) throws BcgogoException {
    return rechargeComplete(request, request.getParameter("rechargenumber"));
  }

  public String rechargeComplete(HttpServletRequest request, String rechargeNumber) throws BcgogoException {
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    int currentPage = NumberUtil.intValue(request.getParameter("pageNo"), 1);
    int pageSize = NumberUtil.intValue(request.getParameter("pageSize"), 10);
    Pager pager = new Pager(currentPage, pageSize, true);
    request.setAttribute("pageNo", pager.getCurrentPage());
    request.setAttribute("pageSize", pager.getPageSize());
    try {
        LOG.debug("rechargeNumber : " + rechargeNumber);
      SmsRechargeDTO smsRechargeDTO = smsRechargeService.getSmsRechargeByRechargeNumber(rechargeNumber);
        LOG.debug("smsRechargeDTO : " + smsRechargeDTO);
      request.setAttribute("smsRechargeDTO", smsRechargeDTO);
      request.setAttribute("rechargeNumber", rechargeNumber);
      if (smsRechargeDTO != null) {
        request.getSession(true).setAttribute("userId", smsRechargeDTO.getUserId());
        request.getSession(true).setAttribute("shopId", smsRechargeDTO.getShopId());
        SmsRechargeCompleteDTO smsRechargeCompleteDTO = smsRechargeService.getSmsRechargeCompleteInfo(smsRechargeDTO, pager.getCurrentPage(), pager.getPageSize());
        if(smsRechargeCompleteDTO != null){
            //用户姓名
            request.getSession(true).setAttribute("userName", smsRechargeCompleteDTO.getUserName());
            //店铺名称
            request.getSession(true).setAttribute("shopName", smsRechargeCompleteDTO.getShopName());
            //短信余额
            request.setAttribute("smsBalance", smsRechargeCompleteDTO.getSmsBalance());
            //充值总额
            request.setAttribute("rechargeTotal", smsRechargeCompleteDTO.getRechargeTotal());
            //充值记录总数
            request.setAttribute("recordCount", smsRechargeCompleteDTO.getRechargeHistoryTotal());
            //充值记录列表
            request.setAttribute("smsRechargeDTOList", smsRechargeCompleteDTO.getSmsRechargeDTOList());
        }
      } else {
        request.setAttribute("info", "充值序号无效！");
      }
    } catch (Exception e) {
      LOG.debug("/smsrecharge.do");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("rechargeNumber:" + rechargeNumber);
      LOG.error(e.getMessage(), e);
      request.setAttribute("info", "访问失败！");
    } finally {
      return "/smsrecharge/smsrechargecomplete";
    }
  }

  @RequestMapping(params = "method=getShopSmsAccount")
  @ResponseBody
  public Object getShopSmsAccount11(HttpServletRequest request, SmsRecordSearchCondition condition) {
    Long shopId = (Long)request.getSession().getAttribute("shopId");
    ISmsAccountService smsAccountService = ServiceManager.getService(ISmsAccountService.class);
    Map<String,Object> data = new HashMap<String, Object>();
    try {
      if(shopId == null) throw new Exception("shopId is null");
      List<Long> shopIds = new ArrayList<Long>();
      shopIds.add(shopId);
      condition.setShopIds(shopIds);
      condition.setLimit(condition.getMaxRows());
      condition.setStart(condition.getMaxRows() * (condition.getStartPageNo() - 1));
      condition.setStatType(StatType.ONE_TIME);
      Result result = smsAccountService.searchShopSmsRecordResult(condition);
      if(result != null && result.getData() != null) {
        data.put("shopSmsRecordList",result.getData());
        Pager pager = new Pager(result.getTotal(), condition.getStartPageNo(), condition.getMaxRows());
        data.put("pager",pager);
      }
      return data;
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    return null;
  }

  @RequestMapping(params = "method=shopSmsAccount")
  public String shopSmsAccount(HttpServletRequest request) {
    Long shopId = (Long)request.getSession().getAttribute("shopId");
    try {
      if(shopId == null) throw new Exception("shopId is null");
      Map<String,Double> result = new HashMap<String,Double>();
      ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
      result = smsRechargeService.shopSmsAccountStatistic(shopId);
      ShopBalanceDTO shopBalanceDTO = ServiceManager.getService(IShopBalanceService.class).getSmsBalanceByShopId(shopId);
      if(shopBalanceDTO != null) {
        result.put("smsBalance",shopBalanceDTO.getSmsBalance());
      } else {
        result.put("smsBalance",0.0);
      }
      request.setAttribute("result",result);
    } catch (Exception e) {
       LOG.error(e.getMessage(),e);
    }
    return "/smsrecharge/shopSmsAccount";
  }

  @Autowired
  private SmsRechargeDTOGenerator smsRechargeDTOGenerator;

  public void setSmsRechargeDTOGenerator(SmsRechargeDTOGenerator smsRechargeDTOGenerator) {
    this.smsRechargeDTOGenerator = smsRechargeDTOGenerator;
  }
}
