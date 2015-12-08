<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>保险理赔</title>

    <%--<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>--%>
    <link rel="stylesheet" type="text/css" href="styles/addPlan<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <style type="text/css">
        body {
            color: #FFFFFF;
            font-family: "宋体", Arial;
            font-size: 12px;
        }
        .Scroller-Container a.hover, .Scroller-Containerheader a.hover{
            margin:0;
        }
        ul li
        {
            float:none;
        }
    </style>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/insuranceInfo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/invoiceCustomerSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/vehiclelicenceNo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/invoicesolr<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"VEHICLE_CONSTRUCTION_INSURANCE");
        <c:choose>
        <c:when test="${not empty insuranceOrderDTO.id}">
        defaultStorage.setItem(storageKey.MenuCurrentItem,"编辑");
        </c:when>
        <c:otherwise>
        defaultStorage.setItem(storageKey.MenuCurrentItem,"新增");
        </c:otherwise>
        </c:choose>
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
<bcgogo:permission>
    <bcgogo:if resourceType="logic" permissions="WEB.VERSION.DISABLE_VEHICLE_CONSTRUCTION">
<%--        <jsp:include page="txnNavi.jsp">
            <jsp:param name="currPage" value="repair"/>
        </jsp:include>--%>
        <jsp:include page="vehicleConstructionNavi.jsp">
            <jsp:param name="currPage" value="insuranceOrder"/>
        </jsp:include>
    </bcgogo:if>
    <bcgogo:else>
        <jsp:include page="vehicleNavi.jsp">
            <jsp:param name="currPage" value="insuranceOrder"/>
        </jsp:include>
    </bcgogo:else>
</bcgogo:permission>


<div class="titBody">
<c:if test="${!empty result}">
<input type="hidden" id="insuranceMessage" value="${result.msg}" resultDate="${result.data}"
       resultOperation="${result.operation}" success="${result.success}">
</c:if>
<form:form action="insurance.do?method=createInsuranceOrder" id="insuranceOrderForm" commandName="insuranceOrderDTO"   class="J_leave_page_prompt"
           method="post">
<input type="hidden" id="orderType" value="insuranceOrder">
<form:hidden path="id"/>
<form:hidden path="repairOrderId"/>
<form:hidden path="repairDraftOrderId"/>
<form:hidden path="repairOrderReceiptNo"/>
<form:hidden path="status"/>
<div class="divTit">
    保险单号<img src="images/star.jpg" class="i_tableStar" style="margin-right:5px;"><form:input path="policyNo" cssClass="txt" maxlength="40"/>
</div>
<div class="divTit">
    报案编号 <form:input path="reportNo" cssClass="txt" maxlength="40"/>
</div>
<div class="divTit">
    保险公司
    <form:select path="insuranceCompanyId" items="${insuranceCompanyDTOs}" cssStyle="width: 150px"
                       itemLabel="name" itemValue="id"/>
    <form:hidden path="insuranceCompany"/>
</div>
<div class="divTit">
    投保日期<img src="images/star.jpg" class="i_tableStar" style="margin-right:5px;"><form:input path="insureStartDateStr" cssClass="txt" readonly="readonly"/>
</div>
<div class="divTit">
    到期日期<img src="images/star.jpg" class="i_tableStar" style="margin-right:5px;"><form:input path="insureEndDateStr" cssClass="txt" readonly="readonly"/>
</div>
<div class="clear i_height"></div>
<div class="wordTitle">索赔信息</div>
<div class="clear"></div>
<div class="tuihuo_first">
    <span class="left_tuihuo"></span>
    <table cellpadding="0" cellspacing="0" class="tabClaims">
        <colgroup>
            <col width = "65" />
            <col width = "135" />
            <col width = "65" />
            <col width = "135" />
            <col width = "65" />
            <col width = "135" />
            <col width = "65" />
            <col width = "135" />
            <col width = "65" />
            <col width = "135" />
        </colgroup>
        <tr>
            <td>被保险人<img src="images/star.jpg" class="i_tableStar"></td>
            <td>
                <form:input path="customer" cssClass="txt" maxlength="20"/>
                <form:hidden path="customerId"/>
            </td>
            <td>车 牌 号<img src="images/star.jpg" class="i_tableStar"></td>
            <td>
                <form:input path="licenceNo" cssClass="txt" maxlength="20"/>
                <form:hidden path="vehicleId"/>
            </td>
            <td>驾 驶 人</td>
            <td><form:input path="driver" cssClass="txt" maxlength="20"/></td>
            <td>驾驶证号</td>
            <td><form:input path="drivingNo" cssClass="txt" maxlength="20"/></td>
            <td>手　　机</td>
            <td><form:input path="mobile" cssClass="txt" maxlength="20"/></td>
        </tr>
        <tr>
            <td>车辆品牌<img src="images/star.jpg" class="i_tableStar"></td>
            <td><form:input path="brand" cssClass="txt" maxlength="20"/></td>
            <td>车型<img src="images/star.jpg" class="i_tableStar"></td>
            <td><form:input path="model" cssClass="txt" maxlength="20"/></td>
            <td>发动机号<img src="images/star.jpg" class="i_tableStar"></td>
            <td><form:input path="engineNumber" cssClass="txt" maxlength="40"/></td>
            <td>车 架 号<img src="images/star.jpg" class="i_tableStar"></td>
            <td colspan="5"><form:input path="chassisNumber" cssClass="txt" maxlength="40"/></td>
        </tr>
        <tr>
            <td>报 案 人</td>
            <td><form:input path="reporter" cssClass="txt" maxlength="20"/></td>
            <td>联系方式</td>
            <td><form:input path="reporterContact" cssClass="txt" maxlength="20"/></td>
            <td>报案时间</td>
            <td><form:input path="reportDateStr" cssClass="txt" readonly="readonly"/></td>
            <td>出险时间</td>
            <td><form:input path="accidentDateStr" cssClass="txt" readonly="readonly"/></td>
            <td>出险地点</td>
            <td><form:input path="accidentAddress" cssClass="txt" maxlength="80"/></td>
        </tr>
    </table>
    <span class="right_tuihuo"></span>
</div>

<div class="clear i_height"></div>
<div class="wordTitle">查勘意见</div>
<div class="clear"></div>
<div class="tuihuo_first">
    <span class="left_tuihuo"></span>
    <table cellpadding="0" cellspacing="0" class="tabClaims">
        <col width="65">
        <col width="125">
        <col width="85">
        <col width="80">
        <col width="80">
        <col width="80">
        <col width="100">
        <col width="100">
        <col width="80">
        <col width="60">
        <tr>
            <td>查勘时间</td>
            <td><form:input path="surveyDateStr" cssClass="txt" readonly="readonly"/></td>
            <td>事故处理方式</td>
            <td><label><form:radiobutton path="accidentHandling" value="交警"/>交警</label></td>
            <td><label><form:radiobutton path="accidentHandling" value="自行协商"/>自行协商</label></td>
            <td><label><form:radiobutton path="accidentHandling" value="保险公司"/>保险公司</label></td>
            <td><label><form:radiobutton path="accidentHandling" value="其他"/>其他</label></td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>查勘地点</td>
            <td><form:input path="surveyAddress" cssClass="txt" maxlength="80"/></td>
            <td>事故责任</td>
            <td><label><form:radiobutton path="accidentLiability" value="单方肇事"/>单方肇事</label></td>
            <td><label><form:radiobutton path="accidentLiability" value="全部"/>全部</label></td>
            <td><label><form:radiobutton path="accidentLiability" value="主要"/>主要</label></td>
            <td><label><form:radiobutton path="accidentLiability" value="同等"/>同等</label></td>
            <td><label><form:radiobutton path="accidentLiability" value="次要"/>次要</label></td>
            <td colspan="3"><label><form:radiobutton path="accidentLiability" value="无责"/>无责</label></td>
        </tr>
        <tr>
            <td colspan="2"><label>是否第一现场勘查</label>
                <label><form:radiobutton path="firstSurveyAddress" value="是"/>是</label>
                <label><form:radiobutton path="firstSurveyAddress" value="否"/>否</label></td>
            <td>事故类型</td>
            <td><label><form:radiobutton path="accidentType" value="单方车损"/>单方车损</label></td>
            <td colspan="2"><label><form:radiobutton path="accidentType" value="涉及第三方车/物损失"/>涉及第三方车/物损失</label></td>
            <td colspan="5"><label><form:radiobutton path="accidentType" value="涉及本车/第三者人员伤亡"/>涉及本车/第三者人员伤亡</label></td>
        </tr>
        <tr>
            <td colspan="11"><label style="vertical-align:text-top;">查勘人员意见</label>
                <form:textarea path="surveyOpinion" cssClass="txt" cssStyle="width:88%; height:50px;" maxlength="400"></form:textarea>
            </td>
        </tr>
        <tr>
            <td>出险原因</td>
            <td><label style="padding-right:30px;"><form:radiobutton path="accidentCause" value="碰撞"/>碰撞</label>
                <label><form:radiobutton path="accidentCause" value="倾覆"/>倾覆</label>
            </td>
            <td><label><form:radiobutton path="accidentCause" value="坠落"/>坠落</label></td>
            <td><label><form:radiobutton path="accidentCause" value="火灾"/>火灾</label></td>
            <td><label><form:radiobutton path="accidentCause" value="爆炸"/>爆炸</label></td>
            <td><label><form:radiobutton path="accidentCause" value="自燃"/>自燃</label></td>
            <td><label><form:radiobutton path="accidentCause" value="外界物体倒塌"/>外界物体倒塌</label></td>
            <td><label><form:radiobutton path="accidentCause" value="外界物体坠落"/>外界物体坠落</label></td>
            <td><label><form:radiobutton path="accidentCause" value="自然灾害"/>自然灾害</label></td>
            <td><label><form:radiobutton path="accidentCause" value="其他"/>其他</label></td>
        </tr>
        <tr>
            <td>涉及险种</td>
            <td><label style="width:145px;"><form:checkbox path="relateInsuranceItems" value="交通事故责任强制保险"/>交通事故责任强制保险</label></td>
            <td><label><form:checkbox path="relateInsuranceItems" value="车损险"/>车损险</label></td>
            <td><label><form:checkbox path="relateInsuranceItems" value="三者险"/>三者险</label></td>
            <td colspan="2"><label><form:checkbox path="relateInsuranceItems" value="车上人员责任险"/>车上人员责任险</label></td>
            <td><label><form:checkbox path="relateInsuranceItems" value="车身划痕险"/>车身划痕险</label></td>
            <td colspan="2"><label><form:checkbox path="relateInsuranceItems" value="玻璃单独破碎险"/>玻璃单独破碎险</label></td>
            <td><label><form:checkbox path="relateInsuranceItems" value="其他"/>其他</label></td>
        </tr>
    </table>
    <span class="right_tuihuo"></span>
</div>

<div class="clear i_height"></div>
<div class="wordTitle">损失情况</div>
<div class="clear"></div>
<div class="tuihuo_first add_claims">
    <span class="left_tuihuo"></span>
    <table cellpadding="0" cellspacing="0" class="tabClaims">
        <col width="40">
        <col width="90">
        <col width="40">
        <col width="40">
        <col width="40">
        <col width="240">
        <tr>
            <td>定损时间</td>
            <td><form:input path="estimateDateStr" cssClass="txt" readonly="readonly"/></td>
            <td>定损地点</td>
            <td><form:input path="estimateAddress" cssClass="txt" maxlength="80"/></td>
            <td>施救费</td>
            <td><form:input path="insuranceCost" cssClass="txt checkNumberEmpty" maxlength="10"/></td>
        </tr>
        <tr>
            <td colspan="4"><label>残值处理方式</label>
                <label><form:radiobutton path="scrapApproach" value="统一回收"/>统一回收</label>
                <label><form:radiobutton path="scrapApproach" value="作价折归被保险人"/>作价折归被保险人</label>
            </td>
            <td>扣减残值</td>
            <td><form:input path="scrapValue" cssClass="txt checkNumberEmpty" maxlength="10"/></td>
        </tr>
    </table>

    <div class="wordTitle">更换项目</div>
    <div class="clear"></div>
    <div class="divSlip">
        <div style="width:40px; padding-left:4px;">序号</div>
        <div style="width:106px;">商品编号</div>
        <div style="width:150px;">更换部件名称</div>
        <div style="width:137px;">品牌/产地</div>
        <div style="width:138px;">规格</div>
        <div style="width:130px;">型号</div>
        <div style="width:58px;">单价</div>
        <div style="width:55px;">数量</div>
        <div style="width:52px;">单位</div>
        <div style="width:70px;">小计</div>
        <div style="width:30px;">操作</div>
    </div>
    <table cellpadding="0" cellspacing="0" class="tabSlip tabPick " id="insuranceItemTB">
        <col width="40">
        <col width="100">
        <col width="155">
        <col width="135">
        <col width="135">
        <col width="130">
        <col width="60">
        <col width="60">
        <col width="50">
        <col width="70">
        <col width="45">
        <c:forEach items="${insuranceOrderDTO.itemDTOs}" var="itemDTO" varStatus="status">
            <tr class="item">
                <td style="padding-left:10px;">${status.index+1}</td>
                <td><form:input path="itemDTOs[${status.index}].commodityCode" value='${itemDTO.commodityCode}'
                                class="txt checkStringEmpty" title='${itemDTO.commodityCode}' maxlength="20"/>
                    <form:hidden path="itemDTOs[${status.index}].id" value="${itemDTO.id}"/>
                    <form:hidden path="itemDTOs[${status.index}].productId" value="${itemDTO.productId}"/>
                    <form:hidden path="itemDTOs[${status.index}].vehicleBrand" value="${itemDTO.vehicleBrand}"/>
                    <form:hidden path="itemDTOs[${status.index}].vehicleModel" value="${itemDTO.vehicleModel}"/>
                </td>
                <td><form:input path="itemDTOs[${status.index}].productName" value="${itemDTO.productName}"
                                            cssClass="txt checkStringEmpty" title="${itemDTO.productName}" maxlength="50"/></td>
                <td><form:input path="itemDTOs[${status.index}].brand" value="${itemDTO.brand}"
                                            cssClass="txt checkStringEmpty" title="${itemDTO.brand}" maxlength="50"/></td>
                <td><form:input path="itemDTOs[${status.index}].spec" value="${itemDTO.spec}"
                                            cssClass="txt checkStringEmpty" title="${itemDTO.spec}" maxlength="50"/></td>
                <td> <form:input path="itemDTOs[${status.index}].model" value="${itemDTO.model}"
                                            cssClass="txt checkStringEmpty" title="${itemDTO.model}" maxlength="50"/></td>
                <td> <form:input path="itemDTOs[${status.index}].price" value="${itemDTO.price}"
                                            cssClass="txt itemPrice checkNumberEmpty" title="${itemDTO.price}" data-filter-zero="true"/></td>
                <td> <form:input path="itemDTOs[${status.index}].amount" value="${itemDTO.amount}"
                                            cssClass="itemAmount txt checkNumberEmpty" title="${itemDTO.amount}" data-filter-zero="true"/></td>
                <td ><label name="itemDTOs[${status.index}].unitLbl" id="itemDTOs${status.index}.unit">${itemDTO.unit}</label>
                    <form:hidden path="itemDTOs[${status.index}].unit" value="${itemDTO.unit}"
                                cssClass="itemUnit" title="${itemDTO.unit}"/>
                    <form:hidden path="itemDTOs[${status.index}].storageUnit" value="${itemDTO.storageUnit}"
                                 cssClass="itemStorageUnit"/>
                    <form:hidden path="itemDTOs[${status.index}].sellUnit" value="${itemDTO.sellUnit}"
                                 cssClass="itemSellUnit"/>
                    <form:hidden path="itemDTOs[${status.index}].rate" value="${itemDTO.rate}" cssClass="itemRate"/></td>
                <td>
                    <label name="itemDTOs[${status.index}].totalLbl" id="itemDTOs${status.index}.totalLbl">${itemDTO.total}</label>
                    <form:hidden path="itemDTOs[${status.index}].total" value="${itemDTO.total}" cssClass="itemTotal" /></td>
                <td>
                    <input id="itemDTOs${status.index}.itemMinusBtn" name="itemDTOs[${status.index}].itemMinusBtn"
                           class="itemMinusBtn" type="button" onfocus="this.blur();">
                    <input id="itemDTOs${status.index}.itemPlusBtn" name="itemDTOs[${status.index}].itemPlusBtn"
                           class="itemPlusBtn" type="button" onfocus="this.blur();">
                </td>
            </tr>
        </c:forEach>
        <tr style="display: none"></tr>
    </table>
    <div class="i_height"></div>
    <div class="wordTitle">修理项目</div>
    <div class="clear"></div>
    <div class="divSlip">
        <div style="width:140px; padding-left:10px;">序号</div>
        <div style="width:640px;">修理项目名称</div>
        <div style="width:150px;">金额</div>
        <div style="width:30px;">操作</div>
    </div>
    <table cellpadding="0" cellspacing="0" class="tabSlip tabPick" id="insuranceServiceTB">
        <col width="150">
        <col>
        <col width="150">
        <col width="45">
        <c:forEach items="${insuranceOrderDTO.serviceDTOs}" var="serviceDTO" varStatus="status">
            <tr class="service">
                <td style="padding-left:10px;">${status.index+1}</td>
                <td>
                    <form:input path="serviceDTOs[${status.index}].service" value="${serviceDTO.service}"
                                class="txt checkStringEmpty" title="${serviceDTO.service}" size="50"/>
                    <form:hidden path="serviceDTOs[${status.index}].id" value="${serviceDTO.id}"/>
                    <form:hidden path="serviceDTOs[${status.index}].serviceId" value="${serviceDTO.serviceId}"/>
                </td>
                <td>
                    <form:input path="serviceDTOs[${status.index}].total" value="${serviceDTO.total}"
                                cssClass="serviceTotal txt checkNumberEmpty" title="${serviceDTO.total}" data-filter-zero="true"/>
                </td>
                <td>
                    <input id="serviceDTOs${status.index}.serviceMinusBtn" name="serviceDTOs[${status.index}].serviceMinusBtn"
                           class="serviceMinusBtn" type="button" onfocus="this.blur();">
                    <input id="serviceDTOs${status.index}.servicePlusBtn" name="serviceDTOs[${status.index}].servicePlusBtn"
                           class="servicePlusBtn" type="button" onfocus="this.blur();">
                </td>
            </tr>
        </c:forEach>
        <tr style="display: none"></tr>
    </table>
    <div class="wordTitle">换件及修理费用总计</div>
    <div class="wordTitle">
        理赔金额<form:input path="claims" cssClass="txt checkNumberEmpty" maxlength="10"/>&nbsp;&nbsp;&nbsp;&nbsp;
        赔付比例<form:input path="claimsPercentage" cssClass="txt" maxlength="5"/>&nbsp;%&nbsp;&nbsp;&nbsp;&nbsp;
        个人赔付金额<form:input path="personalClaims" cssClass="txt"  maxlength="10"/>&nbsp;&nbsp;&nbsp;&nbsp;
        个人赔付比例<form:input path="personalClaimsPercentage" cssClass="txt" maxlength="5"/>&nbsp;%
    </div>
    <div class="clear i_height"></div>
    <span class="right_tuihuo"></span>
</div>

<div class="height"></div>
<div class="invalidImgLeftShow">

  <div class="btn_div_Img" style="margin:-12px 0 20px 0;">
        <input id="saveInsurance" class="i_savedraft" type="button" onfocus="this.blur();" value="">
        <div class="sureWords">保&nbsp;存</div>
  </div>
  <div class="btn_div_Img" style="margin:-12px 0 20px 0;">
        <input id="cancelBtn" class="cancel j_btn_i_operate" type="button" onfocus="this.blur();">
        <div class="sureWords">重&nbsp;置</div>
  </div>
    <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION_INSURANCE.PRINT">
    <div class="btn_div_Img" id="print_div">
        <input id="print_insurance_btn" class="print j_btn_i_operate" type="button" onfocus="this.blur();">
        <div class="sureWords">打印</div>
    </div>
    </bcgogo:hasPermission>






</div>
<div class="shopping_btn">
    <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.SAVE&&WEB.VEHICLE_CONSTRUCTION_INSURANCE_GENERATING_CONSTRUCTION">
        <c:if test="${not empty insuranceOrderDTO.repairOrderId || not empty insuranceOrderDTO.repairDraftOrderId}">
            <div class="btn_div_Img" style="margin-left: 20px;width:100px;">
                <input id="toRepairOrder" class="sureInventory j_btn_i_operate" type="button" onfocus="this.blur();">
                <div class="sureWords" style="width:100%;">查看施工单</div>
                <div class="sureWords" title="单号${insuranceOrderDTO.repairOrderReceiptNo}">单号${insuranceOrderDTO.repairOrderReceiptNo}</div>
            </div>
        </c:if>
        <c:if test="${empty insuranceOrderDTO.repairOrderId && empty insuranceOrderDTO.repairDraftOrderId}">
            <div class="btn_div_Img" style="margin-left: 20px;width:60px;">
                <input id="toRepairOrder" class="sureInventory j_btn_i_operate" type="button" onfocus="this.blur();">
                <div class="sureWords" style="width:100%;">生成施工单</div>
            </div>
        </c:if>
    </bcgogo:hasPermission>




    <div id="account_div" class="btn_div_Img" style="display: block;">
        <input id="settledInsurance" class="saleAccount j_btn_i_operate" type="button" onfocus="this.blur();">
        <div class="optWords">结算</div>
    </div>



    <c:if test="${not empty insuranceOrderDTO.statusStr}">
        <div class="invalidImg" style="display: block;">
            <input type="button" onfocus="this.blur();" id="nullifyInsurance">
            <div class="invalidWords">作废</div>
        </div>
    </c:if>

    <c:if test="${not empty insuranceOrderDTO.status}">
        <div class="invalidImg" style="display: block;">
            <input type="button" onfocus="this.blur();" id="nullifyInsurance">
            <div class="invalidWords">作废</div>
        </div>
    </c:if>




</div>

</div>
</form:form>
</div>

<div id="mask" style="display:block;position: absolute;">
</div>
<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id"></div>
</div>

<!-- 车牌号下拉菜单 -->
<div id="div_brandvehiclelicenceNo" class="i_scroll" style="display:none;width:132px;">
    <div class="Container" style="width:132px;">
        <div id="Scroller-1licenceNo" style="width:132px;">
            <div class="Scroller-Containerheader" id="Scroller-Container_idlicenceNo">
            </div>
        </div>
    </div>
</div>
<iframe id="iframe_PopupBox"
		style="position:absolute;z-index:6;top:210px;left:87px;display:none;overflow-x:hidden;overflow-y:auto; "
		allowtransparency="true" width="1000px" height="100%" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:6;top:210px;left:87px;display:none; "
		allowtransparency="true" width="1000px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_2"
		style="position:absolute;z-index:6;top:210px;left:87px;display:none;  background: none repeat scroll;"
		allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBoxMakeTime" style="position:absolute;z-index:10;display:none; " allowtransparency="true"
		width="350px" height="500px" frameborder="0" src="" scrolling="no"></iframe>
<input type="button" id="idAddRow" value="确定" style="display:none;"
	   onclick="addOneRow()"/> <%--------------------欠款结算  2011-12-14-------------------------------%>
<iframe id="iframe_qiankuan" style="position:absolute; left:0px; top:300px; display:none;z-index:8;"
		allowtransparency="true" width="1000px" height="900px" frameborder="0" src="" scrolling="no">
</iframe>

<iframe id="iframe_PopupBox_account" style="position:absolute;z-index:5; left:500px; top:300px; display:none;"
		allowtransparency="true" width="900px" height="450px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_CardList" style="position:absolute;z-index:9; left:300px; top:200px; display:none;"
		allowtransparency="true" width="850px" height="300px" frameborder="0" src=""></iframe>
<iframe id="iframe_buyCard" style="position:absolute;z-index:7; left:300px; top:10px; display:none;"
		allowtransparency="true" width="800px" height="740px" scrolling="no" frameborder="0" src=""></iframe>

<iframe id="iframe_moreUserInfo"
		style="position:absolute;z-index:7; left:200px; top:200px; display:none;overflow:hidden;"
		allowtransparency="true" width="840px" height="600px" frameborder="0" scrolling="no" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>