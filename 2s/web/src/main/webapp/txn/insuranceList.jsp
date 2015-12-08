<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>保险理赔</title>

    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addPlan<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addWarehouse<%=ConfigController.getBuildVersion()%>.css">
    <style type="text/css">
        a:link{text-decoration:none;}
        a:visited{text-decoration:none;}
        a:hover{text-decoration:underline;}
    </style>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/insuranceList<%=ConfigController.getBuildVersion()%>.js"></script>
    <style type="text/css">
        .hover_txt{
            border:#ff4747 1px solid;
        }
    </style>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <bcgogo:permission>
        <bcgogo:if resourceType="logic" permissions="WEB.VERSION.DISABLE_VEHICLE_CONSTRUCTION">
         <%--   <jsp:include page="txnNavi.jsp">
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

    <div class="titBody ">
        <form:form action="insurance.do?method=showInsuranceOrderList" id="thisform" name="thisform" method="post"
                   commandName="searchCondition">
            <input type="hidden" id="pageNo" name="pageNo" value="1"/>
            <input type="hidden" id="sortStatus" name="sortStatus" value="${searchCondition.sortStatus}"/>



              <div class="lineTitle">查询条件
              </div>

            <div class="lineBody bodys">

                <div class="divTit">保险单号：
                    <form:input path="policyNo" cssClass="txt"/></div>
                <div class="divTit">保险公司：
                    <form:select path="insuranceCompanyId" name="insuranceCompanyId" cssStyle="width:150px">
                        <option value="">--所有--</option>
                        <form:options items="${insuranceCompanyDTOs}" itemValue="id" itemLabel="name"/>
                    </form:select>
                </div>
                <div class="divTit">车牌号：
                    <form:input path="licenceNo" cssClass="txt" maxlength="20"/></div>
                <div class="divTit">保险人：
                    <form:input path="customer" cssClass="txt" maxlength="20"/></div>

                <div class="divTit">出险日期：
                    <form:input path="startTimeStr" cssClass="txt" readonly="readonly"/>
                    &nbsp;至&nbsp;<form:input path="endTimeStr" cssClass="txt" readonly="readonly"/></div>

                <div class="divTit">状态：
                    <form:select style="width: 150px" name="status" path="status">
                        <option value="">--所有--</option>
                        <option value="UNSETTLED">待结算</option>
                        <option value="SETTLED">已结算</option>
                        <option value="REPEAL">已作废</option>
                    </form:select>

                </div>
                <div class="clear"></div>
                <div class="divTit button_conditon button_search">
                    <a class="blue_color clean" id="cleanCondition">清空条件</a>
                  <a id="searchBtn" class="button">查 询</a> </div>





        </div>
        </form:form>
        <div class="lineBottom"></div>
        <div class="clear i_height"></div>
        <div class="supplier group_list2 listStyle" id="insuranceTableTitle">
           <%-- <span>共有：<b class="yellow_color">${totalAmount}</b>&nbsp;条</span>&nbsp;&nbsp;<span>金额：<b class="yellow_color">${totalClaims}</b>元</span>
            <a class="addNewSup blue_color" class="addNew" id="addNewInsurance">新增保险理赔</a>--%>
        </div>

        <div class="clear i_height"></div>
            <div class="cuSearch">
                <div class="gray-radius" style="margin:0;">
                    <div class="line_develop list_develop">
                        <div class="sort_label">排序方式：</div>
                        <a id="insuranceIdSort" name="J_sortStyle">保险单号
                            <span class="arrowDown" id="insuranceIdSortSpan"></span>
                        </a>
                        <a id="insuranceAccidentDateSort" name="J_sortStyle">出险日期
                            <span class="arrowDown" id="insuranceAccidentDateSortSpan"></span>
                        </a>
                        <a id="insuranceCompanySort" name="J_sortStyle">保险公司
                            <span class="arrowDown" id="insuranceCompanySortSpan"></span>
                        </a>
                    </div>

                    <input type="hidden" name="maxRows" id="maxRows" value="5">

        <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="insuranceDataTable">
            <tr class="titleBg">
                <td style="padding-left:10px;"><span style="width:100px; padding-left:10px;">NO</span></td>
                <td>保险单号</td>
                <td>保险公司</td>
                <td>保险人</td>
                <td>车牌号</td>
                <td>出险日期</td>
                <td>状态</td>
                <td>关联单据号</td>
            </tr>
            <tr class="space">
                <td colspan="8"></td>
            </tr>



        </table>



        <!----------------------------分页----------------------------------->

      <bcgogo:ajaxPaging url="insurance.do?method=searchInsuranceOrderData" dynamical="insuranceList"
                 data='{startPageNo:1,maxRows:5}'
                  postFn="initInsuranceTable" display="none"/>


    </div>
</div>

<div id="mask" style="display:block;position: absolute;">
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>