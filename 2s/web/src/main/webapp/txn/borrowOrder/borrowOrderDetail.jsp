<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 13-3-8
  Time: 上午7:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>外部借调</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/borrowOrder<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/stockManager/borrowOrder<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuCurrentItem,"详情");
        $().ready(function(){
            var borrowOrderId=$("#borrowOrderId").val();
            if(stringUtil.isNotEmpty(borrowOrderId)){
                APP_BCGOGO.Net.syncPost({
                    url: 'borrow.do?method=getBorrowOrderDetail',
                    dataType: "json",
                    data: {
                        borrowOrderId: borrowOrderId
                    },
                    success: function(json) {
                        initBorrowOrderDetail(json);
                    },
                    error:function(){
                        nsDialog.jAlert("exception");
                    }
                });
            }

            $("#toBorrowOrderList").click(function(){
                toBorrowOrderList();
            });

            $("#sureReturn").click(function(){

                var itemIds="";
                $(".itemCheck").each(function(i){
                    if($(this).attr("checked")){
                        var borrowAmount=$(this).parent("td").find("amount");
                        var unReturnAmount=$(this).parent("td").find("unReturnAmount");
                        if(unReturnAmount>borrowAmount){
                            nsDialog.jAlert("第"+i+"行，");
                        }
                        var itemId=$(this).attr("borrowItemId");
                        if(stringUtil.isNotEmpty(itemId)){
                            itemIds+=$(this).attr("borrowItemId")+",";
                        }
                    }
                });
                if(stringUtil.isEmpty(itemIds)){
                    nsDialog.jAlert("请选择要归还的商品！")
                    return;
                }
                itemIds=itemIds.substr(0,itemIds.length-1);
                APP_BCGOGO.Net.syncPost({
                    url: 'borrow.do?method=getBorrowOrderToReturn',
                    dataType: "json",
                    data: {
                        borrowOrderId: borrowOrderId,
                        itemIds:itemIds
                    },
                    success: function(json) {
                        if(json.success){
                            initReturnOrder(json);
                            var trStr;
                            $("#returnOrder_dialog").dialog({
                                resizable: true,
                                title: "借调归还",
//                                height: 450,
                                width: 1000,
                                modal: true,
                                closeOnEscape: false
                            });
                            if(!G.isEmpty($("#storehouseId").val())){
                                if(verifyProductThroughOrderVersion(getOrderType())){
                                    $("#storehouseId").change();
                                }
                            }
                        }else{
                            nsDialog.jAlert(json.msg);
                        }

                    },
                    error:function(){
                        nsDialog.jAlert("网络异常！");
                    }
                });

            });

            $("#getReturnRecord").click(function(){
                APP_BCGOGO.Net.syncPost({
                    url: 'borrow.do?method=getReturnRunningRecord',
                    dataType: "json",
                    data: {
                        borrowOrderId: borrowOrderId
                    },
                    success: function(json) {
                        if(json.success){
                            initReturnRunningRecord(json);
                            var trStr;
                            $("#returnRunningRecord_dialog").dialog({
                                resizable: true,
                                title: "商品归还流水记录",
                                height: 450,
                                width: 1000,
                                modal: true,
                                closeOnEscape: false
                            });
                        }else{
                            nsDialog.jAlert(json.msg);
                        }

                    },
                    error:function(){
                        nsDialog.jAlert("网络异常！");
                    }
                });

            });

            $(".returnAmount").live("keyup",function (){
                $(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter($(this).val(),2));
            });
        });
    </script>

</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
    <jsp:include page="../txnNavi.jsp">
        <jsp:param name="currPage" value="inventory"/>
    </jsp:include>
    <jsp:include page="../inventroyNavi.jsp">
        <jsp:param name="currPage" value="borrowOrder"/>
        <jsp:param name="isRepairPickingSwitchOn" value="<%=isRepairPickingSwitchOn%>"/>
    </jsp:include>
    <input id="orderType" name="orderType" value="returnOrder" type="hidden"/>
    <div class="i_mainRight" id="i_mainRight">
        <div class="titBody ">
            <input type="hidden" value="${borrowOrderId}" id="borrowOrderId"/>
            <table class="tabMore" id="tabMore">
                <col width="80">
                <col width="140">
                <col width="64">
                <col width="180">
                <col width="64">
                <col width="180">
                <col width="90">
                <col>
                <tr>
                    <td>单据号：</td>
                    <td><span id="receiptNo"></span></td>
                    <c:if test="${isHaveStoreHouse}">
                        <td>调出仓库：</td><td><span id="storehouseName"></span></td>
                    </c:if>
                    <td>操作人：</td><td><span id="operator"></span></td>
                    <td>借调日期：</td><td><span id="borrowDate"></span></td>
                </tr>
                <tr>
                    <td>借调者类型：</td><td><span id="borrowerType"></span></td>
                    <td>借调者：</td><td><a class="blue_color" style="float:left;" id="borrower"></a></td>
                    <td>联系方式：</td> <td><span id="phone"></span></td>
                    <td>预计还调日期：</td>
                    <td><span id="returnDateSpan"></span></td>
                </tr>
            </table>
            <div class="clear"></div>
            <a class="blue_color" style="float:right; line-height:22px;" id="getReturnRecord">查看归还记录</a>
            <table cellpadding="0" cellspacing="0" class="tabSlip tabPrev" id="borrowOrderTable">
                <col width="22">
                <col width="80">
                <col width="90">
                <col width="100">
                <col width="100">
                <col width="100">
                <col width="100">
                <col width="100">
                <col width="70">
                <col width="70">
                <col width="70">
                <col width="70">
                <tr class="tr_bg">
                    <td><input type="checkbox" id="selectAll"></td>
                    <td>商品编号</td>
                    <td>品名</td>
                    <td>品牌/产地</td>
                    <td>规格</td>
                    <td>型号</td>
                    <td>车辆品牌</td>
                    <td>车型</td>
                    <td>成本均价</td>
                    <td>借调数</td>
                    <td>成本小计</td>
                    <td>剩余未还</td>
                </tr>

            </table>
            <div class="i_height"></div>
            <div class="remark"><span style="vertical-align:top;">备注：</span><span id="memo" style="width: 960px;display: inline-block"></span></div>
            <bcgogo:hasPermission permissions="WEB.BORROW_ORDER.PRINT">
                <div class="height"></div>
                <div class="printImg">
                    <img id="printBtn" src="images/print.png" />
                    <div class="sureWords">打印</div>
                </div>
            </bcgogo:hasPermission>
            <div class="shopping_btn">
                <div class="divImg">
                    <img id="sureReturn" src="images/sureStorage.jpg" />
                    <div class="sureWords">归还</div>
                </div>
                <div class="divImg">
                    <img src="images/cancel.PNG" id="toBorrowOrderList">
                    <div class="sureWords">返回列表</div>
                </div>
            </div>
        </div>

    </div>

</div>
<div id="returnOrder_dialog" style="display: none">
    <%--<label>sssss</label>--%>
    <div class="cartBody">
        <form:form commandName="returnOrderDTO" id="returnOrderDTOForm" action="borrow.do?method=saveReturnOrder" method="post">
            <form:hidden path="selectSupplier" value="${WEB_VERSION_PRODUCT_THROUGH_SELECT_SUPPLIER}"/>
            <div class="storehouse" style="padding-left: 10px">
                <c:if test="${isHaveStoreHouse}">
                    <div class="divTit">回料仓库：
                        <select id="storehouseId" class="j_checkStoreHouse" name="storehouseId">
                            <c:forEach items="${storeHouseDTOs}" var="storeHouseDTO" varStatus="status">
                                <option value="${storeHouseDTO.idStr}">${storeHouseDTO.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </c:if>
            </div>
            <table cellpadding="0" cellspacing="0" class="tabMerge tabMereCus table2" id="returnOrderTable">
                <col width="70">
                <col width="80">
                <col width="80">
                <col width="70">
                <col width="70">
                <col width="80">
                <col width="70">
                <col width="50">
                <col width="30">
                <col width="90">
                <col width="70">
                <tr class="tab_title">
                    <td style="padding-left: 10px">商品编号</td>
                    <td>品名</td>
                    <td>品牌/产地</td>
                    <td>规格</td>
                    <td>型号</td>
                    <td>车辆品牌</td>
                    <td>车型</td>
                    <td>借调数</td>
                    <td>单位</td>
                    <td>本次归还数量</td>
                    <td>归还人</td>
                </tr>
                <tr class="space">
                    <td colspan="11"></td>
                </tr>
            </table>
        </form:form>
    </div>
</div>

<div id="returnRunningRecord_dialog" style="display: none">
</div>

</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>