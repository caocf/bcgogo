<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>促销管理</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "PROMOTIONS_MANAGER_MENU");
        <c:choose>
        <c:when test="${not empty promotionsDTO.idStr}">
        defaultStorage.setItem(storageKey.MenuCurrentItem,"编辑");
        </c:when>
        <c:otherwise>
        defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
        </c:otherwise>
        </c:choose>
//        function getOverUnit(){
//            var unit;
//            if($("#promotionsLimiterSelector").val() == 'OVER_AMOUNT'){
//                unit = "件";
//            }else if($("#promotionsLimiterSelector").val() == "OVER_MONEY"){
//                unit = "元";
//            }
//            return unit;
//        }
        $().ready(function(){
//            $("#promotionsLimiterSelector").live("change", function(){
//                var unit = getOverUnit($(this));
//                $("span.j_over_unit").text(unit);
//            });

            if(!G.isEmpty($("#promotionsId").val())){
                APP_BCGOGO.Net.asyncAjax({
                    url: "promotions.do?method=getPromotionsMJSDetail",
                    type: "POST",
                    data:{promotionsId:$("#promotionsId").val()},
                    cache: false,
                    dataType: "json",
                    success: function (json) {
                        _initPromotionsMJSDetial(json);
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常！");
                    }
                });
            }

            var _initPromotionsMJSDetial=function(json){
                if(G.isEmpty(json)) return;
                $(".table_mjs .promotion_content").remove();
                var promotions=json;
                var promotionsLimiter=promotions.promotionsLimiter;
                var promotionsRuleDTOList=promotions.promotionsRuleDTOList;
                if(!G.isEmpty(promotionsRuleDTOList)){
                    var contents="";
                    for(var i=0;i<promotionsRuleDTOList.length;i++){
                        var rule=promotionsRuleDTOList[i];
                        var ruleType=rule.promotionsRuleType;
                        var giveGiftFlag=rule.giveGiftFlag;
                        var giveDepositFlag=rule.giveDepositFlag;
                        var minAmount=G.rounding(rule.minAmount);
                        var giftDTO;
                        var depositDTO;
                        var ruleMJSDTOs=rule.promotionsRuleMJSDTOs;
                        for(var j=0;j<ruleMJSDTOs.length;j++){
                            var ruleMJS=ruleMJSDTOs[j];
                            if(ruleMJS.giftType=="GIFT"){
                                giftDTO=ruleMJS;
                            }
                        }
                        var giftName="";
                        var giftAmount="";
                        var depositAmount="";
                        var isGift=true;
                        if(!G.isEmpty(giftDTO)){
                            isGift=true;
                            giftName=G.normalize(giftDTO.giftName);
                            giftAmount=G.rounding(giftDTO.amount);
                        }
                        if(i==0){                         //第一个时要特殊处理
                            contents+='<div class="promotion_content" idPrefix="0"><input value="'+ruleType+'" class="promotionsRuleType" type="hidden" name="ruleDTO[0].promotionsRuleType"/>';
                            contents+='<a class="icon_close first_level" style="margin: 0px"></a><div class="line">满&nbsp;<input type="text" value="'+minAmount+'"  class="txt" style="width:100px;" name="promotionsRuleDTOList[0].minAmount" />&nbsp;<span class="j_over_unit">元</span>&nbsp;';
                            contents+='<input id="promotionsLimiter" name="promotionsLimiter" value="'+promotionsLimiter+'" type="hidden" /><select id="promotionsLimiterSelector" class="promotionsLimiterSelector"><option value="OVER_MONEY">金额（订单金额）</option><option value="OVER_AMOUNT">数量</option></select></div><div class="line">';
//                            if(isGift){
//                                contents+='<input class="giveGiftChk" checked="'+isGift+'" type="checkbox" name="promotionsRuleDTOList[0].giveGiftFlag" onclick="chkClick(this)" value="true"/>送 礼 品</label>&nbsp;<input type="hidden" name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[0].giftType" value="GIFT" />';
//                            }else{
//                                contents+='<input class="giveGiftChk"  type="checkbox" name="promotionsRuleDTOList[0].giveGiftFlag" onclick="chkClick(this)" value="false"/>送 礼 品</label>&nbsp;<input type="hidden" name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[0].giftType" value="GIFT" />';
//                            }
                            contents+= '<input type="hidden" name="promotionsRuleDTOList['+i+'].promotionsRuleMJSDTOs[0].giftType" value="GIFT">';
                            contents+='<div class="rad">送 <input value="'+giftName+'"  name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[0].giftName" class="J-initialCss giftNameChk txt" initialValue="礼品名称" type="text" style="width:100px;" />&nbsp;';
                            contents+='<input value="'+giftAmount+'" name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[0].amount" type="text" class="giftAmount J-initialCss txt" initialValue="数量" style="width:60px;" />件</div></div>';
//                            if(isDeposit) {
//                                contents+='<div class="line"><label class="rad"><input  class="giveDepositChk" type="checkbox" checked="'+isDeposit+'" name="promotionsRuleDTOList[0].giveDepositFlag" value="true" onclick="chkClick(this)"/>送预收款</label>&nbsp;<input type="hidden" name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[1].giftType" value="DEPOSIT" />';
//                            }else{
//                                contents+='<div class="line"><label class="rad"><input  class="giveDepositChk" type="checkbox"  name="promotionsRuleDTOList[0].giveDepositFlag" value="false" onclick="chkClick(this)"/>送预收款</label>&nbsp;<input type="hidden" name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[1].giftType" value="DEPOSIT" />';
//                            }
//                            contents+='<input type="hidden" name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[1].giftName" value="送预收款" />';
//                            contents+='<input value="'+depositAmount+'" name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[1].amount" type="text" class="depositAmountChk txt" style="width:100px;" />&nbsp;元</div> ';
                            contents+='</div>';
                        }else{
                            var idPrefix=i;
                            contents+='<div class="promotion_content" idPrefix="'+idPrefix+'"><a class="icon_close" style="margin: 0px"></a><div class="line">满&nbsp;';
                            contents+='<input type="text" value="'+minAmount+'" name="promotionsRuleDTOList['+idPrefix+'].minAmount" class="txt" style="width:100px;" />&nbsp;<span class="j_over_unit">元</span></div><div class="line">';
//                            contents+= '<input type="checkbox" ';
//                            if(isGift){
//                                contents+='checked ';
//                            }
//                            contents+='name="promotionsRuleDTOList['+idPrefix+'].giveGiftFlag" onclick="chkClick(this)" value="'+ isGift + '"/>送 礼 品</label>&nbsp';
                            contents+= '<input type="hidden" name="promotionsRuleDTOList['+idPrefix+'].promotionsRuleMJSDTOs[0].giftType" value="GIFT">';
                            contents+='<div class="rad">送 <input value="'+giftName+'"  name="promotionsRuleDTOList['+idPrefix+'].promotionsRuleMJSDTOs[0].giftName" type="text" class="J-initialCss txt" initialValue="礼品名称" style="width:100px;" />&nbsp';
                            contents+='<input value="'+giftAmount+'" name="promotionsRuleDTOList['+idPrefix+'].promotionsRuleMJSDTOs[0].amount" type="text" class="J-initialCss txt" initialValue="数量" style="width:60px;" />件</div></div>';
                            contents+='</div>';
                        }
                    }
                    $("#promotionsRuleDiv").append(contents);
                    if(promotionsRuleDTOList.length>=3){
                        $("#addMJSRuleLevel").hide();
                    }
                }
                $("#promotionsLimiterSelector").val(promotionsLimiter);
                $("#promotionsLimiter").val(promotionsLimiter);
                $("#promotionsLimiterSelector").change();
            };





        });

        function chkClick(target){
            var $target=$(target);
            if($target.attr("checked")){
                $target.val("true");
            }else{
                $target.val("false");
            }
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
            <jsp:param name="currPage" value="promotions"/>
            <jsp:param name="biMenu" value="promotionManager"/>
        </jsp:include>

        <div class="bodyLeft">

            <h3 class="title">创建促销——满就送</h3>
            <div class="cuSearch">
                <div class="clear chartStep blue_color">
                    <span class="yellow_color">1、促销设置</span>
                    <a class="stepImg"></a>
                    <span>2、添加上架商品</span>
                    <a class="stepImg"></a>
                    <span>3、促销中的商品</span>
                    <a class="stepImg"></a>
                    <span>4、推广您的促销</span>
                </div>
                <form:form commandName="promotionsDTO" id="promotionsForm" action="promotions.do?method=savePromotions" method="post" name="thisform">
                    <input type="hidden" name="type" value="${promotionsDTO.type}"/>
                    <input type="hidden" id="promotionsId" name="id" value="${promotionsDTO.idStr}"/>
                    <table cellpadding="0" cellspacing="0" class="table_mjs table_promotion">
                        <col width="100" >
                        <col width="150" >
                        <col >
                        <tr>
                            <td class="name_right"><a class="red_color">*</a>促销名称</td>
                            <td colspan="2"><input id="promotionsName" name="name" value="${promotionsDTO.name}" type="text" class="txt txt_color" maxlength="20"/>&nbsp;
                                <a class="right J_promotionsName_tip" style="display: none"></a><span class="gray_color">仅限20个字</span></td>
                        </tr>
                        <tr>
                            <td class="name_right" style="vertical-align:top;">促销描述</td>
                            <td colspan="2"><textarea id="description"  name="description" class="txt txt_color" maxlength="200">${promotionsDTO.description}</textarea>&nbsp;
                                <span class="gray_color" style="vertical-align:top;">仅限200个字</span></td>
                        </tr>
                        <tr>
                            <td class="name_right"><span class="red_color" >*</span>开始时间</td>
                            <td colspan="2">
                                <input  name="startTimeStr" value="${promotionsDTO.startTimeStr}" type="text"  class="time_input startTimeStr txt"  style="width:130px;" />
                            </td>
                        </tr>
                        <tr>
                            <td class="name_right" style="vertical-align:top; padding-top:8px;"><a class="red_color">*</a>活动时间</td>
                            <td colspan="2" class="td_time">
                                <label class="rad"><input type="radio" name="date_select" class="date_select_week date_select"/>7天</label>
                                <label class="rad"><input type="radio" name="date_select" class="date_select_month date_select"/>30天</label>
                                <label class="rad"><input type="radio" name="date_select" class="date_select_three_month date_select"/>90天</label>
                                <label class="rad"><input type="radio" name="date_select" class="date_select_unlimited date_select"/>不限时</label>
                                <span class="yellow_color limited_Date">活动持续时间<span id="cDay" class="red_color">0</span>天<span id="cHour" class="red_color">0</span>时</span>
                                   <span  class="yellow_color un_limited" style="display: none">
                                        活动持续时间不限时
                                     </span>
                                <div>
                                    <input id="serviceStartTime" type="hidden" value="${startTime}" />
                                    <input id="timeFlag" type="hidden" value="${promotionsDTO.timeFlag}"/>
                                    <input type="radio" name="date_select" class="date_select_define date_select"/>
                                    自定义时间
                                    <input value="${promotionsDTO.endTimeStr}" name="endTimeStr" type="text" class="time_input txt" style="display: none;width:130px;" />
                                    <%--<input value="${promotionsDTO.endTimeStr}" name="endTimeStr" type="hidden" />--%>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td class="name_right"><a class="red_color">*</a>促销范围</td>
                            <td colspan="2" id="promotionsRangesTD">
                                <input id="promotionsRanges" type="hidden" name="range" value="PARTLY"/>
                                <label class="rad"><input type="radio" name="promotions_ranges" value="ALL"/>全部商品参与</label>
                                <label class="rad"><input type="radio" name="promotions_ranges" value="PARTLY" checked="true"/>部分商品参与</label>
                            </td>
                        </tr>
                        <tr>
                            <td class="name_right" style="vertical-align:top;"><a class="red_color">*</a>促销内容</td>
                            <td colspan="2">
                                <div class="example gray_color">
                                    <span class="example_name">参考示例：</span>
                                    <div class="exampleList">满&nbsp;<span>100</span>&nbsp;<span class="j_over_unit">元</span>&nbsp;送礼品A&nbsp;<span>1</span>&nbsp;件</div>
                                    <div class="exampleList">满&nbsp;<span>200</span>&nbsp;<span class="j_over_unit">元</span>&nbsp;送礼品B&nbsp;<span>1</span>&nbsp;件</div>
                                    <div class="exampleList">满&nbsp;<span>300</span>&nbsp;<span class="j_over_unit">元</span>&nbsp;送礼品C&nbsp;<span>1</span>&nbsp;件</div>
                                    <div class="exampleList">（满金额为单笔订单金额）</div>
                                </div>
                                <div id="promotionsRuleDiv">
                                    <div class="promotion_content" idPrefix="0">
                                        <input class="promotionsRuleType" type="hidden"
                                               value='${ruleDTO.promotionsRuleType!=null?ruleDTO.promotionsRuleType:""}'
                                               name="ruleDTO[0].promotionsRuleType"/>
                                        <a class="icon_close first_level" style="margin: 0px"></a>
                                        <div class="line">满&nbsp;<input type="text" class="txt" style="width:100px;" name="promotionsRuleDTOList[0].minAmount" />&nbsp;<span class="j_over_unit">元</span>&nbsp;
                                            <%--<input id="promotionsLimiter" name="promotionsLimiter" value="OVER_MONEY" type="hidden" />--%>
                                            <select id="promotionsLimiterSelector" class="promotionsLimiterSelector" name="promotionsLimiter">
                                                <option value="OVER_MONEY">金额（订单金额）</option>
                                                <option value="OVER_AMOUNT">数量</option>
                                            </select>
                                        </div>
                                        <div class="line">
                                            <label class="rad">
                                                    <%--<input class="giveGiftChk" type="checkbox" name="promotionsRuleDTOList[0].giveGiftFlag" onclick="chkClick(this)" value="false"/>送 礼 品</label>&nbsp;--%>
                                                送 <input type="hidden" name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[0].giftType" value="GIFT" />
                                                <input  name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[0].giftName" class="J-initialCss giftNameChk txt" initialValue="礼品名称" type="text" style="width:100px;" />&nbsp;
                                                <input  name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[0].amount" type="text" class="giftAmount J-initialCss txt" initialValue="数量" style="width:60px;" /> 件
                                        </div>
                                            <%--<div class="line">--%>
                                            <%--<label class="rad">--%>
                                            <%--<input  class="giveDepositChk" type="checkbox"  name="promotionsRuleDTOList[0].giveDepositFlag" value="false" onclick="chkClick(this)"/>送预收款--%>
                                            <%--</label>&nbsp;--%>
                                            <%--<input type="hidden" name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[1].giftType" value="DEPOSIT" />--%>
                                            <%--<input type="hidden" name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[1].giftName" value="送预收款" />--%>
                                            <%--<input name="promotionsRuleDTOList[0].promotionsRuleMJSDTOs[1].amount" type="text" class="depositAmountChk txt" style="width:100px;" />&nbsp;元--%>
                                            <%--</div>--%>
                                    </div>
                                </div>
                                <div class="promotion_content_add add_content" id="addMJSRuleLevel">
                                    +增加层级+
                                </div>
                            </td>
                        </tr>
                    </table>
                <div class="clear i_height"></div>
                <div class="divTit" style="margin-left:211px;">
                    <a id="savePromotionsBtn" class="button"   pageType="manageMJS">保存促销</a>
                    <a id="nextToAddPromotionsProduct" class="button"  pageType="manageMJS">下一步</a>
                </div>
                </form:form>
            </div>
        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>