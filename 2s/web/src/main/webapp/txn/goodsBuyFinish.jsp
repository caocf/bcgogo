<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>采购单</title>
<%
	boolean storageBinTag = ServiceManager.getService(IShopConfigService.class).isStorageBinSwitchOn(WebUtil.getShopId(request));//选配仓位功能 默认开启这个功能false
	boolean tradePriceTag = ServiceManager.getService(IShopConfigService.class).isTradePriceSwitchOn(WebUtil.getShopId(request),WebUtil.getShopVersionId(request));//选配批发价功能
%>
<link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/draftOrder<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/carWashFinish<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/cuxiao<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/accept<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" href="js/components/themes/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.css">

<link rel="stylesheet" type="text/css" href="styles/goodsBuyFinish<%=ConfigController.getBuildVersion()%>.css"/>
<c:choose>
        <c:when test="<%=storageBinTag%>">
           <link rel="stylesheet" type="text/css" href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
        </c:when>
        <c:otherwise>
           <link rel="stylesheet" type="text/css" href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
        </c:otherwise>
    </c:choose>
     <c:choose>
        <c:when test="<%=tradePriceTag%>">
           <link rel="stylesheet" type="text/css" href="styles/tradePriceOn<%=ConfigController.getBuildVersion()%>.css"/>
        </c:when>
        <c:otherwise>
           <link rel="stylesheet" type="text/css" href="styles/tradePriceOff<%=ConfigController.getBuildVersion()%>.css"/>
        </c:otherwise>
    </c:choose>
<style type="text/css">
  .item input {
    text-overflow: ellipsis;
  }
.sale-memo{
    text-align:left;
    display:inline-block;
    width:870px;
    word-break:break-all;
    word-wrap: break-word;
    line-height:1.5;
}

</style>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
     <c:choose>
        <c:when test="<%=storageBinTag%>">
           <link rel="stylesheet" type="text/css" href="styles/storageBinOn<%=ConfigController.getBuildVersion()%>.css"/>
        </c:when>
        <c:otherwise>
           <link rel="stylesheet" type="text/css" href="styles/storageBinOff<%=ConfigController.getBuildVersion()%>.css"/>
        </c:otherwise>
    </c:choose>
     <c:choose>
        <c:when test="<%=tradePriceTag%>">
           <link rel="stylesheet" type="text/css" href="styles/tradePriceOn<%=ConfigController.getBuildVersion()%>.css"/>
        </c:when>
        <c:otherwise>
           <link rel="stylesheet" type="text/css" href="styles/tradePriceOff<%=ConfigController.getBuildVersion()%>.css"/>
        </c:otherwise>
    </c:choose>
    <style type="text/css">
        .item input {
            text-overflow: ellipsis;
        }
    </style>

<%@include file="/WEB-INF/views/header_script.jsp" %>
<script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
<script type="text/javascript" src="js/draftOrderBox<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/txn<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/messagePrompt<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/goodsBuy<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/inventorySearchIndex<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/supplierSearch<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/checkMobileAndTelphone<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/setLimit<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/autoaccessoryonline/promotionsUtil<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/page/txn/goodsBuyFinish<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/bcgogo-ratting<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript" src="js/components/ui/rattingComments<%=ConfigController.getBuildVersion()%>.js"></script>
<script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid,"WEB.TXN.PURCHASE_MANAGE.PURCHASE");
    defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
//单据操作日志
$().ready(function(){
    if(!G.Lang.isEmpty($("#errorMsg").val())){
        nsDialog.jAlert($("#errorMsg").val());
    }
    var orderId = jQuery("#id").val();
    APP_BCGOGO.Net.asyncPost({
        url: "txn.do?method=showOperationLog",
        data: {
            orderId: orderId,
            orderType: "purchase"
        },
        cache: false,
        dataType: "json",
        success: function(result) {
            if(result && result.length>0){
                for(var i = 0; i < result.length; i++) {
                    var creationDateStr = result[i].creationDateStr == null ? "" : result[i].creationDateStr;
                    var userName = result[i].userName == null ? "" : result[i].userName;
                    var content = result[i].content == null ? "" : result[i].content;
                    var tr = '<tr>';
                    tr += '<td style="border-left:none;padding-left:10px;"><span>' + (i + 1) + '&nbsp;</span></td>';
                    tr += '<td>' + creationDateStr + '</td>';
                    tr += '<td>' + userName + '</td>';
                    tr += '<td>' + content + '</td>';
                    $("#operationLog_tab").append($(tr));
                }
            }
        }
    });
});

  $(document).ready(function () {
    $("#purchaseOrderForm input").bind("mouseover", function () {
      this.title = this.value;
    })
            $(document).click(function (event) {
                if ($(event.target).attr("id") !== "div_brand") {
                    $("#div_brand").css("display", "none");
                }
            });
            //todo zhangjuntao
            $(".itemAmount,.itemPrice").bind("blur", function (event) {
                setTotal();
            });

            // 初始化 datepicker
            $("#deliveryDateStr")
                    .datepicker({
                        "numberOfMonths":1,
                        "showButtonPanel":true,
                        "changeYear":true,
                        "changeMonth":true,
                        "yearRange":"c-100:c+100",
                        "yearSuffix":"",
                        "showHour":false,
                        "showMinute":false
                    })
                    .bind("click", function (event) {
                        $(this).blur();
                    });


            $("#billProducer").live("click", function (event) {
                var obj = this;
                $.ajax({
                            type:"POST",
                            url:"member.do?method=getSaleMans",
                            async:true,
                            data:{
                                now:new Date()
                            },
                            cache:false,
                            dataType:"json",
                            error:function (XMLHttpRequest, error, errorThrown) {
                                $("#div_serviceName").css({'display':'none'});
                            },
                            success:function (jsonObject) {
                                initSaleMan(obj, jsonObject);
                            }
                        }
                );
            });

//          jQuery("#billProducer").live("blur",function(){
//            jQuery("#div_serviceName").fadeOut();
//          });
            $(document).click(function (e) {
                var e = e || event;
                var target = e.srcElement || e.target;
                if (target.id != "div_serviceName" && target.id != "Scroller-Container_ServiceName") {
                    $("#div_serviceName").hide();
                }
            });

            $("#deleteBillProducer")
                    .hide()
                    .live("click", function () {
                        $("#billProducer").val("");
                        $("#billProducerId").val("");
                        $("#deleteBillProducer").hide();
                    });

            $("#billProducerDiv")
                    .mouseenter(function () {
                        if ($("#billProducer").val() && !isDisable()) {
                            $("#deleteBillProducer").show();
                        }
                    })
                    .mouseleave(function () {
                        $("#deleteBillProducer").hide();
                    });

  });


        function initSaleMan(domObject, jsonObject) {
            var offset = $(domObject).offset();
            var offsetHeight = $(domObject).height();
            var offsetWidth = $(domObject).width();
            var domTitle = domObject.name;
            var position = domObject.getBoundingClientRect();
            var x = position.left;
            var y = position.top;
            var selectmore = jsonObject.length;
            if (selectmore <= 0) {
                $("#div_serviceName").css({"display":"none"});
            }
            else {
                $("#div_serviceName").css({
                    "display":"block", "position":"absolute",
                    "left":x + "px",
                    "top":y + offsetHeight + 4 + "px",
                    "width":"80px",
                    "overflowY":"scroll",
                    "overflowX":"hidden"
                });
                $("#Scroller-Container_ServiceName").html("");

                for (var i = 0; i < jsonObject.length; i++) {
                    var id = jsonObject[i].idStr;
                    var a = $("<a id=" + id + "></a>");
                    a.css("color", "#000")
                            .html(jsonObject[i].name + "<br>")
                            .bind("mouseenter", function () {
                                $("#Scroller-Container_ServiceName > a").removeAttr("class");
                                $(this).attr("class", "hover");
                                // jQuery(this).html();
                                selectValue = jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].name;
                                selectItemNum = parseInt(this.id.substring(10));
                            })
                            .click(function () {
                                var sty = this.id;
                                $("#billProducerId").val(sty);
                                //取的第一字符串
                                $(domObject).val(selectValue = jsonObject[$("#Scroller-Container_ServiceName > a").index($(this)[0])].name);
                                selectItemNum = -1;
                            });
                    $("#Scroller-Container_ServiceName").append(a);
                }
            }
        }
        function addNewPurchaseOrder() {
            window.open($("#basePath").val() + "RFbuy.do?method=create", "_blank");
        }
    </script>
    <base href="<%=basePath%>">
</head>

<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<bcgogo:permissionParam permissions="WEB.TXN.INVENTORY_MANAGE.ALARM_SETTINGS">
<input type="hidden" id="permissionInventoryAlarmSettings" value="${permissionParam1}"/>
</bcgogo:permissionParam>
<input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
<div class="i_main">
<jsp:include page="txnNavi.jsp">
	<jsp:param name="currPage" value="purchase"/>
</jsp:include>
<jsp:include page="purchaseNavi.jsp">
	<jsp:param name="currPage" value="goodsBuy"/>
</jsp:include>

<div class="i_mainRight" id="i_mainRight">
<form:form commandName="purchaseOrderDTO" id="purchaseOrderForm" action="RFbuy.do?method=save" method="post" name="thisform">
<jsp:include page="unit.jsp"/>
<form:hidden path="shopId" value="${sessionScope.shopId}"/>
<form:hidden path="supplierShopId" value="${purchaseOrderDTO.supplierShopId}"/>
<form:hidden path="id" value="${purchaseOrderDTO.id}" cssClass="checkStringEmpty J-orderId"/>
<form:hidden path="status" value="${purchaseOrderDTO.status}"/>
<form:hidden path="draftOrderIdStr" value="${purchaseOrderDTO.draftOrderIdStr}"/>
<input id="type" name="type" type="hidden" value="${param.type}">
<input id="orderType" name="orderType" value="purchaseOrder" type="hidden"/>
<input type="hidden" id="errorMsg" value="${errorMsg}">
<input id="isAddContent" name="isAddContent" value="${purchaseOrderDTO.addContent}" type="hidden"/>

    <ul class="yinye_title clear">
        <li class="danju" style="width:250px;">
            单据号： <strong>${purchaseOrderDTO.receiptNo} <a href="javascript:showOrderOperationLog('${purchaseOrderDTO.id}','purchase')"  class="blue_col">操作记录</a></strong>
            <form:hidden path="supplierId" value="${purchaseInventoryDTO.supplierId}"/>
        </li>
        <c:if test="${!empty purchaseOrderDTO.supplierShopId}">
            <li class="danju" style="text-align: left;width: 50px">
                <a class="connect">在线</a>
            </li>
        </c:if>

        <c:if test="${!empty purchaseOrderDTO.saleOrderReceiptNo}">
        <li class="danju">
            供货单号： <strong>${purchaseOrderDTO.saleOrderReceiptNo}</strong>
        </li>
        </c:if>
        <li class="danju">
            单据状态： <strong>${purchaseOrderDTO.status.name}</strong>
        </li>
    </ul>

    <div class="clear"></div>
    <div class="tuihuo_tb">
        <table >
            <col width="80"/>
            <tr>
                <td>供应商信息</td>
            </tr>
        </table>
        <table class="clear" id="tb_tui">
            <col width="80"/>
            <col width="200"/>
            <col width="70"/>
            <col width="100"/>
            <col width="70"/>
            <col width="100"/>
            <col width="100"/>
            <col width="180"/>
            <col/>
            <tr>
                <td class="td_title">供应商</td>
                <td><span style="width:160px;float:left;margin-left:5px;" class="ellipsis">${purchaseOrderDTO.supplier}</span>
                  <bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_COMMENT.SAVE">
                    <c:if
                        test="${purchaseOrderDTO.supplierCommentRecordIdStr == null && purchaseOrderDTO.supplierShopId!=null && purchaseOrderDTO.purchaseInventoryId!=null }">
                      <input id="commentSupplier" name="commentSupplier" value="commentSupplier" type="hidden"/>
                      <a class="reviews" href="javascript:showSupplierComment()">点评</a>
                    </c:if>
                  </bcgogo:hasPermission>
                    <c:if test="${not empty supplierDTO.supplierShopId}">
                    <a class="icon_online_shop" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=${supplierDTO.supplierShopId}"></a>
                    <a class="J_QQ" data_qq="${supplierShop.qqArray}"></a>
                    </c:if>
                </td>
                <td class="td_title">座机</td>
                <td>${purchaseOrderDTO.landline}</td>
                <td class="td_title">联系人</td>
                <td>${purchaseOrderDTO.contact}</td>
                <td class="td_title">联系电话</td>
                <td>${purchaseOrderDTO.mobile}</td>
            </tr>
            <tr>
                <td class="td_title">地址</td>
                <td colspan="5">${purchaseOrderDTO.address}</td>
                <td class="td_title">当前应收应付</td>
                <td class="qian_red">
                    <div class="pay" id="duizhan" style="text-align:left;margin-left: 25px">
                        <c:choose>
                            <c:when test="${supplierRecordDTO.debt >0}">
                                <span class="red_color payMoney">收¥${supplierRecordDTO.debt}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="gray_color fuMoney">收¥0</span>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${totalPayable >0}">
                                <span class="green_color fuMoney">付¥${totalPayable}</span>
                            </c:when>
                            <c:otherwise>
                                <span class="gray_color fuMoney">付¥0</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </td>
            </tr>
        </table>
         <table width="983">
            <col width="80"/>
            <col width="80"/>
            <tr>
                <td>采购信息</td>
                <td style="width:20%;">制单人：${purchaseOrderDTO.billProducer}</td>
                <td style="width:20%;">采购日期：${purchaseOrderDTO.vestDateStr}</td>
                <td style="width:20%;">预计交货日期：${purchaseOrderDTO.deliveryDateStr}</td>
                <td style="width:20%;">
	                <c:if test="${!empty purchaseOrderDTO.preDispatchDateStr}">
		                卖家发货日期：${purchaseOrderDTO.preDispatchDateStr}
	                </c:if>
                </td>
                <td>
                    <c:if test="${!empty purchaseOrderDTO.supplierShopId}">
                        <a id="showOperationLog" href="javascript:showOperationLog()">查看操作记录</a>
                    </c:if>
                </td>
            </tr>
        </table>
        <table class="clear" id="tb_tui">
            <col width="75"/>
            <col />
            <col width="70"/>
            <col width="95"/>
            <c:if test="${purchaseOrderDTO.supplierShopId!=null}">
                <col width="60">
            </c:if>
            <col width="70"/>
            <col width="80"/>
            <col width="80"/>
            <col width="95"/>
            <col width="95"/>
            <col width="50"/>
            <tr  class="tab_title" >
                <td>商品编号</td>
                <td>品名</td>
                <td>品牌/产地</td>
                <td>规格</td>
                <td>型号</td>
                <td>车型</td>
                <td>车辆品牌</td>
                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
                    <c:choose>
                        <c:when test="${purchaseOrderDTO.supplierShopId!=null}">
                            <td>上架价格</td>
                            <td>成交价</td>
                        </c:when>
                        <c:otherwise>
                            <td>单价</td>
                        </c:otherwise>
                    </c:choose>
                </bcgogo:hasPermission>
                <td>采购量/单位</td>
                <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
                    <td>小计</td>
                </bcgogo:hasPermission>
            </tr>
            <c:forEach items="${purchaseOrderDTO.itemDTOs}" var="itemDTO" varStatus="status">
                <tr class="item">
                    <td>
                        <span style="display:none" class="j_supplierProductId">${itemDTO.supplierProductId}</span>
                        ${itemDTO.commodityCode!=null?itemDTO.commodityCode:""}
                    </td>
                    <td>
                        ${itemDTO.productName!=null?itemDTO.productName:""}
                    </td>
                    <td>
                        ${itemDTO.brand!=null?itemDTO.brand:""}
                    </td>
                    <td>
                        ${itemDTO.spec!=null?itemDTO.spec:''}
                    </td>
                    <td>
                        ${itemDTO.model!=null?itemDTO.model:""}
                    </td>
                    <td>
                        ${itemDTO.vehicleModel!=null?itemDTO.vehicleModel:''}
                    </td>
                    <td>
                        ${itemDTO.vehicleBrand!=null?itemDTO.vehicleBrand:''}
                    </td>
                     <bcgogo:permission>
                    <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
                        <c:choose>
                            <c:when test="${purchaseOrderDTO.supplierShopId!=null}">
                                <td>
                                    <span class='${itemDTO.price!=itemDTO.quotedPrice?"oldPrice":""}'><fmt:formatNumber value="${itemDTO.quotedPrice}" pattern="#.##"/></span>
                                </td>
                                <c:choose>
                                    <c:when test="${itemDTO.quotedPreBuyOrderItemId!=null}">
                                        <td class="quotedPreBuyPrice"><span style="color:#FF6700;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></span></td>
                                    </c:when>
                                    <c:when test="${!itemDTO.customPriceFlag && not empty itemDTO.promotionsId && itemDTO.quotedPreBuyOrderItemId==null}">
                                        <td class="promotionPrice J-priceInfo">
                                            <span class="clearfix">
                                            <em class="cuxi">
                                                <span style="color:#FF6700;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></span>
                                            </em>
                                            <div class="alert" style="display: none">
                                                <div class="ti_top"></div>
                                                <div class="ti_body alertBody">
                                                    <%--<div style="font-weight:bold;">活动截止：<span style="color: #FF5113;">${itemDTO.promotionsDTO.endTimeCNStr==null?"不限期":itemDTO.promotionsDTO.endTimeCNStr}</span>
                                                    </div>
                                                    <c:forEach items="${itemDTO.promotionsDTO.promotionsRuleDTOList}" var="promotionsRuleDTO">
                                                        <div>
                                                            满
                                                            <span class="green_color">${promotionsRuleDTO.minAmountStr}</span>件，单价打
                                                            <span style="color: #CB0000;">${promotionsRuleDTO.discountAmountStr}</span>折
                                                        </div>
                                                    </c:forEach>--%>
                                                </div>
                                                <div class="ti_bottom"></div>
                                            </div>
                                            </span>
                                       </td>
                                    </c:when>
                                    <c:when test="${itemDTO.customPriceFlag && itemDTO.price!=itemDTO.quotedPrice && itemDTO.quotedPreBuyOrderItemId==null}">
                                        <td class="expectPrice"><span style="color:#FF6700;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></span></td>
                                    </c:when>
                                    <c:otherwise>
                                        <td style="color:#FF6700;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></td>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <td style="color:#FF6700;"><fmt:formatNumber value="${itemDTO.price}" pattern="#.##"/></td>
                            </c:otherwise>
                        </c:choose>
                    </bcgogo:if>
                  </bcgogo:permission>
                    <td style="color:#FF0000;">
                        <fmt:formatNumber value="${itemDTO.amount}" pattern="#.##"/>${itemDTO.unit!=null?itemDTO.unit:""}
                    </td>
                    <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
                        <td style="color:#6D8FB9;">
                            <fmt:formatNumber value="${itemDTO.total}" pattern="#.##"/>
                        </td>
                    </bcgogo:hasPermission>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="${purchaseOrderDTO.supplierShopId!=null?'10':'9'}">合计：</td>
                <td colspan="2">
                    <span><fmt:formatNumber value="${purchaseOrderDTO.total}" pattern="#.##"/></span>
                </td>
            </tr>
            <tr>
                <td colspan="1" class="td_title">备注：</td>
                <td colspan="11"><span class="sale-memo">${purchaseOrderDTO.memo}</span></td>
            </tr>
	        <c:if test="${purchaseOrderDTO.status eq 'SELLER_REFUSED' or purchaseOrderDTO.status eq 'PURCHASE_SELLER_STOP'
	        or !empty purchaseOrderDTO.refuseMsg}">
		        <tr>
			        <td colspan="1" class="td_title">拒绝理由：</td>
			        <td colspan="11"><span class="sale-memo">${purchaseOrderDTO.refuseMsg}</span></td>
		        </tr>
	        </c:if>

        </table>
	    <c:if test="${purchaseOrderDTO.supplierShopId!=null and (!empty purchaseOrderDTO.company
	    or !empty purchaseOrderDTO.waybills or !empty purchaseOrderDTO.dispatchMemo
	    or purchaseOrderDTO.status eq 'SELLER_DISPATCH')}">
		    <table>
			    <col width="80"/>
			    <tr>
				    <td>物流信息</td>
			    </tr>
		    </table>
		    <table class="clear " id="tb_tui">
			    <col width="140"/>
			    <col width="350"/>
			    <col width="140"/>
			    <col width="350"/>
			    <tr>
				    <td class="td_title">物流公司：</td>
				    <td>${purchaseOrderDTO.company} </td>
				    <td class="td_title">运单号：</td>
				    <td>${purchaseOrderDTO.waybills} </td>
			    </tr>
			    <tr>
				    <td class="td_title">物流备注：</td>
				    <td colspan="3"><span class="sale-memo">${purchaseOrderDTO.dispatchMemo}</span></td>
			    </tr>
		    </table>
	    </c:if>

        <c:if test="${purchaseOrderDTO.supplierCommentRecordIdStr!=null}">
          <table>
            <col width="80"/>
            <tr>
              <td>供应商评分</td>
            </tr>
          </table>

          <table class="clear" id="tb_tui">
            <col width="70"/>
            <col width="65"/>
            <col width="70"/>
            <col width="65"/>
            <col width="70"/>
            <col width="65"/>
            <col width="70"/>
            <col width="65"/>

            <c:if test="${purchaseOrderDTO.addContent == 'true'}">
              <col width="90"/>
            </c:if>

            <input type="hidden" id="qualityScore" name="qualityScore" value="${purchaseOrderDTO.qualityScore}"/>
            <input type="hidden" id="performanceScore" name="performanceScore"
                   value="${purchaseOrderDTO.performanceScore}"/>
            <input type="hidden" id="speedScore" name="speedScore" value="${purchaseOrderDTO.speedScore}"/>
            <input type="hidden" id="attitudeScore" name="attitudeScore" value="${purchaseOrderDTO.attitudeScore}"/>

            <tr>
              <td class="td_title">货品 质量：</td>
              <td class="last-padding" id="qualityScoreTd"></td>

              <td class="td_title">货品性价比：</td>
              <td class="last-padding" id="performanceScoreTd"></td>

              <td class="td_title">发货 速度：</td>
              <td class="last-padding" id="speedScoreTd"></td>

              <td class="td_title">服务 态度：</td>
              <td class="last-padding" id="attitudeScoreTd"></td>

              <c:if test="${purchaseOrderDTO.addContent == 'true'}">
                <td class="last-padding">
                  <a id="addSupplierComment" class="addCommentClass"  href="javascript:addNewSupplierComment()">追加评论</a>
                </td>
              </c:if>
            </tr>
            <tr>
              <td class="td_title">详细评论：</td>
              <td colspan="8"><span class="sale-memo">${purchaseOrderDTO.supplierCommentContent}</span>
                <c:if test="${purchaseOrderDTO.addContent == 'false'}">
                  </br> <span class="sale-memo" style="color:#999999;">${purchaseOrderDTO.addCommentContent}</span>
                </c:if>
                <c:if test="${purchaseOrderDTO.commentStatusStr == 'UN_STAT'}">
                  <span class="sale-memo" style="color:#999999;">(该评分尚未生效，将在一天后生效)</span>
                </c:if>

              </td>
            </tr>
          </table>
        </c:if>


        <div class="height"></div>
    </div>
<bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
            <div id="saveDraftOrder_div" class="btn_div_Img">
                <input type="button" id="saveDraftOrder" class="i_savedraft" onfocus="this.blur();"/>
                <div style="width:100%; ">保存草稿</div>
            </div>
        </bcgogo:hasPermission>
<bcgogo:permission>
  <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE.CANCEL">
        <div class="invalidImg">
            <input id="nullifyBtn" type="button" onfocus="this.blur();">

            <div class="invalidWords" id="invalidWords">作废</div>
        </div>
  </bcgogo:if>
</bcgogo:permission>
		<bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
			<c:if test="${empty purchaseOrderDTO.supplierShopId}">
				<div class="copyInput_div" id="copyInput_div">
					<input id="copyInput" type="button" onfocus="this.blur();"/>
					<div class="copyInput_text_div" id="copyInput_text">复制</div>
				</div>
			</c:if>
		</bcgogo:hasPermission>

    <div class="shopping_btn">
       <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE" resourceType="menu">
            <div class="btn_div_Img" id="purchaseSave_div">
                <input id="purchaseSaveBtn" type="button" class="sureBuy j_btn_i_operate" onfocus="this.blur();"/>
                <div class="optWords">确认采购</div>
            </div>
            <bcgogo:hasPermission permissions="WEB.TXN.PURCHASE_MANAGE.STORAGE.SAVE">
                <c:if test="${(purchaseOrderDTO.status=='SELLER_STOCK' and !empty salesOrderDTO and !salesOrderDTO.isShortage) or purchaseOrderDTO.status=='SELLER_DISPATCH' or purchaseOrderDTO.status=='PURCHASE_ORDER_WAITING'}">
                    <div class="btn_div_Img" id="inventoryBtn_div">
                        <input id="inventoryBtn" type="button" class="sureBuy j_btn_i_operate" onfocus="this.blur();"/>

                        <div class="optWords">入库</div>
                    </div>
                </c:if>
            </bcgogo:hasPermission>
        </bcgogo:hasPermission>
      <bcgogo:permission>
        <bcgogo:if permissions="WEB.TXN.PURCHASE_MANAGE.PURCHASE.PRINT">
            <div class="btn_div_Img" id="print_div">
                <input id="printBtn" type="button" class="print j_btn_i_operate" onfocus="this.blur();"/>
                <div class="optWords">打印</div>
            </div>
        </bcgogo:if>
      </bcgogo:permission>
        <bcgogo:hasPermission permissions="WEB.AUTOACCESSORYONLINE.PURCHASE.RETURN">
            <c:if test="${purchaseOrderDTO.status == 'PURCHASE_ORDER_DONE' && !empty purchaseOrderDTO.supplierShopId}">
                <div class="btn_div_Img">
                    <input id="toOnlinePurchaseReturn" type="button" class="return_list"
                           onfocus="this.blur();"/>
                    <div class="optWords">退货</div>
                </div>
            </c:if>

        </bcgogo:hasPermission>
    </div>
</div>
</form:form>
</div>

</div>

<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <%--<div class="Container" style="height:230px;">--%>
    <%--<div id="Scroller-1" style="width:100%;padding:0;margin:0;">--%>
    <div class="Scroller-Container" id="Scroller-Container_id">
    </div>
    <%--</div>--%>
    <%--</div>--%>
</div>
<div id="mask" style="display:block;position: absolute;">
</div>
<div class="zuofei" id="zuofei"></div>
<iframe name="iframe_PopupBox" id="iframe_PopupBox"
        style="position:absolute;z-index:5; left:200px; top:400px; display:none;" scrolling="no"
        allowtransparency="true" width="1000px" height="1000px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:6; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="800px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_Limit" style="position:absolute;z-index:5; left:40%; top:35%; display:none;"
        allowtransparency="true" width="286px" height="180px" frameborder="0" src="" scrolling="no"></iframe>


<div id="draftOrder_dialog" style="display:none">
  <div class="i_draft_table">
      <table cellpadding="0" cellspacing="0" class="i_draft_table_box" id="draft_table">
          <col>
          <col width="50">
          <col width="100">
          <col width="220">
          <col width="220">
          <col width="400">
          <col>
          <tr class="tab_title">
              <td class="tab_first"></td>
              <td>No</td>
              <td>单据号</td>
              <td>保存时间</td>
              <td>供应商</td>
              <td>采购商品</td>
              <td class="tab_last"></td>
          </tr>
      </table>
    <!--分页-->
    <div class="hidePageAJAX">
      <jsp:include page="/common/pageAJAX.jsp">
        <jsp:param name="url" value="draft.do?method=getDraftOrders"></jsp:param>
        <jsp:param name="data" value="{startPageNo:1,orderTypes:'PURCHASE'}"></jsp:param>
        <jsp:param name="jsHandleJson" value="initOrderDraftTable"></jsp:param>
        <jsp:param name="hide" value="hideComp"></jsp:param>
        <jsp:param name="dynamical" value="dynamical1"></jsp:param>
      </jsp:include>
    </div>
  </div>
  <div class="clear"></div>
</div>

<div id="commodityCode_dialog" style="display:none">
    <span id="commodityCode_dialog_msg"></span>
</div>

<!-- 供应商下拉菜单 zhangchuanlong-->
<div id="div_brandvehiclelicenceNo" class="i_scroll" style="display:none;width:300px;">
  <div class="Container" style="width:300px;">
    <div id="Scroller-1licenceNo" style="width:300px;">
      <div class="Scroller-ContainerSupplier" id="Scroller-Container_idlicenceNo">
      </div>
    </div>
  </div>


</div>

<div id="div_serviceName" class="i_scroll" style="display:none;width:150px;">
    <div class="Scroller-Container" id="Scroller-Container_ServiceName">
    </div>
</div>
 <div id="dialog-confirm" title="提醒">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
        <div id="dialog-confirm-text"></div>
    </p>
</div>

<!-- 增加下拉建议框 -->
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="newSettlePage" style="display:none"></div>
<!-- 操作日志弹出框 -->
<div class="i_searchBrand" id="showOperationLog_div" title="查看单据操作记录" style="display:none; width: 500px;">
    <table id="operationLog_tab" border="0" width="480">
        <tr style="background-color:#E9E9E9; height:16px; ">
            <td class="tab_first">NO</td>
            <td>操作时间</td>
            <td>操作者</td>
            <td>操作内容</td>
        </tr>
    </table>
</div>

<bcgogo:hasPermission permissions="WEB.SUPPLIER_MANAGER.SUPPLIER_COMMENT.SAVE">

  <%--//供应商评价打分--%>
  <div class="i_searchBrand" id="supplierCommentDiv" title="评价供应商" style="display:none; width: 500px;">

    <form id="supplierCommentForm" method="post" action="storage.do?method=saveSupplierComment">
      <table border="0" width="480">
        <tr>
          <td class="last-padding">
            货品 质量：
            <input type="hidden" id="qualityScoreDivHidden" name="qualityScore" value=""/>
            <input type="hidden" id="purchaseInventoryIdStr" name="purchaseInventoryIdStr"
                   value="${purchaseOrderDTO.purchaseInventoryId}"/>
          </td>

          <td class="last-padding" id="qualityScoreDiv"></td>
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
          <td class="last-padding">
            货品性价比：
            <input type="hidden" id="performanceScoreDivHidden" name="performanceScore" value=""/>

          </td>
          <td class="last-padding" id="performanceScoreDiv"></td>
        </tr>

        <tr>
          <td class="last-padding">
            发货 速度：
            <input type="hidden" id="speedScoreDivHidden" name="speedScore" value=""/>

          </td>
          <td class="last-padding" id="speedScoreDiv"></td>
        </tr>
        <tr>
          <td class="last-padding">
            服务 态度：
            <input type="hidden" id="attitudeScoreDivHidden" name="attitudeScore" value=""/>

          </td>
          <td class="last-padding" id="attitudeScoreDiv"></td>
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
            <input id="supplierCommentRecordIdStr" name="supplierCommentRecordIdStr"
                   value="${purchaseOrderDTO.supplierCommentRecordIdStr}" type="hidden"/>

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
<jsp:include page="/txn/orderOperationLog.jsp" />
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>