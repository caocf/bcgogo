<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<script type="text/javascript">
    $(function () {
        $(".J_searchReceiverPushMessageBtn").click(function (e) {
            e.stopPropagation();
            var topCategory = G.normalize($(this).attr("data-top-category"));
            var category = G.normalize($(this).attr("data-category"));
            var receiverStatus = G.normalize($(this).attr("data-receiver-status"));
            var url = "pushMessage.do?method=receiverPushMessageList&topCategory="+topCategory+"&category="+category+"&receiverStatus="+receiverStatus;
            window.location.assign(encodeURI(url));
        });
        $(".J_addNewStationMessage").click(function () {
            openOrAssign("stationMessage.do?method=createStationMessage");
        });
    });
    function refreshPushMessageCategoryStatNumber(){
        APP_BCGOGO.Net.asyncGet({
            url: "pushMessage.do?method=getPushMessageCategoryStatNumber",
            dataType: "json",
            success: function (result) {
                if (!G.isEmpty(result) && result.success) {
                    //update number
                    $(".J_titNum").each(function(index){
                        var count = parseInt(G.normalize(result.data[$(this).attr("data-category")+"_"+$(this).attr("data-receiver-status")],"0"));
                        $(this).text(count);
                        if(count>0){
                            $(this).show();
                        }else{
                            $(this).hide();
                        }
                    });
                    $(".J_H_titNum").each(function(index){
                        var total = 0;
                        $(this).closest("ul").find(".J_titNum").each(function(index){
                            total +=parseInt(G.normalize($(this).text(),"0"));
                        });
                        if(total>0){
                            $(this).text("("+total+")");
                        }else{
                            $(this).text("");
                        }
                    });
                }else{
                    nsDialog.jAlert("数据异常!");
                }
            },
            error: function () {
                nsDialog.jAlert("数据异常!");
            }
        });
    }
</script>
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<bcgogo:permissionParam permissions="WEB.VEHICLE_CONSTRUCTION.APPOINT_ORDER_LIST,WEB.AUTOACCESSORYONLINE.COMMODITYQUOTATIONS.BASE,WEB.AUTOACCESSORYONLINE.MY_QUOTEDPREBUYORDER,WEB.AUTOACCESSORYONLINE.MY_PREBUYORDER,WEB.SCHEDULE.MESSAGE_CENTER.APPLY.CUSTOMER_APPLY,WEB.SCHEDULE.MESSAGE_CENTER.APPLY.SUPPLIER_APPLY,WEB.SCHEDULE.MESSAGE_CENTER.NOTICE.ASSOCIATION_NOTICE,WEB.VERSION.FOUR_S_VERSION_BASE">
    <div class="clear left">
        <bcgogo:hasPermission permissions="WEB.SCHEDULE.MESSAGE_CENTER.RECEIVER.BASE">
            <c:if test="${WEB_AUTOACCESSORYONLINE_COMMODITYQUOTATIONS_BASE || WEB_AUTOACCESSORYONLINE_MY_QUOTEDPREBUYORDER || WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER || WEB_SCHEDULE_MESSAGE_CENTER_APPLY_CUSTOMER_APPLY || WEB_SCHEDULE_MESSAGE_CENTER_APPLY_SUPPLIER_APPLY || WEB_SCHEDULE_MESSAGE_CENTER_NOTICE_ASSOCIATION_NOTICE}">
                <div class="titles">
                    <div class="leftTit J_searchReceiverPushMessageBtn <c:if test='${currPage==\"ReceiverMessage\"}'>message_hover_1</c:if>" style="cursor: pointer">收到的消息</div>
                    <c:if test="${!WEB_VERSION_FOUR_S_VERSION_BASE}">
                      <c:if test="${WEB_SCHEDULE_MESSAGE_CENTER_APPLY_CUSTOMER_APPLY || WEB_SCHEDULE_MESSAGE_CENTER_APPLY_SUPPLIER_APPLY}">
                          <div class="left1_content">
                              <ul>
                                  <c:set var="ApplyMessageCount" value="${RelatedApplyMessage_UNREAD}"/>
                                  <h1 class="J_searchReceiverPushMessageBtn" style="cursor: pointer" data-top-category="ApplyMessage"><a class="<c:if test='${currPage==\"ApplyMessage\"}'>message_hover_2</c:if>">请求</a><span class="J_searchReceiverPushMessageBtn span_a J_H_titNum" data-top-category="ApplyMessage" data-receiver-status="UNREAD" style="cursor: pointer"><c:if test="${ApplyMessageCount>0}">(${ApplyMessageCount})</c:if></span></h1>
                                  <c:if test="${WEB_SCHEDULE_MESSAGE_CENTER_APPLY_CUSTOMER_APPLY || WEB_SCHEDULE_MESSAGE_CENTER_APPLY_SUPPLIER_APPLY}">
                                      <li class="J_searchReceiverPushMessageBtn <c:if test='${currPage==\"RelatedApplyMessage\"}'>message_hover_3</c:if>"  data-category="RelatedApplyMessage" style="cursor: pointer"><span class="<c:if test='${currPage==\"RelatedApplyMessage\"}'>message_hover_2</c:if>">关联请求</span><a class="J_searchReceiverPushMessageBtn titNum J_titNum" data-category="RelatedApplyMessage" data-receiver-status="UNREAD" style="display: ${RelatedApplyMessage_UNREAD==0?'none':''}">${RelatedApplyMessage_UNREAD>99?"99+":RelatedApplyMessage_UNREAD}</a></li>
                                  </c:if>
                              </ul>
                              <div class="clear"></div>
                          </div>
                      </c:if>
                    </c:if>
                    <div class="left1_content">
                        <ul>
                            <c:set var="NoticeMessageCount" value="${RelatedNoticeMessage_UNREAD+BuyingPushStatNoticeMessage_UNREAD+ProductPushStatNoticeMessage_UNREAD+QuotedBuyingIgnoredNoticeMessage_UNREAD+AppointNoticeMessage_UNREAD+SystemNoticeMessage_UNREAD}"/>
                            <h1 class="J_searchReceiverPushMessageBtn" data-top-category="NoticeMessage" style="cursor: pointer"><a class="<c:if test='${currPage==\"NoticeMessage\"}'>message_hover_2</c:if>">通知</a><span class="J_searchReceiverPushMessageBtn span_a J_H_titNum" data-top-category="NoticeMessage" data-receiver-status="UNREAD" style="cursor: pointer"><c:if test="${NoticeMessageCount>0}">(${NoticeMessageCount})</c:if></span></h1>
                            <c:if test="${!WEB_VERSION_FOUR_S_VERSION_BASE}">
                              <c:if test="${WEB_SCHEDULE_MESSAGE_CENTER_NOTICE_ASSOCIATION_NOTICE}">
                                  <li class="J_searchReceiverPushMessageBtn <c:if test='${currPage==\"RelatedNoticeMessage\"}'>message_hover_3</c:if>" data-category="RelatedNoticeMessage"><span class="<c:if test='${currPage==\"RelatedNoticeMessage\"}'>message_hover_2</c:if>">关联申请处理</span><a class="J_searchReceiverPushMessageBtn titNum J_titNum" data-category="RelatedNoticeMessage" data-receiver-status="UNREAD" style="display: ${RelatedNoticeMessage_UNREAD==0?'none':''}">${RelatedNoticeMessage_UNREAD>99?"99+":RelatedNoticeMessage_UNREAD}</a></li>
                              </c:if>
                            </c:if>
                            <c:if test="${WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER}">
                                <li class="J_searchReceiverPushMessageBtn <c:if test='${currPage==\"BuyingPushStatNoticeMessage\"}'>message_hover_3</c:if>" data-category="BuyingPushStatNoticeMessage"><span class="<c:if test='${currPage==\"BuyingPushStatNoticeMessage\"}'>message_hover_2</c:if>">求购推送统计</span><a class="J_searchReceiverPushMessageBtn titNum J_titNum" data-category="BuyingPushStatNoticeMessage" data-receiver-status="UNREAD" style="display: ${BuyingPushStatNoticeMessage_UNREAD==0?'none':''}">${BuyingPushStatNoticeMessage_UNREAD>99?"99+":BuyingPushStatNoticeMessage_UNREAD}</a></li>
                            </c:if>
                            <c:if test="${WEB_AUTOACCESSORYONLINE_MY_QUOTEDPREBUYORDER}">
                                <%--<li class="J_searchReceiverPushMessageBtn <c:if test='${currPage==\"ProductPushStatNoticeMessage\"}'>message_hover_3</c:if>" data-category="ProductPushStatNoticeMessage"><span class="<c:if test='${currPage==\"ProductPushStatNoticeMessage\"}'>message_hover_2</c:if>">商品推送统计</span><a class="J_searchReceiverPushMessageBtn titNum J_titNum" data-category="ProductPushStatNoticeMessage" data-receiver-status="UNREAD" style="display: ${ProductPushStatNoticeMessage_UNREAD==0?'none':''}">${ProductPushStatNoticeMessage_UNREAD>99?"99+":ProductPushStatNoticeMessage_UNREAD}</a></li>--%>
                                <li class="J_searchReceiverPushMessageBtn <c:if test='${currPage==\"QuotedBuyingIgnoredNoticeMessage\"}'>message_hover_3</c:if>" data-category="QuotedBuyingIgnoredNoticeMessage"><span class="<c:if test='${currPage==\"QuotedBuyingIgnoredNoticeMessage\"}'>message_hover_2</c:if>">报价未采纳提示</span><a class="J_searchReceiverPushMessageBtn titNum J_titNum" data-category="QuotedBuyingIgnoredNoticeMessage" data-receiver-status="UNREAD" style="display: ${QuotedBuyingIgnoredNoticeMessage_UNREAD==0?'none':''}">${QuotedBuyingIgnoredNoticeMessage_UNREAD>99?"99+":QuotedBuyingIgnoredNoticeMessage_UNREAD}</a></li>
                            </c:if>
                            <c:if test="${WEB_VEHICLE_CONSTRUCTION_APPOINT_ORDER_LIST}">
                                <li class="J_searchReceiverPushMessageBtn <c:if test='${currPage==\"AppointNoticeMessage\"}'>message_hover_3</c:if>" data-category="AppointNoticeMessage"><span class="<c:if test='${currPage==\"AppointNoticeMessage\"}'>message_hover_2</c:if>">预约提醒</span><a class="J_searchReceiverPushMessageBtn titNum J_titNum" data-category="AppointNoticeMessage" data-receiver-status="UNREAD" style="display: ${AppointNoticeMessage_UNREAD==0?'none':''}">${AppointNoticeMessage_UNREAD>99?"99+":AppointNoticeMessage_UNREAD}</a></li>
                            </c:if>
                            <li class="J_searchReceiverPushMessageBtn <c:if test='${currPage==\"SystemNoticeMessage\"}'>message_hover_3</c:if>" data-category="SystemNoticeMessage"><span class="<c:if test='${currPage==\"SystemNoticeMessage\"}'>message_hover_2</c:if>">系统通知</span><a class="J_searchReceiverPushMessageBtn titNum J_titNum" data-category="SystemNoticeMessage" data-receiver-status="UNREAD" style="display: ${SystemNoticeMessage_UNREAD==0?'none':''}">${SystemNoticeMessage_UNREAD>99?"99+":SystemNoticeMessage_UNREAD}</a></li>
                        </ul>
                        <div class="clear"></div>
                    </div>
                    <c:if test="${WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER || WEB_AUTOACCESSORYONLINE_COMMODITYQUOTATIONS_BASE || WEB_SCHEDULE_MESSAGE_CENTER_APPLY_CUSTOMER_APPLY || WEB_SCHEDULE_MESSAGE_CENTER_APPLY_SUPPLIER_APPLY || WEB_AUTOACCESSORYONLINE_MY_QUOTEDPREBUYORDER}">
                    </c:if>
                    <div class="left1_content">
                        <ul>
                            <c:set var="StationMessageCount" value="${QuotedBuyingInformationStationMessage_UNREAD+RecommendProductStationMessage_UNREAD+MatchStationMessage_UNREAD+BuyingInformationStationMessage_UNREAD}"/>
                            <h1 class="J_searchReceiverPushMessageBtn" data-top-category="StationMessage" style="cursor: pointer"><a class="<c:if test='${currPage==\"StationMessage\"}'>message_hover_2</c:if>">站内消息</a><span class="J_searchReceiverPushMessageBtn span_a J_H_titNum" data-top-category="StationMessage" data-receiver-status="UNREAD" style="cursor: pointer"><c:if test="${StationMessageCount>0}">(${StationMessageCount})</c:if></span></h1>
                            <c:if test="${WEB_AUTOACCESSORYONLINE_MY_PREBUYORDER}">
                                <li class="J_searchReceiverPushMessageBtn <c:if test='${currPage==\"QuotedBuyingInformationStationMessage\"}'>message_hover_3</c:if>" data-category="QuotedBuyingInformationStationMessage"><span class="<c:if test='${currPage==\"QuotedBuyingInformationStationMessage\"}'>message_hover_2</c:if>">求购报价提醒</span><a class="J_searchReceiverPushMessageBtn titNum J_titNum" data-category="QuotedBuyingInformationStationMessage" data-receiver-status="UNREAD" style="display: ${QuotedBuyingInformationStationMessage_UNREAD==0?'none':''}">${QuotedBuyingInformationStationMessage_UNREAD>99?"99+":QuotedBuyingInformationStationMessage_UNREAD}</a></li>
                            </c:if>
                            <c:if test="${WEB_AUTOACCESSORYONLINE_COMMODITYQUOTATIONS_BASE && !isWholesalerVersion}">
                                <li class="J_searchReceiverPushMessageBtn <c:if test='${currPage==\"RecommendProductStationMessage\"}'>message_hover_3</c:if>" data-category="RecommendProductStationMessage"><span class="<c:if test='${currPage==\"RecommendProductStationMessage\"}'>message_hover_2</c:if>">推荐商品消息</span><a class="J_searchReceiverPushMessageBtn titNum J_titNum" data-category="RecommendProductStationMessage" data-receiver-status="UNREAD" style="display: ${RecommendProductStationMessage_UNREAD==0?'none':''}">${RecommendProductStationMessage_UNREAD>99?"99+":RecommendProductStationMessage_UNREAD}</a></li>
                            </c:if>
                            <c:if test="${!WEB_VERSION_FOUR_S_VERSION_BASE}">
                              <c:if test="${WEB_SCHEDULE_MESSAGE_CENTER_APPLY_CUSTOMER_APPLY || WEB_SCHEDULE_MESSAGE_CENTER_APPLY_SUPPLIER_APPLY}">
                                  <li class="J_searchReceiverPushMessageBtn <c:if test='${currPage==\"MatchStationMessage\"}'>message_hover_3</c:if>" data-category="MatchStationMessage"><span class="<c:if test='${currPage==\"MatchStationMessage\"}'>message_hover_2</c:if>">匹配关联消息</span><a class="J_searchReceiverPushMessageBtn titNum J_titNum" data-category="MatchStationMessage" data-receiver-status="UNREAD" style="display: ${MatchStationMessage_UNREAD==0?'none':''}">${MatchStationMessage_UNREAD>99?"99+":MatchStationMessage_UNREAD}</a></li>
                              </c:if>
                            </c:if>
                            <%--<c:if test="${WEB_AUTOACCESSORYONLINE_MY_QUOTEDPREBUYORDER}">--%>
                                <li class="J_searchReceiverPushMessageBtn <c:if test='${currPage==\"BuyingInformationStationMessage\"}'>message_hover_3</c:if>" data-category="BuyingInformationStationMessage"><span class="<c:if test='${currPage==\"BuyingInformationStationMessage\"}'>message_hover_2</c:if>">客户商机消息</span><a class="J_searchReceiverPushMessageBtn titNum J_titNum" data-category="BuyingInformationStationMessage" data-receiver-status="UNREAD" style="display: ${BuyingInformationStationMessage_UNREAD==0?'none':''}">${BuyingInformationStationMessage_UNREAD>99?"99+":BuyingInformationStationMessage_UNREAD}</a></li>
                            <%--</c:if>--%>
                        </ul>
                        <div class="clear"></div>
                    </div>
                </div>
            </c:if>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.SCHEDULE.MESSAGE_CENTER.SEND.BASE">
        <div class="titles">
            <div class="leftTit <c:if test='${currPage==\"SenderMessage\"}'>message_hover_1</c:if>" style="cursor: pointer"><a href="stationMessage.do?method=showSendStationMessageList">发出的消息</a></div>
            <div class="left1_content">
                <ul>
                    <li class="J_addNewStationMessage <c:if test='${currPage==\"AddStationMessage\"}'>message_hover_3 message_hover_2</c:if>"  style="cursor: pointer">写消息</li>
                </ul>
                <div class="clear"></div>
            </div>
        </div>
        </bcgogo:hasPermission>
    </div>
</bcgogo:permissionParam>