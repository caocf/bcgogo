<%@ page import="org.apache.commons.lang.StringUtils" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>商品</title>
    <%
    	boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn(WebUtil.getShopId(request));//选配仓位功能 默认开启这个功能false
    	boolean tradePriceTag = ServiceManager.getService(IShopConfigService.class).isTradePriceSwitchOn(WebUtil.getShopId(request),WebUtil.getShopVersionId(request));//选配批发价功能
    %>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/newTodo<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/stockSearch<%=ConfigController.getBuildVersion()%>.css"/>
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
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/goodsIndex<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/txn/stockSearch<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/setLimit<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        <bcgogo:permissionParam permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SET_UNIT,WEB.TXN.INVENTORY_MANAGE.ALARM,WEB.TXN.PURCHASE_MANAGE.STORAGE.SAVE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PRODUCT_CLASSIFY,WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.UPDATE_TRADE_PRICE,WEB.TXN.INVENTORY_MANAGE.SALE_PRICE_SETTING,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.NEW_STORAGE_PRICE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.INVENTORY,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.STORAGE_BIN,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.TRADE_PRICE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE，WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.NEW_STORAGE_PRICE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.AVERAGE_PRICE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.STORAGE_BIN,WEB.TXN.INVENTORY_MANAGE.PRODUCT_MODIFY,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.DELETE_PRODUCT">
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
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetSalePrice= ${WEB_TXN_INVENTORY_MANAGE_SALE_PRICE_SETTING};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.AveragePrice= ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_AVERAGE_PRICE};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.NewStoragePrice= ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_NEW_STORAGE_PRICE};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.StorageBin =${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_STORAGE_BIN};
            APP_BCGOGO.Permission.Txn.InventoryManage.ProductModify = ${WEB_TXN_INVENTORY_MANAGE_PRODUCT_MODIFY};
            APP_BCGOGO.Permission.Txn.InventoryManage.ProductDelete = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_DELETE_PRODUCT};
            APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetUnit = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_SET_UNIT};
        </bcgogo:permissionParam>
        <bcgogo:hasPermission  resourceType="menu" permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE||WEB.TXN.PURCHASE_MANAGE.STORAGE||WEB.TXN.SALE_MANAGE.SALE||WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE||WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE||WEB.TXN.PURCHASE_MANAGE.RETURN||WEB.TXN.SALE_MANAGE.RETURN||WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
            APP_BCGOGO.Permission.InquiryCenter = true;
        </bcgogo:hasPermission>
        function getGoodsHistory1(itemName, brand, spec, model) {
            bcgogo.checksession({'parentWindow':window,'iframe_PopupBox':$("#iframe_PopupBox")[0],
                                    'src':"goodsHistory.do?method=createGoodsHistory&orderType=WASH" +
                                            "&itemName=" + encodeURIComponent(itemName) + "&brand=" + encodeURIComponent(brand)
                                            + "&spec=" + encodeURIComponent(spec) + "&model=" + encodeURIComponent(model)});
        }

        $(window).load(function() {
            var searchtype = "searchgoodsbuy";
            var searchvalue = "${searchProductName}";
            $(function() {
                $("#a_select_id>a").bind("click", function() {
                    $("#a_select_id>a").removeAttr("class");
                    $(this).attr("class", "hover");
                    searchtype = this.name;
                });
            });


            $(function() {
                //单击放大镜时
                if ('${fuzzyMatchingFlag}' == 'true') {
                    initPagersOfStockSearch("StockSearchWithUnknownField");
                    getProductWithUnknownField();
                }else {
                    //明确搜索框的内容对应哪个field 6字段全部搜索
                    initPagersOfStockSearch("StockSearchWithCertainField");
                    getProductWithCertainField();
                }
                $("#test").hide();
                $("#test1").hide();
                $("#test2").hide();
                $(".i_leftBtn").hide();
                $(".i_bottom").show();
                $(".distance a").click(function() {
                    var tab = $(this).parent().parent().next();
                    if (tab.css("display") == "none") {
                        tab.slideDown();
                        $(this).css("background", "url('images/rightTop.png') no-repeat right");
                    }
                    else {
                        tab.slideUp();
                        $(this).css("background", "url('images/rightArrow.png') no-repeat right");
                    }

                });

                if(!APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.ProductClassify){
                    $("#_productType").attr("readonly",true);
                }
                if(!APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetUnit){
                    $("#_storageUnit").attr("readonly",true);
                    $("#_sellUnit").attr("readonly",true);
                }
            });

            function historySearch() {
                var starttime = $("#datetime").html();
                var endtime = $("#datetime2").html();
                if (starttime == "起始时间" || endtime == "终止时间") {
                    return;
                }
                window.location.assign("goodsindex.do?method=historySearch&starttime=" + starttime + "&endtime=" + endtime + "&searchtype=" + searchtype + "&searchvalue=" + searchvalue);
            }
        });

        function submitform(flag) {
            if (flag == 1) {
//        $("#productDTOListForm").attr("action", "RFbuy.do?method=create");
//        alert("shao");
//        window.location="RFbuy.do?method=create&productIds="+document.getElementById("checkedIds").value;
            }
            else if (flag == 2) {
                $("#productDTOListForm").attr("action", "storage.do?method=getProducts&type=good");
            }
            else if (flag == 3) {
                var b = false;//是否有库存为0的商品
                $("input[name='productIds']").each(function() {
                    if ($(this).attr("checked") == true) {
                        var id = $(this).attr("id");
                        if ($("#inventoryNum" + id).val() * 1 <= 0) {
                            b = true;
                        }
                    }
                });
                if (b) {
                    alert("库存小于等于0不能销售");
                    return;
                }
                else
                    $("#productDTOListForm").attr("action", "sale.do?method=getProducts&type=good");
            } else if (flag == 4) {
                $("#productDTOListForm").attr("action", "txn.do?method=getProducts");
            }
            else {
                return;
            }
            $("#productDTOListForm").submit();
        }

        $(document).ready(function() {
            if ($("#searchProductName").val() != null && $("#searchProductName").val() != '') {
                $("#goodsIndexKuCun").hide();
                $("#goodsIndexKuCun2").show();
            }
        });

        //        function showIt() {
        //            bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox_Limit")[0], 'src':"txn.do?method=setSale"});
        //        }
        //        function showSetTradePrice() {
        //            bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox_Limit':$("#iframe_PopupBox_Limit")[0], 'src':"txn.do?method=setTradePrice"});
        //        }
        //        function showSetproductKind() {
        //            bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox_Kind")[0], 'src':"txn.do?method=setProductKind"});
        //        }
    </script>


</head>
<body class="bodyMain">
<div class="stockSearch">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<input type="hidden" id="goodsIndex" value="goodsIndex"/>
<input type="hidden" id="searchProductName" value="${searchProductName}"/>
<input type="hidden" id="basePath" value="<%=basePath%>"/>
<input type="hidden" id="count" value="${count}"/>
<input type="hidden" id="count1" value="${count1}"/>
<input type="hidden" id="count2" value="${count2}"/>
<input type="hidden" id="count3" value="${count3}"/>
<input type="hidden" id="sortStatus" value=""/>
<input type="hidden" id="pageType" value="goodsIndex"/>
<input type="hidden" id="userGroup" value="<%=WebUtil.getUserGroupType(request)%>">
<!--js permission by zhangjuntao-->
<bcgogo:permissionParam permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE,WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH,WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS,WEB.TXN.INVENTORY_MANAGE.PRODUCT_MODIFY">
<input type="hidden" id="permissionGoodsBuy" value="${permissionParam1}"/>
<input type="hidden" id="permissionRepairOrder" value="${permissionParam2}"/>
<input type="hidden" id="permissionGoodsSale" value="${permissionParam3}"/>
<input type="hidden" id="permissionInventoryAlarmSettings" value="${permissionParam4}"/>
</bcgogo:permissionParam>
<input type="hidden" name="rowStart" id="rowStart" value="0">
<input type="hidden" name="pageRows" id="pageRows" value="25">
<input type="hidden" name="totalRows" id="totalRows" value="0">
<input type="hidden" name="totalRowsLowerLimit" id="totalRowsLowerLimit" value="0">
<input type="hidden" name="totalRowsUpperLimit" id="totalRowsUpperLimit" value="0">
<input type="hidden" id="storageBinTag" value="<%=storageBinTag%>"/>
<input type="hidden" id="tradePriceTag" value="<%=tradePriceTag%>"/>
<input type="hidden" id="limitOrPriceSwitch" value="price"/>
<bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH" resourceType="menu">
<div class="i_main">
<div class="mainTitle">商品</div>
<div class="i_mainRight" id="i_mainRight">
<bcgogo:permission>
  <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE&&WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE" resourceType="menu">
    <div class="distance">
        <span class="left_tuihuo"></span>

        <div class="new_sale">
            <label>缺料待修 　共<span class="qian_blue"><%=request.getAttribute("count1")%></span>条记录</label>
            <a href="javascript:void(0)" class="blue_col">更多</a>
        </div>
        <span class="right_tuihuo"></span>
    </div>
    <div id="test1">
        <table cellpadding="0" cellspacing="0" class="table2" id="sata_tab">
            <col width="50">
            <col width="60">
            <col width="80">
            <col width="120">
            <col width="100">
            <col width="70">
            <col/>
            <col width="110">
            <tr class="table_title">
                <td>No</td>
                <td>缺料品名</td>
                <td>车牌号</td>
                <td>联系人</td>
                <td>联系方式</td>
                <td>车型</td>
                <td>内容</td>
                <td>预计时间</td>
            </tr>
        </table>
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="goodsindex.do?method=lack"></jsp:param>
            <jsp:param name="jsHandleJson" value="initTr1"></jsp:param>
            <jsp:param name="dynamical" value="dynamical1"></jsp:param>
        </jsp:include>
    </div>
    <div class="clear"></div>
    <div class="i_height"></div>
</bcgogo:if>
</bcgogo:permission>
<bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
    <div class="distance">
        <span class="left_tuihuo"></span>

        <div class="new_sale">
            <label>来料待修 　共<span class="qian_blue"><%=request.getAttribute("count2")%></span>条记录</label><a
                href="javascript:void(0)" class="blue_col">更多</a>
        </div>
        <span class="right_tuihuo"></span>
    </div>
    <div id="test">
        <table cellpadding="0" cellspacing="0" class="num_new  num_salet table2" id="kucun">
            <col width="50">
            <col width="70">
            <col width="70">
            <col width="110">
            <col width="100">
            <col width="80">
            <col/>
            <col width="110">
            <col width="120">

            <tr class="table_title">
                <td style="border-left:none;">No</td>
                <td>来料品名</td>
                <td>车牌号</td>
                <td>客户名</td>
                <td>联系方式</td>
                <td>车型</td>
                <td>内容</td>
                <td>进厂时间</td>
                <td style="border-right:none;">预计时间</td>
            </tr>
        </table>
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="goodsindex.do?method=incoming"></jsp:param>
            <jsp:param name="jsHandleJson" value="initTr2"></jsp:param>
            <jsp:param name="dynamical" value="dynamical2"></jsp:param>
        </jsp:include>
    </div>
    <div class="clear"></div>
    <div class="i_height clear"></div>
</bcgogo:hasPermission>

<bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PENDING_STORAGE">
    <div class="distance">
        <span class="left_tuihuo"></span>

        <div class="new_sale">
            <label>待入库&nbsp;&nbsp;共<span class="qian_blue">${count3}</span>条记录</label>
            <a href="javascript:void(0)" class="blue_col">更多</a>
        </div>
        <span class="right_tuihuo"></span>
    </div>
    <div id="test2">
        <table cellpadding="0" cellspacing="0" class="table2 stock_tab" id="tab_three">
            <col width="50">
            <col/>
            <col width="130">
            <col width="130">
            <col width="100">
            <col width="100">
            <col width="60">
            <col width="70">
            <col width="60">
            <tr class="table_title">
                <td style="border-left:none;">NO</td>
                <td>供应商</td>
                <td>品名</td>
                <td>品牌/产地</td>
                <td>规格</td>
                <td>型号</td>
                <td class="txt_right">单价</td>
                <td class="txt_right">数量</td>
                <td style="border-right:none;" class="txt_right">金额</td>
            </tr>
        </table>
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="goodsindex.do?method=waitcoming"></jsp:param>
            <jsp:param name="jsHandleJson" value="initTr3"></jsp:param>
            <jsp:param name="dynamical" value="dynamical3"></jsp:param>
        </jsp:include>
    </div>
    <div class="i_height clear"></div>
</bcgogo:hasPermission>


<span class="left_tuihuo"></span>

<div class="new_sale clear">
    <label>库存信息</label>

    <div style="float:left; padding-left:20px;">
        共
        <span id="inventoryCount" class="qian_blue">0</span>
        种
    </div>
    <div style="float:left; padding-left:20px;">
        数量：
        <span id="inventoryProductAmount" class="qian_blue">0</span>
    </div>
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
        <div style="float:left;padding-left:20px;">
            金额
            <span id="inventorySum" class="qian_blue">0</span>
            元
        </div>
    </bcgogo:hasPermission>
     <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.ALARM">
        <label class="lackName">库存告警</label>

        <div class="lack" id="lowerLimit_click">
            缺货：
            <span id="lowerLimitCount">0</span>
        </div>
        <div class="lack" id="upperLimit_click">
            超出：
            <span id="upperLimitCount">0</span>
        </div>
     </bcgogo:hasPermission>
</div>
<div class="new_sale distance clear" style="line-height:1.5;padding-bottom:5px;">
    <label>库存查询 </label>
    <input type="hidden" autocomplete="off" id="searchProductIds" name="searchProductIds" value="${productIds}"/>

    <input type="text" class="stock_text J-productSuggestion J-initialCss J_clear_input" id="searchWord" name="searchWord" searchField="product_info" value="${searchWord}" initialValue="品名/品牌/规格/型号/适用车辆" style="width:200px;margin-left:15px;"/>
    <input type="text" class="stock_text J-productSuggestion J-initialCss J_clear_input" id="productName" name="productName" searchField="product_name" value="${searchProductName}" initialValue="品名" style="width:60px;"/>
    <input type="text" class="stock_text J-productSuggestion J-initialCss J_clear_input" id="productBrand" name="productBrand" searchField="product_brand" value="${searchProductBrand}" initialValue="品牌/产地" style="width:60px;"/>
    <input type="text" class="stock_text J-productSuggestion J-initialCss J_clear_input" id="productSpec" name="productSpec" searchField="product_spec" value="${searchProductSpec}" initialValue="规格" style="width:60px;"/>
    <input type="text" class="stock_text J-productSuggestion J-initialCss J_clear_input" id="productModel" name="productModel" searchField="product_model" value="${searchProductModel}" initialValue="型号" style="width:60px;"/>
    <input type="text" class="stock_text J-productSuggestion J-initialCss J_clear_input" id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand" value="${searchProductVehicleBrand}" initialValue="车辆品牌" style="width:60px;"/>
    <input type="text" class="stock_text J-productSuggestion J-initialCss J_clear_input" id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model" value="${searchProductVehicleModel}" initialValue="车型" style="width:60px;"/>
    <input type="text" class="stock_text J-productSuggestion J-initialCss J_clear_input" id="commodityCode" name="commodityCode" searchField="commodity_code" value="${searchCommodityCode}" initialValue="商品编号" style="text-transform: uppercase;width:60px;"/>


    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
        <div style="float:left;width:120px;">
            <select id="storehouseText" style="color: #ADADAD;width:90px;height:21px;border: #ADADAD 1px solid ;" initialValue="所有仓库" autocomplete="off">
                <option style="color: #ADADAD;" value="">所有仓库</option>
                <c:forEach items="${storeHouseDTOList}" var="storeHouseDTO">
                    <option style="color: #000000;" value="${storeHouseDTO.id}">${storeHouseDTO.name}</option>
                </c:forEach>
            </select>
        </div>
    </bcgogo:hasPermission>
</div>
<div class="new_sale add_info clear" style="padding-bottom:5px;line-height:1.5;">
    <input id="product_kind" style="width:60px;margin-left: 67px" class="stock_text J-initialCss J_clear_input" type="text" tabindex="6" autocomplete="off" initialValue="商品分类"
           value="商品分类" inputtype="stocksearch"/>

    <input id="supplierInfoSearchText" class="stock_text J-initialCss J_clear_input" type="text" tabindex="9" autocomplete="off" style="width:140px;"
           initialValue="供应商/联系人/手机" pagetype="stockSearch"/>
    <div class="stock_txtName">
        <input type="button" class="stock_search" id="searchInventoryBtn" value="" onfocus="this.blur();"/>
        <input type="button" class="btn_clear" id="clearConditionBtn" value="清空条件" onfocus="this.blur();"/>
    </div>
</div>
<span class="right_tuihuo"></span>

<div class="i_height"></div>
<form id="productDTOListForm" action="" method="post">
    <div class="slider-main-area">
        <table cellpadding="0" cellspacing="0" class="table2 slider-main-table" id="table_productNo">
            <tr class="table_title">
                <th style="width:2%;">
                    <label name="checkAll" id="checkAlls" hidefocus="true" class="checkbox"></label>
                    <input type="checkbox"/>
                </th>
                <th style="width:10%;">商品编号</th>
                <th style="width:18%;" class="clearfix">
                    <div class="fl" style="line-height:2;">品名</div>
                    <input type="button" class="ascending sort fl" id="product_name_sort" hidefocus="true" onclick="switchStyle(this,'name')"/></th>
                <th style="width:10%;" class="clearfix">
                    <div class="fl" style="line-height:2;">品牌/产地</div>
                    <input type="button" class="ascending sort fl" id="product_brand_sort" hidefocus="true" onclick="switchStyle(this,'brand')"/>
                </th>
                <th style="width:14%;">
                    <div class="fl" style="line-height:2;">规格/型号</div>
                    <input type="button" class="ascending sort fl" id="product_spec_sort" hidefocus="true" onclick="switchStyle(this,'spec')"/>
                </th>
                <th style="width:9%;">品牌/车型</th>
                <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.INVENTORY">
                    <th style="width:8%;">
                        <div class="product-inventory-sort clearfix">
                            <div class="fl" style="line-height:2;">库存</div>
                            <input type="button" class="ascending sort fl" id="product_inventory_amount_sort" hidefocus="true" />
                        </div>
                    </th>
                </bcgogo:hasPermission>
                <bcgogo:permission>
                    <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.NEW_STORAGE_PRICE&&WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.AVERAGE_PRICE">
                        <th style="width:10%;">均价/最新价</th>
                    </bcgogo:if>
                    <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.NEW_STORAGE_PRICE">
                        <th style="width:10%;">最新价</th>
                    </bcgogo:if>
                    <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.AVERAGE_PRICE">
                        <th style="width:10%;">均价</th>
                    </bcgogo:if>
                </bcgogo:permission>
                <bcgogo:permission>
                    <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.SALE_PRICE_SETTING&&WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE">
                        <th class="product_setting product_setting_btnSaleja" style="width:6%;">
                            <input type="button" value="销售价" class="se_xiaoshou" onclick="showIt()"/>
                        </th>
                    </bcgogo:if>
                    <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE">
                        <th class="product_setting product_setting_btnSaleja" style="width:6%;">销售价</th>
                    </bcgogo:if>
                </bcgogo:permission>
                <bcgogo:permission>
                    <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.TRADE_PRICE&&WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.UPDATE_TRADE_PRICE">
                        <th class="trade_price_td" style="width:8%;"><input class="se_xiaoshou" type="button" value="批发价" onclick="showSetTradePrice()" hidefocus="true"></th>
                    </bcgogo:if>
                    <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.TRADE_PRICE">
                        <th class="trade_price_td" style="width:8%;">批发价</th>
                    </bcgogo:if>
                </bcgogo:permission>
                <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.DELETE_PRODUCT">
                    <th style="width:5%">操作</th>
                </bcgogo:hasPermission>
                    <%--后续功能，勿删--%>
                    <%--<th style="border-right:none;display: none" class="product_setting product_setting_btnSaleja">--%>
                    <%--<input type="button" value="销售提成" class="se_xiaoshou"/>--%>
                    <%--</th>--%>
                    <%--后续功能，勿删 --%>
                    <%--<th style="border-right:none;display: none" class="product_setting product_setting_btnShan">--%>
                    <%--<input type="button" value="营业归类" class="se_xiaoshou"/>--%>
                    <%--</th>--%>
                    <%--<th style="border-right:none;display: none" class="product_setting product_setting_btnShan">--%>
                    <%--<input type="button" value="商品归类" class="se_xiaoshou"/>--%>
                    <%--</th>--%>
                    <%--<th style="border-right:none;display: none" class="product_setting product_setting_btnHuowei">--%>
                    <%--<input type="button" value="仓库" class="se_xiaoshou"/>--%>
                    <%--</th>--%>
                    <%--<th style="border-right:none;display: none" class="product_setting product_setting_btnHuowei">--%>
                    <%--<input type="button" value="货位" class="se_xiaoshou"/>--%>
            </tr>
        </table>

        <div class="slider-sub-area">
            <table id="table_productDetail" class="slider-sub-table table2" >
                <tr class="table_title">
                    <th class="product_setting product_setting_btnSaleja" style="width:15%;">
                        <bcgogo:permission>
                            <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PRODUCT_CLASSIFY">
                                <input type="button" value="商品分类" class="se_xiaoshou fl" onclick="showSetproductKind()"/>
                                <input type="button" class="ascending sort fl" id="product_kind_sort" hidefocus="true" onclick="switchStyle(this,'productKind')"/>
                                <input type="hidden" id="oldKindName"/>
                            </bcgogo:if>
                            <bcgogo:else>
                                商品分类
                            </bcgogo:else>
                        </bcgogo:permission>
                    </th>
                    <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.STORAGE_BIN">
                        <bcgogo:permission>
                        <bcgogo:if resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                            <%--do nothing--%>
                        </bcgogo:if>
                        <bcgogo:else>
                            <th class="storage_bin_td" style="width:12%;">货位</th>
                        </bcgogo:else>
                    </bcgogo:permission>
                    </bcgogo:hasPermission>
                    <bcgogo:permission>
                        <th class="td_input product_setting product_setting_btnExchange" style="width:15%;">
                            <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS">
                                <input class="over" type="button" onclick="showSetLimit(true);" value="下/上限" onfocus="this.blur();" title="下/上限"/>
                            </bcgogo:if>
                            <bcgogo:else>下/上限</bcgogo:else>
                        </th>
                    </bcgogo:permission>
                </tr>
            </table>
        </div>

        <i class="slider-btnExpand"><i></i></i>
    </div>
</form>
<div class="i_height clear"></div>
<div class="goodsCount" style="display: none;"><span class="qian_blue">25</span>条</div>
<div id="goodsIndexKuCun">
</div>

<jsp:include page="/common/pageAJAXForSolr.jsp">
    <jsp:param name="dynamical" value="StockSearchWithUnknownField"></jsp:param>
    <jsp:param name="buttonId" value="getProductWithUnknownField"></jsp:param>
</jsp:include>
<jsp:include page="/common/pageAJAXForSolr.jsp">
    <jsp:param name="dynamical" value="StockSearchWithCertainField"></jsp:param>
    <jsp:param name="buttonId" value="getProductWithCertainField"></jsp:param>
</jsp:include>
<jsp:include page="/common/pageAJAXForSolr.jsp">
    <jsp:param name="dynamical" value="StockSearchWithUpLimit"></jsp:param>
    <jsp:param name="buttonId" value="limitAction"></jsp:param>
</jsp:include>
<jsp:include page="/common/pageAJAXForSolr.jsp">
    <jsp:param name="dynamical" value="StockSearchWithDownLimit"></jsp:param>
    <jsp:param name="buttonId" value="limitAction"></jsp:param>
</jsp:include>
<div class="clear"></div>
<div class="height"></div>


<div class="table_goodsbtn">
<bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PRINT"></bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PRINT">
        <form id = "editForm" name = "editForm" method = "post" style="float:left;" action = "stockSearch.do?method=getProductDateToPrint" target = "colors123"   onsubmit = "openSpecfiyWindown( 'colors123' )" >
            <input type = "hidden" id="jsonStr" name="jsonStr"/>
            <input type = "hidden" id="currentPage" name="currentPage"/>
            <input type = "submit" value="打印" class="i_operate" onfocus="this.blur();" id="submitPrint"/>
        </form >
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
        <input type="button" value="采购" class="i_operate" onfocus="this.blur();" onclick="buy(1);"/>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
        <input type="button" value="入库" class="i_operate" onfocus="this.blur();" onclick="buy(2);"/>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
        <input type="button" value="销售" class="i_operate" onfocus="this.blur();" onclick="buy(3);"/>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE" resourceType="menu">
        <input type="button" value="施工" class="i_operate" onfocus="this.blur();" onclick="buy(4);"/>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.RETURN" resourceType="menu">
        <input type="button" value="入库退货" class="i_operate" onfocus="this.blur();" onclick="buy(5);"/>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.RETURN" resourceType="menu">
    <input type="button" value="销售退货" class="i_operate" onfocus="this.blur();" onclick="buy(7);"/>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INVENTORY_PRICE_ADJUSTMENT.BASE" resourceType="menu">
        <input type="button" value="库存盘点" class="i_operate" onfocus="this.blur();" onclick="buy(6);"/>
    </bcgogo:hasPermission>
</div>
</div>
</div>
</bcgogo:hasPermission>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;overflow:hidden;"
        allowtransparency="true" width="1000px" height="450px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_2" style="position:absolute;z-index:9; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="450px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_Limit" style="position:absolute;z-index:5; left:40%; top:35%; display:none;"
        allowtransparency="true" width="286px" height="180px" frameborder="0" src="" scrolling="no"></iframe>
<iframe id="iframe_PopupBox_Kind" style="position:absolute;z-index:5; left:40%; top:35%; display:none;"
        allowtransparency="true" width="286px" height="400px" frameborder="0" src="" scrolling="no"></iframe>
<div id="deleteProduct_dialog" style="display:none">
    <div id="deleteProduct_msg"></div>
</div>

<div id="pop_info" class="upInfo" style="display:none;">
<table cellpadding="0" cellspacing="0" class="tabInfo">
    <col width="80">
    <col width="180">
    <col width="90">
    <col width="130">
    <col width="90">
    <col width="150">
    <col width="90">
    <col width="50">
    <tr>
        <td>商品编码：</td>
        <td><input type="text" class="txt" id="_commodityCode" maxlength="20"/></td>
        <td>商品分类：</td>
        <td><input type="text" class="txt" id="_productType" maxlength="20"/></td>
        <td>库存上限：</td>
        <td>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS">
                    <input type="text" class="txt" id="_upperLimit" maxlength="8"/>
                </bcgogo:if>
                <bcgogo:else>
                    <input type="hidden" class="txt" id="_upperLimit" maxlength="8"/>
                </bcgogo:else>
            </bcgogo:permission>

        </td>
        <td <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">rowspan="8"</bcgogo:hasPermission> style="vertical-align:text-top; line-height:30px;">库存总量：</td>
        <td style="text-align:left;">
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.INVENTORY">
                    <span id="_inventoryNum"></span>
                </bcgogo:if>
                <bcgogo:else>
                    <span id="_inventoryNum" style='display:none;'></span>
                </bcgogo:else>
            </bcgogo:permission>
        </td>
    </tr>
    <tr>
        <td>品名：</td>
        <td><input type="text" class="txt" id="_name" maxlength="50"/></td>
        <td>最新入库价：</td>
        <td style="text-align:left;">
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.NEW_STORAGE_PRICE">
                    <span id="_purchasePrice" class="span_purchase_price" style='display:none;'></span> <%--todo zhangjuntao --%>
                </bcgogo:if>
                <bcgogo:else>
                    <span id="_purchasePrice" style='display:none;'></span>
                </bcgogo:else>
            </bcgogo:permission>
        </td>
        <td>库存下限：</td>
        <td>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS">
                    <input type="text" class="txt" id="_lowerLimit" maxlength="8"/>
                </bcgogo:if>
                <bcgogo:else>
                    <input type="hidden" class="txt" id="_lowerLimit" maxlength="8"/>
                </bcgogo:else>
            </bcgogo:permission>
        </td>
        <bcgogo:permission>
            <bcgogo:if resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                <%--do nothing--%>
            </bcgogo:if>
            <bcgogo:else>
                <td rowspan="7" style="vertical-align:text-top; line-height:30px;">货位：</td>
                <td style="text-align:left;">
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.STORAGE_BIN">
                            <input id="_storageBin" class="txt" type="text" maxlength="10" span="" autocomplete="off">
                        </bcgogo:if>
                        <bcgogo:else>
                            <input id="_storageBin" class="txt" type="hidden" maxlength="10" span="" autocomplete="off">
                        </bcgogo:else>
                    </bcgogo:permission>
                </td>
            </bcgogo:else>
        </bcgogo:permission>
    </tr>
    <tr>
        <td>品牌/产地：</td>
        <td><input type="text" class="txt" id="_brand" maxlength="50"/></td>
        <td>平均价：</td>
            <td style="text-align:left;">
                <bcgogo:permission>
                    <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.AVERAGE_PRICE">
                        <span id="_inventoryAveragePrice" class="inventory_average_price" style='display:none;'></span>
                    </bcgogo:if>
                    <bcgogo:else>
                        <span id="_inventoryAveragePrice" class="inventory_average_price" style='display:none;'></span>
                    </bcgogo:else>
                </bcgogo:permission>
            </td>
        <td>单位：</td>
        <td style="text-align:left;">
            <span id="smallRate" style="display:none;width:5%">1</span>
            <input type="text" class="txt" id="_storageUnit" maxlength="6" style="width:25%;display:none"/>
            <span id="equal" style="display:none;width:2%">=</span>
            <span id="rate" style="display:none;width:5%"></span>
            <input type="text" class="txt" id="_sellUnit" maxlength="6"/>
        </td>
    </tr>
    <tr>
        <td>规格：</td>
        <td><input type="text" class="txt" id="_spec" maxlength="50"/></td>
        <td>销售价：</td>
        <td>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.SALE_PRICE_SETTING">
                    <input type="text" class="txt" id="_recommendedPrice" maxlength="10"/>
                </bcgogo:if>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE">
                    <input type="text" class="txt" id="_recommendedPrice" readonly="readonly" maxlength="10"/>
                </bcgogo:if>
                <bcgogo:else>
                    <input type="hidden"  id="_recommendedPrice" />
                </bcgogo:else>
            </bcgogo:permission>
        </td>
        <td>供应商1：</td>
        <td style="text-align:left;"><span id="_productSupplier1"></span></td>
    </tr>
    <tr>
        <td>型号：</td>
        <td><input type="text" class="txt" id="_model" maxlength="50"/></td>
        <td>批发价：</td>
        <td>
            <bcgogo:permission>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.UPDATE_TRADE_PRICE">
                    <input type="text" id="_tradePrice" class="span_purchase_price txt" style='display:none;' maxlength="10"/>
                </bcgogo:if>
                <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.TRADE_PRICE">
                    <input type="text" id="_tradePrice" class="span_purchase_price txt" readonly="readonly" style='display:none;' maxlength="10"/>
                </bcgogo:if>
                <bcgogo:else>
                    <input type="hidden"  id="_tradePrice" />
                </bcgogo:else>
            </bcgogo:permission>
        </td>
        <td>供应商2：</td>
        <td style="text-align:left;"><span id="_productSupplier2"></span></td>
    </tr>
    <tr>
        <td>车辆品牌：</td>
        <td><input type="text" class="txt" id="_productVehicleBrand" maxlength="20"/></td>
        <td></td>
        <td></td>
        <td>供应商3：</td>
        <td style="text-align:left;"><span id="_productSupplier3"></span></td>
    </tr>
    <tr>
        <td>车型：</td>
        <td><input type="text" class="txt" id="_productVehicleModel" maxlength="20"/></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td></td>
        <td class="btnClick" id="btn" colspan="7">
            <input id="updateProduct" class="btnClick" type="button" style="margin-left: 10px"
                   onfocus="this.blur();" value="保 存"/>
            <input id="resetProductInfo" class="btnClick" type="button" onfocus="this.blur();" value="重 置"/>
        </td>
    </tr>
</table>
<input type="hidden" id="numIndex"/>

<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
    <div class="cangku">
        <c:forEach items="${storeHouseDTOList}" var="storeHouseDTO">
            <label>${storeHouseDTO.name}</label>
            <div class="kucun">
                库存：
                <bcgogo:permission>
                    <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.INVENTORY">
                        <span style="width:30px; display:inline-block;" id="_${storeHouseDTO.id}_storehouse_inventory_amount">0</span>
                    </bcgogo:if>
                    <bcgogo:else>
                        <span style="width:30px; display: none;" id="_${storeHouseDTO.id}_storehouse_inventory_amount">0</span>
                    </bcgogo:else>
                </bcgogo:permission>
                货位：
                <bcgogo:permission>
                    <bcgogo:if permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.INVENTORY">
                        <input type="text" class="txt" id="_${storeHouseDTO.id}_storehouse_storageBin" data-storehouse-id="${storeHouseDTO.id}" maxlength="10" style="width: 80px;"/>
                    </bcgogo:if>
                    <bcgogo:else>
                        <input type="hidden"  id="_${storeHouseDTO.id}_storehouse_storageBin" data-storehouse-id="${storeHouseDTO.id}"/>
                    </bcgogo:else>
                </bcgogo:permission>
            </div>
        </c:forEach>
    </div>
</bcgogo:hasPermission>
<div class="i_height"></div>
<bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.INVENTORY_PRICE_ADJUSTMENT.ADJUST" resourceType="menu">
    <div class="priceAdjust">
        <div class="leftPrice"></div>
        <div class="bodyPrice">
            <label>盘点调价</label>
            <div class="divPrice btnClick">
                实际库存：<input id="_actualInventoryNum" type="text" class="txt" style="width: 100px;" maxlength="8"/>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
                <div style="float:left;width:170px;">
                    仓库：
                    <select id="_storehouseId" name="_storehouseId" style="width:120px;height:21px;border: 1px solid #FFAA06;background: none repeat scroll 0 0 #FFFFFF;" autocomplete="off">
                        <c:forEach items="${storeHouseDTOList}" var="storeHouseDTO">
                            <option value="${storeHouseDTO.id}">${storeHouseDTO.name}</option>
                        </c:forEach>
                    </select>
                </div>
                </bcgogo:hasPermission>
                &nbsp;库存均价：<input id="_actualInventoryAveragePrice" type="text" class="actual_Inventory_AveragePrice" maxlength="10" style="width: 100px;display:none;"/>
                <input type="button" id="updatePandian" value="确&nbsp;定" class="btnClick" onfocus="this.blur();"/>
            </div>
        </div>
        <div class="rightPrice"></div>
    </div>
</bcgogo:hasPermission>
<div class="clear i_height"></div>
</div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>