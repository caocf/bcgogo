<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>上架管理</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/goodsInOffSales<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "AUTO_ACCESSORY_ONLINE_GOODS_IN_OFF_SALES_MANAGE");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"列表");

        $().ready(function(){

            $(".toBatchGoodsInSalesEditorBtn").click(function(){
                var productIdArr=new Array();
                $(".itemChk").each(function(){
                    if($(this).attr("checked")&&!G.isEmpty($(this).attr("productId"))){
                        productIdArr.push($(this).attr("productId"))
                    }
                });
                if(productIdArr.length==0){
                    nsDialog.jAlert("请选择要上架的商品。");
                    return;
                }
                toBatchGoodsInSalesEditor(productIdArr.toString());
            });


            $("#resetBtn").click(function(){
                $(".J-productSuggestion").val("").blur();
                $('[name^="recommendedPrice"],[name^="tradePrice"],#product_kind').val("");
                $("#searchProductBtn").click();
            });

            initStockStatInfo();
        });


    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
<div class="clear i_height"></div>
<div class="titBody">
<jsp:include page="supplyCenterLeftNavi.jsp">
    <jsp:param name="currPage" value="inSalingGoodsList"/>
</jsp:include>


<div class="added-management">
<div class="group-notice">
    <div class="line-info">
        您的库存商品：共<em id="allStockProductNum" class="number">0</em>种&nbsp;&nbsp;&nbsp;
        已上架的商品：共<em id="productInSalesNum" class="blue_col" style="font-weight: bold;font-style: normal;" pageType="_unInSalingGoodsList">0</em>种&nbsp;&nbsp;&nbsp;
        未上架的商品：共<em id="productUnInSaleNum" class="blue_col" style="font-weight: bold;font-style: normal;" pageType="_inSalingGoodsList">0</em>种&nbsp;&nbsp;&nbsp;
        正在促销的商品：共<em id="promotionsProductNum" class="blue_col" style="font-weight: bold;font-style: normal;" pageType="_unInSalingGoodsList">0</em>种
    </div>
    <div class="line-info">
        友情提示：选择库存商品上架，若修改信息，库存商品信息也会随之修改！新增商品上架，库存中也会新增该商品哦！
    </div>
</div>

<div class="group-tab">
    <div onclick="toInSalingGoodsList()" class="group-item">已上架商品</div>
    <div onclick="toUnInSalingGoodsList()" class="group-item actived">仓库中的商品</div>
    <a onclick="toGoodsInSalesEditor()" style="float: right;" class="blue_color addNew">新增商品上架</a>
    <div class="cl"></div>
</div>

<div class="group-content" >
    <form:form commandName="searchConditionDTO" id="searchProductForm" action="product.do?method=getProducts" method="post" name="thisform">
    <input type="hidden" id="salesStatus" name="salesStatus" value="NotInSales"/>
    <input type="hidden" id="sortStatus" name="sort" value="inventoryAmountDesc"/>
    <input type="hidden" name="maxRows" value="30" />
    <input type="hidden" name="startPageNo" value="1" />
    <div class="group-display">
        <div class="search-param">
            <dl>
                <dt>商品信息</dt>
                <dd>
                    <input id="productName" name="productName" searchField="product_name"
                           type="text" placeholder="品名" class="w100 txt J-productSuggestion"/>
                    <input  id="productBrand" name="productBrand" searchField="product_brand"
                            type="text"  placeholder="品牌" class="w100 txt J-productSuggestion"/>
                    <input id="productSpec" name="productSpec" searchField="product_spec"
                           type="text" placeholder="规格" class="w100 txt J-productSuggestion"/>
                    <input  id="productModel"  name="productModel" searchField="product_model"
                            type="text" placeholder="型号" class="w100 txt J-productSuggestion"/>
                    <input id="productVehicleBrand" name="productVehicleBrand" searchField="product_vehicle_brand"
                           type="text" placeholder="车辆品牌" class="w100 txt J-productSuggestion"/>
                    <input id="productVehicleModel" name="productVehicleModel" searchField="product_vehicle_model"
                           type="text" placeholder="车型" class="w100 txt J-productSuggestion"/>
                    <input id="commodityCode" name="commodityCode" searchField="commodity_code"
                           type="text" placeholder="商品编号" class="w100 txt J-productSuggestion"/>
                </dd>
                <div class="cl"></div>
                <dt>店铺中的分类</dt>
                <dd>
                    <input id="product_kind" name="productKind" type="text" placeholder="您店铺自己的商品分类" class="w210 txt J-bcgogo-droplist-on"/>
                </dd>
                <div class="cl"></div>
            </dl>

            <div class="group-button">
                <div id="searchProductBtn" pageType="_unInSalingGoodsList" class="button-search button-blue-gradient" onselectstart="return false;">搜&nbsp;&nbsp;索</div>
                <div id="resetBtn" class="button-clear" onselectstart="return false;">清空条件</div>
                <div class="cl"></div>
            </div>
        </div><!--end search-param-->
        <div class="search-result">
            <div class="line_develop" style="margin-left: 9px;width: 798px;">
                <div class="sort_label">排序方式：</div>
                <a class="s-product-inSales hover" ascContact="点击后按库存量升序排列！" descContact="点击后按库存量降序排列！" currentSort="Desc" sortFiled="inventoryAmount">库存量<span class="J-sort-span arrowDown"></span>
                    <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                        <span class="arrowTop" style="margin-left:20px;"></span>
                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody">
                                点击后按库存量升序排列！
                            </div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                </a>
                <a class="s-product-inSales" ascContact="点击后按商品分类升序排列！" descContact="点击后按商品分类降序排列！" currentSort="Desc" sortFiled="inSalesAmount">商品分类<span class="J-sort-span arrowDown"></span>
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
                <a class="s-product-inSales" ascContact="点击后按零售价升序排列！" descContact="点击后按零售价降序排列！"
                   currentSort="Desc" sortFiled="recommendedPrice" style="border-right: 0px">
                    零售价<span class="J-sort-span arrowDown"></span>
                    <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                        <span class="arrowTop" style="margin-left:20px;"></span>
                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody">
                                点击后按零售价升序排列！
                            </div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                </a>
                         <span class="txtTransaction dashBoardHolder">
                            <input type="text" name="recommendedPriceStart" filed="bar-priceInput-start" class="priceInput txt" style="width:42px; height:17px;" />&nbsp;至&nbsp;
                           <div class="dashBoard txtList" style="padding-top:10px;margin-top: -1px;width: 115px;display: none">
                               <span class="clean" style="cursor: pointer;">清除</span>
                               <span class="btnSure">确定</span>
                               <div  class="listNum">
                                   <span  class="pItem blue_color" end="1000">1千以下</span>
                                   <span  class="pItem blue_color" start="1000" end="5000">1千~5千</span>
                                   <span  class="pItem blue_color" start="5000" end="10000">5千~1万</span>
                                   <span  class="pItem blue_color" start="10000">1万以上</span>
                               </div>
                           </div>
                            <input type="text" name="recommendedPriceEnd" filed="bar-priceInput-end" class="priceInput txt" style="width:40px; height:17px;" />
                        </span>
                <a class="s-product-inSales" ascContact="点击后按批发价升序排列！" descContact="点击后按批发价降序排列！"
                   currentSort="Desc" sortFiled="tradePrice" style="border-right: 0px">
                    批发价<span class="J-sort-span arrowDown"></span>
                    <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                        <span class="arrowTop" style="margin-left:20px;"></span>
                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody">
                                点击后按批发价升序排列！
                            </div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                </a>
                         <span class="dashBoardHolder txtTransaction">
                            <input type="text" name="tradePriceStart" filed="bar-priceInput-start" class="priceInput txt" style="width:42px; height:17px;" />&nbsp;至&nbsp;
                             <div class="dashBoard txtList" style="padding-top:10px;margin-top: -1px;width: 115px;display: none">
                                 <span class="clean" style="cursor: pointer;">清除</span>
                                 <span class="btnSure">确定</span>
                                 <div  class="listNum">
                                     <span  class="pItem blue_color" end="1000">1千以下</span>
                                     <span  class="pItem blue_color" start="1000" end="5000">1千~5千</span>
                                     <span  class="pItem blue_color" start="5000" end="10000">5千~1万</span>
                                     <span  class="pItem blue_color" start="10000">1万以上</span>
                                 </div>
                             </div>
                            <input type="text" name="tradePriceEnd" filed="bar-priceInput-end" class="priceInput txt" style="width:40px; height:17px;" />
                        </span>
                    <%--<a class="s-product-inSales" ascContact="点击后按成本均价升序排列！" descContact="点击后按成本均价降序排列！"--%>
                    <%--currentSort="Desc" sortFiled="inventoryAveragePrice" style="border-right: 0px">--%>
                    <%--成本均价<span class="J-sort-span arrowDown"></span>--%>
                    <%--<div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">--%>
                    <%--<span class="arrowTop" style="margin-left:20px;"></span>--%>
                    <%--<div class="alertAll">--%>
                    <%--<div class="alertLeft"></div>--%>
                    <%--<div class="alertBody">--%>
                    <%--点击后按成本均价升序排列！--%>
                    <%--</div>--%>
                    <%--<div class="alertRight"></div>--%>
                    <%--</div>--%>
                    <%--</div>--%>
                    <%--</a>--%>
                    <%--<span class="txtTransaction">--%>
                    <%--<input type="text" name="inventoryAveragePriceDown" class="priceInput txt" style="width:42px; height:17px;" />&nbsp;至&nbsp;--%>
                    <%--<input type="text" name="inventoryAveragePriceUp" class="priceInput txt" style="width:40px; height:17px;" />--%>
                    <%--</span>--%>
            </div>

            </form:form>
            <div class="cl"></div>
            <table id="unInSalingGoodsTable" class="list-result" cellpadding="0" cellspacing="0">
                <thead style="margin-bottom: 10px">
                <tr>
                    <th class="item-checkbox"></th>
                    <th class="item-product-infomation">商品信息</th>
                    <th class="item-product-categories">店铺中的分类</th>
                    <th class="item-product-inventory">库存量</th>
                    <%--<th class="item-product-average-costs">成本均价</th>--%>
                    <%--<th class="item-product-retail-price">零售价</th>--%>
                    <th class="item-product-wholesale">上次上架售价</th>
                    <th class="item-product-operating">操作</th>
                </tr>
                </thead>

                <tr class="space">
                    <td class="greyline" style="height: 42px"  colspan="9">
                        <input class="select_all" type="checkBox" />
                        <div class="toBatchGoodsInSalesEditorBtn batch-operate up-batch">批量上架</div>
                    </td>
                </tr>
            </table>

        </div><!--end search-result-->

        <!-- 分页代码 -->
        <div>
            <bcgogo:ajaxPaging
                    url="product.do?method=getProducts"
                    data="{
                                startPageNo:1,maxRows:25,
                                sort:'inventoryAmountDesc',
                                salesStatus:'NotInSales'
                                }"
                    postFn="initUnInSalingGoodsList"
                    dynamical="_unInSalingGoodsList"/>
        </div>

    </div><!--end group-display-->

    <!-- 底部批量选择 Bar -->
    <div class="batch-shelves">
        <div class="all-select">
            <label><input class="select_all" type="checkbox"/>&nbsp;全选</label>
        </div>
        <div class="toBatchGoodsInSalesEditorBtn button-batch-shelves button-yellow-gradient" onselectstart="return false;">批量上架</div>
        <div class="cl"></div>
    </div>

</div><!--end group-content-->

</div>


</div>
</div>
<%--<div id="mask" style="display:block;position: absolute;"></div>--%>
<div class="insaling-goods-footer">
    <%@include file="/WEB-INF/views/footer_html.jsp" %>
</div>
</body>
</html>