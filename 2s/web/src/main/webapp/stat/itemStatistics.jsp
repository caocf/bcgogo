<%@ include file="/WEB-INF/views/includes.jsp" %>
<%--
  User: zyj
  Date: 12-5-7
  Time: 上午11:00
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>分项销售</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/highcharts.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/Highcharts-2.3.2/js/modules/exporting.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/stat/itemStatistics<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "ITEM_STAT_CATEGORY_STAT");

    <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION,WEB.VERSION.MEMBER_STORED_VALUE">
    APP_BCGOGO.Permission.Version.VehicleConstruction=${WEB_VERSION_VEHICLE_CONSTRUCTION};
    APP_BCGOGO.Permission.Version.MemberStoredValue=${WEB_VERSION_MEMBER_STORED_VALUE};
    </bcgogo:permissionParam>
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
<%--<jsp:include page="statNavi.jsp">
    <jsp:param name="currPage" value="businessStat"/>
</jsp:include>--%>
<jsp:include page="businessStatNavi.jsp">
    <jsp:param name="currPage" value="itemStat"/>
</jsp:include>
<div class="clear"></div>
<div class="num_show" style="margin:0px;">
<div class="count_info">
    <form id="itemStatisticsForm" name="itemStatisticsForm" action="itemStat.do?method=getItemStatData"
          method="post">
        <input type="hidden" name="startPageNo" id="startPageNo" value="1">
        <input type="hidden" name="maxRows" id="maxRows" value="15">
        <input type="hidden" name="currentPage" id="currentPage" value="1">
        <!--营销项目统计-->
        <!--商品库存统计-->
        <div class="divCount">
            <label class="lbl">统计分类：</label>
            <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.ITEMS.CUSTOMER">
            <label name="radioLabel" class="radioTextChecked">
                    <input type="radio" id="customerStatistics" name="statType" checked="checked" title="客户交易统计" value="customerStatistics"/>客户交易统计
            </label>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.ITEMS.SUPPLIER">
            <label name="radioLabel" class="radioText">
                    <input type="radio" id="supplierStatistics" name="statType" title="供应商交易统计" value="supplierStatistics"/>供应商交易统计
            </label>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.ITEMS.PRODUCT">
            <label name="radioLabel" class="radioText">
                    <input type="radio" id="productStatistics" name="statType" title="商品分类销售额统计" value="productStatistics"/>商品分类销售额统计
            </label>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_STAT.ITEMS.BUSINESS_SALE">
            <label name="radioLabel" class="radioText">
                    <input type="radio" id="businessStatistics" name="statType" title="营业分类销售额统计" value="businessStatistics"/>营业分类销售额统计
            </label>
            </bcgogo:hasPermission>
        </div>
        <div class="height"></div>
        <div class="divCount">
            <label class="lbl">统计日期段：</label>
            <label name="radioLabel" class="radioText">
                <input type="radio" id="date_this_week" value="this_week" name="date_select"/> 本周
            </label>
            <label name="radioLabel" class="radioText">
                <input type="radio" id="date_this_month" value="this_month" name="date_select"/> 本月
            </label>
            <label name="radioLabel" class="radioText">
                <input type="radio" id="date_this_year" value="this_year" name="date_select"/> 本年
            </label>

            <label>自定义日期段：</label>

            <input id="startDate" type="text" readonly="readonly" name="startTimeStr" class="textbox" />
            <div style="float:left; line-height:25px; padding-right:20px; padding-left:20px;">至</div>
            <input id="endDate" type="text" readonly="readonly" name="endTimeStr" class="textbox" />
        </div>
        <div class="height"></div>
        <div class="divCount" id="div_business" style="display:none">
            <label class="lbl">营业分类：</label>
            <label>
                <input type="text" id="businessCategory" name="businessCategory" disabled="disabled"
                       style="width:150px;" autocomplete="off" class="textbox" />
            </label>
        </div>
        <div class="divCount" id="div_product" style="display:none">
            <label class="lbl">商品分类：</label>
            <label>
                <input id="productCategory" name="productKind" disabled="disabled"
                       type="text" style="width:150px;" autocomplete="off" class="textbox" />
            </label>
        </div>
        <div class="height"></div>
        <div class="divCount" id="div_customer">
            <label class="lbl">客户：</label>
            <input id="customerName" name="customerName" type="text"
                   style="width:150px; float:left; " autocomplete="off" class="textbox">
            <input type="hidden" id="customerId" name="customerId"/>
            <label style="padding: 0; width: 50px;text-align: right;color:#999999" >手机：</label>
            <input id="mobile" type="text" readonly="readonly" disabled="disabled" style="width:150px;float:left; " autocomplete="off" class="textbox" />
            <%--<label class="chk"><input type="checkbox" />不显示已删除客户</label>--%>
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
            <label class="lbl">车牌号：</label>
            <input id="vehicle" name="vehicle" type="text"
                   style="width:150px;text-transform:uppercase;" autocomplete="off" class="textbox">
            </bcgogo:hasPermission>
        </div>
        <div class="divCount" id="div_supplier" style="display:none">
            <label class="lbl">供应商：</label>
            <input id="supplierName" name="supplierName" disabled="disabled" type="text" style="width:150px;" autocomplete="off" class="textbox">
            <input type="hidden" id="supplierId" name="supplierId" disabled="disabled"/>
        </div>
        <div class="height"></div>
        <div class="height"></div>
        <div class="divCount btnCount">
            <input type="submit" value="统&nbsp;计" onfocus="this.blur();" class="btn"/>
            <input type="button" id="resetSearchCondition" value="重&nbsp;置" onfocus="this.blur();" class="btn"/>
        </div>
    </form>
</div>
    <%--<div class="showRight" style="position:relative">
        <!--柱形图-->
        <span class="num_left"></span>

        <div class="num_center center">
            <div class="btnClick">
                <div class="rad_off" id="radDay">按日</div>
                <div class="r_on" id="radMonth">按月</div>
            </div>

            <div id="chart_div" style="margin-top: -45px;"></div>
            <div id="noData"
                 style="color:#F00;width:100%; height:50px; float:left; clear:both;display:none; position:relative; text-align:center;  padding:80px 0 0; display:none;">
                您查询的日期沒有数据
            </div>
        </div>
        <span class="num_right"></span>
    </div>--%>
    <!--统计信息-->
    <div class="clear"></div>
    <div class="height"></div>
    <div class="height"></div>
    <div class="contentTitle cont_title">
        <a id="table_title" class="hoverTitle">客户交易统计</a>
    </div>
    <div class="clear"></div>
    <div class="bus_stock add tb_add" id="customerStatisticsInfo">
        <table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="customerStatisticsTable" style=" table-layout: auto;">
            <col width="30"/>
            <col width="90"/>
            <col width="100"/>
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                <col width="100"/>
            </bcgogo:hasPermission>
            <col width="100"/>
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                <col width="80"/>
            </bcgogo:hasPermission>
            <col width="110"/>
            <col width="80"/>
            <col width="90"/>
            <col width="90"/>
            <col width="80"/>
            <col width="80"/>
            <col width="80"/>
            <col width="80"/>
            <col width="80"/>
            <tr class="table-row-title">
                <td class="first-padding">NO</td>
                <td>消费时间</td>
                <td>客户</td>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                    <td>会员名</td>
                </bcgogo:hasPermission>
                <td>手机</td>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                    <td>消费车牌</td>
                </bcgogo:hasPermission>
                <td>消费类型</td>
                <td>单据号</td>
                <td>消费总额</td>
                <td>成本</td>
                <td>实收</td>
                <td>优惠</td>
                <td>挂账</td>
                <td>毛利</td>
                <td class="last-padding">毛利率</td>
            </tr>
        </table>
        <div class="clear"></div>
        <div class="i_height"></div>
        <div style="color:#000000; font-weight:bold;">
            <div style="float:left;width:200px;">
                销售总价：<span id="customer_after_member_discount_total" class="j_clear_span">0</span>元
            </div>
            本页小计：销售总额<span id="customer_page_after_member_discount_total" class="j_clear_span">0</span>元
            成本<span id="customer_page_total_cost_price" class="j_clear_span">0</span>元
            实收<span id="customer_page_order_settled_amount" class="j_clear_span">0</span>元
            优惠<span id="customer_page_discount" class="j_clear_span">0</span>元
            挂账<span id="customer_page_order_debt_amount" class="j_clear_span">0</span>元
            毛利<span id="customer_page_gross_profit" class="j_clear_span">0</span>元
            <span style="color:#6699cc; float:right; text-decoration:underline;cursor: pointer;" id="print">打印</span>
        </div>
        <div class="clear"></div>
        <div class="height"></div>
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="itemStat.do?method=getItemStatData"></jsp:param>
            <jsp:param name="dynamical" value="dynamicalCustomer"></jsp:param>
            <jsp:param name="data" value="{startPageNo:'1',maxRows:15}"></jsp:param>
            <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
    </div>

    <div class="bus_stock add tb_add" id="supplierStatisticsInfo" style="display: none">
        <table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="supplierStatisticsTable" style=" table-layout: auto;">
            <col width="30"/>
            <col width="150"/>
            <col width="150"/>
            <col width="150"/>
            <col width="150"/>
            <col width="100"/>
            <col width="100"/>
            <col width="100"/>
            <col width="100"/>
            <col width="100"/>
            <col width="100"/>
            <tr class="tab_title">
                <td class="first-padding">NO</td>
                <td>交易时间</td>
                <td>供应商</td>
                <td>联系人</td>
                <td>联系方式</td>
                <td>单据编号</td>
                <td>单据类型</td>
                <td>交易总额</td>
                <td>实付金额</td>
                <td>挂账</td>
                <td class="last-padding">优惠</td>
            </tr>
        </table>
        <div class="clear"></div>
        <div class="i_height"></div>
        <div style="color:#000000; font-weight:bold;">
            <div style="float:left;width:450px;">
                合计：<span id="supplier_order_total_amount" class="j_clear_span">0</span>元
                入库总额<span id="supplier_order_total_amount_order_type_inventory" class="j_clear_span">0</span>元
                退货总额<span id="supplier_order_total_amount_order_type_return" class="j_clear_span">0</span>元
            </div>
            本页合计：<span id="supplier_page_order_total_amount" class="j_clear_span">0</span>元
            入库总额<span id="supplier_page_order_total_amount_order_type_inventory" class="j_clear_span">0</span>元
            退货总额<span id="supplier_page_order_total_amount_order_type_return" class="j_clear_span">0</span>元
            <span style="color:#6699cc; float:right; text-decoration:underline;cursor: pointer;" id="print1">打印</span>
        </div>
        <div class="clear"></div>
        <div class="height"></div>
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="itemStat.do?method=getItemStatData"></jsp:param>
            <jsp:param name="dynamical" value="dynamicalSupplier"></jsp:param>
            <jsp:param name="data" value="{startPageNo:'1',maxRows:15}"></jsp:param>
            <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
    </div>
    <div class="bus_stock add tb_add" id="productStatisticsInfo" style="display: none">
        <table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="productStatisticsTable" style=" table-layout: auto;">
            <col width="30"/>
            <col width="100"/>
            <col width="100"/>
            <col width="140"/>
            <col width="140"/>
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                <col width="100"/>
            </bcgogo:hasPermission>
            <col width="210"/>
            <col width="100"/>
            <col width="80"/>
            <col width="60"/>
            <col width="100"/>
            <tr class="tab_title">
                <td class="first-padding">NO</td>
                <td>日期</td>
                <td>单据号</td>
                <td>单据类型</td>
                <td>客户</td>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                    <td>车牌号</td>
                </bcgogo:hasPermission>
                <bcgogo:permission>
                    <bcgogo:if resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                        <td>材料</td>
                    </bcgogo:if>
                    <bcgogo:else>
                        <td>商品</td>
                    </bcgogo:else>
                </bcgogo:permission>
                <td>单价</td>
                <td>成本</td>
                <td>数量</td>
                <td class="last-padding">小计</td>
            </tr>
        </table>
        <div class="clear"></div>
        <div class="i_height"></div>
        <div style="color:#000000; font-weight:bold;">
            <div style="float:left;width:500px;">
                总计：
                销售总额：<span id="product_item_total" class="j_clear_span">0</span>元
                成本总额：<span id="product_item_total_cost_price" class="j_clear_span">0</span>元
                数量：<span id="product_item_count" class="j_clear_span">0</span>
            </div>
                本页小计：
                销售总额：<span id="product_page_item_total" class="j_clear_span">0</span>元
                成本总额：<span id="product_page_item_total_cost_price" class="j_clear_span">0</span>元
                数量：<span id="product_page_item_count" class="j_clear_span">0</span>
            <span style="color:#6699cc; float:right; text-decoration:underline;cursor: pointer;" id="print2">打印</span>
        </div>
        <div class="clear"></div>
        <div class="height"></div>
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="itemStat.do?method=getItemStatData"></jsp:param>
            <jsp:param name="dynamical" value="dynamicalProduct"></jsp:param>
            <jsp:param name="data" value="{startPageNo:'1',maxRows:15}"></jsp:param>
            <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
    </div>

    <div class="bus_stock add tb_add" id="businessStatisticsInfo" style="display: none">
        <table cellpadding="0" cellspacing="0" class="serviceTable tb_2" id="businessStatisticsTable" style=" table-layout: auto;">
            <col width="30"/>
            <col width="100"/>
            <col width="90"/>
            <col width="100"/>
            <col width="130"/>
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
            <col width="100"/>
            <col width="140"/>
            <col width="100"/>
            </bcgogo:hasPermission>
            <col width="160"/>
            <col width="80"/>
            <col width="80"/>
            <col width="60"/>
            <col width="80"/>
            <tr class="tab_title">
                <td class="first-padding">NO</td>
                <td>日期</td>
                <td>单据号</td>
                <td>单据类型</td>
                <td>客户</td>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                <td>车牌号</td>
                <td>施工内容</td>
                <td>工时费</td>
                </bcgogo:hasPermission>
                <bcgogo:permission>
                    <bcgogo:if resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                        <td>材料</td>
                    </bcgogo:if>
                    <bcgogo:else>
                        <td>商品</td>
                    </bcgogo:else>
                </bcgogo:permission>
                <td>单价</td>
                <td>成本</td>
                <td>数量</td>
                <td class="last-padding">小计</td>
            </tr>
        </table>
        <div class="clear"></div>
        <div class="i_height"></div>
        <div style="color:#000000; font-weight:bold;">
            <div style="float:left;width:500px;">
                总计：
                销售总额：<span id="business_item_total" class="j_clear_span">0</span>元
                成本总额：<span id="business_item_total_cost_price" class="j_clear_span">0</span>元
                数量：<span id="business_item_count" class="j_clear_span">0</span>

            </div>
            本页小计：
                销售总额：<span id="business_page_item_total" class="j_clear_span">0</span>元
                成本总额：<span id="business_page_item_total_cost_price" class="j_clear_span">0</span>元
                数量：<span id="business_page_item_count" class="j_clear_span">0</span>
            <span style="color:#6699cc; float:right; text-decoration:underline;cursor: pointer;" id="print3">打印</span>
        </div>
        <div class="clear"></div>
        <div class="height"></div>
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="itemStat.do?method=getItemStatData"></jsp:param>
            <jsp:param name="dynamical" value="dynamicalBusiness"></jsp:param>
            <jsp:param name="data" value="{startPageNo:'1',maxRows:15}"></jsp:param>
            <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
    </div>

    <div class="clear"></div>

</div>
<%@ include file="/common/messagePrompt.jsp" %>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>