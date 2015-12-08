<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>供应商查询</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>

    <link rel="stylesheet" href="js/components/themes/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" href="js/components/themes/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/customerOrSupplier/customerSupplierBusinessScope<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/customer/applySupplierData<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/customerOrSupplier/supplierCommentUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "APPLY_GET_APPLY_SUPPLIERS");
        <bcgogo:permissionParam permissions="WEB.SUPPLIER_MANAGER.APPLY_ACTION">
            APP_BCGOGO.Permission.SupplierManager.SupplierApplyAction=${WEB_SUPPLIER_MANAGER_APPLY_ACTION};
        </bcgogo:permissionParam>

        var area_code='${area_code}';


    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<input type="hidden" value="applySupplierList" id="pageType">

<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">供应商查询</div>
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
                    供应商&nbsp;&nbsp;&nbsp;<input type="text" class="txt J_supplierOnlineSuggestion" style="width:420px;color: #000000;" id="name" value="${supplierName}"/>
                </div>
                <div class="divTit" style="width: 100%">
                   所在区域
                    <select class="txt" style="width:130px;height: 20px;" id="provinceNo" name="provinceNo">
                        <option class="default" style="color:#BBBBBB" value="">--请选择省份--</option>
                        <option class="default" style="color:#000000" value="">全国</option>
                        <c:forEach items="${proAreaDTOList}" var="proAreaDTO" varStatus="status">
                            <option  value="${proAreaDTO.no}" style="color:#000000">${proAreaDTO.name}</option>
                        </c:forEach>
                    </select>&nbsp;&nbsp;
                    <select class="txt" style="width:130px;height: 20px;" id="cityNo" name="cityNo">
                        <option class="default" style="color:#BBBBBB" value="">--请选择城市--</option>
                        <option class="default" style="color:#000000" value="">全省</option>
                         <c:forEach items="${cityAreaDTOList}" var="cityAreaDTO" varStatus="status">
                            <option  value="${cityAreaDTO.no}" style="color:#000000">${cityAreaDTO.name}</option>
                        </c:forEach>
                    </select>&nbsp;&nbsp;
                    <select class="txt" style="width:130px;height: 20px;" id="regionNo" name="regionNo">
                        <option class="default" style="color:#BBBBBB" value="">--请选择区域--</option>
                        <option class="default" style="color:#000000" value="">全市</option>
                    </select>
                    &nbsp;&nbsp;主营车型&nbsp;&nbsp;
                    <input type="text" class="w100 txt" placeholder="车辆品牌"  pagetype="customerVehicle" id="vehicles0.vehicleBrand" value="${standardVehicleBrand}" autocomplete="off" />
                             <input type="text" class="w100 txt " placeholder="车型"  id="vehicles0.vehicleModel" pagetype="customerVehicle" value="${standardVehicleModel}"  autocomplete="off" />
                </div>
                <input type="hidden" value="${provinceNo}" id="initProvinceNo">
                <input type="hidden" value="${pushMessageId}" id="pushMessageId">
                <input type="hidden" name="thirdCategoryIdStr" id="thirdCategoryIdStr" value="${thirdCategoryIdStr}" />
                <div class="divTit" style="float:right; padding:5px; line-height:25px;"><a class="button" id="searchBtn" style="margin-right:450px">搜&nbsp;索</a></div>
            </div>
            <div class="clear height"></div>
            <div class="cuSearch">
                <div class="cartTop"></div>
                <div class="cartBody">

                <table cellpadding="0" cellspacing="0" class="tab_cuSearch tabCart" id="applySupplierData">
                            <col width="30">
                            <col width="250">
                            <col width="250">
                            <col width="250">
                            <col width="250">
                            <col width="100">
                            <tr class="titleBg">
                                <td style="padding-left:10px;"><input class="checkAll" type="checkbox"></td>
                                <td>供应商</td>
                                <td>所在地</td>
                                <td>经营产品</td>
                                <td>主营车型</td>
                                <td>操作</td>
                            </tr>
                            <tr class="titBottom_Bg"><td colspan="5"></td></tr>
                        </table>
                        <div class="clear i_height"></div>
                        <!----------------------------分页----------------------------------->
                        <div style="float: left;width: 300px">
                            <input id= "applySupplierBtn" class="jieCount" style="float:left;margin-left: 0px;" type="button" value="申请建立关联">
                        </div>
                        <div style="float: right">
                            <bcgogo:ajaxPagingUpAndDown url="apply.do?method=searchApplySuppliers" dynamical="_ApplySupplier"
                                                        postFn="initApplySupplierDataTr" display="none" getDataFunction="getSearchData"/>
                        </div>
                </div>
            </div>
        </div>
    </div>
    <div class="height"></div>

    <%@include file="/WEB-INF/views/footer_html.jsp" %>
</div>

<div id="mask" style="display:block;position: absolute;"></div>

</body>
</html>