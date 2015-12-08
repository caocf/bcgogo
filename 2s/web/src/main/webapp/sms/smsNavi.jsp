<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<c:set var="catalogue" value="<%=request.getParameter(\"catalogue\")%>"/>
<script type="text/javascript">
    $(function(){
        $(".sms_menu_item").click(function(){
           var url=$(this).attr("url");
            if(!G.isEmpty(url)){
                window.location.href=url;
            }
        });

         $(".sms_menu_item").hover(function(){
             var $num=$(this).find(".titNum");
             if($num.length>0){
                 $num.css("background","url(images/btn_Num.png) no-repeat 0px -13px");
                 $num.css("color","#fff");

             }
         },function(){
             var $num=$(this).find(".titNum");
             if($num.length>0){
                 $num.css("background",'url("images/btn_Num.png") no-repeat scroll 0 0 rgba(0, 0, 0, 0)');
                 $num.css("color","#FFFFFF");
             }
         });

        refreshSmsStat();

    });


    function refreshSmsStat(){
        APP_BCGOGO.Net.asyncAjax({
            url: "sms.do?method=getSmsStat",
            type: "POST",
            cache: false,
            dataType: "json",
            success: function (smsStat) {
                $("#sms_send_num").text(G.normalize(smsStat.sms_send_num));
                $("#sms_sent_num").text(G.normalize(smsStat.sms_sent_num));
                $("#sms_draft_num").text(G.normalize(smsStat.sms_draft_num));
                $("#smsBalanceNavi").text(G.normalize(smsStat.sms_balance));
            },
            error:function(){
                nsDialog.jAlert("网络异常。");
            }
        });
    }
</script>
<%--<div class="mainTitles">--%>
<%--<div class="cusTitle">--%>
<%--<c:choose>--%>
<%--<c:when test='${catalogue==\"smsManage\"}'>写短信</c:when>--%>
<%--<c:when test='${catalogue==\"smsRecharge\"}'>短信充值</c:when>--%>
<%--<c:otherwise>客户管理</c:otherwise>--%>
<%--</c:choose>--%>
<%--消息中心--%>
<%--</div>--%>
<%--<bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.SMS_MANAGER,WEB.CUSTOMER_MANAGER.SMS_RECHARGE" permissionKey="smsMenu">--%>
<%--<c:if test="${smsMenuPermissionCounts>1}">--%>
<%--<div class="titleList">--%>
<%--<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_MANAGER">--%>
<%--<a id="smsManage" class="<c:if test='${catalogue==\"smsManage\"}'>click </c:if>" action-type="menu-click"--%>
<%--menu-name="WEB.CUSTOMER_MANAGER.SMS_SWRITE"   href="sms.do?method=smswrite">写短信</a>--%>
<%--</bcgogo:hasPermission>--%>
<%--<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_RECHARGE">--%>
<%--<a id="smsRecharge" class="<c:if test='${catalogue==\"smsRecharge\"}'>click </c:if>" action-type="menu-click"--%>
<%--menu-name="WEB.CUSTOMER_MANAGER.SMS_RECHARGE"   href="smsrecharge.do?method=smsrecharge">短信充值</a>--%>
<%--</bcgogo:hasPermission>--%>
<%--</div>--%>
<%--</c:if>--%>
<%--</bcgogo:permissionParam>--%>
<%--</div>--%>
<%--<c:if test='${catalogue==\"smsManage\"}'>--%>
<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>
<%--<div class="sms_title">--%>
<%--<div class="sms_titleLeft"></div>--%>
<%--<div class="sms_titleBody">--%>
<%--<ul>--%>
<%--<li><a class="<c:if test='${currPage==\"smsInbox\"}'>sms_hover</c:if>" href="sms.do?method=smsinbox">收件箱</a>--%>
<%--</li>--%>
<%--<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_MANAGER.SMS_SEND_BOX">--%>
<%--<li><a class="<c:if test='${currPage==\"smsSent\"}'>sms_hover</c:if>"--%>
<%--href="sms.do?method=smssent">已发送</a></li>--%>
<%--</bcgogo:hasPermission>--%>
<%--<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_MANAGER.SMS_SEND_BOX">--%>
<%--<li><a class="<c:if test='${currPage==\"smsSend\"}'>sms_hover</c:if>"--%>
<%--href="sms.do?method=smssend">待发送</a></li>--%>
<%--</bcgogo:hasPermission>--%>
<%--<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_MANAGER.SMS_SEND_BOX">--%>
<%--<li><a class="<c:if test='${currPage==\"smsWrite\"}'>sms_hover</c:if>"--%>
<%--href="sms.do?method=smswrite">写短信</a>--%>
<%--</li>--%>
<%--</bcgogo:hasPermission>--%>

<%--</ul>--%>
<%--</div>--%>
<%--<div class="sms_titleRight"></div>--%>
<%--</div>--%>
<%--<div class="sms_main">--%>
<%--<div class="height"></div>--%>
<%--<div class="sms_mainLeft">--%>
<%--<div class="<c:if test='${currPage==\"smsInbox\"}'>sms_leftTwo</c:if>"><a href="sms.do?method=smsinbox"--%>
<%--class="sms_leftIcon4">收 件 箱</a></div>--%>
<%--<div class="i_height"></div>--%>

<%--<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_MANAGER.SMS_SEND_BOX">--%>
<%--<div class="<c:if test='${currPage==\"smsSent\"}'>sms_leftTwo</c:if>"><a href="sms.do?method=smssent"--%>
<%--class="sms_leftIcon2">已 发 送</a>--%>
<%--</div>--%>
<%--<div class="i_height"></div>--%>
<%--</bcgogo:hasPermission>--%>
<%--<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_MANAGER.SMS_SEND_BOX">--%>
<%--<div class="<c:if test='${currPage==\"smsSend\"}'>sms_leftTwo</c:if>"><a href="sms.do?method=smssend"--%>
<%--class="sms_leftIcon3">待 发 送</a>--%>
<%--</div>--%>
<%--<div class="i_height"></div>--%>
<%--</bcgogo:hasPermission>--%>
<%--<bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_MANAGER.SMS_SEND_BOX">--%>
<%--<div class="<c:if test='${currPage==\"smsWrite\"}'>sms_leftTwo</c:if>"><a href="sms.do?method=smswrite"--%>
<%--class="sms_leftIcon1">写 短 信</a>--%>
<%--</div>--%>
<%--</bcgogo:hasPermission>--%>
<%--</div>--%>
<%--</c:if>--%>



<div class="messageLeft" style="margin-left: -7px">
    <div class="messageContainer_01">
        <ul>
            <li url="sms.do?method=smswrite" class="sms_menu_item ${currPage=='smsWrite'?'li_selected':''}"><div class="li_img"><img src="../web/images/icon1.png" /></div>写短信</li>
            <li url="sms.do?method=smssent" class="sms_menu_item ${currPage=='smsSent'?'li_selected':''}"><div class="li_img"><img src="../web/images/icon2.png" /></div>已发送<a id="sms_sent_num" class="titNum">0</a></li>
            <li url="sms.do?method=smssend" class="sms_menu_item ${currPage=='smsSend'?'li_selected':''}"><div class="li_img"><img src="../web/images/icon3.png" /></div>待发送<a id="sms_send_num" class="titNum">0</a></li>
            <li url="sms.do?method=toSmsDraft" class="sms_menu_item ${currPage=='smsDraft'?'li_selected':''}"><div class="li_img"><img src="../web/images/icon4.png" /></div>草稿箱<a id="sms_draft_num" class="titNum">0</a></li>
        </ul>
    </div>
    <div class="messageContainer_01">
        <ul>
            <li url="sms.do?method=toAddressList" class="sms_menu_item ${currPage=='addressList'?'li_selected':''}"><div class="li_img"><img src="../web/images/icon5.png" /></div>通讯录</li>
            <li url="sms.do?method=toSmsTemplateList" class="sms_menu_item ${currPage=='smsTemplate'?'li_selected':''}"><div class="li_img"><img src="../web/images/icon6.png" /></div>短信模板</li>
        </ul>
    </div>
    <div class="messageContainer_01">
        <div class="balance_txt">
            账户余额：<strong class="orange_color" id="smsBalanceNavi"></strong> 元
        </div>
        <a class="grayBtn_64 balance_left10 blue_color" href="smsrecharge.do?method=smsrecharge&rechargeamount=1000">充 值</a>
        <a class="grayBtn_64 balance_left10" href="smsrecharge.do?method=shopSmsAccount">查看账单</a>
        <div class="clear i_height"></div>
    </div>
</div>