<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>添加上架商品</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/promotions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "PROMOTIONS_MANAGER_MENU");

        function getSProductIdArr(){
            var productIdArr=new Array();
            $(".sProductIdDiv input").each(function(){
                productIdArr.push($(this).attr("productId"));
            });
            return productIdArr;
        }

        function reGenerateSProductIdDiv(){
            var $pInputList=$(".sProductIdDiv input");
            if($pInputList.size()>0){
                $pInputList.each(function(i){
                    var prefix="promotionsProductDTOList["+i+"].productLocalInfoId";
                    $(this).attr("name",prefix);
                });
            }
        }

        $().ready(function(){
            $(".itemChk").live("click",function(){
                var checkedCount = $(".itemChk:checked").length;
                $("#allProduct").attr("checked",$(".itemChk").length == checkedCount ? true : false);
                $("#selectProductCount").text(checkedCount);
            });


            //点击加入活动
            $("#addPromotionsProductBtn").click(function(){
                var promotionsId=$("#promotionsId").val();
                if(G.isEmpty(promotionsId)){
                    GLOBAL.error("促销id异常，无法添加商品!");
                    return;
                }
                $(".sProductIdDiv input").remove();
                var index=0;
                $(".itemChk").each(function(){
                    if($(this).attr("checked")){
                        var productId=$(this).attr("productId");
                        if(!arrayUtil.contains(getSProductIdArr(),productId)){
                            $(".sProductIdDiv").append( '<input type="hidden" name="promotionsProductDTOList['+index+'].productLocalInfoId" value="'+productId+'"/>');
                            index++;
                        }
                    }
                });

                if($(".sProductIdDiv input").size()==0){
                    nsDialog.jConfirm("未添加任何商品，是否查看已在使用此促销的商品？", null, function(returnVal){
                        if(returnVal){
                            toProductInPromotion(promotionsId);
                        }
                    });
                    return;
                }
//                reGenerateSProductIdDiv();
                $("#promotionsProductTable").ajaxSubmit({
                    url:"promotions.do?method=addPromotionsProduct",
                    dataType: "json",
                    type: "POST",
                    success: function (json) {
                        if(json.success){
                            var promotionsId=json.data;
                            toProductInPromotion(promotionsId);
                        }else{
                            nsDialog.jAlert(json.msg);
                        }
                    },
                    error:function(){
                        nsDialog.jAlert("网络异常！");
                    }
                });
            });
        });

    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="../supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="promotions"/>
            <jsp:param name="biMenu" value="promotionManager"/>
        </jsp:include>

        <div class="bodyLeft">

            <input type="hidden" id="promotionsType" value="${promotions.type}">
            <h3 class="title">创建促销</h3>
            <div class="cuSearch">
                <div class="clear chartStep blue_color">
                    <span>1、促销设置</span>
                    <a class="stepImg"></a>
                    <span class="yellow_color">2、添加上架商品</span>
                    <a class="stepImg"></a>
                    <span>3、促销中的商品</span>
                    <a class="stepImg"></a>
                    <span>4、推广您的促销</span>
                </div>
                <div class="lineTop"></div>
                <div class="cartBody lineBody">
                    <input type="hidden" id="promotionStatus" value="${promotions.status}">
                    <form:form commandName="searchConditionDTO" id="searchPromotionsProductForm" action="promotions.do?method=getPromotionsProduct" method="post" name="thisform">
                        <input type="hidden" id="sortStatus" name="sort" value="inventoryAmountDesc"/>
                        <input type="hidden" id="promotionsFilter" name="promotionsFilter" value="add_promotions_product_current" />
                        <input type="hidden" id="promotionsIdFilter" name="promotionsId" value="${promotions.idStr}">
                        <input type="hidden" id="promotionsId"  value="${promotions.idStr}">
                        <input type="hidden" name="maxRows" value="30">
                        <input type="hidden" name="startPageNo" value="1">

                        <div class="lineAll">
                            <div class="divTit divWarehouse divShopping">
                                <span class="spanName">商品&nbsp;</span>
                                <div class="warehouseList" style="width:705px; margin:0px;">
                                    <input class="txt J-productSuggestion J-initialCss J_clear_input"  initialValue="品名/品牌/规格/型号/车辆品牌/车型"
                                           searchfield="product_info" name="searchWord" id="searchWord" style="width:255px;" autocomplete="off">
                                    <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productName" name="productName"
                                           searchField="product_name" initialValue="品名" cssStyle="width:80px;" autocomplete="off"/>
                                    <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productBrand" name="productBrand"
                                           searchField="product_brand" initialValue="品牌" cssStyle="width:75px;" autocomplete="off"/>
                                    <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productSpec" name="productSpec"
                                           searchField="product_spec" initialValue="规格" cssStyle="width:80px;" autocomplete="off"/>
                                    <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productModel"  name="productModel"
                                           searchField="product_model" initialValue="型号" cssStyle="width:80px;" autocomplete="off"/>
                                    <input class="txt J-productSuggestion J-initialCss J_clear_input" id="productVehicleBrand" name="productVehicleBrand"
                                           searchField="product_vehicle_brand" initialValue="车辆品牌" cssStyle="width:80px;" autocomplete="off"/>
                                    <input class="txt J-productSuggestion J-initialCss J_clear_input"  id="productVehicleModel" name="productVehicleModel"
                                           searchField="product_vehicle_model" initialValue="车型" cssStyle="width:80px;" autocomplete="off"/>
                                    <input class="txt J-productSuggestion J-initialCss J_clear_input" id="commodityCode" name="commodityCode"
                                           searchField="commodity_code" initialValue="商品编号" cssStyle="text-transform: uppercase;width:80px;" autocomplete="off"/>
                                    <input id="product_kind"  class="J-initialCss J_clear_input txt J-bcgogo-droplist-on" type="text" initialvalue="商品分类" style="width:60px;">
                                    <c:if test="${promotions.range!='EXCEPT'}">
                                        <label class="rad">
                                            <input id="promotionsProductFilter" type="checkbox" />过滤已参加当前活动的商品
                                        </label>
                                    </c:if>
                                </div>
                            </div>
                            <div class="divTit button_conditon button_search">
                                <a id="resetConditionBtn" class="blue_color clean">清空条件</a>
                                <a id="searchPromotionsProductBtn" pageType="addPromotionsProduct" class="button">搜 索</a>
                            </div>
                        </div>
                        <div class="i_height"></div>
                        <div class="line_develop list_develop sort_title_min_width">
                            <div class="sort_label">排序方式：</div>
                            <a class="J_product_sort accumulative" sortFiled="tradePrice" ascContact="点击后按售价升序排列！"  descContact="点击后按售价降序排列！" currentSortStatus="Desc">售价<span class="arrowDown J-sort-span"></span>
                                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                                    <span class="arrowTop" style="margin-left:20px;"></span>
                                    <div class="alertAll">
                                        <div class="alertLeft"></div>
                                        <div class="alertBody">
                                            点击后按售价升序排列！
                                        </div>
                                        <div class="alertRight"></div>
                                    </div>
                                </div>
                            </a>

                            <span class="txtTransaction">
                                <input type="text" name="tradePriceStart" class="txt salePriceInput" style="width:42px; height:17px;" />&nbsp;至&nbsp;
                                <input type="text" name="tradePriceEnd" class="txt salePriceInput" style="width:40px; height:17px;" />
                            </span>
                            <div  class="txtList salePriceBoard" style=" left:245px; padding-top:30px;display: none">
                                <span style="cursor: pointer" onclick="cleanPriceInput(this)" class="cleanSaleBoardBtn clean">清除</span>
                                <span onclick="searchForPriceBoard()" class="saleBoardOkBtn btnSure">确定</span>
                            </div>
                            <a class="J_product_sort accumulative" sortFiled="inventoryAveragePrice" ascContact="点击后按成本价升序排列！"  descContact="点击后按成本价降序排列！" currentSortStatus="Desc">成本价<span class="arrowDown J-sort-span"></span>
                                <div class="alert_develop" style="margin:-5px 0px 0px -15px;display: none">
                                    <span class="arrowTop" style="margin-left:20px;"></span>
                                    <div class="alertAll">
                                        <div class="alertLeft"></div>
                                        <div class="alertBody">
                                            点击后按成本价升序排列！
                                        </div>
                                        <div class="alertRight"></div>
                                    </div>
                                </div>
                            </a>
                            <span class="txtTransaction">
                                <input type="text" name="inventoryAveragePriceDown" class="priceInput txt" style="width:42px; height:17px;" />&nbsp;至&nbsp;
                                <input type="text" name="inventoryAveragePriceUp" class="priceInput txt" style="width:40px; height:17px;" />
                            </span>
                            <div  class="txtList priceBoard" style=" left:450px; padding-top:30px;display: none">
                                <span style="cursor: pointer" onclick="cleanPriceInput(this)" class="clearBoardBtn clean">清除</span>
                                <span onclick="searchForPriceBoard()" class="boardOkBtn btnSure">确定</span>
                            </div>
                            <a class="J_product_sort hover" sortFiled="inventoryAmount" ascContact="点击后按库存量升序排列！" descContact="点击后按库存量降序排列！"
                               currentSortStatus="Desc">库存量<span class="arrowDown J-sort-span"></span>
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
                        </div>
                    </form:form>
                    <form:form commandName="promotionsDTO" id="promotionsProductTable" action="promotions.do?method=addPromotionsProduct" method="post" name="thisform">
                        <input type="hidden" name="id" value="${promotions.idStr}">
                        <div class="sProductIdDiv"></div>
                        <table id="addPromotionsProductTable" class="tab_cuSearch tabSales" cellpadding="0" cellspacing="0">
                            <col width="40">
                            <col>
                            <col width="80">
                            <col width="90">
                            <col width="90">
                            <col width="190">
                            <tr class="titleBg">
                                <td style="padding-left:10px;"></td>
                                <td>商品信息</td>
                                <td>库存量</td>
                                <td>成本价</td>
                                <td>售价</td>
                                <td>已参与促销</td>
                            </tr>
                            <tr class="space"><td colspan="6"></td></tr>

                        </table>
                    </form:form>
                    <div class="clear i_height"></div>
                    <!----------------------------分页----------------------------------->
                    <div class="i_pageBtn">
                        <bcgogo:ajaxPaging
                                url="promotions.do?method=getPromotionsProduct"
                                data="{
                                startPageNo:1, maxRows:30,
                                promotionsFilter:$('#promotionsFilter').val(),
                                promotionsId:$('#promotionsId').val(),
                                sort:'inventoryAmountDesc'
                                }"
                                postFn="initAddPromotionsProductTable"
                                dynamical="_promotionsProduct"/>
                    </div>
                </div>
                <div class="lineBottom"></div>
                <div class="clear i_height"></div>
                <div class="order" style="position:fixed; bottom:0;">
                    <label class="rad"><input id="allProduct" class="select_all" type="checkbox" />全选</label>
                    <span class="gray_color">已选择<span id="selectProductCount">0</span>个商品</span>
                    <div class="step">
                        <a id="preToManagePromotionsBtn" class="btn_promotion">返回上一步</a>
                        <a id="addPromotionsProductBtn" class="btn_promotion">加入活动</a>
                    </div>
                </div>

            </div>

        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>