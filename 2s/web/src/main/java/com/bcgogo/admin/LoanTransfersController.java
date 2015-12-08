package com.bcgogo.admin;

import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.constant.ChinaPayConstants;
import com.bcgogo.constant.SmsRechargeConstants;
import com.bcgogo.enums.payment.LoanTransfersStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.payment.chinapay.ChinaPay;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.payment.service.IChinapayService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.LoanTransfersDTO;
import com.bcgogo.txn.service.payment.ILoanTransfersService;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-10-22
 * Time: 下午5:33
 * 货款转账
 */
@Controller
@RequestMapping("/loanTransfers.do")
public class LoanTransfersController {
  private static final Logger LOG = LoggerFactory.getLogger(LoanTransfersController.class);
  private static final String EMPTY_MEMO = "无";
    public static final int PAGE_SIZE = 10;//页面显示条数
  public static final int DEFAULT_PAGE_NO = 1;  //默认查询页码 1

  //第一步：进入页面
  @RequestMapping(params = "method=showPage")
  public String showPage(HttpServletRequest request, HttpServletResponse response) {
    ILoanTransfersService loanTransfersService = ServiceManager.getService(ILoanTransfersService.class);
    Long shopId = WebUtil.getShopId(request);
    Long shopVersionId = WebUtil.getShopVersion(request).getId();
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      List<ShopVersionDTO> shopVersionDTOList = ServiceManager.getService(IShopVersionService.class).getAllShopVersion();
      Pager pager = new Pager(loanTransfersService.countLoanTransfersByShopId(shopId), NumberUtil.intValue(request.getParameter("pageNo"), DEFAULT_PAGE_NO), PAGE_SIZE);
      Double totalAmount = loanTransfersService.sumLoanTransfersTotalAmountByShopId(shopId);
      List<LoanTransfersDTO> loanTransfersDTOList = loanTransfersService.getLoanTransfersByShopId(shopId, pager);
      request.setAttribute("pager", pager);
      request.setAttribute("shopVersionDTOList", shopVersionDTOList);
      request.setAttribute("loanTransfersDTOList", loanTransfersDTOList);
      request.setAttribute("totalAmount", NumberUtil.formatDoubleWithComma(totalAmount, "0.0"));
    } catch (Exception e) {
      LOG.error("进入转账:{}", e.getMessage(), e);
    }
    return "/admin/loanTransfers/startTransfer";
  }

  //第二步：转账
  @RequestMapping(params = "method=saveLoan")
  public String saveLoan(HttpServletRequest request, HttpServletResponse response, LoanTransfersDTO loanTransfersDTO) {
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    ILoanTransfersService loanTransfersService = ServiceManager.getService(ILoanTransfersService.class);

    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");
      if (loanTransfersDTO.getAmount() == null) throw new Exception("loan transfers amount is null!");
      loanTransfersDTO.setTransfersTime(System.currentTimeMillis());
      loanTransfersDTO.setShopId(shopId);
      if (StringUtils.isBlank(loanTransfersDTO.getMemo())) {
        loanTransfersDTO.setMemo(EMPTY_MEMO);
      }
      loanTransfersDTO.setStatus(LoanTransfersStatus.LOAN_START);

      loanTransfersDTO = loanTransfersService.createLoanTransfers(loanTransfersDTO);
      if (loanTransfersDTO == null) {
        request.setAttribute("loanTransfersInfo", "转账数据不完整！");
        return showPage(request, response);
      }
      //保存银联付款提交记录，付款单
      ChinapayDTO chinapayDTO = chinapayService.pay(loanTransfersDTO.getId(), NumberUtil.yuanToFen(loanTransfersDTO.getAmount()), shopId, SmsRechargeConstants.CHINA_PAY_LOAN_TRANSFERS,
          ChinaPayConstants.LOAN_TRANSFERS_BG_RET_URL, ChinaPayConstants.LOAN_TRANSFERS_PAGE_RET_URL);
      if (chinapayDTO == null) {
        loanTransfersDTO.setStatus(LoanTransfersStatus.LOAN_FAIL);
        loanTransfersService.updateLoanTransfers(loanTransfersDTO);
        request.setAttribute("loanTransfersInfo", "暂时不能转账！");
        return showPage(request, response);
      }
      //更新充值单状态为已提交银联、更新充值单序号
      loanTransfersDTO.setTransfersNumber(chinapayDTO.getOrdId());
      loanTransfersDTO.setStatus(LoanTransfersStatus.LOAN_IN);
      loanTransfersDTO = loanTransfersService.updateLoanTransfers(loanTransfersDTO);
      //充值单序号
      request.setAttribute("transfersNumber", loanTransfersDTO.getTransfersNumber());
      //提交银联的form表单
      request.setAttribute("chinapayForm", ChinaPay.commitFormOfDefray(chinapayDTO));
      request.setAttribute("chinaPayDTOUnitTest", chinapayDTO);
      return "/admin/loanTransfers/transferring";
    } catch (Exception e) {
      LOG.error("转账失败:{}", e.getMessage(), e);
      request.setAttribute("loanTransfersInfo", "转账失败！");
      return showPage(request, response);
    }
  }

  //银联前台
  //货款转账 充值第三步：充值完成
  @RequestMapping(params = "method=saveLoanComplete")
  public String saveLoanComplete(HttpServletRequest request, HttpServletResponse response, ChinapayDTO chinapayDTO) throws BcgogoException {
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    ILoanTransfersService loanTransfersService = ServiceManager.getService(ILoanTransfersService.class);
    try {
//      ChinapayDTO chinapayDTO = ChinaPay.gengerateChinapayDTO(request);
      chinapayDTO.setMessage("ChinaPay call front page for loan transfers.");
      chinapayDTO = chinapayService.pgReceive(chinapayDTO);
      if (chinapayDTO == null) {
        LOG.warn("ChinaPay post us chinapayDTO is null");
        request.setAttribute("loanTransfersInfo", "充值序号无效！");
      } else {
        LoanTransfersDTO loanTransfersDTO = loanTransfersService.handleLoanTransfersByTransfersNumber(chinapayDTO.getOrdId(), chinapayDTO.getPayStat());
        request.setAttribute("loanTransfersDTO", loanTransfersDTO);
        LOG.info("ChinaPay call front page for loan transfers.");
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return "/admin/loanTransfers/transferred";
  }
}
