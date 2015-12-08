<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>发布求购</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/components/themes/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/autoaccessoryonline/preBuyOrder<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/invoiceCommon<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/suggestion<%=ConfigController.getBuildVersion()%>.js"></script>
    <%@ include file="/WEB-INF/views/image_script.jsp" %>
    <script type="text/javascript" charset="utf-8" src="js/components/ui/bcgogo-imageUploader<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB_AUTOACCESSORYONLINE_RELEASE_PREBUYORDER");
        $(function(){
            bindProductImageCmp(0);
        });
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/common/messagePrompt.jsp" %>
<img id="testImg"/>
<input type="hidden" id="policy" value="${upYunFileDTO.policy}">
<input type="hidden" id="signature" value="${upYunFileDTO.signature}">
<div class="i_main clear">
    <jsp:include page="../txn/unit.jsp"/>
    <div class="clear i_height"></div>
    <div class="titBody">
        <jsp:include page="supplyCenterLeftNavi.jsp">
            <jsp:param name="currPage" value="preBuyOrder"/>
        </jsp:include>

        <div class="bodyLeft">
            <div class="cuSearch">
                <div class="lineTitle">发布求购商品列表 </div>
                <div class="cartBody lineBody">
                    <form:form commandName="preBuyOrderDTO" id="preBuyOrderForm" action="preBuyOrder.do?method=savePreBuyOrder" method="post" class="J_leave_page_prompt">
                        <input id="orderType" name="orderType" value="preBuyOrder" type="hidden"/>
                        <input type="hidden" id="basePath" name="bathPath" value="<%=basePath%>"/>
                        <div class="clear i_height"></div>
                        <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="table_productNo">
                            <col width="90">
                            <col width="90">
                            <col width="110">
                            <col width="75">
                            <col width="75">
                            <col width="70">
                            <col width="70">
                            <col width="70">
                            <col width="40">
                            <col width="40">
                            <col width="75">
                            <tr class="titleBg">
                                <td colspan="8" style="padding-left:10px;text-align: center;">求购商品信息</td>
                                <td>求购量</td>
                                <td>单位</td>
                                <td>操作</td>
                            </tr>
                            <tr class="space"><td colspan="10"></td></tr>
                            <c:forEach items="${preBuyOrderDTO.itemDTOs}" var="itemDTO" varStatus="status">
                                <tr class="titBody_Bg item" style="line-height: 8px;">
                                    <td rowspan="3">
                                        <div style="padding-left: 5px">
                                            <input type="hidden" class="J_productMainImageView" id="itemDTOs${status.index}.productMainImage" />
                                            <input type="hidden" id="itemDTOs${status.index}.dataImageRelationId"/>
                                            <input type="hidden" class="J_productInfoImagePath" id="itemDTOs${status.index}.productInfoImagePath" name="itemDTOs[${status.index}].imageCenterDTO.productInfoImagePaths[0]"/>
                                            <div style="position:absolute;" id="itemDTOs${status.index}.productMainImageUploader"></div>
                                            <div data-index="${status.index}" id="itemDTOs${status.index}.addProductMainImage" class="add-img J_addProductMainImage">上传图片</div>
                                            <div id="itemDTOs${status.index}.productMainImageView" style="position: relative;display: none;height:60px;width: 60px"></div>
                                        </div>
                                    </td>
                                    <td>
                                        <form:input path="itemDTOs[${status.index}].commodityCode" placeholder="商品编码" cssClass="txt txt_color" maxlength="20"/>
                                    </td>
                                    <td>
                                        <form:hidden path="itemDTOs[${status.index}].id" autocomplete="off"/>
                                        <form:hidden path="itemDTOs[${status.index}].productId" autocomplete="off"/>
                                        <form:input path="itemDTOs[${status.index}].productName" placeholder="品名" cssClass="txt txt_color" autocomplete="off"/>
                                    </td>
                                    <td>
                                        <form:input path="itemDTOs[${status.index}].brand" placeholder="品牌" cssClass="txt txt_color" autocomplete="off"/>
                                    </td>
                                    <td>
                                        <form:input path="itemDTOs[${status.index}].spec" placeholder="规格" cssClass="txt txt_color" autocomplete="off"/>
                                    </td>
                                    <td>
                                        <form:input path="itemDTOs[${status.index}].model" placeholder="型号" cssClass="txt txt_color" autocomplete="off"/>
                                    </td>
                                    <td>
                                        <form:input path="itemDTOs[${status.index}].vehicleBrand" placeholder="车辆品牌" cssClass="txt txt_color" autocomplete="off"/>
                                    </td>
                                    <td>
                                        <form:input path="itemDTOs[${status.index}].vehicleModel" placeholder="车辆型号" cssClass="txt txt_color" autocomplete="off"/>
                                    </td>
                                    <td>
                                        <form:input path="itemDTOs[${status.index}].amount" placeholder="数量" cssStyle="width:30px;" cssClass="itemAmount txt txt_color" autocomplete="off"/>
                                    </td>
                                    <td>
                                        <form:input path="itemDTOs[${status.index}].unit" placeholder="单位" cssClass="itemUnit txt txt_color" autocomplete="off"/>
                                    </td>

                                    <td rowspan="3">
                                        <a id="itemDTOs${status.index}.deleteRowBtn" class="blue_color" onfocus="this.blur();">删除</a> <br/>
                                    </td>
                                </tr>
                                <tr class="titBody_description J_itemMemo">
                                    <td colspan="9">
                                        <div class="buying_trList buying_description">
                                            <div>
                                                <form:textarea path="itemDTOs[${status.index}].memo" maxlength="200" placeholder='可输入求购商品描述（200字以内）' style="width: 98%;height:30px" cssClass="txt"/>
                                            </div>
                                        </div>
                                    </td>
                                    <td></td>
                                </tr>
                                <tr class="titBottom_Bg J_itemBottom"><td colspan="10"></td></tr>
                            </c:forEach>
                        </table>
                        <div class="clear i_height"></div>
                        <div class="divTit releaseTit">
                            <span class="spanName"><a class="red_color">*</a>有效期</span>&nbsp;
                            <form:select path="preBuyOrderValidDate" cssClass="txt txt_color">
                                <option value="">—请选择—</option>
                                <form:options items="${preBuyOrderValidDate}" itemLabel="name"/>
                            </form:select>
                        </div>
                        <div class="divTit button_conditon" style="width:100%;">
                            <a class="blue_color clean" id="cleanFormBtn" style="float:right;">全部清空</a>
                            <a id="savePreBuyOrderBtn" class="button" style="float:right;">发&nbsp;布</a>
                        </div>
                    </form:form>
                </div>
                <div class="lineBottom"></div>
            </div>
        </div>


        <div class="height"></div>
        <!----------------------------页脚----------------------------------->

    </div>
</div>

<div class="preBuyOrderValidDateDiv" style="display: none">
    <div>
        <span>请选择有效期!</span>
    </div>
    <div class="divTit releaseTit">
        <span class="spanName"><a style="color: #CB0000">*</a>有效期</span>&nbsp;
        <select id="preBuyOrderValidDateSelect" cssClass="txt txt_color">
            <option value="">—请选择—</option>
            <c:forEach items="${preBuyOrderValidDate}" var="item" varStatus="status">
                <option value="${item.label}">${item.name}</option>
            </c:forEach>
        </select>
    </div>
</div>

<div id="mask" style="display:block;position: absolute;"></div>
<div id="id-searchcomplete"></div>
<div id="id-searchcompleteMultiselect"></div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Scroller-Container" id="Scroller-Container_id"></div>
</div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:200px; top:400px; display:none;"
        allowtransparency="true" width="1000px" height="1000px" frameborder="0" src="" scrolling="no"></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>