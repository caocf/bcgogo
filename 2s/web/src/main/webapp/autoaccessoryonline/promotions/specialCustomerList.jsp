<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>客户消费优惠</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "PROMOTIONS_MANAGER_MENU");
        $().ready(function(){
            if(!G.isEmpty($("#promotionsId").val())){
                APP_BCGOGO.Net.asyncAjax({
                    url: "promotions.do?method=getSpecialCustomerDetail",
                    data:{promotionsId:$("#promotionsId").val()},
                    type: "POST",
                    cache: false,
                    dataType: "json",
                    success: function (json) {
                        if(G.isEmpty(json)){
                            return;
                        }
                        var content=generateSpecialCustomerPromotionsContent(json.data);
                        $("#promotionsContentTD").children().remove();
                        $("#promotionsContentTD").append(content);
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常！");
                    }
                });
            }
        });

        function deletePromotions(){
            var promotionsId=$("#promotionsId").val();
            if(G.isEmpty(promotionsId)){
                return;
            }
            nsDialog.jConfirm("是否确认删除客户优惠?",null,function(resultVal){
                if(resultVal){
                    APP_BCGOGO.Net.asyncAjax({
                        url:"promotions.do?method=deleteSpecialCustomer",
                        type: "POST",
                        cache: false,
                        data: {
                            promotionsId:promotionsId
                        },
                        dataType: "json",
                        success: function (json) {
                            if(json.success){
                                toSpecialCustomerList()
                            }else{
                                nsDialog.jAlert(json.msg);
                            }
                        }
                    });
                }
            });
        }


        function editSpecailCustomer(){
            var promotionsId=$("#promotionsId").val();
            window.location.href="promotions.do?method=toManageSpecialCustomer&promotionsId="+promotionsId;
        }

        function getTimeSegmentStr(startTimeStr,endTimeStr){
            var segmentStr="";
            if(G.Lang.isEmpty(startTimeStr)||G.Lang.isEmpty(endTimeStr)){
                return segmentStr;
            }
            var startTime =  dateUtil.convertDateStrToDate(startTimeStr,dateUtil.dateStringFormatDayHourMin);
            var endTime =  dateUtil.convertDateStrToDate(endTimeStr,dateUtil.dateStringFormatDayHourMin);
            var days = parseInt(dateUtil.getDayBetweenTwoDate(startTime,endTime));
            if(days<=30){
                segmentStr="一个月内";
            }else if(days<=90 && days>30){
                segmentStr="三个月内";
            }else if(days<=180 && days>90){
                segmentStr="半年内";
            }else if(days<=365 && days>180){
                segmentStr="一年内";
            }
            return segmentStr;
        }

        function generateSpecialCustomerPromotionsContent(promotions){
            var content="";
            if(G.isEmpty(promotions)||G.isEmpty(promotions.promotionsRuleDTOList)){
                return content;
            }
            var rules=promotions.promotionsRuleDTOList;
            content+='<div class="line">所有客户，'+getTimeSegmentStr(promotions.startTimeStr,promotions.endTimeStr)+'</div>';
            for(var i=0;i<rules.length;i++){
                var rule=rules[i];
                var minAmount=rule.minAmount;
                var discountAmount=rule.discountAmount;
                var ruleType=rule.promotionsRuleType;
                var level="一";      //当前最多三级
                switch(i){
                    case 0:{
                        level="一";
                        break;
                    }
                    case 1:{
                        level="二";
                        break;
                    }
                    case 2:{
                        level="三";
                        break;
                    }
                }
                if(ruleType=="DISCOUNT_FOR_OVER_MONEY"){
                    content+='<div class="line">第'+level+'级 累计消费金额满<b class="red_color">'+minAmount+'</b>元，打<b class="red_color">'+discountAmount+'</b>折;</div>';
                }else if(ruleType=="DISCOUNT_FOR_OVER_AMOUNT"){

                }else if(ruleType=="REDUCE_FOR_OVER_MONEY"){
                    content+='<div class="line">第'+level+'级 累计消费金额满<b class="red_color">'+minAmount+'</b>元，优惠<b class="red_color">'+discountAmount+'</b>元;</div>';
                }
            }
            return content;

        }

    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="../supplyCenterLeftNavi.jsp">
            <jsp:param name="biMenu" value="specialCustomer"/>
            <jsp:param name="currPage" value="promotions"/>
        </jsp:include>
        <div class="bodyLeft">
            <input id="promotionsId" type="hidden" value="${promotions.idStr}" />
            <h3 class="title">客户优惠设置</h3>
            <div class="cuSearch">
                <div class="lineTitle">
                    我的客户优惠
                </div>
                <div class="cartBody lineBody">
                    <table class="tab_cuSearch tabSales" cellpadding="0" cellspacing="0">
                        <col width="150">
                        <col>
                        <col width="110">
                        <col width="80">
                        <tr class="titleBg">
                            <td style="padding-left:10px;">设置时间</td>
                            <td>优惠内容</td>
                            <td>设置人</td>
                            <td>操作</td>
                        </tr>
                        <tr class="space"><td colspan="5"></td></tr>

                        <c:if test="${promotions==null}">
                            <tr class="titBody_Bg">
                                <td colspan="8">
                                    <span style="padding-left:300px;text-align: center;">  您还未设置客户优惠！<a onclick="editSpecailCustomer()" class="blue_color">赶紧设置</a>吧！ </span>
                                </td>
                            </tr>
                        </c:if>

                        <c:if test="${promotions!=null}">
                            <tr class="titBody_Bg">
                                <td style="padding-left:10px;">
                                        ${promotions.saveTimeStr}
                                </td>
                                <td id="promotionsContentTD">

                                </td>
                                <td>${promotions.userName}</td>
                                <td>
                                    <a onclick="editSpecailCustomer()" class="blue_color">修改</a>&nbsp;
                                    <a onclick="deletePromotions()" class="blue_color">删除</a>
                                </td>
                            </tr>
                        </c:if>

                        <tr class="titBottom_Bg"><td colspan="5"></td></tr>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>