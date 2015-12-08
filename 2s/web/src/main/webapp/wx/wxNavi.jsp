<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--<c:set var="catalogue" value="<%=request.getParameter(\"catalogue\")%>"/>--%>
<script type="text/javascript">
    $(function(){
        $(".J_wx_menu_item").click(function(){
            var url=$(this).attr("url");
            if(!G.isEmpty(url)){
                window.location.href=url;
            }
        });

        APP_BCGOGO.Net.syncAjax({
            url: "weChat.do?method=getWXShopAccount",
            type: "POST",
            cache: false,
            dataType: "json",
            success: function (account) {
                var balance=account.balance;
                var item=Number(balance/0.03).toFixed(0);
              $("#wxBalanceNavi").text(balance);
              $("#wxBalanceItem").text(item);
              $("#expireDate").text(account.expireDateStr);
            },
            error:function(){
                nsDialog.jAlert("网络异常。");
            }
        });


    });


</script>

<c:set var="currPage" value="<%=request.getParameter(\"currPage\")%>"/>

<div class="messageLeft" style="margin-left: -7px">
    <div class="messageContainer_01">
        <ul>
            <li url="weChat.do?method=toSendMessagePage" class="J_wx_menu_item ${currPage=='wxWrite'?'li_selected':''}"><div class="li_img"><img src="../web/images/icon1.png" /></div>发送微信</li>
            <li url="weChat.do?method=toWxSent" class="J_wx_menu_item ${currPage=='wxSent'?'li_selected':''}"><div class="li_img"><img src="../web/images/icon2.png" /></div>发送记录</li>
            <li url="weChat.do?method=toWXFans" class="J_wx_menu_item ${currPage=='wxFan'?'li_selected':''}"><div class="li_img"><img src="../web/images/icon5.png" /></div>我的粉丝</li>
            <li url="weChat.do?method=toWxShopConfig" class="J_wx_menu_item ${currPage=='wxShopConfig'?'li_selected':''}"><div class="li_img"><img src="../web/images/icon2.png" /></div>微信配置</li>

            <%--<li url="" class="J_wx_menu_item ${currPage=='wxTemplate'?'li_selected':''}"><div class="li_img"><img src="../web/images/icon3.png" /></div>微信模板</li>--%>
            <%--<li url="" class="J_wx_menu_item ${currPage=='wxHelp'?'li_selected':''}"><div class="li_img"><img src="../web/images/icon4.png" /></div>使用说明</li>--%>
        </ul>
    </div>
    <div class="messageContainer_01">
        <div class="balance_txt">
            余额<strong id="wxBalanceNavi" class="orange_color"></strong>元/<strong id="wxBalanceItem" class="orange_color"></strong>条<br/>
            有效期：<strong id="expireDate" class="orange_color"></strong>
        </div>
        <a href="weChat.do?method=toWXShopBill" class="grayBtn_64 balance_left10" style="margin:5px 0 0 7px">查看账单</a>
        <div class="clear i_height"></div>
    </div>
    <div>
      账户说明:<br/>1.有效期内发送不限量，超过有效期的以余额为准<br/>2.有效期和余额均不足时将无法推送微信账单<br/>3.有效期和余额均不足的，请联系当地经销商或客服充值
    </div>
</div>