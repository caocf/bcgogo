package com.bcgogo.customer;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.StatUtil;
import com.bcgogo.user.dto.CouponConsumeRecordDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.service.IConsumingService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StatConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by LiTao on 2015/11/7.
 */
@Controller
@RequestMapping("/couponConsume.do")
public class CouponConsumeController {
    public static final Logger LOG = LoggerFactory.getLogger(CouponConsumeController.class);

    public static final int PAGE_SIZE = 10;//页面显示条数
    public static final int DEFAULT_PAGE_NO = 1;  //默认查询页码 1

    @Autowired
    private UserDaoManager userDaoManager;

    @Autowired
    private StatUtil statUtil;

    @RequestMapping(params = "method=couponConsumeStat")
    public String couponConsumeStat(ModelMap model, HttpServletRequest request) throws Exception {
        try {
            String startTimeStr = DateUtil.getFirtDayOfMonth();
            String endTimeStr = DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DAY);
            model.addAttribute("startTimeStr", startTimeStr);
            model.addAttribute("endTimeStr", endTimeStr);
        } catch (Exception e) {
            LOG.error("/couponConsume.do method=couponConsumeStat");
            LOG.error(
                    "shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute(
                            "userId"));
            LOG.error(e.getMessage(), e);
        }
        return "stat/couponConsumeStatistics";
    }

    /**
     * 获取一页的代金券收入记录
     * @param request
     * @param orderSearchConditionDTO
     * @param startPageNo   开始页
     * @param maxRows       每页最大行数
     * @return  返回 代金券消费记录列表，pager，代金券消费记录总数，代金券金额总和
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(params = "method=couponConsumeIncome")
    public Object couponConsumeIncome(HttpServletRequest request, OrderSearchConditionDTO orderSearchConditionDTO, int startPageNo, int maxRows)throws Exception{
        try{
            IConsumingService consumingService= ServiceManager.getService(IConsumingService.class);
            List<Object> returnList=new ArrayList<Object>();
            Long shopId = (Long) request.getSession().getAttribute("shopId");
            if (NumberUtil.longValue(shopId) < 0) {
                return "/";
            }
            Long startTime=orderSearchConditionDTO.getStartTime();
            Long endTime=orderSearchConditionDTO.getEndTime();
            if (startTime == null || endTime == null) {
                startTime = DateUtil.getFirstDayDateTimeOfMonth();
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.setTimeInMillis(startTime);
                calendar.add(Calendar.MONTH, 1);
                endTime = calendar.getTimeInMillis();
            }
            String product=orderSearchConditionDTO.getOrderTypeStr();
            String orderType="APP_ONFIELD_ORDER";
            String customerInfo=orderSearchConditionDTO.getCustomerInfo();
            String incomeType="EXPENSES";
            String arrayType=null;
            CouponConsumeRecordDTO couponConsumeRecordDTO=new CouponConsumeRecordDTO();
            couponConsumeRecordDTO.setShopId(shopId);
            couponConsumeRecordDTO.setOrderTypes(orderType);
            couponConsumeRecordDTO.setProduct(product);
            couponConsumeRecordDTO.setCustomerInfo(customerInfo);
            couponConsumeRecordDTO.setIncomeType(incomeType);
            List<String> countAndSum=new ArrayList<String>();
            List<CouponConsumeRecordDTO> couponConsumeRecordDTOs=new ArrayList<CouponConsumeRecordDTO>();
//            Long shopId=null;
//            Long startTime=null;
//            Long endTime=null;
//            String cInfo=null;
//            String IncomeType=null;
//            String arrayType=null;

            try {
                countAndSum = consumingService.countConsumingRecordAndSumCoupon(startTime,  endTime, couponConsumeRecordDTO);
            }catch (Exception e) {
                LOG.debug("获取代金券消费记录列表出错 shopId:" + request.getSession().getAttribute("shopId"));
                LOG.error(e.getMessage(), e);
            }
            int size = 0;
            double sumCoupon=0.0;
            if (countAndSum != null && countAndSum.size() == StatConstant.TWO_QUERY_SIZE) {
                size = (NumberUtil.intValue(countAndSum.get(0)));
                sumCoupon=NumberUtil.doubleValue(countAndSum.get(1), 0);
            }
            Pager pager=statUtil.getPager(size, startPageNo, maxRows);
            if (size > 0) {
                couponConsumeRecordDTOs = consumingService.getConsumingRecordListByPagerTimeArrayType(startTime,  endTime, couponConsumeRecordDTO, arrayType, pager);
            }
//            returnList.add(countAndSum);
            returnList.add(couponConsumeRecordDTOs);
            returnList.add(sumCoupon);
            returnList.add(size);
            returnList.add(pager);
            return returnList;
        }
        catch(Exception e){
            LOG.error("/couponConsume.do method=couponConsumeIncome");
            LOG.error(
                    "shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute(
                            "userId"));
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
    /**
     * 获取一页的代金券支出记录
     * @param request
     * @param orderSearchConditionDTO
     * @param startPageNo   开始页
     * @param maxRows       每页最大行数
     * @return  返回 代金券消费记录列表，pager，代金券消费记录总数，代金券金额总和
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(params = "method=couponConsumeExpenses")
    public Object couponConsumeExpenses(HttpServletRequest request, OrderSearchConditionDTO orderSearchConditionDTO, int startPageNo, int maxRows)throws Exception{
        try{
            IConsumingService consumingService = ServiceManager.getService(IConsumingService.class);
            List<Object> returnList=new ArrayList<Object>();
            Long shopId = (Long) request.getSession().getAttribute("shopId");
            if (NumberUtil.longValue(shopId) < 0) {
                return "/";
            }
            Long startTime=orderSearchConditionDTO.getStartTime();
            Long endTime=orderSearchConditionDTO.getEndTime();
            if (startTime == null || endTime == null) {
                startTime = DateUtil.getFirstDayDateTimeOfMonth();
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.setTimeInMillis(startTime);
                calendar.add(Calendar.MONTH, 1);
                endTime = calendar.getTimeInMillis();
            }
            String product="";
            String customerInfo="";
            String incomeType="INCOME";
            String arrayType=null;
            CouponConsumeRecordDTO couponConsumeRecordDTO=new CouponConsumeRecordDTO();
            couponConsumeRecordDTO.setShopId(shopId);
            couponConsumeRecordDTO.setProduct(product);
            couponConsumeRecordDTO.setCustomerInfo(customerInfo);
            couponConsumeRecordDTO.setIncomeType(incomeType);
            List<String> countAndSum=new ArrayList<String>();
            List<CouponConsumeRecordDTO> couponConsumeRecordDTOs=new ArrayList<CouponConsumeRecordDTO>();
//            Long shopId=null;
//            Long startTime=null;
//            Long endTime=null;
//            String cInfo=null;
//            String IncomeType=null;
//            String arrayType=null;

            try {
                countAndSum = consumingService.countConsumingRecordAndSumCoupon(startTime,  endTime, couponConsumeRecordDTO);
            }catch (Exception e) {
                LOG.debug("获取代金券消费记录列表出错 shopId:" + request.getSession().getAttribute("shopId"));
                LOG.error(e.getMessage(), e);
            }
            int size = 0;
            double sumCoupon=0.0;
            if (countAndSum != null && countAndSum.size() == StatConstant.TWO_QUERY_SIZE) {
                size = (NumberUtil.intValue(countAndSum.get(0)));
                sumCoupon=NumberUtil.doubleValue(countAndSum.get(1), 0);
            }
            Pager pager=statUtil.getPager(size, startPageNo, maxRows);
            if (size > 0) {
                couponConsumeRecordDTOs = consumingService.getConsumingRecordListByPagerTimeArrayType(startTime,  endTime, couponConsumeRecordDTO, arrayType, pager);
            }
//            returnList.add(countAndSum);
            returnList.add(couponConsumeRecordDTOs);
            returnList.add(sumCoupon);
            returnList.add(size);
            returnList.add(pager);
            return returnList;
        }
        catch(Exception e){
            LOG.error("/couponConsume.do method=couponConsumeExpenses");
            LOG.error(
                    "shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute(
                            "userId"));
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 代金券管理页面中，作废代金券消费记录
     * 需要从request获取参数consumingRecordId
     * @param request
     * @return
     */
    @RequestMapping(params = "method=couponConsumeRepeal")
    public String couponConsumeRepeal(HttpServletRequest request) {
        try {
            IConsumingService consumingService = ServiceManager.getService(IConsumingService.class);
            Long shopId = (Long) request.getSession().getAttribute("shopId");
            Long consumingRecordId = null;
            Long orderId = null;
            CouponConsumeRecordDTO couponConsumeRecordDTO = null;
            if (NumberUtil.longValue(shopId) < 0) {
                return "/";
            }
            if (!StringUtils.isBlank(request.getParameter("consumingRecordId"))) {
                consumingRecordId = Long.parseLong(request.getParameter("consumingRecordId"));
            }
            if (consumingRecordId != null) {
                couponConsumeRecordDTO = consumingService.getCouponConsumeRecordById(consumingRecordId);
            }
            if (couponConsumeRecordDTO != null) {
                //判断是否存在代金券消费记录，且状态为未作废的
                if (couponConsumeRecordDTO != null && !OrderStatus.REPEAL.equals(couponConsumeRecordDTO.getOrderStatus())) {
                    orderId = couponConsumeRecordDTO.getOrderId();
                    //如果已存在订单号，则直接通过订单号作废该订单
                    if (orderId != null) {
                        if ("洗车美容".equals(couponConsumeRecordDTO.getProduct())) {
                            return "redirect:/washBeauty.do?method=washBeautyOrderRepeal&washBeautyOrderId=" + orderId.toString();
                        } else if ("施工销售".equals(couponConsumeRecordDTO.getProduct())) {
                            return "redirect:/txn.do?method=repairOrderRepeal&repairOrderId=" + orderId.toString();
                        }
                        //类型错误的不予处理
                        else {
                            return "redirect:/couponConsume.do?method=couponConsumeStat";
                        }
                    }
                    //订单号空白的消费记录，直接作废
                    consumingService.consumingRecordRepeal(shopId, consumingRecordId);
                }
            }
        }catch (Exception e){
            LOG.error("/couponConsume.do method=couponConsumeStat");
            LOG.error(
                    "shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute(
                            "userId"));
            LOG.error(e.getMessage(), e);
        }
        return "redirect:/couponConsume.do?method=couponConsumeStat";
    }

    /**
     * 空白单据的作废
     * 需要从request获取参数consumingRecordId
     * @param request
     * @return
     */
    @RequestMapping(params = "method=blankOrderRepeal")
    public String blankOrderRepeal(HttpServletRequest request) {
        try{
            IConsumingService consumingService = ServiceManager.getService(IConsumingService.class);
            Long shopId = (Long) request.getSession().getAttribute("shopId");
            Long consumingRecordId = null;
            Long orderId = null;
            CouponConsumeRecordDTO couponConsumeRecordDTO = null;
            if (NumberUtil.longValue(shopId) < 0) {
                return "/";
            }
            if (!StringUtils.isBlank(request.getParameter("consumingRecordId"))) {
                consumingRecordId = Long.parseLong(request.getParameter("consumingRecordId"));
            }
            if (consumingRecordId != null) {
                couponConsumeRecordDTO = consumingService.getCouponConsumeRecordById(consumingRecordId);
            }
            if (couponConsumeRecordDTO != null) {
                //判断是否存在代金券消费记录，且状态为未作废的
                if (couponConsumeRecordDTO != null && !OrderStatus.REPEAL.equals(couponConsumeRecordDTO.getOrderStatus())) {
                    orderId = couponConsumeRecordDTO.getOrderId();
                    //如果已存在订单号，则直接通过订单号作废该订单
                    if (orderId != null) {
                        if ("洗车美容".equals(couponConsumeRecordDTO.getProduct())) {
                            return "redirect:/washBeauty.do?method=washBeautyOrderRepeal&washBeautyOrderId=" + orderId.toString();
                        } else if ("施工销售".equals(couponConsumeRecordDTO.getProduct())) {
                            return "redirect:/txn.do?method=repairOrderRepeal&repairOrderId=" + orderId.toString();
                        }
                        //类型错误的不予处理，返回空白单据页面
                        else{
                            return "redirect:/customer.do?method=carindex";
                        }
                    }
                    //订单号空白的消费记录，直接作废
                    consumingService.consumingRecordRepeal(shopId,consumingRecordId);
                }
            }
        }catch (Exception e){
            LOG.error("/couponConsume.do method=blankOrderRepeal");
            LOG.error(
                    "shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute(
                            "userId"));
            LOG.error(e.getMessage(), e);
        }
        return "redirect:/customer.do?method=carindex";
    }
}
