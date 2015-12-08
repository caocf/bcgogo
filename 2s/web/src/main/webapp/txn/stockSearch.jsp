<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>进销存_库存查询</title>
    <%
        boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn(WebUtil.getShopId(request));//选配仓位功能 默认开启这个功能false
        boolean tradePriceTag = ServiceManager.getService(IShopConfigService.class).isTradePriceSwitchOn(WebUtil.getShopId(request),WebUtil.getShopVersionId(request));//选配批发价功能
    %>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cuSearch<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/uploadify/uploadify<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.css"/>
    <style type="text/css">
        #table_productNo .table_title td {
            text-align: center;
        }

        .table2 tr.table_title td {
            padding-left: 0px;
        }

        .ui-autocomplete {
            max-height: 250px;
            min-width: 50px;
            overflow-y: auto;
            overflow-x: hidden;
        }

    </style>
    <c:choose>
        <c:when test="<%=storageBinTag%>">
            <link rel="stylesheet" type="text/css"
                  href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
        </c:when>
        <c:otherwise>
            <link rel="stylesheet" type="text/css"
                  href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
        </c:otherwise>
    </c:choose>
    <c:choose>
        <c:when test="<%=tradePriceTag%>">
            <link rel="stylesheet" type="text/css"
                  href="styles/tradePriceOn<%=ConfigController.getBuildVersion()%>.css"/>
        </c:when>
        <c:otherwise>
            <link rel="stylesheet" type="text/css"
                  href="styles/tradePriceOff<%=ConfigController.getBuildVersion()%>.css"/>
        </c:otherwise>
    </c:choose>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"TXN_INVENTORY_MANAGE_STOCK_SEARCH");
        APP_BCGOGO.PersonalizedConfiguration.TradePriceTag =<%=tradePriceTag%>;
        APP_BCGOGO.PersonalizedConfiguration.StorageBinTag =<%=storageBinTag%>;
        <bcgogo:permissionParam permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SET_UNIT,WEB.TXN.INVENTORY_MANAGE.ALARM,WEB.TXN.PURCHASE_MANAGE.STORAGE.SAVE,
        WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PRODUCT_CLASSIFY,WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.UPDATE_TRADE_PRICE,
        WEB.TXN.INVENTORY_MANAGE.SALE_PRICE_SETTING,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.NEW_STORAGE_PRICE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE,
        WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.INVENTORY,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.STORAGE_BIN,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.TRADE_PRICE,
        WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE，WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.NEW_STORAGE_PRICE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.AVERAGE_PRICE,
        WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.STORAGE_BIN,WEB.TXN.INVENTORY_MANAGE.PRODUCT_MODIFY,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.DELETE_PRODUCT,
        WEB.AUTOACCESSORYONLINE.ON_SALE_OPERATION">
            //新增入库
            APP_BCGOGO.Permission.Txn.PurchaseManage.StorageSave= ${WEB_TXN_PURCHASE_MANAGE_STORAGE_SAVE};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.ProductClassify= ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_PRODUCT_CLASSIFY};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.Alarm= ${WEB_TXN_INVENTORY_MANAGE_ALARM};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.AlarmSettings= ${WEB_TXN_INVENTORY_MANAGE_ALARM_SETTINGS};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.Inventory= ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_INVENTORY};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.StorageBin= ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_STORAGE_BIN};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetTradePrice= ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_UPDATE_TRADE_PRICE};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.TradePrice= ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_TRADE_PRICE};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SalePrice= ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_SALE_PRICE};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetSalePrice= ${WEB_TXN_INVENTORY_MANAGE_SALE_PRICE_SETTING};   //设定销售价
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.AveragePrice= ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_AVERAGE_PRICE};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.NewStoragePrice= ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_NEW_STORAGE_PRICE};
            APP_BCGOGO.Permission.Txn.InventoryManage.ProductModify = ${WEB_TXN_INVENTORY_MANAGE_PRODUCT_MODIFY};
            APP_BCGOGO.Permission.Txn.InventoryManage.ProductDelete = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_DELETE_PRODUCT};    //操作
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetUnit = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_SET_UNIT};
            APP_BCGOGO.Permission.AutoAccessoryOnline.OnSaleOperation = ${WEB_AUTOACCESSORYONLINE_ON_SALE_OPERATION};

        </bcgogo:permissionParam>
        <bcgogo:hasPermission resourceType="menu" permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE||WEB.TXN.PURCHASE_MANAGE.STORAGE||WEB.TXN.SALE_MANAGE.SALE||WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE||WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE||WEB.TXN.PURCHASE_MANAGE.RETURN||WEB.TXN.SALE_MANAGE.RETURN||WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
        APP_BCGOGO.Permission.InquiryCenter = true;
        </bcgogo:hasPermission>
    </script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/setLimit<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/stockSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/addNewProduct<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/exportExcel<%=ConfigController.getBuildVersion()%>.js"></script>
    <%@ include file="/WEB-INF/views/image_script.jsp" %>
    <script type="text/javascript" src="js/components/ui/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        APP_BCGOGO.UserGuide.currentPageIncludeGuideStep = "PRODUCT_ONLINE_GUIDE_TXN";
        APP_BCGOGO.UserGuide.currentPage = "stockSearch";
        function getGoodsHistory1(itemName) {
            bcgogo.checksession({'parentWindow':window,'iframe_PopupBox':$("#iframe_PopupBox_1")[0],'src':"goodsHistory.do?method=createGoodsHistory&orderType=WASH&itemName=" + encodeURIComponent(itemName) + "&time=" + new Date()});
        }
        var shopId = '${sessionScope.shopId}';
        function showSetproductKind() {
            bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox_Kind")[0], 'src':"txn.do?method=setProductKind"});
        }

    </script>
    <bcgogo:hasPermission permissions="WEB.AD_SHOW">
        <script type="text/javascript" src="js/adShow<%=ConfigController.getBuildVersion()%>.js"></script>
    </bcgogo:hasPermission>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>

<div class="stockSearch">
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<input type="hidden" id="sortStatus" value="inventoryAmountDesc"/>
<input type="hidden" id="pageType" value="stockSearch"/>
    <input type="hidden" id="policy" value="${upYunFileDTO.policy}">
    <input type="hidden" id="signature" value="${upYunFileDTO.signature}">
<%--<input type="hidden" id="userGroup" value="<%=WebUtil.getUserGroupType(request)%>">--%>
<input type="hidden" id="numIndex"/>
<input type="hidden" id="moreSupplierPageSize" value="10">
<bcgogo:permissionParam permissions="WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS,WEB.TXN.INVENTORY_MANAGE.PRODUCT_MODIFY">
    <input type="hidden" id="permissionInventoryAlarmSettings" value="${WEB_TXN_INVENTORY_MANAGE_ALARM_SETTINGS}"/>
    <input type="hidden" id="permissionProductModify" value="${WEB_TXN_INVENTORY_MANAGE_PRODUCT_MODIFY}"/>

</bcgogo:permissionParam>
<bcgogo:permissionParam resourceType="menu" permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH">
    <input type="hidden" id="permissionGoodsBuy" value="${WEB_TXN_PURCHASE_MANAGE_PURCHASE}"/>
    <input type="hidden" id="permissionGoodsSale" value="${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH}"/>
</bcgogo:permissionParam>
<input type="hidden" id="storageBinTag" value="<%=storageBinTag%>"/>
<input type="hidden" id="tradePriceTag" value="<%=tradePriceTag%>"/>
<input type="hidden" id="limitOrPriceSwitch" value="price"/>
<input type="hidden" name="rowStart" id="rowStart" value="0">
<input type="hidden" name="pageRows" id="pageRows" value="25">
<input type="hidden" name="totalRows" id="totalRows" value="0">
<input type="hidden" name="totalRowsLowerLimit" id="totalRowsLowerLimit" value="0">
<input type="hidden" name="totalRowsUpperLimit" id="totalRowsUpperLimit" value="0">
</div>


<div class="i_main clear">
<jsp:include page="txnNavi.jsp">
    <jsp:param name="currPage" value="inventory"/>
</jsp:include>
<jsp:include page="inventroyNavi.jsp">
    <jsp:param name="currPage" value="inventory"/>
    <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
</jsp:include>
<div class="i_mainRight" id="i_mainRight">
<bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PENDING_STORAGE||WEB.SCHEDULE.INVENTORY_MANAGE.LACK_STORAGE_FOR_REPAIR">
    <div class="cuTitle J-more" style="cursor: pointer">
        待入库&nbsp;&nbsp;共&nbsp;<b class="yellow_color" id="totalRemindCount">0</b>&nbsp;条记录&nbsp;
        <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PENDING_STORAGE">
            <a class="blue_color">采购待入库&nbsp;<span id="purchaseRemindCount" class="J-purchaseRemindCount">0</span>&nbsp;条</a>&nbsp;&nbsp;
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.SCHEDULE.INVENTORY_MANAGE.LACK_STORAGE_FOR_REPAIR">
            <a class="blue_color">缺料待入库&nbsp;<span class="J-repairLackCount" id="repairLackCountSpan">0</span>&nbsp;条</a>
        </bcgogo:hasPermission>
        <a class="arrow_down">&nbsp;</a>
    </div>
    <div class="cuSearch" style="display: none">
        <div class="cartBody">
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PENDING_STORAGE">
                <div id="pendingStorageDetail">
                <div class="line_develop">
                    <span class="lineBorder">采购待入库&nbsp;&nbsp;共&nbsp;<span class="blue_color J-purchaseRemindCount">0</span>&nbsp;条记录</span>
                </div>
                <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="purchaseRemind">
                    <col width="50"/>
                    <col width="110"/>
                    <col width="120"/>
                    <col/>
                    <col width="120"/>
                    <col width="100"/>
                    <col width="80"/>
                    <col width="120"/>
                    <col width="90"/>
                    <tr class="titleBg">
                        <td style="padding-left:10px;">NO</td>
                        <td>单据号</td>
                        <td>提醒类型</td>
                        <td>供应商</td>
                        <td>采购商品</td>
                        <td>采购种类</td>
                        <td>总金额</td>
                        <td>预计交货日期</td>
                        <td>操作</td>
                    </tr>
                    <tr class="space">
                        <td colspan="9"></td>
                    </tr>
                </table>
                <div class="clear i_height"></div>
                <div class="hidePageAJAX">
                    <bcgogo:ajaxPaging url="remind.do?method=invoicing" postFn="stockSearchInitTr3" dynamical="dynamical1"/>
                </div>
                <div class="height"></div>
                </div>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.SCHEDULE.INVENTORY_MANAGE.LACK_STORAGE_FOR_REPAIR">
                <div id="lackStorageDetail">
                <div class="line_develop">
                    <span class="lineBorder">缺料待入库&nbsp;&nbsp;共&nbsp;<span class="blue_color J-repairLackCount">0</span>&nbsp;条记录</span>
                </div>
                <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="lack_tab">
                    <col width="50"/>
                    <col width="110"/>
                    <col width="120"/>
                    <col width="100"/>
                    <col width="100"/>
                    <col width="80"/>
                    <col width="100"/>
                    <col/>
                    <col width="120"/>
                    <tr class="titleBg">
                        <td style="padding-left:10px;">NO</td>
                        <td>施工单号</td>
                        <td>提醒类型</td>
                        <td>材料品名</td>
                        <td>车牌号</td>
                        <td>客户名</td>
                        <td>联系方式</td>
                        <td>内容</td>
                        <td>预计交货日期</td>
                    </tr>
                    <tr class="space">
                        <td colspan="9"></td>
                    </tr>
                </table>
                <div class="clear i_height"></div>
                <div class="hidePageAJAX">
                    <bcgogo:ajaxPaging url="remind.do?method=lackStorageRemind" postFn="initLackStock" dynamical="dynamical2"/>
                </div>
                </div>
            </bcgogo:hasPermission>
        </div>
        <div class="cartBottom"></div>
    </div>
</bcgogo:hasPermission>
<div class="clear i_height"></div>
<div class="titBody">
    <div class="lineTop"></div>
    <div class="lineBody lineAll">
        <div class="divTit">
            <b>库存查询&nbsp;</b>
            <div class="txt_Search">
                <input type="hidden" id="searchProductIds" name="searchProductIds" value="${productIds}"/>
                <input type="hidden" id="fromPage"  value="${fromPage}"/>
                <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="searchWord" name="searchWord" searchField="product_info" value="${searchWord}" initialValue="品名/品牌/规格/型号/适用车辆" style="width:210px;"/>
                <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="productName" name="productName" searchField="product_name" value="${searchProductName}" initialValue="品名" style="width:70px;"/>
                <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="productBrand" name="productBrand" searchField="product_brand" value="${searchProductBrand}" initialValue="品牌/产地" style="width:80px;"/>
                <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="productSpec" name="productSpec" searchField="product_spec" value="${searchProductSpec}" initialValue="规格" style="width:80px;"/>
                <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="productModel" name="productModel" searchField="product_model" value="${searchProductModel}" initialValue="型号" style="width:80px;"/>
                <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand" value="${searchProductVehicleBrand}" initialValue="车辆品牌" style="width:80px;"/>
                <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model"  value="${searchProductVehicleModel}"  initialValue="车型" style="width:80px;"/>
                <input class="J-initialCss J_clear_input txt" type="text" id="product_kind"  autocomplete="off" initialValue="商品分类" inputtype="stocksearch" style="width:60px;"/>
                <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="commodityCode" name="commodityCode" searchField="commodity_code" initialValue="商品编号" value="${searchCommodityCode}" style="text-transform: uppercase;width:70px;"/>
                <input id="supplierInfoSearchText" class="J-initialCss J_clear_input txt" type="text" tabindex="9" autocomplete="off" style="width:140px;" initialValue="供应商/联系人/手机" pagetype="stockSearch"/>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                    <select id="storehouseText" class="txt selTxt J_clear_input" initialValue="所有仓库" style="color: #ADADAD;">
                        <option style="color: #ADADAD;" value="">所有仓库</option>
                        <c:forEach items="${storeHouseDTOList}" var="storeHouseDTO">
                            <option style="color: #000000;" value="${storeHouseDTO.id}">${storeHouseDTO.name}</option>
                        </c:forEach>
                    </select>
                </bcgogo:hasPermission>
                <input id="searchMode" type="hidden" />
                <a class="clean" id="searchInventoryBtn">查&nbsp;询</a><a class="blue_color clean2" id="clearConditionBtn">清空条件</a>
            </div>
        </div>
    </div>
    <div class="lineBottom"></div>
    <div class="clear i_height"></div>
</div>
<div class="clear"></div>
<div class="group_list">
    共&nbsp;<b class="blue_color" id="inventoryCount">0</b>&nbsp;种&nbsp;&nbsp;
    数量&nbsp;<b class="yellow_color" id="inventoryProductAmount">0</b>&nbsp;
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">
        金额&nbsp;<b class="yellow_color"><span class="arialFont">&yen;</span><span id="inventorySum">0</span></b>&nbsp;&nbsp;
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.ALARM">
        库存告警&nbsp;<a class="btn_so" id="lowerLimit_click">缺货&nbsp;<label id="lowerLimitCount">0</label></a>
        <a class="btn_so" id="upperLimit_click">超出&nbsp;<label id="upperLimitCount">0</label></a>
    </bcgogo:hasPermission>
    <a class="addNewSup blue_color J_addNewProduct">新增商品</a>
</div>
<div class="clear i_height"></div>

<div class="clear"></div>
<div class="cuSearch">
    <div class="cartTop"></div>
    <div class="cartBody">
        <div class="line_develop sort_title_width">
            <div class="sort_label">排序方式：</div>
            <a class="J_product_sort" sortFiled="commodityCode" currentSortStatus="Desc" ascContact="点击后按商品编号升序排列！"
               descContact="点击后按商品编号降序排列！">商品编号<span class="arrowDown J-sort-span"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后按商品编号升序排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
            <a class="J_product_sort" sortFiled="name" currentSortStatus="Desc" ascContact="点击后按品名升序排列！"
               descContact="点击后按品名降序排列！">品名<span class="arrowDown J-sort-span"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后按品名升序排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
            <a class="J_product_sort" sortFiled="brand" currentSortStatus="Desc" ascContact="点击后按品牌/产地升序排列！"
               descContact="点击后按品牌/产地降序排列！">品牌/产地<span class="arrowDown J-sort-span"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后按品牌/产地升序排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
            <a class="J_product_sort" sortFiled="model" currentSortStatus="Desc" ascContact="点击后按规格升序排列！"
               descContact="点击后按规格降序排列！">规格<span class="arrowDown J-sort-span"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后按规格升序排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
            <a class="J_product_sort" sortFiled="spec" currentSortStatus="Desc" ascContact="点击后按型号升序排列！"
               descContact="点击后按型号降序排列！">型号<span class="arrowDown J-sort-span"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后按型号升序排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
    <a class="hover J_product_sort" sortFiled="inventoryAmount" currentSortStatus="Desc"
       ascContact="点击后库存量从低到高排列！" descContact="点击后库存量从高到低排列！">库存量<span class="arrowDown J-sort-span"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后库存量从低到高排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
            <a class="J_product_sort" class="J_product_sort" sortFiled="productKind" currentSortStatus="Desc"
               ascContact="点击后按商品分类升序排列！" descContact="点击后按商品分类降序排列！">商品分类<span class="arrowDown J-sort-span"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后按商品分类升序排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
            <a class="J_product_sort" class="J_product_sort" sortFiled="storageTime" currentSortStatus="Desc"
               ascContact="点击后按最后入库时间升序排列！" descContact="点击后按最后入库时间降序排列！">最后入库时间<span class="arrowDown J-sort-span"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后按最后入库时间升序排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
            <a class="J_product_sort" class="J_product_sort" sortFiled="last30Sales" currentSortStatus="Desc"
               ascContact="点击后按近30天销量升序排列！" descContact="点击后按近30天销量降序排列！">近30天销量<span class="arrowDown J-sort-span"></span>
                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                    <span class="arrowTop" style="margin-left:20px;"></span>
                    <div class="alertAll">
                        <div class="alertLeft"></div>
                        <div class="alertBody">
                            点击后按近30天销量升序排列！
                        </div>
                        <div class="alertRight"></div>
                    </div>
                </div>
            </a>
</div>
<form id="productDTOListForm" action="" method="post" style="display:block;overflow-y:visible;margin-top:10px">
    <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="table_productNo">
      <%--  <tr class="titleBg">
        	<td style="padding-left:10px;width:2%"><input type="checkbox" id="checkAlls"/></td>
            <td>商品信息</td>
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.INVENTORY">
            <td style="width:6%">库存量</td>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.AVERAGE_PRICE">
            <td style="width:6%;">均价</td>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.NEW_STORAGE_PRICE">
            <td style="width:6%;">最新价</td>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.STORAGE_BIN">
                <bcgogo:permission>
                    <bcgogo:if resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                    </bcgogo:if>
                    <bcgogo:else>
                        <td class="storage_bin_td" style="width:8%">货位</td>
                    </bcgogo:else>
                </bcgogo:permission>
            </bcgogo:hasPermission>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.SALE_PRICE_SETTING&&WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE">
                    <td style="width:8%;"><a class="btnTitle" onclick="showIt()">零售价</a></td>
                </bcgogo:if>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE">
                    <td style="width:8%;">零售价</td>
                </bcgogo:if>
            </bcgogo:permission>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.TRADE_PRICE&&WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.UPDATE_TRADE_PRICE">
                    <td class="trade_price_td" style="width:8%;"><a class="btnTitle" onclick="showSetTradePrice()">批发价</a></td>
                </bcgogo:if>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.TRADE_PRICE">
                    <td class="trade_price_td" style="width:8%;" >批发价</td>
                </bcgogo:if>
            </bcgogo:permission>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PRODUCT_CLASSIFY">
                    <td style="width:10%">
                        <a class="btnTitle" onclick="showSetproductKind()">商品分类</a>
                        <input type="hidden" id="oldKindName"/>
                    </td>
                </bcgogo:if>
                <bcgogo:else>
                    <td style="width:10%">商品分类</td>
                </bcgogo:else>
            </bcgogo:permission>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS">
                    <td style="width:10%;"><a class="btnTitle" onclick="showSetLimit(true);">下/上限</a></td>
                </bcgogo:if>
                <bcgogo:else>
                    <td style="width:10%;">下/上限</td>
                </bcgogo:else>
            </bcgogo:permission>
            <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.DELETE_PRODUCT">
                <td style="width:10%">操作</td>
            </bcgogo:hasPermission>
        </tr>
        <tr class="space"><td colspan="11"></td></tr>--%>
    </table>
</form>
<div class="height"></div>
<bcgogo:ajaxPaging url="goodsindex.do?method=inventory" postFn="stockSearchShowResponse" dynamical="_stock_search" display="none"/>
</div>
<div class="cartBottom"></div>
</div>
<div class="height"></div>
<!----------------------------操作按钮----------------------------------->
<bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PRINT">
    <form id = "editForm" name = "editForm" method = "post" style="float:left;" action = "stockSearch.do?method=getProductDateToPrint" target = "colors123"   onsubmit = "openSpecfiyWindown( 'colors123' )" >
        <input type = "hidden" id="jsonStr" name="jsonStr"/>
        <input type = "hidden" id="currentPage" name="currentPage"/>
        <input name="storehouseId" id="editFormStorehouseId" type="hidden" />
        <a class="btnOperating" id="submitPrint" >打 印</a>
        <a class="btnOperating" id="export" >导 出</a>
        <img id="exporting" style="margin-left: 30px; display: none;" title="正在导出" alt="正在导出" src="images/loadinglit.gif">
    </form >
</bcgogo:hasPermission>
<div class="divOperating">
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE">
        <a class="btnOperating" onclick="buy(1);">采 购</a>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">
        <a class="btnOperating" onclick="buy(2);">入 库</a>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE">
        <a class="btnOperating" onclick="buy(3);">销 售</a>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
        <a class="btnOperating" onclick="buy(4);">施 工</a>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN">
        <a class="btnOperating" onclick="buy(5);">入库退货</a>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN">
        <a class="btnOperating" onclick="buy(7);">销售退货</a>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INVENTORY_PRICE_ADJUSTMENT.BASE">
        <a class="btnOperating" onclick="buy(6);">库存盘点</a>
    </bcgogo:hasPermission>

</div>

<div class="height"></div>
<jsp:include page="unit.jsp"/>
</div>


</div>
<div id="mask" style="display:block;position: absolute;">
</div>

</div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="150%" scrolling="no" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="500px" scrolling="no" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_2" style="position:absolute;z-index:9;  display:none;" scrolling="no"
        allowtransparency="true"
        width="1000px" height="500px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_Limit" style="position:absolute;z-index:5; left:40%; top:35%; display:none;"
        allowtransparency="true" width="286px" height="220px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_Kind" style="position:absolute;z-index:5; left:40%; top:35%; display:none;"
        allowtransparency="true" width="286px" height="400px" frameborder="0" src="" scrolling="no"></iframe>
<div id="deleteProduct_dialog" style="display:none">
    <div id="deleteProduct_msg"></div>
</div>
<div id="moreSupplierInfo_dialog" style="display: none" class="alertMain">
    <table class="tabRecord" cellspacing="0" cellpadding="0">
    </table>
    <div class="height"></div>
    <div>
        <bcgogo:ajaxPaging url="stockSearch.do?method=getSupplierInventory" postFn="drawMoreSupplier" dynamical="_more_supplier" display="none"/>
    </div>
</div>
<div id="selectSupplierInventoryCheck_dialog"  class="alertMain" style="display: none">
    <form action="inventoryCheck.do?method=saveProductThroughSingleInventoryCheck" method="post" id="inventoryCheckForm">
        <input type="hidden" id="itemDTOs0.productId" name="itemDTOs[0].productId">
        <input type="hidden" id="inventoryCheckStoreHouseId" name="storehouseId" >
        <input type="hidden" id="itemDTOs0.actualInventoryAmount" name="itemDTOs[0].actualInventoryAmount" >
        <table cellpadding="0" cellspacing="0" class="tabRecord">
            <col width="140">
            <col width="80">
            <col width="140">
            <tr class="tabTitle">
                <td style="padding-left:10px;">供应商</td>
                <td>账面库存</td>
                <td>实际库存</td>
            </tr>
        </table>
        <div style="height: 200px;overflow-x: hidden;overflow-y: auto">
            <table cellpadding="0" cellspacing="0" class="tabRecord" id="selectSupplierInventoryCheckTb">
                <col width="140">
                <col width="80">
                <col width="140">
            </table>
        </div>
    </form>
    <div class="txtAll">实际库存合计：<span id="inventoryCheckActualAmount">0</span>&nbsp;<span id="inventoryCheckActualUnit"></span></div>
    <div class="button"><a class="btnSure" id="singleInventoryCheck">盘&nbsp;点</a><a class="btnSure" id="cancelSingleInventoryCheck">取&nbsp;消</a></div>
</div>

<div id="inventoryCheck_dialog" style="display:none" class="alertMain">
    <table id="inventoryCheckRecord" class='tabRecord' style="margin-top: 10px">
        <tr class='tabTitle'>
            <td>盘点时间</td>
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                <td>仓库</td>
            </bcgogo:hasPermission>
            <td>盘盈盘亏</td>
            <td>盘点数量</td>
            <td>盘点金额</td>
            <td>操作人</td>
            <td>相关单据</td>
        </tr>
    </table>
    <div class="clear i_height"></div>
    <div class="hidePageAJAX">
        <bcgogo:ajaxPaging
                url="inventoryCheck.do?method=getInventoryCheckByProductId" postFn="initInventoryCheckRecord"
                dynamical="_inventory_check_record"/>
    </div>
    <div class="clear i_height"></div>
</div>
<div id="addNewProduct_dialog" style="display: none" class="alertMain addProducts">
    <div class="height"></div>
    <table cellpadding="0" cellspacing="0" class="tab_product">
        <col width="60px">
        <col width="125px">
        <col width="90px">
        <col width="125px">
        <tr>
            <td>商品编码</td>
            <td><input type="text" class="txt" name="new_commodityCode" maxlength="20"/></td>
            <td>商品分类</td>
            <td><input type="text" class="txt" name="new_productType" cacheField = "productKindSource" maxlength="20"/></td>
        </tr>
        <tr>
            <td>品名</td>
            <td><input type="text" class="txt" name="new_name" maxlength="50"/></td>
            <td>品牌/产地</td>
            <td><input type="text" class="txt" name="new_brand" maxlength="50"/></td>
        </tr>
        <tr>
            <td>规格</td>
            <td><input type="text" class="txt" name="new_spec" maxlength="50"/></td>
            <td>型号</td>
            <td><input type="text" class="txt" name="new_model" maxlength="50"/></td>
        </tr>
        <tr>
            <td>车辆品牌</td>
            <td><input type="text" class="txt" name="new_productVehicleBrand" searchField="brand"
                      cacheField="productVehicleBrandSource" maxlength="20"/></td>
            <td>车型</td>
            <td><input type="text" class="txt" name="new_productVehicleModel" searchField="model"
                       cacheField="productVehicleModelSource" maxlength="20"/></td>
        </tr>
        <tr>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE">
                    <td>零售价</td>
                    <td><input type="text" class="txt" name="new_recommendedPrice" maxlength="10"/></td>
                </bcgogo:if>
                <bcgogo:else>
                    <td>零售价</td>
                    <td></td>
                </bcgogo:else>
            </bcgogo:permission>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.TRADE_PRICE">
                    <td>批发价</td>
                    <td><input type="text" class="txt" name="new_tradePrice" maxlength="10"/></td>
                </bcgogo:if>
                <bcgogo:else>
                    <td>批发价</td>
                    <td></td>
                </bcgogo:else>
            </bcgogo:permission>
        </tr>
        <tr>
            <td>上限</td>
            <td><input type="text" class="txt" name="new_upperLimit" maxlength="10"/></td>
            <td>下限</td>
            <td><input type="text" class="txt" name="new_lowerLimit" maxlength="10"/></td>
        </tr>
        <%--<tr>--%>
            <%--<td>货位</td>--%>
            <%--<td><input type="text" class="txt" name="new_storageBin" maxlength="10"/></td>--%>
            <%--<td>仓库</td>--%>
            <%--<td>--%>
                <%--<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">--%>
                    <%--<select name="new_storehouseId" class="txt selTxt">--%>
                        <%--<c:forEach items="${storeHouseDTOList}" var="storeHouseDTO">--%>
                            <%--<option  value="${storeHouseDTO.id}">${storeHouseDTO.name}</option>--%>
                        <%--</c:forEach>--%>
                    <%--</select>--%>
                <%--</bcgogo:hasPermission>--%>
            <%--</td>--%>
        <%--</tr>--%>
        <tr>
            <td style="vertical-align:top;padding-top: 3px">商品主图</td>
            <td colspan="3" style="text-align:left;">
                <input type="hidden" id="new_imagePath" name="new_imagePath" value=""/>
                <div id="_newProductMainImageView" style="padding-left: 18px;float:left;position: relative;height:60px;width: 60px"></div>
                <div style="float:left;width: 250px;padding-left:10px">
                    <div style="float: left;width: 100%">
                        <div style="line-height:24px;text-align:left;float: left;width: 80px;">选择本地图片</div>
                        <div id="_addNewProductMainImageBtn"></div>
                    </div>
                    <div style="color: red;line-height: 18px;text-align: left">提示：所选图片都必须是 jpg、jpeg或png 格式，图片的大小不得超过5M。</div>
                </div>

        </td>
        </tr>
        <tr>
           	<td style="vertical-align:top;padding-top: 3px">单位</td>
           	<td colspan="3" style="text-align:left;">
               	<div class="addProductDivUnit">
                    <input type="text" class="txt" style="width:40px;" name="new_sellUnit" maxlength="10"/>
                    <a class="blue_color J_addProduct_addUnit">增加单位</a>
                </div>
                   <div class="addProductDivUnit J_addProductDivUnit" style="display: none">
                   	<div>增加单位：<input type="text" class="txt" value="" style="width:40px;" name="new_storageUnit" maxlength="10"/></div>
                    <div>换算比例：<input type="text" class="txt" style="width:40px;" name="new_sellUnitRate"/>&nbsp;<span class="unit J_newSellUnitSpan"></span>
                        =&nbsp;<input type="text" class="txt" style="width:40px;" name="new_storageUnitRate"/>&nbsp;<span class="unit J_newStorageUnitSpan"></span>&nbsp;
                        <a class="blue_color J_cancel_addProduct">取消</a>
                    </div>
                   </div>
               </td>
           </tr>

        <%--<tr>--%>
            <%--<td>小单位</td>--%>
            <%--<td>--%>
                <%--<input type="text" class="txt" name="new_sellUnit" maxlength="10"/>--%>
            <%--</td>--%>
            <%--<td>大单位</td>--%>
            <%--<td><input type="text" class="txt" name="new_storageUnit" maxlength="10"/></td>--%>
        <%--</tr>--%>
        <%--<tr>--%>
            <%--<td>比率</td>--%>
            <%--<td ><input type="text" class="txt" name="new_rate" maxlength="10"/></td>--%>
            <%--<td colspan="2" style="text-align:left;">（大单位转小单位）--%>
            <%--</td>--%>
        <%--</tr>--%>
        <%--<tr>--%>
            <%--<td colspan="4" class="gray_color" style="text-align:left; padding-left:30px;">备注：价格对应单位，大、小单位皆有时，默认对应小单位--%>
            <%--</td>--%>
        <%--</tr>--%>
    </table>
    <div class="height"></div>
    <div class="button"><a class="btnSure J_saveNewProduct">保&nbsp;存</a><a class="btnSure J_resetNewProduct">重&nbsp;置</a></div>
</div>
</div>

<script type="text/javascript">

    $(document).ready(function () {
        clearSort();
        if ('${fuzzyMatchingFlag}' == 'true') {
            getProductWithUnknownField();
        } else {
            //明确搜索框的内容对应哪个field 6字段全部搜索
            getProductWithCertainField();
        }
    });

    function getNode(o) {
        return G.get(o);
    }

    function fittinghover() {
        G.get('stock_fitting').className = 'stock_fittinghover';
        G.get('stock_articles').className = 'stock_articles';
        G.get('stock_fittingtitle').style.display = 'block';
        G.get('stock_articlestitle').style.display = 'none';
    }

    function fittingout() {
        G.get('stock_fitting').className = 'stock_fittinghover';
        G.get('stock_articles').className = 'stock_articles';
        G.get('stock_fittingtitle').style.display = 'block';
        G.get('stock_articlestitle').style.display = 'none';
    }

    function articleshover() {
        G.get('stock_fitting').className = 'stock_fitting';
        G.get('stock_articles').className = 'stock_articleshover';
        G.get('stock_fittingtitle').style.display = 'none';
        G.get('stock_articlestitle').style.display = 'block';
    }

    function articlesout() {
        G.get('stock_fitting').className = 'stock_fitting';
        G.get('stock_articles').className = 'stock_articleshover';
        G.get('stock_fittingtitle').style.display = 'none';
        G.get('stock_articlestitle').style.display = 'block';
    }

    function stockClick() {
        G.get('stock_content').style.display = 'block';
    }
</script>
<div id="downloadFileDiv" style="display:none">
    系统最多一次可导出10000条数据，您共有<span id="totalExport">0</span>条，系统将按顺序自动为您分为<span id="numOfExport">0</span>个文件，请选择需导出的文件：
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>