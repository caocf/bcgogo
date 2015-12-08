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
    $(".sProductIdDiv span").each(function(){
        productIdArr.push($(this).attr("productId"));
    });
    return productIdArr;
}



function reGenerateSProductIdDiv(){
    $(".sProductIdDiv span").remove();
    var promotionsId=$("#promotionsId").val();
    $(".itemChk:checked").each(function(i){
        var productId=$(this).attr("productId");
        var _tr=$(this).closest("tr");
        var discount_amount=G.round(_tr.find(".discount_amount").val());
        var bargainType=G.normalize(_tr.find(".bargainType").val());
        var $limit_amount=$(_tr.find(".limit_amount"));
        var limit_amount=G.round($limit_amount.val());
        var limit_flag = true;
        if($limit_amount.attr("disabled")){
            limit_amount=-1;
            limit_flag = false;
        }
        var bargainNode='<span productId="'+productId+'">';
        bargainNode+= '<input type="hidden" name="promotionsProductDTOList['+i+'].productLocalInfoId" class="productId" value="'+productId+'"/>';
        bargainNode+= '<input type="hidden" name="promotionsProductDTOList['+i+'].promotionsId"  value="'+promotionsId+'"/>';
        bargainNode+= '<input type="hidden" name="promotionsProductDTOList['+i+'].discountAmount" class="discount_amount" value="'+discount_amount+'"/>';
        bargainNode+= '<input type="hidden" name="promotionsProductDTOList['+i+'].bargainType" class="bargainType" value="'+bargainType+'"/>';
        bargainNode+= '<input type="hidden" name="promotionsProductDTOList['+i+'].promotionsType"  value="BARGAIN"/>';
        bargainNode+= '<input type="hidden" name="promotionsProductDTOList['+i+'].limitFlag" class="limit_flag" value="'+limit_flag+'"/>';
        bargainNode+= '<input type="hidden" name="promotionsProductDTOList['+i+'].limitAmount" class="limit_amount" value="'+limit_amount+'"/>';
        bargainNode+='</span>';
        $(".sProductIdDiv").append(bargainNode);
    });

}

function _initAddedBargainProduct(ppList){
    if(G.isEmpty(ppList)){
        return;
    }
    for(var i=0;i<ppList.length;i++){
        var pp=ppList[i];
        var productId=pp.productLocalInfoIdStr;
        var $item=$(".productItem").find("input[productId='"+productId+"']");
        if(!G.isEmpty($item)){
            $item.attr("checked",true);
            var $targetTr=$item.closest("tr");
            $targetTr.find(".discount_amount").val(G.rounding(pp.discountAmount));
            $targetTr.find(".bargainTypeSelector").val(pp.bargainType);
            $targetTr.find(".bargainType").val(pp.bargainType);
            if(G.rounding(pp.limitAmount)>0){
                $targetTr.find(".limit_amount").val(G.rounding(pp.limitAmount));
                $targetTr.find(".limit_amount").attr("disabled",false);
                $targetTr.find(".limitFlagChk").attr("checked",true);
            }
        }
    }
    $("#selectProductCount").text($(".itemChk:checked").length);
}

$().ready(function(){

    $(".itemChk").live("click",function(event){
        var $target=$(event.target);
        var productId=$target.attr("productId");
        if($target.attr("checked")){
            if(!arrayUtil.contains(getSProductIdArr(),productId)){
                var index=G.rounding($(".sProductIdDiv").attr("index"));
                index++;
                $(".sProductIdDiv").append( '<input type="hidden"  value="'+productId+'"/>');
                $(".sProductIdDiv").attr("index",index);
            }
        }else{
            $(".sProductIdDiv input[value='"+productId+"']").remove();
        }
        var checkedCount = $(".itemChk:checked").length;
        $("#allProduct").attr("checked",$(".itemChk").length == checkedCount ? true : false);
        $("#selectProductCount").text(checkedCount);
    });

    //点击加入活动
    $("#addBargainProductBtn").live("click",function(){
        var promotionsId=$("#promotionsId").val();
        if(G.isEmpty(promotionsId)){
            GLOBAL.error("促销id异常，无法添加商品!");
            return;
        }
        reGenerateSProductIdDiv();
        var checkedHasValue = true;

        $(".productChk:checked").each(function(){
            var $target = $(this);
            var bargainType=$target.closest("tr").find(".bargainType").val();
            var discount =G.rounding($target.closest("tr").find(".discount_amount").val());
            if(bargainType=="DISCOUNT"){
                if(discount<=0||discount>=10){
                    nsDialog.jAlert("折扣应是0到10的数字。");
                checkedHasValue = false;
                return false;
            }
            }
            if(bargainType=="BARGAIN"){
                if(discount<=0){
                    nsDialog.jAlert("特价应大于0。");
                    checkedHasValue = false;
                    return false;
                }
            }
            var limitAmount =G.rounding($target.closest("tr").find(".limit_amount").val());
            if($target.closest("tr").find(".limitFlagChk").attr("checked")){
                if(limitAmount<=0){
                    nsDialog.jAlert("限购数量应大于0。");
                    checkedHasValue = false;
                    return false;
                }
            }
        });
        if(!checkedHasValue){
//            nsDialog.jAlert("请填写特价设置具体信息！");
            return;
        }

        $(".J-initialCss").placeHolder("clear");
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
                $(".J-initialCss").placeHolder("reset");
            },
            error:function(){
                nsDialog.jAlert("网络异常！");
                $(".J-initialCss").placeHolder("reset");
            }
        });

    });

    $(".discount_amount").live("keyup", function(){
        var $target = $(this);
        $target.val(G.round($target.val()));
        if($target.closest("td").find(".bargainType").val()=="DISCOUNT"){
            var tVal=G.rounding($target.val());
            if(tVal<0||tVal>=10){
                $target.val(0);
            }
        }
    }).live("blur", function(){
                var $target = $(this);
                if($target.closest("td").find(".bargainType").val() == 'BARGAIN'){
                    var tVal = G.round($target.val());
                    var originPrice = $target.parents("tr").find(".J-originPrice").text();
                    if(tVal>=parseFloat(originPrice)){
                        nsDialog.jAlert("特价不可大于或等于原价！请重新设置。");
                        $target.val("");
                    }
                }
            });
});

function initAddBargainProductTable(result){
    $("#addPromotionsProductTable tr:gt(1)").remove();
    if(G.isEmpty(result)){
        return;
    }
    var products=result.results;
    if(G.isEmpty(products)){
        return;
    }
    var tr;
    for(var i=0;i<products.length;i++){
        var product=products[i];
        var productInfo=generateProductInfo(product);
        var inventoryNum=G.rounding(product.inventoryNum)+G.normalize(product.sellUnit);
        var inventoryAveragePrice=G.rounding(product.inventoryAveragePrice);
        var inSalesPrice=G.rounding(product.inSalesPrice);
        var productId=G.normalize(product.productLocalInfoIdStr);

        var promotions=product.promotionsDTOs;
        var promotionsing=!G.isEmpty(promotions);  //判断商品是否正在促销
        if(promotionsing){
            var pTitle=generatePromotionsAlertTitle(product);
            productInfo += pTitle;
        }

        tr+='<tr class="productItem titBody_Bg">';
        tr+='<td style="padding-left:10px;"><input class="itemChk productChk" type="checkbox" productId="'+productId+'"/>';
        tr+='<input type="hidden" field="'+productId+'"  value="'+productId+'"></td>';
        tr+='<td class="promotions_info_td">'+productInfo+'</td>'
        tr+='<td>'+inventoryNum+'</td>'
        tr+='<td>'+'<span class="arialFont">&yen;</span>'+inventoryAveragePrice+'</td>'
        tr+='<td>'+'<span class="arialFont">&yen;</span><span class="J-originPrice">'+inSalesPrice+'</span></td>'
        tr+='<td class="bargainTD"><input class="J-initialCss txt discount_amount"  type="text" style="width:60px;" initialValue="现价" value="现价">';
        tr+='<input type="hidden" class="bargainType"  value="BARGAIN"/><select class="bargainTypeSelector" style="margin-left:5px"><option value="BARGAIN">金额</option><option value="DISCOUNT">折扣</option></select>';
        tr+='<label style="margin-left:5px" class="rad"><input type="checkbox" class="limitFlagChk">每位客户限购</label><input disabled="true" class="limit_amount price-input J-initialCss txt" type="text" style="width:60px;" initialValue="数量" value="数量">件 </td>';
        tr+='</tr>';
        tr+='<tr class="titBottom_Bg"><td colspan="8"></td></tr>';
    }
    $("#addPromotionsProductTable").append(tr);
    $(".J-initialCss").placeHolder();

    if(!G.isEmpty($("#promotionsId").val())){
        APP_BCGOGO.Net.asyncAjax({
            url: "promotions.do?method=getAddedBargainProduct",
            data:{promotionsId:$("#promotionsId").val()},
            type: "POST",
            cache: false,
            dataType: "json",
            success: function (json) {
                _initAddedBargainProduct(json);
            },
            error:function(){
                nsDialog.jAlert("网络异常！");
            }
        });
    }
}

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
                <input type="hidden" id="promotionsType" name="promotionsType" value="${promotions.type}" />

                <div class="lineTop"></div>
                <div class="cartBody lineBody">
                    <input type="hidden" id="promotionStatus" value="${promotions.status}">
                    <form:form commandName="promotionsDTO" id="searchPromotionsProductForm" action="promotions.do?method=addPromotionsProduct" method="post" name="thisform">
                        <input type="hidden" id="sortStatus" name="sort" value="inventoryAmountDesc"/>
                        <%--<input type="hidden" id="promotionsIdList" name="promotionsIdList" value="${promotions.idStr}">--%>
                        <input type="hidden" id="promotionsFilter" name="promotionsFilter" value="add_promotions_product_current"/>
                        <input type="hidden" id="promotionsId" name="promotionsId" value="${promotions.idStr}">
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
                                    <label class="rad">
                                        <input id="promotionsProductFilter" type="checkbox" />过滤已参加当前活动的商品
                                    </label>
                                </div>
                            </div>
                            <div class="divTit button_conditon button_search">
                                <a id="resetConditionBtn" class="blue_color clean">清空条件</a>
                                <a id="searchPromotionsProductBtn" pageType="addBargainProduct" class="button">搜 索</a>
                            </div>
                        </div>
                        <div class="i_height"></div>
                        <div class="line_develop list_develop sort_title_min_width">
                            <div class="sort_label">排序方式：</div>
                                <%--<span class="txtTransaction" style="padding-left:10px;">排序方式</span>--%>
                            <a class="J_product_sort accumulative" sortFiled="tradePrice"
                               ascContact="点击后按售价升序排列！" descContact="点击后按售价降序排列！"
                               currentSortStatus="Desc">售价<span class="arrowDown J-sort-span"></span>

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
                            <a class="J_product_sort accumulative hover" sortFiled="inventoryAmount"
                               ascContact="点击后按库存量升序排列！" descContact="点击后按库存量降序排列！"
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
                    <form:form commandName="promotionsDTO" id="promotionsProductTable" action="promotions.do?method=savePromotions" method="post" name="thisform">
                        <input type="hidden" name="id" value="${promotions.idStr}">
                        <div class="sProductIdDiv"></div>
                        <table id="addPromotionsProductTable" class="tab_cuSearch tabSales" cellpadding="0" cellspacing="0">
                            <col width="40">
                            <col>
                            <col width="60">
                            <col width="70">
                            <col width="70">
                            <col width="330">
                            <tr class="titleBg">
                                <td style="padding-left:10px;"></td>
                                <td>商品信息</td>
                                <td>库存量</td>
                                <td>成本价</td>
                                <td>原售价</td>
                                <td>特价设置</td>
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
                                postFn="initAddBargainProductTable"
                                dynamical="_promotionsProduct"/>
                    </div>
                </div>
                <div class="lineBottom"></div>
                <div class="clear i_height"></div>
                <div class="order" style="position: fixed;bottom: 0;">
                    <label class="rad"><input id="allProduct" class="select_all" type="checkbox" />全选</label>
                    <span class="gray_color">已选择<span id="selectProductCount">0</span>个商品</span>
                    <div class="step">
                        <a id="preToManagePromotionsBtn" class="btn_promotion">返回上一步</a>
                        <a id="addBargainProductBtn" class="btn_promotion">加入活动</a>
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