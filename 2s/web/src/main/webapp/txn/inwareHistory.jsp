<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    //    ItemIndexDTO itemIndex = (ItemIndexDTO) request.getAttribute("command");
//    List<ItemIndexDTO> itemIndexDTOs = (List<ItemIndexDTO>) request.getAttribute("itemIndexDTOs");
//    int  listSize = itemIndexDTOs != null ? itemIndexDTOs.size() : 0;
//    String pageNo = itemIndex.getPageNo() == null || "".equals(itemIndex.getPageNo()) ? "1" : itemIndex.getPageNo();
//    List<String> list = (List<String>)request.getSession().getAttribute("checkedId");
//    List<Integer> numbers = (List<Integer>)request.getSession().getAttribute("itemNumber");
//    String supplierName = (String)request.getSession().getAttribute("supplierName");
//    String searchFlag = (String)request.getSession().getAttribute("searchFlag");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/carSearch<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/goodsHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreUserinfo<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/inwareHistory<%=ConfigController.getBuildVersion()%>.css"/>
	<link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.css" />
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-scanning<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogoValidate<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/common<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/bcgogo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/inwareHistory<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <title>待退货商品查询</title>
</head>

<body>
<input type="hidden" id="basePath" value="<%=basePath%>"/>
<input type="hidden" id="orderType" value="purchaseReturnSearch">

<div class="i_supplierInfo more_supplier" id="div_show">
<div class="i_arrow"></div>
<div class="i_upLeft"></div>
<div class="i_upCenter i_two">
    <div class="i_note more_title" id="div_purchaseReturnSearch">待退货商品查询</div>
    <div class="i_close" id="div_close"></div>
</div>
<div class="i_upRight"></div>
<div class="i_upBody clear">
<div class="i_main clear">
<!--历史搜素-->
<form:form commandName="itemIndexDTO" action="goodsReturn.do?method=createProductsSearch" method="post"
           id="thisform" name="thisform" modelAttribute="itemIndexDTO">
<form:hidden path="pageNo"/>
<input type="hidden" name="pageFlag" id="pageFlag"/>
<c:if test="${pager != null}">
    <input type="hidden" name="prePageNum" value="${pager.currentPage}"/>
</c:if>
<div class="his_search clear">
    <div class="goods_chk clear">
        <ul class="clear searchInwareHistoryItem">
            <li class="search_user" id="search_user">
                <label>供应商</label><form:input path="customerOrSupplierName" autocomplete="off"/></li>
            <li class="clear"><label>品 名</label><form:input path="itemName" autocomplete="off"/>
                <input type="button" class="i_icon" onfocus="this.blur();"/>
            </li>
            <li><label>品 牌</label><form:input path="itemBrand" autocomplete="off"/>
                <input type="button" class="i_icon" onfocus="this.blur();"/></li>
            <li><label>规 格</label><form:input path="itemSpec" autocomplete="off"/>
                <input type="button" class="i_icon" onfocus="this.blur();"/></li>
            <li><label>型 号</label><form:input path="itemModel" autocomplete="off"/>
                <input type="button" class="i_icon" onfocus="this.blur();"/></li>
        </ul>
    </div>
    <div class="his_time">
        <div class="search_his clear" id="returan_search">
            <label style="cursor:pointer;">搜索</label>
        </div>
    </div>
    <div class="clear"></div>
</div>
<!--历史搜素结束-->
<!--数据显示-->
<p class="clear">历史记录</p>

<div id="div_arrear" class="clear">
    <table cellpadding="0" cellspacing="0" class="table2" style="table-layout:fixed;">
        <col width="30">
        <col width="90">
        <col width="90">
        <col width="80">
        <col width="65">
        <col width="65">
        <col width="65">
        <col width="70">
        <col width="50">
        <col width="45">
        <col width="77">
        <col width="75">
            <%--<col width="75">--%>
        <col width="60">
        <tr class="title_his">
            <td style="border-left:none;"></td>
            <td>供应商</td>
            <td>品名</td>
            <td>品牌</td>
            <td>规格</td>
            <td>型号</td>
            <td>车辆品牌</td>
            <td>车型</td>
            <td class="txt_right">可退数</td>
            <td>单位</td>
            <td class="txt_center">退货数</td>
            <td class="txt_right">最近入库价</td>
                <%--<td class="txt_right">入库日期</td>--%>
            <td style="border-right:none;">历史记录</td>
        </tr>
        <c:forEach items="${purchaseReturnItemDTOs}" var="itemDTO" varStatus="status">
            <tr>
                <td style="border-left:none;table-layout:fixed;">
                    <c:if test="${itemDTO.returnAbleAmount > 0.001}">
                        <input class="indexNo" type="checkbox" name="itemDTOs[${status.index}].checkId"
                               value="${itemDTO.supplierId}_${itemDTO.productId}" id="itemDTOs${status.index}.checkId"/>
                        <input type="hidden" value="${itemDTO.supplierId}" id="itemDTOs${status.index}.supplierId"
                               name="itemDTOs[${status.index}].supplierId">
                        <input type="hidden" value="${itemDTO.productId}" id="itemDTOs${status.index}.productId"
                               name="itemDTOs[${status.index}].productId">
                    </c:if>
                </td>
                <td name="supplierName" style="text-overflow:ellipsis" title="${itemDTO.supplierName}">
                        ${itemDTO.supplierName}
                    <input type="hidden" value="${itemDTO.supplierName}" id="itemDTOs${status.index}.supplierName">
                </td>
                <td style="text-overflow:ellipsis" title="${itemDTO.productName}">
                        ${itemDTO.productName}
                    <input type="hidden" value="${itemDTO.productName}" id="itemDTOs${status.index}.productName">
                </td>
                <td style="text-overflow:ellipsis" title="${itemDTO.brand}">
                        ${itemDTO.brand}
                    <input type="hidden" value="${itemDTO.brand}" id="itemDTOs${status.index}.brand">
                </td>
                <td title="${itemDTO.spec}">
                        ${itemDTO.spec}
                    <input type="hidden" value="${itemDTO.spec}" id="itemDTOs${status.index}.spec">
                </td>
                <td title="${itemDTO.model}">
                        ${itemDTO.model}
                    <input type="hidden" value="${itemDTO.spec}" id="itemDTOs${status.index}.spec">
                </td>
                <td title="${itemDTO.vehicleBrand}">
                        ${itemDTO.vehicleBrand}
                    <input type="hidden" value="${itemDTO.vehicleBrand}" id="itemDTOs${status.index}.vehicleBrand">
                </td>
                <td title="${itemDTO.vehicleModel}">
                        ${itemDTO.vehicleModel}
                    <input type="hidden" value="${itemDTO.vehicleModel}" id="itemDTOs${status.index}.vehicleModel">
                </td>
                <td class="txt_right" title="${itemDTO.returnAbleAmount}">
                        ${itemDTO.returnAbleAmount}
                    <input type="hidden" value="${itemDTO.returnAbleAmount}"
                           id="itemDTOs${status.index}.returnAbleAmount"
                           name="itemDTOs[${status.index}].returnAbleAmount">
                </td>
                <td title="${itemDTO.unit}">
                        ${itemDTO.unit}
                    <input type="hidden" value="${itemDTO.unit}" id="itemDTOs${status.index}.unit"
                           name="itemDTOs[${status.index}].unit">
                </td>
                <td class="txt_right">
                    <input type="button" class="opera2" onfocus="this.blur();" id="itemDTOs${status.index}.minus"/>

                    <div class="num"><input type="text" id="itemDTOs${status.index}.amount" class="notBanBackspace"
                                            name="itemDTOs[${status.index}].amount" value="${itemDTO.amount}"
                                            autocomplete="off"/></div>
                    <input type="button" class="opera1" onfocus="this.blur();" id="itemDTOs${status.index}.plus"/>
                </td>
                <td class="txt_right" title="${itemDTO.price}">${itemDTO.price}</td>
                    <%--<td class="txt_right">${itemDTO.orderTimeCreatedStr}</td>--%>
                <td style="border-right:none;" onfocus="this.blur();"><a class="click_detail">点击详细</a></td>
            </tr>
            <tr id="history_detail" style="display:none;" align="right">
                <td colspan="13" align="right">
                    <table cellpadding="0" cellspacing="0" style="table-layout:fixed;width: 800px">
                        <col width="90">
                        <col width="65">
                        <col width="60">
                        <col width="50">
                        <col width="77">
                        <col width="75">
                        <col width="75">
                        <col width="60">
                        <tr class="table_title">
                            <td>品名</td>
                            <td>品牌</td>
                            <td>规格</td>
                            <td>型号</td>
                            <td>供应商</td>
                            <td style="border-left:none;">日期</td>
                            <td class="text_justified">数量</td>
                            <td>单位</td>
                            <td class="text_justified" style="border-right:none;">单价</td>
                            <td>单据类型</td>
                            <td>单据状态</td>
                        </tr>
                        <c:forEach items="${itemDTO.itemIndexDTOs}" var="subItemDTO" end="4">
                            <tr>
                                <td title="${subItemDTO.itemName}">${subItemDTO.itemName}</td>
                                <td title="${subItemDTO.itemBrand}">${subItemDTO.itemBrand}</td>
                                <td title="${subItemDTO.itemSpec}">${subItemDTO.itemSpec}</td>
                                <td title="${subItemDTO.itemModel}">${subItemDTO.itemModel}</td>
                                <td title="${subItemDTO.customerOrSupplierName}">${subItemDTO.customerOrSupplierName}</td>
                                <td style="border-left:none;"
                                    title="${subItemDTO.orderTimeCreatedStr}">${subItemDTO.orderTimeCreatedStr}</td>
                                <td class="text_justified" title="${subItemDTO.itemCount}">${subItemDTO.itemCount}</td>
                                <td title="${subItemDTO.unit}">${subItemDTO.unit}</td>
                                <td class="text_justified" style="border-right:none;"
                                    title="${subItemDTO.itemPrice}">${subItemDTO.itemPrice}</td>
                                <td>${subItemDTO.orderTypeStr}</td>
                                <td>${subItemDTO.orderStatusStr}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </td>
            </tr>
        </c:forEach>
    </table>
    <c:forEach items="${selectPurchaseReturnItemDTOs}" var="itemDTO" varStatus="status">
        <div id="selectItem_Div${status.index}">
            <input type="hidden" name="selectItemDTOs[${status.index}].checkId" value="${itemDTO.checkId}"
                   id="selectItemDTOs${status.index}.checkId" class="selectedItemDTOCheckId"/>
            <input type="hidden" name="selectItemDTOs[${status.index}].supplierId" value="${itemDTO.supplierId}"
                   id="selectItemDTOs${status.index}.supplierId"/>
            <input type="hidden" name="selectItemDTOs[${status.index}].productId" value="${itemDTO.productId}"
                   id="selectItemDTOs${status.index}.productId"/>
            <input type="hidden" name="selectItemDTOs[${status.index}].returnAbleAmount"
                   value="${itemDTO.returnAbleAmount}" id="selectItemDTOs${status.index}.returnAbleAmount">
            <input type="hidden" name="selectItemDTOs[${status.index}].unit" value="${itemDTO.unit}"
                   id="selectItemDTOs${status.index}.unit"/>
            <input type="hidden" name="selectItemDTOs[${status.index}].amount" value="${itemDTO.amount}"
                   id="selectItemDTOs${status.index}.amount"/>
        </div>
    </c:forEach>
    </form:form>
    <div class="clear"></div>
    <!--分页-->
    <c:if test="${pager != null}">
        <jsp:include page="/common/paging.jsp">
            <jsp:param name="url" value="goodsReturn.do?method=createProductsSearch"></jsp:param>
            <jsp:param name="submit" value="thisform"></jsp:param>
        </jsp:include>
    </c:if>
    <div class="his_bottom">
        <input name="checkAll" id="checkAlls" type="checkbox"/><strong>全选</strong>
        <%--<div class="i_leftBtn" id="i_leftBtn_id">--%>
        <%--<div class="lastPage">上一页</div>--%>
        <%--<div class="onlin_his" id="pageNo_id">1</div>--%>
        <%--<%if (listSize >= 5) {%>--%>
        <%--<div class="nextPage">下一页</div>--%>
        <%--<%}%>--%>
        <%--</div>--%>
        <%--<div class="clear"></div>--%>
    </div>
    <!--分页结束-->
    <div class="table_btn">
        <input type="button" value="取消" id="sure_btna" class="btn sure_btna" onfocus="this.blur();"/>
        <input id="create_btn" type="button" value="确认生成入库退货单" class="btn sure_btn" onfocus="this.blur();"/>
    </div>
    <!--结算-->
</div>
<div class="clear"></div>
<div class="i_height"></div>
<!--数据显示结束-->
</div>
</div>
<div class="i_upBottom">
    <div class="i_upBottomLeft"></div>
    <div class="i_upBottomCenter"></div>
    <div class="i_upBottomRight"></div>
</div>
<!--商品搜索下拉菜单-->
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id">
    </div>
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

</div>

<div id="mask" style="display:block;position: absolute;">
</div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
        allowtransparency="true" width="900px" height="650px" frameborder="0" src=""></iframe>

<%@ include file="/common/messagePrompt.jsp" %>
</body>
</html>