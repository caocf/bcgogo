package com.bcgogo.constant.pushMessage;

public class PushMessagePromptTemplate {
  public static final String QUOTED_BUYING_INFORMATION_TITLE = "报价信息";
  public static final String QUOTED_BUYING_INFORMATION_PROMPT_CONTENT = "来自 <span style=\"color:#999999\">%s</span> 的卖家 <span style=\"color:#007CDA\">%s</span> 刚刚给本店的求购报价了！";

  public static final String BUYING_INFORMATION_TITLE = "客户求购商机信息";
  public static final String BUYING_INFORMATION_PROMPT_CONTENT = "来自 <span style=\"color:#999999\">%s</span> 的买家 <span style=\"color:#007CDA\">%s</span> 刚刚发布了一条求购信息！<br>求购商品信息：<span style=\"color:#007CDA\">%s</span>";

  public static final String BUSINESS_CHANCE_LACK_TITLE = "客户缺料商机信息";
  public static final String BUSINESS_CHANCE_LACK_PROMPT_CONTENT = "来自 <span style=\"color:#999999\">%s</span> 的店铺 <span style=\"color:#007CDA\">%s</span> 的商品 <span style=\"color:#007CDA\">%s</span> 缺料了，赶快给他报价吧！";

  public static final String BUSINESS_CHANCE_SELL_WELL_TITLE = "客户畅销品商机信息";
  public static final String BUSINESS_CHANCE_SELL_WELL_PROMPT_CONTENT = "来自 <span style=\"color:#999999\">%s</span> 的店铺 <span style=\"color:#007CDA\">%s</span> 最近30天销售商品 <span style=\"color:#007CDA\">%s</span> 销售量 %s";


  public static final String ACCESSORY_PROMOTIONS_TITLE = "促销商品推荐";
  public static final String ACCESSORY_PROMOTIONS_PROMPT_CONTENT = "快来看看吧，商品 <span style=\"color:#007CDA\">%s</span> ，正在做 <span style=\"color:#007CDA\">%s</span> 促销活动！（商品来自 <span style=\"color:#999999\">%s</span> 卖家 <span style=\"color:#007CDA\">%s</span>）";

  public static final String ACCESSORY_SALES_TITLE = "匹配商品推荐";
  public static final String ACCESSORY_SALES_PROMPT_CONTENT = "快来看看吧，来自 <span style=\"color:#999999\">%s</span> 的卖家 <span style=\"color:#007CDA\">%s</span> 刚刚上架了商品 <span style=\"color:#007CDA\">%s</span> 您可能会感兴趣！";

  public static final String RECOMMEND_ACCESSORY_BY_QUOTED_TITLE = "供应商推荐商品";
  public static final String RECOMMEND_ACCESSORY_BY_QUOTED_PROMPT_CONTENT = "快来看看吧，来自 <span style=\"color:#999999\">%s</span> 的卖家 <span style=\"color:#007CDA\">%s</span> 推荐配件 <span style=\"color:#007CDA\">%s</span> 给本店！";


  public static final String BUYING_INFORMATION_MATCH_RESULT_TITLE = "求购推送统计";
  public static final String BUYING_INFORMATION_MATCH_RESULT_PROMPT_CONTENT = "您刚刚发布的求购信息： <span style=\"color:#007CDA\">%s</span>，已经推送到 <span style=\"color:#007CDA\">%s</span> 家匹配汽配店！";

  public static final String ACCESSORY_MATCH_RESULT_TITLE = "商品推送统计";
  public static final String ACCESSORY_MATCH_RESULT_PROMPT_CONTENT = "您刚刚上架的商品信息：<span style=\"color:#007CDA\">%s</span>，已经推送到 <span style=\"color:#007CDA\">%s</span> 家匹配汽修店！";

  public static final String ACCESSORY_PROMOTIONS_MATCH_RESULT_TITLE = "促销推送统计";
  public static final String ACCESSORY_PROMOTIONS_MATCH_RESULT_PROMPT_CONTENT = "您刚刚促销的商品信息：<span style=\"color:#007CDA\">%s %s</span>，已经推送到 <span style=\"color:#007CDA\">%s</span> 家匹配汽修店！";

  public static final String PRE_BUY_ORDER_ACCESSORY_TITLE = "求购匹配商品推荐";
  public static final String PRE_BUY_ORDER_ACCESSORY_PROMPT_CONTENT = "快来看看吧，来自 <span style=\"color:#999999\">%s</span> 的卖家有在售配件 <span style=\"color:#007CDA\">%s</span> 可能与本店的求购匹配！";

  public static final String QUOTED_BUYING_INFORMATION_IGNORED_TITLE = "求购报价未采纳提示";
  public static final String QUOTED_BUYING_INFORMATION_IGNORED_PROMPT_CONTENT = "本店给来自 <span style=\"color:#999999\">%s</span> 的买家 <span style=\"color:#007CDA\">%s</span> 的求购报价未被采纳，买家已向其他商家下单！<span style=\"color:#999999\">（可能由于您的报价过高或报价商品不符合买家要求！）</span>";

}
