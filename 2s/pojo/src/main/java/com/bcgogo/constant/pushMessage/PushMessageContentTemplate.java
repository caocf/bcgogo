package com.bcgogo.constant.pushMessage;

import com.bcgogo.enums.txn.pushMessage.PushMessageRedirectUrl;

public class PushMessageContentTemplate {
  public static final String APPLY_SUPPLIER_CONTENT = "<a target='_blank' class='blue_color' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）向本店提交关联申请请求！";
  public static final String APPLY_SUPPLIER_CONTENT_TEXT = "$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）向本店提交关联申请请求！";

  public static final String APPLY_CUSTOMER_CONTENT = "<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）向本店提交关联申请请求！";
  public static final String APPLY_CUSTOMER_CONTENT_TEXT = "$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）向本店提交关联申请请求！";

  public static final String SHOP_QUOTE_ENQUIRY_TO_APP_MESSAGE_CONTENT = "<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>$!{context.enquiryTimeStr}已为您报价！";
  public static final String SHOP_QUOTE_ENQUIRY_TO_APP_MESSAGE_CONTENT_TEXT = "$!{context.shopDTO.name} $!{context.enquiryTimeStr}已为您报价！";

  public static final String SUPPLIER_ACCEPT_NOTICE_TO_SUPPLIER_CONTENT = "您已成功通过 <a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）的关联请求，现其已成为您的关联客户";
  public static final String SUPPLIER_ACCEPT_NOTICE_TO_SUPPLIER_CONTENT_TEXT = "您已成功通过 $!{context.shopDTO.name}（$!{context.shopDTO.areaName}） 的关联请求，现其已成为您的关联客户";

  public static final String SUPPLIER_ACCEPT_NOTICE_TO_CUSTOMER_CONTENT= "<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）已通过您的请求，现已成为您的关联供应商";
  public static final String SUPPLIER_ACCEPT_NOTICE_TO_CUSTOMER_CONTENT_TEXT= "$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）已通过您的请求，现已成为您的关联供应商";


  public static final String CUSTOMER_ACCEPT_NOTICE_TO_SUPPLIER_CONTENT= "<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）已通过您的请求，现已成为您的关联客户";
  public static final String CUSTOMER_ACCEPT_NOTICE_TO_SUPPLIER_CONTENT_TEXT= "$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）已通过您的请求，现已成为您的关联客户";


  public static final String CUSTOMER_ACCEPT_NOTICE_TO_CUSTOMER_CONTENT= "您已成功通过 <a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）的关联请求，现其已成为您的关联供应商";
  public static final String CUSTOMER_ACCEPT_NOTICE_TO_CUSTOMER_CONTENT_TEXT= "您已成功通过 $!{context.shopDTO.name}（$!{context.shopDTO.areaName}）的关联请求，现其已成为您的关联供应商";


  public static final String CANCEL_NOTICE_CONTENT= "<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）取消了与您的关联关系，理由：<span style='color:red'>$!{context.cancelMsg}</span> 请知悉！";
  public static final String CANCEL_NOTICE_CONTENT_TEXT= "$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）取消了与您的关联关系，理由：$!{context.cancelMsg} 请知悉！";


  public static final String REFUSE_NOTICE_CONTENT= "<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）拒绝了您的关联请求，拒绝理由：<span style='color:red'>$!{context.refuseMsg}</span>";
  public static final String REFUSE_NOTICE_CONTENT_TEXT= "$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）拒绝了您的关联请求，拒绝理由：$!{context.refuseMsg}";



  public static final String BUYING_INFORMATION_MATCH_RESULT_CONTENT = "您刚刚发布的求购信息已经推送到 <span style='color:red'>$!{context.pushCount}</span> 家匹配汽配店！<br>求购商品信息：<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectPreBuyOrderUrl.getKey()+"'>$!{context.preBuyOrderItemDTO.productInfo}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;求购 <span style='color:#FF5E04'>$!{context.preBuyOrderItemDTO.amount}$!{context.preBuyOrderItemDTO.unit}</span>";
  public static final String BUYING_INFORMATION_MATCH_RESULT_CONTENT_TEXT = "您刚刚发布的求购信息已经推送到 $!{context.pushCount} 家匹配汽配店！<br>求购商品信息：$!{context.preBuyOrderItemDTO.productInfo}求购 $!{context.preBuyOrderItemDTO.amount}$!{context.preBuyOrderItemDTO.unit}";

  public static final String ACCESSORY_MATCH_RESULT_CONTENT = "您刚刚上架的商品信息已经推送到 <span style='color:red'>$!{context.pushCount}</span> 家匹配汽修店！<br>商品信息：<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopProductDetailUrl.getKey() +"'>$!{context.productDTO.productInfo}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='color:#FF5E04'>¥$!{context.productDTO.inSalesPrice}</span>";
  public static final String ACCESSORY_MATCH_RESULT_CONTENT_TEXT = "您刚刚上架的商品信息已经推送到 $!{context.pushCount} 家匹配汽修店！<br>商品信息：%s ¥$!{context.productDTO.inSalesPrice}";



  public static final String ACCESSORY_PROMOTIONS_MATCH_RESULT_CONTENT = "您刚刚发布的促销商品<span style='color:#FF5E04'>$!{context.promotionsDTO.name}</span>信息 <span style='color:red'>$!{context.pushCount}</span> 家匹配汽修店！<br>商品信息：<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopProductDetailUrl.getKey() +"'>$!{context.productDTO.productInfo}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='color:#FF5E04'>¥$!{context.productDTO.inSalesPrice}</span><br>促销信息:$!{context.promotionsDTO.promotionsContent}<br><span style='color:red'>活动截止时间:$!{context.promotionsDTO.endTimeCNStr}</span>";
  public static final String ACCESSORY_PROMOTIONS_MATCH_RESULT_CONTENT_TEXT = "您刚刚发布的促销商品$!{context.promotionsDTO.name}信息 $!{context.pushCount} 家匹配汽修店！商品信息：$!{context.productDTO.productInfo} ¥$!{context.productDTO.inSalesPrice}促销信息:$!{context.promotionsDTO.promotionsContent} 活动截止时间:$!{context.promotionsDTO.endTimeCNStr}";

  public static final String QUOTED_BUYING_INFORMATION_IGNORED_CONTENT = "给买家<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）的求购报价未被采纳，买家已向其他商家下单！<br><span style='color:#999999'>（可能由于您的报价过高或报价商品不符合买家要求！）</span>";
  public static final String QUOTED_BUYING_INFORMATION_IGNORED_CONTENT_TEXT = "给买家$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）的求购报价未被采纳，买家已向其他商家下单！（可能由于您的报价过高或报价商品不符合买家要求！）";



  public static final String QUOTED_BUYING_INFORMATION_CONTENT = "<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）刚刚给本店的求购商品报价了！<br>报价信息:<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopProductDetailUrl.getKey() +"'>$!{context.quotedPreBuyOrderItemDTO.productInfo}</a>&nbsp;&nbsp;&nbsp;<span style='color:#FF5E04'>¥$!{context.quotedPreBuyOrderItemDTO.price}</span>";
  public static final String QUOTED_BUYING_INFORMATION_CONTENT_TEXT = "$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）刚刚给本店的求购商品报价了！报价信息:$!{context.quotedPreBuyOrderItemDTO.productInfo}¥$!{context.quotedPreBuyOrderItemDTO.price}";

  public static final String RECOMMEND_ACCESSORY_BY_QUOTED_CONTENT = "快来看看吧，卖家<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）推荐配件给本店！<br>推荐商品信息:<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopProductDetailUrl.getKey() +"'>$!{context.quotedPreBuyOrderItemDTO.productInfo}</a>        <span style='color:#FF5E04'>¥$!{context.quotedPreBuyOrderItemDTO.price}</span>";
  public static final String RECOMMEND_ACCESSORY_BY_QUOTED_CONTENT_TEXT = "快来看看吧，卖家$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）推荐配件给本店！推荐商品信息:$!{context.quotedPreBuyOrderItemDTO.productInfo}¥$!{context.quotedPreBuyOrderItemDTO.price}";

  public static final String ACCESSORY_SALES_CONTENT = "快来看看吧，卖家<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）刚刚上架了新商品，您可能会感兴趣！<br>商品信息：<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopProductDetailUrl.getKey() +"'>$!{context.productDTO.productInfo}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='color:#FF5E04'>¥$!{context.productDTO.inSalesPrice}</span>";
  public static final String ACCESSORY_SALES_CONTENT_TEXT = "快来看看吧，卖家$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）刚刚上架了新商品，您可能会感兴趣！商品信息：$!{context.productDTO.productInfo} ¥$!{context.productDTO.inSalesPrice}";

  public static final String ACCESSORY_PROMOTIONS_CONTENT_TEXT = "快来看看吧，卖家<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）的商品 <a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopProductDetailUrl.getKey() +"'>$!{context.productDTO.productInfo}</a> 正在做 <span style='color:#FF5E04'>$!{context.promotionsDTO.name}</span> 促销活动！<br>促销内容:$!{context.promotionsDTO.promotionsContent}<br><span style='color:red'>活动截止时间:$!{context.promotionsDTO.endTimeCNStr}</span>";
  public static final String ACCESSORY_PROMOTIONS_CONTENT = "快来看看吧，卖家$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）的商品 $!{context.productDTO.productInfo} 正在做 $!{context.promotionsDTO.name} 促销活动！促销内容:$!{context.promotionsDTO.promotionsContent}活动截止时间:$!{context.promotionsDTO.endTimeCNStr}";

  public static final String PRE_BUY_ORDER_ACCESSORY_CONTENT = "快来看看吧，卖家<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）有在售商品可能与本店的求购匹配！<br>商品信息：<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopProductDetailUrl.getKey() +"'>$!{context.productDTO.productInfo}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='color:#FF5E04'>¥$!{context.productDTO.inSalesPrice}</span>";
  public static final String PRE_BUY_ORDER_ACCESSORY_CONTENT_TEXT = "快来看看吧，卖家$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）有在售商品可能与本店的求购匹配！商品信息：$!{context.productDTO.productInfo} ¥$!{context.productDTO.inSalesPrice}";

  public static final String BUYING_INFORMATION_CONTENT = "买家 <a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）刚刚发布了一条求购信息，您可能感兴趣，马上去给他报价吧！<br>求购商品信息：<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectBuyInformationDetailUrl.getKey() +"'>$!{context.preBuyOrderItemDTO.productInfo}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;求购 <span style='color:#FF5E04'>$!{context.preBuyOrderItemDTO.amount}$!{context.preBuyOrderItemDTO.unit}</span>";
  public static final String BUYING_INFORMATION_CONTENT_TEXT = "买家$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）刚刚发布了一条求购信息，您可能感兴趣，马上去给他报价吧！求购商品信息：$!{context.preBuyOrderItemDTO.productInfo} 求购 $!{context.preBuyOrderItemDTO.amount}$!{context.preBuyOrderItemDTO.unit}";

  public static final String BUSINESS_CHANCE_LACK_CONTENT = "<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）有商品缺料了，赶快给他报价吧！<br>缺料商品信息：<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectBuyInformationDetailUrl.getKey() +"'>$!{context.preBuyOrderItemDTO.productInfo}</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;缺料 <span style='color:#FF5E04'>$!{context.preBuyOrderItemDTO.amount}$!{context.preBuyOrderItemDTO.unit}</span>";
  public static final String BUSINESS_CHANCE_LACK_CONTENT_TEXT = "$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）有商品缺料了，赶快给他报价吧！缺料商品信息：$!{context.preBuyOrderItemDTO.productInfo} 缺料 $!{context.preBuyOrderItemDTO.amount}$!{context.preBuyOrderItemDTO.unit}";

  public static final String BUSINESS_CHANCE_SELL_WELL_CONTENT = "<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）最近30天销售商品 <a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectBuyInformationDetailUrl.getKey() +"'>$!{context.preBuyOrderItemDTO.productInfo}</a>,销售量 <span style='color:#FF5E04'>$!{context.preBuyOrderItemDTO.fuzzyAmountStr}$!{context.preBuyOrderItemDTO.unit}</span>!赶快给他报价吧！";
  public static final String BUSINESS_CHANCE_SELL_WELL_CONTENT_TEXT = "$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）最近30天销售商品 $!{context.preBuyOrderItemDTO.productInfo}，销售量 $!{context.preBuyOrderItemDTO.fuzzyAmountStr}$!{context.preBuyOrderItemDTO.unit}!马上去给他报价吧！";
  //询价单
  public final static String APP_SUBMIT_ENQUIRY_MESSAGE_CONTENT = "车牌号：<span style='color:#007CDA'>$!{context.vehicleNo}</span>，已向本店提交询价单，赶快去报价吧！";
  public final static String APP_SUBMIT_ENQUIRY_MESSAGE_CONTENT_TEXT = "车牌号：$!{context.vehicleNo}，已向本店提交询价单，赶快去报价吧！";
  

   //车牌号：苏E12345 ，已取消预约2013-09-01 10：00 本店洗车服务 ！
  public final static String APP_CANCEL_APPOINT_MESSAGE_CONTENT = "车牌号：<span style='color:#007CDA'>$!{context.vehicleNo}</span>，已取消预约$!{context.applyTimeStr}本店 $!{context.services}！";
  public final static String APP_CANCEL_APPOINT_MESSAGE_CONTENT_TEXT = "车牌号：$!{context.vehicleNo}，已取消预约$!{context.applyTimeStr}本店 $!{context.services}！";
  //车牌号：苏E12345 ，已向本店提交2013-09-01 10：00 洗车服务 预约请求！
  public final static String  APP_APPLY_APPOINT_MESSAGE_CONTENT = "车牌号：<span style='color:#007CDA'>$!{context.vehicleNo}</span>，已向本店提交$!{context.applyTimeStr} $!{context.services} 预约请求！";
  public final static String  APP_APPLY_APPOINT_MESSAGE_CONTENT_TEXT = "车牌号：$!{context.vehicleNo}，已向本店提交$!{context.applyTimeStr} $!{context.services} 预约请求！";
  //车牌号：苏E12345 ，已向本店提交2013-09-01 10：00 洗车服务 预约请求！
  public final static String  SYS_ACCEPT_APPOINT_MESSAGE_CONTENT = "车牌号：<span style='color:#007CDA'>$!{context.vehicleNo}</span> $!{context.applyTimeStr}已预约本店 $!{context.services} 服务，30分钟未处理，系统已自动为您接受该服务！";
  public final static String  SYS_ACCEPT_APPOINT_MESSAGE_CONTENT_TEXT = "车牌号：$!{context.vehicleNo} $!{context.applyTimeStr}已预约本店 $!{context.services} 服务，30分钟未处理，系统已自动为您接受该服务！";
  //过期单据提醒
  public final static String OVERDUE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT  = "车牌号：<span style='color:#007CDA'>$!{context.vehicleNo}</span> 已向本店提交$!{context.appointTimeStr} $!{context.services} 预约服务已经到期，点击查看预约详情友情提示：为了保证客户及时到店服务，请及时联系客户！";
  public final static String OVERDUE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT_TEXT  = "$!{context.vehicleNo} 已向本店提交$!{context.appointTimeStr} $!{context.services} 预约服务已经到期，点击查看预约详情友情提示：为了保证客户及时到店服务，请及时联系客户！";
  //快过期 单据提醒shop
  public final static String SOON_EXPIRE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT  = "车牌号：<span style='color:#007CDA'>$!{context.vehicleNo}</span> 已向本店提交$!{context.appointTimeStr} $!{context.services} 预约服务马上到期，点击查看预约详情友情提示：为了保证客户及时到店服务，请及时联系客户！";
  public final static String SOON_EXPIRE_APPOINT_REMIND_SHOP_MESSAGE_CONTENT_TEXT  = "$!{context.vehicleNo} 已向本店提交$!{context.appointTimeStr} $!{context.services} 预约服务马上到期，点击查看预约详情友情提示：为了保证客户及时到店服务，请及时联系客户！";


  public final static String SINGLE_MATCHING_RECOMMEND_CUSTOMER_RELATED_CONTENT = "<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）与您的客户 <a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectCustomerDetailUrl.getKey() +"'>$!{context.customerName}</a> 可能匹配，马上去关联吧！";
  public final static String SINGLE_MATCHING_RECOMMEND_CUSTOMER_RELATED_CONTENT_TEXT = "$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）与您的客户$!{context.customerName}可能匹配，马上去关联吧！";

  public final static String SINGLE_MATCHING_RECOMMEND_SUPPLIER_RELATED_CONTENT = "<a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectShopUrl.getKey() +"'>$!{context.shopDTO.name}</a>（<span style='color:#999999'>$!{context.shopDTO.areaName}</span>）与您的供应商 <a class='blue_color' target='_blank' href='"+ PushMessageRedirectUrl.RedirectSupplierDetailUrl.getKey() +"'>$!{context.supplierName}</a> 可能匹配，马上去关联吧！";
  public final static String SINGLE_MATCHING_RECOMMEND_SUPPLIER_RELATED_CONTENT_TEXT = "$!{context.shopDTO.name}（$!{context.shopDTO.areaName}）与您的供应商$!{context.supplierName}可能匹配，马上去关联吧！";


  public final static String VEHICLE_FAULT_MESSAGE_2_SHOP_TITLE = "客户车辆故障";
//  public final static String VEHICLE_FAULT_MESSAGE_2_SHOP__CONTENT_TEXT = "$!{context.appUserName}($!{context.mobile})的爱车（$!{context.vehicleNo}）于$!{context.time} 出现故障：#if(${context.description})$!{context.description}，#end($!{context.faultCode})。";
  public final static String VEHICLE_FAULT_MESSAGE_2_SHOP__CONTENT_TEXT = "#if(${context.appUserName}) $!{context.appUserName} #else 客户 #end #if(${context.mobile})($!{context.mobile})#end 的爱车（$!{context.vehicleNo}）于$!{context.time}出现故障：#if(${context.description})$!{context.description}#end #if(${context.faultCode})($!{context.faultCode}) #end。";
//  public final static String VEHICLE_FAULT_MESSAGE_2_SHOP__CONTENT = "$!{context.appUserName}($!{context.mobile})的爱车（$!{context.vehicleNo}）于$!{context.time} 出现故障：#if(${context.description})$!{context.description}，#end($!{context.faultCode})。";
  public final static String VEHICLE_FAULT_MESSAGE_2_SHOP__CONTENT = "#if(${context.appUserName}) $!{context.appUserName} #else 客户 #end #if(${context.mobile})($!{context.mobile})#end 的爱车（$!{context.vehicleNo}）于$!{context.time}出现故障：#if(${context.description})$!{context.description}#end #if(${context.faultCode})($!{context.faultCode}) #end。";

  public final static String VEHICLE_FAULT_MESSAGE_2_APP_TITLE = "车辆故障";
  public final static String VEHICLE_FAULT_MESSAGE_2_APP_CONTENT_TEXT = "您的车辆出现故障！";
  public final static String VEHICLE_FAULT_MESSAGE_2_APP_CONTENT = "您的车辆出现故障！";

}
