<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>在线采购订单</title>

  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/accept<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" href="js/components/themes/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.css">

  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/autoaccessoryonline/todoOrders<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/page/autoaccessoryonline/my_purchase_orders<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/components/ui/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/components/ui/rattingComments<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
      defaultStorage.setItem(storageKey.MenuUid, "WEB.SCHEDULE.REMIND_ORDERS.PURCHASE");
      defaultStorage.setItem(storageKey.MenuCurrentItem,"列表");
      $(function () {
          if ('${orderStatus}') {
              $("#orderStatus").val('${orderStatus}');
              $("#searchOnlinePurchaseOrders").click();
          }
      });
  </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <div class="titBody">
    <jsp:include page="../supplyCenterLeftNavi.jsp">
        <jsp:param name="currPage" value="purchase"/>
    </jsp:include>
    <div class="content-main self-purchase-order">

        <div class="group-notice">
            <table>
                <colgroup>
                <col width="88px">
                <col width="135px">
                <col width="50px">
                <col width="110px">
                <col width="110px">
                <col width="120px">
                <col width="120px">
                </colgroup>
              <tr>
                  <td><span class="info-label">新订单: </span></td>
                  <td><span class="line-info">共有&nbsp;<em class="number" id="purchase_new">0</em>&nbsp;条待卖家处理</span></td>
                  <td><span class="info-content">其中</span></td>
                  <td class="info-content">
                      <a class="line-info">
                          <em class="word">今日新增</em>
                          <em class="number" id="purchase_today_new">(0)</em>
                      </a>
                  </td>
                  <td class="info-content">
                      <a class="line-info">
                          <em class="word">往日新增</em>
                          <em class="number" id="purchase_early_new">(0)</em>
                      </a>
                  </td>
                  <td></td>
                  <td></td>
              </tr>
                <tr>
                  <td><span class="info-label">处理中的订单: </span></td>
                  <td><span class="line-info">共有&nbsp;<em class="number" id="purchase_in_progress">0</em>&nbsp;条</span></td>
                  <td><span class="info-content">其中</span></td>
                    <td>
                        <a class="line-info">
                            <em class="word">卖家备货中</em>
                            <em class="number" id="purchase_seller_stock">(0)</em>
                        </a>
                    </td>
                  <td>
                      <a class="line-info">
                          <em class="word">卖家已发货</em>
                          <em class="number" id="purchase_seller_dispatch">(0)</em>
                      </a>
                  </td>
                  <td>
                      <a class="line-info">
                          <em class="word">卖家中止交易</em>
                          <em class="number" id="purchase_seller_stop">(0)</em>
                      </a>
                  </td>
                    <td>
                        <a class="line-info">
                            <em class="word">卖家拒绝销售</em>
                            <em class="number" id="purchase_seller_refused">(0)</em>
                        </a>
                    </td>
                </tr>
                <tr>
                    <td><span class="info-label">已入库: </span></td>
                    <td><span class="line-info">共有&nbsp;<em class="number" id="purchase_done">0</em>&nbsp;条</span></td>
                    <td><span class="info-content">其中</span></td>
                    <td class="info-content">
                        <a class="line-info">
                            <em class="word">今日入库</em>
                            <em class="number" id="purchase_today_done">(0)</em>
                        </a>
                    </td>
                    <td class="info-content">
                        <a class="line-info">
                            <em class="word" >往日入库</em>
                            <em class="number" id="purchase_early_done">(0)</em>
                        </a>
                    </td>
                    <td></td>
                    <td></td>
                </tr>
            </table>
        </div><!--end group-notice-->
        <div class="search-param">
            <div class="param-title">
                订单中心
            </div>

            <div class="param-content">
                <form id="onlinePurchaseOrderSearchForm" class="J_leave_page_prompt">
                <dl class="content-product-info">
                    <dt >商品信息:</dt>
                    <dd>
                        <input class="product-info search-fuzzy J-productSuggestion" id="searchWord" searchField="product_info" name="searchWord" type="text" placeholder="品名/品牌/规格/型号/车辆品牌/车型/商品编号"/>
                        <input class="product-name search-exact J-productSuggestion" id="productName" name="productName" searchField="product_name" type="text" placeholder="品名"/>
                        <input class="product-brand search-exact J-productSuggestion" id="productBrand" name="productBrand" searchField="product_brand" type="text" placeholder="品牌"/>
                        <input class="product-specifications search-exact J-productSuggestion" id="productSpec" name="productSpec" searchField="product_spec" type="text" placeholder="规格"/>
                        <input class="product-type search-exact J-productSuggestion"  id="productModel" name="productModel" searchField="product_model" type="text" placeholder="型号"/>
                        <input class="product-vehicle-brand search-exact J-productSuggestion" id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand" type="text" placeholder="车辆品牌"/>
                        <input class="product-vehicle-type search-exact J-productSuggestion" id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model" type="text" placeholder="车型"/>
                        <input class="product-number search-exact J-productSuggestion" id="commodityCode" name="commodityCode" searchField="commodity_code" type="text" placeholder="商品编号"/>
                    </dd>
                    <div class="cl"></div>
                </dl>
                <dl class="content-product-info">
                    <dt>订单信息:</dt>
                    <dd>
                        <input id="receiptNo" name="receiptNo" class="product-name order-info" type="text" placeholder="订单号"/>
                    </dd>
                </dl>
                <dl class="content-product-info">
                    <dt style="line-height: 20px">供应商信息:</dt>
                    <dd>
                        <input id="supplierInfoSearchText" name="customerOrSupplierInfo" class="product-name supplier-info" type="text" placeholder="供应商名/联系人/手机"/>
                    </dd>
                </dl>
                <dl class="content-product-info">
                    <dt >订单状态:</dt>
                    <dd>
                        <select class="order-status" id="orderStatus" name="orderStatus">
                            <option value="">全部</option>
                            <option value="SELLER_PENDING,SELLER_STOCK,SELLER_DISPATCH,PURCHASE_SELLER_STOP,SELLER_REFUSED">待办采购单</option>
                            <option value="SELLER_STOCK,SELLER_DISPATCH,PURCHASE_SELLER_STOP,SELLER_REFUSED"
                            ${orderStatus eq "SELLER_STOCK,SELLER_DISPATCH,PURCHASE_SELLER_STOP,SELLER_REFUSED"? "selected":""}>处理中的采购单</option>
                            <option value="SELLER_PENDING" ${orderStatus == "SELLER_PENDING" ? "selected":""}>待卖家处理</option>
                            <option value="SELLER_STOCK" ${orderStatus eq "SELLER_STOCK"? "selected":""}>卖家备货中</option>
                            <option value="SELLER_DISPATCH" ${orderStatus eq "SELLER_DISPATCH"? "selected":""}>卖家已发货</option>
                            <option value="SELLER_REFUSED" ${orderStatus eq "SELLER_REFUSED"? "selected":""}>卖家已拒绝</option>
                            <option value="PURCHASE_SELLER_STOP" ${orderStatus eq "PURCHASE_SELLER_STOP"? "selected":""}>卖家终止销售</option>
                            <option value="PURCHASE_ORDER_DONE" ${orderStatus eq "PURCHASE_ORDER_DONE"? "selected":""}>已入库</option>
                            <option value="PURCHASE_ORDER_REPEAL" ${orderStatus eq "PURCHASE_ORDER_REPEAL"? "selected":""}>已作废</option>
                        </select>
                    </dd>
                    <div class="cl"></div>
                </dl>
                <%--<dl class="content-product-info">--%>
                    <%--<dt>评价状态:</dt>--%>
                    <%--<dd>--%>
                        <%--<select class="order-status" id="commentStatus">--%>
                            <%--<option value="">全部</option>--%>
                            <%--<option value="commented">已评价</option>--%>
                            <%--<option value="uncommented">未评价</option>--%>
                        <%--</select>--%>
                    <%--</dd>--%>
                    <%--<div class="cl"></div>--%>
                <%--</dl>--%>
                <dl class="content-product-info">
                    <dt>下单时间:</dt>
                    <dd>
                        <a class="btnList" id="my_date_oneWeekBefore" name="my_date_select">近一周</a>&nbsp;
                        <a class="btnList" id="my_date_oneMonthBefore" name="my_date_select">近一个月</a>&nbsp;
                        <a class="btnList" id="my_date_threeMonthBefore" name="my_date_select">近三个月</a>&nbsp;
                        <a class="btnList" id="my_date_self_defining" name="my_date_select">自定义</a>&nbsp;
                        <input id="startDate" name="startTimeStr" class="my_startdate J_hasDatePicker product-name date-area" type="text" value="${startTimeStr}" readonly />至
                        <input id="endDate" name="endTimeStr" class="my_enddate J_hasDatePicker product-name date-area" type="text" value="${endTimeStr}" readonly />
                        <input id="inventoryVestStartDate" name="inventoryVestStartDateStr" type="hidden" value="${inventoryVestStartDateStr}" />
                        <input id="inventoryVestEndDate" name="inventoryVestEndDateStr"  type="hidden" value="${inventoryVestEndDateStr}" />
                    </dd>
                </dl>


                <div class="group-button-control">
                    <span id="searchOnlinePurchaseOrders" class="button-search button-blue-gradient">搜&nbsp;&nbsp;索</span>
                    <span class="button-clear" id="clearSearchField">清空条件</span>
                </div>
                </form>
            </div>

        </div><!--end search-param-->


        <div class="search-result">
            <dl class="result-list ">
                <dt class="list-title">
                    <ul>
                        <!--<li class="item-checkbox"><input type="checkbox" /></li>-->
                        <li class="item-product-info width-set">商品信息</li>
                        <li class="item-product-unit-price width-set">单价(元)</li>
                        <li class="item-product-quantity width-set">数量</li>
                        <li class="item-product-price width-set">成交价(元)</li>
                        <li class="item-product-goods-of-return width-set">退货</li>
                        <li class="item-product-payables width-set">应付款(元)</li>
                        <li class="item-product-order-status width-set">订单状态</li>
                        <li class="item-product-operating width-set">操作</li>
                    </ul>
                    <div class="cl"></div>
                </dt>
                <dd class="list-content J-PurchaseOrdersList">
                </dd>

            </dl><!--end result-list-->

            <!--插入你喜爱的分页控件 snips-->
            <div class="page-control" >

                <bcgogo:ajaxPaging url="orderCenter.do?method=getOnlinePurchaseOrders" dynamical="purchaseOrderList"
                                   data='{startPageNo:1,maxRows:5}' postFn="drawPurchaseOrderList" display="none"/>
                <div class="cl"></div>
            </div>
            <%--<div class="page-control">--%>
            <%--add your paging control snips--%>
            <%--</div>--%>

        </div>
        <!--end search-result-->

    </div>
    <!--end content-main-->
    <div class="cl"></div>
    <div class="height"></div>
</div>
    <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_COMMENT.SAVE">
    <input id="purchaseSupplierShopId" name="purchaseSupplierShopId" type="hidden"
           value="${purchaseInventoryDTO.purchaseSupplierShopId}">
        <%--//供应商评价打分--%>
    <div class="i_searchBrand" id="supplierCommentDiv" title="评价供应商" style="display:none; width: 500px;">
        <form id="supplierCommentForm" method="post" action="storage.do?method=saveSupplierComment">
            <table border="0" width="480">
                <tr>
                    <td>
                        货品 质量：
                        <input type="hidden" id="qualityScoreDivHidden" name="qualityScore" value=""/>
                        <input type="hidden" id="purchaseInventoryIdStr" name="purchaseInventoryIdStr" value=""/>
                    </td>
                    <td id="qualityScoreDiv"></td>
                    <td rowspan="4">
                        <div class="alertScore">
                            <a class="arrowLeft"></a>

                            <div class="alertInfo">
                                <div class="alertTop"></div>
                                <div class="alertBody">
                                    <div>小提示：点击星星就能打分了，打分完全是匿名滴。</div>
                                    <a class="yellow_Star"></a>
                                    <a class="hand"></a>
                                </div>
                                <div class="alertBottom"></div>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        货品性价比：
                        <input type="hidden" id="performanceScoreDivHidden" name="performanceScore" value=""/>
                    </td>
                    <td id="performanceScoreDiv"></td>
                </tr>

                <tr>
                    <td>
                        发货 速度：
                        <input type="hidden" id="speedScoreDivHidden" name="speedScore" value=""/>
                    </td>
                    <td id="speedScoreDiv"></td>
                </tr>
                <tr>
                    <td>
                        服务 态度：
                        <input type="hidden" id="attitudeScoreDivHidden" name="attitudeScore" value=""/>
                    </td>
                    <td id="attitudeScoreDiv"></td>
                </tr>
                <tr>
                    <td>详细评论：</td>
                    <td colspan="2"><textarea id="supplierCommentContent" name="commentContent" style="width:320px;"
                                              maxlength="500" onkeydown="getRemainChar(this);" onkeyup="getRemainChar(this);"></textarea>
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                        <span style="margin-left: 50px;">您还能输入<span id="supplierCommentContentRemain">500</span>个字</span>
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                        <div class="btnClick" style="height:50px; line-height:50px">
                            <input type="button" id="commentConfirmBtn" onfocus="this.blur();" value="发表评论">
                            <input type="button" id="commentCancelBtn" onfocus="this.blur();" value="取消">
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>

      <%--//供应商评价打分--%>
  <div class="i_searchBrand" id="addSupplierCommentDiv" title="追加评价" style="display:none; width: 500px;">

    <form id="addSupplierCommentForm" method="post" action="storage.do?method=addSupplierComment">
      <table border="0" width="480">
        <tr>
          <td>备注：</td>
          <td><textarea id="addCommentContent" name="addCommentContent" style="width:320px;"
                        maxlength="500" onkeydown="getRemainChar(this);" onkeyup="getRemainChar(this);"></textarea>
            <input id="supplierCommentRecordIdStr" name="supplierCommentRecordIdStr" value="" type="hidden"/>

          </td>
        </tr>
        <tr>
          <td colspan="2">
            <span style="margin-left: 50px;">您还能输入<span id="addCommentContentRemain">500</span>个字</span>
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <div class="btnClick" style="height:50px; line-height:50px">
              <input type="button" id="addCommentConfirmBtn" onfocus="this.blur();" value="发表">
              <input type="button" id="addCommentCancelBtn" onfocus="this.blur();" value="取消">
            </div>
          </td>
        </tr>
      </table>
    </form>
  </div>
    </bcgogo:hasPermission>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>