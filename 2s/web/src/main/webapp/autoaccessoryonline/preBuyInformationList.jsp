<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-10-28
  Time: 下午12:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>求购资讯</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css">

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/preBuyInformation<%=ConfigController.getBuildVersion()%>.js"></script>
     <script type="text/javascript" src="js/page/txn/addNewProduct<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/quotedPreBuyOrderAlert<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-scrollFlow<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB_AUTOACCESSORYONLINE_BUYING_INFORMATION");
        $(function(){

            APP_BCGOGO.Net.asyncAjax({
                url: "autoAccessoryOnline.do?method=getViewedBusinessChance",
                type: "POST",
                cache: false,
                dataType: "json",
                success: function (result) {
                    if(G.isEmpty(result)||!result.success){
                        return;
                    }
                    var preBuyOrderItems=result.data;
                    if(G.isEmpty(preBuyOrderItems)){
                        $('#viewedBusinessChance').append("<li>您暂无最近浏览的商机</li>");
                        return;
                    }
                    var trStr='';
                    for(var i=0;i<preBuyOrderItems.length;i++){
                        var preBuyOrderItem=preBuyOrderItems[i];
                        var commodityCode=preBuyOrderItem.commodityCode;
                        var productName=preBuyOrderItem.productName;
                        var brand=preBuyOrderItem.brand;
                        var model=preBuyOrderItem.model;
                        var spec=preBuyOrderItem.spec;
                        var vehicleBrand=preBuyOrderItem.vehicleBrand;
                        var vehicleModel=preBuyOrderItem.vehicleModel;
                        var productInfoStr = "";
//                        if(!G.isEmpty(commodityCode)){
//                            productInfoStr += commodityCode + " ";
//                        }
                        if(!G.isEmpty(productName)){
                            productInfoStr += productName + " ";
                        }
                        if(!G.isEmpty(brand)){
                            productInfoStr += brand + " ";
                        }
                        if (!G.isEmpty(model) &&!G.isEmpty(spec)) {
                            productInfoStr += model + "/" + spec;
                        } else if (!G.isEmpty(model)) {
                            productInfoStr += model;
                        } else if (!G.isEmpty(spec)) {
                            productInfoStr += spec;
                        }
                        if(!G.isEmpty(vehicleBrand)){
                            productInfoStr += vehicleBrand + " ";
                        }
                        if(!G.isEmpty(vehicleModel)){
                            productInfoStr += vehicleModel;
                        }
                        var businessChanceTypeStr = preBuyOrderItem.businessChanceTypeStr;
                        var businessChanceType = preBuyOrderItem.businessChanceType;
                        var changeClass='';
                        switch(businessChanceType){
                            case 'Normal':
                                changeClass='yellow_color';
                                break;
                            case 'SellWell':
                                changeClass='green_color';
                                break;
                            case 'Lack':
                                changeClass='red_color';
                                break;
                            default:
                                changeClass='yellow_color'
                        }
                        var itemId=preBuyOrderItem.idStr;
                        trStr+='<li class="p-info-detail text-overflow" onclick="showBuyInformationDetail('+itemId+')" title="'+productInfoStr+'">[<a class="'+changeClass+'">'+businessChanceTypeStr+'</a>]'+productInfoStr+'</li>';
                    }
                    $('#viewedBusinessChance').append(trStr);
                },
                error:function(){
                    nsDialog.jAlert("网络异常。");
                }
            })
        });
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
<jsp:include page="autoAccessoryOnlineNavi.jsp">
    <jsp:param name="currPage" value="preBuyList"/>
</jsp:include>
<div class="titBody">
    <input type="hidden" id="shopId" value="${sessionScope.shopId}"/>
<div><img src="images/pic_flowChart.png" /></div>
<div class="accessories-container">
    <div class="accessoriesLeft">
        <div class="title">您可能感兴趣的求购</div>
        <div id="preBuyItemRecommendDiv" class="preBuy-item-recommend content JScrollFlowHorizontal" style="display: none;float: left;">
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </div>

    <div class="bodyLeft" style="width:794px;">
        <div class="clear"></div>
        <div class="clear i_height"></div>
        <div style="width: 785px" class="lineTitle">
            求购信息查询
        </div>
        <div style="width: 765px" class="lineBody lineAll">
            <form:form commandName="orderSearchConditionDTO" id="searchConditionForm" action="preBuyOrder.do?method=getPreBuyInformationList" method="post">
                <input type="hidden" name="maxRows" id="maxRows" value="15">
                <form:hidden path="shopId"/>
                <div class="divTit" style="padding-right:0px;">
                    <span class="spanName">求购商品信息&nbsp;</span>
                    <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="searchWord" searchField="product_info" initialValue="品名/品牌/规格/型号/适用车辆" cssStyle="width:140px;"/>
                    <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productName" searchField="product_name" initialValue="品名" cssStyle="width:65px;"/>
                    <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productBrand" searchField="product_brand" initialValue="品牌/产地" cssStyle="width:65px;"/>
                    <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productSpec" searchField="product_spec" initialValue="规格" cssStyle="width:65px;"/>
                    <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productModel" searchField="product_model" initialValue="型号" cssStyle="width:65px;"/>
                    <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productVehicleBrand" searchField="product_vehicle_brand" initialValue="车辆品牌" cssStyle="width:65px;"/>
                    <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="productVehicleModel" searchField="product_vehicle_model" initialValue="车型" cssStyle="width:65px;"/>
                    <form:input cssClass="txt J-productSuggestion J-initialCss J_clear_input" path="commodityCode" searchField="commodity_code" initialValue="商品编号" cssStyle="text-transform: uppercase;width:60px;"/>

                </div>
                <div class="divTit">
                    <span class="spanName">所在区域&nbsp;</span>
                    <select class="txt" style="width:130px;height: 20px;color:#ADADAD" id="provinceNo" name="provinceNo">
                        <option class="default" style="color:#ADADAD" value="">--请选择省份--</option>
                        <option class="default" style="color:#000000" value="">全国</option>
                    </select>&nbsp;&nbsp;
                    <select class="txt" style="width:130px;height: 20px;color:#ADADAD" id="cityNo" name="cityNo">
                        <option class="default" style="color:#ADADAD" value="">--请选择城市--</option>
                        <option class="default" style="color:#000000" value="">全省</option>
                    </select>&nbsp;&nbsp;
                    <select class="txt" style="width:130px;height: 20px;color:#ADADAD" id="regionNo" name="regionNo">
                        <option class="default" style="color:#ADADAD" value="">--请选择区域--</option>
                        <option class="default" style="color:#000000" value="">全市</option>
                    </select>
                </div>
                <div class="divTit" style="padding-left:19px;width:500px">
                    <span>求购买家&nbsp;</span>
                    <form:input  path="shopName" cssClass="txt J_supplierOnlineSuggestion J-bcgogo-droplist-on" value="${orderSearchConditionDTO.shopName}"  cssStyle="width:250px;"/>
                </div>
                <div class="divTit button_conditon button_search">
                    <a class="blue_color clean" style="float:right;" id="clearConditionBtn">清空条件</a>
                    <a class="button" style="float:right;" id="searchConditionBtn">搜 索</a>
                </div>
            </form:form>
        </div>
        <div style="width: 795px" class="lineBottom"></div>
        <div class="clear i_height"></div>
        <div class="cuSearch">
            <div style="width: 785px" class="lineTitle">
                求购信息列表
            </div>
            <div style="width: 775px" class="cartBody lineBody">
                <table style="width: 775px" class="tab_cuSearch" cellpadding="0" cellspacing="0" id="preBuyInformationTable">
                    <col width="220">
                    <col width="75">
                    <col width="130">
                    <col>
                    <col width="105">
                    <col width="70">
                    <tr class="titleBg J_title">
                        <td style="padding-left:10px;">求购商品</td>
                        <td>数量</td>
                        <td>求购买家</td>
                        <td>求购所在地</td>
                        <td>有效截止期</td>
                        <td></td>
                    </tr>
                    <tr class="space J_title"><td colspan="6"></td></tr>
                </table>
                <div class="clear i_height"></div>
                <!----------------------------分页----------------------------------->
                <jsp:include page="/common/pageAJAX.jsp">
                    <jsp:param name="url" value="preBuyOrder.do?method=getPreBuyInformationList"></jsp:param>
                    <jsp:param name="jsHandleJson" value="drawPreBuyInformationTable"></jsp:param>
                    <jsp:param name="dynamical" value="preBuyInformation"></jsp:param>
                    <jsp:param name="display" value="none"></jsp:param>
                </jsp:include>
            </div>
            <div class="lineBottom" style="width: 795px;"></div>
        </div>
    </div>

    <div class="height"></div>
</div>
<div class="accessories-right" style="margin-top:15px;">
    <div class="accessoriesRight">
        <div class="title"><span class=" red_color">本周活跃度统计</span></div>
        <div class="clear"></div>
        <div class="content" style="padding-left:10px;">
            <div class="product-line">
                求购信息共发布 <a class="yellow_color"><strong>${normalBusinessChanceNum}</strong></a> 条
                <br />
                缺料信息共发布<a class="yellow_color"><strong> ${lackBusinessChanceNum}</strong></a> 条
                <br />
                畅销品信息共发布 <a class="yellow_color"><strong>${sellWellBusinessChanceNum}</strong></a> 条
                <div style="height:30px; border-top:#ccc 1px dashed; line-height:30px; margin-right:10px;">所有参与报价商家共 <a class="yellow_color"><strong>${quotedPreBuyOrderNum}</strong></a>家</div>
            </div>
            <div class="clear"></div>
        </div>
    </div>
    <div class="accessoriesRight">
        <div class="title">我要卖配件</div>
        <div class="clear"></div>
        <div class="content">
            <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.GOODS_IN_OFF_SALES_MANAGE">
                <a class="button-label" href="goodsInOffSales.do?method=toUnInSalingGoodsList">我要上架</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.PROMOTIONS_MANAGER_RENDER">
                <a class="button-label" href="promotions.do?method=toPromotionsList">我要促销</a>
            </bcgogo:hasPermission>
            <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.MY_QUOTEDPREBUYORDER">
                <a class="button-label" href="preBuyOrder.do?method=quotedPreBuyOrderManage">我的报价</a>
            </bcgogo:hasPermission>
            <a class="button-label" href="orderCenter.do?method=showOrderCenter">订单中心</a>
            <div class="clear"></div>
            <div class="lineHeight30">在线销售订单：共<a class="blue_color number" <c:if test="${orderCenterDTO.saleNew<=0}"> onclick="javascript:return false;"</c:if> href="orderCenter.do?method=getTodoOrders&type=TODO_SALE_ORDERS&currTab=NEW">${orderCenterDTO.saleNew}</a> 条新订单<br />
                在线销售退货：共<a class="blue_color number" <c:if test="${orderCenterDTO.saleReturnNew<=0}"> onclick="javascript:return false;"</c:if> href="onlineSalesReturnOrder.do?method=toOnlineSalesReturnOrder&orderStatus=PENDING">${orderCenterDTO.saleReturnNew}</a> 条待处理</div>
        </div>
    </div>
    <div class="accessoriesRight">
        <div class="title">您最近浏览过的商机</div>
        <div class="clear"></div>
        <div class="content">
            <ul class="ul-04" id="viewedBusinessChance">
             </ul>
            <div class="clear"></div>
        </div>
    </div>
    <div class="accessoriesRight">
        <div class="title">我是卖家</div>
        <div class="clear"></div>
        <div class="content">
            <div class="list"><a class="blue_color questionMark">如何参与报价</a></div>
            <div class="list"><a class="titNum">1</a><span style="color: #272727;">查看您感兴趣的求购信息</span></div>
            <div class="list"><a class="titNum">2</a><span style="color: #272727;">提交您的报价信息</span></div>
            <div class="list"><a class="titNum">3</a><span style="color: #272727;">获得买家联系方式并与其交易</span></div>
        </div>
    </div>
    <div class="accessoriesRight">
        <div class="title">我是卖家</div>
        <div class="clear"></div>
        <div class="content">
            <div class="list"><a class="blue_color questionMark">如何快速采购</a></div>
            <div class="list"><a class="titNum">1</a><span style="color: #272727;">发布求购信息</span></div>
            <div class="list"><a class="titNum">2</a><span style="color: #272727;">等待卖家报价</span></div>
            <div class="list"><a class="titNum">3</a><span style="color: #272727;">选择卖家报价直接采购下单</span></div>
        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
    <%@ include file="./alert/quotedPreBuyOrderAlert.jsp" %>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>