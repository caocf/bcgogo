package com.bcgogo.user.userGuide.validator;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ApplyShopSearchCondition;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.exception.PageException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.cache.UserGuideCacheManager;
import com.bcgogo.user.dto.userGuide.UserGuideDTO;
import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;
import com.bcgogo.user.service.IUserGuideService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-8
 * Time: 上午10:11
 */
@Component
public class ContractGuideValidator extends AbstractValidator {
  private static final Logger LOG = LoggerFactory.getLogger(CustomerApplyGuideValidator.class);

  @Override
  protected UserGuideDTO validate(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, boolean needNext, String... excludeFlowName) throws Exception {
    if (!needNext) {
      userGuideDTO.setValidatorSuccess(hasContract(userGuideDTO.getCurrentFlow().getName(), shopId));
      return userGuideDTO;
    }
    if ("CONTRACT_CUSTOMER_GUIDE_SINGLE_APPLY,CONTRACT_CUSTOMER_GUIDE_BATCH_APPLY".contains(userGuideDTO.getCurrentStep().getName())) {
      return this.validateContractCustomerGuideSingleApply(shopVersionId, shopId, userId, userGuideDTO, excludeFlowName);
    } else if ("CONTRACT_SUPPLIER_GUIDE_SINGLE_APPLY,CONTRACT_SUPPLIER_GUIDE_BATCH_APPLY".contains(userGuideDTO.getCurrentStep().getName())) {
      return this.validateContractSupplierGuideSingleApply(shopVersionId, shopId, userId, userGuideDTO, excludeFlowName);
    } else {
      //其他校验是否满足条件
      userGuideDTO.setValidatorSuccess(hasContract(userGuideDTO.getCurrentFlow().getName(), shopId));
      if (!userGuideDTO.isValidatorSuccess()) return userGuideDTO;
      return defaultStep(shopVersionId, userId, userGuideDTO, shopId, excludeFlowName);
    }
  }


  //单个关联结束之后
  //判断是否进入多个关联引导
  private UserGuideDTO validateContractCustomerGuideSingleApply(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, String... excludeFlowName) throws Exception {
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);

    if (CollectionUtil.isEmpty(userGuideDTO.getNextStepList())) {
      LOG.warn("current step name is {} , next step is empty!", userGuideDTO.getCurrentStep().getName());
      return userGuideDTO;
    }
    if (hasContractCustomer(shopId)) {
      return defaultStep(shopVersionId, userId, userGuideDTO, shopId, excludeFlowName);
    } else {
      userGuideService.updateCurrentUserGuideStepFinished(userId, "CONTRACT_CUSTOMER_GUIDE", "CONTRACT_CUSTOMER_GUIDE_BATCH_APPLY");
      UserGuideStepDTO stepDTO = UserGuideCacheManager.getUserGuideStepByName("CONTRACT_CUSTOMER_GUIDE_BATCH_APPLY").getKey();
      userGuideDTO.setCurrentFlow(UserGuideCacheManager.getCurrentUserGuideFlowByName(shopVersionId, stepDTO.getFlowName()));
      userGuideDTO.setCurrentStep(stepDTO);
      userGuideDTO.setNextStep(UserGuideCacheManager.getUserGuideStepByName(stepDTO.getNextStep()).getKey());
      return userGuideDTO;
    }
  }

  //单个关联结束之后
  //判断是否进入多个关联引导
  private UserGuideDTO validateContractSupplierGuideSingleApply(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, String... excludeFlowName) throws Exception {
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);

    if (CollectionUtil.isEmpty(userGuideDTO.getNextStepList())) {
      LOG.warn("current step name is {} , next step is empty!", userGuideDTO.getCurrentStep().getName());
      return userGuideDTO;
    }
    if (hasContractSupplier(shopId)) {
      return defaultStep(shopVersionId, userId, userGuideDTO, shopId, excludeFlowName);
    } else {
      userGuideService.updateCurrentUserGuideStepFinished(userId, "CONTRACT_SUPPLIER_GUIDE", "CONTRACT_SUPPLIER_GUIDE_BATCH_APPLY");
      UserGuideStepDTO stepDTO = UserGuideCacheManager.getUserGuideStepByName("CONTRACT_SUPPLIER_GUIDE_BATCH_APPLY").getKey();
      userGuideDTO.setCurrentFlow(UserGuideCacheManager.getCurrentUserGuideFlowByName(shopVersionId, stepDTO.getFlowName()));
      userGuideDTO.setCurrentStep(stepDTO);
      userGuideDTO.setNextStep(UserGuideCacheManager.getUserGuideStepByName(stepDTO.getNextStep()).getKey());
      return userGuideDTO;
    }
  }

  private boolean hasContract(String flowName, long shopId) throws PageException {
    if ("CONTRACT_CUSTOMER_GUIDE".equals(flowName)) {
      return hasContractCustomer(shopId);
    } else if ("CONTRACT_SUPPLIER_GUIDE".equals(flowName)) {
      return hasContractSupplier(shopId);
    }
    return false;
  }

  private boolean hasContractCustomer(long shopId) throws PageException {
    boolean isContractCustomerGuideBatchApply = false;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IApplyService applyService = ServiceManager.getService(IApplyService.class);
    ApplyShopSearchCondition searchCondition = new ApplyShopSearchCondition();
    searchCondition.setShopId(shopId);
    int currentPage = NumberUtil.intValue(NumberUtil.intValue(1));
    ShopDTO shopDTO = configService.getShopById(shopId);
    searchCondition.setShopAreaId(shopDTO.getAreaId());
    boolean isTestShop = applyService.isTestShop(shopDTO);
    searchCondition.setProvinceNo(shopDTO.getProvince());
    //系统推荐给客户的批发商店铺版本ID
    String shopVersionIdStr = configService.getConfig("ShopVersionRecommendedToWholesalers", ShopConstant.BC_SHOP_ID);
    Integer total = applyService.countApplyCustomerShop(searchCondition, shopVersionIdStr, isTestShop);
    Pager pager = new Pager(total, currentPage, 10);
    List<ApplyShopSearchCondition> shopDTOs = applyService.searchApplyCustomerShop(searchCondition, shopVersionIdStr, pager, isTestShop);
    for (ApplyShopSearchCondition applyShopSearchCondition : shopDTOs) {
      if (applyShopSearchCondition.getInviteStatus() == null) {
        isContractCustomerGuideBatchApply = true;
        break;
      }
    }
    return isContractCustomerGuideBatchApply;
  }

  private boolean hasContractSupplier(long shopId) throws PageException {
    boolean isContractSupplierGuideBatchApply = false;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IApplyService applyService = ServiceManager.getService(IApplyService.class);
    ApplyShopSearchCondition searchCondition = new ApplyShopSearchCondition();
    searchCondition.setShopId(shopId);
    int currentPage = NumberUtil.intValue(NumberUtil.intValue(1));
    ShopDTO shopDTO = configService.getShopById(shopId);
    searchCondition.setShopAreaId(shopDTO.getAreaId());
    boolean isSTestShop = applyService.isTestShop(shopDTO);
    //系统推荐给客户的批发商店铺版本ID
    String shopVersionIdStr = configService.getConfig("ShopVersionRecommendedToCustomers", ShopConstant.BC_SHOP_ID);
    Integer total = applyService.countApplySupplierShop(searchCondition, shopVersionIdStr, isSTestShop);
    Pager pager = new Pager(total, currentPage, 10);
    List<ApplyShopSearchCondition> shopDTOs = applyService.searchApplySupplierShop(searchCondition, shopVersionIdStr, pager, isSTestShop);
    for (ApplyShopSearchCondition applyShopSearchCondition : shopDTOs) {
      if (applyShopSearchCondition.getInviteStatus() == null) {
        isContractSupplierGuideBatchApply = true;
        break;
      }
    }
    return isContractSupplierGuideBatchApply;
  }


}
