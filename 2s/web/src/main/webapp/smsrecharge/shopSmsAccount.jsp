<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="jUtil" uri="http://www.bcgogo.com/taglibs/tags" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
    <title>短信账单</title>
    <!--<script type="text/javascript" src="script/invoicing.js"></script>-->
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css"  href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/message<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.CUSTOMER_MANAGER.SMS_ACCOUNT");

        $(function(){
            var data = {
                        startPageNo:1,
                        maxRows: 10
                    };
            APP_BCGOGO.Net.syncPost({
                url: "smsrecharge.do?method=getShopSmsAccount",
                dataType: "json",
                data: data,
                success: function (result) {
                    showResponse(result);
                    initPage(result, "smsAccount", "smsrecharge.do?method=getShopSmsAccount", '', "showResponse", '', '', data, '');
                },
                error: function (result) {
                    nsDialog.jAlert("数据异常，请刷新页面！");
                }
            });
        });
       function showResponse(data) {
          if(data && data.shopSmsRecordList && data.shopSmsRecordList.length > 0) {
              $("#shopSmsAccountTable tr").not(":first").remove();
              var tr = '';
              for(var i = 0; i < data.shopSmsRecordList.length; i++) {
                  var shopSmsRecord = data.shopSmsRecordList[i];
                  var smsCategoryStr = '--',typeStr = '--',balance = dataTransition.rounding(shopSmsRecord.balance,1),blueColor = true;

                  if(shopSmsRecord.smsCategory == 'SHOP_RECHARGE') {
                      smsCategoryStr = '充值';
                      typeStr = '银联充值';
                  } else if(shopSmsRecord.smsCategory == 'CRM_RECHARGE') {
                      smsCategoryStr = '充值';
                      typeStr = '现金充值';
                  } else if(shopSmsRecord.smsCategory == 'REGISTER_HANDSEL') {
                      smsCategoryStr = '赠送';
                      typeStr = '注册赠送';
                  } else if(shopSmsRecord.smsCategory == 'RECOMMEND_HANDSEL') {
                      smsCategoryStr = '赠送';
                      typeStr = '推荐赠送';
                  } else if(shopSmsRecord.smsCategory == 'RECHARGE_HANDSEL') {
                      smsCategoryStr = '赠送';
                      typeStr = '充值赠送';
                  } else if(shopSmsRecord.smsCategory == 'REFUND') {
                      smsCategoryStr = '消费';
                      typeStr = '消费退费';
                  } else if(shopSmsRecord.smsCategory == 'SHOP_CONSUME') {
                      smsCategoryStr = '消费';
                      balance = balance * -1;
                      blueColor = false;
                      typeStr = (shopSmsRecord.smsSendSceneStr == null ? '' : shopSmsRecord.smsSendSceneStr);
                  }
                   if(blueColor) {
                       tr += '<tr style="color: #1F541E;text-align: center;"><td>' + (i + 1) + '</td>';
                   } else {
                       tr += '<tr style="text-align: center;"><td>' + (i + 1) + '</td>';
                   }

                  tr += '<td>' + shopSmsRecord.operateTimeStr + '</td>';
                  tr += '<td>' + smsCategoryStr + '</td>';
                  tr += '<td>' + typeStr + '</td>';
                  tr += '<td>' + balance + '</td>';
                  tr += '</tr>';

              }
              $("#shopSmsAccountTable").append($(tr));
          }
       }

    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">短信管理</div>
    </div>
    <div class="messageContent">
        <jsp:include page="/sms/smsNavi.jsp">
            <jsp:param name="currPage" value="smsrecharge" />
        </jsp:include>
        <div class="messageRight">
            <div class="messageRight_radius">
                <table width="100%" border="0">
                    <tr>
                        <td><strong style="font-size:14px;">短信账单</strong> 当前余额：${result.smsBalance == null ? 0 : result.smsBalance}元（充值总计${jUtil:roundInt(result.rechargeTotal)}元；赠送总计${jUtil:roundInt(result.presentTotal)}元；消费总计${jUtil:round(result.consumeTotal)}元）<br /></td>

                        <td width="7%" align="right"><a class="blueBtn_64" style="float:right" href="smsrecharge.do?method=smsrecharge&rechargeamount=1000">我要充值</a></td>
                    </tr>
                </table>
                <div class="clear i_height"></div>
                <table width="796" border="0" cellspacing="0" class="news-table" id="shopSmsAccountTable">
                    <colgroup valign="top">
                        <col width="50" />
                        <col width="200" />
                        <col width="150" />
                        <col  />
                        <col width="150"/>
                    </colgroup>
                    <tr class="news-thbody">
                        <td align="center">序号</td>
                        <td align="center"> 时间 </td>
                        <td align="center">分类</td>
                        <td align="center">类型</td>
                        <td align="center">金额（元）</td>
                    </tr>
                </table>
                <div style="float: right;">
                    <bcgogo:ajaxPaging url="smsrecharge.do?method=getShopSmsAccount" postFn="showResponse" dynamical="smsAccount" display="none" />
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
