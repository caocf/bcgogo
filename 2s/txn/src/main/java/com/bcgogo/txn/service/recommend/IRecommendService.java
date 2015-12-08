package com.bcgogo.txn.service.recommend;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-17
 * Time: 下午1:17
 * To change this template use File | Settings | File Templates.
 */
public interface IRecommendService {
  void moveProductRecommendToTrace() throws Exception;
  void movePreBuyOrderItemRecommendToTrace() throws Exception;
  void moveShopRecommendToTrace() throws Exception;

  void processAccessoryRecommend() throws Exception;

  void processPreBuyOrderInformationRecommend() throws Exception;

  void processShopRecommend() throws Exception;
}
