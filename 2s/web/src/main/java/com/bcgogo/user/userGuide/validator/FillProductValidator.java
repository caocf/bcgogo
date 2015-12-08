package com.bcgogo.user.userGuide.validator;

import com.bcgogo.common.Pair;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.cache.UserGuideCacheManager;
import com.bcgogo.user.dto.userGuide.UserGuideDTO;
import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;
import com.bcgogo.user.service.IUserGuideService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-8
 * Time: 上午10:05
 */
@Component
public class FillProductValidator extends AbstractValidator {
  private static final Logger LOG = LoggerFactory.getLogger(FillProductValidator.class);

  @Override
  protected UserGuideDTO validate(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, boolean needNext, String... excludeFlowName) throws Exception {
    if (!needNext) return userGuideDTO;
    if ("PRODUCT_ONLINE_GUIDE_GOODS_ONLINE".equals(userGuideDTO.getCurrentStep().getName())) {
      return this.validateFillProductInfo(shopVersionId, shopId, userId, userGuideDTO, excludeFlowName);
    } else if ("PRODUCT_ONLINE_GUIDE_INVENTORY".equals(userGuideDTO.getCurrentStep().getName())) {
      return this.validateFillProductInfoNext(shopVersionId, shopId, userId, userGuideDTO, excludeFlowName);
    } else {
      return defaultStep(shopVersionId, userId, userGuideDTO, shopId, excludeFlowName);
    }
  }

  //进入商品上架维护后的下一步，先判断是否有商品，如果没有，则返回PRODUCT_ONLINE_GUIDE_INVENTORY 这一步，如果有商品，则update当前的historyGuide 并返回PRODUCT_ONLINE_GUIDE_FILL_PRODUCT_INFO
  private UserGuideDTO validateFillProductInfo(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, String... excludeFlowName) throws Exception {
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
    if ("PRODUCT_ONLINE_GUIDE_GOODS_ONLINE".equals(userGuideDTO.getCurrentStep().getName())) {
      IProductService productService = ServiceManager.getService(IProductService.class);
      if (productService.countProductNotInSales(shopId) > 0) {
        userGuideService.updateCurrentUserGuideStepFinished(userId, "PRODUCT_ONLINE_GUIDE", "PRODUCT_ONLINE_GUIDE_INVENTORY");
        //current flow
        Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>> pair = userGuideService.getCurrentUserGuideStep(shopVersionId, userId, excludeFlowName);
        if (pair != null && pair.getKey() != null) {
          userGuideDTO.setCurrentFlow(UserGuideCacheManager.getCurrentUserGuideFlowByName(shopVersionId, pair.getKey().getFlowName()));
          //current step
          userGuideDTO.setCurrentStep(UserGuideCacheManager.getUserGuideStepByName("PRODUCT_ONLINE_GUIDE_INVENTORY").getKey());
          userGuideDTO.setNextStep(pair.getKey());
          return userGuideDTO;
        } else {
          return null;
        }
      } else {
        userGuideService.updateCurrentUserGuideStepFinished(userId, userGuideDTO.getCurrentFlow().getName(), userGuideDTO.getCurrentStep().getName());
        userGuideDTO.setNextStep(userGuideDTO.getNextStepList().get(0));
        return userGuideDTO;
      }
    }
    return userGuideDTO;
  }

  //进入商品上架维护后,本地无商品，去入库，再次登录的时候接着上一步 PRODUCT_ONLINE_GUIDE_INVENTORY，先判断是否有商品，如果没有，则返回PRODUCT_ONLINE_GUIDE_INVENTORY 这一步，如果有商品，则update当前的historyGuide 并返回PRODUCT_ONLINE_GUIDE_FILL_PRODUCT_INFO

  private UserGuideDTO validateFillProductInfoNext(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, String... excludeFlowName) throws Exception {
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
    if ("PRODUCT_ONLINE_GUIDE_INVENTORY".equals(userGuideDTO.getCurrentStep().getName())) {
      IProductService productService = ServiceManager.getService(IProductService.class);
      if (productService.countProductNotInSales(shopId) > 0) {
        userGuideService.updateCurrentUserGuideStepFinished(userId, userGuideDTO.getCurrentFlow().getName(), userGuideDTO.getCurrentStep().getName());
        userGuideDTO.setNextStep(userGuideDTO.getNextStepList().get(0));
          return userGuideDTO;
        } else {
          return null;
        }
      } else {
        return null;
      }
    }
}
