<%@ page import="com.bcgogo.txn.dto.PromotionsDTO" %>
<%@ page import="com.bcgogo.txn.dto.PromotionsRuleDTO" %>
<%@ page import="java.util.List" %>
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

        $().ready(function(){
            $("input[name='promotions_rule']").click(function(){
                $(".promotionsRuleType").val($(this).val());
                $("#promotionsRuleType").val($(this).val());
                $("[name^='EXAMPLE_']").hide();
                $("#addMLJRuleLevel").show();
                var promotionsRuleType=$(this).val();
                if(promotionsRuleType=="DISCOUNT_FOR_OVER_MONEY"){
                    $(".t2").text("元");
                    $(".t3").text("打");
                    $(".t4").text("折");
                    $("[name='EXAMPLE_DISCOUNT_FOR_OVER_MONEY']").show();
                }else if(promotionsRuleType=="DISCOUNT_FOR_OVER_AMOUNT"){
                    $(".t2").text("件");
                    $(".t3").text("打");
                    $(".t4").text("折");
                    $("[name='EXAMPLE_DISCOUNT_FOR_OVER_AMOUNT']").show();
                }else if(promotionsRuleType=="REDUCE_FOR_OVER_MONEY"){
                    $(".t2").text("元");
                    $(".t3").text("减");
                    $(".t4").text("元");
                    $("[name='EXAMPLE_REDUCE_FOR_OVER_MONEY']").show();
                }else if(promotionsRuleType=="REDUCE_FOR_OVER_AMOUNT"){
                    $(".t2").text("件");
                    $(".t3").text("减");
                    $(".t4").text("元");
                    $("[name='EXAMPLE_REDUCE_FOR_OVER_AMOUNT']").show();
                }
            });
            if(G.isEmpty($("#promotionsId").val())){
                $(".promotions_rule_default").click();
            }else{
                var promotionsRuleType=$(".promotionsRuleType").val();
                $("input[name='promotions_rule']").each(function(){
                    if($(this).val()==promotionsRuleType){
                        $(this).click();
//                        $(this).attr("checked",true);
                    }
                });

            }

        });
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
            <h3 class="title">创建促销——满立减</h3>
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
                    <input id="promotionsType" type="hidden" name="type" value="${promotionsDTO.type}"/>
                    <input id="promotionsId" type="hidden" name="id" value="${promotionsDTO.idStr}"/>
                    <table cellpadding="0" cellspacing="0" class="table_mlj table_promotion">
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
                            <td class="name_right"><a class="red_color">*</a>开始时间</td>
                            <td colspan="2">
                                <input value="${promotionsDTO.startTimeStr}" name="startTimeStr" type="text" class="time_input txt"  style="width:120px;" />
                            </td>
                        </tr>
                        <tr>
                            <td class="name_right" style="vertical-align:top; padding-top:8px;"><a class="red_color">*</a>结束时间</td>
                            <td colspan="2" class="td_time">
                                <input id="timeFlag" type="hidden" value="${promotionsDTO.timeFlag}"/>
                                <label class="rad"><input type="radio" name="date_select" class="date_select_week date_select" />7天</label>
                                <label class="rad"><input type="radio" name="date_select" class="date_select_month date_select"/>30天</label>
                                <label class="rad"><input type="radio" name="date_select" class="date_select_three_month date_select"/>90天</label>
                                <label class="rad"><input type="radio" name="date_select" class="date_select_unlimited date_select"/>不限时</label>
                                <span class="yellow_color limited_Date"> 活动持续时间
                                        <span class="red_color" id="cDay">0</span>天
                                        <span class="red_color" id="cHour">0</span>时
                                    </span>
                                     <span  class="yellow_color un_limited" style="display: none">
                                        活动持续时间不限时
                                     </span>
                                <div>
                                    <input id="serviceStartTime" type="hidden" value="${startTime}" />
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
                                    <%--<label class="rad"><input type="radio" name="promotions_ranges" value="EXCEPT" />部分商品不参与</label>--%>
                            </td>
                        </tr>
                        <tr>
                            <td class="name_right" style="vertical-align:top;"><a class="red_color">*</a>促销内容</td>
                            <td colspan="2">
                                <div>
                                    <input id="promotionsRuleType" type="hidden" />
                                    <div>
                                        <label class="rad"><input type="radio" checked="true" class="promotions_rule_default" name="promotions_rule" value="DISCOUNT_FOR_OVER_MONEY"/>满金额打折扣</label>
                                        <label class="rad"><input type="radio" name="promotions_rule" value="DISCOUNT_FOR_OVER_AMOUNT"/>满数量打折扣</label>
                                    </div>
                                    <div style="margin-top: 2px">
                                        <label class="rad"><input type="radio" name="promotions_rule" value="REDUCE_FOR_OVER_MONEY"/>满金额减金额</label>
                                        <label class="rad"><input type="radio" name="promotions_rule" value="REDUCE_FOR_OVER_AMOUNT"/>满数量减金额</label>
                                    </div>
                                </div>
                                <div class="example gray_color" name="EXAMPLE_DISCOUNT_FOR_OVER_MONEY">
                                    <span class="example_name">参考示例：</span>
                                    <div class="exampleList">满&nbsp;<span>100</span>&nbsp;元&nbsp;打&nbsp;<span>9</span>&nbsp;折</div>
                                    <div class="exampleList">满&nbsp;<span>200</span>&nbsp;元&nbsp;打&nbsp;<span>8</span>&nbsp;折</div>
                                    <div class="exampleList">满&nbsp;<span>300</span>&nbsp;元&nbsp;打&nbsp;<span>7</span>&nbsp;折</div>
                                    <div class="exampleList">（满金额为单笔订单金额）</div>
                                </div>
                                <div class="example gray_color" name="EXAMPLE_DISCOUNT_FOR_OVER_AMOUNT" style="display: none">
                                    <span class="example_name">参考示例：</span>
                                    <div class="exampleList">满&nbsp;<span>100</span>&nbsp;件&nbsp;打&nbsp;<span>9</span>&nbsp;折</div>
                                    <div class="exampleList">满&nbsp;<span>200</span>&nbsp;件&nbsp;打&nbsp;<span>8</span>&nbsp;折</div>
                                    <div class="exampleList">满&nbsp;<span>300</span>&nbsp;件&nbsp;打&nbsp;<span>7</span>&nbsp;折</div>
                                    <div class="exampleList">（件为设置时的通用单</div>
                                    <div class="exampleList">位打折时会以商品的</div>
                                    <div class="exampleList">销售单位为准）</div>
                                </div>
                                <div class="example gray_color" name="EXAMPLE_REDUCE_FOR_OVER_MONEY" style="display: none">
                                    <span class="example_name">参考示例：</span>
                                    <div class="exampleList">满&nbsp;<span>100</span>&nbsp;元&nbsp;减&nbsp;<span>10</span>&nbsp;元</div>
                                    <div class="exampleList">满&nbsp;<span>200</span>&nbsp;元&nbsp;减&nbsp;<span>25</span>&nbsp;元</div>
                                    <div class="exampleList">满&nbsp;<span>300</span>&nbsp;元&nbsp;减&nbsp;<span>40</span>&nbsp;元</div>
                                    <div class="exampleList">（满金额为单笔订单金额）</div>
                                </div>
                                <div class="example gray_color" name="EXAMPLE_REDUCE_FOR_OVER_AMOUNT" style="display: none">
                                    <span class="example_name">参考示例：</span>
                                    <div class="exampleList">满&nbsp;<span>100</span>&nbsp;件&nbsp;减&nbsp;<span>10</span>&nbsp;元</div>
                                    <div class="exampleList">满&nbsp;<span>200</span>&nbsp;件&nbsp;减&nbsp;<span>25</span>&nbsp;元</div>
                                    <div class="exampleList">满&nbsp;<span>300</span>&nbsp;件&nbsp;减&nbsp;<span>40</span>&nbsp;元</div>
                                    <div class="exampleList">（满金额为单笔订单金额）</div>
                                </div>

                                <div id="promotionsRuleDiv">
                                    <c:if test="${empty promotionsDTO.promotionsRuleDTOList}">
                                        <div class="promotion_content">
                                            <div class="line beforeEdit">
                                                <input class="promotionsRuleType" type="hidden" value="" name="promotionsRuleDTOList[0].promotionsRuleType"/>
                                                <input type="hidden" class="ruleLevel" name="promotionsRuleDTOList[0].level" value="0"/>
                                                <a class="icon_close"></a>
                                                <span>满</span>&nbsp;<input type="text" name="promotionsRuleDTOList[0].minAmount" class="txt txt_color minAmount" style="width:50px;" />&nbsp;<span class="t2">元</span>，
                                                <span class="t3">打</span>&nbsp;<input type="text" name="promotionsRuleDTOList[0].discountAmount" class="txt txt_color discountAmount" style="width:50px;" />&nbsp;<span class="t4">折</span>
                                            </div>

                                        </div>
                                    </c:if>
                                    <c:if test="${not empty promotionsDTO.promotionsRuleDTOList}">
                                        <c:forEach items="${promotionsDTO.promotionsRuleDTOList}" var="ruleDTO" varStatus="status">
                                            <div class="promotion_content">
                                                <div class="line beforeEdit">
                                                    <input class="promotionsRuleType" type="hidden"
                                                           value='${ruleDTO.promotionsRuleType!=null?ruleDTO.promotionsRuleType:""}'
                                                           name="promotionsRuleDTOList[${status.index}].promotionsRuleType"/>
                                                    <input type="hidden" class="ruleLevel" name="promotionsRuleDTOList[${status.index}].level" value="${status.index}"/>
                                                    <a class="icon_close"></a>
                                                    <span>满</span>&nbsp;<input type="text" name="promotionsRuleDTOList[${status.index}].minAmount"
                                                                               value='${ruleDTO.minAmount!=null?ruleDTO.minAmount:""}'
                                                                               class="txt txt_color minAmount" style="width:50px;" />&nbsp;<span class="t2">元</span>，
                                                    <span class="t3">打</span>&nbsp;<input type="text" name="promotionsRuleDTOList[${status.index}].discountAmount"
                                                                                          value='${ruleDTO.discountAmount!=null?ruleDTO.discountAmount:""}'
                                                                                          class="txt txt_color discountAmount" style="width:50px;" />&nbsp;<span class="t4">折</span>
                                                </div>

                                            </div>
                                        </c:forEach>
                                    </c:if>
                                </div>

                                <div class="promotion_content_add add_content" id="addMLJRuleLevel">
                                    +增加层级+
                                </div>
                            </td>
                        </tr>
                    </table>

                <div class="clear i_height"></div>
                <div class="divTit" style="margin-left:211px;">
                    <a id="savePromotionsBtn" class="button" pageType="manageMLJ">保存促销</a>
                    <a id="nextToAddPromotionsProduct" class="button" pageType="manageMLJ">下一步</a>
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