<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>选择商品</title>
  <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/moreUserinfo<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/selectProduct<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/stockSearch<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/page/customer/selectProduct<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/txn/stockSearch<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
    $(function() {
        $("#searchBtn").click();
    });


</script>
</head>

<body>
<div class="stockSearch">
<div id="div_brand_head" class="i_scroll" style="display:none;">
  <div class="Scroller-Container" id="Scroller-Container_id_head" style="width:100%;padding:0;margin:0;">
  </div>
</div>
<input type="hidden" name="rowStart" id="rowStart" value="0">
<input type="hidden" name="pageRows" id="pageRows" value="12">
<input type="hidden" name="totalRows" id="totalRows" value="0">
<input type="hidden" name="pageType" id="pageType" value="stocksearch">
<div class="i_supplierInfo more_supplier select">
  <div class="i_arrow"></div>
  <div class="i_upLeft"></div>
  <div class="i_upCenter i_two">
    <div class="i_note more_title">选择商品</div>
    <div class="i_close" id="div_close"></div>
  </div>
  <div class="i_upRight"></div>
  <div class="i_upBody">
    <div class="new_sale add_info clear" style="padding-bottom:5px;">

    <input id="product_name2_id" class="stock_text" type="text" inputtype="stocksearch" searchfield="product_info" tabindex="6" autocomplete="off" style="width:188px;"
               initialValue="品名/品牌/规格/型号/适用车辆" value="品名/品牌/规格/型号/适用车辆"/>

    <input id="product_brand_id" class="stock_text" type="text" tabindex="7" autocomplete="off" initialValue="品牌" value="品牌" inputtype="stocksearch" style="width:60px;" />

    <input id="product_spec_id" class="stock_text" type="text" tabindex="8" autocomplete="off" initialValue="规格" value="规格" inputtype="stocksearch" style="width:65px;" />

    <input id="product_model_id" class="stock_text" type="text" tabindex="9" autocomplete="off" initialValue="型号" value="型号" inputtype="stocksearch" style="width:65px;" />

    <input id="pv_brand_id" class="stock_text" type="text" tabindex="2" autocomplete="off" initialValue="车辆品牌" value="车辆品牌" inputtype="stocksearch" style="width:80px;" />

    <input id="pv_model_id" class="stock_text" type="text" tabindex="3" autocomplete="off" initialValue="车型" value="车型" inputtype="stocksearch" style="width:60px;" />

    <input id="product_commodity_code" class="stock_text" type="text" tabindex="7" style="text-transform:uppercase;width:60px;" autocomplete="off" initialValue="商品编号" value="商品编号" inputtype="stocksearch" />

    <div class="stock_txtName">
    <input type="button" class="stock_search" id="searchBtn" value="" onfocus="this.blur();"/>
    </div>

    <table cellpadding="0" cellspacing="0" class="table2 tabProduct" id="table_productNo">
    <col width="78"/>
    <col width="120"/>
    <col width="120"/>
    <col width="120"/>
    <col width="120"/>
    <col width="120"/>


         <tr class="table_title">
           <th style="width:2%;">
             <label id="checkAlls" class="checkbox" onfocus="this.blur();" name="checkAll"></label>
             <input type="checkbox"/>
           </th>
            <th style="width:16%;">商品编号</th>
            <th style="width:16%;">品名</th>
            <th style="width:12%;">品牌/产地</th>
            <th style="width:10%;">规格/型号</th>
            <th style="width:10%;">车辆品牌/车型</th>
      </tr>

    </table>

  <jsp:include page="/common/pageAJAXForSolr.jsp">
      <jsp:param name="dynamical" value="StockSearchWithUnknownField"></jsp:param>
      <jsp:param name="buttonId" value="getBriefProductInfoWithUnknownField"></jsp:param>
  </jsp:include>


    <div class="clear height"></div>
    <div class="clear height"></div>
    <div class="btnClick">
    	<input type="button" value="确&nbsp;定" onfocus="this.blur();" id="confirmBtn"/>
        <input type="button" value="关&nbsp;闭" onfocus="this.blur();" id="cancelBtn"/>
    </div>
  </div>
  <div class="i_upBottom">
    <div class="i_upBottomLeft"></div>
    <div class="i_upBottomCenter"></div>
    <div class="i_upBottomRight"></div>
  </div>
</div>

</div>

</div>
</body>
</html>

