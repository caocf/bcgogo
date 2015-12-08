<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>商品采购</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/cuxiao<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/minPageAJAX<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/goodsBuyOnline<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/regionSelection<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        function newPurchaseOrder() {
            window.open($("#basePath").val() + "RFbuy.do?method=create", "_blank");
        }

        defaultStorage.setItem(storageKey.MenuUid,"WEB.TXN.PURCHASE_MANAGE.PURCHASE");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"编辑");
    </script>
</head>

<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
    <jsp:include page="autoAccessoryOnlineNavi.jsp">
        <jsp:param name="currPage" value="goodsBuyOnline"/>
    </jsp:include>
    <div class="clear"></div>
    <div class="shoppingCart">
        <div class="J-OrderBlock J-OrderBlock-${purchaseOrderDTO.supplierShopId}">
            <form:form commandName="purchaseOrderDTO" id="purchaseOrdersForm" action="RFbuy.do?method=updatePurchaseOrderOnline" method="post">
                <input type="hidden" id="id" name="id" value="${purchaseOrderDTO.id}"/>
                <input type="hidden" id="status" name="status" value="${purchaseOrderDTO.status}"/>
                <input type="hidden" id="billProducerId" name="billProducerId" value="${purchaseOrderDTO.billProducerId}"/>
                <input type="hidden" id="billProducer" name="billProducer" value="${purchaseOrderDTO.billProducer}"/>
                <input type="hidden" id="receiptNo" name="receiptNo" value="${purchaseOrderDTO.receiptNo}"/>
                <input type="hidden" id="vestDateStr" name="vestDateStr" value="${purchaseOrderDTO.vestDateStr}"/>
                <input type="hidden" id="supplier" name="supplier" value="${purchaseOrderDTO.supplier}"/>
                <input type="hidden" id="supplierShopId" name="supplierShopId" value="${purchaseOrderDTO.supplierShopId}"/>
                <input type="hidden" id="supplierId" name="supplierId" value="${purchaseOrderDTO.supplierId}"/>
                <div class="divTits">采购单号：<a class="blue_color">${purchaseOrderDTO.receiptNo}</a><a class="connect">在线</a></div>
                <div class="divTits" style="width:150px;">制单人：<span>${purchaseOrderDTO.billProducer}</span></div>
                <div class="divTits">采购日期：<span>${purchaseOrderDTO.vestDateStr}</span></div>
                <div class="divTits">单据状态：<b class="yellow_color" style="font-size:16px;">${purchaseOrderDTO.status.name}</b></div>
                <div class="divTits"><c:if test="${not empty purchaseOrderDTO.saleOrderReceiptNo}">卖家供货单号：<span>${purchaseOrderDTO.saleOrderReceiptNo}</span></c:if></div>
                <div class="cartTop"></div>
                <div class="cartBody">
                    <div class="divTit" style="width:280px;">
                        <span style="float:left;">供应商：</span>
                        <a class="blue_color J-goSupplier ellipsis" style="float: left;margin:0;width:180px;" data-supplierid="${purchaseOrderDTO.supplierId}"
                           title="${purchaseOrderDTO.supplier}" ><b>${purchaseOrderDTO.supplier}</b></a>
                        <a href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${purchaseOrderDTO.supplierShopId}" >
                            <img src="images/icon_online_shop.png" alt="在线商铺" style="vertical-align:middle;margin-bottom:3px;">
                        </a>
                        <a class="J_QQ" data_qq="${supplierShop.qqArray}" style="vertical-align:middle;"></a>
                    </div>
                    <div class="divTit" style="width:100px">联系人：<span>${empty purchaseOrderDTO.contact ? "--": purchaseOrderDTO.contact}</span>
                        <input type="hidden" id="contact" name="contact" class="txt" maxlength="20"
                                                   value="${purchaseOrderDTO.contact}" style="width:60px;"/></div>
                    <div class="divTit" style="width:160px">联系方式：<span>${empty purchaseOrderDTO.mobile ? "--": purchaseOrderDTO.mobile}</span>
                        <input type="hidden" id="mobile" name="mobile" class="txt" maxlength="11"
                                                    value="${purchaseOrderDTO.mobile}" style="width:70px;"/></div>
                    <div class="divTit" style="width:200px">所属区域：<span>${empty purchaseOrderDTO.areaInfo ? "--":purchaseOrderDTO.areaInfo}</span>
                        <input type="hidden" id="province" name="province" value="${purchaseOrderDTO.province}">
                        <input type="hidden" id="city" name="city" value="${purchaseOrderDTO.city}">
                        <input type="hidden" id="region" name="region" value="${purchaseOrderDTO.region}">
                        <%--<select id="province" name="province"--%>
                                <%--initValue="${purchaseOrderDTO.province}" class="txt select J_province"--%>
                                <%--style="width: 65px">--%>
                        <%--</select>--%>
                        <%--<select id="city" name="city"--%>
                                <%--initValue="${purchaseOrderDTO.city}" class="txt select J_city" style="width: 65px">--%>
                        <%--</select>--%>
                        <%--<select id="region" name="region"--%>
                                <%--initValue="${purchaseOrderDTO.region}" class="txt select J_region" style="width: 65px">--%>
                        <%--</select>--%>
                        <input type="hidden" id="address" name="address" class="txt J_address_input" maxlength="50"
                                                                       value="${purchaseOrderDTO.address}" style="width: 120px"/>
                    </div>
                    <c:if test="${!purchaseOrderDTO.fromQuotedPreBuyOrder}">
                        <div class="divTit" style="float: right; margin-right: 35px; margin-top: 5px;">
                            <a class="addProduct blue_color J-addProduct">添加商品</a>
                        </div>
                    </c:if>
                    <table cellpadding="0" cellspacing="0" class="tabCart J-ProductTable J-purchaseOnlineTable">
                        <col>
                        <col width="120">
                        <col width="80">
                        <col width="100">
                        <col width="80">
                        <col width="40">
                        <tr class="titleBg">
                            <td style="padding-left:30px;">商品</td>
                            <td style="float:right;padding-right:65px">卖家报价</td>
                            <td>采购量</td>
                            <td>促销优惠</td>
                            <td>小计</td>
                            <td>操作</td>
                        </tr>
                        <tr class="space"><td colspan="5"></td></tr>
                        <c:forEach var="itemDTO" items="${purchaseOrderDTO.itemDTOs}" varStatus="itemStatus">
                            <tr class="bg J-ItemBody item">
                                <input type="hidden" id="itemDTOs${itemStatus.index}.productName" name="itemDTOs[${itemStatus.index}].productName" value="${itemDTO.productName}"/>
                                <input type="hidden" id="itemDTOs${itemStatus.index}.brand" name="itemDTOs[${itemStatus.index}].brand" value="${itemDTO.brand}"/>
                                <input type="hidden" id="itemDTOs${itemStatus.index}.spec" name="itemDTOs[${itemStatus.index}].spec" value="${itemDTO.spec}"/>
                                <input type="hidden" id="itemDTOs${itemStatus.index}.model" name="itemDTOs[${itemStatus.index}].model" value="${itemDTO.model}"/>
                                <input type="hidden" id="itemDTOs${itemStatus.index}.vehicleBrand" name="itemDTOs[${itemStatus.index}].vehicleBrand" value="${itemDTO.vehicleBrand}"/>
                                <input type="hidden" id="itemDTOs${itemStatus.index}.vehicleModel" name="itemDTOs[${itemStatus.index}].vehicleModel" value="${itemDTO.vehicleModel}"/>
                                <input type="hidden" id="itemDTOs${itemStatus.index}.unit" name="itemDTOs[${itemStatus.index}].unit" value="${itemDTO.unit}"/>

                                <input type="hidden" id="itemDTOs${itemStatus.index}.supplierProductId" name="itemDTOs[${itemStatus.index}].supplierProductId" value="${itemDTO.supplierProductId}"/>
                                <input type="hidden" id="itemDTOs${itemStatus.index}.shoppingCartItemId" name="itemDTOs[${itemStatus.index}].shoppingCartItemId" value="${itemDTO.shoppingCartItemId}"/>
                                <input type="hidden" id="itemDTOs${itemStatus.index}.quotedPreBuyOrderItemId" name="itemDTOs[${itemStatus.index}].quotedPreBuyOrderItemId" value="${itemDTO.quotedPreBuyOrderItemId}"/>
                                <input type="hidden" id="itemDTOs${itemStatus.index}.promotionsId" name="itemDTOs[${itemStatus.index}].promotionsId" class="j_promotions_id" value="${itemDTO.promotionsId}"/>
                                <td style="padding-left:10px;">
                                    <div style="float: left">
                                        <div class="J-productNotInSalesWarningDiv-${itemDTO.supplierProductId}" style="margin: 10px 0;float:left;display: ${'NotInSales' eq itemDTO.supplierProductSalesStatus?'':'none'}">
                                            <a class="ban J-showAlert" style="margin: 2px 5px 0 0;"></a>
                                            <div class="alert" style="display: none">
                                                <a class="arrowTop"></a>
                                                <div class="alertAll">
                                                    <div class="alertLeft"></div>
                                                    <div class="alertBody">
                                                        商品已下架
                                                    </div>
                                                    <div class="alertRight"></div>
                                                </div>
                                            </div>
                                        </div>
                                        <div style="margin: 10px 0;float: left">
                                            <a style="width: 60px;height: 60px;cursor: pointer">
                                                <img src="${itemDTO.imageCenterDTO.productListSmallImageDetailDTO.imageURL}" style="width: 60px;height: 60px">
                                            </a>
                                        </div>
                                        <div style="width: 430px;margin: 10px 5px;float: left">
                                                ${itemDTO.commodityCode}
                                                <span style='font-weight: bold;'>
                                                ${itemDTO.productName}
                                                ${itemDTO.brand}
                                               </span>
                                                ${itemDTO.spec}
                                                ${itemDTO.model}
                                                ${itemDTO.vehicleBrand}
                                                ${itemDTO.vehicleModel}
                                        </div>
                                    </div>
                                    <c:if test="${!purchaseOrderDTO.fromQuotedPreBuyOrder}">
                                    <div style="float: left">
                                        <input type="hidden" id="promotionsId" value='${itemDTO.promotionsId}'/>
                                        <input type="hidden" id="promotionsInfoJson" value='${itemDTO.promotionsInfoJson}'/>
                                    </div>
                                    </c:if>
                                </td>
                                <td>
                                    <c:choose>
                                    <c:when test="${!purchaseOrderDTO.fromQuotedPreBuyOrder}">
                                        <div style="float: left;">
                                            <div>
                                                <c:choose>
                                                    <c:when test="${not empty itemDTO.promotionsId && itemDTO.price!=itemDTO.quotedPrice && !itemDTO.customPriceFlag}">
                                                        <a class="J-oldPrice gray"><fmt:formatNumber value="${itemDTO.quotedPrice}" pattern="#.##"/></a>&nbsp;
                                                        <a class="J-newPrice yellow_color"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></a>
                                                        <input type="hidden" class="purchasePrice" value="${itemDTO.quotedPrice}"/>
                                                           <input type="hidden" class="bargainSaveValue" value="0"/>
                                                        <span class="save J-SaveInfo" style="display: none">
                                                            <div class="saveLeft"></div>
                                                            <div class="saveBody J-SaveValue"></div>
                                                            <div class="saveRight"></div>
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a class="J-oldPrice gray" style="display: ${itemDTO.customPriceFlag?'':'none'}"><fmt:formatNumber value="${itemDTO.quotedPrice}" pattern="#.##"/></a>&nbsp;
                                                        <a class="J-newPrice yellow_color" style="display: ${itemDTO.customPriceFlag?'none':''}"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></a>
                                                        <input type="hidden" class="purchasePrice" value="${itemDTO.quotedPrice}"/>
                                                         <input type="hidden" class="bargainSaveValue" value="0"/>
                                                        <span class="save J-SaveInfo" style="display: none">
                                                            <div class="saveLeft"></div>
                                                            <div class="saveBody J-SaveValue"></div>
                                                            <div class="saveRight"></div>
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div style="margin-left: 2px;">
                                                <span class="J-customPriceInput" style="display: ${itemDTO.customPriceFlag?'':'none'}">
                                                    <input type="text" class="J-customPriceValue" id="itemDTOs${itemStatus.index}.price" name="itemDTOs[${itemStatus.index}].price" style="width: 50px" maxlength="7" value="${itemDTO.customPriceFlag?itemDTO.price:''}">
                                                    <input type="hidden" id="itemDTOs${itemStatus.index}.customPriceFlag" name="itemDTOs[${itemStatus.index}].customPriceFlag" value="${itemDTO.customPriceFlag}">
                                                </span>
                                                <a class="yiPrice J-showAlert J-customPrice" style="display: ${itemDTO.customPriceFlag?'none':''}"></a>

                                                <div class="alert" style="margin: -5px 0px 0px -5px; display: none;">
                                                    <a class="arrowTop" style="margin-left:10px;"></a>

                                                    <div class="alertAll">
                                                        <div class="alertLeft"></div>
                                                        <div class="alertBody"> 期望价</div>
                                                        <div class="alertRight"></div>
                                                    </div>
                                                </div>
                                                <a class="yiPriceBtn J-cancelCustomPrice" style="display: ${itemDTO.customPriceFlag?'':'none'}">取消</a>
                                            </div>
                                        </div>

                                    </c:when>
                                        <c:otherwise>
                                            <div style="float: left;">
                                                <div style="float: right">
                                                    <a class="yellow_color"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></a>
                                                    <input type="hidden" id="itemDTOs${itemStatus.index}.price" name="itemDTOs[${itemStatus.index}].price" style="width: 50px" maxlength="7" value="${itemDTO.customPriceFlag?itemDTO.price:''}">
                                                    <input type="hidden" id="itemDTOs${itemStatus.index}.customPriceFlag" name="itemDTOs[${itemStatus.index}].customPriceFlag" value="${itemDTO.customPriceFlag}">
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><input type="text" id="itemDTOs${itemStatus.index}.amount" name="itemDTOs[${itemStatus.index}].amount" value="${itemDTO.amount}" maxlength="6" class="txt J-ModifyAmount" style="width:50px; height:16px;"/>&nbsp;${itemDTO.unit}</td>
                                <td class="promotions_info_td"></td>
                                <td><b class="yellow_color J-ItemTotal"><fmt:formatNumber value="${itemDTO.total}" pattern="#.##"/></b></td>
                                <td><a class="blue_color J-deleteItemRow">删除</a></td>
                            </tr>
                        </c:forEach>
                        <tr class="J-ItemBottom">
                            <td colspan="3">
                                <span class="buyRemark" style="vertical-align:top;">采购备注：</span>
                                <textarea id="memo" name="memo" class="txt" maxlength="500">${purchaseOrderDTO.memo}</textarea>
                            </td>
                            <td colspan="3">
                                <c:if test="${!purchaseOrderDTO.fromQuotedPreBuyOrder}">
                                <div class="shopInfo">
                                    <div class="infoName" style="font-size: 13">优惠明细：</div>
                                    <div class="bargainDiv" style="display: none">
                                        <span>特价商品：</span>
                                        优惠<span class="arialFont">&yen;</span> <span class="promotions_total" id="bargainSpan">0</span>
                                    </div>
                                    <div class="mljDiv" style="display: none">
                                        <span>满立减：</span>
                                        优惠<span class="arialFont">&yen;</span> <span class="promotions_total" id="mljSpan">0</span>
                                    </div>
                                    <div class="mjsDiv" style="display: none">
                                        <span>满就送：</span>
                                        <span id="mjsSpan"></span>
                                    </div>
                                    <div class="freeShippingDiv" style="display: none">
                                        <span id="freeShipping">送货上门</span>
                                    </div>


                                    <b class="almost">
                                        优惠总计:<span class="arialFont">&yen;</span>
                                        <span class="green_color" id="promotionsTotalSpan">0</span> 元
                                    </b>
                                </div>
                                </c:if>
                                <div style="float: right;">
                                    合计：¥<span class="yellow_color J-OrderTotal"><fmt:formatNumber value="${purchaseOrderDTO.total}" pattern="#.##"/></span>
                                    <c:set var="allTotal" value="${(allTotal==null?0:allTotal)+purchaseOrderDTO.total}"/>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="5">
                                <span class="buyRemark">期望交货日期：</span>
                                <input type="text" id="deliveryDateStr" name="deliveryDateStr" value="${purchaseOrderDTO.deliveryDateStr}" readonly="true" class="J-DeliveryDate txt"/>
                            </td>
                        </tr>
                    </table>
                </div>
                <div class="cartBottom"></div>
                <div class="clear i_height"></div>
            </form:form>
        </div>
        <div class="height"></div>
        <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE.CANCEL">
            <div class="invalidImg" style="display: block">
                <input id="nullifyBtn" type="button" onfocus="this.blur();">

                <div class="invalidWords" id="invalidWords">作废</div>
            </div>
        </bcgogo:hasPermission>

        <div class="btn_div_Img" id="cancel_div">
            <input id="resetBtn" type="button" style="margin: 0 0 0 0;" class="cancel j_btn_i_operate" onfocus="this.blur();"/>
            <div class="optWords">重置</div>
        </div>

        <div class="shopping_btn">
            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE.SAVE">
                <div class="btn_div_Img" id="purchaseModify_div">
                    <input id="purchaseModifyBtn" type="button" class="sureBuy j_btn_i_operate" onfocus="this.blur();"/>
                    <div class="optWords">改单</div>
                </div>
            </bcgogo:hasPermission>
            <div class="btn_div_Img" id="print_div">
                <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                <div class="optWords">打印</div>
            </div>
        </div>
        <div class="height"></div>
    </div>
</div>
<div class="alertMain J-PurchaseOrderOnlineModify" id="selectProductDialog" title="添加配件查询" style="display:none">
    <div class="titBody" style="height:100px;">
        <div class="titLine" style="width:705px;">配件查询</div>
        <div class="titBodys" style="height:60px;">
            <form id="wholeSalerStockSearchForm" action="autoAccessoryOnline.do?method=getCommodityQuotationsList" method="post" name="thisform">
                <input type="hidden" id="shopId" name="shopId" />
                <input type="hidden" name="maxRows" id="maxRows" value="10">
                <input type="hidden" id="fromSource" name="fromSource" value="goodsBuy"/>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="searchWord" name="searchWord" searchField="product_info" initialValue="品名/品牌/规格/型号/适用车辆" style="width:175px;"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="productName" name="productName" searchField="product_name" initialValue="品名"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="productBrand" name="productBrand" searchField="product_brand" initialValue="品牌/产地"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="productSpec" name="productSpec" searchField="product_spec" initialValue="规格"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="productModel" name="productModel" searchField="product_model" initialValue="型号"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand" initialValue="车辆品牌"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model" initialValue="车型"/></div>
                <div class="alertDivTit"><input type="text" class="alertTxt J-productSuggestion J-initialCss J_clear_input" id="commodityCode" name="commodityCode" searchField="commodity_code" initialValue="商品编号" style="text-transform: uppercase;"/></div>
                <div class="alertDivTit">
                    <a class="btnCheck" id="clearConditionBtn">清空条件</a>
                    <a class="btnCheck" style="margin-right: 10px;" id="searchQuotationBtn">查&nbsp;询</a>
                </div>
            </form>
        </div>
    </div>
    <div class="record">共有配件&nbsp;<span class="J-totalNumber">0</span>&nbsp;条记录</div>
    <table cellpadding="0" cellspacing="0" class="tabRecord" id="commodityTable">
        <col width="70">
        <col />
        <col width="90" />
        <col width="90" />
        <tr class="tabTitle">
            <td><input type="checkbox" id="checkAll" class="chk" />全选</td>
            <td>配件名</td>
            <td>库存量</td>
            <td>最新报价</td>
        </tr>
    </table>
    <!--分页-->
    <div class="stockPager">
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="autoAccessoryOnline.do?method=getCommodityQuotationsList"></jsp:param>
            <jsp:param name="data" value="{startPageNo:1,maxRows:15}"></jsp:param>
            <jsp:param name="jsHandleJson" value="drawCommodityQuotationsTable"></jsp:param>
            <jsp:param name="dynamical" value="commodityQuotations"></jsp:param>
            <jsp:param name="pageType" value="minPage"></jsp:param>
            <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
    </div>
    <!--分页结束-->
    <div class="height"></div>
    <div class="button"><a class="btnSure" id="selectProductConfirmBtn">确&nbsp;定</a><a class="btnSure" id="selectProductCloseBtn">取&nbsp;消</a></div>
</div>
<div id="deliveryDateDialog" style="display:none;" class="alertMain">
    <div style="margin-top: 10px;">
        <span style="font-size: 14px;">您没有输入期望交货日期，请输入：</span>
        <input id="deliveryDateInput" type="text" class="text"/>
        <input id="btnType" type="hidden" />
    </div>
    <div class="button" style="width:100%;margin-top: 10px;">
        <a id="confirmBtn1" class="btnSure" href="javascript:;">确 定</a>
        <a id="cancleBtn" class="btnSure" href="javascript:;">取消</a>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>