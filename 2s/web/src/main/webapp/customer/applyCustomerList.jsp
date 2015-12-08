<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>客户查询</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customerOrSupplier/customerSupplierBusinessScope<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/customer/applyCustomerData<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "CUSTOMER_MANAGER_SEARCH_APPLY_CUSTOMER");
        <bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_APPLY_ACTION">
              APP_BCGOGO.Permission.CustomerManager.CustomerApplyAction=${WEB_CUSTOMER_MANAGER_CUSTOMER_APPLY_ACTION};
        </bcgogo:permissionParam>
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<input type="hidden" value="applyCustomerList" id="pageType">

<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">客户查询</div>
    </div>
    <div class="i_search">
        <div class="titBody">
            <div class="lineBody bodys lineDetails">
                <div id="businessScopeSelected" class="selectList" style="width: 100%">
                    <b style="margin-top: 4px;">已选择</b>
                </div>
                <b class="line_title" style="padding-top: 7px;">经营产品</b>
                <div class="businessRange" id="businessScopeDiv" style="width: 950px">加载中...</div>


                <div class="divTit">
                    客户&nbsp;<input type="text" class="txt J_customerOnlineSuggestion" style="width:200px;color: #000000;" id="name" value="${customerName}"/>
                </div>
                <div class="divTit">
                    &nbsp;&nbsp;所在区域&nbsp;
                    <select class="txt" style="width:130px;height: 20px;" id="provinceNo" name="provinceNo">
                        <option class="default" style="color:#BBBBBB" value="">--请选择省份--</option>
                        <option class="default" style="color:#000000" value="">全国</option>
                    </select>&nbsp;&nbsp;
                    <select class="txt" style="width:130px;height: 20px;" id="cityNo" name="cityNo">
                        <option class="default" style="color:#BBBBBB" value="">--请选择城市--</option>
                        <option class="default" style="color:#000000" value="">全省</option>
                    </select>&nbsp;&nbsp;
                    <select class="txt" style="width:130px;height: 20px;" id="regionNo" name="regionNo">
                        <option class="default" style="color:#BBBBBB" value="">--请选择区域--</option>
                        <option class="default" style="color:#000000" value="">全市</option>
                    </select>
                </div>
                <input type="hidden" value="${provinceNo}" id="initProvinceNo">
                <input type="hidden" value="${pushMessageId}" id="pushMessageId">
                <input type="hidden" name="thirdCategoryIdStr" id="thirdCategoryIdStr" value="${thirdCategoryIdStr}" />
                <div class="divTit" style="float:right; padding:5px; line-height:25px;"><a class="button" id="searchBtn">搜&nbsp;索</a></div>
            </div>
            <div class="clear height"></div>
            <div class="cuSearch">
                <div class="cartTop"></div>
                <div class="cartBody">

                    <table cellpadding="0" cellspacing="0" class="tab_cuSearch tabCart" id="applyCustomerData">
                        <col width="30">
                        <col width="250">
                        <col width="250">
                        <col>
                        <col width="100">
                        <tr class="titleBg">
                            <td style="padding-left:10px;"><input class="checkAll" type="checkbox"></td>
                            <td>客户</td>
                            <td>所在地</td>
                            <td>经营产品</td>
                            <td>操作</td>
                        </tr>
                        <tr class="titBottom_Bg"><td colspan="5"></td></tr>
                    </table>
                    <div class="clear i_height"></div>
                    <!----------------------------分页----------------------------------->
                    <div style="float: left;width: 300px">
                        <input id="applyCustomerBtn" class="jieCount" style="float:left;margin-left: 0px;" type="button" value="申请建立关联">
                    </div>
                    <div style="float: right">
                        <bcgogo:ajaxPagingUpAndDown url="apply.do?method=searchApplyCustomers" dynamical="_ApplyCustomer"
                                                    postFn="initApplyCustomerDataTr" display="none" getDataFunction="getSearchData"/>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>
<div class="height"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</div>


<div id="mask" style="display:block;position: absolute;">
</body>
</html>