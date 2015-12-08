<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 14-12-18
  Time: 下午2:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="jUtil" uri="http://www.bcgogo.com/taglibs/tags" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
    <title>微信账单</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css"  href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        //        defaultStorage.setItem(storageKey.MenuUid,"WEB.CUSTOMER_MANAGER.SMS_ACCOUNT");
        $(function(){
            var data = {
                startPageNo:1,
                maxRows: 10
            };
            APP_BCGOGO.Net.syncPost({
                url: "weChat.do?method=getWXShopBill",
                dataType: "json",
                data: data,
                success: function (result) {
                    showResponse(result);
                    initPage(result, "wxShopBill", "weChat.do?method=getWXShopBill", '', "showResponse", '', '', data, '');
                },
                error: function (result) {
                    nsDialog.jAlert("数据异常，请刷新页面！");
                }
            });
        });

        function showResponse(result) {
            if(G.isEmpty(result) || result.data.length <= 0) {
                return;
            }
            var wxShopBills=result.data;
            $("#wxShopBillTable tr").not(":first").remove();
            var tr = '';
            for(var i = 0; i < wxShopBills.length; i++) {
                var wxShopBill = wxShopBills[i];
                var vestDateStr=G.normalize(wxShopBill.vestDateStr);
                var total=G.rounding(wxShopBill.total);
                var amount= G.rounding(wxShopBill.amount);
                var scene=wxShopBill.scene;
                var sceneStr=wxShopBill.sceneStr;
                tr += '<tr>';
                tr +='<td>'+(i+1)+'</td>'
                tr += '<td>' + vestDateStr + '</td>';
                tr += '<td>' + sceneStr + '</td>';
                if(scene=='WX_RECHARGE'){
                    tr += '<td>--</td>';
                    if(total>0){
                        tr += '<td>+' + total + '</td>';
                    }else  {
                        tr += '<td>' + total + '</td>';
                    }
                }else{
                    tr += '<td>' + amount + '</td>';
                    tr += '<td>-' + total + '</td>';
                }
                tr += '</tr>';

            }
            $("#wxShopBillTable").append($(tr));
        }

    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">微信管理</div>
    </div>
    <div class="messageContent">
        <jsp:include page="wxNavi.jsp">
            <jsp:param name="currPage" value="smsrecharge" />
        </jsp:include>
        <div class="messageRight">
            <div class="messageRight_radius">
                <table width="100%" border="0">
                    <tr>
                        <td><strong style="font-size:14px;">微信账单</strong> 当前余额：${shopAccount.balance}元（共发送${billStat[0]}条,总计${jUtil:round(billStat[1])}元）<br /></td>

                        <td width="7%" align="right"></td>
                    </tr>
                </table>
                <div class="clear i_height"></div>
                <table width="796" border="0" cellspacing="0" class="news-table" id="wxShopBillTable">
                    <colgroup valign="top">
                        <col width="50" />
                        <col width="200" />
                        <col  />
                        <col width="150"/>
                    </colgroup>
                    <tr class="news-thbody">
                        <td align="center">序号</td>
                        <td align="center"> 发送时间 </td>
                        <td align="center">消费类型</td>
                        <td align="center">发送条数</td>
                        <td align="center">消费金额（元）</td>
                    </tr>
                </table>
                <div style="float: right;">
                    <bcgogo:ajaxPaging url="weChat.do?method=getWXShopBill" postFn="showResponse" dynamical="wxShopBill" display="none" />
                </div>

                <div class="clear"></div>
            </div>
            <div class="clear i_height"></div>
        </div>
    </div>
</div>
<div id="mask"  style="display:block;position: absolute;"> </div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="900px" height="450px" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
