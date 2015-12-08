package com.bcgogo.payment;

import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.constant.TransactionConstants;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.payment.ChinaPayParamStatus;
import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.enums.txn.finance.ReceivableMethod;
import com.bcgogo.payment.chinapay.ChinaPay;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.payment.dto.TransactionDTO;
import com.bcgogo.payment.model.ChinaPayParamLog;
import com.bcgogo.payment.service.IChinapayService;
import com.bcgogo.payment.service.IPaymentService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.finance.BcgogoReceivableDTO;
import com.bcgogo.txn.service.ISmsRechargeService;
import com.bcgogo.txn.service.finance.IBcgogoReceivableService;
import com.bcgogo.txn.service.payment.IChinapayCheckService;
import com.bcgogo.txn.service.payment.ILoanTransfersService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/chinapay.do")
public class ChinapayController {
  private static final Logger LOG = LoggerFactory.getLogger(ChinapayController.class);

  //本地后台接受银联通知充值结果  短信
  @RequestMapping(params = "method=smsRechargeReceive")
  public void smsRechargeReceive(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    LOG.debug("银联支付后台");
    ChinapayDTO chinapayDTO = ChinaPay.gengerateChinapayDTO(request);
    chinapayDTO.setMessage("ChinaPay call back method for sms recharge.");
    TransactionDTO transactionDTO = chinapayService.receive(chinapayDTO);
    if (transactionDTO == null) {
      return;
    }
    postBackToChinapay(response);
    if (transactionDTO.getReferenceType().equals(TransactionConstants.ReferenceType.REFERENCE_TYPE_SMS_RECHARGE)) {
      smsRechargeService.completeSmsRecharge(transactionDTO.getReferenceId());
    }
  }

  //本地后台接受银联通知充值结果 货款转账
  @RequestMapping(params = "method=loanTransfersReceive")
  public void loanTransfersReceive(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    ILoanTransfersService loanTransfersService = ServiceManager.getService(ILoanTransfersService.class);
    try {
      ChinapayDTO chinapayDTO = ChinaPay.gengerateChinapayDTO(request);
      chinapayDTO.setMessage("ChinaPay call back method for loan transfers.");
      TransactionDTO transactionDTO = chinapayService.receive(chinapayDTO);
      if (transactionDTO == null) {
        return;
      }
      postBackToChinapay(response);
      loanTransfersService.handleLoanTransfersByTransfersNumber(chinapayDTO.getOrdId(), chinapayDTO.getPayStat());
      LOG.info("ChinaPay call back method for loan transfers.");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }


  //本地后台接受银联通知充值结果 软件付款
  @RequestMapping(params = "method=bcgogoSoftwareReceive")
  public void bcgogoSoftwareReceive(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Long ordId = null;
    try {
      ChinapayDTO chinapayDTO = ChinaPay.gengerateChinapayDTO(request);

      chinapayDTO.setMessage("ChinaPay call back method for software.");
      TransactionDTO transactionDTO = chinapayService.receive(chinapayDTO);
      if (transactionDTO == null) {
        return;
      }
      postBackToChinapay(response);
      if (StringUtils.isEmpty(chinapayDTO.getOrdId())) {
        throw new Exception("ordId is null");
      }
      ordId = Long.valueOf(chinapayDTO.getOrdId());
      if (BcgogoConcurrentController.lock(ConcurrentScene.CHINA_PAY,ordId )) {
        ChinaPayParamLog log = paymentService.getChinaPayParamLog(ordId);
        if (ChinaPayParamStatus.EFFECTIVE.equals(log.getChinaPayParamStatus())) {
          BcgogoReceivableDTO dto = log.toSoftwareBcgogoReceivableDTO();
          dto.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
          receivableService.softwareReceivable(dto);
          paymentService.updateChinaPayParamLogChinaPayStatus(log.getOrdId(), ChinaPayParamStatus.BE_USED);
        }
      }
      LOG.info("ChinaPay call back method for software.");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }finally {
      if(ordId!=null)
        BcgogoConcurrentController.release(ConcurrentScene.CHINA_PAY, ordId);
    }
  }

  //本地后台接受银联通知充值结果 软件付款 分期
  @RequestMapping(params = "method=instalmentOnLineReceivable")
  public void instalmentOnLineReceivable(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Long ordId = null;
    try {
      ChinapayDTO chinapayDTO = ChinaPay.gengerateChinapayDTO(request);

      chinapayDTO.setMessage("ChinaPay call back method for software.");
      TransactionDTO transactionDTO = chinapayService.receive(chinapayDTO);
      if (transactionDTO == null) {
        return;
      }
      postBackToChinapay(response);
      if (StringUtils.isEmpty(chinapayDTO.getOrdId())) {
        throw new Exception("ordId is null");
      }
      ordId = Long.valueOf(chinapayDTO.getOrdId());
      if (BcgogoConcurrentController.lock(ConcurrentScene.CHINA_PAY,ordId )) {
        ChinaPayParamLog log = paymentService.getChinaPayParamLog(Long.valueOf(chinapayDTO.getOrdId()));
        if (ChinaPayParamStatus.EFFECTIVE.equals(log.getChinaPayParamStatus())) {
          BcgogoReceivableDTO dto = log.toSoftwareBcgogoReceivableDTO();
          dto.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
          receivableService.instalmentReceivable(dto);
          paymentService.updateChinaPayParamLogChinaPayStatus(log.getOrdId(), ChinaPayParamStatus.BE_USED);
        }
      }
      LOG.info("ChinaPay call back method for software.");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }finally {
      if(ordId!=null)
        BcgogoConcurrentController.release(ConcurrentScene.CHINA_PAY, ordId);
    }
  }

  //本地后台接受银联通知充值结果 硬件付款
  @RequestMapping(params = "method=bcgogoHardwareReceive")
  public void bcgogoHardwareReceive(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Long ordId = null;
    try {
      ChinapayDTO chinapayDTO = ChinaPay.gengerateChinapayDTO(request);
      chinapayDTO.setMessage("ChinaPay call back method for hardware.");
      TransactionDTO transactionDTO = chinapayService.receive(chinapayDTO);
      if (transactionDTO == null) {
        return;
      }
      postBackToChinapay(response);
      if (StringUtils.isEmpty(chinapayDTO.getOrdId())) {
        throw new Exception("ordId is null");
      }
      ordId = Long.valueOf(chinapayDTO.getOrdId());
      if (BcgogoConcurrentController.lock(ConcurrentScene.CHINA_PAY,ordId )) {
        ChinaPayParamLog log = paymentService.getChinaPayParamLog(ordId);
        if (ChinaPayParamStatus.EFFECTIVE.equals(log.getChinaPayParamStatus())) {
          BcgogoReceivableDTO dto = log.toHardwareBcgogoReceivableDTO();
          dto.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
          receivableService.hardwareReceivable(dto);
          paymentService.updateChinaPayParamLogChinaPayStatus(log.getOrdId(), ChinaPayParamStatus.BE_USED);
        }
      }
      LOG.info("ChinaPay call back method for hardware.");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }finally {
      if(ordId!=null)
        BcgogoConcurrentController.release(ConcurrentScene.CHINA_PAY, ordId);
    }
  }

  //本地后台接受银联通知充值结果 合并付款
  @RequestMapping(params = "method=bcgogoCombinedPayReceive")
  public void bcgogoCombinedPayReceive(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Long ordId = null;
    try {
      ChinapayDTO chinapayDTO = ChinaPay.gengerateChinapayDTO(request);

      chinapayDTO.setMessage("ChinaPay call back method for Combined.");
      TransactionDTO transactionDTO = chinapayService.receive(chinapayDTO);
      if (transactionDTO == null) {
        return;
      }
      postBackToChinapay(response);
      if (StringUtils.isEmpty(chinapayDTO.getOrdId())) {
        throw new Exception("ordId is null");
      }
      ordId = Long.valueOf(chinapayDTO.getOrdId());
      if (BcgogoConcurrentController.lock(ConcurrentScene.CHINA_PAY,ordId )) {
        ChinaPayParamLog log = paymentService.getChinaPayParamLog(Long.valueOf(chinapayDTO.getOrdId()));
        if (ChinaPayParamStatus.EFFECTIVE.equals(log.getChinaPayParamStatus())) {
          List<BcgogoReceivableDTO> dtoList = log.toCombinedBcgogoReceivableDTOList();
          if(CollectionUtils.isNotEmpty(dtoList)){
            List<Long> bcgogoReceivableOrderRecordRelationIds = new ArrayList<Long>();
            for(BcgogoReceivableDTO dto : dtoList) {
              bcgogoReceivableOrderRecordRelationIds.add(dto.getBcgogoReceivableOrderRecordRelationId());
            }
            List<BcgogoReceivableDTO> bcgogoReceivableDTOList = receivableService.getBcgogoReceivableDTOByRelationId(bcgogoReceivableOrderRecordRelationIds.toArray(new Long[bcgogoReceivableOrderRecordRelationIds.size()]));
            Map<Long,BcgogoReceivableDTO> bcgogoReceivableDTOMap = new HashMap<Long, BcgogoReceivableDTO>();
            for(BcgogoReceivableDTO bcgogoReceivableDTO :bcgogoReceivableDTOList){
              bcgogoReceivableDTOMap.put(bcgogoReceivableDTO.getBcgogoReceivableOrderRecordRelationId(),bcgogoReceivableDTO);
            }
            for(BcgogoReceivableDTO dto : dtoList) {
              dto.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
              BcgogoReceivableDTO dbBcgogoReceivableDTO = bcgogoReceivableDTOMap.get(dto.getBcgogoReceivableOrderRecordRelationId());
              if(dbBcgogoReceivableDTO!=null){
                dto.setPaymentType(PaymentType.valueOf(dbBcgogoReceivableDTO.getOrderPaymentType()));
                dto.setReceivableMethod(ReceivableMethod.valueOf(dbBcgogoReceivableDTO.getRelationReceivableMethod()));
                if(PaymentType.HARDWARE.equals(dto.getPaymentType())){
                  receivableService.hardwareReceivable(dto);
                }else if(PaymentType.SOFTWARE.equals(dto.getPaymentType()) && ReceivableMethod.INSTALLMENT.equals(dto.getReceivableMethod())){//不会是首次
                  receivableService.instalmentReceivable(dto);
                }else if(PaymentType.SOFTWARE.equals(dto.getPaymentType()) && ReceivableMethod.UNCONSTRAINED.equals(dto.getReceivableMethod())){//
                  receivableService.softwareReceivable(dto);
                }
              }
            }
          }
          paymentService.updateChinaPayParamLogChinaPayStatus(log.getOrdId(), ChinaPayParamStatus.BE_USED);
        }
      }
      LOG.info("ChinaPay call back method for Combined.");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }finally {
      if(ordId!=null)
        BcgogoConcurrentController.release(ConcurrentScene.CHINA_PAY, ordId);
    }
  }


  private void postBackToChinapay(HttpServletResponse response) {
    try {
      response.getWriter().write(ChinaPay.CHECK_SUCCESS_MESSAGE);
    } catch (IOException e) {
      LOG.error("银联支付接收完成后返回状态时异常，可能是网络问题，异常信息[{}]", e.getMessage(), e);
    }
  }

  //BGOGO后台操作操作 手动check所有用户 银联充值失败情况
  @RequestMapping(params = "method=check")
  public void check(HttpServletRequest request, HttpServletResponse response) {
    try {
      String dateTimeStr = request.getParameter("dateTime");
      Long dateTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, dateTimeStr);
      if (dateTime == null) {
        Long timePeriod = Long.valueOf(request.getParameter("timePeriod"));
        if (timePeriod == null) return;
        dateTime = System.currentTimeMillis() - timePeriod * NumberUtil.DAY_TIMEMILLIS;
      }
      IChinapayCheckService chinapayCheckService = ServiceManager.getService(IChinapayCheckService.class);
      LOG.debug("manual payment query, dateTime:" + dateTimeStr + ".............");
      chinapayCheckService.checkChinaPayByShopIdAndTime(null, dateTime);
      PrintWriter writer = response.getWriter();
      String outPutStr = "check success!";
      writer.write(outPutStr);
      writer.close();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

}
