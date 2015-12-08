<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>员工商品提成配置</title>
  <%
    boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn(WebUtil.getShopId(request));//选配仓位功能 默认开启这个功能false
    boolean tradePriceTag = ServiceManager.getService(IShopConfigService.class).isTradePriceSwitchOn(WebUtil.getShopId(request), WebUtil.getShopVersionId(request));//选配批发价功能
  %>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/cuSearch<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/assistantStatConfig<%=ConfigController.getBuildVersion()%>.css"/>

  <style type="text/css">
    #table_productNo .table_title td {
      text-align: center;
    }

    .table2 tr.table_title td {
      padding-left: 0px;
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
    defaultStorage.setItem(storageKey.MenuUid,"WEB.STAT.AGENT_ACHIEVEMENTS.CONFIG");

    <bcgogo:permissionParam permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SET_UNIT,WEB.TXN.INVENTORY_MANAGE.ALARM,WEB.TXN.PURCHASE_MANAGE.STORAGE.SAVE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.PRODUCT_CLASSIFY,WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.UPDATE_TRADE_PRICE,WEB.TXN.INVENTORY_MANAGE.SALE_PRICE_SETTING,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.NEW_STORAGE_PRICE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.INVENTORY,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.STORAGE_BIN,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.TRADE_PRICE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.SALE_PRICE，WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.NEW_STORAGE_PRICE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.AVERAGE_PRICE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.STORAGE_BIN,WEB.TXN.INVENTORY_MANAGE.PRODUCT_MODIFY,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.DELETE_PRODUCT">
    //新增入库
    APP_BCGOGO.Permission.Txn.PurchaseManage.StorageSave = ${WEB_TXN_PURCHASE_MANAGE_STORAGE_SAVE};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.ProductClassify = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_PRODUCT_CLASSIFY};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.Alarm = ${WEB_TXN_INVENTORY_MANAGE_ALARM};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.AlarmSettings = ${WEB_TXN_INVENTORY_MANAGE_ALARM_SETTINGS};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.Inventory = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_INVENTORY};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.StorageBin = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_STORAGE_BIN};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetTradePrice = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_UPDATE_TRADE_PRICE};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.TradePrice = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_TRADE_PRICE};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SalePrice = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_SALE_PRICE};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetSalePrice = ${WEB_TXN_INVENTORY_MANAGE_SALE_PRICE_SETTING};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.AveragePrice = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_AVERAGE_PRICE};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.NewStoragePrice = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_NEW_STORAGE_PRICE};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.StorageBin =${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_STORAGE_BIN};
    APP_BCGOGO.Permission.Txn.InventoryManage.ProductModify = ${WEB_TXN_INVENTORY_MANAGE_PRODUCT_MODIFY};
    APP_BCGOGO.Permission.Txn.InventoryManage.ProductDelete = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_DELETE_PRODUCT};
    APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetUnit = ${WEB_TXN_INVENTORY_MANAGE_STOCK_SEARCH_SET_UNIT};
    </bcgogo:permissionParam>
    <bcgogo:hasPermission resourceType="menu" permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE||WEB.TXN.PURCHASE_MANAGE.STORAGE||WEB.TXN.SALE_MANAGE.SALE||WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE||WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE||WEB.TXN.PURCHASE_MANAGE.RETURN||WEB.TXN.SALE_MANAGE.RETURN||WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
    APP_BCGOGO.Permission.InquiryCenter = true;
    </bcgogo:hasPermission>
  </script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/txn/stockSearch<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/stat/assistantStat/allProductConfig<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
    APP_BCGOGO.UserGuide.currentPageIncludeGuideStep = "PRODUCT_ONLINE_GUIDE_TXN";
    APP_BCGOGO.UserGuide.currentPage = "stockSearch";
    function getGoodsHistory1(itemName) {
      bcgogo.checksession({'parentWindow':window,'iframe_PopupBox':$("#iframe_PopupBox_1")[0],'src':"goodsHistory.do?method=createGoodsHistory&orderType=WASH&itemName=" + encodeURIComponent(itemName) + "&time=" + new Date()});
    }
    var shopId = '${sessionScope.shopId}';
    $(function() {
      $(".i_leftBtn").hide();
      $(".i_bottom").show();
      $(".J-more").click(function() {
        var tab = $(this).next();

        if (tab.css("display") == "none") {
          tab.show();

          $('.arrow_down', this).css("background", "url('images/arrowsUp.png') no-repeat right");
        }
        else {
          tab.hide();
          $('.arrow_down', this).css("background", "url('images/arrowsDown.png') no-repeat right");
        }

      });
      if (!APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.ProductClassify) {
        $("#_productType").attr("readonly", true);
      }
      if (!APP_BCGOGO.Permission.Txn.InventoryManage.StockSearch.SetUnit) {
        $("#_storageUnit").attr("readonly", true);
        $("#_sellUnit").attr("readonly", true);
      }
    });

    function showSetProductTotalAchievement() {
      bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox_Kind")[0], 'src':"assistantStat.do?method=setProductAchievement"});
    }

    function showSetProductProfitAchievement() {
      bcgogo.checksession({"parentWindow":window.parent, 'iframe_PopupBox':$("#iframe_PopupBox_Kind")[0], 'src':"assistantStat.do?method=setProductAchievement"});
    }

  </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>

<div class="stockSearch">
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<input type="hidden" id="sortStatus" value="inventoryAmountDesc"/>
<input type="hidden" id="pageType" value="allProductConfig"/>
<input type="hidden" id="searchType" value=""/>
<input id="unConfig" type="hidden" name="unConfig" value="${unConfig}"/>

<%--<input type="hidden" id="userGroup" value="<%=WebUtil.getUserGroupType(request)%>">--%>
<input type="hidden" id="numIndex"/>
<input type="hidden" id="moreSupplierPageSize" value="10">
<bcgogo:permissionParam permissions="WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS,WEB.TXN.INVENTORY_MANAGE.PRODUCT_MODIFY">
  <input type="hidden" id="permissionInventoryAlarmSettings" value="${WEB_TXN_INVENTORY_MANAGE_ALARM_SETTINGS}"/>
  <input type="hidden" id="permissionProductModify" value="${WEB_TXN_INVENTORY_MANAGE_PRODUCT_MODIFY}"/>

</bcgogo:permissionParam>
<bcgogo:permissionParam resourceType="menu"
                        permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE,WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH">
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


<div class="i_main clear">


    <div class="mainTitles">
    <div class="titleWords">提成配置</div>

  </div>
  <div class="titBodys">
    <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
      <a class="normal_btn" href="assistantStat.do?method=redirectSalesManConfig">施工提成</a>
    </bcgogo:hasPermission>

    <a class="normal_btn" href="assistantStat.do?method=searchSalesManConfig">员工部门</a>
    <a class="hover_btn" href="assistantStat.do?method=redirectProductConfig">销售提成</a>
  </div>


  <div class="i_mainRight" id="i_mainRight">

    <%--<div class="group_list">--%>
      <%--共&nbsp;<b class="blue_color" id="inventoryCount">0</b>&nbsp;种&nbsp;&nbsp;--%>
      <%--数量&nbsp;<b class="yellow_color" id="inventoryProductAmount">0</b>&nbsp;--%>
      <%--<bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE">--%>
        <%--金额&nbsp;<b class="yellow_color"><span class="arialFont">&yen;</span><span id="inventorySum">0</span></b>&nbsp;&nbsp;--%>
      <%--</bcgogo:hasPermission>--%>
      <%--<span style="cursor:pointer;" id="totalShopAchievementConfigSpan">未设置提成&nbsp;<b class="blue_color"--%>
                                                                                      <%--id="totalShopAchievementConfig">${totalShopAchievementConfig}</b>&nbsp;种&nbsp;&nbsp;</span>--%>
      <%--<strong class="fr"><a href="#" onclick="achievementNotify()" class="yellow_color">进入业绩统计></a></strong>--%>
    <%--</div>--%>

    <div class="group_list">
      <span style="cursor:pointer;" id="totalShopAchievementConfigSpan">未设置提成&nbsp;<b class="blue_color"
                                                                                      id="totalShopAchievementConfig">${totalShopAchievementConfig}</b>&nbsp;种&nbsp;&nbsp;</span>
      <strong class="fr"><a href="#" onclick="achievementNotify()" class="yellow_color">进入业绩统计></a></strong>
    </div>

    <div class="clear i_height"></div>
    <div class="titBody">
      <div class="lineTop"></div>
      <div class="lineBody lineAll">
        <div class="divTit">
          <b>库存查询&nbsp;</b>

          <div class="txt_Search">
            <input type="hidden" id="searchProductIds" name="searchProductIds" value="${productIds}"/>
            <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="searchWord"
                   name="searchWord" searchField="product_info" value="${searchWord}" initialValue="品名/品牌/规格/型号/适用车辆"
                   style="width:210px;"/>
            <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="productName"
                   name="productName" searchField="product_name" value="${searchProductName}" initialValue="品名"
                   style="width:70px;"/>
            <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="productBrand"
                   name="productBrand" searchField="product_brand" value="${searchProductBrand}" initialValue="品牌/产地"
                   style="width:80px;"/>
            <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="productSpec"
                   name="productSpec" searchField="product_spec" value="${searchProductSpec}" initialValue="规格"
                   style="width:80px;"/>
            <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="productModel"
                   name="productModel" searchField="product_model" value="${searchProductModel}" initialValue="型号"
                   style="width:80px;"/>
            <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="productVehicleBrand"
                   name="productVehicleBrand" searchField="product_vehicle_brand" value="${searchProductVehicleBrand}"
                   initialValue="车辆品牌" style="width:80px;"/>
            <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="productVehicleModel"
                   name="productVehicleModel" searchField="product_vehicle_model" value="${searchProductVehicleModel}"
                   initialValue="车型" style="width:80px;"/>
            <input class="J-initialCss J_clear_input txt" type="text" id="product_kind" autocomplete="off"
                   initialValue="商品分类" inputtype="stocksearch" style="width:60px;"/>
            <input type="text" class="J-productSuggestion J-initialCss J_clear_input txt" id="commodityCode"
                   name="commodityCode" searchField="commodity_code" initialValue="商品编号" value="${searchCommodityCode}"
                   style="text-transform: uppercase;width:70px;"/>
            <input id="supplierInfoSearchText" class="J-initialCss J_clear_input txt" type="text" tabindex="9"
                   autocomplete="off" style="width:140px;" initialValue="供应商/联系人/手机" pagetype="stockSearch"/>
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.STOREHOUSE">
              <select id="storehouseText" class="txt selTxt" initialValue="所有仓库" style="color: #ADADAD;">
                <option style="color: #ADADAD;" value="">所有仓库</option>
                <c:forEach items="${storeHouseDTOList}" var="storeHouseDTO">
                  <option style="color: #000000;" value="${storeHouseDTO.id}">${storeHouseDTO.name}</option>
                </c:forEach>
              </select>
            </bcgogo:hasPermission>
            <a class="clean" id="searchInventoryBtn">查&nbsp;询</a><a class="blue_color clean2" id="clearConditionBtn">清空条件</a>
          </div>
        </div>
      </div>
      <div class="lineBottom"></div>
      <div class="clear i_height"></div>
    </div>
    <div class="clear"></div>

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
        </div>
        <form id="productDTOListForm" action="" method="post" style="display:block;overflow-y:visible;margin-top:10px">
          <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="table_productNo">
            <tr class="titleBg">
              <td style="padding-left:10px;">商品信息</td>
              <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.INVENTORY">
              </bcgogo:hasPermission>
              <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.AVERAGE_PRICE">
                <td style="width:6%;">均价</td>
              </bcgogo:hasPermission>
              <bcgogo:hasPermission permissions="WEB.TXN.INVENTORY_MANAGE.STOCK_SEARCH.NEW_STORAGE_PRICE">
                <td style="width:6%;">销售价</td>
              </bcgogo:hasPermission>


              <td style="width:8%;">销售提成类型</td>
              <td style="width:10%">
                <a class="btnTitle tichenge" id="batchSetSalesTotal">销售提成</a>
              </td>
              <td style="width:8%;">利润提成类型</td>
              <td style="width:10%">
                <a class="btnTitle tichenge" id="batchSetSalesProfit">利润提成</a>
              </td>
            </tr>
            <tr class="space">
              <td colspan="11"></td>
            </tr>
          </table>
        </form>
        <div class="height"></div>
        <bcgogo:ajaxPaging url="goodsindex.do?method=inventory" postFn="stockSearchForAchievement"
                           dynamical="_stock_search" display="none"/>
      </div>
      <div class="cartBottom"></div>
    </div>
    <div class="height"></div>
  </div>

</div>

<bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
  <div class="xuka">
    <h1>会员卡提成</h1>
    <dl style="margin-right:15px;">
      <dt>购卡</dt>
      <dd><label>提成方式</label>
        <select id="memberNewSelect" class="txt tic" style="width:100px;">

          <c:choose>
            <c:when test="${memberNewSelect=='按销售量'}">
              <option>按销售量</option>
              <option>按销售额</option>
            </c:when>
            <c:otherwise>
              <option>按销售额</option>
              <option>按销售量</option>
            </c:otherwise>
          </c:choose>

        </select>
      </dd>
      <dd><label>提成类型</label>
        <select disabled="disabled" id="memberNewAchievement" class="txt tic" style="width:80px;display: none;">

          <c:choose>
            <c:when test="${memberNewAchievement=='按金额'}">
              <option>按金额</option>
              <option>按比率</option>
            </c:when>
            <c:otherwise>
              <option>按比率</option>
              <option>按金额</option>
            </c:otherwise>
          </c:choose>
        </select></dd>

        <c:choose>
        <c:when test="${memberNewAchievement=='按金额'}">
        <dd><label>提成</label><input id="memberNewAmount" value="${memberNewAmount}" type="text" class="txt ca_ti"/>
          </c:when>
          <c:otherwise>
        <dd><label>提成</label><input id="memberNewAmount" value="${memberNewAmount}%" type="text" class="txt ca_ti"/>
          </c:otherwise>
          </c:choose>

    </dl>

    <dl>
      <dt>续卡</dt>
      <dd><label>提成方式</label><select id="memberRenewSelect" class="txt tic" style="width:100px;">

        <c:choose>
          <c:when test="${memberRenewSelect=='按销售量'}">
            <option>按销售量</option>
            <option>按销售额</option>
          </c:when>
          <c:otherwise>
            <option>按销售额</option>
            <option>按销售量</option>
          </c:otherwise>
        </c:choose>

      </select></dd>
      <dd><label>提成类型</label><select disabled="disabled" id="memberRenewAchievement" style="width:80px;display: none;" class="txt tic"
                                     style="width:80px;">
        <c:choose>
          <c:when test="${memberRenewAchievement=='按金额'}">
            <option>按金额</option>
            <option>按比率</option>
          </c:when>
          <c:otherwise>
            <option>按比率</option>
            <option>按金额</option>
          </c:otherwise>
        </c:choose>
      </select></dd>

      <c:choose>
      <c:when test="${memberRenewAchievement=='按金额'}">
      <dd><label>提成</label><input id="memberRenewAmount" value="${memberRenewAmount}" type="text" class="txt ca_ti"/>
        </c:when>
        <c:otherwise>
      <dd><label>提成</label><input id="memberRenewAmount" value="${memberRenewAmount}%" type="text" class="txt ca_ti"/>
        </c:otherwise>
        </c:choose>

      </dd>
    </dl>
  </div>
</bcgogo:hasPermission>


<div class="height"></div>
<div class="divTit di_an" id="button">
  <a class="button bigButton" href="#" onclick="achievementNotify();">进入业绩统计</a>
</div>


<div id="batchSetSalesProfit_dialog" style="display: none" class="alertMain addProducts">
  <div class="height"></div>
  <table cellpadding="0" cellspacing="0" class="tab_product">
    <col width="80px">
    <col width="80px">
    <tr>
      <td style="text-align: left">提成类型</td>
      <td style="text-align: left"><select id="salesProfitAchievementType" class="txt selec_jin" style="width:85px;">
        <option value="AMOUNT">按销售量</option>
        <option value="RATIO">按销售额</option>
      </select></td>
    </tr>
    <tr>
      <td style="text-align: left">提&nbsp;&nbsp;&nbsp;&nbsp;成</td>
      <td style="text-align: left"><input id="salesProfitAchievementAmount" class="txt" style="width:80px;" maxlength="20"/></td>
    </tr>
  </table>

  <div class="height"></div>
  <div class="button"><a class="btnSure" id="batchSetSalesProfitSubmit">确&nbsp;定</a><a class="btnSure" id="batchSetSalesProfitCancel">取&nbsp;消</a></div>
  <div class="height"></div>
</div>



<div id="batchSetSalesTotal_dialog" style="display: none" class="alertMain addProducts">
  <div class="height"></div>
  <table cellpadding="0" cellspacing="0" class="tab_product">
    <col width="80px">
    <col width="80px">
    <tr>
      <td style="text-align: left">提成类型</td>
      <td style="text-align: left"><select id="salesTotalAchievementType" class="txt selec_jin" style="width:85px;">
        <option value="AMOUNT">按销售量</option>
        <option value="RATIO">按销售额</option>
      </select></td>
    </tr>
    <tr>
      <td style="text-align: left">提&nbsp;&nbsp;&nbsp;&nbsp;成</td>
      <td style="text-align: left"><input id="salesTotalAchievementAmount" class="txt" style="width:80px;" maxlength="20"/></td>
    </tr>
  </table>

  <div class="height"></div>
  <div class="button"><a class="btnSure" id="batchSetSalesTotalSubmit">确&nbsp;定</a><a class="btnSure" id="batchSetSalesTotalCancel">取&nbsp;消</a></div>
  <div class="height"></div>
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


<script type="text/javascript">

  $(document).ready(function () {
    if ('${fuzzyMatchingFlag}' == 'true') {
      getProductWithUnknownField();
    } else {
      //明确搜索框的内容对应哪个field 6字段全部搜索
      getProductWithCertainField();
    }
  });

  function getNode(o) {
    return document.getElementById(o);
  }

  function fittinghover() {
    getNode('stock_fitting').className = 'stock_fittinghover';
    getNode('stock_articles').className = 'stock_articles';
    getNode('stock_fittingtitle').style.display = 'block';
    getNode('stock_articlestitle').style.display = 'none';
  }

  function fittingout() {
    getNode('stock_fitting').className = 'stock_fittinghover';
    getNode('stock_articles').className = 'stock_articles';
    getNode('stock_fittingtitle').style.display = 'block';
    getNode('stock_articlestitle').style.display = 'none';
  }

  function articleshover() {
    getNode('stock_fitting').className = 'stock_fitting';
    getNode('stock_articles').className = 'stock_articleshover';
    getNode('stock_fittingtitle').style.display = 'none';
    getNode('stock_articlestitle').style.display = 'block';
  }

  function articlesout() {
    getNode('stock_fitting').className = 'stock_fitting';
    getNode('stock_articles').className = 'stock_articleshover';
    getNode('stock_fittingtitle').style.display = 'none';
    getNode('stock_articlestitle').style.display = 'block';
  }

  function stockClick() {
    getNode('stock_content').style.display = 'block';
  }
</script>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>