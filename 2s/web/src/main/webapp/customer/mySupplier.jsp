<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>供应商查询</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/customData<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/supplierData<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" href="js/components/themes/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.css">

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SUPPLIER_MANAGER.SUPPLIER_DATA");

        <bcgogo:permissionParam permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.PAY_EARNEST_MONEY,WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.DUE_SETTLEMENT">
        APP_BCGOGO.Permission.SupplierManager.PayEarnestMoney = ${WEB_SUPPLIER_MANAGER_SUPPLIER_DATA_PAY_EARNEST_MONEY};
        APP_BCGOGO.Permission.SupplierManager.DueSettlement = ${WEB_SUPPLIER_MANAGER_SUPPLIER_DATA_DUE_SETTLEMENT};
        </bcgogo:permissionParam>
        <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.RELATION_SUPPLIER">
        APP_BCGOGO.Permission.Version.RelationSupplier =${WEB_VERSION_RELATION_SUPPLIER};
        </bcgogo:permissionParam>
        <bcgogo:permissionParam permissions="WEB.VERSION.PRODUCT.THROUGH_DETAIL">
        APP_BCGOGO.Permission.Version.ProductThroughDetail = ${WEB_VERSION_PRODUCT_THROUGH_DETAIL};
        </bcgogo:permissionParam>
    </script>
    <script type="text/javascript" src="js/invoicing<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/page/customer/supplierData<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/customerOrSupplier/mergeSupplier<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/customerOrSupplier/supplierCommentUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/components/ui/bcgogo-scorePanel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/components/ui/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/customerOrSupplier/mySupplier<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/customerOrSupplier/myCustomerOrSupplier<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/contact<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        $().ready(function () {
            //新增供应商
            $("#addSupplierBtn").click(function () {
                bcgogo.checksession({'parentWindow':window.parent, 'iframe_PopupBox':$("#iframe_PopupBox")[0], 'src':"RFSupplier.do?method=showSupplier"});
            });

        var filter = GLOBAL.Util.getUrlParameter("filter");
        searchBtnClick(filter);
        });
        userGuide.currentPageIncludeGuideStep = "CONTRACT_SUPPLIER_GUIDE_SUPPLIER_DATA,CONTRACT_SUPPLIER_GUIDE_RECOMMEND_SUPPLIER,CONTRACT_SUPPLIER_GUIDE_SINGLE_APPLY,";
        userGuide.currentPage = "supplierData";
        var fromUserGuideStep = '${param.fromUserGuideStep}';
    </script>

</head>
<body class="bodyMain">
<input type="hidden" id="pageType" value="supplierData"/>
<%--存放选择客户的id--%>
<div id="selectedIdArray">
</div>
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
<div class="i_search">
<%--<jsp:include page="supplierNavi.jsp">
  <jsp:param name="currPage" value="searchSupplier"/>
</jsp:include>--%>
<div class="i_main clear">
<div class="mainTitles">
    <div class="cusTitle">供应商查询</div>
    <c:if test="${!wholesalerVersion}">
        <div style="float: right;width: 212px;margin-top: 12px;"><a href="apply.do?method=getApplySuppliersIndexPage"><img style="cursor: pointer;" src="images/lookingSupplier.gif"></a></div>
    </c:if>
</div>
<div class="i_mainRight">

<input type="hidden" value="${supplierIds}" id="supplierIds">

<div class="titBody">
    <div class="lineTitle">供应商搜索<a class="look blue_color" href="supplier.do?method=toMergeRecord">查看供应商合并记录</a></div>
    <div class="lineTop"></div>
    <div class="lineBody lineAll">
        <div class="divTit">

            <span class="spanName">供应商</span>&nbsp;<input type="text" class="txt" style="width:190px; color: #ADADAD;"
                                                          id="supplierInfoText"
                                                          initialValue="供应商/联系人/手机"
                                                          pagetype="supplierdata"
                                                          style="margin-right:5px;color: #666666;width: 200px;border:1px solid #7F9DB9; margin-top:3px; height:21px;"
                                                          value="供应商/联系人/手机"/>

            <input type="hidden" value="" id="supplierId">

        </div>
        <div class="divTit">所属区域&nbsp;
            <select class="txt" style="color: #ADADAD;" id="provinceNo" name="province">
                <option class="default" value="">--所有省--</option>
            </select>&nbsp;
            <select class="txt" style="color: #ADADAD;" id="cityNo" name="city">
                <option class="default" value="">--所有市--</option>
            </select>&nbsp;
            <select class="txt" style="color: #ADADAD;" id="regionNo" name="region">
                <option class="default" value="">--所有区--</option>
            </select>
        </div>

        <%--<input id="relationType" value="" type="hidden"/>--%>
        <input id="searchStrategy" value="" type="hidden"/>

        <div class="divTit">
            <span class="spanName">交易商品</span>&nbsp;
            <%--<input type="text" class="txt J-productSuggestion J-initialCss J_clear_input txt" id="searchWord"--%>
            <%--name="searchWord" searchField="product_info" initialValue="品名/品牌/规格/型号/适用车辆"--%>
            <%--style="width:190px;"/>--%>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="productName"
                   name="productName" searchField="product_name" initialValue="品名"
                   style="width:80px; margin-left:-6px;"/>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="productBrand"
                   name="productBrand" searchField="product_brand" initialValue="品牌/产地" style="width:80px;"/>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="productSpec"
                   name="productSpec" searchField="product_spec" initialValue="规格" style="width:80px;"/>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="productModel"
                   name="productModel" searchField="product_model" initialValue="型号" style="width:80px;"/>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleBrand"
                   name="productVehicleBrand" searchField="product_vehicle_brand" initialValue="车辆品牌"
                   style="width:80px;"/>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleModel"
                   name="productVehicleModel" searchField="product_vehicle_model" initialValue="车型"
                   style="width:80px;"/>
            <input type="text" class="txt J-productSuggestion J-initialCss J_clear_input" id="commodityCode"
                   name="commodityCode" searchField="commodity_code" initialValue="商品编号"
                   style="text-transform: uppercase; width:80px;"/>
        </div>

        <div class="divTit">
            <span class="spanName">最后交易时间</span>&nbsp;
            <a class="btnList" id="my_date_yesterday" pagetype="supplierdata" name="my_date_select">昨天</a>&nbsp;
            <a class="btnList" id="my_date_today" pagetype="supplierdata" name="my_date_select">今天</a>&nbsp;
            <a class="btnList" id="my_date_thisweek" pagetype="supplierdata" name="my_date_select">最近一周</a>&nbsp;
            <a class="btnList" id="my_date_thismonth" pagetype="supplierdata" name="my_date_select">最近一月</a>&nbsp;
            <a class="btnList" id="my_date_thisyear" pagetype="supplierdata" name="my_date_select">最近一年</a>&nbsp;
            <input type="text" id="startDate" name="startDateStr" class="my_startdate txt"/>&nbsp;至&nbsp;<input id="endDate"
                                                                                                   name="endDateStr"
                                                                                                   type="text"
                                                                                                   class="my_enddate txt"/>
        </div>


        <div class="divTit button_condition">
            <a class="blue_color clean" id="clearConditionBtn">清空条件</a>
            <a class="button" id="supplierSearchBtn">搜 索</a>
            <input type="hidden" id="resetSearchCondition" value="${resetSearchCondition}"/>
        </div>

    </div>
    <div class="lineBottom"></div>
    <div class="clear i_height"></div>
</div>
<div class="supplier">
    <span style="cursor:pointer;" id="totalNumSpan">本店供应商共有：<b class="yellow_color" id="totalNum">0</b>&nbsp;名&nbsp;&nbsp;</span>
    <span style="cursor:pointer;" id="totalConsumptionSpan">累计交易：<b class="yellow_color" id="totalConsumption">0</b>&nbsp;元&nbsp;</span>
    <span style="cursor:pointer;" id="totalDebtSpan">应付款合计：<b class="yellow_color"
                                                              id="totalDebt">0</b>&nbsp;元&nbsp;</span>
    <span style="cursor:pointer;" id="totalDepositSpan">预付款合计：<b class="yellow_color" id="totalDeposit">0</b>&nbsp;元&nbsp;</span>

    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.RELATION_SUPPLIER">
        <span style="cursor:pointer;" id="relatedSupplier"><b class="blue_color btn_border">在线店铺：<span
                id="relatedNum">0</span>家</b></span>
    </bcgogo:hasPermission>


    <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_DATA.ADD">
        <a class="addNewSup blue_color" id="addSupplierBtn">新增供应商</a>
    </bcgogo:hasPermission>

    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.RELATION_SUPPLIER">
        <a class="recommended blue_color" id="recommendSupplier">推荐使用软件</a>
    </bcgogo:hasPermission>


</div>
<div class="clear i_height"></div>
<div class="cuSearch">
<div class="cartTop"></div>
<div class="cartBody">
<div class="line_develop list_develop sort_title_width">

    <%--<a id="createdTimeSort">录入时间<span id="createdTimeSortSpan" class="arrowDown"></span></a>--%>
    <div class="sort_label">排序方式：</div>
    <a class="J_supplier_sort" id="createdTimeSort" currentSortStatus="Desc" ascContact="点击后按录入时间升序排列！"
       descContact="点击后按录入时间降序排列！">录入时间<span id="createdTimeSortSpan" class="arrowDown J-sort-span" style="margin-right: 0px;"></span>

        <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
            <span class="arrowTop" style="margin-left:20px;"></span>

            <div class="alertAll">
                <div class="alertLeft"></div>
                <div class="alertBody">
                    点击后按录入时间升序排列！
                </div>
                <div class="alertRight"></div>
            </div>
        </div>
    </a>


    <%--<a id="lastInventoryTimeSort">最后入库日期<span id="lastInventoryTimeSortSpan" class="arrowDown"></span></a>--%>
    <a class="J_supplier_sort" id="lastInventoryTimeSort" currentSortStatus="Desc" ascContact="点击后按最后入库日期升序排列！"
       descContact="点击后按最后入库日期降序排列！">最后入库日期<span id="lastInventoryTimeSortSpan" class="arrowDown J-sort-span" style="margin-right: 0px;"></span>

        <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
            <span class="arrowTop" style="margin-left:20px;"></span>

            <div class="alertAll">
                <div class="alertLeft"></div>
                <div class="alertBody">
                    点击后按最后入库日期升序排列！
                </div>
                <div class="alertRight"></div>
            </div>
        </div>
    </a>


    <%--<a class="accumulative"><span id="totalTradeAmountSort">累计交易金额<span id="totalTradeAmountSortSpan"--%>
    <%--class="arrowDown"></span> </span></a>--%>
    <%----%>
    <a class="J_supplier_sort accumulative" currentSortStatus="Desc" ascContact="点击后按累计交易金额升序排列！"
       descContact="点击后按累计交易金额降序排列！"><span id="totalTradeAmountSort">累计交易金额<span id="totalTradeAmountSortSpan"
                                                                                 class="arrowDown J-sort-span" style="margin-right: 0px;"></span></span>

        <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
            <span class="arrowTop" style="margin-left:20px;"></span>

            <div class="alertAll">
                <div class="alertLeft"></div>
                <div class="alertBody">
                    点击后按累计交易金额升序排列！
                </div>
                <div class="alertRight"></div>
            </div>
        </div>
    </a>

      <span class="txtTransaction">
        <input type="text" id="totalTradeAmountStart" class="txt" style="width:30px; height:17px;"/>&nbsp;至
        <input type="text" id="totalTradeAmountEnd" class="txt" style="width:30px; height:17px;"/>
      </span>


    <div class="txtList" id="totalTradeAmountDiv"
         style=" left:369px; padding-top:30px; display:none; width:87px;">
        <span style="cursor: pointer;" class="clean" id="totalTradeAmountClear">清除</span>
        <span class="btnSure" id="totalTradeAmountSure">确定</span>

        <div class="listNum" id="totalTradeListNum">
            <span class="blue_color" id="totalTradeAmount_1">1千以下</span>
            <span class="blue_color" id="totalTradeAmount_2">1千~5千</span>
            <span class="blue_color" id="totalTradeAmount_3">5千~1万</span>
            <span class="blue_color" id="totalTradeAmount_4">1万以上</span>
        </div>
    </div>


    <%--<a class="accumulative"><span id="totalReceivableSort">应收金额<span id="totalReceivableSortSpan" class="arrowDown"></span> </span></a>--%>

    <a class="J_supplier_sort accumulative" currentSortStatus="Desc" ascContact="点击后按应收金额升序排列！"
       descContact="点击后按应收金额金额降序排列！"><span id="totalReceivableSort">应收金额<span id="totalReceivableSortSpan"
                                                                              class="arrowDown J-sort-span" style="margin-right: 0px;"></span></span>

        <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
            <span class="arrowTop" style="margin-left:20px;"></span>

            <div class="alertAll">
                <div class="alertLeft"></div>
                <div class="alertBody">
                    点击后按应收金额金额升序排列！
                </div>
                <div class="alertRight"></div>
            </div>
        </div>
    </a>


      <span class="txtTransaction">
        <input type="text" class="txt" id="totalReceivableStart" style="width:30px; height:17px;"/>&nbsp;至
        <input type="text" class="txt" id="totalReceivableEnd" style="width:30px; height:17px;"/>
      </span>

    <div class="txtList" id="totalReceivableDiv" style=" left:564px; padding-top:30px; display:none; width:87px;">
        <span style="cursor: pointer;" class="clean" id="totalReceivableClear">清除</span>
        <span class="btnSure" id="totalReceivableSure">确定</span>

        <div class="listNum" id="totalReceivableListNum">
            <span class="blue_color" id="totalReceivable_1">1千以下</span>
            <span class="blue_color" id="totalReceivable_2">1千~5千</span>
            <span class="blue_color" id="totalReceivable_3">5千~1万</span>
            <span class="blue_color" id="totalReceivable_4">1万以上</span>
        </div>
    </div>


    <%--<a class="accumulative"><span id="totalPayableSort">应付金额<span id="totalPayableSortSpan" class="arrowDown"></span></span></a>--%>

    <a class="J_supplier_sort accumulative" currentSortStatus="Desc" ascContact="点击后按应付金额升序排列！"
       descContact="点击后按应付金额金额降序排列！"><span id="totalPayableSort">应付金额<span id="totalPayableSortSpan"
                                                                           class="arrowDown J-sort-span" style="margin-right: 0px;"></span></span>

        <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
            <span class="arrowTop" style="margin-left:20px;"></span>

            <div class="alertAll">
                <div class="alertLeft"></div>
                <div class="alertBody">
                    点击后按应付金额升序排列！
                </div>
                <div class="alertRight"></div>
            </div>
        </div>
    </a>


      <span class="txtTransaction">
        <input type="text" id="debtAmountStart" class="txt" style="width:30px; height:17px;"/>&nbsp;至
        <input type="text" id="debtAmountEnd" class="txt" style="width:30px; height:17px;"/>
      </span>

    <div class="txtList" id="debtAmountDiv" style=" left:759px; padding-top:30px; display:none;width:87px;">
        <span style="cursor: pointer;" class="clean" id="debtAmountClear">清除</span>
        <span class="btnSure" id="debtAmountSure">确定</span>

        <div class="listNum" id="debtAmountListNum">
            <span class="blue_color" id="debtAmount_1">1千以下</span>
            <span class="blue_color" id="debtAmount_2">1千~5千</span>
            <span class="blue_color" id="debtAmount_3">5千~1万</span>
            <span class="blue_color" id="debtAmount_4">1万以上</span>
        </div>
    </div>


    <%--<a class="hover" style="margin-top: 0px;"><span id="depositSort">预付款<span id="depositSortSpan"--%>
    <%--class="arrowDown"></span></span></a>--%>

    <a class="J_supplier_sort" style="margin-top: 0px;" currentSortStatus="Desc" ascContact="点击后按预付款升序排列！"
       descContact="点击后按预付款降序排列！"><span id="depositSort">预付款<span id="depositSortSpan"
                                                                  class="arrowDown J-sort-span" style="margin-right: 0px;"></span></span>

        <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
            <span class="arrowTop" style="margin-left:20px;"></span>

            <div class="alertAll">
                <div class="alertLeft"></div>
                <div class="alertBody">
                    点击后按预付款升序排列！
                </div>
                <div class="alertRight"></div>
            </div>
        </div>
    </a>

</div>

<input type="hidden" name="maxRows" id="maxRows" value="15">
<input type="hidden" name="totalRows" id="totalRows" value="0">
<input type="hidden" name="sortStatus" id="sortStatus">
<input type="hidden" name="sortStr" id="sortStr" value="">
<input type="hidden" name="hasDebt" id="hasDebt" value="">
<input type="hidden" name="hasDeposit" id="hasDeposit" value="">

<table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="supplierDataTable">
    <col width="30">
    <col>
    <col width="110">
    <col width="120">
    <col width="80">
    <col width="60">
    <col width="80">
    <col width="110">
    <col width="90">
    <col width="120">
    <tr class="titleBg">
        <td style="padding-left:10px;"></td>
        <td>供应商</td>
        <td>联系人</td>
        <td>所属区</td>
        <td>累计交易</td>
        <td>预付款</td>
        <td>退货金额</td>
        <td>对账金额</td>
        <td>最后入库日期</td>
        <td>操作</td>
    </tr>
</table>

<div class="i_height clear"></div>


<jsp:include page="/common/pageAJAX.jsp">
    <jsp:param name="url" value="supplier.do?method=searchSupplierDataAction"></jsp:param>
    <jsp:param name="data" value="{startPageNo:1,maxRows:15,customerOrSupplier:'supplier'}"></jsp:param>
    <jsp:param name="jsHandleJson" value="initSupplierList"></jsp:param>
    <jsp:param name="dynamical" value="supplierSuggest"></jsp:param>
    <jsp:param name="display" value="none"></jsp:param>
</jsp:include>


<div class="i_height clear"></div>
<bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_MERGE">
    <a class="btnMerger" id="mergeSupplierBtn">合并供应商</a>
</bcgogo:hasPermission>

</div>
<div class="cartBottom"></div>

</div>


</div>

</div>
</div>

<div id="isInvo"></div>
<div id="supplierinfotoreload"></div>
</div>
</div>
<div id="mask" style="display:block;position: absolute;">
</div>

<div class="alert" id="payableReceivableAlert" style="display: none;">
    点击后对账
</div>

<!-- 合并普通供应商弹出框-->
<div id="mergeSupplierDetail" style="display: none;">
    <jsp:include page="merge/mergeSupplierDetail.jsp"></jsp:include>
</div>

<!-- 合并批发商弹出框-->
<div id="mergeWholesalerDetail" style="display: none;">
    <jsp:include page="merge/mergeWholesalerDetail.jsp"></jsp:include>
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:6;margin:140px 0 0 120px;display:none; "
        allowtransparency="true" width="1000px" height="1000px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1500px" frameborder="0" src="" scrolling="no"></iframe>
<div pop-window-name="input-mobile" style="display: none;">
    <div style="margin-left: 10px;margin-top: 10px">
        <label>手机号：</label>
        <input type="text" pop-window-input-name="mobile" maxlength="11" style="width:125px;height: 20px">
    </div>
</div>
<div id="cancelShopRelationDialog" style="display: none;">
    该用户是您的关联供应商，取消关联后您将无法在线处理订单，是否确认取消？若确认，请填写取消理由
    <textarea id="cancel_msg" init_word="取消关联理由" maxLength=70 style="width:270px;height: 63px;margin-top: 7px;"
              class="gray_color">取消关联理由</textarea>
</div>
<div class="tixing alert" id="multi_alert" style="left: 820px; margin-top: 0px; display: none;">
    <div class="ti_top"></div>
    <div class="ti_body">
        <div>您可推荐未使用一发软件的供应商使用:</div>
        <div>1、成功推荐1家，即可获得500元短信返利！</div>
        <div>2、一站式比价采购，节省成本！</div>
        <div>3、海量供应商供您选择！</div>
    </div>
    <div class="ti_bottom"></div>
</div>
<div class="tixing alert" id="single_alert" style="left: 820px; margin-top: 0px; display: none;">
    <div class="ti_top tiTop_top"></div>
    <div class="ti_body">
        <div>您可推荐未使用一发软件的供应商使用:</div>
        <div>1、成功推荐1家，即可获得500元短信返利！</div>
        <div>2、一站式比价采购，节省成本！</div>
        <div>3、海量供应商供您选择！</div>
    </div>
    <div class="ti_bottom"></div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>