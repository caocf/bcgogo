<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 12-11-6
  Time: 上午7:21
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%--@elvariable id="inquiryCenterInitialDTO" type="com.bcgogo.search.dto.OrderSearchConditionDTO"--%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>查询中心</title>

    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/searchCenter<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up_storage<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css" />
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
	<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css" />

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>

    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-WebStorage<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/search/inquiryImportedOrder<%=ConfigController.getBuildVersion()%>.js"></script>
</head>
<body class="bodyMain" style="overflow:hidden;font-family: '宋体';">
<div class="i_history" >
<div class="i_arrow"></div>
<div class="i_upLeft"></div>
<div class="i_upCenter">
    <div class="i_note">查询中心</div>
    <div class="i_close" id="div_close" ></div>
</div>
<input name="pageType" id="inquiryCenterPageType" type="hidden" value="${inquiryCenterInitialDTO.pageType}"/>
<div class="i_upRight"></div>
<div class="i_upBody i_mainRight">
<div class="s_main clear"  id="inquiryCenterSearch" >
<form id="inquiryCenterSearchForm" commandName="inquiryCenterInitialDTO" name="inquiryCenterSearchForm" action="inquiryCenter.do?method=inquiryImportedOrder" method="post">
    <input type="hidden" name="rowStart" id="rowStart" value="0">
    <input type="hidden" name="pageRows" id="pageRows" value="15">
    <input type="hidden" name="totalRows" id="totalRows" value="0">
    <input type="hidden"  id="searchType"  value="importedOrder" class="searchType" name="searchType"/>
  <%--日期--%>
  <div class="order_search_tab">
    <a id="sysOrderBtn" href="inquiryCenter.do?method=inquiryCenterIndex&pageType=${inquiryCenterInitialDTO.pageType}" class="disStatus" style="color: #787777;">系统单据</a>
    <a id="importedBtn" class="hover_title" href="inquiryCenter.do?method=toInquiryImportedOrder&pageType=${inquiryCenterInitialDTO.pageType}">导入单据</a>
  </div>
  <div class="clear"></div>
    <div class="good_his">
        <strong>日期：</strong>

        <div class="i_searchTime"><input id="startDate" type="text" value="今天" readonly="readonly" name="startTimeStr" /></div>
        <label>至</label>

        <div class="i_searchTime"><input id="endDate" type="text" value="今天" readonly="readonly" name="endTimeStr" /></div>
        <div class="today_list his_first">
            <a class="his_now" id="date_yesterday">昨天</a>
        </div>
        <div class="today_list hoverList">
            <a class="his_now" id="date_today">今天</a>
        </div>
        <div class="today_list">
            <a class="his_now" id="date_last_month">上月</a>
        </div>
        <div class="today_list">
            <a class="his_now" id="date_this_month">本月</a>
        </div>
        <div class="today_list">
            <a class="his_now" id="date_this_year">今年</a>
        </div>
        <a class="moreCondition blue_col" id="moreConditionBtn">更多条件</a>
        <a class="moreCondition condition blue_col" id="resetSearchCondition">重置条件</a>

    </div>
    <%--单据类型--%>
    <div class="goods_chk clear">
        <strong>单据类型：</strong>
        <label class="label_check chk_off" id="orderTypeAllLabel"><input type="checkbox" id="orderTypeAll"/>所有</label>
            <%--<c:if test="<%=goodsBuy%>">--%>
                <%--<label class="label_check chk_off" id="purchaseLabel"><input type="checkbox" name="orderType" checked="checked" value="PURCHASE"/>采购单</label>--%>
            <%--</c:if>--%>
        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
            <label class="label_check chk_off" id="inventoryLabel"><input type="checkbox" name="orderType" checked="checked" value="INVENTORY"/>入库单</label>
        </bcgogo:hasPermission>
        <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
            <label class="label_check chk_off" id="saleLabel"><input type="checkbox" name="orderType" checked="checked" value="SALE"/>销售单</label>
        </bcgogo:hasPermission>
        <bcgogo:permission>
        <bcgogo:if permissions="WEB.VEHICLE_CONSTRUCTION.BASE&&WEB.VEHICLE_CONSTRUCTION.CONSTRUCT.BASE">
            <label class="label_check chk_off" id="repairLabel"><input type="checkbox" name="orderType" checked="checked" value="REPAIR"/>施工单</label>
        </bcgogo:if>
      </bcgogo:permission>
        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE">
            <label class="label_check chk_off" id="washLabel"><input type="checkbox" name="orderType" checked="checked" value="WASH_BEAUTY"/>洗车美容</label>
        </bcgogo:hasPermission>
    </div>
    <div class="clear"></div>
    <%--客户--%>
    <div class="first_tab" id="customerInfo">
        <input type="text" style="width:120px;" class="customerOrSupplierName" autocomplete="off" initialValue="供应商/客户" pagetype="inquiryCenter" value="${inquiryCenterInitialDTO.customerOrSupplier!=null?inquiryCenterInitialDTO.customerOrSupplier:'供应商/客户'}" name="customerOrSupplierName" id="customer_supplierInfoText">
        <input type="text" style="width:55px;" class="contact" initialValue="联系人" value="${inquiryCenterInitialDTO.contact!=null?inquiryCenterInitialDTO.contact:'联系人'}" name="contact" id="contact"> <%--todo order solr 中没有 --%>
        <input type="text" style="width:85px;" class="mobile" initialValue="手机" value="${inquiryCenterInitialDTO.mobile!=null?inquiryCenterInitialDTO.mobile:'手机'}" id="mobile" name="mobile"> <%--todo order solr 中没有 --%>
        <input type="hidden" name="vehicleId" id="vehicleId" value="${inquiryCenterInitialDTO.vehicleId!=null?inquiryCenterInitialDTO.vehicleId:''}">
        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
            <input type="text" class="vehicle" autocomplete="off" style="width:80px;" initialValue="车牌号" value="${inquiryCenterInitialDTO.vehicleNumber!=null?inquiryCenterInitialDTO.vehicleNumber:'车牌号'}" name="vehicle" id="vehicleNumber">
        </bcgogo:hasPermission>
        </bcgogo:hasPermission>
          <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
          <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
            <input type="text"  style="width:90px;" class="searchDetail member" value="会员类型" pagetype="inquiryCenter"  name="memberType" id="memberCardType" initialValue="会员类型" >
            <input type="text"  style="width:90px;" class="searchDetail member memberNo"  value="会员卡号" autocomplete="off"  pagetype="inquiryCenter" name="memberNo" id="customerMemberNo" initialValue="会员卡号" >
            <input type="text" style="width:100px;" class="searchDetail member" value="计次收费项目" id="payPerProject" name="payPerProject" initialValue="计次收费项目" >
          </bcgogo:hasPermission>
          </bcgogo:hasPermission>
    </div>
    <%--施工类型--%>
    <div class="shigong_tab" id="shigong">
        <%--<input type="text" class="repair" style="width:140px;" autocomplete="off" value="施工项目" id="invoicingItem" initialValue="施工项目" name="txtCondition">--%>
        <%--<input type="text"  style="width:100px;" class="repair searchDetail" id="invoicingDepartment" value="施工部门" initialValue="施工部门" >&lt;%&ndash;todo :solr 中还没有build&ndash;%&gt;--%>
        <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
            <input type="text"  style="width:55px;" autocomplete="off" class="repair searchDetail" id="serviceWorker" value="施工人" initialValue="施工人" name="serviceWorker">
            </bcgogo:hasPermission>
        </bcgogo:hasPermission>
        <input type="text" value="${inquiryCenterInitialDTO.salesman!=null?inquiryCenterInitialDTO.salesman:'销售人'}" autocomplete="off" initialValue="销售人" id="saler" class="sale searchDetail" style="width:55px;" name="salesman">
    </div>
    <%--商品--%>
    <div class="forth_tab" id="productProperty">
        <input id="product_name2_id" type="text" style="width:100px;" inputtype="inquiryCenter" name="productName" searchfield="product_info" tabindex="6" autocomplete="off" value="${inquiryCenterInitialDTO.productName!=null?inquiryCenterInitialDTO.productName:'品名'}" initialValue="品名"/>
        <input id="product_brand_id" type="text" style="width:90px;" inputtype="inquiryCenter" name="productBrand" tabindex="7" autocomplete="off" value="${inquiryCenterInitialDTO.productBrand!=null?inquiryCenterInitialDTO.productBrand:'品牌'}" initialValue="品牌"/>
        <input id="product_spec_id" type="text" style="width:90px;" inputtype="inquiryCenter" name="productSpec" tabindex="8" autocomplete="off" value="${inquiryCenterInitialDTO.productSpec!=null?inquiryCenterInitialDTO.productSpec:'规格'}" initialValue="规格"/>
        <input id="product_model_id" type="text" style="width:90px;" inputtype="inquiryCenter" name="productModel" tabindex="9" autocomplete="off" value="${inquiryCenterInitialDTO.productModel!=null?inquiryCenterInitialDTO.productModel:'型号'}" initialValue="型号"/>
        <input id="pv_brand_id" class="text" style="width:90px;" inputtype="inquiryCenter" type="text" name="pvBrand" tabindex="10" autocomplete="off" value="${inquiryCenterInitialDTO.productVehicleBrand!=null?inquiryCenterInitialDTO.productVehicleBrand:'车辆品牌'}" initialValue="车辆品牌"/>
        <input id="pv_model_id" class="text" style="width:90px;" inputtype="inquiryCenter" type="text" name="pvModel" tabindex="11" autocomplete="off" value="${inquiryCenterInitialDTO.productVehicleModel!=null?inquiryCenterInitialDTO.productVehicleModel:'车型'}" initialValue="车型"/>
        <input id="product_commodity_code" type="text" style="width:90px;text-transform: uppercase;" autocomplete="off" inputtype="inquiryCenter" name="commodityCode" tabindex="5"  initialValue="商品编号" value="${inquiryCenterInitialDTO.commodityCode!=null?inquiryCenterInitialDTO.commodityCode:'商品编号'}"/>
        <%--todo 营业归类  商品属性--%>
        <%--<input type="text" class="searchDetail" value="营业归类" initialValue="营业归类" style="width:80px;display:none;"  id="businessClassified">
        <input type="text" class="searchDetail" value="商品属性" initialValue="商品属性" style="width:90px;display:none;"  id="productAttributes">--%>
    </div>
    <%--结算方式--%>
    <%--<div class="tab_dow searchDetail"  id="settlementMethod">--%>
        <%--<strong>结算方式：</strong>--%>

        <%--<div class="chk_choose">--%>
            <%--<label class="label_check chk_off"><input type="checkbox" value="CASH"  id="cash" name="payMethod"/>现金</label>--%>
            <%--<label class="label_check chk_off"><input type="checkbox" value="BANK_CARD"  id="bankCard" name="payMethod"/>银行卡</label>--%>
            <%--<label class="label_check chk_off"><input type="checkbox" value="CHEQUE" id="cheque" name="payMethod"/>支票</label>--%>
            <%--<label class="label_check chk_off"><input type="checkbox" value="DEPOSIT" id="deposit" name="payMethod"/>定金</label>--%>
            <%--<label class="label_check chk_off"><input type="checkbox" value="MEMBER_BALANCE_PAY" id="memberBalancePay" name="payMethod"/>会员储值</label>--%>
            <%--<label class="label_check chk_off"><input type="checkbox" value="true" id="notPaid" name="notPaid"/>欠款</label>--%>
        <%--</div>--%>
        <%--<div class="td_show">--%>
            <%--<strong>消费金额：</strong>--%>
            <%--<input type="text" class="mon_search" name="amountLower" id="amountLower"/>~<input type="text" class="mon_search" name="amountUpper" id="amountUpper"/> 元--%>
            <%--<input type="text" value="操作人" initialValue="操作人" name="operator" id="operator" style="width:55px; margin-left:20px;color: #666666;">--%>
        <%--</div>--%>
    <%--</div>--%>
    <div class="shigong_tab btnSearch">
        <input type="submit" value="搜索" class="btn_condition" onfocus="this.blur();" id="btnSearch"/>
    </div>
</form>
<div class="height"></div>
<div class="tabTitle statisticalData">
    <%--<c:if test="<%=goodsBuy%>">--%>
        <%--<div class="statistics">采购(<span class="qian_blue" id="counts_purchase">0</span>笔<span class="qian_green" id="amounts_purchase">0.0</span>元)</div>--%>
    <%--</c:if>--%>
    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE" resourceType="menu">
        <div class="statistics">入库(<span class="qian_blue" id="counts_inventory">0</span>笔)</div>
       <%--<div class="statistics">入库(<span class="qian_blue" id="counts_inventory">0</span>笔<span class="qian_green" id="amounts_inventory">0.0</span>元)</div>--%>
    </bcgogo:hasPermission>
    <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
        <div class="statistics">销售(<span class="qian_blue" id="counts_sale">0</span>笔)</div>
      <%--<div class="statistics">销售(<span class="qian_blue" id="counts_sale">0</span>笔<span class="qian_red" id="amounts_sale">0.0</span>元)</div>--%>
    </bcgogo:hasPermission>
<bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE" resourceType="menu">
    <div class="statistics">施工(<span class="qian_blue" id="counts_repair">0</span>笔)</div>
    <%--<div class="statistics">施工(<span class="qian_blue" id="counts_repair">0</span>笔<span class="qian_red" id="amounts_repair">0.0</span>元)</div>--%>
</bcgogo:hasPermission>
<bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.BASE" resourceType="menu">
    <div class="statistics" style="width:138px;">洗车美容(<span class="qian_blue" id="counts_wash_beauty">0</span>笔)</div>
   <%--<div class="statistics" style="width:138px;">洗车美容(<span class="qian_blue" id="counts_wash_beauty">0</span>笔<span class="qian_red statistics" id="amounts_wash_beauty">0.0</span>元)</div>--%>
</bcgogo:hasPermission>
<br>
<div id="itemsArea">
    <div class="cus_titleBody" style="width: 1000px;">
        <div style="float:left;width:34px;">No</div>
         <div style="float:left;width:70px;">单据号</div>
        <div style="float:left;width:87px;">客户/供应商</div>
        <div style="float:left;width:107px;">日期</div>
        <div style="float:left;width:87px;">车牌号</div>
        <div style="float:left;width:83px;">单据类型</div>
        <div style="float:left;width:349px;">施工内容/材料</div>
        <div style="float:left;width:83px;">金额</div>
        <div style="float:left;width:82px;">状态</div>
    </div>
    <div id="tableDiv" style="overflow-x:hidden;overflow-y:auto; height:100px;width: 1000px;">
        <table cellpadding="0" cellspacing="0" class="table2" id="tabList" style="table-layout:fixed;">
            <col width="30">
            <col width="75">
            <col width="80">
            <col width="100">
            <col width="80">
            <col width="80"/>
            <col width="340">
            <col width="80">
            <col width="70">
        </table>
    </div>
        <%--采购--%>
        <div class="i_history up_storage orderItems" id="purchase" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">采购单</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:160px;" id="purchaseId">采购单号：<span></span></div>
                    <div style="width:200px;" id="purchaseSupplierName">供应商：<span></span></div>
                    <div style="width:110px;" id="purchaseSupplierContact">联系人：<span></span></div>
                    <div style="width:110px;" id="purchaseStatus">状态：<span></span></div>
                    <div style="width:140px;" id="purchaseDate">采购日期：<span></span></div>
                    <input type="hidden" id="purchaseSupplierId">
                </div>
                <table cellpadding="0" cellspacing="0" class="table2 inquiryItems" id="purchaseHistory">
                    <col width="60">
                    <col width="90"/>
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <tr class="title_his">
                        <td style="border-left:none;">商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                        <td>车辆品牌</td>
                        <td>车型</td>
                        <td class="txt_right">单价</td>
                        <td class="txt_right">数量</td>
                        <td>单位</td>
                        <td style="border-right:none;" class="txt_right">小计</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <ul class="tableInfo" style="list-style: none;">
                    <li style="line-height: 20px;" id="purchaseOrderTotalAmount">合计：<span>0.0</span>元</li>
                    <li style="line-height: 20px;" id="purchaseOrderMemo">备注：<span>无</span></li>
                    <li class="table_btn orderHandle">
                        <input name="purchaseOrderId" type="hidden" id="purchaseOrderId" class="orderHandleId"/>
                        <%--<input type="button" value="查看" class="i_operate" url="RFbuy.do?method=show&id=" onfocus="this.blur();"/>--%>
                        <%--<input type="button" value="作废" class="i_operate" handleType="purchase" url="RFbuy.do?method=purchaseOrderRepeal&id=" onfocus="this.blur();"/>--%>
                        <%--<input type="button" value="复制" class="i_operate"  handleType="purchase"    url="RFbuy.do?method=copyPurchaseOrder&id=" onfocus="this.blur();"/>--%>
                        <%--<input type="button" value="打印" class="i_operate" url="RFbuy.do?method=print&id=" onfocus="this.blur();"/>--%>
                    </li>
                </ul>
            </div>
        </div>
        <%--退货--%>
        <div class="i_history up_storage orderItems" id="return" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">入库退货单</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:160px;" id="returnId">入库退货单号：<span></span></div>
                    <div style="width:200px;" id="returnSupplierName">供应商：<span></span></div>
                    <div style="width:110px;" id="returnSupplierContact">联系人：<span></span></div>
                    <%--<div style="width:110px;" id="returnStatus">状态：<span></span></div>--%>
                    <div style="width:140px;margin-left: 100px" id="returnDate">入库日期：<span></span></div>
                    <input type="hidden" id="returnSupplierId">
                </div>
                <table cellpadding="0" cellspacing="0" class="table2 inquiryItems" id="returnHistory">
                    <col width="60">
                    <col width="90"/>
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <tr class="title_his">
                        <td style="border-left:none;">商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                         <td>车辆品牌</td>
                        <td>车型</td>
                        <td class="txt_right">单价</td>
                        <td class="txt_right">数量</td>
                        <td>单位</td>
                        <td style="border-right:none;" class="txt_right">小计</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <ul class="tableInfo" style="list-style: none;">
                    <li id="returnOrderTotalAmount">合计：<span>0.0</span>元</li>
                    <li id="returnOrderMemo">备注：<span>无</span></li>
                    <li class="table_btn orderHandle">
                        <input name="purchaseReturnId" type="hidden" id="purchaseReturnId" class="orderHandleId"/>
                        <input type="button" value="查看" class="i_operate" url="goodsReturn.do?method=showReturnStorageByPurchaseReturnId&purchaseReturnId=" onfocus="this.blur();"/>
                        <input type="button" value="打印" class="i_operate" url="goodsReturn.do?method=printReturnStorageOrder&purchaseReturnId=" onfocus="this.blur();"/>
                    </li>
                </ul>
            </div>
        </div>
        <%--销售--%>
        <div class="i_history up_storage orderItems" id="sale" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">销售单</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:160px;" id="saleId">销售单号：<span></span></div>
                    <div style="width:200px;" id="saleCustomerName">客户：<span></span></div>
                    <div style="width:110px;" id="saleCustomerContact">联系人：<span></span></div>
                    <div style="width:110px;" id="saleStatus">状态：<span></span></div>
                    <div style="width:140px;" id="saleDate">销售单日期：<span></span></div>
                    <input type="hidden" id="saleCustomerId">
                </div>
                <table cellpadding="0" cellspacing="0" class="table2 inquiryItems" id="saleHistory">
                    <col width="60">
                    <col width="90"/>
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <tr class="title_his">
                        <td style="border-left:none;">商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                        <td>车辆品牌</td>
                        <td>车型</td>
                        <td class="txt_right">单价</td>
                        <td class="txt_right">数量</td>
                        <td>单位</td>
                        <td style="border-right:none;" class="txt_right">小计</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <ul class="tableInfo" style="list-style: none;">
                    <li id="saleOrderMemo">备注：<span>无</span></li>
                    <li>
                        <div class="almost" id="saleOrderTotalAmount">合计：<span>0.0</span>元</div>
                        <div class="almost" id="saleOrderSettledAmount" style="margin-left: 30px">实收：<span>0.0</span>元</div>
                        <div class="almost" id="saleOrderDebt" style="margin-left: 30px">欠款：<span>0.0</span>元</div>
                    </li>
                    <li class="table_btn orderHandle">
                        <input type="hidden" id="saleOrderDebtId">
                        <input type="hidden" id="saleOrderReceivableId">
                        <input name="saleOrderId" type="hidden" id="saleOrderId" class="orderHandleId"/>
                        <%--<input type="button" value="查看" class="i_operate" url="sale.do?method=getSalesOrder&salesOrderId=" onfocus="this.blur();"/>--%>
                        <%--<input type="button" value="作废" class="i_operate" handleType="sale"  url="sale.do?method=saleOrderRepeal&id=" onfocus="this.blur();"/>--%>
                        <%--<input type="button" value="复制" class="i_operate" url="sale.do?method=copyGoodSale&salesOrderId=" onfocus="this.blur();"/>--%>
                        <%--<input type="button" value="打印" class="i_operate" url="sale.do?method=getSalesOrderToPrint&salesOrderId=" onfocus="this.blur();"/>--%>
                    </li>
                </ul>
            </div>
        </div>
        <%--入库--%>
        <div class="i_history up_storage orderItems" id="inventory" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">入库单</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:160px;" id="inventoryId">入库单号：<span></span></div>
                    <div style="width:200px;" id="inventorySupplierName">供应商：<span></span></div>
                    <div style="width:110px;" id="inventorySupplierContact">联系人：<span></span></div>
                    <div style="width:110px;" id="inventoryStatus">状态：<span></span></div>
                    <div style="width:140px;" id="inventoryDate">入库日期：<span></span></div>
                    <input type="hidden" id="inventorySupplierId">
                </div>
                <table cellpadding="0" cellspacing="0" class="table2 inquiryItems" id="inventoryHistory">
                    <col width="60">
                    <col width="90"/>
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <col width="40">
                    <tr class="title_his">
                        <td style="border-left:none;">商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                        <td>车辆品牌</td>
                        <td>车型</td>
                        <td class="txt_right">单价</td>
                        <td class="txt_right">数量</td>
                        <td>单位</td>
                        <td style="border-right:none;" class="txt_right">小计</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <ul class="tableInfo" style="list-style: none;">
                    <li id="inventoryOrderMemo">备注：<span>无</span></li>
                    <li class="almost">
                        <div class="almost" style="padding-right:20px;" id="inventoryOrderTotalAmount">合计：<span>0.0</span>元</div>
                        <div class="almost" style="padding-right:20px;" id="inventoryOrderSettledAmount">实收：<span>0.0</span>元</div>
                        <div class="almost" style="padding-right:20px;" id="inventoryOrderDebt">欠款：<span>0.0</span>元</div>
                    </li>
                    <li class="table_btn orderHandle">
                        <input name="purchaseInventoryId" type="hidden" id="purchaseInventoryId" class="orderHandleId"/>
                        <%--<input type="button" value="查看" class="i_operate" url="storage.do?method=getPurchaseInventory&purchaseInventoryId=" onfocus="this.blur();"/>--%>
                        <%--<input type="button" value="作废" class="i_operate" handleType="inventory" url="storage.do?method=getPurchaseInventory&repealOperation=true&purchaseInventoryId=" onfocus="this.blur();"/>--%>
                        <%--<input type="button" value="复制" class="i_operate" url="storage.do?method=copyPurchaseInventory&purchaseInventoryId=" onfocus="this.blur();"/>--%>
                        <%--<input type="button" value="打印" class="i_operate" url="storage.do?method=getPurchaseInventoryToPrint&purchaseInventoryId=" onfocus="this.blur();"/>--%>
                    </li>
                </ul>
            </div>
        </div>
        <%--会员--%>
        <div class="i_history up_storage orderItems" id="member_buy_card" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">会员购卡续卡</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:130px;" id="memberName">客户名：<span></span></div>
                    <div style="width:200px;" id="memberNo">会员卡号：<span></span></div>
                    <div style="width:230px;" id="memberStatusStr">购卡/续卡类型：<span></span></div>
                    <div style="width:120px;" id="memberOrderNum"><%--单据号：--%><span></span></div>
                    <div style="width:200px;" id="memberOrderOriginal">储值原有金额：<span>0.0</span>元</div>
                    <div style="width:200px;" id="memberOrderWorth">储值新增金额：<span>0.0</span>元</div>
                    <div style="width:180px;" id="memberBalance">储值余额：<span>0.0</span>元</div>
                </div>
                <table cellpadding="0" cellspacing="0" class="table2" id="memberHistory">
                    <col/>
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="120">
                    <col width="100">
                    <tr class="title_his">
                        <td style="border-left:none;">服务项目</td>
                        <td>原有次数</td>
                        <td>新增</td>
                        <td>剩余次数</td>
                        <td>限消费车牌</td>
                        <td style="border-right:none;">失效日期</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <ul class="tableInfo" style="list-style: none;">
                    <li>
                        <div style="padding-right:20px; float:left;" id="memberTotal">合计：<span>0.0</span>元</div>
                        <div style="padding-right:20px; float:left;" id="memberSettledAmount">实收：<span>0.0</span>元</div>
                        欠款：<span id="memberDebt">0.0</span>元
                    </li>
                     <li style="line-height: 20px;float: left" id="memberMemo">备注：<span>无</span></li>
                </ul>
            </div>
        </div>
        <%--会员--%>
        <div class="i_history up_storage orderItems" id="member_return_card" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">会员退卡</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:180px;" id="returnMemberName">客户名：<span></span></div>
                    <div style="width:180px;" id="returnMemberNo">会员卡号：<span></span></div>
                    <div style="width:180px;" id="returnMemberType">退卡类型：<span></span></div>
                    <div style="width:150px;" id="returnMemberOrderNum">单据号：<span></span></div>
                    <div style="width:180px;" id="returnMemberLastBuyTotal">上次购卡金额：<span>0.0</span>元</div>
                    <div style="width:180px;" id="returnMemberLastBuyDate">上次购卡日期：<span></span></div>
                    <div style="width:180px;" id="returnMemberLastRecharge">上次储值金额：<span>0.0</span>元</div>
                    <div style="width:180px;" id="returnMemberBalance">储值余额：<span>0.0</span>元</div>
                </div>
                <table cellpadding="0" cellspacing="0" class="table2" id="returnMemberHistory">
                    <col/>
                    <col width="90">
                    <col width="120">
                    <col width="100">
                    <tr class="title_his">
                        <td style="border-left:none;">服务项目</td>
                        <td>上次购买次数</td>
                        <td>剩余次数</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <div class="tableInfo">
                    <div class="almost">
                        <div style="padding-right:20px; float:left;" id="returnMemberTotal">合计：<span>0.0</span>元</div>
                        <div style="padding-right:20px; float:left;" id="returnMemberSettledAmount">实付：<span>0.0</span>元</div>
                    </div>
                </div>
            </div>
        </div>
        <%--洗车美容--%>
        <div class="i_history up_storage orderItems" id="wash_beauty" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note">洗车美容单</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:110px;" id="washCustomerName">客户：<span></span></div>
                    <div style="width:190px;" id="washMemberCard">会员（卡）号：<span></span></div>
                    <div style="width:140px;" id="washLicence">车牌号：<span></span></div>
                    <div style="width:180px;" id="washDate">消费时间：<span></span></div>
                </div>
                <table cellpadding="0" cellspacing="0" class="table2" id="washHistory">
                    <col width="60">
                    <col/>
                    <col width="120">
                    <col width="200">
                    <tr class="title_his">
                        <td style="border-left:none;">序号</td>
                        <td>施工内容</td>
                        <td>施工人</td>
                        <td style="border-right:none;">金额</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <ul class="tableInfo" style="list-style: none;">
                    <li id="washMemo">备注：<span>无</span></li>
                    <li>
                        <div style="padding-right:20px; float:left;" id="washTotal">合计：<span>0.0</span>元</div>
                        <div style="padding-right:20px; float:left;" id="washSettledAmount">实收：<span>0.0</span>元</div>
                        欠款：<span id="washDebt">0.0</span>元
                    </li>
                    <li class="table_btn orderHandle">
                        <%--<input name="washOrderId" type="hidden" id="washOrderId" class="orderHandleId"/>--%>
												<%--<input type="button" value="查看" class="i_operate" handleType="wash"--%>
															 <%--url="washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" onfocus="this.blur();"/>--%>
												<%--<input type="button" value="作废" class="i_operate" handleType="wash"--%>
															 <%--url="washBeauty.do?method=washBeautyOrderRepeal&washBeautyOrderId="--%>
															 <%--onfocus="this.blur();"/>--%>
												<%--<input type="button" value="复制" class="i_operate" handleType="wash"--%>
															 <%--url="washBeauty.do?method=washBeautyOrderCopy&washBeautyOrderId=" onfocus="this.blur();"/>--%>
												<%--<input type="button" value="打印" class="i_operate" handleType="wash"--%>
															 <%--url="washBeauty.do?method=printWashBeautyTicket&orderId=" onfocus="this.blur();"/>--%>
                    </li>
                </ul>
            </div>
        </div>
        <%--施工单--%>
        <div class="i_history up_storage orderItems" id="repair" style="display:none;left:90px;width: 770px; position:relative; z-index:10;">
            <div class="i_arrow"></div>
            <div class="i_upLeft"></div>
            <div class="i_upCenter">
                <div class="i_note" id="div_drag">施工单</div>
            </div>
            <div class="i_upRight"></div>
            <div class="i_upBody">
                <div class="more_his">
                    <div style="width:110px;" id="repairCustomerName">客户：<span></span></div>
                    <div style="width:190px;" id="repairMemberNo">会员（卡）号：<span></span></div>
                    <div style="width:140px;" id="repairLicenceNo">车牌号：<span></span></div>
                    <div style="width:120px;" id="repairOrderStatus">状态：<span></span></div>
                    <div style="width:180px;" id="startDateStr">进厂时间：<span></span></div>
                </div>
                <div class="more_his">
                    <div class="inquiryTitle">施工单</div>
                    <div class="inquiryTitle" id="repairServiceWorker">施工人：<span></span></div>
                </div>
                <table cellpadding="0" cellspacing="0" class="table2" id="repairServiceHistory">
                    <col width="60">
                    <col/>
                    <col width="120">
                    <col width="200">
                    <tr class="title_his">
                        <td style="border-left:none;">序号</td>
                        <td>施工内容</td>
                        <td>工时</td>
                        <td style="border-right:none;">备注</td>
                    </tr>
                </table>
                <div class="clear height"></div>
                <div class="more_his">
                    <div class="inquiryTitle">材料单</div>
                    <div class="inquiryTitle" id="repairProductSalers">销售人：<span></span></div>
                </div>
                <table cellpadding="0" cellspacing="0" class="table2" id="repairMaterialHistory">
                    <col width="60">
                    <col/>
                    <col width="90">
                    <col width="90">
                    <col width="90">
                    <col width="70">
                    <col width="60">
                    <col width="40">
                    <col width="70">
                    <tr class="title_his">
                        <td style="border-left:none;">商品编号</td>
                        <td>品名</td>
                        <td>品牌/产地</td>
                        <td>规格</td>
                        <td>型号</td>
                        <td class="txt_right">单价</td>
                        <td class="txt_right">数量</td>
                        <td>单位</td>
                        <td style="border-right:none;" class="txt_right">小计</td>
                    </tr>
                </table>
                <div class="height clear"></div>
                <ul class="tableInfo" style="list-style: none;">
                    <li style="line-height: 20px;">预计出厂时间：<span id="endDateStr"></span></li>
                    <li style="line-height: 20px;" id="repairMemo">备注：<span>无</span></li>
                    <li  style="line-height: 20px;">
                        <div style="padding-right:20px; float:left;" id="repairTotal">合计：<span>0.0</span>元</div>
                        <div style="padding-right:20px; float:left;" id="repairSettledAmount">实收：<span>0.0</span>元</div>
                        欠款：<span id="repairDebt">0.0</span>元
                    </li>
                    <li class="table_btn orderHandle">
                        <input name="repairOrderId" type="hidden" id="repairOrderId" class="orderHandleId"/>
                        <%--<input type="button" value="查看" class="i_operate" url="txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" onfocus="this.blur();"/>--%>
                        <%--<input type="button" value="作废" class="i_operate" handleType="repair" url="txn.do?method=repairOrderRepeal&repairOrderId=" onfocus="this.blur();"/>--%>
                        <%--<input type="button" value="打印" handleType="repair" class="i_operate"  url="txn.do?method=getRepairOrderToPrint&repairOrderId=" onfocus="this.blur();"/>--%>
                    </li>
                </ul>
                    </div>
            <div class="i_upBottom">
                <div class="i_upBottomLeft"></div>
                <div class="i_upBottomCenter"></div>
                <div class="i_upBottomRight"></div>
            </div>
        </div>
    </div>
    <div class="height"></div>
    <jsp:include page="/common/pageAJAXForSolr.jsp">
        <jsp:param name="dynamical" value="inquiryCenter"></jsp:param>
        <jsp:param name="formId" value="#inquiryCenterSearchForm"></jsp:param>
    </jsp:include>
    <div class="height"></div>
    <div class="operaBtn">
        <%--<input type="button" value="打印" class="btnPrint" onfocus="this.blur()"/>--%>
        <input type="button" value="关闭" class="btnPrint" id="closeInquiry" onfocus="this.blur()"/>
    </div>
</div>
</div>
</div>
<div class="i_upBottom clear">
    <div class="i_upBottomLeft "></div>
    <div class="i_upBottomCenter"></div>
    <div class="i_upBottomRight "></div>
</div>
</div>

<!--mask-->
<div id="mask" style="display:block;position: absolute;">
</div>
<!--下拉菜单-->
<div id="div_brand_head" class="i_scroll" style="display:none;height:230px;width:154px;">
    <div class="Scroller-Container" id="Scroller-Container_id_head" style="width:150px;">
    </div>
</div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px;width:154px;">
    <div class="Scroller-Container" id="Scroller-Container_id" style="width:150px;">
    </div>
</div>

<div id="div_brandvehicleheader" class="i_scroll" style="display:none;width:132px;">      <%--todo 需要整合--%>
    <div class="Container" style="width:132px;">
        <div id="Scroller-1header" style="width:132px;">
            <div class="Scroller-Containerheader" id="Scroller-Container_idheader">
            </div>
        </div>
    </div>
</div>
<div id="memberCardTypesPanel" style="display:none;color: #000000;background: none repeat scroll 0 0 #FFFFFF;line-height: 22px;">
    <%--@elvariable id="memberCardTypes" type="java.util.List<String>"--%>
    <c:forEach var="cardType" items='${memberCardTypes}'>
        <div style="overflow:hidden;padding:0 5px 0 5px;" title="${cardType}">${cardType}</div>
    </c:forEach>
</div>
</body>
</html>