package com.bcgogo.user.userGuide.validator;

import com.bcgogo.user.dto.userGuide.UserGuideDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-4
 * Time: 下午10:43
 */
@Component
public class UserGuideValidator extends AbstractValidator {
  private static final Logger LOG = LoggerFactory.getLogger(UserGuideValidator.class);
  @Autowired
  private ProductPriceGuideValidator productPriceGuideValidator;
  @Autowired
  private CustomerApplyGuideValidator customerApplyGuideValidator;
  @Autowired
  private SupplierApplyGuideValidator supplierApplyGuideValidator;
  @Autowired
  private ContractGuideValidator contractGuideValidator;
  @Autowired
  private FillProductValidator fillProductValidator;
  @Autowired
  private ContractMessageNoticeValidator contractMessageNoticeValidator;

  @Override
  public UserGuideDTO validate(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, boolean needNext, String... excludeFlowName) throws Exception {
    if (userGuideDTO == null || userGuideDTO.getCurrentStep() == null || userGuideDTO.getCurrentFlow() == null) {
      return userGuideDTO;
    }
    //关联
    if ("CONTRACT_CUSTOMER_GUIDE,CONTRACT_SUPPLIER_GUIDE".contains(userGuideDTO.getCurrentStep().getFlowName()))
      return contractGuideValidator.validate(shopVersionId, shopId, userId, userGuideDTO, needNext, excludeFlowName);
      //上架
    else if ("PRODUCT_ONLINE_GUIDE".contains(userGuideDTO.getCurrentStep().getFlowName()))
      return fillProductValidator.validate(shopVersionId, shopId, userId, userGuideDTO, needNext, excludeFlowName);
      //报价
    else if ("PRODUCT_PRICE_GUIDE".contains(userGuideDTO.getCurrentStep().getFlowName()))
      return productPriceGuideValidator.validate(shopVersionId, shopId, userId, userGuideDTO, needNext, excludeFlowName);
      //供应商关联请求
    else if ("SUPPLIER_APPLY_GUIDE".equals(userGuideDTO.getCurrentStep().getFlowName()))
      return supplierApplyGuideValidator.validate(shopVersionId, shopId, userId, userGuideDTO, needNext, excludeFlowName);
      //客户关联请求
    else if ("CUSTOMER_APPLY_GUIDE".equals(userGuideDTO.getCurrentStep().getFlowName()))
      return customerApplyGuideValidator.validate(shopVersionId, shopId, userId, userGuideDTO, needNext, excludeFlowName);
    else if ("CONTRACT_MESSAGE_NOTICE".equals(userGuideDTO.getCurrentStep().getFlowName()))
      return contractMessageNoticeValidator.validate(shopVersionId, shopId, userId, userGuideDTO, needNext, excludeFlowName);
    else {
      if (!needNext) return userGuideDTO;
      return defaultStep(shopVersionId, userId, userGuideDTO, shopId, excludeFlowName);
    }

  }
}
