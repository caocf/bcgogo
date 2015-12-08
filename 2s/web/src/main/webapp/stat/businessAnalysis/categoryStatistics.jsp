<%--
  Created by IntelliJ IDEA.
  User: liuWei
  Date: 13-1-30
  Time: 下午4:38
  To change this template use File | Settings | File Templates.
--%>

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
  <link rel="stylesheet" type="text/css" href="styles/businessAnalysis<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/messageManage<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/statistics<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
  <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/head<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/stat/itemStatistics<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/businessAnalysis/businessUtil<%=ConfigController.getBuildVersion()%>.js"></script>
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
  <jsp:include page="/stat/statNavi.jsp">
    <jsp:param name="currPage" value="businessAnalysis"/>
  </jsp:include>
  <jsp:include page="businessAnalysisNavi.jsp">
    <jsp:param name="currPage" value="categoryStat"/>
  </jsp:include>

  <div class="titBody">
    <div class="lineTitle">分类销售统计查询</div>
    <form id="itemStatisticsForm" name="itemStatisticsForm" action="itemStat.do?method=getItemStatData"
          method="post">
      <input type="hidden" name="startPageNo" id="startPageNo" value="1">
      <input type="hidden" name="maxRows" id="maxRows" value="25">
      <input type="hidden" name="currentPage" id="currentPage" value="1">
      <input type="hidden" id="statType" name="statType" value="productStatistics"/>


      <div class="lineBody bodys">
        <div class="divTit" style="width:100%;">
          统计日期段：
            <a class="btnList" id="my_date_today" name="my_date_select">本日</a>&nbsp;
            <a class="btnList" id="my_date_thisweek" name="my_date_select">本周</a>&nbsp;
            <a class="btnList" id="my_date_thismonth" name="my_date_select">本月</a>&nbsp;
            <a class="btnList" id="my_date_thisyear" name="my_date_select">本年</a>&nbsp;
            <a class="btnList" id="my_date_self_defining" name="my_date_select">自定义时间段</a>&nbsp;
          <input class="my_startdate" id="startDate" type="text" style="width:100px;" readonly="readonly" name="startTimeStr"/>&nbsp;至&nbsp;
          <input class="my_enddate" id="endDate" type="text" style="width:100px;" readonly="readonly" name="endTimeStr"/>

        </div>

        <div class="divTit titDate analyseCondition">
          分类：&nbsp;<select id="statByType" style="width:130px;">
          <option>按商品分类统计</option>
          <option>按营业分类统计</option>
          <option>按商品统计</option>
          <c:if test="${!wholesalerVersion}"><option>服务/施工内容</option></c:if>
        </select>&nbsp;
            <span style="display: none"><input type="text" id="businessCategory" name="businessCategory" value="---所有营业分类---" style="width:150px;" autocomplete="off" class="textbox"/></span>
            <span><input id="productCategory" name="productKind" value="---所有商品分类---" type="text" style="width:150px;" autocomplete="off" class="textbox" /></span>
            <span id="productAttr" style="display: none;">
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productName" name="productName"
                       searchField="product_name" initialValue="品名" cssStyle="width:80px;" autocomplete="off"/>
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productBrand" name="productBrand"
                       searchField="product_brand" initialValue="品牌/产地" cssStyle="width:75px;" autocomplete="off"/>
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productSpec" name="productSpec"
                       searchField="product_spec" initialValue="规格" cssStyle="width:80px;" autocomplete="off"/>
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productModel"  name="productModel"
                   searchField="product_model" initialValue="型号" cssStyle="width:80px;" autocomplete="off"/>
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleBrand" name="productVehicleBrand"
                       searchField="product_vehicle_brand" initialValue="车辆品牌" cssStyle="width:80px;" autocomplete="off"/>
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleModel" name="productVehicleModel"
                       searchField="product_vehicle_model" initialValue="车型" cssStyle="width:80px;" autocomplete="off"/>
                <input class="txt J-productSuggestion J-initialCss J_clear_input" id="commodityCode" name="commodityCode"
                       searchField="commodity_code" initialValue="商品编号" cssStyle="text-transform: uppercase;width:80px;" autocomplete="off"/>
            </span>
            <c:if test="${!wholesalerVersion}">
                <span style="display: none;"><input id="serviceAndConstruction" name="serviceAndConstruction" type="text" style="width:150px;" autocomplete="off" class="textbox" initialValue="服务/施工内容"></span>
            </c:if>
        </div>
        <div class="divTit">
          客户：&nbsp;<input type="text" id="customerName" name="customerName" style="width:150px;" class="txt"
                          value="客户名"/>&nbsp;<input
            type="text" id="mobile" class="txt" name="mobile" value="手机号"/>
        </div>
          <c:if test="${wholesalerVersion}" var="isWholesalerVersion">
              <div class="divTit">
                  所属区域&nbsp;<select class="txt" style="color: #ADADAD;width: 100px" id="provinceNo" name="provinceNo">
                  <option class="default" value="">--所有省--</option>
              </select>&nbsp;<select class="txt" style="color: #ADADAD;width: 100px" id="cityNo" name="cityNo">
                  <option class="default" value="">--所有市--</option>
              </select>&nbsp;<select class="txt" style="color: #ADADAD;width: 100px" id="regionNo" name="regionNo">
                  <option class="default" value="">--所有区--</option>
              </select>
              </div>
          </c:if>
          <div class="divTit"><input class="btn" id="statistics" type="submit" onfocus="this.blur();" value="统&nbsp;计">
          </div>
        <div class="divTit"><input type="button" id="resetSearchCondition" class="btn" value="重&nbsp;置"/></div>
      </div>
    </form>

    <div class="lineBottom"></div>
    <div class="clear height"></div>
    <div class="lineTitle tabTitle">
      累计交易记录：<span class="blue_color" id="recordNum">0</span>条&nbsp;&nbsp;
      累计交易总计：<span class="blue_color" id="orderTotal">0</span>元&nbsp;&nbsp;
        <span id="serviceNotItem">数量：<span class="blue_color" id="settledTotal">0</span>&nbsp;成本：<span class="blue_color" id="debtTotal">0</span>元&nbsp;&nbsp;</span>
    </div>

    <div class="tab" id="productStatDiv">
        <table cellpadding="0" cellspacing="0" class="tabSlip tabPick" id="productStatisticsTable">
            <col width="30"/>
            <col width="70"/>
            <col width="90"/>
            <col width="50"/>
            <col width="80">
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                <col width="60"/>
            </bcgogo:hasPermission>
            <col width="120"/>
            <col width="80"/>
            <col width="80"/>
            <col width="30"/>
            <col width="80"/>
            <tr class="titleBg">
          <td style="padding-left: 5px">NO</td>
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
      <jsp:include page="/common/pageAJAX.jsp">
        <jsp:param name="url" value="itemStat.do?method=getItemStatData"></jsp:param>
        <jsp:param name="dynamical" value="dynamicalProduct"></jsp:param>
        <jsp:param name="data" value="{startPageNo:'1',maxRows:25,statType:'productStatistics'}"></jsp:param>
        <jsp:param name="jsHandleJson" value="drawProductStatTable"></jsp:param>
      </jsp:include>
      <div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
          <bcgogo:hasPermission permissions="WEB.STAT.BUSINESS_ANALYSIS.CATEGORY_STAT_PRINT">
              <div class="btn_div_Img" id="print_divD">
                  <input id="printBtnD" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                  <input type="hidden" name="print" id="printHiddden">

                  <div class="optWords">打印</div>
              </div>
          </bcgogo:hasPermission>
      </div>
    </div>

    <div class="tab" style="display: none" id="serviceStatDiv">
        <table cellpadding="0" cellspacing="0" class="tabSlip tabPick" id="serviceStatisticsTable">
            <col width="50"/>
            <col width="100"/>
            <col width="100"/>
            <col width="100"/>
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                <col width="100"/>
                <col width="100"/>
                <col/>
                <col width="100"/>
                <col width="100"/>
            </bcgogo:hasPermission>
            <tr class="titleBg">
                <td class="first-padding">NO</td>
                <td>日期</td>
                <td>单据号</td>
                <td>单据类型</td>
                <td>客户</td>
                <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                    <td>车牌号</td>
                    <td>施工内容</td>
                    <td>工时单价</td>
                    <td>实际工时</td>
                    <td class="last-padding">金额总计</td>
                </bcgogo:hasPermission>
            </tr>
        </table>
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="itemStat.do?method=getItemStatData"></jsp:param>
            <jsp:param name="dynamical" value="dynamicalService"></jsp:param>
            <jsp:param name="data" value="{startPageNo:'1',maxRows:25}"></jsp:param>
            <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
        <div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
            <div class="btn_div_Img" id="print_divS">
                <input id="printBtnS" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                <input type="hidden" name="print" id="printHiddenS">

                <div class="optWords">打印</div>
            </div>
        </div>
    </div>

    <div class="tab" style="display: none" id="businessStatDiv">
        <table cellpadding="0" cellspacing="0" class="tabSlip tabPick" id="businessStatisticsTable">

            <col width="30"/>
            <col width="70"/>
            <col width="90"/>
            <col width="80">
            <col width="100"/>
            <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                <col width="60"/>
                <col width="80"/>
                <col width="60"/>
            </bcgogo:hasPermission>
            <col width="100"/>
            <col width="60"/>
            <col width="60"/>
            <col width="30"/>
            <col width="60"/>

            <tr class="titleBg">
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
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="itemStat.do?method=getItemStatData"></jsp:param>
            <jsp:param name="dynamical" value="dynamicalBusiness"></jsp:param>
            <jsp:param name="data" value="{startPageNo:'1',maxRows:25}"></jsp:param>
            <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
        <div class="shopping_btn" style="float:right; clear:right; margin-top:16px;">
            <div class="btn_div_Img" id="print_div">
                <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                <input type="hidden" name="print" id="printHidden">

                <div class="optWords">打印</div>
            </div>
        </div>
    </div>
  </div>


  <div class="clear"></div>

</div>
<%@ include file="/common/messagePrompt.jsp" %>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>